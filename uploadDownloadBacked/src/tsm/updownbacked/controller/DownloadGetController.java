package tsm.updownbacked.controller;

import java.io.File;
import java.io.IOException;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import tsm.updownbacked.model.DecodedPolicy;
import tsm.updownbacked.model.DownloadPolicy;
import tsm.updownbacked.model.Policy;

public class DownloadGetController extends AbstractWebScript{
	 
private String key = "Dh_s0uzo1walbqnsScJJQy|ffs";

private final static String FILENAME_PARAM= "fileName";
private final static String DOWNLOAD_POLICY_NAME= "DownloadPolicy";

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

		DecodedPolicy decodedPolicy = Policy.decodePolicy(key,req.getParameter("policy"));
		boolean policyNameIsWrong = !decodedPolicy.getPolicyName().equals(DOWNLOAD_POLICY_NAME);
		boolean signatureIsWrong = decodedPolicy.isSignedCorrectly() == false;
		
		if (policyNameIsWrong || signatureIsWrong){
			
			throw new WebScriptException("Operation denied: policy signature is wrong");
		}
		DownloadPolicy downloadPolicy = DownloadPolicy.fromDecodedPolicy(decodedPolicy);
		if (downloadPolicy.isExpired()) {

			throw new WebScriptException("Operation denied: policy is expired");
		}
		String filePath = downloadPolicy.getFilePath();
		String fileName = new File(filePath).getName();
		
		NodeRef companyHome = repository.getCompanyHome();
		//Search fileName under CompanyHome Repository
		NodeRef nodeRef = this.serviceRegistry.getFileFolderService().searchSimple(companyHome,  fileName);
		try {
			ContentReader reader = this.serviceRegistry.getFileFolderService().getReader(nodeRef);
			reader.getContent(res.getOutputStream());
		} catch (Exception ex) {
			throw new WebScriptException("Unable to stream output");
		}
	        
	}  	
	
}



