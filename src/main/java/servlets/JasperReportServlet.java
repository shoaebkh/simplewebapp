package servlets;

import net.sf.jasperreports.engine.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class JasperReportServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    // Salesforce connection details - UPDATE THESE WITH YOUR CREDENTIALS
    private static final String SF_JDBC_URL = "jdbc:salesforce:InitiateOAuth=GETANDREFRESH;SecurityToken=fPDsXigWcaNY3Zo7zqPrpA7S;APIVersion=64.0;LoginURL=https://ability-ability-6845-dev-ed.scratch.my.salesforce.com/;AuthScheme=OAuth;UseSandbox=true;";
    private static final String SF_DRIVER_CLASS = "cdata.jdbc.salesforce.SalesforceDriver";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        Connection conn = null;
        OutputStream out = null;
        
        try {
            // Load the JRXML file and compile it at runtime
            InputStream jrxmlStream = getServletContext()
                .getResourceAsStream("/WEB-INF/reports/sampleSFReport.jrxml");
            
            if (jrxmlStream == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, 
                    "Report template not found at /WEB-INF/reports/sampleSFReport.jrxml");
                return;
            }
            
            // Load Salesforce JDBC driver
            System.out.println("Loading Salesforce JDBC driver...");
            Class.forName(SF_DRIVER_CLASS);
            
            // Create Salesforce connection
            System.out.println("Connecting to Salesforce...");
            conn = DriverManager.getConnection(SF_JDBC_URL);
            System.out.println("Connected to Salesforce successfully!");
            
            // Compile the report from JRXML
            System.out.println("Compiling report from JRXML...");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            
            // Parameters for the report
            Map<String, Object> parameters = new HashMap<>();
            //parameters.put("REPORT_CONNECTION", conn); // Pass connection for subdatasets
            
            // Fill the report with Salesforce data
            System.out.println("Filling report with Salesforce data...");
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, parameters, conn);
            
            System.out.println("Report filled successfully with " + 
                jasperPrint.getPages().size() + " pages!");
            
            // Set response headers for PDF output
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", 
                "inline; filename=salesforce_accounts_report.pdf");
            
            // Export to PDF
            out = resp.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            
            System.out.println("Report generated and sent successfully!");
            
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Salesforce JDBC Driver not found: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Salesforce JDBC Driver not found. Please add cdata.jdbc.salesforce.jar to WEB-INF/lib");
        } catch (JRException e) {
            e.printStackTrace();
            System.err.println("JasperReports Error: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error generating report: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected Error: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Unexpected error: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("Salesforce connection closed.");
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        doGet(req, resp);
    }
}