package project;
import java.io.FileReader;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
 
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
 
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import org.apache.commons.io.FileUtils;
 
public class redifftest {
 
    public static void main(String[] args) {
        runTest("chrome");
        runTest("edge");
    }
 
    public static void runTest(String browser) {
        WebDriver driver = null;
 
        try {
            System.out.println("==============================");
            System.out.println("Switching to " + browser.toUpperCase() + " browser...");
            System.out.println("Started test on: " + browser.toUpperCase());
            System.out.println("==============================");
 
            if (browser.equalsIgnoreCase("chrome")) {
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
            } else if (browser.equalsIgnoreCase("edge")) {
                try {
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                } catch (Exception e) {
                    System.out.println("WebDriverManager failed for Edge. Falling back to manual driver path...");
                    
                }
            } else {
                System.out.println("Unsupported browser: " + browser);
                return;
            }
 
            driver.manage().window().maximize();
 
            // Load URL from config.properties
            FileReader fr = new FileReader("config.properties");
            Properties pro = new Properties();
            pro.load(fr);
            driver.get(pro.getProperty("url"));
 
            // Click on "Create Account" link
            driver.findElement(By.linkText("Create Account")).click();
 
            // Validate page title
            String expectedTitle = "Rediffmail Free Unlimited Storage";
            String actualTitle = driver.getTitle();
            if (actualTitle.contains(expectedTitle)) {
                System.out.println(" Navigated to Create Account page");
            } else {
                System.out.println(" Page validation failed");
            }
 
            // Find all links on the page
            List<WebElement> links = driver.findElements(By.tagName("a"));
            System.out.println("[" + browser + "] Total links on Create Account page: " + links.size());
            for (WebElement link : links) {
                System.out.println(link.getText() + " -> " + link.getAttribute("href"));
            }
 
            // Capture parent window handle
            String parentWindow = driver.getWindowHandle();
 
            // Screenshot of Parent Window
            File parentScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(parentScreenshot, new File("C:\\screenshots\\" + browser + "_parent_window.png"));
            System.out.println("[" + browser + "] Screenshot of parent window captured successfully");
 
            // Click "terms and conditions" link using JavaScript
            WebElement termsLink = driver.findElement(By.xpath("//a[contains(text(),'terms and conditions')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", termsLink);
            Thread.sleep(1000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", termsLink);
 
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(40));
            Set<String> allWindows = driver.getWindowHandles();
 
            for (String childWindow : allWindows) {
                if (!childWindow.equals(parentWindow)) {
                    driver.switchTo().window(childWindow);
 
                    String childTitle = driver.getTitle();
                    System.out.println("[" + browser + "] Child Window Title: " + childTitle);
 
                    if (childTitle.contains("Terms and Conditions")) {
                        System.out.println("[" + browser + "] Child window validated");
                    } else {
                        System.out.println("[" + browser + "] Child window validation failed");
                    }
 
                    // Screenshot of Child Window
                    File childScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    FileUtils.copyFile(childScreenshot, new File("C:\\screenshots\\" + browser + "_child_window.png"));
                    System.out.println("[" + browser + "] Screenshot of child window captured successfully");
 
                    driver.close();
                }
            }
 
            driver.switchTo().window(parentWindow);
            driver.quit();
 
            System.out.println("[" + browser.toUpperCase() + "] browser closed successfully.");
            System.out.println("==============================\n");
 
        } catch (Exception e) {
            System.out.println("[" + browser + "] Exception occurred: " + e.getMessage());
        }
    }
}