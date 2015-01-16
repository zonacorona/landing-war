<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="com.rackspace.cloud.api.beans.IndexBean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean id="bean" class="com.rackspace.cloud.api.beans.IndexBean"/>

<%
    bean.setServletCtx(getServletContext());
    bean.getRssFeeds();
%>

<html>
<head>
  <title>Rackspace Cloud Technical Documentation</title>
  <link rel="stylesheet" type="text/css" href="https://7d69c24efa79ed2429e6-1e1c5ac10380ee72a01b057764b93d72.ssl.cf1.rackcdn.com/bootstrap.min.css"  media="screen"/>
  <link rel="stylesheet" type="text/css" href="https://7d69c24efa79ed2429e6-1e1c5ac10380ee72a01b057764b93d72.ssl.cf1.rackcdn.com/bootstrap-responsive.min.css" rel="stylesheet" media="screen"/>
  
  <link rel="shortcut icon" href="//images.cdn.rackspace.com/icons/favicon.ico" type="common/images/x-icon"/>
  <script type="text/javascript" src="//code.jquery.com/jquery-1.11.0.min.js"></script>
  <script type="text/javascript" src="https://7d69c24efa79ed2429e6-1e1c5ac10380ee72a01b057764b93d72.ssl.cf1.rackcdn.com/bootstrap.min.js"></script>
  <link rel="stylesheet" type="text/css" href="https://7d69c24efa79ed2429e6-1e1c5ac10380ee72a01b057764b93d72.ssl.cf1.rackcdn.com/landing-mine.css"/>
  <script type="text/javascript" src="common/js/custom.js"></script>
  <script type="text/javascript">
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', 'UA-23102455-4']);
    _gaq.push(['_trackPageview']);

    (function() {
      var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
      ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
      var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
    })();
  </script>
   
  <link rel="canonical" href="http://docs.rackspace.com"/>
 <link rel="alternate" type="application/atom+xml"
      href="/feeds/atom-all.xml" title="All Rackspace Technical Documentation Atom Feed">

<c:forEach var="aCatProd" items="${bean.categorizedProducts}">
  <c:forEach var="aprod" items="${aCatProd.value.productsMap}">
    <c:if test="${aprod.value.typesListSize>0}">
     <link rel="alternate" 
           type="application/atom+xml"
           href="/feeds/atom-${aprod.value.id}.xml" title="${aprod.value.productName} Atom Feed">
    </c:if> 
  </c:forEach>
</c:forEach>

<%--   Formerlly we had the products in two different Maps, now we have consolidated it into the categorizedProducts Map     
 <c:forEach var="aprod" items="${bean.productsCol1Map}">
    <c:if test="${aprod.value.typesListSize>0}">
    <link rel="alternate" type="application/atom+xml"
      href="/feeds/atom-${aprod.value.id}.xml" title="${aprod.value.productName} Atom Feed">
  </c:if>
  </c:forEach>
  <c:forEach var="aprod" items="${bean.productsCol2Map}">
    <c:if test="${aprod.value.typesListSize>0}">
    <link rel="alternate" type="application/atom+xml"
      href="/feeds/atom-${aprod.value.id}.xml" title="${aprod.value.productName} Atom Feed">
    </c:if>
  </c:forEach>
--%>
  <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
  <script type="text/javascript" 
        src="//content-services.rackspace.com/rax-headerservice/rest/service/raxheaderservice.js?headerdivid=raxheaderfooterservice-headercontent&amp;footerdivid=raxheaderfooterservice-footercontent&amp;contentdivid=container&amp;filter=api_docs&amp;team=api">
  </script>

</head>
<body>

