<<<<<<< HEAD
package hotelroomreservationtracker1;

import javax.swing.SwingUtilities;

public class HotelRoomReservationTracker1 {

    public static final int MAX_ROOMS = 100;

    // --- INNER CLASS FOR ROOM DATA ---
    public static class Room {
        public int roomNumber;
        public String roomType;
        public double price;
        public String guestName;
        public String status;

        public Room(int roomNumber, String roomType, double price) {
            this.roomNumber = roomNumber;
            this.roomType = roomType;
            this.price = price;
            this.guestName = "N/A";
            this.status = "Available";
        }
    }

    // MANUAL FIXED-SIZE ARRAY (ZERO JAVA.UTIL IMPORTS)
    private final Room[] rooms = new Room[MAX_ROOMS];
    private int size = 0;

    // MANUAL LINEAR SEARCH
    public int searchRoom(int number) {
        for (int i = 0; i < size; i++) {
            if (rooms[i].roomNumber == number) {
                return i;
            }
        }
        return -1;
    }

    // ADD ROOMS
    public boolean addRoom(int number, String type, double price) {
        if (size >= MAX_ROOMS
                || number <= 0
                || type == null
                || type.trim().isEmpty()
                || !Double.isFinite(price)
                || price <= 0
                || searchRoom(number) != -1) {
            return false;
        }

        rooms[size] = new Room(number, type.trim(), price);
        size++;
        return true;
    }

    // ASSIGN ROOM
    public boolean assignRoom(int number, String guest) {
        int index = searchRoom(number);

        if (index == -1 || guest == null
                || guest.trim().isEmpty()
                || "Occupied".equalsIgnoreCase(rooms[index].status)) {
            return false;
        }

        rooms[index].guestName = guest.trim();
        rooms[index].status = "Occupied";
        return true;
    }

    // PARA VACATE ROOM
    public boolean vacateRoom(int number) {
        int index = searchRoom(number);

        if (index == -1 || "Available".equalsIgnoreCase(rooms[index].status)) {
            return false;
        }

        rooms[index].guestName = "N/A";
        rooms[index].status = "Available";
        return true;
    }

    // PAG CHANGE SA PRICE
    public boolean changePrice(int number, double newPrice) {
        int index = searchRoom(number);

        if (index == -1 || !Double.isFinite(newPrice) || newPrice <= 0) {
            return false;
        }

        rooms[index].price = newPrice;
        return true;
    }

    public Room getRoomAt(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return rooms[index];
    }

    public int getSize() {
        return size;
    }

    public int countAvailableRooms() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if ("Available".equalsIgnoreCase(rooms[i].status)) {
                count++;
            }
        }
        return count;
    }

    public int countOccupiedRooms() {
        return size - countAvailableRooms();
    }

    // --- MAIN METHOD: LAUNCHES THE GUI ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HotelReservationGUI().setVisible(true);
        });
    }
}