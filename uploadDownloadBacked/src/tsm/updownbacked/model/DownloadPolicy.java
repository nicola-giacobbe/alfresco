package tsm.updownbacked.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

public class DownloadPolicy extends Policy {

	private String path;
	
	public DownloadPolicy(String path, Date expiresAt){
		
		this.expiresAt=expiresAt;
		this.path = path;
	}
	
	public DownloadPolicy(String path ){
		
		super();
		this.path = path;
	}
	
	@Override
	public String getSignedEncodedPolicy(String secretKey) {
		
		TreeMap values = new TreeMap<String, Object>();
		values.put("ExpiresAt", expiresAt);
		values.put("Path", path);
		return getSignedEncodedPolicy(secretKey, "DownloadPolicy", values);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static DownloadPolicy fromDecodedPolicy(DecodedPolicy decodedPolicy) {
		
		String path = decodedPolicy.getValues().get("Path");
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = null;
		try {
		
			date = (Date)formatter.parse(decodedPolicy.getValues().get("ExpiresAt"));
		
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		  
		Date expiresAt = date;
				
		return new DownloadPolicy(path,expiresAt);
	}
}
