package ru.r5am;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by Aleksandr Jashhuk (Zoer) on 16.08.2016.
 * Класс содержит методы для логирования через ЕИСА
 */
class AuthESIA {

    /**
     *  Переход на страницу авторизации по СНИЛС
     *  @param driver - экземпляр WebDriver
     */
     void goToPageSNILS(WebDriver driver) {
         // Нажать кнопку 'Личный кабинет'
         WebElement personalOfficeButton = driver.findElement(By.xpath(
                 "//a[@href='lk/main/login/entry.htm' and text()='Личный кабинет']"
         ));
         personalOfficeButton.click();

         // Нажать кнопку 'авторизация по есиа'
         WebElement authESIAButton = driver.findElement(By.xpath(
                 "//a[@href='saml/login' and text()='авторизация по есиа']"
         ));
         authESIAButton.click();

         // Переход по ссылке 'Вход с помощью: СНИЛС'
         WebElement clickSNILSLink = driver.findElement(By.xpath(
                 "//*[@id='authnFrm']/*/a[@data-bind='visible: snils.canSwitchTo, click: toSnils']"
         ));
         clickSNILSLink.click();
    }

    /**
     * Ввод СНИЛС-а
     * @param driver - экземпляр WebDriver
     * @param sNILNumbers - СНИЛС пользователя
     */
    void inputSNILS(WebDriver driver, String sNILNumbers) {
        WebElement sNILSField = driver.findElement(By.xpath(".//input[@id='snils']"));
        sNILSField.sendKeys("\u0008");      // Послать "забой" - привет FF!
        sNILSField.sendKeys(sNILNumbers);
    }

    /**
     * Ввод пароля
     * @param driver - экземпляр WebDriver
     * @param userPassword - пароль пользователя
     */
    void inputPassword(WebDriver driver, String userPassword) {
        WebElement passwordField = driver.findElement(By.xpath(".//input[@id='password']"));
        passwordField.sendKeys(userPassword);
    }

    /**
     * Нажатие кнопки 'Войти'
     * @param driver - экземпляр WebDriver
     */
    void setInputESIAButton(WebDriver driver) {
        WebElement inputESIAButton = driver.findElement(By.xpath("//button[@data-bind='click: loginByPwd']"));
        inputESIAButton.click();
    }
}
