// LibraryManagementSystem.java
// ------------------------------------------------------------
// This is the SINGLE-FILE version of my Library Management
// System project. I originally wrote this as separate classes
// (Book, Member, IssueRecord, FileHandler, LibraryManager, Main)
// but combined everything into one file here since that's how
// I was asked to submit it. Java only allows ONE public class
// per file, and it must match the file name, so "Main" is the
// public class here and everything else is a normal (package-
// private) class below it in the same file.
// ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

// Book.java
// This class represents one Book in our library system.
// I am keeping it simple: every book has an id, title, author,
// category, how many total copies we own, and how many are
// currently available (not issued to anyone).

class Book {

    // ---------- fields (encapsulated using private) ----------
    private String bookId;
    private String title;
    private String author;
    private String category;
    private int totalCopies;
    private int availableCopies;

    // ---------- constructor ----------
    // I made a constructor that takes all the values at once.
    // This is easier for a beginner than making multiple constructors.
    public Book(String bookId, String title, String author, String category,
                int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // ---------- getters ----------
    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    // ---------- setters ----------
    // I need setters because the librarian can update book details
    // and because copies change when books are issued/returned.
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    // ---------- helper methods ----------

    // This method reduces available copies by 1 when a book is issued.
    // I added a check so available copies never go below zero.
    public void decreaseAvailableCopies() {
        if (this.availableCopies > 0) {
            this.availableCopies = this.availableCopies - 1;
        }
    }

    // This method increases available copies by 1 when a book is returned.
    // I also made sure available copies never go above total copies,
    // just in case something goes wrong with the data.
    public void increaseAvailableCopies() {
        if (this.availableCopies < this.totalCopies) {
            this.availableCopies = this.availableCopies + 1;
        }
    }

    // Quick way to check if at least one copy is free to issue.
    public boolean isAvailable() {
        return this.availableCopies > 0;
    }

    // ---------- file handling helpers ----------
    // I am saving books in a simple CSV (comma separated) format
    // inside a text file. This method converts a Book object into
    // one line of text that can be written to the file.
    public String toFileLine() {
        return bookId + "," + title + "," + author + "," + category + ","
                + totalCopies + "," + availableCopies;
    }

    // This is the opposite of toFileLine(). It takes one line from the
    // file and rebuilds a Book object from it. I used split(",") to
    // break the line into separate pieces.
    public static Book fromFileLine(String line) {
        String[] parts = line.split(",");
        String id = parts[0];
        String title = parts[1];
        String author = parts[2];
        String category = parts[3];
        int total = Integer.parseInt(parts[4]);
        int available = Integer.parseInt(parts[5]);
        return new Book(id, title, author, category, total, available);
    }

    // ---------- display helper ----------
    // I overrode toString() so I can easily print book details
    // in a nice readable format wherever I need to show a book.
    @Override
    public String toString() {
        return "Book ID     : " + bookId + "\n"
                + "Title       : " + title + "\n"
                + "Author      : " + author + "\n"
                + "Category    : " + category + "\n"
                + "Total Copies: " + totalCopies + "\n"
                + "Available   : " + availableCopies;
    }

    // A shorter one-line version used when listing many books in a table.
    public String toShortRow() {
        return String.format("%-8s %-25s %-20s %-15s %-8d %-8d",
                bookId, title, author, category, totalCopies, availableCopies);
    }
}

// Member.java
// This class represents a Member (someone who can borrow books
// from the library). I kept the fields simple: id, name, phone,
// email and the date they joined the library.

class Member {

    // ---------- fields ----------
    private String memberId;
    private String name;
    private String phone;
    private String email;
    private String registrationDate;

    // ---------- constructor ----------
    public Member(String memberId, String name, String phone, String email,
                  String registrationDate) {
        this.memberId = memberId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.registrationDate = registrationDate;
    }

    // ---------- getters ----------
    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    // ---------- setters ----------
    // These are needed so we can update member details later
    // without creating a whole new Member object.
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ---------- file handling helpers ----------
    // Same idea as the Book class - convert this object to one
    // CSV line so it can be saved into members.txt
    public String toFileLine() {
        return memberId + "," + name + "," + phone + "," + email + ","
                + registrationDate;
    }

    // Rebuild a Member object from a saved line of text.
    public static Member fromFileLine(String line) {
        String[] parts = line.split(",");
        String id = parts[0];
        String name = parts[1];
        String phone = parts[2];
        String email = parts[3];
        String regDate = parts[4];
        return new Member(id, name, phone, email, regDate);
    }

    // ---------- display helper ----------
    @Override
    public String toString() {
        return "Member ID   : " + memberId + "\n"
                + "Name        : " + name + "\n"
                + "Phone       : " + phone + "\n"
                + "Email       : " + email + "\n"
                + "Joined On   : " + registrationDate;
    }

