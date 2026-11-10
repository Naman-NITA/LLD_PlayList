import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

enum VehicleType {
    CAR,
    BUS,
    TRUCK,
    BIKE
}

enum TicketStatus {
    ACTIVE,
    PAID,
    CLOSED
}

abstract class Vehicle {
    private final String licenseNumber;
    private final VehicleType vehicleType;

    public Vehicle(String licenseNumber, VehicleType vehicleType) {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("License number must not be blank");
        }
        this.licenseNumber = licenseNumber;
        this.vehicleType = vehicleType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
}

class Car extends Vehicle {
    public Car(String licenseNumber) {
        super(licenseNumber, VehicleType.CAR);
    }
}

class Truck extends Vehicle {
    public Truck(String licenseNumber) {
        super(licenseNumber, VehicleType.TRUCK);
    }
}

class Bus extends Vehicle {
    public Bus(String licenseNumber) {
        super(licenseNumber, VehicleType.BUS);
    }
}

class Bike extends Vehicle {
    public Bike(String licenseNumber) {
        super(licenseNumber, VehicleType.BIKE);
    }
}

/** Creates the matching vehicle subclass for the requested vehicle type. */
class VehicleFactory {
    public static Vehicle createVehicle(VehicleType vehicleType, String licenseNumber) {
        switch (vehicleType) {
            case CAR:
                return new Car(licenseNumber);
            case BUS:
                return new Bus(licenseNumber);
            case TRUCK:
                return new Truck(licenseNumber);
            case BIKE:
                return new Bike(licenseNumber);
            default:
                throw new IllegalArgumentException("Invalid vehicle type");
        }
    }
}

/** Stores one vehicle in a spot that supports only its configured vehicle type. */
class ParkingSpot {
    private final String spotId;
    private final VehicleType vehicleType;
    private Vehicle vehicle;

    public ParkingSpot(String spotId, VehicleType vehicleType) {
        this.spotId = spotId;
        this.vehicleType = vehicleType;
        this.vehicle = null;
    }

    public boolean isFree() {
        return vehicle == null;
    }

    public boolean canFitVehicle(Vehicle vehicle) {
        return isFree() && this.vehicleType == vehicle.getVehicleType();
    }

    public void parkVehicle(Vehicle vehicle) {
        if (!canFitVehicle(vehicle)) {
            throw new IllegalArgumentException("Spot is occupied or vehicle type does not match");
        }
        this.vehicle = vehicle;
    }

    public void unParkVehicle() {
        if (isFree()) {
            throw new IllegalStateException("Parking spot is already free");
        }
        this.vehicle = null;
    }

    public String getSpotId() {
        return spotId;
    }

    public VehicleType getvehicleType() {
        return vehicleType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}

/** Holds the spots on one floor and finds compatible available spots. */
class ParkingFloor {
    private final String floorNo;
    private final List<ParkingSpot> parkingspot;

    public ParkingFloor(String floorNo) {
        this.floorNo = floorNo;
        this.parkingspot = new ArrayList<>();
    }

    public void addParkingSpot(ParkingSpot parkingspot) {
        this.parkingspot.add(parkingspot);
    }

    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot spot : parkingspot) {
            if (spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }
        throw new IllegalStateException("No available parking spot for the given vehicle");
    }

    /** Returns the installed capacity for this vehicle type, including occupied spots. */
    public int getTotalSpots(VehicleType vehicleType) {
        int count = 0;
        for (ParkingSpot spot : parkingspot) {
            if (spot.getvehicleType() == vehicleType) {
                count++;
            }
        }
        return count;
    }

    public String getFloorNo() {
        return floorNo;
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingspot;
    }
}

/** Records a vehicle's active or completed parking session. */
class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot parkingSpot;
    private TicketStatus ticketStatus;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private final int floorNo;

    public ParkingTicket(Vehicle vehicle, ParkingSpot parkingSpot, int floorNo) {
        this.ticketId = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.ticketStatus = TicketStatus.ACTIVE;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.floorNo = floorNo;
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public void setStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
}

/** Calculates parking charges for a vehicle type and number of hours. */
interface PaymentFeeStrategy {
    double calculateFee(VehicleType vehicleType, long hours);
}

class BasicallyHourlyRateStrategy implements PaymentFeeStrategy {
    @Override
    public double calculateFee(VehicleType vehicleType, long hours) {
        double ratePerHour;
        switch (vehicleType) {
            case BIKE:
                ratePerHour = 10;
                break;
            case CAR:
                ratePerHour = 20;
                break;
            case TRUCK:
                ratePerHour = 30;
                break;
            case BUS:
                ratePerHour = 40;
                break;
            default:
                throw new IllegalArgumentException("Invalid vehicle type");
        }
        return ratePerHour * Math.max(1, hours);
    }
}

