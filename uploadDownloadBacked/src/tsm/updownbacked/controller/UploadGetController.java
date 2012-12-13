package tsm.updownbacked.controller;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import tsm.updownbacked.utility.PolicyGenerator;

public class UploadGetController extends DeclarativeWebScript{
	 
 	private String secretKey = "Dh_s0uzo1walbqnsScJJQy|ffs";
	
 	@Override
 	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache){
 		
 		PolicyGenerator policyGenerator = new PolicyGenerator(secretKey);
 		TreeMap<String, Object> model = new TreeMap<String, Object>();
 		//maximumSizeInMB=25,ExpiresAt=30min	
 		String encodedUploadPolicy = policyGenerator.getEncodedUploadedPolicyParam(25);
 	    model.put("signedEncodedPolicy", encodedUploadPolicy);
 	    return model;
 	}
 	
	
}
