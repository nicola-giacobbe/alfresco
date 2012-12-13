package tsm.updownbacked.model;

import java.util.Date;
import java.util.TreeMap;

public class DownloadPolicy extends Policy {

	public DownloadPolicy(String path, Date expiresAt){
		
		this.expiresAt = expiresAt;
		
	}
	
	
	@Override
	public String getSignedEncodedPolicy(String secretKey) {
		
		TreeMap values = new TreeMap<String, Object>();
		values.put("ExpiresAt", expiresAt);
		return getSignedEncodedPolicy(secretKey, "DownloadPolicy", values);
	}

}
