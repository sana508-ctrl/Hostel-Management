package sana;

import java.sql.*;
import java.util.Scanner;

public class HostelManagementSystem {
    static Scanner sc = new Scanner(System.in);

    // Main method for console-based running
    public static void main(String[] args) {
        while (true) {
            System.out.println("\nHostel Management System");
            System.out.println("1. Add Student");
            System.out.println("2. Delete Student");
            System.out.println("3. Update Student Details");
            System.out.println("4. View Students");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addStudentConsole();
                    break;
                case 2:
                    deleteStudentConsole();
                    break;
                case 3:
                    updateStudentConsole();
                    break;
                case 4:
                    viewStudents();
                    break;
                case 5:
                    System.out.println("Exiting...Thank you!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again!");
            }
        }
    }

    // ---------- Console methods ----------
    private static void addStudentConsole() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter student name: ");
            String name = sc.next();
            System.out.print("Enter student age: ");
            int age = sc.nextInt();

            String query = "SELECT room_number FROM rooms WHERE is_available = TRUE";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.println("Available Rooms:");
            while (rs.next()) {
                System.out.println(rs.getInt("room_number"));
            }

            System.out.print("Enter room number to assign: ");
            int roomNumber = sc.nextInt();

            addStudent(name, age, roomNumber);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteStudentConsole() {
        System.out.print("Enter student ID to delete: ");
        int id = sc.nextInt();
        deleteStudent(id);
    }

    private static void updateStudentConsole() {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter student ID to update: ");
            int id = sc.nextInt();

            System.out.print("Enter new name: ");
            String name = sc.next();
            System.out.print("Enter new age: ");
            int age = sc.nextInt();

            System.out.print("Do you want to change room? (yes/no): ");
            String changeRoom = sc.next();

            if (changeRoom.equalsIgnoreCase("yes")) {
                String query = "SELECT room_number FROM rooms WHERE is_available = TRUE";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                System.out.println("Available Rooms:");
                while (rs.next()) {
                    System.out.println(rs.getInt("room_number"));
                }

                System.out.print("Enter new room number: ");
                int newRoomNumber = sc.nextInt();
                updateStudent(id, name, age, newRoomNumber);

            } else {
                updateStudent(id, name, age);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------- Methods for GUI usage ----------

    // Add student with given details
    public static void addStudent(String name, int age, int roomNumber) {
        try (Connection con = DBConnection.getConnection()) {
            String insertStudent = "INSERT INTO students (name, age, room_number) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertStudent);
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setInt(3, roomNumber);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                String updateRoom = "UPDATE rooms SET is_available = FALSE WHERE room_number = ?";
                PreparedStatement psRoom = con.prepareStatement(updateRoom);
                psRoom.setInt(1, roomNumber);
                psRoom.executeUpdate();
                System.out.println("Student added successfully.");
            } else {
                System.out.println("Error adding student.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Delete student by ID
    public static void deleteStudent(int studentId) {
        try (Connection con = DBConnection.getConnection()) {
            String getRoom = "SELECT room_number FROM students WHERE student_id = ?";
            PreparedStatement psGet = con.prepareStatement(getRoom);
            psGet.setInt(1, studentId);
            ResultSet rs = psGet.executeQuery();

            if (rs.next()) {
                int roomNumber = rs.getInt("room_number");

                String deleteStudent = "DELETE FROM students WHERE student_id = ?";
                PreparedStatement ps = con.prepareStatement(deleteStudent);
                ps.setInt(1, studentId);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    String updateRoom = "UPDATE rooms SET is_available = TRUE WHERE room_number = ?";
                    PreparedStatement psRoom = con.prepareStatement(updateRoom);
                    psRoom.setInt(1, roomNumber);
                    psRoom.executeUpdate();
                    System.out.println("Student deleted successfully.");
                } else {
                    System.out.println("Student not found.");
                }
            } else {
                System.out.println("Student not found!");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Update student with new name and age (without room change)
    public static void updateStudent(int studentId, String name, int age) {
        try (Connection con = DBConnection.getConnection()) {
            String updateStudent = "UPDATE students SET name=?, age=? WHERE student_id=?";
            PreparedStatement ps = con.prepareStatement(updateStudent);
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setInt(3, studentId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Student updated successfully.");
            } else {
                System.out.println("Update failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Update student with new name, age, and room
    public static void updateStudent(int studentId, String name, int age, int newRoomNumber) {
        try (Connection con = DBConnection.getConnection()) {
            // Get old room
            String getOldRoom = "SELECT room_number FROM students WHERE student_id=?";
            PreparedStatement psOld = con.prepareStatement(getOldRoom);
            psOld.setInt(1, studentId);
            ResultSet rs = psOld.executeQuery();

            int oldRoomNumber = -1;
            if (rs.next()) {
                oldRoomNumber = rs.getInt("room_number");
            }

            // Update student
            String updateStudent = "UPDATE students SET name=?, age=?, room_number=? WHERE student_id=?";
            PreparedStatement ps = con.prepareStatement(updateStudent);
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setInt(3, newRoomNumber);
            ps.setInt(4, studentId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                // Mark old room available
                String updateOldRoom = "UPDATE rooms SET is_available=TRUE WHERE room_number=?";
                PreparedStatement psOldRoom = con.prepareStatement(updateOldRoom);
                psOldRoom.setInt(1, oldRoomNumber);
                psOldRoom.executeUpdate();

                // Mark new room unavailable
                String updateNewRoom = "UPDATE rooms SET is_available=FALSE WHERE room_number=?";
                PreparedStatement psNewRoom = con.prepareStatement(updateNewRoom);
                psNewRoom.setInt(1, newRoomNumber);
                psNewRoom.executeUpdate();

                System.out.println("Student and room updated successfully.");
            } else {
                System.out.println("Update failed.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // View all students
    public static void viewStudents() {
        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT * FROM students";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.println("\n--- Students List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("student_id") +
                        ", Name: " + rs.getString("name") +
                        ", Age: " + rs.getInt("age") +
                        ", Room: " + rs.getInt("room_number"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
