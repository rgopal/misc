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

    private Hashtable<String, Integer> BERHash
            = new Hashtable<String, Integer>();

    // lookup by index with instance name vector 
    private ArrayList<String> BERs
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

    public void initBERHash() {

        int index = 0;
        // go through the hash to create positions and indexRfBand entries
        for (Comms.BER key : Comms.indexBER) {
            // add the position and increment for next item
            BERHash.put(key.toString(), index++);

            // create a simple array with object name (key)
            BERs.add(key.toString());
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

    public ArrayList<String> getBERs() {
        return this.BERs;
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
        initBERHash();

    }

    public CommsView() {
        // this could initialize the needed data structures
        init();

    }

    // returns a slider to show and select specific data rate (in Comms)
    // with its current value displayed in label member (returned by getLabel)
    // Creates an instance of Comms to store data rate (then used by CreateView
    // for other fields such as modulation, coding, etc.
    public Component getWidget(final Selection selection) {

        final Slider sldrDataRate = new Slider();
        sldrDataRate.setMinValue((int) MathUtil.round(Comms.DATA_RATE_LO * 10 / 1E6)); // x10
        sldrDataRate.setMaxValue((int) MathUtil.round(Comms.DATA_RATE_HI * 10 / 1E6));
        sldrDataRate.setEditable(true);

        sldrDataRate.setIncrements(5); //

        sldrDataRate.setText(String.valueOf(MathUtil.round(
                selection.getComms().getDataRate() * 10 / 1E6)));
        sldrDataRate.setProgress((int) MathUtil.round(
                selection.getComms().getDataRate() * 10 / 1E6));

        sldrDataRate.setRenderValueOnTop(true);

        selection.getCommsView().slider = sldrDataRate;
        updateValues(selection);

        // all actions at the end to update other fields
        sldrDataRate.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                // System.out.println(sldrDataRate.getText());
                try {
                    selection.getComms().
                            setDataRate(Double.parseDouble(sldrDataRate.getText()) * 1E6 / 10.0);
                    // update EIRP

                    updateValues(selection);

                } catch (java.lang.NumberFormatException e) {
                    System.out.println("TxView: bad number " + sldrDataRate.getText());
                }
            }
        });

        // combo box created so return it
        return sldrDataRate;
    }

    public void updateValues(Selection selection) {

        if (selection.getComms() != null) {
            selection.getCommsView().setShortName("Cm");
            selection.getCommsView().setName("Comms");  // short

            selection.getCommsView().setSummary("C/No " + Com.textN(
                    selection.getComms().getCNo(), 5) + "");

            selection.getCommsView().setValue("EbNo " + Com.textN(selection.getComms().
                    geteBno(), 5) + "");

            selection.getCommsView().setSubValue("BEP " + Com.textN(selection.getComms().getBEP(), 5));
        }

    }

    public String getDisplayName() {
        return name;
    }

    // do bandwidth, rollOff, modulation, coding
    public Form createView(final Selection selection) {
        Form sub = new Form(selection.getComms().getName());

        Container cntMany = new Container();
        TableLayout layout = new TableLayout(12, 3);

        cntMany.setLayout(layout);
        sub.addComponent(cntMany);

        // Hardcoded table. Name, value, unit
        TableLayout.Constraint constraint;

        constraint = layout.createConstraint();
        constraint.setWidthPercentage(40);
        // constraint.setHorizontalSpan(3);
        // now go sequentially through the fields

        Label lDataRate01 = new Label("Data Rate");
        final Label lDataRate02 = new Label(Com.text(
                selection.getComms().getDataRate() / 1E6));
        Label lDataRate03 = new Label("Mbps ");
        cntMany.addComponent(constraint, lDataRate01);
        constraint = layout.createConstraint();
        constraint.setWidthPercentage(30);
        cntMany.addComponent(constraint, lDataRate02);
        cntMany.addComponent(lDataRate03);

        final Slider sldrDataRate = new Slider();
        Com.formatSlider(sldrDataRate);

        sldrDataRate.setMinValue((int) MathUtil.round(
                Comms.DATA_RATE_LO * 10 / 1E6)); // x10
        sldrDataRate.setMaxValue((int) MathUtil.round(
                Comms.DATA_RATE_HI * 10 / 1E6));
        sldrDataRate.setEditable(true);

        sldrDataRate.setIncrements(5); //
        sldrDataRate.setProgress((int) MathUtil.round(selection.getComms().
                getDataRate() * 10 / 1E6));

        sldrDataRate.setRenderValueOnTop(true);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cntMany.addComponent(constraint, sldrDataRate);

        Label lCNo01 = new Label("Available C/No");
        // BEP and SEP vary field so padd some
        Label lCNo02 = new Label(Com.text(selection.getComms().getCNo()) + "     "
                + "  ");
        Label lCNo03 = new Label("dBHz ");
        cntMany.addComponent(lCNo01);
        cntMany.addComponent(lCNo02);
        cntMany.addComponent(lCNo03);

        Label lEbNo01 = new Label("Available Eb/No");
        final Label lEbNo02 = new Label(Com.text(selection.getComms().geteBno()));
        redOrGreen(lEbNo02, selection.getComms().getTargetEbNo());
        Label lEbNo03 = new Label("dB ");
        cntMany.addComponent(lEbNo01);
        cntMany.addComponent(lEbNo02);
        cntMany.addComponent(lEbNo03);

        Label lEbcNo01 = new Label("Available Ebc/No");
        final Label lEbcNo02 = new Label(Com.text(selection.getComms().getEbcNo()));
        Label lEbcNo03 = new Label("dB ");
        cntMany.addComponent(lEbcNo01);
        cntMany.addComponent(lEbcNo02);
        cntMany.addComponent(lEbcNo03);

        Label lEsNo01 = new Label("Available Es/No");
        final Label lEsNo02 = new Label(Com.text(selection.getComms().geteSno()));
        Label lEsNo03 = new Label("dB ");
        cntMany.addComponent(lEsNo01);
        cntMany.addComponent(lEsNo02);
        cntMany.addComponent(lEsNo03);

        Label lBW01 = new Label("Bandwidth");
        final Label lBW02 = new Label(Com.text(selection.getComms().
                getBW() / 1.0E6));
        Label lBW03 = new Label("MHz ");
        cntMany.addComponent(lBW01);
        cntMany.addComponent(lBW02);
        cntMany.addComponent(lBW03);

        final Slider sldrBW = new Slider();
        Com.formatSlider(sldrBW);

        sldrBW.setMinValue((int) MathUtil.round(Comms.BW_LO * 10.0 / 1E6)); // x10
        sldrBW.setMaxValue((int) MathUtil.round(Comms.BW_HI * 10.0 / 1E6));
        sldrBW.setEditable(true);

        sldrBW.setIncrements(5); //
        sldrBW.setProgress((int) MathUtil.round(selection.getComms().getBW()
                * 10.0 / 1.0E6));

        sldrBW.setRenderValueOnTop(true);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(3);
        cntMany.addComponent(constraint, sldrBW);

        Label lBEP01 = new Label("Derived BEP");
        final Label lBEP02 = new Label(Com.textD(selection.getComms().getCbEP()));
        Label lBEP03 = new Label(" ");
        cntMany.addComponent(lBEP01);
        cntMany.addComponent(lBEP02);
        cntMany.addComponent(lBEP03);

        Label lcBEP01 = new Label("Derived Coded BEP");
        final Label lcBEP02 = new Label(Com.textD(selection.getComms().getCbEP()));
        Label lcBEP03 = new Label(" ");
        cntMany.addComponent(lcBEP01);
        cntMany.addComponent(lcBEP02);
        cntMany.addComponent(lcBEP03);

        Label lSEP01 = new Label("Derived SEP");
        final Label lSEP02 = new Label(Com.textD(selection.getComms().getSEP()));
        Label lSEP03 = new Label(" ");
        cntMany.addComponent(lSEP01);
        cntMany.addComponent(lSEP02);
        cntMany.addComponent(lSEP03);

        Label lderivedEbNo01 = new Label("Target Eb/No");
        final Label lderivedEbNo02 = new Label(
                Com.text(selection.getComms().getTargetEbNo()));
        lderivedEbNo02.getStyle().setFgColor(Integer.valueOf("0000FF", 16));
        Label lderivedEbNo03 = new Label("dB ");
        cntMany.addComponent(lderivedEbNo01);
        cntMany.addComponent(lderivedEbNo02);
        cntMany.addComponent(lderivedEbNo03);
        // now vertical positioning
        cntMany = new Container();
        sub.addComponent(cntMany);

        // Table with 4 cloumns
        layout = new TableLayout(3, 4);
        cntMany.setLayout(layout);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(4);
        Label filler = new Label(" ");
        cntMany.addComponent(constraint, filler);

        Label l1 = new Label("Modulation");
        Label l2 = new Label("Rate");
        Label l3 = new Label("Code");
        Label l4 = new Label("BER");
        cntMany.addComponent(l1);
        cntMany.addComponent(l2);
        cntMany.addComponent(l3);
        cntMany.addComponent(l4);

        Container cntMod = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        cntMany.addComponent(cntMod);

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

                    Log.p("CommsView: selected Modulatoin "
                            + bgMod.getSelectedIndex(), Log.DEBUG);
                    selection.getComms().setModulation(
                            Comms.indexModulation.toArray(new Comms.Modulation[0])[bgMod.getSelectedIndex()]);
                    // update bit error probability
                       sldrBW.setProgress((int) MathUtil.round(selection.getComms().getBW()
                            * 10.0 / 1.0E6));
                      lEbNo02.setText(Com.shortText(selection.getComms().geteBno()));
                    lEbcNo02.setText(Com.shortText(selection.getComms().getEbcNo()));
                    lEsNo02.setText(Com.shortText(selection.getComms().geteSno()));
                    lBEP02.setText(Com.textD(selection.getComms().getBEP()));
                    lcBEP02.setText(Com.textD(selection.getComms().getCbEP()));
                    lSEP02.setText(Com.textD(selection.getComms().getSEP()));
                    lderivedEbNo02.setText(Com.text(selection.getComms().
                            getTargetEbNo()));
                    redOrGreen(lEbNo02, selection.getComms().getTargetEbNo());
                    updateValues(selection);
                }
            });
            cntMod.addComponent(bModulation);
        }
        // set UI default to BPSK
        bgMod.setSelected((int) modulationHash.get(
                Comms.Modulation.BPSK.toString()));
        // set model defaul to the BPSK
        selection.getComms().setModulation(
                Comms.indexModulation.toArray(new Comms.Modulation[0])[bgMod.getSelectedIndex()]);

        // now bCode rate
        Container cntRate = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        cntMany.addComponent(cntRate);

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

                    Log.p("CommsView: selected Code Rate "
                            + bgRate.getSelectedIndex(), Log.DEBUG);
                    selection.getComms().setCodeRate(
                            Comms.indexCodeRate.toArray(
                                    new Comms.CodeRate[0])[bgRate.getSelectedIndex()]);

                    if (selection.getComms().getCodeRate()
                            == Comms.CodeRate.FEC_1_1) {
                        selection.getComms().setCode(Comms.Code.NONE);
                        // set the right bCode rate in button group
                        bgCode.setSelected((int) codeHash.get(
                                Comms.Code.NONE.toString()));
                    }
                       sldrBW.setProgress((int) MathUtil.round(selection.getComms().getBW()
                            * 10.0 / 1.0E6));
                    lEbNo02.setText(Com.shortText(selection.getComms().geteBno()));
                    lEbcNo02.setText(Com.shortText(selection.getComms().getEbcNo()));
                    lEsNo02.setText(Com.shortText(selection.getComms().geteSno()));
                    lBEP02.setText(Com.textD(selection.getComms().getBEP()));
                    lcBEP02.setText(Com.textD(selection.getComms().getCbEP()));
                    lSEP02.setText(Com.textD(selection.getComms().getSEP()));
                    lderivedEbNo02.setText(Com.text(selection.getComms().
                            getTargetEbNo()));
                    redOrGreen(lEbNo02, selection.getComms().getTargetEbNo());
                    updateValues(selection);
                }
            });
            //add to container
            cntRate.addComponent(bCodeRate);
        }
        // set UI default to the 7/8
        bgRate.setSelected((int) codeRateHash.get(
                Comms.CodeRate.FEC_7_8.toString()));

        // model default to 7/8
        selection.getComms().setCodeRate(
                Comms.indexCodeRate.toArray(new Comms.CodeRate[0])[bgRate.getSelectedIndex()]);

        // now the bCode
        Container cntCode = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        cntMany.addComponent(cntCode);

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
                    Log.p("CommsView: selected Code "
                            + bgCode.getSelectedIndex(), Log.DEBUG);
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
                       sldrBW.setProgress((int) MathUtil.round(selection.getComms().getBW()
                            * 10.0 / 1.0E6));
                    lEbNo02.setText(Com.shortText(selection.getComms().geteBno()));
                    lEbcNo02.setText(Com.shortText(selection.getComms().getEbcNo()));
                    lEsNo02.setText(Com.shortText(selection.getComms().geteSno()));
                    lBEP02.setText(Com.textD(selection.getComms().getBEP()));
                    lcBEP02.setText(Com.textD(selection.getComms().getCbEP()));
                    lSEP02.setText(Com.textD(selection.getComms().getSEP()));
                    lderivedEbNo02.setText(Com.text(selection.getComms().
                            getTargetEbNo()));
                    redOrGreen(lEbNo02, selection.getComms().getTargetEbNo());

                    updateValues(selection);
                }

            });
            cntCode.addComponent(bCode);
        }

        // set default to BCH
        bgCode.setSelected((int) codeHash.get(
                Comms.Code.BCH.toString()));
        // model default to BCH
        selection.getComms().setCode(
                Comms.indexCode.toArray(new Comms.Code[0])[bgCode.getSelectedIndex()]);

        // now BER
        final ButtonGroup bgBER = new ButtonGroup();
        Container cntBER = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        cntMany.addComponent(cntBER);

        for (int i = 0; i < Comms.BERHash.size(); i++) {
            final RadioButton bBER = new RadioButton();
            bBER.setName(selection.getCommsView().getBERs().get(i));
            bBER.setText(selection.getCommsView().getBERs().get(i));
            bBER.getSelectedStyle().setFgColor(Integer.valueOf("00FF00", 16));

            //add to button group
            bgBER.add(bBER);
            // selected is green
            bBER.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    Log.p("CommsView: selected BER "
                            + bgBER.getSelectedIndex(), Log.DEBUG);
                    selection.getComms().setbER(
                            Comms.indexBER.toArray(new Comms.BER[0])[bgBER.
                            getSelectedIndex()]);
                    lderivedEbNo02.setText(Com.text(selection.getComms().
                            getTargetEbNo()));
                    lEbNo02.setText(lEbNo02.getText());
                    redOrGreen(lEbNo02, selection.getComms().getTargetEbNo());
                    updateValues(selection);

                }

            });
            cntBER.addComponent(bBER);
        }

        // set default to BCH
        bgBER.setSelected((int) BERHash.get(
                Comms.BER.BER_6.toString()));
        // model default to BCH
        selection.getComms().setbER(
                Comms.indexBER.toArray(new Comms.BER[0])[bgBER.getSelectedIndex()]);

        sldrDataRate.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("CommsView: selected Data Rate "
                        + sldrDataRate.getText(), Log.DEBUG);
                try {

                    selection.getComms().setDataRate(
                            Double.parseDouble(sldrDataRate.getText())
                            * 1E6 / 10.0);
                    lDataRate02.setText(
                            Com.shortText(selection.getComms().getDataRate() / 1E6));
                    lBW02.setText(Com.text(selection.getComms().getBW() / 1E6));

                    // combination may increase the higher limit of bandwidth
                    if (selection.getComms().getBW() > Comms.BW_HI) {
                        // TODO display gets stuck on high side
                        sldrBW.setMinValue((int) MathUtil.round(
                                Comms.BW_LO * 10.0 / 1E6));
                        sldrBW.setMaxValue((int) MathUtil.round(
                                selection.getComms().getBW() * 10.0 / 1E6));
                    }
                    sldrBW.setProgress((int) MathUtil.round(selection.getComms().getBW()
                            * 10.0 / 1.0E6));
                  lEbNo02.setText(Com.shortText(selection.getComms().geteBno()));
                    lEbcNo02.setText(Com.shortText(selection.getComms().getEbcNo()));
                    lEsNo02.setText(Com.shortText(selection.getComms().geteSno()));
                    lBEP02.setText(Com.textD(selection.getComms().getBEP()));
                    lcBEP02.setText(Com.textD(selection.getComms().getCbEP()));
                    lSEP02.setText(Com.textD(selection.getComms().getSEP()));
                    lderivedEbNo02.setText(Com.text(selection.getComms().
                            getTargetEbNo()));
                    redOrGreen(lEbNo02, selection.getComms().getTargetEbNo());
                    updateValues(selection);

                } catch (java.lang.NumberFormatException e) {
                    Log.p("CommsView: bad number for Data Rate " + sldrDataRate.getText());

                }

            }
        });

        sldrBW.addDataChangedListener(new DataChangedListener() {
            public void dataChanged(int type, int index) {
                Log.p("CommsView: selected BW " + sldrBW.getText(), Log.DEBUG);
                try {

                    selection.getComms().setBW(Double.parseDouble(
                            sldrBW.getText()) * 1E6 / 10.0);

                    lBW02.setText(Com.text(selection.getComms().getBW() / 1E6));
                    lDataRate02.setText(
                            Com.shortText(selection.getComms().getDataRate() / 1E6));
                    // BW does not change data rate 
                    lEbNo02.setText(Com.shortText(selection.getComms().geteBno()));
                    lEbcNo02.setText(Com.shortText(selection.getComms().getEbcNo()));
                    lEsNo02.setText(Com.shortText(selection.getComms().geteSno()));
                    lBEP02.setText(Com.textD(selection.getComms().getBEP()));
                    lcBEP02.setText(Com.textD(selection.getComms().getCbEP()));
                    lSEP02.setText(Com.textD(selection.getComms().getSEP()));
                    lderivedEbNo02.setText(Com.text(selection.getComms().
                            getTargetEbNo()));
                    redOrGreen(lEbNo02, selection.getComms().getTargetEbNo());
                    updateValues(selection);

                } catch (java.lang.NumberFormatException e) {
                    Log.p("CommsView: bad number for BW " + sldrBW.getText(), Log.DEBUG);

                }

            }
        });

        // have a multi-row table layout and dump the transmit terminal values
        return sub;
    }

    // if label's text is smaller than  then red color or else 
    private void redOrGreen(Label l, double value) {
        double lValue = Double.parseDouble(l.getText());
        if (lValue < value) {
            l.getStyle().setFgColor(Integer.valueOf("FF0000", 16));  // red
        } else {
            l.getStyle().setFgColor(Integer.valueOf("00FF00", 16)); // green
        }
    }
}
