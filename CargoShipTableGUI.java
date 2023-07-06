package pragna;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CargoShipTableGUI extends JFrame {
    private JTextField txtCargoId, txtCargoName, txtCapacity, txtTypeOfLoad;
    private JTable tblCargoShips;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public CargoShipTableGUI() {
        initializeUI();
        connectToDatabase();
        displayCargoShips();
    }

    private void initializeUI() {
        txtCargoId = new JTextField();
        txtCargoName = new JTextField();
        txtCapacity = new JTextField();
        txtTypeOfLoad = new JTextField();

        tblCargoShips = new JTable();
        tblCargoShips.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCargoShips.getSelectionModel().addListSelectionListener(e -> selectCargoShip());

        JScrollPane scrollPane = new JScrollPane(tblCargoShips);

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

        panel.add(new JLabel("Cargo ID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Cargo Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Capacity:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Type of Load:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtCargoId, gbc);
        gbc.gridy++;
        panel.add(txtCargoName, gbc);
        gbc.gridy++;
        panel.add(txtCapacity, gbc);
        gbc.gridy++;
        panel.add(txtTypeOfLoad, gbc);

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

        btnAdd.addActionListener(e -> insertCargoShip());

        btnModify.addActionListener(e -> modifyCargoShip());

        btnDelete.addActionListener(e -> deleteCargoShip());

        btnDisplay.addActionListener(e -> displayCargoShips());

        setTitle("Cargo Ships App");
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

    private void insertCargoShip() {
        String cargoId = txtCargoId.getText();
        String cargoName = txtCargoName.getText();
        String capacity = txtCapacity.getText();
        String typeOfLoad = txtTypeOfLoad.getText();

        try {
            String query = "INSERT INTO cargo_ship (cargo_id, cargo_name, capacity, type_of_load) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cargoId);
            statement.setString(2, cargoName);
            statement.setString(3, capacity);
            statement.setString(4, typeOfLoad);
            statement.executeUpdate();

            clearFields();
            displayCargoShips();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyCargoShip() {
        int selectedRow = tblCargoShips.getSelectedRow();
        if (selectedRow >= 0) {
            String cargoId = txtCargoId.getText();
            String cargoName = txtCargoName.getText();
            String capacity = txtCapacity.getText();
            String typeOfLoad = txtTypeOfLoad.getText();

            try {
                String query = "UPDATE cargo_ship SET cargo_name=?, capacity=?, type_of_load=? WHERE cargo_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, cargoName);
                statement.setString(2, capacity);
                statement.setString(3, typeOfLoad);
                statement.setString(4, cargoId);
                statement.executeUpdate();

                clearFields();
                displayCargoShips();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a cargo ship to modify.");
        }
    }

    private void deleteCargoShip() {
        int selectedRow = tblCargoShips.getSelectedRow();
        if (selectedRow >= 0) {
            String cargoId = tblCargoShips.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this cargo ship?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM cargo_ship WHERE cargo_id=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, cargoId);
                    statement.executeUpdate();

                    clearFields();
                    displayCargoShips();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a cargo ship to delete.");
        }
    }

    private void displayCargoShips() {
        try {
            String query = "SELECT * FROM cargo_ship";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<CargoShip> cargoShips = new ArrayList<>();
            while (resultSet.next()) {
                String cargoId = resultSet.getString("cargo_id");
                String cargoName = resultSet.getString("cargo_name");
                String capacity = resultSet.getString("capacity");
                String typeOfLoad = resultSet.getString("type_of_load");
                cargoShips.add(new CargoShip(cargoId, cargoName, capacity, typeOfLoad));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Cargo ID", "Cargo Name", "Capacity", "Type of Load"});

            for (CargoShip cargoShip : cargoShips) {
                model.addRow(new String[]{cargoShip.getCargoId(), cargoShip.getCargoName(), cargoShip.getCapacity(), cargoShip.getTypeOfLoad()});
            }

            tblCargoShips.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectCargoShip() {
        int selectedRow = tblCargoShips.getSelectedRow();
        if (selectedRow >= 0) {
            String cargoId = tblCargoShips.getValueAt(selectedRow, 0).toString();
            String cargoName = tblCargoShips.getValueAt(selectedRow, 1).toString();
            String capacity = tblCargoShips.getValueAt(selectedRow, 2).toString();
            String typeOfLoad = tblCargoShips.getValueAt(selectedRow, 3).toString();

            txtCargoId.setText(cargoId);
            txtCargoName.setText(cargoName);
            txtCapacity.setText(capacity);
            txtTypeOfLoad.setText(typeOfLoad);
        }
    }

    private void clearFields() {
        txtCargoId.setText("");
        txtCargoName.setText("");
        txtCapacity.setText("");
        txtTypeOfLoad.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CargoShipTableGUI::new);
    }

    private class CargoShip {
        private String cargoId;
        private String cargoName;
        private String capacity;
        private String typeOfLoad;

        public CargoShip(String cargoId, String cargoName, String capacity, String typeOfLoad) {
            this.cargoId = cargoId;
            this.cargoName = cargoName;
            this.capacity = capacity;
            this.typeOfLoad = typeOfLoad;
        }

        public String getCargoId() {
            return cargoId;
        }

        public String getCargoName() {
            return cargoName;
        }

        public String getCapacity() {
            return capacity;
        }

        public String getTypeOfLoad() {
            return typeOfLoad;
        }
    }
}

