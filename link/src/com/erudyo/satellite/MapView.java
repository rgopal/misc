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
import com.codename1.maps.layers.PointLayer;
import com.codename1.maps.layers.PointsLayer;
import com.codename1.maps.providers.GoogleMapsProvider;
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import java.io.IOException;

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

    public Form createView(Selection selection) {

        // this would not work if longPointerPress was overriden in MapComponent
        final MapComponent mc = new MapComponent(new GoogleMapsProvider("AIzaSyBEUsbb2NkrYxdQSG-kUgjZCoaLY0QhYmk"));

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
                    name = "T" + java.lang.String.valueOf((int) c.getLongitude())
                            + String.valueOf((int) c.getLatitude());
                    final PointLayer p = new PointLayer(c, name, blue_pin);

                    // TODO - create a new terminal
                    Log.p("Map: new terminal " + name + " created", Log.DEBUG);

                    p.setDisplayName(true);
                    pl.addPoint(p);

                    pl.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent evt) {
                            PointLayer pnew = (PointLayer) evt.getSource();
                            // System.out.println("pressed " + p);

                            Log.p("Map: current coordinates "
                                    + pnew.getLongitude() + "|" + pnew.getLatitude(), Log.DEBUG);

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

        putMeOnMap(mc);

        mc.zoomToLayers();

        map.addComponent(BorderLayout.CENTER, mc);

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
