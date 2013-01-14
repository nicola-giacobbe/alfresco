package tsm.updownbacked.utility;

import tsm.updownbacked.model.DownloadPolicy;
import tsm.updownbacked.model.UploadPolicy;

public class PolicyGenerator {

	private String secretKey;
	
	public PolicyGenerator(String secretKey){
		
		this.secretKey=secretKey;
	} 
	
	public String getEncodedUploadedPolicyParam(int maximumSizeInMB,String filePath,String redirectUrl,String tagVersion){
		
		UploadPolicy policy = new UploadPolicy (maximumSizeInMB,filePath,redirectUrl,tagVersion);
		String policyParam = policy.getSignedEncodedPolicy(secretKey);
		return policyParam;
	}
	
	public String getEncodedDownloadPolicyParam(String filePath,String tagVersion){
		
		DownloadPolicy policy = new DownloadPolicy(filePath,tagVersion);
		String policyParam = policy.getSignedEncodedPolicy(secretKey);
		return policyParam;
	}
	
}
