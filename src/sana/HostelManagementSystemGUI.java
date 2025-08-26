package sana;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HostelManagementSystemGUI extends JFrame {

    private JTextField nameField, ageField, roomField, idField, updateNameField, updateAgeField, updateRoomField;
    private JTextArea outputArea;

    public HostelManagementSystemGUI() {
        setTitle("Hostel Management System");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel for inputs
        JPanel inputPanel = new JPanel(new GridLayout(10, 2, 5, 5));

        inputPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Student Age:"));
        ageField = new JTextField();
        inputPanel.add(ageField);

        inputPanel.add(new JLabel("Room Number:"));
        roomField = new JTextField();
        inputPanel.add(roomField);

        inputPanel.add(new JLabel("Student ID (for delete/update):"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("New Name (for Update):"));
        updateNameField = new JTextField();
        inputPanel.add(updateNameField);

        inputPanel.add(new JLabel("New Age (for Update):"));
        updateAgeField = new JTextField();
        inputPanel.add(updateAgeField);

        inputPanel.add(new JLabel("New Room Number (for Update, optional):"));
        updateRoomField = new JTextField();
        inputPanel.add(updateRoomField);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        JButton addButton = new JButton("Add Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton updateButton = new JButton("Update Student");
        JButton viewButton = new JButton("View Students");
        JButton clearButton = new JButton("Clear Fields");
        JButton exitButton = new JButton("Exit");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Add to frame
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        updateButton.addActionListener(e -> updateStudent());
        viewButton.addActionListener(e -> viewStudents());
        clearButton.addActionListener(e -> clearFields());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void addStudent() {
        String name = nameField.getText();
        String ageText = ageField.getText();
        String roomText = roomField.getText();

        if (name.isEmpty() || ageText.isEmpty() || roomText.isEmpty()) {
            showMessage("Please fill all fields for adding student.");
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            int room = Integer.parseInt(roomText);
            HostelManagementSystem.addStudent(name, age, room);
            showMessage("Student added successfully!");
        } catch (NumberFormatException ex) {
            showMessage("Invalid number format!");
        }
    }

    private void deleteStudent() {
        String idText = idField.getText();

        if (idText.isEmpty()) {
            showMessage("Enter student ID to delete.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            HostelManagementSystem.deleteStudent(id);
            showMessage("Student deleted successfully!");
        } catch (NumberFormatException ex) {
            showMessage("Invalid student ID format!");
        }
    }

    private void updateStudent() {
        String idText = idField.getText();
        String newName = updateNameField.getText();
        String newAgeText = updateAgeField.getText();
        String newRoomText = updateRoomField.getText();

        if (idText.isEmpty() || newName.isEmpty() || newAgeText.isEmpty()) {
            showMessage("Please fill ID, new name, and new age for update.");
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            int newAge = Integer.parseInt(newAgeText);

            if (!newRoomText.isEmpty()) {
                int newRoom = Integer.parseInt(newRoomText);
                HostelManagementSystem.updateStudent(id, newName, newAge, newRoom);
            } else {
                HostelManagementSystem.updateStudent(id, newName, newAge);
            }

            showMessage("Student updated successfully!");

        } catch (NumberFormatException ex) {
            showMessage("Invalid number format!");
        }
    }

    private void viewStudents() {
        outputArea.setText(""); // Clear previous output
        outputArea.append("--- Students List ---\n");
        try {
            HostelManagementSystem.viewStudents();
        } catch (Exception e) {
            showMessage("Error fetching students: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        roomField.setText("");
        idField.setText("");
        updateNameField.setText("");
        updateAgeField.setText("");
        updateRoomField.setText("");
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    // Main method to launch GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HostelManagementSystemGUI().setVisible(true);
        });
    }
}
