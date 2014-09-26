/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import com.codename1.util.MathUtil;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Satellite extends Entity {

    private Antenna antenna;
    private Amplifier amplifier;

    private double EIRP;       // they should be calculated
    private double gain;       // they should be calculated
    protected double latitude;     // latitude
    protected double longitude;  // longitude
    protected double r;       // distance from center of Earth
    protected double h;       // altitude of satellite from sub-satellite point
    protected double a;       // semi major axis
    protected double v;       // velocity
    protected double R0;      // altitude
    protected Com.Orbit orbit;

    public Satellite() {
        amplifier = new Amplifier();
        antenna = new Antenna();
        // gain should be calculated when diameter changed
        antenna.setDiameter(2.4);
        EIRP = 43;
        gain = 50;

        amplifier.setPower(200);
    }

    public static void initSatellites(String[][] satellites) {

        // satellites contains values from the file.  Allow selection of an
        // vector of Satellite objects with band as the key in a hashtable
        Hashtable<Com.Band, Vector<Satellite>> bandSatellite = new Hashtable<Com.Band, Vector<Satellite>>();

        // go through all bands
        for (Com.Band band : Com.bands) {

            // start at 1 since first line is heading (name, long, lat, eirp, gain, band
            for (int i = 1; i < satellites.length; i++) {

                // check the band from file and create entry in hash table
                if (satellites[i][5].equalsIgnoreCase(String.valueOf(band))
                        || satellites[i][5].equalsIgnoreCase("*")) {

                    // get band using its string version as key (* matches all)
                    Com.Band textBand = Com.bandHash.get(satellites[i][5]);
                    if (bandSatellite.get(textBand) == null) {
                        bandSatellite.put(textBand, new Vector());
                        satelliteFields(satellites[i], bandSatellite.get(textBand));

                    } else {
                        satelliteFields(satellites[i], bandSatellite.get(textBand));
                    }
                    bandSatellite.get(textBand).add(new Satellite(satellites[i][0]));

                }
                System.out.println(satellites[i][0]);
            }
        }
    }

    public static void satelliteFields(String[] fields, Vector<Satellite> vector) {
        // vector has already been created for a band, just add entries
        Satellite satellite = new Satellite();

        // fields are name, long, lat, eirp, gain, band
        satellite.setName(fields[0]);
        satellite.setLatitude(Double.parseDouble(fields[1]));
        satellite.setLongitude(Double.parseDouble(fields[2]));
        satellite.setEIRP(Double.parseDouble(fields[3]));
        satellite.setGain(Double.parseDouble(fields[4]));

        vector.add(satellite);

    }

    // eventually calculate this from Antenna and amplifier

    public double getEIRP() {
        return 55;
    }

    public Satellite(String n) {
        super(n);
    }

    public Satellite(String n, String d, String s) {
        super(n, d, s);
    }

    public double maxCoverage() {
        double angle;
        angle = MathUtil.asin(Com.RE / (Com.RE + this.R0));
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

    /**
     * @return the amplifier
     */
    public Amplifier getAmplifier() {
        return amplifier;
    }

    /**
     * @param amplifier the amplifier to set
     */
    public void setAmplifier(Amplifier amplifier) {
        this.amplifier = amplifier;
    }

    /**
     * @param antenna the antenna to set
     */
    public void setAntenna(Antenna antenna) {
        this.antenna = antenna;
    }

    /**
     * @param EIRP the EIRP to set
     */
    public void setEIRP(double EIRP) {
        this.EIRP = EIRP;
    }

    /**
     * @return the gain
     */
    public double getGain() {
        return gain;
    }

    /**
     * @param gain the gain to set
     */
    public void setGain(double gain) {
        this.gain = gain;
    }

}
