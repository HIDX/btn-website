import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.io.FileWriter as FileWriter
import org.openqa.selenium.WebDriver as WebDriver
import org.openqa.selenium.Cookie as Cookie

// Buka browser dan navigasi ke SauceDemo
WebUI.openBrowser('')

WebUI.navigateToUrl('https://www.saucedemo.com/')

// Login ke aplikasi
WebUI.setText(findTestObject('Object Repository/Login/Input_Username'), 'standard_user')

WebUI.setEncryptedText(findTestObject('Login/Input_Password'), 'qcu24s4901FyWDTwXGr6XA==')

WebUI.click(findTestObject('Object Repository/Login/Button_Login'))

WebUI.verifyElementPresent(findTestObject('Add_Cart/ProductPage_Header'), 10)

// Ambil data dari Excel
def testData = findTestData('TestDataBelanja1')

// Loop untuk melakukan positive dan negative case
for (int row = 1; row <= testData.getRowNumbers(); row++) {
    def caseType = testData.getValue('CaseType', row)

    def productName = testData.getValue('Produk', row)

    def expectedTotal = testData.getValue('ExpectedTotal', row)

    WebUI.comment("Running $caseType case with product: $productName")

    if (caseType == 'Positive') {
        // Tambahkan produk ke keranjang
        WebUI.click(findTestObject('Object Repository/Add_Cart/btn_addCart_BackPack', [('productName') : productName]))

        // Navigasi ke keranjang
        WebUI.click(findTestObject('Object Repository/Add_Cart/Cart_Button'))

        WebUI.click(findTestObject('Object Repository/Add_Cart/btn_Checkout'))

        // Isi informasi pengiriman
        WebUI.setText(findTestObject('Add_Cart/txt_First_Name'), 'wandi')

        WebUI.setText(findTestObject('Add_Cart/txt_LastName'), 'wu')

        WebUI.setText(findTestObject('Add_Cart/txt_PostalCode'), '2222')

        WebUI.click(findTestObject('Add_Cart/btn_Continue'))

        // Ambil total aktual dan lakukan verifikasi
        def totalItem = WebUI.getText(findTestObject('Object Repository/Add_Cart/txt_Item_total'))

        def tax = WebUI.getText(findTestObject('Object Repository/Add_Cart/txt_Tax'))

        def  actualTotal = WebUI.getText(findTestObject('Object Repository/Add_Cart/txt_Total'))

        // Debugging: tampilkan nilai item total, tax, dan total
        WebUI.comment("Nilai asli itemTotal:$totalItem ")

        WebUI.comment("Nilai asli tax: $tax")

        WebUI.comment("Nilai asli total: $actualTotal ")
		WebUI.delay(20)
		
        // Hapus kata "Total: ", simbol "$", koma, dan spasi lainnya, lalu konversi menjadi double dengan error handling
        try {
            actualTotal = actualTotal.replace('Total: ', '').replace('$', '').replace(',', '').trim()

            WebUI.comment("Nilai actualTotal setelah di-trim: $actualTotal")

            double actualTotalAsDouble = Double.parseDouble(actualTotal)

            // Expected total sudah diambil dari Excel, lakukan hal yang sama
            expectedTotal = expectedTotal.replace('$', '').replace(',', '').trim()

            double expectedTotalAsDouble = Double.parseDouble(expectedTotal)

            // Tampilkan nilai actual dan expected untuk verifikasi manual (opsional)
            WebUI.comment("Nilai actualTotal sebagai double: $actualTotalAsDouble")

            WebUI.comment("Nilai expectedTotal sebagai double: $expectedTotalAsDouble")

            // Lakukan verifikasi apakah actual total sama dengan expected total (perbandingan numerik)
            WebUI.verifyEqual(actualTotalAsDouble, expectedTotalAsDouble)
        }
        catch (Exception e) {
            WebUI.comment("Error saat konversi nilai ke double: $e.getMessage()")

            throw e
        }
		
	
    } else if (caseType == 'Negative') {
        WebUI.click(findTestObject('Object Repository/Add_Cart/Cart_Button'))

        WebUI.click(findTestObject('Object Repository/Add_Cart/btn_Checkout'))

        WebUI.setText(findTestObject('Add_Cart/txt_First_Name'), 'wan')

        WebUI.setText(findTestObject('Add_Cart/txt_LastName'), 'wan')

        WebUI.setText(findTestObject('Add_Cart/txt_PostalCode'), '2222')

        WebUI.click(findTestObject('Add_Cart/btn_Continue'))

        def actualTotal = WebUI.getText(findTestObject('Object Repository/Add_Cart/txt_Total'))

        try {
            actualTotal = actualTotal.replace('Total: ', '').replace('$', '').replace(',', '').trim()

            WebUI.comment("Nilai actualTotal setelah di-trim: $actualTotal")

            double actualTotalAsDouble = Double.parseDouble(actualTotal)

            expectedTotal = expectedTotal.replace('$', '').replace(',', '').trim()

            double expectedTotalAsDouble = Double.parseDouble(expectedTotal)

            WebUI.comment("Nilai actualTotal sebagai double: $actualTotalAsDouble")

            WebUI.comment("Nilai expectedTotal sebagai double: $expectedTotalAsDouble")

            WebUI.verifyEqual(actualTotalAsDouble, expectedTotalAsDouble)
        }
        catch (Exception e) {
            WebUI.comment("Error saat konversi nilai ke double: $e.getMessage()")

            throw e
        } 
    }
    
    // Kembali ke halaman produk
    WebUI.click(findTestObject('Object Repository/Add_Cart/btn_Finish'))
}

// Tutup browser setelah selesai
WebUI.closeBrowser()


