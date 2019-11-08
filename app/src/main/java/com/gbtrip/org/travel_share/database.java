package com.gbtrip.org.travel_share;
public class database {
    String username;
    String userage;
    String usercity;
    String useremail;
    String userphone;
    public database(){

    }
    public database(String username, String userage, String usercity, String useremail, String userphone) {
        this.username = username;
        this.userage = userage;
        this.usercity = usercity;
        this.useremail = useremail;
        this.userphone = userphone;
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

    public String getUsercity() {
        return usercity;
    }

    public void setUsercity(String usercity) {
        this.usercity = usercity;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }
}

