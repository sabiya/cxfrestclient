
package com.jaxrs.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.RollingFileAppender;

/**
 * This class calls Rest Based Service which expects a JSON Message
 * That Service Returns Response after 1 Minute
 * Which is default ReceiveYimeOut For CXF Client Configuration.
 * @author sabiya
 *
 */
@WebServlet("/RestClient")
public class RestClient extends HttpServlet{

	private static Logger logger = Logger.getLogger(RestClient.class.getCanonicalName());
	@Override
	public void doPost(HttpServletRequest request,
    		HttpServletResponse response) throws ServletException, IOException{
		String resourceAddress = request.getParameter("resourceAddress");
		String filePath= request.getParameter("filePath");
		String contentType= request.getParameter("contentType");
		String serviceresponse = invokeService(resourceAddress, filePath, contentType);
		response.getWriter().println(serviceresponse);
	}
	/**
	 * Invokes a Given Service And Return a Response
	 * @param contentType 
	 * @return
	 * @throws IOException
	 */
	public String invokeService(String resourceAddress, String filePath, String contentType) throws IOException {
		JAXRSClientFactoryBean cxfClientBean = new JAXRSClientFactoryBean();
		cxfClientBean
		// http://sabiya.technologic.com:8585/Services/PCJSONProcessBase/PCJSONProcessResource/executePCJSONProcess"
		.setAddress(resourceAddress);
		new RestClient().setContentType(cxfClientBean, contentType);
		WebClient wc = cxfClientBean.createWebClient();
        //"application/json"
		// WebClient.getConfig(wc).getHttpConduit().getClient().setReceiveTimeout(900000);

		Path path = Paths.get(filePath);
		byte[] data = Files.readAllBytes(path);

		long startTimeMillis = System.currentTimeMillis();
		long finishTimeMillis = 0;
		String errorMessage;
		logger.log(Level.FINE, "\n Invoking a service ..");
		try {
			Object obj = new RestClient().invoke(wc, data);
			Response res = (Response) obj;
			finishTimeMillis = System.currentTimeMillis();
			logger.log(Level.FINE,"\n\nResponse:: \n");
			InputStream s = (InputStream) res.getEntity();
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			int c;
			while ((c = s.read()) != -1) {
				o.write(c);
				System.out.print((char) c);
			}
			return o.toString();
		} catch (Exception e) {
			errorMessage= e.getMessage();
			logger.log(Level.SEVERE, "Error occurred while invoking a service", e);
		}
		System.out.println("\nRequest Execution Time in Minutes :"
				+ ((finishTimeMillis - startTimeMillis) / 1000.0) / 60.0);
		return "Failed To Execute Service:" + errorMessage;
	}

	/**
	 * @param cxfClientBean
	 * @param contentType
	 */
	private void setContentType(JAXRSClientFactoryBean cxfClientBean, String contentType) {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("Content-Type", contentType);
		cxfClientBean.setHeaders(properties);
	}

	/**
	 * @param wc
	 * @param body
	 * @return
	 */
	private Response invoke(WebClient wc, Object body) {
		Response resp = null;
		if (body != null) {
			resp = wc.invoke("POST", body);
		}
		return resp;
	}
}
