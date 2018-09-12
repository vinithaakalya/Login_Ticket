import java.io.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Reporter;
import org.testng.annotations.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Properties;
import org.w3c.dom.Document;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


import static org.testng.Assert.assertEquals;
import static org.testng.Reporter.*;


public class NewLogin {
    Properties property;
    FileInputStream fs;
    String userid;
    String password;
    int temp;
   static HashMap<String, Tag> map = new HashMap<>();
   static private WebDriver driver =null;

    public void getValue() throws IOException {
        //PropertiesFile propertiesFile = new PropertiesFile()
        fs = new FileInputStream("/Users/vinitha/Downloads/Login_Ticket/src/test/java/prop.properties");
        property = new Properties();
        property.load(fs);
         userid = property.getProperty("userid");
         password = property.getProperty("password");
         if(userid!=null&&password!=null)
        log("Username and Password is taken from the property file");
    }

    @BeforeClass
    void createDriver()
    {
        System.out.println("before class");
        System.setProperty("webdriver.chrome.driver", "/Users/vinitha/Downloads/chromedriver");
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
        desiredCapabilities.setCapability("marionette", true);
        driver = new ChromeDriver(desiredCapabilities);
        log("Path to Chrome Driver is set");

    }

    @BeforeMethod
    public void webSiteLogin()
    {
        try {

            File fXmlFile = new File("/Users/vinitha/Downloads/Login_Ticket/src/test/java/testing.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(fXmlFile);
            document.getDocumentElement().normalize();
            Node rootNode = document.getElementsByTagName("Locators").item(0);
            Element rootElement = (Element) rootNode;
            NodeList NList = rootElement.getElementsByTagName("Locator");
            for (temp = 0; temp < NList.getLength(); temp++) {
                Node nNode = NList.item(temp);
                Tag tag = new Tag();
                Element eElement = (Element) nNode;
                String title=eElement.getAttribute("title");
                tag.locatorType = eElement.getFirstChild().getNextSibling().getNodeName();
                tag.locatorValue = eElement.getFirstChild().getNextSibling().getTextContent();
                map.put(title, tag);
               // System.out.println("locator type :" + tag.locatorType);
                //System.out.println("locator value :" + tag.locatorValue);
                //System.out.println("title " + eElement.getAttribute("title"));
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        driver.manage().window().fullscreen();
        driver.navigate().to("https://www.tiket.com/");
        log("Successfully Logged into the website");
    }
     @Test
     public void newUser() throws IOException {
       getValue();
       getElement("loginbtn").click();;
       getElement("registerbtn").click();
       getElement("nameRegfield").sendKeys("Vinitha Nataraj");
       getElement("EmailRegfield").sendKeys(userid);
       getElement("passwordRegfield").sendKeys(password);
       getElement("passwordConform").sendKeys(password);
       getElement("registrButton").click();
       log("Successfully Registered");
   }
    @Test
    public void userLogin() throws IOException {
        getValue();
        getElement("loginbtn").click();
       // driver.findElement(By.xpath("//*[@id=\"login\"]/form/div[1]/input")).click();
        getElement("EmailLogin").sendKeys(userid);
        getElement("passwordLgn").sendKeys(password);
        getElement("logBtn").click();
        log("Successfully Logged in");
    }
    @Test
    public void userLogOut() throws IOException {
        userLogin();
        getElement("scrollBtn").click();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        getElement("logOutBtn").click();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        log("Successfully LoggedOut");
    }


    WebElement getElement(String title){
       WebElement webElement;
        Tag tag1 = map.get(title);
       //System.out.println(tag1.locatorType);
      String locatorType = tag1.locatorType;
       switch (locatorType){
           case "id" : webElement = driver.findElement(By.id(tag1.locatorValue));
                break;
           case "xpath":
               webElement=driver.findElement(By.xpath(tag1.locatorValue));
               break;
           case "className":
               webElement=driver.findElement(By.className(tag1.locatorValue));
               break;
           case "cssSelector":
               webElement=driver.findElement(By.cssSelector(tag1.locatorValue));
               break;
           case "name":
               webElement=driver.findElement(By.name(tag1.locatorValue));
               break;
           case "tagName":
                webElement=driver.findElement(By.tagName(tag1.locatorValue));
                break;
           default:
               webElement=driver.findElement(By.xpath(tag1.locatorValue));
       }
        log("webElement is generated");
       return webElement;

   }




   @AfterTest
    public void quit()
   {
     driver.quit();
     log("Successfully quited");
   }

}
