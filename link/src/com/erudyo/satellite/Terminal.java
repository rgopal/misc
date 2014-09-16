/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.satellite;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 * @author rgopal
 */
public class Terminal extends Entity {
    private double longitude;     //longitude
    private double latitude;       //latitude
            
    public Terminal () {
        
    }
 
    public Terminal (String n) {
        super(n);
    }
    public Terminal (String n, String d, String s) {
        super(n,d,s);
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

   
    public void setLongitude(double d, double m, double s) {
        this.longitude = Com.toRadian(d,m,s);
    }
    
    public void setLatitude(double d, double m, double s) {
        this.latitude = Com.toRadian(d,m,s);
    }
    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
