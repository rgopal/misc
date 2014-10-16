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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
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

    public void showSatellite(final Selection selection,
            final MapComponent mc, String satName, final LinesLayer tXline,
            final LinesLayer rXline) {

        try {
            Log.p("MapView: displaying satellite " + satName
                    + " for " + selection.getBand() + " band.", Log.DEBUG);
            Image blue_pin = Image.createImage("/blue_pin.png");
            Image red_pin = Image.createImage("/red_pin.png");

            Satellite satellite = Satellite.satelliteHash.get(satName);
            if (satellite == null) {
                Log.p("MapView: satellite is null " + satName, Log.DEBUG);
            }

            PointsLayer pl = new PointsLayer();
            pl.setPointIcon(red_pin);

            // Coord takes it in degrees.   Don't use true for projected
            Coord c = new Coord(Math.toDegrees(satellite.getLatitude()),
                    Math.toDegrees(satellite.getLongitude()));

            final PointLayer p = new PointLayer(c, satellite.getName(), red_pin);

            p.setDisplayName(false);   // it clutters
            pl.addPoint(p);

            pl.addActionListener(new ActionListener() {
                // need to get PointLayer and not PointsLayer

                public void actionPerformed(ActionEvent evt) {
                    PointLayer pnew = (PointLayer) evt.getSource();

                    // Mercator is the cylindrical projection.  Don't
                    // know why this has to be called
                    Coord m = Mercator.inverseMercator(pnew.getLatitude(),
                            pnew.getLongitude());
                    // get the point in this point layer to print 
                    Boolean ans = Dialog.show("GEO_Satellite", pnew.getName() + " at Long|Lat "
                            + Com.toDMS(Math.toRadians(m.getLongitude())) + "|"
                            + Com.toDMS(Math.toRadians(m.getLatitude()))
                            + "Select this satellite?", "Yes", "No");
                    if (ans) {
                        Satellite satellite = Satellite.satelliteHash.get(pnew.getName());
                        if (satellite == null) {
                            Log.p("Mapview: can't find satellite " + pnew.getName(), Log.DEBUG);
                        } else {
                            // change satellite and update lines
                            Log.p("Mapview: selecting satellite " + pnew.getName(), Log.DEBUG);
                            selection.setSatellite(satellite);
                            mc.removeLayer(tXline);
                            mc.removeLayer(rXline);
                            showLines(selection, mc, tXline, rXline);

                        }
                    }

                    Log.p("MapView: satellite " + p.getName()
                            + " long | lat " + " "
                            + m.getLongitude() + "|"
                            + m.getLatitude(), Log.DEBUG);

                }
            });
            mc.addLayer(pl);
            // Google coordinatges are in degrees (no minutes, seconds)
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void showLines(final Selection selection, final MapComponent mc,
            final LinesLayer tXline, final LinesLayer rXline) {

        Coord tx, cSatellite, rx;

        tx = new Coord(Math.toDegrees(selection.gettXterminal().getLatitude()),
                Math.toDegrees(selection.gettXterminal().getLongitude()));

        cSatellite = new Coord(Math.toDegrees(selection.getSatellite().getLatitude()),
                Math.toDegrees(selection.getSatellite().getLongitude()));

        tXline.addLineSegment(new Coord[]{tx, cSatellite});
        tXline.lineColor(0xFF0000);
        mc.addLayer(tXline);

        rx = new Coord(Math.toDegrees(selection.getrXterminal().getLatitude()),
                Math.toDegrees(selection.getrXterminal().getLongitude()));

        cSatellite = new Coord(Math.toDegrees(selection.getSatellite().getLatitude()),
                Math.toDegrees(selection.getSatellite().getLongitude()));

        Log.p("MapView: drawing lines among " + rx + " " + cSatellite
                + " " + tx, Log.DEBUG);

        rXline.addLineSegment(new Coord[]{rx, cSatellite});
        mc.addLayer(rXline);
        rXline.lineColor(0x0000FF);  // Blue
    }

    public Form createView(final Selection selection) {
        

        try {
            Location loc = LocationManager.getLocationManager().getCurrentLocation();
            lastLocation = new Coord(loc.getLatitude(), loc.getLongtitude(), true);

            final LinesLayer rXline = new LinesLayer();
            final LinesLayer tXline = new LinesLayer();

            // this would not work if longPointerPress was overriden in MapComponent
            final MapComponent mc = new MapComponent(new GoogleMapsProvider("AIzaSyBEUsbb2NkrYxdQSG-kUgjZCoaLY0QhYmk"), loc, 5);

            // now draw lines between terminals and satellite
            showLines(selection, mc, tXline, rXline);

            // show all satellites on the map
            // showSatellites(selection, mc);
            for (String sat : selection.getBandSatellite().
                    get(selection.getBand())) {
                showSatellite(selection, mc, sat, tXline, rXline);
            }
            // now display all terminal all terminals
            for (String term : selection.getBandTerminal().
                    get(selection.getBand())) {
                try {
                    Log.p("MapView: displaying terminal " + term
                            + " for " + selection.getBand() + " band.", Log.DEBUG);
                    Image blue_pin = Image.createImage("/blue_pin.png");
                    Image red_pin = Image.createImage("/red_pin.png");

                    Terminal terminal = Terminal.terminalHash.get(term);
                    if (terminal == null) {
                        Log.p("MapView: terminal is null " + term, Log.DEBUG);
                    }

                    PointsLayer pl = new PointsLayer();
                    pl.setPointIcon(red_pin);

                    // Coord takes it in degrees.   Don't use true for projected
                    Coord c = new Coord(Math.toDegrees(terminal.getLatitude()),
                            Math.toDegrees(terminal.getLongitude()));

                    final PointLayer p = new PointLayer(c, terminal.getName(), blue_pin);

                    p.setDisplayName(false);   // it clutters
                    pl.addPoint(p);

                    pl.addActionListener(new ActionListener() {
                        // need to get PointLayer and not PointsLayer

                        public void actionPerformed(ActionEvent evt) {
                            PointLayer pnew = (PointLayer) evt.getSource();

                            // Mercator is the cylindrical projection.  Don't
                            // know why this has to be called
                            Coord m = Mercator.inverseMercator(pnew.getLatitude(),
                                    pnew.getLongitude());
                            // get the point in this point layer to print 
                            Dialog.show("Terminal", pnew.getName() + " at Long|Lat "
                                    + Com.toDMS(Math.toRadians(m.getLongitude())) + "|"
                                    + Com.toDMS(Math.toRadians(m.getLatitude())), "OK", null);
                            Log.p("MapView: terminal " + p.getName()
                                    + " long | lat " + " "
                                    + m.getLongitude() + "|"
                                    + m.getLatitude(), Log.DEBUG);

                        }
                    });
                    mc.addLayer(pl);
                    // Google coordinatges are in degrees (no minutes, seconds)
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            map = new Form(getName()) {
                @Override
                public void longPointerPress(int x, int y) {
                    try {
                        Image blue_pin = Image.createImage("/blue_pin.png");
                        Image red_pin = Image.createImage("/red_pin.png");

                        // this is not correct in the model
                        Log.p("Map: your location in x|y " + x + "|" + y, Log.DEBUG);
                        PointsLayer pl = new PointsLayer();
                        pl.setPointIcon(blue_pin);
                        String name;
                        Coord c = mc.getCoordFromPosition(x, y);
                        // dc.setProjected(true);  // WRONG
                        name = "T" + java.lang.String.valueOf((int) c.getLongitude())
                                + String.valueOf((int) c.getLatitude());

                        final PointLayer p = new PointLayer(c, name, blue_pin);

                        // TODO - create a new terminal
                        Log.p("Map: new terminal " + name + " created", Log.DEBUG);

                        p.setDisplayName(true);
                        pl.addPoint(p);
                        Terminal term = new Terminal(name);
                        term.setLatitude(Math.toRadians(c.getLatitude()));
                        term.setLongitude(Math.toRadians(c.getLongitude()));
                        term.setBand(selection.getBand());

                        pl.addActionListener(new ActionListener() {
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

                            }
                        });
                        mc.addLayer(pl);
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
            mc.setZoomLevel(3); // see if does the job
            map.addComponent(BorderLayout.CENTER, mc);

        } catch (Exception d) {
            Log.p("MapView: CreateView can't get current location", Log.WARNING);
        }
        return map;
    }

    public void putMeOnMap(MapComponent map) {

        try {
            final Image blue_pin = Image.createImage("/blue_pin.png");
            final Image red_pin = Image.createImage("/red_pin.png");

            Location loc = LocationManager.getLocationManager().getCurrentLocation();
            lastLocation = new Coord(loc.getLatitude(), loc.getLongtitude());

            PointsLayer pl = new PointsLayer();
            pl.setPointIcon(blue_pin);
            PointLayer p = new PointLayer(lastLocation, "Current Location", red_pin);
            p.setDisplayName(true);
            pl.addPoint(p);
            pl.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    PointLayer p = (PointLayer) evt.getSource();
                    // System.out.println("pressed " + p);

                    Log.p("Map: Current position coordinates "
                            + p.getLongitude() + "|" + p.getLatitude(), Log.DEBUG);

                    // Dialog.show("Current Position", "You Coordinates" + "\n" + p.getLatitude() + "|" + p.getLongitude(), "Ok", null);
                }
            });
            map.addLayer(pl);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
