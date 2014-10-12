/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import com.codename1.io.Log;

import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.util.MathUtil;
import com.codename1.maps.Coord;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Terminal extends Entity {

    private double longitude;     //longitude
    private double latitude;       //latitude
    private RfBand.Band band;
    private RfBand.Band uLband;
    private RfBand.Band dLband;
    private Antenna antenna;
    private Amplifier amplifier;
    // eventually calculate these things
    private double EIRP;        // somewhere this has to be updated
    private double gain;

    private int index;

    // for receiver
    private double gainTemp;
    private double polarizationLoss;
    private double systemTemp;

    public Terminal() {

    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return name;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    // all terminals stored in a Class level hash
    static Hashtable<String, Terminal> terminalHash
            = new Hashtable<String, Terminal>();

    // lookup by index with class level vector
    final public static ArrayList<Terminal> indexTerminal
            = new ArrayList<Terminal>();

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
            
              Log.p("Terminal: Processing terminal "
                        + Arrays.toString(terminals[i]), Log.INFO);
            RfBand.Band band = RfBand.rFbandHash.get(terminals[i][5]).getBand();

            // need to key on a correct band
            if (band == null) {
                Log.p("Terminal: Bad band " + terminals[i].toString(), Log.WARNING);

            } else {
                // extract the band of the terminal
                if (bandTerminal.get(band) == null) {
                    bandTerminal.put(band, new ArrayList<Terminal>());
                }

                // get band using its string version as key (* matches all)
                terminalFields(terminals[i], bandTerminal.get(band));

                // check the band from file and create entry in hash table
                Log.p("Terminal: Processed terminal "
                        + Arrays.toString(terminals[i]), Log.INFO);
            }
        }

        return bandTerminal;
    }

    public static void terminalFields(String[] fields, ArrayList<Terminal> vector) {
        // vector has already been created for a band, just add entries
        Terminal terminal = new Terminal(fields[0]);

        // terminals in format name, longitude, latitude, antenna size, amplifier
        // gain, and band
        // get the band first
        terminal.setBand(RfBand.rFbandHash.get(fields[5]).getBand());

        // set uplin and downlink
        terminal.setuLband(RfBand.findUl(terminal.getBand()));
        terminal.setdLband(RfBand.findDl(terminal.getBand()));

        // update band and frequency for antenna
        // get the uplink version of the terminal band
        // this will collide with similar change for DownLink if
        // the terminal is shared by Tx and Rx
        terminal.getAntenna().setBand(RfBand.findUl(terminal.getBand()));

        terminal.setLongitude(Math.toRadians(Double.parseDouble(fields[1])));
        terminal.setLatitude(Math.toRadians(Double.parseDouble(fields[2])));

        terminal.getAntenna().setDiameter(Double.parseDouble(fields[3]));
        terminal.getAmplifier().setPower(Double.parseDouble(fields[4]));

        // where do we update terminal EIRP.  Now automatic with "update"
        vector.add(terminal);

    }

    public RfBand.Band getBand() {
        return band;
    }

    // unless the constructor finishes this is not available
    public void init() {

    }

    public Terminal(String name) {

        // race condintion?   Had to move this before passing this in addAffected
        this.name = name;

        antenna = new Antenna();
        antenna.addAffected(this);
        // System.out.println(this.name);
        antenna.setDiameter(1);

        amplifier = new Amplifier();
        amplifier.addAffected(this);
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

        // add the new Terminal instance to the Hashtable at the class level
        Terminal.terminalHash.put(getName(), this);

        // Add new object instance to the array list (all satellites)
        indexTerminal.add(this);

        index = indexTerminal.size() - 1;

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
        updateAffected();
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
        updateAffected();
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
    public boolean setEIRP(double EIRP) {
        // TODO, change simple setting to actually distributing change
        // to Antenna and Amplifier.   Call their set methods and let update
        // come back and change the EIRP

        // Each set can do the max it can and let the caller know if it failed
        // or passed
        return true;
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
    public boolean setGain(double gain) {
        // Gain also would depend on multiple components so use set methods

        return true;
    }

    private double calcEIRP() {
        double eirp = (10 * MathUtil.log10(
                this.getAmplifier().getPower())) // was in W
                + this.getAntenna().getGain() // in dB
                - this.getAntenna().getDepointingLoss()
                - this.getAmplifier().getLFTX(); // in dB
        return eirp;
    }

    private double calcGainTemp() {

        double gain;

        gain = 10.0 * MathUtil.log10(this.gain)
                - this.getAntenna().calcDepointingLoss()
                - this.getAmplifier().getLFRX()
                - this.polarizationLoss
                - 10.0 * MathUtil.log10(calcSystemNoiseTemp());
        return gain;
    }

    // downlink system noise temperature at the receiver input given by

    private double calcSystemNoiseTemp() {
        double tA;
        double noiseTemp;
        double teRX;
        // noise figure is in dB
        teRX = (MathUtil.pow(10.0, this.amplifier.getNoiseFigure() / 10.0)
                - 1.0) * Com.T0;
        tA = this.getAmplifier().getTempSky(this.dLband)
                + this.getAmplifier().getTempGround();

        // LFRX is in dB so change
        double lfrx = MathUtil.pow(10.0, this.getAmplifier().getLFRX() / 10.0);

        noiseTemp = tA / lfrx
                + this.getAmplifier().getFeederTemp()
                * (1.0 - 1.0 / lfrx) + teRX;

        return noiseTemp;
    }

    // this function called by children and sibling "e" of this when they change
    public void update(Entity e) {

        // update everything that could be affected
        // EIRP depends on antenna and amplifier, but both need to exist 
        if (this.getAmplifier() != null && this.getAntenna() != null) {
            this.EIRP = calcEIRP();

            this.gainTemp = calcGainTemp();
        }

        // avoid using set since that should be used to send updates down
    }

    /**
     * @return the uLband
     */
    public RfBand.Band getuLband() {
        return uLband;
    }

    /**
     * @param uLband the uLband to set
     */
    public void setuLband(RfBand.Band uLband) {
        this.uLband = uLband;
    }

    /**
     * @return the dLband
     */
    public RfBand.Band getdLband() {
        return dLband;
    }

    /**
     * @param dLband the dLband to set
     */
    public void setdLband(RfBand.Band dLband) {
        this.dLband = dLband;
    }

    /**
     * @return the gainTemp
     */
    public double getGainTemp() {
        return gainTemp;
    }

    /**
     * @param gainTemp the gainTemp to set
     */
    public void setGainTemp(double gainTemp) {
        this.gainTemp = gainTemp;
    }

    /**
     * @return the polarizationLoss
     */
    public double getPolarizationLoss() {
        return polarizationLoss;
    }

    /**
     * @param polarizationLoss the polarizationLoss to set
     */
    public void setPolarizationLoss(double polarizationLoss) {
        this.polarizationLoss = polarizationLoss;
    }

    /**
     * @return the systemTemp
     */
    public double getSystemTemp() {
        return systemTemp;
    }

    /**
     * @param systemTemp the systemTemp to set
     */
    public void setSystemTemp(double systemTemp) {
        this.systemTemp = systemTemp;
    }
}
