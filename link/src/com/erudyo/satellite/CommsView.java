/*
 * 
 */
package com.erudyo.satellite;

import com.codename1.io.Log;
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
import com.codename1.ui.RadioButton;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.layouts.BoxLayout;
import java.util.ArrayList;
import java.util.Hashtable;

public class CommsView extends View {

    public Label label;
    public Slider slider;

    private Hashtable<String, Integer> modulationHash
            = new Hashtable<String, Integer>();

    // lookup by index with instance name vector 
    private ArrayList<String> modulations
            = new ArrayList<String>();

    private Hashtable<String, Integer> codeHash
            = new Hashtable<String, Integer>();

    // lookup by index with instance name vector 
    private ArrayList<String> codes
            = new ArrayList<String>();

    private Hashtable<String, Integer> codeRateHash
            = new Hashtable<String, Integer>();

    // lookup by index with instance name vector 
    private ArrayList<String> codeRates
            = new ArrayList<String>();

    public void initModulationHash() {

        int index = 0;
        // go through the hash to create positions and indexRfBand entries
        for (Comms.Modulation key : Comms.indexModulation) {
            // add the position and increment for next item
            modulationHash.put(key.toString(), index++);

            // create a simple array with object name (key)
            modulations.add(key.toString());
        }

    }

    // this could be done in Static
    public void initCodeHash() {

        int index = 0;
        // go through the hash to create positions and indexRfBand entries
        for (Comms.Code key : Comms.getIndexCode()) {
            // add the position and increment for next item
            codeHash.put(key.toString(), index++);

            // create a simple array with object name (key)
            codes.add(key.toString());
        }

    }

    public void initCodeRateHash() {

        int index = 0;
        // go through the hash to create positions and indexRfBand entries
        for (Comms.CodeRate key : Comms.indexCodeRate) {
            // add the position and increment for next item
            codeRateHash.put(key.toString(), index++);

            // create a simple array with object name (key)
            codeRates.add(key.toString());
        }

    }

    public Hashtable<String, Integer> getModulationHash() {
        return this.modulationHash;
    }

    public Hashtable<String, Integer> getCodeHash() {
        return this.codeHash;
    }

    public ArrayList<String> getModulations() {
        return this.modulations;
    }

    public ArrayList<String> getCodes() {
        return this.codes;
    }

    public ArrayList<String> getCodeRates() {
        return this.codeRates;
    }

    public CommsView(Selection selection) {
        super.name = "Comms";
        init();
    }

    public void init() {
        initModulationHash();
        initCodeHash();
        initCodeRateHash();

    }

