
import com.mysql.cj.protocol.Resultset;
import java.sql.*;
import java.util.Scanner;


class Database {
    private static String URL="jdbc:mysql://localhost:3306/EXAMINATION_SYSTEM";
    private static String Username ="root";
    private static String Password="Root@454";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, Username, Password);
    }
}

class Student {
    private int rollNo;
    private String firstName;
    private String lastName;
    private String department;
    private String sClass;
    private String username;
    private String password;

    public void student_registeration(Connection connection) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your Roll number: ");
            rollNo = scanner.nextInt();
            scanner.nextLine();
            System.out.print("First Name: ");
            firstName = scanner.nextLine();
            System.out.print("Last Name: ");
            lastName = scanner.nextLine();
            System.out.print("Department Name:");
            department = scanner.nextLine();
            System.out.print("Enter your class (like FYBSC/SYBA) : ");
            sClass = scanner.nextLine();

            //username
            System.out.print("Create a username for login (at least 8 characters long, including lowercase letters and '_' symbol): ");
            // to check if the username is valid or not
            while (true) {
                username = scanner.nextLine();
                if (isValidUsername(username)) {
                    System.out.println("username is valid!");
                    break;
                } else {
                    System.out.println("Not valid USERNAME");
                    System.out.println("Enter username again:");
                }
            }

            //password
            System.out.print("Create your password ( at least 8 characters long, and atleast one uppercase letter,  lowercase letter,  digits , and a  '_' or '@' symbol): ");
            //to check if the password entered is valid or not
            while (true) {
                password = scanner.nextLine();
                if (isValidpassword(password)) {
                    System.out.println("Password is Valid!!");
                    break;
                } else {
                    System.out.println("Password is invalid!");
                    System.out.println("Enter Password again: ");
                }
            }

