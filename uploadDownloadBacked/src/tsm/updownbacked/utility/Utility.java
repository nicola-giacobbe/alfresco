package tsm.updownbacked.utility;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.extensions.webscripts.ui.common.StringUtils;
import org.springframework.security.crypto.codec.Hex;

public class Utility {

	
	public static String sign(String secretKey,String... items){
		
		SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
	    Mac mac = null;
		
	    try {
	    	
	    	mac = Mac.getInstance("HmacSHA256");
		
	    } catch (NoSuchAlgorithmException e1) {
			
			e1.printStackTrace();
		} 
	    
        try {
        	
			mac.init(signingKey);		
			
		} catch (InvalidKeyException e) {
			
			e.printStackTrace();
		}
 
        byte[] bytes = StringUtils.join(items, "\n").getBytes();
        //compute hash
        byte[] rawHmac = mac.doFinal(bytes);
        return new String(Hex.encode(rawHmac));
	}


	
	public static String urlSafeBase64Encode(String s){		
		String result = null;
		Base64 decoder = new Base64(true);
	    byte[] decodedBytes = decoder.encode(s.getBytes());
	    result = new String(decodedBytes);
		return result;
	}

	public static String urlSafeBase64Decode(String s){
		  
		String result = null;
		//decoding URL-safe
		Base64 decoder = new Base64(true);
	    byte[] decodedBytes = decoder.decode(s);
	    result = new String(decodedBytes);
		return result;
	}   
	
	
}
