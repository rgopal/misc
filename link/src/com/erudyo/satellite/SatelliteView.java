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
        TableLayout layout = new TableLayout(24, 3);
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
        final Label lNoiseFigure = new Label(Com.shortText(satellite.getRxAmplifier().
                getNoiseFigure()));
        Label lNoiseFigureUnit = new Label("dB");

        final Slider sldrRxNoiseFigure = new Slider();
        sldrRxNoiseFigure.setMinValue((int) MathUtil.round(
                Amplifier.NOISE_FIG_LO * 10)); // x10
        sldrRxNoiseFigure.setMaxValue((int) MathUtil.round(
                Amplifier.NOISE_FIG_HI * 10));
        sldrRxNoiseFigure.setEditable(true);
        // sldrRxNoiseFigure.setPreferredW(8);
        sldrRxNoiseFigure.setIncrements(5); //
        sldrRxNoiseFigure.setProgress((int) MathUtil.round(
                satellite.getRxAmplifier().getNoiseFigure() * 10));
        sldrRxNoiseFigure.setRenderValueOnTop(true);
        cnt.addComponent(lNoiseFigureLabel);
        cnt.addComponent(lNoiseFigure);
        cnt.addComponent(lNoiseFigureUnit);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        // constraint.setWidthPercentage(45);
        cnt.addComponent(constraint, sldrRxNoiseFigure);
