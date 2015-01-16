package com.rackspace.cloud.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class FilterUrl implements Filter {

	private static Logger log=Logger.getLogger(Filter.class);
	private static StringBuilder publishPage;
	private ServletContext ctx=null;
	private String[] rules;
	//This caches the bookinfo.xml <latestpdf> tag value for a specific webapp folder
	private Map<String,ServletCtxAndForwardUrl>bookInfoMap;

	private Properties emailProps;

	public static String EMAIL_ONE="david.cramer.rackspace.com";
	public static String EMAIL_TWO="thu.doan@rackspace.com";
	public static String RACKSPACE_EMAIL="clouddoctoolsteam@list.rackspace.com";

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		String METHOD_NAME="init()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
		}
		this.ctx=arg0.getServletContext();
		String rootPath=this.ctx.getRealPath(File.separator);
		String webAppsPath=rootPath+="..";
		webAppsPath=this.getRealPath(webAppsPath);
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": rootPath="+rootPath);
			log.debug(METHOD_NAME+": webAppsPath="+webAppsPath);
		}
		File webAppsFolder=new File(webAppsPath);
		//webAppsFilesAndFoldersMap is a Map that has files and folders from the webapps folder keyed by 
		//the .war file name of folder name whose value is the respective File Object
		Map<String,File>webAppsFilesAndFoldersMap=getWebAppsFilesAndFolders(webAppsFolder);

		this.expandUnexpandedWars(webAppsFilesAndFoldersMap);

		this.bookInfoMap=new HashMap<String, FilterUrl.ServletCtxAndForwardUrl>();

		try {
			this.rules=this.getAllRules();			
		} 
		catch (IOException e) {
			log.error(METHOD_NAME+": IOException: e.getMessage()="+e.getMessage());
			e.printStackTrace();
		}

		String pathToEmailProps=webAppsPath;
		if(!pathToEmailProps.endsWith("/")){
			pathToEmailProps+="/";
		}
		pathToEmailProps+="ROOT/WEB-INF/email.properties";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": pathToEmailProps="+pathToEmailProps);
		}
		File emailPropsFile=new File(pathToEmailProps);
		try {
			FileInputStream fileInny=new FileInputStream(emailPropsFile);
			this.emailProps=new Properties();
			this.emailProps.load(fileInny);
		} 
		catch (FileNotFoundException e) {
			log.error(METHOD_NAME+": FileNotFoundException: e.getMessage()="+e.getMessage());
			e.printStackTrace();
		} 
		catch (IOException e) {			
			log.error(METHOD_NAME+": IOException: e.getMessage()="+e.getMessage());
			e.printStackTrace();
		}

		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END:");
		}
	}
	

	@Override
	public void destroy() {

	}

	@Override
	/*******
	 * 
	 * 
	 * Warning!!!!!! this will not work on Tomcat unless you have the attribute crossContext="true" in conf/context.xml
	 * for example:
	 * 
	 * <Context crossContext="true">
	 * 
	 * ...
	 * 
	 * </Context>
	 * 
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String METHOD_NAME="doFilter()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": ENTER");
		}
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String url =req.getRequestURL().toString();
		req.setAttribute("origurl", url);
		req.setAttribute("origuri", req.getRequestURI());
		String serverName=req.getServerName();
		String uri=req.getRequestURI();
		if(log.isDebugEnabled()){			
			log.debug(METHOD_NAME+":serverName="+serverName+" uri="+uri);
			log.debug(METHOD_NAME+":url="+url);
			log.debug(METHOD_NAME+":url.toLowerCase().endsWith(\"index.jsp\")="+url.toLowerCase().endsWith("index.jsp"));
			
			log.debug(METHOD_NAME+":!@#!@#!@#!@#!@#!@#!@#@!#");
			log.debug(METHOD_NAME+":(if(uri.equals(\"/publish\")="+(uri.equals("/publish")));
			log.debug(METHOD_NAME+":(if(serverName.contains(\"localhost\"))="+(uri.contains("localhost")));
			log.debug(METHOD_NAME=":longLogic="+(uri.equals("/publish") && ((serverName.equals("docs-staging.rackspace.com")||serverName.equals("docs-internal.rackspace.com")|| serverName.equals("localhost")))));
			log.debug(METHOD_NAME+":!@#!@#!@#!@#!@#!@#!@#@!#");
			
			
		}	
		if(url.toLowerCase().endsWith("index.jsp")){
			int indexJSPIndex=url.indexOf(uri);

			if(log.isDebugEnabled()){			
				log.debug(METHOD_NAME+":indexJSPIndex="+indexJSPIndex);
			}
			if(indexJSPIndex!=-1){
				String newUrl=url.substring(0,indexJSPIndex+1);
				if(log.isDebugEnabled()){			
					log.debug(METHOD_NAME+":indexJSPIndex="+indexJSPIndex);
					log.debug(METHOD_NAME+":newUrl="+newUrl);
				}
				res.setStatus(301);
				res.setHeader( "Location", newUrl);
				res.setHeader( "Connection", "close" );
			}
		}
		//We are looking for the instance of docs-staging.rackspace.com/publish
		//,docs-internal-staging.rackspace.com/publish or localhost/publish
		else if(uri.equals("/publish") && ((serverName.equals("docs-staging.rackspace.com")||
				serverName.equals("docs-internal.rackspace.com")|| serverName.equals("localhost")))){
			if(log.isDebugEnabled()){			
				log.debug(METHOD_NAME+":@#$@#$@#$@#$Made it here!!!!");
			}
			returnPublishLinksPage(request,response,chain);
		}
		else{

			ServletCtxAndForwardUrl webAppCtxAndurl=null;

			if(this.bookInfoMap.containsKey(url)){
				webAppCtxAndurl=bookInfoMap.get(url);
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+" key: "+url+" found in map!!!");
				}
			}
			else{
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+" key: "+url+" NOT found in map, calling processRules().");
				}
				webAppCtxAndurl=this.processUrl(url, uri, res);
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+" (null!=webAppCtxAndurl)="+((null!=webAppCtxAndurl)));
				}
				//This is a partial pattern url forwarding of the rule form:
				//servers/api/v2/cs-gettingstarted-preview~cs-gettingstarted-preview
				if(null!=webAppCtxAndurl && 
				  (null==webAppCtxAndurl.getFullRedirectUrlStr()||webAppCtxAndurl.getFullRedirectUrlStr().isEmpty())){

					String forwardUrl=webAppCtxAndurl.getRightSideUrl();
					if(null!=forwardUrl){
						boolean endsWithPdfDate=this.doesPdfNameEndInADate(forwardUrl);
						if(forwardUrl.endsWith("/")){
							forwardUrl=forwardUrl.substring(0,(forwardUrl.length()-1));
						}
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+" forwardUrl="+forwardUrl+" endsWithPdfDate="+endsWithPdfDate);
						}
						//The url was process once already, it ended in .pdf but did not have a date in the 
						//original url. After the initial process, it now contains a date suffix, we need to
						//retrieve the correct resource
						if(forwardUrl.endsWith(".pdf") && endsWithPdfDate){
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": calling processRules() for a second time.");
								log.debug(METHOD_NAME+": url="+url);
							}
							//Remove the last slash if it exists
							if(url.endsWith("/")){
								url=url.substring(0,(url.length()-1));
							}
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": url="+url);
							}					
							int lastSlashIndex=url.lastIndexOf("/");
							if(-1!=lastSlashIndex){
								url=url.substring(0,(lastSlashIndex+1));
								url+=forwardUrl;
							}
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": url="+url);
							}						
							webAppCtxAndurl=this.processUrl(url, uri, res);
						}
					}
				}
			}
			if(null!=this.rules && null!=webAppCtxAndurl){

				String fullUrlForward=webAppCtxAndurl.getFullRedirectUrlStr();
				
				//This is a full url matches a rule of the form:
				//^^rpc/v4/gettingstarted/content/rpc-common-front.html~rpc-v4-rackspace-private-cloud-gettingstarted-v4/content/rpc-common-front.html
				if(null!=fullUrlForward && !fullUrlForward.isEmpty()){
					
					String forwardUrl=webAppCtxAndurl.getFullRedirectUrlStr();
					ServletContext servCtx=webAppCtxAndurl.getCtx();
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": forwardUrl="+forwardUrl);
						log.debug(METHOD_NAME+": (null!=servCtx)="+(null!=servCtx));						
					}
					
					if(null!=servCtx){
						String currentPath=servCtx.getRealPath(".");
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": currentPath="+currentPath);
						}
						RequestDispatcher dispatcher=servCtx.getRequestDispatcher(forwardUrl);
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": dispatcher="+dispatcher);        				
						}
						if(null!=dispatcher){
							dispatcher.forward(req, res);
						}
					}
				}
				//This is a partial redirect and the url matches a rule of the form:
				//servers/api/v2/cs-gettingstarted-preview~cs-gettingstarted-preview
				else{
					ServletContext webAppCtx=webAppCtxAndurl.getCtx();
					String forwardUrl=webAppCtxAndurl.getRightSideUrl();
					if(!forwardUrl.startsWith("/")){
						forwardUrl="/"+forwardUrl;
					}
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": forwardUrl="+forwardUrl);
					}
					if(null!=webAppCtx && null!=forwardUrl){
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": webAppCtx.getContextPath()="+webAppCtx.getContextPath()+"\n"+
									"doesPdfNameEndInADate("+forwardUrl+")="+doesPdfNameEndInADate(forwardUrl));
						}
						if(!(forwardUrl.toLowerCase().endsWith("latest.pdf"))){
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+":going to redirect to forwardUrl:"+forwardUrl);
								log.debug(METHOD_NAME+": webAppCtx.getContextPath()="+webAppCtx.getContextPath());
							}					
							/* Warning!!!!!! this will not work on Tomcat unless you have the attribute crossContext="true" in conf/context.xml
							 * for example:
							 * 
							 * <Context crossContext="true">
							 * 
							 * ...
							 * 
							 * </Context>
							 */
							RequestDispatcher dispatcher=webAppCtx.getRequestDispatcher((forwardUrl));
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": dispatcher="+dispatcher);        				
							}
							if(null!=dispatcher){
								dispatcher.forward(req, res);
							}
						}
					}
				}
			}
			else{
				chain.doFilter(req, res);
			}
		}
		log.debug(METHOD_NAME+": EXIT");
	}
	
	
	private void returnPublishLinksPage(ServletRequest request, ServletResponse response,
			FilterChain chain)throws ServletException, IOException{
		String METHOD_NAME="returnPublishLinksPage";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": ENTER:");
		}
		
		if(null==FilterUrl.publishPage || FilterUrl.publishPage.length()==0){
			FilterUrl.publishPage=new StringBuilder();
			InputStream innyStream=FilterUrl.class.getResourceAsStream("publish.html");
			if(null!=innyStream){
				int readInt;
				char readChar;
				//ServletOutputStream outty=response.getOutputStream();
				while(-1!=(readInt=innyStream.read())){
					readChar=(char)readInt;
					//outty.write(readChar);
					FilterUrl.publishPage.append(readChar);
				}
				//outty.flush();
				//outty.close();
				innyStream.close();
			}
			else{
				PrintWriter outty=response.getWriter();
				outty.println("<HTML><BODY><H1>Cannot find publish.html<H1></BODY></HTML>");	
			}
		}
		PrintWriter outty=response.getWriter();
		outty.println(FilterUrl.publishPage.toString());
		outty.close();
		
		chain.doFilter(request, response);
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": EXIT:");
		}
	}

	private void expandUnexpandedWars(Map<String,File>webAppsFilesAndFoldersMap){
		String METHOD_NAME="expandUnexpandedWars()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
		}

		if(null!=webAppsFilesAndFoldersMap){

			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": webAppsFilesAndFoldersMap.size()="+webAppsFilesAndFoldersMap.size());
			}
			Set<String>keys=webAppsFilesAndFoldersMap.keySet();
			StringBuffer zipExceptionMessage=new StringBuffer("Error trying to unzip .war files:\n");
			List<Email>emailsList=new ArrayList<Email>();
			for(String aKey:keys){
				//String aWebAppFileAndFolderName=aKey.getName();
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": aKey="+aKey);
				}
				//This is a *.war file
				if(aKey.endsWith(".war")){
					String warFileNameWithoutDotWarExtentsion=aKey.substring(0,aKey.lastIndexOf(".war"));
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": warFileNameWithoutDotWarExtentsion="+warFileNameWithoutDotWarExtentsion);
						log.debug(METHOD_NAME+": webAppsFilesAndFoldersMap.containsKey("+warFileNameWithoutDotWarExtentsion+")="+
								webAppsFilesAndFoldersMap.containsKey(warFileNameWithoutDotWarExtentsion));
					}					
					//The associated .war file folder name is in the map
					if(webAppsFilesAndFoldersMap.containsKey(warFileNameWithoutDotWarExtentsion)){	

						String theWarFolderStr=this.getRealPath(webAppsFilesAndFoldersMap.get(warFileNameWithoutDotWarExtentsion));
						File theWarFolder=new File(theWarFolderStr);

						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+":!!!!!!!!!!theWarFolderStr="+theWarFolderStr);
						}
						//The warFolder itself however does not exist
						if(!theWarFolder.exists()){
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": the WarFolder does not exist must expand the .war file: "+
										"theWarFolder.getName()="+theWarFolder.getName());
							}
							this.expandWar(emailsList,zipExceptionMessage, webAppsFilesAndFoldersMap.get(aKey));							
						}
						//The warFolder does exist, however, we need to check to make sure that 
						//more than just the WEB-INF folder exists
						else{
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": the WarFolder exists must check for content: "+
										"theWarFolder.getName()="+theWarFolder.getName());
							}
							File[]warfolderFilesAndFolders=theWarFolder.listFiles();
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": the warfolderFilesAndFolders.length="+warfolderFilesAndFolders.length);
							}
							//There is only 1 file or 1 folder
							if(warfolderFilesAndFolders.length<=1){		
								if(log.isDebugEnabled()){
									log.debug(METHOD_NAME+": the WarFolder exists but there is only one file/folder, expand the .war ");
									log.debug(METHOD_NAME+": ~first delete: "+theWarFolder.getAbsolutePath());
								}

								this.deleteFolderAndSubFolders(theWarFolder);								
								this.expandWar(emailsList,zipExceptionMessage,webAppsFilesAndFoldersMap.get(aKey));
							}
							//We must first check the time stamps of the .war file against the respective web folder. If the .war
							//file is more recent then we must manually expand the .war
							else{
								File theWarFile=webAppsFilesAndFoldersMap.get(aKey);
								if(log.isDebugEnabled()){
									log.debug(METHOD_NAME+": theWarFile.exists()="+theWarFile.exists());									
								}
								if(theWarFile.exists()){
									long lastModifiedWarFolder=theWarFolder.lastModified();
									long lastModifiedWarFile=theWarFile.lastModified();
									if(log.isDebugEnabled()){
										log.debug(METHOD_NAME+": lastModifiedWarFolder="+lastModifiedWarFolder);
										log.debug(METHOD_NAME+": lastModifiedWarFile="+lastModifiedWarFile);
										log.debug(METHOD_NAME+": (lastModifiedWarFile>lastModifiedWarFolder)="+
												(lastModifiedWarFile>lastModifiedWarFolder));
									}								
									if(lastModifiedWarFile>lastModifiedWarFolder){
										deleteFolderAndSubFolders(theWarFolder);
										expandWar(emailsList,zipExceptionMessage,theWarFile);
									}
									//To be sure we must check the time stamp in the WEB-INF/warinfo.properties file
									else{
										//At this point we know that both the .war file and folder exist try to open the 
										//warinfo.properties in the .war file and the folder
										ZipFile theWarZipFile;
										try {
											String theFolderWarInfoPropsPath=webAppsFilesAndFoldersMap.get(warFileNameWithoutDotWarExtentsion).getAbsolutePath();
											if(null!=theFolderWarInfoPropsPath && !theFolderWarInfoPropsPath.endsWith("/")){
												theFolderWarInfoPropsPath+="/";
											}
											theFolderWarInfoPropsPath+=("WEB-INF/warinfo.properties");
											if(log.isDebugEnabled()){
												log.debug(METHOD_NAME+": theFolderWarInfoPropsPath="+theFolderWarInfoPropsPath);
											}
											File folderWarInfoProps=new File(theFolderWarInfoPropsPath);
											if(log.isDebugEnabled()){
												log.debug(METHOD_NAME+": folderWarInfoProps.exists()="+(folderWarInfoProps.exists()));
											}										
											if(folderWarInfoProps.exists()){
												Properties warInfoProps=new Properties();
												warInfoProps.load(new FileInputStream(folderWarInfoProps));
												String warInfoPropsTimeStamp=warInfoProps.getProperty("timestamp");
												theWarZipFile = new ZipFile(theWarFile);
												String theWarWarInfoPropsTimeStamp=FilterUrl.getTimeStamp(theWarZipFile);
												if(log.isDebugEnabled()){
													log.debug(METHOD_NAME+": warInfoPropsTimeStamp="+warInfoPropsTimeStamp);
													log.debug(METHOD_NAME+": theWarWarInfoPropsTimeStamp="+theWarWarInfoPropsTimeStamp);
												}
												SimpleDateFormat simpleDate=new SimpleDateFormat("yyy-MM-dd HH:mm:ss.SSS");

												try {
													long theWarWarInfoDate=simpleDate.parse(theWarWarInfoPropsTimeStamp).getTime();
													long theFolderWarInfoDate=simpleDate.parse(warInfoPropsTimeStamp).getTime();
													if(log.isDebugEnabled()){
														log.debug(METHOD_NAME+": theWarWarInfoDate="+theWarWarInfoDate);
														log.debug(METHOD_NAME+": theWarWarInfoDate="+theWarWarInfoDate);
													}
													//There is a problem, the .war is newer than the respective folder, we must 
													//manually expand the .war
													if(theWarWarInfoDate>theFolderWarInfoDate){
														if(log.isDebugEnabled()){
															log.debug(METHOD_NAME+": deleting folder: "+theWarFolder.getAbsolutePath());
															log.debug(METHOD_NAME+": then expanding .war:" +theWarFolder.getAbsolutePath());
														}
														deleteFolderAndSubFolders(theWarFolder);
														expandWar(emailsList,zipExceptionMessage, theWarFile);
													}													
												} 
												catch (ParseException e) {
													log.error(e);
													e.printStackTrace();
												}
											}
										}
										catch (ZipException e) {
											log.error(e);
											e.printStackTrace();
										}
										catch (IOException e) {
											log.error(e);
											e.printStackTrace();
										}
										catch(Throwable e){
											log.error(e);
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
					//The corresponding webapps folder for the current .war file does not exist, we must manually expand the .war file
					else{
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": warFileNameWithoutDotWarExtentsion="+warFileNameWithoutDotWarExtentsion+
									" does not exist in Map must expand the .war="+aKey);
						}
						this.expandWar(emailsList,zipExceptionMessage, webAppsFilesAndFoldersMap.get(aKey));
					}
				}
			}
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": emailsList.size()="+emailsList.size());
			}
			if(emailsList.size()>0){
				log.debug(METHOD_NAME+": Sending email: emailList.size()="+emailsList.size());

				SendMail sendMail=new SendMail(emailsList, zipExceptionMessage.toString());
				Thread sendMailThread=new Thread(sendMail);
				sendMailThread.start();
			}
		}

		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END");
		}
	}

	private String getRealPath(File warFolder){
		String retVal=null;
		String METHOD_NAME="getRealPath(File)";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: warFolder="+warFolder);
		}
		if(null!=warFolder){
			retVal=warFolder.getAbsolutePath();
			int index=retVal.lastIndexOf("/ROOT/..");
			if(-1!=index){
				String firstPart=retVal.substring(0,index);
				String secondPart=retVal.substring(index+("/ROOT/..".length()));
				retVal=firstPart+secondPart;			
			}
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: retVal="+retVal);
		}
		return retVal;
	}

	private String getRealPath(String folder){
		String retVal=folder;
		String METHOD_NAME="getRealPath(String)";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: retVal="+retVal);
		}
		if(null!=folder){
			int index=retVal.lastIndexOf("/ROOT/..");
			if(-1!=index){
				String firstPart=retVal.substring(0,index);
				String secondPart=retVal.substring(index+("/ROOT/..".length()));
				retVal=firstPart+secondPart;			
			}	
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: retVal="+retVal);
		}
		return retVal;
	}

	private void deleteFolderAndSubFolders(File dir){
		String METHOD_NAME="deleteFolderAndSubFolders()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: dir.getAbsolutePath()="+dir.getAbsolutePath());
		}
		try{
			if(dir.isDirectory()){
				File[] files=dir.listFiles();
				for(File aFile:files){
					deleteFolderAndSubFolders(aFile);
				}
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": deleting directory: dir.getAbsolutePath()="+dir.getAbsolutePath());
				}
				dir.delete();
			}
			else{
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": deleting file: dir.getAbsolutePath()="+dir.getAbsolutePath());
				}
				dir.delete();
			}
		}
		catch(Throwable e){
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": caught Throwable exception , could not delete everything in: "+
						dir.getAbsolutePath()+" message: "+e.getMessage());
			}
			e.printStackTrace();
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: ");
		}
	}
 
	public static void main(String[] args){
		String str="http://docs-staging.rackspace.com/loadbalancers/api/v1.0/clb-devguide/content/GET_showLoadBalancer_v1.0__account__loadbalancers__loadBalancerId__load-balancers.html";
		String otherStr="loadbalancers/api/v1.0/clb-devguide/content/GET_showLoadBalancer_v1.0__account__loadbalancers__loadBalancerId__load-balancers.html";
		
		int index=str.indexOf(otherStr);
		String newStr=str.substring(0,index);
		System.out.println("^^^^^^^^^^newStr="+newStr);
		
	}

	//Rules are found in WEB-INF/lib/redirect.properties. Each rule is of the form:
	//servers/api/v2/cs-releasenotes~servers-v2-cs-releasenotes
	//The ~ character delimits the searchUrl on the left and the foward webapp folder to the right
	//Therefore with the rule above any url that has: servers/api/v2/c-releasenotes as part of the request,
	//we know to look in the servers-v2-cs-releasenotes webapps folder
	//uri is the path after the .com in the server name for example in the url:
	//http://docs-staging.rackspace.com/loadbalancers/api/v1.0/clb-devguide/content/Monitors-d1e3370.html
	//the uri is:                      /loadbalancers/api/v1.0/clb-devguide/content/Monitors-d1e3370.html	
	private ServletCtxAndForwardUrl processUrl(String url, String uri, HttpServletResponse resp){
		ServletCtxAndForwardUrl retVal=null;
		String tempUrl=new String(url);
		String METHOD_NAME="processUrl()";
		if(null!=uri && uri.startsWith("/")){
			uri=uri.substring(1);
		}	
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+":START: url="+url+" uri="+uri);
		}

		//Make sure there are rules and a url
		if(null!=rules && rules.length>0 && null!=url && !url.isEmpty()){
			//Iterate through all the rules
			for(String aRule:this.rules){
				if(log.isDebugEnabled()){
					//aRule is of the forms: 
					//servers/api/v2/cs-gettingstarted-preview~cs-gettingstarted-preview
					//or
					//^^rpc/v4/gettingstarted/content/rpc-common-front.html~rpc-v4-rackspace-private-cloud-gettingstarted-v4/content/rpc-common-front.html
					log.debug(METHOD_NAME+": aRule="+aRule);
				}

				String[] searchUrlAndForward=aRule.split("~");
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+":(null!=searchUrlAndForward)="+(null!=searchUrlAndForward));
				}
				if(null!=searchUrlAndForward){
					//serchUrl is the lhs of the ~ of a rule so in the rule: 
					//servers/api/v2/cs-gettingstarted-preview~cs-gettingstarted-preview
					//the searchUrl is: servers/api/v2/cs-gettingstarted-preview
					String searchUrl=searchUrlAndForward[0];
					
					String projPath=searchUrlAndForward[1];;
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": searchUrl="+searchUrl+
								              " uri="+uri+
								              " (null!=uri && uri.startWith(\""+searchUrl+"\"))="+uri.startsWith(searchUrl)+
								              " (null!=searchUrl && searchUrl.startsWith(\"^^\"))="+
								              (null!=searchUrl && searchUrl.startsWith("^^")));	
					}
					//First we deal with the full redirection, full redirection rules are of the form:
					//^^rpc/v4/gettingstarted/content/rpc-common-front.html~rpc-v4-rackspace-private-cloud-gettingstarted-v4/content/rpc-common-front.html
					//full redirection never deals with .pdf, we just redirect the whole url
					if(null!=searchUrl && searchUrl.startsWith("^^")){					
						retVal=this.processFullForward(url, uri, searchUrl, projPath);
					}
					//Make sure there is a searchUrl and uri and the uri starts with the searchUrl
					else if(null!=searchUrl && 
							!searchUrl.trim().isEmpty() && 
							null!=uri && 
							!uri.isEmpty() && 
							uri.startsWith(searchUrl)){
						retVal=processPartialUrlPattern(resp, aRule, searchUrlAndForward,url,searchUrl,tempUrl, projPath);
					}
					if(retVal!=null){
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": retVal has a value");
						}
						break;
					}
					else{
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": retVal is NULL continue");
						}
					}
				}
				else{
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME=": rule: "+aRule+" is malformed, make sure the search Url is delimited from the forward path by a ~");
					}
				}
			}
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+" END");
		}
		return retVal;
	}
	
	private ServletCtxAndForwardUrl processPartialUrlPattern(HttpServletResponse resp, String aRule, String[] searchUrlAndForward, 
			String url, String searchUrl,String tempUrl, String projPath){
		String METHOD_NAME="processPartialUrlPattern()";
		
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
		}

		ServletCtxAndForwardUrl retVal=null;
		int index=url.indexOf(searchUrl);
		//The searchurl is a substring in the request url, we have a redirection match, we must do a forward
		if(-1!=index){
			int rightIndex=getSkipToNextSlashIndex(index,searchUrl,tempUrl);
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": rightIndex="+rightIndex);
			}
			//Make sure there is a forward rule
			if((searchUrlAndForward.length>1) && rightIndex!=-1){
				projPath=searchUrlAndForward[1];
				String webAppFolderStr=projPath;

				//the path to forward to can have the format: cf-devguide/content in this case, 
				//folder to the left of the first '/' is the webapp folder and the folder/folders 
				//to the right of the initial '/' is/are subfolder/subfolders
				int projPathSlashIndex=projPath.indexOf("/");
				if(-1!=projPathSlashIndex){
					webAppFolderStr=projPath.substring(0,projPathSlashIndex);
					projPath=projPath.substring((projPathSlashIndex));
				}
				String rightSideOfUrl=tempUrl.substring(rightIndex);
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": projPath="+projPath);
					log.debug(METHOD_NAME+": webAppFolder="+webAppFolderStr);
					log.debug(METHOD_NAME+": rightSideOfUrl="+rightSideOfUrl);
				}
				if(null!=rightSideOfUrl && !rightSideOfUrl.isEmpty()){
					String rightSideWithNoSlash=rightSideOfUrl;
					if(rightSideWithNoSlash.startsWith("/")){
						rightSideWithNoSlash=rightSideWithNoSlash.substring(1);
					}
					if(null!=this.ctx){
						String temp=this.getProjFolderWithSuffix(webAppFolderStr);
						ServletContext projCtx=this.ctx.getContext(temp);
						//if(null==projCtx){
						//											String temp2="/"+webAppFolder+"-internal";
						//											if(log.isDebugEnabled()){
						//												log.debug(METHOD_NAME+": projCtx is null trying to get context: "+temp2+" instead");
						//											}
						//											
						//											projCtx=this.ctx.getContext(temp2);
						//											if(null==projCtx){
						//												temp2="/"+webAppFolder+"-reviewer";
						//												if(log.isDebugEnabled()){
						//													log.debug(METHOD_NAME+": projCtx is still null this is the last try to get context: "+temp2+" instead");
						//												}
						//												
						//												projCtx=this.ctx.getContext(temp2);
						//											}
						//}
						if(null!=projCtx){
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+" projCtx is NOT NULL");
								log.debug(METHOD_NAME+" projCtx,getContextPath()="+projCtx.getContextPath());
							}
							retVal=new ServletCtxAndForwardUrl();
							retVal.setCtx(projCtx);
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": projPathSlashIndex="+projPathSlashIndex+
										" rightSideOfUrl="+rightSideOfUrl);
							}
							if(-1!=projPathSlashIndex){
								rightSideOfUrl=projPath+rightSideOfUrl;
							}
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": rightSideOfUrl="+rightSideOfUrl);
							}
							retVal.setRightSideUrl(rightSideOfUrl);

							//Now we need to test to see if this is a .pdf, if so there are two scenarios we
							//have to handle:
							//#1. The .pdf does not have a date in its file name, that means we have to see if there
							//    is a bookinfo.xml <latestpdf> with an entry value matching this .pdf name, if it matches
							//    replace the .pdf file name with the <pdfoutname>
							//#2. The .pdf file name ends in a date suffix, this means it was redirected by FilterUrl
							//    there really is no .pdf by this name, we have to look in the book
							if((rightSideWithNoSlash.toLowerCase()).endsWith("latest.pdf")){
								try{
									InputStream inny=projCtx.getResourceAsStream("bookinfo.xml");
									DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
									DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
									Document doc=dBuilder.parse(inny);
									doc.getDocumentElement().normalize();

									NodeList latestPdfsNodeList=doc.getElementsByTagName("latestpdf");
									if(null!=latestPdfsNodeList){
										if(log.isDebugEnabled()){
											log.debug(METHOD_NAME+": latestPdfsNodeList.getLength()="+latestPdfsNodeList.getLength());
										}
										String latestPdfFileName="";
										//There should be only one <latestpdf> tag, but in case there are multiple,
										//get the first found value
										for(int i=0;i<latestPdfsNodeList.getLength();++i){
											Node aNode=latestPdfsNodeList.item(i);
											String context=aNode.getTextContent();
											//We have found the first instance of a value for <latestpdf> tag
											if(null!=context && !context.trim().isEmpty()){
												latestPdfFileName=context.trim();
												if(log.isDebugEnabled()){
													log.debug(METHOD_NAME+": breaking in pdf section");
												}
												break;
											}
										}
										NodeList pdfOutnameNodeList=doc.getElementsByTagName("pdfoutname");
										if(null!=pdfOutnameNodeList){
											if(log.isDebugEnabled()){
												log.debug(METHOD_NAME+": latestPdfsNodeList.getLength()="+latestPdfsNodeList.getLength());
											}
											String pdfOutname="";
											for(int i=0;i<pdfOutnameNodeList.getLength();++i){
												Node aNode=pdfOutnameNodeList.item(i);
												String context=aNode.getTextContent();
												//We have found the first instance of a value for <latestpdf> tag
												if(null!=context && !context.trim().isEmpty()){
													pdfOutname=context.trim();
													if(log.isDebugEnabled()){
														log.debug(METHOD_NAME+": breaking in pdf section");
													}
													break;
												}																
											}
											boolean hasADate=this.doesPdfNameEndInADate(rightSideWithNoSlash);
											if(log.isDebugEnabled()){
												log.debug(METHOD_NAME+": hasADate="+hasADate+" latestPdfFileName="+latestPdfFileName+
														" pdfOutname="+pdfOutname);
											}
											//check to make sure that the <latestpdf> has the same value
											if(latestPdfFileName.equals(rightSideWithNoSlash)){
												index=url.indexOf(latestPdfFileName);
												if(log.isDebugEnabled()){
													log.debug(METHOD_NAME+": index="+index);
												}
												if(-1!=index){
													url=url.substring(0,index);
													url+=pdfOutname;
													String pdfRightSide=pdfOutname;
													if(!pdfRightSide.startsWith("/")){
														pdfRightSide+="/";
													}
													if(log.isDebugEnabled()){
														log.debug(METHOD_NAME+": url="+url+"\npdfRightSide="+pdfRightSide);
													}
													retVal.setRightSideUrl(pdfRightSide);

													resp.setStatus(301);
													resp.setHeader( "Location", url);
													resp.setHeader( "Connection", "close" );
												}																	
											}
										}
									}
								}
								catch(ParserConfigurationException e){
									e.printStackTrace();
									log.debug(METHOD_NAME+": ParserConfigurationException caught message:"+e.getMessage());
								}
								catch(IOException e){
									e.printStackTrace();
									log.debug(METHOD_NAME+": IOException caught message:"+e.getMessage());														
								}
								catch(Throwable e){
									e.printStackTrace();
									log.debug(METHOD_NAME+": Unknown Thorable exception caught message:"+e.getMessage());														
								}
							}
							else{
								if(bookInfoMap.size()>5000){
									bookInfoMap.clear();
								}
								this.bookInfoMap.put(url, retVal);	
								if(log.isDebugEnabled()){
									log.debug(METHOD_NAME+": added key:"+url);
								}
							}
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": breaking in NON ");
							}							
						}
						/* Warning!!!!!! this will not work on Tomcat unless you have the attribute crossContext="true" in conf/context.xml
						 * for example:
						 * 
						 * <Context crossContext="true">
						 * 
						 * ...
						 * 
						 * </Context>
						 */
						else{
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME=": projCtx is NULL");
							}
						}									
					}
				}									
			}
			else{
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+" No matching forward url for rule:"+aRule);
				}
			}
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END:");
		}
		return retVal;
	}
	
	
	private ServletCtxAndForwardUrl processFullForward(String url, String uri, String searchUrl, String projPath){
		String METHOD_NAME="processFullForward()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: url="+url+
					              "\n uri="+uri+
					              "\n searchUrl="+searchUrl+
					              "\n projPath="+projPath);
		}
		ServletCtxAndForwardUrl retVal=null;
		//remove the ^^prefix
		String searchUrlNoPrefix=searchUrl.substring(2);
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": searchUrlNoPrefix="+searchUrlNoPrefix);
		}
		if(null!=searchUrlNoPrefix && !searchUrlNoPrefix.trim().isEmpty()){
			if(searchUrlNoPrefix.startsWith("/")){
				searchUrlNoPrefix=(searchUrlNoPrefix.substring(1));
			}
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": searchUrlNoPrefix="+searchUrlNoPrefix);
				log.debug(METHOD_NAME+": (searchUrlNoPrefix.trim().equals(\""+uri.trim()+"\")="+(searchUrlNoPrefix.trim().equals(uri.trim())));
			}
			//Now check to see if the uri matches the searchUrlNoPrefix
			if(searchUrlNoPrefix.trim().equals(uri.trim())){
				retVal=new ServletCtxAndForwardUrl();
				
				String serverOnly=null;
				String fullUrlForwardirection=null;
				int index=url.indexOf(uri);
				if(-1!=index){
					//serverUrlString should be of the form: http://docs-staging.rackspace.com/
					serverOnly=url.substring(0,index);	
					if(null!=serverOnly&&!serverOnly.isEmpty()){
						if(!serverOnly.endsWith("/")){
							serverOnly+="/";
						}
					}
					fullUrlForwardirection=(serverOnly+projPath);
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": serverOnly="+serverOnly);
						log.debug(METHOD_NAME+": fullUrlForwardirection="+fullUrlForwardirection);
					}					
					
					//the path to forward to can have the format: cf-devguide/content in this case, 
					//folder to the left of the first '/' is the webapp folder and the folder/folders 
					//to the right of the initial '/' is/are subfolder/subfolders
					int projPathSlashIndex=projPath.indexOf("/");
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": projPath="+projPath);
						log.debug(METHOD_NAME+": projPathSlashIndex="+projPathSlashIndex);
					}
					if(-1!=projPathSlashIndex){
						String webAppFolderStr=projPath.substring(0,projPathSlashIndex);
						String pathToResource=projPath.substring(webAppFolderStr.length());
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": webAppFolderStr="+webAppFolderStr);
							log.debug(METHOD_NAME+": pathToResource="+pathToResource);
						}
						retVal.setFullRedirectUrlStr(pathToResource);
						String temp="";
						String rootPath=this.ctx.getRealPath(File.separator);
						String webAppsPath=rootPath+="..";
						webAppsPath=this.getRealPath(webAppsPath);
						File webAppsFolder=new File(webAppsPath);
						Map<String,File>webAppsFilesAndFoldersMap=this.getWebAppsFilesAndFolders(webAppsFolder);
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": rootPath="+rootPath);
							log.debug(METHOD_NAME+": webAppsPath="+webAppsPath);
							log.debug(METHOD_NAME+": (webAppsFilesAndFoldersMap.containsKey(webAppFolderStr))="+
							(webAppsFilesAndFoldersMap.containsKey(webAppFolderStr)));
						}
						if(webAppsFilesAndFoldersMap.containsKey(webAppFolderStr)){
							temp=this.getProjFolderWithSuffix(webAppFolderStr);

							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": temp="+temp);
								log.debug(METHOD_NAME+": this.ctx.getRealPath(\"..\")="+this.ctx.getRealPath(".."));
							}
							ServletContext projCtx=this.ctx.getContext(temp);
							if(log.isDebugEnabled()){
								if(null!=projCtx){
								    log.debug(METHOD_NAME+": projCtx.getRealPath(\".\")="+projCtx.getRealPath("."));
								}
								log.debug(METHOD_NAME+": projCtx="+projCtx);
								log.debug(METHOD_NAME+": setting project context");
							}
							retVal.setCtx(projCtx);
						}
					}
				}
			}
		}	
		//Record that we have a match for this url
		if(null!=retVal){
			if(bookInfoMap.size()>5000){
				bookInfoMap.clear();
			}
			this.bookInfoMap.put(url, retVal);	
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+" END");
		}
		return retVal;
			
	}
	
	private String getProjFolderWithSuffix(String webAppFolder){
		String METHOD_NAME="getProjFolderWithSuffix()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+" START: webAppFolder="+webAppFolder);
		}
		String webAppFolderName=this.getRealPath(this.ctx.getRealPath(File.separator)+"/..");										
		Map<String,File>webAppsFilesAndFoldersMap=this.getWebAppsFilesAndFolders(new File(webAppFolderName));
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": this.ctx is NOT NULL");
			log.debug(METHOD_NAME+": webAppFolderName="+webAppFolderName);
			log.debug(METHOD_NAME+": this.ctx.getContextPath()="+this.ctx.getContextPath());
			log.debug(METHOD_NAME+": webAppsFilesAndFoldersMap.containsKey("+webAppFolder+")="+webAppsFilesAndFoldersMap.containsKey(webAppFolder));
			log.debug(METHOD_NAME+": webAppsFilesAndFoldersMap.containsKey("+webAppFolder+"-internal)="+webAppsFilesAndFoldersMap.containsKey(webAppFolder+"-internal"));
			log.debug(METHOD_NAME+": webAppsFilesAndFoldersMap.containsKey("+webAppFolder+"-reviewer)="+webAppsFilesAndFoldersMap.containsKey(webAppFolder+"-reviewer"));
		}

		String retVal="";
		if(webAppsFilesAndFoldersMap.containsKey(webAppFolder)){
			//ServletContext diffCtx=this.ctx.getContext("/TestWeb1");
			retVal="/"+webAppFolder;
		}
		else if(webAppsFilesAndFoldersMap.containsKey(webAppFolder+"-internal")){
			retVal="/"+webAppFolder+"-internal";
		}
		else if(webAppsFilesAndFoldersMap.containsKey(webAppFolder+"-reviewer")){
			retVal="/"+webAppFolder+"-reviewer";
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: trying to get context: "+retVal);
		}		
		return retVal;
	}

	public boolean doesPdfNameEndInADate(String pdfFileName){
		boolean retVal=true;
		String METHOD_NAME="doesPdfNameEndInADate()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: retVal="+retVal+" pdfFileName="+pdfFileName);
			log.debug(METHOD_NAME+": (pdfFileName.toLowerCase()).trim().endsWith(\".pdf\")="+(pdfFileName.toLowerCase()).trim().endsWith(".pdf"));
			log.debug(METHOD_NAME+": pdfFileName.length()="+pdfFileName.length());
		}
		if(null!=pdfFileName && !pdfFileName.isEmpty()){
			pdfFileName=pdfFileName.trim();
			if(pdfFileName.endsWith("/")){
				pdfFileName=pdfFileName.substring(0, (pdfFileName.length()-1));
			}
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": pdfFileName="+pdfFileName);
			}   		
			if((pdfFileName.toLowerCase()).endsWith(".pdf") && pdfFileName.length()>12){

				String datePart=pdfFileName.substring((pdfFileName.length())-12);
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": datePart="+datePart);
				}
				datePart=datePart.substring(0,(datePart.length()-4));
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": datePart="+datePart);
				}
				Date date=null;
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
				try {
					//date = sdf.parse(datePart);
					date = sdf.parse(datePart);
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": date="+date);
					}
				} 
				catch (ParseException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					retVal=false;
					log.debug(METHOD_NAME+": ParseException caught message:"+e.getMessage()+" keep on going");
				}
				catch(Throwable e){
					retVal=false; 	
					log.debug(METHOD_NAME+": Throwable exception caught message:"+e.getMessage()+" keep on going");
				}
			}
			else{
				retVal=false;
			}
		}
		else{
			retVal=false;
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: retVal="+retVal);
		}
		return retVal;
	}

	private int getSkipToNextSlashIndex(int startIndex, String searchUrl, String url){
		String METHOD_NAME="getSkipToNextSlashIndex()";
		int retVal=-1;
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+" START retVal="+retVal);
			log.debug(METHOD_NAME+" startIndex="+startIndex+" url="+url+" searchUrl="+searchUrl);
			log.debug(METHOD_NAME+" url.length="+url.length());
		}
		if(startIndex>0 && startIndex<url.length()){			
			retVal=startIndex+searchUrl.length();
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+" END retVal="+retVal);
		}
		return retVal;
	}

	//The rules are found in the WEB-INF/lib/redirect.properties file, each rule is delimited by the @ character
	private String[] getAllRules()throws IOException{
		String METHOD_NAME="getAllRuels()";

		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": ENTER");
		}
		String[] retVal=null;
		ServletContext rootCtx=this.ctx.getContext("/ROOT");
		Properties redirectProps=new Properties();

		redirectProps.load(rootCtx.getResourceAsStream("WEB-INF/redirect.properties"));

		String redirectsProp=redirectProps.getProperty("redirects");
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": redirectsProp="+redirectsProp);
		}
		if(null!=redirectsProp && !redirectsProp.isEmpty()){
			retVal=redirectsProp.split("@");
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": EXIT");
		}
		return retVal;
	}

	public class ServletCtxAndForwardUrl{
		private ServletContext ctx=null;
		private String rightSideUrl=null;
		private String fullRedirectUrlStr=null;

		public ServletContext getCtx() {
			return ctx;
		}
		public void setCtx(ServletContext ctx) {
			this.ctx = ctx;
		}
		public String getRightSideUrl() {
			return rightSideUrl;
		}
		public void setRightSideUrl(String rightSideUrl) {
			this.rightSideUrl = rightSideUrl;
		}
		public String getFullRedirectUrlStr(){
			return this.fullRedirectUrlStr;
		}
		public void setFullRedirectUrlStr(String fullRedirectUrlStr){
			this.fullRedirectUrlStr=fullRedirectUrlStr;
		}
	}

	//Returns a Map of all the files and folders in the webapps folder keyed by the .war file name of the 
	//folder name and whose value is the respective File Object
	private Map<String,File>getWebAppsFilesAndFolders(File webAppsFolder){
		String METHOD_NAME="getWebAppsFilesAndFolders()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: installedWebAppDirStr="+webAppsFolder);
		}
		String realPathStr=this.getRealPath(webAppsFolder);
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": realPathStr="+realPathStr);
		}
		webAppsFolder=new File(realPathStr);
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": webAppsFolder="+webAppsFolder);
		}
		Map<String,File>retVal=new HashMap<String,File>();
		if(null!=webAppsFolder && webAppsFolder.exists() && webAppsFolder.isDirectory()){
			File[]installedWebAppFilesAndFolders=webAppsFolder.listFiles();
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": installedWebAppFilesAndFolders BEGIN");
			}
			for(File aFileOrFolder:installedWebAppFilesAndFolders){
				String aFileOrFolderName=aFileOrFolder.getName();
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": aFileOrFolderName="+aFileOrFolderName);
				}				
				if(aFileOrFolder.isFile() && aFileOrFolderName.endsWith(".war")){
					retVal.put(aFileOrFolderName, aFileOrFolder);
				}
				//This is a folder
				else if(aFileOrFolder.isDirectory()){
					retVal.put(aFileOrFolderName, aFileOrFolder);
				}
			}
			log.debug(METHOD_NAME+": installedWebAppFilesAndFolders END");
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END retVal.size()="+retVal.size());
		}
		return retVal;
	}	


	private void expandWar(List<Email>emailList, StringBuffer zipExceptionMessage, File warFile){
		String METHOD_NAME="expandWar()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: warFile="+warFile);
		}
		String theWarFileName=null;
		String webAppsFolder=null;
		if(null!=warFile && warFile.exists()){
			try {
				theWarFileName=this.getRealPath(warFile);
				String theWarFolderName=theWarFileName.substring((theWarFileName.lastIndexOf("/")+1),theWarFileName.lastIndexOf(".war"));	    	
				webAppsFolder=theWarFileName.substring(0,(theWarFileName.lastIndexOf("/")+1));
				//webAppsFolder=(this.ctx.getContext(webAppsFolder).getRealPath(webAppsFolder));
				String theWebAppFolder=webAppsFolder+theWarFolderName+"/";

				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": theWarFileName="+theWarFileName);
					log.debug(METHOD_NAME+": theWarFolderName="+theWarFolderName);
					log.debug(METHOD_NAME+": webAppsFolder="+webAppsFolder);
					log.debug(METHOD_NAME+": theWebAppFolder="+theWebAppFolder);
				}
				ZipFile zipFile=new ZipFile(warFile);

				Enumeration zipEnums=zipFile.entries();
				int count=0;

				while(zipEnums.hasMoreElements()){
					ZipEntry aZipEntry=(ZipEntry)zipEnums.nextElement();
					String aZipEntryName=aZipEntry.getName();
					String aZipEntryFolderName=aZipEntryName.substring(0,(aZipEntryName.lastIndexOf("/")+1));	    		
					String newFolderName=theWebAppFolder+aZipEntryFolderName;
					File folder=new File(newFolderName);
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": aZipEntryName="+aZipEntryName);
						log.debug(METHOD_NAME+": aZipEntryFolderName="+aZipEntryFolderName);
						log.debug(METHOD_NAME+": newFolderName="+newFolderName);
						log.debug(METHOD_NAME+": folder.exists()="+folder.exists());
					}	    		
					if(!folder.exists()){
						folder.mkdirs();
					}
					String zipFileName=theWebAppFolder+aZipEntryName;

					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": zipFileName="+zipFileName);
					}
					File aFile=new File(zipFileName);

					InputStream inny=zipFile.getInputStream(aZipEntry);
					FileOutputStream tempWarFileOutStream=null;
					try{
						tempWarFileOutStream=new FileOutputStream(aFile);
					}
					catch(FileNotFoundException e){
						//Sometimes when we iterate through the zipentries, a zipentry file is encountered
						//before the parent folder, so in this instance we have to first create the parent 
						//folder then create the file
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": could not find aFile: "+aFile.getAbsolutePath());
							log.debug(METHOD_NAME+": aFile.isFile()= "+aFile.isFile());
						}
						String parentFolderStr=aFile.getAbsolutePath();
						int lastSlashIndex=parentFolderStr.lastIndexOf("/");
						if(-1!=lastSlashIndex){
							parentFolderStr=parentFolderStr.substring(0,lastSlashIndex);

							File parentFolder=new File(parentFolderStr);
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": parentFolderStr="+parentFolderStr);
								log.debug(METHOD_NAME+": parentFolder.exists()="+parentFolder.exists());
							}
							if(!parentFolder.exists()){
								parentFolder.mkdirs();
								tempWarFileOutStream=new FileOutputStream(aFile);
							}
						}
					}
					int read=-1;
					//Write out the compress .war file to the tempdir
					while(-1!=(read=inny.read())){
						tempWarFileOutStream.write(read);
					}
					if(null!=tempWarFileOutStream){
						tempWarFileOutStream.close();
					}
					if(null!=inny){
						inny.close();	
					}
					++count;
				}
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": expanded "+count+" files/folders.");
				}
			} 
			catch (ZipException e) {
				log.error(METHOD_NAME+": ZipException e.getMessage()="+e.getMessage(),e);

				zipExceptionMessage.append(theWarFileName);
				zipExceptionMessage.append("\n");
				
				List<String>docNames=new ArrayList<String>();
				docNames.add("ROOT.war");

				if(null==this.emailProps && null!=webAppsFolder){
					log.error(METHOD_NAME+":Trying to load email.properties");
					if(!webAppsFolder.endsWith("/")){
						webAppsFolder+="/";
					}
					String rootPath=(webAppsFolder+"ROOT/");
					String pathToEmailProps=(rootPath+"WEB-INF/email.properties");
					log.error(METHOD_NAME+":rootPath="+rootPath);
					log.error(METHOD_NAME+":pathToEmailProps="+pathToEmailProps);
					File emailPropsFile=new File(pathToEmailProps);
					try {
						FileInputStream fileInny=new FileInputStream(emailPropsFile);
						this.emailProps=new Properties();
						this.emailProps.load(fileInny);
					} 
					catch (FileNotFoundException e2) {
						log.error(METHOD_NAME+": FileNotFoundException: e2.getMessage()="+e2.getMessage());
						e2.printStackTrace();
					} 
					catch (IOException e2) {			
						log.error(METHOD_NAME+": IOException: e2.getMessage()="+e2.getMessage());
						e2.printStackTrace();
					}
				}

				if(null!=this.emailProps){
					log.debug(METHOD_NAME+": email.properties is loaded, get the emails");
					String emailsStr=emailProps.getProperty("emails");
					String fromStr=emailProps.getProperty("from", RACKSPACE_EMAIL);
					String[] emailsArray=emailsStr.split(";");

					for(int i=0;i<emailsArray.length;++i){
						String emailAddress=emailsArray[i];		    	
						Email anEmail=new Email("DocTools Admin", emailAddress, fromStr, "ROOT.war ZipException", docNames);
						emailList.add(anEmail);
					}
				}
				else{
					log.debug(METHOD_NAME+": could not load email.properties: Adding default hard coded emails: ");
					Email anEmail1=new Email("DocTools Admin", EMAIL_ONE, RACKSPACE_EMAIL, "ROOT.war ZipException", docNames);
					Email anEmail2=new Email("DocTools Admin", EMAIL_TWO, RACKSPACE_EMAIL, "ROOT.war ZipException", docNames);
					emailList.add(anEmail1);
					emailList.add(anEmail2);	    				    			
				}
				e.printStackTrace();
			} 
			catch (IOException e) {
				log.error(METHOD_NAME+": IOException e.getMessage()="+e.getMessage(),e);
				e.printStackTrace();
			}
			catch(Throwable e){
				log.error(METHOD_NAME+": Throwable e.getMessage()="+e.getMessage(),e);
				e.printStackTrace();
			}
		}
		else{
			if(null==warFile){
				log.debug(METHOD_NAME+": warFile is null");
			}
			if(!warFile.exists()){
				log.debug(METHOD_NAME+": warFile.getAbsolutePath(): "+warFile.getAbsolutePath()+" does not exist");
			}
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END:");
		}
	}
	
	public static String getTimeStamp(ZipFile currentZipFile){
		String METHOD_NAME="getTimeStamp()";
		String retVal="";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: retVal="+retVal);
		}		
		try{
			ZipEntry aZipEntry=currentZipFile.getEntry("WEB-INF/warinfo.properties");
			//Just in case we cant find the warinfo.properties file iterate through the zip entries and try to get
			//it that way
			if(null==aZipEntry){
				retVal=getTimeStampOld(currentZipFile);
			}
			else{
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": aZipEntry.getName()="+aZipEntry.getName());
				}
				Properties warinfoProps=new Properties();
				warinfoProps.load(currentZipFile.getInputStream(aZipEntry));
				String timestamp=warinfoProps.getProperty("timestamp");
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": timestamp=" +timestamp);
				}
				if(null!=timestamp && !timestamp.isEmpty()){
					retVal=(timestamp.trim());					
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(Throwable e){
			e.printStackTrace();
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: retVal="+retVal);
		}		
		return retVal;		
	}
	
	public static String getTimeStampOld(ZipFile currentZipFile){
		String METHOD_NAME="getTimeStampOld()";
		String retVal="";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: retVal="+retVal);
		}
		Enumeration currentZipEntries=currentZipFile.entries();
		try{
			while(currentZipEntries.hasMoreElements()){
				ZipEntry aZipEntry=(ZipEntry)currentZipEntries.nextElement();
				String aZipEntryPathFileName=aZipEntry.getName();
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": aZipEntryPathFileName="+aZipEntryPathFileName);
					log.debug(METHOD_NAME+": aZipEntryPathFileName.endsWith(\"+warinfo.properties+\")="+
							aZipEntryPathFileName.endsWith("warinfo.properties"));
				}
				//We are only interested in the warinfo.properties 
				if(!aZipEntry.isDirectory() && aZipEntryPathFileName.endsWith("warinfo.properties")){
					Properties warinfoProps=new Properties();
					warinfoProps.load(currentZipFile.getInputStream(aZipEntry));
					String timestamp=warinfoProps.getProperty("timestamp");
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": timestamp=" +timestamp);
					}
					if(null!=timestamp && !timestamp.isEmpty()){
						retVal=timestamp;//((timestamp.replaceAll(":", "-"))).trim().replaceAll(" ", "_");					
					}
					break;
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(Throwable e){
			e.printStackTrace();
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: retVal="+retVal);
		}
		return retVal;		
	}	

}
