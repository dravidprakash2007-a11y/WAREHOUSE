# 📦 Warehouse & Shipping Management System

## 📌 Project Overview

The **Warehouse & Shipping Management System** is a console-based Java application designed to manage warehouse inventory, receive incoming items, create shipments, dispatch orders, track deliveries, and calculate shipping costs.

This project demonstrates the practical implementation of Object-Oriented Programming (OOP) concepts in Java, including encapsulation, inheritance, polymorphism, method overloading, and method overriding.

## ✨ Features

- **Inventory Management:** Receive items and maintain stock quantities.
- **Warehouse Management:** Store warehouse details, location, capacity, and inventory.
- **Shipment Creation:** Create shipments with destination, carrier, and item details.
- **Pick and Pack:** Prepare items for shipment with specialized handling rules.
- **Shipment Dispatch:** Process outbound shipments and update stock.
- **Delivery Tracking:** Track shipment status from creation to successful delivery.
- **Shipping Cost Calculation:** Calculate shipping charges based on weight, distance, and priority.
- **Fragile Item Handling:** Apply special handling rules to fragile products.
- **Perishable Item Handling:** Support special storage rules for perishable products.
- **Stock Snapshots:** Display current inventory and shipping cost summaries.

## 🛠️ Technologies Used

- **Programming Language:** Java
- **Application Type:** Console-Based Application
- **Concepts:** Object-Oriented Programming (OOP)
- **Collections:** Java Collections Framework
- **Development Tools:** Any Java-compatible IDE or text editor

## 🏗️ Project Structure

```text
WarehouseShippingManagement/
│
├── Item.java
├── FragileItem.java
├── PerishableItem.java
├── Warehouse.java
├── Shipment.java
├── LogisticsService.java
├── WarehouseAppMain.java
│
└── README.md
```

## 📚 Classes and Responsibilities

### 1. Item
Represents a product stored in the warehouse.

- SKU
- Item name
- Unit price
- Quantity available
- Item-related getters and setters

### 2. FragileItem
Extends the `Item` class and implements specialized handling requirements for fragile goods.

### 3. PerishableItem
Extends the `Item` class and supports special storage and handling rules for perishable goods.

### 4. Warehouse
Manages warehouse identification, location, capacity, and inventory.

- Receives incoming stock
- Maintains inventory
- Validates stock quantities and capacity
- Displays inventory snapshots

### 5. Shipment
Represents a shipment containing items and delivery information.

- Shipment ID
- Destination
- Item list
- Carrier
- Shipment status

### 6. LogisticsService
Handles logistics operations.

- Inbound inventory processing
- Outbound shipment processing
- Shipment tracking
- Shipping cost calculation
- Billing and cost summaries

### 7. WarehouseAppMain
The main entry point of the application. Demonstrates inventory receiving, shipment creation, dispatch, delivery tracking, and cost summaries.

## 🧠 Object-Oriented Programming Concepts

| Concept | Implementation |
|---|---|
| Encapsulation | Private fields with controlled getters and setters |
| Inheritance | `FragileItem` and `PerishableItem` extend `Item` |
| Method Overloading | Multiple `shippingCost()` methods with different parameters |
| Method Overriding | Specialized packing and handling behavior |
| Polymorphism | Processing different item types through `List<Item>` |

## 🚚 Shipment Tracking Workflow

```text
      START
        |
        v
  Receive Inventory
        |
        v
  Create Shipment
        |
        v
    Pick and Pack
        |
        v
  Dispatch Shipment
        |
        v
  Status: IN_TRANSIT
        |
        v
  Status: DELIVERED
        |
        v
  Display Cost Summary
        |
        v
       END
```

The shipment status progresses through the following stages:

1. `CREATED` – Shipment has been created.
2. `IN_TRANSIT` – Shipment has been dispatched and is on its way.
3. `DELIVERED` – Shipment has reached its destination.

## ⚙️ How to Run the Project

### Prerequisites

- Install the Java Development Kit (JDK).
- Configure Java on your system.
- Open the project in a Java IDE or terminal.

### Compile the Java Files

If all Java files are in the same directory, open a terminal in the project folder and run:

```bash
javac *.java
```

### Run the Application

```bash
java WarehouseAppMain
```

**Note:** If your project uses Java packages, compile and run it using the appropriate package structure and fully qualified class name.

## 📊 Expected Output

The application demonstrates operations such as:

```text
========== WAREHOUSE & SHIPPING SYSTEM ==========

Receiving Inventory...
Item received successfully.

Warehouse Stock Snapshot:
SKU: ITM101
Name: Fragile Product
Quantity: 20
Unit Price: 500.0

Creating Shipment...
Shipment created successfully.

Picking and Packing Items...
Items packed successfully.

Dispatching Shipment...
Shipment Status: IN_TRANSIT

Updating Delivery Status...
Shipment Status: DELIVERED

========== SHIPPING COST SUMMARY ==========
Shipping Cost: Calculated based on shipment details
Delivery Status: DELIVERED

================================================
```

*The displayed values are illustrative; actual output depends on the data and implementation in your Java program.*

## 🎯 Project Objectives

- Understand and apply Java OOP concepts.
- Develop an inventory management workflow.
- Implement shipment creation and delivery tracking.
- Demonstrate inheritance and polymorphism using specialized items.
- Apply method overloading and overriding.
- Practice validation of inventory quantities and shipment statuses.
- Build
