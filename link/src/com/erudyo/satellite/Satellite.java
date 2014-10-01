/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import com.codename1.util.MathUtil;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Random;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Satellite extends Entity {

    private Antenna antenna;
    private Amplifier amplifier;

    // all satellites stored in a Class level hash.  For future, since
    // selection has its own at instance level
    static Hashtable<String, Satellite> satelliteHash
            = new Hashtable<String, Satellite>();

    // lookup by index with class level vector
    final public static ArrayList<Satellite> indexSatellite
            = new ArrayList<Satellite>();

    public String toString() {
        return getName();
    }
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
    protected RfBand.Band band; //
   

    private int index;
    
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public void setBand(RfBand.Band band) {
        this.band = band;
    }

    public RfBand.Band getBand() {
        return band;
    }

    public Satellite() {
        amplifier = new Amplifier();
        antenna = new Antenna();
        // gain should be calculated when diameter changed
        antenna.setDiameter(2.4);
        EIRP = 43;
        gain = 50;

        amplifier.setPower(200);

        if (getName() == null) {
            Random randomGenerator = new Random();

            setName("S" + String.valueOf(randomGenerator.nextInt(10000)));
        }

        // add the new Satellite instance to the Hashtable at the class level
        Satellite.satelliteHash.put(getName(), this);
        

        // Add new object instance to the array list (all satellites)
        indexSatellite.add(this);
        
        index = indexSatellite.size()-1;
   
     
    }

    public static Hashtable<RfBand.Band, ArrayList<Satellite>> getFromFile(String[][] satellites) {

        // satellites contains values from the file.  Allow selection of an
        // vector of Satellite objects with band as the key in a hashtable
        Hashtable<RfBand.Band, ArrayList<Satellite>> bandSatellite
                = new Hashtable<RfBand.Band, ArrayList<Satellite>>();

        // go through all bands
        for (int i = 1; i < satellites.length; i++) {
            // get the band first
            RfBand.Band band = RfBand.rFbandHash.get(satellites[i][5]).getBand();

            // need to key on a correct band
            if (band == null) {
                System.out.println("Bad Data: " + satellites[i].toString());
            } else {
                // extract the band of the terminal
                if (bandSatellite.get(band) == null) {
                    bandSatellite.put(band, new ArrayList<Satellite>());
                }

                // get band using its string version as key (* matches all)
                satelliteFields(satellites[i], bandSatellite.get(band));

                // check the band from file and create entry in hash table
                System.out.println("Processed: " + satellites[i][0]);
            }
        }
        return bandSatellite;
    }

    public static void satelliteFields(String[] fields, ArrayList<Satellite> vector) {
        // vector has already been created for a band, just add entries
        Satellite satellite = new Satellite();

        // fields are name, long, lat, eirp, gain, band
        satellite.setName(fields[0]);
        satellite.setLatitude(Math.toRadians(Double.parseDouble(fields[1])));
        satellite.setLongitude(Math.toRadians(Double.parseDouble(fields[2])));
        satellite.setEIRP(Double.parseDouble(fields[3]));
        satellite.setGain(Double.parseDouble(fields[4]));
        if (!fields[5].equalsIgnoreCase("*")) {
            satellite.setBand(RfBand.rFbandHash.get(fields[5]).getBand());
            vector.add(satellite);

        }
        // eventually calculate this from Antenna and amplifier
    }

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
