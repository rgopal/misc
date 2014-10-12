/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import com.codename1.io.Log;

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
import com.codename1.ui.Slider;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.Resources;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.util.MathUtil;

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
            Log.p("TxView: Can't find terminals for band " + band, Log.DEBUG);
            return l;
        }

        // create mtodel for all terminals of selected band
        ListModel model = new DefaultListModel(
                selection.getBandTerminal().get(band));

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

                Log.p("TxView: Terminal selection "
                        + combo.getSelectedItem(), Log.DEBUG);
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

        // set the tx view present in the selection. Don't use this
        selection.getTxView().label = label;

        // combo box created so return it
        return label;
    }

    public String getDisplayName() {
        return name;
    }

    public Form createView(final Selection selection) {
        Form sub = new Form("Tx " + selection.gettXterminal().getName());

        Container cnt = new Container(new BorderLayout());
        sub.addComponent(cnt);

        // there are six items in Views.  Hardcoded table. Name, value, unit
        TableLayout layout = new TableLayout(13, 3);
        cnt.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setVerticalSpan(2);
        constraint.setWidthPercentage(20);

        // now go sequentially through the Tx terminal fields
        final Terminal ter = selection.gettXterminal();

        Label L01 = new Label("Cent Freq");
        Label L02 = new Label(Com.shortText(ter.gettXantenna().getFrequency() / 1E9));
        Label L03 = new Label("GHz " + ter.getBand());
        cnt.addComponent(L01);
        cnt.addComponent(constraint, L02);
        cnt.addComponent(L03);

        Label L61 = new Label("  Longitude");
        final Label L62 = new Label(Com.toDMS(ter.getLongitude()));
        Label L63 = new Label("deg");

        cnt.addComponent(L61);
        cnt.addComponent(L62);
        cnt.addComponent(L63);

        Label L71 = new Label("  Latitude");
        final Label L72 = new Label(Com.toDMS(ter.getLatitude()));
        Label L73 = new Label("deg");

        cnt.addComponent(L71);
        cnt.addComponent(L72);
        cnt.addComponent(L73);

        Label L11 = new Label("Amp Power");

        final Slider L12 = new Slider();
        L12.setMinValue((int) MathUtil.round(Amplifier.POWER_LO * 10)); // x10
        L12.setMaxValue((int) MathUtil.round(Amplifier.POWER_HI * 10));
        L12.setEditable(true);
        // L12.setPreferredW(8);
        L12.setIncrements(5); //
        L12.setProgress((int) MathUtil.round(ter.getAmplifier().getPower() * 10));
        L12.setRenderValueOnTop(true);

        final Label L13 = new Label(Com.shortText(ter.getAmplifier().
                getPower()) + "W");
        cnt.addComponent(L11);
        cnt.addComponent(L12);
        cnt.addComponent(L13);

        Label L2A1 = new Label("Antenna Eff");
        Label L2A2 = new Label(Com.shortText(ter.gettXantenna().getEfficiency()));
        Label L2A3 = new Label(" ");
        cnt.addComponent(L2A1);
        cnt.addComponent(L2A2);
        cnt.addComponent(L2A3);

        Label L21 = new Label("  Diameter");
        final Slider L22 = new Slider();

        L22.setMinValue((int) MathUtil.round(Antenna.DIAMETER_LO * 10)); // x10
        L22.setMaxValue((int) MathUtil.round(Antenna.DIAMETER_HI * 10));
        L22.setEditable(true);
        //L22.setPreferredW(8);
        L22.setIncrements(5); //
        L22.setProgress((int) MathUtil.round(ter.gettXantenna().getDiameter() * 10));

        L22.setRenderValueOnTop(true);
        final Label L23 = new Label(Com.shortText(ter.gettXantenna().getDiameter()) + "m");
        cnt.addComponent(L21);
        cnt.addComponent(L22);
        cnt.addComponent(L23);

        Label L31 = new Label(" Gain");
        final Label L32 = new Label(Com.shortText(ter.gettXantenna().getGain()));
        Label L33 = new Label("dBi");
        cnt.addComponent(L31);
        cnt.addComponent(L32);
        cnt.addComponent(L33);

        Label L41 = new Label(" 3dB Angle");
        final Label L42 = new Label(Com.toDMS(ter.gettXantenna().getThreeDBangle()));
        Label L43 = new Label("deg");
        cnt.addComponent(L41);
        cnt.addComponent(L42);
        cnt.addComponent(L43);

        // does change so not in combo/sliders
        Label lPointLoss = new Label(" Point Loss");
        final Label valuePointLoss = new Label(Com.shortText(
                ter.gettXantenna().getDepointingLoss()));
        Label unitPointLoss = new Label("dB");
        cnt.addComponent(lPointLoss);
        cnt.addComponent(valuePointLoss);
        cnt.addComponent(unitPointLoss);

        // does not change so not in combo/sliders
        Label lImpLoss = new Label(" Imp Loss");
        final Label valueImpLoss = new Label(Com.shortText(
                ter.getAmplifier().getLFTX()));
        Label unitImpLoss = new Label("dB");
        cnt.addComponent(lImpLoss);
        cnt.addComponent(valueImpLoss);
        cnt.addComponent(unitImpLoss);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);        // whole row

        Label L0A1 = new Label("Terminal");
        L0A1.setAlignment(Component.CENTER);
        cnt.addComponent(constraint, L0A1);

        Label lEIRP = new Label("Term EIRP");
        final Label valueEIRP = new Label(Com.shortText(ter.getEIRP()));
        Label unitEIRP = new Label("dBW");
        cnt.addComponent(lEIRP);
        cnt.addComponent(valueEIRP);
        cnt.addComponent(unitEIRP);

        sub.setScrollable(true);

        // all actions at the end to update other fields
        L12.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("TxView: selected power " + L12.getText(), Log.DEBUG);
                try {
                    selection.gettXterminal().getAmplifier().
                            setPower(Double.parseDouble(L12.getText()) / 10.0);
                    // update EIRP
                    L13.setText(Com.shortText(ter.getAmplifier().
                            getPower()) + "W");
                    valueEIRP.setText(Com.shortText(ter.getEIRP()));
                    // does not change depointing

                } catch (java.lang.NumberFormatException e) {
                    Log.p("TxView: bad number for power " + L12.getText(), Log.DEBUG);
                }
            }
        });

        L22.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("TxView: selected diameter " + L22.getText(), Log.DEBUG);
                try {

                    // convert from cm to m first
                    selection.gettXterminal().gettXantenna().
                            setDiameter(Double.parseDouble(L22.getText()) / 10.0);
                    // update EIRP and three DB
                    L23.setText(Com.shortText(ter.gettXantenna().getDiameter()) + "m");
                    L42.setText(Com.toDMS(ter.gettXantenna().getThreeDBangle()));
                    L32.setText(Com.shortText(ter.gettXantenna().getGain()));
                    valuePointLoss.setText(Com.shortText(
                            ter.gettXantenna().getDepointingLoss()));
                    valueEIRP.setText(Com.shortText(ter.getEIRP()));
                } catch (java.lang.NumberFormatException e) {
                    Log.p("TxView: bad number for diameter " + L22.getText(), Log.DEBUG);

                }

            }
        });

        // have a multi-row table layout and dump the transmit terminal values
        return sub;
    }
}
