/*
 * STATUS: probably not needed.   Use the init function in Satellite for GEO
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
        setR0(35786E3);       // height of GEO satellite
        setLatitude(0.0);         // latitude is zero
        orbit = Com.Orbit.GEO;
    }
    public GeoSatellite(String n) {
        super(n);
        init();
    }
   
}
