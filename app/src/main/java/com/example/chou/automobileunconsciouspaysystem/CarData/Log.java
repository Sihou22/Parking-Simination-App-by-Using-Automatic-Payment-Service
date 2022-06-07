package com.example.chou.automobileunconsciouspaysystem.CarData;

public class Log {
    private String recordid;
    private String text;
    private String _id;
    private String intime;
    private String outtime;
    private int parkid;
    private String parkname;
    private String carbrand;
    private String state;
    private int pay;

    public String getRecordid() {
        return recordid;
    }

    public void setRecordid(String recordid) {
        this.recordid = recordid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }

    public String getOuttime() {
        return outtime;
    }

    public void setOuttime(String outtime) {
        this.outtime = outtime;
    }

    public int getParkid() {
        return parkid;
    }

    public void setParkid(int parkid) {
        this.parkid = parkid;
    }

    public void set_id(String _id)
    {
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }

    public void setParkname(String parkname) {
        this.parkname = parkname;
    }

    public String getParkname() {
        return parkname;
    }

    public void setCarbrand(String carbrand) {
        this.carbrand = carbrand;
    }

    public String getCarbrand() {
        return carbrand;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPay() {return pay;}

    public void setPay(int pay) {this.pay = pay;}
}

