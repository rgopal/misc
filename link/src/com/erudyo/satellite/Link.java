package com.erudyo.satellite;


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

    private Form current;

    public void init(Object context) {
        try {
            Resources theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
        } catch(IOException e){
            e.printStackTrace();
        }
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
        if(current != null){
            current.show();
            return;
        }
        Antenna antenna = new Antenna();
        
        Form hi = new Form("Hi World");
        Object[][] m = new Object[5][3];
        m[0][0] = "Transmitter";
        m[1][0] = "Path";
        m[2][0] = "Satellite";
        m[3][0] = "Path";
        m[4][0] = "Receiver";
        String[] names = {"item", "value", "units"};
        Table table = new Table(new DefaultTableModel(names, m, true));
        hi.addComponent (table);
        antenna.setDiameter(1);
        antenna.setFrequency(12E9);
        GeoSatellite g = new GeoSatellite(); g.setLongitude(99.0); g.setLatitude(0.0);
        Terminal t = new Terminal(); t.setLatitude(30);
        Path p = new Path(g,t); System.out.println (p.getAzimuth());
        hi.addComponent(new Label(antenna.getBand().toString() + antenna.getThreeDBangle()));
        WebBrowser browser = new WebBrowser();
        
        hi.addComponent(browser);
        browser.setURL("jar:///satellite.html");
       
        hi.show();
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }

}
