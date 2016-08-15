package ru.r5am;

// Created by Zoer on 13.08.2016.

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SemiManualRunner {
    public static void main(String[] args) {

        // Расположение chromedriver.exe
        String PATH_TO_CHROMEDRIVER_EXE = "src\\main\\resources\\web_drivers\\chromedriver.exe";

        // Форматируем дату и время, выставляем временнУю зону Москвы
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        dateFormat.setTimeZone(tz);

        // Обработка аргументов командной строки (args4j)
        CommandLineArguments arguments = new CommandLineArguments();
        CmdLineParser parser = new CmdLineParser(arguments);

        try {
            parser.parseArgument(args);                         // Парсинг аргументов
        } catch (CmdLineException e) {
            System.err.println("e = " + e.toString());
            parser.printUsage(System.out);
        }

        if(arguments.production)
            System.out.println("--prod flag is set.");

        if(arguments.firefox)
            System.out.println("--firefox flag is set.");

//        System.out.println("Другие аргументы:");
//        for( String s : arguments.extraArgs )
//            System.out.println(s);


        System.out.println("Начало работы: " + dateFormat.format(new Date()));

        System.setProperty("webdriver.chrome.driver", PATH_TO_CHROMEDRIVER_EXE);   // Выставить путь к ChromeDriver

        // Опции командной строки браузера
        ChromeOptions option = new ChromeOptions();
        option.addArguments("--window-size=1024,768");      // Но больше текущего разрешения экрана не делает окно

        WebDriver driver = new ChromeDriver(option);      // Драйвер Chrome
        // driver.manage().window().maximize();            // Максимизировать размер окна браузера



        // Посетить страницу
        driver.get("http://google.com");





        driver.quit();  // Покинуть driver, закрыть связанные с ним окна
        System.out.println("Окончание работы: " + dateFormat.format(new Date()));
    }
}
