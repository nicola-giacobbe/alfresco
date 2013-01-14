package tsm.updownbacked.utility;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.codec.binary.Base64;
import org.springframework.extensions.webscripts.ui.common.StringUtils;
import org.springframework.security.crypto.codec.Hex;

import com.sun.star.auth.InvalidArgumentException;

public class Utility {

	
	public static String sign(String secretKey,String... items){

		SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(),"HmacSHA256");
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
		// compute hash
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
	
	public static String guessContentType(String filePath){
		String extension = "";

		int i = filePath.lastIndexOf('.');
		if (i > 0) {
		    extension = filePath.substring(i).toLowerCase();
		}
		
		if(extension.equals(".jpeg") || extension.equals(".jpg")){
			return "image/jpeg";
		}else if(extension.equals(".gif")){
			return "image/gif";
		}else if(extension.equals(".png")){
			return "image/png";
		}else if(extension.equals(".pdf")){
			return "application/pdf";
		}else if(extension.equals(".txt")){
			return "text/plain";
		}else if(extension.equals(".html") || extension.equals(".htm")){
			return "text/html";
		}else if(extension.equals(".zip")){
			return "application/zip";
		}else if(extension.equals(".csv")){
			return "text/csv";
		}
		
		return "application/octet-stream";

    }
	
	
    public static NodeRef getFolderStructure(NodeRef parent,String filePath,ServiceRegistry servRegistry)throws InvalidArgumentException{
		
		NodeRef nodeRefDestinationFolder = null;
		
		//Validate path
	    /*   String regularExpression = "([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?";
	    Pattern pattern = Pattern.compile(regularExpression);
	    boolean isMatched = Pattern.matches(regularExpression,filePath);
		if(!isMatched){
			
			throw new InvalidArgumentException();
		}*/
		//Obtain folder list from filePath
		LinkedList<String> folderList = new LinkedList<String>();
	    if(filePath.contains("/")){
	    	
			  String[] lines = filePath.split("/");
			  folderList = new LinkedList<String>(Arrays.asList(lines));  
			  //remove fileName
			  folderList.removeLast(); 
			  //creating folder structure under Company Home
		      NodeRef nodeRefParent=parent;
		      LinkedList<NodeRef> nodeRefList = new LinkedList<NodeRef>();
		      for (int i = 0; i < folderList.size(); i++) {
					   
		    	   FileInfo directoryInfo = null;
		    	   List<String> nodes = new ArrayList<String>();
		    	   nodes.add(folderList.get(i));
		    	   try{
		    		   //search for an existent folder under nodeRefParent
		    		   directoryInfo= servRegistry.getFileFolderService().resolveNamePath(nodeRefParent, nodes);
		    	  
		    	   }catch(FileNotFoundException ex){
		    		   
		    		   //if folder is not found create it
		    		   directoryInfo = servRegistry.getFileFolderService().create(nodeRefParent, folderList.get(i), ContentModel.TYPE_FOLDER); 
		    	   }
		    	   
	               nodeRefParent = directoryInfo.getNodeRef();   
			       nodeRefList.add(nodeRefParent);
		      }
		      
		      nodeRefDestinationFolder = nodeRefList.getLast();
		      
		}else{
			return null;
		}	
		return nodeRefDestinationFolder;	
	}
    
}
