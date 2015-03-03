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
import static com.erudyo.satellite.TxView.addNewInner;
import java.util.ArrayList;

public class RxView extends View {

    private Terminal terminal;
    public ComboBox spin;

    public RxView() {

    }

    // create the text part (list of all attribute values) fresh when called.
    public ArrayList<ArrayList<String>> getText(Selection selection) {
        ArrayList<ArrayList<String>> outer = new ArrayList<ArrayList<String>>();

        // add only two items
        outer.add(TxView.addNewInner("Receive Terminal",
                selection.getrXterminal().getName(), ""));

        outer.add(addNewInner("DownLink Band",
                selection.getrXterminal().getrXantenna().getBand().toString(), ""));

        outer.add(TxView.addNewInner("Center Frequency", Com.shortText(
                selection.getrXterminal().
                getrXantenna().getFrequency() / 1E9), "GHz"));

         outer.add(TxView.addNewInner("Latitude",
                Com.toDMS(selection.getrXterminal().getLatitude()), "degree"));

         outer.add(TxView.addNewInner("Longitude",
                Com.toDMS(selection.getrXterminal().getLongitude()), "degree"));

     
        outer.add(TxView.addNewInner("LNA Noise Figure", Com.shortText(
                selection.getrXterminal().getrXamplifier().getNoiseFigure()), "dB"));

        outer.add(TxView.addNewInner("Antenna Efficiency",
                Com.shortText(selection.getrXterminal().
                        getrXantenna().getEfficiency()), " "));

        outer.add(TxView.addNewInner("Antenna Diameter",
                Com.shortText(selection.getrXterminal().
                        getrXantenna().getEfficiency()), "m"));

        outer.add(TxView.addNewInner("Antenna Gain",
                Com.shortText(selection.getrXterminal().
                        getrXantenna().getGain()), "dBi"));

        outer.add(TxView.addNewInner("Antenna 3dB Angle",
                Com.toDMS(selection.getrXterminal().
                        getrXantenna().getThreeDBangle()), "degree"));

        outer.add(TxView.addNewInner("Antenna Point Loss",
                Com.shortText(selection.getrXterminal().
                        getrXantenna().getDepointingLoss()), "dB"));

        outer.add(TxView.addNewInner("Implementation Loss",
                Com.shortText(selection.getrXterminal().getrXamplifier()
                        .getLFRX()), "dB"));

        outer.add(TxView.addNewInner("System Noise Temp",
                Com.textN(
                        selection.getrXterminal().calcSystemNoiseTemp(), 6), "K"));

        outer.add(TxView.addNewInner("Receive Gain/Temp",
                Com.shortText(selection.getrXterminal().getGainTemp()), "dB/K"));

        return outer;
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

                selection.getrXterminal().setBand(
                        selection.getBand());

                selection.getrXterminal().getrXantenna().
                        setFrequency(RfBand.centerFrequency(
                                        RfBand.findDl(selection.getBand())));

                updateMainForm(selection);
                Log.p("RxView: Terminal selection "
                        + combo.getSelectedItem(), Log.DEBUG);

                // System.out.println(combo.getSelectedItem());
            }
        });

        updateMainForm(selection);
        // combo box created so return it
        return combo;
    }

    // update from the current selection of the terminal
    public void updateMainForm(Selection selection) {

        // may not exist at initialization time
        if (selection.getrXterminal() != null) {
            selection.getRxView().setShortName("Rx");
            selection.getRxView().setName("Rx Terminal");

            selection.getRxView().setSummary("GT " + Com.textN(
                    selection.getrXterminal().getGainTemp(), 5) + "dB/K");

            selection.getRxView().setValue("Dia " + Com.textN(selection.getrXterminal().
                    getrXantenna().getDiameter(), 5) + "m");

            selection.getRxView().setSubValue("NF" + Com.textN(
                    selection.getrXterminal().getrXamplifier().getNoiseFigure(), 5) + "dB");
            // update other view summaries in Link TODO
        }
        selection.getdLpathView().updateMainForm(selection);
        selection.getCommsView().updateValues(selection);

    }

    public String getDisplayName() {
        return name;
    }

    public RxView(Selection selection) {
        this.terminal = selection.getrXterminal();

    }

    public Form createView(final Selection selection) {
        Form cnt = new Form("Rx " + selection.getrXterminal().getName());

        // there are six items in Views.  Hardcoded table. Name, value, unit
        TableLayout layout = new TableLayout(15, 3);
        cnt.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setHorizontalSpan(3);
        constraint.setWidthPercentage(40);

        // now go sequentially through the Tx terminal fields
        final Terminal rxTerm = selection.getrXterminal();

        Label L01 = new Label("Center Frequency");
        Label lFrequency = new Label(Com.shortText(rxTerm.
                getrXantenna().getFrequency() / 1E9));
        Label L03 = new Label("GHz " + rxTerm.getrXantenna().getBand());
        cnt.addComponent(constraint, L01);
        cnt.addComponent(lFrequency);
        cnt.addComponent(L03);

        Label L71 = new Label("Terminal Latitude");
        final Label lLatitude = new Label(Com.toDMS(rxTerm.getLatitude()));
        Label L73 = new Label("degree");

        cnt.addComponent(L71);
        cnt.addComponent(lLatitude);
        cnt.addComponent(L73);
        
        Label L61 = new Label("Terminal Longitude");
        final Label lLongitude = new Label(Com.toDMS(rxTerm.getLongitude()));
        Label L63 = new Label("degree");

        cnt.addComponent(L61);
        cnt.addComponent(lLongitude);
        cnt.addComponent(L63);


        Label lNoiseFigureLabel = new Label("LNA Noise Figure");
        Label lNoiseFigureUnit = new Label("dB");
        final Slider sldrNoiseFigure = new Slider();
        Com.formatSlider(sldrNoiseFigure);
        sldrNoiseFigure.setMinValue((int) MathUtil.round(Amplifier.NOISE_FIG_LO * 10)); // x10
        sldrNoiseFigure.setMaxValue((int) MathUtil.round(Amplifier.NOISE_FIG_HI * 10));
        sldrNoiseFigure.setEditable(true);
        // sldrNoiseFigure.setPreferredW(8);
        sldrNoiseFigure.setIncrements(5); //
        sldrNoiseFigure.setProgress((int) MathUtil.round(rxTerm.getrXamplifier().getNoiseFigure() * 10));
        sldrNoiseFigure.setRenderValueOnTop(true);

        final Label lNoiseFigure = new Label(Com.shortText(rxTerm.getrXamplifier().
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
        Com.formatSlider(sldrDiameter);

        sldrDiameter.setMinValue((int) MathUtil.round(Antenna.DIAMETER_LO * 10)); // x10
        sldrDiameter.setMaxValue((int) MathUtil.round(Antenna.DIAMETER_HI * 10));
        sldrDiameter.setEditable(true);
        //L22.setPreferredW(8);
        sldrDiameter.setIncrements(5); //
        sldrDiameter.setProgress((int) 
                MathUtil.round(rxTerm.getrXantenna().getDiameter() * 10));

        sldrDiameter.setRenderValueOnTop(true);
        final Label lDiameterValue = 
                new Label(Com.shortText(rxTerm.getrXantenna().getDiameter()));
        cnt.addComponent(lDiameterLabel);
        cnt.addComponent(lDiameterValue);
        cnt.addComponent(lDiameterUnit);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cnt.addComponent(constraint, sldrDiameter);

        Label L31 = new Label("Antenna Gain");
        final Label lGain = new Label(Com.shortText(rxTerm.getrXantenna().getGain()));
        Label L33 = new Label("dBi");
        cnt.addComponent(L31);
        cnt.addComponent(lGain);
        cnt.addComponent(L33);

        Label L41 = new Label("    3dB Angle");
        final Label lThreeDBangle = 
                new Label(Com.toDMS(rxTerm.getrXantenna().getThreeDBangle()));
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
                rxTerm.getrXamplifier().getLFRX()));
        Label unitImpLoss = new Label("dB");
        cnt.addComponent(lImpLoss);
        cnt.addComponent(valueImpLoss);
        cnt.addComponent(unitImpLoss);

        // does not change so not in combo/sliders
        Label lsysTemp = new Label("System Noise Temperature");
        final Label valuesysTemp = new Label(Com.textN(
                rxTerm.calcSystemNoiseTemp(), 6));
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

        cnt.setScrollable(true);
        // all actions at the end to update other fields
        sldrNoiseFigure.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("RxView: selected noise figure " + 
                        sldrNoiseFigure.getText(), Log.DEBUG);
                try {
                    selection.getrXterminal().getrXamplifier().
                            setNoiseFigure(Double.parseDouble(
                                    sldrNoiseFigure.getText()) / 10.0);
                    // update EIRP
                    lNoiseFigure.setText(Com.shortText(rxTerm.getrXamplifier().
                            getNoiseFigure()) + "dB");
                    valuesysTemp.setText(Com.textN(
                            rxTerm.calcSystemNoiseTemp(), 6));
                    valueGainTemp.setText(Com.shortText(rxTerm.getGainTemp()));
                    // does not change depointing
                    updateMainForm(selection);

                } catch (java.lang.NumberFormatException e) {
                    Log.p("RxView: bad number for Noise Figure "
                            + sldrNoiseFigure.getText(), Log.DEBUG);
                }
            }
        });

        sldrDiameter.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("RxView: selected diameter " + 
                        sldrDiameter.getText(), Log.DEBUG);
                try {

                    // convert from cm to m first
                    selection.getrXterminal().getrXantenna().
                            setDiameter(
                                    Double.parseDouble(sldrDiameter.getText()) / 10.0);
                    // update EIRP and three DB
                    lDiameterValue.setText(Com.shortText(rxTerm.getrXantenna().getDiameter()));
                    lThreeDBangle.setText(Com.toDMS(rxTerm.getrXantenna().getThreeDBangle()));
                    lGain.setText(Com.shortText(rxTerm.getrXantenna().getGain()));
                    valuePointLoss.setText(Com.shortText(
                            rxTerm.getrXantenna().getDepointingLoss()));
                    valueGainTemp.setText(Com.shortText(rxTerm.getGainTemp()));
                    // should not change
                    valuesysTemp.setText(Com.textN(
                            rxTerm.calcSystemNoiseTemp(), 6));
                    updateMainForm(selection);
                } catch (java.lang.NumberFormatException e) {
                    Log.p("TxView: bad number for diameter " + 
                            sldrDiameter.getText(), Log.DEBUG);

                }

            }
        });

        // have a multi-row table layout and dump the transmit terminal values
        return cnt;
    }

}
