package Server.packet;

import Server.ServerLoader;

import java.sql.*;

public class MessageArray {
    private static String[] messages = new String[50];
    private static int start = 0;
    private static int end = 0;

    public static void addMessage(String data){
        Connection conn = null;

        try {

            conn = DriverManager.getConnection(ServerLoader.DBurl);

            PreparedStatement stmt = conn.prepareStatement
                    ("insert into MESSAGES values(?)");

            stmt.setString(1, data);


            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static String getMessages(){
        Connection conn = null;
        StringBuffer out = new StringBuffer();
        try {

            conn = DriverManager.getConnection(ServerLoader.DBurl);
            String sql = "SELECT *" +
                    "FROM MESSAGES;";
            PreparedStatement stmt = conn.prepareStatement
                    (sql);




            ResultSet res = stmt.executeQuery();
            while(res.next()) {
                out.append(res.getString("MESSAGE") + "\n");

            }

            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return out.toString();
    }
}
