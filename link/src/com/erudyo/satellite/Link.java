/*
 * OVERVIEW:
 * main driver for the application.  Uses models (such as Terminal) and views
 * such as (TxView) to organize code.  Selection class can hold semiMajor specific collection
 * of both views and models to provide multiple instances for user selections.
 * 
 * Typicall, semiMajor class has semiMajor calc* function which is stateless (defined as static)
 * while the set* function would change instance level values.  Object instances
 * could depend on each other and instance's update method is automatically called
 * when any child or sibling changes.  An instance registers itself by calling
 * the addAffected method of another object (such as its child).
 */
package com.erudyo.satellite;
// this is main

import com.codename1.ui.SideMenuBar;
import com.codename1.ui.TextArea;
import com.codename1.components.InfiniteProgress;
import com.codename1.ui.Slider;
import com.codename1.components.ShareButton;
import com.codename1.components.WebBrowser;
import com.codename1.facebook.ui.LikeButton;
import com.codename1.io.CSVParser;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkManager;
import com.codename1.io.Util;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.maps.Coord;
import com.codename1.maps.MapComponent;
import com.codename1.maps.MapComponent;
import com.codename1.maps.layers.ArrowLinesLayer;
import com.codename1.maps.layers.LinesLayer;
import com.codename1.maps.layers.PointLayer;
import com.codename1.maps.layers.PointsLayer;
import com.codename1.maps.providers.GoogleMapsProvider;
import com.codename1.ui.Button;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.spinner.GenericSpinner;
import com.codename1.ui.table.DefaultTableModel;
import com.codename1.ui.table.Table;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
            // refreshTheme(parentForm);
            CSVParser parser = new CSVParser('|');
            
            
            InputStream is = Display.getInstance().
                    getResourceAsStream(null, "/all_satellites.txt");

            // Satellite has to read all the records from file.  Selection
            // could include only semiMajor subset per instance (e.g., satellites
            // visible from semiMajor location
            selection.setBandSatellite(Satellite.getFromFile(
                    parser.parse(new InputStreamReader(is))));
            
            is = Display.getInstance().
                    getResourceAsStream(null, "/terminals.txt");
            
             parser = new CSVParser(',');
            
            selection.setBandTerminal(Terminal.getFromFile(
                    parser.parse(new InputStreamReader(is))));

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
    
    private void initViews(Selection selection) {
        
        Log.p("Started application");
        
        views = new View[6];
        // selection contains current selection of atellite, terminals, band, etc.
        // selections from previous session can be read from persistent storage
        // else default values are used.

        views[0] = selection.getCommsView();
        views[1] = selection.getTxView();
        // process satellite first since it is needed by UlPath and DlPath
        views[2] = selection.getSatelliteView();
        views[3] = selection.getuLpathView();
        
        views[4] = selection.getdLpathView();
        views[5] = selection.getRxView();
        
    }
    
    public void start() {
        if (current != null) {
            current.show();
            return;
        }
        
        main = new Form("Satellite Link");
        main.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        // set the current Band and then satellite, terminal
        initBands(main, selection);
        
        Container cnt = new Container(new BorderLayout());
        main.addComponent(cnt);

        // there are six items in Views.  Hardcoded table.
        // no sub-label
        TableLayout layout = new TableLayout(6, 4);
        cnt.setLayout(layout);
        
        for (final View view : views) {
            
            initViews(view, cnt, selection, layout);
        }
        Button b = new Button("Map");

        // get map form for selecting terminals and satellite
        main.addComponent(b);
        final MapView map = new MapView("Map");
        b.addActionListener(new ActionListener() {
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
    
    public void initViews(final View view, Container cnt, final Selection selection, TableLayout layout) {
        
        try {
            Image cmdIcon = Image.createImage("/blue_pin.png");

            // create name, value, unit, and command components for each view
            // use fixed length for each column
            Component n = view.getWidget(selection);
            Component s = view.getSubWidget(selection);

            // all these widgets have to be remembered by respective views 
            Component v = view.getLabel(selection);
            Component u = view.getSubLabel(selection);
            
            Button c = new Button("->"); //view.getName());

            TableLayout.Constraint constraint = layout.createConstraint();
            // constraint.setVerticalSpan(2);
            constraint.setWidthPercentage(40);      // half of width

            cnt.addComponent(constraint, n);
            
            constraint = layout.createConstraint(); // 30% of width
            constraint.setWidthPercentage(25);
            
            cnt.addComponent(constraint, s);
            cnt.addComponent(v);
            // cnt.addComponent(u);  NO Sublabel
            cnt.addComponent(c);
            
            c.setIcon(cmdIcon);
            
            c.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    final Form form = view.createView(selection);
                    BackCommand bc = new BackCommand();
                    form.addCommand(bc);
                    form.setBackCommand(bc);
                    
                    final Command cmd1 = new Command("Command1");
                    final Command cmd2 = new Command("Command2");
                    Command cmd = new Command("Command3") {
                        public void actionPerformed(ActionEvent ev) {
                            // create a new form with its own back.  In future
                            // add home (back to home)
                            final Form text = new Form(form.getName());
                            
                            TextArea taTable = new TextArea();
                            
                            ArrayList<ArrayList<String>> table
                                    = view.getText(selection);
                          
                            String string = new String();
                            for (ArrayList<String> line : table) {
                                
                                for (String item : line) {
                                    if (item != null) {
                          
                                        string = string + item;
                                        // put empty spaces
                                        string = string + "\t\t"; // "                   ".
                                             //   substring(0,20 - item.length());
                                    } else {
                                        Log.p("Link: null item in all " + line, Log.WARNING);
                                    }
                                }
                                string = string + "\n";
                               
                            }
                           
                            taTable.setText(string);
                            taTable.setEditable(false);
                            taTable.setFocusable(false);
                            taTable.setUIID("Label");
                            text.addComponent(taTable);
                            
                            Command bc = new Command("Back to " + form.getName()) {
                                public void actionPerformed(ActionEvent ev) {
                                    form.showBack();
                                }
                            };
                            text.addCommand(bc);
                            text.setBackCommand(bc);
                            text.show();
                            
                            Log.p("Link: menu command " + cmd1.getId(), Log.DEBUG);
                        }
                    };
                    cmd.putClientProperty(SideMenuBar.COMMAND_PLACEMENT_KEY,
                            SideMenuBar.COMMAND_PLACEMENT_VALUE_RIGHT);
                    form.addCommand(cmd);
                    form.addCommand(cmd1);
                    form.addCommand(cmd2);
                    form.show();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
            
        }
        
    }
    
    public void initBands(Form main, final Selection selection) {
        // band selection

        Container topLine = new Container();
        topLine.setLayout(new BoxLayout(BoxLayout.X_AXIS));
        main.addComponent(topLine);
        
        final Label bandLabel = new Label();
        final ComboBox spin = new ComboBox();
        topLine.addComponent(spin);
        topLine.addComponent(bandLabel);
        
        ListModel model = new DefaultListModel(selection.getRfBands());
        spin.setModel(model);
        
        String item = (String) spin.getSelectedItem();
        
        RfBand rFband = RfBand.rFbandHash.get(item);
        
        selection.setBand(rFband.getBand());

        // note that only semiMajor String has substring functions
        bandLabel.setText((Com.shortText(rFband.lowFrequency / 1E9))
                + " - " + (Com.shortText(rFband.highFrequency / 1E9))
                + " GHz");

        // change in band will change satellite and terminals which would
        // change the paths UL and DL also.   Need to handle it here and
        // then again in respective satellite and terminal change
        spin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                
                String item = (String) spin.getSelectedItem();
                RfBand rFband = RfBand.rFbandHash.get(item);
                
                selection.setBand(rFband.getBand());
                
                bandLabel.setText(Com.shortText((rFband.lowFrequency / 1E9))
                        + " - " + (Com.shortText(rFband.highFrequency / 1E9))
                        + " GHz");
                Log.p("Link: band selection " + spin.getSelectedItem().toString(), Log.DEBUG);

                // update combos and labels
                comboSatellite(selection, spin);
                
                comboTx(selection, spin);
                comboRx(selection, spin);
                comboUlPath(selection);
            }
        });
        
    }
    
    public void comboSatellite(final Selection selection, final ComboBox spin) {
        // use global variable to change ListModel of satellite combo
        if (selection.getBandSatellite().get(selection.getBand()) == null) {
            
            Log.p("link: Can't get bandSatellite for band "
                    + selection.getBand(), Log.DEBUG);
            // Force it to KA which hopefully works
            selection.setBand(RfBand.Band.KA);

            // get index of KA
            int i = selection.getRfBandHash().get("KA");

            // change the current Combobox entry
            spin.setSelectedIndex(i);
        }
        DefaultListModel model = new DefaultListModel(
                (selection.getBandSatellite().get(selection.getBand()).toArray(
                        new String[0])));
        
        if (model == null) {
            Log.p("Link: Can't create DefaultListModel for satellite band "
                    + selection.getBand(), Log.DEBUG);
        } else {
            // use the list of satellites for select band 
            selection.getSatelliteView().spin.setModel(model);
            String name = (String) selection.getSatelliteView().spin.getSelectedItem();

            // update selected satellite
            selection.setSatellite(Satellite.satelliteHash.get(name));

            // update the UL paths
            comboUlPath(selection);
            // update label of satellite
            selection.getSatelliteView().label.setText(
                    selection.getSatellite().getName());
        }
        
    }
    
    public void comboUlPath(Selection selection) {
        selection.getuLpath().setSatellite(selection.getSatellite());
        selection.getuLpath().setTerminal(selection.gettXterminal());
        selection.getuLpath().setAll();
        
    }
    
    public void comboRx(final Selection selection, ComboBox spin) {
        // use global variable to change ListModel of satellite combo
        if (selection.getBandTerminal().get(selection.getBand()) == null) {

            // Force it to KA which hopefully works
            selection.setBand(RfBand.Band.KA);

            // get index of KA
            int i = selection.getRfBandHash().get("KA");

            // change the current Combobox entry
            spin.setSelectedIndex(i);
        }
        DefaultListModel model = new DefaultListModel(
                (selection.getBandTerminal().get(selection.getBand()).toArray(
                        new String[0])));
        
        if (model == null) {
            Log.p("Link: Can't create DefaultListModel for Rx terminal band "
                    + selection.getBand(), Log.DEBUG);
        } else {
            selection.getRxView().spin.setModel(model);
            // update label TODO

            String name = (String) selection.getRxView().spin.getSelectedItem();

            // update selected satellite
            selection.setrXterminal(Terminal.terminalHash.get(name));
            // update label TODO
            selection.getRxView().label.setText(
                    selection.getrXterminal().getName());
            
        }
        
    }
    
    public void comboTx(final Selection selection, ComboBox spin) {
        // use global variable to change ListModel of satellite combo
        if (selection.getBandTerminal().get(selection.getBand()) == null) {

            // Force it to KA which hopefully works
            selection.setBand(RfBand.Band.KA);

            // get index of KA
            int i = selection.getRfBandHash().get("KA");

            // change the current Combobox entry
            spin.setSelectedIndex(i);
        }
        
        DefaultListModel model = new DefaultListModel(
                (selection.getBandTerminal().get(selection.getBand()).toArray(
                        new String[0])));
        
        if (model == null) {
            Log.p("Link: Can't create DefaultListModel for Tx terminal band "
                    + selection.getBand(), Log.ERROR);
        } else {
            selection.getTxView().spin.setModel(model);
            // update label TODO

            String name = (String) selection.getTxView().spin.getSelectedItem();

            // update selected Tx terminal
            selection.settXterminal(Terminal.terminalHash.get(name));

            // update the UL path
            comboUlPath(selection);

            // update label of Tx terminal
            selection.getTxView().label.setText(
                    selection.gettXterminal().getName());
            // update its lat/long (comes from UlPath)
            selection.getuLpathView().label.setText(
                    Com.toDMS(selection.gettXterminal().getLongitude()));
            // update label of Tx terminal

            selection.getuLpathView().subLabel.setText(
                    Com.toDMS(selection.gettXterminal().getLatitude()));
        }
        
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
