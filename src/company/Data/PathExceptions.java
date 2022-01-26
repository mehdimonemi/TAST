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

    public int isException(String originD, String destinationD, String a, String b) {
        int result = -1;
        for (String district1 : originDistricts) {
            for (String district2 : destinationDistricts) {
                if (district1.equals(originD) && district2.equals((destinationD))) {
                    result = 1;
                }
                if (district1.equals(destinationD) && district2.equals((originD))) {
                    result = 2;
                }
            }
        }
        for (Block block : blocksMustbe) {
            if (a.equals(block.getOrigin()) || a.equals(block.getDestination()) ||
                    b.equals(block.getOrigin()) || b.equals(block.getDestination())) {
                result = -1;
            }
        }
        return result;
    }
}