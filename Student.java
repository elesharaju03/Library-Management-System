import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

class Books

{

    static final String FILE_NAME =

            "student.txt";





    

    static int getTotalCopies(String bookName)
    {
        File file = new File(FILE_NAME);

        if (!file.exists())
        {
            return 0;
        }

        try
        {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();

                if (line.equals("BOOK MASTER"))
                {
                    String currentBook = "";
                    int totalCopies = 0;
                    String status = "";

                    while (scanner.hasNextLine())
                    {
                        line = scanner.nextLine();

                        if (line.equals("------------------------------"))
                        {
                            break;
                        }

                        if (line.startsWith("Book Name    : "))
                        {
                            currentBook =
                                    line.substring("Book Name    : ".length());
                        }
                        else if (line.startsWith("Total Copies : "))
                        {
                            try
                            {
                                totalCopies = Integer.parseInt(
                                        line.substring("Total Copies : ".length()).trim()
                                );
                            }
                            catch (NumberFormatException e)
                            {
                                totalCopies = 0;
                            }
                        }
                        else if (line.startsWith("Status       : "))
                        {
                            status =
                                    line.substring("Status       : ".length()).trim();
                        }
                    }

                    if (currentBook.equalsIgnoreCase(bookName)
                            && status.equalsIgnoreCase("ACTIVE"))
                    {
                        scanner.close();
                        return totalCopies;
                    }
                }
            }

            scanner.close();
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }

