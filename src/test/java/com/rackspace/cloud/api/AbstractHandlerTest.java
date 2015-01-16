package com.rackspace.cloud.api;

import java.util.Hashtable;

import org.apache.felix.http.base.internal.context.ExtServletContext;
import org.apache.felix.http.base.internal.handler.AbstractHandler;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public abstract class AbstractHandlerTest {
    protected ExtServletContext context;

    protected abstract AbstractHandler createHandler();

    public void setUp()
    {
        this.context = Mockito.mock(ExtServletContext.class);
    }
    
    @Test
    public void testId()
    {
        AbstractHandler h1 = createHandler();
        AbstractHandler h2 = createHandler();

        Assert.assertNotNull(h1.getId());
        Assert.assertNotNull(h2.getId());
        Assert.assertFalse(h1.getId().equals(h2.getId()));
    }

    @Test
    public void testInitParams()
    {
        AbstractHandler handler = createHandler();
        Assert.assertEquals(0, handler.getInitParams().size());
        
        Hashtable<String, String> map = new Hashtable<String, String>();
        map.put("key1", "value1");

        handler.setInitParams(map);
        Assert.assertEquals(1, handler.getInitParams().size());
        Assert.assertEquals("value1", handler.getInitParams().get("key1"));
    }
}