    // Short row format for showing many members in a list/table.
    public String toShortRow() {
        return String.format("%-8s %-20s %-15s %-25s %-12s",
                memberId, name, phone, email, registrationDate);
    }
}

// IssueRecord.java
// This class keeps track of one "transaction" - meaning one book
// being issued to one member. It stores the issue date, the due
// date, whether it has been returned yet, and any fine that has
// to be paid if it was returned late.

class IssueRecord {

    // ---------- fields ----------
    private String issueId;
    private String bookId;
    private String memberId;
    private String issueDate;   // format: dd-MM-yyyy
    private String dueDate;     // format: dd-MM-yyyy
    private String returnDate;  // will be "NOT_RETURNED" until the book comes back
    private double fineAmount;
    private String status;      // "ISSUED" or "RETURNED"

    // ---------- constructor ----------
    public IssueRecord(String issueId, String bookId, String memberId,
                        String issueDate, String dueDate, String returnDate,
                        double fineAmount, String status) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    // ---------- getters ----------
    public String getIssueId() {
        return issueId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public String getStatus() {
        return status;
    }

    // ---------- setters ----------
    // These are used when the book is returned - we need to update
    // the return date, fine amount and status of this record.
    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ---------- helper ----------
    // Quick way to check if this record still has the book issued out.
    public boolean isCurrentlyIssued() {
        return status.equals("ISSUED");
    }

    // ---------- file handling helpers ----------
    public String toFileLine() {
        return issueId + "," + bookId + "," + memberId + "," + issueDate + ","
                + dueDate + "," + returnDate + "," + fineAmount + "," + status;
    }

    public static IssueRecord fromFileLine(String line) {
        String[] parts = line.split(",");
        String issueId = parts[0];
        String bookId = parts[1];
        String memberId = parts[2];
        String issueDate = parts[3];
        String dueDate = parts[4];
        String returnDate = parts[5];
        double fine = Double.parseDouble(parts[6]);
        String status = parts[7];
        return new IssueRecord(issueId, bookId, memberId, issueDate, dueDate,
                returnDate, fine, status);
    }

    // ---------- display helper ----------
    @Override
    public String toString() {
        return "Issue ID    : " + issueId + "\n"
                + "Book ID     : " + bookId + "\n"
                + "Member ID   : " + memberId + "\n"
                + "Issue Date  : " + issueDate + "\n"
                + "Due Date    : " + dueDate + "\n"
                + "Return Date : " + returnDate + "\n"
                + "Fine Amount : " + fineAmount + "\n"
                + "Status      : " + status;
    }

    public String toShortRow() {
        return String.format("%-8s %-8s %-10s %-12s %-12s %-14s %-8.2f %-10s",
                issueId, bookId, memberId, issueDate, dueDate, returnDate,
                fineAmount, status);
    }
}

// FileHandler.java
// This class does all the file reading and writing for the whole
// project. I put all the file handling code in one class so that
// I don't repeat myself in LibraryManager.java. This uses basic
// file handling as required (BufferedReader, BufferedWriter,
// FileReader, FileWriter) - no database is used.


class FileHandler {

    // ---------- file names ----------
    // I am storing the data in the "data" folder next to the project
    // so that the text files don't get mixed up with the source code.
    private static final String BOOKS_FILE = "data/books.txt";
    private static final String MEMBERS_FILE = "data/members.txt";
    private static final String ISSUES_FILE = "data/issues.txt";

    // I added this small helper so that the data folder gets created
    // automatically the very first time the program runs, otherwise
    // we would get a "file not found" style error.
    public static void makeSureDataFolderExists() {
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    // ==========================================================
    // BOOK FILE HANDLING
    // ==========================================================

    // This method reads books.txt line by line and turns each line
    // back into a Book object using Book.fromFileLine().
    public static ArrayList<Book> loadBooks() {
        ArrayList<Book> bookList = new ArrayList<Book>();
        File file = new File(BOOKS_FILE);

        // If the file does not exist yet (first run of the program)
        // we just return an empty list instead of crashing.
        if (!file.exists()) {
            return bookList;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                // skip any accidental blank lines in the file
                if (!line.trim().isEmpty()) {
                    Book b = Book.fromFileLine(line);
                    bookList.add(b);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading books file: " + e.getMessage());
        } finally {
            // I always close the reader in a finally block so the
            // file does not stay "locked" even if something goes wrong.
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing books file: " + e.getMessage());
            }
        }
        return bookList;
    }

    // This method takes the whole list of books and writes it back
    // to books.txt. I overwrite the whole file every time instead of
    // appending, this way the file always matches the current data
    // in the program (this is simpler for a beginner project).
    public static void saveBooks(ArrayList<Book> bookList) {
        makeSureDataFolderExists();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(BOOKS_FILE));
            for (int i = 0; i < bookList.size(); i++) {
                Book b = bookList.get(i);
                writer.write(b.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error while saving books file: " + e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing books file: " + e.getMessage());
            }
        }
    }

