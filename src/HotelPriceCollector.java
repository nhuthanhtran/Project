import java.util.Map;

/**
 *  Class: HotelPriceCollector
 *  @author Thanh Tran, Lamorse Early
 *  @version 1.0
 *  Course: ITEC 4260
 *  Written: December 2, 2024.
 *
 * This class serves as the entry point for collecting and storing hotel price data.
 * It integrates with the `HotelDatabase` and `HotelPriceScreener` classes to scrape
 * hotel price information for specified hotels and cities, save the data into a
 * SQLite database, and query the lowest prices.
 */
public class HotelPriceCollector {
    private static HotelDatabase database;
    private static HotelPriceScreener screener;

    /**
     * Main method that orchestrates the scraping and database storage process for multiple hotels and cities.
     *
     * @param args command-line arguments.
     * @throws Exception if any error occurs during the scraping or database operations.
     */
    public static void main(String[] args) throws Exception {
        database = new HotelDatabase();
        screener = new HotelPriceScreener();

        // Array of hotels to collect price data for.
        String[] hotels = {
                "The Ritz-Carlton",
                "Four Seasons Hotel",
                "Hilton Garden Inn",
                "Marriott Marquis",
                "Hyatt Regency"
        };

        // Array of cities to collect price data for.
        String[] cities = {
                "New York",
                "Los Angeles",
                "Chicago",
                "San Francisco",
                "Miami"
        };

        for (String hotel : hotels) {
            for (String city : cities) {
                Map<String, Integer> data = screener.gatherHotelData(hotel, city);
                String tableName = hotel + "_" + city;
                tableName = tableName.replaceAll("[^a-zA-Z0-9_]", "_");
                database.createTable(tableName);
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    database.addRecordToTable(tableName, entry.getKey(), entry.getValue());
                }
                database.getLowestPrices(tableName);
            }
        }
        screener.cleanUp();
        database.closeConnection();
    }
}
