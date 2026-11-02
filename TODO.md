## Current tasks
## **5.** Home menu + `promptText` / `promptAmount` + `addDeposit()` / `makePayment()` + `recordTransaction()` + `saveTransaction()`.
- [x] addDeposit()
- [] makePayment()
- [x] recordTransaction()
- [] saveTransaction()
- [] tidy code

NOT MANDATORY:
- add description length (20 chars)
## General Ideas  

- [ ] Read up on using lanterna for TUI: https://github.com/mabe02/lanterna
- [x] Read documentation and create a list for MVP, Bonus, TUI


Build order: **MVP → Bonus → Extras (TUI)**. Commit after every step (this also covers the spec's commit-history requirement).

Style: menu loops in the main app class, one method per menu option (same pattern as `StoreApp`).

## Classes

| Class | Job |
|---|---|
| `Transaction` | Data only: date, time, description, vendor, amount + getters |
| `AccountingLedgerApp` | `main`, menu loops, and all action/helper methods |
| `FileManager` *(optional)* | `loadTransactions()` / `saveTransaction(t)` if you want file code out of the app class |

### Static fields in `AccountingLedgerApp`

```java
static Scanner scanner = new Scanner(System.in);      // one Scanner for the whole app
static ArrayList<Transaction> transactions;           // loaded once at startup
```

New transactions are added to the list **and** appended to the file, so both stay in sync.

## Method map

**Menus (loops)**
- `main` → `loadTransactions()` → `showHomeScreen()`
- `showHomeScreen()`: D / P / L / X
- `showLedgerScreen()`: A / D / P / R / H
- `showReportsScreen()`: 1–6 / 0

**Actions (one per menu option)**
- `addDeposit()`, `makePayment()`
- `displayAll()`, `displayDeposits()`, `displayPayments()`
- `monthToDate()`, `previousMonth()`, `yearToDate()`, `previousYear()`, `searchByVendor()`
- `customSearch()` *(bonus)*

**Helpers**
- `loadTransactions()`, `saveTransaction(Transaction t)`
- `printTransactions(List<Transaction> list)`: shared display
- `promptText(label)`, `promptAmount(label)`: re-ask on blank or non-numeric input
- `sortNewestFirst(list)`

## Key rule: split "get" from "show"

Keeps Phase 3 (Lanterna) easy, because the TUI can reuse the logic without the console prompts.

- `filterMonthToDate()` **returns** an `ArrayList<Transaction>` and doesn't print anything.
- The menu option calls it and passes the result to `printTransactions(...)`.
- Same for adding: `addDeposit()` prompts, then calls `recordTransaction(desc, vendor, amount)`, which stamps date/time, makes payments negative, adds to the list, and saves.

Lanterna later calls `filterMonthToDate()` and `recordTransaction(...)` directly.

## Lessons from `StoreApp`

- [ ] One shared `Scanner`, not one per method
- [ ] Handle non-numeric input (`promptAmount` + try/catch)
- [ ] Keep new items in the list after saving (don't remove them)

---

## Phase 0 – Setup

- [ ] **1.** Create the repo under `~/pluralsight/capstones` (public on GitHub).
- [x] **2.** Add `transactions.csv` with the header + spec sample rows:
  ```
  date|time|description|vendor|amount
  2023-04-15|10:13:25|ergonomic keyboard|Amazon|-89.50
  2023-04-15|11:15:00|Invoice 1001 paid|Joe|1500.00
  ```

## Phase 1 – MVP

- [X] **4.** `Transaction` + `loadTransactions()` + `printTransactions()` → `displayAll()` works.
- [ ] **5.** Home menu + `promptText` / `promptAmount` + `addDeposit()` / `makePayment()` + `recordTransaction()` + `saveTransaction()`.
- [ ] **6.** Ledger menu: All / Deposits / Payments, newest first.
- [ ] **7.** Reports menu, options 1–5 and 0 Back, using `filter...()` + `printTransactions()`.
- [ ] **8.** Test pass: every menu path, bad input, lowercase keys, empty results.
- [ ] **9.** README: description, how to run, screenshots. Commit *"MVP complete"*.

## Phase 2 – Bonus

- [ ] **10.** Custom search (option 6): blank answer = skip that filter (start date, end date, description, vendor, amount).
- [ ] **11.** Customize: app name, theme, ASCII starter screen, themed sample data.
- [ ] **12.** Polish: colors for deposits/payments, totals under lists, "No transactions found".

## Phase 3 – Extras (Lanterna TUI)

- [ ] **13.** Add Lanterna via Maven; get a blank window running.
- [ ] **14.** `LanternaApp` screens that reuse `filter...()` and `recordTransaction(...)`.
- [ ] **15.** Choose console or TUI at startup (`--tui` argument or menu choice).
- [ ] **16.** Update the README with how to run the TUI.

## Demo prep (10 min)

- [ ] **17.** Walkthrough: deposit → payment → ledger views → a couple of reports → custom search → TUI.
  - Interesting code to show: the get/show split, or custom search skipping blank filters..