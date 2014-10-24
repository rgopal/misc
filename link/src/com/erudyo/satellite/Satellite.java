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
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Satellite extends Entity {

    private Antenna rXantenna;
    private Antenna tXantenna;
    private Amplifier rXamplifier;
    private Amplifier tXamplifier;

    private double maxEIRP = 0.0;      // in dBW
    private double maxGT = 0.0;        // in dB 1/K
    private Beam maxEIRPbeam;
    private Beam maxGTbeam;

    private static Hashtable<String, ArrayList<String>> satBeamFile;

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

    private double tempGround = 45.0;
    private double tempSky = 20.0;  // in K, depends on frequency
    private double polarizationLoss;

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

    /**
     * @return the tempGround
     */
    public double getTempGround() {
        return tempGround;
    }

    /**
     * @param tempGround the tempGround to set
     */
    public void setTempGround(double tempGround) {
        this.tempGround = tempGround;
    }

    /**
     * @return the tempSky
     */
    public double getTempSky(RfBand.Band band) {
        double temp = tempSky;
        if (band != RfBand.Band.C) {
            Log.p("Satellite: no tempSky for band " + band, Log.WARNING);
        }
        return temp;
    }

    /**
     * @param tempSky the tempSky to set
     */
    public void setTempSky(double tempSky) {
        this.tempSky = tempSky;
    }

    /**
     * @return the polarizationLoss
     */
    public double getPolarizationLoss() {
        return polarizationLoss;
    }

    /**
     * @param polarizationLoss the polarizationLoss to set
     */
    public void setPolarizationLoss(double polarizationLoss) {
        this.polarizationLoss = polarizationLoss;
    }

    /**
     * @return the tXantenna
     */
    public Antenna getTxAntenna() {
        return tXantenna;
    }

    /**
     * @param tXantenna the tXantenna to set
     */
    public void setTxAntenna(Antenna tXantenna) {
        this.tXantenna = tXantenna;
        updateAffected();

    }

    /**
     * @return the tXamplifier
     */
    public Amplifier getTxAmplifier() {
        return tXamplifier;
    }

    /**
     * @param tXamplifier the tXamplifier to set
     */
    public void setTxAmplifier(Amplifier tXamplifier) {
        this.tXamplifier = tXamplifier;
        updateAffected();
    }

    /**
     * @return the maxEIRPfromContours
     */
    public double getMaxEIRPfromContours() {
        return maxEIRP;
    }

    /**
     * @param maxEIRPfromContours the maxEIRPfromContours to set
     */
    public void setMaxEIRPfromContours(double maxEIRPfromContours) {
        this.maxEIRP = maxEIRPfromContours;
    }

    /**
     * @return the maxGTfromContours
     */
    public double getMaxGTfromContours() {
        return maxGT;
    }

    /**
     * @param maxGTfromContours the maxGTfromContours to set
     */
    public void setMaxGTfromContours(double maxGTfromContours) {
        this.maxGT = maxGTfromContours;
    }

    public enum ContourType {

        EIRP, GAIN_TEMP
    };

    // satellite has multiple beams
    // A beam has multiple placemarks (placemark has name)
    // A placemark could be a point or a contour
    // A placemark could be of type EIRP or GAIN
    // A contour has multiple lines
    public class Beam {

        public String name;
        public int position;
        public double maxEIRP;
        public Contour maxEIRPcontour;
        public double maxGT;
        public Contour maxGTcontour;

        public ArrayList<Point> points;  // one or more points (EIRP, GAIN)
        public ArrayList<Contour> contours;
    }

    // access a lineString by its name in a hashtable (created from a file)
    Hashtable<String, Beam> beams;

    // multiple contours per lineString (one of two types (EIRP, GAIN_TEMP)
    public class Contour {

        public int color;
        public int width;
        public String name;
        public double EIRP;
        public double GT;
        public int position;
        public ContourType type;
        ArrayList<Line> lines;
    }

    public class Point {

        public String name;
        public int position;
        public int color;
        public double latitude;     // in radians
        public double longitude;    // in radians
        public ContourType type;   // share type with contour
    }

    public class Line {

        public String altitudeMode;    // for future
        public ArrayList<Double> latitude;   // in radians
        public ArrayList<Double> longitude;
        public int position;

    }

    private Beam getBeamFromFile(Satellite satellite, String file) {
        Log.p("Satellite: getBeamsFromFile is trying to get beams for "
                + satellite, Log.DEBUG);
        Beam beam = new Beam();
        Hashtable<String, Beam> beams = new Hashtable<String, Beam>();
        try {

            InputStream is = Display.getInstance().
                    getResourceAsStream(null, file);

            Result result = Result.fromContent(new InputStreamReader(is), Result.XML);

            // note that Result returns are full XML strings so until they are atomic, you
            // cannot directly use them as String
            // get all PlaceMarks (could be point or contours)
            String placeMarks[] = result.getAsStringArray("//PlaceMark");

            // PROBLEMS.  Tried position() and it will be stuck at 0.
            // can't use name (since there are duplicates
            // subResults add single quotes and there are empty strings when
            // tokenizing for coordinates.   StringUtil does not take regex
            // so a lot of time spent in debugging all this
            int posBeam = 0;

            // perhaps only one beam in this file
            beam.name = result.getAsString(
                    "//Document/name");

            int contourPos = 0;
            int pointPos = 0;

            // get ready for new contour or point
            beam.contours = new ArrayList<Contour>();
            beam.points = new ArrayList<Point>();

            int placePos = 1;
            // placeMark could be a point or a contour
            for (String placeMark : placeMarks) {

                // find the type of this placeMark
                String pointExists = null;
                pointExists
                        = Result.fromContent(placeMark, Result.XML).
                        getAsString("//MultiGeometry");

                if (pointExists == null) {
                    Log.p("Satellite: getBeamFromFile creating new point # "
                            + pointPos, Log.DEBUG);
                    Point point = new Point();
                    beam.points.add(point);

                    point.position = pointPos++;
                    point.name = Com.removeQuoteEol(Result.fromContent(placeMark, Result.XML).
                            getAsString("//name"));

                    point.color = Integer.parseInt(
                            Result.fromContent(placeMark, Result.XML).
                            getAsString("//Style/LabelStyle/color").
                            substring(2, 7), 16);

                    String coords = Com.removeNonNum(Result.fromContent(placeMark, Result.XML).
                            getAsString("//Point/coordinates"));

                    String latLong[] = com.codename1.io.Util.split(coords,
                            ",");

                    point.longitude = Double.parseDouble(latLong[0])
                            * Com.PI / 180.0;
                    point.latitude = Double.parseDouble(latLong[1])
                            * Com.PI / 180.0;

                } else {

                    // process this as contour
                    Log.p("Satellite: getBeamFromFile creating new contour # "
                            + contourPos, Log.DEBUG);

                    Contour contour = new Contour();
                    contour.position = contourPos++;
                    beam.contours.add(contour);

                    // parent contour gets the power level name
                    contour.name = Com.removeQuoteEol(Result.fromContent(placeMark, Result.XML).
                            getAsString("//name"));

                    // space separated value and string dBW (but "" get added by split)
                    String nameTokens[] = com.codename1.io.Util.split(contour.name, " ");

                    // check if both the tokens are numbers
                    int nameInd = 0;
                    try {

                        for (; nameInd < nameTokens.length; nameInd++) {
                            // first item is ""  so get number from second item
                            if (nameTokens[nameInd].length() != 0) {
                                break;
                            }

                        }

                        contour.EIRP = Double.parseDouble(nameTokens[nameInd]);
                        // and the next item after number is dBW string
                        if ((nameInd < nameTokens.length - 1)
                                && (nameTokens[nameInd + 1].toUpperCase().equals("DBW"))) {
                            contour.type = ContourType.EIRP;
                        } else {
                            contour.type = ContourType.GAIN_TEMP;
                        }
                    } catch (NumberFormatException nfe) {
                        Log.p("Satellite: KML bad number in " + contour.name,
                                Log.DEBUG);
                        // note that index is not incremented
                    }
                    // update current maximum for the beam (EIRP or GT)
                    if (contour.type == ContourType.EIRP) {
                        if (contour.EIRP > beam.maxEIRP) {
                            beam.maxEIRP = contour.EIRP;
                            beam.maxEIRPcontour = contour;
                        }
                    } else {
                        if (contour.GT > beam.maxGT) {
                            beam.maxGT = contour.GT;
                            beam.maxGTcontour = contour;
                        }
                    }

                    // this is 8 bytes long so get rid of firs FF.  And note 16
                    contour.color = Integer.parseInt(Com.SwapBlueRed(
                            Result.fromContent(placeMark, Result.XML).
                            getAsString("//Style/LineStyle/color").substring(2, 7)), 16);

                    contour.width = Integer.parseInt(
                            Com.removeNonNum(Result.fromContent(placeMark, Result.XML).
                                    getAsString("//Style/LineStyle/width")));

                    // now get multiple instances of MultiGeometry, each with
                    String[] lineStrings = Result.fromContent(placeMark, Result.XML).
                            getAsStringArray("//MultiGeometry/LineString");

                    // now get the individual lines for this contour
                    contour.lines = new ArrayList<Line>();

                    int linePos = 0;
                    for (String lineString : lineStrings) {
                        Line line = new Line();
                        contour.lines.add(line);
                        line.position = linePos++;
                        line.altitudeMode = Result.fromContent(lineString, Result.XML).
                                getAsString("//altitudeMode");

                        String coordList = Result.fromContent(lineString, Result.XML).
                                getAsString("//coordinates");

                        // split into an array since the whole placeMark is lat1,long2 lat2,long2
                        String coordinates[] = com.codename1.io.Util.split(coordList, " ");

                        // convert into two diemnstional integer
                        line.longitude = new ArrayList<Double>();
                        line.latitude = new ArrayList<Double>();

                        // a placeMark is "" in the beginneing because of space
                        for (int i = 0, index = 0; i < coordinates.length; i++) {

                            // this is crazy. 
                            String s = coordinates[i];
                            if (s.length() != 0) {

                                String tokens[] = com.codename1.io.Util.split(s, ",");

                                // check if both the tokens are numbers
                                try {

                                    double l, g;
                                    g = Double.parseDouble(tokens[0]) * Com.PI / 180.0;
                                    l = Double.parseDouble(tokens[1]) * Com.PI / 180.0;

                                    line.latitude.add(l);
                                    line.longitude.add(g);

                                    index++;
                                } catch (NumberFormatException nfe) {
                                    Log.p("Satellite: KML bad number at " + i, Log.DEBUG);
                                    // note that index is not incremented
                                }

                            }
                        }

                        Log.p("Satellite drawbeams processed  "
                                + line.latitude.size() + " segments ", Log.DEBUG);
                    }
                }

                // increment the index to get item from XML
                placePos++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return beam;
    }

    public Hashtable<String, Beam> getBeams() {

        // maybe the beams were already read from the files
        if (beams != null) {
            return beams;
        } else {

            int posBeam = 0;

            String[] beamFiles = satBeamFile.get(this.name).toArray(new String[0]);
            if (beamFiles == null) {
                return null;
            }

            // read the beam files and populate beams member
            for (String beamFile : beamFiles) {
                if (beams == null) {
                    beams = new Hashtable<String, Beam>();
                }
                Beam beam = getBeamFromFile(this, "/" + beamFile);
                beam.position = posBeam++;
                beams.put(beam.name, beam);
                Log.p("Satellite: got beam for " + this
                        + " at position " + posBeam + " from file "
                        + beamFile, Log.DEBUG);
                double current = beam.maxEIRP;
                if (current > maxEIRP) {
                    maxEIRP = current;
                }
                current = beam.maxGT;
                if (current > maxGT) {
                    maxGT = current;
                }
            }
            return beams;
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
            Log.p("Satellite: static{} reading file all_satellites.txt", Log.DEBUG);
            Satellite.getFromFile(
                    parser.parse(new InputStreamReader(is)));

            Log.p("Satellite: static reading file satellite_beams", Log.DEBUG);

            is = Display.getInstance().
                    getResourceAsStream(null, "/satellite_beams.txt");
            satBeamFile = new Hashtable<String, ArrayList<String>>();

            String satBeams[][] = parser.parse(new InputStreamReader(is));

            String oldSat = "", sat, beam, file;
            for (int i = 0; i < satBeams.length; i++) {
                sat = satBeams[i][0];
                beam = satBeams[i][1];
                file = satBeams[i][2];

                if (!sat.equalsIgnoreCase(oldSat)) {
                    // new satellite so create a new list of beams
                    satBeamFile.put(sat, new ArrayList<String>());
                    oldSat = sat;
                }
                satBeamFile.get(sat).add(file);  // add the file
                Log.p("Satellite: added file " + file + " for sat|beam "
                        + sat + "|" + beam, Log.DEBUG);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void init(String name) {

        this.name = name;       // should be unique

        rXantenna = new Antenna();
        rXantenna.setDiameter(2.4);
        rXantenna.setName("RxAnt" + this.name);
        rXantenna.addAffected(this);

        tXantenna = new Antenna();
        tXantenna.setDiameter(2.4);
        tXantenna.setName("TxAnt" + this.name);
        tXantenna.addAffected(this);

        rXamplifier = new Amplifier();
        rXamplifier.setName("RxAmp" + this.name);
        rXamplifier.setPower(50.0);
        // set the power prior to caling affected (most objects are empty)
        rXamplifier.addAffected(this);

        tXamplifier = new Amplifier();
        tXamplifier.setName("TxAmp" + this.name);
        tXamplifier.setPower(100.0);
        // set everything before affected call.
        tXamplifier.addAffected(this);
        // nasty bug.  SHould be double format

        setSemiMajor(42164.2E3);        //semi major axis of GEO orbit
        setVelocity(3075E3);        // GEO satellite velocity
        setAltitude(35786.1E3);       // height of GEO satellite
        setLatitude(0.0);         // latitude is zero

        orbit = Com.Orbit.GEO;

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
        Satellite satellite = new Satellite();
        // race condition?
        satellite.init(fields[0]);

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

    // uplink system noise temperature at the receiver input given by
    public double calcSystemNoiseTemp() {
        double tA;
        double noiseTemp;
        double teRX;
        // noise figure is in dB
        teRX = (MathUtil.pow(10.0, this.rXamplifier.getNoiseFigure() / 10.0)
                - 1.0) * Com.T0;
        tA = this.getTempSky(getRxAntenna().getBand())
                + this.getTempGround();

        // LFRX is in dB so change
        double lfrx = MathUtil.pow(10.0, this.getRxAmplifier().getLFRX() / 10.0);

        noiseTemp = tA / lfrx
                + this.getRxAmplifier().getFeederTemp()
                * (1.0 - 1.0 / lfrx) + teRX;

        return noiseTemp;
    }

    public double getEIRP() {
        return this.EIRP;
    }

    private class Hierarchy {

        public Beam beam;
        public Contour contour;
        public double value;   // could be EIRP or GainTemp
    }

    public double getEIRPforTerminal(Terminal terminal) {
        // TODO use terminal's location to adjust EIRP
        Log.p("Satellite: getEIRPforTerminal EIRP = " + getEIRP() + " max EIRP "
                + "from beam = " + this.maxEIRP, Log.DEBUG);

        if (beams != null) {
            // sort the contours by EIRP across all beams
            ArrayList<Hierarchy> beamEIRP
                    = new ArrayList<Hierarchy>();
            // get all EIRP values for each beam and each contour
            for (Beam beam : beams.values()) {
                for (Contour contour : beam.contours) {
                    Hierarchy hier = new Hierarchy();
                    hier.beam = beam;
                    hier.contour = contour;
                    hier.value = contour.EIRP;
                    beamEIRP.add(hier);
                }

            }
            // now sort with descending EIRP.  

            Collections.sort(beamEIRP, new Comparator<Hierarchy>() {
                @Override
                public int compare(Hierarchy one, Hierarchy two) {

                    return (int) (two.value - one.value);

                }

            });

            // find the  EIRP of contour, starting with higest, that contains terminal 
            for (Hierarchy hier : beamEIRP) {
                // get the contour details first
                Contour contour = hier.contour;

                // consider all lines (
                for (Line line : contour.lines) {

                    if (pointInPolygon(terminal.getLatitude(), terminal.getLongitude(),
                            line.latitude.toArray(new Double[0]),
                            line.longitude.toArray(new Double[0]))) {
                        Log.p("Satellite getEIRPforTerminal " + terminal
                                + " found in beam " + hier.beam.name + " contour "
                                + contour.name + " lines " + line.position, Log.DEBUG);
                        return contour.EIRP;

                    }

                }

            }

        } else {
            return getEIRP();
        }
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

    public Antenna getRxAntenna() {
        return rXantenna;
    }

    /**
     * @return the rXamplifier
     */
    public Amplifier getRxAmplifier() {
        return rXamplifier;
    }

    /**
     * @param amplifier the rXamplifier to set
     */
    public void setRxAmplifier(Amplifier amplifier) {
        this.rXamplifier = amplifier;
        updateAffected();
    }

    /**
     * @param antenna the rXantenna to set
     */
    public void setRxAntenna(Antenna antenna) {
        this.rXantenna = antenna;
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

    public double getGainTempForTerminal(Terminal terminal) {
        // TODO use the G/T contours and terminal's location to adjust
        Log.p("Satellite: getGainTempForTerminal max GT " + this.maxGT);
        return getGainTemp();
    }

    /**
     * @param gain the gainTemp to set
     */
    public void setGainTemp(double gain) {
        this.gainTemp = gain;
        updateAffected();  // for parents
    }

    /**
     * @param semiMajor the semiMajor to set
     */
    public void setSemiMajor(double semiMajor) {
        this.semiMajor = semiMajor;
    }

    // uses Transmit antenna
    private double calcEIRP() {
        // rXamplifier was in dB others 
        double eirp = (10 * MathUtil.log10(
                this.getTxAmplifier().getPower()))
                + this.getTxAntenna().getGain()
                - this.getTxAntenna().getDepointingLoss()
                - this.getTxAmplifier().getLFTX();
        return eirp;
    }

    // users receive antenna
    private double calcGainTemp() {

        double gain;

        // rXantenna gain is already in dB
        gain = this.getRxAntenna().getGain()
                - this.getRxAntenna().calcDepointingLoss()
                - this.getRxAmplifier().getLFRX()
                - this.getPolarizationLoss()
                - 10.0 * MathUtil.log10(calcSystemNoiseTemp());
        return gain;
    }

    public void update(Entity e) {

        // update everything that could be affected
        // EIRP depends on rXantenna and rXamplifier, but both need to exist 
        if (this.getRxAmplifier() != null && this.getRxAntenna() != null
                && this.getTxAntenna() != null) {
            this.EIRP = calcEIRP();
            this.gainTemp = calcGainTemp();
            // updateAffected();   should be called automatically

        } else {
            Log.p("Satellite: amplifier, Tx antenna or Rx antenna is null for "
                    + this, Log.DEBUG);
        }

        // avoid using set since that should be used to send updates down
    }

    // used to find point inside a polygon
    private class LatLng {

        public double latitude;    //
        public double longitude;    // 

    }

    public boolean pointInPolygon(double lat, double lng,
            Double[] latitude, Double[] longitude) {
        // ray casting alogrithm http://rosettacode.org/wiki/Ray-casting_algorithm
        int crossings = 0;
        ArrayList<LatLng> path = new ArrayList<LatLng>();
        int size = 0;

        LatLng point = new LatLng();
        point.latitude = lat;
        point.longitude = lng;
        if (latitude.length != longitude.length) {
            Log.p("Com: pointInPolygon different size for lat|lng "
                    + latitude.length + "|" + longitude.length, Log.DEBUG);
            size = latitude.length < longitude.length
                    ? latitude.length : longitude.length;
        }
        // path.remove(path.size()-1); //remove the last point that is added automatically by getPoints()

        for (int i = 0; i < size; i++) {
            LatLng ll = new LatLng();
            ll.latitude = latitude[i];
            ll.longitude = longitude[i];

            path.add(ll);
        }
        // for each edge
        for (int i = 0; i < path.size(); i++) {
            LatLng a = path.get(i);
            int j = i + 1;
            //to close the last edge, you have to take the first point of your polygon
            if (j >= path.size()) {
                j = 0;
            }
            LatLng b = path.get(j);
            if (rayCrossesSegment(point, a, b)) {
                crossings++;
            }
        }

        // odd number of crossings?
        return (crossings % 2 == 1);
    }

    public boolean rayCrossesSegment(LatLng point, LatLng a, LatLng b) {
        // Ray Casting algorithm checks, for each segment, if 
        //the point is 1) to the left of the segment and 2) not above nor below 
        // the segment. If these two conditions are met, it returns true
        double px = point.longitude,
                py = point.latitude,
                ax = a.longitude,
                ay = a.latitude,
                bx = b.longitude,
                by = b.latitude;
        if (ay > by) {
            ax = b.longitude;
            ay = b.latitude;
            bx = a.longitude;
            by = a.latitude;
        }
        // alter longitude to cater for 180 degree crossings
        if (px < 0 || ax < 0 || bx < 0) {
            px += 360;
            ax += 360;
            bx += 360;
        }

        // if the point has the same latitude as a or b, increase slightly py
        if (py == ay || py == by) {
            py += 0.00000001;
        }

        // if the point is above, below or to the right of the segment, it returns false
        if ((py > by || py < ay) || (px > Math.max(ax, bx))) {
            return false;
        } // if the point is not above, below or to the right and is to the left, return true
        else if (px < Math.min(ax, bx)) {
            return true;
        } // if the two above conditions are not met, you have to compare the slope of segment [a,b] (the red one here) and segment [a,p] (the blue one here) to see if your point is to the left of segment [a,b] or not
        else {
            double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.POSITIVE_INFINITY;
            double blue = (ax != px) ? ((py - ay) / (px - ax)) : Double.POSITIVE_INFINITY;
            return (blue >= red);
        }

    }
}
