package com.javacodegeeks.snippets.enterprise;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.eclipse.jetty.http.HttpStatus;

@MultipartConfig
public class ExampleServlet extends HttpServlet {
	 private final static Logger LOGGER = 
	            Logger.getLogger(ExampleServlet.class.getCanonicalName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setStatus(HttpStatus.OK_200);
		String name = req.getParameter("name");
		resp.getWriter().println(name);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

	    // Create path components to save the file
	    final String path = "/tmp";
	    final Part filePart = req.getPart("file");
	    final String fileName = getFileName(filePart);

	    OutputStream out = null;
	    InputStream filecontent = null;
	    final PrintWriter writer = resp.getWriter();

	    try {
	        out = new FileOutputStream(new File(path + File.separator
	                + fileName));
	        filecontent = filePart.getInputStream();

	        int read = 0;
	        final byte[] bytes = new byte[1024];

	        while ((read = filecontent.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        
	        // Location to send un-zipped files to. Set to /tmp/cbsi
	        unzipFile("/tmp/" + fileName, "/tmp/cbsi");
	        
	        writer.println("New file " + fileName + " created at " + path);
	        LOGGER.log(Level.INFO, "File{0}being uploaded to {1}", 
	                new Object[]{fileName, path});
	        
	    } catch (FileNotFoundException fne) {
	        writer.println("You either did not specify a file to upload or are "
	                + "trying to upload a file to a protected or nonexistent "
	                + "location.");
	        writer.println("<br/> ERROR: " + fne.getMessage());

	        LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", 
	                new Object[]{fne.getMessage()});
	    } finally {
	        if (out != null) {
	            out.close();
	        }
	        if (filecontent != null) {
	            filecontent.close();
	        }
	        if (writer != null) {
	            writer.close();
	        }
	    }
	}
	private String getFileName(final Part part) {
	    final String partHeader = part.getHeader("content-disposition");
	    LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}
	// Helper method to unzip files. Takes two parameters: file and the location(directory) to save unzipped files to.
	private static void unzipFile(String zipFile, String dir) throws IOException {
		String fileZip = zipFile;
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        
        while(zipEntry != null){
	        	if(!zipEntry.isDirectory()) {
	            String fileName = zipEntry.getName();
	            File newFile = new File(dir + File.separator + fileName);
	            newFile.getParentFile().mkdirs();
	            FileOutputStream fos = new FileOutputStream(newFile);
	            int len;
	            while ((len = zis.read(buffer)) > 0) {
	                fos.write(buffer, 0, len);
	            }
	            fos.close();
	        	}
	        zipEntry = zis.getNextEntry();
	            
        }
        zis.closeEntry();
        zis.close();
	}
}

