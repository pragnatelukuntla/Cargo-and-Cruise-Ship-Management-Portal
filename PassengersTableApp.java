package pragna;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PassengersTableApp extends JFrame {
    private JTextField txtBookingId, txtName;
    private JTextArea txtPassengerData;
    private Connection connection;
    private Statement statement;

    public PassengersTableApp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Passengers Table");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);

        // Create panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));

        // Create input fields
        JLabel lblBookingId = new JLabel("Booking ID");
        txtBookingId = new JTextField();
        JLabel lblName = new JLabel("Name");
        txtName = new JTextField();

        // Add input fields to the panel
        inputPanel.add(lblBookingId);
        inputPanel.add(txtBookingId);
        inputPanel.add(lblName);
        inputPanel.add(txtName);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();

        // Create buttons
        JButton btnUpdate = new JButton("Update");
        JButton btnDisplay = new JButton("Display");
        JButton btnDelete = new JButton("Delete");

        // Add buttons to the panel
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDisplay);
        buttonPanel.add(btnDelete);

        // Create panel for passenger data
        JPanel dataPanel = new JPanel(new BorderLayout());
        JLabel lblPassengerData = new JLabel("Passenger Data:");
        txtPassengerData = new JTextArea();
        txtPassengerData.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtPassengerData);

        // Add components to the data panel
        dataPanel.add(lblPassengerData, BorderLayout.NORTH);
        dataPanel.add(scrollPane, BorderLayout.CENTER);

        // Add panels to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(dataPanel, BorderLayout.SOUTH);

        // Connect to the database
        connectToDatabase();

        // Update button action listener
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePassenger();
            }
        });

        // Display button action listener
        btnDisplay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPassengers();
            }
        });

        // Delete button action listener
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePassenger();
            }
        });
    }

    // Method to connect to the MySQL database
    private void connectToDatabase() {
        try {
        	String url = "jdbc:oracle:thin:@localhost:1521:xe";
            String username = "abhignya";
            String password = "abhignya";
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update the name of a passenger
    private void updatePassenger() {
        String bookingId = txtBookingId.getText();
        String name = txtName.getText();

        if (!bookingId.isEmpty() && !name.isEmpty()) {
            try {
                String query = "UPDATE passengers SET name = '" + name + "' WHERE                booking_id = '" + bookingId + "'";
                int rowsAffected = statement.executeUpdate(query);

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Passenger name updated successfully.", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update passenger name.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter booking ID and name.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to display all passengers
    private void displayPassengers() {
        try {
            String query = "SELECT * FROM passengers";
            ResultSet resultSet = statement.executeQuery(query);

            StringBuilder data = new StringBuilder();
            while (resultSet.next()) {
                String bookingId = resultSet.getString("booking_id");
                String name = resultSet.getString("name");
                data.append("Booking ID: ").append(bookingId).append(", Name: ").append(name).append("\n");
            }

            if (data.length() == 0) {
                data.append("No passengers found.");
            }

            txtPassengerData.setText(data.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a passenger record
    private void deletePassenger() {
        String bookingId = txtBookingId.getText();

        if (!bookingId.isEmpty()) {
            try {
                String query = "DELETE FROM passengers WHERE booking_id = '" + bookingId + "'";
                int rowsAffected = statement.executeUpdate(query);

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Passenger deleted successfully.", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete passenger.", "Deletion Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter booking ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

 /*   public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PassengersTableApp().setVisible(true);
            }
        });
    }*/
}
