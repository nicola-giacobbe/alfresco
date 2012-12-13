package tsm.updownbacked.model;

import java.util.TreeMap;

public class DecodedPolicy {

	private boolean isSignedCorrectly;
	private String policyName;
	private TreeMap<String, String> values;
	
	public DecodedPolicy(boolean isSignedCorrectly, String policyType, TreeMap<String, String> values){		
		this.isSignedCorrectly = isSignedCorrectly;
		setPolicyName(policyType);
		this.setValues(values);
	}

	public boolean isSignedCorrectly() {
		return isSignedCorrectly;
	}

	public void setSignedCorrectly(boolean isSignedCorrectly) {
		this.isSignedCorrectly = isSignedCorrectly;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public TreeMap<String, String> getValues() {
		return values;
	}

	public void setValues(TreeMap<String, String> values) {
		this.values = values;
	}
	 
}
