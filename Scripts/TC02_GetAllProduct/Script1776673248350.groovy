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
 *  TC-002 | Get Semua Produk dengan results=20
 * ============================================================
 *  Scenario   : TS-002
 *  Type       : Positive
 *  Priority   : High
 *  Endpoint   : GET /products?results=20
 *  Auth       : Tidak diperlukan
 *
 *  Note:
 *    Saat memvalidasi TIPE DATA, bukan hanya nilai.
 *    Ini penting karena API bisa saja mengembalikan price sebagai
 *    String "10.5" dan bukan Number 10.5 — keduanya tampak sama
 *    di layar tapi berbeda saat diproses oleh aplikasi.
 * ============================================================
 */

KeywordUtil.logInfo("=== TC-002: Get All Products ===")
KeywordUtil.logInfo("Endpoint: GET ${GlobalVariable.baseUrl}/products?results=20")
 
import groovy.json.JsonSlurper

// Kirim request GET /products?results=20
def response = WS.sendRequest(findTestObject('Simple Grocery Store API/Products/Get all products'))

// Verifikasi status code 200
WS.verifyResponseStatusCode(response, 200)

// Parse response JSON
def jsonResponse = new JsonSlurper().parseText(response.getResponseText())

// Verifikasi response adalah array
assert jsonResponse instanceof List : 'Response harus berupa array/list'

// Verifikasi jumlah produk tidak melebihi 20
assert jsonResponse.size() <= 20 : 'Jumlah produk tidak boleh lebih dari 20'

// Verifikasi produk pertama memiliki field yang diperlukan
def firstProduct = jsonResponse[0]
assert firstProduct.id != null : 'Produk harus memiliki field id'
assert firstProduct.name != null : 'Produk harus memiliki field name'

println('Jumlah produk ditemukan: ' + jsonResponse.size())
println('Test Case 2 PASSED: Get All Products berhasil')

