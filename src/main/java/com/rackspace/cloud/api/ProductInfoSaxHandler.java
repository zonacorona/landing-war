package com.rackspace.cloud.api;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ProductInfoSaxHandler extends DefaultHandler{

	private List<Product>productsList;
	private Product aNewProduct;
	
	private String products;
	private boolean hasProducts;
	
	private String product;
	private boolean hasProduct;
	
	private String categoryName;
	private boolean hasCategoryName;

	private String categoryOrder;
	private boolean hasCategoryOrder;
	
	private String categorySequence;
	private boolean hasCategorySequence;

	private String id;
	private boolean hasId;

	private String productLogo;
	private boolean hasProductLogo;

	private String productClassification;
	private boolean hasProductClassification;

	private String name;
	private boolean hasName;

	private String hasTradeMark;
	private boolean hasHasTradeMark;

	private String title;
	private boolean hasTitle;

	private String url;
	private boolean hasUrl;

	private String icon1;
	private boolean hasIcon1;

	private String icon2;
	private boolean hasIcon2;

	private String sequence;
	private boolean hasSequence;

	private String column;
	private boolean hasColumn;

	private String order;
	private boolean hasOrder;

	private String sectionId;
	private boolean hasSectionId;

	public boolean isHasProducts() {
		return hasProducts;
	}

	public void setHasProducts(boolean hasProducts) {
		this.hasProducts = hasProducts;
	}

	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
	}

	public boolean isHasProduct() {
		return hasProduct;
	}

	public void setHasProduct(boolean hasProduct) {
		this.hasProduct = hasProduct;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public boolean isHasCategoryName() {
		return hasCategoryName;
	}

	public void setHasCategoryName(boolean hasCategory) {
		this.hasCategoryName = hasCategory;
	}

	public String getCategoryOrder() {
		return categoryOrder;
	}

	public void setCategoryOrder(String categoryOrder) {
		this.categoryOrder = categoryOrder;
	}

	public boolean isHasCategoryOrder() {
		return hasCategoryOrder;
	}

	public void setHasCategoryOrder(boolean hasCategoryOrder) {
		this.hasCategoryOrder = hasCategoryOrder;
	}
	
	public String getCategorySequence(){
		return this.categorySequence;
	}
	
	public void setCategorySequence(String categorySequence){
		this.categorySequence=categorySequence;
	}
	
	public boolean isHasCategorySequence(){
		return this.hasCategorySequence;
	}

	public void setHasCategorySequence(boolean hasCategorySequence){
		this.hasCategorySequence=hasCategorySequence;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isHasId() {
		return hasId;
	}

	public void setHasId(boolean hasId) {
		this.hasId = hasId;
	}

	public String getProductLogo() {
		return productLogo;
	}

	public void setProductLogo(String productLogo) {
		this.productLogo = productLogo;
	}

	public boolean isHasProductLogo() {
		return hasProductLogo;
	}

	public void setHasProductLogo(boolean hasProductLogo) {
		this.hasProductLogo = hasProductLogo;
	}

	public String getProductClassification() {
		return productClassification;
	}

	public void setProductClassification(String productClassification) {
		this.productClassification = productClassification;
	}

	public boolean isHasProductClassification() {
		return hasProductClassification;
	}

	public void setHasProductClassification(boolean hasProductClassification) {
		this.hasProductClassification = hasProductClassification;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isHasName() {
		return hasName;
	}

	public void setHasName(boolean hasName) {
		this.hasName = hasName;
	}

	public String getHasTradeMark() {
		return hasTradeMark;
	}

	public void setHasTradeMark(String hasTradeMark) {
		this.hasTradeMark = hasTradeMark;
	}

	public boolean isHasHasTradeMark() {
		return hasHasTradeMark;
	}

	public void setHasHasTradeMark(boolean hasHasTradeMark) {
		this.hasHasTradeMark = hasHasTradeMark;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isHasTitle() {
		return hasTitle;
	}

	public void setHasTitle(boolean hasTitle) {
		this.hasTitle = hasTitle;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isHasUrl() {
		return hasUrl;
	}

	public void setHasUrl(boolean hasUrl) {
		this.hasUrl = hasUrl;
	}

	public String getIcon1() {
		return icon1;
	}

	public void setIcon1(String icon1) {
		this.icon1 = icon1;
	}

	public boolean isHasIcon1() {
		return hasIcon1;
	}

	public void setHasIcon1(boolean hasIcon1) {
		this.hasIcon1 = hasIcon1;
	}

	public String getIcon2() {
		return icon2;
	}

	public void setIcon2(String icon2) {
		this.icon2 = icon2;
	}

	public boolean isHasIcon2() {
		return hasIcon2;
	}

	public void setHasIcon2(boolean hasIcon2) {
		this.hasIcon2 = hasIcon2;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	public boolean isHasSequence() {
		return hasSequence;
	}

	public void setHasSequence(boolean hasSequence) {
		this.hasSequence = hasSequence;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public boolean isHasColumn() {
		return hasColumn;
	}

	public void setHasColumn(boolean hasColumn) {
		this.hasColumn = hasColumn;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public boolean isHasOrder() {
		return hasOrder;
	}

	public void setHasOrder(boolean hasOrder) {
		this.hasOrder = hasOrder;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public boolean isHasSectionId() {
		return hasSectionId;
	}

	public void setHasSectionId(boolean hasSectionId) {
		this.hasSectionId = hasSectionId;
	}

	public List<Product> getProductsList() {
		return productsList;
	}

	public void startElement(String uri, String localName,String qName, 
        Attributes attributes) throws SAXException{
	     
		if(qName.equals("products")){
			this.hasProducts=true;
		}
		if(qName.equals("product")){
			this.hasProduct=true;
			this.id="";
			this.categoryName="";
			this.categoryOrder="";
			this.categorySequence="";
			this.productLogo="";
			this.sectionId="";
			this.productClassification="";
			this.url="";
			this.icon1="";
			this.icon2="";
			this.order="";
			this.name="";
			this.hasTradeMark="";
			this.title="";
			this.column="";
		}
		if(qName.equals("categoryName")){
			this.categoryName="";
			this.hasCategoryName=true;
		}
		if(qName.equals("categoryOrder")){
			this.hasCategoryOrder=true;
		}				
		if(qName.equals("categorySequence")){
			this.hasCategorySequence=true;
		}						
		if(qName.equals("id")){
			this.hasId=true;
		}
		if(qName.equals("productLogo")){
			this.hasProductLogo=true;
		}
		if(qName.equals("productClassification")){
			this.hasProductClassification=true;
		}
		if(qName.equals("name")){
			this.hasName=true;
		}
		if(qName.equals("title")){
			this.hasTitle=true;
		}
		if(qName.equals("url")){
			this.hasUrl=true;
		}
		if(qName.equals("icon1")){
			this.hasIcon1=true;
		}
		if(qName.equals("icon2")){
			this.hasIcon2=true;
		}
		if(qName.equals("sequence")){
			this.hasSequence=true;
		}
		if(qName.equals("column")){
			this.hasColumn=true;
		}
		if(qName.equals("order")){
			this.hasOrder=true;
		}
		if(qName.equals("sectionId")){
			this.hasSectionId=true;
		}
		if(qName.equals("hasTradeMark")){
			this.hasHasTradeMark=true;
		}
	
	}

	public void endElement(String uri, String localName,
			String qName) throws SAXException {
		if(qName.equalsIgnoreCase("product")){
			//This also instantiates a new typesList and typesMap
			this.aNewProduct=new Product();
			this.aNewProduct.setId(this.id);
			this.aNewProduct.setCategoryName(this.categoryName);
			this.aNewProduct.setCategoryOrder(this.categoryOrder);
			this.aNewProduct.setCategorySequence(this.categorySequence);
			this.aNewProduct.setProductLogo(this.productLogo);
			this.aNewProduct.setSectionId(this.sectionId);
			this.aNewProduct.setProductClassification(this.productClassification);
			this.aNewProduct.setUrl(this.url);
			this.aNewProduct.setIcon1Url(this.icon1);
			this.aNewProduct.setIcon2Url(this.icon2);
			this.aNewProduct.setOrder(this.order);			
			this.aNewProduct.setProductName(this.name);
			this.aNewProduct.setHasTradeMark(this.hasTradeMark);
			this.aNewProduct.setTitle(this.title);
			this.aNewProduct.setColumn(this.column);
			
			this.productsList.add(this.aNewProduct);
			
		}
		if(qName.equalsIgnoreCase("id")){
			this.hasId=false;
		}
		if(qName.equalsIgnoreCase("categoryName")){
			this.hasCategoryName=false;
		}
		if(qName.equalsIgnoreCase("categoryOrder")){
			this.hasCategoryOrder=false;
		}
		if(qName.equalsIgnoreCase("categorySequence")){
			this.hasCategorySequence=false;
		}
		if(qName.equalsIgnoreCase("productLogo")){
			this.hasProductLogo=false;
		}
		if(qName.equalsIgnoreCase("sectionId")){
			this.hasSectionId=false;
		}
		if(qName.equalsIgnoreCase("productClassification")){
			this.hasProductClassification=false;
		}
		if(qName.equalsIgnoreCase("url")){
			this.hasUrl=false;
		}
		if(qName.equalsIgnoreCase("icon1")){
			this.hasIcon1=false;
		}
		if(qName.equalsIgnoreCase("icon2")){
			this.hasIcon2=false;
		}
		if(qName.equalsIgnoreCase("order")){
			this.hasOrder=false;
		}
		if(qName.equalsIgnoreCase("name")){
			this.hasName=false;
		}
		if(qName.equalsIgnoreCase("hasTradeMark")){
			this.hasHasTradeMark=false;
		}
		if(qName.equalsIgnoreCase("title")){
			this.hasTitle=false;
		}
		if(qName.equalsIgnoreCase("column")){
			this.hasColumn=false;
		}
	}
	
		
	public void characters(char ch[], int start, int length)
			throws SAXException {
		
		if(this.hasProducts){
			this.productsList=new ArrayList<Product>();
			this.hasProducts=false;
		}
		if(this.hasId){
			this.id+=new String(ch,start,length);			
		}
		if(this.hasCategoryName){
			this.categoryName+=new String(ch, start, length);						
		}
		if(this.hasCategoryOrder){
			this.categoryOrder+=new String(ch,start,length);	
		}
		if(this.hasCategorySequence){
			this.categorySequence+=new String(ch,start,length);			
		}
		if(this.hasProductLogo){
			this.productLogo+=new String(ch,start,length);			
		}
		if(this.hasName){
			this.name+=new String(ch,start,length);			
		}
		if(this.hasHasTradeMark){
			this.hasTradeMark+=new String(ch,start,length);
		}
		if(this.hasProductClassification){
			this.productClassification+=new String(ch,start,length);			
		}
		if(this.hasTitle){
			this.title+=new String(ch,start,length);
		}
		if(this.hasUrl){
			this.url+=new String(ch,start,length);
		}
		if(this.hasIcon1){
			this.icon1+=new String(ch,start,length);
		}
		if(this.hasIcon2){
			this.icon2+=new String(ch,start,length);
		}
		if(this.hasColumn){
			this.column+=new String(ch,start,length);
		}
		if(this.hasOrder){
			this.order+=new String(ch,start,length);
		}
		if(this.hasSectionId){
			this.sectionId+=new String(ch,start,length);
		}		
	}

	public class Sequence{
		private String column;
		private String order;
		public String getColumn() {
			return column;
		}
		public void setColumn(String column) {
			this.column = column;
		}
		public String getOrder() {
			return order;
		}
		public void setOrder(String order) {
			this.order = order;
		}

	}
}