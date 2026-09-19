

# Library Management System (Java)

This is a console-based Library Management System I built for my MIS assignment. It's a simple Java program that lets you manage books, members, and keep track of who has borrowed what — all through a text menu, no GUI, no database. Everything just gets saved to plain `.txt` files.

I mainly built this to practice OOP (classes, encapsulation, that kind of thing) and basic file handling in Java, since that's what the assignment was about.

## What it can do

- Add, view, search, update and delete books
- Add, view, search, update and delete members
- Issue a book to a member and return it later
- Automatically calculates a late fine if a book is returned past the due date (14 days, Rs. 5/day — you can change these)
- Auto-generates IDs so you don't have to type them yourself (B001, M001, I001...)
- A few basic reports — total books, how many are issued, overdue books, fines collected, etc.
- Handles bad input reasonably well (typing letters where it expects a number, invalid menu options, that sort of thing)
- Saves everything to text files so your data is still there next time you run it

## Project structure

```
src/
├── Book.java
├── Member.java
├── IssueRecord.java
├── FileHandler.java
├── LibraryManager.java
└── Main.java
```

`Main.java` is the entry point with all the menus. `LibraryManager.java` has most of the actual logic (adding books, issuing, returning, reports, etc). `FileHandler.java` just deals with reading/writing the text files so I didn't have to repeat that code everywhere.

There's also a single-file version if you need everything in one `.java` file — same code, just all six classes stacked into one `Main.java`.

A `data/` folder gets created automatically the first time you run it and add something. That's where `books.txt`, `members.txt` and `issues.txt` live.

## How to run it

You just need the JDK installed. Check with:

```bash
java -version
javac -version
```

Then from inside the `src` folder:

```bash
javac *.java
java Main
```

That's it, no setup, no dependencies.

## A quick look

```
=====================================================
     WELCOME TO THE LIBRARY MANAGEMENT SYSTEM
=====================================================

----------------- MAIN MENU -----------------
1. Book Management
2. Member Management
3. Issue / Return Book
4. Reports
5. Exit
-----------------------------------------------
Enter your choice:
```

Pretty much every option leads to its own sub-menu (add/view/search/update/delete).

## Changing the loan period or fine

These are just constants at the top of `LibraryManager.java`:

```java
private static final int LOAN_PERIOD_DAYS = 14;
private static final double FINE_PER_DAY = 5.0;
```

Change the numbers, recompile, done.

## How the data is stored

Nothing fancy, just comma-separated lines in text files:

```
books.txt   -> B001,The Hobbit,J.R.R. Tolkien,Fiction,3,2
members.txt -> M001,Ramesh Shrestha,9800000000,ramesh@example.com,19-09-2026
issues.txt  -> I001,B001,M001,19-09-2026,03-10-2026,NOT_RETURNED,0.0,ISSUED
```

## Things I might add later

- Waitlist for books that are fully checked out
- Maybe export a report as a CSV file
- A basic login for the librarian
- Possibly move to SQLite instead of text files, just to try it out

## Notes

This was written for a coursework assignment so I kept the code fairly simple on purpose — plain loops and ArrayLists instead of streams/lambdas, since that's roughly where I'm at with Java right now. Feel free to fork it or use it as a reference if you're working on something similar.
