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
 *  TC-008 | Update Quantity Item di Keranjang (PATCH)
 * ============================================================
 *  Scenario   : TS-008
 *  Type       : Positive
 *  Priority   : High
 *  Endpoint   : PATCH /carts/:cartId/items/:itemId
 *  Auth       : Tidak diperlukan
 *  Depends on : TC-006 (cartId), TC-007 (itemId)
 *
 *  Note:
 *    PATCH | PUT
 *    - PATCH: Hanya mengupdate SEBAGIAN field (parsial update)
 *    - PUT  : Mengganti SELURUH resource dengan data baru (full replace)
 *    Di sini PATCH hanya mengubah quantity, bukan seluruh item.
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

KeywordUtil.logInfo("=== TC-008: Update Quantity Item (PATCH) ===")

// ── Validasi precondition ──
assert GlobalVariable.cartId != null : "❌ cartId belum ada! Jalankan TC-006 dulu."
assert GlobalVariable.itemId != null : "❌ itemId belum ada! Jalankan TC-007 dulu."
KeywordUtil.logInfo("Precondition OK — cartId: ${GlobalVariable.cartId} | itemId: ${GlobalVariable.itemId}")

int newQuantity = 3  // Quantity baru yang akan di-set

// ── STEP 1: Build PATCH request ──
String patchUrl = "${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items/${GlobalVariable.itemId}"
KeywordUtil.logInfo("Endpoint: PATCH ${patchUrl}")
KeywordUtil.logInfo("Body: {quantity: ${newQuantity}}")

def patchReq = new RequestObject('PATCH_Update_Quantity')
patchReq.setRestUrl(patchUrl)
patchReq.setRestRequestMethod('PATCH')
patchReq.setBodyContent(new HttpTextBodyContent(
	"""{"quantity": ${newQuantity}}""", 'UTF-8', 'application/json'
))
patchReq.setHttpHeaderProperties([
    new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')
])

// ── STEP 2: Kirim request ──
def patchResponse = WS.sendRequest(patchReq)
KeywordUtil.logInfo("Status Code: ${patchResponse.getStatusCode()}")

// ── STEP 3: Verifikasi status 204 No Content ──
// 204 = sukses tapi tidak ada body response (normal untuk PATCH)
WS.verifyResponseStatusCode(patchResponse, 204)
KeywordUtil.logInfo("✅ STEP 3 PASS — Status 204 (No Content) — Update berhasil!")

// ── STEP 4: Verifikasi perubahan dengan GET cart items ──
def getReq = new RequestObject('GET_Items_After_PATCH')
getReq.setRestUrl("${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items")
getReq.setRestRequestMethod('GET')

def getResponse = WS.sendRequest(getReq)
WS.verifyResponseStatusCode(getResponse, 200)

def items = new JsonSlurper().parseText(getResponse.getResponseText())
def updatedItem = items.find { it.id == GlobalVariable.itemId }

assert updatedItem != null          : "❌ Item tidak ditemukan setelah PATCH!"
assert updatedItem.quantity == newQuantity :
    "❌ Quantity tidak berubah! Expected: ${newQuantity}, Actual: ${updatedItem.quantity}"

KeywordUtil.logInfo("✅ STEP 4 PASS — Quantity berhasil diubah menjadi: ${updatedItem.quantity}")
KeywordUtil.logInfo("=== TC-008 PASSED ✅ ===")