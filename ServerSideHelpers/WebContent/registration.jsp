<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@ taglib prefix="s"  uri="/struts-tags" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Registration</title>
</head>
<body>
<s:form action="webregister">
	<%-- <s:textfield name="userName" label="Username"/>
	<s:textfield name="password" label="Password"/>
	<s:submit value="Register"></s:submit> --%>
	<s:textfield key="username"></s:textfield>
	<s:password key="password"></s:password>
	<s:submit value="Register"></s:submit>   
	</s:form>
</body>
</html>