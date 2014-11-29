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
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Slider;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.Resources;
import com.codename1.util.MathUtil;

public class DlPathView extends View {

    public DlPathView() {

    }

    public DlPathView(Selection selection) {

    }

    public String getDisplayName() {
        return name;
    }

    // this is simple so return terminal's name and location
    public Component getWidget(final Selection selection) {
        Label lName = new Label(getName());

        // indexSatellite has all satellites, so get the band specific list
        if (selection.getrXterminal() == null) {
            Log.p("DlPathView: Can't find Rx terminal ", Log.DEBUG);
            return lName;
        }

        // chcek for satellite
        if (selection.getSatellite() == null) {
            Log.p("DlPathView: Can't find satellite ", Log.DEBUG);
            return lName;
        }

        updateValues(selection);
        return lName;
    }

    public void updateValues(Selection selection) {

        if (selection.getdLpath() != null) {
            selection.getdLpathView().setShortName("DL");
            selection.getdLpathView().setName("DL ");  // short

            selection.getdLpathView().setValue("" + Com.toDMS(
                    selection.getdLpath().getElevation()).substring(0, 6));

            selection.getdLpathView().setSummary(Com.textN(selection.getdLpath().
                    getCNo(), 5) + "dBHz");

            selection.getdLpathView().setSubValue(Com.textN(selection.getdLpath().
                    getSpectralDensity(), 5) + "dBHz");
        }

        selection.getCommsView().updateValues(selection);
    }

