import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;

/**
 *  Class: HotelDatabase
 *  @author Thanh Tran, Lamorse Early
 *  @version 1.0
 *  Course: ITEC 4260
 *  Written: December 2, 2024.
 *
 * The HotelDatabase class provides methods to manage and interact with a SQLite database
 * for storing and retrieving hotel price data. It allows for table creation, adding or updating
 * records, querying for the lowest prices, and closing the database connection.
 */
public class HotelDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:hotel_data.db";
    private Connection conn;

    /**
     * Constructor to establish a connection to the SQLite database.
     * If the connection is successful, a message is printed to the console.
     */
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

    /**
     * Method createTable: Creates a table with the specified name in the database.
     *
     * @param tableName the name of the table to be created.
     */
    public void createTable(String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " date TEXT NOT NULL UNIQUE,"
                + " price INTEGER"
                + ");";
        try  {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    /**
     * Method addRecordToTable: Adds or updates a record in the specified table. If a record with
     * the same date already exists, its price will be updated.
     *
     * @param tableName the name of the table to insert or update the record.
     * @param date      the date associated with the price.
     * @param price     the price to be stored.
     */
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

    /**
     * Method getLowestPrices
     * Retrieves and writes the 10 lowest prices from the specified table to a text file.
     * Each time this method is called, the text file is updated with the latest data.
     *
     * @param tableName the name of the table to query.
     */
    public void getLowestPrices(String tableName) {
        String sql = "SELECT date, price FROM \"" + tableName + "\" "
                + "WHERE price >= 0 "
                + "ORDER BY price ASC "
                + "LIMIT 10;";
        StringBuilder output = new StringBuilder();

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            output.append("10 lowest price dates of ").append(tableName).append("\n");

            while (rs.next()) {
                String date = rs.getString("date");
                int price = rs.getInt("price");
                output.append("Date: ").append(date).append(", Price: $").append(price).append("\n");
            }
            output.append("\n");
            writeToFile("lowest_prices.txt", output.toString());
        } catch (Exception e) {
            System.out.println("Error querying table: " + e.getMessage());
        }
    }

    /**
     * Helper method to write a string to a specified text file.
     * Appends the content to the file if it already exists.
     *
     * @param fileName the name of the file to write to.
     * @param content  the content to write to the file.
     */
    private void writeToFile(String fileName, String content) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Helper method to execute the query for testing database manipulation.
     *
     * @param sql
     * @return
     */
    public ResultSet executeQuery(String sql) {
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executes an update query (INSERT, UPDATE, DELETE, or DROP).
     *
     * @param sql the SQL query string to execute.
     * @throws SQLException if a database access error occurs.
     */
    public int executeUpdate(String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }

    /**
     * Closes the connection to the database.
     * If the connection is successfully closed, a message is printed to the console.
     */
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connection closed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
