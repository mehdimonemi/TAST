package company.Data;

import java.util.ArrayList;


/**
 * Created by Monemi_M on 10/07/2017.
 */
public class Commodity {

    double howMuchIsAllowed = 1;
    private int id;
    private String origin;
    private String destination;
    private int originId;
    private int destinationId;
    private String originDistrict;
    private String destinationDistrict;

    private String transportKind;
    private String mainCargoType;
    private String cargoType;
    private String wagonType;

    private double operationWagon;
    private double operationTon;
    private double planWagon;
    private double planTon;

    private double distance = 0;

    private double tonKilometerPlan;
    private double tonKilometerOperation;
    private ArrayList<Block> blocks = new ArrayList<>();

    private int check = 0;

    public Commodity(int id, String origin, String destination, double volumeWagon, double volumeTon,
                     double planWagon, double planTon, String wagonType, String kind, String mainCargoType, String cargoType,
                     ArrayList<Station> stations) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        setOriginId(stations);
        setDestinationId(stations);
        setOriginDistrict(stations);
        setDestinationDistrict(stations);
        this.operationWagon = volumeWagon;
        this.operationTon = volumeTon;
        this.planWagon = planWagon;
        this.planTon = planTon;
        this.wagonType = wagonType;
        this.transportKind = kind;
        this.mainCargoType = mainCargoType;
        this.cargoType = cargoType;
    }

    public Commodity() {

    }

    public double getHowMuchIsAllowed() {
        return howMuchIsAllowed;
    }

    public void setHowMuchIsAllowed(double howMuchIsAllowed) {
        this.howMuchIsAllowed = howMuchIsAllowed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getOriginId() {
        return originId;
    }

    public void setOriginId(ArrayList<Station> stations) {
        for (Station station : stations) {
            if (station.getName().equals(this.origin)) {
                this.originId = station.getId();
            }
        }
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(ArrayList<Station> stations) {
        for (Station station : stations) {
            if (station.getName().equals(this.destination)) {
                this.destinationId = station.getId();
            }
        }
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<Block> blocks) {
        this.blocks = blocks;
    }

    public double getOperationWagon() {
        return operationWagon;
    }

    public void setOperationWagon(double operationWagon) {
        this.operationWagon = operationWagon;
    }

    public double getOperationTon() {
        return operationTon;
    }

    public void setOperationTon(double operationTon) {
        this.operationTon = operationTon;
    }

    public double getPlanWagon() {
        return planWagon;
    }

    public void setPlanWagon(double planWagon) {
        this.planWagon = planWagon;
    }

    public double getPlanTon() {
        return planTon;
    }

    public void setPlanTon(double planTon) {
        this.planTon = planTon;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTonKilometerPlan() {
        return tonKilometerPlan;
    }

    public void setTonKilometerPlan(double tonKilometerPlan) {
        this.tonKilometerPlan = tonKilometerPlan;
    }

    public double getTonKilometerOperation() {
        return tonKilometerOperation;
    }

    public void setTonKilometerOperation(double tonKilometerOperation) {
        this.tonKilometerOperation = tonKilometerOperation;
    }

    public String getTransportKind() {
        return transportKind;
    }

    public void setTransportKind(String transportKind) {
        this.transportKind = transportKind;
    }

    public String getMainCargoType() {
        return mainCargoType;
    }

    public void setMainCargoType(String mainCargoType) {
        this.mainCargoType = mainCargoType;
    }

    public String getWagonType() {
        return wagonType;
    }

    public void setWagonType(String wagonType) {
        this.wagonType = wagonType;
    }

    public String getOriginDistrict() {
        return originDistrict;
    }

    public void setOriginDistrict(ArrayList<Station> stations) {
        this.originDistrict = "null";
        for (Station station : stations) {
            if (station.getName().equals(this.origin)) {
                this.originDistrict = station.getDistrict();
            }
        }
    }

    public String getDestinationDistrict() {
        return destinationDistrict;
    }

    public void setDestinationDistrict(ArrayList<Station> stations) {
        this.destinationDistrict = "null";
        for (Station station : stations) {
            if (station.getName().equals(this.destination)) {
                this.destinationDistrict = station.getDistrict();
            }
        }
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public boolean hasBlock(Block block) {
        for (Block block1 : blocks) {
            if (block1.equals(block)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Commodity{" +
                "id=" + id +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", district='" + originDistrict + '\'' +
                ", planTon='" + planTon + '\'' +
                ", tonKilometerPlan='" + tonKilometerPlan + '\'' +
                '}';
    }
}
