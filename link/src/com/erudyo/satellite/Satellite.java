/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import com.codename1.io.CSVParser;
import com.codename1.io.Log;
import com.codename1.maps.MapComponent;
import com.codename1.processing.Result;
import com.codename1.ui.Display;
import com.codename1.ui.List;
import com.codename1.util.MathUtil;
import com.codename1.util.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Satellite extends Entity {

    private Antenna antenna;
    private Amplifier amplifier;

    // can have multiple bands and transponders;
    private Hashtable<RfBand.Band, Integer> transponders;

    // all satellites stored in semiMajor Class level hash.  For future, since
    // selection has its own at instance level
    static Hashtable<String, Satellite> satelliteHash
            = new Hashtable<String, Satellite>();

    // lookup by index with class level vector
    final public static ArrayList<Satellite> indexSatellite
            = new ArrayList<Satellite>();

    public Satellite() {
    }

    // return the number of transponders for a specific band
    public int getNumberTransponders(RfBand.Band band) {
        int num = 0;
        if (transponders == null) {
            Log.p("Satellite: transponders is null", Log.WARNING);
        }
        if (transponders.get(band) != null) {
            num = transponders.get(band);
        }
        return num;
    }

    public String toString() {
        return getName();
    }
    private double EIRP;       // they should be calculated
    private double gainTemp;       // they should be calculated dB 1/K
    protected double latitude;     // latitude
    protected double longitude;  // longitude
    protected double distanceEarthCenter;       // distance from center of Earth
    protected double h;       // altitude of satellite from sub-satellite point
    private double semiMajor;       // semi major axis
    protected double velocity;       // velocity
    protected double altitude;      // altitude
    protected Com.Orbit orbit;
    // protected RfBand.Band band; // now multiple bands in transponders

    private int index;

    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public enum ContourType {

        EIRP, GAIN_TEMP
    };

    // multiple multiGeo possible in a satellite
    // satellite -> Beam -> (label, MultiGeo->Contour)
    private class Beam {

        public String name;
        public int position;
        public int color;
        public double longitude;
        public double latitude;

        public ArrayList<Contour> contours;
    }

    // access a xCoord by its name in a hashtable (created from a file)
    Hashtable<String, Beam> beams;

    // multiple contours per xCoord (one of two types (EIRP, GAIN_TEMP)
    private class Contour {

        public int color;
        public int weight;
        public String name;
        public int position;
        public double latitude;
        public double longitude;
        public String EIRPname;
        public String gainName;
        public ContourType type;
        ArrayList<Line> lines;

    }

    private class Line {

        public Double coordinates[][];
        
        public int width;
        public int color;
    }

    private Hashtable<String, Beam> getBeamsFromFile(Satellite satellite) {
        Log.p("Satellite: getBeamsFromFile is trying to get beams for "
                + satellite, Log.DEBUG);
       Hashtable<String, Beam> beams = new Hashtable<String, Beam>();
        try {

            

            InputStream is = Display.getInstance().
                    getResourceAsStream(null, "/Amazonas 1 61W CTH Americas.kml");

            // Satellite has to read all the records from file.  Selection
            // could include only semiMajor subset per instance (e.g., satellites
            // visible from semiMajor location
            Result result = Result.fromContent(new InputStreamReader(is), Result.XML);

            // note that returns are full XML strings so until they are atomic, you
            // cannot use them as values
            String placeMarks[] = result.getAsStringArray("//PlaceMark/name");

            Beam beam = new Beam();

            int pos = 0;
            // perhaps only one beam in this file
            beam.name = result.getAsString(
                    "//Document/name");
            beam.position = pos++;
            beams.put(beam.name, beam);

            int contourPos = 0;

            for (String placeMark : placeMarks) {
                // get ready for new contour
                beam.contours = new ArrayList<Contour>();
                Contour contour = new Contour();
                contour.position = contourPos++;
                contour.name = result.getAsString(
                        "//PlaceMark/Style[@id='dbwlabel']/../name");

                contour.color = Integer.parseInt(
                        result.getAsString(
                                "//PlaceMark/Style[@id='dbwlabel']/LabelStyle/color").
                        substring(2, 7), 16);

                String coords = result.getAsString(
                        "//PlaceMark/Style[@id='dbwlabel']/../Point/Coordinates");

                String latLong[] = com.codename1.io.Util.split(coords, ",");

                contour.latitude = Double.parseDouble(latLong[0])
                        * Com.PI / 180.0;
                contour.longitude = Double.parseDouble(latLong[1])
                        * Com.PI / 180.0;
    
                beam.contours.add(contour);
                
                Log.p("Satellite: drawbeams created new contour # "
                        + contour.position, Log.DEBUG);

                // now get multiple instances of MultiGeometry, each with
                String[] multiGeo = result.getAsStringArray("//PlaceMark/"
                        + "[name='" + placeMark + "']"
                        + "/MultiGeometry");

                // now get the individual lines for this contour
                contour.lines = new ArrayList<Line>();

                for (String xCoord : multiGeo) {
                    Line line = new Line();

                    // parent contour gets the power level name
                    contour.EIRPname = placeMark; 
                    
                    String coordList = result.getAsString("//PlaceMark/"
                            + "[name='" + placeMark + "']"
                            + "/MultiGeometry"
                            + "/LineString/coordinates");

                    // split into an array since the whole placeMark is lat1,long2 lat2,long2
                    String coordinates[] = com.codename1.io.Util.split(coordList, " ");

                    // convert into two diemnstional integer
                    line.coordinates = new Double[coordinates.length][2];

                    // a placeMark is "" in the beginneing because of space
                    for (int i = 0, index = 0; i < coordinates.length; i++) {

                        // this is crazy. 
                        String s = coordinates[i];
                        if (s.length() != 0) {
                            String tokens[] = com.codename1.io.Util.split(s, ",");

                            line.coordinates[index][0]
                                    = Double.parseDouble(tokens[0]) * Com.PI / 180.0;
                            line.coordinates[index][1]
                                    = Double.parseDouble(tokens[1]) * Com.PI / 180.0;
                            index++;
                        }
                    }

                    // this is 8 bytes long so get rid of firs FF.  And note 16
                    line.color = Integer.parseInt(
                            result.getAsString("//PlaceMark/"
                                    + "[name='" + placeMark + "']"
                                    + "/Style/LineStyle/color").substring(2, 7), 16);

                    line.width = Integer.parseInt(
                            result.getAsString("//PlaceMark/"
                                    + "[name='" + placeMark + "']"
                                    + "/Style/LineStyle/width"));

                    // add it to the current contour
                    contour.lines.add(line);

                    Log.p("Satellite drawbeams processing segments "
                            + line.coordinates.length + " color "
                            + line.color + " width" + line.width, Log.DEBUG);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return beams;
    }

    public void drawBeams(MapComponent mc) {
        // TODO get the right file for a specific satellite (this)
        if (beams == null) {
            beams = getBeamsFromFile(this);
        } else {
            // now the multiGeo Hashtable is available so use it and 
        }
    }

    static {
        try {
            CSVParser parser = new CSVParser('|');

            InputStream is = Display.getInstance().
                    getResourceAsStream(null, "/all_satellites.txt");

            // Satellite has to read all the records from file.  Selection
            // could include only semiMajor subset per instance (e.g., satellites
            // visible from semiMajor location
            Satellite.getFromFile(
                    parser.parse(new InputStreamReader(is)));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Satellite(String name) {
        amplifier = new Amplifier();
        antenna = new Antenna();
        // gainTemp should be calculated when diameter changed
        antenna.setDiameter(2.4);

        this.name = name;       // should be unique

        setSemiMajor(42164.2E3);        //semi major axis of GEO orbit
        setVelocity(3075E3);        // GEO satellite velocity
        setAltitude(35786.1E3);       // height of GEO satellite
        setLatitude(0.0);         // latitude is zero

        orbit = Com.Orbit.GEO;

        amplifier.setPower(50);

        if (getName() == null) {
            Random randomGenerator = new Random();

            setName("S" + String.valueOf(randomGenerator.nextInt(10000)));
        }

        // add the new Satellite instance to the Hashtable at the class level
        Satellite.satelliteHash.put(getName(), this);

        // Add new object instance to the array list (all satellites)
        indexSatellite.add(this);

        index = indexSatellite.size() - 1;

    }

    public static void processBand(String[] fields, RfBand.Band band,
            Hashtable<RfBand.Band, ArrayList<Satellite>> bandSatellite,
            Satellite satellite, int index) {

        if (band == null) {
            Log.p("Satellite: bad data " + Arrays.toString(fields), Log.WARNING);
        } else {

            // extract the band of the terminal
            if (bandSatellite.get(band) == null) {
                bandSatellite.put(band, new ArrayList<Satellite>());
            }

            int num = 0;
            try {
                num = Integer.parseInt(fields[index + 1]);
            } catch (Exception e) {
                Log.p("Satellite: no number for transponders for satellite "
                        + Arrays.toString(fields), Log.DEBUG);
            }
            // put the number of transponders for this band
            satellite.transponders.put(band, num);

            // add satellite to this band
            bandSatellite.get(band).add(satellite);

        }

    }

    private static Hashtable<RfBand.Band, ArrayList<Satellite>> bandSatellite;

    // selection needs this
    public static Hashtable<RfBand.Band, ArrayList<Satellite>> getBandSatellite() {
        return bandSatellite;
    }

    public static void getFromFile(String[][] satellites) {

        // satellites contains values from the file.  Allow selection of an
        // vector of Satellite objects with band as the key in semiMajor hashtable
        bandSatellite
                = new Hashtable<RfBand.Band, ArrayList<Satellite>>();

        // go through all satellites
        int i = 0;
        int comms = 0;
        try {

            for (i = 1; i < satellites.length; i++) {

                Log.p("Satellite: processing satellite # "
                        + i + " " + satellites[i][0],
                        Log.INFO);
                if (!satellites[i][5].equals("Communications")) {
                    Log.p("Satellite: " + satellites[i][0] + "  NOT Communications",
                            Log.DEBUG);
                    continue;
                }

                comms++;
                satelliteFields(satellites[i], bandSatellite);
            }
        } catch (Exception e) {
            Log.p("Satellite: error at txt file line "
                    + String.valueOf(i), Log.WARNING);
        }

        Log.p("Satellite: processed " + comms + " Communications satellites"
                + "out of total " + String.valueOf(i - 1), Log.INFO);

    }

    public static void satelliteFields(String[] fields, Hashtable<RfBand.Band, ArrayList<Satellite>> bandSatellite) {
//            ArrayList<Satellite> vector) {
//Name 1|Name of Satellite, Alternate Names 2|Country of Operator/Owner 3|Operator
//Owner 4|Users 5|Purpose 6|Class of Orbit 7|Type of Orbit 8|Longitude of GEO (de
//grees) 9|Perigee (km) 10|Apogee (km) 11|Eccentricity 12|Inclination (degrees) 13
//|Period (minutes) 14|Launch Mass (kg.) 15|Dry Mass (kg.) 16|Power (watts) 17|Dat
//e of Launch 18|Expected Lifetime 19|Contractor 20|Country of Contractor 21|Launc
//h Site 22|Launch Vehicle 23|COSPAR Number 24|NORAD Number 25|Comments 26| 27|Sou
// rce Used for Orbital Data 28|Source1 29|Source2 30|Source3 31|Source4 32|Source5
// 33|Source6 34|EIRP 35|Gain 36|BAND1 37|Transponders1 38|BAND2 39|Transponders2 
// 40|BAND3 41|Transponders3 42

        // vector has already been created for semiMajor band, just add entries
        Satellite satellite = new Satellite(fields[0]);

        // fields are name, long, lat, eirp, gainTemp, band
        try {
            satellite.setLongitude(Math.toRadians(Double.parseDouble(fields[8])));

            // select only GEO satellites
            satellite.setLatitude(0.0);

            satellite.setEIRP(Double.parseDouble(fields[34]));
            satellite.setGainTemp(Double.parseDouble(fields[35]));

        } catch (Exception e) {
            Log.p("Satellites: double error in Long,Lat,EIRP, or Gain "
                    + fields, Log.WARNING);
        }
        if (satellite.transponders == null) {
            satellite.transponders = new Hashtable<RfBand.Band, Integer>();
        }

        // put the band and number of transponders
        // process C, KU, and KA (they are in the order in all_satellites.txt
        if (!(fields[36].equals("")) && RfBand.rFbandHash.get(
                fields[36].toUpperCase()).getBand()
                == RfBand.Band.C) {

            processBand(fields, RfBand.Band.C, bandSatellite, satellite, 36);
        }
        if (!(fields[38].equals("")) && RfBand.rFbandHash.get(
                fields[38].toUpperCase()).getBand()
                == RfBand.Band.KU) {
            processBand(fields, RfBand.Band.KU, bandSatellite, satellite, 38);
        }
        if (!(fields[40].equals("")) && RfBand.rFbandHash.get(
                fields[40].toUpperCase()).getBand()
                == RfBand.Band.KA) {
            processBand(fields, RfBand.Band.KA, bandSatellite, satellite, 40);
        }

    }

    public double getEIRP() {
        return 55;
    }

    public double maxCoverage() {
        double angle;
        angle = MathUtil.asin(Com.RE / (Com.RE + this.altitude));
        return angle;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the distanceEarthCenter
     */
    public double getDistanceEarthCenter() {
        return distanceEarthCenter;
    }

    /**
     * @param r the distanceEarthCenter to set
     */
    public void setDistanceEarthCenter(double r) {
        this.distanceEarthCenter = r;
    }

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the semiMajor
     */
    public double getSemiMajor() {
        return semiMajor;
    }

    /**
     * @return the velocity
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * @param v the velocity to set
     */
    public void setVelocity(double v) {
        this.velocity = v;
    }

    /**
     * @return the altitude
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * @param R0 the altitude to set
     */
    public void setAltitude(double R0) {
        this.altitude = R0;
    }

    public Antenna getAntenna() {
        return antenna;
    }

    /**
     * @return the amplifier
     */
    public Amplifier getAmplifier() {
        return amplifier;
    }

    /**
     * @param amplifier the amplifier to set
     */
    public void setAmplifier(Amplifier amplifier) {
        this.amplifier = amplifier;
        updateAffected();
    }

    /**
     * @param antenna the antenna to set
     */
    public void setAntenna(Antenna antenna) {
        this.antenna = antenna;
        updateAffected();
    }

    /**
     * @param EIRP the EIRP to set
     */
    public void setEIRP(double EIRP) {
        this.EIRP = EIRP;
        updateAffected();
    }

    /**
     * @return the gainTemp
     */
    public double getGainTemp() {
        return gainTemp;
    }

    /**
     * @param gain the gainTemp to set
     */
    public void setGainTemp(double gain) {
        this.gainTemp = gain;
        updateAffected();
    }

    /**
     * @param semiMajor the semiMajor to set
     */
    public void setSemiMajor(double semiMajor) {
        this.semiMajor = semiMajor;
    }

}
