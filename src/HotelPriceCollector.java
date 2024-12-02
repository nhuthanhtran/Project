import java.util.Map;

public class HotelPriceCollector {
    private static HotelDatabase database;
    private static HotelPriceScreener screener;

    public static void main(String[] args) throws Exception {
        database = new HotelDatabase();
        screener = new HotelPriceScreener();
        String[] hotels = {
                "The Ritz-Carlton",
                "Four Seasons Hotel",
                "Hilton Garden Inn",
                "Marriott Marquis",
                "Hyatt Regency"
        };

        String[] cities = {
                "New York",
                "Los Angeles",
                "Chicago",
                "San Francisco",
                "Miami"
        };
//        Map<String, Integer> data = screener.gatherHotelData("Marriott Marquis", "Atlanta");
//        database.createTable("MarriottMarquis_Atlanta");
//        for (Map.Entry<String, Integer> entry : data.entrySet()) {
//            database.addRecordToTable("MarriottMarquis_Atlanta", entry.getKey(), entry.getValue());
//        }
//        database.getLowestPrices("MarriottMarquis_Atlanta");
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
