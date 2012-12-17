package tsm.updownbacked.model;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;
import org.apache.commons.codec.binary.Base64;
import org.springframework.extensions.webscripts.WebScriptException;

import tsm.updownbacked.utility.Utility;

public abstract class Policy {

	protected Date expiresAt;
	
	protected Policy(){
		 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTime(new Date());
	     calendar.add(Calendar.MINUTE, 30);
	     setExpiresAt(calendar.getTime());
	}
	
	public abstract String getSignedEncodedPolicy(String secretKey);

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}
	
	public boolean isExpired(){
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(new Date());
		
	    if( calendar.getTime().compareTo(expiresAt)>0){
    		return true;
    	}else{
    		return false;
    	} 
	}

	private static String stringEncode(Object o)
	{
		if (o == null)
			return "";

		if (o instanceof Date){
			
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sd.setTimeZone(TimeZone.getTimeZone("GMT"));
		    return sd.format((Date) o);
		}	

		if (o instanceof Integer)
			return ((Integer)o).toString();

		if (o instanceof String)
			return (String) o;

		return o.toString();
	}
	
	
	public static String getSignedEncodedPolicy(String secretKey, String policyName, TreeMap<String, Object> values){
		
		StringBuilder unsigned = new StringBuilder();
		unsigned.append( String.format("%s\n", policyName) );
		
		for (Entry<String, Object> entry : values.entrySet()) { 
		 
			unsigned.append( String.format("%s=%s\n",entry.getKey(), stringEncode( entry.getValue() )) );
		}
		
		String signature = Utility.sign(secretKey, unsigned.toString());
		String signed = unsigned + "Signature=" + signature;
		return Utility.urlSafeBase64Encode(signed);
	}
	
	public static DecodedPolicy decodePolicy(String secretKey, String encodedPolicy){
		
		Base64 decoder = new Base64(true);
	    byte[] decodedBytes = decoder.decode(encodedPolicy);
	    String decoded = new String(decodedBytes);

	    String[] lines = decoded.split("\n");
	    LinkedList<String> linesLinkedList = new LinkedList<String>(Arrays.asList(lines));
	    
	    String signatureLine = linesLinkedList.getLast();
	    if (signatureLine.startsWith("Signature=") == false){
	    	 throw new WebScriptException("decoded policy doesn't have a correct format");
	    }
	    linesLinkedList.removeLast();
	    //find signature
        String signature = signatureLine.substring(signatureLine.indexOf("=") + 1);
	    
        //find policyName
	    String policyName = linesLinkedList.getFirst();
		linesLinkedList.removeFirst();
		
		TreeMap<String, String> values = new TreeMap<String, String>();
	    for (String pairLine : linesLinkedList) {
			
			String[] parts = pairLine.split("="); 
			values.put(parts[0],parts[1]);
		}
	    		
		boolean signatureIsCorrect = Utility.sign( secretKey,getUnsignedString(linesLinkedList,policyName,values)).equals( signature );
	    
		return new DecodedPolicy(signatureIsCorrect, policyName, values);
	}
	
	private static String getUnsignedString(LinkedList<String> linesLinkedList,String policyName,TreeMap<String, String> values){
		
		StringBuilder unsigned = new StringBuilder();
		unsigned.append( String.format("%s\n", policyName) );
		
		for (Entry<String, String> entry : values.entrySet()) { 
		 
			unsigned.append( String.format("%s=%s\n",entry.getKey(), stringEncode( entry.getValue() )) );
		}
		
		return unsigned.toString();
		
	}
	
}
