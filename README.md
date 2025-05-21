# 🎲 Live Betting API

The **Live Betting API** allows real-time creation and viewing of match bulletins and enables users to place bets based on current odds. Odds are updated every second in the background. The application is built using **Spring Boot 3.4.5** and **Java 21**, with in-memory **H2 database** support for persistence.

---

## 📦 Features

- 📝 **Bulletin Management**
  - Create football match events with betting odds.
  - View bulletins with live-updating odds.
  - Odds updated every second using background scheduler.

- 🎫 **BetSlip Operations**
  - Place bets on specific events with selected odds and bet type.
  - Customer ID handled via custom header (`x-customer-id`).
  - Bet multiple times with `multiple` field to simulate high-stake tickets.

- ⏱ **Business Constraints & Validation**
  - Odds isolation: ensures the selected odd matches the current one.
  - Timeout handling: bet operations timeout after 2 seconds (configurable).
  - Maximum 500 multiple bets per event.
  - Maximum total investment limit of 10,000 TL per coupon.

- 🛠 Uses Strategy, Factory, Singleton design patterns
- ✅ Validation: Constraint validation with custom error messages
- 🧪 Unit and integration tests for critical scenarios

---
## 🧪 Testing Notes

- Under `app/assets/http`, you can find Postman-compatible HTTP request examples to manually or automatically test API endpoints.
- While testing `event` creation, **make sure the `startDate` is a valid future time**, otherwise validation will fail with 400 Bad Request.

## 🧰 Technologies Used

- Java 21
- Spring Boot 3.4.5
- Spring Web / JPA / Validation
- H2 In-Memory Database
- Lombok
- JUnit 5 & Mockito
- MapStruct
- Maven