        return 0;
    }



    
    static int getIssuedCopies(String bookName)

    {

        File file = new File(FILE_NAME);

        if (!file.exists())

        {

            return 0;

        }

        int issuedCopies = 0;

        try

        {

            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine())

            {

                String line = fileScanner.nextLine();

                if (line.equals("BOOK ISSUE"))

                {

                    String currentBook = "";

                    String status = "";

                    while (fileScanner.hasNextLine())

                    {

                        line = fileScanner.nextLine();

                        if (line.equals(

                                "------------------------------"))

                        {

                            break;

                        }

                        if (line.startsWith("Book Name    : "))

                        {

                            currentBook = line.substring(

                                    "Book Name    : ".length());

                        }

                        else if (line.startsWith("Status       : "))

                        {

                            status = line.substring(

                                    "Status       : ".length());

                        }

                    }

                    if (currentBook.equalsIgnoreCase(bookName)

                            && status.equals("ISSUED"))

                    {

                        issuedCopies++;

                    }

                }

            }

            fileScanner.close();

        }

        catch (IOException e)

        {

            System.out.println(

                    "File Error: " + e.getMessage());

        }

        return issuedCopies;

    }



    // Remaining copies = Total copies - currently issued copies.

    // A returned book is automatically available again because

    // its BOOK ISSUE record is no longer considered active after

    // a matching BOOK RETURN record.

    static int getAvailableCopies(String bookName)

    {

        File file = new File(FILE_NAME);

        if (!file.exists())

        {

            return getTotalCopies(bookName);

        }

        int totalCopies = getTotalCopies(bookName);

        int activeIssues = 0;

        try

        {

            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine())

            {

                String line = fileScanner.nextLine();

                if (line.equals("BOOK ISSUE"))

                {

                    String currentMobile = "";

                    String currentBook = "";

                    String status = "";

                    while (fileScanner.hasNextLine())

                    {

                        line = fileScanner.nextLine();

                        if (line.equals(

                                "------------------------------"))

                        {

                            break;

                        }

                        if (line.startsWith("Mobile       : "))

                        {

                            currentMobile = line.substring(

                                    "Mobile       : ".length());

                        }

                        else if (line.startsWith("Book Name    : "))

                        {

                            currentBook = line.substring(

                                    "Book Name    : ".length());

                        }

                        else if (line.startsWith("Status       : "))

                        {

                            status = line.substring(

                                    "Status       : ".length());

                        }

                    }

                    if (currentBook.equalsIgnoreCase(bookName)

                            && status.equals("ISSUED"))

                    {

                        // Check whether this particular issue has

                        // already been returned.

                        boolean returned = false;

                        Scanner returnScanner =

                                new Scanner(file);

                        // We cannot safely continue with the same

                        // scanner position, so active status is

                        // calculated below using the complete file.

                        returnScanner.close();

                        activeIssues++;

                    }

                }

            }

            fileScanner.close();

            // The method above counts issue records. We now subtract

            // all matching return records.

            Scanner returnScanner = new Scanner(file);

            while (returnScanner.hasNextLine())

            {

                String line = returnScanner.nextLine();

                if (line.equals("BOOK RETURN"))

                {

                    String returnedBook = "";

                    while (returnScanner.hasNextLine())

                    {

                        line = returnScanner.nextLine();

                        if (line.equals(

                                "------------------------------"))

                        {

                            break;

                        }

                        if (line.startsWith("Book Name    : "))

                        {

                            returnedBook = line.substring(

                                    "Book Name    : ".length());

                        }

                    }

                    if (returnedBook.equalsIgnoreCase(bookName))

                    {

                        activeIssues--;

                    }

                }

            }

            returnScanner.close();

        }

        catch (IOException e)

        {

            System.out.println(

                    "File Error: " + e.getMessage());

        }

        if (activeIssues < 0)

        {

            activeIssues = 0;

        }

        int available = totalCopies - activeIssues;

        if (available < 0)

        {

            available = 0;

        }

        return available;

    }



    // =========================================================

    // DISPLAY A BOOK WITH CURRENT AVAILABLE QUANTITY

    // =========================================================

    static void printBookOption(

            int number,

            String bookName)

    {

        System.out.println(

                number + " - " + bookName

                        + " [Available: "

                        + getAvailableCopies(bookName)

                        + "]"

        );

    }



    static void issueBook(

            Scanner sc,

            String mobile)

    {

        int selectedBookCount = 0;

        boolean continueSelection = true;



        // =========================================================

        // MULTI-BOOK SELECTION

        // Student can select multiple books in one session.

        // After every successful issue, the student can choose

        // another book or exit the book-selection process.

        // =========================================================

        while (continueSelection)

        {

            String selectedBook =

                    selectBook(sc);



            if (selectedBook == null)

            {

                System.out.println(

                        "\nBook selection cancelled."

                );

                break;

            }



            // -----------------------------------------------------

            // CHECK BOOK AVAILABILITY

            // -----------------------------------------------------

            int availableCopies =

                    getAvailableCopies(selectedBook);



            int totalCopies =

                    getTotalCopies(selectedBook);



            System.out.println(

                    "\nAvailable copies of "

                            + selectedBook

                            + " : "

                            + availableCopies

                            + " / "

                            + totalCopies

            );



            if (availableCopies <= 0)

            {

                System.out.println(

                        "\nSorry! No copies of this book are "

                                + "currently available."

                );



                System.out.println(

                        "Please select another book."

                );



                System.out.println(

                        "\nDo you want to select another book?"

                );

                System.out.println(

                        "1 - YES, select another book"

                );

                System.out.println(

                        "2 - NO, exit book selection"

                );

                System.out.print(

                        "Enter your choice: "

                );



                int anotherChoice = sc.nextInt();

                sc.nextLine();



                if (anotherChoice != 1)

                {

                    continueSelection = false;

                }



                continue;

            }



            int copiesAfterIssue =

                    availableCopies - 1;



            // -----------------------------------------------------

            // ISSUE DATE AND TIME

            // -----------------------------------------------------

            LocalDateTime issueDateTime =

                    LocalDateTime.now();



            LocalDate issueDate =

                    issueDateTime.toLocalDate();



            LocalDate dueDate =

                    issueDate.plusDays(

                            Student.LOAN_DAYS

                    );



            String issueTime =

                    issueDateTime.format(

                            Student.DATE_TIME_FORMAT

                    );



            selectedBookCount++;



            // -----------------------------------------------------

            // DISPLAY ISSUE INFORMATION

            // -----------------------------------------------------

            System.out.println(

                    "\n=============================="

            );



            System.out.println(

                    "       BOOK ISSUE DETAILS"

            );



            System.out.println(

                    "=============================="

            );



            System.out.println(

                    "Book Selection #" + selectedBookCount

            );



            System.out.println(

                    "Student Mobile : "

                            + mobile

            );



            System.out.println(

                    "Book Name      : "

                            + selectedBook

            );



            System.out.println(

                    "Issue Date/Time: "

                            + issueTime

            );



            System.out.println(

                    "Due Date       : "

                            + dueDate.format(

                                    Student.DATE_FORMAT

                            )

            );



            System.out.println(

                    "\nIMPORTANT:"

            );



            System.out.println(

                    "Please return the book on or before "

                            + dueDate.format(

                                    Student.DATE_FORMAT

                            )

                            + "."

            );



            System.out.println(

                    "Late submission penalty: ₹"

                            + Student.PENALTY_PER_DAY

                            + " per day."

            );



            // -----------------------------------------------------

            // SAVE EACH SELECTED BOOK DETAILS TO FILE

            // -----------------------------------------------------

            try

            {

                FileWriter fw =

                        new FileWriter(

                                FILE_NAME,

                                true

                        );



                BufferedWriter bw =

                        new BufferedWriter(fw);



                bw.newLine();

                bw.write("BOOK ISSUE");

                bw.newLine();



                bw.write(

                        "Mobile       : " + mobile

                );

                bw.newLine();



                bw.write(

                        "Book Selection : " + selectedBookCount

                );

                bw.newLine();



                bw.write(

                        "Book Name    : "

                                + selectedBook

                );

                bw.newLine();



                bw.write(

                        "Issue Date   : "

                                + issueDate.format(

                                        Student.DATE_FORMAT

                                )

                );

                bw.newLine();



                bw.write(

                        "Issue Time   : "

                                + issueTime

                );

                bw.newLine();



                bw.write(

                        "Due Date     : "

                                + dueDate.format(

                                        Student.DATE_FORMAT

                                )

                );

                bw.newLine();



                bw.write("Status       : ISSUED");

                bw.newLine();



                bw.write(

                        "Total Copies : " + totalCopies

                );

                bw.newLine();



                bw.write(

                        "Copies Before: " + availableCopies

                );

                bw.newLine();



                bw.write(

                        "Copies After : " + copiesAfterIssue

                );

                bw.newLine();



                bw.write(

                        "Penalty/Day  : ₹"

                                + Student.PENALTY_PER_DAY

                );

                bw.newLine();



                bw.write(

                        "------------------------------"

                );

                bw.newLine();



                bw.close();

                fw.close();



                System.out.println(

                        "\nBook issued successfully!"

                );



                System.out.println(

                        "Copies Before Issue : "

                                + availableCopies

                );



                System.out.println(

                        "Copies After Issue  : "

                                + copiesAfterIssue

                );



                System.out.println(

                        "Please remember your due date."

                );

            }

            catch (IOException e)

            {

                System.out.println(

                        "File Error: "

                                + e.getMessage()

                );



                selectedBookCount--;

                continue;

            }



            // -----------------------------------------------------

            // ASK WHETHER THE STUDENT WANTS ANOTHER BOOK

            // -----------------------------------------------------

            System.out.println(

                    "\nDo you want to select another book?"

            );



            System.out.println(

                    "1 - YES, select another book"

            );



            System.out.println(

                    "2 - NO, exit book selection"

            );



            System.out.print(

                    "Enter your choice: "

            );



            int anotherChoice = sc.nextInt();

            sc.nextLine();



            if (anotherChoice != 1)

            {

                continueSelection = false;

            }

        }



        // ---------------------------------------------------------

        // FINAL MULTI-BOOK SUMMARY

        // ---------------------------------------------------------

        if (selectedBookCount > 0)

        {

            System.out.println(

                    "\n=============================="

            );



            System.out.println(

                    "     BOOK SELECTION SUMMARY"

            );



            System.out.println(

                    "=============================="

            );



            System.out.println(

                    "Total Books Issued : "

                            + selectedBookCount

            );



            System.out.println(

                    "All selected book details have been saved."

            );



            System.out.println(

                    "=============================="

            );

        }

    }



    // =========================================================

    // RETURN BOOK

    // MOBILE NUMBER RECEIVED FROM LOGIN

    // =========================================================

    static void returnBook(

            Scanner sc,

            String mobile)

    {

        System.out.print(

                "\nEnter Book Name to Return: "

        );





        String bookName =

                sc.nextLine();





        File file =

                new File(FILE_NAME);





        if (!file.exists())

        {

            System.out.println(

                    "\nNo library records found."

            );

            return;

        }





        try

        {

            Scanner fileScanner =

                    new Scanner(file);





            String currentMobile = "";

            String currentBook = "";

            String dueDateText = "";

            String status = "";





            boolean found = false;





            while (fileScanner.hasNextLine())

            {

                String line =

                        fileScanner.nextLine();





                // -------------------------------------------------

                // FIND BOOK ISSUE RECORD

                // -------------------------------------------------

                if (line.equals("BOOK ISSUE"))

                {

                    currentMobile = "";

                    currentBook = "";

                    dueDateText = "";

                    status = "";





                    while (fileScanner.hasNextLine())

                    {

                        line =

                                fileScanner.nextLine();





                        if (line.equals(

                                "------------------------------"))

                        {

                            break;

                        }





                        if (line.startsWith(

                                "Mobile       : "))

                        {

                            currentMobile =

                                    line.substring(

                                            "Mobile       : ".length()

                                    );

                        }





                        else if (line.startsWith(

                                "Book Name    : "))

                        {

                            currentBook =

                                    line.substring(

                                            "Book Name    : ".length()

                                    );

                        }





                        else if (line.startsWith(

                                "Due Date     : "))

                        {

                            dueDateText =

                                    line.substring(

                                            "Due Date     : ".length()

                                    );

                        }





                        else if (line.startsWith(

                                "Status       : "))

                        {

                            status =

                                    line.substring(

                                            "Status       : ".length()

                                    );

                        }

                    }





                    // -------------------------------------------------

                    // MATCH LOGGED-IN USER

                    // -------------------------------------------------

                    if (currentMobile.equals(mobile)

                            && currentBook.equalsIgnoreCase(

                                    bookName

                            )

                            && status.equals("ISSUED"))

                    {

                        found = true;





                        LocalDate dueDate =

                                LocalDate.parse(

                                        dueDateText,

                                        Student.DATE_FORMAT

                                );





                        LocalDateTime returnDateTime =

                                LocalDateTime.now();





                        LocalDate returnDate =

                                returnDateTime.toLocalDate();





                        String returnTime =

                                returnDateTime.format(

                                        Student.DATE_TIME_FORMAT

                                );





                        // -------------------------------------------------

                        // CALCULATE LATE DAYS

                        // -------------------------------------------------

                        long lateDays =

                                ChronoUnit.DAYS.between(

                                        dueDate,

                                        returnDate

                                );





                        if (lateDays < 0)

                        {

                            lateDays = 0;

                        }





                        double penalty =

                                lateDays

                                        * Student.PENALTY_PER_DAY;



                        // -------------------------------------------------

                        // UPDATE BOOK QUANTITY

                        // -------------------------------------------------

                        int copiesBeforeReturn =

                                getAvailableCopies(currentBook);

                        int totalCopies =

                                getTotalCopies(currentBook);

                        int copiesAfterReturn =

                                copiesBeforeReturn + 1;

                        if (copiesAfterReturn > totalCopies)

                        {

                            copiesAfterReturn = totalCopies;

                        }



                        // -------------------------------------------------

                        // DISPLAY RETURN DETAILS

                        // -------------------------------------------------

                        System.out.println(

                                "\n=============================="

                        );

                        System.out.println(

                                "       BOOK RETURN DETAILS"

                        );

                        System.out.println(

                                "=============================="

                        );





                        System.out.println(

                                "Student Mobile : "

                                        + mobile

                        );





                        System.out.println(

                                "Book Name      : "

                                        + currentBook

                        );





                        System.out.println(

                                "Due Date       : "

                                        + dueDateText

                        );





                        System.out.println(

                                "Return Date    : "

                                        + returnDate.format(

                                                Student.DATE_FORMAT

                                        )

                        );





                        System.out.println(

                                "Return Time    : "

                                        + returnTime

                        );

                        System.out.println(

                                "Copies Before Return : "

                                        + copiesBeforeReturn

                        );

                        System.out.println(

                                "Copies After Return  : "

                                        + copiesAfterReturn

                        );





                        if (lateDays > 0)

                        {

                            System.out.println(

                                    "\nBOOK RETURNED LATE!"

                            );





                            System.out.println(

                                    "Late Days      : "

                                            + lateDays

                            );





                            System.out.println(

                                    "Penalty/Day    : ₹"

                                            + Student.PENALTY_PER_DAY

                            );





                            System.out.println(

                                    "Total Penalty  : ₹"

                                            + penalty

                            );

                        }

                        else

                        {

                            System.out.println(

                                    "\nBook returned on time."

                            );





                            System.out.println(

                                    "Late Days      : 0"

                            );





                            System.out.println(

                                    "Total Penalty  : ₹0"

                            );

                        }





                        // -------------------------------------------------

                        // SAVE RETURN RECORD

                        // -------------------------------------------------

                        saveReturnDetails(

                                mobile,

                                currentBook,

                                dueDateText,

                                returnDate,

                                returnTime,

                                lateDays,

                                penalty

                        );





                        break;

                    }

                }

            }





            fileScanner.close();





            if (!found)

            {

                System.out.println(

                        "\nNo active book record found "

                                + "for this book."

                );

            }

        }

        catch (IOException e)

        {

            System.out.println(

                    "File Error: "

                            + e.getMessage()

            );

        }

    }





    // =========================================================

    // SAVE RETURN DETAILS

    // =========================================================

    static void saveReturnDetails(

            String mobile,

            String bookName,

            String dueDate,

            LocalDate returnDate,

            String returnTime,

            long lateDays,

            double penalty)

    {

        try

        {

            int totalCopies =

                    getTotalCopies(bookName);

            int copiesBeforeReturn =

                    getAvailableCopies(bookName);

            int copiesAfterReturn =

                    copiesBeforeReturn + 1;

            if (copiesAfterReturn > totalCopies)

            {

                copiesAfterReturn = totalCopies;

            }



            FileWriter fw =

                    new FileWriter(

                            FILE_NAME,

                            true

                    );





            BufferedWriter bw =

                    new BufferedWriter(fw);





            bw.newLine();

            bw.write(

                    "BOOK RETURN"

            );

            bw.newLine();





            bw.write(

                    "Mobile       : " + mobile

            );

            bw.newLine();





            bw.write(

                    "Book Name    : " + bookName

            );

            bw.newLine();





            bw.write(

                    "Due Date     : " + dueDate

            );

            bw.newLine();





            bw.write(

                    "Return Date  : "

                            + returnDate.format(

                                    Student.DATE_FORMAT

                            )

            );

            bw.newLine();





            bw.write(

                    "Return Time  : "

                            + returnTime

            );

            bw.newLine();





            bw.write(

                    "Late Days    : " + lateDays

            );

            bw.newLine();





            bw.write(

                    "Penalty/Day  : ₹"

                            + Student.PENALTY_PER_DAY

            );

            bw.newLine();





            bw.write(

                    "Total Penalty: ₹" + penalty

            );

            bw.newLine();





            bw.write(

                    "Status       : RETURNED"

            );

            bw.newLine();



            bw.write(

                    "Total Copies : " + totalCopies

            );

            bw.newLine();



            bw.write(

                    "Copies Before: " + copiesBeforeReturn

            );

            bw.newLine();



            bw.write(

                    "Copies After : " + copiesAfterReturn

            );

            bw.newLine();





            bw.write(

                    "------------------------------"

            );

            bw.newLine();





            bw.close();

            fw.close();





            System.out.println(

                    "\nReturn details saved successfully!"

            );

        }

        catch (IOException e)

        {

            System.out.println(

                    "File Error: "

                            + e.getMessage()

            );

        }

    }





    // =========================================================

    // BOOK SELECTION

    // NO ARRAY USED

    // =========================================================

    static String selectBook(Scanner sc)
    {
        List<String[]> books =
                Admin.getActiveBooksByCategory();

        if (books.isEmpty())
        {
            System.out.println("\nNo books are currently available.");
            return null;
        }

        System.out.println("\nChoose the category:");

        String[] categories =
        {
            "Coding Books",
            "Science Books",
            "Exam Materials Books",
            "Previous Year Question Papers",
            "Novel Books",
            "Anime Comics"
        };

        for (int i = 0; i < categories.length; i++)
        {
            boolean hasBooks = false;

            for (String[] book : books)
            {
                if (book[1].equalsIgnoreCase(categories[i]))
                {
                    hasBooks = true;
                    break;
                }
            }

            if (hasBooks)
            {
                System.out.println(
                        (char) ('a' + i) + " - " + categories[i]
                );
            }
        }

        System.out.print("Enter your category: ");
        String input = sc.nextLine().trim().toLowerCase();

        if (input.isEmpty())
        {
            System.out.println("Invalid category.");
            return null;
        }

        char category = input.charAt(0);

        if (category < 'a' || category > 'f')
        {
            System.out.println("Invalid category.");
            return null;
        }

        String selectedCategory =
                categories[category - 'a'];

        List<String[]> categoryBooks =
                new ArrayList<>();

        for (String[] book : books)
        {
            if (book[1].equalsIgnoreCase(selectedCategory))
            {
                categoryBooks.add(book);
            }
        }

        if (categoryBooks.isEmpty())
        {
            System.out.println(
                    "\nNo active books found in this category."
            );
            return null;
        }

        System.out.println("\n" + selectedCategory);
        System.out.println("==============================");

        for (int i = 0; i < categoryBooks.size(); i++)
        {
            String bookName = categoryBooks.get(i)[0];

            System.out.println(
                    (i + 1) + " - " + bookName
                            + " [Available: "
                            + getAvailableCopies(bookName)
                            + " / "
                            + getTotalCopies(bookName)
                            + "]"
            );
        }

        System.out.print("\nChoose a book: ");

        int choice;

        try
        {
            choice = Integer.parseInt(sc.nextLine().trim());
        }
        catch (NumberFormatException e)
        {
            System.out.println("Invalid book choice.");
            return null;
        }

        if (choice < 1 || choice > categoryBooks.size())
        {
            System.out.println("Invalid book choice.");
            return null;
        }

        return categoryBooks.get(choice - 1)[0];
    }

}
