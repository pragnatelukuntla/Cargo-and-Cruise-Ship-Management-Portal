package pragna;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
public class MovementTableGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultTableModel model;
    private JTable table;
    private JTextField txtStartingPort, txtDestinationPort, txtShipId, txtStartingDate;
    private Connection connection;
    private Statement statement;

    public MovementTableGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Movement Table");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        // Create table model
        model = new DefaultTableModel();
        model.addColumn("Starting Port");
        model.addColumn("Destination Port");
        model.addColumn("Ship ID");
        model.addColumn("Starting Date");

        // Create table
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Create panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));

        // Create input fields
        JLabel lblStartingPort = new JLabel("Starting Port");
        txtStartingPort = new JTextField();
        JLabel lblDestinationPort = new JLabel("Destination Port");
        txtDestinationPort = new JTextField();
        JLabel lblShipId = new JLabel("Ship ID");
        txtShipId = new JTextField();
        JLabel lblStartingDate = new JLabel("Starting Date");
        txtStartingDate = new JTextField();

        // Add input fields to the panel
        inputPanel.add(lblStartingPort);
        inputPanel.add(txtStartingPort);
        inputPanel.add(lblDestinationPort);
        inputPanel.add(txtDestinationPort);
        inputPanel.add(lblShipId);
        inputPanel.add(txtShipId);
        inputPanel.add(lblStartingDate);
        inputPanel.add(txtStartingDate);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();

        // Create buttons
        JButton btnInsert = new JButton("Insert");
        JButton btnModify = new JButton("Modify");
        JButton btnDelete = new JButton("Delete");
        JButton btnDisplay = new JButton("Display");

        // Add buttons to the panel
        buttonPanel.add(btnInsert);
        buttonPanel.add(btnModify);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnDisplay);

        // Add panels to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Connect to the database
        connectToDatabase();

        // Insert button action listener
        btnInsert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertData();
            }
        });

        // Modify button action listener
        btnModify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyData();
            }
        });

        // Delete button action listener
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteData();
            }
        });

        // Display button action listener
        btnDisplay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayData();
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

    // Method to insert data into the table
    private void insertData() {
        String startingPort = txtStartingPort.getText();
        String destinationPort = txtDestinationPort.getText();
        String shipId = txtShipId.getText();
        String startingDate = txtStartingDate.getText();

        if (!startingPort.isEmpty() && !destinationPort.isEmpty() && !shipId.isEmpty() && !startingDate.isEmpty()) {
            try {
                String query = "INSERT INTO movement (Starting_port, Destination_port, Ship_id, Starting_date) " +
                        "VALUES ('" + startingPort + "', '" + destinationPort + "', '" + shipId + "', '" + startingDate + "')";
                statement.executeUpdate(query);

                Vector<String> row = new Vector<>();
                row.add(startingPort);
                row.add(destinationPort);
                row.add(shipId);
                row.add(startingDate);

                model.addRow(row);
                clearFields();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to modify data in the table
    private void modifyData() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            String startingPort = txtStartingPort.getText();
            String destinationPort = txtDestinationPort.getText();
            String shipId = txtShipId.getText();
            String startingDate = txtStartingDate.getText();

            if (!startingPort.isEmpty() && !destinationPort.isEmpty() && !shipId.isEmpty() && !startingDate.isEmpty()) {
                try {
                    String query = "UPDATE movement SET Starting_port = '" + startingPort +
                            "', Destination_port = '" + destinationPort +
                            "', Ship_id = '" + shipId +
                            "', Starting_date = '" + startingDate +
                            "' WHERE Ship_id = '" + table.getValueAt(selectedRow, 2) + "'";
                    statement.executeUpdate(query);

                    table.setValueAt(startingPort, selectedRow, 0);
                    table.setValueAt(destinationPort, selectedRow, 1);
                    table.setValueAt(shipId, selectedRow, 2);
                    table.setValueAt(startingDate, selectedRow, 3);

                    clearFields();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to modify.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to delete data from the table
    private void deleteData() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            try {
                String shipId = (String) table.getValueAt(selectedRow, 2);
                String query = "DELETE FROM movement WHERE Ship_id = '" + shipId + "'";
                statement.executeUpdate(query);

                model.removeRow(selectedRow);
                clearFields();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to display the selected data in the input fields
    private void displayData() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow != -1) {
            String startingPort = (String) table.getValueAt(selectedRow, 0);
            String destinationPort = (String) table.getValueAt(selectedRow, 1);
            String shipId = (String) table.getValueAt(selectedRow, 2);
            String startingDate = (String) table.getValueAt(selectedRow, 3);

            txtStartingPort.setText(startingPort);
            txtDestinationPort.setText(destinationPort);
            txtShipId.setText(shipId);
            txtStartingDate.setText(startingDate);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to display.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to clear input fields
    private void clearFields() {
        txtStartingPort.setText("");
        txtDestinationPort.setText("");
        txtShipId.setText("");
        txtStartingDate.setText("");
    }

   /*  public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MovementTableApp().setVisible(true);
            }
        });
    }*/
}

