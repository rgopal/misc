package com.erudyo.satellite;
// this is main

import com.codename1.components.InfiniteProgress;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkManager;
import com.codename1.io.Util;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.maps.Coord;
import com.codename1.maps.Coord;
import com.codename1.maps.MapComponent;
import com.codename1.maps.MapComponent;
import com.codename1.maps.layers.ArrowLinesLayer;
import com.codename1.maps.layers.LinesLayer;
import com.codename1.maps.layers.PointLayer;
import com.codename1.maps.layers.PointsLayer;
import com.codename1.maps.providers.GoogleMapsProvider;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.plaf.UIManager;
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

import com.codename1.io.CSVParser;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.ui.table.Table;
import com.codename1.ui.table.DefaultTableModel;
import com.codename1.components.WebBrowser;
import java.io.IOException;

public class Link {

    private Form main;
    private Form current;
    private Com.Band band = Com.Band.KA;
    private Com.Band dLband = Com.Band.KA_DL;
    private Com.Band uLband = Com.Band.KA_UL;
    private Com.Orbit orbit = Com.Orbit.GEO;

    private Selection selection;

    private View[] views;

    private String[][] satellites;
    private String[][] terminals;

    public void init(Object context) {
        try {
            Resources theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
            CSVParser parser = new CSVParser();
            InputStream is = Display.getInstance().getResourceAsStream(null, "/satellites.txt");
            satellites = parser.parse(new InputStreamReader(is));
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
        views = new View[5];

        selection = new Selection();
        selection.settXterminal(new Terminal());
        selection.gettXterminal().setBand(band);
        selection.gettXterminal().setName("TX");
        views[0] = new TxView(selection.gettXterminal());

        selection.setuLpath(new Path());
        selection.getuLpath().setName("ULP");
        views[1] = new PathView(selection.getuLpath());

        selection.setSatellite(new Satellite());
        selection.getSatellite().setName("Sat");
        views[2] = new SatelliteView(selection.getSatellite());

        selection.setdLpath(new Path());
        selection.getdLpath().setName("DLP");
        views[3] = new PathView(selection.getdLpath());

        selection.setrXterminal(new Terminal());
        selection.getrXterminal().setBand(band);
        selection.getrXterminal().setName("RX");
        views[4] = new RxView(selection.getrXterminal());

        selection.setSatellites(satellites);
        selection.setTerminals(terminals);
    }

    public void start() {
        if (current != null) {
            current.show();
            return;
        }

        main = new Form("Satellite Link");
        main.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        Container cnt = new Container(new BorderLayout());
        main.addComponent(cnt);
        cnt.setLayout(new TableLayout(5, 4));

        for (final View view : views) {
            // create name, value, unit, and command components for each view

            Label n = new Label(view.getName());
            Label v = new Label(view.getValue());
            Label u = new Label(view.getUnit());
            Button c = new Button(view.getName());
            cnt.addComponent(n);
            cnt.addComponent(v);
            cnt.addComponent(u);
            cnt.addComponent(c);

            c.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    Form form = view.createView();
                    BackCommand bc = new BackCommand();
                    form.addCommand(bc);
                    form.setBackCommand(bc);
                    form.show();
                }
            });
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

        main.show();

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
