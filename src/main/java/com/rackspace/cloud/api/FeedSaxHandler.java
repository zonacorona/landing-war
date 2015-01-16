package com.rackspace.cloud.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FeedSaxHandler extends DefaultHandler{
	
	private boolean hasFeed;
	
	//Some entries in the atom-all.xml have lists that do not have <html:a ... for such entries
	//we use the link href value, but we only want to add such entries once instead of multiple times
	//because they would result in anchors with the same name and same url
	private boolean hasAddedAnUpdateWithoutAUrl=false;
	
	private Set<String>uniqueUrlNames;
	
	private StringBuilder title;
	private boolean hasTitle;
    
    private String aUrl;
    private boolean hasAUrl;    
    
    private String releaseNotesUrl;
    private boolean hasReleaseNotesUrl;
	
	private List<Feed>entriesList;
	
	private Feed anEntry;
	private boolean hasAnEntry;
    
    private String li;
    private boolean hasLi;
    
    private StringBuilder description;
    private boolean hasDescription;
    
    public String getDescription(){
    	String retVal="";
    	if(null!=this.description){
    		retVal=this.description.toString();
    	}
    	return retVal;
    }
    
    public void setDesription(StringBuilder description){
    	this.description=description;
    }
    
    public boolean isHasDescription(){
    	return this.hasDescription;
    }
    
    public void setHasDescription(boolean hasDescription){
    	this.hasDescription=hasDescription;
    }
    
    public String getReleaseNotesUrl(){
    	return this.releaseNotesUrl;
    }
    
    public void setReleaseNotesUrl(String releaseNotesUrl){
    	this.releaseNotesUrl=releaseNotesUrl;
    }
    
    public boolean isHasReleaseNotesUrl(){
    	return this.hasReleaseNotesUrl;
    }
    
    public void setHasReleaseNotesUrl(boolean hasReleaseNotesUrl){
    	this.hasReleaseNotesUrl=hasReleaseNotesUrl;
    }
    
    
    public boolean isHasFeed(){
    	return this.hasFeed;
    }
    
    public void setHasFeed(boolean hasFeed){
    	this.hasFeed=hasFeed;
    }
    
    public String getAUrl(){
    	return this.aUrl;
    }
    
    public void setAUrl(String aUrl){
    	this.aUrl=aUrl;
    }
    
    public boolean isHasAUrl(){
    	return this.hasAUrl;
    }
    
    public void setHasAUrl(boolean hasAUrl){
    	this.hasAUrl=hasAUrl;
    }
    
    public String getLi(){
    	return this.li;
    }
    
    public void setLi(String li){
    	this.li=li;
    }
    
    public boolean isHasLi(){
    	return this.hasLi;
    }
	
    
    public void setHasLi(boolean hasLi){
    	this.hasLi=hasLi;
    }
    
	public String getTitle(){
		String retVal="";
		if(null!=this.title){
			retVal=this.title.toString();
		}
		return retVal;	
	}
	
	public void setTitle(String title){
		this.title=new StringBuilder(title);
	}
	
	public boolean isHasTitle(){
		return this.hasTitle;
	}
	
	public List<Feed>getEntriesList(){
		return this.entriesList;
	}
	
	public void setEntriesList(List<Feed>entriesList){
		this.entriesList=entriesList;
	}
	
	public Feed getAnEntry(){
		return this.anEntry;
	}
	
	public void setAnEntry(Feed anEntry){
		this.anEntry=anEntry;
	}
	
	public boolean isHasAnEntry(){
		return this.hasAnEntry;
	}
	
	public void setHasAFeed(boolean hasAnEntry){
		this.hasAnEntry=hasAnEntry;
	}
	
	public void startElement(String uri, String localName,String qName, 
			Attributes attributes) throws SAXException{

		if(qName.equals("feed")){				
			this.hasFeed=true;
			this.entriesList=new ArrayList<Feed>();
		}
		if(qName.equals("entry")){
			this.hasAnEntry=true;
			this.uniqueUrlNames=new HashSet<String>();
		}
		if(qName.equals("link")){
			this.hasReleaseNotesUrl=true;
			this.releaseNotesUrl=attributes.getValue("href");
		}
		if(qName.equals("title")){
			this.hasTitle=true;
			this.title=new StringBuilder("");
		}
		if(qName.equals("li")){
			this.hasLi=true;
			this.aUrl="";
			this.description=new StringBuilder("");
		}
		if(qName.equals("html:a")){
			this.hasAUrl=true;	
			this.aUrl=attributes.getValue("href");			
		}
		if(qName.equals("p")){
			this.hasDescription=true;
		}
	}
	
	public void characters(char ch[], int start, int length)
			throws SAXException {
		if(this.hasTitle){
			String subTitle=new String(ch, start, length);
			if(null!=title){
			    this.title.append(subTitle);
			}
		}
		if(this.hasDescription){
			String descrip=new String(ch, start, length);
			if(null!=descrip){
				if(null==this.description){
					this.description=new StringBuilder("");
				}
				this.description.append(descrip);
			}
		}
	}
	
	public void endElement(String uri, String localName,
			String qName) throws SAXException {
		
		if(qName.equals("feed")){
			this.hasFeed=false;
		}
		if(qName.equals("entry")){
			this.hasAnEntry=false;	
			this.releaseNotesUrl="";
			this.hasAddedAnUpdateWithoutAUrl=false;
		}
		if(qName.equals("title")){
			this.hasTitle=false;
		}
		if(qName.equals("link")){
			this.hasReleaseNotesUrl=false;
		}
		if(qName.equals("html:a")){
			this.hasAUrl=false;			
		}		
		if(qName.equals("li")){

			this.hasLi=false;
			this.anEntry=new Feed();
			if(null!=this.title){
				StringBuilder tempTitle=new StringBuilder(this.title);
				//The code commented below is a way to try to ensure that the title is unique that way
				//the name of the url that is displayed in the list of recent updates have a much higher
				//probability of being unique
				//Uncomment the code if this is a desired feature
//				if(null!=this.description){
//					StringTokenizer strTokens=new StringTokenizer(this.description.toString());
//					//Get the first 4 tokens
//					for(int i=0;((i<4&& strTokens.hasMoreTokens())||
//							(this.uniqueUrlNames.contains(tempTitle.toString())&& strTokens.hasMoreTokens()));++i){
//						//We have a match just getting the next token may make the title unique, but it may
//						//not make much sense so try to get the next 4 tokens
//						if((this.uniqueUrlNames.contains(tempTitle.toString())) && (strTokens.countTokens()>=4) ){							
//							for(int j=0;j<3&& strTokens.hasMoreTokens();++j){
//								if(i==0){
//									tempTitle.append("-"+strTokens.nextToken());
//								}
//								else{
//									tempTitle.append(" "+strTokens.nextToken());
//								}
//							}
//						}
//						else{
//							if(i==0){
//								tempTitle.append("-"+strTokens.nextToken());
//							}
//							else{
//								tempTitle.append(" "+strTokens.nextToken());
//							}
//						}
//					}
//					this.uniqueUrlNames.add(tempTitle.toString());
//				}
				
				this.anEntry.setTitle(tempTitle.toString());
			}
			if(null!=this.aUrl && !(this.aUrl.trim().isEmpty()) ){				
			    anEntry.setUrl(this.aUrl);
			    this.hasAUrl=true;
			}
			else{
				anEntry.setUrl(this.releaseNotesUrl);
				this.hasAUrl=false;
			}
			this.anEntry.setReleaseNotesUrl(this.releaseNotesUrl);
			this.aUrl="";
			if(null!=anEntry.getUrl() && !anEntry.getUrl().isEmpty()){
				
				if(!this.hasAddedAnUpdateWithoutAUrl || this.hasAUrl){
				    this.entriesList.add(this.anEntry);			
				    this.hasAddedAnUpdateWithoutAUrl=true;
				    this.hasAUrl=false;
				}
			}
		}
		if(qName.equals("p")){
			this.hasDescription=false;
		}
	}

}
