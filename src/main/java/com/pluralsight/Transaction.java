package com.pluralsight;

import java.time.LocalDateTime;

public class Transaction implements Comparable<Transaction> {
    private LocalDateTime dateTime;
    private String description;
    private String vendor;
    private double amount;

    public Transaction(LocalDateTime dateTime, String description, String vendor, double amount) {
        this.dateTime = dateTime;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    //region Getters
    public LocalDateTime getDateTime() {
        return dateTime;
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

    //region Checkers
    public boolean isDeposit() {
        return this.amount > 0;
    }

    public boolean isPayment() {
        return this.amount < 0;
    }
    //endregion

    // For debugging
    @Override
    public String toString() {
        return "Transaction{" +
                "dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", vendor='" + vendor + '\'' +
                ", amount=" + amount +
                '}';
    }

    @Override
    public int compareTo(Transaction o) {
        return o.getDateTime().compareTo(this.dateTime);
    }
}
