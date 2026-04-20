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
 *  TC-010 | Menghapus Item dari Keranjang
 * ============================================================
 *  Scenario   : TS-009
 *  Type       : Positive
 *  Priority   : High
 *  Endpoint   : DELETE /carts/:cartId/items/:itemId
 *  Depends on : TC-006, TC-007
 *
 *  Note:
 *    Pola test DELETE yang baik selalu terdiri dari 2 langkah:
 *    1. DELETE resource
 *    2. GET resource yang sama → harus 404
 *    Tanpa langkah 2, kamu tidak benar-benar memverifikasi
 *    bahwa resource sudah terhapus.
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

KeywordUtil.logInfo("=== TC-010: Hapus Item dari Keranjang ===")

assert GlobalVariable.cartId != null : "❌ cartId belum ada!"
assert GlobalVariable.itemId != null : "❌ itemId belum ada!"

// ── Dulu tambahkan item baru dulu agar ada yang dihapus ──
// (TC-010 tidak bergantung pada state item dari TC-008/009)
String addUrl = "${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items"
def addReq = new RequestObject('ADD_Item_For_Delete_Test')
addReq.setRestUrl(addUrl)
addReq.setRestRequestMethod('POST')
addReq.setBodyContent(new HttpTextBodyContent('{"productId": 4646}', 'UTF-8', 'application/json'))
addReq.setHttpHeaderProperties([new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')])

def addResp = WS.sendRequest(addReq)
WS.verifyResponseStatusCode(addResp, 201)
def newItemId = new JsonSlurper().parseText(addResp.getResponseText()).itemId
KeywordUtil.logInfo("Item baru ditambahkan untuk ditest DELETE — itemId: ${newItemId}")

// ── STEP 1: Kirim DELETE request ──
String deleteUrl = "${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items/${newItemId}"
KeywordUtil.logInfo("Endpoint: DELETE ${deleteUrl}")

def deleteReq = new RequestObject('DELETE_Item')
deleteReq.setRestUrl(deleteUrl)
deleteReq.setRestRequestMethod('DELETE')

def deleteResp = WS.sendRequest(deleteReq)

// ── STEP 2: Status harus 204 No Content ──
WS.verifyResponseStatusCode(deleteResp, 204)
KeywordUtil.logInfo("✅ STEP 2 PASS — Status 204 (Item dihapus)")

// ── STEP 3: Verifikasi item sudah benar-benar tidak ada ──
def getItemsReq = new RequestObject('GET_Items_After_Delete')
getItemsReq.setRestUrl("${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items")
getItemsReq.setRestRequestMethod('GET')

def getResp = WS.sendRequest(getItemsReq)
WS.verifyResponseStatusCode(getResp, 200)

def remainingItems = new JsonSlurper().parseText(getResp.getResponseText())
def deletedItem = remainingItems.find { it.id == newItemId }

assert deletedItem == null :
	"❌ BUG! Item yang sudah dihapus masih muncul dalam cart!"
KeywordUtil.logInfo("✅ STEP 3 PASS — Item sudah tidak ada dalam cart (benar-benar terhapus)")

KeywordUtil.logInfo("=== TC-010 PASSED ✅ ===")