/*
 * OVERVIEW
 * TODO:  when band, satellite, terminal changes, then Affected list also 
 * needs to be udpated here and in other views.
 *
 */
package com.erudyo.satellite;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Slider;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.Resources;
import com.codename1.ui.util.Resources;
import com.codename1.util.MathUtil;
import static com.erudyo.satellite.TxView.addNewInner;
import java.util.ArrayList;

public class UlPathView extends View {

    public UlPathView() {

    }

    public UlPathView(Selection selection) {
        super.name = "UL Path";

    }

    public String getDisplayName() {
        return name;
    }

    // this is simple so return terminal's name and location
    public Component getWidget(final Selection selection) {
        Label lName = new Label(getName());

        // indexSatellite has all satellites, so get the band specific list
        if (selection.gettXterminal() == null) {
            Log.p("UlPathView: Can't find Tx terminal ", Log.DEBUG);
            return lName;
        }

        // chcek for satellite
        if (selection.getSatellite() == null) {
            Log.p("UlPathView: Can't find satellite ", Log.DEBUG);
            return lName;
        }

        updateMainForm(selection);
        // actually lName is not used (since it is not a combo)
        return lName;
    }

    // update Main form from the current selection of the terminal
    public void updateMainForm(Selection selection) {

        if (selection.getuLpath() != null) {
            selection.getuLpathView().setShortName("UL");
            selection.getuLpathView().setName("UL ");  // short

            selection.getuLpathView().setValue("El " + Com.toDMS(
                    selection.getuLpath().getElevation()).substring(0, 6));

            selection.getuLpathView().setSummary("CNo " + Com.textN(selection.getuLpath().
                    getCNo(), 5));

            selection.getuLpathView().setSubValue("SpD " + Com.textN(selection.getuLpath().
                    getSpectralDensity(), 6));
        }

        selection.getCommsView().updateValues(selection);

    }

    public ArrayList<ArrayList<String>> getText(Selection selection) {
        ArrayList<ArrayList<String>> outer
                = new ArrayList<ArrayList<String>>();
        outer.add(TxView.addNewInner(
                selection.getuLpath().getName(),"", ""));

        outer.add(TxView.addNewInner("Tx Terminal: ",
                selection.gettXterminal().getName(), ""));

        Terminal txTerm = selection.gettXterminal();

        outer.add(TxView.addNewInner("Center Frequency",
                Com.shortText(txTerm.gettXantenna().getFrequency() / 1E9),
                "GHz "));

        outer.add(TxView.addNewInner("Satellite EIRP ",
                Com.shortText(selection.getSatellite().bandSpecificItems.
                        get(selection.getBand()).EIRP), "dbW"));

        outer.add(TxView.addNewInner("Tx Term Latitude",
                Com.toDMS(txTerm.getLatitude()), "degree"));

        outer.add(TxView.addNewInner("Tx Term Longitude",
                Com.toDMS(txTerm.getLongitude()), "degree"));

        outer.add(TxView.addNewInner("Tx Term Azimuth",
                Com.toDMS(selection.getuLpath().getAzimuth()), "degree"));

        outer.add(TxView.addNewInner("Distance - Sat",
                Com.textN(selection.getuLpath().getDistance() / 1E3, 8), "km"));

        outer.add(TxView.addNewInner("Sat Path Loss",
                Com.textN(selection.getuLpath().getPathLoss(), 7), "dB"));

        outer.add(TxView.addNewInner("Atmos Atten",
                Com.textN(selection.getuLpath().getAttenuation(), 7), "dB"));

        outer.add(TxView.addNewInner("Power Received at Sat",
                Com.textN(selection.getuLpath().getPowerReceived(), 6), "dBW"));

        outer.add(TxView.addNewInner("Spectral Density at Sat.",
                Com.textN(selection.getuLpath().getSpectralDensity(), 6), "dBW/m2"));

        outer.add(TxView.addNewInner("Satellite Band G/T",
                Com.shortText(selection.getuLpath().getSatellite().bandSpecificItems.
                        get(selection.getBand()).gainTemp),
                "dB 1/K"));

        outer.add(TxView.addNewInner("Sat G/T for Tx Term",
                Com.shortText(selection.getuLpath().getSatellite().
                        getGainTempForTerminal(txTerm)), "dB 1/K"));
        outer.add(TxView.addNewInner("UL C/No",
                Com.textN(selection.getuLpath().getCNo(), 7),
                "dB Hz"));

        return outer;
    }

