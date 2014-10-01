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
    public Label label;
    public Label subLabel;

    public SatelliteView () {
        
    }
    public SatelliteView(Selection selection) {
        this.satellite = selection.getSatellite();
      
        // don't call update_values since SatelliteView is still being built
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

        // fires when the list is changed (by user)
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                // find the name of the satellite
                String name = (String) combo.getSelectedItem();
              
                Satellite sat = Satellite.satelliteHash.get(name);
                
                selection.setSatellite(sat);
                
                // update other values dependent on this satellite
                updateValues(selection);
               
                // change the subwidget, label, and sublabel etc.
                selection.getSatelliteView().label.setText(sat.getName());
            
                // System.out.println(combo.getSelectedItem());
            }
        });

     
        // combo box created so return it
        return combo;
    }
    public Component getLabel(final Selection selection) {
         Label l = new Label(getValue());
         
         // get selected band
         RfBand.Band band = selection.getBand();
        
        final Label label = new Label(selection.getSatellite().getName());      

                
        // set the satellite view present in the selection
               selection.getSatelliteView().label = label;
     
        // combo box created so return it
        return label;
    }
    
    // update from the current selection of the Satellite
    public void updateValues(Selection selection) {
        selection.getSatelliteView().summary = Com.shortText(selection.getSatellite().getLongitude());
                selection.getSatelliteView().value = Com.shortText(selection.getSatellite().getEIRP());
        selection.getSatelliteView().unit = Com.shortText(selection.getSatellite().getGain());
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
