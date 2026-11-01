package hotelroomreservationtracker1;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

public class HotelReservationGUI extends JFrame {

    private final HotelRoomReservationTracker1 tracker =
            new HotelRoomReservationTracker1();

    private JTextField txtNumber;
    private JComboBox<String> txtType;
    private JTextField txtPrice;
    private JTextField txtGuest;

    // TABLE POPUPS
    private JDialog roomDialog;
    private RoomTableModel roomModel;
    private JLabel roomCountLabel;

    public HotelReservationGUI() {

        setTitle("Hotel Room Reservation Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setPreferredSize(new Dimension(390, 450));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

      
        tracker.addRoom(101, "Single", 1500);
        tracker.assignRoom(101, "Aiza Datu");

        tracker.addRoom(103, "Suite", 3500);
        tracker.assignRoom(103, "Kei Nagase");

     
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(
                BorderFactory.createTitledBorder("Room Information")
        );

        txtNumber = new JTextField();

        txtType = new JComboBox<>(
                new String[]{"Single", "Double", "Suite", "Family"}
        );

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

       
        JPanel buttons = new JPanel(new GridLayout(5, 1, 8, 8));

        buttons.setBorder(
                BorderFactory.createTitledBorder("Room Operations")
        );

        JButton addButton =
                new JButton("Add Room Records");

        JButton availabilityButton =
                new JButton("Display Room Availability");

        JButton assignVacateButton =
                new JButton("Assign / Vacate Room");

        JButton searchButton =
                new JButton("Search by Room Number");

        JButton viewAllButton =
                new JButton("View All Recorded Rooms");

        buttons.add(addButton);
        buttons.add(availabilityButton);
        buttons.add(assignVacateButton);
        buttons.add(searchButton);
        buttons.add(viewAllButton);

        mainPanel.add(form, BorderLayout.NORTH);
        mainPanel.add(buttons, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // BUTTONS
        addButton.addActionListener(e -> addRoom());

        availabilityButton.addActionListener(
                e -> displayAvailability()
        );

        assignVacateButton.addActionListener(
                e -> handleAssignOrVacate()
        );

        searchButton.addActionListener(e -> searchRoom());

        viewAllButton.addActionListener(
                e -> showRoomsTable()
        );

        pack();
        setLocationRelativeTo(null);
    }

    // PRICE
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

    // ADD ROOM RECORD
    private void addRoom() {

        try {
            int number = Integer.parseInt(
                    txtNumber.getText().trim()
            );

            String type = (String) txtType.getSelectedItem();

            double price = Double.parseDouble(
                    txtPrice.getText().trim()
            );

            String guest = txtGuest.getText().trim();

           
            if (number <= 0 ||
                !Double.isFinite(price) ||
                price <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Room number and price must be greater than zero."
                );

                return;
            }

            if (tracker.addRoom(number, type, price)) {

                if (!guest.isEmpty()) {
                    tracker.assignRoom(number, guest);
                }

                
                refreshRoomTable();

                JOptionPane.showMessageDialog(
                        this,
                        "Room " + number + " recorded successfully!"
                );

                clearFields();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Failed: Duplicate room number or room storage is full."
                );
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Room number must be an integer and price must be a valid number."
            );
        }
    }

    // DISPLAY ROOM AVAILABILITY
    private void displayAvailability() {

        JOptionPane.showMessageDialog(
                this,
                "TOTAL ROOMS RECORDED: " + tracker.getSize()
                + "\nAVAILABLE ROOMS: "
                + tracker.countAvailableRooms()
                + "\nOCCUPIED ROOMS: "
                + tracker.countOccupiedRooms()
        );
    }

    // ASSIGN OR VACATE ROOM
    private void handleAssignOrVacate() {

        String input = txtNumber.getText().trim();

        if (input.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a room number first."
            );

            return;
        }

        try {

            int number = Integer.parseInt(input);

            if (number <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Enter a positive room number."
                );

                return;
            }

