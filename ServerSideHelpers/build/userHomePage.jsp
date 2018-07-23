<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
 <%@ taglib prefix="s"  uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>User Home Page</title>
</head>
<body>
	<%--  <s:if test="#session.loggedin != 'true'">
		<jsp:forward page="/index.jsp">
	</s:if>  --%>
	<h1>Login Successful! Welcome to the application</h1>
	
	<a href="<s:url action ='index'/>">Logout</a> <br> <br> <br>
	<a href="<s:url action ='listAudiotronsRecorded'/>">View Audiotrons Recorded</a> <br> <br>
	<a href="<s:url action ='listAudiotronsRated'/>">View Audiotrons Rated</a> <br> <br>
	<a href="<s:url action = 'listAudiotronsFavorited'/>">View Audiotrons marked as Favorites</a>
	
</body>
</html>