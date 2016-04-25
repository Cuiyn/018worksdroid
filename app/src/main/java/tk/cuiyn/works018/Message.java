package tk.cuiyn.works018;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cuiyn on 16-3-12.
 */
public class Message {
    int pk;

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    Map<String, String> fields = new HashMap<>();

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    String subjectName;

    public int getPk() {
        return pk;
    }

    public String getText() {
        return fields.get("text");
    }

    public String getDate() {
        return fields.get("date");
    }

    public int getSubject() {
        return Integer.valueOf(fields.get("subject")).intValue();
    }
}
