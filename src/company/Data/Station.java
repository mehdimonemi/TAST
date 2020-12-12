package company.Data;

import java.util.ArrayList;

/**
 * Created by Monemi_M on 10/07/2017.
 */
public class Station {
    private int id;
    private String name;
    private String district;
    private ArrayList <String> alterNames = new ArrayList<>();

    public Station(int id, String name, String district) {
        this.name = name;
        this.district = district;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public ArrayList<String> getAlterNames() {
        return alterNames;
    }

    public void setAlterNames(ArrayList<String> alterNames) {
        this.alterNames = alterNames;
    }
}
