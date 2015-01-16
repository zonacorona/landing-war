package com.rackspace.cloud.api;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class SendMail implements Runnable {
	private static Logger log=Logger.getLogger(SendMail.class);
	private List<Email>emails;
	private String message;
	
	public SendMail(List<Email>emails, String message){
		this.emails=emails;
		this.message=message;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(null!=this.emails){
			sendEmails(this.emails, this.message);
		}
	}

	private void sendEmails(List<Email>emails, String message){
		String METHOD_NAME="sendEmails()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START");
		}
		if(null!=emails){
			//message must not be null or empty
			if(null!=message && !message.isEmpty()){
				String host="localhost";
				String from="";
				String to="Content_Feedback@RACKSPACE.COM";
				String subject="";
				String name="";
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": message="+message);
				}

				Properties props=new Properties();
				props.setProperty("mail.smtp.host", host);
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": host="+host);
				}
				Session sess=Session.getDefaultInstance(props);
				if(log.isDebugEnabled()){
					log.debug(METHOD_NAME+": created the session, now interating through emails");
				}			
				for(Email anEmail:emails){

					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": created mime message");
					}
					to=anEmail.getTo();
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": Sending the email to="+to);
					}
					if(null!=to && !to.isEmpty()){

						from=anEmail.getFrom();
						if(null==from || from.isEmpty()){
							from="anonymous@anonymous.com";
						}
						subject=anEmail.getSubject();
						if(null==subject){
							subject="customer feedback";
						}
						name=anEmail.getName();
						if(null==name){
							name="anonymous";
						}
						if(log.isDebugEnabled()){
							log.debug(METHOD_NAME+": from="+from+" name="+name+" subject="+subject);						
						}					
						MimeMessage mimeMessage=new MimeMessage(sess);
						try {
							if(null!=from && !from.isEmpty()){

								mimeMessage.setFrom(new InternetAddress(from));
							}

							mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
							mimeMessage.setSubject(subject);
							mimeMessage.setText(message);
							Transport.send(mimeMessage);
							if(log.isDebugEnabled()){
								log.debug(METHOD_NAME+"Sent message to: "+to);
								log.debug(METHOD_NAME+"message: "+message);
							}
						} 
						catch (AddressException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						catch(Throwable e){
							e.printStackTrace();
						}
					}
				}
			}
		}
		else{
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": emails is null do not send any emails");
			}
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END");
		}
	}
}
