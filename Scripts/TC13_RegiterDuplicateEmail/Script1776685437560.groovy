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
 *  TC-013 | Register dengan Email Duplikat (Negative)
 * ============================================================
 *  Scenario   : TS-012
 *  Type       : NEGATIVE
 *  Priority   : High
 *  Endpoint   : POST /api-clients
 *
 *  Note:
 *    Ini adalah "duplicate data" negative test.
 *    API yang baik harus menolak registrasi email yang sudah ada
 *    dengan status 409 Conflict — bukan 200 atau 201.
 *    Kita sengaja mendaftar email yang sama dua kali.
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

KeywordUtil.logInfo("=== TC-013: Registrasi Email Duplikat (Negative) ===")

// Email yang akan didaftarkan dua kali (fixed, bukan timestamp)
String duplicateEmail = "duplicate_test_fixed@portfolioqa.com"
String registerUrl    = "${GlobalVariable.baseUrl}/api-clients"

def buildRegisterReq = { String email ->
	def req = new RequestObject("POST_Register_${email}")
	req.setRestUrl(registerUrl)
	req.setRestRequestMethod('POST')
	req.setBodyContent(new HttpTextBodyContent(
		"""{"clientName": "Test User", "clientEmail": "${email}"}""",
        'UTF-8', 'application/json'
    ))
    req.setHttpHeaderProperties([
        new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')
    ])
    return req
}

// ── STEP 1: Registrasi PERTAMA (mungkin berhasil, mungkin sudah terdaftar) ──
KeywordUtil.logInfo("STEP 1: Registrasi pertama dengan email: ${duplicateEmail}")
def firstResp = WS.sendRequest(buildRegisterReq(duplicateEmail))
int firstStatus = firstResp.getStatusCode()
KeywordUtil.logInfo("Registrasi pertama — Status: ${firstStatus}")

// Status pertama bisa 201 (baru) atau 409 (sudah ada dari run sebelumnya)
assert firstStatus == 201 || firstStatus == 409 :
    "❌ Status tidak terduga: ${firstStatus}"

// ── STEP 2: Registrasi KEDUA dengan email SAMA ──
KeywordUtil.logInfo("STEP 2: Registrasi KEDUA dengan email YANG SAMA: ${duplicateEmail}")
def secondResp = WS.sendRequest(buildRegisterReq(duplicateEmail))
int secondStatus = secondResp.getStatusCode()

KeywordUtil.logInfo("Registrasi kedua — Status: ${secondStatus}")
KeywordUtil.logInfo("Response: ${secondResp.getResponseText()}")

// ── STEP 3: Harus 409 Conflict (email sudah terdaftar) ──
assert secondStatus == 409 :
    "❌ BUG! Email duplikat seharusnya ditolak dengan 409, Actual: ${secondStatus}"
KeywordUtil.logInfo("✅ STEP 3 PASS — Status 409 Conflict (Email duplikat ditolak)")

// ── STEP 4: Response mengandung pesan error ──
def responseText = secondResp.getResponseText()
assert responseText != null && responseText.length() > 0 : "❌ Error response kosong!"

try {
    def errorJson = new JsonSlurper().parseText(responseText)
    assert errorJson.error != null : "❌ Field 'error' tidak ada dalam error response!"
    KeywordUtil.logInfo("✅ STEP 4 PASS — Pesan error: ${errorJson.error}")
} catch(e) {
    KeywordUtil.logInfo("Response bukan JSON: ${responseText}")
}

KeywordUtil.logInfo("=== TC-013 PASSED ✅ — Email duplikat ditangani dengan benar ===")