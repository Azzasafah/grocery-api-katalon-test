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
 *  TC-018 | Hapus Pesanan & Verifikasi Tidak Bisa Diakses Lagi
 * ============================================================
 *  Scenario   : TS-017
 *  Type       : Positive
 *  Priority   : High
 *  Endpoint   : DELETE /orders/:orderId
 *  Auth       : Bearer Token
 *  Depends on : TC-012 (token), TC-014 (orderId)
 *
 *  Note:
 *    Pola terbaik untuk test DELETE:
 *    1. DELETE → harus 204
 *    2. GET yang sama → harus 404 (resource sudah tidak ada)
 *    Ini membuktikan bahwa data benar-benar terhapus dari sistem.
 * ============================================================
 */

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.testobject.RequestObject as RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable

KeywordUtil.logInfo("=== TC-018: Hapus Order & Verifikasi ===")
assert GlobalVariable.orderId     != null : "❌ orderId belum ada! Jalankan TC-014."
assert GlobalVariable.accessToken != null : "❌ accessToken belum ada! Jalankan TC-012."

String orderUrl = "${GlobalVariable.baseUrl}/orders/${GlobalVariable.orderId}"
KeywordUtil.logInfo("Target orderId: ${GlobalVariable.orderId}")

// ── STEP 1: Kirim DELETE /orders/:orderId ──
KeywordUtil.logInfo("STEP 1: Mengirim DELETE request ke ${orderUrl}")
def deleteReq = new RequestObject('DELETE_Order')
deleteReq.setRestUrl(orderUrl)
deleteReq.setRestRequestMethod('DELETE')
deleteReq.setHttpHeaderProperties([
	new TestObjectProperty('Authorization', ConditionType.EQUALS, "Bearer ${GlobalVariable.accessToken}")
])

def deleteResp = WS.sendRequest(deleteReq)
KeywordUtil.logInfo("Status DELETE: ${deleteResp.getStatusCode()}")

// ── STEP 2: Status harus 204 No Content ──
WS.verifyResponseStatusCode(deleteResp, 204)
KeywordUtil.logInfo("✅ STEP 2 PASS — Status 204 (Order dihapus)")

// ── STEP 3: Verifikasi order sudah tidak bisa diakses ──
KeywordUtil.logInfo("STEP 3: Verifikasi order sudah tidak ada dengan GET ${orderUrl}")
def getReq = new RequestObject('GET_Deleted_Order')
getReq.setRestUrl(orderUrl)
getReq.setRestRequestMethod('GET')
getReq.setHttpHeaderProperties([
	new TestObjectProperty('Authorization', ConditionType.EQUALS, "Bearer ${GlobalVariable.accessToken}")
])

def getResp = WS.sendRequest(getReq)
KeywordUtil.logInfo("Status GET setelah DELETE: ${getResp.getStatusCode()}")
KeywordUtil.logInfo("Response: ${getResp.getResponseText()}")

// ── STEP 4: Harus 404 Not Found ──
assert getResp.getStatusCode() == 404 :
	"❌ BUG! Order yang sudah dihapus masih bisa diakses! Status: ${getResp.getStatusCode()}"
KeywordUtil.logInfo("✅ STEP 4 PASS — Status 404 (Order benar-benar sudah terhapus)")

// ── STEP 5: Verifikasi order tidak muncul di GET /orders ──
KeywordUtil.logInfo("STEP 5: Verifikasi order tidak muncul di list GET /orders")
def listReq = new RequestObject('GET_Orders_After_Delete')
listReq.setRestUrl("${GlobalVariable.baseUrl}/orders")
listReq.setRestRequestMethod('GET')
listReq.setHttpHeaderProperties([
	new TestObjectProperty('Authorization', ConditionType.EQUALS, "Bearer ${GlobalVariable.accessToken}")
])

def listResp = WS.sendRequest(listReq)
WS.verifyResponseStatusCode(listResp, 200)

import groovy.json.JsonSlurper
def allOrders = new JsonSlurper().parseText(listResp.getResponseText())
def deletedOrder = allOrders.find { it.id == GlobalVariable.orderId }
assert deletedOrder == null :
	"❌ BUG! Order yang dihapus masih muncul di list GET /orders!"
KeywordUtil.logInfo("✅ STEP 5 PASS — Order tidak muncul di list orders")

// ── Clear GlobalVariable orderId ──
GlobalVariable.orderId = null
KeywordUtil.logInfo("orderId di-clear dari GlobalVariable")

KeywordUtil.logInfo("=== TC-018 PASSED ✅ — Order berhasil dihapus dan diverifikasi ===")