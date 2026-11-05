

### Interesting Code
```java
    
    String formatDateTime = String.format("%sT%s", tokens[0], tokens[1]);
    LocalDateTime dateTime = LocalDateTime.parse(formatDateTime);
    String description = tokens[2];
    String vendor = tokens[3];
    double amount = Double.parseDouble(tokens[4]);

    ledger.add(new Transaction(dateTime, description, vendor, amount));
    
```