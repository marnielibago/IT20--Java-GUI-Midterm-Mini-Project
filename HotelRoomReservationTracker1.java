public class HotelRoomReservationTracker1 {

   // Para Stores information about sa each hotel room.
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

    // Para sa Stores guest booking informatio

    public static class Booking {
        public int roomNumber;
        public String guestName;

        public Booking(int number, String guest) {
            roomNumber = number;
            guestName = guest;
        }
    }
    
     // Para sa Stores hotel rooms and waiting reservations.
    private final Room[] rooms = new Room[100];
    private int size = 0;
    private final Booking[] waitingQueue = new Booking[100];
    private int queueSize = 0;


    // Para sa Finds sa room using its room number.
    
    public int searchRoom(int number) {
        for (int i = 0; i < size; i++) {
            if (rooms[i].roomNumber == number) {
                return i;
            }
        }
        return -1;
    }

    // Adds sa new room to the hotel room list

    public boolean addRoom(int number, String type, double price) {
        if (size == rooms.length || number <= 0 ||
                type == null || type.trim().isEmpty() ||
                !Double.isFinite(price) || price <= 0 ||
                searchRoom(number) != -1) {
            return false;
        }
        rooms[size++] = new Room(number, type.trim(), price);
        return true;
    }
    
//Assigns a guest to an available hotel room.
    public boolean assignRoom(int number, String guest) {
        int i = searchRoom(number);
        if (i == -1 || guest == null || guest.trim().isEmpty() ||
                rooms[i].status.equals("Occupied")) {
            return false;
        }
        rooms[i].guestName = guest.trim();
        rooms[i].status = "Occupied";
        return true;
    }

    public boolean bookInAdvance(int number, String guest) {
        int i = searchRoom(number);
        if (i == -1 || guest == null || guest.trim().isEmpty() ||
                !rooms[i].status.equals("Occupied") ||
                queueSize == waitingQueue.length) {
            return false;
        }
        waitingQueue[queueSize++] = new Booking(number, guest.trim());
        return true;
    }

    public String nextGuestForRoom(int number) {
        for (int i = 0; i < queueSize; i++) {
            if (waitingQueue[i].roomNumber == number) {
                return waitingQueue[i].guestName;
            }
        }
        return null;
    }

    public boolean vacateRoom(int number) {
        int i = searchRoom(number);
        if (i == -1 || rooms[i].status.equals("Available")) {
            return false;
        }

        rooms[i].guestName = "N/A";
        rooms[i].status = "Available";

        for (int q = 0; q < queueSize; q++) {
            if (waitingQueue[q].roomNumber == number) {
              
                assignRoom(number, waitingQueue[q].guestName);
        
                for (int j = q; j < queueSize - 1; j++) {
                    waitingQueue[j] = waitingQueue[j + 1];
                }
                waitingQueue[--queueSize] = null;
                break;
            }
        }
        return true;
    }

    public boolean changePrice(int number, double price) {
        int i = searchRoom(number);
        if (i == -1 || !Double.isFinite(price) || price <= 0) {
            return false;
        }
        rooms[i].price = price;
        return true;
    }

    public Room getRoomAt(int index) {
        return index >= 0 && index < size ? rooms[index] : null;
    }

    public Booking getBookingAt(int index) {
        return index >= 0 && index < queueSize ? waitingQueue[index] : null;
    }

    public int getSize() {
        return size;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public int countAvailableRooms() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (rooms[i].status.equals("Available")) {
                count++;
            }
        }
        return count;
    }

    public int countOccupiedRooms() {
        return size - countAvailableRooms();
    }
}
