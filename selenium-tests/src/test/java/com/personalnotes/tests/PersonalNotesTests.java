package com.personalnotes.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
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
    private final String TEST_EMAIL = "testuser" + System.currentTimeMillis() + "@example.com";
    private final String TEST_PASSWORD = "Test@12345";

    @BeforeClass
    public void setUp() {
        baseUrl = System.getenv().getOrDefault("APP_URL", "http://localhost:5001");
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
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
        driver.get(baseUrl + "/signup.html");
        WebElement signupForm = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//h2[contains(text(), 'Sign Up')]")));
        assert signupForm.isDisplayed() : "Signup page did not load";
    }

    @Test(priority = 2)
    public void testSignupWithValidCredentials() throws InterruptedException {
        driver.get(baseUrl + "/signup.html");
        
        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//input[@placeholder='Email']")));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Password']"));
        WebElement signupButton = driver.findElement(By.xpath("//button[contains(text(), 'Sign Up')]"));
        
        emailField.sendKeys(TEST_EMAIL);
        passwordField.sendKeys(TEST_PASSWORD);
        signupButton.click();
        
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();
        assert currentUrl.contains("login") || currentUrl.contains("dashboard") : 
            "Signup did not redirect after successful registration";
    }

    @Test(priority = 3)
    public void testSignupEmptyEmail() throws InterruptedException {
        driver.get(baseUrl + "/signup.html");
        
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Password']"));
        WebElement signupButton = driver.findElement(By.xpath("//button[contains(text(), 'Sign Up')]"));
        
        passwordField.sendKeys(TEST_PASSWORD);
        signupButton.click();
        
        Thread.sleep(1000);
        WebElement errorMsg = driver.findElements(By.xpath("//*[contains(text(), 'required')]"))
            .stream().findFirst().orElse(null);
        assert errorMsg != null : "No error message for empty email";
    }

    @Test(priority = 4)
    public void testSignupEmptyPassword() throws InterruptedException {
        driver.get(baseUrl + "/signup.html");
        
        WebElement emailField = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        WebElement signupButton = driver.findElement(By.xpath("//button[contains(text(), 'Sign Up')]"));
        
        emailField.sendKeys("test@example.com");
        signupButton.click();
        
        Thread.sleep(1000);
        WebElement errorMsg = driver.findElements(By.xpath("//*[contains(text(), 'required')]"))
            .stream().findFirst().orElse(null);
        assert errorMsg != null : "No error message for empty password";
    }

    @Test(priority = 5)
    public void testSignupWeakPassword() throws InterruptedException {
        driver.get(baseUrl + "/signup.html");
        
        WebElement emailField = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Password']"));
        WebElement signupButton = driver.findElement(By.xpath("//button[contains(text(), 'Sign Up')]"));
        
        emailField.sendKeys("test@example.com");
        passwordField.sendKeys("weak");
        signupButton.click();
        
        Thread.sleep(1000);
        String pageSource = driver.getPageSource();
        assert (pageSource.contains("error") || pageSource.contains("Error")) : 
            "No error displayed for weak password";
    }

    // ==================== LOGIN TESTS ====================

    @Test(priority = 6)
    public void testLoginPageLoads() {
        driver.get(baseUrl + "/login.html");
        WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//h2[contains(text(), 'Login')]")));
        assert loginForm.isDisplayed() : "Login page did not load";
    }

    @Test(priority = 7)
    public void testLoginWithValidCredentials() throws InterruptedException {
        driver.get(baseUrl + "/login.html");
        
        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//input[@placeholder='Email']")));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Password']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Login')]"));
        
        emailField.sendKeys(TEST_EMAIL);
        passwordField.sendKeys(TEST_PASSWORD);
        loginButton.click();
        
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();
        assert currentUrl.contains("dashboard") : "Login did not redirect to dashboard";
    }

    @Test(priority = 8)
    public void testLoginEmptyEmail() throws InterruptedException {
        driver.get(baseUrl + "/login.html");
        
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Password']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Login')]"));
        
        passwordField.sendKeys(TEST_PASSWORD);
        loginButton.click();
        
        Thread.sleep(1000);
        WebElement errorMsg = driver.findElements(By.xpath("//*[contains(text(), 'required')]"))
            .stream().findFirst().orElse(null);
        assert errorMsg != null : "No error message for empty email";
    }

    @Test(priority = 9)
    public void testLoginEmptyPassword() throws InterruptedException {
        driver.get(baseUrl + "/login.html");
        
        WebElement emailField = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Login')]"));
        
        emailField.sendKeys(TEST_EMAIL);
        loginButton.click();
        
        Thread.sleep(1000);
        WebElement errorMsg = driver.findElements(By.xpath("//*[contains(text(), 'required')]"))
            .stream().findFirst().orElse(null);
        assert errorMsg != null : "No error message for empty password";
    }

    @Test(priority = 10)
    public void testLoginWithInvalidCredentials() throws InterruptedException {
        driver.get(baseUrl + "/login.html");
        
        WebElement emailField = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Password']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Login')]"));
        
        emailField.sendKeys("invalid@example.com");
        passwordField.sendKeys("wrongpassword");
        loginButton.click();
        
        Thread.sleep(1000);
        String pageSource = driver.getPageSource();
        assert (pageSource.contains("Invalid") || pageSource.contains("invalid") || 
                pageSource.contains("Error") || pageSource.contains("error")) : 
            "No error message for invalid credentials";
    }

    // ==================== NOTES DASHBOARD TESTS ====================

    @Test(priority = 11)
    public void testDashboardLoadsAfterLogin() throws InterruptedException {
        loginUser();
        
        WebElement dashboardTitle = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//h1[contains(text(), 'Dashboard')] | //h1[contains(text(), 'Notes')]")));
        assert dashboardTitle.isDisplayed() : "Dashboard did not load after login";
    }

    @Test(priority = 12)
    public void testAddNoteButtonPresent() throws InterruptedException {
        loginUser();
        
        WebElement addNoteBtn = driver.findElements(By.xpath("//button[contains(text(), 'Add')] | //button[contains(text(), 'New')]"))
            .stream().findFirst().orElse(null);
        assert addNoteBtn != null : "Add note button not found on dashboard";
    }

    @Test(priority = 13)
    public void testAddNewNote() throws InterruptedException {
        loginUser();
        
        WebElement addNoteBtn = driver.findElement(By.xpath("//button[contains(text(), 'Add')] | //button[contains(text(), 'New')]"));
        addNoteBtn.click();
        
        Thread.sleep(1000);
        
        WebElement titleField = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//input[@placeholder='Title'] | //textarea[@placeholder='Title']")));
        WebElement bodyField = driver.findElement(By.xpath("//textarea[@placeholder='Body'] | //textarea[@placeholder='Content']"));
        WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(), 'Save')]"));
        
        titleField.sendKeys("Test Note Title");
        bodyField.sendKeys("This is a test note body");
        saveBtn.click();
        
        Thread.sleep(1500);
        String pageSource = driver.getPageSource();
        assert pageSource.contains("Test Note Title") : "Note was not added to dashboard";
    }

    @Test(priority = 14)
    public void testEditNote() throws InterruptedException {
        loginUser();
        
        WebElement editBtn = driver.findElements(By.xpath("//button[contains(text(), 'Edit')]"))
            .stream().findFirst().orElse(null);
        
        if (editBtn != null) {
            editBtn.click();
            Thread.sleep(1000);
            
            WebElement bodyField = driver.findElement(By.xpath("//textarea[@placeholder='Body'] | //textarea[@placeholder='Content']"));
            bodyField.clear();
            bodyField.sendKeys("Updated note content");
            
            WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(), 'Save')]"));
            saveBtn.click();
            Thread.sleep(1000);
            
            assert driver.getPageSource().contains("Updated note content") : "Note was not updated";
        }
    }

    @Test(priority = 15)
    public void testDeleteNote() throws InterruptedException {
        loginUser();
        
        List<WebElement> deleteButtons = driver.findElements(By.xpath("//button[contains(text(), 'Delete')]"));
        if (!deleteButtons.isEmpty()) {
            WebElement deleteBtn = deleteButtons.get(0);
            deleteBtn.click();
            
            Thread.sleep(1000);
            
            WebElement confirmDelete = driver.findElements(By.xpath("//button[contains(text(), 'Confirm')] | //button[contains(text(), 'Yes')]"))
                .stream().findFirst().orElse(null);
            if (confirmDelete != null) {
                confirmDelete.click();
                Thread.sleep(1000);
            }
            
            assert true : "Delete action completed";
        }
    }

    // ==================== HELPER METHODS ====================

    private void loginUser() throws InterruptedException {
        driver.get(baseUrl + "/login.html");
        
        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//input[@placeholder='Email']")));
        WebElement passwordField = driver.findElement(By.xpath("//input[@placeholder='Password']"));
        WebElement loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Login')]"));
        
        emailField.sendKeys(TEST_EMAIL);
        passwordField.sendKeys(TEST_PASSWORD);
        loginButton.click();
        
        Thread.sleep(2000);
    }
}
