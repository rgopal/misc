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

public class CommsView extends View {

    public Label label;
    public Slider slider;

    public CommsView(Selection selection) {
        super.name = "Comms";
    }

    public CommsView() {

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
        layout = new TableLayout(2, 2);
        cnt.setLayout(layout);
      

        Label l1 = new Label ("Modulation");
        Label l2 = new Label ("FEC Codes");
        cnt.addComponent(l1);
        cnt.addComponent(l2);
       
        Container cnt0 = new Container(new BoxLayout(BoxLayout.Y_AXIS));
         cnt.addComponent(cnt0);
        
        ButtonGroup bg = new ButtonGroup();
        RadioButton[] mods = new RadioButton[Comms.modulationHash.size()];

        /**
         * this method should be used to initialize variables instead of the
         * constructor/class scope to avoid race conditions
         */
        int i = 0;
        for (i = 0; i < Comms.modulationHash.size(); i++) {
            mods[i] = new RadioButton();
            mods[i].setName(selection.getModulations().get(i));
            mods[i].setText(selection.getModulations().get(i));
            //add to button group
            bg.add(mods[i]);
            //add to container
            cnt0.addComponent(mods[i]);
        }
        
         Container cnt1 = new Container(new BoxLayout(BoxLayout.Y_AXIS));
         cnt.addComponent(cnt1);
        
        bg = new ButtonGroup();
        RadioButton[] codes = new RadioButton[Comms.codeHash.size()];

        /**
         * this method should be used to initialize variables instead of the
         * constructor/class scope to avoid race conditions
         */
        
        for (i = 0; i < Comms.codeHash.size(); i++) {
            codes[i] = new RadioButton();
            codes[i].setName(selection.getCodes().get(i));
            codes[i].setText(selection.getCodes().get(i));
            //add to button group
            bg.add(codes[i]);
            //add to container
            cnt1.addComponent(codes[i]);
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
