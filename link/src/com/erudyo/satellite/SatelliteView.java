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
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.Label;
import com.codename1.ui.Component;
import com.codename1.ui.ComboBox;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.util.Resources;
import java.util.Hashtable;
import java.util.Vector;

public class SatelliteView extends View {
    private Satellite satellite;

    public SatelliteView () {
        
    }
    public SatelliteView(Selection selection) {
        this.satellite = selection.getSatellite();
        
        this.name = "Satellite";
       
        this.summary = String.valueOf((int) satellite.getAntenna().getGain()) + "K " +
                    String.valueOf((int) satellite.getEIRP()) + "dbM ";
        this.value = "sat";
        this.unit = "dB";
    }
    
    // overload getWidget to create a Combobox driven by selected band
    

    public Component getWidget(final Selection selection) {
         Label l = new Label(getName());
         
         // get selected band
         RfBand.Band band = selection.getBand();
        
       

        final ComboBox spin = new ComboBox();
       

        // indexSatellite has all satellites, so get the band specific list
        
        if (Selection.bandSatellite.get(band) == null) {
            System.out.println("Can't find satellites for band " + band);
            return l;
        } 
        
        // create model for all satellites of selected band
        ListModel model = new DefaultListModel
                (Selection.bandSatellite.get(band));
        
        // show the selected satellite
        int index = spin.getSelectedIndex();
        
        spin.setModel(model);
        
        // Band combobox should be able to change this
        
        selection.setSatelliteView (spin);

        spin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                int index = spin.getSelectedIndex();
                
                // get satellite instance from index and set the selection
                selection.setSatellite(Satellite.indexSatellite.toArray(new 
                        Satellite[0])[index]);
               
            
                System.out.println(spin.getSelectedItem());
            }
        });

        // combo box created so return it
        return spin;
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
