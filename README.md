alfresco
========

- class files must be put under ..tomcat\webapps\alfresco\WEB-INF\classes\.. with the same folder structure 
as the package source:
e.g \alfresco\WEB-INF\classes\tsm\updownbacked for package tsm.updownbacked...etc

- in ..Alfresco\tomcat\shared\classes\alfresco\extension we put the a context xml file to define beans for java backed
webscripts:
 
<bean id="webscript.tsm.upload.upload.get"
	class="tsm.updownbacked.controller.UploadGetController" parent="webscript">
</bean>

- The webscripts(descriptor,controller,response files) are loaded from the Alfresco home page, just browse inside 
Company Home from the navigation menu' then create the folders in which put the webscripts as a content. 

The webscript 'upload.get' inside the tsm\upload subfolder of Company Home parent folder will be linked with the 
controller specified by the bean.
 
The upload webscripts go under ...\Company Home\tsm\upload
The download webscripts go under ...\Company Home\tsm\download

the webscript descriptors will link an url(../alfresco/service/upload/) to the webscript
the webscripts response files are the .ftl files