            String query = "INSERT INTO E_student(roll_no, s_first_name,s_last_name,s_department,E_student.`class`,s_user_name,s_password) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, rollNo);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            preparedStatement.setString(4, department);
            preparedStatement.setString(5, sClass);
            preparedStatement.setString(6, username);
            preparedStatement.setString(7, password);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(" ");
                System.out.println("You are registered as a Student Successfully!!!");
                System.out.println("========================================================================================================================================");
                System.out.println("                                                             **LOGIN PAGE**");
                System.out.println("========================================================================================================================================");
                System.out.println(" ");
                System.out.println(" ");
            } else {
                System.out.println("Failed to register.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //method for student login and check if the username is already registered or not
    public void student_login(Connection connection){
        try{Student student1 = new Student();
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your username : ");
            String username = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            String query = "SELECT * FROM E_student WHERE s_user_name = ? AND s_password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("                                                   You have Successfully Login As a Student!!!!!");
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println(" ");
                System.out.println(" ");

                student1.display_profile(connection,username);//method to print student's profile

                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");

                student1.display_examSchedule(connection,username);//method to print the exam schedule

                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");

                student1.display_result(connection,username);//method to print the Result

            }
            else{
                System.out.println("You are not registered! ");
                System.out.println("Enter your details below to register:");
                student1.student_registeration(connection);
            }
        }
        catch(SQLException e){
            System.out.println("Error: "+e.getMessage());
        }
    }
    public void display_profile(Connection connection, String username) throws SQLException {
        String query = "SELECT roll_no, s_first_name, s_last_name, s_department, class FROM E_student WHERE s_user_name=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int rollNo = resultSet.getInt("roll_no");
            String firstName = resultSet.getString("s_first_name");
            String lastName = resultSet.getString("s_last_name");
            String department = resultSet.getString("s_department");
            String sClass = resultSet.getString("class");

            System.out.println("==========================================================================================================================================");
            System.out.println("                                                            **PROFILE**");
            System.out.println("==========================================================================================================================================");
            System.out.println("Roll NO.: " + rollNo);
            System.out.println("NAME: " + firstName + " " + lastName);
            System.out.println("DEPARTMENT: " + department);
            System.out.println("CLASS: " + sClass);
        } else {
            System.out.println("No profile found for username:" + username);
        }
    }

    public void display_examSchedule(Connection connection, String username) throws SQLException {
        String query = "SELECT Exam_name, E_credits, E_marks, E_status, E_date FROM E_EXAMS WHERE Dept = (SELECT s_department FROM E_student WHERE s_user_name = ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.println("=================================================================================================================================================");
        System.out.println("                                                              **EXAM SCHEDULE**");
        System.out.println("=================================================================================================================================================");
        System.out.println(" ");

        System.out.printf("+-------------------+----------+-----------------+---------------+---------------+%n");
        System.out.printf("| Exam Name         | CREDITS  | Total Marks     | STATUS        | DATE          |%n");
        System.out.printf("+-------------------+----------+-----------------+---------------+---------------+%n");

        while (resultSet.next()) {
            String examName = resultSet.getString("Exam_name");
            int credits = resultSet.getInt("E_credits");
            int marks = resultSet.getInt("E_marks");
            String status = resultSet.getString("E_status");
            String date = resultSet.getString("E_date");

            System.out.printf("| %-17s | %-8d | %-15d | %-13s | %-13s |%n", examName, credits, marks, status, date);
        }

        System.out.printf("+-------------------+----------+-----------------+---------------+---------------+%n");
    }

    public void display_result(Connection connection,String username) throws SQLException{
        String sQuery="SELECT roll_no,first_name,last_name,Department,subject,credits,TotalMarks,Marks_obt FROM result where roll_no=(SELECT roll_no from E_student where s_user_name= ? )";
        PreparedStatement preparedStatement=connection.prepareStatement(sQuery);
        preparedStatement.setString(1,username);
        ResultSet resultset= preparedStatement.executeQuery();
        System.out.println("=================================================================================================================================================================================================================");
        System.out.println("                                                                                                   **RESULT**                                                                         ");
        System.out.println("==================================================================================================================================================================================================================");
        System.out.println(" ");
        System.out.println("+---------+------------------+--------------+------------------+---------------+---------+-----------+----------------------+");
        System.out.println("|ROLL NO. |FIRST NAME\t\t |LAST_NAME\t\t |DEPARTMENT\t\t |SUBJECT\t\t |CREDITS\t|TOTAL MARKS\t|MARKS OBTAINED\t|");
        System.out.println("+---------+------------------+--------------+------------------+---------------+---------+-----------+----------------------+");
        while(resultset.next()){
            int rollNo=resultset.getInt("roll_no");
            String firstName= resultset.getString("first_name");
            String lastName=resultset.getString("last_name");
            String Dept=resultset.getString("Department");
            String subject =resultset.getString("subject");
            int credits=resultset.getInt("credits");
            int TotalMarks=resultset.getInt("TotalMarks");
            int Marks_obt=resultset.getInt("Marks_obt");
            System.out.println("|"+rollNo+"\t\t|"+firstName+"\t\t|"+lastName+"|\t\t"+Dept+"\t\t|"+subject+" \t\t|"+credits+" \t\t|"+TotalMarks+" \t\t|"+Marks_obt+"\t\t|");
            System.out.println("+-------------------------+--------------------------------+---------------------------------+-----------------------------------------+----------------------------------------------+------------------+-------------------+---------------+");
        }
    }

    //method to check the validation of username
    private static boolean isValidUsername(String username) {
        // Check length
        if (username.length() < 8) {
            System.out.println("Username must be at least 8 characters long.");
            return false;
        }
        // Check for at least  lowercase letter, symbol
        boolean  hasLowercase = false, hasSymbol = false;
        for (char c : username.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (c == '_') {
                hasSymbol = true;
            }
        }

        if (!hasLowercase) {
            System.out.println("Username must contain at least one lowercase letter.");
        }
        if (!hasSymbol) {
            System.out.println("Username must contain at least one of the following symbols: '_' ");
        }

        // Return true only if all conditions are met
        return hasLowercase && hasSymbol;
    }

    private static boolean isValidpassword(String password) {
        // Check length
        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters long.");
            return false;
        }
        // Check for at least one uppercase letter, lowercase letter, symbol, and number
        boolean hasUppercase = false, hasLowercase = false, hasSymbol = false, hasNumber = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (c == '_' || c == '@') {
                hasSymbol = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }
        }

        if (!hasUppercase) {
            System.out.println("password must contain at least one uppercase letter.");
        }
        if (!hasLowercase) {
            System.out.println("password must contain at least one lowercase letter.");
        }
        if (!hasSymbol) {
            System.out.println("password must contain at least one of the following symbols: '_', '@'.");
        }
        if (!hasNumber) {
            System.out.println("password must contain at least one number.");
        }

        // Return true only if all conditions are met
        return hasUppercase && hasLowercase && hasSymbol && hasNumber;
    }

}
//teacher class
class Teacher  {
    private String ID;
    private String f_name;
    private String l_name;
    private String dept;
    private String subject;
    private String PassW;

