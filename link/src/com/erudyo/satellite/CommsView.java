/*
 * STATUS: Primary items for COMMS.   FEC, Modulation, Data Rate, BW
 */
package com.erudyo.satellite;

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

        final Slider L12 = new Slider();
        L12.setMinValue((int) MathUtil.round(Comms.DATA_RATE_LO * 10)); // x10
        L12.setMaxValue((int) MathUtil.round(Comms.DATA_RATE_HI * 10));
        L12.setEditable(true);

        L12.setIncrements(5); //

        L12.setText(String.valueOf(MathUtil.round(selection.getComms().getDataRate() * 10)));
        L12.setProgress((int) MathUtil.round(selection.getComms().getDataRate() * 10));
        label.setText(String.valueOf(Double.parseDouble(L12.getText()) / 10.0)
                + "Mbps");
        L12.setRenderValueOnTop(true);

        selection.getCommsView().slider = L12;

        // all actions at the end to update other fields
        L12.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                // System.out.println(L12.getText());
                try {
                    selection.getComms().
                            setDataRate(Double.parseDouble(L12.getText()) / 10.0);
                    // update EIRP
                    label.setText(String.valueOf(Double.parseDouble(L12.getText()) / 10.0)
                            + "Mbps");

                } catch (java.lang.NumberFormatException e) {
                    System.out.println("TxView: bad number " + L12.getText());
                }
            }
        });

        // combo box created so return it
        return L12;
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

        Container cnt = new Container(new BorderLayout());
        sub.addComponent(cnt);

        // Hardcoded table. Name, value, unit
        TableLayout layout = new TableLayout(2, 3);
        cnt.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        // constraint.setVerticalSpan(2);
        constraint.setWidthPercentage(20);

        // now go sequentially through the Tx terminal fields
        // final Terminal ter = selection.gettXterminal();
        Label L01 = new Label("Data Rate");
        Label L02 = new Label(Com.shortText(selection.getComms().getDataRate()));
        Label L03 = new Label("Mbps ");
        cnt.addComponent(L01);
        cnt.addComponent(constraint, L02);
        cnt.addComponent(L03);

        Label L11 = new Label("Data Rate");
        final Slider L12 = new Slider();

        L12.setMinValue((int) MathUtil.round(Comms.DATA_RATE_LO * 10)); // x10
        L12.setMaxValue((int) MathUtil.round(Comms.DATA_RATE_HI * 10));
        L12.setEditable(true);

        L12.setIncrements(5); //
        L12.setProgress((int) MathUtil.round(selection.getComms().getDataRate() * 10));

        L12.setRenderValueOnTop(true);

        final Label L13 = new Label(Com.shortText(selection.getComms().getDataRate()) + "Mbps");
        cnt.addComponent(L11);
        cnt.addComponent(L12);
        cnt.addComponent(L13);

        Label L21 = new Label("BW");
        final Slider L22 = new Slider();

        L22.setMinValue((int) MathUtil.round(Comms.BW_LO * 10)); // x10
        L22.setMaxValue((int) MathUtil.round(Comms.BW_HI * 10));
        L22.setEditable(true);

        L22.setIncrements(5); //
        L22.setProgress((int) MathUtil.round(selection.getComms().getBW() * 10));

        L22.setRenderValueOnTop(true);

        final Label L23 = new Label(Com.shortText(selection.getComms().getBW()) + "MHz");
        cnt.addComponent(L21);
        cnt.addComponent(L22);
        cnt.addComponent(L23);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);

        // now vertical positioning
        cnt = new Container(new BorderLayout());
        sub.addComponent(cnt);

        // Hardcoded table. Name, value, unit
        layout = new TableLayout(2, 3);
        cnt.setLayout(layout);

        Label l1 = new Label("Modulation");
        Label l2 = new Label("Rate");
          Label l3 = new Label("Code");
        cnt.addComponent(l1);
        cnt.addComponent(l2);
         cnt.addComponent(l3);

        Container cntMod = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        cnt.addComponent(cntMod);

        final ButtonGroup bgMod = new ButtonGroup();
        // RadioButton mod = new RadioButton(); // [Comms.modulationHash.size()];

        /**
         * this method should be used to initialize variables instead of the
         * constructor/class scope to avoid race conditions
         */
   
        for (int i = 0; i < Comms.modulationHash.size(); i++) {
            final RadioButton mod = new RadioButton();
            mod.setName(selection.getCommsView().getModulations().get(i));
            mod.setText(selection.getCommsView().getModulations().get(i));

            //add to button group
            bgMod.add(mod);
            mod.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {

                    // System.out.println(bgMod.getSelectedIndex());
                  
                    selection.getComms().setModulation (
                            Comms.indexModulation.toArray(new 
                                    Comms.Modulation[0])[bgMod.getSelectedIndex()]);
            
                }
            });
            cntMod.addComponent(mod);
        }

        Container cntRate = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        cnt.addComponent(cntRate);

        final ButtonGroup bgRate = new ButtonGroup();
        RadioButton codeRate = new RadioButton();

        /**
         * this method should be used to initialize variables instead of the
         * constructor/class scope to avoid race conditions
         */
        for (int i = 0; i < Comms.codeRateHash.size(); i++) {
            codeRate = new RadioButton();
            codeRate.setName(selection.getCommsView().getCodeRates().get(i));
            codeRate.setText(selection.getCommsView().getCodeRates().get(i));
            //add to button group
            bgRate.add(codeRate);
              codeRate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {

                   // System.out.println(bgCode.getSelectedIndex());
                  
                    selection.getComms().setCodeRate (
                            Comms.indexCodeRate.toArray(new 
                                    Comms.CodeRate[0])[bgRate.getSelectedIndex()]);
            
                }
            });
            //add to container
            cntRate.addComponent(codeRate);
        }

        // now the codeRate
        Container cntCode = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        cnt.addComponent(cntCode);

        final ButtonGroup bgCode = new ButtonGroup();
        // RadioButton mod = new RadioButton(); // [Comms.modulationHash.size()];

        /**
         * this method should be used to initialize variables instead of the
         * constructor/class scope to avoid race conditions
         */
 
        for (int i = 0; i < Comms.codeHash.size(); i++) {
            final RadioButton mod = new RadioButton();
            mod.setName(selection.getCommsView().getCodes().get(i));
            mod.setText(selection.getCommsView().getCodes().get(i));

            //add to button group
            bgCode.add(mod);
            mod.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {

                    // System.out.println(bgMod.getSelectedIndex());
                  
                    selection.getComms().setCode (
                            Comms.indexCode.toArray(new 
                                    Comms.Code[0])[bgCode.getSelectedIndex()]);
            
                }
            });
            cntCode.addComponent(mod);
        }
        
        L12.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                System.out.println(L12.getText());
                try {

                    selection.getComms().setDataRate(Double.parseDouble(L12.getText()) / 10.0);
                    L13.setText(Com.shortText(selection.getComms().getDataRate()) + "Mbps");

                } catch (java.lang.NumberFormatException e) {
                    System.out.println("TxView: bad number " + L12.getText());

                }

            }
        });

        L22.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                System.out.println(L22.getText());
                try {

                    selection.getComms().setBW(Double.parseDouble(L22.getText()) / 10.0);
                    L23.setText(Com.shortText(selection.getComms().getBW()) + "MHz");

                } catch (java.lang.NumberFormatException e) {
                    System.out.println("TxView: bad number " + L22.getText());

                }

            }
        });

        // have a multi-row table layout and dump the transmit terminal values
        return sub;
    }
}
