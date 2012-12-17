package tsm.updownbacked.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.filestore.FileContentReader;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.io.IOUtils;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import tsm.updownbacked.model.DecodedPolicy;
import tsm.updownbacked.model.DownloadPolicy;
import tsm.updownbacked.model.Policy;
import tsm.updownbacked.utility.Utility;

public class DownloadGetController extends AbstractWebScript{
	 
private String key = "Dh_s0uzo1walbqnsScJJQy|ffs";

private Repository repository;
	
	private ServiceRegistry serviceRegistry;
	
	public void setRepository(Repository repository){
	    this.repository = repository;
	}
	  
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
	this.serviceRegistry = serviceRegistry;
}
	
@Override
public void execute(WebScriptRequest req, WebScriptResponse res)throws IOException {

		DecodedPolicy decodedPolicy = Policy.decodePolicy(key,req.getParameter("signedEncodedPolicy"));
		boolean policyNameIsWrong = !decodedPolicy.getPolicyName().equals("DownloadPolicy");
		boolean signatureIsWrong = decodedPolicy.isSignedCorrectly() == false;
		
		if (policyNameIsWrong || signatureIsWrong){
			
			throw new WebScriptException("Operation denied: policy signature is wrong");
		}
		DownloadPolicy downloadPolicy = DownloadPolicy.fromDecodedPolicy(decodedPolicy);
		if (downloadPolicy.isExpired()) {

			throw new WebScriptException("Operation denied: policy is expired");
		}
		
		
		NodeRef companyHome = repository.getCompanyHome();
		ContentReader reader = serviceRegistry.getContentService().getReader(companyHome, ContentModel.PROP_CONTENT);	
		reader.setMimetype(Utility.guessContentType(req.getParameter("fileName")));
        try { 
        	
        	FileChannel fileChannel = reader.getFileChannel();
            ByteBuffer buffer = ByteBuffer.allocate(64);
            fileChannel.read(buffer);
 
        } catch (IOException e) {
            e.printStackTrace();
        }
	        
	}  	
	
}



