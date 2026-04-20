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
import com.kms.katalon.core.util.KeywordUtil
import groovy.json.JsonSlurper
import internal.GlobalVariable


/**
 * ============================================================
 *  TC-004 | Get Single Product dengan ID Valid (8739)
 * ============================================================
 *  Scenario   : TS-004
 *  Type       : Positive
 *  Priority   : High
 *  Endpoint   : GET /products/:productId
 *  Auth       : Tidak diperlukan
 * ============================================================
 */
 
KeywordUtil.logInfo("=== TC-004: Get Single Product (ID Valid) ===")
 
// Kirim request GET /products/8739
def response = WS.sendRequest(findTestObject('Simple Grocery Store API/Get single product'))

// Verifikasi status code 200
WS.verifyResponseStatusCode(response, 200)

// Parse response JSON
def product = new JsonSlurper().parseText(response.getResponseText())

// Verifikasi field-field produk ada
assert product.id != null : 'Produk harus memiliki id'
assert product.name != null : 'Produk harus memiliki name'
assert product.category != null : 'Produk harus memiliki category'
assert product.inStock != null : 'Produk harus memiliki field inStock'

println('Produk ditemukan: ' + product.name)
println('Harga: ' + product.price)
println('Test Case 3 PASSED: Get Single Product berhasil')

 
KeywordUtil.logInfo("=== TC-004 PASSED ✅ — Produk: ${product.name} | Stok: ${product.inStock} ===")
 