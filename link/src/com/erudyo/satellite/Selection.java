/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import java.util.Hashtable;
import java.util.ArrayList;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Selection {

    private Terminal tXterminal;
    private Terminal rXterminal;
    private Satellite satellite;
    private Path uLpath;
    private Path dLpath;
  
    private SatelliteView satelliteView;
    private TxView tXview;
    private RxView rXview;

    private RfBand.Band band = RfBand.Band.KA;

    // all the UI oriented lists are to support ComboBox selection
    // they use names of satellites, terminals, and bands.
    // Actual object can be then found from respective Hashtables
    // and indexes.   Originally they had real objects which should
    // not have indexes shared across multiple UI/selections
    private Hashtable<RfBand.Band, ArrayList<String>> bandSatellite
            = new Hashtable<RfBand.Band, ArrayList<String>>();

    // first hash has band as key and the second satellite name
    private Hashtable<RfBand.Band, Hashtable<String, Integer>> bandSatelliteHash
            = new Hashtable<RfBand.Band, Hashtable<String, Integer>>();
    private Hashtable<RfBand.Band, ArrayList<String>> bandTerminal
            = new Hashtable<RfBand.Band, ArrayList<String>>();

    // first hash has band as key and the second terminal name
    private Hashtable<RfBand.Band, Hashtable<String, Integer>> bandTerminalHash
            = new Hashtable<RfBand.Band, Hashtable<String, Integer>>();

    // lookup bands (could be customiazed for each Selection instance)
    // provides the position if name is known
    private Hashtable<String, Integer> rFbandHash
            = new Hashtable<String, Integer>();

    // lookup by index with instance name vector 
    private ArrayList<String> rfBands
            = new ArrayList<String>();

    // Create a custom UI oriented Combo model from RfBand Hash
    // Can add filtering in future.   Light since only name is
    // used and not the object instances
    public void setRfBandHash(Hashtable<String, RfBand> h) {

        int index = 0;
        // go through the hash to create positions and indexRfBand entries
        for (String key : h.keySet()) {
            // add the position and increment for next item
            rFbandHash.put(key, index++);

            // create a simple array with object name (key)
            rfBands.add(key);
        }

    }

    public Hashtable<String, Integer> getRfBandHash() {
        return this.rFbandHash;
    }

    // this is created by setHashRfBand()
    /* public void setIndexRfBand(ArrayList<RfBand> h) {
     this.indexRfBand = h;
         
     }
     */
    public ArrayList<String> getRfBands() {
        return this.rfBands;
    }

  /* 

        // transmit terminal at current location if not in persistent storage
        tXterminal = new Terminal();
        tXterminal.setBand(band);

        // default generic satellite (from satellite list)
        satellite = new Satellite();

        uLpath = new Path(satellite, tXterminal);
        uLpath.setBand(band);
        // sharing common Path, here name needs to be set 
        uLpath.setName("UpLink");
        uLpath.setS(satellite);
        uLpath.setT(tXterminal);

        // receive terminal near transmit if not in persistent storage
        rXterminal = new Terminal();
        rXterminal.setBand(band);

        dLpath = new Path(satellite, tXterminal);
        dLpath.setBand(band);
        dLpath.setName("DownLink");
        dLpath.setS(satellite);
        dLpath.setT(rXterminal);
*/
         public Selection() {
        // use constructor with Selection instance as input
        satelliteView = new SatelliteView(this);
        tXview = new TxView(this);
        rXview = new RxView(this);

    }

    public SatelliteView getSatelliteView() {
        return satelliteView;
    }

    public void setSatelliteView(SatelliteView s) {
        this.satelliteView = s;
    }

    public RxView getRxView() {
        return rXview;
    }

    public void setRxView(RxView r) {
        this.rXview = r;
    }

    public TxView getTxView() {
        return tXview;
    }

    public void setTxView(TxView t) {
        this.tXview = t;
    }

    public RfBand.Band getBand() {
        return band;
    }

    public void setBand(RfBand.Band band) {
        this.band = band;
    }

    /**
     * @return the tXterminal
     */
    public Terminal gettXterminal() {
        return tXterminal;
    }

    /**
     * @param tXterminal the tXterminal to set
     */
    public void settXterminal(Terminal tXterminal) {
        this.tXterminal = tXterminal;
    }

    /**
     * @return the rXterminal
     */
    public Terminal getrXterminal() {
        return rXterminal;
    }

    /**
     * @param rXterminal the rXterminal to set
     */
    public void setrXterminal(Terminal rXterminal) {
        this.rXterminal = rXterminal;
    }

    /**
     * @return the satellite
     */
    public Satellite getSatellite() {
        return satellite;
    }

    /**
     * @param satellite the satellite to set
     */
    public void setSatellite(Satellite satellite) {
        this.satellite = satellite;
    }



    /**
     * @return the uLpath
     */
    public Path getuLpath() {
        return uLpath;
    }

    /**
     * @param uLpath the uLpath to set
     */
    public void setuLpath(Path uLpath) {
        this.uLpath = uLpath;
    }

    /**
     * @return the dLpath
     */
    public Path getdLpath() {
        return dLpath;
    }

    /**
     * @param dLpath the dLpath to set
     */
    public void setdLpath(Path dLpath) {
        this.dLpath = dLpath;
    }

  

    // selection of satellites relevant for this instance of Selection
    // In future, it could be a filtered version (based on location, e.g.)
    public void setBandSatellite(Hashtable<RfBand.Band, 
            ArrayList<Satellite>> s) {

        // go over all bands
        for (RfBand band : RfBand.indexRfBand) {
            int index = 0;
            // go through all satellites of this band
            if (s.get(band.getBand()) != null) {
                for (Satellite sat : s.get(band.getBand())) {

                    // check if the array exists
                    if (bandSatellite.get(band.getBand()) == null) {
                        bandSatellite.put(band.getBand(), new ArrayList());
                    }

                    // add the name of the satellite in the array list
                    bandSatellite.get(band.getBand()).add(sat.getName());

                    // check if Hashtable entry for the band exists
                    if (bandSatelliteHash.get(band.getBand()) == null) {
                        bandSatelliteHash.put(band.getBand(),
                                new Hashtable<String, Integer>());
                    }

                    // now add new satellite position
                    bandSatelliteHash.get(band.getBand()).
                            put(sat.getName(), index++);

                }
            }
        }
    }

    public Hashtable<RfBand.Band, ArrayList<String>> getBandTerminal() {
        return this.bandTerminal;
    }

    public Hashtable<RfBand.Band, ArrayList<String>> getBandSatellite() {
        return this.bandSatellite;
    }

    public Hashtable<RfBand.Band, Hashtable<String, Integer>> getBandTerminalHash() {
        return bandTerminalHash;
    }

    public Hashtable<RfBand.Band, Hashtable<String, Integer>> getBandSatelliteHash() {
        return bandSatelliteHash;
    }

    public void setBandTerminal(Hashtable<RfBand.Band, ArrayList<Terminal>> t) {
        // go over all bands
        for (RfBand band : RfBand.indexRfBand) {
            int index = 0;
//          // go through all satellites of this band
            if (t.get(band.getBand()) != null) {
                for (Terminal term : t.get(band.getBand())) {

                    // check if the array exists
                    if (bandTerminal.get(band.getBand()) == null) {
                        bandTerminal.put(band.getBand(), new ArrayList());
                    }

                    // add the name of the terminal in the array list
                    bandTerminal.get(band.getBand()).add(term.getName());

                    // check if Hashtable entry for the band exists
                    if (bandTerminalHash.get(band.getBand()) == null) {
                        bandTerminalHash.put(band.getBand(),
                                new Hashtable<String, Integer>());
                    }

                    // now add new satellite position
                    bandTerminalHash.get(band.getBand()).
                            put(term.getName(), index++);

                }
            }
        }
    }
    /*
     * @param terminals the terminals to set
     */

    public void setTerminals(String[][] terminals) {

    }

}
