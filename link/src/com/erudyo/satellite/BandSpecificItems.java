/*
 * OVERVIEW
 * This was originally nested within Satellite but creating a BandBeams 
 * object from within Satellite static method was not allowed.  So, this 
 * became its own class to get pass that static/non static problem.
 */

package com.erudyo.satellite;

import java.util.Hashtable;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 * @author rgopal
 */
  // can have multiple bands and transponders;
    // in bandBeams private Hashtable<RfBand.Band, Integer> transponders;
    // each band would have its own beam items
    public class BandSpecificItems {

        RfBand.Band band;           // band of these beams
        public double EIRP;       // calculated value
        public double gainTemp;       // they should be calculated dB 1/K
        public double maxEIRP;      // maximum across all beams of a band
        public double maxGT;
        public int transponders;    // number of transponders (same across beams)
        public Antenna rXantenna;  // assumed same chars across beams (for same band)
        public Antenna tXantenna;
        public Amplifier rXamplifier;
        public Amplifier tXamplifier;
        // access a lineString by its name in a hashtable (created from a file)
        Hashtable<String, Satellite.Beam> beams;  // collection of beams.
    }