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
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.util.Resources;
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

public class UlPathView extends View {

    public Label label;
    public Label subLabel;

    static Path path;

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

        // create a Path object to hold specific satellite and terminal
        selection.setuLpath(new Path(selection.getSatellite(),
                selection.gettXterminal()));

        // terminal name and location
        lName.setText("Lng " + // selection.gettXterminal().getName() +
                Com.toDMS(selection.gettXterminal().getLongitude()) + " "
        );

        // don't use this, have to find through selection 
        selection.getuLpathView().label = lName;
        return lName;
    }

    // this is simple so return terminal's name and location
    public Component getSubWidget(final Selection selection) {
        Label lName = new Label(getName());

        // indexSatellite has all satellites, so get the band specific list
        if (selection.gettXterminal() == null) {
            Log.p("UlPathView: Can't find Tx terminal for band ", Log.DEBUG);
            return lName;
        }

        // terminal name and latitude
        lName.setText(// "Lat " +   // selection.gettXterminal().getName() +

                Com.toDMS(selection.gettXterminal().getLatitude()));

        // don't use this, have to find through selection 
        selection.getuLpathView().subLabel = lName;
        return lName;
    }

    // return satellite longitude and gain
    public Component getLabel(final Selection selection) {
        Label lNameGain = new Label(getValue());

        // indexSatellite has all satellites, so get the band specific list
        if (selection.getSatellite() == null) {
            Log.p("UlPathView: Can't find satellite ", Log.DEBUG);
            return lNameGain;
        }

        // terminal name and location
        lNameGain.setText( // selection.getSatellite().getName()
                Com.toDMS(selection.getSatellite().getLongitude()));

        // + Com.toDMS(selection.getSatellite().getGain()) + " x");
        return lNameGain;
    }

    public Form createView(final Selection selection) {

        Form path = new Form(this.getName());

        Form sub = new Form("Tx " + selection.gettXterminal().getName());

        Container cnt = new Container(new BorderLayout());
        sub.addComponent(cnt);

        // there are six items in Views.  Hardcoded table. Name, value, unit
        TableLayout layout = new TableLayout(13, 3);
        cnt.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setVerticalSpan(2);
        constraint.setWidthPercentage(25);

        // now go sequentially through the Uplink path fields
        final Terminal txTerm = selection.gettXterminal();

        Label L01 = new Label("C Freq");
        Label L02 = new Label(Com.shortText(txTerm.getAntenna().getFrequency() / 1E9));
        Label L03 = new Label("GHz " + txTerm.getBand());
        cnt.addComponent(L01);
        cnt.addComponent(L02);
        cnt.addComponent(constraint, L03);

        Label L61 = new Label("s " + selection.getuLpath().
                getSatellite());
        final Label L62 = new Label(Com.toDMS(selection.getuLpath().
                getSatellite().getLongitude()));
        Label L63 = new Label("deg");

        cnt.addComponent(L61);
        cnt.addComponent(L62);
        cnt.addComponent(L63);
        /*
         Label L71 = new Label("Lat.");
         final Label L72 = new Label(Com.toDMS(ter.getLatitude()));
         Label L73 = new Label("deg");

         cnt.addComponent(L71);
         cnt.addComponent(L72);
         cnt.addComponent(L73);
         */
        // terminal latitude within 80 degree from south (-) to 80 North (+)
        Label lLatitude = new Label("Lat.");

        final Slider sldrLatitude = new Slider();
        // can't take negative values, 81.3
        sldrLatitude.setMinValue((int) MathUtil.round(0.0)); // x10
        sldrLatitude.setMaxValue((int) MathUtil.round(1626.0));
        sldrLatitude.setEditable(true);
        // sldrLatitude.setPreferredW(8);
        sldrLatitude.setIncrements(5); //

        // display the current latitude in degrees
        sldrLatitude.setProgress((int) MathUtil.round(txTerm.getLatitude()
                * 180.0 * 10 / Com.PI) + 813);
        sldrLatitude.setRenderValueOnTop(true);

        final Label valueLatitude = new Label(Com.toDMS(txTerm.getLatitude()) + "");
        cnt.addComponent(lLatitude);
        cnt.addComponent(sldrLatitude);
        cnt.addComponent(valueLatitude);
        
           Label lPathLoss = new Label("PathLoss");
        final Label valuePathLoss = new Label(Com.shortText(selection.
                getuLpath().getPathLoss() ));
        Label unitPathLoss = new Label("dB");
        cnt.addComponent(lPathLoss);
        cnt.addComponent(valuePathLoss);
        cnt.addComponent(unitPathLoss);

        Label lDistance = new Label("Distance");
        final Label valueDistance = new Label(Com.text(selection.
                getuLpath().getDistance() / 1E3));
        Label unitDistance = new Label("km");
        cnt.addComponent(lDistance);
        cnt.addComponent(valueDistance);
        cnt.addComponent(unitDistance);

        Label lElevation = new Label("Elevation");
        final Label valueElevation = new Label(Com.toDMS(
                selection.getuLpath().getElevation()));
        Label unitElevation = new Label(" ");
        cnt.addComponent(lElevation);
        cnt.addComponent(valueElevation);
        cnt.addComponent(unitElevation);

        Label lAzimuth = new Label("Azimuth");
        final Label valueAzimuth = new Label(Com.toDMS(
                selection.getuLpath().getAzimuth()));
        Label unitAzimuth = new Label(" ");
        cnt.addComponent(lAzimuth);
        cnt.addComponent(valueAzimuth);
        cnt.addComponent(unitAzimuth);

        Label lLongitude = new Label("Long.");
        final Slider sldrLongitude = new Slider();

        // longitude should be around the satellite longitude (still -81.3 to +81.3
        sldrLongitude.setMinValue((int) MathUtil.round(0.0));
        sldrLongitude.setMaxValue((int) MathUtil.round(1626.0));
        sldrLongitude.setEditable(true);
        //L22.setPreferredW(8);
        sldrLongitude.setIncrements(5); //
        // center of slider is the position of the satellite.  First
        // become relative to satellite which is (terminal - satellite). 
        // then make the 0 the middle point (by adding 813).
        sldrLongitude.setProgress((int) (MathUtil.round(txTerm.getLongitude()
                * 180.0 * 10 / Com.PI) - selection.getuLpath().
                getSatellite().getLongitude() * 180.0 * 10.0 / Com.PI) + 813);

        sldrLongitude.setRenderValueOnTop(true);
        final Label valueLongitude = new Label(Com.toDMS(
                txTerm.getLongitude()) + "");
        cnt.addComponent(lLongitude);
        cnt.addComponent(sldrLongitude);
        cnt.addComponent(valueLongitude);

        // all actions at the end to update other fields
        sldrLatitude.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("UlPathView: selected latitude " + sldrLatitude.getText(), Log.DEBUG);
                try {
                    // Slider can only show positive values
                    selection.gettXterminal().
                            setLatitude((Double.parseDouble(sldrLatitude.getText()) - 813.0)
                                    * Com.PI / (180.0 * 10.0));
                    // update all labels
                    valueLatitude.setText(Com.toDMS(selection.gettXterminal().
                            getLatitude()));

                    valueElevation.setText(Com.toDMS(selection.getuLpath().
                            getElevation()));

                    valueAzimuth.setText(Com.toDMS(selection.getuLpath().
                            getAzimuth()));
                    valueDistance.setText(Com.text(selection.getuLpath().
                            getDistance() / 1E3));
                    
                    valuePathLoss.setText(Com.shortText(selection.getuLpath().
                            getPathLoss()));

                } catch (java.lang.NumberFormatException e) {
                    Log.p("UlPathView: bad number for Latitude "
                            + sldrLatitude.getText(), Log.DEBUG);
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
                                    - 813.0)* Com.PI / (180.0 * 10.0)
                                    + selection.getuLpath().getSatellite().getLongitude()
                            );
                    // update all labels
                      valueLongitude.setText(Com.toDMS(selection.gettXterminal().
                            getLongitude()));

                    valueElevation.setText(Com.toDMS(selection.getuLpath().
                            getElevation()));

                    valueAzimuth.setText(Com.toDMS(selection.getuLpath().
                            getAzimuth()));
                    valueDistance.setText(Com.text(selection.getuLpath().
                            getDistance() / 1E3));      // convert to km
                    
                    valuePathLoss.setText(Com.shortText(selection.getuLpath().
                            getPathLoss()));            // already in dB

                    //L32.setText(Com.shortText(ter.getAntenna().getGain()));
                    //L52.setText(Com.shortText(ter.getEIRP()));
                } catch (java.lang.NumberFormatException e) {
                    Log.p("TxView: bad number for diameter " + sldrLongitude.getText(), Log.DEBUG);

                }

            }
        });

        /*

         Label L31 = new Label(" Gain");
         final Label L32 = new Label(Com.shortText(ter.getAntenna().getGain()));
         Label L33 = new Label("dBi");
         cnt.addComponent(L31);
         cnt.addComponent(L32);
         cnt.addComponent(L33);

         Label L41 = new Label(" 3dB Angle");
         final Label L42 = new Label(Com.toDMS(ter.getAntenna().getThreeDBangle()));
         Label L43 = new Label("deg");
         cnt.addComponent(L41);
         cnt.addComponent(L42);
         cnt.addComponent(L43);

         Label L4A1 = new Label(" Point Loss");
         final Label L4A2 = new Label(Com.shortText(ter.getAntenna().getDepointingLoss()));
         Label L4A3 = new Label("dB");
         cnt.addComponent(L4A1);
         cnt.addComponent(L4A2);
         cnt.addComponent(L4A3);

         constraint = layout.createConstraint();
         constraint.setHorizontalSpan(3);        // whole row

         Label L0A1 = new Label("Terminal");
         L0A1.setAlignment(Component.CENTER);
         cnt.addComponent(constraint, L0A1);

         Label L51 = new Label("Term EIRP");
         final Label L52 = new Label(Com.shortText(ter.getEIRP()));
         Label L53 = new Label("dBW");
         cnt.addComponent(L51);
         cnt.addComponent(L52);
         cnt.addComponent(L53);

         sub.setScrollable(true);

    

       

         */
        // have a multi-row table layout and dump the transmit terminal values
        return sub;
    }
}
