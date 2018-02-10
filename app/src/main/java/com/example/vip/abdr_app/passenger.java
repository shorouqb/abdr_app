package com.example.vip.abdr_app;

public class passenger {
    String uname,pass,firstn,lastn,city,nationality,email,idImage;
    int idNo,P_ID;
    long phone;

    public  passenger(String fname,String lname,String city,String email,String nationality,
                      long phone,int idNo,String name,String pass,int P_ID,String idimg){
        this.idNo=idNo;
        this.phone=phone;
        this.firstn=fname;
        this.lastn=lname;
        this.city=city;
        this.nationality=nationality;
        this.email=email;
        this.idImage=idimg;
        this.uname=name;
        this.pass=pass;
        this.P_ID=P_ID;
    }
   }