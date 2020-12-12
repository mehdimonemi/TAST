package company.Data;

import java.util.ArrayList;

/**
 * Created by Monemi_M on 01/10/2018.
 */
public class PathExceptions {
    ArrayList<String> origins = new ArrayList<>();
    ArrayList<String> destinations = new ArrayList<>();
    ArrayList<Block> blocksMustbe = new ArrayList<>();


    public ArrayList<String> getOrigins() {
        return origins;
    }

    public void setOrigins(ArrayList<String> origins) {
        this.origins = origins;
    }

    public ArrayList<String> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<String> destinations) {
        this.destinations = destinations;
    }

    public ArrayList<Block> getBlocksMustbe() {
        return blocksMustbe;
    }

    public void setBlocksMustbe(ArrayList<Block> blocksMustbe) {
        this.blocksMustbe = blocksMustbe;
    }

    public int isException(String origin, String destination){
        for(String a:origins){
            for (String b:destinations){
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
