/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import com.codename1.io.Log;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
import com.codename1.ui.Button;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Slider;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.Resources;
import com.codename1.util.MathUtil;
import java.util.ArrayList;

public class TxView extends View {
    
    public ComboBox spin;
    
    public TxView() {
        
    }
    
    public TxView(Selection selection) {
        // don't call since it is being built
    }

    // create the text part fresh when called
    public ArrayList<ArrayList<String>> getText(Selection selection) {
        ArrayList<ArrayList<String>> outer = new ArrayList<ArrayList<String>>();
        
        ArrayList<String> inner = new ArrayList<String>();
        // first row
        inner.add("Transmit Terminal");
        inner.add(selection.gettXterminal().getName());
        inner.add(" ");
        outer.add(inner);

        // get a new row
        inner = new ArrayList<String>();
        inner.add("Longitude");
        inner.add(Com.toDMS(selection.gettXterminal().getLongitude()));
        inner.add("degree");
        outer.add(inner);

        // get a new row
        inner = new ArrayList<String>();
        inner.add("Latitude");
        inner.add(Com.toDMS(selection.gettXterminal().getLatitude()));
        inner.add("degree");
        
        outer.add(inner);
        
        return outer;
    }

    // update from the current selection of the terminal
    public void updateValues(Selection selection) {

        // may not exist at initialization time
        if (selection.gettXterminal() != null) {
            selection.getTxView().setShortName("Tx");
            selection.getTxView().setName("Tx Terminal");
            
            selection.getTxView().setSummary(Com.textN(
                    selection.gettXterminal().getEIRP(), 5) + "dBW");
            
            selection.getTxView().setValue(Com.textN(selection.gettXterminal().
                    gettXantenna().getDiameter(), 5) + "m");
            
            selection.getTxView().setSubValue(Com.textN(
                    selection.gettXterminal().gettXamplifier().getPower(), 5) + "W");
            // update other view summaries in Link TODO Comms
            selection.getuLpathView().updateValues(selection);
            
            selection.getCommsView().updateValues(selection);
        }
        
    }
    
