package com.gbtrip.org.travel_share;

public class conditondata {
    String username;
    String userage;
    String usergender;
    public conditondata(){

    }

    public conditondata(String username, String userage, String usergender) {
        this.username = username;
        this.userage = userage;
        this.usergender = usergender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserage() {
        return userage;
    }

    public void setUserage(String userage) {
        this.userage = userage;
    }

    public String getUsergender() {
        return usergender;
    }

    public void setUsergender(String usergender) {
        this.usergender = usergender;
    }
}
