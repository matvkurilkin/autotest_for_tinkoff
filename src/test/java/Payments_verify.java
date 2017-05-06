/**
 * Created by mkurilkin on 06.05.2017.
 */
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class Payments_verify {
WebDriver driver;
WebDriverWait wait;
String region = "html/body/div[1]/div/div[1]/div[2]/div[1]/div[2]/section/span/div[1]/div/span[2]";
String Button_pay = "//div[6]/div/div/div/div/button";



    @BeforeTest
    public void before() {
        System.setProperty ("webdriver.gecko.driver", "geckodriver.exe");
        driver = new FirefoxDriver ( );
        wait = new WebDriverWait (driver, 30);
    }

    @Test(priority = 0)
    public void test_take_me_to_payments (){
        String Moscow = "//div[2]/div/div/span";
        driver.manage().window().maximize();
        //Переходом по адресу https://www.tinkoff.ru/ загрузить стартовую страницу Tinkoff Bank.
        driver.get ("https://www.tinkoff.ru");

        //Из верхнего меню, нажатием на пункт меню ‘Платежи’, перейти на страницу Платежи.
        //В списке категорий платежей выбрать “Коммунальные платежи”
        OpenPayments (driver, wait);
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (region)));
        final String regionName = driver.findElement (By.xpath (region)).getText ( );

        //Убедиться, что текущий регион – “г. Москва”. (в противном случае выбрать регион “г. Москва” из списка регионов).
        if ("Москве" != regionName) {
            driver.findElement (By.xpath (region)).click ( );
            wait.until (ExpectedConditions.elementToBeClickable (By.xpath (Moscow)));
            driver.findElement (By.xpath (Moscow)).click ( );
        }

        //Со страницы “Выбор провайдера” выбрать 1-ый из списка (Должен быть “ЖКУ-Москва”). Сохранить его наименование (далее “искомый”) и нажатием на соответствующий элемент перейти на страницу “Платеж”.
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath ("//span[2]/a/span")));
        final String Provider = driver.findElement (By.xpath ("//span[2]/a/span")).getText ( );
        driver.findElement (By.xpath ("//span[2]/a/span")).click ( );

        //На странице “Платеж” перейти на вкладку “Оплатить”.
        String Button_know = "//div[2]/div/div/div/div/div/div/button";
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (Button_know)));
        driver.findElement (By.xpath ("//li[2]/span/a/span/span")).click ( );

    }
    @Test(priority = 1)
    public void test_verify_pay_code (){
        //Выполнить проверки на валидные значения для обязательных полей: проверить все текстовые сообщения об ошибке (и их содержимое), которые появляются под соответствующим полем ввода в результате ввода некорректных данных.
        String error_text_wrong_code = "Поле неправильно заполнено";
        String error_text_no_code = "Поле обязательное";
        String locator_payer_code = "//input[@id='payerCode']";
        String error_locator = "//form/div/div/div[2]";

        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (Button_pay)));
        driver.findElement (By.xpath (Button_pay)).click ( );
        //Проверка на пустое поле
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (error_locator)));
        assertTrue (driver.findElement (By.xpath (error_locator)).getText ( ).contains (error_text_no_code));

        //Проверка при неправильно введенном коде
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (locator_payer_code)));
        driver.findElement(By.xpath(locator_payer_code)).clear();
        driver.findElement(By.xpath(locator_payer_code)).sendKeys("123");
        driver.findElement (By.xpath (Button_pay)).click ( );
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (error_locator)));
        assertTrue (driver.findElement (By.xpath (error_locator)).getText ( ).contains (error_text_wrong_code));
    }
    @Test(priority = 2)
    public void test_verfy_date (){
        String error_text_empty_date = "Поле обязательное";
        String error_text_wrong_date = "Поле заполнено некорректно";
        String locator_period = "//input[@name='provider-period']";
        String error_locator = "//form/div[2]/div/div[2]";

        //Проверка на пустое поле
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (error_locator)));
        assertTrue (driver.findElement (By.xpath (error_locator)).getText ( ).contains (error_text_empty_date));
        // Проверка на неправильное заполнение
        driver.findElement(By.xpath(locator_period)).clear();
        driver.findElement(By.xpath(locator_period)).sendKeys("132017");
        driver.findElement (By.xpath (Button_pay)).click ( );
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (error_locator)));
        assertTrue (driver.findElement (By.xpath (error_locator)).getText ( ).contains (error_text_wrong_date));
    }
    @Test(priority = 3)
    public void Test_verify_pay_value (){
        String error_text_empty_pay = "Поле обязательное";
        String error_text_low_pay = "Минимальная сумма перевода - 10";
        String error_text_high_pay = "Максимальная сумма перевода - 15 000";
        String error_locator = "//div[4]/div/div/div/div/div/div/div/div[2]";
        String locator_name = "//div/div/div/div/div/label/div/input";

        //Проверка на пустое поле
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (error_locator)));
        assertTrue (driver.findElement (By.xpath (error_locator)).getText ( ).contains (error_text_empty_pay));

        // Проверка на маленькую сумму
        driver.findElement(By.xpath(locator_name)).clear();
        driver.findElement(By.xpath(locator_name)).sendKeys("9");
        driver.findElement (By.xpath (Button_pay)).click ( );
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (error_locator)));
        assertTrue (driver.findElement (By.xpath (error_locator)).getText ( ).contains (error_text_low_pay));

        // Проверка на большую сумму
        driver.findElement(By.xpath(locator_name)).clear();
        driver.findElement(By.xpath(locator_name)).sendKeys("999999");
        driver.findElement (By.xpath (Button_pay)).click ( );
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (error_locator)));
        assertTrue (driver.findElement (By.xpath (error_locator)).getText ( ).contains (error_text_high_pay));
    }
    @Test(priority = 4)
    public void verify_provider_first (){
        String first_element = "//span/div/div/div[2]/div";

        //Повторить шаг (2).
        OpenPayments (driver, wait);
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (region)));
        driver.findElement (By.xpath ("//div/span/span")).click ( );

        //В строке быстрого поиска провайдера ввести наименование искомого провайдера.
        final String Provider = driver.findElement (By.xpath ("//span[2]/a/span")).getText ( );
        driver.findElement (By.xpath ("//div/span/span")).sendKeys (Provider);
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (first_element)));
        //Убедиться, что в списке предложенных провайдеров искомый провайдер первый.
        assertTrue (driver.findElement (By.xpath (first_element)).getText ( ).contains (Provider));
    }
    @Test(priority = 5)
    public void verify_same_page (){
        String first_element = "//span/div/div/div[2]/div";
        String verify_title = "//li/span/a/span/span";
        String verify_text = "Узнать задолженность за ЖКУ в Москве";

        //Нажатием на элемент, соответствующий искомому провайдеру, перейти на страницу “Платеж”.
        driver.findElement (By.xpath (first_element)).click ();

        //Убедиться, что загруженная страница та же, что и страница, загруженная в результате шага (5).
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (verify_title)));
        String Button_know = "//div[2]/div/div/div/div/div/div/button";
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (Button_know)));

    }
    @Test(priority = 6)
    public void SPB_verify_no_provider (){
        String SPB = "//div[2]/div/div[2]/span";
        String verify_xpath = "//span[2]/a/span";
        //Выполнить шаги (2) и (3).
        OpenPayments (driver, wait);
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (region)));
        driver.findElement (By.xpath (region)).click ( );

        //В списке регионов выбрать “г. Санкт-Петербург”.
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (SPB)));
        driver.findElement (By.xpath (SPB)).click ( );
        wait.until (ExpectedConditions.elementToBeClickable (By.xpath (verify_xpath)));

        //Убедится, что в списке провайдеров на странице “Выбор провайдера” отсутствует искомый провайдер.
        assertFalse(driver.findElement(By.cssSelector("BODY")).getText().matches("ЖКУ-Москва"));


    }
    @Test(priority = 7)
    public void close (){
        driver.quit ();
    }

        public void OpenPayments(WebDriver driver, WebDriverWait wait) {
            driver.findElement (By.xpath ("//div[3]/a/span")).click ( );
            wait.until (ExpectedConditions.elementToBeClickable (By.xpath ("//li[2]/span[2]/a/span")));
            driver.findElement (By.xpath ("//li[2]/span[2]/a/span")).click ( );

        }
    }

