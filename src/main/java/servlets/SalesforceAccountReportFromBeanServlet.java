package servlets;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

public class SalesforceAccountReportFromBeanServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        Connection conn = null;
        OutputStream out = null;
        
        try {
            // Load the JRXML file and compile it at runtime
            InputStream jrxmlStream = getServletContext()
                .getResourceAsStream("/WEB-INF/reports/javaBeanReport.jrxml");
            
            if (jrxmlStream == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, 
                    "Report template not found at /WEB-INF/reports/javaBeanReport.jrxml");
                return;
            }
            
            List<AccountBean> accountList = getAccounts();
            // --- Jasper DataSource ---
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(accountList);
            
            // Compile the report from JRXML
            System.out.println("Compiling report from JRXML...");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            
            // Parameters for the report
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("TABLE_DATA_SOURCE", dataSource); // Pass connection for subdatasets
            
            // Fill the report with Java Beans data
            //JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);    
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            
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

    private List<AccountBean> getAccounts() {
        List<AccountBean> accountList = new ArrayList<>();
        accountList.add(new AccountBean("Shoaeb","Pune","8899008899"));
        accountList.add(new AccountBean("Pravin","Pune","887766554477"));
        accountList.add(new AccountBean("Samir","Pune","887755668888"));
        return accountList;
    }

    private List<AccountBean> getAccountsFromSF() {
        /*
        // --- Salesforce login ---
        ConnectorConfig config = new ConnectorConfig();
        config.setUsername("YOUR_SF_USERNAME");
        config.setPassword("YOUR_SF_PASSWORD+SECURITY_TOKEN");
        config.setAuthEndpoint("https://login.salesforce.com/services/Soap/u/60.0"); // API version adjust

        PartnerConnection connection = Connector.newConnection(config);

        // --- SOQL query ---
        String soql = "SELECT Name, BillingCity, Phone FROM Account LIMIT 20";
        QueryResult result = connection.query(soql);

        // --- Convert to JavaBeans ---
        List<AccountBean> accountList = new ArrayList<>();
        for (SObject record : result.getRecords()) {
            String name = (String) record.getField("Name");
            String billingCity = (String) record.getField("BillingCity");
            String phone = (String) record.getField("Phone");
            accountList.add(new AccountBean(name, billingCity, phone));
        }
        */

       return null;
    }
}

/**List<AccountBean> accountList = getAccounts();
        // --- Jasper DataSource ---
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(accountList);

        // --- Parameters ---
        Map<String, Object> params = new HashMap<>();
        params.put("ReportTitle", "Salesforce Accounts");

        // --- Compile + Fill ---
        JasperReport jasperReport = JasperCompileManager.compileReport("javaBeanReport.jrxml");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        // --- Export ---
        JasperExportManager.exportReportToPdfFile(jasperPrint, "AccountsReport.pdf");
        System.out.println("✅ Report generated: AccountsReport.pdf"); */