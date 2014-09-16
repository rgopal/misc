/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.satellite;

import static com.erudyo.satellite.Com.RE;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 * @author rgopal
 */
public class GeoSatellite extends Satellite {
      
    public GeoSatellite() {
        
        init();
    }
    public void init () {
        setA(42164.2E3);     //semi major axis of GEO orbit
        setV(3075E3);        // GEO satellite velocity
        setR0(Com.RE/.178);       // height of GEO satellite
        setLatitude(0.0);         // latitude is zero
        orbit = Com.Orbit.GEO;
    }
    public GeoSatellite(String n) {
        super(n);
        init();
    }
    public GeoSatellite (String n, String d, String s) {
        super(n,d,s);
        init();
    }
}
