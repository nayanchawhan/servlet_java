import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class ViewLogsServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h2>Intrusion Logs - V2</h2>");

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            Class.forName("org.postgresql.Driver");

            String host = System.getenv("Hostname");
            String port = System.getenv("Port");
            String database = System.getenv("Database");
            String username = System.getenv("Username");
            String password = System.getenv("Password");
            
            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            
            con = DriverManager.getConnection(jdbcUrl, username, password);

            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM logs");

            while (rs.next()) {
                out.println("ID: " + rs.getInt(1) +
                        " | Type: " + rs.getString(2) +
                        " | Details: " + rs.getString(3) + "<br>");
            }

        } catch (Exception e) {
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (Exception ex) {
                out.println("<p>Error closing resources</p>");
            }
        }

        out.println("</body></html>");
        out.close();
    }
}
