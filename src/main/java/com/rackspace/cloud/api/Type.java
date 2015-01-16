package com.rackspace.cloud.api;

import org.apache.log4j.Logger;

public class Type implements Comparable<Type>{
	private String id;
	private String displayName;
	private String url;
	private String sequence;
	private String prodId;
	private String folderName;
	private static Logger log=Logger.getLogger(Type.class);


	public boolean getHasTarget(){
		boolean retVal=false;
		if(null!=url&&((url.toLowerCase()).contains("http://"))){
			retVal=true;
		}
		return retVal;
	}
	
	public Type(){
		this.id=null;
		this.displayName=null;
		this.url=null;
		this.sequence=null;
		this.prodId=null;
	}

	public Type(String prodId, String id, String displayName, String url, String seq, String folderName){
		this.id=id;
		this.displayName=displayName;
		this.url=url;
		this.sequence=seq;
		this.prodId=prodId;
		this.folderName=folderName;
	}

	public int compareTo(Type t){
		String METHOD_NAME="compreTo(Type t)";
		int retVal=1;
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: retVal="+retVal+" this.sequence="+this.sequence+" t.sequence="+t.sequence);
		}
		if(null!=t && null!=t.sequence && null!=this.sequence){
			int thisOrder=0;
			int otherOrder=0;
			try{
		        thisOrder=Integer.valueOf(this.sequence);		        
			}
			catch(NumberFormatException e){
				//If there is no order specified for this object then make it 
				//appear later
				return 1;
			}
		    try{
		    	otherOrder=Integer.valueOf(t.sequence);
		    }
		    catch(NumberFormatException e){
		    	//If there is no order specified for the other object then make this
		    	//object appear first
		    	return -1;
		    }
		    if(thisOrder>otherOrder){
		    	retVal=1;
		    }
		    else if(thisOrder<otherOrder){
		    	retVal=-1;
		    }
		    else{
		    	retVal=0;
		    }
		}
		if(0==retVal){
			retVal=this.displayName.compareTo(t.displayName);
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: retVal="+retVal +" this.sequence="+this.sequence+" t.sequence="+t.sequence);
		}
		return retVal;
	}
	
	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getProdId(){
		return this.prodId;
	}
	
	public void setProdId(String prodId){
		this.prodId=prodId;
	}
	
	public String getSequence() {
		return sequence;
	}

	public void setSequenece(String order) {
		this.sequence = order;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String toString(){
		return "{prodid="+prodId+",id="+this.getId()+", "+"displayName="+this.getDisplayName()+", url="+this.getUrl()+", this.sequence="+this.sequence+"}";
	}
}
