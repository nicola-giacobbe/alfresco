package tsm.updownbacked.model;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

public class UploadPolicy extends Policy {

	private int maximumSizeInMB;
	
	public UploadPolicy(){
		setMaximumSizeInMB(25);
	}
	
	public UploadPolicy(int maximumSizeInMB,Date expiresAt){
		
		this.maximumSizeInMB = maximumSizeInMB;
		this.expiresAt = expiresAt;
	}
	
	public UploadPolicy(int maximumSizeInMB){
		super();
		this.maximumSizeInMB = maximumSizeInMB;
	}

	public int getMaximumSizeInMB() {
		return maximumSizeInMB;
	}

	public void setMaximumSizeInMB(int maximumSizeInMB) {
		this.maximumSizeInMB = maximumSizeInMB;
	}
	
	
	public String getSignedEncodedPolicy(String secretKey){
		
		TreeMap<String, Object> values = new TreeMap<String, Object>();
		values.put("MaximumSizeInMB", maximumSizeInMB);
		values.put("ExpiresAt", expiresAt);

		return getSignedEncodedPolicy(secretKey, "UploadPolicy", values);
	}
	
	public static UploadPolicy fromDecodedPolicy(DecodedPolicy decodedPolicy) {
		
		int maximumSizeInMB = Integer.parseInt( decodedPolicy.getValues().get("MaximumSizeInMB") );
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = null;
		try {
		
			date = (Date)formatter.parse(decodedPolicy.getValues().get("ExpiresAt"));
		
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		  
		Date expiresAt = date;
				
		return new UploadPolicy(maximumSizeInMB,expiresAt);
	}
	
}
