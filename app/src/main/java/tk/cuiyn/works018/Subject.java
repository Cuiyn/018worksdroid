package tk.cuiyn.works018;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cuiyn on 16-3-12.
 */
public class Subject {
    int pk;
    Map<String, String> fields = new HashMap<>();

    public int getPk() {
        return pk;
    }

    public String getName() {
        return fields.get("name");
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}
