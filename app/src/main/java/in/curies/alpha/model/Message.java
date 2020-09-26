package in.curies.alpha.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@IgnoreExtraProperties
public class Message {

    private String message;
    private String from;
    private String time;

    public Message() {
    }

    public Message(String message, String from, Date time) {
        this.message = message;
        this.from = from;
        String pattern = "dd/MM/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        this.time = df.format(time);
    }

    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }

    public String getTime() {
        return time;
    }
}
