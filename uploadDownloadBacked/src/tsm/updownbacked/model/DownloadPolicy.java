package tsm.updownbacked.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

public class DownloadPolicy extends Policy {

	private String filePath;
	
	public DownloadPolicy(String filePath, Date expiresAt,String tagVersion){
		
		this.expiresAt=expiresAt;
		this.filePath = filePath;
		this.tagVersion = tagVersion;
	}
	
	public DownloadPolicy(String filePath,String tagVersion ){
		
		super();
		this.filePath = filePath;
		this.tagVersion = tagVersion;
	}
	
	@Override
	public String getSignedEncodedPolicy(String secretKey) {
		
		TreeMap values = new TreeMap<String, Object>();
		values.put("FilePath", filePath);
		values.put("ExpiresAt", expiresAt);
		values.put("TagVersion", tagVersion);
		return getSignedEncodedPolicy(secretKey, "DownloadPolicy", values);
	}


	public static DownloadPolicy fromDecodedPolicy(DecodedPolicy decodedPolicy) {
		
		String filePath = decodedPolicy.getValues().get("FilePath");
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = null;
		System.out.println("ExpiresAt value: "+decodedPolicy.getValues().get("ExpiresAt"));
		try {
		
			date = (Date)formatter.parse(decodedPolicy.getValues().get("ExpiresAt"));
		
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		  
		Date expiresAt = date;
		
		String tagVersion = decodedPolicy.getValues().get("TagVersion");
				
		return new DownloadPolicy(filePath,expiresAt,tagVersion);
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}
