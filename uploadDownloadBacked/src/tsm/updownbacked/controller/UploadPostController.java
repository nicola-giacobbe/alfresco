package tsm.updownbacked.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.FormData;
import com.sun.star.auth.InvalidArgumentException;
import tsm.updownbacked.model.DecodedPolicy;
import tsm.updownbacked.model.Policy;
import tsm.updownbacked.model.UploadPolicy;
import tsm.updownbacked.utility.Utility;

public class UploadPostController extends DeclarativeWebScript{
		 
 	private String key = "Dh_s0uzo1walbqnsScJJQy|ffs";
 	private static final long  MEGABYTE = 1024L * 1024L;
 	private final static String tagVersionPrefix= "TSM_TAG_VERSION"; 
	
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
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		//Decoding policy with secret key
    	DecodedPolicy decodedPolicy = Policy.decodePolicy(key, req.getParameter("policy"));		
    	
    	//Validate decoded policy
    	boolean policyNameIsWrong = !decodedPolicy.getPolicyName().equals("UploadPolicy");
		boolean signatureIsWrong = decodedPolicy.isSignedCorrectly() == false;		
		if (policyNameIsWrong || signatureIsWrong){			
			//model.put("redirectUrl", uploadPolicy.getRedirectUrl()+"?errorMessage="+ e1.getMessage());	
			//return model;
			throw new WebScriptException("Operation denied: policy signature is wrong");
		}
		
		UploadPolicy uploadPolicy = UploadPolicy.fromDecodedPolicy(decodedPolicy);
		if (uploadPolicy.isExpired()){		
			//model.put("redirectUrl", uploadPolicy.getRedirectUrl()+"?errorMessage="+ e1.getMessage());	
			//return model;
			throw new WebScriptException("Operation denied: policy is expired");
		}
    			
		FileInfo fileInfo=null;
		NodeRef companyHome = repository.getCompanyHome();
		//req = WebScriptRequest
	    FormData formData = (FormData)req.parseContent();
        FormData.FormField[] fields = formData.getFields();
        
        for(FormData.FormField field : fields) {
            	
                if(field.getIsFile()) {
                	//Getting fields from multipart-form
                    Content content = field.getContent();      
                	String filePath = uploadPolicy.getFilePath();
            		String fileName = new File(filePath).getName();
            		NodeRef nodeRefTargetFolder =null;            		
            		NodeRef nodeRefTargetFile = null;
            		ContentWriter writer = null;
            		NodeService nodeService = this.serviceRegistry.getNodeService();;
            		
            		//Create folder structure under Company Home in Alfresco Repository and return NodeRef
            		try {					
            			nodeRefTargetFolder = Utility.getFolderStructure(companyHome,filePath,this.serviceRegistry);				
            		} catch (InvalidArgumentException e1) {
            			
            			//model.put("redirectUrl", uploadPolicy.getRedirectUrl()+"?errorMessage="+ e1.getMessage());	
            			//return model;	
						throw new WebScriptException("Operation failed: error occurred during the creation of the folder structure under Company Home in Alfresco Repository");
						
            		}         		
            		
            		//Search for nodeRef with that filename under Company Home or in the target folder 
            		nodeRefTargetFile = gerNodRefTargetFile(companyHome,fileName, nodeRefTargetFolder);
            		
            		//If nodeRef exists for the same filename update nodeRef 
            		if(null!=nodeRefTargetFile){
            			
            			System.out.println("UPDATE targetNode with new content..");          			
            			//Obtaining contentwriter for new nodeRef to be updated         		
            			addTagVersionProperty(uploadPolicy, filePath,nodeRefTargetFile, nodeService);           		
            			writer = this.serviceRegistry.getContentService().getWriter(nodeRefTargetFile, ContentModel.TYPE_CONTENT, true);               	
            		
            		}else{
        			    
            			System.out.println("CREATE targetNode and put content..");
            			
	            		if(null==nodeRefTargetFolder){
	            			fileInfo = this.serviceRegistry.getFileFolderService().create(companyHome, fileName, ContentModel.TYPE_CONTENT);   
	            		}else{
	            			fileInfo = this.serviceRegistry.getFileFolderService().create(nodeRefTargetFolder, fileName, ContentModel.TYPE_CONTENT);  
	            		}

            			addTagVersionProperty(uploadPolicy, filePath,fileInfo.getNodeRef(), nodeService);        		
	            		
            			nodeService.addAspect(fileInfo.getNodeRef(), ContentModel.ASPECT_VERSIONABLE,null);             		                    
	            		//Obtaining contentwriter for new nodeRef
	                    writer = this.serviceRegistry.getFileFolderService().getWriter(fileInfo.getNodeRef());                                        
         
            		} 
                  	
            		writer.setMimetype(Utility.guessContentType(fileName));
					//getting ByteArrayOutputStream from InputStream
					byte[] data = getByteArrayOutputFromContent(uploadPolicy,content);                   
					writer.putContent(new ByteArrayInputStream(data));				
						
                    try {						
                    	model.put("redirectUrl", uploadPolicy.getRedirectUrl()+"?path=" + URLEncoder.encode(uploadPolicy.getFilePath(), "UTF-8"));					        
                    } catch (UnsupportedEncodingException e) {                  	
                    	e.printStackTrace();
					}
                 }	
                  
        }           
	   
	    return model;

    }

	private void addTagVersionProperty(UploadPolicy uploadPolicy,String filePath, NodeRef nodeRefTargetFile, NodeService nodeService) {
		
		Map<QName, Serializable> contentProps = new HashMap<QName, Serializable>();
		contentProps.put(ContentModel.PROP_AUTHOR, tagVersionPrefix+uploadPolicy.getTagVersion()+Utility.urlSafeBase64Encode(filePath));
		nodeService.addProperties(nodeRefTargetFile,contentProps);
	
	}

	private NodeRef gerNodRefTargetFile(NodeRef companyHome, String fileName,NodeRef nodeRefTargetFolder) {
		
		NodeRef nodeRefTargetFile;
		if(null==nodeRefTargetFolder){		         			          
		    nodeRefTargetFile = this.serviceRegistry.getFileFolderService().searchSimple(companyHome,fileName);
		}else{
			nodeRefTargetFile = this.serviceRegistry.getFileFolderService().searchSimple(nodeRefTargetFolder,fileName);
		}
		return nodeRefTargetFile;
	}
	
	
	private byte[] getByteArrayOutputFromContent(UploadPolicy uploadPolicy,
			Content content) {
		
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
		if (bao.size()/MEGABYTE > uploadPolicy.getMaximumSizeInMB()){			
			throw new WebScriptException("Operation denied: file uploaded too large");
		}
		return data;
	}

    
}
