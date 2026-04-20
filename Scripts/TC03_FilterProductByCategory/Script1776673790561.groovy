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
import com.kms.katalon.core.testobject.RequestObject
import groovy.json.JsonSlurper
import internal.GlobalVariable
 

/**
 * ============================================================
 *  TC-003 | Filter Produk Berdasarkan Kategori 'fresh-produce'
 * ============================================================
 *  Scenario   : TS-003
 *  Type       : Positive
 *  Priority   : Medium
 *  Endpoint   : GET /products?category=fresh-produce
 *  Auth       : Tidak diperlukan
 *
 *  Note:
 *    Kita membangun request secara dinamis menggunakan RequestObject
 *    manual karena Object Repository hanya menyimpan 1 URL.
 *    Teknik ini penting saat mengganti parameter URL
 *    secara programatik.
 * ============================================================
 */

KeywordUtil.logInfo("=== TC-003: Filter Produk per Kategori ===")
 
// ── Helper: Fungsi untuk build request GET dengan URL custom ──
def buildGetRequest = { String url ->
	RequestObject req = new RequestObject()
	req.setRestUrl(url)
	req.setRestRequestMethod('GET')
	return req
}

// ══════════════════════════════════════════════
//  BAGIAN A: Filter dengan kategori VALID
// ══════════════════════════════════════════════
String targetCategory = 'fresh-produce'
String urlA = "${GlobalVariable.baseUrl}/products?category=${targetCategory}"
KeywordUtil.logInfo("BAGIAN A — URL: ${urlA}")
 
def responseA = WS.sendRequest(buildGetRequest(urlA))
WS.verifyResponseStatusCode(responseA, 200)
 
def productsA = new JsonSlurper().parseText(responseA.getResponseText())
assert productsA instanceof List : "❌ Response harus berupa Array"
 
// Setiap produk dalam response HARUS berkategori 'fresh-produce'
productsA.each { product ->
	assert product.category == targetCategory :
		"❌ Produk '${product.name}' memiliki kategori '${product.category}', bukan '${targetCategory}'!"
}
KeywordUtil.logInfo("✅ BAGIAN A PASS — ${productsA.size()} produk, semua berkategori '${targetCategory}'")
 
// ══════════════════════════════════════════════
//  BAGIAN B: Filter dengan kategori VALID lainnya
// ══════════════════════════════════════════════
String urlB = "${GlobalVariable.baseUrl}/products?category=meat-seafood"
KeywordUtil.logInfo("BAGIAN B — URL: ${urlB}")
 
def responseB = WS.sendRequest(buildGetRequest(urlB))
WS.verifyResponseStatusCode(responseB, 200)
 
def productsB = new JsonSlurper().parseText(responseB.getResponseText())
assert productsB instanceof List : "❌ Response harus berupa Array"
KeywordUtil.logInfo("✅ BAGIAN B PASS — Kategori 'meat-seafood': ${productsB.size()} produk ditemukan")
 
// ══════════════════════════════════════════════
//  BAGIAN C: Filter dengan kategori TIDAK VALID
// ══════════════════════════════════════════════
String urlC = "${GlobalVariable.baseUrl}/products?category=kategori-tidak-ada"
KeywordUtil.logInfo("BAGIAN C — URL: ${urlC} (kategori tidak valid)")
 
def responseC = WS.sendRequest(buildGetRequest(urlC))
// API bisa mengembalikan 200 dengan array kosong ATAU 400 — keduanya valid
def statusC = responseC.getStatusCode()
assert statusC == 200 || statusC == 400 :
	"❌ Untuk kategori tidak valid, expected 200 (empty array) atau 400. Actual: ${statusC}"
KeywordUtil.logInfo("✅ BAGIAN C PASS — Status: ${statusC} (kategori tidak valid ditangani dengan benar)")
 
KeywordUtil.logInfo("=== TC-003 PASSED ✅ ===")
 

