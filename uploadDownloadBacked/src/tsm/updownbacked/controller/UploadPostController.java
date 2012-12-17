package tsm.updownbacked.controller;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.jlan.server.filesys.FileName;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentService;
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
import tsm.updownbacked.utility.PolicyGenerator;
import tsm.updownbacked.utility.Utility;

public class UploadPostController extends DeclarativeWebScript{
		 
 	private String key = "Dh_s0uzo1walbqnsScJJQy|ffs";

 	private static final long  MEGABYTE = 1024L * 1024L;
 	
 	//private static final String  uploadFolderPath = "C:\\Alfresco\\tomcat\\shared\\uploadFolder\\";
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
    			
		String filename = null;
		NodeRef companyHome = repository.getCompanyHome();
		ContentService contentService = this.serviceRegistry.getContentService();
		
	    FormData formData = (FormData)req.parseContent(); // <-- req = WebScriptRequest
        FormData.FormField[] fields = formData.getFields();
        for(FormData.FormField field : fields) {
            	
                if(field.getIsFile()) {
                 
                    filename = field.getFilename();
                    Content content = field.getContent();
                    System.out.println("Size: "+field.getContent().getSize());
                                      
                    if (field.getContent().getSize()/MEGABYTE > uploadPolicy.getMaximumSizeInMB()){
    					
    					throw new WebScriptException("Operation denied: file uploaded too large");
    				}
                
                    ContentWriter contentWriter = contentService.getWriter(companyHome, ContentModel.PROP_CONTENT, true);
                    contentWriter.setMimetype(Utility.guessContentType(filename));

                    FileChannel fileChannel = contentWriter.getFileChannel(false);
                    ByteBuffer bf;
					try {
						bf = ByteBuffer.wrap(content.getContent().getBytes());
					    fileChannel.position(contentWriter.getSize());
                        fileChannel.write(bf);
                        fileChannel.force(false);
                        fileChannel.close();
                    } catch (IOException e) {
                    	
						e.printStackTrace();
    					throw new WebScriptException("Operation failed: error during I/O operation");
					}
                   
                    
                    model.put("fileName", filename);
                    model.put("fileSize", content.getSize());
                 }	
                  
               
        }

	 	PolicyGenerator policyGenerator = new PolicyGenerator(key);
	 	String encodedDownloadPolicy = policyGenerator.getEncodedDownloadPolicyParam(companyHome.getId());    
	 	model.put("downloadUrlWithPolicy",downloadActionUrl+"?fileName="+filename+"&"+"signedEncodedPolicy="+encodedDownloadPolicy);            
	   
	    return model;

    }
    
}
