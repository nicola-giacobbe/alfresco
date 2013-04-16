package tsm.updownbacked.model;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

public class UploadPolicy extends Policy {

	private int maximumSizeInMB;
	private String filePath;
	private String redirectUrl;
	
	public UploadPolicy(){
		setMaximumSizeInMB(25);
	}
	
	public UploadPolicy(int maximumSizeInMB,Date expiresAt){
		
		this.maximumSizeInMB = maximumSizeInMB;
		this.expiresAt = expiresAt;
	}
	
	public UploadPolicy(int maximumSizeInMB,Date expiresAt,String filePath,String redirectUrl,String tagVersion){
		
		this.maximumSizeInMB = maximumSizeInMB;
		this.expiresAt = expiresAt;
		this.filePath = filePath;
		this.redirectUrl = redirectUrl;
		this.tagVersion = tagVersion;
	}
	
	public UploadPolicy(int maximumSizeInMB){
		super();
		this.maximumSizeInMB = maximumSizeInMB;
	}
	
	public UploadPolicy(int maximumSizeInMB,String filePath,String redirectUrl,String tagVersion){
		super();
		this.maximumSizeInMB = maximumSizeInMB;
		this.filePath = filePath;
		this.redirectUrl = redirectUrl;
		this.tagVersion = tagVersion;
	}
	
	public String getSignedEncodedPolicy(String secretKey){
		
		TreeMap<String, Object> values = new TreeMap<String, Object>();
		values.put("MaximumSizeInMB", maximumSizeInMB);
		values.put("FilePath", filePath);
		values.put("RedirectUrl", redirectUrl);
		values.put("ExpiresAt", expiresAt);
		values.put("TagVersion", tagVersion);
		

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
		
		String filePath = decodedPolicy.getValues().get("FilePath");
		String redirectUrl = decodedPolicy.getValues().get("RedirectUrl");
		String tagVersion = decodedPolicy.getValues().get("TagVersion");
					
		return new UploadPolicy(maximumSizeInMB,expiresAt,filePath,redirectUrl,tagVersion);
	}
	
	public int getMaximumSizeInMB() {
		return maximumSizeInMB;
	}

	public void setMaximumSizeInMB(int maximumSizeInMB) {
		this.maximumSizeInMB = maximumSizeInMB;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
}
