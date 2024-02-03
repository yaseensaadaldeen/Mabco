package com.example.mabco;


import org.json.JSONArray;

public class SingletonSession {
    private static SingletonSession instance;


    //no outer class can initialize this class's object
    private SingletonSession() {}

    public static SingletonSession Instance()
    {
        //if no instance is initialized yet then create new instance
        //else return stored instance
        if (instance == null)
        {
            instance = new SingletonSession();
        }
        return instance;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public  int position= -1;

    public int getPositionslider() {
        return positionslider;
    }

    public void setPositionslider(int positionslider) {
        this.positionslider = positionslider;
    }

    public  int positionslider= -1;


    public JSONArray getJsnobject() {
        return jsnobject;
    }

    public void setJsnobject(JSONArray jsnobject) {
        this.jsnobject = jsnobject;
    }

    public  JSONArray jsnobject;


    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float discount=-1;


}
