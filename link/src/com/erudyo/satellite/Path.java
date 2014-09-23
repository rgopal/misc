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
 *
 * @author rgopal ISO Standard is Long and then LAT, North and East are
 * positive. Fine to use all decimal after degree.
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

    /**
     * @return the band
     */
    public Com.Band getBand() {
        return band;
    }

    /**
     * @param band the band to set
     */
    public void setBand(Com.Band band) {
        this.band = band;
    }

    enum relativePosition {

        NE, SE, NW, SW, N, S, E, W
    };
    private Satellite s;
    private Com.Band band;
    private Terminal t;
    private double azimuth;
    private double elevation;
    private double R;     //distance of satellite from terminal

    public Path() {

    }

    public Path(Satellite s, Terminal t) {
        this.s = s;
        this.t = t;
        R = calcDistance();
        azimuth = calcAzimuth();
        elevation = calcElevation();
    }

    public Path(String n) {
        super(n);
    }

    public Path(String n, String d, String s) {
        super(n, d, s);
    }

    public double calcRelativeLong() {

        double relativeLong;
        // get relative longitdue between satellite and terminal
        if (s.longitude > t.getLongitude()) {
            relativeLong = s.getLongitude() - t.getLongitude();
        } else {
            relativeLong = t.getLongitude() - s.getLongitude();
        }
        // return absolute value of difference.  Above is not needed
        return Math.abs(relativeLong);
    }

    public double calcBigPhi() {
        if (s == null || t == null) {
            return 0;
        }
        double Phi;
        double relativeLong;
        relativeLong = calcRelativeLong();
        Phi = MathUtil.acos(Math.cos(relativeLong)
                * Math.cos(Math.abs(t.getLatitude())));
        return Phi;
    }

    public double calcDistance() {
        double d;
        d = MathUtil.pow((1.0 + 0.42 * (1.0 - Math.cos(calcBigPhi()))), 0.5) * s.getR0();
        return d;
    }

    public double calcElevation() {

        double elev;
        double bigPhi;

        bigPhi = calcBigPhi();
        // if bigPhi is zero then you have to look straight up
        if (Com.sameValue(bigPhi, 0.0)) {
            return (Com.PI / 2.0);
        }
        elev = MathUtil.atan( (Math.cos(bigPhi) - (Com.RE / (Com.RE + s.getR0())))
                    / MathUtil.pow((1.0 - Math.cos(bigPhi) * Math.cos(bigPhi)), 0.5));
        return elev;

    }

    public double calcAzimuth() {
        double azimuth = 0.0;
        double a;
        relativePosition rel;
        double relLong, bigPhi;
        relLong = calcRelativeLong();
        bigPhi = calcBigPhi();

        // if terminal and satellite are on the same longitude
        // then terminal is either looking south (in northern hemi)
        // or north (in southern hemisphere).  Phi may not be zero.
        // If Phi is zero then relLong is also zero?
        if (Com.sameValue(relLong, 0.0) || Com.sameValue(bigPhi, 0.0)) {
            return (Com.PI);
        }

        a = MathUtil.asin(Math.sin(relLong) * Math.cos(s.getLatitude())
                / Math.sin(bigPhi));
        rel = findRelativePosition();
        switch (rel) {
            case NE:
                azimuth = Com.PI - a;
                break;
            case NW:
                azimuth = Com.PI + a;
                break;
            case SE:
                azimuth = a;
                break;
            case SW:
                azimuth = Com.PI * 2.0 - a;
                break;
        }
        return azimuth;
    }

    public relativePosition findRelativePosition() {
        // terminal in northern hemisphere
        relativePosition rel;
        rel = relativePosition.NE;      // Terminal is North East

        // northern hemisphere for terminal
        if (t.getLatitude() >= 0.0) {
            // satellite is East of Terminal
            if (s.getLongitude() > t.getLongitude()) {
                rel = relativePosition.NE;
            } else {
                rel = relativePosition.NW;
            }
        } else {
            // terminal in southern hemisphere
            if (s.getLongitude() >= t.getLongitude()) {
                // satellite is East of terminal
                rel = relativePosition.SE;
            } else {
                rel = relativePosition.SW;
            }
        }

        return rel;
    }
}
