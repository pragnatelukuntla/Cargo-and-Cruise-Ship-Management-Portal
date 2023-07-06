package pragna;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CruiseShipTableGUI extends JFrame {
    private JTextField txtCruiseId, txtCruiseName, txtNoOfPassengers, txtRating, txtFare;
    private JTable tblCruiseShips;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public CruiseShipTableGUI() {
        initializeUI();
        connectToDatabase();
        displayCruiseShips();
    }

    private void initializeUI() {
        txtCruiseId = new JTextField();
        txtCruiseName = new JTextField();
        txtNoOfPassengers = new JTextField();
        txtRating = new JTextField();
        txtFare = new JTextField();

        tblCruiseShips = new JTable();
        tblCruiseShips.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCruiseShips.getSelectionModel().addListSelectionListener(e -> selectCruiseShip());

        JScrollPane scrollPane = new JScrollPane(tblCruiseShips);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Cruise ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Cruise Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("No. of Passengers:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Rating:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Fare:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtCruiseId, gbc);
        gbc.gridy++;
        panel.add(txtCruiseName, gbc);
        gbc.gridy++;
        panel.add(txtNoOfPassengers, gbc);
        gbc.gridy++;
        panel.add(txtRating, gbc);
        gbc.gridy++;
        panel.add(txtFare, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;

        panel.add(btnAdd, gbc);
        gbc.gridy++;
        panel.add(btnModify, gbc);
        gbc.gridy++;
        panel.add(btnDelete, gbc);
        gbc.gridy++;
        panel.add(btnDisplay, gbc);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> insertCruiseShip());

        btnModify.addActionListener(e -> modifyCruiseShip());

        btnDelete.addActionListener(e -> deleteCruiseShip());

        btnDisplay.addActionListener(e -> displayCruiseShips());

        setTitle("Cruise Ships App");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connectToDatabase() {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "abhignya";
        String password = "abhignya";

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertCruiseShip() {
        String cruiseId = txtCruiseId.getText();
        String cruiseName = txtCruiseName.getText();
        String noOfPassengers = txtNoOfPassengers.getText();
        String rating = txtRating.getText();
        String fare = txtFare.getText();

        try {
            String query = "INSERT INTO cruise_ship (cruise_id, cruise_name, no_of_passengers, rating, fare) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cruiseId);
            statement.setString(2, cruiseName);
            statement.setString(3, noOfPassengers);
            statement.setString(4, rating);
            statement.setString(5, fare);
            statement.executeUpdate();

            clearFields();
            displayCruiseShips();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyCruiseShip() {
        int selectedRow = tblCruiseShips.getSelectedRow();
        if (selectedRow >= 0) {
            String cruiseId = txtCruiseId.getText();
            String cruiseName = txtCruiseName.getText();
            String noOfPassengers = txtNoOfPassengers.getText();
            String rating = txtRating.getText();
            String fare = txtFare.getText();

            try {
                String query = "UPDATE cruise_ship SET cruise_name=?, no_of_passengers=?, rating=?, fare=? WHERE cruise_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, cruiseName);
                statement.setString(2, noOfPassengers);
                statement.setString(3, rating);
                statement.setString(4, fare);
                statement.setString(5, cruiseId);
                statement.executeUpdate();

                clearFields();
                displayCruiseShips();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a cruise ship to modify.");
        }
    }

    private void deleteCruiseShip() {
        int selectedRow = tblCruiseShips.getSelectedRow();
        if (selectedRow >= 0) {
            String cruiseId = tblCruiseShips.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this cruise ship?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM cruise_ship WHERE cruise_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, cruiseId);
                    statement.executeUpdate();

                    clearFields();
                    displayCruiseShips();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a cruise ship to delete.");
        }
    }

    private void displayCruiseShips() {
        try {
            String query = "SELECT * FROM cruise_ship";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<CruiseShip> cruiseShips = new ArrayList<>();
            while (resultSet.next()) {
                String cruiseId = resultSet.getString("cruise_id");
                String cruiseName = resultSet.getString("cruise_name");
                String noOfPassengers = resultSet.getString("no_of_passengers");
                String rating = resultSet.getString("rating");
                String fare = resultSet.getString("fare");
                cruiseShips.add(new CruiseShip(cruiseId, cruiseName, noOfPassengers, rating, fare));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Cruise ID", "Cruise Name", "No. of Passengers", "Rating", "Fare"});

            for (CruiseShip cruiseShip : cruiseShips) {
                model.addRow(new String[]{cruiseShip.getCruiseId(), cruiseShip.getCruiseName(), cruiseShip.getNoOfPassengers(), cruiseShip.getRating(), cruiseShip.getFare()});
            }

            tblCruiseShips.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectCruiseShip() {
        int selectedRow = tblCruiseShips.getSelectedRow();
        if (selectedRow >= 0) {
            String cruiseId = tblCruiseShips.getValueAt(selectedRow, 0).toString();
            String cruiseName = tblCruiseShips.getValueAt(selectedRow, 1).toString();
            String noOfPassengers = tblCruiseShips.getValueAt(selectedRow, 2).toString();
            String rating = tblCruiseShips.getValueAt(selectedRow, 3).toString();
            String fare = tblCruiseShips.getValueAt(selectedRow, 4).toString();

            txtCruiseId.setText(cruiseId);
            txtCruiseName.setText(cruiseName);
            txtNoOfPassengers.setText(noOfPassengers);
            txtRating.setText(rating);
            txtFare.setText(fare);
        }
    }

    private void clearFields() {
        txtCruiseId.setText("");
        txtCruiseName.setText("");
        txtNoOfPassengers.setText("");
        txtRating.setText("");
        txtFare.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CruiseShipTableGUI::new);
    }

    private class CruiseShip {
        private String cruiseId;
        private String cruiseName;
        private String noOfPassengers;
        private String rating;
        private String fare;

        public CruiseShip(String cruiseId, String cruiseName, String noOfPassengers, String rating, String fare) {
            this.cruiseId = cruiseId;
            this.cruiseName = cruiseName;
            this.noOfPassengers = noOfPassengers;
            this.rating = rating;
            this.fare = fare;
        }

        public String getCruiseId() {
            return cruiseId;
        }

        public String getCruiseName() {
            return cruiseName;
        }

        public String getNoOfPassengers() {
            return noOfPassengers;
        }

        public String getRating() {
            return rating;
        }

        public String getFare() {
            return fare;
        }
    }
}

