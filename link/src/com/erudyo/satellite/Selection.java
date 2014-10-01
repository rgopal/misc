/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import java.util.Hashtable;
import java.util.Vector;
import java.util.ArrayList;
import com.codename1.ui.ComboBox;

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
    private Satellite[] satellites;
    private Terminal[] terminals;
    private SatelliteView satelliteView = new SatelliteView();
    private TxView tXview = new TxView();
    private RxView rXview = new RxView();
    
    private RfBand.Band band = RfBand.Band.KA;

    // all the UI oriented lists are to support ComboBox selection
    // they use names of satellites, terminals, and bands.
    // Actual object can be then found from respective Hashtables
    // and indexes.   Originally they had real objects which should
    // not have indexes shared across multiple UI/selections
    
    private  Hashtable<RfBand.Band, ArrayList<Satellite>> bandSatellite;
    private  Hashtable<RfBand.Band, ArrayList<Terminal>> bandTerminal;
    
     // lookup bands (could be customiazed for each Selection instance)
     // provides the position if name is known
     private  Hashtable<String, Integer> rFbandHash
            = new Hashtable<String, Integer>();

    // lookup by index with instance name vector 
     private  ArrayList<String> rfBands 
            = new ArrayList<String>();

     
     // Create a custom UI oriented Combo model from RfBand Hash
     // Can add filtering in future.   Light since only name is
     // used and not the object instances
     public void setRfBandHash(Hashtable<String, RfBand> h) {
         
         int index = 0;
         // go through the hash to create positions and indexRfBand entries
         for (String key: h.keySet()) {
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
    public Selection() {

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

    }
    
    public SatelliteView getSatelliteView() {
        return satelliteView;
    }
    
    public void setSatelliteView (SatelliteView s) {
        this.satelliteView = s;
    }
    
     
      public RxView getRxView() {
        return rXview;
    }
    
    public void setRxView (RxView r) {
        this.rXview = r;
    }

      public TxView getTxView() {
        return tXview;
    }
    
    public void setTxView (TxView t) {
        this.tXview = t;
    }

    public RfBand.Band getBand () {
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
     public Terminal getTx() {
        return tXterminal;
    }

    /**
     * @param satellite the satellite to set
     */
    public void setTx(Terminal terminal) {
        this.tXterminal = terminal;
    }

      public Terminal getRx() {
        return rXterminal;
    }

    /**
     * @param satellite the satellite to set
     */
    public void setRx(Terminal terminal) {
        this.rXterminal = terminal;
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

    /**
     * @return the satellites
     */
    public Satellite[] getSatellites() {
        return satellites;
    }

    /**
     * @param satellites the satellites to set
     */
    public void setSatellites(String[][] satellites) {

    }

    /**
     * @return the terminals
     */
    public Terminal[] getTerminals() {
        return terminals;
    }

    // selection of satellites relevant for this instance of Selection
    // In future, it could be a filtered version (based on location, e.g.)
    public void setBandSatellite(Hashtable<RfBand.Band, ArrayList<Satellite>> s) {
        bandSatellite = s;
    }
    
    public Hashtable<RfBand.Band, ArrayList<Terminal>> getBandTerminal() {
        return bandTerminal;
    }
     public Hashtable<RfBand.Band, ArrayList<Satellite>> getBandSatellite() {
        return bandSatellite;
    }
    
    public void setBandTerminal(Hashtable<RfBand.Band, ArrayList<Terminal>> t) {
        bandTerminal = t;
    }
    /*
     * @param terminals the terminals to set
     */
    public void setTerminals(String[][] terminals) {

    }

}
