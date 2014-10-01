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
    private RfBand.Band band = RfBand.Band.KA;
    private RfBand.Band dLband = RfBand.Band.KA_DL;
    private RfBand.Band uLband = RfBand.Band.KA_UL;
    private Com.Orbit orbit = Com.Orbit.GEO;

    private Selection selection;

    // Each band has a vector of terminals and satellites read from files
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
            Selection.bandSatellite = Satellite.getFromFile(satellites);
            is = Display.getInstance().getResourceAsStream(null, "/terminals.txt");

            terminals = parser.parse(new InputStreamReader(is));
            Selection.bandTerminal = Terminal.getFromFile(terminals);

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

        initBands(main, selection);

        Container cnt = new Container(new BorderLayout());
        main.addComponent(cnt);

        // there are six items in Views.  Hardcoded table.
        cnt.setLayout(new TableLayout(6, 5));

        for (final View view : views) {

            initViews(view, cnt, selection);
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

    public void initViews(final View view, Container cnt, Selection selection) {

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

    public void initBands(Form main, final Selection selection) {
        // band selection

        Container topLine = new Container();
        topLine.setLayout(new BoxLayout(BoxLayout.X_AXIS));
        main.addComponent(topLine);

        final Label band = new Label();
        final ComboBox spin = new ComboBox();
        topLine.addComponent(spin);
        topLine.addComponent(band);

        ListModel model = new DefaultListModel(RfBand.indexRfBand);
        int index = spin.getSelectedIndex();

        // make sure to add new RfBand[] so that JVM knows to downcast Object
        selection.setBand(RfBand.indexRfBand.toArray(new RfBand[0])[index].getBand());

        // note that only a String has substring functions
        band.setText((Com.shortText(RfBand.indexRfBand.toArray(new RfBand[0])[index].lowFrequency / 1E9))
                + " - " + (Com.shortText(RfBand.indexRfBand.toArray(new RfBand[0])[index].highFrequency / 1E9))
                + " GHz");

        spin.setModel(model);

        spin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                int index = spin.getSelectedIndex();
                selection.setBand(RfBand.indexRfBand.toArray(
                        new RfBand[0])[index].getBand());
                band.setText(Com.shortText((RfBand.indexRfBand.toArray(new RfBand[0])[index].lowFrequency / 1E9))
                        + " - " + (Com.shortText(RfBand.indexRfBand.toArray(new RfBand[0])[index].highFrequency / 1E9))
                        + " GHz");
                System.out.println(spin.getSelectedItem());
                comboSatellite(selection, spin);
                comboTx(selection, spin);
                comboRx(selection, spin);
            }
        });

    }

    public void comboSatellite(final Selection selection, final ComboBox spin) {
        // use global variable to change ListModel of satellite combo
        if (Selection.bandSatellite.get(selection.getBand()) == null) {

            System.out.println("link: Can't get bandSatellite for band "
                    + selection.getBand());
            // Force it to KA which hopefully works
            selection.setBand(RfBand.Band.KA);

            // get index of KA
            int i;

            i = RfBand.rFbandHash.get("KA").getIndex();

            int index = RfBand.indexRfBand.toArray(new RfBand[0])[i].getIndex();

            // change the current Combobox entry
            spin.setSelectedIndex(index);
        }
        DefaultListModel model = new DefaultListModel(
                (Selection.bandSatellite.get(selection.getBand()).toArray(
                        new Satellite[0])));

        if (model == null) {
            System.out.println("Link: Can't create DefaultListModel for satellite band "
                    + selection.getBand());
        } else {
            selection.getSatelliteView().spin.setModel(model);
        }

    }

    public void comboRx(final Selection selection, ComboBox spin) {
        // use global variable to change ListModel of satellite combo
        if (Selection.bandTerminal.get(selection.getBand()) == null) {

            // Force it to KA which hopefully works
            selection.setBand(RfBand.Band.KA);

            // get index of KA
            int i;

            i = RfBand.rFbandHash.get("KA").getIndex();

            int index = RfBand.indexRfBand.toArray(new RfBand[0])[i].getIndex();

            // change the current Combobox entry
            spin.setSelectedIndex(index);
        }
        DefaultListModel model = new DefaultListModel(
                (Selection.bandTerminal.get(selection.getBand()).toArray(
                        new Terminal[0])));

        if (model == null) {
            System.out.println("Link: Can't create DefaultListModel for Rx terminal band "
                    + selection.getBand());
        } else {
            selection.getRxView().spin.setModel(model);
        }

    }

    public void comboTx(final Selection selection, ComboBox spin) {
        // use global variable to change ListModel of satellite combo
        if (Selection.bandTerminal.get(selection.getBand()) == null) {

            // Force it to KA which hopefully works
            selection.setBand(RfBand.Band.KA);

            // get index of KA
            int i;

            i = RfBand.rFbandHash.get("KA").getIndex();

            int index = RfBand.indexRfBand.toArray(new RfBand[0])[i].getIndex();

            // change the current Combobox entry
            spin.setSelectedIndex(index);
        }
        DefaultListModel model = new DefaultListModel(
                (Selection.bandTerminal.get(selection.getBand()).toArray(
                        new Terminal[0])));

        if (model == null) {
            System.out.println("Link: Can't create DefaultListModel for Tx terminal band "
                    + selection.getBand());
        } else {
            selection.getTxView().spin.setModel(model);
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
