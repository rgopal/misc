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
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
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
    private Terminal rXterminal;
    private Terminal tXterminal;
    private Satellite satellite;
    private Path uLpath;
    private Path dLpath;

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

        views = new View[6];

        views[0] = new MapView("MapView");

        tXterminal = new Terminal();
        tXterminal.setBand(band);
        tXterminal.setName("TX Term");

        views[1] = new TxView(tXterminal);
       
         uLpath = new Path();
         uLpath.setName("UL_PATH");
         views[2] = new PathView(dLpath);
        
         views[3] = new SatelliteView(satellite);
         views[4] = new PathView(uLpath);

         dLpath = new Path();
         dLpath.setName("DL_PATH");
         rXterminal = new Terminal();
         rXterminal.setName("RX Terminal");
         rXterminal.setBand(band);
         views[5] = new RxView(rXterminal);

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

    public void start() {
        if (current != null) {
            current.show();
            return;
        }
        Antenna antenna = new Antenna();
        main = new Form("Satellite Link");
        main.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        for (final View view : views) {
            Button b = new Button(view.getName());
            main.addComponent(b);
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    Form form = view.createView();
                    BackCommand bc = new BackCommand();
                    form.addCommand(bc);
                    form.setBackCommand(bc);
                    form.show();
                }
            });
        }
        Object[][] m = new Object[5][3];
        m[0][0] = "Transmitter";
        m[1][0] = "Path";
        m[2][0] = "Satellite";
        m[3][0] = "Path";
        m[4][0] = "Receiver";
        String[] names = {"item", "value", "units"};
        Table table = new Table(new DefaultTableModel(names, m, true));
        main.addComponent(table);
        antenna.setDiameter(1);
        antenna.setFrequency(12E9);
        // Galaxy satellite
        GeoSatellite g = new GeoSatellite();
        g.setLongitude(-Com.PI * 91.0 / 180.0);
        g.setLatitude(0.0);
        // germantown location
        Terminal t = new Terminal();
        t.setLatitude(Com.PI * 39.1793 / 180.0);
        t.setLongitude(-Com.PI * 77.2469 / 180.0);

        Path p = new Path(g, t);
        System.out.println(Com.toDMS(p.getAzimuth()));
        System.out.println(Com.toDMS(p.getElevation()));
        main.addComponent(new Label(antenna.getBand().toString() + antenna.getThreeDBangle()));
        WebBrowser browser = new WebBrowser();

        main.addComponent(browser);
        browser.setURL("jar:///hometest.html");

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