            String[] options = {
                "Assign Room",
                "Vacate Room"
            };

            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Choose an action for Room " + number,
                    "Assign or Vacate",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            // ASSIGN ROOM
            if (choice == 0) {

                String guest = txtGuest.getText().trim();

                if (tracker.assignRoom(number, guest)) {

                    refreshRoomTable();

                    JOptionPane.showMessageDialog(
                            this,
                            "Room " + number
                            + " assigned to " + guest + "!"
                    );

                    clearFields();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Failed: Room not found, already occupied, or guest name is empty."
                    );
                }

            // VACATE ROOM
            } else if (choice == 1) {

                if (tracker.vacateRoom(number)) {

                    refreshRoomTable();

                    JOptionPane.showMessageDialog(
                            this,
                            "Room " + number
                            + " vacated successfully!"
                    );

                    clearFields();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Failed: Room not found or already available."
                    );
                }
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid numeric room number."
            );
        }
    }

   
    private void searchRoom() {

        String input = JOptionPane.showInputDialog(
                this,
                "Enter room number to search:"
        );

        if (input == null) {
            return;
        }

        if (input.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a room number to search."
            );

            return;
        }

        try {

            int number = Integer.parseInt(input.trim());

            int index = tracker.searchRoom(number);

            if (index == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Search Result: Room " + number
                        + " NOT FOUND!"
                );

            } else {

                HotelRoomReservationTracker1.Room room =
                        tracker.getRoomAt(index);

                JOptionPane.showMessageDialog(
                        this,
                        "Search Result: Room Found!\n"
                        + "Room Number: " + room.roomNumber + "\n"
                        + "Room Type: " + room.roomType + "\n"
                        + "Price: PHP " + room.price + "\n"
                        + "Status: " + room.status + "\n"
                        + "Guest Name: " + room.guestName
                );
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid numeric room number."
            );
        }
    }

    // TABLE POPUP
    private void showRoomsTable() {

       
        if (roomDialog != null && roomDialog.isDisplayable()) {

            refreshRoomTable();

            roomDialog.toFront();
            roomDialog.requestFocus();

            return;
        }

        roomModel = new RoomTableModel();

        JTable table = new JTable(roomModel);

        table.setRowHeight(28);
        table.setFillsViewportHeight(true);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

       
        JLabel title = new JLabel(
                "HOTEL ROOM RECORDS",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

       
        roomCountLabel = new JLabel();

        JButton refreshButton =
                new JButton("Refresh Records");

        refreshButton.addActionListener(
                e -> refreshRoomTable()
        );

        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );

        bottomPanel.add(
                roomCountLabel,
                BorderLayout.WEST
        );

        bottomPanel.add(
                refreshButton,
                BorderLayout.EAST
        );

        
        roomDialog = new JDialog(
                this,
                "All Recorded Rooms",
                false
        );

        roomDialog.setLayout(new BorderLayout(8, 8));
        roomDialog.setSize(850, 400);

        roomDialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

      
        roomDialog.add(title, BorderLayout.NORTH);

        roomDialog.add(
                scrollPane,
                BorderLayout.CENTER
        );

        roomDialog.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        roomDialog.setLocationRelativeTo(this);

        refreshRoomTable();

      
        roomDialog.setVisible(true);
    }

    
    private class RoomTableModel extends AbstractTableModel {

        private final String[] columns = {
            "Room Number",
            "Room Type",
            "Price (PHP)",
            "Status",
            "Guest Name"
        };

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

            if (room == null) {
                return "";
            }

            switch (column) {

                case 0:
                    return room.roomNumber;

                case 1:
                    return room.roomType;

                case 2:
                    return room.price;

                case 3:
                    return room.status;

                case 4:
                    return room.guestName;

                default:
                    return "";
            }
        }
    }

    
    private void refreshRoomTable() {

        if (roomModel != null) {
            roomModel.fireTableDataChanged();
        }

        if (roomCountLabel != null) {
            roomCountLabel.setText(
                    "Total Rooms: " + tracker.getSize()
            );
        }
    }

    
    private void clearFields() {

        txtNumber.setText("");
        txtType.setSelectedIndex(0);
        txtPrice.setText("1500");
        txtGuest.setText("");
    }

    // MAIN METHOD
    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> new HotelReservationGUI().setVisible(true)
        );
    }
}
