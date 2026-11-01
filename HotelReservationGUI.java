package hotelroomreservationtracker1;

import javax.swing.*;
import java.awt.*;

public class HotelReservationGUI extends JFrame {

    // CONNECTING TO BACKEND TRACKER
    private final HotelRoomReservationTracker1 tracker = new HotelRoomReservationTracker1();

    private JTextField txtNumber;
    private JComboBox<String> txtType;
    private JTextField txtPrice;
    private JTextField txtGuest;

    public HotelReservationGUI() {
        setTitle("Hotel Room Reservation Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setPreferredSize(new Dimension(360, 450));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // INPUT FORM
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Room Information"));

        txtNumber = new JTextField();
        txtType = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Family"});
        txtPrice = new JTextField("1500");
        txtGuest = new JTextField();

        txtType.addActionListener(e -> updatePrice());

        form.add(new JLabel("Room Number:"));
        form.add(txtNumber);
        form.add(new JLabel("Room Type:"));
        form.add(txtType);
        form.add(new JLabel("Room Price:"));
        form.add(txtPrice);
        form.add(new JLabel("Guest Name:"));
        form.add(txtGuest);

        // BUTTONS
        JPanel buttons = new JPanel(new GridLayout(5, 1, 8, 8));
        buttons.setBorder(BorderFactory.createTitledBorder("Room Operations"));

        JButton addButton = new JButton("Add Room Records");
        JButton availabilityButton = new JButton("Display Room Availability");
        JButton assignVacateButton = new JButton("Assign / Vacate Room");
        JButton searchButton = new JButton("Search by Room Number");
        JButton viewAllButton = new JButton("View All Recorded Rooms");

        buttons.add(addButton);
        buttons.add(availabilityButton);
        buttons.add(assignVacateButton);
        buttons.add(searchButton);
        buttons.add(viewAllButton);

        mainPanel.add(form, BorderLayout.NORTH);
        mainPanel.add(buttons, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Actions dayon here
    
        addButton.addActionListener(e -> addRoom());
        availabilityButton.addActionListener(e -> displayAvailability());
        assignVacateButton.addActionListener(e -> handleAssignOrVacate());
        searchButton.addActionListener(e -> searchRoom());
        viewAllButton.addActionListener(e -> showRoomsTable());
        pack();
    }

    private void updatePrice() {
        String type = (String) txtType.getSelectedItem();
        if ("Single".equals(type)) {
            txtPrice.setText("1500");
        } else if ("Double".equals(type)) {
            txtPrice.setText("1900");
        } else if ("Suite".equals(type)) {
            txtPrice.setText("3500");
        } else if ("Family".equals(type)) {
            txtPrice.setText("4000");
        }
    }

    private void addRoom() {
        try {
            int number = Integer.parseInt(txtNumber.getText().trim());
            String type = (String) txtType.getSelectedItem();
            double price = Double.parseDouble(txtPrice.getText().trim());
            String guest = txtGuest.getText().trim();

           if (tracker.addRoom(number, type, price)) {
    if (!guest.isEmpty()) {
        tracker.assignRoom(number, guest);
    }

    refreshRoomTable();
    JOptionPane.showMessageDialog(this, "Room " + number + " recorded successfully!");
    clearFields();

} else {
    JOptionPane.showMessageDialog(this, "Failed to record: Duplicate room number, invalid price, or full storage.");
}
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Input Validation Error: Room Number and Price must be valid numbers.");
        }
    }

    private void displayAvailability() {
        JOptionPane.showMessageDialog(this,
                "TOTAL ROOMS RECORDED: " + tracker.getSize()
                + "\nAVAILABLE ROOMS: " + tracker.countAvailableRooms()
                + "\nOCCUPIED ROOMS: " + tracker.countOccupiedRooms()
        );
    }

    private void handleAssignOrVacate() {
        String input = txtNumber.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Input Validation Error: Enter a Room Number in the field first.");
            return;
        }

        try {
            int number = Integer.parseInt(input);
            String[] options = {"Assign Room", "Vacate Room"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Choose action for Room Number: " + number,
                    "Assign or Vacate",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            //here is assigning and vacate

          if (choice == 0) {
    String guest = txtGuest.getText().trim();

    if (tracker.assignRoom(number, guest)) {

        refreshRoomTable(); // NEW CODE - ADD HERE

        JOptionPane.showMessageDialog(this, "Room " + number + " assigned to " + guest + "!");
        clearFields();

    } else {
        JOptionPane.showMessageDialog(this, "Failed: Room not found, already occupied, or guest name is empty.");
    }else if (choice == 1) {
    if (tracker.vacateRoom(number)) {

        refreshRoomTable(); // NEW CODE - ADD HERE

        JOptionPane.showMessageDialog(this, "Room " + number + " vacated successfully!");
        clearFields();

    } else {
        JOptionPane.showMessageDialog(this, "Failed: Room not found or already available.");
    }
}
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Input Validation Error: Enter a numeric room number.");
        }
    }

    private void searchRoom() {
        String input = JOptionPane.showInputDialog(this, "Enter Room Number to search:");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int number = Integer.parseInt(input.trim());
            int index = tracker.searchRoom(number);

            if (index == -1) {
                JOptionPane.showMessageDialog(this, "SearchResult: Room " + number + " NOT FOUND in array records!");
            } else {
                HotelRoomReservationTracker1.Room room = tracker.getRoomAt(index);
                JOptionPane.showMessageDialog(this,
                        "SearchResult: Room Found!\n"
                        + "Room Number: " + room.roomNumber + "\n"
                        + "Room Type: " + room.roomType + "\n"
                        + "Price: PHP " + room.price + "\n"
                        + "Status: " + room.status + "\n"
                        + "Guest Name: " + room.guestName
                );
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Input Validation Error: Enter a valid room number.");
        }
    }

    private void viewAllRooms() {
        if (tracker.getSize() == 0) {
            JOptionPane.showMessageDialog(this, "No room records saved yet.");
            return;
        }

        StringBuilder sb = new StringBuilder("Stored Array Records:\n\n");
        for (int i = 0; i < tracker.getSize(); i++) {
            HotelRoomReservationTracker1.Room r = tracker.getRoomAt(i);
            sb.append("Room ").append(r.roomNumber)
              .append(" | ").append(r.roomType)
              .append(" | PHP ").append(r.price)
              .append(" | Status: ").append(r.status)
              .append(" | Guest: ").append(r.guestName)
              .append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private void clearFields() {
        txtNumber.setText("");
        txtType.setSelectedIndex(0);
        txtPrice.setText("1500");
        txtGuest.setText("");
    }

    
    // NEW TABLE POPUP
    private JDialog roomDialog;
    private javax.swing.table.AbstractTableModel roomModel;

    private void showRoomsTable() {

        // If table is already open, bring it to front
        if (roomDialog != null && roomDialog.isDisplayable()) {
            refreshRoomTable();
            roomDialog.toFront();
            return;
        }

        // Table columns
        String[] columns = {
            "Room Number",
            "Room Type",
            "Price",
            "Status",
            "Guest Name"
        };

        // Table connected to backend array
        roomModel = new javax.swing.table.AbstractTableModel() {

            @Override
            public int getRowCount() {
                return tracker.getSize();
            }

            @Override
            public int getColumnCount() {
                return columns.length;
            }

            @Override
            public String getColumnName(int column) {
                return columns[column];
            }

            @Override
            public Object getValueAt(int row, int column) {

                HotelRoomReservationTracker1.Room room =
                        tracker.getRoomAt(row);

                switch (column) {
                    case 0: return room.roomNumber;
                    case 1: return room.roomType;
                    case 2: return room.price;
                    case 3: return room.status;
                    case 4: return room.guestName;
                    default: return "";
                }
            }
        };

        JTable table = new JTable(roomModel);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        // Create popup window
        roomDialog = new JDialog(
                this,
                "All Recorded Rooms",
                false
        );

        roomDialog.setSize(800, 400);
        roomDialog.setLayout(new BorderLayout());
        roomDialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        roomDialog.add(scrollPane, BorderLayout.CENTER);
        roomDialog.setLocationRelativeTo(this);
        roomDialog.setVisible(true);
    }

    // UPDATE TABLE WHEN RECORDS CHANGE
    private void refreshRoomTable() {
        if (roomModel != null) {
            roomModel.fireTableDataChanged();
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HotelReservationGUI().setVisible(true);
        });
    }
}
