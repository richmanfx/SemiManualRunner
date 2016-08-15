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

        // Обрабатываем аргументы командной строки
        CommandLineArguments arguments = getCommandLineArguments(args);

        // Имя файла тестового скрипта
        String testScriptNameFile = "U" + arguments.testNumber + ".online.xml";
        // System.out.println("Имя файла тестового скрипта: " + testScriptNameFile);

        // Обработка XML-конфигов
        if(arguments.production) {        // Работа на продакшн сервере
            System.out.println("Работаем на продакшн сервере.");
            // Считываем конфиги
            String pathToProdConfigs = "src\\main\\resources\\prod_configs\\";
        } else {
            System.out.println("Работаем на тестовом сервере");
            // Считываем конфиги
            String pathToTestConfigs = "src\\main\\resources\\test_configs\\";
            // Загрузить конфиг в объект Document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);        // Никогда не забывай об этом!
            DocumentBuilder builder = factory.newDocumentBuilder();
//            System.out.println("Файл: " + pathToTestConfigs + ONLINE_SETTINGS_CONFIG_FILE);
            Document doc = builder.parse(pathToTestConfigs + ONLINE_SETTINGS_CONFIG_FILE);
            // Создать XPathFactory
            XPathFactory myXPathFactory = XPathFactory.newInstance();
            // Используется эта фабрика для создания объекта XPath
            XPath xpath = myXPathFactory.newXPath();
            // XPath компилирует XPath-выражение
            XPathExpression expr = xpath.compile("//*");
            // Выполнить запрос XPath для получения результата
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            // сохранить результат в DOM NodeList
            NodeList nodes = (NodeList) result;

            // Выводим результат - значения параметров
            for (int i = 0; i < nodes.getLength() ; i++) {
                System.out.println(nodes.item(i).getLocalName() + ": " + nodes.item(i).getTextContent() );
            }

        }

        System.out.println("Начало работы: " + dateFormat.format(new Date()));

        System.setProperty("webdriver.chrome.driver", PATH_TO_CHROMEDRIVER_EXE);   // Выставить путь к ChromeDriver

        // Опции командной строки браузера
        ChromeOptions option = new ChromeOptions();
        option.addArguments("--window-size=1024,768");      // Но больше текущего разрешения экрана не делает окно

        WebDriver driver = new ChromeDriver(option);      // Драйвер Chrome
        // driver.manage().window().maximize();            // Максимизировать размер окна браузера



        // Посетить страницу
      //  driver.get("http://google.com");





        driver.quit();  // Покинуть driver, закрыть связанные с ним окна
        System.out.println("Окончание работы: " + dateFormat.format(new Date()));
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

        if(arguments.firefox)
            System.out.println("Используется Firefox");
        else
            System.out.println("Используется Chrome (по-умолчанию)");

//        System.out.println("Другие аргументы:");
//        for( String s : arguments.extraArgs )
//            System.out.println(s);
        return arguments;
    }
}
