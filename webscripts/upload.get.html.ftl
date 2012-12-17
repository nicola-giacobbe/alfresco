<html>
 <head> 
   <title>Upload Web Script </title> 
   <link rel="stylesheet" href="${url.context}/css/main.css" TYPE="text/css">
 </head>
 <body>
   <table>
     <tr>
       <td><img src="${url.context}/images/logo/AlfrescoLogo32.png" alt="Alfresco" /></td>
       <td><nobr>Upload Web Script </nobr></td>
     </tr>
     <tr><td><td>Alfresco ${server.edition} v${server.version}
   </table>
   <p>
   <table>
     <form action="${url.service}" method="post" enctype="multipart/form-data" accept-charset="utf-8">
       <tr><td>File:</td><td><input type="file" name="file"></td></tr>
       <tr><td>Title:</td><td><input name="title"></td></tr>
       <tr><td></td></tr>
       <tr><td><input type="submit" name="submit" value="Upload"></td></tr>

       <input type="hidden" name="signedEncodedPolicy" value="${signedEncodedPolicy}">
     </form>
   </table>
 </body>
</html>