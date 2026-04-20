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
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.testobject.RequestObject
import groovy.json.JsonSlurper
import internal.GlobalVariable

/**
 * ============================================================
 *  TC-005 | Get Product dengan ID yang TIDAK ADA (Negative Test)
 * ============================================================
 *  Scenario   : TS-005
 *  Type       : NEGATIVE ← Ini adalah negative test!
 *  Priority   : High
 *  Endpoint   : GET /products/99999999
 *  Auth       : Tidak diperlukan
 *
 *  Note:
 *    Negative test memberikan error yang benar saat menerima input tidak valid.
 *    Bayangkan jika API mengembalikan 200 dengan data kosong
 *    untuk ID yang tidak ada — itu adalah BUG!
 * ============================================================
 */

KeywordUtil.logInfo("=== TC-005: Get Product dengan ID Tidak Ada (Negative) ===")

// ── Daftar ID tidak valid yang akan ditest ──
def invalidIds = [99999999, 0, -1, 'abcdef']

invalidIds.each { invalidId ->
   KeywordUtil.logInfo("--- Testing dengan productId: ${invalidId} ---")

   // Build request dinamis
   def req = new RequestObject("GET_Product_Invalid_${invalidId}")
	req.setRestUrl("${GlobalVariable.baseUrl}/products/${invalidId}")
	req.setRestRequestMethod('GET')

   def response = WS.sendRequest(req)
   int statusCode = response.getStatusCode()

   KeywordUtil.logInfo("Status Code: ${statusCode}")
   KeywordUtil.logInfo("Response: ${response.getResponseText()}")

   // ── VALIDASI UTAMA: Status TIDAK boleh 200 untuk ID tidak valid ──
   assert statusCode != 200 :
	   "❌ BUG DITEMUKAN! productId '${invalidId}' tidak ada tapi API mengembalikan 200 OK!"

   // ── Status harus 400 (Bad Request) atau 404 (Not Found) ──
   assert statusCode == 400 || statusCode == 404 :
	   "❌ Status code tidak sesuai untuk invalid ID! Expected 400/404, Actual: ${statusCode}"

   // ── Response harus mengandung pesan error ──
   def responseText = response.getResponseText()
   assert responseText != null && responseText.length() > 0 :
	   "❌ Response body kosong untuk error response!"

   // Coba parse sebagai JSON untuk cek error message
   try {
	   def errorJson = new JsonSlurper().parseText(responseText)
	   assert errorJson.error != null || errorJson.message != null :
		   "❌ Error response tidak mengandung field 'error' atau 'message'!"
	   KeywordUtil.logInfo("✅ Error message: ${errorJson.error ?: errorJson.message}")
   } catch(Exception e) {
	   // Response bukan JSON — masih acceptable
	   KeywordUtil.logInfo("Response bukan JSON: ${responseText}")
   }

   KeywordUtil.logInfo("✅ productId '${invalidId}' → Status ${statusCode} (Correct!)")
}

KeywordUtil.logInfo("=== TC-005 PASSED ✅ — Semua invalid ID ditangani dengan benar ===")