    public Form createView(final Selection selection) {

        Form cnt = new Form("Tx SAT: " + selection.getSatellite().getName());

    

        // there are six items in Views.  Hardcoded table. Name, value, unit
        TableLayout layout = new TableLayout(15, 3);
        cnt.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setHorizontalSpan(3);
        constraint.setWidthPercentage(40);

        // now go sequentially through the Uplink path fields
        final Terminal rxTerm = selection.getdLpath().getTerminal();
        final Satellite satellite = selection.getdLpath().getSatellite();

        Label L01 = new Label("Center Frequency");
        Label lFrequency = new Label(Com.shortText(satellite.bandSpecificItems.get(
                selection.getBand()).tXantenna.getFrequency() / 1E9));
        Label L03 = new Label("GHz " + satellite.bandSpecificItems.
                get(selection.getBand()).tXantenna.getBand());
        cnt.addComponent(constraint, L01);
        constraint = layout.createConstraint();
 
        constraint.setWidthPercentage(30);
        cnt.addComponent(constraint,lFrequency);
        cnt.addComponent(L03);

        Label L61 = new Label("Sat" + "@" + Com.shortText(selection.getuLpath().
                getSatellite().getLongitude() * 180.0 / Com.PI) + Com.DEGREE + " EIRP");
        final Label L62 = new Label(
                Com.shortText(selection.getSatellite().bandSpecificItems.
                        get(selection.getBand()).EIRP));

        // TODO check EIRP for satellite terminal
        Label L63 = new Label("dbW");

        cnt.addComponent(L61);
        cnt.addComponent(L62);
        cnt.addComponent(L63);

        Label lLatitude = new Label("Term Latitude");
        Label lLatitudeUnit = new Label("degree");
        final Slider sldrLatitude = new Slider();
        Com.formatSlider(sldrLatitude);
        sldrLatitude.getStyle().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));

        // can't take negative values, 81.3
        sldrLatitude.setMinValue((int) MathUtil.round(0.0)); // x10
        sldrLatitude.setMaxValue((int) MathUtil.round(1626.0));
        sldrLatitude.setEditable(true);
        // sldrLatitude.setPreferredW(8);
        sldrLatitude.setIncrements(5); //

        // display the current latitude in degrees
        sldrLatitude.setProgress((int) MathUtil.round(rxTerm.getLatitude()
                * 180.0 * 10 / Com.PI) + 813);
        sldrLatitude.setRenderValueOnTop(true);

        final Label valueLatitude = new Label(Com.toDMS(rxTerm.getLatitude()) + "");
        cnt.addComponent(lLatitude);

        cnt.addComponent(valueLatitude);

        cnt.addComponent(lLatitudeUnit);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrLatitude);

        Label lLongitude = new Label("Term Longitude");
        Label lLongitudeUnit = new Label("degree");
        final Slider sldrLongitude = new Slider();
        Com.formatSlider(sldrLongitude);

        // longitude should be around the satellite longitude (still -81.3 to +81.3
        sldrLongitude.setMinValue((int) MathUtil.round(0.0));
        sldrLongitude.setMaxValue((int) MathUtil.round(1626.0));
        sldrLongitude.setEditable(true);
        sldrLongitude.getStyle().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        //L22.setPreferredW(8);
        sldrLongitude.setIncrements(5); //
        // center of slider is the position of the satellite.  First
        // become relative to satellite which is (terminal - satellite). 
        // then make the 0 the middle point (by adding 813).
        sldrLongitude.setProgress((int) (MathUtil.round(rxTerm.getLongitude()
                * 180.0 * 10 / Com.PI) - selection.getuLpath().
                getSatellite().getLongitude() * 180.0 * 10.0 / Com.PI) + 813);

        sldrLongitude.setRenderValueOnTop(true);
        final Label valueLongitude = new Label(Com.toDMS(
                rxTerm.getLongitude()) + "d");
        cnt.addComponent(lLongitude);

        cnt.addComponent(valueLongitude);
        cnt.addComponent(lLongitudeUnit);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrLongitude);
        Label lElevation = new Label("Term Elevation");
        final Label valueElevation = new Label(Com.toDMS(
                selection.getdLpath().getElevation()));
        Label unitElevation = new Label(" ");
        cnt.addComponent(lElevation);
        cnt.addComponent(valueElevation);
        cnt.addComponent(unitElevation);

        Label lAzimuth = new Label("    Azimuth");
        final Label valueAzimuth = new Label(Com.toDMS(
                selection.getdLpath().getAzimuth()));
        Label unitAzimuth = new Label("d");
        cnt.addComponent(lAzimuth);
        cnt.addComponent(valueAzimuth);
        cnt.addComponent(unitAzimuth);

        Label lDistance = new Label("    Distance");
        final Label valueDistance = new Label(Com.textN(selection.
                getdLpath().getDistance() / 1E3, 8));
        Label unitDistance = new Label("km");
        cnt.addComponent(lDistance);
        cnt.addComponent(valueDistance);
        cnt.addComponent(unitDistance);

        Label lPathLoss = new Label("    DL PathLoss");
        final Label valuePathLoss = new Label(Com.text(selection.
                getdLpath().getPathLoss()));
        Label unitPathLoss = new Label("dB");
        cnt.addComponent(lPathLoss);
        cnt.addComponent(valuePathLoss);
        cnt.addComponent(unitPathLoss);

        // attenuation does not depend on anything so not incouded in
        // sliders
        Label lAttenuation = new Label("Atmos Atten");
        final Label valueAttenuation = new Label(Com.shortText(
                selection.getdLpath().getAttenuation()));
        Label unitAttenuation = new Label("dB");
        cnt.addComponent(lAttenuation);
        cnt.addComponent(valueAttenuation);
        cnt.addComponent(unitAttenuation);

        // attenuation does not depend on anything so not incouded in
        // sliders
                Label lPowerRx = new Label("Power Received at Term");
        final Label valuePowerRx = new Label(Com.textN(
                selection.getdLpath().getPowerReceived(), 6));
        Label unitPowerRx = new Label("dBW");
        cnt.addComponent(lPowerRx);
        cnt.addComponent(valuePowerRx);
        cnt.addComponent(unitPowerRx);

        Label lSpecDensity = new Label("Spectral Densisty at Term");
        final Label valueSpecDensity = new Label(Com.textN(
                selection.getdLpath().getSpectralDensity(), 6));
        Label unitSpecDensity = new Label("dBW/m2");
        cnt.addComponent(lSpecDensity);
        cnt.addComponent(valueSpecDensity);
        cnt.addComponent(unitSpecDensity);
        
        Label lGainTemp = new Label("Rx Terminal G/T");
        final Label valueGainTemp = new Label(Com.shortText(
                selection.getdLpath().getTerminal().getGainTemp()));
        Label unitGainTemp = new Label("dB 1/K");
        cnt.addComponent(lGainTemp);
        cnt.addComponent(valueGainTemp);
        cnt.addComponent(unitGainTemp);

        // C/No depends on Tx EIRP and the path loss
        Label lCNo = new Label("Downlink C/No");
        final Label valueCNo = new Label(Com.textN(
                selection.getdLpath().getCNo(), 7));

        Label unitCNo = new Label("dB Hz");
        cnt.addComponent(lCNo);
        cnt.addComponent(valueCNo);
        cnt.addComponent(unitCNo);

    

        // all actions at the end to update other fields
        sldrLatitude.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("DlPathView: selected latitude " + sldrLatitude.getText(), Log.DEBUG);
                try {
                    // Slider can only show positive values
                    // you can reach rx terminal in Path and also in Selection
                    // need to fix this here and in UL Path View TODO
                    selection.getrXterminal().
                            setLatitude((Double.parseDouble(sldrLatitude.getText()) - 813.0)
                                    * Com.PI / (180.0 * 10.0));
                    // update all labels
                    valueLatitude.setText(Com.toDMS(selection.getrXterminal().
                            getLatitude()));

                    valueElevation.setText(Com.toDMS(selection.getdLpath().
                            getElevation()));

                    valueAzimuth.setText(Com.toDMS(selection.getdLpath().
                            getAzimuth()));
                    valueDistance.setText(Com.textN(selection.getdLpath().
                            getDistance() / 1E3, 8));

                    valuePathLoss.setText(Com.shortText(selection.getdLpath().
                            getPathLoss()));

                    valueCNo.setText(Com.textN(
                            selection.getdLpath().getCNo(), 7));
                    valueSpecDensity.setText(Com.textN(
                            selection.getdLpath().getSpectralDensity(), 7));
                    valuePowerRx.setText(Com.textN(
                            selection.getuLpath().getPowerReceived(),7));
                    updateValues(selection);

                } catch (java.lang.NumberFormatException e) {
                    Log.p("DlPathView: bad number for Latitude "
                            + sldrLatitude.getText(), Log.DEBUG);
                }
            }
        });

        sldrLongitude.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("DlPathView: selected longitude " + sldrLongitude.getText(), Log.DEBUG);
                try {

                    // Slider can only show positive values, so correct with respect
                    // to satellite latitude (which was the center with +/- 80 degree
                    // first subtract 813 and then add satellite's position 
                    selection.getrXterminal().
                            setLongitude((Double.parseDouble(sldrLongitude.getText())
                                    - 813.0) * Com.PI / (180.0 * 10.0)
                                    + selection.getdLpath().getSatellite().getLongitude()
                            );
                    // update all labels
                    valueLongitude.setText(Com.toDMS(selection.getrXterminal().
                            getLongitude()));

                    valueElevation.setText(Com.toDMS(selection.getdLpath().
                            getElevation()));

                    valueAzimuth.setText(Com.toDMS(selection.getdLpath().
                            getAzimuth()));
                    valueDistance.setText(Com.textN(selection.getdLpath().
                            getDistance() / 1E3, 8));      // convert to km

                    valuePathLoss.setText(Com.text(selection.getdLpath().
                            getPathLoss()));            // already in dB

                    valueCNo.setText(Com.textN(
                            selection.getdLpath().getCNo(), 7));
                    valueSpecDensity.setText(Com.textN(
                            selection.getdLpath().getSpectralDensity(), 7));
                    valuePowerRx.setText(Com.textN(
                            selection.getuLpath().getPowerReceived(),7));
                    updateValues(selection);

                } catch (java.lang.NumberFormatException e) {
                    Log.p("DlPathView: bad number for diameter " + sldrLongitude.getText(), Log.DEBUG);

                }

            }
        });
        cnt.setScrollable(false);
        // have a multi-row table layout and dump the transmit terminal values
        return cnt;
    }

    // this is downlink
    private String calcCNo(Selection selection) {
        String str;
        // CNo depends on Tx and receive of satellite, here EIRP, loss
        // and gain are all in dBHz
        str = Com.text(selection.getdLpath().getCNo());
        return str;
    }

    // this is for downlink (satellite is Tx)
    private String calcSpecDens(Selection selection) {
        String str;
        // Spec density depends on EIRP/4piR2 but EIRP is already in dB
        str = Com.text(selection.getdLpath().getSpectralDensity());
        return str;
    }
}
