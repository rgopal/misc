/*
 * OVERVIEW
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

    public Form createView(Selection selection) {

        Form path = new Form(this.getName());

        path.setScrollable(
                false);
        // override pointerPressed to locate new positions 

        return path;
    }}
