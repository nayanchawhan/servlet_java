import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class LogsApiServlet extends HttpServlet {

    private Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection("jdbc:postgresql://db:5432/intrusion_db", "postgres", "postgres");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        
        try {
            con = getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT id, type, details FROM logs ORDER BY id ASC");
            
            out.print("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    out.print(",");
                }
                out.print("{");
                out.print("\"id\":" + rs.getInt(1) + ",");
                out.print("\"type\":\"" + escapeJson(rs.getString(2)) + "\",");
                out.print("\"details\":\"" + escapeJson(rs.getString(3)) + "\"");
                out.print("}");
                first = false;
            }
            out.print("]");
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        } finally {
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (Exception ex) {
                // Ignore
            }
        }
        out.close();
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String type = request.getParameter("type");
        String details = request.getParameter("details");
        
        if (type == null || details == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Missing type or details parameter\"}");
            out.close();
            return;
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            con = getConnection();
            ps = con.prepareStatement("INSERT INTO logs (type, details) VALUES (?, ?)");
            ps.setString(1, type);
            ps.setString(2, details);
            ps.executeUpdate();
            
            out.print("{\"status\":\"success\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception ex) {
                // Ignore
            }
        }
        out.close();
    }
    
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
