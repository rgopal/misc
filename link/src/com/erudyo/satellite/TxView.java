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
    
    public TxView() {

    }


    public TxView(Selection selection) {
        this.terminal = selection.gettXterminal();
        this.name = "Tx";
        this.summary = "" + String.valueOf(terminal.getAntenna().getDiameter()) +
                       "m " + String.valueOf(terminal.getAmplifier().getPower()) + "W ";
        
        this.value = "23";
        this.unit = "dB";
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Component getWidget(final Selection selection) {
         Label l = new Label(getName());
         
         // get selected band
         RfBand.Band band = selection.getBand();
        
       

        final ComboBox spin = new ComboBox();
       

        // indexSatellite has all satellites, so get the band specific list
        
        if (Selection.bandTerminal.get(band) == null) {
            System.out.println("Can't find terminals for band " + band);
            return l;
        } 
        
        // create model for all terminals of selected band
        ListModel model = new DefaultListModel
                (Selection.bandTerminal.get(band));
        
        // show the selected terminal
        int index = spin.getSelectedIndex();
        
        spin.setModel(model);
        
        // Band combobox should be able to change this
        
        selection.setTxView (spin);

        spin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                int index = spin.getSelectedIndex();
                
                // get terminal instance from index and set the selection
                selection.setTx(Terminal.indexTerminal.toArray(new 
                        Terminal[0])[index]);
               
            
                // System.out.println(spin.getSelectedItem());
            }
        });

        // combo box created so return it
        return spin;
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
