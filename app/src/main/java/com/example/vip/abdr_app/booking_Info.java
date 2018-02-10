package com.example.vip.abdr_app;

import java.io.Serializable;
import java.util.ArrayList;


public class booking_Info implements Serializable {
    int p_id;
    String from , to , flight_time , flight_date ,B_refernce,booking_time,booking_date,address,distance;
    String [][]luggage;
    ArrayList<String> selectedFamily;
    double price ,latitude,longitude ,dis_price ,lug_pric , w_price;
    int l_count;
    public booking_Info(String fr ,String to ,String ti ,String da ,String b_r ,int p_id ){
        this.from = fr;
        this.to = to;
        this.flight_time = ti;
        this.flight_date=da;
        this.B_refernce=b_r;
        this.p_id =p_id;
    }
    public void setp_id(int p_id){
        this.p_id = p_id;
    }
    public int getp_id(){
        return p_id;
    }
    //---------------------------------------------------------------------------
    public double getDis_price() {
        return dis_price;
    }
    public void setDis_price(double dis_price) {
        this.dis_price = dis_price;
    }
    //---------------------------------------------------------------------------
    public double getLug_pric() {
        return lug_pric;
    }
    public void setLug_pric(double lug_pric) {
        this.lug_pric = lug_pric;
    }
    //------------------------------------------------------------------------
    public double getW_price() {
        return w_price;
    }
    public void setW_price(double w_price) {
        this.w_price = w_price;
    }
    //--------------------------------------------------------------------------
    public void setSelectedFamily(ArrayList<String> selectedItems){

        this.selectedFamily= selectedItems;
    }
    public ArrayList<String> getSelectedFamily(){
        return selectedFamily;
    }
    //--------------------------------------------------------------------------
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    //--------------------------------------------------------------------------
    public void setLuggage(String [][] lug){
        this.luggage = lug;
    }
    public String [][] getLuggage (){
        return luggage;
    }
    //-----------------------------------------------------------------------------
    public void setL_count(int count ){
        this.l_count = count;
    }
    public int getL_count(){
        return l_count;
    }
    //-----------------------------------------------------------------------------
    public void setbooking_time(String ft){
        this.booking_time = ft;
    }
    public String getbooking_time(){
        return booking_time;
    }
    //-----------------------------------------------------------------------------
    public void setbooking_date(String fd){
        this.booking_date = fd;
    }
    public String getbooking_date(){
        return booking_date;
    }
    //-----------------------------------------------------------------------------
    public void setPrice(double pre ){
        this.price = pre;
    }
    public double getPrice(){
        return price;
    }
    //-----------------------------------------------------------------------------
    public void setLatitude(double latitude ){
        this.latitude = latitude;
    }
    public double getLatitude(){
        return latitude;
    }
    //-----------------------------------------------------------------------------
    public void setLongitude(double longitude ){
        this.longitude = longitude;
    }
    public double getLongitude(){
        return longitude;
    }
    //-----------------------------------------------------------------------------

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
    //---------------------------------------------------------------------
    public String getFrom(){
        return from;
    }
    public String getTo(){
        return to;
    }
    public String getflight_time(){
        return flight_time;
    }
    public String getflight_Date(){
        return flight_date;
    }
    public String getB_refernce(){
        return B_refernce;
    }




}