    public Component getWidget(final Selection selection) {
        Label l = new Label(getName());
        
        final ComboBox combo = new ComboBox();

        // when satellite is set, this is already called
        // selection.initVisibleTerminal();
        // indexSatellite has all satellites, so get the band specific list
        if (selection.getVisibleTerminal().get(Selection.VISIBLE.YES) == null) {
            Log.p("TxView: Can't find terminals for satellite "
                    + selection.getSatellite(), Log.DEBUG);
            return l;
        }

        // create mtodel for all terminals of selected band
        ListModel model = new DefaultListModel(
                selection.getVisibleTerminal().get(Selection.VISIBLE.YES));
        
        combo.setModel(model);

        // update selected receive terminal
        if (selection.getVisibleTerminal().get(Selection.VISIBLE.YES).size() < 1) // get the first terminal (only 1)
        {
            Log.p("TxView: no terminals visible for satellite "
                    + selection.getSatellite(), Log.DEBUG);
        }

        // select the nearest terminal to the satellite
        selection.settXterminal(Terminal.terminalHash.
                get(selection.getVisibleTerminal().
                        get(Selection.VISIBLE.YES).toArray(
                                new String[0])[0]));

        // set the selected Tx on combo box also 
        combo.setSelectedItem(selection.gettXterminal().getName());

        // Band combobox should be able to change this
        selection.getTxView().spin = combo;
        
        updateValues(selection);
        
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                // set the selected Tx terminal
                selection.settXterminal(Terminal.terminalHash.get(
                        (String) combo.getSelectedItem()));
                
                selection.gettXterminal().setBand(
                        (selection.getBand()));
                
                updateValues(selection);
                
                Log.p("TxView: Terminal selection "
                        + combo.getSelectedItem(), Log.DEBUG);
            }
        });

        // combo box created so return it
        return combo;
    }
    
    public String getDisplayName() {
        return name;
    }
    
    public Form createView(final Selection selection) {
        Form cnt = new Form("Tx " + selection.gettXterminal().getName());
        
       // Container cnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        //Don't use a Container, a lot of right space is left sub.addComponent(cnt);
        
        TableLayout layout = new TableLayout(13, 3);
        cnt.setLayout(layout);
        
        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setHorizontalSpan(3);
        constraint.setWidthPercentage(40);

        // now go sequentially through the Tx terminal fields
        final Terminal tXterminal = selection.gettXterminal();
        
        Label L01 = new Label("Center Frequency");
        Label L02 = new Label(Com.shortText(tXterminal.gettXantenna().getFrequency() / 1E9));
        Label L03 = new Label("GHz " + tXterminal.gettXantenna().getBand());
        cnt.addComponent(constraint, L01);
        constraint = layout.createConstraint();
        constraint.setWidthPercentage(30);
        cnt.addComponent(constraint, L02);
        cnt.addComponent(L03);
        
        Label L61 = new Label("Terminal Longitude");
        final Label L62 = new Label(Com.toDMS(tXterminal.getLongitude()));
        Label L63 = new Label("degree");
        
        cnt.addComponent(L61);
        cnt.addComponent(L62);
        cnt.addComponent(L63);
        
        Label L71 = new Label("Terminal Latitude");
        final Label L72 = new Label(Com.toDMS(tXterminal.getLatitude()));
        Label L73 = new Label("deg");
        
        cnt.addComponent(L71);
        cnt.addComponent(L72);
        cnt.addComponent(L73);
        
        Label L11 = new Label("  Ampl Power");
        Label lPowerUnit = new Label("W");
        
        final Slider sldrPower = new Slider();
        Com.formatSlider(sldrPower);
        
        sldrPower.setMinValue((int) MathUtil.round(Amplifier.POWER_LO * 10)); // x10
        sldrPower.setMaxValue((int) MathUtil.round(Amplifier.POWER_HI * 10));
        sldrPower.setEditable(true);
        
        sldrPower.setIncrements(5); //
        sldrPower.setProgress((int) MathUtil.round(tXterminal.gettXamplifier().getPower() * 10));
        sldrPower.setRenderValueOnTop(true);
        
        final Label L13 = new Label(Com.textN(tXterminal.gettXamplifier().
                getPower(),6));
        cnt.addComponent(L11);
        cnt.addComponent(L13);
        cnt.addComponent(lPowerUnit);
        
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrPower);
        
        Label L2A1 = new Label("Antenna Efficiency");
        Label L2A2 = new Label(Com.shortText(tXterminal.gettXantenna().getEfficiency()));
        Label L2A3 = new Label(" ");
        cnt.addComponent(L2A1);
        cnt.addComponent(L2A2);
        cnt.addComponent(L2A3);
        
        Label L21 = new Label("    Diameter");
        Label lDiameterUnit = new Label("m");
        final Slider sldrDiameter = new Slider();
        Com.formatSlider(sldrDiameter);
        
        sldrDiameter.setMinValue((int) MathUtil.round(Antenna.DIAMETER_LO * 10)); // x10
        sldrDiameter.setMaxValue((int) MathUtil.round(Antenna.DIAMETER_HI * 10));
        sldrDiameter.setEditable(true);
        //L22.setPreferredW(8);
        sldrDiameter.setIncrements(5); //
        sldrDiameter.setProgress((int) MathUtil.round(tXterminal.gettXantenna().getDiameter() * 10));
        
        sldrDiameter.setRenderValueOnTop(true);
        final Label lDiameterValue = new Label(Com.shortText(tXterminal.gettXantenna().getDiameter()));
        cnt.addComponent(L21);
        cnt.addComponent(lDiameterValue);
        cnt.addComponent(lDiameterUnit);
        
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrDiameter);
        
        Label L31 = new Label("Antenna Gain");
        final Label L32 = new Label(Com.shortText(tXterminal.gettXantenna().getGain()));
        Label L33 = new Label("dBi");
        cnt.addComponent(L31);
        cnt.addComponent(L32);
        cnt.addComponent(L33);
        
        Label L41 = new Label("    3dB Angle");
        final Label L42 = new Label(Com.toDMS(tXterminal.gettXantenna().getThreeDBangle()));
        Label L43 = new Label("deg");
        cnt.addComponent(L41);
        cnt.addComponent(L42);
        cnt.addComponent(L43);

        // does change so not in combo/sliders
        Label lPointLoss = new Label("    Point Loss");
        final Label valuePointLoss = new Label(Com.shortText(
                tXterminal.gettXantenna().getDepointingLoss()));
        Label unitPointLoss = new Label("dB");
        cnt.addComponent(lPointLoss);
        cnt.addComponent(valuePointLoss);
        cnt.addComponent(unitPointLoss);

        // does not change so not in combo/sliders
        Label lImpLoss = new Label("    Imp Loss");
        final Label valueImpLoss = new Label(Com.shortText(
                tXterminal.gettXamplifier().getLFTX()));
        Label unitImpLoss = new Label("dB");
        cnt.addComponent(lImpLoss);
        cnt.addComponent(valueImpLoss);
        cnt.addComponent(unitImpLoss);
        
        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);        // whole row

        Label lEIRP = new Label("Tx Terminal EIRP");
        final Label valueEIRP = new Label(Com.textN(tXterminal.getEIRP(),6));
        Label unitEIRP = new Label("dBW");
        cnt.addComponent(lEIRP);
        cnt.addComponent(valueEIRP);
        cnt.addComponent(unitEIRP);
        
        cnt.setScrollable(true);

        // all actions at the end to update other fields
        sldrPower.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("TxView: selected power " + sldrPower.getText(), Log.DEBUG);
                try {
                    selection.gettXterminal().gettXamplifier().
                            setPower(Double.parseDouble(sldrPower.getText()) / 10.0);
                    // update EIRP
                    L13.setText(Com.textN(tXterminal.gettXamplifier().
                            getPower(),6));
                    valueEIRP.setText(Com.textN(tXterminal.getEIRP(),6));
                    // does not change depointing
                    updateValues(selection);
                    
                } catch (java.lang.NumberFormatException e) {
                    Log.p("TxView: bad number for power " + sldrPower.getText(), Log.DEBUG);
                }
            }
        });
        
        sldrDiameter.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("TxView: selected diameter " + sldrDiameter.getText(), Log.DEBUG);
                try {

                    // convert from cm to m first
                    selection.gettXterminal().gettXantenna().
                            setDiameter(Double.parseDouble(sldrDiameter.getText()) / 10.0);
                    // update EIRP and three DB
                    lDiameterValue.setText(Com.shortText(tXterminal.gettXantenna().getDiameter()));
                    L42.setText(Com.toDMS(tXterminal.gettXantenna().getThreeDBangle()));
                    L32.setText(Com.shortText(tXterminal.gettXantenna().getGain()));
                    valuePointLoss.setText(Com.shortText(
                            tXterminal.gettXantenna().getDepointingLoss()));
                    valueEIRP.setText(Com.shortText(tXterminal.getEIRP()));
                    updateValues(selection);
                } catch (java.lang.NumberFormatException e) {
                    Log.p("TxView: bad number for diameter " + sldrDiameter.getText(), Log.DEBUG);
                    
                }
                
            }
        });

        // have a multi-row table layout and dump the transmit terminal values
        return cnt;
    }
}
