package pragna;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ReservesTableApp extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtDate, txtStartingPort, txtDestinationPort, txtName;
    private JComboBox<String> cmbShips;
    private JTextArea txtReservationDetails;
    private Connection connection;
    private Statement statement;
    private Set<String> bookedIds;

    public ReservesTableApp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Reserves Table");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        // Create panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));

        // Create input fields
        JLabel lblDate = new JLabel("Date");
        txtDate = new JTextField();
        JLabel lblStartingPort = new JLabel("Starting Port");
        txtStartingPort = new JTextField();
        JLabel lblDestinationPort = new JLabel("Destination Port");
        txtDestinationPort = new JTextField();
        JLabel lblName = new JLabel("Name");
        txtName = new JTextField();
        JLabel lblShip = new JLabel("Ship");

        // Create combo box for ship selection
        cmbShips = new JComboBox<>();
        cmbShips.setEnabled(false);

        // Add input fields to the panel
        inputPanel.add(lblDate);
        inputPanel.add(txtDate);
        inputPanel.add(lblStartingPort);
        inputPanel.add(txtStartingPort);
        inputPanel.add(lblDestinationPort);
        inputPanel.add(txtDestinationPort);
        inputPanel.add(lblName);
        inputPanel.add(txtName);
        inputPanel.add(lblShip);
        inputPanel.add(cmbShips);

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();

        // Create buttons
        JButton btnSearch = new JButton("Search");
        JButton btnReserve = new JButton("Reserve");

        // Add buttons to the panel
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnReserve);

        // Create panel for reservation details
        JPanel reservationPanel = new JPanel(new BorderLayout());
        JLabel lblReservationDetails = new JLabel("Reservation Details:");
        txtReservationDetails = new JTextArea();
        txtReservationDetails.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtReservationDetails);

        // Add components to the reservation panel
        reservationPanel.add(lblReservationDetails, BorderLayout.NORTH);
        reservationPanel.add(scrollPane, BorderLayout.CENTER);

        // Add panels to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(reservationPanel, BorderLayout.SOUTH);

        // Connect to the database
        connectToDatabase();

        // Search button action listener
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchShips();
            }
        });

        // Reserve button action listener
        btnReserve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reserveShip();
            }
        });

        // Initialize the set of booked IDs
        bookedIds = new HashSet<>();
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

    // Method to search for available ships on a given date and route
    private void searchShips() {
        String date = txtDate.getText();
        String startingPort = txtStartingPort.getText();
        String destinationPort = txtDestinationPort.getText();

        if (!date.isEmpty() && !startingPort.isEmpty() && !destinationPort.isEmpty()) {
            try {
                String query = "SELECT ship_id FROM movement WHERE starting_port = '" + startingPort +
                        "' AND destination_port = '" + destinationPort + "' AND starting_date = '" + date + "'";
                ResultSet resultSet = statement.executeQuery(query);

                cmbShips.removeAllItems();
                cmbShips.setEnabled(true);

                while (resultSet.next()) {
                    String shipId = resultSet.getString("ship_id");
                    cmbShips.addItem(shipId);
                }

                if (cmbShips.getItemCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No ships available.", "No Ships", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to reserve a ship and update the passengers table
    private void reserveShip() {
        String shipId = (String) cmbShips.getSelectedItem();
        String bookingId = generateBookingId();
        String name = txtName.getText();

        if (!name.isEmpty()) {
            try {
                // Check if the ship is available on the specified date, starting port, and destination port
                String query = "SELECT ship_id FROM movement WHERE starting_port = '" + txtStartingPort.getText() +
                        "' AND destination_port = '" + txtDestinationPort.getText() + "' AND starting_date = '" +
                        txtDate.getText() + "' AND ship_id = '" + shipId + "'";
                ResultSet resultSet = statement.executeQuery(query);

                if (!resultSet.next()) {
                    JOptionPane.showMessageDialog(this, "Cannot reserve as no ships are available.", "No Ships Available", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if booking ID already exists
                query = "SELECT booking_id FROM passengers WHERE booking_id = '" + bookingId + "'";
                resultSet = statement.executeQuery(query);

                if (resultSet.next()) {
                    // Booking ID already exists, generate a new one
                    bookingId = generateBookingId();
                }

                // Update reserves table
                query = "INSERT INTO reserves (ship_id, booking_id, date_of_reservation, starting_port, destination_port) " +
                        "VALUES ('" + shipId + "', '" + bookingId + "', '" + txtDate.getText() + "', '" +
                        txtStartingPort.getText() + "', '" + txtDestinationPort.getText() + "')";
                statement.executeUpdate(query);

                // Update passengers table
                query = "INSERT INTO passengers (booking_id, name) VALUES ('" + bookingId + "', '" + name + "')";
                statement.executeUpdate(query);

                // Display reservation details to the user
               txtReservationDetails.setText("Reservation Details:\n\n" +
                        "Booking ID: " + bookingId + "\n" +
                        "Date of Reservation: " + txtDate.getText() + "\n" +
                        "Ship ID: " + shipId + "\n" +
                        "Starting Port: " + txtStartingPort.getText() + "\n" +
                        "Destination Port: " + txtDestinationPort.getText());

                JOptionPane.showMessageDialog(this, "Reservation successful.\n\nBooking ID: " + bookingId, "Reservation Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a name.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to generate a booking ID of length 5
    private String generateBookingId() {
        Random random = new Random();
        String bookingId;

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                int digit = random.nextInt(10);
                sb.append(digit);
            }
            bookingId = sb.toString();
        } while (bookedIds.contains(bookingId));

        bookedIds.add(bookingId);
        return bookingId;
    }

  /*  public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ReservesTableApp().setVisible(true);
            }
        });
    */
}
