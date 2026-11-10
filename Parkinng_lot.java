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

    /**
     * Creates a vehicle with its identifying license and category.
     *
     * @param licenseNumber vehicle registration identifier; must not be blank
     * @param vehicleType category used to match the vehicle to a parking spot
     */
    public Vehicle(String licenseNumber, VehicleType vehicleType) {
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("License number must not be blank");
        }
        this.licenseNumber = licenseNumber;
        this.vehicleType = vehicleType;
    }

    /**
     * Gets this vehicle's license number.
     *
     * @return vehicle registration identifier
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Gets this vehicle's category.
     *
     * @return vehicle category
     */
    public VehicleType getVehicleType() {
        return vehicleType;
    }
}

class Car extends Vehicle {
    /**
     * Creates a car with the given license number.
     *
     * @param licenseNumber car registration identifier
     */
    public Car(String licenseNumber) {
        super(licenseNumber, VehicleType.CAR);
    }
}

class Truck extends Vehicle {
    /**
     * Creates a truck with the given license number.
     *
     * @param licenseNumber truck registration identifier
     */
    public Truck(String licenseNumber) {
        super(licenseNumber, VehicleType.TRUCK);
    }
}

class Bus extends Vehicle {
    /**
     * Creates a bus with the given license number.
     *
     * @param licenseNumber bus registration identifier
     */
    public Bus(String licenseNumber) {
        super(licenseNumber, VehicleType.BUS);
    }
}

class Bike extends Vehicle {
    /**
     * Creates a bike with the given license number.
     *
     * @param licenseNumber bike registration identifier
     */
    public Bike(String licenseNumber) {
        super(licenseNumber, VehicleType.BIKE);
    }
}

class VehicleFactory {
    /**
     * Creates the vehicle subtype corresponding to the requested category.
     *
     * @param vehicleType category of vehicle to create
     * @param licenseNumber registration identifier for the vehicle
     * @return a vehicle instance of the requested subtype
     */
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

class ParkingSpot {
    private final String spotId;
    private final VehicleType vehicleType;
    private Vehicle vehicle;

    /**
     * Creates an empty spot for one vehicle category.
     *
     * @param spotId identifier for this parking spot
     * @param vehicleType category this spot can accommodate
     */
    public ParkingSpot(String spotId, VehicleType vehicleType) {
        this.spotId = spotId;
        this.vehicleType = vehicleType;
        this.vehicle = null;
    }

    /**
     * Checks whether this spot currently has no vehicle.
     *
     * @return true if the spot is vacant
     */
    public boolean isFree() {
        return vehicle == null;
    }

    /**
     * Checks whether the spot is vacant and compatible with the vehicle.
     *
     * @param vehicle vehicle to check
     * @return true if this spot can accommodate the vehicle
     */
    public boolean canFitVehicle(Vehicle vehicle) {
        return isFree() && this.vehicleType == vehicle.getVehicleType();
    }

    /**
     * Parks a compatible vehicle in this spot.
     *
     * @param vehicle vehicle to park
     */
    public void parkVehicle(Vehicle vehicle) {
        if (!canFitVehicle(vehicle)) {
            throw new IllegalArgumentException("Spot is occupied or vehicle type does not match");
        }
        this.vehicle = vehicle;
    }

    /**
     * Removes the vehicle from this spot.
     */
    public void unParkVehicle() {
        if (isFree()) {
            throw new IllegalStateException("Parking spot is already free");
        }
        this.vehicle = null;
    }

    /**
     * Gets this spot's identifier.
     *
     * @return spot identifier
     */
    public String getSpotId() {
        return spotId;
    }

    /**
     * Gets the category supported by this spot.
     *
     * @return supported vehicle category
     */
    public VehicleType getvehicleType() {
        return vehicleType;
    }

    /**
     * Gets the vehicle currently using this spot.
     *
     * @return parked vehicle, or null if vacant
     */
    public Vehicle getVehicle() {
        return vehicle;
    }
}

class ParkingFloor {
    private final String floorNo;
    private final List<ParkingSpot> parkingspot;

    /**
     * Creates a floor with no parking spots.
     *
     * @param floorNo floor name or number
     */
    public ParkingFloor(String floorNo) {
        this.floorNo = floorNo;
        this.parkingspot = new ArrayList<>();
    }

    /**
     * Adds a parking spot to this floor.
     *
     * @param parkingspot spot to add
     */
    public void addParkingSpot(ParkingSpot parkingspot) {
        this.parkingspot.add(parkingspot);
    }

    /**
     * Finds the first vacant spot compatible with a vehicle.
     *
     * @param vehicle vehicle for which a spot is needed
     * @return first compatible vacant spot
     */
    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot spot : parkingspot) {
            if (spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }
        throw new IllegalStateException("No available parking spot for the given vehicle");
    }

