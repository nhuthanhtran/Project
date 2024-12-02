import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HotelPriceScreener {
    private static WebDriver driver;

    public HotelPriceScreener() {
        driver = new ChromeDriver();
    }

    public Map<String, Integer> gatherHotelData(String hotel, String city) throws Exception {
        Map<String, Integer> hotelData = new LinkedHashMap<>();
        String url = "https://www.google.com/travel/search?q=" + hotel + "+" + city;
        driver.get(url);
        Thread.sleep(1000);
        try {
        WebElement hotelLink = driver.findElement(By.xpath("//*[@id=\"id\"]/c-wiz/c-wiz[3]/div/a"));
        if (hotelLink.isDisplayed()) {
            hotelLink.click();
            Thread.sleep(1000);
        } } catch (Exception e) {
            System.out.println("Hotel Link Not Found");
        }

        WebElement priceButton = driver.findElement(By.xpath("//span[text()='Prices']"));
        priceButton.click();
        Thread.sleep(2000);

        WebElement checkinDate = driver.findElement(By.xpath("//*[@id=\"prices\"]/c-wiz[1]/c-wiz[1]/div/section/div[1]/div[1]/div/div[2]/div[1]/div/input"));
       checkinDate.click();
            List<WebElement> dailyPrice = driver.findElements(By.xpath("//div[contains(@class, 'p1BRgf')]"));
            WebElement departureDate  = driver.findElement(By.xpath("//div[contains(@aria-label, 'departure date')]"));
            if (departureDate.isDisplayed()) {
                departureDate.click();
                System.out.println("null departure date");
                Thread.sleep(2000);
            }
            System.out.println(dailyPrice.size());
            for (int i = 0; i < dailyPrice.size(); i++) {
                String date = dailyPrice.get(i).findElement(By.xpath(".//div[@jsname='nEWxA']")).getAttribute("aria-label").replace(", departure date.", "");
                String rate = dailyPrice.get(i).findElement(By.xpath(".//div[@jsname='qCDwBb']")).getText();
                int price = -1;
                if (rate.contains("$")) {
                    price = Integer.parseInt(rate.replace("$", "").replaceAll(",", ""));
                }
                hotelData.put(date, price);
                System.out.println(date + " " + rate);

                //WebElement nextButton = driver.findElement(By.xpath("//div[@class='VfPpkd-RLmnJb']"));
                //<div class="VfPpkd-RLmnJb"></div>
                //#ow14 > div.fSZK0b.DzHPKc.iWO5td > div > div.g3VIld.pqCVeb.XkOXPd.yXO5Pd.AjYsGd.k57Mic.J9Nfi.iWO5td > div.PbnGhe.oJeWuf.fb0g6 > div.qxcyof.RNniQb > div > div > div.d53ede.rQItBb.FfP4Bc.Gm3csc > div > div > button > svg
                WebElement nextButton = driver.findElement(By.cssSelector("div.qxcyof.RNniQb > div > div > div.d53ede.rQItBb.FfP4Bc.Gm3csc > div > div > button > div.VfPpkd-RLmnJb"));
                //<span jsname="V67aGc" class="VfPpkd-vQzf8d"></span>
                //WebElement nextButton = driver.findElement(By.xpath("//*[@id=\"ow11\"]/div[2]/div/div[2]/div[3]/div[1]/div/div/div[3]/div/div/button/div[3]"));
                if (i % 30 == 0) {
                    if (nextButton.isDisplayed()) {
                        nextButton.click();
                        Thread.sleep(2000);
                    }
                }
            }
            return hotelData;
    }

    public void cleanUp() throws Exception {
        driver.quit();
        Thread.sleep(1000);
    }
}
