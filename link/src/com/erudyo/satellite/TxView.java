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
import com.codename1.ui.ComboBox;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.util.Resources;

public class TxView extends View {

    Terminal terminal;
    public ComboBox spin;
    public Label label;
    
    
    public TxView() {

    }


    public TxView(Selection selection) {
        this.terminal = selection.gettXterminal();
      
    }

    public Component getWidget(final Selection selection) {
         Label l = new Label(getName());
         
         // get selected band
         RfBand.Band band = selection.getBand();
  

        final ComboBox combo = new ComboBox();
       

        // indexSatellite has all satellites, so get the band specific list
        
        if (selection.getBandTerminal().get(band) == null) {
            System.out.println("Can't find terminals for band " + band);
            return l;
        } 
        
        // create mtodel for all terminals of selected band
        ListModel model = new DefaultListModel
                (selection.getBandTerminal().get(band));
   
        
        combo.setModel(model);
        
        // Band combobox should be able to change this
        
        selection.getTxView().spin = combo;

        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                 // find the name of the satellite
                String name = (String) combo.getSelectedItem();
              
                Terminal ter = Terminal.terminalHash.get(name);
                
                selection.settXterminal(ter);
                
                // update other values dependent on this satellite
                // updateValues(selection);
               
                // change the subwidget, label, and sublabel etc.
                selection.getTxView().label.setText(ter.getName());
            
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
        
        final Label label = new Label(selection.gettXterminal().getName());      

                
        // set the tx view present in the selection
               selection.getTxView().label = label;
     
        // combo box created so return it
        return label;
    }
    
    public String getDisplayName() {
        return name;
    }

    public Form createView() {
        Form tx = new Form(this.name);

    
        tx.setScrollable(
                true);
            // override pointerPressed to locate new positions 

        return tx;
    }
}
