import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Class: HotelPriceScreenerTest
 *  @author Thanh Tran, Lamorse Early
 *  @version 1.0
 *  Course: ITEC 4260
 *  Written: December 2, 2024.
 *
 * Tests the functionality of the HotelPriceScreener class, particularly its ability
 * to scrape hotel price data from Google Travel and its proper resource cleanup.
 */
public class HotelPriceScreenerTest {

    private HotelPriceScreener screener;

    /**
     * Sets up the WebDriver and initializes the HotelPriceScreener before each test.
     */
    @Before
    public void setUp() {
        screener = new HotelPriceScreener();
    }

    /**
     * Tests the gatherHotelData method with a sample hotel and city.
     * Validates that the returned data map is not null and contains entries.
     */
    @Test
    public void testHiltonNewYorkHotelData() throws Exception {
        String hotel = "Hilton Garden Inn";
        String city = "New York";

        Map<String, Integer> hotelData = screener.gatherHotelData(hotel, city);

        assertNotNull("The hotel data map should not be null.", hotelData);
        assertFalse("The hotel data map should not be empty.", hotelData.isEmpty());

        for (Map.Entry<String, Integer> entry : hotelData.entrySet()) {
            System.out.println("Date: " + entry.getKey() + ", Price: $" + entry.getValue());
        }

        System.out.println("Top 10 lowest prices of " + hotel + " in " + city + " :");
        printTop10LowestPrices(hotelData);
    }

    /**
     * Helper method to get and print the 10 lowest non-negative prices from the hotel data map.
     *
     * @param hotelData the map containing hotel data with dates and prices.
     */
    public void printTop10LowestPrices(Map<String, Integer> hotelData) {
        List<Map.Entry<String, Integer>> top10LowestPrices = hotelData.entrySet().stream()
                .filter(entry -> entry.getValue() >= 0)
                .sorted(Map.Entry.comparingByValue())
                .limit(10)
                .collect(Collectors.toList());

        for (Map.Entry<String, Integer> entry : top10LowestPrices) {
            System.out.println("Date: " + entry.getKey() + ", Price: $" + entry.getValue());
        }
    }

    public void testHiltonSanFranciscoHotelData() throws Exception {
        String hotel = "Hilton Garden Inn";
        String city = "San Francisco";

        Map<String, Integer> hotelData = screener.gatherHotelData(hotel, city);

        assertNotNull("The hotel data map should not be null.", hotelData);
        assertFalse("The hotel data map should not be empty.", hotelData.isEmpty());

        for (Map.Entry<String, Integer> entry : hotelData.entrySet()) {
            System.out.println("Date: " + entry.getKey() + ", Price: $" + entry.getValue());
        }

        System.out.println("Top 10 lowest prices of " + hotel + " in " + city + " :");
        printTop10LowestPrices(hotelData);
    }

    /**
     * Cleans up resources and closes the WebDriver after each test.
     */
    @After
    public void tearDown() throws Exception {
        if (screener != null) {
            screener.cleanUp();
        }
    }
}

