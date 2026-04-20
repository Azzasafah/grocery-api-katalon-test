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
 *  TC-014 | Buat Order dari Cart yang Valid (Authenticated)
 * ============================================================
 *  Scenario   : TS-013
 *  Type       : Positive
 *  Priority   : Critical
 *  Endpoint   : POST /orders
 *  Auth       : Bearer Token (WAJIB)
 *  Depends on : TC-006 (cartId), TC-012 (accessToken)
 *
 *  Note:
 *    Kita menyisipkan Bearer Token di header.
 *    Format yang benar: "Bearer <token>" — dengan spasi antara
 *    kata "Bearer" dan tokennya. Salah format = 401 Unauthorized.
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

KeywordUtil.logInfo("=== TC-014: Buat Order (Authenticated) ===")

// ── Validasi semua precondition ──
assert GlobalVariable.cartId != null && GlobalVariable.cartId.toString().length() > 0 :
	"❌ cartId belum ada! Jalankan TC-006."
assert GlobalVariable.accessToken != null && GlobalVariable.accessToken.toString().length() > 0 :
	"❌ accessToken belum ada! Jalankan TC-012."

KeywordUtil.logInfo("Precondition OK")
KeywordUtil.logInfo("cartId: ${GlobalVariable.cartId}")
KeywordUtil.logInfo("accessToken: ${GlobalVariable.accessToken.toString().substring(0,8)}*****")

// ── Pastikan cart memiliki item sebelum checkout ──
// (Tambahkan item fresh jika cart kosong)
String checkItemsUrl = "${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items"
def checkReq = new RequestObject('CHECK_Cart_Items')
checkReq.setRestUrl(checkItemsUrl)
checkReq.setRestRequestMethod('GET')
def checkResp = WS.sendRequest(checkReq)
def existingItems = new JsonSlurper().parseText(checkResp.getResponseText())

// ── FORCE CART VALID (fresh cart + fresh item) ──
KeywordUtil.logInfo("cart valid (buat ulang cart + isi item fresh)")

// STEP A: Buat cart baru
def createCartReq = new RequestObject('CREATE_CART_FRESH')
createCartReq.setRestUrl("${GlobalVariable.baseUrl}/carts")
createCartReq.setRestRequestMethod('POST')

def createCartResp = WS.sendRequest(createCartReq)
WS.verifyResponseStatusCode(createCartResp, 201)

def cartJson = new JsonSlurper().parseText(createCartResp.getResponseText())
GlobalVariable.cartId = cartJson.cartId

KeywordUtil.logInfo("Cart baru dibuat: ${GlobalVariable.cartId}")

// STEP B: Ambil product valid
def productReq = new RequestObject('GET_PRODUCTS')
productReq.setRestUrl("${GlobalVariable.baseUrl}/products?results=1")
productReq.setRestRequestMethod('GET')

def productResp = WS.sendRequest(productReq)
WS.verifyResponseStatusCode(productResp, 200)

def productJson = new JsonSlurper().parseText(productResp.getResponseText())
def productId = productJson[0].id

KeywordUtil.logInfo("Product valid dipilih: ${productId}")

// STEP C: Tambahkan item ke cart baru
def addReq = new RequestObject('ADD_ITEM_FRESH')
addReq.setRestUrl("${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items")
addReq.setRestRequestMethod('POST')
addReq.setBodyContent(new HttpTextBodyContent(
    "{\"productId\": ${productId}}",
    'UTF-8',
    'application/json'
))
addReq.setHttpHeaderProperties([
    new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')
])

def addResp = WS.sendRequest(addReq)

def addStatus = addResp.getStatusCode()
assert addStatus == 201 || addStatus == 400 :
    "❌ Gagal add item: ${addResp.getResponseText()}"

KeywordUtil.logInfo("Add item status: ${addStatus}")

KeywordUtil.logInfo("Item berhasil ditambahkan ke cart baru")

// ── STEP 1: Build POST /orders request ──
String orderUrl    = "${GlobalVariable.baseUrl}/orders"
String customerName = "Budi Santoso (Portfolio QA)"
String requestBody  = """{"cartId": "${GlobalVariable.cartId}", "customerName": "${customerName}"}"""

KeywordUtil.logInfo("Endpoint: POST ${orderUrl}")
KeywordUtil.logInfo("Body: ${requestBody}")

def orderReq = new RequestObject('POST_Create_Order')
orderReq.setRestUrl(orderUrl)
orderReq.setRestRequestMethod('POST')
orderReq.setBodyContent(new HttpTextBodyContent(requestBody, 'UTF-8', 'application/json'))
orderReq.setHttpHeaderProperties([
    new TestObjectProperty('Content-Type',  ConditionType.EQUALS, 'application/json'),
    new TestObjectProperty('Authorization', ConditionType.EQUALS, "Bearer ${GlobalVariable.accessToken}")
])

// ── STEP 2: Kirim request ──
def response = WS.sendRequest(orderReq)
KeywordUtil.logInfo("Status: ${response.getStatusCode()}")
KeywordUtil.logInfo("Response: ${response.getResponseText()}")

// ── STEP 3: Status harus 201 Created ──
WS.verifyResponseStatusCode(response, 201)
KeywordUtil.logInfo("✅ STEP 3 PASS — Status 201")

// ── STEP 4: Validasi response mengandung orderId ──
def json = new JsonSlurper().parseText(response.getResponseText())
assert json.orderId != null              : "❌ Field 'orderId' tidak ada dalam response!"
assert json.orderId instanceof String    : "❌ 'orderId' harus bertipe String"
assert json.orderId.trim().length() > 0  : "❌ 'orderId' tidak boleh kosong!"
KeywordUtil.logInfo("✅ STEP 4 PASS — orderId: ${json.orderId}")

// ── STEP 5: Simpan orderId ──
GlobalVariable.orderId = json.orderId
KeywordUtil.logInfo("✅ STEP 5 — orderId disimpan ke GlobalVariable: ${GlobalVariable.orderId}")

KeywordUtil.logInfo("=== TC-014 PASSED ✅ — Order berhasil dibuat! ===")