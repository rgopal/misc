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
        // constraint.setHorizontalSpan(3);
        constraint.setWidthPercentage(45);

        // now go sequentially through the Tx terminal fields
        final Terminal rxTerm = selection.getrXterminal();

        Label L01 = new Label("Center Frequency");
        Label lFrequency = new Label(Com.shortText(rxTerm.getrXantenna().getFrequency() / 1E9));
        Label L03 = new Label("GHz " + rxTerm.getBand());
        cnt.addComponent(constraint,L01);
        cnt.addComponent(lFrequency);
        cnt.addComponent(L03);

        Label L61 = new Label("Termianl Longitude");
        final Label lLongitude = new Label(Com.toDMS(rxTerm.getLongitude()));
        Label L63 = new Label("deg");

        cnt.addComponent(L61);
        cnt.addComponent(lLongitude);
        cnt.addComponent(L63);

        Label L71 = new Label("    Latitude");
        final Label lLatitude = new Label(Com.toDMS(rxTerm.getLatitude()));
        Label L73 = new Label("degree");

        cnt.addComponent(L71);
        cnt.addComponent(lLatitude);
        cnt.addComponent(L73);

        Label lNoiseFigureLabel = new Label("LNA Noise Figure");
   Label lNoiseFigureUnit = new Label("dB");
        final Slider sldrNoiseFigure = new Slider();
        sldrNoiseFigure.setMinValue((int) MathUtil.round(Amplifier.NOISE_FIG_LO * 10)); // x10
        sldrNoiseFigure.setMaxValue((int) MathUtil.round(Amplifier.NOISE_FIG_HI * 10));
        sldrNoiseFigure.setEditable(true);
        // sldrNoiseFigure.setPreferredW(8);
        sldrNoiseFigure.setIncrements(5); //
        sldrNoiseFigure.setProgress((int) MathUtil.round(rxTerm.getAmplifier().getNoiseFigure() * 10));
        sldrNoiseFigure.setRenderValueOnTop(true);

        final Label lNoiseFigure = new Label(Com.shortText(rxTerm.getAmplifier().
                getNoiseFigure()));
        cnt.addComponent(lNoiseFigureLabel);
      cnt.addComponent(lNoiseFigure);
        cnt.addComponent(lNoiseFigureUnit);
        
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
          cnt.addComponent(constraint, sldrNoiseFigure);
        // does not change
        Label lAntennaEfficiencyLabel = new Label("Antenna Efficiency");
        Label lEfficiency = new Label(Com.shortText(rxTerm.getrXantenna().getEfficiency()));
        Label L2A3 = new Label(" ");
        cnt.addComponent(lAntennaEfficiencyLabel);
        cnt.addComponent(lEfficiency);
        cnt.addComponent(L2A3);

        Label lDiameterLabel = new Label("    Diameter");
           Label lDiameterUnit = new Label("m");
        final Slider sldrDiameter = new Slider();

        sldrDiameter.setMinValue((int) MathUtil.round(Antenna.DIAMETER_LO * 10)); // x10
        sldrDiameter.setMaxValue((int) MathUtil.round(Antenna.DIAMETER_HI * 10));
        sldrDiameter.setEditable(true);
        //L22.setPreferredW(8);
        sldrDiameter.setIncrements(5); //
        sldrDiameter.setProgress((int) MathUtil.round(rxTerm.getrXantenna().getDiameter() * 10));

        sldrDiameter.setRenderValueOnTop(true);
        final Label lDiameterValue = new Label(Com.shortText(rxTerm.getrXantenna().getDiameter()));
        cnt.addComponent(lDiameterLabel);
       cnt.addComponent(lDiameterValue);
        cnt.addComponent(lDiameterUnit);

             constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
         cnt.addComponent(constraint,sldrDiameter);
         

        Label L31 = new Label("Antenna Gain");
        final Label lGain = new Label(Com.shortText(rxTerm.getrXantenna().getGain()));
        Label L33 = new Label("dBi");
        cnt.addComponent(L31);
        cnt.addComponent(lGain);
        cnt.addComponent(L33);

        Label L41 = new Label("    3dB Angle");
        final Label lThreeDBangle = new Label(Com.toDMS(rxTerm.getrXantenna().getThreeDBangle()));
        Label L43 = new Label("degree");
        cnt.addComponent(L41);
        cnt.addComponent(lThreeDBangle);
        cnt.addComponent(L43);

        // does change so not in combo/sliders
        Label lPointLoss = new Label("    Pointing Loss");
        final Label valuePointLoss = new Label(Com.shortText(
                rxTerm.getrXantenna().getDepointingLoss()));
        Label unitPointLoss = new Label("dB");
        cnt.addComponent(lPointLoss);
        cnt.addComponent(valuePointLoss);
        cnt.addComponent(unitPointLoss);

        // does not change so not in combo/sliders
        Label lImpLoss = new Label("    LFRX");
        final Label valueImpLoss = new Label(Com.shortText(
                rxTerm.getAmplifier().getLFRX()));
        Label unitImpLoss = new Label("dB");
        cnt.addComponent(lImpLoss);
        cnt.addComponent(valueImpLoss);
        cnt.addComponent(unitImpLoss);

        // does not change so not in combo/sliders
        Label lsysTemp = new Label("System Noise Temperature");
        final Label valuesysTemp = new Label(Com.textN(
                rxTerm.calcSystemNoiseTemp(),6));
        Label unitsysTemp = new Label("K");
        cnt.addComponent(lsysTemp);
        cnt.addComponent(valuesysTemp);
        cnt.addComponent(unitsysTemp);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);        // whole row

        Label lGainTemp = new Label("Terminal Gain/Temp");
        final Label valueGainTemp = new Label(Com.shortText(rxTerm.getGainTemp()));
        Label unitGainTemp = new Label("dB 1/K");
        cnt.addComponent(lGainTemp);
        cnt.addComponent(valueGainTemp);
        cnt.addComponent(unitGainTemp);

        sub.setScrollable(true);

        // all actions at the end to update other fields
        sldrNoiseFigure.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("RxView: selected noise figure " + sldrNoiseFigure.getText(), Log.DEBUG);
                try {
                    selection.getrXterminal().getAmplifier().
                            setNoiseFigure(Double.parseDouble(sldrNoiseFigure.getText()) / 10.0);
                    // update EIRP
                    lNoiseFigure.setText(Com.shortText(rxTerm.getAmplifier().
                            getNoiseFigure()) + "dB");
                    valuesysTemp.setText(Com.textN(
                            rxTerm.calcSystemNoiseTemp(),6));
                    valueGainTemp.setText(Com.shortText(rxTerm.getGainTemp()));
                    // does not change depointing

                } catch (java.lang.NumberFormatException e) {
                    Log.p("RxView: bad number for Noise Figure "
                            + sldrNoiseFigure.getText(), Log.DEBUG);
                }
            }
        });

        sldrDiameter.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("RxView: selected diameter " + sldrDiameter.getText(), Log.DEBUG);
                try {

                    // convert from cm to m first
                    selection.getrXterminal().getrXantenna().
                            setDiameter(Double.parseDouble(sldrDiameter.getText()) / 10.0);
                    // update EIRP and three DB
                    lDiameterValue.setText(Com.shortText(rxTerm.getrXantenna().getDiameter()) );
                    lThreeDBangle.setText(Com.toDMS(rxTerm.getrXantenna().getThreeDBangle()));
                    lGain.setText(Com.shortText(rxTerm.getrXantenna().getGain()));
                    valuePointLoss.setText(Com.shortText(
                            rxTerm.getrXantenna().getDepointingLoss()));
                    valueGainTemp.setText(Com.shortText(rxTerm.getGainTemp()));
                    // should not change
                    valuesysTemp.setText(Com.textN(
                            rxTerm.calcSystemNoiseTemp(),6));
                } catch (java.lang.NumberFormatException e) {
                    Log.p("TxView: bad number for diameter " + sldrDiameter.getText(), Log.DEBUG);

                }

            }
        });

        // have a multi-row table layout and dump the transmit terminal values
        return sub;
    }

}
