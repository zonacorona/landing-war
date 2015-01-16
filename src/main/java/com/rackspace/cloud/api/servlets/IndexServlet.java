package com.rackspace.cloud.api.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.rackspace.cloud.api.beans.IndexBean;

/**
 * Servlet implementation class IndexServlet
 */
public class IndexServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(IndexServlet.class);
	private String footer;
	private String header;

	//Map<String,Type>types;


	Map<String,String>actionOutputs;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IndexServlet() {
		super();
		this.footer=new String("");
		this.header=new String("");
		this.actionOutputs=new HashMap<String,String>();
		//this.types=new LinkedHashMap<String, Type>();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
         * This handles requests to http://localhost:8080/IndexServlet?refresh=true which loads all the products and types again
         * Each of the docbooks make a rest call to this url when they are initialized 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String METHOD_NAME="doGet()";
		String refreshParam=request.getParameter("refresh");
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": %%%%%%%%%%%%%%%% doGet() START: refreshParam="+refreshParam+" START:");
		}
		String message="<message>Loaded products from bean</message>";
		if(null!=refreshParam && refreshParam.equalsIgnoreCase("true")){
			IndexBean aBean=new IndexBean();
			aBean.loadProducts();
			PrintWriter outty = response.getWriter();
			response.setContentType("text/xml");
			outty.println("<xml version='1.0' encoding='ISO-8859-1?' ");			
			outty.println("message");
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": %%%%%%%%%%%%%%%% doGet() START: refreshParam="+refreshParam+" message="+message+"END: ");
		}


//		if(log.isDebugEnabled()){
//			log.debug("doGet(): Enter");
//		}
//		PrintWriter outty = response.getWriter();
//		response.setContentType("text/json");
//		String action = request.getParameter("action");
//
//		IndexBean indexBean = new IndexBean(getServletContext());
//		List<Product>products=indexBean.getProds();
//		if(null!=products && products.size()>0){			    
//
//
//			if(null!=action && !action.isEmpty()){
//				String retAction=processAction(action, indexBean);
//				outty.println(retAction);
//			}
//			else{
//				String headerFooter=request.getParameter("headerfooter");
//				if(null!=headerFooter&&!headerFooter.isEmpty()){
//					//This is a header request
//					if(headerFooter.equals("1")){
//						String theHeader=processHeader(indexBean);
//						outty.println(theHeader);
//					}
//					//This is a footer request
//					else if(headerFooter.equals("2")||headerFooter.equals("3")){
//						String theFooter=processFooter();
//						outty.println(theFooter);
//					}
//				}
//				else{
//					
//					String refresh=request.getParameter("refresh");
//					this.actionOutputs.clear();										
//					try{
//						if(log.isDebugEnabled()){
//							log.debug("doGet():calling reloadAll() refresh="+refresh);
//						}
//						indexBean.reloadAll();
//						this.processAction("1", indexBean);
//						outty.print("success");
//					}
//					catch(Throwable e){
//						if(log.isDebugEnabled()){
//							log.debug("doGet(): unknown exception caught: e.getMessage()="+e.getMessage());
//						}
//						e.printStackTrace();
//						outty.print("failed");
//					}
//				}
//			}
//		}
//		else{
//			outty.println("{}");
//		}
//		if(log.isDebugEnabled()){
//			log.debug("doGet(): Exit");
//		}
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

