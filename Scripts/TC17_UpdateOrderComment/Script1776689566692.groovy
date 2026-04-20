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
 *  TC-017 | Update Komentar Pesanan (PATCH /orders/:orderId)
 * ============================================================
 *  Scenario   : TS-016
 *  Type       : Positive
 *  Priority   : Medium
 *  Endpoint   : PATCH /orders/:orderId
 *  Auth       : Bearer Token
 *  Depends on : TC-012 (token), TC-014 (orderId)
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

KeywordUtil.logInfo("=== TC-017: Update Komentar Order (PATCH) ===")
assert GlobalVariable.orderId     != null : "❌ orderId belum ada! Jalankan TC-014."
assert GlobalVariable.accessToken != null : "❌ accessToken belum ada! Jalankan TC-012."

String newComment = "Mohon diantar sebelum jam 5 sore. Terima kasih!"
String patchUrl   = "${GlobalVariable.baseUrl}/orders/${GlobalVariable.orderId}"
KeywordUtil.logInfo("Endpoint: PATCH ${patchUrl}")
KeywordUtil.logInfo("Comment baru: '${newComment}'")

// ── STEP 1: Kirim PATCH request ──
def patchReq = new RequestObject('PATCH_Update_Order_Comment')
patchReq.setRestUrl(patchUrl)
patchReq.setRestRequestMethod('PATCH')
patchReq.setBodyContent(new HttpTextBodyContent(
	"""{"comment": "${newComment}"}""", 'UTF-8', 'application/json'
))
patchReq.setHttpHeaderProperties([
    new TestObjectProperty('Content-Type',  ConditionType.EQUALS, 'application/json'),
    new TestObjectProperty('Authorization', ConditionType.EQUALS, "Bearer ${GlobalVariable.accessToken}")
])

def patchResp = WS.sendRequest(patchReq)
KeywordUtil.logInfo("Status: ${patchResp.getStatusCode()}")

// ── STEP 2: Status harus 204 No Content ──
WS.verifyResponseStatusCode(patchResp, 204)
KeywordUtil.logInfo("✅ STEP 2 PASS — Status 204 (Comment diupdate)")

// ── STEP 3: Verifikasi comment berubah via GET /orders/:orderId ──
def getReq = new RequestObject('GET_Order_After_Patch')
getReq.setRestUrl("${GlobalVariable.baseUrl}/orders/${GlobalVariable.orderId}")
getReq.setRestRequestMethod('GET')
getReq.setHttpHeaderProperties([
    new TestObjectProperty('Authorization', ConditionType.EQUALS, "Bearer ${GlobalVariable.accessToken}")
])

def getResp = WS.sendRequest(getReq)
WS.verifyResponseStatusCode(getResp, 200)

def order = new JsonSlurper().parseText(getResp.getResponseText())
assert order.comment == newComment :
    "❌ Comment tidak berubah! Expected: '${newComment}', Actual: '${order.comment}'"
KeywordUtil.logInfo("✅ STEP 3 PASS — Comment berhasil diupdate: '${order.comment}'")

KeywordUtil.logInfo("=== TC-017 PASSED ✅ ===")