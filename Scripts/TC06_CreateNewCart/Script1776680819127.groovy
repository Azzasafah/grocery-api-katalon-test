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
 *  TC-006 | Membuat Keranjang Belanja Baru
 * ============================================================
 *  Scenario   : TS-006
 *  Type       : Positive
 *  Priority   : Critical ← Kritis! Cart adalah fondasi semua test Cart & Order
 *  Endpoint   : POST /carts
 *  Auth       : Tidak diperlukan
 *
 *  Note:
 *    Perhatikan bahwa menyimpan cartId ke GlobalVariable.
 *    Ini adalah pola SHARED STATE — satu test case menyiapkan
 *    data untuk test case berikutnya. Urutan eksekusi menjadi
 *    penting, dan itulah kenapa Test Suite harus diatur dengan benar.
 * ============================================================
 */

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.util.KeywordUtil
import groovy.json.JsonSlurper
import internal.GlobalVariable

KeywordUtil.logInfo("=== TC-006: Membuat Keranjang Belanja Baru ===")
KeywordUtil.logInfo("Endpoint: POST ${GlobalVariable.baseUrl}/carts")

// ── STEP 1: Kirim POST /carts (tanpa body) ──
def response = WS.sendRequest(findTestObject('Simple Grocery Store API/Cart/Create a new cart'))

// ── STEP 2: Status code harus 201 Created ──
WS.verifyResponseStatusCode(response, 201)
KeywordUtil.logInfo("✅ STEP 2 PASS — Status Code: 201 Created")

// ── STEP 3: Parse response ──
def json = new JsonSlurper().parseText(response.getResponseText())
KeywordUtil.logInfo("Response: ${response.getResponseText()}")

// ── STEP 4: Response harus mengandung cartId ──
assert json.cartId != null               : "❌ Field 'cartId' tidak ada dalam response!"
assert json.cartId instanceof String     : "❌ 'cartId' harus bertipe String"
assert json.cartId.trim().length() > 0   : "❌ 'cartId' tidak boleh string kosong!"
KeywordUtil.logInfo("✅ STEP 4 PASS — Cart ID: ${json.cartId}")

// ── STEP 5: Simpan cartId ke GlobalVariable ──
GlobalVariable.cartId = json.cartId
KeywordUtil.logInfo("✅ STEP 5 — cartId disimpan ke GlobalVariable: ${GlobalVariable.cartId}")

// ── STEP 6: Buat cart kedua, pastikan cartId berbeda (uniqueness test) ──
def response2 = WS.sendRequest(findTestObject('Simple Grocery Store API/Cart/Create a new cart'))
WS.verifyResponseStatusCode(response2, 201)
def json2 = new JsonSlurper().parseText(response2.getResponseText())

assert json2.cartId != json.cartId :
	"❌ Dua cart yang dibuat memiliki cartId yang sama! Seharusnya selalu unik."
KeywordUtil.logInfo("✅ STEP 6 PASS — CartId selalu unik. Cart1: ${json.cartId} | Cart2: ${json2.cartId}")

KeywordUtil.logInfo("=== TC-006 PASSED ✅ — cartId aktif: ${GlobalVariable.cartId} ===")