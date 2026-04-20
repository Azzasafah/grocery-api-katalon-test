<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>Update order</name>
   <tag></tag>
   <elementGuidId>b48f4ca5-01f7-4703-9195-4bc7e7c4ffb7</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <smartLocatorEnabled>false</smartLocatorEnabled>
   <useRalativeImagePath>false</useRalativeImagePath>
   <autoUpdateContent>false</autoUpdateContent>
   <connectionTimeout>0</connectionTimeout>
   <followRedirects>false</followRedirects>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n    \&quot;customerName\&quot;: \&quot;${customerName}\&quot;,\n    \&quot;comment\&quot;: \&quot;${comment}\&quot;\n}&quot;,
  &quot;contentType&quot;: &quot;application/json&quot;,
  &quot;charset&quot;: &quot;UTF-8&quot;
}</httpBodyContent>
   <httpBodyType>text</httpBodyType>
   <httpHeaderProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Authorization</name>
      <type>Main</type>
      <value>Bearer ${accessToken}</value>
      <webElementGuid>ac59c495-416b-440f-a333-9fdeedf31b8c</webElementGuid>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
      <webElementGuid>782d8288-b170-4151-a3c3-5dbc0a981b87</webElementGuid>
   </httpHeaderProperties>
   <katalonVersion>11.1.1</katalonVersion>
   <maxResponseSize>0</maxResponseSize>
   <migratedVersion>5.4.1</migratedVersion>
   <path></path>
   <restRequestMethod>PATCH</restRequestMethod>
   <restUrl>${baseUrl}/orders/${orderId}</restUrl>
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
      <id>2813b09f-e196-4eed-9179-48d5c9671393</id>
      <masked>false</masked>
      <name>baseUrl</name>
   </variables>
   <variables>
      <defaultValue>GlobalVariable.accessToken</defaultValue>
      <description></description>
      <id>5ff163ad-5632-4be0-8244-2c57f3daf4c5</id>
      <masked>false</masked>
      <name>accessToken</name>
   </variables>
   <variables>
      <defaultValue>'kXPLrO5MMm4iNoiKTEEr0'</defaultValue>
      <description></description>
      <id>56dd00ed-9ea9-499c-986b-abd0df066a50</id>
      <masked>false</masked>
      <name>orderId</name>
   </variables>
   <variables>
      <defaultValue>'hafizh'</defaultValue>
      <description></description>
      <id>5ff34170-c9b6-47a6-b481-124c59b4650c</id>
      <masked>false</masked>
      <name>customerName</name>
   </variables>
   <variables>
      <defaultValue>'pickup 2 AM'</defaultValue>
      <description></description>
      <id>a6f15610-fbf6-490c-bfd1-2471c283ca72</id>
      <masked>false</masked>
      <name>comment</name>
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
