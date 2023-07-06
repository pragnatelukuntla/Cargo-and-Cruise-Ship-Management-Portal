package pragna;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainPage extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton retrievedetailsButton;

    public MainPage() {
        // Set frame properties
        setTitle("Improvement of Car-D portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create label
        JLabel welcomeLabel = new JLabel("  Improvement of Car-D portal  ");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // Create panel for the button
        JPanel buttonPanel = new JPanel();
        retrievedetailsButton = new JButton("Retrieve Marks");
       // buttonPanel.add(retrievedetailsButton);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menus
        JMenu cargo_shipMenu = new JMenu("cargo_ship Details");
        JMenu cruise_shipMenu = new JMenu("cruise_ship Details");
        JMenu passengersMenu = new JMenu("passengers Details");
        JMenu reservesMenu = new JMenu("reserves Details");
        JMenu movementMenu = new JMenu("movement Details");
     

        // Create menu item for student menu
        JMenuItem viewcargo_shipDetails = new JMenuItem("View cargo_ship Details");
        viewcargo_shipDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CargoShipTableGUI();
            }
        });

        // Create menu item for course menu
        JMenuItem viewcruise_shipDetails = new JMenuItem("View cruise_ship Details");
        viewcruise_shipDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CruiseShipTableGUI();
            }
        });

        // Create menu item for enrollment menu
        JMenuItem viewpassengersDetails = new JMenuItem("View passengers Details");
        viewpassengersDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new PassengersTableApp();
            }
        });

        // Create menu item for semester menu
        JMenuItem viewreservesDetails = new JMenuItem("View  reserves Details");
        viewreservesDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ReservesTableApp();
            }
        });

        // Create menu item for grade menu
        JMenuItem viewmovementDetails = new JMenuItem("View movement Details");
        viewmovementDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MovementTableGUI();
            }
        });

        // Add menu items to respective menus
        cargo_shipMenu.add(viewcargo_shipDetails);
        cruise_shipMenu.add(viewcruise_shipDetails);
        passengersMenu.add(viewpassengersDetails);
        reservesMenu.add(viewreservesDetails);
        movementMenu.add(viewmovementDetails);

        // Add menus to the menu bar
        menuBar.add(cargo_shipMenu);
        menuBar.add(cruise_shipMenu);
        menuBar.add(passengersMenu);
        menuBar.add(reservesMenu);
        menuBar.add(movementMenu);

        // Set the menu bar
        setJMenuBar(menuBar);

        // Add the button panel to the frame
        add(buttonPanel, BorderLayout.CENTER);

        // Set button action for "Retrieve Marks"
     /*   retrievedetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RetrieveProgram retrieveProgram=new RetrieveProgram();
                retrieveProgram.setVisible(true);
            }
        });*/

        // Add window listener to handle maximizing the window
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    System.out.println("Window maximized");
                } else {
                    System.out.println("Window not maximized");
                }
            }
        });

        // Set frame size and visibility
        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainPage();
    }
}
