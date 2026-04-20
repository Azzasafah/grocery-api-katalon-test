<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Replace product in cart</name>
   <tag></tag>
   <elementGuidId>7797ffe2-b368-447f-b3c5-f9584031b8ba</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <smartLocatorEnabled>false</smartLocatorEnabled>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>false</autoUpdateContent>
   <connectionTimeout>0</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n    \&quot;productId\&quot;: \&quot;${productId}\&quot;,\n    \&quot;quantity\&quot;: \&quot;${quantity}\&quot;\n}&quot;,
  &quot;contentType&quot;: &quot;text/plain&quot;,
  &quot;charset&quot;: &quot;UTF-8&quot;
}</httpBodyContent>
   <httpBodyType>text</httpBodyType>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>e058032e-f33a-4c53-8028-4392a44c44e0</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>11.1.1</katalonVersion>
   <maxResponseSize>0</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <path></path>
   <restRequestMethod>PUT</restRequestMethod>
   <restUrl>${baseUrl}/carts/${cartId}/items/${itemId}</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceEndpoint></soapServiceEndpoint>
   <soapServiceFunction></soapServiceFunction>
   <socketTimeout>0</socketTimeout>
   <useServiceInfoFromWsdl>true</useServiceInfoFromWsdl>
   <variables>
      <defaultValue>GlobalVariable.baseUrl</defaultValue>
      <description></description>
      <id>1f74f6ff-fda2-4f9b-8da1-e2124e1e0a01</id>
      <masked>false</masked>
      <name>baseUrl</name>
   </variables>
   <variables>
      <defaultValue>'_o9FCzz_54PWqg38R66t-'</defaultValue>
      <description></description>
      <id>95412a5b-4eca-4945-a7c9-4a04d953c0d4</id>
      <masked>false</masked>
      <name>cartId</name>
   </variables>
   <variables>
      <defaultValue>'647108832'</defaultValue>
      <description></description>
      <id>7613d0c5-29dc-41ee-a25d-0f109106b533</id>
      <masked>false</masked>
      <name>itemId</name>
   </variables>
   <variables>
      <defaultValue>'1709'</defaultValue>
      <description></description>
      <id>77348632-f0a2-4bfe-91e6-bdde8f0a4341</id>
      <masked>false</masked>
      <name>productId</name>
   </variables>
   <variables>
      <defaultValue>5</defaultValue>
      <description></description>
      <id>807a3127-b271-4dda-813d-ae39737a70ad</id>
      <masked>false</masked>
      <name>quantity</name>
   </variables>
   <verificationScript>import static org.assertj.core.api.Assertions.*

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webservice.verification.WSResponseManager

import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable

RequestObject request = WSResponseManager.getInstance().getCurrentRequest()

ResponseObject response = WSResponseManager.getInstance().getCurrentResponse()</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
