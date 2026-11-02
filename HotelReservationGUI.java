import java.awt.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class HotelReservationGUI extends JFrame {

    private final HotelRoomReservationTracker1 tracker =
            new HotelRoomReservationTracker1();

    private JTextField txtNumber;
    private JComboBox<String> txtType;
    private JTextField txtPrice;
    private JTextField txtGuest;

    private JDialog roomDialog;
    private RoomTableModel roomModel;
    private BookingTableModel bookingModel;
    private JLabel roomCountLabel;

    public HotelReservationGUI() {
        setTitle("Hotel Room Reservation Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setPreferredSize(new Dimension(440, 450));
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

    
        JPanel buttons = new JPanel(new BorderLayout(4, 4));
        buttons.setBorder(
                BorderFactory.createTitledBorder("Room Operations")
        );

        JButton addButton = new JButton("ADD ROOMS");
        JButton availabilityButton = new JButton("ROOM AVAILABLE");
        JButton assignVacateButton = new JButton("ASSIGN, VACATE & BOOK");
        JButton searchButton = new JButton("SEARCH ROOM");
        JButton viewAllButton = new JButton("VIEW ALL RECORDS");

        JPanel buttonGrid = new JPanel(new GridLayout(2, 2, 8, 8));

        buttonGrid.add(addButton);
        buttonGrid.add(availabilityButton);

        buttonGrid.add(assignVacateButton);
        buttonGrid.add(searchButton);

        buttons.add(buttonGrid, BorderLayout.CENTER);

        viewAllButton.setPreferredSize(new Dimension(0, 60));
        
        buttons.add(viewAllButton, BorderLayout.SOUTH);

        mainPanel.add(form, BorderLayout.NORTH);
        mainPanel.add(buttons, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> addRoom());
        availabilityButton.addActionListener(
                e -> displayAvailability()
        );
        assignVacateButton.addActionListener(
                e -> handleAssignOrVacate()
        );
        searchButton.addActionListener(e -> searchRoom());
        viewAllButton.addActionListener(e -> showRoomsTable());

        pack();
        setLocationRelativeTo(null);
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

    // ADD ROOM
    private void addRoom() {
        try {
            int number = Integer.parseInt(txtNumber.getText().trim());
            String type = (String) txtType.getSelectedItem();
            double price = Double.parseDouble(txtPrice.getText().trim());
            String guest = txtGuest.getText().trim();

            if (number <= 0 || !Double.isFinite(price) || price <= 0) {
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
                        "Room " + number + " Recorded successfully!"
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

    // ROOM AVAILABILITY
    private void displayAvailability() {
        JOptionPane.showMessageDialog(
                this,
                "TOTAL ROOMS RECORDED: " + tracker.getSize()
                + "\nAVAILABLE ROOMS: " + tracker.countAvailableRooms()
                + "\nOCCUPIED ROOMS: " + tracker.countOccupiedRooms()
                + "\nWAITING BOOKINGS: " + tracker.getQueueSize()
        );
    }

    // ASSIGN / VACATE / BOOK IN ADVANCE
    private void handleAssignOrVacate() {
        String[] options = {
            "Assign Room", "Vacate Room", "Book in Advance"
        };
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose a room operation:",
                "Room Operations",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice < 0) {
            return;
        }

        int number;
        try {
            number = Integer.parseInt(txtNumber.getText().trim());
            if (number <= 0) {
                JOptionPane.showMessageDialog(this, "Enter a positive room number.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid room number first.");
            return;
        }

        if (choice == 0) { 
            String guest = txtGuest.getText().trim();
            if (tracker.assignRoom(number, guest)) {
                refreshRoomTable();
                JOptionPane.showMessageDialog(this,
                        "Room " + number + " assigned to " + guest + "!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Room not found, already occupied, or guest name is empty.");
            }

        } else if (choice == 1) { 
            String nextGuest = tracker.nextGuestForRoom(number);
            if (tracker.vacateRoom(number)) {
                refreshRoomTable();
                if (nextGuest == null) {
                    JOptionPane.showMessageDialog(this,
                            "Room " + number + " vacated and is now AVAILABLE.");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Room " + number + " vacated.\n"
                            + "Next in queue: " + nextGuest + "\n"
                            + "Room automatically assigned to " + nextGuest + ".");
                }
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Room not found or already available.");
            }

        } else if (choice == 2) {
            String guest = txtGuest.getText().trim();
            if (tracker.bookInAdvance(number, guest)) {
                refreshRoomTable();
                JOptionPane.showMessageDialog(this,
                        guest + " added to the waiting queue for Room "
                        + number + ".\n"
                        + "Queue position overall: " + tracker.getQueueSize());
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Booking failed. Enter a guest name and an existing "
                        + "OCCUPIED room number. The queue may also be full.");
            }
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
                        "Search Result: Room " + number + " NOT FOUND!"
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

    // VIEW ALL RECORDS 
    private void showRoomsTable() {
        if (roomDialog != null && roomDialog.isDisplayable()) {
            refreshRoomTable();
            roomDialog.toFront();
            roomDialog.requestFocus();
            return;
        }

        roomModel = new RoomTableModel();
        bookingModel = new BookingTableModel();

        JTable roomTable = new JTable(roomModel);
        JTable bookingTable = new JTable(bookingModel);
        roomTable.setRowHeight(28);
        bookingTable.setRowHeight(28);
        roomTable.setFillsViewportHeight(true);
        bookingTable.setFillsViewportHeight(true);
        roomTable.getTableHeader().setReorderingAllowed(false);
        bookingTable.getTableHeader().setReorderingAllowed(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Rooms", new JScrollPane(roomTable));
        tabs.addTab("Advance Booking Queue", new JScrollPane(bookingTable));

        JLabel title = new JLabel(
                "HOTEL ROOM RECORDS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        roomCountLabel = new JLabel();
        JButton refreshButton = new JButton("Refresh Records");
        refreshButton.addActionListener(e -> refreshRoomTable());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bottomPanel.add(roomCountLabel, BorderLayout.WEST);
        bottomPanel.add(refreshButton, BorderLayout.EAST);

        roomDialog = new JDialog(this, "All Recorded Rooms", false);
        roomDialog.setLayout(new BorderLayout(8, 8));
        roomDialog.setSize(850, 400);
        roomDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        roomDialog.add(title, BorderLayout.NORTH);
        roomDialog.add(tabs, BorderLayout.CENTER);
        roomDialog.add(bottomPanel, BorderLayout.SOUTH);
        roomDialog.setLocationRelativeTo(this);

        refreshRoomTable();
        roomDialog.setVisible(true);
    }

    // TABLE MODEL
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

    private class BookingTableModel extends AbstractTableModel {
        private final String[] columns = {
            "Queue Order", "Room Number", "Guest Name", "Status"
        };

        @Override
        public int getRowCount() {
            return tracker.getQueueSize();
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
            HotelRoomReservationTracker1.Booking booking =
                    tracker.getBookingAt(row);
            if (booking == null) {
                return "";
            }
            switch (column) {
                case 0: return row + 1;
                case 1: return booking.roomNumber;
                case 2: return booking.guestName;
                case 3: return "Waiting";
                default: return "";
            }
        }
    }

    private void refreshRoomTable() {
        if (roomModel != null) {
            roomModel.fireTableDataChanged();
        }
        if (bookingModel != null) {
            bookingModel.fireTableDataChanged();
        }

        if (roomCountLabel != null) {
            roomCountLabel.setText(
                    "Total Rooms: " + tracker.getSize()
                    + "   |   Waiting: " + tracker.getQueueSize()
            );
        }
    }

    // CLEAR FIELDS
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
