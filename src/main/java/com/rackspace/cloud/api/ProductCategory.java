package com.rackspace.cloud.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductCategory {
	
	private String categoryName;
	private String categoryOrder;
	//private Set<String> productIds;
	private Map<String, Product> productsMap;
	private List<Product>productsList;
	
	public ProductCategory(){
		this.categoryName=null;
		this.categoryOrder=null;
		//this.productIds=new HashSet<String>();
		this.productsMap=new LinkedHashMap<String, Product>();		
		this.productsList=new ArrayList<Product>();
		
	}
	
	public ProductCategory(String categoryName, String categoryOrder,Set<String> productIds, 
			Map<String,Product>productsMap,List<Product>productsList){
		this.categoryName=categoryName;
		this.categoryOrder=categoryOrder;
		//this.productIds=productIds;
		this.productsMap=productsMap;
		this.productsList=productsList;
	}

	
	public boolean containsProductId(String productId){
		boolean retVal=false;
		if(null!=productId){// && null!=this.productIds){
			retVal=this.productsMap.containsKey(productId);
		}
		return retVal;
	}
	
	public List<Product>getProductsList(){
		return this.productsList;
	}
	
	public void setProductsList(List<Product>productsLists){
		this.productsList=productsLists;
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
	public Map<String, Product> getProductsMap() {
		return productsMap;
	}
	public void setProductsMap(Map<String, Product> productsMap) {
		this.productsMap = productsMap;
	}
	
	public int getProductsMapSize(){
	    int retVal=0;
	    if(null!=this.productsMap){
	    	retVal=productsMap.size();
	    }
	    return retVal;
	}

}
