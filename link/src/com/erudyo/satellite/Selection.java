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
    private ComboBox satelliteView;
    private ComboBox tXview;
    private ComboBox rXview;
    
    private RfBand.Band band = RfBand.Band.KA;

    public static Hashtable<RfBand.Band, ArrayList<Satellite>> bandSatellite;
    public static Hashtable<RfBand.Band, ArrayList<Terminal>> bandTerminal;

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
    
    public ComboBox getSatelliteView() {
        return satelliteView;
    }
    
    public void setSatelliteView (ComboBox c) {
        this.satelliteView = c;
    }
    
     
      public ComboBox getRxView() {
        return rXview;
    }
    
    public void setRxView (ComboBox c) {
        this.rXview = c;
    }

      public ComboBox getTxView() {
        return tXview;
    }
    
    public void setTxView (ComboBox c) {
        this.tXview = c;
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

    /**
     * @param terminals the terminals to set
     */
    public void setTerminals(String[][] terminals) {

    }

}
