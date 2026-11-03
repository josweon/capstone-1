package com.pluralsight;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class AccountingLedgerApp {
    //region Initializers
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Transaction> ledger = new ArrayList<>();
    static DateTimeFormatter dateOnly = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static DateTimeFormatter timeOnly = DateTimeFormatter.ofPattern("HH:mm:ss");

    static String PROD = "src/main/resources/transactions.csv";
    static String TEST = "src/main/resources/test-transactions.csv";
    //endregion
    public static void main(String[] args) {
        loadTransactions();
        showHomeScreen();
    }

    //region Displays
    public static void displayTransactions(ArrayList<Transaction> transactions, String transType) {
        String rowFormatHeading = "| %-10s | %-8s | %-20s | %-20s | %-10s | %n";
        String rowFormat = "| %-10s | %-8s | %-20s | %-20s | $%-9.2f | %n";

        String separator = "-".repeat(84);
        String heading = " ".repeat(25);

        String transHeader           = "==* T R A N S A C T I O N S *==";
        String depositHeader         = "==* D E P O S I T S *==";
        String paymentsHeader        = "==* P A Y M E N T S *==";
        String reportMonthToDate     = "==* M O N T H * T O * D A T E *==";
        String reportPreviousMonth   = "==* P R E V * M O N T H *==";
        String reportYearToDate      = "==* Y E A R * T O * D A T E *==";
        String reportPreviousYear    = "==* P R E V * Y E A R *==";

        switch (transType) {
            case "a" -> System.out.println(heading + transHeader);
            case "d" -> System.out.println(heading + depositHeader);
            case "p" -> System.out.println(heading + paymentsHeader);
            default -> System.out.println("Invalid operation");
        }

        System.out.println(separator);
        System.out.printf(rowFormatHeading, "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println(separator);

        for (Transaction t : transactions) {
            System.out.printf(rowFormat,
                    t.getDateTime().format(dateOnly),
                    t.getDateTime().format(timeOnly),
                    t.getDescription(),
                    t.getVendor(),
                    t.getAmount());
        }
        System.out.println(separator);
    }

    public static void displayReceipt(ArrayList<Transaction> receipt) {
        String rowFormatHeading = "| %-10s | %-8s | %-20s | %-20s | %-10s | %n";
        String rowFormat = "| %-10s | %-8s | %-20s | %-20s | $%-9.2f | %n";

        String separator = "-".repeat(84);

        System.out.println(separator);
        System.out.printf(rowFormatHeading, "Date", "Time", "Description", "Vendor", "Amount");
        System.out.println(separator);

        for (Transaction t : receipt) {
            System.out.printf(rowFormat,
                    t.getDateTime().format(dateOnly),
                    t.getDateTime().format(timeOnly),
                    t.getDescription(),
                    t.getVendor(),
                    t.getAmount());
        }
    }
    //endregion

    //region Loaders
    public static void loadTransactions() {
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader(TEST));
            // TODO: HANDLE first line safely
            bufReader.readLine();

            String input;
            while ((input = bufReader.readLine()) != null) {
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
    }

    public static ArrayList<Transaction> loadDeposits() {
        ArrayList<Transaction> deposits = new ArrayList<>();
        for (Transaction t : ledger) {
            if (t.isDeposit()) {
                deposits.add(t);
            }
        }
        return deposits;
    }

    public static ArrayList<Transaction> loadPayments() {
        ArrayList<Transaction> payments = new ArrayList<>();
        for (Transaction t : ledger) {
            if (t.isPayment()) {
                payments.add(t);
            }
        }
        return payments;
    }
    //endregion

    //region Prompters
    public static String promptText() {
        // TODO: HANDLE non-string types?
        String text = scanner.nextLine();
        return text.trim().toLowerCase();
    }

    public static double promptAmount() {
        String text;
        double amount = 0;

        try {
            text = scanner.nextLine().trim();
            amount = Double.parseDouble(text);

        } catch (NumberFormatException e) {
            System.out.println("ERROR: Input not <double>");
        }

        return amount;
    }
    //endregion

    //region Menus
    public static void showHomeScreen() {
        Collections.sort(ledger);
        String menu = """
                ==* H O M E S C R E E N *=
                D) Add Deposit
                P) Make Payment (Debit)
                L) Ledger
                X) Exit
                
                Enter option:\s""";

        boolean exit = false;
        do {
            System.out.print(menu);
            String option = promptText();

            switch (option) {
                case "d" -> addDeposit();
                case "p" -> makePayment();
                case "l" -> showLedgerScreen();
                case "x" -> exit = true;
                default -> System.out.println("Invalid Operation");
            }
        } while (!exit);
        System.out.println("Thank you for using JDL services!");
    }

    public static void showLedgerScreen() {
        String menu = """
                \t==* L E D G E R *==
                \tA) All
                \tD) Deposits
                \tP) Payments
                \tR) Reports
                \tH) <-Homescreen
                
                \tEnter option:\s""";

        boolean exit = false;
        do {
            System.out.print(menu);
            String option = promptText();

            switch (option) {
                // TODO: Change ledger to loadTransactions()?
                case "a" -> displayTransactions(ledger, "a");
                case "d" -> displayTransactions(loadDeposits(), "d");
                case "p" -> displayTransactions(loadPayments(), "p");
                case "r" -> showReportsScreen();
                case "h" -> exit = true;
                default -> System.out.println("Invalid Operation");
            }
        } while (!exit);
    }

    // TODO: Report menu screen
    public static void showReportsScreen() {
        String menu = """
                \t\t==* R E P O R T S *==
                \t\t1) Month To Date
                \t\t2) Previous Month
                \t\t3) Year to Date
                \t\t4) Previous Year
                \t\t5) Search by Vendor
                \t\t0) <- Go back
                
                \t\tEnter option:\s""";

        boolean exit = false;
        do {
            System.out.print(menu);
            String option = promptText();

            switch (option) {
                case "1" -> System.out.println("\t\tmonthToDate");
                case "2" -> System.out.println("\t\tpreviousMonth");
                case "3" -> System.out.println("\t\tyearToDate()");
                case "4" -> System.out.println("\t\tdisplayReports()");
                case "5" -> System.out.println("\t\tsearchByVendor()");
                case "0" -> exit = true;
                default -> System.out.println("Invalid Operation");
            }
        } while (!exit);
    }
    public static ArrayList<Transaction> monthToDate (){
        ArrayList<Transaction> report = new ArrayList<>();
        LocalDateTime currentDate = LocalDateTime.now();

        for (Transaction t: ledger){
        }
        return report;
    }
    //endregion

    //region Helpers
    public static void addDeposit() {
        System.out.println("\t==* A D D * D E P O S I T *==");
        ArrayList<Transaction> receipt = new ArrayList<>();

        String done;
        do {
            System.out.print("\tEnter amount: $");
            double amount = promptAmount();
            System.out.print("\tEnter description: ");
            // TODO: Do while for description to valid
            String description = scanner.nextLine().trim();
            System.out.print("\tEnter vendor: ");
            String vendor = scanner.nextLine().trim();

            LocalDateTime now = LocalDateTime.now();

            Transaction newTrans = new Transaction(now, description, vendor, amount);
            recordTransaction(newTrans, receipt);

            System.out.print("Would you like to add another? (y/n): ");
            done = promptText();

        } while (!done.equalsIgnoreCase("n"));
        System.out.print("\n");

        if (!receipt.isEmpty()) {
            saveTransaction(receipt);
        }
    }

    public static void makePayment() {
        System.out.println("\t==* M A K E * P A Y M E N T *==");
        ArrayList<Transaction> receipt = new ArrayList<>();

        System.out.print("\tPayment to?: ");
        String description = promptText();

        // TODO: Handle duplicates and only display last 4 digits
        int attempts = 3;
        boolean valid = false;

        String vendor = null;
        do {
            if (attempts <= 1) {
                System.out.println("\tretry later");
                break;
            }

            System.out.print("\tEnter Debit card: ");
            vendor = promptText();

            if (vendor == null) {
                System.out.printf("%n\tNo information added%n\t%d Attempts left...%n%n", attempts);
                attempts --;
            }else if (vendor.length() < 16) {
                System.out.printf("%n\tPlease enter a valid card%n\t%d Attempts left...%n%n", attempts);
                attempts --;
            } else {
                valid = true;
            }

        } while(!valid);

        System.out.print("\tEnter amount: $");
        double amount = promptAmount() * -1;
        LocalDateTime now = LocalDateTime.now();

        Transaction newTrans = new Transaction(now, description, vendor, amount);
        recordTransaction(newTrans, receipt);
        saveTransaction(receipt);
    }

    public static void saveTransaction(ArrayList<Transaction> receipt) {
        try {
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter(TEST, true));
            for (Transaction t : receipt) {
                String date = (t.getDateTime()).format(dateOnly);
                String time = (t.getDateTime()).format(timeOnly);
                String record = String.format("%n%s|%s|%s|%s|%.2f",
                        date,
                        time,
                        t.getDescription(),
                        t.getVendor(),
                        t.getAmount());

                bufWriter.write(record);
            }
            bufWriter.close();
        } catch (IOException e) {
            System.out.println("ERROR: Cannot write to file " + e.getMessage());
        }
    }

    public static void recordTransaction(Transaction t, ArrayList<Transaction> receipt) {
        ledger.add(t);
        receipt.add(t);
        System.out.println("\nsuccess!");
        displayReceipt(receipt);
    }
    //endregion
}
