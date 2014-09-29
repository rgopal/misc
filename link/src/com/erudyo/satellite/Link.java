package com.erudyo.satellite;
// this is main

import com.codename1.components.InfiniteProgress;
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
    private Com.Band band = Com.Band.KA;
    private Com.Band dLband = Com.Band.KA_DL;
    private Com.Band uLband = Com.Band.KA_UL;
    private Com.Orbit orbit = Com.Orbit.GEO;

    private Selection selection;

    // Each band has a vector of terminals and satellites read from files
    private Hashtable<Com.Band, Vector<Satellite>> bandSatellite;
    private Hashtable<Com.Band, Vector<Terminal>> bandTerminal;

    private View[] views;

    public void init(Object context) {

        String[][] satellites;
        String[][] terminals;

        selection = new Selection();
        try {
            Resources theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
            Display.getInstance().installNativeTheme();
            // refreshTheme(parentForm);
            CSVParser parser = new CSVParser();

            InputStream is = Display.getInstance().getResourceAsStream(null, "/satellites.txt");
            satellites = parser.parse(new InputStreamReader(is));
            bandSatellite = Satellite.getFromFile(satellites);
            is = Display.getInstance().getResourceAsStream(null, "/terminals.txt");

            terminals = parser.parse(new InputStreamReader(is));
            bandTerminal = Terminal.getFromFile(terminals);

            // also read terminals and the current Tx and Rx terminal
        } catch (IOException e) {
            e.printStackTrace();
        }

        initSelection();

        // Pro users - uncomment this code to get crash reports sent to you automatically
         /*Display.getInstance().addEdtErrorHandler(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
         evt.consume();
         Log.p("Exception in AppName version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
         Log.p("OS " + Display.getInstance().getPlatformName());
         Log.p("Error " + evt.getSource());
         Log.p("Current Form " + Display.getInstance().getCurrent().getName());
         Log.e((Throwable)evt.getSource());
         Log.sendLog();
         }
         });*/
    }

    private void initSelection() {
        views = new View[6];
        // selection contains current selection of atellite, terminals, band, etc.
        // selections from previous session can be read from persistent storage
        // else default values are used.

        views[0] = new HeadView();
        views[1] = new TxView(selection);
        views[2] = new DlPathView(selection);
        views[3] = new SatelliteView(selection);
        views[4] = new UlPathView(selection);
        views[5] = new RxView(selection);

    }

    public void start() {
        if (current != null) {
            current.show();
            return;
        }

        main = new Form("Satellite Link");
        main.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        initBands(main); 
        
        Container cnt = new Container(new BorderLayout());
        main.addComponent(cnt);
        
        // there are six items in Views.  Hardcoded table.
        cnt.setLayout(new TableLayout(6, 5));
        
        for (final View view : views) {
            initViews(view, cnt);
        }
        Button b = new Button("Map");

        // get map form for selecting terminals and satellite
        main.addComponent(b);
        final MapView map = new MapView("Map");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Form form = map.createView();
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

    public void initViews(final View view, Container cnt) {
       

        try {
            Image cmdIcon = Image.createImage("/blue_pin.png");

            // create name, value, unit, and command components for each view
            
            // use fixed length for each column
            Component n = view.getWidget();
            Label s = new Label(view.getSummary());
            Label v = new Label(view.getValue());
            Label u = new Label(view.getUnit());
            
            
            Button c = new Button("->"); //view.getName());
            cnt.addComponent(n);
            cnt.addComponent(s);
            cnt.addComponent(v);
            cnt.addComponent(u);
            cnt.addComponent(c);

            c.setIcon(cmdIcon);

            c.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    Form form = view.createView();
                    BackCommand bc = new BackCommand();
                    form.addCommand(bc);
                    form.setBackCommand(bc);
                    form.show();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();

        }

    } 

    public void initBands(Form main) {
        // band selection

        Container topLine = new Container();
        topLine.setLayout(new BoxLayout(BoxLayout.X_AXIS));
        main.addComponent(topLine);

        final Label band = new Label();
        final ComboBox spin = new ComboBox();
        topLine.addComponent(spin);
        topLine.addComponent(band);

        ListModel model = new DefaultListModel(Com.bands);
        int index = spin.getSelectedIndex();
        selection.setBand(Com.bands[index]);
        System.out.println(Com.bandParams[2].highFrequency);
        // note that only a String has substring functions
        band.setText((Com.shortText(Com.bandParams[index].lowFrequency / 1E9))
                + " - " + (Com.shortText(Com.bandParams[index].highFrequency / 1E9))
                + " GHz");

        spin.setModel(model);

        spin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                int index = spin.getSelectedIndex();
                selection.setBand(Com.bands[index]);
                System.out.println("this".substring(1));

                band.setText(Com.shortText((Com.bandParams[index].lowFrequency / 1E9))
                        + " - " + (Com.shortText(Com.bandParams[index].highFrequency / 1E9))
                        + " GHz");
                System.out.println(spin.getSelectedItem());
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
