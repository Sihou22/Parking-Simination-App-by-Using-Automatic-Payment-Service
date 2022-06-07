package com.example.chou.automobileunconsciouspaysystem.CarData;

public class Car {
    private String carid;
    private String _id;
    private String carbrand;
    private int owner;
    private boolean sets;

    public void setCarid(String carid)
    {
        this.carid = carid;
    }

    public String getCarid() {
        return carid;
    }

    public void set_id(String _id)
    {
        this._id = _id;
    }

    public String get_id() {
        return _id;
    }

    public void setCarband(String brand)
    {
        this.carbrand = carbrand;
    }

    public String getCarband() {
        return carbrand;
    }

    public void setOwner(int id)
    {
        this.owner=id;
    }

    public int getOwner() {
        return owner;
    }

}
