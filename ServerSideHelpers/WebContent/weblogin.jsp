<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Welcome to Login page</title>
</head>
<body>
	<s:form action="weblogin" method="post">
	<s:textfield key="userName"></s:textfield>
	<s:password key="password" ></s:password>
	<s:submit></s:submit>
	</s:form>
	<s:div id="errorMessage"><s:property value="errorMessage"/></s:div>
	<a href="<s:url action ='registration.jsp'/>">Register</a>
</body>
</html>