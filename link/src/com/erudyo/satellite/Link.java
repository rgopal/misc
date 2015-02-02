/*
 * OVERVIEW:
 * main driver for the application.  Uses models (such as Terminal) and views
 * such as (TxView) to organize code.  Selection class can hold specific collection
 * of both views and models to provide multiple instances for user selections.
 *
 * Satellite and Terminal instances are created when .txt files are read.
 * Path objects are created using satellite and terminals. 
 * 
 * Typicall, a class has calc* functions which are stateless (defined as static)
 * while the set* function would change instance level values.  Object instances
 * could depend on each other and instance's update method is automatically called
 * when any child or sibling changes.  An instance registers itself by calling
 * the addAffected method of another object (such as its child).
 * TODO
 * create a new optimization view.   It will ask which parameters (dish size, amp
 * power, mod/cod, bandwidth, data rate, ber) should be fixed and which can be varied.   
 * Another settings view
 * will take minimum and maximum values for this ranges so that algorithm can 
 * enumerate/vary and get the optimize the value.  Locations and satellite is probably fixed
 * for this optimization.   One can then select a specific variable (data rate,
 * ber, bandwidth) that need to be optimized.
 */
package com.erudyo.satellite;
// this is main

import com.codename1.ui.SideMenuBar;
import com.codename1.components.ShareButton;
import com.codename1.facebook.ui.LikeButton;
import com.codename1.ui.Button;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;
import com.codename1.ui.Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Link {

    private Form main;
    private Form current;
    private RfBand.Band band = RfBand.Band.KA;
    private RfBand.Band dLband = RfBand.Band.KA_DL;
    private RfBand.Band uLband = RfBand.Band.KA_UL;
    private Com.Orbit orbit = Com.Orbit.GEO;

    private Selection selection;

    public void init(Object context) {

        // create new instance to keep track of all other objects for UI
        Log.setLevel(Log.WARNING);

        // this creates View objects
        selection = new Selection();

        // In future Selection instance
        // could have customized list based on user interface preferences
        try {
            Resources theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(
                    theme.getThemeResourceNames()[0]));
            Display.getInstance().installNativeTheme();

            Com.blue_pin = Image.createImage("/blue_pin.png");
            Com.red_pin = Image.createImage("/red_pin.png");

            // not here since no Form setFonts();
            // refreshTheme(parentForm);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Pro users - uncomment this code to get crash reports sent to you automatically
        Display.getInstance().addEdtErrorHandler(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                evt.consume();
                Log.p("Exception in AppName version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
                Log.p("OS " + Display.getInstance().getPlatformName());
                Log.p("Error " + evt.getSource());
                Log.p("Current Form " + Display.getInstance().getCurrent().getName());
                Log.e((Throwable) evt.getSource());
                // Log.sendLog(); only for pro
            }
        });
    }

    private void setFonts(Form c) {
        Hashtable themeAddition = new Hashtable();
        Font defaultFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,
                Font.SIZE_SMALL);
        Font mediumFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,
                Font.SIZE_SMALL);
        Font mono = Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN,
                Font.SIZE_SMALL);

        /*
         /   if(installedTheme == null || !installedTheme.containsKey("TouchCommand.derive")) {
         themeProps.put("TouchCommand.border", Border.getDefaultBorder());
         themeProps.put("TouchCommand.padding", "10,10,10,10");
         themeProps.put("TouchCommand.margin", "0,0,0,0");
         themeProps.put("TouchCommand.align", centerAlign);
         */
        themeAddition.put("Title.font", mediumFont);
        themeAddition.put("font", defaultFont);

        themeAddition.put("sel#font", defaultFont);
        themeAddition.put("unsel#font", defaultFont);
        themeAddition.put("press#font", defaultFont);

        themeAddition.put("Label.font", defaultFont);
        // transparency 0 looks regular, F0 looks grey/black for blue font, FF black
        // FF generates error, has to be 255
        themeAddition.put("Label.transparency", "255");
        // fonts are just 4 colors, tried mixing colors does not work
        themeAddition.put("Label.fgColor", "0"); // default  folor
        themeAddition.put("Label.padding", "4,4,4,4");
        themeAddition.put("Label.margin", "2,2,2,2");

        themeAddition.put("RadioButton.padding", "2,1,1,2");
        themeAddition.put("RadioButton.margin", "2,1,1,2");

        //themeAddition.put("Label.fgColor", "00FF00");
        themeAddition.put("Button.font", defaultFont);
        themeAddition.put("ComboBox.font", defaultFont);
        themeAddition.put("ComboBox.sel#font", defaultFont);
        themeAddition.put("ComboBox.press#font", defaultFont);
        themeAddition.put("ComboBox.unsel#font", defaultFont);

        themeAddition.put("Slider.bgColor", "FF");
        themeAddition.put("Slider.fgColor", "00");
        themeAddition.put("SliderFull.bgColor", "FF");
        themeAddition.put("SliderFull.fgColor", "00");
        themeAddition.put("Slider.padding", "1,1,1,1");
        themeAddition.put("SliderFull.padding", "1,1,1,1");

        themeAddition.put("ComboBoxItem.font", defaultFont);
        themeAddition.put("ComboBoxItem.sel#font", mediumFont);
        themeAddition.put("ComboBoxItem.press#font", defaultFont);
        themeAddition.put("ComboBoxItem.unsel#font", defaultFont);

        themeAddition.put("Button.sel#font", defaultFont);
        themeAddition.put("Button.press#font", defaultFont);
        themeAddition.put("ButtonGroupOnly.font", defaultFont);
        themeAddition.put("ButtonGroupOnly.sel#font", defaultFont);
        themeAddition.put("ButtonGroupOnly.press#font", defaultFont);
        themeAddition.put("ButtonGroup.font", defaultFont);
        themeAddition.put("ButtonGroup.sel#font", defaultFont);
        themeAddition.put("ButtonGroup.press#font", defaultFont);

        themeAddition.put("TextArea.font", mono);

        UIManager.getInstance().addThemeProps(themeAddition);
        // Form c = Display.getInstance().getCurrent();
        c.refreshTheme();

    }

    public void start() {
        if (current != null) {
            current.show();
            return;
        }

        main = new Form("Satellite Link");
        setFonts(main);
        main.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        // set the current Band and select satellite and tx, rx terminals
        initBands(main, selection);

        Container cnt = new Container(new BorderLayout());
        main.addComponent(cnt);

        TableLayout layout = new TableLayout(10, 4);
        cnt.setLayout(layout);

        // satellites are read from the txt file (and selected with band)
        initViews(selection.getSatelliteView(), cnt, selection, layout, 4);

        initViews(selection.getTxView(), cnt, selection, layout, 4);

        // uplink path and then view
        selection.setuLpath(new Path(selection.getSatellite(),
                selection.gettXterminal(), Path.PATH_TYPE.UPLINK));
        selection.getuLpath().setPathType(Path.PATH_TYPE.UPLINK);
        initViews(selection.getuLpathView(), cnt, selection, layout, 1);

        // downlink path and view
        initViews(selection.getRxView(), cnt, selection, layout, 4);
        selection.setdLpath(new Path(selection.getSatellite(),
                selection.getrXterminal(), Path.PATH_TYPE.DOWNLINK));
        initViews(selection.getdLpathView(), cnt, selection, layout, 1);

        // finally communications
        selection.setComms(new Comms(selection.getuLpath(),
                selection.getdLpath()));

        initViews(selection.getCommsView(), cnt, selection, layout, 1);
        Button bMap = new Button("Map");

        // get map form for selecting terminals and satellite
        main.addComponent(bMap);
        final MapView map = new MapView("Map");
        bMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Form form = map.createView(selection);
                BackCommand bc = new BackCommand();
                form.addCommand(bc);
                form.setBackCommand(bc);
                form.show();
            }
        });

        LikeButton like = new LikeButton();
        main.addComponent(like);

        ShareButton s = new ShareButton();
        s.setText("Share");
        s.setTextToShare("Try the satellite link analysis app");
        main.addComponent(s);

        like.setUIID("Button");

        main.show();

    }

    public void initViews(final View view, Container cntLink,
            final Selection selection, TableLayout layout, int span) {

        try {
            Image cmdIcon = Image.createImage("/blue_pin.png");

            // create name, value, unit, and command components for each view
            // use fixed length for each column
            Component widget = view.getWidget(selection);
            Component subWidget = view.getSubWidget(selection);

            // all these widgets have to be remembered by respective views 
            Component label = view.getLabel(selection);
            label.getStyle().setFgColor(Integer.valueOf("00FF00", 16));  // green

            Component subLabel = view.getSubLabel(selection);
            subLabel.getStyle().setFgColor(Integer.valueOf("0000FF", 16));  // blue

            Button bSelectView = new Button(view.getShortName()); //view.getName());

            TableLayout.Constraint constraint;
            // if span is more than 1 then only widget and command are displayed
            if (span > 1) {
                constraint = layout.createConstraint();
                constraint.setWidthPercentage(10);
                Label name = new Label(view.getName());

                cntLink.addComponent(constraint, name);

                constraint = layout.createConstraint();
                constraint.setHorizontalSpan(3);

                cntLink.addComponent(constraint, widget);

                // add others in a new line
                constraint = layout.createConstraint();

                constraint.setWidthPercentage(30);

                constraint = layout.createConstraint(); // 30% of width
                constraint.setWidthPercentage(25);
                cntLink.addComponent(constraint, subWidget);

                constraint = layout.createConstraint();
                constraint.setWidthPercentage(25);
                cntLink.addComponent(constraint, label);
                cntLink.addComponent(subLabel);
                cntLink.addComponent(bSelectView);
                bSelectView.setIcon(cmdIcon);

            } else {
                constraint = layout.createConstraint();

                constraint.setWidthPercentage(30);
                // cntLink.addComponent(constraint, widget);

                cntLink.addComponent(constraint, subWidget);

                constraint = layout.createConstraint(); // 30% of width
                constraint.setWidthPercentage(30);
                cntLink.addComponent(constraint, label);
                cntLink.addComponent(subLabel);
                constraint = layout.createConstraint(); // 30% of width
                constraint.setWidthPercentage(30);
                cntLink.addComponent(bSelectView);
                bSelectView.setIcon(cmdIcon);
            }

            bSelectView.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    final Form form = view.createView(selection);
                    BackCommand bc = new BackCommand();
                    form.addCommand(bc);
                    form.setBackCommand(bc);
                    final String spaces = "                  ";

                    final Command cHome = new Command("Home");
                    final Command cPrev = new Command("Prev");

                    Command cInfo = new Command("Info") {
                        public void actionPerformed(ActionEvent ev) {
                            displayInfo(form, view);
                        }
                    };
                    cInfo.putClientProperty(SideMenuBar.COMMAND_PLACEMENT_KEY,
                            SideMenuBar.COMMAND_PLACEMENT_VALUE_RIGHT);

                    final Command cNext = new Command("Next");
                    form.addCommand(cNext);
                    form.addCommand(cInfo);
                    form.addCommand(cPrev);
                    form.addCommand(cHome);

                    form.show();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void displayInfo(final Form form, final View view) {
        // create a new form with its own back.  In future
        // add home (back to home)
        final Form text = new Form(view.getName());

        
        
        ArrayList<ArrayList<String>> table
                = view.getText(selection);
        TableLayout layout = new TableLayout(table.size() + 2, 3);
        // don't use bad format Container cnt = new Container() ; // new BoxLayout(BoxLayout.Y_AXIS));
        // text.addComponent(cnt);
        text.setLayout(layout);

        TableLayout.Constraint constraint = layout.createConstraint();
        constraint.setWidthPercentage(45);

        Label lFirst = new Label("Item      ");
        Label lSecond = new Label("Value   ");
        Label lThird = new Label("Unit ");

        text.addComponent(constraint, lFirst);
        constraint = layout.createConstraint();
        constraint.setWidthPercentage(30);
        
        text.addComponent(constraint, lSecond);
        constraint = layout.createConstraint();
        constraint.setWidthPercentage(25);
        text.addComponent(constraint, lThird);

        for (ArrayList<String> line : table) {
            // check the number of items in this line (max 3)
            
            constraint = layout.createConstraint();
            constraint.setHorizontalSpan(4 - line.size());
            int index = 0;
            // either one item or exactly three items
            for (String item : line) {
                if (item != null) {
                    Label lItem = new Label(item);
                  
                    // Add constraint for the last item
                    if (index++ == line.size() - 1) {
                        text.addComponent(constraint, lItem);
                    } else {
                        text.addComponent(lItem);
                    }
                } else {
                    Log.p("Link:displayInfo null item in line  " + line, Log.WARNING);
                }
            }

        }

        //taTable.setText(string);
        //taTable.setEditable(false);
        //taTable.setFocusable(false);
        Command bc = new Command("Back") {
            public void actionPerformed(ActionEvent ev) {
                form.showBack();
            }
        };
        text.addCommand(bc);
        text.setBackCommand(bc);
        text.show();
    }

    public void initBands(Form main, final Selection selection) {
        // band selection

        Container cntBand = new Container();
        cntBand.setLayout(new BoxLayout(BoxLayout.X_AXIS));
        main.addComponent(cntBand);

        final Label lBandName = new Label();
        lBandName.setText("Band");
        cntBand.addComponent(lBandName);
        
        final Label lBand = new Label();
        selection.setlBand(lBand);
        selection.setCbBand(new ComboBox());

        final ComboBox cbBand = selection.getCbBand();

        cntBand.addComponent(lBand);
        cntBand.addComponent(cbBand);
        

        ListModel model = new DefaultListModel(selection.getRfBands());
        cbBand.setModel(model);

        String item = (String) cbBand.getSelectedItem();

        RfBand rFband = RfBand.rFbandHash.get(item);

        selection.setBand(rFband.getBand());

        selection.bandSatelliteSort(selection.getCurrentLocation().
                getLongitude() * Com.PI / 180.0);

        // note that only Full Java String has substring functions
        lBand.setText((Com.shortText(rFband.lowFrequency / 1E9))
                + " - " + (Com.shortText(rFband.highFrequency / 1E9))
                + " GHz");

        // change in band will change satellite and terminals which would
        // change the paths UL and DL also.   Need to handle it here and
        // then again in respective satellite and terminal change
        cbBand.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                selection.setBand(RfBand.rFbandHash.get(
                        cbBand.getSelectedItem()).getBand());
                selection.comboBand(selection);
            }
        });

    }

    class BackCommand extends Command {

        public BackCommand() {

            super("Back");
        }

        public void actionPerformed(ActionEvent evt) {
            main.showBack();
        }
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }

    public void destroy() {
    }

}
