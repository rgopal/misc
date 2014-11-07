/*
 * 
 * OVERVIEW
 * 
 * Satellite class comprises location and if available band specific antenna
 * and amplifier components.  Beam specific KML files can be read on demand
 * if a satellite with available files is selected.  Satellites are grouped
 * by their band capability and only a subset is shown (selected band) at a
 * time.  If a terminal is located in the beam coverage area then that is used
 * for G/T and EIRP numbers.  If KML files are not present then user can 
 * change amplifier power and antenna size to get the desired values.  Generically
 * maximum (calculated or from contours) EIRP and G/T are displayed.
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
// satellite hierarchy is as follows: antenna and amplifier are for rx and
// tx and used for interactive calculations only.
// If the beam files are available EIRP and/or Gain then they are used for calcs
// 
// A beam has multiple contours. Each beam supports a specific band and has multiple
// transponders.  Each contour could be for EIRP or GT type
// 
// A beam also has multiple transponders.   
public class Satellite extends Entity {

    public final static double NEGLIGIBLE = -100.0;

    // names of files containing KML info for some satellites
    private static Hashtable<String, Hashtable<RfBand.Band, ArrayList<String>>> satBandBeamFile;

    public Hashtable<RfBand.Band, BandSpecificItems> bandSpecificItems;

    // all satellites stored in Class level hash.
    // selection has its own at instance level
    static Hashtable<String, Satellite> satelliteHash
            = new Hashtable<String, Satellite>();

    // lookup by index with class level vector
    final public static ArrayList<Satellite> indexSatellite
            = new ArrayList<Satellite>();

    protected double latitude;     // latitude
    protected double longitude;  // longitude
    protected double distanceEarthCenter;       // distance from center of Earth
    protected double h;       // altitude of satellite from sub-satellite point
    private double semiMajor;       // semi major axis
    protected double velocity;       // velocity
    protected double altitude;      // altitude
    protected Com.Orbit orbit;
    // protected RfBand.Band band; // now multiple bands in Beam

    private double tempGround = 45.0;
    private double tempSky = 20.0;  // in K, depends on frequency
    private double polarizationLoss;

    private int index;

    public enum ContourType {

        EIRP, GAIN_TEMP
    };

    // satellite has multiple beams of a specific band (child of BandBeam)
    // A beam has multiple placemarks (placemark has name)
    // A placemark could be a point or a contour
    // A placemark could be of type EIRP or GAIN
    // A contour has multiple lines
    public class Beam {

        public String name;

        public int position;
        public double maxEIRP = NEGLIGIBLE; // maximum across all contours within a beam
        public Contour maxEIRPcontour;
        public double maxGT = NEGLIGIBLE;
        public Contour maxGTcontour;

        public ArrayList<Point> points;  // one or more points (EIRP, GAIN)
        public ArrayList<Contour> contours;
    }

    // access a lineString by its name in a hashtable (created from a file)
    // now a part of BandBeams - Hashtable<String, Beam> beams;
    // multiple contours per lineString (one of two types (EIRP, GAIN_TEMP)
    public class Contour {

        public int color;
        public int width;
        public String name;
        public double EIRP = NEGLIGIBLE;
        public double GT = NEGLIGIBLE;
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

    public Satellite() {
    }

    // return the number of transponders for a specific band
    // used when no contours are present
    public int getNumberTransponders(RfBand.Band band) {
        int num = 0;
        if (bandSpecificItems == null) {
            Log.p("Satellite: bandBeams is null", Log.WARNING);
        }

        num = bandSpecificItems.get(band).transponders;

        return num;
    }

    public String toString() {
        return getName();
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
    public Antenna getTxAntenna(RfBand.Band band) {
        return bandSpecificItems.get(band).tXantenna;
    }

    /**
     * @param tXantenna the tXantenna to set
     */
    public void setTxAntenna(Antenna tXantenna, RfBand.Band band) {
        bandSpecificItems.get(band).tXantenna = tXantenna;
        updateAffected();

    }

    /**
     * @return the tXamplifier
     */
    public Amplifier getTxAmplifier(RfBand.Band band) {
        return bandSpecificItems.get(band).tXamplifier;
    }

    /**
     * @param tXamplifier the tXamplifier to set
     */
    public void setTxAmplifier(Amplifier tXamplifier, RfBand.Band band) {
        bandSpecificItems.get(band).tXamplifier = tXamplifier;
        updateAffected();
    }

    /**
     * @return the maxEIRPfromContours
     */
    public double getMaxEIRPfromContours(RfBand.Band band) {

        // if there are beam contours then use them else 
        if (bandSpecificItems != null && bandSpecificItems.get(band) != null) {
            return bandSpecificItems.get(band).maxEIRP;
        }
        return NEGLIGIBLE;
    }

    /**
     * @return the maxGTfromContours
     */
    public double getMaxGTfromContours(RfBand.Band band) {
        // if contours use them else return band level value
        if (bandSpecificItems != null && bandSpecificItems.get(band) != null) {
            return bandSpecificItems.get(band).maxGT;
        }
        return NEGLIGIBLE;
    }

    // gets beam contours from a file (already known that it exists)
    private Beam getBeamFromFile(Satellite satellite, String file) {
        Log.p("Satellite: getBeamsFromFile is trying to get beams for "
                + satellite, Log.DEBUG);
        Beam beam = new Beam();

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

            // get ready for new contour (EIRP or GT) or point
            beam.contours = new ArrayList<Contour>();
            beam.points = new ArrayList<Point>();

            int placePos = 1;   // used in XML processing
            // placeMark could be a point or a contour
            for (String placeMark : placeMarks) {

                // find the type of this placeMark
                String pointExists = null;
                pointExists
                        = Result.fromContent(placeMark, Result.XML).
                        getAsString("//MultiGeometry");

                if (pointExists == null) {
                    Log.p("  getBeamFromFile creating new point # "
                            + pointPos, Log.DEBUG);
                    Point point = new Point();
                    beam.points.add(point);

                    point.position = pointPos++;
                    // TODO differentiate between EIRP and GT points (similar to
                    // contours
                    point.name = Com.removeQuoteEol(Com.removeQuoteEol(
                            Result.fromContent(placeMark, Result.XML).
                            getAsString("//name")));

                    point.color = Integer.parseInt(
                            Com.removeQuoteEol(Result.fromContent(placeMark, Result.XML).
                                    getAsString("//Style/LabelStyle/color")).
                            substring(2, 8), 16);

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
                    Log.p("  getBeamFromFile creating new contour # "
                            + contourPos, Log.DEBUG);

                    Contour contour = new Contour();
                    contour.position = contourPos++;
                    beam.contours.add(contour);

                    // parent contour gets the power level name
                    contour.name = Com.removeQuoteEol(Result.fromContent(
                            placeMark, Result.XML).
                            getAsString("//name"));

                    // space separated value and string dBW (but "" get added by split)
                    String nameTokens[] = com.codename1.io.Util.split(contour.name, " ");

                    // find the token which is a number
                    int nameInd = 0;
                    try {
                        for (; nameInd < nameTokens.length; nameInd++) {
                            // first item is ""  so get number from second item
                            if (nameTokens[nameInd].length() != 0) {
                                break;
                            }
                        }
                        // and the next item after number is dBW (for EIRP) string
                        if ((nameInd < nameTokens.length - 1)
                                && (nameTokens[nameInd + 1].toUpperCase().equals("DBW"))) {
                            contour.type = ContourType.EIRP;
                            contour.EIRP = Double.parseDouble(nameTokens[nameInd]);
                        } else {
                            contour.type = ContourType.GAIN_TEMP;
                            contour.GT = Double.parseDouble(nameTokens[nameInd]);
                        }
                    } catch (NumberFormatException nfe) {
                        Log.p("  KML bad number in " + contour.name,
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
                            Com.removeQuoteEol(Result.fromContent(placeMark, Result.XML).
                                    getAsString("//Style/LineStyle/color")).substring(2, 8)), 16);

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
                        line.altitudeMode = Com.removeQuoteEol(
                                Result.fromContent(lineString, Result.XML).
                                getAsString("//altitudeMode"));

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
                                    Log.p("    Satellite: KML bad number at " + i, Log.DEBUG);
                                    // note that index is not incremented
                                }

                            }
                        }

                        Log.p("  getBeamFromFile processed  "
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

    // called from MapView
    public Hashtable<String, Beam> getBeams(Selection selection) {

        // find the beams for a specific band
        if (bandSpecificItems != null
                && bandSpecificItems.get(selection.getBand()) != null) {
            return bandSpecificItems.get(selection.getBand()).beams;
        }

        return null;
    }

    // called when a satellite is selected (setSatellite is called?)
    // so that terminal location can be processed independent of Maps
    public void readBeams(RfBand.Band band) {

        Hashtable<String, Beam> beams = null;

        // first check if beams are available for this band
        if (bandSpecificItems != null
                && bandSpecificItems.get(band) != null) {
            beams = bandSpecificItems.get(band).beams;
        }
        // maybe the beams were already read from the files, if not
        if (beams == null) {
            int posBeam = 0;

            if (satBandBeamFile == null) {
                Log.p("Satellite: readBeams satBeamFile is null for " + this
                        + " and band " + band,
                        Log.WARNING);
                return;
            }

            if (satBandBeamFile.get(this.name) == null) {
                Log.p("Satellite: readBeams no beam files for " + this
                        + " and band " + band,
                        Log.INFO);
                return;
            }
            // read the beam files and populate beams member
            // a check for null would not work so had to use size()
            if (satBandBeamFile.get(this.name).get(band).size() == 0) {
                Log.p("Satellite: readBeams no beam files for " + this
                        + " and band " + band,
                        Log.WARNING);
                return;
            } else {
                for (String beamFile : satBandBeamFile.get(this.name).get(band)) {
                    if (beams == null) {
                        beams = new Hashtable<String, Beam>();
                        if (bandSpecificItems == null) {
                            bandSpecificItems = new Hashtable<RfBand.Band, BandSpecificItems>();
                        }
                        if (bandSpecificItems.get(band) == null) {
                            bandSpecificItems.put(band, new BandSpecificItems());
                        }
                        bandSpecificItems.get(band).beams = beams;
                    }
                    Beam beam = getBeamFromFile(this, "/" + beamFile);
                    beam.position = posBeam++;
                    beams.put(beam.name, beam);
                    Log.p("Satellite: got a beam for " + this + " and band " + band
                            + " at position " + posBeam + " from file "
                            + beamFile, Log.DEBUG);

                    // update maximum (from contours) EIRP and GT for the band
                    if (beam.maxEIRP > bandSpecificItems.get(band).maxEIRP) {
                        bandSpecificItems.get(band).maxEIRP = beam.maxEIRP;
                    }

                    if (beam.maxGT > bandSpecificItems.get(band).maxGT) {
                        bandSpecificItems.get(band).maxGT = beam.maxGT;
                    }
                }
            }

        } else {
            Log.p("Satellite: readBeams already had beams for "
                    + this + " and band " + band, Log.DEBUG);
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

            satBandBeamFile = new Hashtable<String, Hashtable<RfBand.Band, ArrayList<String>>>();

            String satBeams[][] = parser.parse(new InputStreamReader(is));

            String oldSat = "", oldBand = "", sat, beam, file, band;
            for (int i = 0; i < satBeams.length; i++) {
                sat = satBeams[i][0];
                beam = satBeams[i][1];
                file = satBeams[i][2];
                band = satBeams[i][3];

                // assume this is sorted by satellite and band
                if (!sat.equalsIgnoreCase(oldSat)) {
                    // new satellite so create a new collections of bands
                    satBandBeamFile.put(sat, new Hashtable<RfBand.Band, ArrayList<String>>());
                    oldSat = sat;

                    // create a new entry for a band
                    ArrayList<String> aItems
                            = new ArrayList<String>();

                    // new band start a new collection of files
                    satBandBeamFile.get(sat).put(RfBand.rFbandHash.
                            get(band.toUpperCase()).getBand(), aItems);

                } else // create a new band item if band changes for same satellite
                if (!band.equalsIgnoreCase(oldBand)) {
                    ArrayList<String> bItems
                            = new ArrayList<String>();

                    // new band start a new collection of files
                    satBandBeamFile.get(sat).put(RfBand.rFbandHash.
                            get(band.toUpperCase()).getBand(), bItems);
                    oldBand = band;
                }

                satBandBeamFile.get(sat).get(RfBand.rFbandHash.
                        get(band.toUpperCase()).getBand()).add(file);  // add the file

                Log.p("Satellite: static{} added file " + file + " for sat|beam|band "
                        + sat + "|" + beam + "|" + band, Log.DEBUG);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // a satellite has dedicated antennas and amplifiers for each band
    // and the bands do not change (unlike terminals)
    public static void initAntAmp(Satellite satellite, RfBand.Band band, int num) {

        satellite.bandSpecificItems.get(band).transponders = num;

        satellite.bandSpecificItems.get(band).rXantenna = new Antenna();
        satellite.bandSpecificItems.get(band).rXantenna.setDiameter(1.2);
        satellite.bandSpecificItems.get(band).rXantenna.setName(
                "RxAnt" + band + satellite.name);
        satellite.bandSpecificItems.get(band).rXantenna.setBand(RfBand.findUl(band));
        satellite.bandSpecificItems.get(band).rXantenna.
                setFrequency(RfBand.centerFrequency(
                                RfBand.findUl(band)));
        satellite.bandSpecificItems.get(band).rXantenna.addAffected(satellite);

        satellite.bandSpecificItems.get(band).tXantenna = new Antenna();
        satellite.bandSpecificItems.get(band).tXantenna.setDiameter(2.4);
        satellite.bandSpecificItems.get(band).tXantenna.setBand(RfBand.findDl(band));
        satellite.bandSpecificItems.get(band).tXantenna.
                setFrequency(RfBand.centerFrequency(
                                RfBand.findDl(band)));
        satellite.bandSpecificItems.get(band).tXantenna.setName("TxAnt" + band + satellite.name);
        satellite.bandSpecificItems.get(band).tXantenna.addAffected(satellite);

        satellite.bandSpecificItems.get(band).rXamplifier = new Amplifier();
        satellite.bandSpecificItems.get(band).rXamplifier.setName("RxAmp" + band + satellite.name);
        satellite.bandSpecificItems.get(band).rXamplifier.setPower(50.0);

        // set the power prior to caling affected (most objects are empty)
        satellite.bandSpecificItems.get(band).rXamplifier.addAffected(satellite);

        satellite.bandSpecificItems.get(band).tXamplifier = new Amplifier();
        satellite.bandSpecificItems.get(band).tXamplifier.setName("TxAmp" + band + satellite.name);
        satellite.bandSpecificItems.get(band).tXamplifier.setPower(100.0);

        // set everything before affected call.
        satellite.bandSpecificItems.get(band).tXamplifier.addAffected(satellite);
        // nasty bug.  SHould be double format
    }

    public void init(String name) {

        this.name = name;       // should be unique

        this.bandSpecificItems = new Hashtable<RfBand.Band, BandSpecificItems>();

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
            Log.p("Satellite: processBand band is null "
                    + Arrays.toString(fields), Log.WARNING);
        } else {

            // extract the band of the terminal
            if (bandSatellite.get(band) == null) {
                bandSatellite.put(band, new ArrayList<Satellite>());
            }
            // if band specific items are present
            if (index > 0) {
                int num = 0;
                try {
                    num = Integer.parseInt(fields[index + 1]);
                } catch (Exception e) {
                    Log.p("Satellite: processBand no number for transponders for satellite "
                            + Arrays.toString(fields), Log.DEBUG);
                }

                if (satellite.bandSpecificItems.get(band) == null) {

                    satellite.bandSpecificItems.put(band, new BandSpecificItems());
                }
                // these are read from txt files and can be calculated and changed
                // unlike maxEIRP that is from contours
                satellite.bandSpecificItems.get(band).EIRP
                        = (Double.parseDouble(fields[index + 2]));
                satellite.bandSpecificItems.get(band).gainTemp
                        = (Double.parseDouble(fields[index + 3]));

                initAntAmp(satellite, band, num);
            }
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

        //Name 1|Name of Satellite, Alternate Names 2|Country of Operator/Owner 3|
        //Operator/Owner 4|Users 5|Purpose 6|Class of Orbit 7|Type of Orbit 8|
        //Longitude of GEO (degrees) 9|Perigee (km) 10|Apogee (km) 11|Eccentricity 12|
        //Inclination (degrees) 13|Period (minutes) 14|Launch Mass (kg.) 15|Dry Mass (kg.) 16|
        //Power (watts) 17|Date of Launch 18|Expected Lifetime 19|Contractor 20|
        //Country of Contractor 21|Launch Site 22|Launch Vehicle 23|COSPAR Number 24|
        //NORAD Number 25|Comments 26| 27|Source Used for Orbital Data 28|Source1 29|
        //Source2 30|Source3 31|Source4 32|Source5 33|Source6 34|BAND C 35|Transponders C 36|EIRP C 37|GT C 38|
        //BAND X 39|Transponders X 40|EIRP X 41|GT X 42|BAND Ku 43|Transponders Ku 44|
        //EIRP Ku 45|GT Ku 46|BAND Ka 47|Transponders Ka 48|EIRP Ka 49|GT Ka 50
        // vector has already been created for semiMajor band, just add entries
        Satellite satellite = new Satellite();
        // race condition?
        satellite.init(fields[0]);

        // fields are name, long, lat, eirp, gainTemp, band
        try {
            satellite.setLongitude(Math.toRadians(Double.parseDouble(fields[8])));

            // select only GEO satellites
            satellite.setLatitude(0.0);

        } catch (Exception e) {
            Log.p("Satellites: double error in Long,Lat,EIRP, or Gain "
                    + fields.toString(), Log.WARNING);
        }

        // put the band, number of transponders, EIRP, GT
        // process C, KU, and KA (they are in the order in all_satellites.txt
        if (!(fields[34].equals("")) && RfBand.rFbandHash.get(
                fields[34].toUpperCase()).getBand()
                == RfBand.Band.C) {

            processBand(fields, RfBand.Band.C, bandSatellite, satellite, 34);
        }
        if (!(fields[38].equals("")) && RfBand.rFbandHash.get(
                fields[38].toUpperCase()).getBand()
                == RfBand.Band.X) {

            processBand(fields, RfBand.Band.X, bandSatellite, satellite, 38);
        }
        if (!(fields[42].equals("")) && RfBand.rFbandHash.get(
                fields[42].toUpperCase()).getBand()
                == RfBand.Band.KU) {
            processBand(fields, RfBand.Band.KU, bandSatellite, satellite, 42);
        }
        if (!(fields[46].equals("")) && RfBand.rFbandHash.get(
                fields[46].toUpperCase()).getBand()
                == RfBand.Band.KA) {
            processBand(fields, RfBand.Band.KA, bandSatellite, satellite, 46);
        }
        // unknown band
        processBand(fields, RfBand.Band.UK, bandSatellite, satellite, 0);

    }

    // uplink system noise temperature at the receiver input given by
    public double calcSystemNoiseTemp(RfBand.Band band) {
        double tA;
        double noiseTemp;
        double teRX;
        // noise figure is in dB
        teRX = (MathUtil.pow(10.0, bandSpecificItems.get(band).rXamplifier.getNoiseFigure() / 10.0) - 1.0) * Com.T0;
        tA = this.getTempSky(bandSpecificItems.get(band).rXantenna.getBand())
                + this.getTempGround();

        // LFRX is in dB so change
        double lfrx = MathUtil.pow(10.0, bandSpecificItems.get(band).rXamplifier.getLFRX() / 10.0);

        noiseTemp = tA / lfrx
                + bandSpecificItems.get(band).rXamplifier.getFeederTemp()
                * (1.0 - 1.0 / lfrx) + teRX;

        return noiseTemp;
    }

    // this is the calculated EIRP for a band
    public double getEIRP(RfBand.Band band) {
        return bandSpecificItems.get(band).EIRP;
    }

    private class Hierarchy {

        public Beam beam;
        public Contour contour;
        public double value;   // could be EIRP or GainTemp
    }

    // Returns a real EIRP based on satellite EIRP contours if they are 
    // available.  Otherwise returns the calculated EIRP assuming
    // satellite EIRP contours have same value in the whole footprint
    public double getEIRPforTerminal(Terminal terminal) {

        return getMaxforTerminal(terminal, ContourType.EIRP);

    }

    // common function to find the contour with max EIRP or GT 
    // containing the terminal.  If terminal is not in any contour
    // then NEGLIGIBLE value is returned 
    public double getMaxforTerminal(Terminal terminal, ContourType contourType) {
        RfBand.Band band;
        double maxValue;
        double calcValue;
        double foundValue = NEGLIGIBLE;

        // if terminal is Tx then it will be UPLINK band
        if (contourType == ContourType.EIRP) {
            band = terminal.getBand();
            maxValue = this.getMaxEIRPfromContours(band);
            calcValue = bandSpecificItems.get(band).EIRP;
        } else {
            band = terminal.getBand();
            maxValue = this.getMaxGTfromContours(band);
            calcValue = bandSpecificItems.get(band).gainTemp;
        }

        // now we have generic max values (calc value is first read from txt file)
        Log.p("Satellite: getMaxforTerminal - max values " + contourType + " calculated/txt = "
                + calcValue + "  from contours = " + maxValue
                + " and band " + band + " for satellite " + this, Log.DEBUG
        );

        Hashtable<String, Beam> beams = null;

        if (bandSpecificItems != null && bandSpecificItems.get(band) != null) {
            beams = bandSpecificItems.get(band).beams;
        }
        if (beams != null) {
            // need to sort the contours by EIRP or GT across all beams
            ArrayList<Hierarchy> beamEIRP
                    = new ArrayList<Hierarchy>();
            ArrayList<Hierarchy> beamGT
                    = new ArrayList<Hierarchy>();
            // get all values EIRP and GT for each beam and each contour
            for (Beam beam : beams.values()) {
                for (Contour contour : beam.contours) {
                    // consier relevant contours
                    Hierarchy hier = new Hierarchy();
                    hier.beam = beam;
                    hier.contour = contour;

                    if (contour.type == ContourType.EIRP) {
                        hier.value = contour.EIRP;
                        beamEIRP.add(hier);
                    } else {
                        hier.value = contour.GT;
                        beamGT.add(hier);
                    }
                }
            }

            if (contourType == ContourType.EIRP) {
                if (beamEIRP.size() > 0) {
                    maxValue = findMax(terminal, beamEIRP, contourType);
                } else {
                    // have to have GT contours
                    if (beamGT.size() > 0) {
                        foundValue = findMax(terminal, beamGT, ContourType.GAIN_TEMP);

                        if (!Com.sameValue(foundValue, NEGLIGIBLE)) {
                            maxValue = bandSpecificItems.get(band).EIRP
                                    - (getMaxGTfromContours(band)
                                    - foundValue);
                        } else {
                            maxValue = NEGLIGIBLE;
                        }
                    } else {
                        Log.p("Satellite: missing GT contour for " + this
                                + " band " + band, Log.WARNING);
                    }
                    Log.p("Satellite: using GT contour to calculate EIRP - GT foundValue = "
                            + foundValue + " derived EIRP value  = " + maxValue,
                            Log.DEBUG);
                }
            } else {
                if (beamGT.size() > 0) {
                    maxValue = findMax(terminal, beamGT, contourType);
                } else {
                    // has to be EIRP contours
                    if (beamEIRP.size() > 0) {
                        foundValue = findMax(terminal, beamEIRP, ContourType.EIRP);

                        if (!Com.sameValue(foundValue, NEGLIGIBLE)) {
                            maxValue = bandSpecificItems.get(band).gainTemp
                                    - (getMaxEIRPfromContours(band)
                                    - foundValue);
                        } else {
                            maxValue = NEGLIGIBLE;
                        }
                    } else {
                        Log.p("Satellite: missing EIRP contour for " + this
                                + " band " + band, Log.WARNING);
                    }
                    Log.p("Satellite: using EIRP contour to calculate GT - EIRP foundValue = "
                            + foundValue + " derived GT  value = " + maxValue,
                            Log.DEBUG);
                }
            }
        } else {
            Log.p("Satellite: getMaxForTerminal beams is null for " + this
                    + " band " + band, Log.WARNING);
        }
        return (maxValue);
    }

    // sort beam/contour/line structures and look for the highest value
    // contour/lines that contains the terminal.  Return the associated
    // value for EIRP or G/T.  If not contained in any polygon then
    // return NEGLIGIBLE
    private double findMax(Terminal terminal, ArrayList<Hierarchy> beamValue,
            ContourType contourType) {

        double foundValue = NEGLIGIBLE;

        // now sort with descending value (EIRP or .  
        Collections.sort(beamValue, new Comparator<Hierarchy>() {
            @Override
            public int compare(Hierarchy one, Hierarchy two) {
                return (int) (two.value - one.value);
            }
        });

        boolean found = false;
        // find the  EIRP/GT of contour, starting with higest, that contains terminal 
        for (Hierarchy hier : beamValue) {
            // get the contour details first
            Contour contour = hier.contour;

            Log.p("    findMax in contour " + contour.name + " for "
                    + contourType, Log.DEBUG);
            // Find the first contour with terminal (has highest value)
            for (Line line : contour.lines) {

                Log.p("     findMax in Line " + line + " for "
                        + contourType, Log.DEBUG);
                if (pointInPolygon(terminal.getLatitude(), terminal.getLongitude(),
                        line.latitude.toArray(new Double[0]),
                        line.longitude.toArray(new Double[0]))) {
                    Log.p("Satellite: findMax for " + terminal
                            + " found " + " for " + contourType
                            + " beam " + hier.beam.name + " contour "
                            + contour.name + " lines " + line.position
                            + " for satellite"
                            + this, Log.DEBUG);
                    if (contourType == ContourType.EIRP) {
                        foundValue = contour.EIRP;
                    } else {
                        foundValue = contour.GT;
                    }
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }
        if (!found) {
            Log.p("Satellite: findMax for " + terminal
                    + " NOT found " + " for " + contourType
                    + " for satellite " + this, Log.DEBUG);
        }
        return foundValue;
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

    public Antenna getRxAntenna(RfBand.Band band) {
        return bandSpecificItems.get(band).rXantenna;
    }

    /**
     * @return the rXamplifier
     */
    public Amplifier getRxAmplifier(RfBand.Band band) {
        return bandSpecificItems.get(band).rXamplifier;
    }

    /**
     * @param amplifier the rXamplifier to set
     */
    public void setRxAmplifier(Amplifier amplifier, RfBand band) {
        bandSpecificItems.get(band).rXamplifier = amplifier;
        updateAffected();
    }

    /**
     * @param antenna the rXantenna to set
     */
    public void setRxAntenna(Antenna antenna, RfBand.Band band) {
        bandSpecificItems.get(band).rXantenna = antenna;
        updateAffected();
    }

    /**
     * @param EIRP the EIRP to set
     */
    public void setEIRP(double EIRP, RfBand.Band band) {
        bandSpecificItems.get(band).EIRP = EIRP;
        updateAffected();
    }

    /**
     * @return the gainTemp without any specific terminal
     */
    public double getGainTemp(RfBand.Band band) {
        return bandSpecificItems.get(band).gainTemp;
    }

    public double getGainTempForTerminal(Terminal terminal) {
        return getMaxforTerminal(terminal, ContourType.GAIN_TEMP);
    }

    /**
     * @param gain the gainTemp to set
     */
    public void setGainTemp(double gain, RfBand.Band band) {
        bandSpecificItems.get(band).gainTemp = gain;
        updateAffected();  // for parents
    }

    /**
     * @param semiMajor the semiMajor to set
     */
    public void setSemiMajor(double semiMajor) {
        this.semiMajor = semiMajor;
    }

    // uses Transmit antenna
    private double calcEIRP(RfBand.Band band) {
        // rXamplifier was in dB others 
        double eirp = (10 * MathUtil.log10(bandSpecificItems.
                get(band).tXamplifier.getPower()))
                + bandSpecificItems.get(band).tXantenna.getGain()
                - bandSpecificItems.get(band).tXantenna.getDepointingLoss()
                - bandSpecificItems.get(band).tXamplifier.getLFTX();
        return eirp;
    }

    // users receive antenna.  TODO check band here 
    private double calcGainTemp(RfBand.Band band) {

        double gain;

        // rXantenna gain is already in dB
        gain = bandSpecificItems.get(band).rXantenna.getGain()
                - bandSpecificItems.get(band).rXantenna.calcDepointingLoss()
                - bandSpecificItems.get(band).rXamplifier.getLFRX()
                - this.getPolarizationLoss()
                - 10.0 * MathUtil.log10(calcSystemNoiseTemp(band));
        return gain;
    }

    // update everything that could be affected
    // EIRP depends on rXantenna and rXamplifier, but both need to exist
    // if beams are not present then calcEIRP is used
    public void update(Entity e) {

        for (RfBand.Band band : bandSpecificItems.keySet()) {
            if (bandSpecificItems.get(band).rXamplifier != null
                    && bandSpecificItems.get(band).rXantenna != null
                    && bandSpecificItems.get(band).tXantenna != null) {
                bandSpecificItems.get(band).EIRP = calcEIRP(band);
                bandSpecificItems.get(band).gainTemp = calcGainTemp(band);
                // updateAffected();   should be called automatically

            } else {
                Log.p("Satellite: amplifier, Tx antenna or Rx antenna is null for "
                        + this, Log.DEBUG);
            }
        }
        // update all parents (such as Paths)
        updateAffected();
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
        size = latitude.length < longitude.length
                ? latitude.length : longitude.length;
        if (latitude.length != longitude.length) {
            Log.p("Com: pointInPolygon different size for lat|lng "
                    + latitude.length + "|" + longitude.length, Log.WARNING);

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
            px += Com.PI * 2.0;
            ax += Com.PI * 2.0;
            bx += Com.PI * 2.0;
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
