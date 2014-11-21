/*
 * OVERVIEW:
 * main driver for the application.  Uses models (such as Terminal) and views
 * such as (TxView) to organize code.  Selection class can hold semiMajor specific collection
 * of both views and models to provide multiple instances for user selections.
 *
 * Satellite and Terminal instances are created when .txt files are read.
 * Path objects are created 
 * 
 * Typicall, semiMajor class has semiMajor calc* function which is stateless (defined as static)
 * while the set* function would change instance level values.  Object instances
 * could depend on each other and instance's update method is automatically called
 * when any child or sibling changes.  An instance registers itself by calling
 * the addAffected method of another object (such as its child).
 * TODO
 * create a new optimization view.   It will ask which parameters (dish size, amp
 * power, mod/cod, bandwidth, data rate, ber) should be fixed and which can be varied.   Another settings view
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

    // Each band has semiMajor vector of terminals and satellites read from files
    private View[] views;

    public void init(Object context) {

        // create semiMajor new instance to keep track of all other objects for UI
        Log.setLevel(Log.DEBUG);

        // this creates View objects
        selection = new Selection();

        // get the bands into selection.  In future semiMajor Selection instance
        // could have customized list based on user interface preferences
        // indexRfBand is built by setRfBandHash
        // selection.setIndexRfBand(RfBand.indexRfBand);
        try {
            Resources theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(
                    theme.getThemeResourceNames()[0]));
            Display.getInstance().installNativeTheme();
            // not here since no Form setFonts();
            // refreshTheme(parentForm);

            // also read terminals and the current Tx and Rx terminal
        } catch (IOException e) {
            e.printStackTrace();
        }

        initViews(selection);

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
        Font mediumFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,
                Font.SIZE_MEDIUM);
        Font mono = Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN,
                Font.SIZE_SMALL);

        themeAddition.put("Title.font", mediumFont);
        themeAddition.put("font", defaultFont);
        themeAddition.put("sel#font", defaultFont);
        themeAddition.put("press#font", defaultFont);
        themeAddition.put("Label.font", defaultFont);
        themeAddition.put("Button.font", defaultFont);
        themeAddition.put("ComboBox.font", defaultFont);
        themeAddition.put("ComboBox.sel#font", defaultFont);
        themeAddition.put("ComboBox.press#font", defaultFont);
        themeAddition.put("ComboBox.unsel#font", defaultFont);

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

    private void initViews(Selection selection) {

        Log.p("Started application", Log.DEBUG);

        views = new View[6];
        // selection contains current selection of atellite, terminals, band, etc.
        // selections from previous session can be read from persistent storage
        // else default values are used.

        // process satellite first since it is needed by initVisibleTerminal UlPath and DlPath
        views[0] = selection.getSatelliteView();

        views[1] = selection.getTxView();
        views[2] = selection.getuLpathView();

        views[3] = selection.getRxView();
        views[4] = selection.getdLpathView();
        views[5] = selection.getCommsView();

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

        // there are six items in Views.  Hardcoded table.
        // no sub-label
        TableLayout layout = new TableLayout(10, 4);
        cnt.setLayout(layout);

        // satellites are read from the txt file (and selected with band)
        initViews(selection.getSatelliteView(), cnt, selection, layout, 4);

        initViews(selection.getTxView(), cnt, selection, layout, 4);
        // now uplink, downlink paths and Comms can be created

        selection.setuLpath(new Path(selection.getSatellite(),
                selection.gettXterminal(), Path.PATH_TYPE.UPLINK));
        selection.getuLpath().setPathType(Path.PATH_TYPE.UPLINK);

        initViews(selection.getuLpathView(), cnt, selection, layout, 1);

        initViews(selection.getRxView(), cnt, selection, layout, 4);
        selection.setdLpath(new Path(selection.getSatellite(),
                selection.getrXterminal(), Path.PATH_TYPE.DOWNLINK));

        initViews(selection.getdLpathView(), cnt, selection, layout, 1);

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
            Component subLabel = view.getSubLabel(selection);

            Button bSelectView = new Button("->"); //view.getName());

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
                            // create a new form with its own back.  In future
                            // add home (back to home)
                            final Form text = new Form(view.getName());

                            ArrayList<ArrayList<String>> table
                                    = view.getText(selection);

                            //TextArea taTable = new TextArea();
                            TableLayout layout = new TableLayout(table.size() + 2, 3);

                            Container cnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));

                            text.addComponent(cnt);
                            cnt.setLayout(layout);

                            TableLayout.Constraint constraint = layout.createConstraint();

                            constraint.setWidthPercentage(40);

                            Label lFirst = new Label("Item      ");
                            Label lSecond = new Label("Value   ");
                            Label lThird = new Label("Unit ");

                            cnt.addComponent(constraint, lFirst);
                            constraint = layout.createConstraint();
                            constraint.setWidthPercentage(35);
                            cnt.addComponent(constraint, lSecond);
                            constraint = layout.createConstraint();
                            constraint.setWidthPercentage(25);
                            cnt.addComponent(constraint, lThird);

                            for (ArrayList<String> line : table) {
                                // check the number of items in this line (max 3)
                                constraint = layout.createConstraint();
                                constraint.setHorizontalSpan(4 - line.size());
                                int index = 0;
                                // either one item or exactly three items
                                for (String item : line) {
                                    if (item != null) {
                                        Label lItem = new Label(item);
                                        if (index++ == 0) {
                                            cnt.addComponent(constraint, lItem);
                                        } else {
                                            cnt.addComponent(lItem);
                                        }

                                    } else {
                                        Log.p("Link: null item in all " + line, Log.WARNING);
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

                            Log.p("Link: menu command " + cHome.getId(), Log.DEBUG);
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

    public void initBands(Form main, final Selection selection) {
        // band selection

        Container cntBand = new Container();
        cntBand.setLayout(new BoxLayout(BoxLayout.X_AXIS));
        main.addComponent(cntBand);

        final Label lBand = new Label();
        selection.setlBand(lBand);
        selection.setCbBand(new ComboBox());

        final ComboBox cbBand = selection.getCbBand();

        cntBand.addComponent(cbBand);
        cntBand.addComponent(lBand);

        ListModel model = new DefaultListModel(selection.getRfBands());
        cbBand.setModel(model);

        String item = (String) cbBand.getSelectedItem();

        RfBand rFband = RfBand.rFbandHash.get(item);

        selection.setBand(rFband.getBand());

        selection.bandSatelliteSort(selection.getCurrentLocation().
                getLongitude() * Com.PI / 180.0);

        // note that only semiMajor String has substring functions
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
