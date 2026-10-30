package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class AccountingLedgerApp {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Transaction> ledger = new ArrayList<>();

    public static void main(String[] args) {
        DateTimeFormatter dateOnly = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeOnly = DateTimeFormatter.ofPattern("HH-mm-ss");

        try {
            BufferedReader bufReader = new BufferedReader(new FileReader("src/main/resources/transactions.csv"));
            // TODO: HANDLE first line safely
            bufReader.readLine();

            String input;
            while((input = bufReader.readLine()) != null){
                // TODO: HANDLE missing delimiter
                String[] tokens = input.split("\\|");

                // date|time|description|vendor|amount
                String formatDateTime = String.format("%sT%s", tokens[0], tokens[1]);
                LocalDateTime dateTime = LocalDateTime.parse(formatDateTime);
                String description = tokens[2];
                String vendor = tokens[3];
                double amount = Double.parseDouble(tokens[4]);

                ledger.add(new Transaction(dateTime, description, vendor, amount));
            }
            bufReader.close();

        } catch (IOException e) {
            System.out.println("ERROR: Cannot read file" + e.getMessage());
        }

        // Test ledger
        for (Transaction t: ledger) {
            System.out.println(t.toString());
        }
    }
}
