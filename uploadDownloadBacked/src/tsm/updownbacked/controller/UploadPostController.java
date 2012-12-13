package tsm.updownbacked.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServiceImpl;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.FormData;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

import tsm.updownbacked.model.DecodedPolicy;
import tsm.updownbacked.model.Policy;
import tsm.updownbacked.model.UploadPolicy;

public class UploadPostController extends DeclarativeWebScript{
		 
 	private String key = "Dh_s0uzo1walbqnsScJJQy|ffs";

 	private static final long  MEGABYTE = 1024L * 1024L;
 	 
 	private Repository repository;
 	public void setRepository(Repository repository){
 	    this.repository = repository;
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
    	
		
		try{
			
			NodeRef companyHome = repository.getCompanyHome();
			ContentService contentService = new ContentServiceImpl(); 
			
		    FormData formData = (FormData)req.parseContent(); // <-- req = WebScriptRequest
	        FormData.FormField[] fields = formData.getFields();
	        for(FormData.FormField field : fields) {
	            	
	                if(field.getIsFile()) {
	                 
	                    String filename = field.getFilename();
	                    Content content = field.getContent();
	                    String mimetype = field.getMimetype();
	                    ContentWriter writer= contentService.getWriter(companyHome, ContentModel.PROP_CONTENT, false);
	                    
	                    writer.setMimetype(mimetype);
	                    writer.putContent(content.getInputStream());
	                    if (content.getSize()/MEGABYTE > uploadPolicy.getMaximumSizeInMB()){
	    					
	    					throw new WebScriptException("File uploaded too large");
	    				}  
	                    
	                    model.put("ItemName", filename);
	                    model.put("ItemSize", content.getSize());
	                	
	                  
	                }
	        }
        
	    }catch(Exception e){
                		
                		e.printStackTrace();
                		throw new WebScriptException("Error during upload operation");
    	}        
		
	    return model;

    }
    
}
