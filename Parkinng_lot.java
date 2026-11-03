public enum VehicleType{
    CAR,
    BUS,
    TRUCK,
    BIKE
}

public enum TicketStatus{
    ACTIVE,
    PAID,
    CLOSED
}

public abstract class Vehicle{
    private final String licenseNumber;
    private final VehicleType vehicleType;

    public Vehicle(String licenseNumber , VehicleType vehicleType){
        this.licenseNumber = licenseNumber;
        this.vehicleType = vehicleType;
    }

    public String getLicenseNumber(){
        return licenseNumber;
    }
    public VehicleType getVehicleType(){
        return vehicleType;
    }
}

public class Car extends Vehicle{
    public Car(String licenseNumber){
        super(licenseNumber,VehicleType.CAR);
    }
}
public class Truck extends Vehicle{
    public Truck(String licenseNumber){
        super(licenseNumber,VehicleType.TRUCK);
    }
}

public class Bus extends Vehicle{
    public Bus(String licenseNumber){
        super(licenseNumber,VehicleType.BUS);
    }
}
public class Bike extends Vehicle{
    public Bike(String licenseNumber){
        super(licenseNumber,VehicleType.BIKE);
    }
}

public class VehicleFactory{
    public static Vehicle createVehicle(VehicleType vehicleType,String licenseNumber){
        switch(vehicleType){
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

public class ParkingSpot{
    private final String spotId;
    private final VehicleType vehicleType;
    private Vehicle vehicle;

    public ParkingSpot(String spotId,VehicleType vehicleType){
        this.spotId = spotId;
        this.vehicleType = vehicleType;
        this.vehicle = null;
    }

    public boolean isFree(){
        return vehicle == null;
    }

    public boolean canFitVehicle(Vehicle vehicle){
        return isFree() && this.vehicleType == vehicle.getVehicleType();
    }

    public void parkVehicle(Vehicle vehicle){
        if(!canFitVehicle(vehicle)){
            throw new IllegalArgumentException("Vehicle type does not match parking spot type");
        }
        if(!isFree()){
            throw new IllegalStateException("Parking spot is already occupied");
        }
        this.vehicle = vehicle;
    }

    public void unParkVehicle(){
        if(isFree()){
            throw new IllegalStateException("Parking spot is already free");
        }
        this.vehicle = null;
    }
     
     public String getSpotId(){
        return spotId;
     }
     public VehicleType getvehicleType(){
        return vehicleType;
     }
     public Vehicle getVehicle(){
        return vehicle;
     }
}

public class ParkingFloor{
    private final String floorNo;
    private final List<ParkingSpot>parkingspot;

    public ParkingFloor(String floorNo){
        this.floorNo = floorNo;
        this.parkingspot = new ArrayList<>();
    }

    public void addParkingSpot(ParkingSpot parkingspot){
        this.parkingspot.add(parkingspot);
    }

    public ParkingSpot findAvailableSpot(Vehicle vehicle){
        for(ParkingSpot spot : parkingspot){
            if(spot.canFitVehicle(vehicle)){
                return spot;
            }
        }
        throw new IllegalStateException("No available parking spot for the given vehicle");
    }

    public int getTotalSpots(VehicleType vehicleType){
        int cnt=0;
        for(ParkingSpot spot: parkingspot){
            if(spot.getvehicleType() == vehicleType){
                cnt++;
            }
        }
        return cnt;
    }
    public String getFloorNo(){
        return floorNo;
    }
    public List<ParkingSpot>getParkingSpots(){
        return parkingspot;
    }
}

public class ParkingTicket{
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot  parkingSpot;
    private TicketStatus ticketStatus;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private int floorNo;

    public ParkingTicket(Vehicle vehicle,ParkingSpot parkingSpot,int floorNo){
        this.ticketId = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.ticketStatus = TicketStatus.ACTIVE;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.floorNo = floorNo;
    }

    public String getTicketId(){
        return ticketId;
    }
    public Vehicle getVehicle(){
        return vehicle;
    }
    public ParkingSpot getParkingSpot(){
        return parkingSpot;
    }
    public TicketStatus getTicketStatus(){
        return ticketStatus;
    }
    public LocalDateTime getEntryTime(){
        return entryTime;
    }
    public LocalDateTime getExitTime(){
        return exitTime;
    }
    public int getFloorNo(){
        return floorNo;
    }

    public void setExitTime(LocalDateTime t) { this.exitTime = t; }

    public void setStatus(TicketStatus s)    { this.status = s; }
}

public interface PaymentFeeStrategy{
    double calculateFee(VehicleType vehicleType,long hours);
}

public class BasicallyHourlyRateStrategy implements PaymentFeeStrategy{
    double calculateFee(VehicleType vehicleType,long hours){
        double rateperhour =0;
        switch(vehicleType){
            case BIKE: rateperhour = 10; break;
            case CAR: rateperhour = 20;  break;
            case TRUCK: rateperhour = 30; break;
            case BUS: rateperhour = 40; break;
            default: throw new IllegalArgumentException("Invalid vehicle type");
        }
        return rateperhour * hours;
    }
}

public class PremiumHourlyRateStrategy implements PaymentFeeStrategy{
    double calculateFee(VehicleType vehicleType,long hours){
        double rateperhour =0;
        switch(vehicleType){
            case BIKE: rateperhour = 15; break;
            case CAR: rateperhour = 25;  break;
            case TRUCK: rateperhour = 35; break;
            case BUS: rateperhour = 45; break;
            default: throw new IllegalArgumentException("Invalid vehicle type");
        }
        return rateperhour * hours;
    }
}

public interface PaymentStrategy{
    boolean pay(double amount);
}

public class CashPaymentStrategy implements PaymentStrategy{
    public boolean pay(double amount) {
        return true;
    }
}

public class CardPaymentStrategy implements PaymentStrategy{
   private final String cardNumber;

   public CardPaymentStrategy(String cardNumber){
    this.cardNumber = cardNumber;
   }

    public boolean pay(double amount){
        return true;
    }
}

public class UPIPaymentStrategy implements PaymentStrategy{
 private final String upidId;
    
    public UPIPaymentStrategy(String upidId){
        this.upidId = upidId;
    }

    public boolean pay(double amount){
        return true;
    }
}

public class ParkingLot{

    private static ParkingLot instance;

    private ParkingLot(){}
     // we can make it thread safe by using synchronized keyword or double checked locking
     public static ParkingLot getInstance(){
        if(instance == null){
            instance = new ParkingLot();
        }
        return instance;
     }

     // Data
     
}