    //method for teacher_registration
    public void teacher_registration(Connection connection) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your ID: ");
            ID = scanner.next();
            scanner.nextLine();
            System.out.print("First Name: ");
            f_name = scanner.nextLine();
            System.out.print("Last Name: ");
            l_name = scanner.nextLine();
            System.out.print("Department Name:");
            dept = scanner.nextLine();
            System.out.print("Enter the subject you teach: ");
            subject = scanner.nextLine();
            System.out.print("Create your password (should include at least 8 characters long, and atleast  one uppercase letter, one lowercase letter, one number, and one '_' or '@' symbol): ");

            while (true) {
                PassW = scanner.nextLine();
                if (isValidpassword(PassW)) {
                    System.out.println("Password is Valid!!");
                    break;
                } else {
                    System.out.println("Password is invalid!");
                    System.out.println("Enter Password again: ");
                }
            }

            String query = "INSERT INTO E_teacher(id,first_name,last_name,department,subject,password) VALUES(?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ID);
            preparedStatement.setString(2, f_name);
            preparedStatement.setString(3, l_name);
            preparedStatement.setString(4, dept);
            preparedStatement.setString(5, subject);
            preparedStatement.setString(6, PassW);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("You are registered as a Teacher Successfully!!!");
                System.out.println("========================================================================================================================================");
                System.out.println("                                                             **LOGIN PAGE**");
                System.out.println("========================================================================================================================================");
            } else {
                System.out.println("Failed to register.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //method to check whether the password is valid
    private static boolean isValidpassword(String password) {
        // Check length
        if (password.length() < 8) {
            System.out.println("password must be at least 8 characters long.");
            return false;
        }
        // Check for at least one uppercase letter, lowercase letter, symbol, and number
        boolean hasUppercase = false, hasLowercase = false, hasSymbol = false, hasNumber = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (c == '_' || c == '@') {
                hasSymbol = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }
        }

        if (!hasUppercase) {
            System.out.println("password must contain at least one uppercase letter.");
        }
        if (!hasLowercase) {
            System.out.println("password must contain at least one lowercase letter.");
        }
        if (!hasSymbol) {
            System.out.println("password must contain at least one of the following symbols: '_', '@'.");
        }
        if (!hasNumber) {
            System.out.println("password must contain at least one number.");
        }

        // Return true only if all conditions are met
        return hasUppercase && hasLowercase && hasSymbol && hasNumber;
    }


    public void teacher_login(Connection connection) {
        try {
            Teacher teacher1 = new Teacher();
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your id : ");
            String ID = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();
            String query = "SELECT * FROM E_teacher WHERE id= ? AND password=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ID);
            preparedStatement.setString(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("                                                             You have Successfully Login As a Teacher!!!!!");
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");
                teacher1.display_profile(connection, ID);//calling method to display teacher's profile
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(" ");
                teacher1.display_examSchedule(connection, ID);//calling method to display exam_schedule
                System.out.println(" ");
                // to insert the student's result
                while (true) {
                    System.out.print("Do you want to add the student's result(yes/no)? ");
                    String choice = scanner.nextLine();
                    if (choice.equalsIgnoreCase("yes")) {
                        teacher1.insertResult(connection);
                        System.out.println(" ");
                    } else if (choice.equalsIgnoreCase("no")) {
                        break;
                    }
                }
            } else {
                System.out.println("You are not registered.");
                System.out.println("Enter your details below to register:");
                teacher1.teacher_registration(connection);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public void display_profile(Connection connection, String ID) throws SQLException {
        String query = "SELECT id,first_name,last_name,department,subject FROM E_teacher WHERE id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, ID);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            String id = resultSet.getString("id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String department = resultSet.getString("department");
            String sub = resultSet.getString("subject");

            System.out.println("========================================================================================================================================================");
            System.out.println("                                                                               **PROFILE**                                      ");
            System.out.println("========================================================================================================================================================");
            System.out.println("ID:  "+id);
            System.out.println("NAME:  " + firstName + " " + lastName);
            System.out.println("DEPARTMENT:  " + department);
            System.out.println("SUBJECT:  " + sub);
        } else {
            System.out.println("No profile found for ID: " + ID);
        }
    }

    public void display_examSchedule(Connection connection, String ID) throws SQLException {
        Teacher teacher1 = new Teacher();
        String examName;
        int Credits;
        int Marks;
        String status;
        String date;
        String query = "SELECT Exam_name, E_credits, E_marks, E_status, E_date FROM E_EXAMS WHERE dept = (SELECT department FROM E_teacher WHERE id = ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, ID);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.println("=================================================================================================================================================");
        System.out.println("                                                              **EXAM SCHEDULE**");
        System.out.println("=================================================================================================================================================");
        System.out.println(" ");

        System.out.printf("+----------------------+----------+-----------------+---------------+-------------+%n");
        System.out.printf("| Exam Name            | CREDITS  | Total Marks     | STATUS        | DATE        |%n");
        System.out.printf("+----------------------+----------+-----------------+---------------+-------------+%n");

        while (resultSet.next()) {
            examName = resultSet.getString("Exam_name");
            Credits = resultSet.getInt("E_credits");
            Marks = resultSet.getInt("E_marks");
            status = resultSet.getString("E_status");
            date = resultSet.getString("E_date");

            System.out.printf("| %-20s | %-8d | %-15d | %-13s | %-11s |%n", examName, Credits, Marks, status, date);
            System.out.printf("+----------------------+----------+-----------------+---------------+-------------+%n");
        }
        //to add new exam schedule
        while (resultSet.next()) {
            examName = resultSet.getString("Exam_name");
            Credits = resultSet.getInt("E_credits");
            Marks= resultSet.getInt("E_marks");
            status=resultSet.getString("E_status");
            date =resultSet.getString("E_date");
            System.out.println("|"+examName+"\t\t|"+Credits+"\t|"+Marks+"        \t |"+status+"\t\t|"+date+"\t\t|");
            System.out.println("+---------------------+-------------+------------+--------------+------------+");
        }
    //to add new exam schedule
        Scanner scanner=new Scanner(System.in);
        String value = "yes";
        while(value == "yes") {
            System.out.println("Do you want to add new exam schedule(yes/no)?");
            value = scanner.next();
            if (value.equalsIgnoreCase("yes")) {
                teacher1.insert_examSchedule(connection);
            } else if (value.equalsIgnoreCase("no")){
                break;
            }
        }
    }
    public void insert_examSchedule(Connection connection) throws SQLException {

        Scanner scanner= new Scanner(System.in);
        System.out.print("Enter the exam ID:");
        int examID=scanner.nextInt();//to get exam id
        scanner.nextLine();

        System.out.print("Enter the exam name: ");
        String examName = scanner.nextLine();//to get exam name

        System.out.print("Enter the Department name:");
        String department = scanner.nextLine();//to get exam department

        System.out.print("Exam Credits: ");
        int Credits=scanner.nextInt();//to get the exam  credits
        scanner.nextLine();

        System.out.print("Total marks: ");
        int Marks=scanner.nextInt();
        scanner.nextLine();

        System.out.print("Exam Status(completed/Upcoming):");
        String status=scanner.nextLine();

        System.out.print("Exam Date(yyyy-mm-dd):");
        String date=scanner.nextLine();

        String query="INSERT INTO E_EXAMS(exam_id,Exam_name,Dept,E_credits,E_marks,E_status,E_date) VALUES(?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement=connection.prepareStatement(query);
        preparedStatement.setInt(1,examID);
        preparedStatement.setString(2,examName);
        preparedStatement.setString(3,department);
        preparedStatement.setInt(4,Credits);
        preparedStatement.setInt(5,Marks);
        preparedStatement.setString(6,status);
        preparedStatement.setString(7,date);
        int rowsUpdated = preparedStatement.executeUpdate();
        if(rowsUpdated!=0){
            System.out.println("EXAM UPDATED SUCCESSFULLY");
        }
        else{
            System.out.println("Failed to update the exam Schedule");
        }
    }

    public void insertResult(Connection connection) throws SQLException {

        Scanner scanner = new Scanner(System.in);

        System.out.print(" Enter the Student's Roll no.:");
        int rollNo=scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter the Student's first name: ");
        String firstName= scanner.nextLine();

        System.out.print("Enter the Student's last name: ");
        String LastName= scanner.nextLine();

        System.out.print("Enter the Department name:");
        String department= scanner.nextLine();

        System.out.print("Enter the exam subject :");
        String subject= scanner.nextLine();

        System.out.print("Enter the total credits of that subject:");
        int credits=scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter the total marks of that subject:");
        int TotalMarks=scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter the marks obtained by the student");
        int marks=scanner.nextInt();
        scanner.nextLine();

        String query = "INSERT INTO result(roll_no,first_name, last_name, Department, subject, credits,TotalMarks,Marks_obt) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, rollNo);
        preparedStatement.setString(2, firstName);
        preparedStatement.setString(3,LastName);
        preparedStatement.setString(4,department);
        preparedStatement.setString(5,subject);
        preparedStatement.setInt(6,credits);
        preparedStatement.setInt(7,TotalMarks);
        preparedStatement.setInt(8,marks);
        int rowsInserted = preparedStatement.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Result inserted successfully.");
        } else {
            System.out.println("Failed to insert result.");
        }
    }
}

public class EXAMINATION_SYSTEM{
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try (Connection connection = Database.getConnection()) {
            System.out.println("**********************************************************************************************************************************************************************************");
            System.out.println("                                                                              EXAMINATION SYSTEM");
            System.out.println("***********************************************************************************************************************************************************************************");
            System.out.println(" ");
            System.out.println(" ");
            System.out.print("Do you want to register/login as a student or teacher(student/teacher)?  :");
            Scanner scanner = new Scanner(System.in);
            String role = scanner.next();
            if (role.equalsIgnoreCase("student")) {
                Student student = new Student();
                System.out.print("Have you already registered(yes/no)?");
                String var=scanner.next();
                if(var.equals("yes")) {
                    student.student_login(connection);
                }
                else if(var.equals("no")){
                    student.student_registeration(connection);
                    student.student_login(connection);
                }
            } else if (role.equalsIgnoreCase("teacher")) {
                Teacher teacher = new Teacher();
                System.out.print("Have you already registered(yes/no)? ");
                String var1=scanner.next();
                if(var1.equalsIgnoreCase("yes")){
                    teacher.teacher_login(connection);
                }
                else if(var1.equalsIgnoreCase("no")){
                    teacher.teacher_registration(connection);
                    teacher.teacher_login(connection);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}


