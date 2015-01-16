package com.rackspace.cloud.api;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.rackspace.cloud.api.Email;


public class EmailTest {
	
	@Test
	public void testEmail(){
		//Instantiate an Email Object
		Email email=new Email();
		
		//All initial values should be null
		Assert.assertNull(email.getFrom());
		Assert.assertNull(email.getName());
		Assert.assertNull(email.getSubject());
		Assert.assertNull(email.getTo());
		Assert.assertNull(email.getDocNames());

		//Create a names list
		List<String>namesList=new ArrayList<String>();
		namesList.add("docName1");
		
		email.setDocNames(namesList);
		email.setSubject("subject");
		email.setTo("blah");
		email.setFrom("fromBlah");
		email.setName("myname");
		
		List<String>namesList2=new ArrayList<String>();
		namesList2.add("docName1");
		
		email.setDocNames(namesList2);
		email.setSubject("subject");
		email.setTo("blah");
		email.setFrom("fromBlah");
		email.setName("myname");
		
		Assert.assertEquals("subject", email.getSubject());
		Assert.assertEquals("blah",email.getTo());
		Assert.assertEquals("fromBlah", email.getFrom());
		Assert.assertEquals("myname",email.getName());
		
		//Email(String name, String to, String from, String subject, List<String>docNames){
		Email email2=new Email("myname","blah","fromBlah","subject",namesList);
		
		Assert.assertEquals(email.getName(), email2.getName());
		Assert.assertEquals(email.getSubject(), email2.getSubject());
		Assert.assertEquals(email.getTo(), email2.getTo());
		Assert.assertEquals(email.getFrom(), email2.getFrom());
		
		String emailString=email.toString();
		String emailString2=email2.toString();
		
		Assert.assertEquals(emailString, emailString2);
		
		email2.addToDocNames("docName2");
		Assert.assertNotSame(email.getDocNames().size(), email2.getDocNames());
		
		email2.setName("newName");
		emailString2=email2.toString();
		
		Assert.assertNotEquals(emailString, emailString2);
		
		
	}
	

}
