package ru.r5am;

// Created by Zoer on 13.08.2016.

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SemiManualRunner {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        // Расположение chromedriver.exe
        String PATH_TO_CHROMEDRIVER_EXE = "src\\main\\resources\\web_drivers\\chromedriver.exe";
        // Имена конфигурационных файлов
        String ONLINE_SETTINGS_CONFIG_FILE = "online.settings.xml";
        String TEST_RUNTIME_CONFIG_FILE = "test.runtime.xml";
        String TESTPROPERTIES_ONLINE_FILE = "testProperties.online.xml";

        // Форматируем дату и время, выставляем временнУю зону Москвы
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        dateFormat.setTimeZone(tz);

        System.out.println("Начало работы: " + dateFormat.format(new Date()));

        // Обрабатываем аргументы командной строки
        CommandLineArguments arguments = getCommandLineArguments(args);

        // Имя файла тестового скрипта
        String testScriptNameFile = "U" + arguments.testNumber + ".online.xml";
        // System.out.println("Имя файла тестового скрипта: " + testScriptNameFile);

        // Обработка XML-конфигов
        String pathToConfigs;
        if(arguments.production) {
            System.out.println("Работаем на продакшн сервере.");
            pathToConfigs = "src\\main\\resources\\prod_configs\\";
        } else {
            System.out.println("Работаем на тестовом сервере");
            pathToConfigs = "src\\main\\resources\\test_configs\\";
        }
        // Считываем конфиги
        NodeList nodesOnlineSettings = getXMLConfigNodeList(ONLINE_SETTINGS_CONFIG_FILE, pathToConfigs);
        NodeList nodesTestRuntime = getXMLConfigNodeList(TEST_RUNTIME_CONFIG_FILE, pathToConfigs);
        NodeList nodesTestPropertiesOnline = getXMLConfigNodeList(TESTPROPERTIES_ONLINE_FILE, pathToConfigs);
        NodeList nodesUTestNumberOnline = getXMLConfigNodeList(testScriptNameFile, pathToConfigs);

/*        // Выводим проверочный результат - значения параметров из XML файла
        for (int i = 0; i < nodesUTestNumberOnline.getLength() ; i++) {
            System.out.println(nodesUTestNumberOnline.item(i).getLocalName() + ": " +
                               nodesUTestNumberOnline.item(i).getTextContent() );
        }
*/

        // Запускаем требуемый браузер
        WebDriver driver = null;
        try {
            if (arguments.firefox) {
                System.out.println("Используется Firefox");
//                driver = startFirefox();
            } else {
                System.out.println("Используется Chrome (по-умолчанию)");
                driver = startChrome(PATH_TO_CHROMEDRIVER_EXE);
            }
        } catch (Exception e) {
            System.out.println("Ошибка запуска браузера: " + e.toString());
        }


        // Открыть страницу
        String siteName = getValueFromXMLConfig(testScriptNameFile, pathToConfigs,
                                "ONLINE_DATA_DOCUMENT/PARAMETERS/NAME[text()='serviceDirectLink']/following-sibling::*"
                                               ).split("//")[1].split("/")[0];

        System.out.println("На сайт: " + siteName);

        driver.get("http://" + siteName);




        if(driver != null)  // Если браузер был успешно запущен
            driver.quit();  // Покинуть driver, закрыть связанные с ним окна

        System.out.println("Окончание работы: " + dateFormat.format(new Date()));
    }

    private static WebDriver startChrome(String pathToChromedriverExe) {
        System.setProperty("webdriver.chrome.driver", pathToChromedriverExe);   // Выставить путь к ChromeDriver

        // Опции командной строки браузера
        ChromeOptions option = new ChromeOptions();
        option.addArguments("--window-size=1024,768");      // Но больше текущего разрешения экрана не делает окно
        WebDriver driver = new ChromeDriver(option);
        driver.manage().window().maximize();            // Максимизировать размер окна браузера
        return driver;
    }

    private static NodeList getXMLConfigNodeList(String ONLINE_SETTINGS_CONFIG_FILE, String pathToConfigs)
                        throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        // Загрузить конфиг в объект Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);        // Никогда не забывай об этом!
        DocumentBuilder builder = factory.newDocumentBuilder();
//            System.out.println("Файл: " + pathToTestConfigs + ONLINE_SETTINGS_CONFIG_FILE);
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

    private static String getValueFromXMLConfig(String ONLINE_SETTINGS_CONFIG_FILE, String pathToConfigs, String xPath)
                    throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        // Загрузить конфиг в объект Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);        // Никогда не забывай об этом!
        DocumentBuilder builder = factory.newDocumentBuilder();
//            System.out.println("Файл: " + pathToTestConfigs + ONLINE_SETTINGS_CONFIG_FILE);
        Document doc = builder.parse(pathToConfigs + ONLINE_SETTINGS_CONFIG_FILE);
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


    // Обработка аргументов командной строки (args4j)
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
