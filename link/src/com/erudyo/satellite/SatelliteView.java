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
    public ComboBox spin;
    //private Label label;
    //private Label subLabel;

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
    
    // override getWidget to create a Combobox driven by selected band
    public Component getWidget(final Selection selection) {
         Label l = new Label(getName());
         
         // get selected band
         RfBand.Band band = selection.getBand();
        
 
        final ComboBox combo = new ComboBox();      

        // indexSatellite has all satellites, so get the band specific list
        
        if (selection.getBandSatellite().get(band) == null) {
            System.out.println("Can't find satellites for band " + band);
            return l;
        } 
        
        // create model for all satellites of selected band
        ListModel model = new DefaultListModel
                (selection.getBandSatellite().get(band));
        
        // show the selected satellite
        int index = combo.getSelectedIndex();
        
        combo.setModel(model);
        
        // set the satellite view present in the selection (and not this.spin)
       
        selection.getSatelliteView().spin = combo;
       
        // selection.setSatelliteView (spin);

        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                int index = combo.getSelectedIndex();
                
                // get satellite instance from index and set the selection
                selection.setSatellite(Satellite.indexSatellite.toArray(new 
                        Satellite[0])[index]);
               
            
                System.out.println(combo.getSelectedItem());
            }
        });

        // update other fields which are displayed, based on current selection
        updateValues(selection);
     
        // combo box created so return it
        return combo;
    }
    
    // update from the current selection of the Satellite
    public void updateValues(Selection selection) {
          this.summary = Com.shortText(selection.getSatellite().getLongitude());
        this.value = Com.shortText(selection.getSatellite().getEIRP());
        this.unit = Com.shortText(selection.getSatellite().getGain());
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
