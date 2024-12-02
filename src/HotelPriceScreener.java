import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  Class: HotelPriceScreener
 *  @author Thanh Tran, Lamorse Early
 *  @version 1.0
 *  Course: ITEC 4260
 *  Written: December 2, 2024.
 *
 *  A Selenium-based web scraping utility for gathering hotel price data from Google Travel.
 *  This class automates the process of navigating to the appropriate hotel and city page,
 *  extracting price information, and storing it in a structured format.
 *
 *  Note: The class assumes the use of the ChromeDriver and requires the corresponding
 *  WebDriver binary to be installed and properly set up in the system path.
 *
 */

public class HotelPriceScreener {
    private static WebDriver driver;

    /**
     * Constructor: Initializes the WebDriver instance using ChromeDriver.
     */
    public HotelPriceScreener() {
        driver = new ChromeDriver();
    }

    /**
     * Gathers hotel price data for a specified hotel and city by automating interactions
     * with Google Travel. The method navigates to the search results page, clicks through
     * to the hotel details page, and scrapes price data for multiple dates.
     *
     * @param hotel the name of the hotel to search for.
     * @param city the city where the hotel is located.
     * @return a map containing dates as keys and corresponding hotel prices as values.
     *         Dates with missing prices are mapped to -1.
     * @throws Exception if any Selenium or interruption errors occur during execution.
     */
    public Map<String, Integer> gatherHotelData(String hotel, String city) throws Exception {
        Map<String, Integer> hotelData = new LinkedHashMap<>();
        String url = "https://www.google.com/travel/search?q=" + hotel + "+" + city;

        navigateToUrl(url);
        List<WebElement> dailyPrices = fetchDailyPrices();
        scrapePrices(dailyPrices, hotelData);

        return hotelData;
    }

    /**
     * Helper method to navigate to a given URL and to click the hotel link if it exists.
     *
     * @param url the URL to navigate to.
     * @throws InterruptedException if interrupted during navigation.
     */
    private void navigateToUrl(String url) throws InterruptedException {
        driver.get(url);
        Thread.sleep(1500);
        try {
            WebElement hotelLink = driver.findElement(By.xpath("//*[@id=\"id\"]/c-wiz/c-wiz[3]/div/a"));
            if (hotelLink.isDisplayed()) {
                hotelLink.click();
                Thread.sleep(1500);
            }
        } catch (Exception e) {
            System.out.println("There is only one result for hotel search");
        }
    }

    /**
     * Helper method to open the "Prices" tab on the hotel page and to fetch daily price elements.
     *
     * @return a list of WebElement representing daily price containers.
     * @throws InterruptedException if interrupted during interaction.
     */
    private List<WebElement> fetchDailyPrices() throws InterruptedException  {
        WebElement priceButton = driver.findElement(By.xpath("//span[text()='Prices']"));
        priceButton.click();
        Thread.sleep(1000);
        WebElement checkinDate = driver.findElement(By.xpath("//*[@id=\"prices\"]/c-wiz[1]/c-wiz[1]/div/section/div[1]/div[1]/div/div[2]/div[1]/div/input"));
        checkinDate.click();
        WebElement departureDate  = driver.findElement(By.xpath("//div[contains(@aria-label, 'departure date')]"));
        if (departureDate.isDisplayed()) {
            departureDate.click();
            Thread.sleep(1000);
        }
        List<WebElement> dailyPrice = driver.findElements(By.xpath("//div[contains(@class, 'p1BRgf')]"));
        return dailyPrice;
    }

    /**
     * Helper method to scrape prices and populate the data map.
     *
     * @param dailyPrices the list of daily price elements.
     * @param hotelData   the map to store the date and price data.
     * @throws InterruptedException if interrupted during scraping.
     */
    private void scrapePrices(List<WebElement> dailyPrices, Map<String, Integer> hotelData) throws InterruptedException {
        for (int i = 0; i < dailyPrices.size(); i++) {
            WebElement priceElement = dailyPrices.get(i);
            String date = priceElement.findElement(By.xpath(".//div[@jsname='nEWxA']"))
                    .getAttribute("aria-label")
                    .replace(", departure date.", "");;
            int price = fetchPriceFromElement(priceElement);

            hotelData.put(date, price);

            if (i % 30 == 0) {
                clickNextButton();
            }
        }
    }

    /**
     * Helper method to extract the price from a daily price element.
     *
     * @param element the WebElement representing a daily price.
     * @return the extracted price as an integer, or -1 if the price is unavailable.
     */
    private int fetchPriceFromElement(WebElement element) {
        String rate = element.findElement(By.xpath(".//div[@jsname='qCDwBb']")).getText();
        if (rate.contains("$")) {
            return Integer.parseInt(rate.replace("$", "").replaceAll(",", ""));
        }
        return -1;
    }

    /**
     * Helper method to click the "Next" button to load more prices.
     *
     * @throws InterruptedException if interrupted during interaction.
     */
    private void clickNextButton() throws InterruptedException {
        try {
            WebElement nextButton = driver.findElement(By.cssSelector(
                    "div.qxcyof.RNniQb > div > div > div.d53ede.rQItBb.FfP4Bc.Gm3csc > div > div > button > div.VfPpkd-RLmnJb"
            ));
            if (nextButton.isDisplayed()) {
                nextButton.click();
                Thread.sleep(500);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Next button not found or no more pages available.");
        }
    }

    /**
     * Cleans up resources by quitting the WebDriver instance and closing the browser.
     *
     * @throws Exception if an interruption occurs during cleanup.
     */
    public void cleanUp() throws Exception {
        driver.quit();
        Thread.sleep(1000);
    }
}
