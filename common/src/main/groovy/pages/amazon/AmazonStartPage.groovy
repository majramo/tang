package pages.amazon

import base.AnyPage
import base.TangAssert
import corebase.ISeleniumHelper
import org.graphwalker.exceptions.InvalidDataException
import org.graphwalker.multipleModels.ModelAPI

public class AmazonStartPage extends AnyPage {

    private static final String PAGE_URL = "http://www.amazon.com"
    private static final String ADD_BOOK_TO_CART = "//*[@id = 'bb_atc_button']"
    private static final String SEARCH_INPUT_FIELD = "//*[@id='twotabsearchtextbox']"
    private static final String SEARCH_BUTTON = "//*[@id='nav-bar-inner']//input[@type='submit']"
    private static final String SEARCH_RESUTLS = "//*[contains(@id,'result_')]//h3/a"
    private static final String CUSTOMER_WHO_BOUGHT = "//*[@id='hlb-upsell']/div/h2/i"
    private static final String SHOPPING_CART_LINK = "//*[@id=\"nav-cart\"]"
    private static final String EMPTY_SHOPPING_BAG = "//*[contains(text(),'Your Shopping Cart is empty')]"
    private static final String ITEMS_IN_SHOPPINGBAG = "//*[@id='nav-cart-count']"
    private static final String AMAZON_COM = "Amazon.com"
    private static final String SHOPPING_CART = "Shopping Cart"
    private static final String MBT_BOOK_NAME = "Model-based testing"
    private static final String PRACTICAL_MBT_BOOK_NAME = "Practical Model-Based Testing: A Tools Approach"
    private static final String BOOK_INFO_TITLE = ".//*[@id='btAsinTitle']"
    int expected_num_of_books = 0
    private static ModelAPI modelAPI;
    private int expected_num_of_books = 0

    public AmazonStartPage(final ISeleniumHelper driver, TangAssert tangAssert, ModelAPI modelAPI) {
        super(driver)
        this.modelAPI = modelAPI;
//        driver.setSlowDownOn(2000)
    }

    public AmazonStartPage(final ISeleniumHelper driver, TangAssert tangAssert) {
        super(driver)
    }

    public void load() {
        driver.openUrl(PAGE_URL)
    }

    public void e_AddBookToCart() {
        driver.click(ADD_BOOK_TO_CART)
        expected_num_of_books++
    }

    /**
     * This method implements the Edge 'e_ClickBook'
     */
    public void e_ClickBook() {
        driver.click(SEARCH_RESUTLS + "[contains(@href,'Practical')]");
    }

    /**
     * This method implements the Edge 'e_EnterBaseURL'
     */
    public void e_EnterBaseURL() {
        load()
    }

    /**
     * This method implements the Edge 'e_SearchBook'
     */
    public void e_SearchBook() {
        driver.type(SEARCH_INPUT_FIELD, MBT_BOOK_NAME)
        driver.click(SEARCH_BUTTON, 40)

    }

    public void searchBook() {
        e_SearchBook()
        v_SearchResult()
    }

    /**
     * This method implements the Edge 'e_ShoppingCart'
     */
    public void e_ShoppingCart() {
        driver.click(SHOPPING_CART_LINK)
    }

    /**
     * This method implements the Edge 'e_StartBrowser'
     */
    public void e_StartBrowser() {
    }

    /**
     * This method implements the Vertex 'v_BaseURL'
     */
    public void v_BaseURL() {
        tangAssert.assertTrue(driver.getTitle().matches(AMAZON_COM + " .*"));
    }

    /**
     * This method implements the Vertex 'v_BookInformation'
     */
    public void v_BookInformation() {
        tangAssert.assertTrue(driver.getText(BOOK_INFO_TITLE).contains(PRACTICAL_MBT_BOOK_NAME))
    }

    /**
     * This method implements the Vertex 'v_BrowserStarted'
     */
    public void v_BrowserStarted() {
        tangAssert.assertNotNull(driver);
    }

    /**
     * This method implements the Vertex 'v_OtherBoughtBooks'
     * @throws InterruptedException
     */
    public void v_OtherBoughtBooks() throws InterruptedException {
        sleep(3000)
        driver.isTagAvailable(".//*[@id='confirm-text']")
        tangAssert.assertTrue(driver.isTagAvailable(CUSTOMER_WHO_BOUGHT), "Other books info not found " + CUSTOMER_WHO_BOUGHT)
        tangAssert.assertTrue(driver.isTextPresent(CUSTOMER_WHO_BOUGHT, PRACTICAL_MBT_BOOK_NAME), "Books info not found " + PRACTICAL_MBT_BOOK_NAME)
    }

    /**
     * This method implements the Vertex 'v_SearchResult'
     */
    public void v_SearchResult() {
        tangAssert.assertTrue(driver.isTagAvailable(SEARCH_RESUTLS), "The book tag");
        tangAssert.assertTrue(driver.isTextPresent(SEARCH_RESUTLS, PRACTICAL_MBT_BOOK_NAME), "Book " + PRACTICAL_MBT_BOOK_NAME);

    }

    public void v_ShoppingCartMbt() throws InvalidDataException, InterruptedException {
        verifyTitle()

        Integer expected_num_of_books = Integer.valueOf(modelAPI.getMbt().getDataValue("num_of_books"));

        if (expected_num_of_books == 0) {
            tangAssert.assertTrue(driver.isTagAvailable(EMPTY_SHOPPING_BAG))
            return;
        }

        tangAssert.assertEquals(expected_num_of_books, Integer.parseInt(driver.getText(ITEMS_IN_SHOPPINGBAG)));
    }

    public void v_ShoppingCart() throws InvalidDataException, InterruptedException {
        verifyTitle()

        if (expected_num_of_books == 0) {
            tangAssert.assertTrue(driver.isTagAvailable(EMPTY_SHOPPING_BAG))
            return;
        }

        tangAssert.assertEquals(expected_num_of_books, Integer.parseInt(driver.getText(ITEMS_IN_SHOPPINGBAG)));
    }

    private void verifyTitle() {

        String title = driver.requireTitle(SHOPPING_CART)
        tangAssert.assertTrue(title.contains(AMAZON_COM), "Title <" + title + "> does not match <" + AMAZON_COM + ">");
        tangAssert.assertTrue(title.contains(SHOPPING_CART), "Title <" + title + "> does not match <" + SHOPPING_CART + ">");
    }

    public verifyShoppingCart() {
        e_ShoppingCart()
        v_ShoppingCart()
    }

    public clickOnBook() {
        e_ClickBook()
        v_BookInformation()
    }

    def addBookToCart() {
        e_AddBookToCart()
        v_OtherBoughtBooks()
    }
}


