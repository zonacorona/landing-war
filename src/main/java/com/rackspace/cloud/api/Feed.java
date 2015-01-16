package com.rackspace.cloud.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;



public class Feed{

	private static Logger log = Logger.getLogger(Feed.class);
	
	private String title;
	private String url;
	private String releaseNotesUrl;
	
	private String description;
	
	public Feed(){
		this.title=null;
		this.releaseNotesUrl=null;
		this.url=null;
		this.description=null;
	}
	
	public Feed(String title, String url, String releaseNotesUrl, String description){
		this.title=title;
		this.url=url;
		this.releaseNotesUrl=releaseNotesUrl;
		this.description=description;
	}

	public String getReleaseNotesUrl(){
		return this.releaseNotesUrl;
	}
	
	public void setReleaseNotesUrl(String releaseNotesUrl){
		this.releaseNotesUrl=releaseNotesUrl;
	}
	
	public String getTitle() {
		if(null!=title){
			//If there is a trademark character escape it, note if we are using the jstl function in a jsp: <c:out>
			//you must set the c:out attribute: escapeXml="false", for example:
			//<c:out value="${someVal}" excapeXml="false"/>
			title=this.title.replace("™", "&trade;");
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		if(null!=this.url){
			//A url to a docs resource should always have the String "/content/" in it, for example:
		    //http://localhost:9090/auth/api/v2.0/auth-client-devguide/content/GET_listUsers__v2.0_users__User_Calls.html
			//If the revhistory was not set up right then the content String could be missing, try to add it correctly
			if(!url.contains("content")){
				int indexLastSlash=this.url.lastIndexOf("/");
				if(-1!=indexLastSlash){
				    String lhs=this.url.substring(0,indexLastSlash);
				    String rhs=this.url.substring(indexLastSlash);
				    this.url=lhs+"/content"+rhs;
				}
			}
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String toString(){
		StringBuilder retVal=new StringBuilder("{");
		
		retVal.append("title=");
		retVal.append(this.title);
		retVal.append(", url=");
		retVal.append(this.url);
		retVal.append(", releaseNotesUrl=");
		retVal.append(this.releaseNotesUrl);
		
		retVal.append("}");
		
		return retVal.toString();
	}
	
// For testing locally, NOTE: you must have a local atom file and point
// the atomAllXmlFile variable to the right path.
//	public static void main(String[]args){
//		SAXParserFactory factory=SAXParserFactory.newInstance();
//		
//		try {
//			SAXParser parser=factory.newSAXParser();
//			FeedSaxHandler feedSaxHandler=new FeedSaxHandler();
//			File atomAllXmlFile=new File("/Users/thu4404/Downloads/atom-allTest.xml");
//			if(atomAllXmlFile.exists()){
//			    try {
//					FileInputStream inny=new FileInputStream(atomAllXmlFile);
//					try {
//						parser.parse(inny, feedSaxHandler);
//						List<Feed>rssFeeds=feedSaxHandler.getEntriesList();
//						System.out.println("@@@@@@@@@@@@@@@@@rssFeeds Begin:");
//						
//						for(Feed aFeed:rssFeeds){
//							System.out.println(aFeed);
//						}
//						
//						System.out.println("@@@@@@@@@@@@@@@@@rssFeeds End:");
//					} 
//					catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				} 
//			    catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			    
//			}
//		} 
//		catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
}