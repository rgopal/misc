/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.satellite;


import com.codename1.util.MathUtil;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 * @author rgopal
 */
public class Satellite extends Entity {
   
   private Antenna antenna;
   private Amplifier amplifier;
  
    protected double latitude;     // latitude
    protected double longitude;  // longitude
    protected double r;       // distance from center of Earth
    protected double h;       // altitude of satellite from sub-satellite point
    protected double a;       // semi major axis
    protected double v;       // velocity
    protected double R0;      // altitude
    protected Com.Orbit orbit;
  
  
    public Satellite () {
        amplifier = new Amplifier();
        antenna = new Antenna();
        antenna.setDiameter(2.4);
        amplifier.setPower(200);
        
        
    }
    // eventually calculate this from Antenna and amplifier
    public double getEIRP() {
        return 55;
    }
    public Satellite (String n) {
        super(n);
    }
    public Satellite (String n, String d, String s) {
        super(n,d,s);
    }

    public double maxCoverage() {
        double angle;
        angle = MathUtil.asin(Com.RE/(Com.RE+this.R0));
        return angle;
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

    /**
     * @return the r
     */
    public double getR() {
        return r;
    }

    /**
     * @param r the r to set
     */
    public void setR(double r) {
        this.r = r;
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

    /**
     * @return the h
     */
    public double getH() {
        return h;
    }

    /**
     * @param h the h to set
     */
    public void setH(double h) {
        this.h = h;
    }

    /**
     * @return the a
     */
    public double getA() {
        return a;
    }

    /**
     * @param a the a to set
     */
    public void setA(double a) {
        this.a = a;
    }

    /**
     * @return the v
     */
    public double getV() {
        return v;
    }

    /**
     * @param v the v to set
     */
    public void setV(double v) {
        this.v = v;
    }

    /**
     * @return the R0
     */
    public double getR0() {
        return R0;
    }

    /**
     * @param R0 the R0 to set
     */
    public void setR0(double R0) {
        this.R0 = R0;
    }

    public Antenna getAntenna() {
        return antenna;
    }
   
    
   


}
