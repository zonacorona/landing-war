<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body ng-app>

  <div id="controller" ng-controller="TryitController">
  
      <div class="credentials">
        <form id="credentialsform">
          
          <label class="headertitle">Credentials</label><br>
          <div id="unpass">
            <label id="unamelabel">User Name:</label>
            <input id="unametext" type="text" name="username" ng-model="uname"><br>
          
            <label id="passlabel">Password:</label>
            <input id="passtext" type="password" name="password" ng-model="passwd"><br>
            
          </div>
          <div id="authbtn" ng-click="authenticate()">Authenticate</div>
        </form>  
      </div>
      <div id="commandsectionid" style="display:none;">
        <div class="catalogs" ng-repeat="catalog in catalogs">
        <H3>{{catalog.name}}</H3><br>

							<div class="endpointselectclass" ng-bind-html-unsafe="renderEndpointSelection(catalog.endpoints)"></div>



        <%--
        <select class="endpointselectclass">
          <option ng-repeat="endpoint in catalog.endpoints" value="{{endpoint.publicURL}}">{{endpoint.region|{region:"default"}}}</option>
        </select>
        --%>
        <div class="commandborder" ng-repeat="command in commands">
        <div>
      
        </div>
      </div>


  
  </div>


  <script type="text/javascript" src="common/js/angular.min.js"></script>
  <script type="text/javascript" src="common/js/jquery1.10.2.min.js"></script>
  <script type="text/javascript" src="common/js/tryit.js"></script>
</body>
  <link type="text/css" rel="stylesheet" href="common/css/tryit.css" />
</html>