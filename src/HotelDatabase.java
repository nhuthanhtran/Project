import java.sql.*;

public class HotelDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:hotel_data.db";
    private Connection conn;

    public HotelDatabase() {
            try {
                conn = DriverManager.getConnection(DATABASE_URL);
                if (conn != null) {
                    System.out.println("Connected to the database.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void createTable(String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " date TEXT NOT NULL UNIQUE,"
                + " price INTEGER"
                + ");";
        try  {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("Table '" + tableName + "' created successfully.");
        } catch (Exception e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public void addRecordToTable(String tableName, String date, int price) {
        String sql = "INSERT INTO " + tableName + " (date, price) \n"
                + "VALUES (?, ?)\n"
                + "ON CONFLICT(date) DO UPDATE SET price = excluded.price;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, date);
            pstmt.setInt(2, price);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error adding or updating record: " + e.getMessage());
        }
    }

    public void getLowestPrices(String tableName) {
        String sql = "SELECT date, price FROM \"" + tableName + "\" "
                + "WHERE price >= 0 "
                + "ORDER BY price ASC "
                + "LIMIT 10;";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String date = rs.getString("date");
                int price = rs.getInt("price");
                System.out.println("Date: " + date + ", Price: $" + price);
            }
        } catch (Exception e) {
            System.out.println("Error querying table: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Connection closed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
