import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;

// Class for Intrusion Log
class IntrusionLog {
    int logId;
    String intrusionType;
    String details;

    public IntrusionLog(int logId, String intrusionType, String details) {
        this.logId = logId;
        this.intrusionType = intrusionType;
        this.details = details;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Log ID: ").append(logId)
          .append(" | Type: ").append(intrusionType)
          .append(" | Details: ").append(details);
        return sb.toString();
    }
}

// Main Class
public class IntrusionDetectionApp {

    ArrayList<IntrusionLog> logs = new ArrayList<>();
    JTextArea outputArea;

    // 🔹 JDBC Method
    public void insertLogToDB(IntrusionLog log) {
        try {
            java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
            Class.forName("org.postgresql.Driver");

            Connection con = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/intrusion_db",
                "postgres",
                "postgres"
            );

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO logs (type, details) VALUES (?, ?)");

            ps.setString(1, log.intrusionType);
            ps.setString(2, log.details);


            ps.executeUpdate();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            // JOptionPane.showMessageDialog(null, "DB Error: " + e);
        }
    }

    public void loadLogsFromDB() {
        logs.clear();
        try {
            java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/intrusion_db", "postgres", "postgres");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, type, details FROM logs");
            while (rs.next()) {
                logs.add(new IntrusionLog(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Constructor (GUI)
    public IntrusionDetectionApp() {
        loadLogsFromDB();
        JFrame frame = new JFrame("Intrusion Detection Log System");
        frame.setLayout(new BorderLayout());

        outputArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(outputArea);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton addBtn = new JButton("Add Log");
        JButton showBtn = new JButton("Show Failed Logins");
        JButton sortBtn = new JButton("Sort Logs");

        panel.add(addBtn);
        panel.add(showBtn);
        panel.add(sortBtn);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        // 🔹 Add Log Button
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(frame,
                        "Enter Log (Example: INTRUSION: Multiple failed login attempts)");

                try {
                    String type = input.substring(0, input.indexOf(":"));
                    String details = input.substring(input.indexOf(":") + 2);

                    IntrusionLog log = new IntrusionLog(logs.size() + 1, type, details);
                    logs.add(log);

                    insertLogToDB(log); // 🔥 JDBC call

                    outputArea.append("Log Added & Stored in DB\n");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid Input!");
                }
            }
        });

        // 🔹 Show Failed Logs
        showBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outputArea.setText("");

                for (IntrusionLog log : logs) {
                    if (log.details.toLowerCase().contains("failed login")) {
                        outputArea.append(log.toString() + "\n");
                    }
                }
            }
        });

        // 🔹 Sort Logs
        sortBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Collections.sort(logs, new Comparator<IntrusionLog>() {
                    public int compare(IntrusionLog a, IntrusionLog b) {
                        return a.intrusionType.compareTo(b.intrusionType);
                    }
                });

                outputArea.setText("Sorted Logs:\n");
                for (IntrusionLog log : logs) {
                    outputArea.append(log.toString() + "\n");
                }
            }
        });

        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Main Method
    public static void main(String[] args) throws Exception {
        new IntrusionDetectionApp();
    }
}