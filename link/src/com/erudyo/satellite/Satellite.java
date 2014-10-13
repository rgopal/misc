/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import java.util.Arrays;

import com.codename1.io.Log;
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

    // can have multiple bands and transponders;
    private Hashtable<RfBand.Band, Integer> transponders;

    // all satellites stored in semiMajor Class level hash.  For future, since
    // selection has its own at instance level
    static Hashtable<String, Satellite> satelliteHash
            = new Hashtable<String, Satellite>();

    // lookup by index with class level vector
    final public static ArrayList<Satellite> indexSatellite
            = new ArrayList<Satellite>();

    public Satellite() {
    }

    // return the number of transponders for a specific band
    public int getNumberTransponders(RfBand.Band band) {
        int num = 0;
        if (transponders == null) {
            Log.p("Satellite: transponders is null", Log.WARNING);
        }
        if (transponders.get(band) != null) {
            num = transponders.get(band);
        }
        return num;
    }

    public String toString() {
        return getName();
    }
    private double EIRP;       // they should be calculated
    private double gainTemp;       // they should be calculated dB 1/K
    protected double latitude;     // latitude
    protected double longitude;  // longitude
    protected double distanceEarthCenter;       // distance from center of Earth
    protected double h;       // altitude of satellite from sub-satellite point
    private double semiMajor;       // semi major axis
    protected double velocity;       // velocity
    protected double altitude;      // altitude
    protected Com.Orbit orbit;
    // protected RfBand.Band band; // now multiple bands in transponders

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

    public Satellite(String name) {
        amplifier = new Amplifier();
        antenna = new Antenna();
        // gainTemp should be calculated when diameter changed
        antenna.setDiameter(2.4);

        this.name = name;       // should be unique

        setSemiMajor(42164.2E3);        //semi major axis of GEO orbit
        setVelocity(3075E3);        // GEO satellite velocity
        setAltitude(35786.1E3);       // height of GEO satellite
        setLatitude(0.0);         // latitude is zero

        orbit = Com.Orbit.GEO;

        amplifier.setPower(200);

        if (getName() == null) {
            Random randomGenerator = new Random();

            setName("S" + String.valueOf(randomGenerator.nextInt(10000)));
        }

        // add the new Satellite instance to the Hashtable at the class level
        Satellite.satelliteHash.put(getName(), this);

        // Add new object instance to the array list (all satellites)
        indexSatellite.add(this);

        index = indexSatellite.size() - 1;

    }

    public static void processBand(String[] fields, RfBand.Band band,
            Hashtable<RfBand.Band, ArrayList<Satellite>> bandSatellite, 
            Satellite satellite, int index) {

        if (band == null) {
            Log.p("Satellite: bad data " + Arrays.toString(fields), Log.WARNING);
        } else {

            // extract the band of the terminal
            if (bandSatellite.get(band) == null) {
                bandSatellite.put(band, new ArrayList<Satellite>());
            }

            int num = 0;
            try {
                num = Integer.parseInt(fields[index + 1]);
            } catch (Exception e) {
                Log.p("Satellite: no number of transponders for satellite "
                        + Arrays.toString(fields), Log.DEBUG);
            }
            // put the number of transponders for this band
            satellite.transponders.put(band, num);
            
            // add satellite to this band
            bandSatellite.get(band).add(satellite);

        }

    }

    public static Hashtable<RfBand.Band, ArrayList<Satellite>> getFromFile(String[][] satellites) {

        // satellites contains values from the file.  Allow selection of an
        // vector of Satellite objects with band as the key in semiMajor hashtable
        Hashtable<RfBand.Band, ArrayList<Satellite>> bandSatellite
                = new Hashtable<RfBand.Band, ArrayList<Satellite>>();

        // go through all satellites
        for (int i = 1; i < satellites.length; i++) {

            Log.p("Satellite: processing satellite "
                    + Arrays.toString(satellites[i])
                    + Log.INFO);

            satelliteFields(satellites[i], bandSatellite);
        }
        return bandSatellite;
    }

    public static void satelliteFields(String[] fields, Hashtable<RfBand.Band, ArrayList<Satellite>> bandSatellite) {
//            ArrayList<Satellite> vector) {
//Name 1|Name of Satellite, Alternate Names 2|Country of Operator/Owner 3|Operator
//Owner 4|Users 5|Purpose 6|Class of Orbit 7|Type of Orbit 8|Longitude of GEO (de
//grees) 9|Perigee (km) 10|Apogee (km) 11|Eccentricity 12|Inclination (degrees) 13
//|Period (minutes) 14|Launch Mass (kg.) 15|Dry Mass (kg.) 16|Power (watts) 17|Dat
//e of Launch 18|Expected Lifetime 19|Contractor 20|Country of Contractor 21|Launc
//h Site 22|Launch Vehicle 23|COSPAR Number 24|NORAD Number 25|Comments 26| 27|Sou
// rce Used for Orbital Data 28|Source1 29|Source2 30|Source3 31|Source4 32|Source5
// 33|Source6 34|EIRP 35|Gain 36|BAND1 37|Transponders1 38|BAND2 39|Transponders2 
// 40|BAND3 41|Transponders3 42

        // vector has already been created for semiMajor band, just add entries
        Satellite satellite = new Satellite(fields[0]);

        // fields are name, long, lat, eirp, gainTemp, band
        try {
        satellite.setLongitude(Math.toRadians(Double.parseDouble(fields[8])));
        
        // select only GEO satellites
        satellite.setLatitude(0.0);

        satellite.setEIRP(Double.parseDouble(fields[34]));
        satellite.setGainTemp(Double.parseDouble(fields[35]));

        } catch (Exception e) {
            Log.p("Satellites: double error in Long,Lat,EIRP, or Gain " +
                    fields, Log.WARNING);   
        }
        if (satellite.transponders == null) {
            satellite.transponders = new Hashtable<RfBand.Band, Integer>();
        }

        // put the band and number of transponders
        // process C, KU, and KA (they are in the order in all_satellites.txt
        
        if (!(fields[36].equals("")) && RfBand.rFbandHash.get(
                fields[36].toUpperCase()).getBand()
                == RfBand.Band.C) {

            processBand(fields, RfBand.Band.C, bandSatellite, satellite, 36);
        }
        if (!(fields[38].equals("")) && RfBand.rFbandHash.get(
                fields[38].toUpperCase()).getBand()
                == RfBand.Band.KU) {
            processBand(fields, RfBand.Band.KU, bandSatellite, satellite, 38);
        }
        if (!(fields[40].equals("")) && RfBand.rFbandHash.get(
                fields[40].toUpperCase()).getBand()
                == RfBand.Band.KU) {
            processBand(fields, RfBand.Band.KA, bandSatellite, satellite, 40);
        }

    }

    public double getEIRP() {
        return 55;
    }

    public double maxCoverage() {
        double angle;
        angle = MathUtil.asin(Com.RE / (Com.RE + this.altitude));
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
     * @return the distanceEarthCenter
     */
    public double getDistanceEarthCenter() {
        return distanceEarthCenter;
    }

    /**
     * @param r the distanceEarthCenter to set
     */
    public void setDistanceEarthCenter(double r) {
        this.distanceEarthCenter = r;
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
     * @return the semiMajor
     */
    public double getSemiMajor() {
        return semiMajor;
    }

    /**
     * @return the velocity
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * @param v the velocity to set
     */
    public void setVelocity(double v) {
        this.velocity = v;
    }

    /**
     * @return the altitude
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * @param R0 the altitude to set
     */
    public void setAltitude(double R0) {
        this.altitude = R0;
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
        updateAffected();
    }

    /**
     * @param antenna the antenna to set
     */
    public void setAntenna(Antenna antenna) {
        this.antenna = antenna;
        updateAffected();
    }

    /**
     * @param EIRP the EIRP to set
     */
    public void setEIRP(double EIRP) {
        this.EIRP = EIRP;
        updateAffected();
    }

    /**
     * @return the gainTemp
     */
    public double getGainTemp() {
        return gainTemp;
    }

    /**
     * @param gain the gainTemp to set
     */
    public void setGainTemp(double gain) {
        this.gainTemp = gain;
        updateAffected();
    }

    /**
     * @param semiMajor the semiMajor to set
     */
    public void setSemiMajor(double semiMajor) {
        this.semiMajor = semiMajor;
    }

}
