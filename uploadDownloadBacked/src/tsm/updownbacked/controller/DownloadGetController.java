package tsm.updownbacked.controller;

public class DownloadGetController {

	 /*  [HttpGet]
				public ActionResult Download(string path, string policy)
				{
					var decodedPolicy = Policy.DecodePolicy(SecretKey, policy);

					var policyNameIsWrong = decodedPolicy.PolicyName != "DownloadPolicy";
					var signatureIsWrong = decodedPolicy.IsSignedCorrectly == false;
					if (policyNameIsWrong || signatureIsWrong)
						return Forbidden();

					var downloadPolicy = DownloadPolicy.FromDecodedPolicy(decodedPolicy);
					if (downloadPolicy.IsExpired)
						return Forbidden();

					return File(Storage.GetFile(downloadPolicy.FilePath), 
						GuessContentType(downloadPolicy.FilePath));
				}

			    private string GuessContentType(string filePath)
			    {
					// todo: improve content type guessing, possibly based on 
					// http://stackoverflow.com/questions/1910097/content-type-by-extension
				    switch (Path.GetExtension(filePath).ToLowerInvariant())
					{
						case ".jpg":
						case ".jpeg":
							return "image/jpeg";
						case ".gif":
							return "image/gif";
						case ".png":
							return "image/png";
						case ".pdf":
							return "application/pdf";
						case ".txt":
							return "text/plain";
						case ".html":
						case ".htm":
							return "text/html";
						case ".zip":
							return "application/zip";
						case ".csv":
							return "text/csv";
						default:
							return "application/octet-stream";
					}
			    }*/

}