    // ==========================================================
    // MEMBER FILE HANDLING
    // ==========================================================

    public static ArrayList<Member> loadMembers() {
        ArrayList<Member> memberList = new ArrayList<Member>();
        File file = new File(MEMBERS_FILE);

        if (!file.exists()) {
            return memberList;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Member m = Member.fromFileLine(line);
                    memberList.add(m);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading members file: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing members file: " + e.getMessage());
            }
        }
        return memberList;
    }

    public static void saveMembers(ArrayList<Member> memberList) {
        makeSureDataFolderExists();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(MEMBERS_FILE));
            for (int i = 0; i < memberList.size(); i++) {
                Member m = memberList.get(i);
                writer.write(m.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error while saving members file: " + e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing members file: " + e.getMessage());
            }
        }
    }

    // ==========================================================
    // ISSUE RECORD FILE HANDLING
    // ==========================================================

    public static ArrayList<IssueRecord> loadIssueRecords() {
        ArrayList<IssueRecord> issueList = new ArrayList<IssueRecord>();
        File file = new File(ISSUES_FILE);

        if (!file.exists()) {
            return issueList;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    IssueRecord r = IssueRecord.fromFileLine(line);
                    issueList.add(r);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading issues file: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing issues file: " + e.getMessage());
            }
        }
        return issueList;
    }

    public static void saveIssueRecords(ArrayList<IssueRecord> issueList) {
        makeSureDataFolderExists();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(ISSUES_FILE));
            for (int i = 0; i < issueList.size(); i++) {
                IssueRecord r = issueList.get(i);
                writer.write(r.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error while saving issues file: " + e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing issues file: " + e.getMessage());
            }
        }
    }
}

// LibraryManager.java
// This is the "brain" of the whole project. It stores the list of
// books, members and issue records in memory (ArrayLists), and has
// all the methods needed to add, search, update, delete, issue and
// return books. It also has the report methods.
//
// I load everything from the text files when the program starts,
// and I save back to the files every time something changes. This
// way the data is never lost even if the program is closed.


class LibraryManager {

    // ---------- in-memory data ----------
    private ArrayList<Book> bookList;
    private ArrayList<Member> memberList;
    private ArrayList<IssueRecord> issueList;

    // ---------- settings / constants ----------
    // Members get 14 days to return a book before it becomes overdue.
    private static final int LOAN_PERIOD_DAYS = 14;

    // Fine charged per day if a book is returned late.
    // I picked a simple flat rate so the calculation is easy to follow.
    private static final double FINE_PER_DAY = 5.0;

    // I used this date format everywhere in the project so dates
    // always look the same, for example: 21-09-2026
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // ---------- constructor ----------
    // When LibraryManager is created, I immediately load whatever
    // data already exists in the text files.
    public LibraryManager() {
        FileHandler.makeSureDataFolderExists();
        bookList = FileHandler.loadBooks();
        memberList = FileHandler.loadMembers();
        issueList = FileHandler.loadIssueRecords();
    }

    // ==========================================================
    // ID GENERATION HELPERS
    // ==========================================================
    // These methods create the next ID automatically, like B001,
    // B002, and so on. This avoids the user typing duplicate IDs.

    public String generateNextBookId() {
        int maxNumber = 0;
        for (int i = 0; i < bookList.size(); i++) {
            String id = bookList.get(i).getBookId(); // example: "B007"
            String numberPart = id.substring(1);      // remove the "B"
            int number = Integer.parseInt(numberPart);
            if (number > maxNumber) {
                maxNumber = number;
            }
        }
        int nextNumber = maxNumber + 1;
        return "B" + String.format("%03d", nextNumber); // B001, B002, ...
    }

    public String generateNextMemberId() {
        int maxNumber = 0;
        for (int i = 0; i < memberList.size(); i++) {
            String id = memberList.get(i).getMemberId();
            String numberPart = id.substring(1);
            int number = Integer.parseInt(numberPart);
            if (number > maxNumber) {
                maxNumber = number;
            }
        }
        int nextNumber = maxNumber + 1;
        return "M" + String.format("%03d", nextNumber); // M001, M002, ...
    }

    public String generateNextIssueId() {
        int maxNumber = 0;
        for (int i = 0; i < issueList.size(); i++) {
            String id = issueList.get(i).getIssueId();
            String numberPart = id.substring(1);
            int number = Integer.parseInt(numberPart);
            if (number > maxNumber) {
                maxNumber = number;
            }
        }
        int nextNumber = maxNumber + 1;
        return "I" + String.format("%03d", nextNumber); // I001, I002, ...
    }