<!----   Top Nav Bar -->
<div id="raxheaderfooterservice-headercontent"></div>
<noscript>
  <div class="noscript-message-wrapper">
    <div class="noscript-message-container">
      <p>The Rackspace Support Network works best with JavaScript enabled. Please <a href="http://enable-javascript.com" target="_blank">enable JavaScript</a> to enhance your experience.</p>
    </div>
  </div>
  <div class="noscript-header-wrapper">
    <div class="noscript-message-container">
      <p>
        <a href="http://www.rackspace.com/support/">Support Hub</a> | 
        <a href="http://www.rackspace.com/knowledge_center/">Knowlege Center</a> | 
        <a href="http://docs.rackspace.com/">API Documentation</a> | 
        <a href="https://community.rackspace.com">Rackspace Community</a>
      </p>
    </div>
  </div>
</noscript>
<!-------------- Banner Bar  ------>


<!-- Main Content ---->
<div id="main-content">
  <div class="container" id="container" style="width: 960px;">
    
    <div class="span8" style="margin-left: 0px; width: 700px;">
 
    <c:set var="productcounter" value="0" scope="page"/>
    <%-- 
      We get All the products by iterating through all the IndexBean.categorizedProducts <String, ProductCategory> 
    --%>
    <c:forEach var="aCatProd" items="${bean.categorizedProducts}"> 
      <c:set var="productcounter" value="${productcounter+1}" scope="page"/>
      <%-- We need to add padding to the top for the very first category --%>
      <c:choose>
        <c:when test="${productcounter==1}">
          <h2 style="padding-top:20px;">${aCatProd.value.categoryName}</h2>
        </c:when>
        <c:otherwise>
          <h2>${aCatProd.value.categoryName}</h2>
        </c:otherwise>
      </c:choose>
      
      <div class="row-fluid" style="margin-bottom: 35px;">
        <!-- <div class="page-header"> -->
        <!--   <h3>Other Products</h3> -->
        <!-- </div> -->

	    <c:set var="outtercounter" value="1" scope="page"/>
	    <c:set var="columncounter" value="1" scope="page"/>
	    <%--
	      Within each ProductCategory, there is a ProductCategory.productsMap: Map<String, Product>
	      At each iteration, output the Product logo, name, and list of books
	    --%>  
	    <c:forEach var="aprod" items="${aCatProd.value.productsMap}">
          <%-- After each 3rd product we want a new line --%>
	      <c:if test="${(outtercounter==1)}">
	          <div class="row-fluid">  
	      </c:if>
	      <%-- We only display the product if there are Books to display for a given product --%>
	      <c:if test="${aprod.value.typesListSize>0}">
	      
            <%-- Display the Product Logo --%>
	        <div class="span4 ${aprod.value.productLogo}">
	          <%-- Display the respective product classification (value is either Cloud or Rackspace) --%>
	          <h3><span>${aprod.value.productClassification}</span>${aprod.value.productName}</h3>
	        	      
	        <c:set var="innercounter" value="0" scope="page"/>
	      
	        <%-- This list contains all the individual books for a given Product --%>
	        <ul class="unstyled" style="margin-top: 5px; padding-bottom:15px;">

                <%-- Iterate through the list of Books and display their contents --%>
	            <c:forEach var="atype" items="${aprod.value.typesList}">
	            
	              <c:set var="innercounter" value="${innercounter + 1}" scope="page"/>
	              <c:set var="thefoldername" value="${atype.folderName}" scope="page"/> 	            


	              <c:choose><%-- first inner forEach choose start --%>
	                <c:when test="${innercounter<=3}"><%-- first inner forEach when start --%>
	                  <li>
	                  
	                  <c:choose>
	                  <c:when test="${atype.url=='subcat'}">
	                    <div class="subcat"><c:out value='${atype.displayName}'/></div>
	                  </c:when>
	                  <c:otherwise>
	                  <c:choose>
	                    <c:when test="${atype.hasTarget==true}">	            
                          <a name="<c:out value='${thefoldername}.war'/>" href="<c:out value='${atype.url}'/>" title="<c:out value='${atype.prodId}'/>" target="_blank">
	                    </c:when>
	                    <c:otherwise>
                          <a name="<c:out value='${thefoldername}.war'/>" href="<c:out value='${atype.url}'/>" title="<c:out value='${atype.prodId}'/>" >                          
                       </c:otherwise>
                       </c:choose> 
                     
                     
                          <c:out value="${atype.displayName}"/>
                          <c:if test="${fn:endsWith(thefoldername,'-internal')}">
                            <c:if test="${!fn:endsWith(thefoldername,'resources-internal')}">
                              <span class="internalreviewer">(i)</span>
                            </c:if>
                          </c:if>
                          <c:if test="${fn:endsWith(thefoldername,'-reviewer')}">
                            <c:if test="${!fn:endsWith(thefoldername,'resources-reviewer')}">
                              <span class="internalreviewer">(r)</span>
                            </c:if>
                          </c:if>
                          <c:if test="${debug=='true'}"> (<c:out value="${atype.sequence}"/>)</c:if>
                          </a>	            	            
	                  
	                  </c:otherwise>
	                  </c:choose>	                  
	                  </li>
	                  
	                  
	                </c:when><%-- first inner forEach when end --%>	  
	                
	                <c:otherwise><%-- first inner forEach otherwise start --%>	  
           
	                  <c:if test="${(innercounter==4)}">
	                    <li class="more_btn">
	                      <a href>More</a>
	                    </li>
	                    <div class="more_section" style="display:none;">	                  	                
	                  </c:if>
	                            
	                  <li>
	                  
	                  <c:choose>
	                  <c:when test="${atype.url=='subcat'}">
	                    <div class="subcat"><c:out value='${atype.displayName}'/></div>
	                  </c:when>
	                  <c:otherwise>	                  
	                  
	                  <c:choose>
	                    <c:when test="${atype.hasTarget==true}">	            
                          <a name="<c:out value='${thefoldername}.war'/>" 
                             href="<c:out value='${atype.url}'/>" 
                             title="<c:out value='${atype.prodId}'/>" 
                             target="_blank">
	                    </c:when>
	                    <c:otherwise>
                          <a name="<c:out value='${thefoldername}.war'/>" 
                             href="<c:out value='${atype.url}'/>" 
                             title="<c:out value='${atype.prodId}'/>" >
                        </c:otherwise>
                      </c:choose>                            
                          <c:out value="${atype.displayName}"/>
                          <c:if test="${fn:endsWith(thefoldername,'-internal')}">
                            <c:if test="${!fn:endsWith(thefoldername,'resources-internal')}">
                              <span class="internalreviewer">(i)</span>
                            </c:if>
                          </c:if>
                          <c:if test="${fn:endsWith(thefoldername,'-reviewer')}">
                            <c:if test="${!fn:endsWith(thefoldername,'resources-reviewer')}">
                              <span class="internalreviewer">(r)</span>
                            </c:if>
                          </c:if>
                          <c:if test="${debug=='true'}"> (<c:out value="${atype.sequence}"/>)</c:if>
                          </a>	
                                                  
                      </c:otherwise> 
                      </c:choose>               	            	                  
	                  </li>
	                  
	                  <c:if test="${innercounter==aprod.value.typesListSize}">
	                      <li class="less_btn">
	                        <a href>Less</a>
	                      </li>
	                    </div>
	                  </c:if>
	                </c:otherwise><%-- first inner forEach otherwise end --%>	
	              </c:choose><%-- first inner forEach choose end --%>


	            </c:forEach><%-- End of loop that iterates through each Book for a given Product --%>  
            
	        
	        </ul>
	        </div>
	        <c:if test="${(columncounter%3)==0}">
	          </div>
	          <div class="row-fluid">  
	          <c:set var="columncounter" value="0" scope="page"/>
	        </c:if>
	      <c:set var="columncounter" value="${columncounter+1 }" scope="page"/>                    
          <c:set var="outtercounter" value="${outtercounter+1}" scope="page"/>
        
        </c:if><%-- end of test="${aprod.value.typesListSize>0} --%>
        </c:forEach><%-- end of second loop var="aprod" items="${aCatProd.value.productsMap}" --%>
            
      </div><%-- end of div tag class="row-fluid" style="margin-bottom: 35px;" --%>
      
      </div><%-- Extra padding to end the div tag --%>
      
    </c:forEach><%-- End of outter most Object wich is a ProductCategory: items="${bean.categorizedProducts}" --%>
    </div><%-- end of div tag with class="span8" --%>
      
      
      <%-- SDKs and Resources section --%>
      <div id="sdksandresources">
      <div class="span3 pull-right" style="margin-right: 25px; width: 205px;">
 
         <c:forEach var="aprod" items="${bean.productsCol3Map}">
          <c:if test="${aprod.value.typesListSize>0}">
          <div class="resource-section">
            <h4><c:out value="${aprod.value.productName }"/></h4>
            <ul class="unstyled">
              <c:forEach var="atype" items="${aprod.value.typesList}">
                <c:set var="thefoldername" value="${atype.folderName}" scope="page"/>
                  <c:choose>
                    <c:when test="${atype.url=='subcat'}">
                      <li>
                        <div class="subcat"><c:out value='${atype.displayName}'/></div>
                      </li>
                    </c:when> 
                    <c:otherwise>
	                  <li>
	                    <c:choose>
	                      <c:when test="${atype.hasTarget==true}">	            
                            <a name="<c:out value='${thefoldername}.war'/>" 
                               href="<c:out value='${atype.url}'/>" 
                               title="<c:out value='${atype.prodId}'/>" 
                               target="_blank">
	                      </c:when>
	                      <c:otherwise>
                            <a name="<c:out value='${thefoldername}.war'/>" 
                               href="<c:out value='${atype.url}'/>" 
                               title="<c:out value='${atype.prodId}'/>" >
                          </c:otherwise>
                        </c:choose>                                            
                        <c:out value="${atype.displayName}"/>
                          <c:if test="${fn:endsWith(thefoldername,'-internal')}">
                            <c:if test="${!fn:endsWith(thefoldername,'resources-internal')}">
                              <span class="internalreviewer">(i)</span>
                            </c:if>
                          </c:if>
                            <c:if test="${fn:endsWith(thefoldername,'-reviewer')}">
                              <c:if test="${!fn:endsWith(thefoldername,'resources-reviewer')}">
                                <span class="internalreviewer">(r)</span>
                              </c:if>
                            </c:if>
                            <c:if test="${debug=='true'}"> (<c:out value="${atype.sequence}"/>)</c:if>
                            </a>	            	            	                    
	                  </li>                 
                    </c:otherwise>                 
                  </c:choose>
              
              </c:forEach>
            </ul>  
          </div>   
          </c:if>     
        </c:forEach>
 
        <%-- <c:if test="${bean.shouldDisplayRssFeeds}"> BEGIN --%>
        <c:if test="${bean.shouldDisplayRssFeeds}">
            <div class="resource-section recent-updates">
                <h4>Recent Updates<a href="feeds/atom-all.xml"><i class="fa fa-rss"></i></a></h4>
                <ul class="unstyled">
                <c:forEach var="aFeed" items="${bean.rssFeeds}"> 
                    <c:set var="url" value="${aFeed.url}" scope="page"/> 
                    <c:set var="title" value="${aFeed.title}" scope="page"/> 
                    <c:set var="releaseNotesUrl" value="${aFeed.releaseNotesUrl}" scope="page"/>
