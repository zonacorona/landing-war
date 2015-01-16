package com.rackspace.cloud.api.beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.rackspace.cloud.api.Feed;
import com.rackspace.cloud.api.FeedSaxHandler;
import com.rackspace.cloud.api.Product;
import com.rackspace.cloud.api.ProductCategory;
import com.rackspace.cloud.api.ProductInfoSaxHandler;
import com.rackspace.cloud.api.Type;

/**
 * 
 * @author thu4404
 * This bean reads from this project's WEB-INF/productinfo.xml file, traverses through the nodes and reads each value, creating
 * Product Objects and putting them in one of the prdouctsCol*Map according to the sequence specified
 */
public class IndexBean {
	private ServletContext servletCtx;
	//This map contains the products categorized by their categoryOrder as the key
	//ProductCategory Objects contains all the products associated with a particular CategoryName 
	//and CategoryOrder
	private static Map<String, ProductCategory> categorizedProducts;	
	private static Map<String, Product>productsCol3Map;
	
	private static List<Feed> rssFeeds;
	private Long atomAllXmlPrevTimeStamp=null;
	private Long atomAllXmlCurrTimeStamp=null;
	
	public static final int MAX_FEEDS_DISPLAY=20;
	
	private InputStream productInfoXml;	
	
	//private List<Feeds>

	private List<InputStreamAndFileName> typeInnyStreams;

	private static Logger log = Logger.getLogger(IndexBean.class);
	
	//We should only display the MAX_FEEDS_DISPLAY latest Rss Feeds if the Tomcat/latest/webapps/feeds/atom-all.xml file exists
	public boolean shouldDisplayRssFeeds;

	public IndexBean(){

		if(null==categorizedProducts){
			synchronized(this){
				if(null==categorizedProducts){
					categorizedProducts=new TreeMap<String,ProductCategory>();
				}
			}
		}
		if(null==productsCol3Map){
			synchronized(this){
				if(null==productsCol3Map){
					productsCol3Map=new LinkedHashMap<String, Product>();
				}
			}
		}	
		if(null==IndexBean.rssFeeds){
			synchronized(this){
				if(null==IndexBean.rssFeeds){
					IndexBean.rssFeeds=new ArrayList<Feed>();
				}
			}
		}
		this.setRssFeedTimeStamps();
		
	}
	
	private Long getAtomAllTimeStamp(){
		String METHOD_NAME="getAtomAllTimeStamp()";
		
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
		}
		
