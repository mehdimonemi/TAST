package company.Data;

import java.util.ArrayList;


/**
 * Created by Monemi_M on 10/07/2017.
 */
public class Commodity {

    public static int commodityCounter=1;

    double howMuchIsAllowed = 1;
    private int tag;
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

    private double Wagon;
    private double Ton;

    private double distance = 0;

    private double tonKilometer;
    private ArrayList<Block> blocks = new ArrayList<>();

    private int check = 0;

    public Commodity(String origin, String destination, double Wagon, double Ton, String wagonType,
                     String kind, String mainCargoType, String cargoType,
                     ArrayList<Station> stations) {
        this.tag =commodityCounter++;
        this.origin = origin;
        this.destination = destination;
        setOriginId(stations);
        setDestinationId(stations);
        setOriginDistrict(stations);
        setDestinationDistrict(stations);
        this.Wagon = Wagon;
        this.Ton = Ton;
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

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
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

    public double getWagon() {
        return Wagon;
    }

    public void setWagon(double wagon) {
        this.Wagon = wagon;
    }

    public double getTon() {
        return Ton;
    }

    public void setTon(double ton) {
        this.Ton = ton;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTonKilometer() {
        return tonKilometer;
    }

    public void setTonKilometer(double tonKilometer) {
        this.tonKilometer = tonKilometer;
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
        return "Commodity "+ tag +" {" + origin + "--" + destination + ", Ton='" + Ton + "}";
    }
}