//	private Map<String,Map<String,Vector<ProductEntry>>> getProdEntriesByType(IndexBean indexBean){
//		if(this.prodEntriesByTypes.isEmpty()){	
//			synchronized(this.prodEntriesByTypes){
//				if(this.prodEntriesByTypes.isEmpty()){
//					this.loadProdEntriesByType(indexBean);
//				}
//			}
//		}
//		return this.prodEntriesByTypes;
//	}
//
//	private void loadProdEntriesByType(IndexBean indexBean){
//		List<Type>types=indexBean.getTypes();
//		List<ProductEntry>prodEntries=indexBean.getProdEntries();
//		indexBean.getProductEntriesForProdsCategorizedByTypes();
//		List<Product>prods=indexBean.getProds();
//
//		//Iterate through all the types
//		for(int i=0;i<types.size();++i){
//			Type aType=types.get(i);
//			String typeId=aType.getId();
//			this.types.put(typeId, aType);
//			Map<String, Vector<ProductEntry>>prodEntriesByProd=new LinkedHashMap<String, Vector<ProductEntry>>();
//			this.prodEntriesByTypes.put(typeId, prodEntriesByProd);
//
//			//Iterate through all the products, we need to do this because we have to keep the order
//			//that the products appear in the productInfo.xml file
//			for(int x=0;x<prods.size();++x){
//				Product aProd=prods.get(x);
//				String aProdId=aProd.getId();
//				Vector<ProductEntry>theProdEntries=new Vector<ProductEntry>();
//				//put the Vector of product entries keyed by the product id into the map
//				prodEntriesByProd.put(aProdId, theProdEntries);
//
//				//Now iterate through all the productentries
//				for(int y=0;y<prodEntries.size();++y){
//					//We are only interested in the product entries that match the current type and product
//					ProductEntry aProdEntry=prodEntries.get(y);
//					String aProdEntryProdId=aProdEntry.getProdId();
//					String aProdEntryTypeId=aProdEntry.getTypeId();
//					if(aProdEntryProdId.equals(aProdId) && (aProdEntryTypeId.equals(typeId))){			    			
//
//							theProdEntries=prodEntriesByProd.get(aProdEntryProdId);
//							//Add the product entry in sequence order
//							int insertIndex=0;
//							//Need to add to the Vector sorted by the sequence
//							for(int i2=0;i2<theProdEntries.size();++i2){
//								insertIndex=i2;
//								ProductEntry prodEntryFromList=theProdEntries.get(i2);
//								int compared=aProdEntry.compareTo(prodEntryFromList);
//								if(compared==-1||compared==0){
//									if(insertIndex>0){
//										--insertIndex;
//									}
//									break;
//								}
//								else if(insertIndex==(theProdEntries.size()-1)){
//									insertIndex=theProdEntries.size();
//								}
//							}
//							//add theProdEntry to the Vector sorted by the sequence
//							theProdEntries.insertElementAt(aProdEntry, insertIndex);													
//					}
//				}		    	
//			}
//		}	
//	}
//
//
//	private String processHeader(IndexBean indexBean){		
//		if(log.isDebugEnabled()){
//			log.debug("processHeader(): Enter: this.header.isEmpty()="+this.header.isEmpty());
//		}
//		if(this.header.isEmpty()){
//
//			//Make sure we have all the product entries keyed by type
//			this.getProdEntriesByType(indexBean);
//
//			//The header has no value, so at this time we need to make sure
//			//that no request will get an empty header
//			try{
//				JSONObject headerJSONObj=new JSONObject();
//				JSONArray headerJSONArr=new JSONArray();
//				headerJSONObj.put("header", headerJSONArr);
//
//				Map<String,Product>productsMap=indexBean.getProductsMap();
//
//				Set<String>keys=this.prodEntriesByTypes.keySet();
//
//				for(String aKey:keys){
//					Map<String,Vector<ProductEntry>>prodEntries=this.prodEntriesByTypes.get(aKey);
//					
//					Type aType=this.types.get(aKey);
//
//					JSONObject aTopLevelJSONObj=new JSONObject();
//					headerJSONArr.put(aTopLevelJSONObj);
//					JSONArray prodsJSONArr=new JSONArray();
//					aTopLevelJSONObj.put("headerid", aType.getId());
//					aTopLevelJSONObj.put("headerurl", aType.getUrl());
//					aTopLevelJSONObj.put("headername", aType.getDisplayName());
//					aTopLevelJSONObj.put("prods",prodsJSONArr);
//
//					Set<String>prodKeys=prodEntries.keySet();
//					for(String aProdKey:prodKeys){
//
//						Product aProd=productsMap.get(aProdKey);
//						Vector<ProductEntry>theProdEntries=prodEntries.get(aProdKey);
//
//						if(null!=theProdEntries && theProdEntries.size()>0){
//							JSONObject aProdJSONObj=new JSONObject();
//							prodsJSONArr.put(aProdJSONObj);
//							JSONArray prodEntriesJSONArr=new JSONArray();
//							aProdJSONObj.put("prodentries", prodEntriesJSONArr);
//							aProdJSONObj.put("produrl", aProd.getUrl());
//							aProdJSONObj.put("producttitle",aProd.getTitle());
//							
//							for(ProductEntry aProdEntry:theProdEntries){
//								String displayName=aProdEntry.getTypedisplayname();
//								displayName=displayName.replaceAll("\n", " ");
//								displayName=displayName.replaceAll("\r", " ");						
//
//								JSONObject aProdEntryJSONObj=new JSONObject();
//								prodEntriesJSONArr.put(aProdEntryJSONObj);
//								aProdEntryJSONObj.put("typeurl",aProdEntry.getTypeurl());
//								aProdEntryJSONObj.put("typedisplayname",displayName);
//							}
//						}
//					}
//				}
//				synchronized(this.header){
//					if(this.header.isEmpty()){
//						this.header=headerJSONObj.toString();
//					}
//				}
//			}
//			catch(JSONException e){
//				e.printStackTrace();
//			}
//		}
//		if(log.isDebugEnabled()){
//			log.debug("processHeader(): Exit: ");
//		}
//		return this.header;
//	}

	private String processFooter(){		
		if(log.isDebugEnabled()){
			log.debug("loadFooter() Enter:");		
		}
		if(this.footer.isEmpty()){
			File footerXML=new File(super.getServletContext().getRealPath("/")+"WEB-INF/rax-footer.xml");
			JSONObject retJSONObj=new JSONObject();
			if(footerXML.exists()){

				//File productInfoXML=new File(this.servletCtx.getRealPath("/")+"WEB-INF/typeinfo.xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				try {
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc=dBuilder.parse(footerXML);
					doc.getDocumentElement().normalize();

					NodeList footerNodeList=doc.getElementsByTagName("top");
					JSONObject footerJSONObj=new JSONObject();
					retJSONObj.put("footer", footerJSONObj);					
					if(null!=footerNodeList && footerNodeList.getLength()>0){	
						
						for(int i=0; i<footerNodeList.getLength();++i){
							Node aNode=footerNodeList.item(i);						
							if(aNode.getNodeType() == Node.ELEMENT_NODE){

								Element theTop = (Element)aNode;
								NodeList titleNodeList=theTop.getElementsByTagName("title");
								String titleStr=getTagValue(titleNodeList);
								footerJSONObj.put("toptitle", titleStr);								
								NodeList linksTags=theTop.getElementsByTagName("links");
								if(null!=linksTags && linksTags.getLength()>0){

									JSONArray topLinksJSONArr=new JSONArray();
									footerJSONObj.put("toplinks", topLinksJSONArr);
									for(int x=0;x<linksTags.getLength();++x){
										Node aLinkNode=linksTags.item(x);
										if(aLinkNode.getNodeType()==Node.ELEMENT_NODE){

											Element linksElement=(Element)aLinkNode;
											NodeList linkTags=linksElement.getElementsByTagName("link");

											if(null!=linkTags && linkTags.getLength()>0){
												for(int z=0;z<linkTags.getLength();++z){
													Node linkNode=linkTags.item(z);
													if(linkNode.getNodeType()==Node.ELEMENT_NODE){
														Element linkElement=(Element)linkNode;
														NodeList idNodeList=linkElement.getElementsByTagName("id");
														NodeList urlNodeList=linkElement.getElementsByTagName("url");
														String idStr=getTagValue(idNodeList);
														String urlStr=getTagValue(urlNodeList);

														JSONObject aTopLink=new JSONObject();
														topLinksJSONArr.put(aTopLink);
														aTopLink.put("url",urlStr);
														aTopLink.put("id", idStr);
													}
												}
											}
										}
									}
								}
							}
						}
					}
					NodeList noImgColNodeList=doc.getElementsByTagName("noimgcols");
					if(null!=noImgColNodeList && noImgColNodeList.getLength()>0){

						JSONArray noImgColsJSONArr=new JSONArray();
						footerJSONObj.put("noimgcols", noImgColsJSONArr);
						for(int i=0;i<noImgColNodeList.getLength();++i){
							Node aNode=noImgColNodeList.item(i);
							if(aNode.getNodeType()==Node.ELEMENT_NODE){
								Element noImgColsElement=(Element)aNode;
								NodeList noImgColList=noImgColsElement.getElementsByTagName("noimgcol");

								if(null!=noImgColList && noImgColList.getLength()>0){
									for(int x=0;x<noImgColList.getLength();++x){
										Node noImgColnode=noImgColList.item(x);
										if(noImgColnode.getNodeType()==Node.ELEMENT_NODE){

											Element noImgElement=(Element)noImgColnode;
											NodeList headerNodeList=noImgElement.getElementsByTagName("header");
											JSONObject headerJSONObj=new JSONObject();
											noImgColsJSONArr.put(headerJSONObj);											
											if(null!=headerNodeList && headerNodeList.getLength()>0){
												for(int y=0;y<headerNodeList.getLength();++y){
													Node headerNode=headerNodeList.item(y);
													if(headerNode.getNodeType()==Node.ELEMENT_NODE){

														Element headerElement=(Element)headerNode;
														NodeList headerDisplayNodeList=headerElement.getElementsByTagName("display");
														String displayStr="";
														String urlStr="";
														displayStr=getTagValue(headerDisplayNodeList);
														NodeList urlNodeList=headerElement.getElementsByTagName("url");
														urlStr=getTagValue(urlNodeList);

														headerJSONObj.put("headerurl", urlStr);
														headerJSONObj.put("headerdisplay", displayStr);
													}
												}
											}
											NodeList linksNodeList=noImgElement.getElementsByTagName("links");
											JSONArray linksJSONArr=new JSONArray();
											headerJSONObj.put("links", linksJSONArr);
											if(null!=linksNodeList && linksNodeList.getLength()>0){
												for(int y=0;y<linksNodeList.getLength();++y){
													Node linksNode=linksNodeList.item(y);
													if(linksNode.getNodeType()==Node.ELEMENT_NODE){
														Element linksElement=(Element)linksNode;
														NodeList linkNodeList=linksElement.getElementsByTagName("link");
														if(null!=linkNodeList&&linkNodeList.getLength()>0){
															for(int z=0;z<linkNodeList.getLength();++z){
																Node aLinkNode=linkNodeList.item(z);
																if(aLinkNode.getNodeType()==Node.ELEMENT_NODE){
																	Element aLinkElement=(Element)aLinkNode;
																	String linkDisplayStr="";
																	String linkUrlStr="";
																	NodeList displayNodeList=aLinkElement.getElementsByTagName("display");
																	linkDisplayStr=getTagValue(displayNodeList);
																	NodeList linkUrlNodeList=aLinkElement.getElementsByTagName("url");
																	linkUrlStr=getTagValue(linkUrlNodeList);

																	JSONObject aLinkJSONObj=new JSONObject();
																	linksJSONArr.put(aLinkJSONObj);
																	aLinkJSONObj.put("linkdisplay", linkDisplayStr);
																	aLinkJSONObj.put("linkurl", linkUrlStr);
																}	
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
					NodeList imgColsList=doc.getElementsByTagName("imgcols");					
					if(null!=imgColsList && imgColsList.getLength()>0){
						JSONArray imgColsJSONArr=new JSONArray();
						footerJSONObj.put("imgcols", imgColsJSONArr);						
						for(int i=0;i<imgColsList.getLength();++i){
							Node imgColsNode=imgColsList.item(i);
							if(imgColsNode.getNodeType()==Node.ELEMENT_NODE){
								Element imgColsElement=(Element)imgColsNode;
								NodeList imgColNodeList=imgColsElement.getElementsByTagName("imgcol");
								if(null!=imgColNodeList && imgColNodeList.getLength()>0){
									for(int n=0;n<imgColNodeList.getLength();++n){
										Node imgColNode=imgColNodeList.item(n);
										JSONObject anImgColJSONObj=new JSONObject();
										imgColsJSONArr.put(anImgColJSONObj);
										if(imgColNode.getNodeType()==Node.ELEMENT_NODE){
											Element imgColElement=(Element)imgColNode;
											NodeList imgColHeaderList=imgColElement.getElementsByTagName("header");
											if(null!=imgColHeaderList && imgColHeaderList.getLength()>0){
												String displayStr="";
												String urlStr="";
												for(int y=0;y<imgColHeaderList.getLength();++y){
													Node imgColHeaderNode=imgColHeaderList.item(y);
													if(imgColHeaderNode.getNodeType()==Node.ELEMENT_NODE){
														Element imgColHeaderElement=(Element)imgColHeaderNode;
														NodeList imgColDisplay=imgColHeaderElement.getElementsByTagName("display");
														displayStr=getTagValue(imgColDisplay);
														NodeList imgColUrl=imgColHeaderElement.getElementsByTagName("url");
														urlStr=getTagValue(imgColUrl);
													}
												}
												anImgColJSONObj.put("headerurl", urlStr);
												anImgColJSONObj.put("headerdisplay", displayStr);										
											}
											NodeList imgColLinksList=imgColElement.getElementsByTagName("links");
											if(null!=imgColLinksList && imgColLinksList.getLength()>0){
												JSONArray anImagLinksJSONArr=new JSONArray();
												anImgColJSONObj.put("links", anImagLinksJSONArr);
												for(int y=0;y<imgColLinksList.getLength();++y){
													Node imgColLinksNode=imgColLinksList.item(y);
													if(imgColLinksNode.getNodeType()==Node.ELEMENT_NODE){
														Element imgColLinksElement=(Element)imgColLinksNode;
														NodeList imgColLinkList=imgColLinksElement.getElementsByTagName("link");
														if(null!=imgColLinkList && imgColLinkList.getLength()>0){
															String displayStr="";
															String classStr="";
															String urlStr="";
															String numberStr="";
															for(int x=0;x<imgColLinkList.getLength();++x){
																Node imgColLinkNode=imgColLinkList.item(x);
																if(imgColLinkNode.getNodeType()==Node.ELEMENT_NODE){
																	Element imgColLinkElement=(Element)imgColLinkNode;
																	NodeList imgColLinkDisplayList=imgColLinkElement.getElementsByTagName("display");
																	NodeList imgColLinkClassList=imgColLinkElement.getElementsByTagName("class");
																	NodeList imgColLinkUrlList=imgColLinkElement.getElementsByTagName("url");
																	NodeList imgColLinkNumberList=imgColLinkElement.getElementsByTagName("number");
																	displayStr=getTagValue(imgColLinkDisplayList);
																	classStr=getTagValue(imgColLinkClassList);
																	urlStr=getTagValue(imgColLinkUrlList);
																	numberStr=getTagValue(imgColLinkNumberList);

																	JSONObject anImgColLink=new JSONObject();
																	anImagLinksJSONArr.put(anImgColLink);
																	anImgColLink.put("linknumber", numberStr);
																	anImgColLink.put("linkurl", urlStr);
																	anImgColLink.put("linkclass",classStr);
																	anImgColLink.put("linkdisplay",displayStr);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
					NodeList imgColsList2=doc.getElementsByTagName("imgcols2");
					if(null!=imgColsList2 && imgColsList2.getLength()>0){
						JSONArray imgCols2JSONArr=new JSONArray();
						footerJSONObj.put("imgcols2", imgCols2JSONArr);					
						for(int i=0;i<imgColsList2.getLength();++i){
							Node imgCols2Node=imgColsList2.item(i);
							if(imgCols2Node.getNodeType()==Node.ELEMENT_NODE){
								Element imgCols2Element=(Element)imgCols2Node;
								NodeList imgCol2NodeList=imgCols2Element.getElementsByTagName("imgcol2");
								if(null!=imgCol2NodeList && imgCol2NodeList.getLength()>0){
									for(int n=0;n<imgCol2NodeList.getLength();++n){
										Node imgCol2Node=imgCol2NodeList.item(n);
										JSONObject img2HeaderJSONObj=new JSONObject();
										imgCols2JSONArr.put(img2HeaderJSONObj);
										if(imgCol2Node.getNodeType()==Node.ELEMENT_NODE){
											Element imgCol2Element=(Element)imgCol2Node;
											NodeList imgCol2HeaderList=imgCol2Element.getElementsByTagName("header");
											if(null!=imgCol2HeaderList && imgCol2HeaderList.getLength()>0){
												String displayStr="";
												String urlStr="";
												for(int y=0;y<imgCol2HeaderList.getLength();++y){
													Node imgCol2HeaderNode=imgCol2HeaderList.item(y);
													if(imgCol2HeaderNode.getNodeType()==Node.ELEMENT_NODE){
														Element imgCol2HeaderElement=(Element)imgCol2HeaderNode;
														NodeList imgCol2Display=imgCol2HeaderElement.getElementsByTagName("display");
														displayStr=getTagValue(imgCol2Display);
														NodeList imgColUrl=imgCol2HeaderElement.getElementsByTagName("url");
														urlStr=getTagValue(imgColUrl);
													}
												}
												img2HeaderJSONObj.put("headerdisplay", displayStr);
												img2HeaderJSONObj.put("headerurl", urlStr);												
											}
											NodeList imgCol2LinksList=imgCol2Element.getElementsByTagName("links");
											if(null!=imgCol2LinksList && imgCol2LinksList.getLength()>0){
												JSONArray img2HeaderLinksJSONArr=new JSONArray();
												img2HeaderJSONObj.put("links", img2HeaderLinksJSONArr);
												for(int y=0;y<imgCol2LinksList.getLength();++y){
													Node imgCol2LinksNode=imgCol2LinksList.item(y);
													if(imgCol2LinksNode.getNodeType()==Node.ELEMENT_NODE){
														Element imgCol2LinksElement=(Element)imgCol2LinksNode;
														NodeList imgCol2LinkList=imgCol2LinksElement.getElementsByTagName("link");
														if(null!=imgCol2LinkList && imgCol2LinkList.getLength()>0){
															String classStr="";
															String urlStr="";
															for(int x=0;x<imgCol2LinkList.getLength();++x){
																Node imgCol2LinkNode=imgCol2LinkList.item(x);
																if(imgCol2LinkNode.getNodeType()==Node.ELEMENT_NODE){
																	Element imgCol2LinkElement=(Element)imgCol2LinkNode;

																	NodeList imgCol2LinkClassList=imgCol2LinkElement.getElementsByTagName("class");
																	NodeList imgCol2LinkUrlList=imgCol2LinkElement.getElementsByTagName("url");
																	classStr=getTagValue(imgCol2LinkClassList);
																	urlStr=getTagValue(imgCol2LinkUrlList);
																	JSONObject anImgCol2LinkJSONObj=new JSONObject();
																	img2HeaderLinksJSONArr.put(anImgCol2LinkJSONObj);
																	anImgCol2LinkJSONObj.put("linkclass", classStr);
																	anImgCol2LinkJSONObj.put("linkurl",	urlStr);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
					NodeList bottomList=doc.getElementsByTagName("bottom");
					if(null!=bottomList && bottomList.getLength()>0){
						for(int i=0;i<bottomList.getLength();++i){
							Node bottomNode=bottomList.item(i);
							if(bottomNode.getNodeType()==Node.ELEMENT_NODE){
								Element bottomElement=(Element)bottomNode;
								NodeList linksList=bottomElement.getElementsByTagName("link");
								if(null!=linksList&&linksList.getLength()>0){
									JSONArray bottomJSONArr=new JSONArray();
									footerJSONObj.put("bottom", bottomJSONArr);									
									for(int x=0;x<linksList.getLength();++x){
										String displayStr="";
										String urlStr="";
										Node linkNode=linksList.item(x);
										if(linkNode.getNodeType()==Node.ELEMENT_NODE){
											Element linkElement=(Element)linkNode;
											NodeList displayNodeList=linkElement.getElementsByTagName("display");
											displayStr=getTagValue(displayNodeList);
											NodeList urlNodeList=linkElement.getElementsByTagName("url");
											urlStr=getTagValue(urlNodeList);										
											JSONObject aBottomJSONObj=new JSONObject();
											bottomJSONArr.put(aBottomJSONObj);
											aBottomJSONObj.put("url", urlStr);
											aBottomJSONObj.put("display", displayStr);
										}
									}
								}
							}
						}
					}
				}
				catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch(IOException e){
					e.printStackTrace();
				}
				catch(SAXException e){
					e.printStackTrace();
				}
				catch(JSONException e){
					e.printStackTrace();
				}
			}
			synchronized(this.footer){
				if(this.footer.isEmpty()){
					this.footer=retJSONObj.toString();
				}
			}
		}
		if(log.isDebugEnabled()){
			log.debug("loadFooter() Exit:");
		}
		return this.footer;
	}

	private String getTagValue(NodeList nodeList){
		String retVal="";
		if(null!=nodeList && nodeList.getLength()>0){
			Node node=nodeList.item(0);
			NodeList children=node.getChildNodes();
			if(null!=children){
				Node child=children.item(0);
				if(null!=child){
					retVal=child.getNodeValue();
				}
			}
		}		
		return retVal;
	}

//	private String processAction(String action, IndexBean indexBean){
//		if(log.isDebugEnabled()){
//			log.debug("processAction(): Enter");
//		}	
//		//See if we already have output for the requested action
//		String actionOutput=this.actionOutputs.get(action);
//		if(actionOutput==null || actionOutput.isEmpty()){
//			JSONObject retJSONObj=new JSONObject();	
//			
//			Map<String,Product>products=indexBean.getProductsMap();
//			Map<String, Map<String,Vector<ProductEntry>>>prodEntriesForGivenProd=indexBean.getProductEntriesForProdsCategorizedByTypes();
//
//			List<Product>prods=indexBean.getProds();
//			JSONArray prodsArr=new JSONArray();
//			try{
//			retJSONObj.put("prods", prodsArr);
//			for(int i=0;i<prods.size();++i){
//				Product aProd=prods.get(i);
//
//				JSONObject aProdJSONObj=new JSONObject();
//    			aProdJSONObj.put("prodid", aProd.getId());
//				aProdJSONObj.put("produrl", aProd.getUrl());
//				aProdJSONObj.put("prodlogourl", aProd.getLogoUrl());
//				aProdJSONObj.put("prodiconurl", aProd.getIconUrl());
//    			aProdJSONObj.put("prodtitle", aProd.getTitle());
//    			aProdJSONObj.put("mousedown", aProd.getMousedown());
//				prodsArr.put(aProdJSONObj);
//			}
//			//We now have all the products in the Map and there is at least one product type 
//			if(prodEntriesForGivenProd.containsKey(action)){
//				//This contains all the product entries for a given product, categorized by each headertype
//				Map<String,Vector<ProductEntry>>prodEntriesForProd=prodEntriesForGivenProd.get(action);
//				//Get the product
//				Product theProduct=products.get(action);
//				JSONObject productJSONObj=new JSONObject();
//				retJSONObj.put("product", productJSONObj);
//				
//				JSONArray headerTypesArr=new JSONArray();
//				productJSONObj.put("headertypes", headerTypesArr);				
//				productJSONObj.put("description", theProduct.getDescription().trim());
//				productJSONObj.put("title", theProduct.getTitle());
//				productJSONObj.put("action",action);
//				//Iterate through all the productheaders and associated productentries
//				Set<String>headerDisplayNameKeys=prodEntriesForProd.keySet();
//
//				for(String aHeaderDisplayNameKey:headerDisplayNameKeys){
//					JSONObject aheaderTypeJSONObj=new JSONObject();
//					headerTypesArr.put(aheaderTypeJSONObj);
//					aheaderTypeJSONObj.put("headername", aHeaderDisplayNameKey);
//					Vector<ProductEntry> prodEntries=prodEntriesForProd.get(aHeaderDisplayNameKey);
//					JSONArray prodEntriesJSONArr=new JSONArray();
//					aheaderTypeJSONObj.put("types",prodEntriesJSONArr);
//					for(ProductEntry aProdEntry:prodEntries){
//						String displayName=aProdEntry.getTypedisplayname();
//						displayName=displayName.replaceAll("\n", " ");
//						displayName=displayName.replaceAll("\r", " ");
//
//						JSONObject aProdEntryJSONObj=new JSONObject();
//						aProdEntryJSONObj.put("id",aProdEntry.getTypeId());
//					    aProdEntryJSONObj.put("displayname",displayName);
//					    aProdEntryJSONObj.put("url",aProdEntry.getTypeurl());
//					    prodEntriesJSONArr.put(aProdEntryJSONObj);						
//					}
//				}
//			}
//			synchronized(this.actionOutputs){
//				if(this.actionOutputs.get(action)==null||this.actionOutputs.get(action).isEmpty()){
//					this.actionOutputs.put(action, retJSONObj.toString());
//				}
//			}
//			actionOutput=this.actionOutputs.get(action);
//			}
//			catch(JSONException e){
//    			e.printStackTrace();
//			}
//		}
//		if(log.isDebugEnabled()){
//			log.debug("processAction(): Exit ");
//		}
//		return actionOutput;
//	}
}
