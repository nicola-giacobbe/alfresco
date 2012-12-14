package tsm.updownbacked.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.alfresco.repo.model.Repository;
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
import tsm.updownbacked.utility.PolicyGenerator;

public class UploadPostController extends DeclarativeWebScript{
		 
 	private String key = "Dh_s0uzo1walbqnsScJJQy|ffs";

 	private static final long  MEGABYTE = 1024L * 1024L;
 	
 	private static final String  uploadFolderPath = "C:\\Alfresco\\tomcat\\shared\\uploadFolder\\";
 	private static final String  downloadActionUrl = "/alfresco/service/download";
 	
 	private Repository repository;
 	
 	public void setRepository(Repository repository){
 	    this.repository = repository;
 	}
 	  
 	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache){

 		String uploadedFilePath = null;
    	DecodedPolicy decodedPolicy = Policy.decodePolicy(key, req.getParameter("signedEncodedPolicy"));

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
    
			
		/*	NodeRef companyHome = repository.getCompanyHome();
			ContentService contentService = new ContentServiceImpl(); */
			
		    FormData formData = (FormData)req.parseContent(); // <-- req = WebScriptRequest
	        FormData.FormField[] fields = formData.getFields();
	        for(FormData.FormField field : fields) {
	            	
	                if(field.getIsFile()) {
	                 
	                    String filename = field.getFilename();
	                    Content content = field.getContent();
	                    System.out.println("Size: "+field.getContent().getSize());
	                   
	                    /*// Try and get the content writer
	                    ContentWriter writer= contentService.getWriter(companyHome, ContentModel.PROP_CONTENT, false);
	                   
	                    writer.setMimetype(mimetype);
	                    // Stream the content into the repository
	                    writer.putContent(content.getInputStream()); */
	                    

						//!! content.getSize() return a negative value
	                    if (content.getSize()/MEGABYTE > uploadPolicy.getMaximumSizeInMB()){
	    					
	    					throw new WebScriptException("Operation denied: file uploaded too large");
	    				}
	                    InputStream is = content.getInputStream();  
	                    
	                    OutputStream os;
	                    uploadedFilePath = uploadFolderPath+filename;
						try {
							
							os = new FileOutputStream(new File(uploadedFilePath));
							byte[] buffer = new byte[4000];  
		                    
							try {
								for (int n; (n = is.read(buffer)) != -1; )   
								os.write(buffer, 0, n);
						
		                    } catch (IOException e) {
		                    	
								e.printStackTrace();
								throw new WebScriptException("Operation failed: error during I/O Operation");
							} 						
		                    
						} catch (FileNotFoundException e) {
							
							e.printStackTrace();
							throw new WebScriptException("Operation failed: error during I/O Operation");
						}     
                        
	                 
	                    model.put("ItemName", filename);
	                    model.put("ItemSize", content.getSize());
	                	
	                  
	                }
	        }

	 	PolicyGenerator policyGenerator = new PolicyGenerator(key);
	 	String encodedDownloadPolicy = policyGenerator.getEncodedDownloadPolicyParam(uploadedFilePath);    
	 	model.put("downloadUrlWithPolicy",downloadActionUrl+"?signedEncodedPolicy="+encodedDownloadPolicy);            
	   
	    return model;

    }
    
}
