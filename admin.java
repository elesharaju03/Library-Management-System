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

class Admin
{
    static final String FILE_NAME = "student.txt";

    // Change this to the main/super-admin mobile number.
    static final String DEFAULT_ADMIN_MOBILE = "9999999999";
    static final String DEFAULT_ADMIN_NAME = "System Super Admin";
    static final String DEFAULT_COMPANY = "Main College";

    static final String[] ROLES =
    {
        "SUPER_ADMIN",
        "ADMIN",
        "LIBRARIAN",
        "MEMBER"
    };

    static final String[] CATEGORIES =
    {
        "Coding Books",
        "Science Books",
        "Exam Materials Books",
        "Previous Year Question Papers",
        "Novel Books",
        "Anime Comics"
    };

    // =========================================================
    // SYSTEM INITIALIZATION
    // =========================================================
    static void initializeSystem()
    {
        try
        {
            File file = new File(FILE_NAME);

            if (!file.exists())
            {
                file.createNewFile();
            }

            if (!hasAdminRecord())
            {
                writeAdminRecord();
            }

            if (!hasCompanyRecord())
            {
                writeCompanyRecord(DEFAULT_COMPANY);
            }

            if (!hasBookMasterRecords())
            {
                seedDefaultBooks();
            }
        }
        catch (IOException e)
        {
            System.out.println("System initialization error: " + e.getMessage());
        }
    }

