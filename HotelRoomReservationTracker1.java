
package hotelroomreservationtracker1;

public class HotelRoomReservationTracker1 {

    // ROOM INFORMATION
    public static class Room {
        public int roomNumber;
        public String roomType, guestName, status;
        public double price;

        public Room(int number, String type, double price) {
            roomNumber = number;
            roomType = type;
            this.price = price;
            guestName = "N/A";
            status = "Available";
        }
    }

    // MANUAL ARRAY
    private Room[] rooms = new Room[100];
    private int size = 0;

    // LINEAR SEARCH
    public int searchRoom(int number) {
        for (int i = 0; i < size; i++) {
            if (rooms[i].roomNumber == number) {
                return i;
            }
        }
        return -1;
    }

    // ADD ROOM
    public boolean addRoom(int number, String type, double price) {
        if (size == rooms.length || number <= 0 ||
            !Double.isFinite(price) || price <= 0 ||
            type == null || type.trim().isEmpty() ||
            searchRoom(number) != -1) {
            return false;
        }

        rooms[size] = new Room(number, type.trim(), price);
        size++;
        return true;
    }

    // ASSIGN ROOM
    public boolean assignRoom(int number, String guest) {
        int i = searchRoom(number);

        if (i == -1 || guest == null ||
            guest.trim().isEmpty() ||
            rooms[i].status.equals("Occupied")) {
            return false;
        }

        rooms[i].guestName = guest.trim();
        rooms[i].status = "Occupied";
        return true;
    }

    // VACATE ROOM
    public boolean vacateRoom(int number) {
        int i = searchRoom(number);

        if (i == -1 || rooms[i].status.equals("Available")) {
            return false;
        }

        rooms[i].guestName = "N/A";
        rooms[i].status = "Available";
        return true;
    }

    // CHANGE PRICE
    public boolean changePrice(int number, double price) {
        int i = searchRoom(number);

        if (i == -1 || !Double.isFinite(price) || price <= 0) {
            return false;
        }

        rooms[i].price = price;
        return true;
    }

    // GET ROOM
    public Room getRoomAt(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return rooms[index];
    }

    // TOTAL ROOMS
    public int getSize() {
        return size;
    }

    // AVAILABLE ROOMS
    public int countAvailableRooms() {
        int count = 0;

        for (int i = 0; i < size; i++) {
            if (rooms[i].status.equals("Available")) {
                count++;
            }
        }
        return count;
    }

    // OCCUPIED ROOMS
    public int countOccupiedRooms() {
        return size - countAvailableRooms();
    }
}
