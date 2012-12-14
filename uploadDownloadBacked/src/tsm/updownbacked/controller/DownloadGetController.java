package tsm.updownbacked.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import tsm.updownbacked.model.DecodedPolicy;
import tsm.updownbacked.model.DownloadPolicy;
import tsm.updownbacked.model.Policy;

public class DownloadGetController extends AbstractWebScript{
	 
private String key = "Dh_s0uzo1walbqnsScJJQy|ffs";

@Override
public void execute(WebScriptRequest req, WebScriptResponse res)throws IOException {

		DecodedPolicy decodedPolicy = Policy.decodePolicy(key,req.getParameter("signedEncodedPolicy"));
		boolean policyNameIsWrong = !decodedPolicy.getPolicyName().equals("DownloadPolicy");
		boolean signatureIsWrong = decodedPolicy.isSignedCorrectly() == false;
		
		if (policyNameIsWrong || signatureIsWrong){
			
			throw new WebScriptException("Operation denied: policy signature is wrong");
		}
		DownloadPolicy downloadPolicy = DownloadPolicy.fromDecodedPolicy(decodedPolicy);
		if (downloadPolicy.isExpired()) {

			throw new WebScriptException("Operation denied: policy is expired");
		}

		ServletOutputStream out = null;
		try {
			out = (ServletOutputStream) res.getOutputStream();

		} catch (IOException e) {
			e.printStackTrace();
		}
		res.setContentType(guessContentType(downloadPolicy.getPath()));
		File file = new File(req.getParameter(downloadPolicy.getPath()));
		BufferedInputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			byte[] buf = new byte[4 * 1024]; // 4K buffer
			int bytesRead;
			while ((bytesRead = is.read(buf)) != -1) {
				out.write(buf, 0, bytesRead);
			}
			is.close();
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new WebScriptException("Operation failed: error during I/O Operation");
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebScriptException("Operation failed: error during I/O Operation");
		}

	}  

	
	private String guessContentType(String filePath)
    {
		String extension = "";

		int i = filePath.lastIndexOf('.');
		if (i > 0) {
		    extension = filePath.substring(i+1).toLowerCase();
		}
		
		if(extension.equals(".jpg")){
			
		}else if(extension.equals(".jpeg")){
			return "image/jpeg";
		}else if(extension.equals(".gif")){
			return "image/gif";
		}else if(extension.equals(".pdf")){
			return "image/png";
		}else if(extension.equals(".txt")){
			return "application/pdf";
		}else if(extension.equals(".html")){
			return "text/plain";
		}else if(extension.equals(".htm")){
			return "text/html";
		}else if(extension.equals(".zip")){
			return "application/zip";
		}else if(extension.equals(".csv")){
			return "text/csv";
		}
		
		return "application/octet-stream";

    }
	
	
}