<%--                     
                          <li>
                              <a href="${url}"><c:out value="${title}" escapeXml="false"/></a>
                          </li>
--%> 
                    <c:choose>
                       <c:when test="${fn:contains(url,'http://')}">
                          <li>
                              <!--this is a weird url, we are just going to point to the book's landing page-->
                              <a href="${releaseNotesUrl}"><c:out value="${title}" escapeXml="false"/></a>
                          </li>
                       </c:when>
                       <c:otherwise>
                          <li>
                              <a href="${url}"><c:out value="${title}" escapeXml="false"/></a>
                          </li>
                       </c:otherwise>
                    </c:choose>
                      
                </c:forEach>           
                </ul>
            </div>
       
        </c:if>
        <%-- <c:if test="${bean.shouldDisplayRssFeeds}"> END --%>
        
      </div><%-- class="span3 pull-right" end div tag --%>
      </div> 
      </div>    
      
  </div><%-- end div tag with class="container" --%>
  
  
  
</div><%-- end div tag with calss="main-content" --%>


</div></div><%-- Extra padding to end the main content and to begin the footer --%>

<div id="raxheaderfooterservice-footercontent"></div>

<noscript>
  <div class="noscript-header-wrapper">
    <div class="noscript-footer-container">

      <p>1-800-961-2888 | Sales 1-800-961-4454</p>

      <b>Support</b>
      <p><a href="http://www.rackspace.com#chat">Live Sales Chat</a> | <a href="http://www.rackspace.com/forms/contactsales/">Email Us</a></p>

      <b>Products</b>
      <p>
	<a href="http://www.rackspace.com/cloud/">Public Cloud</a> | 
	<a class="footeranchor" href="http://www.rackspace.com/cloud/private/">Private Cloud</a> | 
	<a href="http://www.rackspace.com/cloud/hybrid/">Hybrid Cloud</a> | 
	<a href="http://www.rackspace.com/managed-hosting/">Managed Hosting</a> | 
	<a href="http://www.rackspace.com/email-hosting/">Email Hosting</a>	
      </p>

      <b>Support</b>
      <p>
	<a href="http://support.rackspace.com/" target="_blank">Support Home</a> | 
	<a href="http://www.rackspace.com/knowledge_center/">Knowledge Center</a> | 
	<a href="https://community.rackspace.com" target="_blank">Rackspace Community</a> | 
	<a href="http://docs.rackspace.com/" target="_blank">Technical Documentation</a> | 
	<a href="http://developer.rackspace.com" target="_blank">Developer Center</a>
      </p>

      <b>Control Panels</b>
      <p>
	<a href="https://my.rackspace.com/portal/auth/login" target="_blank">MyRackspace Portal</a> | 
	<a href="https://mycloud.rackspace.com" target="_blank">Cloud Control Panel</a> | 
	<a href="https://manage.rackspacecloud.com/pages/Login.jsp" target="_blank">Cloud Sites Control Panel</a> | 
	<a href="https://apps.rackspace.com" target="_blank">Rackspace Webmail Login</a> | 
	<a href="https://cp.rackspace.com" target="_blank">Email Admin Login</a>	
      </p>

      <b>About Rackspace</b>
      <p>
	<a class="footeranchor" href="http://www.rackspace.com/about/">Our Story</a> | 
	<a href="http://stories.rackspace.com" target="_blank">Case Studies</a> | 
	<a href="http://www.rackspace.com/events/">Events</a> | 
	<a href="http://www.rackspace.com/programs/">Programs</a> | 
	<a href="http://www.rackspace.com/blog/newsroom/">Newsroom</a> | 		
	<a href="http://www.rackspace.com/blog/">The Rackspace Blog</a> | 
	<a href="http://developer.rackspace.com/blog/" target="_blank">DevOps Blog</a> | 
	<a href="http://www.rackspace.com/information/contactus/">Contact Information</a> | 
	<a href="http://www.rackspace.com/information/legal/">Legal</a> | 
	<a href="http://talent.rackspace.com/" target="_blank">Careers</a>
      </p>

      <p>©2014 Rackspace US, Inc.</p> 
      <p>
	<a href="http://www.rackspace.com/about/" class="basement">About Rackspace</a> | <a href="http://ir.rackspace.com" class="basement">Investors</a>
	| <a href="http://www.rackertalent.com" class="basement">Careers</a> | <a href="http://www.rackspace.com/information/legal/privacystatement">Privacy Statement</a> | 
	<a href="http://www.rackspace.com/information/legal/websiteterms">Website Terms</a> | <a href="http://www.rackspace.com/information/legal/copyrights_trademarks">Trademarks</a> | 
	<a href="http://www.rackspace.com/sitemap/" class="basement">Sitemap</a>
      </p>
    </div>
   </div><%-- end of div tag after noscript --%>
</noscript> 

</body>
</html>
