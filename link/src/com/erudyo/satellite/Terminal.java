/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.maps.Coord;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;
import java.util.ArrayList;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Terminal extends Entity {

    private double longitude;     //longitude
    private double latitude;       //latitude
    private RfBand.Band band;
    private Antenna antenna;
    private Amplifier amplifier;
    // eventually calculate these things
    private double EIRP;
    private double gain;

    // all terminals stored in a Class level hash
    static Hashtable<String, Terminal> terminals
            = new Hashtable<String, Terminal>();

    public void setBand(RfBand.Band band) {
        this.band = band;
    }

    // read the terminals.txt file and return all data as a Hashtable, one ArrayList
    // of terminals for each band.
    public static Hashtable<RfBand.Band, ArrayList<Terminal>> getFromFile(String[][] terminals) {

        // terminals contains values from the file.  Allow selection of an
        // vector of Satellite objects with band as the key in a hashtable
        Hashtable<RfBand.Band, ArrayList<Terminal>> bandTerminal
                = new Hashtable<RfBand.Band, ArrayList<Terminal>>();

        // start at 1 since first line is heading (name, long, lat, eirp, gain, band
        for (int i = 1; i < terminals.length; i++) {
            // get the band first
            RfBand.Band band = RfBand.rFbandHash.get(terminals[i][5]).getBand();

            // need to key on a correct band
            if (band == null) {
                System.out.println("Bad band: " + terminals[i].toString());

            } else {
                // extract the band of the terminal
                if (bandTerminal.get(band) == null) {
                    bandTerminal.put(band, new ArrayList<Terminal>());
                }

                // get band using its string version as key (* matches all)
                terminalFields(terminals[i], bandTerminal.get(band));

                // check the band from file and create entry in hash table
                System.out.println("Processed " + terminals[i][0]);
            }
        }

        return bandTerminal;
    }

    public static void terminalFields(String[] fields, ArrayList<Terminal> vector) {
        // vector has already been created for a band, just add entries
        Terminal terminal = new Terminal();

        // terminals in format name, latitude, longitude, antenna size, amplifier
        // gain, and band
        terminal.setName(fields[0]);
        terminal.setLatitude(Math.toRadians(Double.parseDouble(fields[1])));
        terminal.setLongitude(Math.toRadians(Double.parseDouble(fields[2])));
        terminal.getAntenna().setDiameter(Double.parseDouble(fields[3]));
        terminal.getAmplifier().setPower(Double.parseDouble(fields[4]));

        terminal.setBand(RfBand.rFbandHash.get(fields[5]).getBand());

        vector.add(terminal);

    }

    public RfBand.Band getBand() {
        return band;
    }

    public Terminal() {
        antenna = new Antenna();
        antenna.setDiameter(1);
        amplifier = new Amplifier();
        amplifier.setPower(10);
        // get current location

        try {
            Location loc = LocationManager.getLocationManager().getCurrentLocation();
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (getName() == null) {
            Random randomGenerator = new Random();

            setName("T" + String.valueOf(randomGenerator.nextInt(10000)));
        }

        // add the new Satellite instance to the Hashtable at the class level
        Terminal.terminals.put(getName(), this);
    }

    public Terminal(String n) {
        super(n);
    }

    public Terminal(String n, String d, String s) {
        super(n, d, s);
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
        this.longitude = Com.toRadian(d, m, s);
    }

    public void setLatitude(double d, double m, double s) {
        this.latitude = Com.toRadian(d, m, s);
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
     * @return the antenna
     */
    public Antenna getAntenna() {
        return antenna;
    }

    /**
     * @param antenna the antenna to set
     */
    public void setAntenna(Antenna antenna) {
        this.antenna = antenna;
    }

    public Amplifier getAmplifier() {
        return amplifier;
    }

    /**
     * @return the EIRP
     */
    public double getEIRP() {
        return EIRP;
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
