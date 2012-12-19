package tsm.updownbacked.utility;

import tsm.updownbacked.model.DownloadPolicy;
import tsm.updownbacked.model.UploadPolicy;

public class PolicyGenerator {

	private String secretKey;
	
	public PolicyGenerator(String secretKey){
		
		this.secretKey=secretKey;
	} 
	
	public String getEncodedUploadedPolicyParam(int maximumSizeInMB,String filePath,String redirectUrl){
		
		UploadPolicy policy = new UploadPolicy (maximumSizeInMB,filePath,redirectUrl);
		String policyParam = policy.getSignedEncodedPolicy(secretKey);
		return policyParam;
	}
	
	public String getEncodedDownloadPolicyParam(String filePath){
		
		DownloadPolicy policy = new DownloadPolicy(filePath);
		String policyParam = policy.getSignedEncodedPolicy(secretKey);
		return policyParam;
	}
	
}
