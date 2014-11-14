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
import com.codename1.maps.Coord;
import com.codename1.maps.MapComponent;
import com.codename1.maps.providers.GoogleMapsProvider;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Label;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.ArrayList;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Selection {

    /**
     * @return the cbBand
     */
    public static ComboBox getCbBand() {
        return cbBand;
    }

    /**
     * @param aCbBand the cbBand to set
     */
    public static void setCbBand(ComboBox aCbBand) {
        cbBand = aCbBand;
    }

    /**
     * @return the lBand
     */
    public static Label getlBand() {
        return lBand;
    }

    /**
     * @param alBand the lBand to set
     */
    public static void setlBand(Label alBand) {
        lBand = alBand;
    }

    private Comms comms;                // basic comms parameters
    private Terminal tXterminal;        // created in tXview (getWidget)
    private Terminal rXterminal;
    private Satellite satellite;
    private Path uLpath;
    private Path dLpath;
    private Location currentLocation;

    //  keep only one copy of views, shared by all isntances
    static private ComboBox cbBand;       // could have had its own view
    static private Label lBand;
    static private SatelliteView satelliteView;
    static private TxView tXview;
    static private UlPathView uLpathView;
    static private RxView rXview;
    static private CommsView commsView;
    static private DlPathView dLpathView;

    /**
     * @return the currentLocation
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }

    /**
     * @param currentLocation the currentLocation to set
     */
    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

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
        cbBand = new ComboBox();
        satelliteView = new SatelliteView(this);
        tXview = new TxView(this);
        uLpathView = new UlPathView(this);
        rXview = new RxView(this);

        dLpathView = new DlPathView(this);
        commsView = new CommsView(this);
        initRfBandHash();

        // get the list of satellites read from the .txt file
        setBandSatellite(Satellite.getBandSatellite());
        // initialize visible terminals for selected satellite (use Affected)

        try {

            currentLocation
                    = LocationManager.getLocationManager().getCurrentLocation();

            Log.p("Selection: current location is Long|Lat "
                    + currentLocation.getLongitude() + "|"
                    + (currentLocation.getLatitude()), Log.DEBUG);

        } catch (Exception d) {
            currentLocation = new Location();
            currentLocation.setLongitude(0.0);
            currentLocation.setLatitude(0.0);
            Log.p("Selection:  can't get current location.  Using 0.0", Log.WARNING);

        }

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
        tXterminal.setBand(this.getBand());

        // after init
        if (getuLpath() != null) {
            getuLpath().setTerminal(gettXterminal());
            // uLpath will update other objects
        }
        if (comms != null) {
            comms.changeDLname(dLpath.name);
            comms.changeULname(uLpath.name);
        }
    }

    /**
     * @return the rXterminal
     */
    public Terminal getrXterminal() {
        // why is terminal being changed

        return rXterminal;
    }

    /**
     * @param rXterminal the rXterminal to set
     */
    public void setrXterminal(Terminal rXterminal) {
        // why is terminal being changed
        this.rXterminal = rXterminal;
        rXterminal.setBand(this.getBand());

        if (getdLpath() != null) {
            getdLpath().setTerminal(getrXterminal());
        }
        if (comms != null) {
            comms.changeDLname(dLpath.name);
            comms.changeULname(uLpath.name);
        }
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
        // update visible terminals 
        this.satellite = satellite;

        // try to get beams from txt files for this satellite
        satellite.readBeams(band);

        initVisibleTerminal();  // they are both here in this class

        // update the UL and DL paths
        if (uLpath != null) {
            uLpath.setSatellite(satellite);
        }
        if (dLpath != null) {
            dLpath.setSatellite(satellite);
        }
        if (comms != null) {
            comms.changeDLname(dLpath.name);
            comms.changeULname(uLpath.name);
        }
    }

    public void comboBand(final Selection selection) {

        ComboBox cbBand = selection.getCbBand();

        cbBand.setSelectedItem(selection.getBand());
        RfBand rFband = RfBand.rFbandHash.get(selection.
                getBand().toString());

        selection.getlBand().setText(Com.shortText((rFband.lowFrequency / 1E9))
                + " - " + (Com.shortText(rFband.highFrequency / 1E9))
                + " GHz");
        Log.p("Link: band selection " + cbBand.getSelectedItem().toString(), Log.DEBUG);

        if (!selection.comboSatellite(selection)) {
            // get index of KA in case no satellites were found
            int i = selection.getRfBandHash().get("KA");
            // change the current Combobox entry to KA
            cbBand.setSelectedIndex(i);
            selection.setBand(RfBand.Band.KA);
            selection.getrXterminal().setBand(selection.getBand());
            selection.gettXterminal().setBand(selection.getBand());
        }

        // it will select Tx terminal
        selection.comboTx(selection);
        // will select Rx terminal
        selection.comboRx(selection);
    }

    public void comboRx(final Selection selection) {
        // use global variable to change ListModel of satellite combo
        if (selection.getVisibleTerminal().get(Selection.VISIBLE.YES) == null) {

            Log.p("Link: no visible terminal for satellite "
                    + selection.getSatellite(), Log.WARNING);
        } else {

            selection.initVisibleTerminal();
            DefaultListModel model = new DefaultListModel(
                    (selection.getVisibleTerminal().get(Selection.VISIBLE.YES).toArray(
                            new String[0])));

            if (model == null) {
                Log.p("Link: Can't create DefaultListModel for Rx terminal "
                        + selection.getSatellite(), Log.DEBUG);
            } else {

                // let RxView do this again.   TODO Delete this
                selection.getRxView().spin.setModel(model);

                int position;
                // update selected receive terminal  TODO check for 0 terminals
                if (selection.getVisibleTerminal().get(Selection.VISIBLE.YES).size() < 2) {
                    // get the first terminal (only 1)
                    position = 0;
                } else {
                    position = 1;
                }
                // set the selected receive terminal
                // band is selected in setrX/settX (circular conditions)
                selection.setrXterminal(Terminal.terminalHash.
                        get(selection.getVisibleTerminal().
                                get(Selection.VISIBLE.YES).toArray(
                                        new String[0])[position]));

                model.setSelectedIndex(position);
                // update label 
                selection.getRxView().updateValues(selection);
                selection.getdLpathView().updateValues(selection);

            }
        }
    }

    // resets the Tx terminal combo and finds the nearest terminal for
    // the specific satellite
    public void comboTx(final Selection selection) {
        // use global variable to change ListModel of satellite combo
        if (selection.getVisibleTerminal().get(Selection.VISIBLE.YES) == null) {

            Log.p("Link: Visible terminal list is empty for "
                    + selection.getSatellite(), Log.ERROR);
            // change the current Combobox entry
            // cbBand.setSelectedIndex(i);
        } else {

            selection.initVisibleTerminal();
            DefaultListModel model = new DefaultListModel(
                    (selection.getVisibleTerminal().get(Selection.VISIBLE.YES).toArray(
                            new String[0])));

            if (model == null) {
                Log.p("Link: Can't create DefaultListModel for VISIBLE Tx terminal for sat "
                        + selection.getSatellite(), Log.ERROR);
            } else {
                selection.getTxView().spin.setModel(model);
                // update visible terminals 

                if (selection.getVisibleTerminal().get(
                        Selection.VISIBLE.YES).size() < 1) // get the first terminal (only 1)
                {
                    Log.p("Link: no visible terminal for tx  for satellite " + selection.getSatellite(),
                            Log.WARNING);
                }

                // tX terminal will set UL and DL band/freq for antennas
                // set the selected Tx terminal
                selection.settXterminal(Terminal.terminalHash.
                        get(selection.getVisibleTerminal().
                                get(Selection.VISIBLE.YES).toArray(
                                        new String[0])[0]));

                model.setSelectedIndex(0);
                selection.getTxView().updateValues(selection);
                selection.getuLpathView().updateValues(selection);
            }
        }

    }

    public boolean comboSatellite(final Selection selection) {
        // use global variable to change ListModel of satellite combo
        if (selection.getBandSatellite().get(selection.getBand()) == null) {

            Log.p("link: Can't get bandSatellite for band "
                    + selection.getBand(), Log.WARNING);
            // Force it to KA which hopefully works
            selection.setBand(RfBand.Band.KA);

            return false;
        }
        // sort the satellite for current location (sorts bandSatellite, bandSatelliteHash
        bandSatelliteSort(getCurrentLocation().getLongitude() * Com.PI / 180.0);

        DefaultListModel model = new DefaultListModel(
                (selection.getBandSatellite().get(selection.getBand()).toArray(
                        new String[0])));

        if (model == null) {
            Log.p("Link: Can't create DefaultListModel for satellite band "
                    + selection.getBand(), Log.DEBUG);
        } else {
            // use the list of satellites for select band 
            selection.getSatelliteView().spin.setModel(model);

            if (selection.getSatellite() == null) {
                selection.setSatellite(Satellite.satelliteHash.
                        get(selection.getSatelliteView().spin.getSelectedItem()));
            }
            
            selection.getSatelliteView().spin.setSelectedItem(
                    selection.getSatellite().getName());

            // Satellite Band processed on its own
            // now create visible lists for this satellite
            selection.initVisibleTerminal();

            // update values for satellite, UL path, DL path, Comms TODO
            selection.getSatelliteView().updateValues(selection);

        }

        return true;
    }

    /**
     * @return the uLpath
     */
    public Path getuLpath() {
        return uLpath;
    }

    /*
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
    public void setBandSatellite(Hashtable<RfBand.Band, ArrayList<Satellite>> sats) {

        // go over all bands
        for (RfBand band : RfBand.indexRfBand) {
            int index = 0;
            // go through all satellites of this band
            if (sats.get(band.getBand()) != null) {
                for (Satellite sat : sats.get(band.getBand())) {

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

    // sort the satellites in some specific order (distance from current location)
    public void bandSatelliteSort(final double longitude) {

        // go over all bands
        for (final RfBand band : RfBand.indexRfBand) {
            if (band.getBand() == RfBand.Band.UK) {
                continue;
            }

            Log.p("sortBandSatellite: processing band " + band, Log.DEBUG);
            if (bandSatellite == null
                    || bandSatellite.get(band.getBand()) == null) {
                Log.p("sortBandSatellite: bandSatellite is null for band "
                        + band.getBand(), Log.DEBUG);
                // don't return, just go to next band
                continue;
            }

            Collections.sort(bandSatellite.get(band.getBand()),
                    new Comparator<String>() {
                        @Override
                        public int compare(String one, String two) {

                            try {
                                //   Log.p("bandSortSatellite: one|two|band" + one
                                //         + "|" + two + "|" + band.getBand(),
                                //       Log.DEBUG);
                                Double first, second;

                                first = Path.calcRelativeLongitude(longitude,
                                        Satellite.satelliteHash.get(one).getLongitude());
                                second = Path.calcRelativeLongitude(longitude,
                                        Satellite.satelliteHash.get(two).getLongitude());

                                // this is elaborate, and 0, -1, 1 need to be 
                                // addressed, per contract for TimSort (Java 7)
                                if (first == second) {
                                    return 0;
                                } else if (first < second) {
                                    return -1;
                                } else {
                                    return 1;
                                }

                            } catch (Exception e) {
                                Log.p("Satellite: sort one|two" + one + "|"
                                        + two, Log.DEBUG);
                                e.printStackTrace();

                                return (0);
                            }
                        }

                    }
            );

            // remove old values for the band
            bandSatelliteHash.get(band.getBand()).clear();
            for (int i = 0; i < bandSatellite.get(band.getBand()).size(); i++) {

                // check if Hashtable entry for the band exists
                if (bandSatelliteHash.get(band.getBand()) == null) {
                    bandSatelliteHash.put(band.getBand(),
                            new Hashtable<String, Integer>());
                }
                // now add new satellite position for this satellite
                bandSatelliteHash.get(band.getBand()).
                        put(bandSatellite.get(band.getBand()).get(i), i);
                Log.p("sortBandSatellite adding satellite "
                        + bandSatellite.get(band.getBand()).get(i), Log.DEBUG);
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
    // list.  Sorted by distance between satellite and a terminal.
    // Call it again when a terminal is added so no new addVisible
    // at this time.

    public void initVisibleTerminal() {

        int indexVis = 0;
        int indexNonVis = 0;

        visibleTerminal = new Hashtable<VISIBLE, ArrayList<String>>();

        // create new list of visible and non visible satellites with
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

                // check if Hashtable entry exists
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

                // check if Hashtable entry exists
                if (visibleTerminalHash.get(VISIBLE.NO) == null) {
                    visibleTerminalHash.put(VISIBLE.NO,
                            new Hashtable<String, Integer>());
                }

                // now add new satellite position
                visibleTerminalHash.get(VISIBLE.NO).
                        put(term.getName(), indexNonVis++);
            }

        }
        if (visibleTerminal.get(VISIBLE.YES) == null) {
            Log.p("Selection: visible terminal list is null for satellite "
                    + this.getSatellite(), Log.DEBUG);

        } else // now sort each list by distance of terminal from satellite
        {
            Collections.sort(visibleTerminal.get(VISIBLE.YES), new Comparator<String>() {
                @Override
                public int compare(String one, String two) {

                    if (getSatellite() == null) {
                        Log.p("Selection: satellite is null in initVisible YES so alpha sort ", Log.WARNING);
                        return one.compareTo(two);
                    } else {
                        return (int) Math.round(Path.calcDistance(satellite,
                                Terminal.terminalHash.get(one))
                                - Path.calcDistance(satellite,
                                        Terminal.terminalHash.get(two)));
                    }

                }

            }
            );
        }

        Collections.sort(visibleTerminal.get(VISIBLE.NO), new Comparator<String>() {
            @Override
            public int compare(String one, String two) {

                if (getSatellite() == null) {
                    Log.p("Selection: satellite is null in initVisible NO so alpha sort ", Log.WARNING);
                    return one.compareTo(two);
                } else {

                    return (int) Math.round(Path.calcDistance(satellite,
                            Terminal.terminalHash.get(one))
                            - Path.calcDistance(satellite,
                                    Terminal.terminalHash.get(two)));
                }

            }

        }
        );
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
