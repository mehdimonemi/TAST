package company.Data;

import java.util.ArrayList;

/**
 * Created by Monemi_M on 01/10/2018.
 */
public class PathExceptions {
    ArrayList<String> originDistricts = new ArrayList<>();
    ArrayList<String> destinationDistricts = new ArrayList<>();
    ArrayList<Block> blocksMustbe = new ArrayList<>();


    public ArrayList<String> getOriginDistricts() {
        return originDistricts;
    }

    public void setOriginDistricts(ArrayList<String> originDistricts) {
        this.originDistricts = originDistricts;
    }

    public ArrayList<String> getDestinationDistricts() {
        return destinationDistricts;
    }

    public void setDestinationDistricts(ArrayList<String> destinationDistricts) {
        this.destinationDistricts = destinationDistricts;
    }

    public ArrayList<Block> getBlocksMustbe() {
        return blocksMustbe;
    }

    public void setBlocksMustbe(ArrayList<Block> blocksMustbe) {
        this.blocksMustbe = blocksMustbe;
    }

    public int isException(String origin, String destination){
        for(String a: originDistricts){
            for (String b: destinationDistricts){
                if(a.equals(origin) && b.equals((destination))){
                    return 1;
                }
                if (a.equals(destination) && b.equals((origin))){
                    return 2;
                }
            }
        }
        return -1;
    }
}
