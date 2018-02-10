package com.example.vip.abdr_app;

import android.content.Context;
import android.content.SharedPreferences;

public class localuser {
    public static final String SP_NAME="userDetails";
    SharedPreferences userLocalDatabase;
    SharedPreferences serviceDatabase;
    public localuser(Context context){

        userLocalDatabase=context.getSharedPreferences(SP_NAME,0);
        serviceDatabase=context.getSharedPreferences(SP_NAME,0);
    }
    public localuser(){


    }
    public void storeData(passenger passenger){
        SharedPreferences.Editor spEditor=userLocalDatabase.edit();
        spEditor.putString("username" ,passenger.uname);
        spEditor.putString("password" ,passenger.pass);
        spEditor.putString("first_name" ,passenger.firstn);
        spEditor.putString("last_name" ,passenger.lastn);
        spEditor.putString("city" ,passenger.city);
        spEditor.putString("id_card_img" ,passenger.idImage);
        spEditor.putString("nationality" ,passenger.nationality);
        spEditor.putString("email" ,passenger.email);
        spEditor.putLong("phone" ,passenger.phone);
        spEditor.putInt("id_number" ,passenger.idNo);
        spEditor.putInt("P_ID" ,passenger.P_ID);
        spEditor.commit();

     }
    public passenger getLoggedInUser(){
        String uname =userLocalDatabase.getString("username"," ");
        String password =userLocalDatabase.getString("password"," ");
        String first_name =userLocalDatabase.getString("first_name"," ");
        String last_name =userLocalDatabase.getString("last_name"," ");
        String city =userLocalDatabase.getString("city"," ");
        String idimg =userLocalDatabase.getString("id_card_img","");
        String nationality =userLocalDatabase.getString("nationality"," ");
        String email =userLocalDatabase.getString("email"," ");
        int id =userLocalDatabase.getInt("P_ID",-1);
        long phone =userLocalDatabase.getLong("phone",-1);
        int id_number =userLocalDatabase.getInt("id_number",-1);
        passenger p=new passenger(first_name,last_name,city,email,nationality,phone,id_number,uname, password,id,idimg);
        return p;
        }
    public void setLoggedInUser(boolean loggedin){
       SharedPreferences.Editor spEditor=userLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn",loggedin);
        spEditor.commit();

    }
    public boolean getIfLoggedInUser(){
    if (userLocalDatabase.getBoolean("LoggedIn",false)){
        return true;
    }
        else{
        return false;
    }

    }
    public void clear(){
        SharedPreferences.Editor spEditor=userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();

    }
    public void setstat(String st,String br){
        SharedPreferences.Editor spEditor=serviceDatabase.edit();
        spEditor.putString("state"+br ,st);
        spEditor.putString("sbr"+br ,br);
        spEditor.commit();

    }
    public String getstat(String brf){

        String st =serviceDatabase.getString("state"+brf," ");
        String br =serviceDatabase.getString("sbr"+brf," ");
        return st+","+br;
    }

}
