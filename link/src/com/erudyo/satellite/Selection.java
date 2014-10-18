/*
 * OVERVIEW
 * Holds all high level objects from models and views so that they can be
 * persistenly stored and used in what-if analysis.   Defitely creates all 
 * views.  Terminals and Satellites are created when the respective text
 * files are read.  Persistence can be used to update the values from text
 * files with changes made by the user in previous sessions.
 *
 * Respective views create objects not read from the txt files (includes Comms,
 * Band).  This happens in the getWidget method which needs the respective model
 * to get data from.
 *
 * Satellite view selects an existing Satellite instance (model) which was created
 * from the text file (and updated if Selection is persistent).
 */
package com.erudyo.satellite;

import com.codename1.io.Log;
import java.util.Hashtable;
import java.util.ArrayList;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Selection {

    private Comms comms;                // basic comms parameters
    private Terminal tXterminal;        // created in tXview (getWidget)
    private Terminal rXterminal;
    private Satellite satellite;
    private Path uLpath;
    private Path dLpath;

    private SatelliteView satelliteView;
    private TxView tXview;
    private UlPathView uLpathView;
    private RxView rXview;
    private CommsView commsView;
    private DlPathView dLpathView;

    public enum VISIBLE {

        YES, NO
    };

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
    private Hashtable<VISIBLE, ArrayList<String>> visibleTerminal
            = new Hashtable<VISIBLE, ArrayList<String>>();

    // first hash has band as key and the second terminal name
    private Hashtable<VISIBLE, Hashtable<String, Integer>> visibleTerminalHash
            = new Hashtable<VISIBLE, Hashtable<String, Integer>>();

    // lookup bands (could be customiazed for each Selection instance)
    // provides the position if name is known
    private Hashtable<String, Integer> rFbandHash
            = new Hashtable<String, Integer>();

    // lookup by index with instance name vector 
    private ArrayList<String> rfBands
            = new ArrayList<String>();

    // Create a custom UI oriented Combo model from RfBand Arraylist
    // Can add filtering in future.   Light since only name is
    // used and not the object instances.  Preserve order (instead of Hash)
    public void initRfBandHash() {

        int index = 0;
        // go through the hash to create positions and indexRfBand entries
        for (RfBand key : RfBand.indexRfBand) {
            // add the position and increment for next item
            rFbandHash.put(key.toString(), index++);

            // create a simple array with object name (key)
            rfBands.add(key.toString());
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
        // use constructor with Selection instance as input
        satelliteView = new SatelliteView(this);
        tXview = new TxView(this);
        uLpathView = new UlPathView(this);
        rXview = new RxView(this);
        commsView = new CommsView(this);
        dLpathView = new DlPathView(this);

        initRfBandHash();

        // get the list of satellites read from the .txt file
        setBandSatellite(Satellite.getBandSatellite());

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
    public void setBandSatellite(Hashtable<RfBand.Band, ArrayList<Satellite>> s) {

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

    public Hashtable<VISIBLE, ArrayList<String>> getVisibleTerminal() {
        return this.visibleTerminal;
    }

    public Hashtable<RfBand.Band, ArrayList<String>> getBandSatellite() {
        return this.bandSatellite;
    }

    public Hashtable<VISIBLE, Hashtable<String, Integer>> getVisibleTerminalHash() {
        return visibleTerminalHash;
    }

    public Hashtable<RfBand.Band, Hashtable<String, Integer>> getBandSatelliteHash() {
        return bandSatelliteHash;
    }

    // add a new terminal created by GUI
    public void addVisibleTerminal() {

    }
    // set the visible terminal String list based on the Terminal class
    // list.   Call it again when a terminal is added so no new addVisible
    // at this time

    public void initVisibleTerminal() {
        Hashtable<RfBand.Band, ArrayList<Terminal>> t;
        t = null;

        int indexVis = 0;
        int indexNonVis = 0;

        // create list of visible and non visible satellites with
        // respect to selected satellite
        for (Terminal term : Terminal.indexTerminal) {

            if (Path.visible(this.satellite, term)) {
                Log.p("Selection: terminal " + term
                        + " is visible to satellite "
                        + this.satellite, Log.DEBUG);

                // check if the array exists
                if (visibleTerminal.get(VISIBLE.YES) == null) {
                    visibleTerminal.put(VISIBLE.YES, new ArrayList());
                }

                // add the name of the terminal in the array list
                visibleTerminal.get(VISIBLE.YES).add(term.getName());

                // check if Hashtable entry for the band exists
                if (visibleTerminalHash.get(VISIBLE.YES) == null) {
                    visibleTerminalHash.put(VISIBLE.YES,
                            new Hashtable<String, Integer>());
                }

                // now add new satellite position
                visibleTerminalHash.get(VISIBLE.YES).
                        put(term.getName(), indexVis++);
            } else {

                Log.p("Selection: terminal " + term
                        + " is NOT visible to satellite "
                        + this.satellite, Log.DEBUG);

                // check if the array exists
                if (visibleTerminal.get(VISIBLE.NO) == null) {
                    visibleTerminal.put(VISIBLE.NO, new ArrayList());
                }

                // add the name of the terminal in the array list
                visibleTerminal.get(VISIBLE.NO).add(term.getName());

                // check if Hashtable entry for the band exists
                if (visibleTerminalHash.get(VISIBLE.NO) == null) {
                    visibleTerminalHash.put(VISIBLE.NO,
                            new Hashtable<String, Integer>());
                }

                // now add new satellite position
                visibleTerminalHash.get(VISIBLE.NO).
                        put(term.getName(), indexNonVis++);
            }

        }

    }

    /*
     * @param terminals the terminals to set
     */
    public void setTerminals(String[][] terminals) {

    }

    /**
     * @return the comms
     */
    public Comms getComms() {
        return comms;
    }

    /**
     * @param comms the comms to set
     */
    public void setComms(Comms comms) {
        this.comms = comms;
    }

    /**
     * @return the commsView
     */
    public CommsView getCommsView() {
        return commsView;
    }

    /**
     * @param commsView the commsView to set
     */
    public void setCommsView(CommsView commsView) {
        this.commsView = commsView;
    }

    /**
     * @return the uLpathView
     */
    public UlPathView getuLpathView() {
        return uLpathView;
    }

    /**
     * @return the uLpathView
     */
    public DlPathView getdLpathView() {
        return dLpathView;
    }

    /**
     * @param uLpathView the uLpathView to set
     */
    public void setuLpathView(UlPathView uLpathView) {
        this.uLpathView = uLpathView;
    }

    /**
     * @param dLpathView the dLpathView to set
     */
    public void setdLpathView(DlPathView dLpathView) {
        this.dLpathView = dLpathView;
    }

}
