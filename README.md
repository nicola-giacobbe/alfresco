alfresco
========

- .class files must be put under with the same folder structure as the package source:
..tomcat\webapps\alfresco\WEB-INF\classes\..
e.g \alfresco\WEB-INF\classes\tsm\updownbacked for package tsm.updownbacked...etc

-..Alfresco\tomcat\shared\classes\alfresco\extension we put the a context xml file to define beans for java backed webscripts:
 
<bean id="webscript.tsm.upload.upload.get"
	class="tsm.updownbacked.controller.UploadGetController" parent="webscript">
</bean>

The webscript 'upload.get' inside the tsm\upload subfolder of Company Home parent folder will be linked with the controller specified by the bean
 
The upload webscripts go under ...\Company Home\tsm\upload
The download webscripts go under ...\Company Home\tsm\download

the webscript descriptor will link an url(../alfresco/service/upload/) to the webscript