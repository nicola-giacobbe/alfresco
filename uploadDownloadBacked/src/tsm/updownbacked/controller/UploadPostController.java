package tsm.updownbacked.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.FormData;
import tsm.updownbacked.model.DecodedPolicy;
import tsm.updownbacked.model.Policy;
import tsm.updownbacked.model.UploadPolicy;
import tsm.updownbacked.utility.Utility;

public class UploadPostController extends DeclarativeWebScript{
		 
 	private String key = "Dh_s0uzo1walbqnsScJJQy|ffs";

 	private static final long  MEGABYTE = 1024L * 1024L;
 
 	private static final String  downloadActionUrl = "/alfresco/service/download";
 	
 	private Repository repository;
 	
 	private ServiceRegistry serviceRegistry;
 	
 	public void setRepository(Repository repository){
 	    this.repository = repository;
 	}
 	  
 	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}


	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache){

		//Decoding policy with secret key
    	DecodedPolicy decodedPolicy = Policy.decodePolicy(key, req.getParameter("policy"));
		
    	boolean policyNameIsWrong = !decodedPolicy.getPolicyName().equals("UploadPolicy");
		boolean signatureIsWrong = decodedPolicy.isSignedCorrectly() == false;
		Map<String, Object> model = new HashMap<String, Object>();
	
		if (policyNameIsWrong || signatureIsWrong){
			
			throw new WebScriptException("Operation denied: policy signature is wrong");
		}
		UploadPolicy uploadPolicy = UploadPolicy.fromDecodedPolicy(decodedPolicy);
		if (uploadPolicy.isExpired()){
			
			throw new WebScriptException("Operation denied: policy is expired");
		}
    			
		String filename = null;
		FileInfo fileInfo=null;
		NodeRef companyHome = repository.getCompanyHome();
		// req = WebScriptRequest
	    FormData formData = (FormData)req.parseContent();
        FormData.FormField[] fields = formData.getFields();
        
        for(FormData.FormField field : fields) {
            	
                if(field.getIsFile()) {
                	//Getting fields from multipart-form
                    filename = field.getFilename();
                    Content content = field.getContent();

                    String path = uploadPolicy.getFilePath();                 
                    
                    
                    //Search for nodeRef with that filename             
            		NodeRef nodeRef = this.serviceRegistry.getFileFolderService().searchSimple(companyHome,filename);
            		
            		//If nodeRef exists for the same filename delete nodeRef and create another
            		if(null!=nodeRef){
            		this.serviceRegistry.getFileFolderService().delete(nodeRef);
            		}
            		//Create nodeRef for new file
                    fileInfo = this.serviceRegistry.getFileFolderService().create(companyHome, filename, ContentModel.TYPE_CONTENT);
                    
                    
                    
                      
                    //Obtaining contentwriter for new nodeRef
                    ContentWriter contentWriter = this.serviceRegistry.getFileFolderService().getWriter(fileInfo.getNodeRef());
                    contentWriter.setMimetype(Utility.guessContentType(filename));
                    
                    //getting ByteArrayOutputStream from InputStream
                    InputStream in = content.getInputStream();

                    byte[] buff = new byte[8000];

                    int bytesRead = 0;

                    ByteArrayOutputStream bao = new ByteArrayOutputStream();

                    try {
						while((bytesRead = in.read(buff)) != -1) {
						   bao.write(buff, 0, bytesRead);
						}
					} catch (IOException e) {
						
						e.printStackTrace();
						throw new WebScriptException("Operation failed: I/O error");
						
					}
                    //Obtaining Byte array
                    byte[] data = bao.toByteArray();
                    System.out.println("size in megabyte :"+bao.size()/MEGABYTE);
                    if (bao.size()/MEGABYTE > uploadPolicy.getMaximumSizeInMB()){
    					
    					throw new WebScriptException("Operation denied: file uploaded too large");
    				}
                    
                    contentWriter.putContent(new ByteArrayInputStream(data));
                 
                    //  var redirectUrl = uploadPolicy.RedirectUrl + "?path=" + HttpUtility.UrlEncode(actualFileName);
                    //  return Redirect(redirectUrl);
                    //Setup model
                    try {
						
                    	model.put("redirectUrl", uploadPolicy.getRedirectUrl()+"?path=" + URLEncoder.encode(uploadPolicy.getFilePath(), "UTF-8"));
					
                    } catch (UnsupportedEncodingException e) {
                    	
						e.printStackTrace();
					}
                 }	
                  
        }           
	   
	    return model;

    }
    
}
