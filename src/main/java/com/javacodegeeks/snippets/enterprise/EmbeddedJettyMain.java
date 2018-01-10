package com.javacodegeeks.snippets.enterprise;
//import com.javacodegeeks.snippets.enterprise.embeddedjetty.servlet.ExampleServlet;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class EmbeddedJettyMain {

	public static void main(String[] args) throws Exception {

		Server server = new Server(8080); // Start new server on 8080.
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setResourceBase("webapps");
        
		server.setHandler(context);
		
		ServletHolder sh = new ServletHolder(new ExampleServlet());
        sh.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
        context.addServlet(sh, "/example");
		server.start();

	}

}
