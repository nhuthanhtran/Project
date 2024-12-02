import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Class: HotelDatabaseTest
 *  @author Thanh Tran, Lamorse Early
 *  @version 1.0
 *  Course: ITEC 4260
 *  Written: December 2, 2024.
 *
 * Tests the functionality of the HotelDatabase class, including table creation,
 * data insertion, querying, and handling edge cases.
 */
public class HotelDatabaseTest {

    private HotelDatabase database;
    private static final String TEST_TABLE = "TestHotelPrices";

    /**
     * Sets up the HotelDatabase instance before each test.
     */
    @Before
    public void setUp() {
        database = new HotelDatabase();
    }

    /**
     * Cleans up resources and closes the database connection after each test.
     */
    @After
    public void tearDown() {
        try {
            database.executeUpdate("DELETE FROM " + TEST_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        database.closeConnection();
    }

    /**
     * General test case for creating a table and inserting data.
     */
    @Test
    public void testCreateTableAndInsertData() {
        database.createTable(TEST_TABLE);

        database.addRecordToTable(TEST_TABLE, "2024-12-01", 150);
        database.addRecordToTable(TEST_TABLE, "2024-12-02", 120);

        try {
            ResultSet rs = database.executeQuery("SELECT * FROM " + TEST_TABLE);
            assertTrue("The table should contain rows.", rs.next());
            assertEquals("The price for 2024-12-01 should be 150.", 150, rs.getInt("price"));
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    /**
     * Corner test case for handling duplicate date entries with the same price.
     */
    @Test
    public void testDuplicateDateInsertionWithSamePrice() {
        database.createTable(TEST_TABLE);

        database.addRecordToTable(TEST_TABLE, "2024-12-01", 150);
        database.addRecordToTable(TEST_TABLE, "2024-12-01", 150);

        try {
            ResultSet rs = database.executeQuery("SELECT COUNT(*) AS count FROM " + TEST_TABLE);
            assertTrue("ResultSet should contain a result.", rs.next());
            assertEquals("There should still only be one record for the duplicate date.", 1, rs.getInt("count"));
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    /**
     * Corner test case for handling updates when a duplicate date has a different price.
     */
    @Test
    public void testDuplicateDateInsertionWithDifferentPrice() {
        database.createTable(TEST_TABLE);

        database.addRecordToTable(TEST_TABLE, "2024-12-01", 150);
        database.addRecordToTable(TEST_TABLE, "2024-12-01", 120); // New price

        try {
            ResultSet rs = database.executeQuery("SELECT price FROM " + TEST_TABLE + " WHERE date = '2024-12-01'");
            assertTrue("ResultSet should contain a result.", rs.next());
            assertEquals("The price for the date 2024-12-01 should be updated to 120.", 120, rs.getInt("price"));
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }

    /**
     * Test case for querying the lowest prices.
     */
    @Test
    public void testGetLowestPrices() {
        database.createTable(TEST_TABLE);

        database.addRecordToTable(TEST_TABLE, "2024-12-01", 200);
        database.addRecordToTable(TEST_TABLE, "2024-12-02", 100);
        database.addRecordToTable(TEST_TABLE, "2024-12-03", 150);

        database.getLowestPrices(TEST_TABLE);
        try {
            ResultSet rs = database.executeQuery("SELECT price FROM " + TEST_TABLE + " ORDER BY price ASC LIMIT 1");
            assertTrue("ResultSet should contain a result.", rs.next());
            assertEquals("The lowest price should be 100.", 100, rs.getInt("price"));
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
    }
}
