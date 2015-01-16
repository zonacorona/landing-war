<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
	String rackspace404Url = "http://www.rackspace.com/sitemap404.php?url=";
	String origUri = (String) request.getAttribute("origuri");
	if (null != origUri) {
		if(origUri.endsWith("/") && origUri.length()>1){
			origUri=origUri.substring(0,origUri.length()-2);
		}
		//int lastIndex=origUri.lastIndexOf("/");
		//if(-1!=lastIndex){
			//origUri=origUri.substring(lastIndex);
		//}
		rackspace404Url+=origUri;
		if(origUri.endsWith("/") && origUri.length()>1){
			origUri=origUri.substring(0,origUri.length()-2);
		}
		rackspace404Url+="%20site:docs.rackspace.com";
	}
	response.sendRedirect(rackspace404Url);
%>


