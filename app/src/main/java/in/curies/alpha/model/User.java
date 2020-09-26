package in.curies.alpha.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;

@IgnoreExtraProperties
public class User implements Serializable {

    private String username;
    private String email;
    private String course;
    private boolean fees;
    private String phoneNumber;
    private String couponCode;
    private Date createdDate;
    private Date enrollmentDate;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username,
                String email,
                String phoneNumber,
                String course,
                boolean fees,
                String couponCode,
                Date createdDate,
                Date enrollmentDate) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.course = course;
        this.fees = fees;
        this.couponCode = couponCode;
        this.createdDate = createdDate;
        this.enrollmentDate = enrollmentDate;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }

    public boolean isFees() {
        return fees;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }
}