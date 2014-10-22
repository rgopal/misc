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
      
             
        final Satellite satellite = selection.getdLpath().getSatellite();

        Form sub = new Form("Satellite: " + satellite.getName());
        Container cnt = new Container(new BorderLayout());
        sub.addComponent(cnt);

        // there are several items in Views.  Hardcoded table. Name, value, unit
        TableLayout layout = new TableLayout(13, 3);
        cnt.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setVerticalSpan(2);
        constraint.setWidthPercentage(35);

        // now go sequentially through Satellite Receive Side
  
        Label L01 = new Label("C Freq");
        Label lFrequency = new Label(Com.shortText(satellite.getAntenna().getFrequency() / 1E9));
        Label L03 = new Label("GHz " + satellite.getAntenna().getBand());
        cnt.addComponent(L01);
        cnt.addComponent(lFrequency);
        cnt.addComponent(constraint, L03);



        Label lPathLoss = new Label("UL Loss");
        final Label valuePathLoss = new Label(Com.text(selection.
                getuLpath().getPathLoss()));
        Label unitPathLoss = new Label("dB");
        cnt.addComponent(lPathLoss);
        cnt.addComponent(valuePathLoss);
        cnt.addComponent(unitPathLoss);

        // attenuation does not depend on anything so not incouded in
        // sliders
        Label lAttenuation = new Label("Atm Atten.");
        final Label valueAttenuation = new Label(Com.shortText(
                selection.getuLpath().getAttenuation()));
        Label unitAttenuation = new Label("dB");
        cnt.addComponent(lAttenuation);
        cnt.addComponent(valueAttenuation);
        cnt.addComponent(unitAttenuation);



        // now the Rx side of the satellite
        Label lNoiseFigureLabel = new Label("Noise Figure");

        final Slider lNoiseFig = new Slider();
        lNoiseFig.setMinValue((int) MathUtil.round(Amplifier.NOISE_FIG_LO * 10)); // x10
        lNoiseFig.setMaxValue((int) MathUtil.round(Amplifier.NOISE_FIG_HI * 10));
        lNoiseFig.setEditable(true);
        // lNoiseFig.setPreferredW(8);
        lNoiseFig.setIncrements(5); //
        lNoiseFig.setProgress((int) MathUtil.round(satellite.getAmplifier().getNoiseFigure() * 10));
        lNoiseFig.setRenderValueOnTop(true);

        final Label lNoiseFigure = new Label(Com.shortText(satellite.getAmplifier().
                getNoiseFigure()) + "dB");
        cnt.addComponent(lNoiseFigureLabel);
        cnt.addComponent(lNoiseFig);
        cnt.addComponent(lNoiseFigure);

        // does not change
        Label lAntennaEfficiencyLabel = new Label("Antenna Eff");
        Label lEfficiency = new Label(Com.shortText(satellite.getAntenna().getEfficiency()));
        Label L2A3 = new Label(" ");
        cnt.addComponent(lAntennaEfficiencyLabel);
        cnt.addComponent(lEfficiency);
        cnt.addComponent(L2A3);

        Label lDiameterLabel = new Label("  Diameter");
        final Slider lDiameter = new Slider();

        lDiameter.setMinValue((int) MathUtil.round(Antenna.DIAMETER_LO * 10)); // x10
        lDiameter.setMaxValue((int) MathUtil.round(Antenna.DIAMETER_HI * 10));
        lDiameter.setEditable(true);
        //L22.setPreferredW(8);
        lDiameter.setIncrements(5); //
        lDiameter.setProgress((int) MathUtil.round(satellite.getAntenna().getDiameter() * 10));

        lDiameter.setRenderValueOnTop(true);
        final Label lTermDiameter = new Label(Com.shortText(satellite.getAntenna().getDiameter()) + "m");
        cnt.addComponent(lDiameterLabel);
        cnt.addComponent(lDiameter);
        cnt.addComponent(lTermDiameter);

        Label L31 = new Label(" Gain");
        final Label lGain = new Label(Com.shortText(satellite.getAntenna().getGain()));
        Label L33 = new Label("dBi");
        cnt.addComponent(L31);
        cnt.addComponent(lGain);
        cnt.addComponent(L33);

        Label L41 = new Label(" 3dB Angle");
        final Label lThreeDBangle = new Label(Com.toDMS(satellite.getAntenna().getThreeDBangle()));
        Label L43 = new Label("deg");
        cnt.addComponent(L41);
        cnt.addComponent(lThreeDBangle);
        cnt.addComponent(L43);

        // does change so not in combo/sliders
        Label lPointLoss = new Label(" Point Loss");
        final Label valuePointLoss = new Label(Com.shortText(
                satellite.getAntenna().getDepointingLoss()));
        Label unitPointLoss = new Label("dB");
        cnt.addComponent(lPointLoss);
        cnt.addComponent(valuePointLoss);
        cnt.addComponent(unitPointLoss);
        // attenuation does not depend on anything so not incouded in
        // sliders
        Label lGainTemp = new Label("Sat G/T");
        final Label valueGainTemp = new Label(Com.shortText(
                selection.getuLpath().getSatellite().getGainTemp()));
        Label unitGainTemp = new Label("dB 1/K");
        cnt.addComponent(lGainTemp);
        cnt.addComponent(valueGainTemp);
        cnt.addComponent(unitGainTemp);

        // C/No depends on Tx EIRP and the path loss
        Label lCNo = new Label("C/No");
        final Label valueCNo = new Label(Com.text(selection.getuLpath().getCNo()));

        Label unitCNo = new Label("dB Hz");
        cnt.addComponent(lCNo);
        cnt.addComponent(valueCNo);
        cnt.addComponent(unitCNo);

        Label lSpecDensity = new Label("Spec Dens");
        final Label valueSpecDensity = new Label(Com.text(
                selection.getuLpath().getSpectralDensity()));
        Label unitSpecDensity = new Label("dBW/m2");
        cnt.addComponent(lSpecDensity);
        cnt.addComponent(valueSpecDensity);
        cnt.addComponent(unitSpecDensity);
        
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

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);        // whole row

        Label L0A1 = new Label("Satellite");
        L0A1.setAlignment(Component.CENTER);
        cnt.addComponent(constraint, L0A1);

        sub.setScrollable(true);

        // all actions at the end to update other fields
        lNoiseFig.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected noise figure " + lNoiseFig.getText(), Log.DEBUG);
                try {
                    satellite.getAmplifier().
                            setNoiseFigure(Double.parseDouble(lNoiseFig.getText()) / 10.0);
                    // update EIRP
                    lNoiseFigure.setText(Com.shortText(satellite.getAmplifier().
                            getNoiseFigure()) + "dB");
                    valuesysTemp.setText(Com.text(
                            satellite.calcSystemNoiseTemp()));
                    valueGainTemp.setText(Com.shortText(satellite.getGainTemp()));
                    // does not change depointing

                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for Noise Figure "
                            + lNoiseFig.getText(), Log.DEBUG);
                }
            }
        });

        lDiameter.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected diameter " + lDiameter.getText(), Log.DEBUG);
                try {

                    // convert from cm to m first
                    selection.getSatellite().getAntenna().
                            setDiameter(Double.parseDouble(lDiameter.getText()) / 10.0);
                    // update EIRP and three DB
                    lTermDiameter.setText(Com.shortText(satellite.getAntenna().getDiameter()) + "m");
                    lThreeDBangle.setText(Com.toDMS(satellite.getAntenna().getThreeDBangle()));
                    lGain.setText(Com.shortText(satellite.getAntenna().getGain()));
                    valuePointLoss.setText(Com.shortText(
                            satellite.getAntenna().getDepointingLoss()));
                    valueGainTemp.setText(Com.shortText(satellite.getGainTemp()));
                    valueCNo.setText(Com.text(selection.getuLpath().getCNo()));
                 
                    // should not change
                    valuesysTemp.setText(Com.text(
                            satellite.calcSystemNoiseTemp()));
                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for diameter " + lDiameter.getText(), Log.DEBUG);

                }

            }
        });
     
        sub.setScrollable(true);
        // have a multi-row table layout and dump the transmit terminal values
        return sub;
    }
}