    /**
     * Counts all spots of a vehicle category, occupied or vacant.
     *
     * @param vehicleType category whose capacity is counted
     * @return number of configured spots for the category
     */
    public int getTotalSpots(VehicleType vehicleType) {
        int count = 0;
        for (ParkingSpot spot : parkingspot) {
            if (spot.getvehicleType() == vehicleType) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets this floor's name or number.
     *
     * @return floor identifier
     */
    public String getFloorNo() {
        return floorNo;
    }

    /**
     * Gets the spots registered on this floor.
     *
     * @return list of parking spots
     */
    public List<ParkingSpot> getParkingSpots() {
        return parkingspot;
    }
}

class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot parkingSpot;
    private TicketStatus ticketStatus;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private final int floorNo;

    /**
     * Creates an active ticket for a vehicle's parking session.
     *
     * @param vehicle vehicle associated with the session
     * @param parkingSpot spot reserved by the session
     * @param floorNo number of the assigned floor
     */
    public ParkingTicket(Vehicle vehicle, ParkingSpot parkingSpot, int floorNo) {
        this.ticketId = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.ticketStatus = TicketStatus.ACTIVE;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.floorNo = floorNo;
    }

    /**
     * Gets this ticket's unique identifier.
     *
     * @return ticket identifier
     */
    public String getTicketId() {
        return ticketId;
    }

    /**
     * Gets the vehicle associated with this ticket.
     *
     * @return ticket vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Gets the spot reserved by this ticket.
     *
     * @return reserved parking spot
     */
    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    /**
     * Gets the current ticket state.
     *
     * @return ticket status
     */
    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    /**
     * Gets the time the parking session began.
     *
     * @return entry timestamp
     */
    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    /**
     * Gets the time the parking session ended.
     *
     * @return exit timestamp, or null before checkout
     */
    public LocalDateTime getExitTime() {
        return exitTime;
    }

    /**
     * Gets the floor number assigned to this ticket.
     *
     * @return assigned floor number
     */
    public int getFloorNo() {
        return floorNo;
    }

    /**
     * Records the time the vehicle exits.
     *
     * @param exitTime checkout timestamp
     */
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    /**
     * Changes the ticket's lifecycle status.
     *
     * @param ticketStatus new status
     */
    public void setStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
}

interface PaymentFeeStrategy {
    /**
     * Calculates the fee for a vehicle and billable duration.
     *
     * @param vehicleType category used to select the hourly rate
     * @param hours number of billable hours
     * @return parking fee
     */
    double calculateFee(VehicleType vehicleType, long hours);
}

class BasicallyHourlyRateStrategy implements PaymentFeeStrategy {
    /**
     * Calculates the basic fee using the vehicle's hourly rate.
     *
     * @param vehicleType category used to select the rate
     * @param hours billable hours; at least one hour is charged
     * @return basic parking fee
     */
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
    /**
     * Calculates the premium fee using the vehicle's hourly rate.
     *
     * @param vehicleType category used to select the rate
     * @param hours billable hours; at least one hour is charged
     * @return premium parking fee
     */
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

interface PaymentStrategy {
    /**
     * Attempts to pay a parking fee.
     *
     * @param amount amount to pay
     * @return true if payment is accepted; otherwise false
     */
    boolean pay(double amount);
}

class CashPaymentStrategy implements PaymentStrategy {
    /**
     * Accepts a positive payment amount for the cash example.
     *
     * @param amount amount to pay
     * @return true if the amount is positive
     */
    @Override
    public boolean pay(double amount) {
        return amount > 0;
    }
}

class CardPaymentStrategy implements PaymentStrategy {
    private final String cardNumber;

    /**
     * Creates a card payment strategy.
     *
     * @param cardNumber card identifier used by this example implementation
     */
    public CardPaymentStrategy(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Simulates card payment validation.
     *
     * @param amount amount to pay
     * @return true if a card identifier is present and the amount is positive
     */
    @Override
    public boolean pay(double amount) {
        return cardNumber != null && !cardNumber.trim().isEmpty() && amount > 0;
    }
}

class UPIPaymentStrategy implements PaymentStrategy {
    private final String upiId;

    /**
     * Creates a UPI payment strategy.
     *
     * @param upiId UPI identifier used by this example implementation
     */
    public UPIPaymentStrategy(String upiId) {
        this.upiId = upiId;
    }

    /**
     * Simulates UPI payment validation.
     *
     * @param amount amount to pay
     * @return true if a UPI identifier is present and the amount is positive
     */
    @Override
    public boolean pay(double amount) {
        return upiId != null && !upiId.trim().isEmpty() && amount > 0;
    }
}

class ParkingLot {
    private static ParkingLot instance;

    /**
     * Prevents direct construction so the lot is created through getInstance().
     */
    private ParkingLot() {
    }

    /**
     * Gets the shared parking lot instance, creating it on first use.
     *
     * @return process-wide parking lot instance
     */
    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    private final List<ParkingFloor> floors = new ArrayList<>();
    private final Map<String, ParkingTicket> activeTickets = new HashMap<>();
    private PaymentFeeStrategy paymentFeeStrategy = new BasicallyHourlyRateStrategy();

    /**
     * Adds a floor to the lot's allocation search order.
     *
     * @param floor floor to register
     */
    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    /**
     * Selects the fee policy used for future checkouts.
     *
     * @param strategy fee calculation strategy to use
     */
    public void setFeeStrategy(PaymentFeeStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Fee strategy must not be null");
        }
        this.paymentFeeStrategy = strategy;
    }

    /**
     * Allocates a compatible vacant spot and creates an active ticket.
     *
     * @param vehicle vehicle to park
     * @return active ticket for the parking session
     */
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
     * Calculates and collects the fee, then closes the ticket and releases its spot.
     *
     * @param ticketId identifier of the active ticket to check out
     * @param paymentStrategy payment method used to collect the fee
     * @return amount charged for the parking session
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

public class Parkinng_lot {
    /**
     * Runs a short example of parking, declined payment, and successful checkout.
     *
     * @param args command-line arguments; not used by this example
     */
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
