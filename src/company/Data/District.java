package company.Data;

/**
 * Created by monemi_m on 03/28/2018.
 */
public class District {
    String name;
    int code;

    public District(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