    public CommsView() {
        // this could initialize the needed data structures
        init();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // returns a slider to show and select specific data rate (in Comms)
    // with its current value displayed in label member (returned by getLabel)
    // Creates an instance of Comms to store data rate (then used by CreateView
    // for other fields such as modulation, coding, etc.
    public Component getWidget(final Selection selection) {

        // basic selection is data rate (will be returned later)
        label = new Label();

        // create a Comms object to hold specifics
        selection.setComms(new Comms());

        final Slider sldrDataRate = new Slider();
        sldrDataRate.setMinValue((int) MathUtil.round(Comms.DATA_RATE_LO * 10)); // x10
        sldrDataRate.setMaxValue((int) MathUtil.round(Comms.DATA_RATE_HI * 10));
        sldrDataRate.setEditable(true);

        sldrDataRate.setIncrements(5); //

        sldrDataRate.setText(String.valueOf(MathUtil.round(selection.getComms().getDataRate() * 10)));
        sldrDataRate.setProgress((int) MathUtil.round(selection.getComms().getDataRate() * 10));
        label.setText(String.valueOf(Double.parseDouble(sldrDataRate.getText()) / 10.0)
                + "Mbps");
        sldrDataRate.setRenderValueOnTop(true);

        selection.getCommsView().slider = sldrDataRate;

        // all actions at the end to update other fields
        sldrDataRate.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                // System.out.println(sldrDataRate.getText());
                try {
                    selection.getComms().
                            setDataRate(Double.parseDouble(sldrDataRate.getText()) / 10.0);
                    // update EIRP
                    label.setText(String.valueOf(Double.parseDouble(sldrDataRate.getText()) / 10.0)
                            + "Mbps");

                } catch (java.lang.NumberFormatException e) {
                    System.out.println("TxView: bad number " + sldrDataRate.getText());
                }
            }
        });

        // combo box created so return it
        return sldrDataRate;
    }

    public Component getLabel(final Selection selection) {
        try {

            label.setText(
                    Com.shortText(Double.parseDouble(
                                    selection.getCommsView().slider.getText()) / 10) + "Mbps");
        } catch (java.lang.NumberFormatException e) {
            System.out.println("TxView: bad number " + label.getText());
        }

        // set the tx view present in the selection
        selection.getCommsView().label = label;

        // combo box created so return it
        return label;
    }

    public String getDisplayName() {
        return name;
    }

    // do bandwidth, rollOff, modulation, coding
    public Form createView(final Selection selection) {
        Form sub = new Form("Common " + selection.getComms().getName());

        Container cntAllThree = new Container(new BorderLayout());
        sub.addComponent(cntAllThree);

        // Hardcoded table. Name, value, unit
        TableLayout layout = new TableLayout(2, 3);
        cntAllThree.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setVerticalSpan(2);
        constraint.setWidthPercentage(20);

        // now go sequentially through the Tx terminal fields
        // final Terminal ter = selection.gettXterminal();
        Label lDataRate01 = new Label("Data Rate");
        Label lDataRate02 = new Label(Com.shortText(selection.getComms().getDataRate()));
        Label lDataRate03 = new Label("Mbps ");
        cntAllThree.addComponent(lDataRate01);
        cntAllThree.addComponent(constraint, lDataRate02);
        cntAllThree.addComponent(lDataRate03);

        Label lDataRate = new Label("Data Rate");
        final Slider sldrDataRate = new Slider();

        sldrDataRate.setMinValue((int) MathUtil.round(Comms.DATA_RATE_LO * 10)); // x10
        sldrDataRate.setMaxValue((int) MathUtil.round(Comms.DATA_RATE_HI * 10));
        sldrDataRate.setEditable(true);

        sldrDataRate.setIncrements(5); //
        sldrDataRate.setProgress((int) MathUtil.round(selection.getComms().getDataRate() * 10));

        sldrDataRate.setRenderValueOnTop(true);

        final Label lDataRateValue = new Label(Com.shortText(selection.getComms().getDataRate()) + "Mbps");
        cntAllThree.addComponent(lDataRate);
        cntAllThree.addComponent(sldrDataRate);
        cntAllThree.addComponent(lDataRateValue);

        Label lBW = new Label("BW");
        final Slider sldrBW = new Slider();

        sldrBW.setMinValue((int) MathUtil.round(Comms.BW_LO * 10)); // x10
        sldrBW.setMaxValue((int) MathUtil.round(Comms.BW_HI * 10));
        sldrBW.setEditable(true);

        sldrBW.setIncrements(5); //
        sldrBW.setProgress((int) MathUtil.round(selection.getComms().getBW() * 10));

        sldrBW.setRenderValueOnTop(true);

        final Label lBWvalue = new Label(Com.shortText(selection.getComms().getBW()) + "MHz");
        cntAllThree.addComponent(lBW);
        cntAllThree.addComponent(sldrBW);
        cntAllThree.addComponent(lBWvalue);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);

        // now vertical positioning
        cntAllThree = new Container(new BorderLayout());
        sub.addComponent(cntAllThree);

        // Hardcoded table. Name, value, unit
        layout = new TableLayout(2, 3);
        cntAllThree.setLayout(layout);

        Label l1 = new Label("Modulation");
        Label l2 = new Label("Rate");
        Label l3 = new Label("Code");
        cntAllThree.addComponent(l1);
        cntAllThree.addComponent(l2);
        cntAllThree.addComponent(l3);

        Container cntMod = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        cntAllThree.addComponent(cntMod);

        final ButtonGroup bgMod = new ButtonGroup();
        // RadioButton bCode = new RadioButton(); // [Comms.modulationHash.size()];

        /**
         * this method should be used to initialize variables instead of the
         * constructor/class scope to avoid race conditions
         */
        for (int i = 0; i < Comms.modulationHash.size(); i++) {
            final RadioButton bModulation = new RadioButton();

            // trying to set hint.  Name also does not work
            bModulation.setUIID(Comms.indexModulation.toArray(new Comms.Modulation[0])[i].name());
            bModulation.setText(Comms.indexModulation.toArray(new Comms.Modulation[0])[i].name());
        //(selection.getCommsView().getModulations().get(i));

            //add to button group
            bgMod.add(bModulation);
            bModulation.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {

                    Log.p("CommsView: selected Modulatoin " + 
                            bgMod.getSelectedIndex(), Log.DEBUG);
                    selection.getComms().setModulation(
                            Comms.indexModulation.toArray(new 
                                    Comms.Modulation[0])
                                    [bgMod.getSelectedIndex()]);

                }
            });
            cntMod.addComponent(bModulation);
        }
        // set default to the first one
        bgMod.setSelected((int) modulationHash.get(
                Comms.Modulation.BPSK.toString()));

        // now bCode rate
        Container cntRate = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        cntAllThree.addComponent(cntRate);

        final ButtonGroup bgRate = new ButtonGroup();

        RadioButton bCodeRate = new RadioButton();
        final ButtonGroup bgCode = new ButtonGroup();

        /**
         * this method should be used to initialize variables instead of the
         * constructor/class scope to avoid race conditions
         */
        for (int i = 0; i < Comms.codeRateHash.size(); i++) {
            bCodeRate = new RadioButton();
            bCodeRate.setName(selection.getCommsView().getCodeRates().get(i));
            bCodeRate.setText(selection.getCommsView().getCodeRates().get(i));
            //add to button group
            bgRate.add(bCodeRate);
            bCodeRate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {

                    Log.p("CommsView: selected Code Rate " + 
                            bgRate.getSelectedIndex(), Log.DEBUG);
                    selection.getComms().setCodeRate(
                            Comms.indexCodeRate.toArray(new 
                                    Comms.CodeRate[0])
                                    [bgRate.getSelectedIndex()]);
                    if (selection.getComms().getCodeRate()
                            == Comms.CodeRate.FEC_1_1) {
                        selection.getComms().setCode(Comms.Code.NONE);
                        // set the right bCode rate in button group
                        bgCode.setSelected((int) codeHash.get(
                                Comms.Code.NONE.toString()));
                    }

                }
            });
            //add to container
            cntRate.addComponent(bCodeRate);
        }
          // set default to the first one
        bgRate.setSelected((int) codeRateHash.get(
                Comms.CodeRate.FEC_7_8.toString()));
        
        // now the bCode
        Container cntCode = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        cntAllThree.addComponent(cntCode);

        // RadioButton bCode = new RadioButton(); // [Comms.modulationHash.size()];
        /**
         * this method should be used to initialize variables instead of the
         * constructor/class scope to avoid race conditions
         */
        for (int i = 0; i < Comms.codeHash.size(); i++) {
            final RadioButton bCode = new RadioButton();
            bCode.setName(selection.getCommsView().getCodes().get(i));
            bCode.setText(selection.getCommsView().getCodes().get(i));

            //add to button group
            bgCode.add(bCode);
            bCode.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                   Log.p("CommsView: selected Code " + 
                           bgCode.getSelectedIndex(), Log.DEBUG);
                    selection.getComms().setCode(
                            Comms.indexCode.toArray(new Comms.Code[0])[bgCode.getSelectedIndex()]);
                    // if no bCode then rate is 1/1
                    if (selection.getComms().getCode()
                            == Comms.Code.NONE) {
                        selection.getComms().setCodeRate(Comms.CodeRate.FEC_1_1);
                        // set the right bCode rate in button group
                        bgRate.setSelected((int) codeRateHash.get(
                                Comms.CodeRate.FEC_1_1.toString()));

                    }
                }

            });
            cntCode.addComponent(bCode);
        }

          // set default to 
        bgCode.setSelected((int) codeHash.get(
                Comms.Code.BCH.toString()));
        
        sldrDataRate.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("CommsView: selected Data Rate " + 
                        sldrDataRate.getText(), Log.DEBUG);
                try {

                    selection.getComms().setDataRate(Double.parseDouble(sldrDataRate.getText()) / 10.0);
                    lDataRateValue.setText(Com.shortText(selection.getComms().getDataRate()) + "Mbps");

                } catch (java.lang.NumberFormatException e) {
                    Log.p("TxView: bad number for Data Rate " + sldrDataRate.getText());

                }

            }
        });

        sldrBW.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("CommsView: selected BW " + sldrBW.getText(), Log.DEBUG);
                try {

                    selection.getComms().setBW(Double.parseDouble(sldrBW.getText()) / 10.0);
                    lBWvalue.setText(Com.shortText(selection.getComms().getBW()) + "MHz");
                    

                } catch (java.lang.NumberFormatException e) {
                    Log.p("TxView: bad number for BW " + sldrBW.getText(), Log.DEBUG);

                }

            }
        });

        // have a multi-row table layout and dump the transmit terminal values
        return sub;
    }
}
