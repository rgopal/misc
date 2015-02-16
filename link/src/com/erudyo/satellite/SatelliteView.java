/*
 * OVERVIEW
 * 
 * View shows the selected satellite and lets user interact with its model
 *
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class SatelliteView extends View {

    public ComboBox spin;

    public SatelliteView() {

    }

    public SatelliteView(Selection selection) {

        // don't call update_values since SatelliteView is still being built
    }

    // create the text part (list of all attribute values) fresh when called.
    public ArrayList<ArrayList<String>> getText(Selection selection) {
        ArrayList<ArrayList<String>> outer = new ArrayList<ArrayList<String>>();

        outer.add(TxView.addNewInner("Satellite",
                selection.getSatellite().getName(), ""));
        Satellite satellite = selection.getSatellite();

        final BandSpecificItems bandBeams = satellite.bandSpecificItems.get(
                selection.getBand());

        final RfBand.Band band = selection.getBand();

        if (bandBeams.beams != null) {

            outer.add(TxView.addNewInner("Info from Contours ", "for Band ",
                    band.toString()));

            outer.add(TxView.addNewInner("   Number of Beams ",
                    Integer.toString(bandBeams.beams.size()), ""));

            outer.add(TxView.addNewInner("   Max G/T",
                    Com.shortText(bandBeams.maxGT),
                    "dB 1/K"));

            outer.add(TxView.addNewInner("   Max Tx EIRP",
                    Com.shortText(bandBeams.maxEIRP),
                    "dBW"));
        }

        outer.add(TxView.addNewInner("COSPAR",
                selection.getSatellite().getvCOSPAR(), ""));
        outer.add(TxView.addNewInner("NORAD",
                selection.getSatellite().getvNORAD(), ""));
        outer.add(TxView.addNewInner(
                selection.getSatellite().getComments(), "", ""));
        outer.add(TxView.addNewInner("Source",
                selection.getSatellite().getSource(), ""));

        // now go sequentially through Satellite Receive Side
        outer.add(TxView.addNewInner("Rx Central Frequency",
                Com.shortText(bandBeams.rXantenna.getFrequency() / 1E9), "GHz "));

        //TODO Add Contour info
        // check if contours are present (then no calculations needed)
        // now the Rx side of the satellite
        outer.add(TxView.addNewInner("Rx Noise Figure",
                Com.shortText(bandBeams.rXamplifier.
                        getNoiseFigure()), "dB"));

        outer.add(TxView.addNewInner("Rx Antenna Diameter",
                Com.shortText(
                        bandBeams.rXantenna.getDiameter()), "m"));

        outer.add(TxView.addNewInner("Rx Antenna Gain",
                Com.shortText(
                        bandBeams.rXantenna.getGain()), "dBi"));

        outer.add(TxView.addNewInner("Rx 3dB Angle",
                Com.toDMS(
                        bandBeams.rXantenna.getThreeDBangle()), "degree"));

        outer.add(TxView.addNewInner("Rx Depointing Error",
                Com.toDMS(
                        bandBeams.rXantenna.getDepointingError()), "degree"));

        // does change so not in combo/sliders
        outer.add(TxView.addNewInner("Rx Depointing Loss",
                Com.textN(
                        bandBeams.rXantenna.getDepointingLoss(), 5), "dB"));

        outer.add(TxView.addNewInner("Rx System Noise Temp",
                Com.text(
                        satellite.calcSystemNoiseTemp(band)), "K"));

        outer.add(TxView.addNewInner("Rx Satellite G/T",
                Com.shortText(
                        bandBeams.gainTemp), "dB 1/K"));

        outer.add(TxView.addNewInner("Tx Central Frequency",
                Com.shortText(
                        bandBeams.tXantenna.getFrequency() / 1E9),
                "GHz "));

        // now the Tx side of satellite
        outer.add(TxView.addNewInner("Tx Amp Power",
                Com.shortText(bandBeams.tXamplifier.
                        getPower()), "W"));

        outer.add(TxView.addNewInner("Tx Ant Diameter",
                Com.shortText(
                        bandBeams.tXantenna.getDiameter()), "m"));

        outer.add(TxView.addNewInner("Tx Ant Gain",
                Com.shortText(bandBeams.tXantenna.getGain()),
                "dBi"));

        outer.add(TxView.addNewInner("Tx Ant 3dB Angle",
                Com.toDMS(bandBeams.tXantenna.getThreeDBangle()),
                "degree"));

        outer.add(TxView.addNewInner("Tx Depointing Error",
                Com.toDMS(
                        bandBeams.tXantenna.getDepointingError()), "degree"));

        outer.add(TxView.addNewInner("Tx Pointing Loss",
                Com.shortText(
                        bandBeams.tXantenna.getDepointingLoss()),
                "dB"));

        outer.add(TxView.addNewInner("Satellite Tx EIRP",
                Com.shortText(bandBeams.EIRP),
                "dBW")
        );

        return outer;
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
        ListModel model = new DefaultListModel(
                selection.getBandSatellite().get(band));

        // show the selected satellite
        int index = combo.getSelectedIndex();

        combo.setModel(model);

        // set the satellite view present in the selection (and not this.spin)
        selection.getSatelliteView().spin = combo;

        selection.setSatellite(Satellite.satelliteHash.get(
                (String) combo.getSelectedItem()));

        String name = (String) combo.getSelectedItem();

        updateValues(selection);

        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // set the selected satellite
                selection.setSatellite(Satellite.satelliteHash.get(
                        (String) combo.getSelectedItem()));
                // select new terminals based on which terminals are nearest
                selection.comboRx(selection);
                selection.comboTx(selection);

                // update the UL path
                if (selection.getuLpath() != null) {
                    selection.getuLpath().setSatellite(selection.getSatellite());
                }
                // update the DL path 
                if (selection.getdLpath() != null) {
                    selection.getdLpath().setSatellite(selection.getSatellite());
                }
                // update list of visible terminals

                // update other values dependent on this satellite
                updateValues(selection);

            }
        });

        // combo box created so return it
        return combo;
    }

    // update from the current selection of the Satellite
    public void updateValues(Selection selection) {

        selection.getSatelliteView().setShortName("ST");
        selection.getSatelliteView().setName("Satellite");

        selection.getSatelliteView().setSummary(Com.toDMS(
                selection.getSatellite().getLongitude()));

        // get maximum EIRP that is calculated or from contours
        double display, calc, contour;
        calc = selection.getSatellite().getEIRP(selection.getBand());
        contour = selection.getSatellite().getMaxEIRPfromContours(
                selection.getBand());

        if (Com.sameValue(contour, Satellite.NEGLIGIBLE)) {
            display = calc;
        } else {
            display = contour;
        }

        selection.getSatelliteView().setValue(Com.textN(display, 5) + "dbW");

        // get max G/T for sublabel
        calc = selection.getSatellite().getGainTemp(selection.getBand());
        contour = selection.getSatellite().getMaxGTfromContours(
                selection.getBand());

        if (Com.sameValue(contour, Satellite.NEGLIGIBLE)) {
            display = calc;
        } else {
            display = contour;
        }

        selection.getSatelliteView().setSubValue(Com.textN(display, 5) + "dB/K");

        // update other view summaries in Link form
        selection.getTxView().updateMainForm(selection);
        selection.getuLpathView().updateMainForm(selection);
        selection.getRxView().updateMainForm(selection);
        selection.getCommsView().updateValues(selection);

    }

    public String getDisplayName() {
        return name;
    }

    public void displayContourValues(BandSpecificItems bandBeams,
            Form cnt, TableLayout layout) {

        TableLayout.Constraint constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);

        Label lHeading;

        if (Com.sameValue(bandBeams.maxGT, Satellite.NEGLIGIBLE)) {
            // no contours for GT found
            lHeading = new Label("G/T for terminal uses EIRP contours");

        } else {
            lHeading = new Label("G/T contours used for a terminal");
        }
        cnt.addComponent(constraint, lHeading);

        constraint = layout.createConstraint();
        // constraint.setVerticalSpan(3);
        constraint.setWidthPercentage(45);

        Label lGainTemp = new Label("Sat Max G/T");
        final Label valueRxGainTemp = new Label(Com.shortText(
                bandBeams.maxGT));
        Label unitGainTemp = new Label("dB 1/K");
        cnt.addComponent(lGainTemp);
        cnt.addComponent(valueRxGainTemp);
        cnt.addComponent(unitGainTemp);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        if (Com.sameValue(bandBeams.maxEIRP, Satellite.NEGLIGIBLE)) {
            // no contours for GT found
            lHeading = new Label("EIRP for terminal uses G/T contours");

        } else {
            lHeading = new Label("EIRP contours used for a terminal");
        }
        cnt.addComponent(constraint, lHeading);

        Label lEIRP = new Label("Satellite Max Tx EIRP");
        final Label valueTxEIRP = new Label(Com.shortText(bandBeams.maxEIRP));
        Label unitEIRP = new Label("dBW");
        cnt.addComponent(lEIRP);
        cnt.addComponent(valueTxEIRP);
        cnt.addComponent(unitEIRP);

    }

    public Form createView(final Selection selection) {

        final Satellite satellite = selection.getSatellite();

        final BandSpecificItems bandBeams = satellite.bandSpecificItems.get(
                selection.getBand());
        Form cnt = new Form("SAT " + satellite.getName());
        final RfBand.Band band = selection.getBand();
        // Container cnt = new Container(new BorderLayout());
        // sub.addComponent(cnt);

        // there are several items in Views.  Hardcoded table. Name, value, unit
        TableLayout layout = new TableLayout(30, 3);
        cnt.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setVerticalSpan(3);
        constraint.setWidthPercentage(45);

        // now go sequentially through Satellite Receive Side
        Label L01 = new Label("Rx Central Frequency");
        Label lFrequency = new Label(Com.shortText(
                bandBeams.rXantenna.getFrequency() / 1E9));
        Label L03 = new Label("GHz " + bandBeams.rXantenna.getBand());
        cnt.addComponent(constraint, L01);
        cnt.addComponent(lFrequency);
        cnt.addComponent(L03);

        // check if contours are present (then no calculations needed)
        if (!Com.sameValue(bandBeams.maxEIRP, Satellite.NEGLIGIBLE)
                || !Com.sameValue(bandBeams.maxGT, Satellite.NEGLIGIBLE)) {
            displayContourValues(bandBeams, cnt, layout);
            return cnt;
        }
        // now the Rx side of the satellite
        Label lNoiseFigureLabel = new Label("Rx Noise Figure");
        final Label lNoiseFigure = new Label(Com.shortText(bandBeams.rXamplifier.
                getNoiseFigure()));
        Label lNoiseFigureUnit = new Label("dB");

        final Slider sldrRxNoiseFigure = new Slider();
        Com.formatSlider(sldrRxNoiseFigure);
        sldrRxNoiseFigure.setMinValue((int) MathUtil.round(
                Amplifier.NOISE_FIG_LO * 10)); // x10
        sldrRxNoiseFigure.setMaxValue((int) MathUtil.round(
                Amplifier.NOISE_FIG_HI * 10));
        sldrRxNoiseFigure.setEditable(true);
        // sldrRxNoiseFigure.setPreferredW(8);
        sldrRxNoiseFigure.setIncrements(5); //
        sldrRxNoiseFigure.setProgress((int) MathUtil.round(
                bandBeams.rXamplifier.getNoiseFigure() * 10));
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
        Com.formatSlider(sldrRxDiameter);

        sldrRxDiameter.setMinValue((int) MathUtil.round(Antenna.DIAMETER_LO * 10)); // x10
        sldrRxDiameter.setMaxValue((int) MathUtil.round(Antenna.DIAMETER_HI * 10));
        sldrRxDiameter.setEditable(true);
        sldrRxDiameter.setIncrements(5); //
        sldrRxDiameter.setProgress((int) MathUtil.round(bandBeams.rXantenna.getDiameter() * 10));

        sldrRxDiameter.setRenderValueOnTop(true);
        constraint = layout.createConstraint();

        final Label lRxDiameter = new Label(Com.shortText(
                bandBeams.rXantenna.getDiameter()));
        Label lDiameterUnit = new Label("m");

        cnt.addComponent(lRxDiameterLabel);
        cnt.addComponent(lRxDiameter);
        cnt.addComponent(lDiameterUnit);
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrRxDiameter);

        Label L31 = new Label("  Gain");
        final Label lRxGain = new Label(Com.shortText(
                bandBeams.rXantenna.getGain()));
        Label L33 = new Label("dBi");
        cnt.addComponent(L31);
        cnt.addComponent(lRxGain);
        cnt.addComponent(L33);

        Label L41 = new Label("  3dB Angle");
        final Label lRxThreeDBangle = new Label(Com.toDMS(
                bandBeams.rXantenna.getThreeDBangle()));
        Label L43 = new Label("degree");
        cnt.addComponent(L41);
        cnt.addComponent(lRxThreeDBangle);
        cnt.addComponent(L43);

        Label lRxDepointingErrorLabel = new Label("  Depointing Error");
        final Slider sldrRxDepointingError = new Slider();
        Com.formatSlider(sldrRxDepointingError);

        sldrRxDepointingError.setMinValue((int) MathUtil.round(
                Antenna.DEPOINTING_LO * 10 * 180.0 / Com.PI)); // x10
        sldrRxDepointingError.setMaxValue((int) MathUtil.round(
                Antenna.DEPOINTING_HI * 10 * 180.0 / Com.PI));
        sldrRxDepointingError.setEditable(true);
        sldrRxDepointingError.setIncrements(5); //
        sldrRxDepointingError.setProgress((int) MathUtil.round(
                bandBeams.rXantenna.getDepointingError() * 10 * 180 / Com.PI));
        sldrRxDepointingError.setRenderValueOnTop(true);

        final Label lRxDepointingError = new Label(Com.toDMS(
                bandBeams.rXantenna.getDepointingError()));
        Label lDepointingErrorUnit = new Label("degree");

        cnt.addComponent(lRxDepointingErrorLabel);
        cnt.addComponent(lRxDepointingError);
        cnt.addComponent(lDepointingErrorUnit);

        constraint = layout.createConstraint();

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrRxDepointingError);

        // does change so not in combo/sliders
        Label lRxPointLoss = new Label(" Depointing Loss");
        final Label valueRxPointLoss = new Label(Com.textN(
                bandBeams.rXantenna.getDepointingLoss(), 5));
        Label unitPointLoss = new Label("dB");
        cnt.addComponent(lRxPointLoss);
        cnt.addComponent(valueRxPointLoss);
        cnt.addComponent(unitPointLoss);

        Label lsysTemp = new Label(" System Noise Temp");
        final Label valueRxsysTemp = new Label(Com.text(
                satellite.calcSystemNoiseTemp(band)));
        Label unitsysTemp = new Label("K");
        cnt.addComponent(lsysTemp);
        cnt.addComponent(valueRxsysTemp);
        cnt.addComponent(unitsysTemp);
        cnt.setScrollable(true);

        // attenuation does not depend on anything so not incouded in
        // sliders
        Label lGainTemp = new Label("Satellite G/T");
        final Label valueRxGainTemp = new Label(Com.shortText(
                bandBeams.gainTemp));
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
        // all actions at the end to update other fields
        sldrRxNoiseFigure.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected noise figure "
                        + sldrRxNoiseFigure.getText(), Log.DEBUG);
                try {
                    bandBeams.rXamplifier.
                            setNoiseFigure(Double.parseDouble(
                                            sldrRxNoiseFigure.getText()) / 10.0);
                    // update EIRP
                    lNoiseFigure.setText(Com.shortText(bandBeams.rXamplifier.
                            getNoiseFigure()));
                    valueRxsysTemp.setText(Com.text(
                            satellite.calcSystemNoiseTemp(band)));
                    valueRxGainTemp.setText(Com.textN(bandBeams.gainTemp, 5));
                    // update satellite specific name, summary, value, subValue
                    // Here only the gain (which is subValue) changes
                    selection.getSatelliteView().updateValues(selection);
                    // does not change depointing

                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for Rx Noise Figure "
                            + sldrRxNoiseFigure.getText(), Log.DEBUG);
                }
            }
        });

        sldrRxDepointingError.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected depointingError "
                        + sldrRxDepointingError.getText(), Log.DEBUG);
                try {

                    bandBeams.rXantenna.
                            setDepointingError(Double.parseDouble(
                                            sldrRxDepointingError.getText()) * (Com.PI / 180) / (10.0));
                    // update EIRP and three DB
                    lRxDepointingError.setText(Com.toDMS(bandBeams.rXantenna.getDepointingError()));

                    lRxGain.setText(Com.shortText(bandBeams.rXantenna.getGain()));
                    valueRxPointLoss.setText(Com.textN(
                            bandBeams.rXantenna.getDepointingLoss(), 5));
                    valueRxGainTemp.setText(Com.textN(bandBeams.gainTemp, 5));

                    // should not change
                    valueRxsysTemp.setText(Com.text(
                            satellite.calcSystemNoiseTemp(band)));
                    selection.getSatelliteView().updateValues(selection);
                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for Rx Depointing Error "
                            + sldrRxDiameter.getText(), Log.DEBUG);

                }

            }
        });

        sldrRxDiameter.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected diameter "
                        + sldrRxDiameter.getText(), Log.DEBUG);
                try {

                    // convert from cm to m first
                    bandBeams.rXantenna.
                            setDiameter(Double.parseDouble(
                                            sldrRxDiameter.getText()) / 10.0);
                    // update EIRP and three DB
                    lRxDiameter.setText(Com.shortText(bandBeams.rXantenna.getDiameter()));
                    lRxThreeDBangle.setText(Com.toDMS(bandBeams.rXantenna.getThreeDBangle()));
                    lRxGain.setText(Com.shortText(bandBeams.rXantenna.getGain()));
                    valueRxPointLoss.setText(Com.textN(
                            bandBeams.rXantenna.getDepointingLoss(), 5));
                    valueRxGainTemp.setText(Com.textN(bandBeams.gainTemp, 5));

                    // should not change
                    valueRxsysTemp.setText(Com.text(
                            satellite.calcSystemNoiseTemp(band)));
                    selection.getSatelliteView().updateValues(selection);
                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for Rx diameter " + sldrRxDiameter.getText(), Log.DEBUG);

                }

            }
        });

        Label filler = new Label(" ");
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, filler);

        Label LTx01 = new Label("Tx Central Frequency");
        Label lTxFrequency = new Label(Com.shortText(
                bandBeams.tXantenna.getFrequency() / 1E9));
        Label LTx03 = new Label("GHz " + bandBeams.tXantenna.getBand());
        cnt.addComponent(LTx01);
        cnt.addComponent(lTxFrequency);
        cnt.addComponent(LTx03);

        // now the Tx side of satellite
        Label L11 = new Label("Tx Amp Power");
        Label L13 = new Label("W");

        final Slider sldrTxPower = new Slider();
        Com.formatSlider(sldrTxPower);
        sldrTxPower.setMinValue((int) MathUtil.round(Amplifier.POWER_LO * 10)); // x10
        sldrTxPower.setMaxValue((int) MathUtil.round(Amplifier.POWER_HI * 10));
        sldrTxPower.setEditable(true);
        // sldrTxPower.setPreferredW(8);
        sldrTxPower.setIncrements(5); //
        sldrTxPower.setProgress((int) MathUtil.round(
                bandBeams.tXamplifier.getPower() * 10));
        sldrTxPower.setRenderValueOnTop(true);
        sldrTxPower.getStyle().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));

        final Label lTxAmplifier = new Label(Com.shortText(bandBeams.tXamplifier.
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

        Com.formatSlider(sldrTxDiameter);
        sldrRxDiameter.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM,
                Font.STYLE_PLAIN, Font.SIZE_SMALL));

        sldrTxDiameter.setMinValue((int) MathUtil.round(Antenna.DIAMETER_LO * 10)); // x10
        sldrTxDiameter.setMaxValue((int) MathUtil.round(Antenna.DIAMETER_HI * 10));
        sldrTxDiameter.setEditable(true);

        sldrTxDiameter.setIncrements(5); //
        sldrTxDiameter.setProgress((int) MathUtil.round(
                bandBeams.tXantenna.getDiameter() * 10));

        sldrTxDiameter.setRenderValueOnTop(true);
        final Label lTxDiameter = new Label(Com.shortText(
                bandBeams.tXantenna.getDiameter()));
        cnt.addComponent(L21);
        cnt.addComponent(lTxDiameter);
        cnt.addComponent(lTxDiaUnit);

        constraint = layout.createConstraint();

        constraint.setHorizontalSpan(3);

        cnt.addComponent(constraint, sldrTxDiameter);

        Label lTxGainLabel = new Label("  Gain");
        final Label lTxGain = new Label(Com.shortText(bandBeams.tXantenna.getGain()));
        Label lTxGainUnit = new Label("dBi");
        cnt.addComponent(lTxGainLabel);
        cnt.addComponent(lTxGain);
        cnt.addComponent(lTxGainUnit);

        Label lTx3dBLabel = new Label("  3dB Angle");
        final Label lTx3dB = new Label(Com.toDMS(bandBeams.tXantenna.getThreeDBangle()));
        Label lTx3dBUnit = new Label("degree");
        cnt.addComponent(lTx3dBLabel);
        cnt.addComponent(lTx3dB);
        cnt.addComponent(lTx3dBUnit);

        Label lTxDepointingErrorLabel = new Label("  Depointing Error");
        Label lTxDepointingErrorUnit = new Label("degree");
        final Slider sldrTxDepointingError = new Slider();
        Com.formatSlider(sldrTxDepointingError);

        sldrTxDepointingError.setMinValue((int) MathUtil.round(
                Antenna.DEPOINTING_LO * 10 * 180.0 / Com.PI)); // x10
        sldrTxDepointingError.setMaxValue((int) MathUtil.round(
                Antenna.DEPOINTING_HI * 10 * 180.0 / Com.PI));
        sldrTxDepointingError.setEditable(true);
        sldrTxDepointingError.setIncrements(5); //
        sldrTxDepointingError.setProgress((int) MathUtil.round(
                bandBeams.tXantenna.getDepointingError() * 10 * 180 / Com.PI));
        sldrTxDepointingError.setRenderValueOnTop(true);

        final Label lTxDepointingError = new Label(Com.toDMS(
                bandBeams.tXantenna.getDepointingError()));

        cnt.addComponent(lTxDepointingErrorLabel);
        cnt.addComponent(lTxDepointingError);
        cnt.addComponent(lTxDepointingErrorUnit);

        constraint = layout.createConstraint();

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrTxDepointingError);

        // does change so not in combo/sliders
        Label lTxPointLoss = new Label(" Pointing Loss");
        final Label valueTxPointLoss = new Label(Com.shortText(
                bandBeams.tXantenna.getDepointingLoss()));
        Label unitTxPointLoss = new Label("dB");
        cnt.addComponent(lTxPointLoss);
        cnt.addComponent(valueTxPointLoss);
        cnt.addComponent(unitTxPointLoss);

        Label lEIRP = new Label("Satellite Tx EIRP");
        final Label valueTxEIRP = new Label(Com.shortText(bandBeams.EIRP));
        Label unitEIRP = new Label("dBW");
        cnt.addComponent(lEIRP);
        cnt.addComponent(valueTxEIRP);
        cnt.addComponent(unitEIRP);

        // all actions at the end to update other fields
        sldrTxPower.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected power " + sldrTxPower.getText(), Log.DEBUG);
                try {
                    bandBeams.tXamplifier.
                            setPower(Double.parseDouble(sldrTxPower.getText()) / 10.0);
                    // update EIRP
                    lTxAmplifier.setText(Com.shortText(bandBeams.tXamplifier.
                            getPower()));
                    valueTxEIRP.setText(Com.shortText(bandBeams.EIRP));
                    selection.getSatelliteView().updateValues(selection);
                    // does not change depointing

                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for Tx power " + sldrTxPower.getText(), Log.DEBUG);
                }
            }
        });

        sldrTxDiameter.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected Tx diameter " + sldrTxDiameter.getText(), Log.DEBUG);
                try {

                    // convert from cm to m first
                    bandBeams.tXantenna.
                            setDiameter(Double.parseDouble(sldrTxDiameter.getText()) / 10.0);
                    // update EIRP and three DB
                    sldrTxDiameter.setText(Com.shortText(bandBeams.tXantenna.getDiameter()));
                    lTxDiameter.setText(Com.shortText(bandBeams.tXantenna.getDiameter()));
                    lTx3dB.setText(Com.toDMS(bandBeams.tXantenna.getThreeDBangle()));
                    lTxGain.setText(Com.shortText(bandBeams.tXantenna.getGain()));
                    valueTxPointLoss.setText(Com.shortText(
                            bandBeams.tXantenna.getDepointingLoss()));
                    valueTxEIRP.setText(Com.shortText(bandBeams.EIRP));
                    selection.getSatelliteView().updateValues(selection);
                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for Tx diameter "
                            + sldrTxDiameter.getText(), Log.DEBUG);

                }

            }
        });

        sldrTxDepointingError.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("SatelliteView: selected Tx depointingError "
                        + sldrTxDepointingError.getText(), Log.DEBUG);
                try {

                    bandBeams.tXantenna.
                            setDepointingError(Double.parseDouble(
                                            sldrTxDepointingError.getText()) * (Com.PI / 180) / (10.0));
                    // update EIRP and three DB
                    lTxDepointingError.setText(Com.toDMS(bandBeams.tXantenna.getDepointingError()));

                    lTxGain.setText(Com.shortText(bandBeams.tXantenna.getGain()));
                    valueTxPointLoss.setText(Com.shortText(
                            bandBeams.tXantenna.getDepointingLoss()));
                    // valueTxGainTemp.setText(Com.shortText(bandBeams.gainTemp));

                    // should not change
                    //valueTxsysTemp.setText(Com.text(      satellite.calcSystemNoiseTemp(band)));
                    selection.getSatelliteView().updateValues(selection);
                } catch (java.lang.NumberFormatException e) {
                    Log.p("SatelliteView: bad number for Tx Depointing Error "
                            + sldrRxDiameter.getText(), Log.DEBUG);

                }

            }
        });
        cnt.setScrollable(true);
        // have a multi-row table layout and dump the transmit terminal values
        return cnt;
    }
}
