package com.rackspace.cloud.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

public class ProductInfoTest {
	
	@Test
	public void ProductInfoSaxHandlerTest(){
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			ProductInfoSaxHandler prodSax=new ProductInfoSaxHandler();
			InputStream innyStream=this.getClass().getClassLoader().getResourceAsStream("productinfo.xml");
			saxParser.parse(innyStream, prodSax);
			
			List<Product>prods=prodSax.getProductsList();

			Assert.assertTrue(prods.size()==31);		
		}
		catch(SAXException e){
			e.printStackTrace();
		}
		catch(ParserConfigurationException e){
			e.printStackTrace();
		}
		catch(IOException e){
		    e.printStackTrace();
		}
	}

}