    // ==========================================================
    // BOOK MANAGEMENT
    // ==========================================================

    // Adds a new book to the library. Returns true if it worked.
    public boolean addBook(String title, String author, String category, int totalCopies) {
        if (title == null || title.trim().isEmpty()) {
            System.out.println("Book title cannot be empty.");
            return false;
        }
        if (totalCopies <= 0) {
            System.out.println("Total copies must be more than zero.");
            return false;
        }

        String newId = generateNextBookId();
        // when a book is first added, available copies = total copies
        Book newBook = new Book(newId, title, author, category, totalCopies, totalCopies);
        bookList.add(newBook);
        FileHandler.saveBooks(bookList);

        System.out.println("Book added successfully with ID: " + newId);
        return true;
    }

    // Shows all books currently stored in the system.
    public void viewAllBooks() {
        if (bookList.isEmpty()) {
            System.out.println("No books found in the library yet.");
            return;
        }
        System.out.println("\n---------------------- BOOK LIST ----------------------");
        System.out.printf("%-8s %-25s %-20s %-15s %-8s %-8s%n",
                "ID", "Title", "Author", "Category", "Total", "Avail");
        for (int i = 0; i < bookList.size(); i++) {
            System.out.println(bookList.get(i).toShortRow());
        }
        System.out.println("---------------------------------------------------------");
    }

    // Searches for a book by its exact ID and returns the Book object,
    // or null if it was not found. Many other methods reuse this.
    public Book findBookById(String bookId) {
        for (int i = 0; i < bookList.size(); i++) {
            if (bookList.get(i).getBookId().equalsIgnoreCase(bookId)) {
                return bookList.get(i);
            }
        }
        return null; // not found
    }

    // Searches books whose title contains the given keyword.
    // This is a simple "contains" search, not exact match, so the
    // user does not have to type the whole title correctly.
    public void searchBookByTitle(String keyword) {
        boolean found = false;
        System.out.println("\nSearch results for: " + keyword);
        for (int i = 0; i < bookList.size(); i++) {
            Book b = bookList.get(i);
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(b);
                System.out.println("----------------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books matched your search.");
        }
    }

    // Updates the details of a book (except its ID, IDs never change).
    public boolean updateBook(String bookId, String newTitle, String newAuthor,
                               String newCategory, int newTotalCopies) {
        Book b = findBookById(bookId);
        if (b == null) {
            System.out.println("No book found with ID: " + bookId);
            return false;
        }

        // Work out how many copies are currently issued out, so we
        // don't accidentally set total copies lower than that.
        int issuedCopies = b.getTotalCopies() - b.getAvailableCopies();
        if (newTotalCopies < issuedCopies) {
            System.out.println("Cannot set total copies below the number "
                    + "of copies currently issued (" + issuedCopies + ").");
            return false;
        }

        b.setTitle(newTitle);
        b.setAuthor(newAuthor);
        b.setCategory(newCategory);
        // recalculate available copies based on the new total
        int newAvailable = newTotalCopies - issuedCopies;
        b.setTotalCopies(newTotalCopies);
        b.setAvailableCopies(newAvailable);

        FileHandler.saveBooks(bookList);
        System.out.println("Book updated successfully.");
        return true;
    }

    // Deletes a book completely. I don't allow deleting a book that
    // is currently issued to someone, that would cause data problems.
    public boolean deleteBook(String bookId) {
        Book b = findBookById(bookId);
        if (b == null) {
            System.out.println("No book found with ID: " + bookId);
            return false;
        }

        int issuedCopies = b.getTotalCopies() - b.getAvailableCopies();
        if (issuedCopies > 0) {
            System.out.println("Cannot delete this book, " + issuedCopies
                    + " copy/copies are still issued to members.");
            return false;
        }

        bookList.remove(b);
        FileHandler.saveBooks(bookList);
        System.out.println("Book deleted successfully.");
        return true;
    }

    // ==========================================================
    // MEMBER MANAGEMENT
    // ==========================================================

    public boolean addMember(String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Member name cannot be empty.");
            return false;
        }

        String newId = generateNextMemberId();
        String today = LocalDate.now().format(DATE_FORMAT);
        Member newMember = new Member(newId, name, phone, email, today);
        memberList.add(newMember);
        FileHandler.saveMembers(memberList);

