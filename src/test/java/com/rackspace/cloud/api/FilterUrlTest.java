package com.rackspace.cloud.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.http.base.internal.handler.AbstractHandler;
import org.apache.felix.http.base.internal.handler.FilterHandler;
import org.junit.Before;
import org.mockito.Mockito;

public class FilterUrlTest extends AbstractHandlerTest{
	
	private FilterUrl filter;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	@Before
	public void setUp(){
		super.setUp();
		this.filter=Mockito.mock(FilterUrl.class);
		this.request=Mockito.mock(HttpServletRequest.class);
		this.response=Mockito.mock(HttpServletResponse.class);
		
	}

    protected AbstractHandler createHandler()
    {
        return createHandler("dummy", 0);
    }

    private FilterHandler createHandler(String pattern, int ranking)
    {
        return new FilterHandler(this.context, this.filter, pattern, ranking);
    }
    
    //TODO need to write test, may want to move them to integration test
    
}
