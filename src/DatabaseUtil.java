import java.sql.*;

public class DatabaseUtil {
    // MySQL 8.0+ 的推荐URL格式（需指定时区）
    private static final String URL = "jdbc:mysql://localhost:3306/student?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Wy050818"; // 替换为你的密码
    
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // MySQL 8.0+ 驱动类名
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
