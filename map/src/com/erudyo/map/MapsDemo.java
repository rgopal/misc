package com.erudyo.map;

import com.codename1.components.InfiniteProgress;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkManager;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.maps.Coord;
import com.codename1.maps.Coord;
import com.codename1.maps.MapComponent;
import com.codename1.maps.MapComponent;
import com.codename1.maps.layers.PointLayer;
import com.codename1.maps.layers.PointsLayer;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This is a simple demo that demonstrates how to use the MapComponent and how
 * to show POI on a map using google location API's
 * Make sure to get a key from https://developers.google.com/maps/documentation/places/
 * to run the 'Find Resturants' sub demo
 *
 * @author Chen
 */
public class MapsDemo {

    private Form main;

    public void init(Object context) {
        System.out.println("init");
        try {
            Resources theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        main = new Form("Maps Demo");
        main.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        Button b = new Button("Where am I?");
        main.addComponent(b);
        b.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                showMeOnMap();
            }
        });
        b = new Button("Find Resturants");
        main.addComponent(b);
        b.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                InfiniteProgress inf = new InfiniteProgress();
                Dialog progress = inf.showInifiniteBlocking();

                showResturantsOnMap(progress);
            }
        });

        main.show();
    }

    private void showMeOnMap() {
        Form map = new Form("Map");
        map.setLayout(new BorderLayout());
        map.setScrollable(false);
        final MapComponent mc = new MapComponent();

        putMeOnMap(mc);
        mc.zoomToLayers();

        map.addComponent(BorderLayout.CENTER, mc);
        map.addCommand(new BackCommand());
        map.setBackCommand(new BackCommand());
        map.show();

    }

    private void showResturantsOnMap(final Dialog progress) {
        try {
            final Form map = new Form("Map");
            map.setLayout(new BorderLayout());
            map.setScrollable(false);
            final MapComponent mc = new MapComponent();
            Location loc = LocationManager.getLocationManager().getCurrentLocation();
            putMeOnMap(mc);
            map.addComponent(BorderLayout.CENTER, mc);
            map.addCommand(new BackCommand());
            map.setBackCommand(new BackCommand());

            ConnectionRequest req = new ConnectionRequest() {

                protected void readResponse(InputStream input) throws IOException {
                    JSONParser p = new JSONParser();
                    Hashtable h = p.parse(new InputStreamReader(input));
                    // "status" : "REQUEST_DENIED"
                    String response = (String)h.get("status");
                    if(response.equals("REQUEST_DENIED")){
                        System.out.println("make sure to obtain a key from "
                                + "https://developers.google.com/maps/documentation/places/");
                        progress.dispose();
                        Dialog.show("Info", "make sure to obtain an application key from "
                                + "google places api's"
                                , "Ok", null);
                        return;
                    }
                       
                    final Vector v = (Vector) h.get("results");

                    Image im = Image.createImage("/red_pin.png");
                    PointsLayer pl = new PointsLayer();
                    pl.setPointIcon(im);
                    pl.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent evt) {
                            PointLayer p = (PointLayer) evt.getSource();
                            System.out.println("pressed " + p);

                            Dialog.show("Details", "" + p.getName(), "Ok", null);
                        }
                    });

                    for (int i = 0; i < v.size(); i++) {
                        Hashtable entry = (Hashtable) v.elementAt(i);
                        Hashtable geo = (Hashtable) entry.get("geometry");
                        Hashtable loc = (Hashtable) geo.get("location");
                        Double lat = (Double) loc.get("lat");
                        Double lng = (Double) loc.get("lng");
                        PointLayer point = new PointLayer(new Coord(lat.doubleValue(), lng.doubleValue()),
                                (String) entry.get("name"), null);
                        pl.addPoint(point);
                    }
                    progress.dispose();
                   
                    mc.addLayer(pl);
                    map.show();
                    mc.zoomToLayers();

                }
            };
            req.setUrl("https://maps.googleapis.com/maps/api/place/search/json");
            req.setPost(false);
            req.addArgument("location", "" + loc.getLatitude() + "," + loc.getLongtitude());
            req.addArgument("radius", "5000");
            req.addArgument("types", "food");
            req.addArgument("sensor", "false");
           
            //get your own key from https://developers.google.com/maps/documentation/places/
            //and replace it here.
            String key = "AIzaSyBEUsbb2NkrYxdQSG-kUgjZCoaLY0QhYmk";
           
            req.addArgument("key", key);

            NetworkManager.getInstance().addToQueue(req);
        } //https://maps.googleapis.com/maps/api/place/search/json?location=-33.8670522,151.1957362&radius=500&types=food&name=harbour&sensor=false&key=AIzaSyDdCsmiS9AT6MfFEWi_Vy87LJ0B2khZJME
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void putMeOnMap(MapComponent map) {
        try {
            Location loc = LocationManager.getLocationManager().getCurrentLocation();
            Coord lastLocation = new Coord(loc.getLatitude(), loc.getLongtitude());
            Image i = Image.createImage("/blue_pin.png");
            PointsLayer pl = new PointsLayer();
            pl.setPointIcon(i);
            PointLayer p = new PointLayer(lastLocation, "You Are Here", i);
            p.setDisplayName(true);
            pl.addPoint(p);
            map.addLayer(pl);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void stop() {
        System.out.println("stopped");
    }

    public void destroy() {
        System.out.println("destroyed");

    }

    class BackCommand extends Command {

        public BackCommand() {
            super("Back");
        }

        public void actionPerformed(ActionEvent evt) {
            main.showBack();
        }
    }
}
