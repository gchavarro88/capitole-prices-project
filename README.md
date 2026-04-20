# Capitole Prices Service

REST API service for querying product prices based on application date, product ID, and brand.

## Table of Contents
- [Overview](#overview)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Running Tests](#running-tests)
- [API Endpoints](#api-endpoints)
- [Test Cases](#test-cases)
- [Project Structure](#project-structure)

## Overview

This service implements a pricing system that:
- Retrieves applicable prices for products based on date ranges
- Handles overlapping price periods by priority
- Supports multiple brands and price lists
- Follows hexagonal architecture (ports and adapters)

### Business Rules
- When multiple prices apply for the same period, the one with **higher priority** is returned
- Prices are valid within their `startDate` and `endDate` range
- All prices must be non-negative
- Currency is returned with each price (EUR)

##  Technologies

- **Java 21**
- **Spring Boot 3.3.0**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Maven 3.6+**
- **JUnit 5** & **Mockito** (testing)
- **AssertJ** (assertions)

##  Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Git

### Verify Java Version
```bash
java -version
# Should show: openjdk version "21.0.x" or higher
```

### Set Java Home (macOS)
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd capitole-prices-project
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start on: **http://localhost:8080**

### 4. Access H2 Console (Optional)
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:pricesdb`
- Username: `sa`
- Password: *(empty)*

##  API Documentation

### Option 1: OpenAPI Specification File
The API is documented using OpenAPI 3.0 specification:
```
src/main/resources/openapi/prices-api.yaml
```

## Running Tests

### Run All Tests (18 tests)
```bash
mvn test
```

### Run Integration Tests Only
```bash
mvn test -Dtest=PriceControllerIntegrationTest
```

### Run Unit Tests Only
```bash
mvn test -Dtest=PriceServiceTest,PriceTest
```

### Run Specific Test
```bash
mvn test -Dtest=PriceControllerIntegrationTest#getPriceWhenRangeIsOnThePriorityZeroAndPriceListOneTest
```

### Test Results
```
Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
```

## API Endpoints

### GET /price
Retrieve the applicable price for a product.

**Parameters:**
| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| applicationDate | DateTime | Yes | ISO 8601 date-time | 2020-06-14T10:00:00+00:00 |
| productId | Long | Yes | Product identifier | 35455 |
| brandId | Integer | Yes | Brand identifier (1=ZARA) | 1 |

**Example Request:**
```bash
curl -X GET "http://localhost:8080/price?applicationDate=2020-06-14T10:00:00%2B00:00&productId=35455&brandId=1"
```

**Example Response:**
```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "2020-06-14T00:00:00Z",
  "endDate": "2020-12-31T23:59:59Z",
  "price": 35.50,
  "currency": "EUR"
}
```

**Error Response (404):**
```json
{
  "message": "No price found for productId: 35455, brandId: 1, applicationDate: 2020-06-14T10:00:00",
  "timestamp": "2026-04-19T10:00:00Z"
}
```

## Test Cases

The following test cases are implemented and passing:

| # | Date & Time | Product | Brand | Expected Price | Expected List | Description |
|---|-------------|---------|-------|----------------|---------------|-------------|
| 1 | 2020-06-14 10:00 | 35455 | 1 | 35.50 EUR | 1 | Base price |
| 2 | 2020-06-14 16:00 | 35455 | 1 | 25.45 EUR | 2 | Promotion (higher priority) |
| 3 | 2020-06-14 21:00 | 35455 | 1 | 35.50 EUR | 1 | Back to base after promo |
| 4 | 2020-06-15 10:00 | 35455 | 1 | 30.50 EUR | 3 | Morning promotion |
| 5 | 2020-06-16 21:00 | 35455 | 1 | 38.95 EUR | 4 | New price |

### CURL Commands for Testing

```bash
# Test 1: 10:00 on June 14th - Expected: 35.50 EUR
curl -X GET "http://localhost:8080/price?applicationDate=2020-06-14T10:00:00%2B00:00&productId=35455&brandId=1"

# Test 2: 16:00 on June 14th - Expected: 25.45 EUR (promotion)
curl -X GET "http://localhost:8080/price?applicationDate=2020-06-14T16:00:00%2B00:00&productId=35455&brandId=1"

# Test 3: 21:00 on June 14th - Expected: 35.50 EUR
curl -X GET "http://localhost:8080/price?applicationDate=2020-06-14T21:00:00%2B00:00&productId=35455&brandId=1"

# Test 4: 10:00 on June 15th - Expected: 30.50 EUR
curl -X GET "http://localhost:8080/price?applicationDate=2020-06-15T10:00:00%2B00:00&productId=35455&brandId=1"

# Test 5: 21:00 on June 16th - Expected: 38.95 EUR
curl -X GET "http://localhost:8080/price?applicationDate=2020-06-16T21:00:00%2B00:00&productId=35455&brandId=1"
```

## Database Schema

### PRICES Table

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| ID | BIGINT | NO | Primary key (auto-generated) |
| BRAND_ID | INTEGER | NO | Brand identifier (1 = ZARA) |
| START_DATE | TIMESTAMP | NO | Price validity start date |
| END_DATE | TIMESTAMP | NO | Price validity end date |
| PRICE_LIST | INTEGER | NO | Price list identifier |
| PRODUCT_ID | BIGINT | NO | Product identifier |
| PRIORITY | INTEGER | NO | Price priority (higher = preferred) |
| PRICE | DECIMAL(10,2) | NO | Final sale price |
| CURR | VARCHAR(3) | NO | Currency code (ISO 4217) |

### Sample Data

```sql
INSERT INTO PRICES (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR) VALUES
(1, '2020-06-14 00:00:00', '2020-12-31 23:59:59', 1, 35455, 0, 35.50, 'EUR'),
(1, '2020-06-14 15:00:00', '2020-06-14 18:30:00', 2, 35455, 1, 25.45, 'EUR'),
(1, '2020-06-15 00:00:00', '2020-06-15 11:00:00', 3, 35455, 1, 30.50, 'EUR'),
(1, '2020-06-15 16:00:00', '2020-12-31 23:59:59', 4, 35455, 1, 38.95, 'EUR');
```

## Troubleshooting

### Issue: "Cannot find symbol" errors
**Solution:** Ensure you're using Java 21:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn clean install
```

### Issue: Tests failing
**Solution:** Run tests with clean build:
```bash
mvn clean test
```

### Issue: Port 8080 already in use
**Solution:** Change port in `application.properties`:
```properties
server.port=8081
```

## Notes

- The project uses **manual getters/setters** instead of Lombok to avoid annotation processing issues
- OpenAPI generator plugin is disabled by default (Maven 3.6.3 compatibility issues)
- H2 database is in-memory and resets on each application restart
- All dates should be in ISO 8601 format with timezone offset

## Author

**Gustavo Chavarro Ortiz**

## License

This project is part of the Capitole Interview Test.
