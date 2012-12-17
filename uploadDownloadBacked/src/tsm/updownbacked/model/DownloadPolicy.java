package tsm.updownbacked.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

public class DownloadPolicy extends Policy {

	private String idNodRef;
	
	public DownloadPolicy(String idNodRef, Date expiresAt){
		
		this.expiresAt=expiresAt;
		this.idNodRef = idNodRef;
	}
	
	public DownloadPolicy(String idNodRef ){
		
		super();
		this.idNodRef = idNodRef;
	}
	
	@Override
	public String getSignedEncodedPolicy(String secretKey) {
		
		TreeMap values = new TreeMap<String, Object>();
		values.put("ExpiresAt", expiresAt);
		values.put("idNodRef", idNodRef);
		return getSignedEncodedPolicy(secretKey, "DownloadPolicy", values);
	}

	public String getIdNodRef() {
		return idNodRef;
	}

	public void setIdNodRef(String idNodRef) {
		this.idNodRef = idNodRef;
	}

	public static DownloadPolicy fromDecodedPolicy(DecodedPolicy decodedPolicy) {
		
		String idNodRef = decodedPolicy.getValues().get("idNodRef");
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = null;
		try {
		
			date = (Date)formatter.parse(decodedPolicy.getValues().get("ExpiresAt"));
		
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		  
		Date expiresAt = date;
				
		return new DownloadPolicy(idNodRef,expiresAt);
	}
}
