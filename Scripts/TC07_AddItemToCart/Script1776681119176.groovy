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
 *  TC-007 | Menambahkan Item ke Keranjang
 * ============================================================
 *  Scenario   : TS-007
 *  Type       : Positive
 *  Priority   : Critical
 *  Endpoint   : POST /carts/:cartId/items
 *  Auth       : Tidak diperlukan
 *  Depends on : TC-006 (cartId harus sudah tersimpan)
 *
 *  Note:
 *    Lihat bagaimana membangun URL secara dinamis menggunakan
 *    GlobalVariable.cartId. Menggabungkan data dari response
 *    sebelumnya ke dalam request berikutnya.
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

KeywordUtil.logInfo("=== TC-007: Tambah Item ke Keranjang ===")

// ── Validasi precondition: cartId harus sudah ada ──
assert GlobalVariable.cartId != null && GlobalVariable.cartId.toString().length() > 0 :
	"❌ PRECONDITION GAGAL: cartId belum diset! Pastikan TC-006 sudah dijalankan."
KeywordUtil.logInfo("Precondition OK — cartId: ${GlobalVariable.cartId}")

// ── STEP 1: Build request POST /carts/{cartId}/items ──
String addItemUrl = "${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items"
KeywordUtil.logInfo("Endpoint: POST ${addItemUrl}")

def req = new RequestObject('POST_Add_Item_To_Cart')
req.setRestUrl(addItemUrl)
req.setRestRequestMethod('POST')
req.setBodyContent(new HttpTextBodyContent('{"productId": 4646}', 'UTF-8', 'application/json'))
req.setHttpHeaderProperties([
	new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')
])

// ── STEP 2: Kirim request ──
def response = WS.sendRequest(req)
KeywordUtil.logInfo("Response: ${response.getResponseText()}")

// ── STEP 3: Status code harus 201 ──
WS.verifyResponseStatusCode(response, 201)
KeywordUtil.logInfo("✅ STEP 3 PASS — Status Code: 201 Created")

// ── STEP 4: Parse response & validasi itemId ──
def json = new JsonSlurper().parseText(response.getResponseText())
assert json.itemId != null : "❌ Field 'itemId' tidak ada dalam response!"
KeywordUtil.logInfo("✅ STEP 4 PASS — itemId: ${json.itemId}")

// ── STEP 5: Simpan itemId ──
GlobalVariable.itemId = json.itemId
KeywordUtil.logInfo("✅ STEP 5 — itemId disimpan ke GlobalVariable: ${GlobalVariable.itemId}")

// ── STEP 6: Verifikasi item muncul saat GET cart items ──
def getItemsReq = new RequestObject('GET_Cart_Items_Verify')
getItemsReq.setRestUrl("${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items")
getItemsReq.setRestRequestMethod('GET')

def getResponse = WS.sendRequest(getItemsReq)
WS.verifyResponseStatusCode(getResponse, 200)

def cartItems = new JsonSlurper().parseText(getResponse.getResponseText())
assert cartItems instanceof List  : "❌ Cart items harus berupa Array!"
assert cartItems.size() >= 1      : "❌ Cart seharusnya berisi minimal 1 item!"

// Cari item yang baru ditambahkan
def addedItem = cartItems.find { it.id == GlobalVariable.itemId }
assert addedItem != null : "❌ Item yang baru ditambahkan tidak ditemukan dalam cart!"
assert addedItem.quantity == 1   : "❌ Quantity default seharusnya 1, actual: ${addedItem.quantity}"

KeywordUtil.logInfo("✅ STEP 6 PASS — Item ditemukan dalam cart, quantity: ${addedItem.quantity}")
KeywordUtil.logInfo("=== TC-007 PASSED ✅ — itemId aktif: ${GlobalVariable.itemId} ===")