package tsm.updownbacked.controller;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
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
	private final static String DOWNLOAD_POLICY_NAME= "DownloadPolicy";
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
	public void execute(WebScriptRequest req, WebScriptResponse res)throws IOException {

		ContentReader reader =null;
		/*PolicyGenerator policyGenerator = new PolicyGenerator(key);
		String encodedUploadPolicy = policyGenerator.getEncodedDownloadPolicyParam("BA/BI/pet.txt", "2");		
		DecodedPolicy decodedPolicy = Policy.decodePolicy(key,encodedUploadPolicy);*/
		
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
		if(companyHome==null){
			throw new WebScriptException("Unable to find the company home node");
		}
		
		//Take the node base on the TargetVersion
		if(null!=downloadPolicy.getTagVersion() && !downloadPolicy.getTagVersion().isEmpty()){
			
			StoreRef workspaceStoreRef = new StoreRef("workspace://SpacesStore");
			StoreRef archiveStoreRef = new StoreRef("archive://SpacesStore");
			String queryString = "@cm\\:author:"+(char)34+tagVersionPrefix+downloadPolicy.getTagVersion()+Utility.urlSafeBase64Encode(filePath)+(char)34+"AND@cm\\:name:"+(char)34+fileName+(char)34;
			
			//At the moment, there can only be one store set for the search: NEED TO DO TWO SEARCH	
			//SEARCH in workspace(header version are here)
			NodeRef targetNodeRef = null;
			SearchParameters sp = new SearchParameters();
			sp.addStore(workspaceStoreRef);
	        sp.setLanguage(SearchService.LANGUAGE_LUCENE);
	        sp.setQuery(queryString);
	        ResultSet resultsWorkSpace = null;
	        resultsWorkSpace = serviceRegistry.getSearchService().query(sp);
	        for(ResultSetRow row : resultsWorkSpace){
	            targetNodeRef = row.getNodeRef();	           
	        }
	        System.out.println("size resultsWorkSpace"+resultsWorkSpace.length());
	        	        
	        //SEARCH in archive(not header version are here as archived nodes)
	        SearchParameters spArchive = new SearchParameters();
	        spArchive.addStore(archiveStoreRef);
	        spArchive.setLanguage(SearchService.LANGUAGE_LUCENE);
	        spArchive.setQuery(queryString);
	        ResultSet resultsArchive = null;
	        resultsArchive = serviceRegistry.getSearchService().query(spArchive);
	        
	        for(ResultSetRow row : resultsArchive){
	            targetNodeRef = row.getNodeRef();	           
	        }
	        
	        if(targetNodeRef==null){
				throw new WebScriptException("Unable to find the target version node");
	        }
	        System.out.println("size resultsArchive"+resultsArchive.length());
	        
			reader = this.serviceRegistry.getFileFolderService().getReader(targetNodeRef);	
		
		//Take header version if no targetVersion is not provided
		}else{
			
			FileInfo fileInfo = searchFileInfoByFilePath(filePath, companyHome);	
			reader = this.serviceRegistry.getFileFolderService().getReader(fileInfo.getNodeRef());		
		}
		
		try {				
			reader.getContent(res.getOutputStream());
			reader.setMimetype(Utility.guessContentType(fileName));
		} catch (Exception ex) {
			//ex.printStackTrace();
			WebScriptException ex2 = new WebScriptException("Unable to stream output");
			ex2.initCause(ex);
			throw ex2;
		}
	        
	}

	private FileInfo searchFileInfoByFilePath(String filePath, NodeRef companyHome) {
		
		LinkedList<String> folderList = new LinkedList<String>();
		if(filePath.contains("/")){
			  String[] lines = filePath.split("/");
			  folderList = new LinkedList<String>(Arrays.asList(lines)); 
		}

		FileInfo fileInfo=null;
		try {
		
			fileInfo = this.serviceRegistry.getFileFolderService().resolveNamePath(companyHome, folderList);
		
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			throw new WebScriptException("Unable to find the file");
		}
		
		return fileInfo;
	}  	
	
}



