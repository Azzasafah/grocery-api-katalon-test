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
 *  TC-011 | Tambah Item dengan ProductId Tidak Valid (Negative)
 * ============================================================
 *  Scenario   : TS-010
 *  Type       : NEGATIVE
 *  Priority   : Medium
 *  Endpoint   : POST /carts/:cartId/items
 *  Depends on : TC-006
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

KeywordUtil.logInfo("=== TC-011: Tambah Item dengan ProductId Tidak Valid (Negative) ===")
assert GlobalVariable.cartId != null : "❌ cartId belum ada! Jalankan TC-006."

// Daftar input tidak valid yang akan diuji
def invalidInputs = [
	[productId: 99999999, desc: "productId tidak ada di database"],
	[productId: -1,       desc: "productId negatif"],
	[productId: 0,        desc: "productId = 0"],
]

invalidInputs.each { input ->
	KeywordUtil.logInfo("--- Test input: ${input.desc} (productId: ${input.productId}) ---")

	String url = "${GlobalVariable.baseUrl}/carts/${GlobalVariable.cartId}/items"
	def req = new RequestObject("POST_Invalid_Item_${input.productId}")
	req.setRestUrl(url)
	req.setRestRequestMethod('POST')
	req.setBodyContent(new HttpTextBodyContent(
		"""{"productId": ${input.productId}}""", 'UTF-8', 'application/json'
    ))
    req.setHttpHeaderProperties([
        new TestObjectProperty('Content-Type', ConditionType.EQUALS, 'application/json')
    ])

    def response = WS.sendRequest(req)
    int status = response.getStatusCode()
    KeywordUtil.logInfo("Status: ${status} | Response: ${response.getResponseText()}")

    // TIDAK boleh 201 untuk productId tidak valid
    assert status != 201 :
        "❌ BUG! productId '${input.productId}' (${input.desc}) tidak seharusnya berhasil ditambahkan!"

    assert status == 400 || status == 404 :
        "❌ Expected 400 atau 404, Actual: ${status}"

    KeywordUtil.logInfo("✅ ${input.desc} → Status ${status} (Benar!)")
}

KeywordUtil.logInfo("=== TC-011 PASSED ✅ — Semua input tidak valid ditolak dengan benar ===")