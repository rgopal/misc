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
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.Label;
import com.codename1.ui.Component;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Font;
import com.codename1.ui.Slider;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.Resources;
import com.codename1.util.MathUtil;
import java.util.Hashtable;
import java.util.Vector;

public class SatelliteView extends View {

    private Satellite satellite;
    public ComboBox spin;
    public Label label;
    public Label subLabel;

    public SatelliteView() {

    }

    public SatelliteView(Selection selection) {
        this.satellite = selection.getSatellite();

        // don't call update_values since SatelliteView is still being built
    }

    // override getWidget to create semiMajor Combobox driven by selected band
    public Component getWidget(final Selection selection) {
        Label l = new Label(getName());

        // get selected band
        RfBand.Band band = selection.getBand();

        final ComboBox combo = new ComboBox();

        // indexSatellite has all satellites, so get the band specific list
        if (selection.getBandSatellite().get(band) == null) {
            Log.p("Can't find satellites for band " + band, Log.WARNING);
            return l;
        }

        // create model for all satellites of selected band
        ListModel model = new DefaultListModel(selection.getBandSatellite().get(band));

        // show the selected satellite
        int index = combo.getSelectedIndex();

        combo.setModel(model);

        // set the satellite view present in the selection (and not this.spin)
        selection.getSatelliteView().spin = combo;

        selection.setSatellite(Satellite.satelliteHash.get(
                (String) combo.getSelectedItem()));

        String name = (String) combo.getSelectedItem();

        //label does not exist yet, so this does not work
        //       Satellite sat = Satellite.satelliteHash.get(name);
        // selection.getSatelliteView().label.setText(sat.getName());
        // selection.setSatelliteView (spin);
        // fires when the list is changed (by user)
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                // set the selected satellite
                selection.setSatellite(Satellite.satelliteHash.get(
                        (String) combo.getSelectedItem()));

                // update other values dependent on this satellite
                updateValues(selection);

                // update the UL path
                selection.getuLpath().setSatellite(selection.getSatellite());
                // update the DL path TODO
                //selection.getdLpath().setSatellite(selection.getSatellite());
                // change the subwidget, label, and sublabel etc.
                selection.getSatelliteView().label.setText(
                        Satellite.satelliteHash.get(
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

        // satellite may be empty right now
        final Label label = new Label(
                (String) selection.getSatelliteView().spin.getSelectedItem());

        //selection.getSatellite().getName()); since satellite is empty
        // set the satellite view present in the selection
        selection.getSatelliteView().label = label;

        // combo box created so return it
        return label;
    }

    // update from the current selection of the Satellite
    public void updateValues(Selection selection) {
        selection.getSatelliteView().summary = Com.shortText(
                selection.getSatellite().getLongitude());
        selection.getSatelliteView().value = Com.shortText(
                selection.getSatellite().getEIRP());
        selection.getSatelliteView().unit = Com.shortText(
                selection.getSatellite().getGainTemp());
    }

    public String getDisplayName() {
        return name;
    }

    public Form createView(final Selection selection) {

        final Satellite satellite = selection.getuLpath().getSatellite();

        Form sub = new Form("Satellite: " + satellite.getName());
        Container cnt = new Container(new BorderLayout());
        sub.addComponent(cnt);

        // there are several items in Views.  Hardcoded table. Name, value, unit
        TableLayout layout = new TableLayout(13, 3);
        cnt.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setVerticalSpan(3);
        constraint.setWidthPercentage(45);

        // now go sequentially through Satellite Receive Side
        Label L01 = new Label("Central Frequency");
        Label lFrequency = new Label(Com.shortText(satellite.getRxAntenna().getFrequency() / 1E9));
        Label L03 = new Label("GHz " + satellite.getRxAntenna().getBand());
        cnt.addComponent(constraint, L01);
        cnt.addComponent(lFrequency);
        cnt.addComponent(L03);

        // now the Rx side of the satellite
        Label lNoiseFigureLabel = new Label("Rx Noise Figure");
        final Label lNoiseFigure = new Label(Com.shortText(satellite.getAmplifier().
                getNoiseFigure()));
        Label lNoiseFigureUnit = new Label("dB");

        final Slider sldrNoiseFigure = new Slider();
        sldrNoiseFigure.setMinValue((int) MathUtil.round(Amplifier.NOISE_FIG_LO * 10)); // x10
        sldrNoiseFigure.setMaxValue((int) MathUtil.round(Amplifier.NOISE_FIG_HI * 10));
        sldrNoiseFigure.setEditable(true);
        // sldrNoiseFigure.setPreferredW(8);
        sldrNoiseFigure.setIncrements(5); //
        sldrNoiseFigure.setProgress((int) MathUtil.round(satellite.getAmplifier().getNoiseFigure() * 10));
        sldrNoiseFigure.setRenderValueOnTop(true);
 cnt.addComponent(lNoiseFigureLabel);
        cnt.addComponent(lNoiseFigure);
        cnt.addComponent(lNoiseFigureUnit);
        
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        // constraint.setWidthPercentage(45);
        cnt.addComponent(constraint, sldrNoiseFigure);

       

        Label lAntennaEfficiencyLabel = new Label("  Antenna Efficiency");
        Label lEfficiency = new Label(Com.shortText(satellite.getRxAntenna().getEfficiency()));
        Label L2A3 = new Label(" ");
        cnt.addComponent(lAntennaEfficiencyLabel);
        cnt.addComponent(lEfficiency);
        cnt.addComponent(L2A3);

        Label lDiameterLabel = new Label("  Antenna Diameter");
        final Slider sldrDiameter = new Slider();

        sldrDiameter.setMinValue((int) MathUtil.round(Antenna.DIAMETER_LO * 10)); // x10
        sldrDiameter.setMaxValue((int) MathUtil.round(Antenna.DIAMETER_HI * 10));
        sldrDiameter.setEditable(true);
        //L22.setPreferredW(8);
        sldrDiameter.setIncrements(5); //
        sldrDiameter.setProgress((int) MathUtil.round(satellite.getRxAntenna().getDiameter() * 10));

        sldrDiameter.setRenderValueOnTop(true);
                constraint = layout.createConstraint();
       
        
        final Label lDiameter = new Label(Com.shortText(
                satellite.getRxAntenna().getDiameter()));
          Label lDiameterUnit = new Label("m");
          
        cnt.addComponent(lDiameterLabel);
        cnt.addComponent(lDiameter);
        cnt.addComponent(lDiameterUnit);
 constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrDiameter);
        Label L31 = new Label(" Gain");
        final Label lGain = new Label(Com.shortText(satellite.getRxAntenna().getGain()));
        Label L33 = new Label("dBi");
        cnt.addComponent(L31);
        cnt.addComponent(lGain);
        cnt.addComponent(L33);

        Label L41 = new Label(" 3dB Angle");
        final Label lThreeDBangle = new Label(Com.toDMS(satellite.getRxAntenna().getThreeDBangle()));
        Label L43 = new Label("deg");
        cnt.addComponent(L41);
        cnt.addComponent(lThreeDBangle);
        cnt.addComponent(L43);

        // does change so not in combo/sliders
        Label lPointLoss = new Label(" Point Loss");
        final Label valuePointLoss = new Label(Com.shortText(
                satellite.getRxAntenna().getDepointingLoss()));
        Label unitPointLoss = new Label("dB");
        cnt.addComponent(lPointLoss);
        cnt.addComponent(valuePointLoss);
        cnt.addComponent(unitPointLoss);
        
        // attenuation does not depend on anything so not incouded in
        // sliders
        Label lGainTemp = new Label("Satellite G/T");
        final Label valueGainTemp = new Label(Com.shortText(
                selection.getuLpath().getSatellite().getGainTemp()));
        Label unitGainTemp = new Label("dB 1/K");
        cnt.addComponent(lGainTemp);
        cnt.addComponent(valueGainTemp);
        cnt.addComponent(unitGainTemp);

        // does not change so not in combo/sliders
        Label lImpLoss = new Label(" LFRX");
        final Label valueImpLoss = new Label(Com.shortText(
                satellite.getAmplifier().getLFRX()));
        Label unitImpLoss = new Label("dB");
        cnt.addComponent(lImpLoss);
        cnt.addComponent(valueImpLoss);
        cnt.addComponent(unitImpLoss);

        // does not change so not in combo/sliders
        Label lsysTemp = new Label(" Sys Noise T");
        final Label valuesysTemp = new Label(Com.text(
                satellite.calcSystemNoiseTemp()));
        Label unitsysTemp = new Label("K");
        cnt.addComponent(lsysTemp);
        cnt.addComponent(valuesysTemp);
        cnt.addComponent(unitsysTemp);

        sub.setScrollable(true);

        // all actions at the end to update other fields
        sldrNoiseFigure.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected noise figure " + sldrNoiseFigure.getText(), Log.DEBUG);
                try {
                    satellite.getAmplifier().
                            setNoiseFigure(Double.parseDouble(sldrNoiseFigure.getText()) / 10.0);
                    // update EIRP
                    lNoiseFigure.setText(Com.shortText(satellite.getAmplifier().
                            getNoiseFigure()) + "dB");
                    valuesysTemp.setText(Com.text(
                            satellite.calcSystemNoiseTemp()));
                    valueGainTemp.setText(Com.shortText(satellite.getGainTemp()));
                    // does not change depointing

                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for Noise Figure "
                            + sldrNoiseFigure.getText(), Log.DEBUG);
                }
            }
        });

        sldrDiameter.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected diameter " + sldrDiameter.getText(), Log.DEBUG);
                try {

                    // convert from cm to m first
                    selection.getSatellite().getRxAntenna().
                            setDiameter(Double.parseDouble(sldrDiameter.getText()) / 10.0);
                    // update EIRP and three DB
                    lDiameter.setText(Com.shortText(satellite.getRxAntenna().getDiameter()));
                    lThreeDBangle.setText(Com.toDMS(satellite.getRxAntenna().getThreeDBangle()));
                    lGain.setText(Com.shortText(satellite.getRxAntenna().getGain()));
                    valuePointLoss.setText(Com.shortText(
                            satellite.getRxAntenna().getDepointingLoss()));
                    valueGainTemp.setText(Com.shortText(satellite.getGainTemp()));

                    // should not change
                    valuesysTemp.setText(Com.text(
                            satellite.calcSystemNoiseTemp()));
                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for diameter " + sldrDiameter.getText(), Log.DEBUG);

                }

            }
        });

        sub.setScrollable(true);
        // have a multi-row table layout and dump the transmit terminal values
        return sub;
    }
}
