/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.satellite;

import com.codename1.util.MathUtil;
import java.lang.Math;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 * @author rgopal
 * ISO Standard is Long and then LAT,  North and East are positive.  Fine to use
 * all decimal after degree.
 */
public class Path extends Entity {

    /**
     * @return the s
     */
    public Satellite getS() {
        return s;
    }

    /**
     * @param s the s to set
     */
    public void setS(Satellite s) {
        this.s = s;
    }

    /**
     * @return the t
     */
    public Terminal getT() {
        return t;
    }

    /**
     * @param t the t to set
     */
    public void setT(Terminal t) {
        this.t = t;
    }

    /**
     * @return the R
     */
    public double getR() {
        return R;
    }

    /**
     * @param R the R to set
     */
    public void setR(double R) {
        this.R = R;
    }

    /**
     * @return the azimuth
     */
    public double getAzimuth() {
        return azimuth;
    }

    /**
     * @param azimuth the azimuth to set
     */
    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    /**
     * @return the elevation
     */
    public double getElevation() {
        return elevation;
    }

    /**
     * @param elevation the elevation to set
     */
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }
    enum relativePosition {NE, SE, NW, SW};
    private Satellite s;
    private Terminal t;
    private double azimuth;
    private double elevation;
    private double R;     //distance of satellite from terminal
    
    public Path () {
        
    }
    public Path (Satellite s, Terminal t) {
        this.s = s;
        this.t = t;
        R = calcDistance();
        azimuth = calcAzimuth();
        elevation = calcElevation();
    }
    public Path (String n) {
        super(n);
    }
    public Path (String n, String d, String s) {
        super(n,d,s);
    }

    public double calcRelativeLong () {
        double relativeLong;
               // get relative longitdue between satellite and terminal
        if (s.longitude > t.getLongitude())
            relativeLong = s.getLongitude() - t.getLongitude();
        else
            relativeLong = t.getLongitude() - s.getLongitude() ;
        return relativeLong;
    }
    public double calcBigPhi() {
        if (s == null || t == null)
            return 0;
        double Phi;
        double relativeLong;
        relativeLong = calcRelativeLong();
        Phi = MathUtil.acos(Math.cos(relativeLong)*Math.cos(t.getLatitude()));
        return Phi;
    }
    
    public double calcDistance() {
        double d;
        d = MathUtil.pow((1.0 + 0.42*(1.0 - Math.cos(calcBigPhi()))),0.5)*s.getR0();
        return d;
    }
    public double calcElevation() {
       
        double num;
        double denom;
        num = MathUtil.atan(Math.cos(calcBigPhi()));
        denom = MathUtil.pow(1.0 - Math.cos(calcBigPhi()), 0.5);
        return num/denom;
        
    }
    public double calcAzimuth() {
        double azimuth = 0.0;
        double a;
        relativePosition rel;
        a = MathUtil.asin (Math.sin(calcRelativeLong())*Math.cos(s.getLatitude())/
                            Math.sin(calcBigPhi()));
        rel = findRelativePosition();
        switch (rel) {
            case SE:
                azimuth = 180 - a;
            case NE:
                azimuth = a;
            case SW:
                azimuth = 180+a;
            case NW:
                azimuth = 360-a;
        }    
   
        return azimuth;
    }
   public relativePosition findRelativePosition() {
       // return position of sub-satellite point with respect to P (of terminal)
       // In ISO notation North is positive and East is positive
       relativePosition rel;
       
       // first latitude
       if (getS().getLatitude() > getT().getLatitude()  ) {
           // satellite is north of terminal, now check for longitude
           if (getS().getLongitude() > getT().getLongitude()) {
               // satellite is East of Terminal
               rel = relativePosition.NE;
           } else {
               rel = relativePosition.NW; 
           }
       } else {
           // check for longitude now
            if (getS().getLongitude() > getT().getLongitude()) {
               // satellite is East of Terminal
               rel = relativePosition.SE;
           } else {
               rel = relativePosition.SW; 
           }
       }
           
       return rel;
   } 
}
