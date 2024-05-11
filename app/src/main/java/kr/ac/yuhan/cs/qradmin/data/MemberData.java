package kr.ac.yuhan.cs.qradmin.data;

import java.util.Date;

public class MemberData {
    // Member Data Field
    private int number;
    private String userId;
    private Date joinDate;
    private int point;

    // MemberData Constructor
    public MemberData(int number, String userId, Date joinDate, int point) {
        this.number = number;
        this.userId = userId;
        this.joinDate = joinDate;
        this.point = point;
    }

    // Getter & Setter
    public int getNumber() {
        return number;
    }

    public String getUserId() {
        return userId;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public int getPoint() {
        return point;
    }
}
