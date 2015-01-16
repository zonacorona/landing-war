package com.rackspace.cloud.api.bean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.rackspace.cloud.api.Product;
import com.rackspace.cloud.api.beans.IndexBean;
import com.rackspace.cloud.api.beans.InputStreamAndFileName;

public class IndexBeanTest {
	
	private HttpServletRequest request;
	private HttpServletResponse response;

	private IndexBean bean;
	
	@Mock
	private ServletContext ctx;
	
	@Before
	public void setUp(){
		this.request=Mockito.mock(HttpServletRequest.class);
		this.response=Mockito.mock(HttpServletResponse.class);
		this.bean=new IndexBean();
		this.ctx=Mockito.mock(ServletContext.class);
		this.bean.setServletCtx(ctx);
	}
	
	@Test
	public void testIndexBean(){

		InputStream inny=this.getClass().getClassLoader().getResourceAsStream("productinfo.xml");		
		//Had to create this method for testing
		this.bean.setProductInfoXML(inny);
		
		InputStream inny2=this.getClass().getClassLoader().getResourceAsStream("files-v1-releasenotes-bookinfo.xml");
		InputStreamAndFileName book2=new InputStreamAndFileName(inny2,"files-v1-releasenotes");
		
		InputStream inny3=this.getClass().getClassLoader().getResourceAsStream("loadBalancerBookinfo.xml");
		InputStreamAndFileName book3=new InputStreamAndFileName(inny3,"loadBalancer");
		
		InputStream inny4=this.getClass().getClassLoader().getResourceAsStream("server-v2-devguide-bookinfo.xml");
		InputStreamAndFileName book4=new InputStreamAndFileName(inny4,"server-v2-devguide");
		
		List<InputStreamAndFileName>inputs=new ArrayList<InputStreamAndFileName>();
		inputs.add(book2);
		inputs.add(book3);
		inputs.add(book4);
		
		//Had to create this method for testing
		this.bean.setTypeInputStreams(inputs);
		
		this.bean.loadProducts();
		//Map<String, Product>map1=this.bean.getProductsCol1Map();

		//Assert.assertNotNull(map1);
		//Assert.assertNotEquals(map1.size(), 0);
	}
}
