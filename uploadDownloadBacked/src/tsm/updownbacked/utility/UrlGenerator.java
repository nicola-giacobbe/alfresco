package tsm.updownbacked.utility;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import tsm.updownbacked.model.Policy;
import tsm.updownbacked.model.UploadPolicy;


public class UrlGenerator {

	private String secretKey;
	private URI uri;
	
	public UrlGenerator(String baseUrl, String secretKey) throws URISyntaxException{
		
		this.secretKey=secretKey;
		uri= new URI(baseUrl);
	}
	
	public String getUploadUrl(String path, String redirectUrl, int maximumSizeInMB, Date expiresAt){
		UploadPolicy policy = new UploadPolicy (maximumSizeInMB,expiresAt);
		return getUploadUrl(policy);
	}
	
	public String getUploadUrl(UploadPolicy policy){
		return getUrlWithPolicy(policy);
	}
	
	
	private String getUrlWithPolicy(Policy policy){
		
		String queryStringAdditions = "policy=" + policy.getSignedEncodedPolicy(secretKey);

		String uploadUrl = uri.getPath();
		CharSequence charQuestionMark="?";
		
		if (uploadUrl.contains(charQuestionMark))
			uploadUrl += "&" + queryStringAdditions;
		else
			uploadUrl += "?" + queryStringAdditions;

		return uploadUrl;
	}
	
	public String getEncodedUploadedPolicyParam(int maximumSizeInMB){
		
		UploadPolicy policy = new UploadPolicy (maximumSizeInMB);
		
		String policyParam = policy.getSignedEncodedPolicy(secretKey);
		return policyParam;
	}
	
}
