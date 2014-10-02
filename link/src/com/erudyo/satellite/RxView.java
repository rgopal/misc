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

public class RxView extends View {

    private Terminal terminal;
    public ComboBox spin;
    public Label label;

    public RxView() {

    }

    public Component getWidget(final Selection selection) {
        Label l = new Label(getName());

        // get selected band
        RfBand.Band band = selection.getBand();

        final ComboBox combo = new ComboBox();

        // bandTerminal has all terminals, so get the band specific list
        if (selection.getBandTerminal().get(band) == null) {
            System.out.println("Can't find terminals for band " + band);
            return l;
        }

        // create model for all terminals of selected band
        ListModel model = new DefaultListModel(selection.getBandTerminal().get(band));

        combo.setModel(model);
        

        // Band combobox should be able to change this
        selection.getRxView().spin = combo;

        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                // find the name of the satellite
                String name = (String) combo.getSelectedItem();

                Terminal ter = Terminal.terminalHash.get(name);

                selection.setrXterminal(ter);

                // update other values dependent on this satellite
                // updateValues(selection);
                // change the subwidget, label, and sublabel etc.
                selection.getRxView().label.setText(ter.getName());

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

        // does not work final Label label = new Label(selection.getrXterminal().getName()); 
        
        final Label label = new Label((String) selection.getRxView().spin.getSelectedItem());
        // terminal is not set yet selection.gettXterminal().getName());      

        // set the Rx view present in the selection
        selection.getRxView().label = label;

        // combo box created so return it
        return label;
    }

    public String getDisplayName() {
        return name;
    }

    public RxView(Selection selection) {
        this.terminal = selection.getrXterminal();

    }

    public Form createView(Selection selection) {
        Form rx = new Form(this.terminal.getName());

        rx.setScrollable(
                false);

        return rx;
    }
}
