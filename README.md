# 🏛️ JDL Accounting Services - Capstone 1

## Project Summary

**JDL Accounting Services** is a Java CLI application for tracking financial transactions. You can record `deposits` and `payments`, which are saved to a CSV file. You can view the `ledger` as all entries, deposits only or payments only.    

You can also run `reports` according to:
- `month-to-date`
- `previous month`
- `year-to-date`
- `previous year`
- `vendor search`

A `custom search` lets you filter by any combination of `date` range, `description`, `vendor` and `amount`.  

## Features

<p align="center">
    <img src= "assets/demo_welcome_screen.png" alt="Welcome Screen">
    <img src="assets/demo_full_menu.png" alt="Full Menu">
    <img src="assets/demo_search.png" alt="Search">
</p>

## How to Run

**Requires:** Java 17+ and IntelliJ

1. Clone the repo:
    ```bash
    git clone https://github.com/josweon/capstone-1.git
    ```
2. Open the folder in IntelliJ.
3. Run `AccountingLedgerApp.java`.

## Project Structure

```
capstone-1/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/pluralsight/
    │   ├── AccountingLedgerApp.java   # menus, reports, file I/O
    │   └── Transaction.java           # transaction model
    └── resources/
        ├── transactions.csv           # ledger data
        └── test-transactions.csv      # sample data for TESTING
```

### Interesting Code
```java
    
    String formatDateTime = String.format("%sT%s", tokens[0], tokens[1]);
    LocalDateTime dateTime = LocalDateTime.parse(formatDateTime);
    String description = tokens[2];
    String vendor = tokens[3];
    double amount = Double.parseDouble(tokens[4]);

    ledger.add(new Transaction(dateTime, description, vendor, amount));
    
```

-[]future Improvements