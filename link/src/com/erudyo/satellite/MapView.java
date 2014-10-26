/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import com.codename1.io.Log;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.maps.Coord;
import com.codename1.maps.MapComponent;
import com.codename1.maps.Mercator;
import com.codename1.maps.layers.LinesLayer;
import com.codename1.maps.layers.PointLayer;
import com.codename1.maps.layers.PointsLayer;
import com.codename1.maps.providers.GoogleMapsProvider;
import com.codename1.ui.Button;
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.list.DefaultListModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Copyright (coord) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class MapView extends View {

    private Coord lastLocation;

    public String getDisplayName() {
        return "MapView";
    }
    Form map;

    public MapView(String name) {
        super(name);
        this.value = "map";
        this.unit = "xx";
    }

    private enum TERMINAL_CHOICE {

        TX, RX
    };

    // ping pong for selecting a terminal by clicking
    private TERMINAL_CHOICE currentChoice = TERMINAL_CHOICE.TX;
    // need to remember these linesSat which are removed and added as selection
    // of satellites or terminals changes

    private LinesLayer tXline;          // this is for satellite, tx, rx
    private LinesLayer rXline;

    public void showTerminal(final Selection selection,
            final MapComponent mc, String termName, final LinesLayer tXline,
            final LinesLayer rXline) {
        try {
            Log.p("MapView: displaying terminal " + termName
                    + " for " + selection.getBand() + " band.", Log.DEBUG);
            Image blue_pin = Image.createImage("/blue_pin.png");
            Image red_pin = Image.createImage("/red_pin.png");

            Terminal terminal = Terminal.terminalHash.get(termName);
            if (terminal == null) {
                Log.p("MapView: terminal is null " + termName, Log.DEBUG);
            }

            PointsLayer pl = new PointsLayer();
            pl.setPointIcon(blue_pin);

            // Coord takes it in degrees.   Don't use true for projected
            Coord c = new Coord(Math.toDegrees(terminal.getLatitude()),
                    Math.toDegrees(terminal.getLongitude()));

            final PointLayer p = new PointLayer(c, terminal.getName(), blue_pin);

            p.setDisplayName(false);   // it clutters
            pl.addPoint(p);

            pl.addActionListener(new ActionListener() {
                // need to get PointLayer and not PointsLayer

                public void actionPerformed(ActionEvent evt) {
                    PointLayer plTerm = (PointLayer) evt.getSource();

                    // Mercator is the cylindrical projection.  Don't
                    // know why this has to be called
                    Coord coord = Mercator.inverseMercator(plTerm.getLatitude(),
                            plTerm.getLongitude());

                    // if approved change the selected terminal as tx or rx
                    changeTerminal(selection, mc, plTerm, coord);

                }
            });
            mc.addLayer(pl);
            // Google coordinatges are in degrees (no minutes, seconds)
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // used to remove icons etc. for previous selected satellite
    private class PreviousSatellite {

        public Satellite satellite;
        public PointLayer pointLayer;
        public PointsLayer pointsLayer;
        public ArrayList<PointsLayer> pointsSat = new ArrayList<PointsLayer>();
// pointsSat of beams of selected satellite
        public ArrayList<LinesLayer> linesSat = new ArrayList<LinesLayer>();
// linesSat of beams of selec satellite

    }

    // used in two places (showBeams)
    final PreviousSatellite prevSat = new PreviousSatellite();

    // display a satellite as a point on the equator
    public void showSatellite(final Selection selection,
            final MapComponent mc, String satName, final LinesLayer tXline,
            final LinesLayer rXline) {

        // 
        try {
            Log.p("MapView: displaying satellite " + satName
                    + " for " + selection.getBand() + " band.", Log.DEBUG);
            final Image blue_pin = Image.createImage("/blue_pin.png");
            final Image red_pin = Image.createImage("/red_pin.png");

            Satellite satellite = Satellite.satelliteHash.get(satName);
            if (satellite == null) {
                Log.p("MapView: satellite is null " + satName, Log.DEBUG);
            }

            final PointsLayer plSat = new PointsLayer();

            // default is blue
            Image pin;

            if (satellite == selection.getSatellite()) {
                pin = red_pin;
            } else {
                pin = blue_pin;
            }

            plSat.setPointIcon(pin);

            // Coord takes it in degrees.   Don't use true for projected
            Coord coord = new Coord(Math.toDegrees(satellite.getLatitude()),
                    Math.toDegrees(satellite.getLongitude()));

            final PointLayer pSat = new PointLayer(coord, satellite.getName(), pin);

            pSat.setDisplayName(false);   // it clutters
            plSat.addPoint(pSat);

            // memorize the currently selected satellite
            if (satellite == selection.getSatellite()) {
                prevSat.pointLayer = pSat;
                prevSat.pointsLayer = plSat;
                prevSat.satellite = satellite;
            }
            plSat.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    PointLayer plSelSat = (PointLayer) evt.getSource();

                    // Mercator is the cylindrical projection.  Don't
                    // know why this has to be called
                    Coord coordSelSat = Mercator.inverseMercator(plSelSat.getLatitude(),
                            plSelSat.getLongitude());
                    // get the point in this point layer to print 
                    Boolean ans = Dialog.show("GEO_Satellite", plSelSat.getName() + " at Long|Lat "
                            + Com.toDMS(Math.toRadians(coordSelSat.getLongitude())) + "|"
                            + Com.toDMS(Math.toRadians(coordSelSat.getLatitude()))
                            + "Select this satellite?", "Yes", "No");
                    if (ans) {
                        Satellite satellite = Satellite.satelliteHash.get(plSelSat.getName());
                        if (satellite == null) {
                            Log.p("Mapview: can't find satellite " + plSelSat.getName(), Log.DEBUG);
                        } else {
                            // change satellite and update linesSat
                            Log.p("Mapview: selecting satellite " + plSelSat.getName(), Log.DEBUG);
                            // remove beams of old satellite
                            if (prevSat != null) {
                                removeBeams(prevSat.satellite, prevSat.pointsSat,
                                        prevSat.linesSat, mc);

                                prevSat.pointLayer.setIcon(blue_pin);
                                prevSat.pointsLayer.setPointIcon(blue_pin);

                            }
                            selection.setSatellite(satellite);
                            plSat.setPointIcon(red_pin);
                            plSelSat.setIcon(red_pin);
                            prevSat.satellite = selection.getSatellite();
                            prevSat.pointLayer = plSelSat;
                            prevSat.pointsLayer = plSat;
                            
                               selection.getSatelliteView().spin.
                            setSelectedItem(selection.getSatellite().getName());

                            drawBeams(selection, prevSat.pointsSat, prevSat.linesSat, mc);

                            // draw tx and rx lines for terminals
                            showLines(selection, mc);

                        }
                    }

                    Log.p("MapView: satellite " + pSat.getName()
                            + " long | lat " + " "
                            + coordSelSat.getLongitude() + "|"
                            + coordSelSat.getLatitude(), Log.DEBUG);

                }
            });
            mc.addLayer(plSat);
            // Google coordinatges are in degrees (no minutes, seconds)
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    // remove lines and points of older satellite
    private void removeBeams(Satellite satellite, ArrayList<PointsLayer> pointsSat,
            ArrayList<LinesLayer> linesSat, MapComponent mc) {
        if (pointsSat == null) {
            return;
        }
        if (linesSat == null) {
            return;
        }
        for (PointsLayer p : pointsSat) {
            Log.p("MapView: removebeams removing points " + p + " for "
                    + satellite, Log.DEBUG);
            mc.removeLayer(p);
        }
        // this array list does not follow contour/line structure, all
        // lines are present in a flat list
        for (LinesLayer l : linesSat) {
            Log.p("MapView: removebeams removing lines " + l
                    + " for " + satellite, Log.DEBUG);
            mc.removeLayer(l);
        }

    }

    // draw lines and points of newly selected satellite
    public void drawBeams(Selection selection, ArrayList<PointsLayer> pointsSat,
            ArrayList<LinesLayer> linesSat, MapComponent mc) {
        Hashtable<String, Satellite.Beam> beams;
              beams  = selection.getSatellite().getBeams(selection);
        
        // they will be emptied (not nulled, not recreated).  Also, even if the beams is null, these 
        // could have been non empty because of previous satellite
        
        pointsSat.clear();
        linesSat.clear();
            
        if (beams == null) {
            Log.p("MapView: no beams found for satellite nulling older"
                    + selection.getSatellite(), Log.DEBUG);
   
            return;
        } else {
            try {
                Image blue_pin = Image.createImage("/blue_pin.png");

                if (beams == null) {
                    Log.p("MapView: drawbeams beams is null", Log.DEBUG);
                } else {
                    for (String beam : beams.keySet()) {
                        Log.p("Mapview: drawbeams processing beam " + beam
                                + " for satellite " + selection.getSatellite(), Log.DEBUG);
                        // first all the points
                        for (Satellite.Point point : beams.get(beam).points) {
                            Log.p("  Mapview: drawbeams processing point " + point.name,
                                    Log.DEBUG);
                            PointLayer p = new PointLayer(new Coord(Math.toDegrees(point.latitude),
                                    Math.toDegrees(point.longitude)),
                                    point.name, blue_pin);
                            PointsLayer psl = new PointsLayer();

                            //if (mc.getZoomLevel() > 10) {
                            p.setDisplayName(true);

                            psl.addPoint(p);
                            psl.setPointIcon(blue_pin);
                            mc.addLayer(psl);
                            pointsSat.add(psl);

                        }

                        // then all the contours
                        for (Satellite.Contour contour : beams.get(beam).contours) {
                            Log.p("  Mapview: drawbeams processing contour " + contour.name,
                                    Log.DEBUG);

                            for (Satellite.Line line : contour.lines) {
                                Log.p("    Mapview: drawbeams processing line " + line.position,
                                        Log.DEBUG);

                                // start a new line 
                                LinesLayer ll = new LinesLayer();
                                linesSat.add(ll);

                                int segments = line.latitude.size();
                                if (segments != line.longitude.size()) {
                                    Log.p("        MapView: lat and long have different size "
                                            + segments, Log.DEBUG);
                                }

                                for (int i = 0; i < segments; i++) {

                                    Double lat = Math.toDegrees(line.latitude.toArray(new Double[0])[i]);

                                    Double lng = Math.toDegrees(line.longitude.toArray(new Double[0])[i]);

                                    Coord start = new Coord(lat, lng);
                                    if ((i + 1) < segments) {
                                        Double lat2 = Math.toDegrees(line.latitude.toArray(new Double[0])[i + 1]);

                                        Double lng2 = Math.toDegrees(line.longitude.toArray(new Double[0])[i + 1]);

                                        Coord end = new Coord(lat2, lng2);

                                        ll.addLineSegment(new Coord[]{start, end});
                                        ll.lineColor(contour.color);         // red for transmit

                                    }

                                }
                                // add in the list of LinesLayer for future removal
                                mc.addLayer(ll);

                            }
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // display two linesSat for transmit and receive  for selected satellite
// tX terminal and rX terminal and change icon colors
    public void showLines(final Selection selection, final MapComponent mc) {

        Coord tx, cSatellite, rx;

        // remove old linesSat first (if they exist)
        if (tXline != null) {

            Log.p("Mapview: removing tx line " + tXline, Log.DEBUG);
            mc.removeLayer(tXline);
        }
        if (rXline != null) {
            Log.p("Mapview: removing rx line " + rXline, Log.DEBUG);
            mc.removeLayer(rXline);
        }
        // can't remove old line segments so create a  new object
        tXline = new LinesLayer();
        rXline = new LinesLayer();

        tx = new Coord(Math.toDegrees(selection.gettXterminal().getLatitude()),
                Math.toDegrees(selection.gettXterminal().getLongitude()));

        cSatellite = new Coord(Math.toDegrees(selection.getSatellite().getLatitude()),
                Math.toDegrees(selection.getSatellite().getLongitude()));

        tXline.addLineSegment(new Coord[]{tx, cSatellite});
        tXline.lineColor(0xFF0000);         // red for transmit
        mc.addLayer(tXline);

        rx = new Coord(Math.toDegrees(selection.getrXterminal().getLatitude()),
                Math.toDegrees(selection.getrXterminal().getLongitude()));

        cSatellite = new Coord(Math.toDegrees(selection.getSatellite().getLatitude()),
                Math.toDegrees(selection.getSatellite().getLongitude()));

        Log.p("MapView: drawing lines among " + rx + " " + cSatellite
                + " " + tx, Log.DEBUG);

        rXline.addLineSegment(new Coord[]{rx, cSatellite});
        mc.addLayer(rXline);
        rXline.lineColor(0x0000FF);  // Blue for receive
    }

    // createa  new Map Form everytime this is called.  Other forms are stored
    // in memory but Map seems to be bulky so recreated
    public Form createView(final Selection selection) {

        MapComponent mcLoc = null;
        try {
            // giving exception 
            // Location loc = LocationManager.getLocationManager().getCurrentLocation();
            Coord satLocation = new Coord(selection.getSatellite().getLatitude(),
                    selection.getSatellite().getLongitude(), true);

            // this would not work if longPointerPress was overriden in MapComponent
            mcLoc = new MapComponent(
                    new GoogleMapsProvider("AIzaSyBEUsbb2NkrYxdQSG-kUgjZCoaLY0QhYmk"),
                    satLocation, 5);
        } catch (Exception d) {
            Log.p("MapView: CreateView can't get current location", Log.WARNING);
        }

        final MapComponent mc = mcLoc;
        // initialize static tx and rx linesSat
        rXline = null;
        tXline = null;

        // now draw linesSat between terminals and satellite
        showLines(selection, mc);

        // show all satellites on the map
        for (String sat : selection.getBandSatellite().
                get(selection.getBand())) {
            showSatellite(selection, mc, sat, tXline, rXline);
            if (Satellite.satelliteHash.get(sat) == selection.getSatellite()) {

                // first remove old lines if any
                //  since each MapView is newly created 
                // NOT NEEDED removeBeams(selection, pointsSat, linesSat, mc);
                // show the beams of the selected satellite
                drawBeams(selection, prevSat.pointsSat, prevSat.linesSat, mc);
            }
        }
        // now display all terminal all terminals
        for (String term : selection.getVisibleTerminal().
                get(Selection.VISIBLE.YES)) {
            showTerminal(selection, mc, term, tXline, rXline);
        }

        map = new Form(getName()) {
            @Override
            // long press creates a new Terminal
            public void longPointerPress(int x, int y) {
                try {
                    Image blue_pin = Image.createImage("/blue_pin.png");
                    Image red_pin = Image.createImage("/red_pin.png");

                    // this is not correct in the model
                    Log.p("Map: your location in x|y " + x + "|" + y, Log.DEBUG);
                    PointsLayer pslNewTerm = new PointsLayer();
                    pslNewTerm.setPointIcon(blue_pin);
                    String name;
                    Coord coordNewTerm = mc.getCoordFromPosition(x, y);
                        // dc.setProjected(true);  // WRONG

                    // get a name which is unique
                    name = "T" + java.lang.String.valueOf((int) coordNewTerm.getLongitude())
                            + String.valueOf((int) coordNewTerm.getLatitude());
                    if (Terminal.terminalHash.get(name) != null) {
                        Log.p("Mapview:  terminal already exists " + name, Log.WARNING);
                        // use more precision
                        name = "T" + Com.toDMS(coordNewTerm.getLongitude())
                                + Com.toDMS(coordNewTerm.getLatitude());
                        if (Terminal.terminalHash.get(name) != null) {
                            name = name + "A";
                        }
                    }

                    final PointLayer plNewTerm = new PointLayer(coordNewTerm, name, blue_pin);

                    plNewTerm.setDisplayName(true);
                    pslNewTerm.addPoint(plNewTerm);

                    // create a new terminal
                    Terminal newTerm = new Terminal(name);
                    newTerm.setLatitude(Math.toRadians(coordNewTerm.getLatitude()));
                    newTerm.setLongitude(Math.toRadians(coordNewTerm.getLongitude()));
                    // newTerm.setBand(selection.getBand()); // not used
                    // update the list of visible terminals for this view

                    // now update items for various views
                    selection.initVisibleTerminal();  // re populate visible

                    Log.p("Map: new terminal " + name + " created", Log.DEBUG);

                    // update the models for tx and rx combos in master view
                    selection.getTxView().spin.setModel(new DefaultListModel(
                            selection.getVisibleTerminal().get(Selection.VISIBLE.YES)));

                    selection.getRxView().spin.setModel(new DefaultListModel(
                            selection.getVisibleTerminal().get(Selection.VISIBLE.YES)));

                    // set current Tx and Rx terminals again since models have changed
                    // (only one will get set again in changeTerminal)
                    selection.getRxView().spin.setSelectedItem(
                            selection.getrXterminal().getName());

                    selection.getTxView().spin.setSelectedItem(
                            selection.gettXterminal().getName());

                    // now change terminal (since this was selected at creation
                    changeTerminal(selection, mc, plNewTerm, coordNewTerm);

                    pslNewTerm.addActionListener(new ActionListener() {
                        // need to get PointLayer and not PointsLayer

                        public void actionPerformed(ActionEvent evt) {
                            PointLayer pnew = (PointLayer) evt.getSource();

                            // they kept it in internal format, so call this to
                            // make it back to WGS84 (add point had called fromWGS
                            Coord m = Mercator.inverseMercator(pnew.getLatitude(),
                                    pnew.getLongitude());

                            // get the point in this point layer to print 
                            Log.p("MapView: new terminal current long | lat "
                                    + m.getLongitude() + "|" + m.getLatitude(), Log.DEBUG);

                            // TODO ??? not needed changeTerminal(selection, mc, pnew, m);
                        }
                    });
                    mc.addLayer(pslNewTerm);
                    // Google coordinatges are in degrees (no minutes, seconds)
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        };

        map.setLayout(
                new BorderLayout());
        map.setScrollable(
                false);
        // override pointerPressed to locate new positions 

        // putMeOnMap(mc);
        // mc.zoomToLayers();  // too sparse
        mc.setZoomLevel(2); // see if does the job
        map.addComponent(BorderLayout.CENTER, mc);

        return map;
    }

    public void changeTerminal(Selection selection, MapComponent mc,
            PointLayer pnew, Coord m) {

        Boolean ans = Dialog.show("Terminal", pnew.getName() + " at Long|Lat "
                + Com.toDMS(Math.toRadians(m.getLongitude())) + "|"
                + Com.toDMS(Math.toRadians(m.getLatitude()))
                + "\nSelect this terminal as " + currentChoice + "?", "Yes", "No");

        if (ans) {
            Terminal terminal = Terminal.terminalHash.get(pnew.getName());
            if (terminal == null) {
                Log.p("Mapview: can't find terminal " + pnew.getName() + " for "
                        + currentChoice, Log.DEBUG);
            } else {
                // change terminal and update linesSat
                Log.p("Mapview: selecting terminal " + pnew.getName(), Log.DEBUG);

                if (currentChoice == TERMINAL_CHOICE.TX) {
                    selection.settXterminal(terminal);
                    // update the selection of TxView 

                    selection.getTxView().spin.
                            setSelectedItem(terminal.getName());
                    
                    selection.gettXterminal().gettXantenna().
                            setBand(selection.getBand());

                    currentChoice = TERMINAL_CHOICE.RX;
                    Log.p("MapView: changeterminal() has select TX "
                            + terminal.getName(), Log.DEBUG);

                } else {
                    selection.setrXterminal(terminal);
                    // update the model and selection of TxView 
                    selection.getRxView().spin.
                            setSelectedItem(terminal.getName());
                    
                       selection.getrXterminal().getrXantenna().
                            setBand(selection.getBand());

                    Log.p("MapView: changeterminal() has select RX "
                            + terminal.getName(), Log.DEBUG);
                    currentChoice = TERMINAL_CHOICE.TX;
                }

                showLines(selection, mc);

            }

        } else {
            Log.p("MapView: terminal is not selected "
                    + pnew.getName(), Log.DEBUG);
        }
    }
}
