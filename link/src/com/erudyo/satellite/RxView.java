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
import com.codename1.io.Log;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Slider;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.Resources;
import com.codename1.util.MathUtil;

public class RxView extends View {

    private Terminal terminal;
    public ComboBox spin;
    public Label label;

    public RxView() {

    }

    public Component getWidget(final Selection selection) {
        Label l = new Label(getName());

        final ComboBox combo = new ComboBox();

        // check the list of terminal visible for the satellite
        if (selection.getVisibleTerminal().get(Selection.VISIBLE.YES) == null) {
            System.out.println("Can't find terminals for satellite  "
                    + selection.getSatellite());
            return l;
        }

        // create model for all terminals of selected band
        ListModel model = new DefaultListModel(
                selection.getVisibleTerminal().get(Selection.VISIBLE.YES));

        combo.setModel(model);

        int position;
        // update selected receive terminal
        if (selection.getVisibleTerminal().get(Selection.VISIBLE.YES).size() < 2) {
            // get the first terminal (only 1)
            position = 0;
        } else {
            // else the second
            position = 1;
        }
        // set the selected receive terminal

        selection.setrXterminal(Terminal.terminalHash.
                get(selection.getVisibleTerminal().
                        get(Selection.VISIBLE.YES).toArray(
                                new String[0])[position]));

        // set the Rx Terminal during initialization
        combo.setSelectedItem(selection.getrXterminal().getName());

        // Band combobox should be able to change this
        selection.getRxView().spin = combo;

        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                // change the the selected  rX satellite
                selection.setrXterminal(Terminal.terminalHash.get(
                        (String) combo.getSelectedItem()));

                // update other values dependent on this satellite
                // updateValues(selection);
                // change the subwidget, label, and sublabel etc.
                selection.getRxView().label.setText(
                        Terminal.terminalHash.get(
                                (String) combo.getSelectedItem()).getName());
                Log.p("RxView: Terminal selection "
                        + combo.getSelectedItem(), Log.DEBUG);

                // System.out.println(combo.getSelectedItem());
            }
        });

        // combo box created so return it
        return combo;
    }

    public Component getLabel(final Selection selection) {
        Label l = new Label(getValue());

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

    public Form createView(final Selection selection) {
        Form sub = new Form("Rx " + selection.getrXterminal().getName());

        Container cnt = new Container(new BorderLayout());
        sub.addComponent(cnt);

        // there are six items in Views.  Hardcoded table. Name, value, unit
        TableLayout layout = new TableLayout(13, 3);
        cnt.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setVerticalSpan(2);
        constraint.setWidthPercentage(20);

        // now go sequentially through the Tx terminal fields
        final Terminal rxTerm = selection.getrXterminal();

        Label L01 = new Label("Cent Freq");
        Label L02 = new Label(Com.shortText(rxTerm.getrXantenna().getFrequency() / 1E9));
        Label L03 = new Label("GHz " + rxTerm.getBand());
        cnt.addComponent(L01);
        cnt.addComponent(constraint, L02);
        cnt.addComponent(L03);

        Label L61 = new Label("  Longitude");
        final Label L62 = new Label(Com.toDMS(rxTerm.getLongitude()));
        Label L63 = new Label("deg");

        cnt.addComponent(L61);
        cnt.addComponent(L62);
        cnt.addComponent(L63);

        Label L71 = new Label("  Latitude");
        final Label L72 = new Label(Com.toDMS(rxTerm.getLatitude()));
        Label L73 = new Label("deg");

        cnt.addComponent(L71);
        cnt.addComponent(L72);
        cnt.addComponent(L73);

        Label L11 = new Label("Noise Figure");

        final Slider lNoiseFig = new Slider();
        lNoiseFig.setMinValue((int) MathUtil.round(Amplifier.NOISE_FIG_LO * 10)); // x10
        lNoiseFig.setMaxValue((int) MathUtil.round(Amplifier.NOISE_FIG_HI * 10));
        lNoiseFig.setEditable(true);
        // lNoiseFig.setPreferredW(8);
        lNoiseFig.setIncrements(5); //
        lNoiseFig.setProgress((int) MathUtil.round(rxTerm.getAmplifier().getNoiseFigure() * 10));
        lNoiseFig.setRenderValueOnTop(true);

        final Label L13 = new Label(Com.shortText(rxTerm.getAmplifier().
                getNoiseFigure()) + "dB");
        cnt.addComponent(L11);
        cnt.addComponent(lNoiseFig);
        cnt.addComponent(L13);

        // does not change
        Label L2A1 = new Label("Antenna Eff");
        Label L2A2 = new Label(Com.shortText(rxTerm.getrXantenna().getEfficiency()));
        Label L2A3 = new Label(" ");
        cnt.addComponent(L2A1);
        cnt.addComponent(L2A2);
        cnt.addComponent(L2A3);

        Label L21 = new Label("  Diameter");
        final Slider lDiameter = new Slider();

        lDiameter.setMinValue((int) MathUtil.round(Antenna.DIAMETER_LO * 10)); // x10
        lDiameter.setMaxValue((int) MathUtil.round(Antenna.DIAMETER_HI * 10));
        lDiameter.setEditable(true);
        //L22.setPreferredW(8);
        lDiameter.setIncrements(5); //
        lDiameter.setProgress((int) MathUtil.round(rxTerm.getrXantenna().getDiameter() * 10));

        lDiameter.setRenderValueOnTop(true);
        final Label L23 = new Label(Com.shortText(rxTerm.getrXantenna().getDiameter()) + "m");
        cnt.addComponent(L21);
        cnt.addComponent(lDiameter);
        cnt.addComponent(L23);

        Label L31 = new Label(" Gain");
        final Label L32 = new Label(Com.shortText(rxTerm.getrXantenna().getGain()));
        Label L33 = new Label("dBi");
        cnt.addComponent(L31);
        cnt.addComponent(L32);
        cnt.addComponent(L33);

        Label L41 = new Label(" 3dB Angle");
        final Label L42 = new Label(Com.toDMS(rxTerm.getrXantenna().getThreeDBangle()));
        Label L43 = new Label("deg");
        cnt.addComponent(L41);
        cnt.addComponent(L42);
        cnt.addComponent(L43);

        // does change so not in combo/sliders
        Label lPointLoss = new Label(" Point Loss");
        final Label valuePointLoss = new Label(Com.shortText(
                rxTerm.getrXantenna().getDepointingLoss()));
        Label unitPointLoss = new Label("dB");
        cnt.addComponent(lPointLoss);
        cnt.addComponent(valuePointLoss);
        cnt.addComponent(unitPointLoss);

        // does not change so not in combo/sliders
        Label lImpLoss = new Label(" LFRX");
        final Label valueImpLoss = new Label(Com.shortText(
                rxTerm.getAmplifier().getLFRX()));
        Label unitImpLoss = new Label("dB");
        cnt.addComponent(lImpLoss);
        cnt.addComponent(valueImpLoss);
        cnt.addComponent(unitImpLoss);

        // does not change so not in combo/sliders
        Label lsysTemp = new Label(" Sys Noise T");
        final Label valuesysTemp = new Label(Com.text(
                rxTerm.calcSystemNoiseTemp()));
        Label unitsysTemp = new Label("K");
        cnt.addComponent(lsysTemp);
        cnt.addComponent(valuesysTemp);
        cnt.addComponent(unitsysTemp);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);        // whole row

        Label L0A1 = new Label("Terminal");
        L0A1.setAlignment(Component.CENTER);
        cnt.addComponent(constraint, L0A1);

        Label lGainTemp = new Label("Gain/Temp");
        final Label valueGainTemp = new Label(Com.shortText(rxTerm.getGainTemp()));
        Label unitGainTemp = new Label("dB 1/K");
        cnt.addComponent(lGainTemp);
        cnt.addComponent(valueGainTemp);
        cnt.addComponent(unitGainTemp);

        sub.setScrollable(true);

        // all actions at the end to update other fields
        lNoiseFig.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("RxView: selected noise figure " + lNoiseFig.getText(), Log.DEBUG);
                try {
                    selection.getrXterminal().getAmplifier().
                            setNoiseFigure(Double.parseDouble(lNoiseFig.getText()) / 10.0);
                    // update EIRP
                    L13.setText(Com.shortText(rxTerm.getAmplifier().
                            getNoiseFigure()) + "dB");
                    valuesysTemp.setText(Com.text(
                            rxTerm.calcSystemNoiseTemp()));
                    valueGainTemp.setText(Com.shortText(rxTerm.getGainTemp()));
                    // does not change depointing

                } catch (java.lang.NumberFormatException e) {
                    Log.p("RxView: bad number for Noise Figure "
                            + lNoiseFig.getText(), Log.DEBUG);
                }
            }
        });

        lDiameter.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("RxView: selected diameter " + lDiameter.getText(), Log.DEBUG);
                try {

                    // convert from cm to m first
                    selection.getrXterminal().getrXantenna().
                            setDiameter(Double.parseDouble(lDiameter.getText()) / 10.0);
                    // update EIRP and three DB
                    L23.setText(Com.shortText(rxTerm.getrXantenna().getDiameter()) + "m");
                    L42.setText(Com.toDMS(rxTerm.getrXantenna().getThreeDBangle()));
                    L32.setText(Com.shortText(rxTerm.getrXantenna().getGain()));
                    valuePointLoss.setText(Com.shortText(
                            rxTerm.getrXantenna().getDepointingLoss()));
                    valueGainTemp.setText(Com.shortText(rxTerm.getGainTemp()));
                    // should not change
                    valuesysTemp.setText(Com.text(
                            rxTerm.calcSystemNoiseTemp()));
                } catch (java.lang.NumberFormatException e) {
                    Log.p("TxView: bad number for diameter " + lDiameter.getText(), Log.DEBUG);

                }

            }
        });

        // have a multi-row table layout and dump the transmit terminal values
        return sub;
    }

}
