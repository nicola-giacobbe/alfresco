package tsm.updownbacked.utility;

import tsm.updownbacked.model.UploadPolicy;

public class PolicyGenerator {

	private String secretKey;
	
	public PolicyGenerator(String secretKey){
		
		this.secretKey=secretKey;
	}
	
	public String getEncodedUploadedPolicyParam(int maximumSizeInMB){
		
		UploadPolicy policy = new UploadPolicy (
				maximumSizeInMB
		);
		
		String policyParam = policy.getSignedEncodedPolicy(secretKey);

		return policyParam;
	}
	
}