    // =========================================================
    // ADMIN / MEMBER AUTHENTICATION
    // =========================================================
    static boolean hasAdminRecord()
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return false;

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                if (scanner.nextLine().trim().equals("ADMIN RECORD"))
                    return true;
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
        return false;
    }

    static void writeAdminRecord()
    {
        writeMemberRecord(
                "SUPER_ADMIN",
                DEFAULT_ADMIN_NAME,
                DEFAULT_ADMIN_MOBILE,
                "superadmin@library.local",
                DEFAULT_COMPANY,
                "ACTIVE",
                true, true, true, true, true
        );
    }

    static boolean isAdminMobile(String mobile)
    {
        return authenticate(mobile) != null;
    }

    static String[] authenticate(String mobile)
    {
        // Backward compatibility: an older version of this program stored
        // only the default ADMIN RECORD. Keep that administrator as the
        // Super Admin after upgrading to the new permission model.
        if (mobile.equals(DEFAULT_ADMIN_MOBILE))
        {
            return new String[] {
                    DEFAULT_ADMIN_NAME,
                    DEFAULT_ADMIN_MOBILE,
                    "superadmin@library.local",
                    DEFAULT_COMPANY,
                    "SUPER_ADMIN",
                    "true", "true", "true", "true", "true"
            };
        }

        File file = new File(FILE_NAME);
        if (!file.exists()) return null;

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                String header = scanner.nextLine().trim();

                if (!header.equals("ADMIN RECORD") && !header.equals("MEMBER RECORD"))
                    continue;

                String name = "";
                String registeredMobile = "";
                String email = "";
                String company = "";
                String role = "";
                String status = "";
                boolean addBook = false;
                boolean updateBook = false;
                boolean removeBook = false;
                boolean viewBooks = false;
                boolean manageMembers = false;

                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    if (line.equals("------------------------------")) break;

                    if (line.startsWith("Name         : ")) name = value(line, "Name         : ");
                    else if (line.startsWith("Mobile       : ")) registeredMobile = value(line, "Mobile       : ");
                    else if (line.startsWith("Email        : ")) email = value(line, "Email        : ");
                    else if (line.startsWith("Company      : ")) company = value(line, "Company      : ");
                    else if (line.startsWith("Role         : ")) role = value(line, "Role         : ");
                    else if (line.startsWith("Status       : ")) status = value(line, "Status       : ");
                    else if (line.startsWith("Add Book     : ")) addBook = yes(line, "Add Book     : ");
                    else if (line.startsWith("Update Book  : ")) updateBook = yes(line, "Update Book  : ");
                    else if (line.startsWith("Remove Book  : ")) removeBook = yes(line, "Remove Book  : ");
                    else if (line.startsWith("View Books   : ")) viewBooks = yes(line, "View Books   : ");
                    else if (line.startsWith("Manage Members: ")) manageMembers = yes(line, "Manage Members: ");
                }

                if (registeredMobile.equals(mobile)
                        && status.equalsIgnoreCase("ACTIVE"))
                {
                    return new String[] {
                            name, registeredMobile, email, company, role,
                            Boolean.toString(addBook),
                            Boolean.toString(updateBook),
                            Boolean.toString(removeBook),
                            Boolean.toString(viewBooks),
                            Boolean.toString(manageMembers)
                    };
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }

        return null;
    }

    static String value(String line, String prefix)
    {
        return line.substring(prefix.length()).trim();
    }

    static boolean yes(String line, String prefix)
    {
        return value(line, prefix).equalsIgnoreCase("YES");
    }

    static boolean hasPermission(String mobile, String permission)
    {
        String[] user = authenticate(mobile);
        if (user == null) return false;

        if (user[4].equalsIgnoreCase("SUPER_ADMIN")) return true;

        switch (permission)
        {
            case "ADD_BOOK": return Boolean.parseBoolean(user[5]);
            case "UPDATE_BOOK": return Boolean.parseBoolean(user[6]);
            case "REMOVE_BOOK": return Boolean.parseBoolean(user[7]);
            case "VIEW_BOOKS": return Boolean.parseBoolean(user[8]);
            case "MANAGE_MEMBERS": return Boolean.parseBoolean(user[9]);
            default: return false;
        }
    }

    static void showLoggedInUser(String mobile)
    {
        String[] user = authenticate(mobile);
        if (user == null) return;

        System.out.println("\n==============================");
        System.out.println("        ADMIN DETAILS");
        System.out.println("==============================");
        System.out.println("Name         : " + user[0]);
        System.out.println("Mobile       : " + user[1]);
        System.out.println("Email        : " + user[2]);
        System.out.println("Company      : " + user[3]);
        System.out.println("Role         : " + user[4]);
        System.out.println("Add Book     : " + user[5]);
        System.out.println("Update Book  : " + user[6]);
        System.out.println("Remove Book  : " + user[7]);
        System.out.println("View Books   : " + user[8]);
        System.out.println("Manage Members: " + user[9]);
        System.out.println("==============================");
    }

    // =========================================================
    // COMPANY MANAGEMENT
    // =========================================================
    static boolean hasCompanyRecord()
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return false;

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
                if (scanner.nextLine().trim().equals("COMPANY RECORD")) return true;
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
        return false;
    }

    static void writeCompanyRecord(String companyName)
    {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw))
        {
            bw.newLine();
            bw.write("COMPANY RECORD"); bw.newLine();
            bw.write("Company Name : " + companyName); bw.newLine();
            bw.write("Status       : ACTIVE"); bw.newLine();
            bw.write("------------------------------"); bw.newLine();
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
    }

    static boolean companyExists(String companyName)
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return false;

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                if (!scanner.nextLine().trim().equals("COMPANY RECORD")) continue;

                String name = "";
                String status = "";
                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    if (line.equals("------------------------------")) break;
                    if (line.startsWith("Company Name : ")) name = value(line, "Company Name : ");
                    else if (line.startsWith("Status       : ")) status = value(line, "Status       : ");
                }

                if (name.equalsIgnoreCase(companyName) && status.equalsIgnoreCase("ACTIVE"))
                    return true;
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
        return false;
    }

    static void addCompany(Scanner sc)
    {
        System.out.println("\n==============================");
        System.out.println("        ADD COMPANY / COLLEGE");
        System.out.println("==============================");
        System.out.print("Enter company/college name: ");
        String company = sc.nextLine().trim();

        if (company.isEmpty())
        {
            System.out.println("Company name cannot be empty.");
            return;
        }

        if (companyExists(company))
        {
            System.out.println("Company already exists.");
            return;
        }

        writeCompanyRecord(company);
        System.out.println("Company added successfully: " + company);
    }

    static void viewCompanies()
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        System.out.println("\n==============================");
        System.out.println("      COMPANIES / COLLEGES");
        System.out.println("==============================");

        int count = 0;
        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                if (!scanner.nextLine().trim().equals("COMPANY RECORD")) continue;

                String name = "";
                String status = "";
                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    if (line.equals("------------------------------")) break;
                    if (line.startsWith("Company Name : ")) name = value(line, "Company Name : ");
                    else if (line.startsWith("Status       : ")) status = value(line, "Status       : ");
                }

                if (!name.isEmpty())
                {
                    count++;
                    System.out.println(count + ". " + name + " | Status: " + status);
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }

        if (count == 0) System.out.println("No companies found.");
    }

    // =========================================================
    // MEMBER MANAGEMENT
    // =========================================================
    static void writeMemberRecord(
            String role,
            String name,
            String mobile,
            String email,
            String company,
            String status,
            boolean addBook,
            boolean updateBook,
            boolean removeBook,
            boolean viewBooks,
            boolean manageMembers)
    {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw))
        {
            bw.newLine();
            bw.write("MEMBER RECORD"); bw.newLine();
            bw.write("Name         : " + name); bw.newLine();
            bw.write("Mobile       : " + mobile); bw.newLine();
            bw.write("Email        : " + email); bw.newLine();
            bw.write("Company      : " + company); bw.newLine();
            bw.write("Role         : " + role); bw.newLine();
            bw.write("Status       : " + status); bw.newLine();
            bw.write("Add Book     : " + (addBook ? "YES" : "NO")); bw.newLine();
            bw.write("Update Book  : " + (updateBook ? "YES" : "NO")); bw.newLine();
            bw.write("Remove Book  : " + (removeBook ? "YES" : "NO")); bw.newLine();
            bw.write("View Books   : " + (viewBooks ? "YES" : "NO")); bw.newLine();
            bw.write("Manage Members: " + (manageMembers ? "YES" : "NO")); bw.newLine();
            bw.write("------------------------------"); bw.newLine();
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
    }

    static boolean memberExists(String mobile)
    {
        return authenticateAnyStatus(mobile) != null;
    }

    static String[] authenticateAnyStatus(String mobile)
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return null;

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                String header = scanner.nextLine().trim();
                if (!header.equals("ADMIN RECORD") && !header.equals("MEMBER RECORD")) continue;

                String name = "", registeredMobile = "", email = "", company = "", role = "", status = "";
                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    if (line.equals("------------------------------")) break;
                    if (line.startsWith("Name         : ")) name = value(line, "Name         : ");
                    else if (line.startsWith("Mobile       : ")) registeredMobile = value(line, "Mobile       : ");
                    else if (line.startsWith("Email        : ")) email = value(line, "Email        : ");
                    else if (line.startsWith("Company      : ")) company = value(line, "Company      : ");
                    else if (line.startsWith("Role         : ")) role = value(line, "Role         : ");
                    else if (line.startsWith("Status       : ")) status = value(line, "Status       : ");
                }
                if (registeredMobile.equals(mobile))
                    return new String[] {name, registeredMobile, email, company, role, status};
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
        return null;
    }

    static void addMember(Scanner sc)
    {
        System.out.println("\n==============================");
        System.out.println("       ADD ADMIN / MEMBER");
        System.out.println("==============================");

        System.out.print("Enter member name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }

        System.out.print("Enter mobile number: ");
        String mobile = Student.readValidMobile(sc);
        if (memberExists(mobile))
        {
            System.out.println("This mobile number already exists.");
            return;
        }

        System.out.print("Enter email: ");
        String email = sc.nextLine().trim();

        viewCompanies();
        System.out.print("Enter company/college name: ");
        String company = sc.nextLine().trim();
        if (!companyExists(company))
        {
            System.out.println("Company does not exist. Add the company first.");
            return;
        }

        System.out.println("\nSelect role:");
        System.out.println("1 - ADMIN");
        System.out.println("2 - LIBRARIAN");
        System.out.println("3 - MEMBER");
        System.out.print("Enter role: ");
        String roleChoice = sc.nextLine().trim();

        String role;
        boolean addBook, updateBook, removeBook, viewBooks, manageMembers;

        switch (roleChoice)
        {
            case "1":
                role = "ADMIN";
                addBook = true; updateBook = true; removeBook = true; viewBooks = true; manageMembers = false;
                break;
            case "2":
                role = "LIBRARIAN";
                addBook = true; updateBook = true; removeBook = false; viewBooks = true; manageMembers = false;
                break;
            case "3":
                role = "MEMBER";
                addBook = false; updateBook = false; removeBook = false; viewBooks = true; manageMembers = false;
                break;
            default:
                System.out.println("Invalid role.");
                return;
        }

        writeMemberRecord(role, name, mobile, email, company, "ACTIVE",
                addBook, updateBook, removeBook, viewBooks, manageMembers);

        System.out.println("\nMember added successfully!");
        System.out.println("Name    : " + name);
        System.out.println("Mobile  : " + mobile);
        System.out.println("Role    : " + role);
        System.out.println("Company : " + company);
    }

    static void viewMembers()
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        System.out.println("\n==============================");
        System.out.println("       ADMINS / MEMBERS");
        System.out.println("==============================");

        int count = 0;
        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                String header = scanner.nextLine().trim();
                if (!header.equals("ADMIN RECORD") && !header.equals("MEMBER RECORD")) continue;

                String name = "", mobile = "", company = "", role = "", status = "";
                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    if (line.equals("------------------------------")) break;
                    if (line.startsWith("Name         : ")) name = value(line, "Name         : ");
                    else if (line.startsWith("Mobile       : ")) mobile = value(line, "Mobile       : ");
                    else if (line.startsWith("Company      : ")) company = value(line, "Company      : ");
                    else if (line.startsWith("Role         : ")) role = value(line, "Role         : ");
                    else if (line.startsWith("Status       : ")) status = value(line, "Status       : ");
                }

                count++;
                System.out.println("\nMember #" + count);
                System.out.println("Name    : " + name);
                System.out.println("Mobile  : " + mobile);
                System.out.println("Role    : " + role);
                System.out.println("Company : " + company);
                System.out.println("Status  : " + status);
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
    }

    // Deactivation preserves old records and transaction history.
    static void removeMember(Scanner sc)
    {
        System.out.println("\n==============================");
        System.out.println("       REMOVE MEMBER");
        System.out.println("==============================");
        System.out.print("Enter member mobile number: ");
        String mobile = Student.readValidMobile(sc);

        String[] member = authenticateAnyStatus(mobile);
        if (member == null)
        {
            System.out.println("Member not found.");
            return;
        }

        if (mobile.equals(DEFAULT_ADMIN_MOBILE))
        {
            System.out.println("The default Super Admin cannot be removed.");
            return;
        }

        if (member[4].equalsIgnoreCase("SUPER_ADMIN"))
        {
            System.out.println("A Super Admin cannot be removed from this menu.");
            return;
        }

        System.out.println("Member: " + member[0] + " | Role: " + member[4]);
        System.out.print("Confirm removal? (YES/NO): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("YES"))
        {
            System.out.println("Removal cancelled.");
            return;
        }

        boolean updated = updateMemberStatus(mobile, "REMOVED");
        System.out.println(updated ? "Member removed successfully." : "Unable to remove member.");
    }

    static boolean updateMemberStatus(String mobile, String newStatus)
    {
        return updateMemberField(mobile, newStatus, null);
    }

    static boolean updateMemberField(String mobile, String newStatus, String newRole)
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return false;

        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();

                if (!line.equals("MEMBER RECORD"))
                {
                    lines.add(line);
                    continue;
                }

                List<String> block = new ArrayList<>();
                block.add(line);
                String currentMobile = "";

                while (scanner.hasNextLine())
                {
                    line = scanner.nextLine();
                    block.add(line);
                    if (line.startsWith("Mobile       : ")) currentMobile = value(line, "Mobile       : ");
                    if (line.equals("------------------------------")) break;
                }

                if (currentMobile.equals(mobile))
                {
                    for (int i = 0; i < block.size(); i++)
                    {
                        String b = block.get(i);
                        if (b.startsWith("Status       : "))
                            block.set(i, "Status       : " + newStatus);
                        if (newRole != null && b.startsWith("Role         : "))
                            block.set(i, "Role         : " + newRole);
                    }
                    found = true;
                }

                lines.addAll(block);
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
            return false;
        }

        if (!found) return false;

        try (FileWriter fw = new FileWriter(file, false);
             BufferedWriter bw = new BufferedWriter(fw))
        {
            for (String line : lines)
            {
                bw.write(line);
                bw.newLine();
            }
            return true;
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // ADMIN MENU
    // =========================================================
    static void adminMenu(Scanner sc, String adminMobile)
    {
        int choice;

        do
        {
            String[] user = authenticate(adminMobile);
            if (user == null)
            {
                System.out.println("\nAccess denied. Account is inactive or missing.");
                return;
            }

            System.out.println("\n================================================");
            System.out.println("                ADMIN PANEL");
            System.out.println("================================================");
            System.out.println("Logged in : " + user[0]);
            System.out.println("Role      : " + user[4]);
            System.out.println("Company   : " + user[3]);
            System.out.println("------------------------------------------------");
            System.out.println("BOOK MANAGEMENT");
            System.out.println("1 - Add Book");
            System.out.println("2 - Update Book");
            System.out.println("3 - Remove / Deactivate Book");
            System.out.println("4 - View All Books");
            System.out.println("5 - View Book Inventory");
            System.out.println("------------------------------------------------");
            System.out.println("MEMBER MANAGEMENT");
            System.out.println("6 - Add Admin / Member");
            System.out.println("7 - Remove Admin / Member");
            System.out.println("8 - View Admins / Members");
            System.out.println("------------------------------------------------");
            System.out.println("COMPANY MANAGEMENT");
            System.out.println("9 - Add Company / College");
            System.out.println("10 - View Companies / Colleges");
            System.out.println("------------------------------------------------");
            System.out.println("11 - My Admin Details");
            System.out.println("12 - Exit");
            System.out.print("Enter your choice: ");

            try
            {
                choice = Integer.parseInt(sc.nextLine().trim());
            }
            catch (NumberFormatException e)
            {
                System.out.println("Please enter a valid number.");
                choice = 0;
                continue;
            }

            switch (choice)
            {
                case 1:
                    if (hasPermission(adminMobile, "ADD_BOOK")) addBook(sc);
                    else accessDenied();
                    break;

                case 2:
                    if (hasPermission(adminMobile, "UPDATE_BOOK")) updateBook(sc);
                    else accessDenied();
                    break;

                case 3:
                    if (hasPermission(adminMobile, "REMOVE_BOOK")) removeBook(sc);
                    else accessDenied();
                    break;

                case 4:
                    if (hasPermission(adminMobile, "VIEW_BOOKS")) viewAllBooks();
                    else accessDenied();
                    break;

                case 5:
                    if (hasPermission(adminMobile, "VIEW_BOOKS")) viewAllBooks();
                    else accessDenied();
                    break;

                case 6:
                    if (hasPermission(adminMobile, "MANAGE_MEMBERS")) addMember(sc);
                    else accessDenied();
                    break;

                case 7:
                    if (hasPermission(adminMobile, "MANAGE_MEMBERS")) removeMember(sc);
                    else accessDenied();
                    break;

                case 8:
                    if (hasPermission(adminMobile, "MANAGE_MEMBERS")) viewMembers();
                    else accessDenied();
                    break;

                case 9:
                    if (hasPermission(adminMobile, "MANAGE_MEMBERS")) addCompany(sc);
                    else accessDenied();
                    break;

                case 10:
                    if (hasPermission(adminMobile, "MANAGE_MEMBERS")) viewCompanies();
                    else accessDenied();
                    break;

                case 11:
                    showLoggedInUser(adminMobile);
                    break;

                case 12:
                    System.out.println("\nAdmin logged out successfully.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
        while (choice != 12);
    }

    static void accessDenied()
    {
        System.out.println("\nACCESS DENIED!");
        System.out.println("Your account does not have permission for this operation.");
    }

    // =========================================================
    // DEFAULT BOOKS
    // =========================================================
    static void seedDefaultBooks()
    {
        String[][] defaultBooks =
        {
            {"Clean Code", "Coding Books", "5"},
            {"Code Complete", "Coding Books", "5"},
            {"Head First Java", "Coding Books", "5"},
            {"Python Crash Course", "Coding Books", "5"},
            {"Automate the Boring Stuff with Python", "Coding Books", "5"},
            {"Head First Learn to Code", "Coding Books", "5"},
            {"Effective Java", "Coding Books", "5"},
            {"Grokking Algorithms", "Coding Books", "5"},
            {"Eloquent JavaScript", "Coding Books", "5"},
            {"Think Like a Programmer", "Coding Books", "5"},
            {"Cosmos", "Science Books", "5"},
            {"The Selfish Gene", "Science Books", "5"},
            {"The Order of Time", "Science Books", "5"},
            {"The Demon-Haunted World", "Science Books", "5"},
            {"Cosmic Connection", "Science Books", "5"},
            {"Astrophysics", "Science Books", "5"},
            {"The Magic of Reality", "Science Books", "5"},
            {"Sapiens", "Science Books", "5"},
            {"The Gene", "Science Books", "5"},
            {"The Elegant Universe", "Science Books", "5"},
            {"NEET Guide", "Exam Materials Books", "5"},
            {"Indian Railways RRB Group D 2022", "Exam Materials Books", "5"},
            {"Quantitative Aptitude by R.S. Aggarwal", "Exam Materials Books", "5"},
            {"Fast Track Objective Arithmetic", "Exam Materials Books", "5"},
            {"Verbal & Non-Verbal Reasoning", "Exam Materials Books", "5"},
            {"Analytical Reasoning", "Exam Materials Books", "5"},
            {"Word Power Made Easy", "Exam Materials Books", "5"},
            {"Objective General English", "Exam Materials Books", "5"},
            {"Lucent's General Knowledge", "Exam Materials Books", "5"},
            {"Indian Polity", "Exam Materials Books", "5"},
            {"19 NEET Solved Papers", "Previous Year Question Papers", "5"},
            {"Disha RRB NTPC Previous Year Papers", "Previous Year Question Papers", "5"},
            {"Kiran SSC Mathematics Solved Papers", "Previous Year Question Papers", "5"},
            {"Kiran SSC Reasoning Solved Papers", "Previous Year Question Papers", "5"},
            {"Kiran SSC English Solved Papers", "Previous Year Question Papers", "5"},
            {"Kiran SSC General Awareness Solved Papers", "Previous Year Question Papers", "5"},
            {"Disha 24 Years IBPS PO Solved Papers", "Previous Year Question Papers", "5"},
            {"Disha 24 Years IBPS Clerk Solved Papers", "Previous Year Question Papers", "5"},
            {"MTG 24 Years JEE Main Solved Papers", "Previous Year Question Papers", "5"},
            {"Arihant 29 Years UPSC IAS Prelims Solved Papers", "Previous Year Question Papers", "5"},
            {"Don Quixote", "Novel Books", "5"},
            {"A Tale of Two Cities", "Novel Books", "5"},
            {"Pride and Prejudice", "Novel Books", "5"},
            {"To Kill a Mockingbird by Harper Lee", "Novel Books", "5"},
            {"1984 by George Orwell", "Novel Books", "5"},
            {"The Great Gatsby", "Novel Books", "5"},
            {"One Hundred Years of Solitude", "Novel Books", "5"},
            {"Anna Karenina", "Novel Books", "5"},
            {"The Lord of the Rings", "Novel Books", "5"},
            {"The Kingdom", "Novel Books", "5"},
            {"Fullmetal Alchemist: Brotherhood", "Anime Comics", "5"},
            {"Attack on Titan", "Anime Comics", "5"},
            {"One Piece", "Anime Comics", "5"},
            {"Dragon Ball Z", "Anime Comics", "5"},
            {"Naruto Shippuden", "Anime Comics", "5"},
            {"Hunter x Hunter", "Anime Comics", "5"},
            {"Demon Slayer: Kimetsu no Yaiba", "Anime Comics", "5"},
            {"Jujutsu Kaisen", "Anime Comics", "5"},
            {"Cowboy Bebop", "Anime Comics", "5"},
            {"Bleach", "Anime Comics", "5"}
        };

        for (String[] book : defaultBooks)
            writeBookMaster(book[0], book[1], Integer.parseInt(book[2]), "ACTIVE");
    }

    static boolean hasBookMasterRecords()
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return false;

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
                if (scanner.nextLine().trim().equals("BOOK MASTER")) return true;
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
        return false;
    }

    static void writeBookMaster(String bookName, String category, int totalCopies, String status)
    {
        try (FileWriter fw = new FileWriter(FILE_NAME, true);
             BufferedWriter bw = new BufferedWriter(fw))
        {
            bw.newLine();
            bw.write("BOOK MASTER"); bw.newLine();
            bw.write("Book Name    : " + bookName); bw.newLine();
            bw.write("Category     : " + category); bw.newLine();
            bw.write("Total Copies : " + totalCopies); bw.newLine();
            bw.write("Status       : " + status); bw.newLine();
            bw.write("------------------------------"); bw.newLine();
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
    }

    // =========================================================
    // ADD / UPDATE BOOK
    // =========================================================
    static void addBook(Scanner sc)
    {
        System.out.println("\n==============================");
        System.out.println("           ADD BOOK");
        System.out.println("==============================");

        System.out.print("Enter book name: ");
        String bookName = sc.nextLine().trim();
        if (bookName.isEmpty())
        {
            System.out.println("Book name cannot be empty.");
            return;
        }

        System.out.println("\nChoose category:");
        for (int i = 0; i < CATEGORIES.length; i++)
            System.out.println((i + 1) + " - " + CATEGORIES[i]);

        System.out.print("Enter category: ");
        int categoryChoice;
        try { categoryChoice = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid category."); return; }

        if (categoryChoice < 1 || categoryChoice > CATEGORIES.length)
        {
            System.out.println("Invalid category.");
            return;
        }

        System.out.print("Enter total number of copies: ");
        int totalCopies;
        try { totalCopies = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid quantity."); return; }

        if (totalCopies <= 0)
        {
            System.out.println("Number of copies must be greater than zero.");
            return;
        }

        String category = CATEGORIES[categoryChoice - 1];
        int existingCopies = getCatalogTotalCopies(bookName);

        if (existingCopies >= 0)
        {
            int available = Books.getAvailableCopies(bookName);
            int issued = existingCopies - available;
            if (totalCopies < issued) totalCopies = issued;

            updateBookMaster(bookName, category, totalCopies, "ACTIVE");
            System.out.println("\nBook details updated successfully.");
        }
        else
        {
            writeBookMaster(bookName, category, totalCopies, "ACTIVE");
            System.out.println("\nBook added successfully!");
        }

        System.out.println("Book Name    : " + bookName);
        System.out.println("Category     : " + category);
        System.out.println("Total Copies : " + totalCopies);
    }

    static void updateBook(Scanner sc)
    {
        System.out.println("\n==============================");
        System.out.println("          UPDATE BOOK");
        System.out.println("==============================");
        System.out.print("Enter existing book name: ");
        String bookName = sc.nextLine().trim();

        int existingCopies = getCatalogTotalCopies(bookName);
        if (existingCopies < 0)
        {
            System.out.println("Book not found.");
            return;
        }

        String existingCategory = getBookCategory(bookName);
        int issued = existingCopies - Books.getAvailableCopies(bookName);

        System.out.println("Current category: " + existingCategory);
        // Book names are immutable once transactions exist because the
        // existing issue/return history identifies books by name.
        // Admins can safely change category and total copies instead.
        String newName = bookName;

        System.out.print("Enter new category (press ENTER to keep current): ");
        String newCategory = sc.nextLine().trim();
        if (newCategory.isEmpty()) newCategory = existingCategory;

        System.out.print("Enter new total copies (minimum " + issued + "): ");
        String quantityText = sc.nextLine().trim();
        int newTotal = existingCopies;
        if (!quantityText.isEmpty())
        {
            try { newTotal = Integer.parseInt(quantityText); }
            catch (NumberFormatException e) { System.out.println("Invalid quantity."); return; }
        }

        if (newTotal < issued)
        {
            System.out.println("Cannot reduce copies below currently issued copies: " + issued);
            return;
        }

        if (!validCategory(newCategory))
        {
            System.out.println("Invalid category.");
            return;
        }

        updateBookMaster(bookName, newCategory, newTotal, "ACTIVE");
        System.out.println("Book updated successfully.");
    }

    static boolean validCategory(String category)
    {
        for (String c : CATEGORIES)
            if (c.equalsIgnoreCase(category)) return true;
        return false;
    }

    static boolean updateBookName(String oldName, String newName, String category, int totalCopies, String status)
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return false;

        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                if (!line.equals("BOOK MASTER"))
                {
                    lines.add(line);
                    continue;
                }

                List<String> block = new ArrayList<>();
                block.add(line);
                String currentBook = "";
                while (scanner.hasNextLine())
                {
                    line = scanner.nextLine();
                    block.add(line);
                    if (line.startsWith("Book Name    : ")) currentBook = value(line, "Book Name    : ");
                    if (line.equals("------------------------------")) break;
                }

                if (currentBook.equalsIgnoreCase(oldName))
                {
                    lines.add("BOOK MASTER");
                    lines.add("Book Name    : " + newName);
                    lines.add("Category     : " + category);
                    lines.add("Total Copies : " + totalCopies);
                    lines.add("Status       : " + status);
                    lines.add("------------------------------");
                    found = true;
                }
                else lines.addAll(block);
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
            return false;
        }

        if (!found) return false;

        try (FileWriter fw = new FileWriter(file, false);
             BufferedWriter bw = new BufferedWriter(fw))
        {
            for (String line : lines) { bw.write(line); bw.newLine(); }
            return true;
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
            return false;
        }
    }

    static void removeBook(Scanner sc)
    {
        System.out.println("\n==============================");
        System.out.println("         REMOVE BOOK");
        System.out.println("==============================");
        System.out.print("Enter book name to remove: ");
        String bookName = sc.nextLine().trim();

        int totalCopies = getCatalogTotalCopies(bookName);
        if (totalCopies < 0)
        {
            System.out.println("Book not found in the catalog.");
            return;
        }

        int available = Books.getAvailableCopies(bookName);
        int issued = totalCopies - available;
        if (issued > 0)
        {
            System.out.println("Warning: " + issued + " copy/copies are currently issued.");
            System.out.println("The book will be hidden from new selection, but existing loans can still be returned.");
        }

        System.out.print("Confirm removal? (YES/NO): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("YES"))
        {
            System.out.println("Removal cancelled.");
            return;
        }

        boolean updated = updateBookStatus(bookName, "REMOVED");
        System.out.println(updated ? "Book removed successfully!" : "Unable to remove the book.");
    }

    static void viewAllBooks()
    {
        List<String[]> books = getActiveBooksByCategory();
        System.out.println("\n==============================");
        System.out.println("         LIBRARY BOOKS");
        System.out.println("==============================");

        if (books.isEmpty())
        {
            System.out.println("No active books found.");
            return;
        }

        String currentCategory = "";
        int count = 0;
        for (String[] book : books)
        {
            if (!book[1].equalsIgnoreCase(currentCategory))
            {
                currentCategory = book[1];
                System.out.println("\n[" + currentCategory + "]");
            }

            int total = getCatalogTotalCopies(book[0]);
            int available = Books.getAvailableCopies(book[0]);
            count++;
            System.out.println(count + ". " + book[0]
                    + " | Total: " + total
                    + " | Available: " + available
                    + " | Issued: " + (total - available));
        }
        System.out.println("\nTotal active books: " + count);
    }

    static List<String[]> getActiveBooksByCategory()
    {
        List<String[]> books = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return books;

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                if (!scanner.nextLine().equals("BOOK MASTER")) continue;

                String bookName = "", category = "", status = "";
                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    if (line.equals("------------------------------")) break;
                    if (line.startsWith("Book Name    : ")) bookName = value(line, "Book Name    : ");
                    else if (line.startsWith("Category     : ")) category = value(line, "Category     : ");
                    else if (line.startsWith("Status       : ")) status = value(line, "Status       : ");
                }

                if (!bookName.isEmpty() && !category.isEmpty() && status.equalsIgnoreCase("ACTIVE"))
                    books.add(new String[] {bookName, category});
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
        return books;
    }

    static int getCatalogTotalCopies(String bookName)
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return -1;

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                if (!scanner.nextLine().equals("BOOK MASTER")) continue;

                String currentBook = "";
                int totalCopies = -1;
                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    if (line.equals("------------------------------")) break;
                    if (line.startsWith("Book Name    : ")) currentBook = value(line, "Book Name    : ");
                    else if (line.startsWith("Total Copies : "))
                    {
                        try { totalCopies = Integer.parseInt(value(line, "Total Copies : ")); }
                        catch (NumberFormatException ignored) { totalCopies = -1; }
                    }
                }
                if (currentBook.equalsIgnoreCase(bookName)) return totalCopies;
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
        return -1;
    }

    static String getBookCategory(String bookName)
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return "";

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                if (!scanner.nextLine().equals("BOOK MASTER")) continue;
                String currentBook = "", category = "";
                while (scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    if (line.equals("------------------------------")) break;
                    if (line.startsWith("Book Name    : ")) currentBook = value(line, "Book Name    : ");
                    else if (line.startsWith("Category     : ")) category = value(line, "Category     : ");
                }
                if (currentBook.equalsIgnoreCase(bookName)) return category;
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
        }
        return "";
    }

    static boolean updateBookMaster(String bookName, String category, int totalCopies, String status)
    {
        File file = new File(FILE_NAME);
        if (!file.exists()) return false;

        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (Scanner scanner = new Scanner(file))
        {
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                if (!line.equals("BOOK MASTER"))
                {
                    lines.add(line);
                    continue;
                }

                List<String> block = new ArrayList<>();
                block.add(line);
                String currentBook = "";
                while (scanner.hasNextLine())
                {
                    line = scanner.nextLine();
                    block.add(line);
                    if (line.startsWith("Book Name    : ")) currentBook = value(line, "Book Name    : ");
                    if (line.equals("------------------------------")) break;
                }

                if (currentBook.equalsIgnoreCase(bookName))
                {
                    lines.add("BOOK MASTER");
                    lines.add("Book Name    : " + bookName);
                    lines.add("Category     : " + category);
                    lines.add("Total Copies : " + totalCopies);
                    lines.add("Status       : " + status);
                    lines.add("------------------------------");
                    found = true;
                }
                else lines.addAll(block);
            }
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
            return false;
        }

        if (!found) return false;

        try (FileWriter fw = new FileWriter(file, false);
             BufferedWriter bw = new BufferedWriter(fw))
        {
            for (String line : lines) { bw.write(line); bw.newLine(); }
            return true;
        }
        catch (IOException e)
        {
            System.out.println("File Error: " + e.getMessage());
            return false;
        }
    }

    static boolean updateBookStatus(String bookName, String newStatus)
    {
        int totalCopies = getCatalogTotalCopies(bookName);
        if (totalCopies < 0) return false;
        String category = getBookCategory(bookName);
        if (category.isEmpty()) return false;
        return updateBookMaster(bookName, category, totalCopies, newStatus);
    }
}