class PremiumHourlyRateStrategy implements PaymentFeeStrategy {
    @Override
    public double calculateFee(VehicleType vehicleType, long hours) {
        double ratePerHour;
        switch (vehicleType) {
            case BIKE:
                ratePerHour = 15;
                break;
            case CAR:
                ratePerHour = 25;
                break;
            case TRUCK:
                ratePerHour = 35;
                break;
            case BUS:
                ratePerHour = 45;
                break;
            default:
                throw new IllegalArgumentException("Invalid vehicle type");
        }
        return ratePerHour * Math.max(1, hours);
    }
}

/** Payment implementation returns whether the requested amount was accepted. */
interface PaymentStrategy {
    boolean pay(double amount);
}

class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        return amount > 0;
    }
}

class CardPaymentStrategy implements PaymentStrategy {
    private final String cardNumber;

    public CardPaymentStrategy(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean pay(double amount) {
        return cardNumber != null && !cardNumber.trim().isEmpty() && amount > 0;
    }
}

class UPIPaymentStrategy implements PaymentStrategy {
    private final String upiId;

    public UPIPaymentStrategy(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public boolean pay(double amount) {
        return upiId != null && !upiId.trim().isEmpty() && amount > 0;
    }
}

/** Coordinates floors, spot allocation, tickets, fees, and checkout. */
class ParkingLot {
    private static ParkingLot instance;

    private ParkingLot() {
    }

    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    private final List<ParkingFloor> floors = new ArrayList<>();
    private final Map<String, ParkingTicket> activeTickets = new HashMap<>();
    private PaymentFeeStrategy paymentFeeStrategy = new BasicallyHourlyRateStrategy();

    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    public void setFeeStrategy(PaymentFeeStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Fee strategy must not be null");
        }
        this.paymentFeeStrategy = strategy;
    }

    /** Finds a compatible spot, occupies it, and creates the active parking ticket. */
    public ParkingTicket parkVehicle(Vehicle vehicle) {
        for (ParkingTicket ticket : activeTickets.values()) {
            if (ticket.getVehicle().getLicenseNumber().equals(vehicle.getLicenseNumber())) {
                throw new IllegalStateException("Vehicle is already parked");
            }
        }

        for (int i = 0; i < floors.size(); i++) {
            ParkingFloor floor = floors.get(i);
            for (ParkingSpot spot : floor.getParkingSpots()) {
                if (spot.canFitVehicle(vehicle)) {
                    spot.parkVehicle(vehicle);
                    ParkingTicket ticket = new ParkingTicket(vehicle, spot, i + 1);
                    activeTickets.put(ticket.getTicketId(), ticket);
                    return ticket;
                }
            }
        }
        throw new IllegalStateException("No available parking spot for the given vehicle");
    }

    /**
     * Charges rounded-up hourly fees. A declined payment leaves the ticket and
     * parking spot active so checkout can be retried.
     */
    public double checkout(String ticketId, PaymentStrategy paymentStrategy) {
        ParkingTicket ticket = activeTickets.get(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Active ticket not found");
        }

        LocalDateTime exitTime = LocalDateTime.now();
        long minutes = Duration.between(ticket.getEntryTime(), exitTime).toMinutes();
        long hours = Math.max(1, (minutes + 59) / 60);
        double fee = paymentFeeStrategy.calculateFee(ticket.getVehicle().getVehicleType(), hours);
        if (!paymentStrategy.pay(fee)) {
            throw new IllegalStateException("Payment failed; ticket remains active");
        }

        ticket.setStatus(TicketStatus.PAID);
        ticket.setExitTime(exitTime);
        ticket.getParkingSpot().unParkVehicle();
        ticket.setStatus(TicketStatus.CLOSED);
        activeTickets.remove(ticketId);
        return fee;
    }
}

/** Small executable example for compiling and trying the parking flow. */
public class Parkinng_lot {
    public static void main(String[] args) {
        ParkingLot parkingLot = ParkingLot.getInstance();
        ParkingFloor floor = new ParkingFloor("Ground");
        floor.addParkingSpot(new ParkingSpot("B-1", VehicleType.BIKE));
        floor.addParkingSpot(new ParkingSpot("C-1", VehicleType.CAR));
        parkingLot.addFloor(floor);

        Vehicle vehicle = VehicleFactory.createVehicle(VehicleType.BIKE, "DEMO-123");
        ParkingTicket ticket = parkingLot.parkVehicle(vehicle);
        System.out.println("Parked at spot " + ticket.getParkingSpot().getSpotId());

        try {
            parkingLot.checkout(ticket.getTicketId(), amount -> false);
            throw new AssertionError("Declined payment should not complete checkout");
        } catch (IllegalStateException expected) {
            if (ticket.getTicketStatus() != TicketStatus.ACTIVE
                    || ticket.getParkingSpot().isFree()) {
                throw new AssertionError("Declined payment must keep the ticket and spot active");
            }
        }

        double fee = parkingLot.checkout(ticket.getTicketId(), new CashPaymentStrategy());
        System.out.println("Checkout successful. Fee: " + fee);
        System.out.println("Ticket status: " + ticket.getTicketStatus());
    }
}
