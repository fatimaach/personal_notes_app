package com.personalnotes.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class PersonalNotesTests {
    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;
    private final String TEST_PASSWORD = "Test@12345";
    private final String TEST_NAME = "Test User";

    @BeforeClass
    public void setUp() {
        baseUrl = System.getenv().getOrDefault("APP_URL", "http://localhost:5001");
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ==================== SIGNUP TESTS ====================

    @Test(priority = 1)
    public void testSignupPageLoads() {
        resetSession();
        driver.get(baseUrl + "/signup.html");
        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement signupButton = driver.findElement(By.xpath("//button[contains(text(), 'Sign Up')]"));
        assert emailField.isDisplayed() && passwordField.isDisplayed() && signupButton.isDisplayed() :
            "Signup page did not load";
    }

    @Test(priority = 2)
    public void testSignupWithValidCredentials() throws InterruptedException {
        resetSession();
        String email = newTestEmail();
        signupUser(email, TEST_PASSWORD);
        assert driver.getCurrentUrl().contains("dashboard") :
            "Signup did not redirect after successful registration";
    }

    @Test(priority = 3)
    public void testSignupEmptyEmail() throws InterruptedException {
        resetSession();
        driver.get(baseUrl + "/signup.html");
        driver.findElement(By.id("name")).sendKeys(TEST_NAME);
        driver.findElement(By.id("password")).sendKeys(TEST_PASSWORD);
        driver.findElement(By.xpath("//button[contains(text(), 'Sign Up')]"))
            .click();

        String alertText = waitForAlertText();
        assert "Please fill all fields.".equals(alertText) : "Unexpected alert text: " + alertText;
    }

    @Test(priority = 4)
    public void testSignupEmptyPassword() throws InterruptedException {
        resetSession();
        driver.get(baseUrl + "/signup.html");
        driver.findElement(By.id("name")).sendKeys(TEST_NAME);
        driver.findElement(By.id("email")).sendKeys(newTestEmail());
        driver.findElement(By.xpath("//button[contains(text(), 'Sign Up')]"))
            .click();

        String alertText = waitForAlertText();
        assert "Please fill all fields.".equals(alertText) : "Unexpected alert text: " + alertText;
    }

    @Test(priority = 5)
    public void testSignupWeakPassword() throws InterruptedException {
        resetSession();
        driver.get(baseUrl + "/signup.html");
        driver.findElement(By.id("name")).sendKeys(TEST_NAME);
        driver.findElement(By.id("email")).sendKeys(newTestEmail());
        driver.findElement(By.id("password")).sendKeys("weak");
        driver.findElement(By.xpath("//button[contains(text(), 'Sign Up')]"))
            .click();

        String alertText = waitForAlertText();
        assert "Password must be at least 6 characters long.".equals(alertText) :
            "Unexpected alert text: " + alertText;
    }

    // ==================== LOGIN TESTS ====================

    @Test(priority = 6)
    public void testLoginPageLoads() {
        resetSession();
        driver.get(baseUrl + "/login.html");
        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Login')]"));
        assert emailField.isDisplayed() && passwordField.isDisplayed() && loginButton.isDisplayed() :
            "Login page did not load";
    }

    @Test(priority = 7)
    public void testLoginWithValidCredentials() throws InterruptedException {
        resetSession();
        String email = newTestEmail();
        signupUser(email, TEST_PASSWORD);
        resetSession();
        loginUser(email, TEST_PASSWORD);
        assert driver.getCurrentUrl().contains("dashboard") : "Login did not redirect to dashboard";
    }

    @Test(priority = 8)
    public void testLoginEmptyEmail() throws InterruptedException {
        resetSession();
        driver.get(baseUrl + "/login.html");
        driver.findElement(By.id("password")).sendKeys(TEST_PASSWORD);
        driver.findElement(By.xpath("//button[contains(text(), 'Login')]"))
            .click();

        String alertText = waitForAlertText();
        assert "Please enter email and password.".equals(alertText) :
            "Unexpected alert text: " + alertText;
    }

    @Test(priority = 9)
    public void testLoginEmptyPassword() throws InterruptedException {
        resetSession();
        driver.get(baseUrl + "/login.html");
        driver.findElement(By.id("email")).sendKeys(newTestEmail());
        driver.findElement(By.xpath("//button[contains(text(), 'Login')]"))
            .click();

        String alertText = waitForAlertText();
        assert "Please enter email and password.".equals(alertText) :
            "Unexpected alert text: " + alertText;
    }

    @Test(priority = 10)
    public void testLoginWithInvalidCredentials() throws InterruptedException {
        resetSession();
        driver.get(baseUrl + "/login.html");
        driver.findElement(By.id("email")).sendKeys("invalid@example.com");
        driver.findElement(By.id("password")).sendKeys("wrongpassword");
        driver.findElement(By.xpath("//button[contains(text(), 'Login')]"))
            .click();

        String alertText = waitForAlertText();
        assert "Invalid credentials".equals(alertText) : "Unexpected alert text: " + alertText;
    }

    // ==================== NOTES DASHBOARD TESTS ====================

    @Test(priority = 11)
    public void testDashboardLoadsAfterLogin() throws InterruptedException {
        resetSession();
        String email = newTestEmail();
        signupUser(email, TEST_PASSWORD);
        WebElement dashboardTitle = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//h2[contains(text(), 'Welcome')]")));
        assert dashboardTitle.isDisplayed() : "Dashboard did not load after login";
    }

    @Test(priority = 12)
    public void testAddNoteButtonPresent() throws InterruptedException {
        resetSession();
        signupUser(newTestEmail(), TEST_PASSWORD);
        WebElement saveBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//button[contains(text(), 'Save Note')]")));
        assert saveBtn.isDisplayed() : "Save note button not found on dashboard";
    }

    @Test(priority = 13)
    public void testAddNewNote() throws InterruptedException {
        resetSession();
        signupUser(newTestEmail(), TEST_PASSWORD);
        String title = "Test Note " + System.currentTimeMillis();
        createNote(title, "This is a test note body " + System.currentTimeMillis());

        String pageSource = driver.getPageSource();
        assert pageSource.contains(title) : "Note was not added to dashboard";
    }

    @Test(priority = 14)
    public void testEditNote() throws InterruptedException {
        resetSession();
        signupUser(newTestEmail(), TEST_PASSWORD);
        String title = "Edit Note " + System.currentTimeMillis();
        String updatedTitle = title + " Updated";
        createNote(title, "Original content " + System.currentTimeMillis());

        WebElement noteCard = waitForNoteCard(title);
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(
            noteCard.findElement(By.xpath(".//button[contains(text(), 'Edit')]"))));
        scrollAndClick(editBtn);

        WebElement titleField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("noteTitle")));
        WebElement bodyField = driver.findElement(By.id("noteContent"));
        titleField.clear();
        titleField.sendKeys(updatedTitle);
        bodyField.clear();
        bodyField.sendKeys("Updated note content");

        driver.findElement(By.xpath("//button[contains(text(), 'Save Note')]"))
            .click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            By.id("notesList"), updatedTitle));
        assert driver.getPageSource().contains("Updated note content") : "Note was not updated";
    }

    @Test(priority = 15)
    public void testDeleteNote() throws InterruptedException {
        resetSession();
        signupUser(newTestEmail(), TEST_PASSWORD);
        String title = "Delete Note " + System.currentTimeMillis();
        createNote(title, "To delete " + System.currentTimeMillis());

        WebElement noteCard = waitForNoteCard(title);
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
            noteCard.findElement(By.xpath(".//button[contains(text(), 'Delete')]"))));
        scrollAndClick(deleteBtn);

        String confirmText = waitForAlertText();
        assert "Delete this note?".equals(confirmText) : "Unexpected confirm text: " + confirmText;

        wait.until(ExpectedConditions.not(
            ExpectedConditions.textToBePresentInElementLocated(By.id("notesList"), title)));
    }

    // ==================== HELPER METHODS ====================

    private void resetSession() {
        driver.manage().deleteAllCookies();
        driver.get(baseUrl + "/login.html");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear(); sessionStorage.clear();");
    }

    private String newTestEmail() {
        return "testuser" + System.nanoTime() + "@example.com";
    }

    private void waitForDashboard() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("noteTitle")));
    }

    private void signupUser(String email, String password) {
        driver.get(baseUrl + "/signup.html");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")))
            .sendKeys(TEST_NAME);
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.xpath("//button[contains(text(), 'Sign Up')]"))
            .click();

        waitForDashboard();
    }

    private void loginUser(String email, String password) {
        driver.get(baseUrl + "/login.html");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")))
            .sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.xpath("//button[contains(text(), 'Login')]"))
            .click();

        waitForDashboard();
    }

    private void createNote(String title, String content) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("noteTitle")))
            .sendKeys(title);
        driver.findElement(By.id("noteContent")).sendKeys(content);
        driver.findElement(By.xpath("//button[contains(text(), 'Save Note')]"))
            .click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("notesList"), title));
    }

    private WebElement waitForNoteCard(String title) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[@id='notesList']//div[contains(@class,'card')][.//h5[normalize-space()=" +
                "'" + title + "']]")
        ));
    }

    private void scrollAndClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block: 'center'});", element
        );

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        wait.until(ExpectedConditions.elementToBeClickable(element));

        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private String waitForAlertText() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        return text;
    }
}
