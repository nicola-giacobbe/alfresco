package tsm.updownbacked.test;

import java.text.ParseException;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.sun.star.auth.InvalidArgumentException;

import tsm.updownbacked.model.DecodedPolicy;
import tsm.updownbacked.model.Policy;
import tsm.updownbacked.model.UploadPolicy;
import tsm.updownbacked.utility.PolicyGenerator;

public class Test { 
	
	 private static String secretKey = "Dh_s0uzo1walbqnsScJJQy|ffs";
	
	 public static void main(String args[]) throws ParseException, InvalidArgumentException{
		 
			PolicyGenerator policyGenerator = new PolicyGenerator(secretKey);
	 		TreeMap<String, Object> model = new TreeMap<String, Object>();
	 		//maximumSizeInMB=25,ExpiresAt=30min	
	 		//String encodedUploadPolicy = policyGenerator.getEncodedUploadedPolicyParam(25);

	    	//DecodedPolicy decodedPolicy = Policy.decodePolicy(secretKey, encodedUploadPolicy);
	    	//UploadPolicy uploadPolicy = UploadPolicy.fromDecodedPolicy(decodedPolicy);
	 	    String regularExpression = "([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?";
		    Pattern pattern = Pattern.compile(regularExpression);
		    boolean isMatched = Pattern.matches(regularExpression,"BIM/BANG/path.txt");
			if(!isMatched){
				
				throw new InvalidArgumentException();
			}
	 }
	 
}
