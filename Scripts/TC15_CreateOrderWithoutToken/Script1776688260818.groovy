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
 *  TC-015 | Buat Order TANPA Authorization Header (Negative)
 * ============================================================
 *  Scenario   : TS-014
 *  Type       : NEGATIVE — Critical!
 *  Priority   : Critical
 *  Endpoint   : POST /orders (tanpa token)
 *
 *  Note:
 *    Memastikan endpoint yang seharusnya terproteksi benar-benar
 *    menolak request tanpa autentikasi. Jika API mengembalikan
 *    201 tanpa token = celah keamanan serius!
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

KeywordUtil.logInfo("=== TC-015: Buat Order TANPA Token (Negative — Security Test) ===")

String orderUrl = "${GlobalVariable.baseUrl}/orders"

// ══════════════════════════════════════════════
//  TEST A: Tanpa Authorization header sama sekali
// ══════════════════════════════════════════════
KeywordUtil.logInfo("--- TEST A: Tanpa header Authorization ---")
def reqA = new RequestObject('POST_Order_No_Auth')
reqA.setRestUrl(orderUrl)
reqA.setRestRequestMethod('POST')
reqA.setBodyContent(new HttpTextBodyContent(
	'{"cartId": "fakecartid", "customerName": "Test"}', 'UTF-8', 'application/json'
))
reqA.setHttpHeaderProperties([
	new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')
	// ← Sengaja TIDAK menambahkan Authorization header
])

def respA = WS.sendRequest(reqA)
KeywordUtil.logInfo("Status: ${respA.getStatusCode()}")
assert respA.getStatusCode() == 401 :
	"❌ SECURITY BUG! Tanpa token seharusnya 401, Actual: ${respA.getStatusCode()}"
KeywordUtil.logInfo("✅ TEST A PASS — Tanpa token → 401 Unauthorized")

// ══════════════════════════════════════════════
//  TEST B: Dengan token yang tidak valid (salah format)
// ══════════════════════════════════════════════
KeywordUtil.logInfo("--- TEST B: Token tidak valid ---")
def reqB = new RequestObject('POST_Order_Invalid_Token')
reqB.setRestUrl(orderUrl)
reqB.setRestRequestMethod('POST')
reqB.setBodyContent(new HttpTextBodyContent(
	'{"cartId": "fakecartid", "customerName": "Test"}', 'UTF-8', 'application/json'
))
reqB.setHttpHeaderProperties([
	new TestObjectProperty('Content-Type',  ConditionType.EQUALS, 'application/json'),
	new TestObjectProperty('Authorization', ConditionType.EQUALS, 'Bearer token_palsu_12345')
])

def respB = WS.sendRequest(reqB)
KeywordUtil.logInfo("Status: ${respB.getStatusCode()}")
assert respB.getStatusCode() == 401 :
	"❌ Token palsu seharusnya ditolak (401), Actual: ${respB.getStatusCode()}"
KeywordUtil.logInfo("✅ TEST B PASS — Token palsu → 401 Unauthorized")

// ══════════════════════════════════════════════
//  TEST C: Format Bearer salah (tanpa kata "Bearer")
// ══════════════════════════════════════════════
KeywordUtil.logInfo("--- TEST C: Format Authorization salah (tanpa 'Bearer') ---")
def reqC = new RequestObject('POST_Order_Wrong_Format')
reqC.setRestUrl(orderUrl)
reqC.setRestRequestMethod('POST')
reqC.setBodyContent(new HttpTextBodyContent(
	'{"cartId": "fakecartid", "customerName": "Test"}', 'UTF-8', 'application/json'
))
reqC.setHttpHeaderProperties([
	new TestObjectProperty('Content-Type',  ConditionType.EQUALS, 'application/json'),
	new TestObjectProperty('Authorization', ConditionType.EQUALS, "${GlobalVariable.accessToken}")
	// Format salah: tanpa kata "Bearer " di depan
])

def respC = WS.sendRequest(reqC)
KeywordUtil.logInfo("Status: ${respC.getStatusCode()}")
assert respC.getStatusCode() == 401 :
	"❌ Format Authorization salah seharusnya ditolak (401), Actual: ${respC.getStatusCode()}"
KeywordUtil.logInfo("✅ TEST C PASS — Format Authorization salah → 401")

KeywordUtil.logInfo("=== TC-015 PASSED ✅ — Semua skenario tanpa auth ditolak dengan benar ===")