        System.out.println("Member registered successfully with ID: " + newId);
        return true;
    }

    public void viewAllMembers() {
        if (memberList.isEmpty()) {
            System.out.println("No members found yet.");
            return;
        }
        System.out.println("\n-------------------------- MEMBER LIST --------------------------");
        System.out.printf("%-8s %-20s %-15s %-25s %-12s%n",
                "ID", "Name", "Phone", "Email", "Joined");
        for (int i = 0; i < memberList.size(); i++) {
            System.out.println(memberList.get(i).toShortRow());
        }
        System.out.println("--------------------------------------------------------------------");
    }

    public Member findMemberById(String memberId) {
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i).getMemberId().equalsIgnoreCase(memberId)) {
                return memberList.get(i);
            }
        }
        return null;
    }

    public void searchMemberByName(String keyword) {
        boolean found = false;
        System.out.println("\nSearch results for: " + keyword);
        for (int i = 0; i < memberList.size(); i++) {
            Member m = memberList.get(i);
            if (m.getName().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(m);
                System.out.println("----------------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No members matched your search.");
        }
    }

    public boolean updateMember(String memberId, String newName, String newPhone, String newEmail) {
        Member m = findMemberById(memberId);
        if (m == null) {
            System.out.println("No member found with ID: " + memberId);
            return false;
        }
        m.setName(newName);
        m.setPhone(newPhone);
        m.setEmail(newEmail);
        FileHandler.saveMembers(memberList);
        System.out.println("Member updated successfully.");
        return true;
    }

    // I don't let the librarian delete a member who still has a book
    // issued to them - that book needs to be returned first.
    public boolean deleteMember(String memberId) {
        Member m = findMemberById(memberId);
        if (m == null) {
            System.out.println("No member found with ID: " + memberId);
            return false;
        }

        for (int i = 0; i < issueList.size(); i++) {
            IssueRecord r = issueList.get(i);
            if (r.getMemberId().equalsIgnoreCase(memberId) && r.isCurrentlyIssued()) {
                System.out.println("Cannot delete this member, they still have "
                        + "a book issued (Issue ID: " + r.getIssueId() + ").");
                return false;
            }
        }

        memberList.remove(m);
        FileHandler.saveMembers(memberList);
        System.out.println("Member deleted successfully.");
        return true;
    }

    // ==========================================================
    // ISSUE / RETURN LOGIC
    // ==========================================================

    // This is the main "issue a book" operation. It checks that both
    // the book and the member exist, and that a copy is available.
    public boolean issueBook(String bookId, String memberId) {
        Book b = findBookById(bookId);
        if (b == null) {
            System.out.println("No book found with ID: " + bookId);
            return false;
        }

        Member m = findMemberById(memberId);
        if (m == null) {
            System.out.println("No member found with ID: " + memberId);
            return false;
        }

        if (!b.isAvailable()) {
            System.out.println("Sorry, no copies of this book are available right now.");
            return false;
        }

        // work out the issue date and due date using today's date
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(LOAN_PERIOD_DAYS);

        String issueId = generateNextIssueId();
        IssueRecord newRecord = new IssueRecord(
                issueId,
                bookId,
                memberId,
                today.format(DATE_FORMAT),
                dueDate.format(DATE_FORMAT),
                "NOT_RETURNED",
                0.0,
                "ISSUED"
        );

        issueList.add(newRecord);
        b.decreaseAvailableCopies();

        FileHandler.saveIssueRecords(issueList);
        FileHandler.saveBooks(bookList);

        System.out.println("Book issued successfully!");
        System.out.println("Issue ID  : " + issueId);
        System.out.println("Due Date  : " + dueDate.format(DATE_FORMAT));
        return true;
    }

    // This method returns a book using its Issue ID, and calculates
    // a fine automatically if the book is being returned late.
    public boolean returnBook(String issueId) {
        IssueRecord record = null;
        for (int i = 0; i < issueList.size(); i++) {
            if (issueList.get(i).getIssueId().equalsIgnoreCase(issueId)) {
                record = issueList.get(i);
                break;
            }
        }

        if (record == null) {
            System.out.println("No issue record found with ID: " + issueId);
            return false;
        }

        if (!record.isCurrentlyIssued()) {
            System.out.println("This book has already been returned.");
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDate dueDate = LocalDate.parse(record.getDueDate(), DATE_FORMAT);

        double fine = 0.0;
        if (today.isAfter(dueDate)) {
            long lateDays = ChronoUnit.DAYS.between(dueDate, today);
            fine = lateDays * FINE_PER_DAY;
        }

        record.setReturnDate(today.format(DATE_FORMAT));
        record.setFineAmount(fine);
        record.setStatus("RETURNED");

        // put the copy back into the available pool
        Book b = findBookById(record.getBookId());
        if (b != null) {
            b.increaseAvailableCopies();
        }

        FileHandler.saveIssueRecords(issueList);
        FileHandler.saveBooks(bookList);

        System.out.println("Book returned successfully!");
        if (fine > 0) {
            System.out.println("This book was returned late.");
            System.out.println("Fine to be paid: Rs. " + fine);
        } else {
            System.out.println("Returned on time, no fine.");
        }
        return true;
    }

    // Shows every issue record (issued and returned) so the librarian
    // can see full transaction history.
    public void viewAllIssueRecords() {
        if (issueList.isEmpty()) {
            System.out.println("No issue records found yet.");
            return;
        }
        System.out.println("\n------------------------------ ISSUE / RETURN HISTORY ------------------------------");
        System.out.printf("%-8s %-8s %-10s %-12s %-12s %-14s %-8s %-10s%n",
                "IssueID", "BookID", "MemberID", "IssueDate", "DueDate",
                "ReturnDate", "Fine", "Status");
        for (int i = 0; i < issueList.size(); i++) {
            System.out.println(issueList.get(i).toShortRow());
        }
        System.out.println("--------------------------------------------------------------------------------------");
    }

    // Shows only the books that are currently issued (not yet returned).
    public void viewCurrentlyIssuedBooks() {
        boolean found = false;
        System.out.println("\n--------------------- CURRENTLY ISSUED BOOKS ---------------------");
        for (int i = 0; i < issueList.size(); i++) {
            IssueRecord r = issueList.get(i);
            if (r.isCurrentlyIssued()) {
                System.out.println(r.toShortRow());
                found = true;
            }
        }
        if (!found) {
            System.out.println("There are no books issued right now.");
        }
        System.out.println("--------------------------------------------------------------------");
    }

    // Shows books that are overdue (past due date and still not returned).
    public void viewOverdueBooks() {
        LocalDate today = LocalDate.now();
        boolean found = false;
        System.out.println("\n------------------------- OVERDUE BOOKS -------------------------");
        for (int i = 0; i < issueList.size(); i++) {
            IssueRecord r = issueList.get(i);
            if (r.isCurrentlyIssued()) {
                LocalDate dueDate = LocalDate.parse(r.getDueDate(), DATE_FORMAT);
                if (today.isAfter(dueDate)) {
                    long lateDays = ChronoUnit.DAYS.between(dueDate, today);
                    double estimatedFine = lateDays * FINE_PER_DAY;
                    System.out.println(r.toShortRow()
                            + "   (Late by " + lateDays + " day(s), Est. Fine: Rs. "
                            + estimatedFine + ")");
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("No overdue books at the moment. Well done!");
        }
        System.out.println("-------------------------------------------------------------------");
    }

    // ==========================================================
    // REPORTS
    // ==========================================================

    // Prints a summary report of the whole library: total books,
    // total copies, how many are issued, total members, and so on.
    public void generateSummaryReport() {
        int totalBookTitles = bookList.size();
        int totalCopies = 0;
        int totalAvailable = 0;

        for (int i = 0; i < bookList.size(); i++) {
            totalCopies = totalCopies + bookList.get(i).getTotalCopies();
            totalAvailable = totalAvailable + bookList.get(i).getAvailableCopies();
        }

        int totalIssuedCopies = totalCopies - totalAvailable;
        int totalMembers = memberList.size();

        int currentlyIssuedCount = 0;
        int overdueCount = 0;
        double totalFinesCollected = 0.0;

        LocalDate today = LocalDate.now();

        for (int i = 0; i < issueList.size(); i++) {
            IssueRecord r = issueList.get(i);
            if (r.isCurrentlyIssued()) {
                currentlyIssuedCount++;
                LocalDate dueDate = LocalDate.parse(r.getDueDate(), DATE_FORMAT);
                if (today.isAfter(dueDate)) {
                    overdueCount++;
                }
            } else {
                // already returned, so we can add up whatever fine was charged
                totalFinesCollected = totalFinesCollected + r.getFineAmount();
            }
        }

        System.out.println("\n============== LIBRARY SUMMARY REPORT ==============");
        System.out.println("Total Book Titles      : " + totalBookTitles);
        System.out.println("Total Copies Owned     : " + totalCopies);
        System.out.println("Copies Available Now   : " + totalAvailable);
        System.out.println("Copies Currently Issued: " + totalIssuedCopies);
        System.out.println("Total Registered Members: " + totalMembers);
        System.out.println("Active Issue Records    : " + currentlyIssuedCount);
        System.out.println("Overdue Books           : " + overdueCount);
        System.out.println("Total Fines Collected   : Rs. " + totalFinesCollected);
        System.out.println("=====================================================");
    }

    // Shows a simple report of books grouped loosely by category count.
    // I did this using a very simple approach (nested loop) instead of
    // using a HashMap, since I am still learning collections properly.
    public void generateCategoryReport() {
        ArrayList<String> categoriesSeen = new ArrayList<String>();
        ArrayList<Integer> categoryCounts = new ArrayList<Integer>();

        for (int i = 0; i < bookList.size(); i++) {
            String category = bookList.get(i).getCategory();
            int indexFound = -1;

            for (int j = 0; j < categoriesSeen.size(); j++) {
                if (categoriesSeen.get(j).equalsIgnoreCase(category)) {
                    indexFound = j;
                    break;
                }
            }

            if (indexFound == -1) {
                // first time we see this category
                categoriesSeen.add(category);
                categoryCounts.add(1);
            } else {
                // increase the existing count
                int currentCount = categoryCounts.get(indexFound);
                categoryCounts.set(indexFound, currentCount + 1);
            }
        }

        System.out.println("\n============== BOOKS BY CATEGORY ==============");
        if (categoriesSeen.isEmpty()) {
            System.out.println("No books have been added yet.");
        } else {
            for (int i = 0; i < categoriesSeen.size(); i++) {
                System.out.println(categoriesSeen.get(i) + " : " + categoryCounts.get(i) + " title(s)");
            }
        }
        System.out.println("=================================================");
    }

    // ==========================================================
    // GETTERS (used by Main.java menu code if needed)
    // ==========================================================
    public ArrayList<Book> getBookList() {
        return bookList;
    }

    public ArrayList<Member> getMemberList() {
        return memberList;
    }

    public ArrayList<IssueRecord> getIssueList() {
        return issueList;
    }
}

// Main.java
// This is the starting point of the program. It just shows menus
// and takes input from the user using Scanner, then calls the
// correct method from LibraryManager depending on what the user
// chooses. I tried to keep every menu method small and focused on
// just one job (one menu), instead of writing everything inside
// the main() method.


public class Main {

    // I made these "static" so every method in this class can use
    // the same Scanner and the same LibraryManager without creating
    // new ones every time.
    static Scanner sc = new Scanner(System.in);
    static LibraryManager manager = new LibraryManager();

    public static void main(String[] args) {

        System.out.println("=====================================================");
        System.out.println("     WELCOME TO THE LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=====================================================");

        boolean exitProgram = false;

        // I used a while(true) style loop with a boolean flag so the
        // menu keeps showing until the user chooses to exit.
        while (!exitProgram) {
            printMainMenu();
            int choice = readIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    bookManagementMenu();
                    break;
                case 2:
                    memberManagementMenu();
                    break;
                case 3:
                    issueReturnMenu();
                    break;
                case 4:
                    reportsMenu();
                    break;
                case 5:
                    System.out.println("Thank you for using the Library Management System!");
                    exitProgram = true;
                    break;
                default:
                    System.out.println("Invalid choice, please choose a number between 1 and 5.");
            }
        }

        sc.close();
    }

    // ==========================================================
    // MAIN MENU
    // ==========================================================
    public static void printMainMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println("1. Book Management");
        System.out.println("2. Member Management");
        System.out.println("3. Issue / Return Book");
        System.out.println("4. Reports");
        System.out.println("5. Exit");
        System.out.println("-----------------------------------------------");
    }

    // ==========================================================
    // BOOK MANAGEMENT MENU
    // ==========================================================
    public static void bookManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n------------- BOOK MANAGEMENT -------------");
            System.out.println("1. Add New Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book by Title");
            System.out.println("4. Update Book Details");
            System.out.println("5. Delete Book");
            System.out.println("6. Back to Main Menu");
            System.out.println("---------------------------------------------");

            int choice = readIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addBookOption();
                    break;
                case 2:
                    manager.viewAllBooks();
                    break;
                case 3:
                    searchBookOption();
                    break;
                case 4:
                    updateBookOption();
                    break;
                case 5:
                    deleteBookOption();
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    public static void addBookOption() {
        System.out.println("\n--- Add New Book ---");
        String title = readStringInput("Enter Book Title: ");
        String author = readStringInput("Enter Author Name: ");
        String category = readStringInput("Enter Category (e.g. Fiction, Science): ");
        int totalCopies = readIntInput("Enter Total Copies: ");

        manager.addBook(title, author, category, totalCopies);
    }

    public static void searchBookOption() {
        String keyword = readStringInput("Enter title keyword to search: ");
        manager.searchBookByTitle(keyword);
    }

    public static void updateBookOption() {
        String bookId = readStringInput("Enter Book ID to update: ");
        Book existing = manager.findBookById(bookId);

        if (existing == null) {
            System.out.println("No book found with that ID.");
            return;
        }

        System.out.println("Current details:");
        System.out.println(existing);

        String newTitle = readStringInput("Enter New Title: ");
        String newAuthor = readStringInput("Enter New Author: ");
        String newCategory = readStringInput("Enter New Category: ");
        int newTotalCopies = readIntInput("Enter New Total Copies: ");

        manager.updateBook(bookId, newTitle, newAuthor, newCategory, newTotalCopies);
    }

    public static void deleteBookOption() {
        String bookId = readStringInput("Enter Book ID to delete: ");
        manager.deleteBook(bookId);
    }

    // ==========================================================
    // MEMBER MANAGEMENT MENU
    // ==========================================================
    public static void memberManagementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n------------- MEMBER MANAGEMENT -------------");
            System.out.println("1. Register New Member");
            System.out.println("2. View All Members");
            System.out.println("3. Search Member by Name");
            System.out.println("4. Update Member Details");
            System.out.println("5. Delete Member");
            System.out.println("6. Back to Main Menu");
            System.out.println("-----------------------------------------------");

            int choice = readIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addMemberOption();
                    break;
                case 2:
                    manager.viewAllMembers();
                    break;
                case 3:
                    searchMemberOption();
                    break;
                case 4:
                    updateMemberOption();
                    break;
                case 5:
                    deleteMemberOption();
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    public static void addMemberOption() {
        System.out.println("\n--- Register New Member ---");
        String name = readStringInput("Enter Member Name: ");
        String phone = readStringInput("Enter Phone Number: ");
        String email = readStringInput("Enter Email Address: ");

        manager.addMember(name, phone, email);
    }

    public static void searchMemberOption() {
        String keyword = readStringInput("Enter name keyword to search: ");
        manager.searchMemberByName(keyword);
    }

    public static void updateMemberOption() {
        String memberId = readStringInput("Enter Member ID to update: ");
        Member existing = manager.findMemberById(memberId);

        if (existing == null) {
            System.out.println("No member found with that ID.");
            return;
        }

        System.out.println("Current details:");
        System.out.println(existing);

        String newName = readStringInput("Enter New Name: ");
        String newPhone = readStringInput("Enter New Phone: ");
        String newEmail = readStringInput("Enter New Email: ");

        manager.updateMember(memberId, newName, newPhone, newEmail);
    }

    public static void deleteMemberOption() {
        String memberId = readStringInput("Enter Member ID to delete: ");
        manager.deleteMember(memberId);
    }

    // ==========================================================
    // ISSUE / RETURN MENU
    // ==========================================================
    public static void issueReturnMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n------------- ISSUE / RETURN BOOK -------------");
            System.out.println("1. Issue a Book");
            System.out.println("2. Return a Book");
            System.out.println("3. View All Issue Records");
            System.out.println("4. View Currently Issued Books");
            System.out.println("5. View Overdue Books");
            System.out.println("6. Back to Main Menu");
            System.out.println("-------------------------------------------------");

            int choice = readIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    issueBookOption();
                    break;
                case 2:
                    returnBookOption();
                    break;
                case 3:
                    manager.viewAllIssueRecords();
                    break;
                case 4:
                    manager.viewCurrentlyIssuedBooks();
                    break;
                case 5:
                    manager.viewOverdueBooks();
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    public static void issueBookOption() {
        System.out.println("\n--- Issue a Book ---");
        String bookId = readStringInput("Enter Book ID: ");
        String memberId = readStringInput("Enter Member ID: ");

        manager.issueBook(bookId, memberId);
    }

    public static void returnBookOption() {
        System.out.println("\n--- Return a Book ---");
        String issueId = readStringInput("Enter Issue ID: ");

        manager.returnBook(issueId);
    }

    // ==========================================================
    // REPORTS MENU
    // ==========================================================
    public static void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n------------------- REPORTS -------------------");
            System.out.println("1. Library Summary Report");
            System.out.println("2. Books by Category Report");
            System.out.println("3. Currently Issued Books");
            System.out.println("4. Overdue Books Report");
            System.out.println("5. Back to Main Menu");
            System.out.println("---------------------------------------------------");

            int choice = readIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    manager.generateSummaryReport();
                    break;
                case 2:
                    manager.generateCategoryReport();
                    break;
                case 3:
                    manager.viewCurrentlyIssuedBooks();
                    break;
                case 4:
                    manager.viewOverdueBooks();
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    // ==========================================================
    // INPUT HELPER METHODS
    // ==========================================================
    // I put these here so I don't have to repeat the same
    // try/catch validation code everywhere in the menus above.

    // Reads a plain line of text from the user (name, title, etc.)
    public static String readStringInput(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    // Reads an integer safely. If the user types letters or leaves
    // it blank, we catch the error and ask again instead of crashing.
    public static int readIntInput(String prompt) {
        int value = -1;
        boolean validInput = false;

        while (!validInput) {
            System.out.print(prompt);
            String text = sc.nextLine();
            try {
                value = Integer.parseInt(text.trim());
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid number, please try again.");
            }
        }
        return value;
    }
}