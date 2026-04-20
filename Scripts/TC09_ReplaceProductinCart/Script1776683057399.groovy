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
 /**
 * ============================================================
 *  TC-009 | Ganti Produk di Keranjang (PUT — Full Replace)
 * ============================================================
 *  Scenario   : TS-008 (lanjutan)
 *  Type       : Positive
 *  Priority   : Medium
 *  Endpoint   : PUT /carts/:cartId/items/:itemId
 *  Auth       : Tidak diperlukan
 *  Depends on : TC-006, TC-007
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

KeywordUtil.logInfo("=== TC-009: Ganti Produk di Keranjang (PUT) ===")

assert GlobalVariable.cartId != null : "❌ cartId belum ada! Jalankan TC-006 dulu."
assert GlobalVariable.itemId != null : "❌ itemId belum ada! Jalankan TC-007 dulu."

int newProductId = 4643
int newQuantity  = 2

String putUrl = "${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items/${GlobalVariable.itemId}"
KeywordUtil.logInfo("Endpoint: PUT ${putUrl}")
KeywordUtil.logInfo("Body: {productId: ${newProductId}, quantity: ${newQuantity}}")

// ── STEP 1: Kirim PUT request ──
def putReq = new RequestObject('PUT_Replace_Product')
putReq.setRestUrl(putUrl)
putReq.setRestRequestMethod('PUT')
putReq.setBodyContent(new HttpTextBodyContent(
    """{"productId": ${newProductId}, "quantity": ${newQuantity}}""",
    'UTF-8', 'application/json'
))
putReq.setHttpHeaderProperties([
    new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')
])

def putResponse = WS.sendRequest(putReq)

// ── STEP 2: Status 204 No Content ──
WS.verifyResponseStatusCode(putResponse, 204)
KeywordUtil.logInfo("✅ STEP 2 PASS — Status 204 (PUT berhasil)")

// ── STEP 3: Verifikasi perubahan produk ──
def getReq = new RequestObject('GET_Items_After_PUT')
getReq.setRestUrl("${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items")
getReq.setRestRequestMethod('GET')

def getResp = WS.sendRequest(getReq)
WS.verifyResponseStatusCode(getResp, 200)
def items = new JsonSlurper().parseText(getResp.getResponseText())
def replacedItem = items.find { it.id == GlobalVariable.itemId }

assert replacedItem != null                         : "❌ Item tidak ditemukan setelah PUT!"
assert replacedItem.productId == newProductId       : "❌ ProductId tidak berubah! Expected: ${newProductId}, Actual: ${replacedItem.productId}"
assert replacedItem.quantity  == newQuantity        : "❌ Quantity tidak sesuai! Expected: ${newQuantity}, Actual: ${replacedItem.quantity}"

KeywordUtil.logInfo("✅ STEP 3 PASS — Produk berhasil diganti: productId=${replacedItem.productId}, quantity=${replacedItem.quantity}")
KeywordUtil.logInfo("=== TC-009 PASSED ✅ ===")