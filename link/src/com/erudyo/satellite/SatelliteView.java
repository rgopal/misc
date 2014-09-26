/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.util.Resources;
import java.util.Hashtable;
import java.util.Vector;

public class SatelliteView extends View {
    private Satellite satellite;

    public SatelliteView () {
        
    }
    public SatelliteView(Satellite satellite) {
        this.satellite = satellite;
        this.name = "Satellite";
        this.summary = String.valueOf((int) satellite.getAntenna().getGain()) + "K " +
                    String.valueOf((int) satellite.getEIRP()) + "dbM ";
        this.value = "sat";
        this.unit = "dB";
    }

   
    public String getDisplayName() {
        return name;
    }
 

    public Form createView() {
        Form sat = new Form(getName());

        sat.setScrollable(
                false);
            // override pointerPressed to locate new positions 

      
        return sat;
    }
}
