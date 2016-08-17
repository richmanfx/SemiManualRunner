package ru.r5am;

// Created by Zoer on 13.08.2016.

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class SemiManualRunner {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, InterruptedException {

        // Расположение chromedriver.exe
        String PATH_TO_CHROMEDRIVER_EXE = "src\\main\\resources\\web_drivers\\chromedriver.exe";
        // Имена конфигурационных файлов
        String ONLINE_SETTINGS_CONFIG_FILE = "online.settings.xml";
        String TEST_RUNTIME_CONFIG_FILE = "test.runtime.xml";
        String TEST_PROPERTIES_ONLINE_FILE = "testProperties.online.xml";

        // Форматируем дату и время, выставляем временнУю зону Москвы
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        dateFormat.setTimeZone(tz);

        System.out.println("Начало работы: " + dateFormat.format(new Date()));

        // Обрабатываем аргументы командной строки
        CommandLineArguments arguments = getCommandLineArguments(args);

        // Имя файла тестового скрипта
        String testScriptNameFile = null;
        if(arguments.testNumber != null) {
            testScriptNameFile = "U" + arguments.testNumber + ".online.xml";
            // System.out.println("Имя файла тестового скрипта: " + testScriptNameFile);
        } else {
            System.out.println("\nНе указан номер теста в параметрах командной строки.\n");
            System.exit(1);
        }

        // Обработка XML-конфигов
        String pathToConfigs;
        if(arguments.production) {
            System.out.println("Работаем на продакшн сервере.");
            pathToConfigs = "src\\main\\resources\\prod_configs\\";
        } else {
            System.out.println("Работаем на тестовом сервере");
            pathToConfigs = "src\\main\\resources\\test_configs\\";
        }

        // Проверить наличие конфигурационных файлов
        // TODO: Сделать перебор по списку имён файлов
        fileExist(testScriptNameFile, pathToConfigs);
        fileExist(ONLINE_SETTINGS_CONFIG_FILE, pathToConfigs);
        fileExist(TEST_RUNTIME_CONFIG_FILE, pathToConfigs);
        fileExist(TEST_PROPERTIES_ONLINE_FILE, pathToConfigs);


        // Запускаем требуемый браузер
        WebDriver driver = null;
        try {
            if (arguments.firefox) {
                System.out.println("Используется Firefox");
                driver = startFirefox(arguments.browserResolution);
            } else {
                System.out.println("Используется Chrome (по-умолчанию)");
                driver = startChrome(PATH_TO_CHROMEDRIVER_EXE, arguments.browserResolution);
            }
        } catch (Exception e) {
            System.out.println("Ошибка запуска браузера: " + e.toString());
            System.exit(1);
        }

        // Пока не найдём элемент или 10 секунд (10 сек - для всех, до отмены, глобально)
        if (driver != null) {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }

        // Открыть страницу
        String siteName = getValueFromXMLConfig(testScriptNameFile, pathToConfigs,
                                "ONLINE_DATA_DOCUMENT/PARAMETERS/NAME[text()='serviceDirectLink']/following-sibling::*"
                                               ).split("//")[1].split("/")[0];
        if (driver != null) {
            driver.get("http://" + siteName);

        }

        // Доступен ли сайт?
        siteAvailable(driver);


        // Проверить залогинены ли в личном кабинете и разлогиниться
        boolean status = getLoginStatus(driver);
        if(status) loginOut(driver);       // Разлогиниваемся
/*
        // Логинимся
        AuthESIA login = new AuthESIA();
        login.goToPageSNILS(driver);

        // Получить номер СНИЛСа из XML файла
        String name;    // Разные имена user-а для тестовых и продакшн серверов
        if(arguments.production) {
            name = "userName";
        } else {
            name = "userNameTest";
        }
        String sNILNumbers = getValueFromXMLConfig(TEST_PROPERTIES_ONLINE_FILE , pathToConfigs,
                "ONLINE_DATA_DOCUMENT/PARAMETERS/NAME[text()='" + name + "']/following-sibling::*"
        );
        login.inputSNILS(driver, sNILNumbers);      // Ввести СНИЛС

        // Получить пароль из XML файла
        String password;    // Разные пароли user-а могут быть для тестовых и продакшн серверов
        if(arguments.production) {
            password = "userPassword";
        } else {
            password = "userPasswordTest";
        }
        String userPassword = getValueFromXMLConfig(TEST_PROPERTIES_ONLINE_FILE , pathToConfigs,
                "ONLINE_DATA_DOCUMENT/PARAMETERS/NAME[text()='" + password + "']/following-sibling::*"
        );
        login.inputPassword(driver, userPassword);
        login.setInputESIAButton(driver);           // Нажать кнопку 'Войти'
*/
        // Ждём  N миллисекунд - посмотреть на результат до закрытия браузера
        sleep(5000);



        // Заканчиваем работу приложения
        if (driver != null) {
            driver.quit();  // Покинуть driver, закрыть связанные с ним окна
        }
        System.out.println("Окончание работы: " + dateFormat.format(new Date()));
    }

    /**
     * Проверяет налие файла. Если файла нет, то выход из приложения.
     * @param fileName - имя файла
     * @param pathToFile - путь до файла
     */
    private static void fileExist(String fileName, String pathToFile) {
        File file = new File(pathToFile + fileName);
        if (!(file.exists() && file.isFile())) {
            System.out.println("Файл " + fileName + " не существует.");
            System.exit(1);
        }
    }


    /**
     * Проверяет доступность сайта и, если недоступен, выходит из программы.
     * @param driver Экземпляр WebDriver-а
     */
    private static void siteAvailable(WebDriver driver) {
        // Доступен ли сайт?
        String siteTitle = null;
        if (driver != null) {
            siteTitle = driver.getTitle();
        }
        if (siteTitle != null && siteTitle.contains("недоступен")) {
            System.out.println("Сайт " + siteTitle + ".");
            driver.quit();
            System.exit(1);
        }
    }

    /**
     * Разлогинивает пользователя (пока только admtyumen.ru !!!)
     * @param driver Экземпляр WebDriver-а
     */
    private static void loginOut(WebDriver driver) {
        // Удостовериться, что user залогинен
        WebElement userNameLine = driver.findElement(By.xpath("//span[@class='user__name__line']"));
        if(userNameLine != null) {

            // Нажать кнопку "Выход"
            WebElement exitButton = driver.findElement(By.xpath("//a[@class='btn' and text()='Выход']"));
            exitButton.click();
        }
    }

    /**
     * Проверяет залогинен ли пользователь (пока только admtyumen.ru !!!)
     * @param driver Экземпляр WebDriver-а
     * @return loginStatus Статус залогинености в личном кабинете: true - залогинен
     */
    private static boolean getLoginStatus(WebDriver driver) {
        boolean loginStatus = false;
        // Есть ли кнопка 'Личный кабинет'?
        try {
             driver.findElement(By.xpath("//a[@href='lk/main/login/entry.htm' and text()='Личный кабинет']"
            ));
        } catch (Exception e) {
            System.out.println("Не дождались кнопки 'Личный кабинет'.");
            loginStatus = true;     // Залогинены
        }

        return loginStatus;
    }

    /**
     * Запускает браузер Chrome с максимальным или заданным размером окна
     * @param pathToChromedriverExe - путь к файлу драйвера chromedriver.exe
     * @param browserResolution - требуемый размер окна браузера
     * @return driver - екземпляр WebDriver
     */
    private static WebDriver startChrome(String pathToChromedriverExe, String browserResolution) {
        System.setProperty("webdriver.chrome.driver", pathToChromedriverExe);   // Выставить путь к ChromeDriver
        // Опции командной строки браузера
        ChromeOptions option = new ChromeOptions();
        if(browserResolution != null)
           option.addArguments("--window-size=" + browserResolution); // Более текущего разрешения экрана не делает окно
        WebDriver driver = new ChromeDriver(option);
        if(Objects.equals(browserResolution, null))
            driver.manage().window().maximize();                   // Максимизировать размер окна браузера
        return driver;
    }

    /**
     * Запускает браузер Firefox с максимальным или заданным размером окна
     * @return driver - екземпляр WebDriver
     */
    private static WebDriver startFirefox(String browserResolution) {
        WebDriver driver = new FirefoxDriver();
        if(Objects.equals(browserResolution, null))
            driver.manage().window().maximize();                   // Максимизировать размер окна браузера
        else {
            int x = Integer.parseInt(browserResolution.split(",")[0]);
            int y = Integer.parseInt(browserResolution.split(",")[1]);
            driver.manage().window().setSize(new Dimension(x, y));
        }
        return driver;
    }

/*      Пока не нужен - не понял как искать параметры в DOM-документе
    private static NodeList getXMLConfigNodeList(String ONLINE_SETTINGS_CONFIG_FILE, String pathToConfigs)
                        throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        // Загрузить конфиг в объект Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);        // Никогда не забывай об этом!
        DocumentBuilder builder = factory.newDocumentBuilder();
//            System.out.println("Файл: " + pathToConfigs + ONLINE_SETTINGS_CONFIG_FILE);
        Document doc = builder.parse(pathToConfigs + ONLINE_SETTINGS_CONFIG_FILE);
        // Создать XPathFactory
        XPathFactory myXPathFactory = XPathFactory.newInstance();
        // Используется эта фабрика для создания объекта XPath
        XPath xpath = myXPathFactory.newXPath();
        // XPath компилирует XPath-выражение
        XPathExpression expr = xpath.compile("//*");
        // Выполнить запрос XPath для получения результата
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        // сохранить результат в DOM NodeList
        return (NodeList) result;
    }
*/

    /**
     *
     * @param CONFIG_FILE - XML файл конфигурации
     * @param pathToConfigs - путь к конфигурационному файлу
     * @param xPath - выражение XPath для поиска
     * @return result.toString() - результат запроса XPath
     * @throws ParserConfigurationException -
     * @throws SAXException -
     * @throws IOException -
     * @throws XPathExpressionException -
     */
    private static String getValueFromXMLConfig(String CONFIG_FILE, String pathToConfigs, String xPath)
                    throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        // Загрузить конфиг в объект Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);        // Никогда не забывай об этом!
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(pathToConfigs + CONFIG_FILE);
        // Создать XPathFactory
        XPathFactory myXPathFactory = XPathFactory.newInstance();
        // Используется эта фабрика для создания объекта XPath
        XPath xpath = myXPathFactory.newXPath();
        // XPath компилирует XPath-выражение
        XPathExpression expr = xpath.compile(xPath);
        // Выполнить запрос XPath для получения результата
        Object result = expr.evaluate(doc, XPathConstants.STRING);
        return result.toString();
    }


    /**
     * Обработка аргументов командной строки (args4j)
     * @param args - массив строк с аргументами командной строки приложения
     * @return arguments - экземпляр описания ожидаемых аргументов
     */
    private static CommandLineArguments getCommandLineArguments(String[] args) {

        CommandLineArguments arguments = new CommandLineArguments();
        CmdLineParser parser = new CmdLineParser(arguments);

        try {
            parser.parseArgument(args);                         // Парсинг аргументов
        } catch (CmdLineException e) {
            System.err.println("Ошибка в агументах командной строки: " + e.toString());
            parser.printUsage(System.out);
        }

        if(arguments.testNumber != null)
            System.out.println("Номер теста: " + arguments.testNumber);

//        System.out.println("Другие аргументы:");
//        for( String s : arguments.extraArgs )
//            System.out.println(s);

        return arguments;
    }
}