    public Form createView(final Selection selection) {

        Form path = new Form(this.getName());

        Form cnt = new Form("Tx Terminal: " + selection.gettXterminal().getName());

        // there are six items in Views.  Hardcoded table. Name, value, unit
        TableLayout layout = new TableLayout(17, 3);
        cnt.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setHorizontalSpan(2);
        constraint.setWidthPercentage(40);

        // now go sequentially through the Uplink path fields
        final Terminal txTerm = selection.gettXterminal();

        Label L01 = new Label("Center Frequency");
        Label lFrequency = new Label(Com.shortText(txTerm.gettXantenna().getFrequency() / 1E9));
        Label L03 = new Label("GHz " + txTerm.getBand());
        cnt.addComponent(constraint, L01);
        constraint = layout.createConstraint();
        constraint.setWidthPercentage(30);
        cnt.addComponent(constraint, lFrequency);
        cnt.addComponent(L03);

        Label lSatellite = new Label("Sat " + "@ " + Com.shortText(selection.getuLpath().
                getSatellite().getLongitude() * 180.0 / Com.PI) + Com.DEGREE + " Band EIRP");
        final Label lSatelliteEIRP = new Label(
                Com.shortText(selection.getSatellite().bandSpecificItems.
                        get(selection.getBand()).EIRP));

        // TODO check EIRP for satellite terminal
        Label lEIRPunit = new Label("dbW");

        cnt.addComponent(lSatellite);
        cnt.addComponent(lSatelliteEIRP);
        cnt.addComponent(lEIRPunit);

        Label lLatitude = new Label("Tx Term Latitude");
        Label lLatitudeUnit = new Label("degree");
        final Slider sldrLatitude = new Slider();
        Com.formatSlider(sldrLatitude);
        sldrLatitude.getStyle().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));

        // can't take negative values, 81.3
        sldrLatitude.setMinValue((int) MathUtil.round(0.0)); // x10
        sldrLatitude.setMaxValue((int) MathUtil.round(
                MathUtil.round(10 * 2 * Com.VISIBLE_ANGLE * 180.0 / Com.PI)));
        sldrLatitude.setEditable(true);
        // sldrLatitude.setPreferredW(8);
        sldrLatitude.setIncrements(5); //

        // display the current latitude in degrees
        sldrLatitude.setProgress((int) MathUtil.round(txTerm.getLatitude()
                * 180.0 * 10 / Com.PI) + (int) MathUtil.round(10 * Com.VISIBLE_ANGLE * 180.0 / Com.PI));
        sldrLatitude.setRenderValueOnTop(true);

        final Label valueLatitude = new Label(Com.toDMS(txTerm.getLatitude()) + "");
        cnt.addComponent(lLatitude);
        cnt.addComponent(valueLatitude);
        cnt.addComponent(lLatitudeUnit);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrLatitude);

        Label lLongitude = new Label("Tx Term Longitude");
        Label lLongitudeUnit = new Label("degree");
        final Slider sldrLongitude = new Slider();
        Com.formatSlider(sldrLongitude);

        // longitude should be around the satellite longitude (still -81.3 to +81.3
        sldrLongitude.setMinValue((int) MathUtil.round(0.0));
        sldrLongitude.setMaxValue((int) MathUtil.round(
                (int) MathUtil.round(10 * 2 * Com.VISIBLE_ANGLE * 180.0 / Com.PI)));
        sldrLongitude.setEditable(true);
        sldrLongitude.getStyle().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        //L22.setPreferredW(8);
        sldrLongitude.setIncrements(5); //
        // center of slider is the position of the satellite.  First
        // become relative to satellite which is (terminal - satellite). 
        // then make the 0 the middle point (by adding 813).
        sldrLongitude.setProgress((int) (MathUtil.round(txTerm.getLongitude()
                * 180.0 * 10 / Com.PI) - selection.getuLpath().
                getSatellite().getLongitude() * 180.0 * 10.0 / Com.PI)
                + (int) MathUtil.round(10 * Com.VISIBLE_ANGLE * 180.0 / Com.PI));

        sldrLongitude.setRenderValueOnTop(true);
        final Label valueLongitude = new Label(Com.toDMS(
                txTerm.getLongitude()) + "");
        cnt.addComponent(lLongitude);

        cnt.addComponent(valueLongitude);
        cnt.addComponent(lLongitudeUnit);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrLongitude);
        Label lElevation = new Label("Tx Term Elevation");
        final Label valueElevation = new Label(Com.toDMS(
                selection.getuLpath().getElevation()));
        Label unitElevation = new Label("degree");
        cnt.addComponent(lElevation);
        cnt.addComponent(valueElevation);
        cnt.addComponent(unitElevation);

        Label lAzimuth = new Label("    Azimuth");
        final Label valueAzimuth = new Label(Com.toDMS(
                selection.getuLpath().getAzimuth()));
        Label unitAzimuth = new Label("degree");
        cnt.addComponent(lAzimuth);
        cnt.addComponent(valueAzimuth);
        cnt.addComponent(unitAzimuth);

        Label lDistance = new Label("    Distance - Sat");
        final Label valueDistance = new Label(Com.textN(selection.
                getuLpath().getDistance() / 1E3, 8));
        Label unitDistance = new Label("km");
        cnt.addComponent(lDistance);
        cnt.addComponent(valueDistance);
        cnt.addComponent(unitDistance);

        Label lPathLoss = new Label("    Sat Path Loss");
        final Label valuePathLoss = new Label(Com.textN(selection.
                getuLpath().getPathLoss(), 7));
        Label unitPathLoss = new Label("dB");
        cnt.addComponent(lPathLoss);
        cnt.addComponent(valuePathLoss);
        cnt.addComponent(unitPathLoss);

        // attenuation does not depend on anything so not incouded in
        // sliders
        Label lAttenuation = new Label("Atmos Atten");
        final Label valueAttenuation = new Label(Com.textN(
                selection.getuLpath().getAttenuation(), 7));
        Label unitAttenuation = new Label("dB");
        cnt.addComponent(lAttenuation);
        cnt.addComponent(valueAttenuation);
        cnt.addComponent(unitAttenuation);

        // attenuation does not depend on anything so not incouded in
        // sliders
        Label lPowerRx = new Label("Power Received at Sat");
        final Label valuePowerRx = new Label(Com.textN(
                selection.getuLpath().getPowerReceived(), 6));
        Label unitPowerRx = new Label("dBW");
        cnt.addComponent(lPowerRx);
        cnt.addComponent(valuePowerRx);
        cnt.addComponent(unitPowerRx);

        Label lSpecDensity = new Label("Spectral Density at Sat.");
        final Label valueSpecDensity = new Label(Com.textN(
                selection.getuLpath().getSpectralDensity(), 6));
        Label unitSpecDensity = new Label("dBW/m2");
        cnt.addComponent(lSpecDensity);
        cnt.addComponent(valueSpecDensity);
        cnt.addComponent(unitSpecDensity);

        Label lGainTemp = new Label("Satellite Band G/T");
        final Label valueGainTemp = new Label(Com.shortText(
                selection.getuLpath().getSatellite().bandSpecificItems.
                get(selection.getBand()).gainTemp));
        Label unitGainTemp = new Label("dB 1/K");
        cnt.addComponent(lGainTemp);
        cnt.addComponent(valueGainTemp);
        cnt.addComponent(unitGainTemp);

        Label lGainTempTerm = new Label("Sat G/T for Tx Term");
        final Label valueGainTempTerm = new Label(Com.shortText(
                selection.getuLpath().getSatellite().
                getGainTempForTerminal(txTerm)));
        Label unitGainTempTerm = new Label("dB 1/K");
        cnt.addComponent(lGainTempTerm);
        cnt.addComponent(valueGainTempTerm);
        cnt.addComponent(unitGainTempTerm);

        // C/No depends on Tx EIRP and the path loss
        Label lCNo = new Label("UL C/No");
        final Label valueCNo = new Label(Com.textN(
                selection.getuLpath().getCNo(), 7));

        Label unitCNo = new Label("dB Hz");
        cnt.addComponent(lCNo);
        cnt.addComponent(valueCNo);
        cnt.addComponent(unitCNo);

        // all actions at the end to update other fields
        sldrLatitude.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("UlPathView: selected latitude " + sldrLatitude.getText(), Log.DEBUG);
                try {
                    // Slider can only show positive values
                    selection.gettXterminal().
                            setLatitude((Double.parseDouble(sldrLatitude.getText())
                                    - (int) MathUtil.round(10
                                            * Com.VISIBLE_ANGLE * 180.0 / Com.PI))
                                    * Com.PI / (180.0 * 10.0));
                    // update all labels
                    valueLatitude.setText(Com.toDMS(selection.gettXterminal().
                            getLatitude()));

                    valueElevation.setText(Com.toDMS(selection.getuLpath().
                            getElevation()));

                    valueAzimuth.setText(Com.toDMS(selection.getuLpath().
                            getAzimuth()));
                    valueDistance.setText(Com.textN(selection.getuLpath().
                            getDistance() / 1E3, 8));

                    valuePathLoss.setText(Com.textN(selection.getuLpath().
                            getPathLoss(), 7));

                    valueCNo.setText(Com.textN(
                            selection.getuLpath().getCNo(), 7));
                    valueSpecDensity.setText(Com.textN(
                            selection.getuLpath().getSpectralDensity(), 7));
                    valuePowerRx.setText(Com.textN(
                            selection.getuLpath().getPowerReceived(), 7));
                    valueGainTempTerm.setText(Com.shortText(
                            selection.getuLpath().getSatellite().
                            getGainTempForTerminal(txTerm)));
                    updateMainForm(selection);

                } catch (java.lang.NumberFormatException e) {
                    Log.p("UlPathView: bad number for Latitude "
                            + sldrLatitude.getText(), Log.WARNING);
                }
            }
        });

        sldrLongitude.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("UlPathView: selected longitude " + sldrLongitude.getText(), Log.DEBUG);
                try {

                    // Slider can only show positive values, so correct with respect
                    // to satellite latitude (which was the center with +/- 80 degree
                    // first subtract 813 and then add satellite's position 
                    selection.gettXterminal().
                            setLongitude((Double.parseDouble(sldrLongitude.getText())
                                    - (int) MathUtil.round(10
                                            * Com.VISIBLE_ANGLE * 180.0
                                            / Com.PI)) * Com.PI / (180.0 * 10.0)
                                    + selection.getuLpath().getSatellite().getLongitude()
                            );
                    // update all labels
                    valueLongitude.setText(Com.toDMS(selection.gettXterminal().
                            getLongitude()));

                    valueElevation.setText(Com.toDMS(selection.getuLpath().
                            getElevation()));

                    valueAzimuth.setText(Com.toDMS(selection.getuLpath().
                            getAzimuth()));
                    valueDistance.setText(Com.textN(selection.getuLpath().
                            getDistance() / 1E3, 8));      // convert to km

                    valuePathLoss.setText(Com.textN(selection.getuLpath().
                            getPathLoss(), 7));            // already in dB

                    valueCNo.setText(Com.textN(
                            selection.getuLpath().getCNo(), 7));
                    valueSpecDensity.setText(Com.textN(
                            selection.getuLpath().getSpectralDensity(), 7));
                    valuePowerRx.setText(Com.textN(
                            selection.getuLpath().getPowerReceived(), 7));
                    valueGainTempTerm.setText(Com.shortText(
                            selection.getuLpath().getSatellite().
                            getGainTempForTerminal(txTerm)));
                    updateMainForm(selection);

                } catch (java.lang.NumberFormatException e) {
                    Log.p("UlPathView: bad number for diameter "
                            + sldrLongitude.getText(), Log.WARNING);

                }

            }
        });

        // have a multi-row table layout and dump the transmit terminal values
        return cnt;
    }

}
