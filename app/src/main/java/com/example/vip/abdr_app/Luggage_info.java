package com.example.vip.abdr_app;

public class Luggage_info {
    private int ln;
    private String color ;
    private String wight ;
    private String brand ;
    private String discription ;
    private boolean fragile ;
    private boolean wrap ;
    private boolean checked ;

    public Luggage_info(int ln,String co ,String wi,String br,String dis,boolean fr,boolean wr){
        this.ln=ln;
        this.color = co;
        this.brand=br;
        this.discription=dis;
        this.wight=wi;
        this.fragile=fr;
        this.wrap=wr;
    }

    public int getLn() {
        return ln;
    }

    public void setLn(int ln) {
        this.ln = ln;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWight() {
        return wight;
    }

    public void setWight(String wight) {
        this.wight = wight;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public boolean isFragile() {
        return fragile;
    }

    public void setFragile(boolean fragile) {
        this.fragile = fragile;
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }
}
