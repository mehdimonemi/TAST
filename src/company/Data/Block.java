package company.Data;

import java.util.ArrayList;

/**
 * Created by Monemi_M on 10/07/2017.
 */
public class Block {
    private int id;
    private String origin;
    private String destination;
    private int originId;
    private int destinationId;

    private double demandWentPlanTon;
    private double demandWentOperationTon;
    private double demandWentPlanWagon;
    private double demandWentOperatoinWagon;
    private double demandBackPlanTon;
    private double demandBackOperationTon;
    private double demandBackPlanWagon;
    private double demandBackOperationWagon;

    private double demandWentPlanTonKilometer;
    private double demandBackPlanTonKilometer;
    private double AverageMovingDistance;

    private double length;
    private double capacity;
    private int track;
    private int direction;
    private String district;


    public Block(int id,String district, String origin, String destination, double length, int track, int direction,
                 double capacity, ArrayList<Station> stations) {
        this.id = id;
        this.district = district;
        this.origin = origin;
        this.destination = destination;
        setOriginId(stations);
        setDestinationId(stations);
        this.length = length;
        this.track = track;
        this.direction = direction;
        this.capacity = capacity;
    }

    public Block(String origin, String destination, ArrayList<Station> stations) {
        this.origin = origin;
        this.destination = destination;
        setOriginId(stations);
        setDestinationId(stations);
    }

    public Block(String origin, String destination, double length) {
        this.origin = origin;
        this.destination = destination;
        this.length = length;
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
        for(Station station: stations){
            if(station.getName().equals(this.origin)){
                this.originId = station.getId();
            }
        }
    }

    public int getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(ArrayList<Station> stations) {
        for(Station station: stations){
            if(station.getName().equals(this.destination)){
                this.destinationId = station.getId();
            }
        }
    }

    public double getDemandWentPlanTon() {
        return demandWentPlanTon;
    }

    public void setDemandWentPlanTon(double demandWentPlanTon) {
        this.demandWentPlanTon = demandWentPlanTon;
    }

    public double getDemandBackPlanTon() {
        return demandBackPlanTon;
    }

    public void setDemandBackPlanTon(double demandBackPlanTon) {
        this.demandBackPlanTon = demandBackPlanTon;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getDemandWentOperationTon() {
        return demandWentOperationTon;
    }

    public void setDemandWentOperationTon(double demandWentOperationTon) {
        this.demandWentOperationTon = demandWentOperationTon;
    }

    public double getDemandBackOperationTon() {
        return demandBackOperationTon;
    }

    public void setDemandBackOperationTon(double demandBackOperationTon) {
        this.demandBackOperationTon = demandBackOperationTon;
    }

    public double getDemandWentPlanWagon() {
        return demandWentPlanWagon;
    }

    public void setDemandWentPlanWagon(double demandWentPlanWagon) {
        this.demandWentPlanWagon = demandWentPlanWagon;
    }

    public double getDemandBackPlanWagon() {
        return demandBackPlanWagon;
    }

    public void setDemandBackPlanWagon(double demandBackPlanWagon) {
        this.demandBackPlanWagon = demandBackPlanWagon;
    }

    public double getDemandWentOperatoinWagon() {
        return demandWentOperatoinWagon;
    }

    public void setDemandWentOperatoinWagon(double demandWentOperatoinWagon) {
        this.demandWentOperatoinWagon = demandWentOperatoinWagon;
    }

    public double getDemandBackOperationWagon() {
        return demandBackOperationWagon;
    }

    public void setDemandBackOperationWagon(double demandBackOperationWagon) {
        this.demandBackOperationWagon = demandBackOperationWagon;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getDemandWentPlanTonKilometer() {
        return demandWentPlanTonKilometer;
    }

    public void setDemandWentPlanTonKilometer(double demandWentPlanTonKilometer) {
        this.demandWentPlanTonKilometer = demandWentPlanTonKilometer;
    }

    public double getDemandBackPlanTonKilometer() {
        return demandBackPlanTonKilometer;
    }

    public void setDemandBackPlanTonKilometer(double demandBackPlanTonKilometer) {
        this.demandBackPlanTonKilometer = demandBackPlanTonKilometer;
    }

    public double getAverageMovingDistance() {
        return AverageMovingDistance;
    }

    public void setAverageMovingDistance(double averageMovingDistance) {
        AverageMovingDistance = averageMovingDistance;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Block{" +
                "origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}