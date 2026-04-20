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
 *  TC-016 | Get All Orders (Authenticated)
 * ============================================================
 */
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.testobject.RequestObject as RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import groovy.json.JsonSlurper
import internal.GlobalVariable

KeywordUtil.logInfo("=== TC-016: Get All Orders ===")
assert GlobalVariable.accessToken != null : "❌ accessToken belum ada! Jalankan TC-012."
assert GlobalVariable.orderId != null     : "❌ orderId belum ada! Jalankan TC-014."

// ── Build GET /orders request ──
def req = new RequestObject('GET_All_Orders')
req.setRestUrl("${GlobalVariable.baseUrl}/orders")
req.setRestRequestMethod('GET')
req.setHttpHeaderProperties([
	new TestObjectProperty('Authorization', ConditionType.EQUALS, "Bearer ${GlobalVariable.accessToken}")
])

def response = WS.sendRequest(req)
KeywordUtil.logInfo("Status: ${response.getStatusCode()}")

// Verifikasi status 200
WS.verifyResponseStatusCode(response, 200)
KeywordUtil.logInfo("✅ Status 200 OK")

// Parse dan validasi response
def orders = new JsonSlurper().parseText(response.getResponseText())
assert orders instanceof List : "❌ Response harus berupa Array!"
assert orders.size() >= 1    : "❌ Harus ada minimal 1 order!"
KeywordUtil.logInfo("✅ Jumlah order: ${orders.size()}")

// Verifikasi orderId dari TC-014 ada dalam list
def foundOrder = orders.find { it.id == GlobalVariable.orderId }
assert foundOrder != null :
	"❌ orderId '${GlobalVariable.orderId}' tidak ditemukan dalam list orders!"
KeywordUtil.logInfo("✅ orderId '${GlobalVariable.orderId}' ditemukan dalam list")

// Validasi struktur setiap order
orders.each { order ->
	assert order.id          != null : "❌ Order tidak memiliki 'id'"
	assert order.items       != null : "❌ Order tidak memiliki 'items'"
	assert order.customerName!= null : "❌ Order tidak memiliki 'customerName'"
}
KeywordUtil.logInfo("✅ Semua order memiliki struktur yang valid")
KeywordUtil.logInfo("=== TC-016 PASSED ✅ ===")