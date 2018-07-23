<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
	<style>
		body, input{
			font-family: Calibri, Arial;
		}
		table {
			border-collapse: collapse;
			width:550px;
		}
		th {
			height: 40px;
			background-color: #ffee55;
		}
	</style>
	<title>List of Audiotrons</title>
</head>
<body>
	<h2>Audiotrons Recorded</h2>
	<table id="media" border="1">
		<tr>
			<td> Audiotron Name </td>
			<td> Audiotron Creation Date </td>
			<td> Category </td>
			<td> Edit Audiotron Name </td>
			<td> Delete Audiotron </td>
		</tr>
		<s:iterator var="i" step="1" value="audiotronListForUserFromDB">
			<tr>
				<td><s:property value="audiotronName"/></td>
				<td><s:property value="audiotronCreationDate"/></td>
				<td><s:property value="category.categoryName"/></td>
				<td>
					<s:url id="editURL" action="editAudiotronName">
					<s:param name="mediaId" value="%{mediaId}"></s:param>
					<s:form action="editAudiotronName">
							<s:hidden name="mediaId" />
							<s:textfield name="audiotronName" label="Audiotron Name" />
							<s:submit />
					</s:form>
					</s:url>
					<%-- <s:a href="%{editURL}">Edit</s:a> --%>
				</td>
				<td>
					<s:url id="deleteURL" action="deleteAudiotronsRecorded">
					<s:param name="mediaId" value="%{mediaId}"></s:param>
					</s:url>
					<s:a href="%{deleteURL}">Delete</s:a>
				</td>
			</tr>
		</s:iterator>
	</table>
	<br>
	<br>
	<br>
	<h2>Audiotrons Rated</h2>
	<table id="feedBackBean" border="1">
		<tr>
			<td> Audiotron Name </td>
			<td> Audiotron Creation Date </td>
			<td> Rating </td>
			<td> Category </td>
		</tr>
		<s:iterator var="i" step="1" value="audiotronsRatedByUser">
			<tr>
				<td><s:property value="media.audiotronName"/></td>
				<td><s:property value="media.audiotronCreationDate"/></td>
				<td><s:property value="rating"/></td>
				<td><s:property value="media.category.categoryName"/></td>
			</tr>
		</s:iterator>
	</table>
	<br>
	<br>
	<br>
	<h2> Audiotrons marked as Favorites</h2>
	<table id="favoriteBean" border="1">
	<tr>
		<td> Audiotron Name </td>
		<td> Audiotron Creation Date </td>
		<td> Category </td>
		<td> Unmark audiotron as Favorite </td>
	</tr>
	<s:iterator var="i" step="1" value="favoriteAudiotronsListByUser">
			<tr>
				<td><s:property value="media.audiotronName"/></td>
				<td><s:property value="media.audiotronCreationDate"/></td>
				<td><s:property value="media.category.categoryName"/></td>
				<td>
					<s:url id="unmarkURL" action="unmarkAudiotronAsFavorite">
					<s:param name="favoritesId" value="%{favoritesId}"></s:param>
					</s:url>
					<s:a href="%{unmarkURL}">Unmark as Favorite</s:a>
			</tr>
	</s:iterator>
	</table>		
</body>
</html>