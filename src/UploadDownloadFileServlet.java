import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

@WebServlet("/UploadDownloadFileServlet")
public class UploadDownloadFileServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isMultipart;
	   private String filePath;
	   private int maxFileSize = 50 * 1024;
	   private int maxMemSize = 50 * 1024;
	   private File file ;
	   
	   public void init( ){
	      // Get the file location where it would be stored.
	      filePath = 
	             getServletContext().getInitParameter("file-upload"); 
	   }
	   public void doPost(HttpServletRequest request, 
	               HttpServletResponse response)
	              throws ServletException, java.io.IOException {
		   String location = request.getParameter("location");
		   String fileN = (String) request.getParameter("filename");
		   String ext1 = FilenameUtils.getExtension(fileN);
		   System.out.println(ext1);
           if(ext1 == "pdf"){
           	request.setAttribute("filename", fileN);
           	RequestDispatcher rd = request.getRequestDispatcher("PdfToTextConverter");
	            rd.forward(request,response);
           }
           else if(ext1 == "txt"){
        	   isMultipart = ServletFileUpload.isMultipartContent(request);
     	      response.setContentType("text/html");
     	      java.io.PrintWriter out = response.getWriter( );
     	      if( !isMultipart ){
     	         out.println("<html>");
     	         out.println("<head>");
     	         out.println("<title>Servlet upload</title>");  
     	         out.println("</head>");
     	         out.println("<body>");
     	         out.println("<p>No file uploaded</p>"); 
     	         out.println("</body>");
     	         out.println("</html>");
     	         return;
     	      }
     	      DiskFileItemFactory factory = new DiskFileItemFactory();
     	      // maximum size that will be stored in memory
     	      factory.setSizeThreshold(maxMemSize);
     	      // Location to save data that is larger than maxMemSize.
     	      factory.setRepository(new File("c:\\temp"));
     	      
     	      // Create a new file upload handler
     	      ServletFileUpload upload = new ServletFileUpload(factory);
     	      // maximum file size to be uploaded.
     	      upload.setSizeMax( maxFileSize );

     	      try{ 
     	      // Parse the request to get file items.
     	      List fileItems = upload.parseRequest(request);
     		
     	      // Process the uploaded file items
     	      Iterator i = fileItems.iterator();

     	      out.println("<html>");
     	      out.println("<head>");
     	      out.println("<title>Servlet upload</title>");  
     	      out.println("</head>");
     	      out.println("<body>");
     	      while ( i.hasNext () ) 
     	      {
     	         FileItem fi = (FileItem)i.next();
     	         if ( !fi.isFormField () )	
     	         {
     	            // Get the uploaded file parameters
     	            String fieldName = fi.getFieldName();
     	            String fileName = fi.getName();
     	            String contentType = fi.getContentType();
     	            boolean isInMemory = fi.isInMemory();
     	            long sizeInBytes = fi.getSize();
     	            // Write the file
     	          
     	            if( fileName.lastIndexOf("\\") >= 0 ){
     	               file = new File( filePath + 
     	               fileName.substring( fileName.lastIndexOf("\\"))) ;
     	            }else{
     	               file = new File( filePath + 
     	               fileName.substring(fileName.lastIndexOf("\\")+1)) ;
     	            }
     	            fi.write( file ) ;
     	            out.println("Uploaded Filename: " + fileName + "<br>");
     	            String onlyFileName= fileName.substring(fileName.lastIndexOf("\\")+1);
     	            //request.setAttribute("onlyFileName", fileName.substring(fileName.lastIndexOf("\\")+1));
     	            //request.setAttribute("fileName",fileName);
     	            onlyFileName="C:/Users/garima/Downloads/apache-tomcat-7.0.37/webapps/"+onlyFileName;
     	    		BufferedReader br = new BufferedReader(new FileReader(onlyFileName));
     	    		String line= null, completeLine = "";
     	    		line = br.readLine();
     	    		while(line !=null) {
     	    			completeLine += line;
     	    			line = br.readLine();
     	    			//System.out.println(line);
     	    			
     	    		}
     	    		
     	    		br.close();
     	    		request.setAttribute("completeLine", completeLine);
     	    		request.setAttribute("location", location);
     	            RequestDispatcher rd = request.getRequestDispatcher("AudioConverter");
     	            rd.forward(request,response);
     	         }
     	      }
     	      out.println("</body>");
     	      out.println("</html>");
     	   }catch(Exception ex) {
     	       System.out.println(ex);
     	   }
           }
	      // Check that we have a file upload request
	      
	   }
	   public void doGet(HttpServletRequest request, 
	                       HttpServletResponse response)
	        throws ServletException, java.io.IOException {
	        
	        throw new ServletException("GET method used with " +
	                getClass( ).getName( )+": POST method required.");
	   }
}