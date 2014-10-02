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
import com.codename1.ui.Button;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.Resources;

public class TxView extends View {

    public ComboBox spin;
    public Label label;

    public TxView() {

    }

    public TxView(Selection selection) {
        super.name = "Tx Terminal";
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
        ListModel model = new DefaultListModel(selection.getBandTerminal().get(band));

        combo.setModel(model);

        // set the selected Tx terminal during initialization
        selection.settXterminal(Terminal.terminalHash.get(
                (String) combo.getSelectedItem()));

        // Band combobox should be able to change this
        selection.getTxView().spin = combo;

        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                // set the selected Tx terminal
                selection.settXterminal(Terminal.terminalHash.get(
                        (String) combo.getSelectedItem()));

                // update other values dependent on this satellite
                // updateValues(selection);
                // change the subwidget, label, and sublabel etc.
                selection.getTxView().label.setText(
                        Terminal.terminalHash.get(
                                (String) combo.getSelectedItem()).getName());

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

        final Label label = new Label((String) selection.getTxView().spin.getSelectedItem());
 // terminal is not set yet               selection.gettXterminal().getName());      

        // set the tx view present in the selection
        selection.getTxView().label = label;

        // combo box created so return it
        return label;
    }

    public String getDisplayName() {
        return name;
    }

    public Form createView(Selection selection) {
        Form sub = new Form(this.name);

        Container cnt = new Container(new BorderLayout());
        sub.addComponent(cnt);

        // there are six items in Views.  Hardcoded table. Name, value, unit
        cnt.setLayout(new TableLayout(9, 3));

        // now go sequentially through the Tx terminal fields
        Terminal ter = selection.gettXterminal();
        Label L01 = new Label("Center Frequency");
        Label L02 = new Label(Com.shortText(ter.getAntenna().getFrequency() / 1E9));
        Label L03 = new Label("GHz " + ter.getBand());
        cnt.addComponent(L01);
        cnt.addComponent(L02);
        cnt.addComponent(L03);

        Label L11 = new Label("Amplifier Power");
        Label L12 = new Label(String.valueOf(ter.getAmplifier().getPower()));
        Label L13 = new Label("W");
        cnt.addComponent(L11);
        cnt.addComponent(L12);
        cnt.addComponent(L13);

        Label L21 = new Label("Antenna Diameter");
        Label L22 = new Label(Com.shortText(ter.getAntenna().getDiameter()));
        Label L23 = new Label("m");
        cnt.addComponent(L21);
        cnt.addComponent(L22);
        cnt.addComponent(L23);

        Label L2A1 = new Label("   Efficiency");
        Label L2A2 = new Label(Com.shortText(ter.getAntenna().getEfficiency()));
        Label L2A3 = new Label(" ");
        cnt.addComponent(L2A1);
        cnt.addComponent(L2A2);
        cnt.addComponent(L2A3);

        Label L31 = new Label("   Gain");
        Label L32 = new Label(Com.shortText(ter.getAntenna().getGain()));
        Label L33 = new Label("dB");
        cnt.addComponent(L31);
        cnt.addComponent(L32);
        cnt.addComponent(L33);

        Label L41 = new Label("   3dB Angle");
        Label L42 = new Label(Com.toDMS(ter.getAntenna().getThreeDBangle()));
        Label L43 = new Label("degrees");
        cnt.addComponent(L41);
        cnt.addComponent(L42);
        cnt.addComponent(L43);

        Label L4A1 = new Label("   Pointing Loss");
        Label L4A2 = new Label(Com.shortText(ter.getAntenna().getDepointingLoss()));
        Label L4A3 = new Label(" ");
        cnt.addComponent(L4A1);
        cnt.addComponent(L4A2);
        cnt.addComponent(L4A3);

        Label L51 = new Label("Terminal EIRP");
        Label L52 = new Label(Com.shortText(ter.getEIRP()));
        Label L53 = new Label("dBW");
        cnt.addComponent(L51);
        cnt.addComponent(L52);
        cnt.addComponent(L53);
        sub.setScrollable(true);

        // have a multi-row table layout and dump the transmit terminal values
        return sub;
    }
}
