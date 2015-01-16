package com.rackspace.cloud.api.beans;

import java.io.InputStream;

public class InputStreamAndFileName{
	InputStream inny;
	String fileName;
	
	public InputStreamAndFileName(){
		this.inny=null;
		this.fileName=null;
	}
	
	public InputStreamAndFileName(InputStream inny, String fileName){
		this.inny=inny;
		this.fileName=fileName;
	}
	
	public InputStream getInny(){
		return inny;			
	}
	
	public void setInny(InputStream inny){
		this.inny=inny;
	}
	
	public String getFileName(){
		return this.fileName;
	}
	
	public void setFileName(String fileName){
		this.fileName=fileName;
	}
}
