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

                int index = combo.getSelectedIndex();

                // get terminal instance from index and set the selection
                selection.setRx(Terminal.indexTerminal.toArray(new Terminal[0])[index]);

                // System.out.println(spin.getSelectedItem());
            }
        });

        // combo box created so return it
        return combo;
    }

    public String getDisplayName() {
        return name;
    }

    public RxView(Selection selection) {
        this.terminal = selection.getrXterminal();
        this.name = "Rx";
        this.summary = "" + String.valueOf(terminal.getAntenna().getDiameter())
                + "m " + String.valueOf(terminal.getAmplifier().getPower()) + "W ";
        this.value = "ter";
        this.unit = "dB";
    }

    public Form createView() {
        Form rx = new Form(this.terminal.getName());

        rx.setScrollable(
                false);

        return rx;
    }
}
