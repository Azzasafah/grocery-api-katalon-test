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

/**
 * ============================================================
 *  TC-012 | Register API Client & Dapatkan Access Token
 * ============================================================
 *  Scenario   : TS-011
 *  Type       : Positive
 *  Priority   : Critical
 *  Endpoint   : POST /api-clients
 *  Auth       : Tidak diperlukan (ini endpoint untuk MENDAPAT token)
 *
 *  Note:
 *    Pola "Register once, use everywhere" — token yang didapat
 *    di sini akan digunakan oleh TC-014 hingga TC-018.
 *    Gunakan timestamp untuk memastikan email selalu unik.
 * ============================================================
 */

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.testobject.RequestObject as RequestObject
import com.kms.katalon.core.testobject.impl.HttpTextBodyContent
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import groovy.json.JsonSlurper
import internal.GlobalVariable

KeywordUtil.logInfo("=== TC-012: Register API Client & Get Access Token ===")

// ── Gunakan timestamp untuk email unik ──
String uniqueEmail = "portfolio_qa_${System.currentTimeMillis()}@testmail.com"
String clientName  = "Portfolio QA Tester"

KeywordUtil.logInfo("Email: ${uniqueEmail}")

// ── STEP 1: Kirim POST /api-clients ──
String registerUrl = "${GlobalVariable.baseUrl}/api-clients"
def req = new RequestObject('POST_Register_API_Client')
req.setRestUrl(registerUrl)
req.setRestRequestMethod('POST')
req.setBodyContent(new HttpTextBodyContent(
	"""{"clientName": "${clientName}", "clientEmail": "${uniqueEmail}"}""",
    'UTF-8', 'application/json'
))
req.setHttpHeaderProperties([
    new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')
])

def response = WS.sendRequest(req)
KeywordUtil.logInfo("Status: ${response.getStatusCode()}")
KeywordUtil.logInfo("Response: ${response.getResponseText()}")

// ── STEP 2: Status harus 201 Created ──
WS.verifyResponseStatusCode(response, 201)
KeywordUtil.logInfo("✅ STEP 2 PASS — Status 201")

// ── STEP 3: Verifikasi accessToken ada dalam response ──
def json = new JsonSlurper().parseText(response.getResponseText())
assert json.accessToken != null               : "❌ Field 'accessToken' tidak ada!"
assert json.accessToken instanceof String     : "❌ 'accessToken' harus bertipe String"
assert json.accessToken.toString().length() > 10 : "❌ 'accessToken' terlalu pendek!"
KeywordUtil.logInfo("✅ STEP 3 PASS — accessToken valid (length: ${json.accessToken.toString().length()})")

// ── STEP 4: Simpan token ke GlobalVariable ──
GlobalVariable.accessToken = json.accessToken
KeywordUtil.logInfo("✅ STEP 4 — accessToken disimpan ke GlobalVariable")

KeywordUtil.logInfo("=== TC-012 PASSED ✅ ===")