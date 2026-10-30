package com.pluralsight;

import java.time.LocalDateTime;

public class Transaction {
    private LocalDateTime date;
    private LocalDateTime time;
    private String description;
    private String vendor;
    private double amount;

    public Transaction(LocalDateTime date, LocalDateTime time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    //region Getters
    public LocalDateTime getDate() {
        return date;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getVendor() {
        return vendor;
    }

    public double getAmount() {
        return amount;
    }
    //endregion
    public boolean isDeposit(){
        return this.amount > 0;
    }

    public boolean isPayment(){
        return this.amount > 0;
    }

    // For debugging
    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", vendor='" + vendor + '\'' +
                ", amount=" + amount +
                '}';
    }
}

