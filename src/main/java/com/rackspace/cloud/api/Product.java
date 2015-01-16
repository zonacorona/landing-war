package com.rackspace.cloud.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Product implements Comparable<Product>{
	
	private String id;
	private String productClassification;
	private String productLogo;
	private String productName;
	private String logoUrl;
	private String icon1Url;
	private String icon2Url;
	private String url;
	private String column;
	private String order;
	private String sectionId;
	private String title;
	private String hasTradeMark;
	private boolean hasHasTradeMark;
	private String supplementalDisplayName;
	private String supplementalUrl;
	private List<Type>typesList;
	private String categoryName;
	private String categoryOrder;
	private String categorySequence;
	
	private Map<String,String>typesMap;
	
	public Product(){
		this.id=null;
		this.productLogo=null;
		this.productName=null;
		this.logoUrl=null;
		this.icon1Url=null;
		this.icon2Url=null;
		this.url=null;
		this.column=null;
		this.order=null;
		this.sectionId=null;
		this.title=null;
		this.supplementalDisplayName=null;
		this.supplementalUrl=null;
		this.hasTradeMark=null;
		this.hasHasTradeMark=false;
		this.categoryName=null;
		this.categoryOrder=null;
		this.categorySequence=null;
		this.typesList=new ArrayList<Type>();
		//This Map is used track the typesList, we do not want the typesList to have multiple entries, this was a quick fix
		//We really should make this Map of type Map<String, Type> and directly insert into the Map in Type order, but that would
		//require more coding and testing.
		this.typesMap=new LinkedHashMap<String, String>();
	}
	
	public Product(String id, String productClassification, String productLogo, String prodName, String title, String url, String logoUrl, 
			       String icon1Url, String icon2Url, String column, String order, String sectionId, String hasTradeMark, 
			       boolean hasHasTradeMark, String categoryName,String categoryOrder, String categorySequence, String supplementalDisplayName, 
			       String supplementalUrl){
		this.id=id;
		this.productClassification=productClassification;
		this.productLogo=productLogo;
		this.productName=prodName;
		this.title=title;
		this.url=url;
		this.logoUrl=logoUrl;
		this.icon1Url=icon1Url;
		this.icon2Url=icon2Url;
		this.column=column;
		this.order=order;
		this.sectionId=sectionId;
		this.hasTradeMark=hasTradeMark;
		this.hasHasTradeMark=hasHasTradeMark;
		this.supplementalDisplayName=supplementalDisplayName;
		this.supplementalUrl=supplementalUrl;
		this.categoryName=categoryName;
		this.categoryOrder=categoryOrder;
		this.categorySequence=categorySequence;
		this.typesList=new ArrayList<Type>();
		//This Map is used track the typesList, we do not want the typesList to have multiple entries, this was a quick fix
		//We really should make this Map of type Map<String, Type> and directly insert into the Map in Type order, but that would
		//require more coding and testing.
		this.typesMap=new LinkedHashMap<String,String>();
	}
	
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryOrder() {
		return categoryOrder;
	}

	public void setCategoryOrder(String categoryOrder) {
		this.categoryOrder = categoryOrder;
	}
	
	public String getCategorySequence(){
		return this.categorySequence;
	}
	
	public void setCategorySequence(String categorySequence){
		this.categorySequence=categorySequence;
	}

	public String getProductClassification(){
		return this.productClassification;
	}
	
	public void setProductClassification(String productClassification){
		this.productClassification=productClassification;
	}
	
	public String getProductLogo(){
		return this.productLogo;
	}
	
	public void setProductLogo(String productLogo){
		this.productLogo=productLogo;
	}

	public boolean getHasSupplementalValue(){
		if(null==this.supplementalDisplayName || this.supplementalDisplayName.isEmpty()){
			return false;
		}
		return true;
	}

	public String getSupplementalDisplayName() {
		return this.supplementalDisplayName;
	}

	public void setSupplementalDisplayName(String supplementalDisplayName) {
		this.supplementalDisplayName = supplementalDisplayName;
	}

	public String getSupplementalUrl() {
		return this.supplementalUrl;
	}

	public void setSupplementalUrl(String supplementalUrl) {
		this.supplementalUrl = supplementalUrl;
	}

	public int getTypesListSize(){
		if(null==this.typesList){
			return 0;
		}
		return this.typesList.size();
	}
	
	public List<Type> getTypesList() {
		return typesList;
	}

	public void setTypesList(List<Type> typesList) {
		this.typesList = typesList;
	}
	
	public Map<String,String>getTypesMap(){
		return this.typesMap;
	}
	
	public void setTypesMap(Map<String,String>typesMap){
		this.typesMap=typesMap;
	}

	public String getHasTradeMark(){
	    return this.hasTradeMark;	
	}
	
	public String isHasTradeMark() {
		return hasTradeMark;
	}

	public void setHasTradeMark(String hasTradeMark) {
		this.hasTradeMark = hasTradeMark;
	}
	
	public boolean getHasHasTradeMark(){
		return this.hasHasTradeMark;
	}
	
	public boolean isHasHasTradeMark(){
		return this.hasHasTradeMark;
	}
	
	public void setHasHasTradeMark(boolean hasHasTradeMark){
		this.hasHasTradeMark=hasHasTradeMark;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getColumn(){
		return this.column;
	}
	
	public void setColumn(String col){
		this.column=col;
	}
	
	public String getOrder(){
		return this.order;
	}
	
	public void setOrder(String ord){
		this.order=ord;
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setId(String id){
		this.id=id;
	}

	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getLogoUrl() {
		return logoUrl;
	}
	
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	
	public String getIcon1Url() {
		return icon1Url;
	}
	
	public void setIcon1Url(String icon1Url) {
		this.icon1Url = icon1Url;
	}

	public String getIcon2Url() {
		return icon2Url;
	}
	
	public void setIcon2Url(String icon2Url) {
		this.icon2Url = icon2Url;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String toString(){
		StringBuffer retVal=new StringBuffer("");
		retVal.append("{");
		if(null!=this){
			retVal.append("id=");		
			retVal.append(this.getId());
			retVal.append(", categoryName=");		
			retVal.append(this.getCategoryName());
			retVal.append(", categoryOrder=");		
			retVal.append(this.getCategoryOrder());
			retVal.append(", categorySequence=");		
			retVal.append(this.getCategorySequence());
			retVal.append(", productLogo=");
			retVal.append(this.getProductLogo());
			retVal.append(", prodName=");
			retVal.append(this.getProductName());
			retVal.append(", title=");
			retVal.append(this.getTitle());
			retVal.append(", url=");
			retVal.append(this.getUrl());
			retVal.append(", logourl=");
			retVal.append(this.getLogoUrl());
			retVal.append(", icon1Url=");
			retVal.append(this.getIcon1Url());
			retVal.append(", icon2Url=");
			retVal.append(this.getIcon2Url());
			retVal.append(", column=");
			retVal.append(this.getColumn());
			retVal.append(", order=");
			retVal.append(this.getOrder());
			retVal.append(", sectionId=");
			retVal.append(this.getSectionId());
		}
		retVal.append("}");
		
		return retVal.toString();	
	}
	
	public int compareTo(Product p){
		int retVal=1;
		if(null!=p && null!=p.order && null!=this.order){
			int thisOrder=0;
			int otherOrder=0;
			try{
		        thisOrder=Integer.valueOf(this.order);		        
			}
			catch(NumberFormatException e){
				//If there is no order specified for this object then make it 
				//appear later
				return 1;
			}
		    try{
		    	otherOrder=Integer.valueOf(p.order);
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
		    	retVal=1;
		    }
		    else{
		    	retVal=0;
		    }
		}
		if(0==retVal){
			retVal=this.productName.compareTo(p.productName);
		}
		return retVal;
	}
}
