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
 *  TC-001 | Verifikasi API Status mengembalikan 'UP'
 * ============================================================
 *  Scenario   : TS-001
 *  Type       : Positive
 *  Priority   : Critical
 *  Endpoint   : GET /status
 *  Auth       : Tidak diperlukan
 *  Note:
 *    Ini adalah "Smoke Test" — test paling dasar yang memastikan
 *    API bisa diakses. Selalu jalankan ini PERTAMA sebelum test lain.
 *    Jika test ini FAIL, tidak perlu lanjut ke test yang lain.
 * ============================================================
 */


KeywordUtil.logInfo("=== TC-001: Verifikasi API Status ===")
KeywordUtil.logInfo("Endpoint: GET ${GlobalVariable.baseUrl}/status")
 
// ── STEP 1: Kirim request ke GET /status ──
def response = WS.sendRequest(findTestObject('Simple Grocery Store API/Status/API Status'))
 
// ── STEP 2: Verifikasi status code harus 200 ──
WS.verifyResponseStatusCode(response, 200)
KeywordUtil.logInfo("✅ STEP 2 PASS — Status Code: ${response.getStatusCode()}")
 
// ── STEP 3: Parse response body ──
def json = new JsonSlurper().parseText(response.getResponseText())
KeywordUtil.logInfo("Response Body: ${response.getResponseText()}")
 
// ── STEP 4: Verifikasi field 'status' bernilai 'UP' ──
assert json.status != null        : "❌ Field 'status' tidak ditemukan dalam response!"
assert json.status == 'UP'        : "❌ API status bukan 'UP'! Actual: ${json.status}"
KeywordUtil.logInfo("✅ STEP 4 PASS — API Status: ${json.status}")
 
// ── STEP 5: Verifikasi response tidak kosong ──
assert response.getResponseText().length() > 0 : "❌ Response body kosong!"
KeywordUtil.logInfo("✅ STEP 5 PASS — Response tidak kosong")
 
KeywordUtil.logInfo("=== TC-001 PASSED ✅ ===")