		Long retVal=null;
		//we need to get the current time stamp of the feeds/atom-all.xml file
		String webappsPath=this.getWebappsPath();
		if(null!=webappsPath && !webappsPath.isEmpty()){
			String atomAllXmlPath=webappsPath+"feeds/atom-all.xml";
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": atomAllXmlPath="+atomAllXmlPath);
			}
			File atomAllXmlFile=new File(atomAllXmlPath);

			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": atomAllXmlFile.exists()="+atomAllXmlFile.exists());
			}
			if(atomAllXmlFile.exists()){
				retVal=atomAllXmlFile.lastModified();
			}
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: retVal="+retVal);
		}
		return retVal;
	}
	
	//This initializes the atomAllXmlPrevTimeStamp and atomAllXmlCurrTimeStamp
	private void setRssFeedTimeStamps(){
		String METHOD_NAME="setRssFeedTimeStamps()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
		}
		//atomAllXmlPrevTimeStamp has never been initialized before, set it the feeds/atom-all last modified timestamp
		if(null==this.atomAllXmlPrevTimeStamp){
			//first we set the first time stamp to the file's last modified
			this.atomAllXmlPrevTimeStamp=this.getAtomAllTimeStamp();	
			this.atomAllXmlCurrTimeStamp=-1L;
		}
		else{
			this.atomAllXmlCurrTimeStamp=this.getAtomAllTimeStamp();
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": this.atomAllXmlPrevTimeStamp="+this.atomAllXmlPrevTimeStamp);
			log.debug(METHOD_NAME+": this.atomAllXmlCurrTimeStamp="+this.atomAllXmlCurrTimeStamp);
			log.debug(METHOD_NAME+": END:");
		}
	}
	
	
	public String getWebappsPath(){
		String METHOD_NAME="getWebappsPath()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
			log.debug(METHOD_NAME+": (null!=this.servletCtx)="+(null!=this.servletCtx));
		}
		String retVal=null;
		
		if(null!=this.servletCtx){
			String webappPath=this.servletCtx.getRealPath("..");
			
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": webappPath="+webappPath);
			}
			if(null!=webappPath){
				if(!webappPath.endsWith("/")){
					webappPath+="/";
				}
				retVal=webappPath;
			}			
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: retVal="+retVal);
		}
		return retVal;
	}
	
	public boolean getShouldDisplayRssFeeds(){
		String METHOD_NAME="shoiuldDisplayRssFeeds()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
		}
		boolean retVal=false;
		
		String webappPath=this.getWebappsPath();
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": webappPath="+webappPath);
		}
		if(null!=webappPath && !webappPath.isEmpty()){
		    String atomAllXmlPath=webappPath+"feeds/atom-all.xml";
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": atomAllXmlPath="+atomAllXmlPath);
			}
			File atomAllXmlFile=new File(atomAllXmlPath);
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": atomAllXmlFile.exists()="+atomAllXmlFile.exists());
			}
			retVal=atomAllXmlFile.exists();			
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: retVal="+retVal);
		}		
		return retVal;
	}
	
	public List<Feed>getRssFeeds(){
		return this.loadRssFeeds();
	}
	
	//This will load the first IndexBean.MAX_FEEDS_DISPLAY feeds/atom-all.xml 
	private List<Feed>loadRssFeeds(){
		String METHOD_NAME="loadRssFeeds()";
		this.setRssFeedTimeStamps();
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
			log.debug(METHOD_NAME+": this.atomAllXmlCurrTimeStamp="+this.atomAllXmlCurrTimeStamp);
			log.debug(METHOD_NAME+": this.atomAllXmlPrevTimeStamp="+this.atomAllXmlPrevTimeStamp);
		}
		if(this.atomAllXmlCurrTimeStamp==-1 ||this.atomAllXmlCurrTimeStamp>this.atomAllXmlPrevTimeStamp){
			//This is the first time we have loaded, so set atomAllXmlPrevTimeStamp and atomAllXmlCurrTimeStamp
			//to the same value
			if(this.atomAllXmlCurrTimeStamp==-1){
				this.atomAllXmlCurrTimeStamp=this.atomAllXmlPrevTimeStamp;
			}
			//TODO need to switch this to a database value
			//First try the external 
			File atomAllXmlFile=new File("/home/docs/Tomcat/latest/webapps/feeds/atom-all.xml");
			if(!atomAllXmlFile.exists()){
				atomAllXmlFile=new File("/home/docs/Tomcat/internal/latest/webapps/feeds/atom-all.xml");
			}
			if(!atomAllXmlFile.exists()){
				atomAllXmlFile=new File("/Users/thu4404/Downloads/Tomcat/latest/webapps/feeds/atom-all.xml");
			}
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": atomAllXmlCurrTimeStamp="+atomAllXmlFile.exists());
			}
			if(atomAllXmlFile.exists()){
				//Rest the first time stamp to the new last modified value of feeds/atom-all.xml
				this.atomAllXmlPrevTimeStamp=this.atomAllXmlCurrTimeStamp;
				SAXParserFactory factory = SAXParserFactory.newInstance();
				
				//Now we have to load the rss Feed
				try {
					SAXParser saxParser = factory.newSAXParser();
					FeedSaxHandler rssFeed=new FeedSaxHandler();	
					
					//Initialize rssFeeds to empty List if it is null
					if(null==this.rssFeeds){
						this.rssFeeds=new ArrayList<Feed>();
					}
					else{
						FileInputStream inny=new FileInputStream(atomAllXmlFile);
						saxParser.parse(inny, rssFeed);
						List<Feed>entries=rssFeed.getEntriesList();
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": (null!=entries)="+(null!=entries));
							if(null!=entries){
								log.debug(METHOD_NAME+": (entries.size())="+(entries.size()));
							}
							log.debug(METHOD_NAME+":@@@@@@@@@@@@@@@@@@Entries Start:");
							for(Feed aFeed: entries){
								log.debug(aFeed);
							}
							log.debug(METHOD_NAME+":@@@@@@@@@@@@@@@@@@Entries END:");
						}
						if(null!=entries && entries.size()>0){

							int maxEntriesToDisplay=IndexBean.MAX_FEEDS_DISPLAY;
							//If there are less than IndexBean.MAX_FEEDS_DISPLAY number of 
							//entries to display in the entries List than just display the
							//available entries
							if(entries.size()<IndexBean.MAX_FEEDS_DISPLAY){
								maxEntriesToDisplay=entries.size();
							}
							synchronized (this.rssFeeds) {
								//Clear the list
								this.rssFeeds.clear();
								for(int i=0;i<maxEntriesToDisplay;++i){
									Feed aFeed=entries.get(i);
									this.rssFeeds.add(aFeed);
								}
							}
						}
					}					
				} 
				catch (ParserConfigurationException e) {
					e.printStackTrace();
				} 
				catch (SAXException e) {
					e.printStackTrace();
				}	
				catch(FileNotFoundException e){
					e.printStackTrace();
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		if(null==this.rssFeeds){
			this.rssFeeds=new ArrayList<Feed>();
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: rssFeeds.size()="+rssFeeds.size());
		}	
		return this.rssFeeds;
	}

	public void setTypeInputStreams(List<InputStreamAndFileName> innys){
		this.typeInnyStreams=innys;
	}

	public List<InputStreamAndFileName> getTypeInputStreams(){

		String METHOD_NAME="getTypeInputStreams()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: (null==this.typeInnyStreams && null!=this.servletCtx)="+
					(null==this.typeInnyStreams && null!=this.servletCtx));
		}
		//This part of the code can only be tested via integration since it needs the servlet container
		if(null==this.typeInnyStreams && null!=this.servletCtx){
			this.typeInnyStreams=new ArrayList<InputStreamAndFileName>();
			String webappPath=this.servletCtx.getRealPath("..");
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": webappPath="+webappPath);
			}
			File theFile=new File(webappPath);
			//Make sure that we are dealing with a directory
			if(theFile.isDirectory()){
				//Get all the files in the directory
				File[] allFiles=theFile.listFiles();
				if(allFiles!=null && allFiles.length>0){
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": allFiles.length="+allFiles.length);
					}
					for(File aFile:allFiles){
						//We are only interested in the subfolders
						if(aFile.isDirectory()){
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": aFile is a directory aFile.getName()="+aFile.getName());
							}
							//Check to see if a bookinfo.xml exists
							String bookInfoStr=aFile.getAbsoluteFile()+"/bookinfo.xml";
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": bookInfoStr="+bookInfoStr);
							}
							File bookInfoFile=new File(bookInfoStr);
							if(bookInfoFile.exists()){

								InputStreamAndFileName innyBook=null;
								try {
									FileInputStream bookInny=new FileInputStream(bookInfoFile);
									innyBook=new InputStreamAndFileName(bookInny,aFile.getName());
								} 
								catch (FileNotFoundException e) {
									innyBook=null;
									e.printStackTrace();
								}
								if(null!=innyBook){
									this.typeInnyStreams.add(innyBook);
								}
							}
						}
					}
				}
			}
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END:");
		}
		return this.typeInnyStreams;
	}

	public InputStream getProductInfoXML()throws FileNotFoundException{
		//producInfo.xml is the master list that should contain all registered products
		//it is possible that another .war project may have a product with a product id that is not
		//configured in productInfo.xml. In such cases, if the product is not properly configured 
		//in the respective bookinfo.xml in the corresponding webapp, the product will appear without
		//a title nor icon. Product ID in the bookinfo.xml that clashes with existing product ID's in the
		//productInfo.xml will also cause unexpected behavior and must be avoided
		if(null==this.productInfoXml && this.servletCtx!=null){
			this.productInfoXml=new FileInputStream(servletCtx.getRealPath("/")+"WEB-INF/productInfo.xml");
		}
		return this.productInfoXml;
	}

	//This is used for JUnit mockito testing
	public void setProductInfoXML(InputStream productInfoXml){
		this.productInfoXml=productInfoXml;
	}

	public IndexBean(ServletContext servCtx){
		this();
		this.servletCtx=servCtx;		
	}


	public Map<String, ProductCategory>getCategorizedProducts(){
		if(IndexBean.categorizedProducts.size()==0){
			this.loadProducts();
		}
		return IndexBean.categorizedProducts;
	}

	public void setServletCtx(ServletContext ctx){
		this.servletCtx=ctx;
	}


	//This reads through the productinfo.xml to load all the products. Note the landing page will only load the product if the respective
	//typesList for a given product has at least one entry. The typeList is in each respective bookinfo.xml file in each project folder
	public void loadProducts(){
		String METHOD_NAME="loadProducts()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: IndexBean.categorizedProducts.size()="+IndexBean.categorizedProducts.size()+
					" productsCol3Map.size()="+productsCol3Map.size());
		}
		if(null!=this.servletCtx){

			SAXParserFactory factory = SAXParserFactory.newInstance();
			try {
				SAXParser saxParser = factory.newSAXParser();				
				ProductInfoSaxHandler prodSax=new ProductInfoSaxHandler();

				saxParser.parse(this.getProductInfoXML(), prodSax);

				List<Product>productsListFromProdInfoXML=prodSax.getProductsList();

				List<Product>sdksAndResourcesList=new ArrayList<Product>();

				for(Product aProduct:productsListFromProdInfoXML){
					//iterate and get values
					String categoryOrder=aProduct.getCategoryOrder();
					String categoryName=aProduct.getCategoryName();
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": aProduct.getId()="+aProduct.getId()+
								" aProduct.getProductName="+aProduct.getProductName()+
								" aProduct.getCategoryName()="+aProduct.getCategoryName()+
								" aProduct.getProductName()="+aProduct.getProductName()+
								" aProdcut.getSectionId()="+aProduct.getSectionId()+
								" aProduct.getCategoryOrder()="+aProduct.getCategoryOrder());
					}
					//This product has categoryOrder, products: SDKs and Resources do not have 
					//categoryOrder nor categoryName
					if(null!=categoryOrder && !categoryOrder.isEmpty()){
						ProductCategory prodCat=null;
						//This is the first time we get to this categoryOrder
						if(!IndexBean.categorizedProducts.containsKey(categoryOrder)){
							//We need to instantiate a new ProductCategory
							//The productIds Set<String> is automatically instantiated in the default constructor
							//The productsMap Map<String is automatically instantiated in the default constructor							
							//productsList List<Product> is automatically instantiated in the default constructor
							prodCat=new ProductCategory();
							prodCat.setCategoryName(categoryName);
							prodCat.setCategoryOrder(categoryOrder);

							//prodCat.addProductId(aProduct.getId());

							//TODO prob dont need a Map prodCat.setProductsMap(new );				    	
							IndexBean.categorizedProducts.put(categoryOrder, prodCat);
						}
						//prodCat should not be null at this point
						prodCat=IndexBean.categorizedProducts.get(categoryOrder);
						this.loadAProdIntoSortedArray(prodCat, aProduct);						
					}
					else{
						loadAProdIntoSortedArray(sdksAndResourcesList,aProduct);
					}
				}

				//Now we have to add 
				synchronized(this){
					productsCol3Map.clear();

					//Add the product in order to the productMap
					for(Product aProd:sdksAndResourcesList){
						productsCol3Map.put(aProd.getId(), aProd);
					}

					//Now we have to load all the products in the sorted Arrays into their respective Maps
					loadProductsFromSortedArrayToMap();

					//Now load all the types, NOTE this could also potentially load Products that are not configured
					//in the productsinfo.xml in the ROOT app, but are configured in an indvidual bookinfo.xml
					//At this point we should have all the productId's loaded from the productInfo.xml. All the 
					//product id's read from subsequent bookinfo.xml should have already been loaded from the 
					//productinfo.xml.
					this.loadTypes();
				}								
			} 
			catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch(IOException e1){
				e1.printStackTrace();
			}


		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: IndexBean.categorizedProducts.size()="+IndexBean.categorizedProducts.size()+
					" productsCol3Map.size()="+productsCol3Map.size());
		}
	}

	private void loadProductsFromSortedArrayToMap(){
		String METHOD_NAME="loadProductsFromSortedArrayToMap()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": this.categorizedProducts.size()="+this.categorizedProducts.size());
		}
		//Iterate through all the ProductCategory's in the categorizedProducts Map which is keyed by
		//CategoryOrder
		for(ProductCategory aProdCat:this.categorizedProducts.values()){
			Map<String, Product>productsMap=aProdCat.getProductsMap();
			List<Product>prodsList=aProdCat.getProductsList();
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": prodsList.size()="+prodsList.size());
			}
			for(Product aProd:prodsList){
				productsMap.put(aProd.getId(), aProd);
			}
		}

		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END:");
		}

	}

	public Map<String, Product> getProductsCol3Map() {
		if(productsCol3Map.size()==0){
			this.loadProducts();
		}
		return productsCol3Map;
	}

	public void setProductsCol3Map(Map<String, Product> productsCol3Map) {
		IndexBean.productsCol3Map = productsCol3Map;
	}

	public ServletContext getServletCtx() {
		return servletCtx;
	}

	public static void setCategorizedProducts(Map<String, ProductCategory> categorizedProducts) {
		IndexBean.categorizedProducts = categorizedProducts;
	}

	private List<Product> loadAProdIntoSortedArray(List<Product>prodsList, Product aNewProduct){
		String METHOD_NAME="loadAProdIntoSortedArray(List<Products>,Product)";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
		}
		List<Product>retVal;
		retVal=prodsList;
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": retVal="+retVal);
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": retVal.size()="+retVal.size());
		}
		int insertIndex=0;
		//The product list is empty, just add the new product
		if(retVal.size()==0){
			retVal.add(aNewProduct);											
		}
		//There are existing products in the list, add the new product in sorted order
		else{
			Integer newProdOrder=Integer.valueOf(aNewProduct.getOrder());
			for(Product aProd:retVal){
				Integer aProdOrder=Integer.valueOf(aProd.getOrder());
				//if((aNewProduct.compareTo(aProd)==-1)||(aNewProduct.compareTo(aProd)==0)){
				if(newProdOrder<=aProdOrder){													
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": insertIndex="+insertIndex);
					}
					break;
				}
				++insertIndex;
			}
			if(insertIndex>retVal.size()){
				insertIndex=retVal.size();
			}
			retVal.add(insertIndex,aNewProduct);											
		}	
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: retVal.size()="+retVal.size());
		}
		return retVal;
	}

	private void loadAProdIntoSortedArray(ProductCategory prodCat, Product aNewProduct){
		String METHOD_NAME="loadAProdIntoSortedArray(ProductCategory, Product)";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START aNewProduct="+aNewProduct);
		}			
		List<Product>prodsList=prodCat.getProductsList();
		prodsList=loadAProdIntoSortedArray(prodsList,aNewProduct);
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: prodsList.size()="+prodsList.size());
		}
	}

	//This is called after all the productsCol*Map's have loaded products from the productinfo.xml file. This method can
	//also potentially load products with with novel productid's not encountered from the productinfo.xml.
	//It iterates through all the webapps folder on the server and looks at the bookinfo.xml file
	private void loadTypes(){
		String METHOD_NAME="loadTypes()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": ENTER");
		}
		//Get all the bookinfo.xml's in all the different webapps folder
		//Remember each bookinfo.xml is in the root of each respective web
		this.typeInnyStreams=this.getTypeInputStreams();
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": typeInnyStreams.size()="+typeInnyStreams.size());
		}
		for(InputStreamAndFileName anInny:this.typeInnyStreams){

			BufferedReader r = null;
			InputStreamReader innyReader;

			try{
				innyReader=new InputStreamReader(anInny.getInny());
				r=new BufferedReader(innyReader);
				StringBuffer anXmlStrBuff=new StringBuffer("");
				String line=null;
				while(null!=(line=r.readLine())){
					anXmlStrBuff.append(line);
					anXmlStrBuff.append("\n");
				}
				if(log.isDebugEnabled()){
					log.debug("loadProductEntries(): finished reading file");
				}
				r.close();
				innyReader.close();
				this.getTypesForABookInfoXML(anXmlStrBuff.toString(),anInny.getFileName());
			}
			catch(FileNotFoundException e){
				e.printStackTrace();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}

		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": EXIT");
		}
	}

	private void getTypesForABookInfoXML(String anXmlStr, String folderName){
		String METHOD_NAME="getTypesForAnXML()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": ENTER: folderName="+folderName);
			//log.debug("anXmlStr="+anXmlStr);
		}
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(anXmlStr));
			Document doc=dBuilder.parse(is);
			doc.getDocumentElement().normalize();

			NodeList products=doc.getElementsByTagName("product");
			if(null!=products){
				for(int i=0; i<products.getLength();++i){
					Node aNode=products.item(i);
					if(aNode.getNodeType() == Node.ELEMENT_NODE){
						Element aProduct = (Element)aNode;

						String prodId="";
						NodeList prodIdNodes=aProduct.getElementsByTagName("id");
						if(null!=prodIdNodes){
							Node idNode=prodIdNodes.item(0);
							if(null!=idNode){
								NodeList children=idNode.getChildNodes();
								if(null!=children){				        		
									Node node=children.item(0);									
									if(null!=node){
										prodId=node.getNodeValue();
									}
								}
							}
						}
						//We now have the id of the product, we need to check the Categorized Products for a Categorized Product
						//that contains the productId, if we can't find one then, we just skip and log an error
						ProductCategory theProdCat=this.getProdCatByProdId(prodId);
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+":((null!=theProdCat)="+((null!=theProdCat)));
							log.debug(METHOD_NAME+":((productsCol3Map.containsKey(\""+prodId+"\"))="+
									((productsCol3Map.containsKey(prodId))));
						}						
						if(null!=theProdCat || productsCol3Map.containsKey(prodId)){
							NodeList typesNodes=aProduct.getElementsByTagName("types");
							if(null!=typesNodes){
								if(typesNodes.item(0).getNodeType() == Node.ELEMENT_NODE){
									Element typesElement=(Element)typesNodes.item(0);
									NodeList typesNodeList=typesElement.getElementsByTagName("type");
									if(null!=typesNodeList){
										for(int x=0;x<typesNodeList.getLength();++x){
											Element aTypeElement=(Element)typesNodeList.item(x);

											String typeId=aTypeElement.getElementsByTagName("id").item(0).getChildNodes().item(0).getNodeValue();
											String typeName=aTypeElement.getElementsByTagName("displayname").item(0).getChildNodes().item(0).getNodeValue();
											String typeUrl=aTypeElement.getElementsByTagName("url").item(0).getChildNodes().item(0).getNodeValue();
											String sequence=aTypeElement.getElementsByTagName("sequence").item(0).getChildNodes().item(0).getNodeValue();

											Type aNewType=new Type(prodId,typeId,typeName,typeUrl,sequence, folderName);
											if(log.isDebugEnabled()){
												log.debug(METHOD_NAME+":typeName="+typeName);
												log.debug(METHOD_NAME+":prodId="+prodId);
											}											
											if(null!=theProdCat && theProdCat.containsProductId(prodId)){
												if(log.isDebugEnabled()){
													List<Product>prods=theProdCat.getProductsList();
													for(Product aProd:prods){
														log.debug(METHOD_NAME+": aProd="+aProd);
													}
												}											
												this.loadTypesForProduct(theProdCat, prodId, typeName, aNewType);
											}
											else if(productsCol3Map.containsKey(prodId)){
												if(log.isDebugEnabled()){
													log.debug(METHOD_NAME+":Loading types for prodId="+prodId);
												}
												this.loadTypesForProduct(productsCol3Map, prodId, typeName, aNewType);											
											}
											//This is a new product we have to try to get some product information
											//and add the product key to one of the maps
											else{
												Product aNewProd=getAProduct(aProduct);

												if(null!=aNewProd){
													List<Type> typesList=aNewProd.getTypesList();
													Map<String,String>typesMap=aNewProd.getTypesMap();
													typesMap.put(typeName, typeName);
													insertTypeIntoList(typesList,aNewType);
													String column=aNewProd.getColumn();
													if(null!=column && !column.isEmpty()){
														if(column.equals("3")){
															productsCol3Map.put(aNewProd.getId(), aNewProd);
														}
													}
												}
											}
										}
									}
								}
							}
						}
						else{
							log.error(METHOD_NAME+": prodId: "+prodId+" cannot be found in any of the Maps, this bookinfo.xml is in: "+folderName);
						}
					}
				}				
			}
		}
		catch (ParserConfigurationException e) {
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": ParserConfigurationException caught");
			}
			e.printStackTrace();
		}
		catch(IOException e){
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": IOException caught");
			}
			e.printStackTrace();
		}
		catch(SAXException e){
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": SAXException caught");
			}
			e.printStackTrace();
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": EXIT:");
		}
	}	

	//This iterates through the IndexBean.categorizedProducts Map which is keyed by the CategoryOrder, and
	//checks each respective ProductCategory.productsMap. ProductCategory.productsMap contains all the products
	//associated with a CategoryOrder/CategoryName keyed by the productId.
	private ProductCategory getProdCatByProdId(String id){
		ProductCategory retVal=null;
		if(null!=IndexBean.categorizedProducts){
			Set<String>keys=IndexBean.categorizedProducts.keySet();
			for(String aKey:keys){
				ProductCategory aProdCat=IndexBean.categorizedProducts.get(aKey);
				if(aProdCat.containsProductId(id)){
					retVal=aProdCat;
					break;
				}
			}
		}
		return retVal;
	}

	private void loadTypesForProduct(ProductCategory prodCat, String prodId, String typeName, Type aNewType){
		String METHOD_NAME="loadTypesForProduct(ProductCategory, String,Type)";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
		}
		Map<String, Product>prodsMap=prodCat.getProductsMap();
		if(!prodsMap.containsKey(prodId)){
			for(Product aProd:prodCat.getProductsList()){
				if(aProd.getId().equals(prodId)){
					prodsMap.put(prodId, aProd);
					break;
				}
			}
		}
		this.loadTypesForProduct(prodsMap, prodId, typeName, aNewType);
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END:");
		}
	}

	private void loadTypesForProduct(Map<String, Product>prodsMap, String prodId, String typeName, Type aNewType){
		String METHOD_NAME="loadTypesForProduct()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START: prodsMap="+prodsMap+" prodId="+prodId+" typeName="+
					typeName+" aNewType="+aNewType);
		}		
		Map<String,String>typesMap=(prodsMap.get(prodId)).getTypesMap();
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+"!typesMap.containsKey("+typeName+")="+(!typesMap.containsKey(typeName)));
		}
		if(!typesMap.containsKey(typeName)){
			typesMap.put(typeName, typeName);
			List<Type> typesList=(prodsMap.get(prodId)).getTypesList();
			insertTypeIntoList(typesList,aNewType);
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": inserting into prodsMap: aNewType="+aNewType.toString());
			}
		}		
	}

	private void insertTypeIntoList(List<Type>typesList, Type aNewType){
		String METHOD_NAME="insertTypeIntoList()";
		if(log.isDebugEnabled()  && aNewType.getProdId().equalsIgnoreCase("cloudfiles-v1.0")){
			log.debug(METHOD_NAME+": START:");
			if(null!=typesList){
				log.debug(METHOD_NAME+": typesList.size()="+typesList.size()+" aNewType="+aNewType);
			}
			log.debug(METHOD_NAME+": adding to the typesList");
		}
		//Add the type in order
		if(typesList.size()==0){
			if(log.isDebugEnabled() && aNewType.getProdId().equalsIgnoreCase("cloudfiles-v1.0")){
				log.debug(METHOD_NAME+": typesList is empty just add aNewType to typesList");
			}
			typesList.add(aNewType);
		}
		else{
			int insertIndex=0;	
			if(log.isDebugEnabled() && aNewType.getProdId().equalsIgnoreCase("cloudfiles-v1.0")){
				log.debug(METHOD_NAME+": ~~~~~~~~~~~~~~~~~loop START aNewType.toString()="+aNewType.toString());
			}
			for(int n=0;n<typesList.size();++n){
				insertIndex=n;
				Type aTypeFromList=typesList.get(n);
				if(log.isDebugEnabled() && aNewType.getProdId().equalsIgnoreCase("cloudfiles-v1.0")){
					log.debug(METHOD_NAME+": ~~~~~~~~~~~~~~~~~aTypeFromList="+aTypeFromList+" @@@@@@@@aNewType="+aNewType);
					log.debug(METHOD_NAME+": ~~~~~~~~~~~~~~~~~aNewType.compareTo(aTypeFromList)="+aNewType.compareTo(aTypeFromList));
					log.debug(METHOD_NAME+": ~~~~~~~~~~~~~~~~~insertIndex="+insertIndex);
				}
				if((aNewType.compareTo(aTypeFromList)==-1)||(aNewType.compareTo(aTypeFromList)==0)){														
					//					if(insertIndex>1){
					//						--insertIndex;
					//					}
					break;
				}
				else if(insertIndex==(typesList.size()-1)){
					insertIndex=typesList.size();													
				}				
			}
			if(log.isDebugEnabled()  && aNewType.getProdId().equalsIgnoreCase("cloudfiles-v1.0")){
				log.debug(METHOD_NAME+": ~~~~~~~~~~~~~~~~~loop END insertIndex="+insertIndex);
			}
			typesList.add(insertIndex, aNewType);
		}	
		if(log.isDebugEnabled() && aNewType.getProdId().equalsIgnoreCase("cloudfiles-v1.0")){
			log.debug(METHOD_NAME+": END:");
		}
	}

	private String getNodeString(String tagName, Element aProduct){
		String tagNameVal="";
		NodeList tagNameNodes=aProduct.getElementsByTagName(tagName);
		if(null!=tagNameNodes){
			Node tagNameNode=tagNameNodes.item(0);
			if(null!=tagNameNode){
				NodeList children=tagNameNode.getChildNodes();
				if(null!=children){
					Node node=children.item(0);
					if(null!=node){
						tagNameVal=node.getNodeValue();
					}
				}
			}
		}
		return tagNameVal;
	}

	private Product getAProduct(Element aProduct){
		String METHOD_NAME="getAProduct()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
		}
		Product retVal=null;
		if(null!=aProduct){
			String id=getNodeString("id",aProduct);
			String productLogo=getNodeString("productLogo",aProduct);			
			String productClassification=getNodeString("productClassification",aProduct);	
			String column="";
			String order="";
			NodeList sequenceNodes=aProduct.getElementsByTagName("sequence");
			if(null!=sequenceNodes){
				for(int x=0;x<sequenceNodes.getLength();++x){
					Node aSequenceNode=sequenceNodes.item(x);
					if(aSequenceNode.getNodeType() == Node.ELEMENT_NODE){
						Element aSequence=(Element)aSequenceNode;
						NodeList colNodes=aSequence.getElementsByTagName("column");
						if(null!=colNodes){
							Node colNode=colNodes.item(0);
							if(null!=colNode){
								NodeList children=colNode.getChildNodes();
								if(null!=children){
									Node node=children.item(0);
									if(null!=node){
										column=node.getNodeValue();
									}
								}
							}
						}
						NodeList orderNodes=aSequence.getElementsByTagName("order");
						if(null!=orderNodes){
							Node orderNode=orderNodes.item(0);
							if(null!=orderNode){
								NodeList children=orderNode.getChildNodes();
								if(null!=children){
									Node node=children.item(0);
									if(null!=node){
										order=node.getNodeValue();
									}
								}
							}
						}
					}
				}
			}
			String name=getNodeString("name",aProduct);	
			String supplementalHtmlDisplayName="";
			String supplementalUrl="";

			NodeList supplementalNodes=aProduct.getElementsByTagName("supplementalHtml");
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": ~~~~~~~~~~~~~trying to get supplemetalHtml values");
			}
			if(null!=supplementalNodes){
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": ~~~~~~~~~~~~~supplementalNodes.getLength()="+supplementalNodes.getLength());
				}				
				for(int i=0;i<supplementalNodes.getLength();++i){
					Node supplementalNode=supplementalNodes.item(i);
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": ~~~~~~~~~~~~~i="+i);
					}	
					if(null!=supplementalNode && supplementalNode.getNodeType() == Node.ELEMENT_NODE){
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": supplementalNode.getNodeType()"+supplementalNode.getNodeType()+" Node.ELEMENT_NODE="+Node.ELEMENT_NODE);
						}
						Element supplementalElement=(Element)supplementalNode;

						NodeList supplementalDisplyNodes=supplementalElement.getElementsByTagName("display");
						if(null!=supplementalDisplyNodes){
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": supplementalDisplyNodes.getLength()="+supplementalDisplyNodes.getLength());
							}
							Node supplementalDisplayNode=supplementalDisplyNodes.item(0);
							if(null!=supplementalDisplayNode){
								NodeList children=supplementalDisplayNode.getChildNodes();
								if(null!=children){
									Node node=children.item(0);
									if(null!=node){
										supplementalHtmlDisplayName=node.getNodeValue();
									}
								}
							}
						}
						NodeList supplementalUrlNodes=supplementalElement.getElementsByTagName("supplementalUrl");
						if(null!=supplementalUrlNodes){
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+": supplementalUrlNodes.getLength()="+supplementalUrlNodes.getLength());
							}
							Node supplementalUrlNode=supplementalUrlNodes.item(0);
							if(null!=supplementalUrlNode){
								NodeList children=supplementalUrlNode.getChildNodes();
								if(null!=children){
									Node node=children.item(0);
									if(null!=node){
										supplementalUrl=node.getNodeValue();
									}
								}
							}
						}
						break;
					}
				}
			}
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+":  @@@@@@@@@@supplementalHtmlDisplayName="+supplementalHtmlDisplayName);
				log.debug(METHOD_NAME+":  @@@@@@@@@@supplementalUrl="+supplementalUrl);
			}
			String title=getNodeString("title",aProduct);	

			boolean hasTradeMark=false;
			String hasTradeMarkStr=getNodeString("hasTradeMark",aProduct);
			if(null!=hasTradeMarkStr && !hasTradeMarkStr.trim().isEmpty()){
				hasTradeMark=Boolean.parseBoolean(hasTradeMarkStr);
			}								
			String icon1=getNodeString("icon1",aProduct);
			String icon2=getNodeString("icon2",aProduct);
			String prodUrl=getNodeString("url",aProduct);
			String sectionId=getNodeString("sectionId",aProduct);

			retVal=new Product();
			retVal.setId(id);
			retVal.setProductClassification(productClassification);
			retVal.setProductLogo(productLogo);
			retVal.setProductName(name);
			retVal.setTitle(title);
			retVal.setUrl(prodUrl);
			retVal.setIcon1Url(icon1);
			retVal.setIcon2Url(icon2);
			retVal.setColumn(column);
			retVal.setOrder(order);
			retVal.setSectionId(sectionId);
			retVal.setHasTradeMark(hasTradeMarkStr);
			retVal.setHasHasTradeMark(hasTradeMark);
			retVal.setSupplementalDisplayName(supplementalHtmlDisplayName);
			retVal.setSupplementalUrl(supplementalUrl);
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END: retVal="+retVal);
		}
		return retVal;

	}



}
