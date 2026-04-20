

---

# 🧪 Grocery Store API Testing (Katalon)

## 📌 Overview

This project contains automated API testing for the **Simple Grocery Store API**, built using **Katalon Studio**.

The goal is to validate:

* Functional correctness of endpoints
* End-to-end business flow (cart → order)
* Error handling through negative scenarios

---

## 🚀 Tech Stack

* **Katalon Studio**
* **Groovy**
* **REST API Testing**
* **Postman Collection (as reference)**

---

## 🌐 API Under Test

Base URL:

```bash
https://simple-grocery-store-api.click
```

### Core Endpoints Tested

* `GET /status`
* `GET /products`
* `GET /products/:id`
* `POST /carts`
* `POST /carts/:cartId/items`
* `PATCH /carts/:cartId/items/:itemId`
* `PUT /carts/:cartId/items/:itemId`
* `DELETE /carts/:cartId/items/:itemId`
* `POST /api-clients`
* `POST /orders` (requires Bearer Token)
* `GET /orders`
* `PATCH /orders/:orderId`
* `DELETE /orders/:orderId`

---

## 📂 Project Structure

```bash
GroceryStoreAPITest/
├── Test Cases/
├── Test Suites/
├── Object Repository/
├── Profiles/
├── Reports/
```

---

## 🧩 Test Suite Structure

### 🔹 TS_Smoke

Basic validation to ensure API is running.

```text
TC01_CheckAPIStatus
TC02_GetAllProduct
```

---

### 🔹 TS_Positive_Flow (End-to-End)

Simulates full user journey:

```text
Create Cart → Add Item → Update → Replace → Delete
→ Register → Create Order → Get Order → Update → Delete
```

Test Cases:

```text
TC06_CreateNewCart
TC07_AddItemToCart
TC08_UpdateQuantityInCart
TC09_ReplaceProductInCart
TC10_DeleteItemFromCart
TC12_Register
TC14_CreateOrder
TC16_GetAllOrders
TC17_UpdateOrderComment
TC18_DeleteOrder
```

---

### 🔹 TS_Negative_API

Validates API error handling:

```text
TC05_GetProductInvalidId
TC11_AddInvalidProductToCart
TC13_RegisterDuplicateEmail
TC15_CreateOrderWithoutToken
```

---

# 📊 Test Coverage Matrix

| Endpoint         | Method | Positive | Negative | Reason                    |
| ---------------- | ------ | -------- | -------- | ------------------------- |
| /status          | GET    | TC01     | -        | API health check          |
| /products        | GET    | TC02     | -        | Validate product list     |
| /products/:id    | GET    | TC03     | TC05     | Validate valid/invalid ID |
| /carts           | POST   | TC06     | -        | Create cart for flow      |
| /carts/:id/items | POST   | TC07     | TC11     | Add item validation       |
| /api-clients     | POST   | TC12     | TC13     | Auth & duplicate email    |
| /orders          | POST   | TC14     | TC15     | Secure order creation     |

---

## 🔄 Test Flow (Key Scenario)

### 🛒 Cart Flow

1. Create cart
2. Add item
3. Update quantity
4. Replace product
5. Delete item
6. Verify cart is empty

---

### 📦 Order Flow

1. Register API client → get `accessToken`
2. Create cart → get `cartId`
3. Add item to cart
4. Create order using:

```json
{
  "cartId": "xxx",
  "customerName": "Your Name"
}
```

5. Validate order creation
6. Update order
7. Delete order

---

## 🔐 Authentication

Order endpoints require:

```http
Authorization: Bearer <accessToken>
```

Token is generated via:

```
POST /api-clients
```

---

## ⚠️ Key Validations Implemented

* Status code verification (200, 201, 204, 400, 401)
* Response structure validation
* Dynamic data handling (cartId, itemId, orderId)
* Precondition checks (cart must exist & contain items)
* Negative scenario assertions

---

## 🧠 QA Highlights

This project demonstrates:

```text
✔ API Automation Testing
✔ Positive & Negative Testing
✔ End-to-End Flow Validation
✔ Dynamic Data Handling (Global Variables)
✔ Test Suite Structuring
```

---

## ▶️ How to Run

1. Open project in **Katalon Studio**
2. Select Test Suite:

   * `TS_Smoke`
   * `TS_Positive_Flow`
   * `TS_Negative_API`
3. Click **Run**

---

## 📊 Sample Result

```text
TS_Smoke        → ✅ Passed
TS_Positive     → ✅ Passed
TS_Negative     → ✅ Passed
```

---

## 📎 Reference

API Collection:

* - Postman Collection used as baseline for endpoints and request structure  
- Available in project folder: `APICollection/`
- File: `SimpleGroceryStoreAPI.postman_collection`

---

## 💡 Future Improvements

* CI/CD Integration (GitHub Actions)
* Test Data Management
* Reporting enhancement (Allure / HTML Report)
* Regression Test Suite

---

## 👤 Author

**Safah**
QA Engineer (Aspiring)

---

## ⭐ Notes

This project is built as a **portfolio-level API automation project** demonstrating real-world QA workflow and structure.

---
