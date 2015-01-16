package com.rackspace.cloud.api.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.rackspace.cloud.api.Email;
import com.rackspace.cloud.api.EmailException;
import com.rackspace.cloud.api.SendMail;

/**
 * Servlet implementation class FeedbackServlet
 */
public class FeedbackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger log=Logger.getLogger(FeedbackServlet.class);
	private static Map<String,Integer>permittedParameterNamesMap;
	
	static{
		//instantiate the Map
		permittedParameterNamesMap=new HashMap<String,Integer>();
		//the map tracks all the allowable parameters, and the maximum length of the input allowed
		permittedParameterNamesMap.put("nameinput", new Integer("100"));
		permittedParameterNamesMap.put("emailinput", new Integer("100"));
		permittedParameterNamesMap.put("commentsinput", new Integer("1000"));
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeedbackServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// String METHOD_NAME="doGet()";
		// if(log.isDebugEnabled()){
		// 	log.debug(METHOD_NAME+": START");
		// }
		// this.doPost(request, response);
		
		// if(log.isDebugEnabled()){
		// 	log.debug(METHOD_NAME+": END");
		// }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// String METHOD_NAME="doPost()";
		// if(log.isDebugEnabled()){
		// 	log.debug(METHOD_NAME+": START");
		// }		
		// Map map=request.getParameterMap();
		// Set<String>keys=map.keySet();
		
		// for(String aKey:keys){
		// 	log.debug(METHOD_NAME+"~~~~~~~~~request.getParameter("+aKey+")="+request.getParameter(aKey));					
		// }	
		// try{
		// 	//Make sure we make it past validation
		// 	validateParameters(request);
		// 	sendTheEmail(request);
		// }
		// catch(EmailException e){
		// 	log.debug(METHOD_NAME+": validation failed with error message: "+e.getMessage());
		// 	e.printStackTrace();
		// }
		// if(log.isDebugEnabled()){
		// 	log.debug(METHOD_NAME+": END");
		// }		
	}
	
	private void sendTheEmail(HttpServletRequest request){
		String METHOD_NAME="sendTheEmail()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": START:");
		}
		String nameInput=request.getParameter("nameinput");
		if(null==nameInput||nameInput.isEmpty()){
			nameInput="anonymous";
		}
		String fromEmailInput=request.getParameter("emailinput");
		if(fromEmailInput==null||fromEmailInput.isEmpty()){
			fromEmailInput="anonymous@anonymous.com";
		}
		String commentsInput=request.getParameter("commentsinput");
		if(commentsInput==null){
			commentsInput="";
		}
		String feedBackEmail=System.getProperty("FEEDBACK_EMAIL", "Content_Feedback@rackspace.com");
		String subject="Customer feedback from docs.rackspace.com landing page";
		String serverName=request.getServerName();
		if(null!=serverName && serverName.toLowerCase().contains("internal")){
			 subject="Customer feedback from docs-internal.rackspace.com landing page";
		}
		Email anEmail=new Email(nameInput, feedBackEmail,fromEmailInput, subject, null);
		
		List<Email> emailList=new ArrayList<Email>();
		emailList.add(anEmail);
		SendMail sendMail=new SendMail(emailList, commentsInput);
		
		Thread aThread=new Thread(sendMail);
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+"!!!!!!!!Sending email to: anEmail.getTo()="+anEmail.getTo());
		}
		aThread.start();		
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+": END:");
		}
		
	}
	
	//This method validates that the parameters name are acceptable and that they are 
	//not larger than permitted. We don't care what characters the user inputs since 
	//this does not result in any database calls. This method throws EmailException
	//if validation fails in any way.
	private void validateParameters(HttpServletRequest request)throws EmailException{
		String METHOD_NAME="validateParameters()";
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+":START:");
		}
		
		Map<String, String[]>parametersMap=request.getParameterMap();
		for(String aParamName:parametersMap.keySet()){
			if(log.isDebugEnabled()){
				log.debug(METHOD_NAME+": if(permittedParameterNamesMap.containsKey("+aParamName+")="+
			    permittedParameterNamesMap.containsKey(aParamName));
			}
			if(null!=aParamName && !aParamName.isEmpty()){
				if(permittedParameterNamesMap.containsKey(aParamName)){
					int maxSizeAllowed=permittedParameterNamesMap.get(aParamName);
					String aParamVal=request.getParameter(aParamName);
					if(log.isDebugEnabled()){
						log.debug(METHOD_NAME+": aParamVal="+aParamVal+" aParamVal="+aParamVal);
					}					
					if(null==aParamVal || aParamVal.length() > maxSizeAllowed){
						if(null==aParamVal){
							throw new EmailException("Request parameter: "+aParamName+" cannot have a null value");
						}
						else{
							throw new EmailException("Request parameter: "+aParamName+" length: "+aParamVal.length()+
									" is larger than the maximum length: "+maxSizeAllowed);
						}
					}
				}
				else{
					throw new EmailException("Request parameter name: "+aParamName+" is not an acceptable paraemter");
				}
			}			
		}
		if(log.isDebugEnabled()){
			log.debug(METHOD_NAME+":END:");
		}		
	}

}