/*
        Label lAntennaEfficiencyLabel = new Label("  Antenna Efficiency");
        Label lEfficiency = new Label(Com.shortText(
                satellite.getRxAntenna().getEfficiency()));
        Label L2A3 = new Label(" ");
        cnt.addComponent(lAntennaEfficiencyLabel);
        cnt.addComponent(lEfficiency);
        cnt.addComponent(L2A3);
*/
        Label lRxDiameterLabel = new Label("  Antenna Diameter");
        final Slider sldrRxDiameter = new Slider();

        sldrRxDiameter.setMinValue((int) MathUtil.round(Antenna.DIAMETER_LO * 10)); // x10
        sldrRxDiameter.setMaxValue((int) MathUtil.round(Antenna.DIAMETER_HI * 10));
        sldrRxDiameter.setEditable(true);
        //L22.setPreferredW(8);
        sldrRxDiameter.setIncrements(5); //
        sldrRxDiameter.setProgress((int) MathUtil.round(satellite.getRxAntenna().getDiameter() * 10));

        sldrRxDiameter.setRenderValueOnTop(true);
        constraint = layout.createConstraint();

        final Label lRxDiameter = new Label(Com.shortText(
                satellite.getRxAntenna().getDiameter()));
        Label lDiameterUnit = new Label("m");

        cnt.addComponent(lRxDiameterLabel);
        cnt.addComponent(lRxDiameter);
        cnt.addComponent(lDiameterUnit);
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrRxDiameter);
        Label L31 = new Label(" Gain");
        final Label lRxGain = new Label(Com.shortText(
                satellite.getRxAntenna().getGain()));
        Label L33 = new Label("dBi");
        cnt.addComponent(L31);
        cnt.addComponent(lRxGain);
        cnt.addComponent(L33);

        Label L41 = new Label(" 3dB Angle");
        final Label lRxThreeDBangle = new Label(Com.toDMS(
                satellite.getRxAntenna().getThreeDBangle()));
        Label L43 = new Label("deg");
        cnt.addComponent(L41);
        cnt.addComponent(lRxThreeDBangle);
        cnt.addComponent(L43);

        // does change so not in combo/sliders
        Label lPointLoss = new Label(" Point Loss");
        final Label valueRxPointLoss = new Label(Com.shortText(
                satellite.getRxAntenna().getDepointingLoss()));
        Label unitPointLoss = new Label("dB");
        cnt.addComponent(lPointLoss);
        cnt.addComponent(valueRxPointLoss);
        cnt.addComponent(unitPointLoss);

        // attenuation does not depend on anything so not incouded in
        // sliders
        Label lGainTemp = new Label("Satellite G/T");
        final Label valueRxGainTemp = new Label(Com.shortText(
                satellite.getGainTemp()));
        Label unitGainTemp = new Label("dB 1/K");
        cnt.addComponent(lGainTemp);
        cnt.addComponent(valueRxGainTemp);
        cnt.addComponent(unitGainTemp);

   /*     // does not change so not in combo/sliders
        Label lImpLoss = new Label(" LFRX");
        final Label valueRxImpLoss = new Label(Com.shortText(
                satellite.getRxAmplifier().getLFRX()));
        Label unitImpLoss = new Label("dB");
        cnt.addComponent(lImpLoss);
        cnt.addComponent(valueRxImpLoss);
        cnt.addComponent(unitImpLoss);
*/
        // does not change so not in combo/sliders
        Label lsysTemp = new Label(" System Noise Temp");
        final Label valueRxsysTemp = new Label(Com.text(
                satellite.calcSystemNoiseTemp()));
        Label unitsysTemp = new Label("K");
        cnt.addComponent(lsysTemp);
        cnt.addComponent(valueRxsysTemp);
        cnt.addComponent(unitsysTemp);

        sub.setScrollable(true);

        // all actions at the end to update other fields
        sldrRxNoiseFigure.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected noise figure " + sldrRxNoiseFigure.getText(), Log.DEBUG);
                try {
                    satellite.getRxAmplifier().
                            setNoiseFigure(Double.parseDouble(sldrRxNoiseFigure.getText()) / 10.0);
                    // update EIRP
                    lNoiseFigure.setText(Com.shortText(satellite.getRxAmplifier().
                            getNoiseFigure()) + "dB");
                    valueRxsysTemp.setText(Com.text(
                            satellite.calcSystemNoiseTemp()));
                    valueRxGainTemp.setText(Com.shortText(satellite.getGainTemp()));
                    // does not change depointing

                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for Noise Figure "
                            + sldrRxNoiseFigure.getText(), Log.DEBUG);
                }
            }
        });

        sldrRxDiameter.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected diameter " + sldrRxDiameter.getText(), Log.DEBUG);
                try {

                    // convert from cm to m first
                    selection.getSatellite().getRxAntenna().
                            setDiameter(Double.parseDouble(sldrRxDiameter.getText()) / 10.0);
                    // update EIRP and three DB
                    lRxDiameter.setText(Com.shortText(satellite.getRxAntenna().getDiameter()));
                    lRxThreeDBangle.setText(Com.toDMS(satellite.getRxAntenna().getThreeDBangle()));
                    lRxGain.setText(Com.shortText(satellite.getRxAntenna().getGain()));
                    valueRxPointLoss.setText(Com.shortText(
                            satellite.getRxAntenna().getDepointingLoss()));
                    valueRxGainTemp.setText(Com.shortText(satellite.getGainTemp()));

                    // should not change
                    valueRxsysTemp.setText(Com.text(
                            satellite.calcSystemNoiseTemp()));
                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for diameter " + sldrRxDiameter.getText(), Log.DEBUG);

                }

            }
        });

        // now the Tx side of satellite
        Label L11 = new Label("Amplifier Power");
        Label L13 = new Label("W");

        final Slider sldrTxPower = new Slider();
        sldrTxPower.setMinValue((int) MathUtil.round(Amplifier.POWER_LO * 10)); // x10
        sldrTxPower.setMaxValue((int) MathUtil.round(Amplifier.POWER_HI * 10));
        sldrTxPower.setEditable(true);
        // sldrTxPower.setPreferredW(8);
        sldrTxPower.setIncrements(5); //
        sldrTxPower.setProgress((int) MathUtil.round(satellite.getTxAmplifier().getPower() * 10));
        sldrTxPower.setRenderValueOnTop(true);
        sldrTxPower.getStyle().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));

        final Label lTxAmplifier = new Label(Com.shortText(satellite.getTxAmplifier().
                getPower()));

        cnt.addComponent(L11);
        cnt.addComponent(lTxAmplifier);
        cnt.addComponent(L13);
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);

        cnt.addComponent(constraint, sldrTxPower);

        Label L21 = new Label("Tx  Diameter");
        Label lTxDiaUnit = new Label("m");
        final Slider sldrTxDiameter = new Slider();
        sldrRxDiameter.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM,
                Font.STYLE_PLAIN, Font.SIZE_SMALL));

        sldrTxDiameter.setMinValue((int) MathUtil.round(Antenna.DIAMETER_LO * 10)); // x10
        sldrTxDiameter.setMaxValue((int) MathUtil.round(Antenna.DIAMETER_HI * 10));
        sldrTxDiameter.setEditable(true);

        sldrTxDiameter.setIncrements(5); //
        sldrTxDiameter.setProgress((int) MathUtil.round(satellite.getTxAntenna().getDiameter() * 10));

        sldrTxDiameter.setRenderValueOnTop(true);
        final Label lTxDiameter = new Label(Com.shortText(satellite.getTxAntenna().getDiameter()) + "m");
        cnt.addComponent(L21);
        cnt.addComponent(lTxDiameter);
        cnt.addComponent(lTxDiaUnit);

        constraint = layout.createConstraint();

        constraint.setHorizontalSpan(3);

        cnt.addComponent(constraint, sldrTxDiameter);

        Label lTxGainLabel = new Label(" Gain");
        final Label lTxGain = new Label(Com.shortText(satellite.getTxAntenna().getGain()));
        Label lTxGainUnit = new Label("dBi");
        cnt.addComponent(lTxGainLabel);
        cnt.addComponent(lTxGain);
        cnt.addComponent(lTxGainUnit);

        Label lTx3dBLabel = new Label(" 3dB Angle");
        final Label lTx3dB = new Label(Com.toDMS(satellite.getTxAntenna().getThreeDBangle()));
        Label lTx3dBUnit = new Label("degree");
        cnt.addComponent(lTx3dBLabel);
        cnt.addComponent(lTx3dB);
        cnt.addComponent(lTx3dBUnit);

        // does change so not in combo/sliders
        Label lTxPointLoss = new Label(" Pointing Loss");
        final Label valueTxPointLoss = new Label(Com.shortText(
                satellite.getTxAntenna().getDepointingLoss()));
        Label unitTxPointLoss = new Label("dB");
        cnt.addComponent(lTxPointLoss);
        cnt.addComponent(valueTxPointLoss);
        cnt.addComponent(unitTxPointLoss);

        Label lEIRP = new Label("Satellite Tx EIRP");
        final Label valueTxEIRP = new Label(Com.shortText(satellite.getEIRP()));
        Label unitEIRP = new Label("dBW");
        cnt.addComponent(lEIRP);
        cnt.addComponent(valueTxEIRP);
        cnt.addComponent(unitEIRP);

    

        // all actions at the end to update other fields
        sldrTxPower.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected power " + sldrTxPower.getText(), Log.DEBUG);
                try {
                    selection.getSatellite().getTxAmplifier().
                            setPower(Double.parseDouble(sldrTxPower.getText()) / 10.0);
                    // update EIRP
                    lTxAmplifier.setText(Com.shortText(satellite.getTxAmplifier().
                            getPower()));
                    valueTxEIRP.setText(Com.shortText(satellite.getEIRP()));
                    // does not change depointing

                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for power " + sldrTxPower.getText(), Log.DEBUG);
                }
            }
        });

        sldrTxDiameter.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected Tx diameter " + sldrTxDiameter.getText(), Log.DEBUG);
                try {

                    // convert from cm to m first
                    selection.getSatellite().getTxAntenna().
                            setDiameter(Double.parseDouble(sldrTxDiameter.getText()) / 10.0);
                    // update EIRP and three DB
                    sldrTxDiameter.setText(Com.shortText(satellite.getTxAntenna().getDiameter()));
                    lTx3dB.setText(Com.toDMS(satellite.getTxAntenna().getThreeDBangle()));
                    lTxGain.setText(Com.shortText(satellite.getTxAntenna().getGain()));
                    valueTxPointLoss.setText(Com.shortText(
                            satellite.getTxAntenna().getDepointingLoss()));
                    valueTxEIRP.setText(Com.shortText(satellite.getEIRP()));
                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for diameter " + 
                            sldrTxDiameter.getText(), Log.DEBUG);

                }

            }
        });

        sub.setScrollable(true);
        // have a multi-row table layout and dump the transmit terminal values
        return sub;
    }
}
