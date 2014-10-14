/*
 * OVERVIEW
 * Manages an instance representing path between semiMajor satellite and semiMajor terminal, 
 * including the distance, azimuth, and elevation angles (all stored in Radians).
 * Affected by satellite and terminal.  Includes attenuation (atmospheric, and 
 * rain).
 * Includes sky and grouond temperatures for calculating G/T values.
 * Currently does not affect anything else.  Antenna noise temperature can
 * be calculated only here (and not in terminal) since the terminal needs to
 * be involved in a path.  Unlike EIRP which was in terminal.
 * TODO:  check if it will affect the final Data Rate or Link Marging 
 * (may be in selection)
 * Expand attenuation as a function of frequency
 */
package com.erudyo.satellite;

import com.codename1.io.Log;
import com.codename1.util.MathUtil;
import java.lang.Math;

/**
 * Copyright (c) 2014 distance. Gopal. All Rights Reserved.
 *
 * @author rgopal ISO Standard is Long and then LAT, North and East are
 * positive. Fine to use all decimal after degree.
 */
public class Path extends Entity {

    private Satellite satellite;
    private RfBand.Band band;
    private Terminal terminal;
    private double pathLoss = 200.0;        // in dB
    private double attenuation = 0.3;         // in dB (varies on frequency)
    private double azimuth;
    private double elevation;
    private double distance;     //distance of satellite from terminal

    private double rainAttenuation = 7.0;  // for .01

    public Path() {

    }

    public Path(Satellite s, Terminal t) {
        this.satellite = s;
        this.terminal = t;

        // include this Path in the Affected list of satellite and terminal
        s.addAffected(this);
        t.addAffected(this);

        setAll();
    }

    /**
     * @return the satellite
     */
    public Satellite getSatellite() {
        return satellite;
    }

    /**
     * @param s the satellite to set
     */
    public void setSatellite(Satellite s) {
        // check if the satellite is new
        if (s != this.satellite) {
            this.satellite.removeAffected(this);
            s.addAffected(this);
            this.satellite = s;
        } else {
            Log.p("Path: path " + this
                    + " already in affected list of satellite " + s,
                    Log.WARNING);
        }
        setAll();
    }

    /**
     * @return the terminal
     */
    public Terminal getTerminal() {
        return terminal;
    }

    /**
     * @param t the terminal to set
     */
    public void setTerminal(Terminal t) {
        if (this.terminal != t) {
            this.terminal.removeAffected(this);
            t.addAffected(this);
            this.terminal = t;
        } else {
             Log.p("Path: path " + this
                    + " already in affected list of terminal " + t,
                    Log.WARNING);

        }
        this.terminal = t;
        setAll();
    }

    /**
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @param R the distance to set. This will change angles. Can't think of why
     * this will be called
     */
    private void setDistance(double R) {
        this.distance = R;

    }

    /**
     * @return the azimuth
     */
    public double getAzimuth() {
        return azimuth;
    }

    /**
     * @param azimuth the azimuth to set. This could be called to reverse
     * calculate the location (TODO)
     */
    private void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    /**
     * @return the elevation. TODO reverse calculate location
     */
    public double getElevation() {
        return elevation;
    }

    /**
     * @param elevation the elevation to set
     */
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    /**
     * @return the band. Would affect rain attenuation
     */
    public RfBand.Band getBand() {
        return band;
    }

    /**
     * @param band the band to set
     */
    public void setBand(RfBand.Band band) {
        this.band = band;
    }

    /**
     * @return the pathLoss
     */
    public double getPathLoss() {
        return pathLoss;
    }

    /**
     * @param pathLoss the pathLoss to set
     */
    public void setPathLoss(double pathLoss) {
        this.pathLoss = pathLoss;
    }

    /**
     * @return the attenuation
     */
    public double getAttenuation() {
        return attenuation;
    }

    /**
     * @param attenuation the attenuation to set
     */
    public void setAttenuation(double attenuation, RfBand.Band band) {
        // this should be a function of frequency
        this.attenuation = attenuation;
    }

    /**
     * @return the rainAttenuation
     */
    public double getRainAttenuation() {
        return rainAttenuation;
    }

    /**
     * @param rainAttenuation the rainAttenuation to set
     */
    public void setRainAttenuation(double rainAttenuation) {
        this.rainAttenuation = rainAttenuation;
    }

    enum relativePosition {

        NE, SE, NW, SW, N, S, E, W
    };

    public void setAll() {

        // check if satellite is visible from terminal
        double degree = Math.abs(satellite.getLongitude()
                - terminal.getLongitude()) * 180.0 / Com.PI;
        if (degree > 80.0) {
            Log.p("Path: terminal and satellite are far apart " + degree, Log.WARNING);
        }

        this.distance = calcDistance(satellite.getAltitude());
        this.azimuth = calcAzimuth();
        this.elevation = calcElevation();

        // get center frequency of band used by terminal
        this.pathLoss = calcPathLoss(this.distance,
                RfBand.centerFrequency(terminal.gettXantenna().getBand()));
    }

    public double calcPathLoss(double distance, Double frequency) {

        double p;
        if (Com.sameValue(frequency, 0.0)) {
            Log.p("Path: frequency is zero in calcPathLoss", Log.ERROR);
            return (200);
        } else {
            p = 10.0 * MathUtil.log10(
                    MathUtil.pow(4.0 * Com.PI * distance
                            / (Com.C / frequency), 2.0)
            );
        }
        return p;
    }
    // called if there is any change in the child or sibling

    public void updateAll() {
        setAll();
    }

    public void update(Entity e) {

        // EIRP depends on antenna and amplifier, but both need to exist 
        if (this.getSatellite() != null && this.getTerminal() != null) {
            setAll();       // calculate everything
        } else {
            Log.p("Path: satellite or terminal is null "
                    + satellite + terminal, Log.ERROR);
        }

        // avoid using set since that should be used to send updates down
    }

    public Path(String n) {
        super(n);
    }

    public double calcRelativeLongitude() {

        double relativeLong;
        // get relative longitdue between satellite and terminal
        if (satellite.longitude > terminal.getLongitude()) {
            relativeLong = satellite.getLongitude() - terminal.getLongitude();
        } else {
            relativeLong = terminal.getLongitude() - satellite.getLongitude();
        }
        // return absolute value of difference.  Above is not needed
        return Math.abs(relativeLong);
    }

    public double calcBigPhi() {
        if (satellite == null || terminal == null) {
            Log.p("Path: satellite or terminal is null " + satellite + terminal,
                    Log.WARNING);
        }
        double Phi;
        double relativeLong;
        relativeLong = calcRelativeLongitude();
        Phi = MathUtil.acos(Math.cos(relativeLong)
                * Math.cos(terminal.getLatitude())
                * Math.cos(satellite.getLatitude())
                + Math.sin(satellite.getLatitude())
                * Math.sin(terminal.getLatitude())
        );
        if (Phi < 0 || Phi > Com.PI / 2.0) {
            Log.p("Path: Phi value out of range " + Com.toDMS(Phi),
                    Log.WARNING);
        }
        return Phi;
    }

    public double calcDistance(Double altitude) {
        double d;
        d = MathUtil.pow((1.0
                + 0.42 * (1.0 - Math.cos(calcBigPhi()))), 0.5)
                * altitude;
        return d;
    }

    public double calcElevation() {

        double elev;
        double bigPhi;

        bigPhi = calcBigPhi();
        // if bigPhi is zero then you have to look straight up
        if (Com.sameValue(bigPhi, 0.0)) {
            return (Com.PI / 2.0);
        }
        elev = MathUtil.atan((Math.cos(bigPhi) - (Com.RE / (Com.RE + satellite.getAltitude())))
                / MathUtil.pow((1.0 - Math.cos(bigPhi) * Math.cos(bigPhi)), 0.5));
        return elev;

    }

    public double calcAzimuth() {
        double azimuth = 0.0;
        double a;
        relativePosition rel;
        double relLong, bigPhi;
        relLong = calcRelativeLongitude();
        bigPhi = calcBigPhi();

        // if terminal and satellite are on the same longitude
        // then terminal is either looking south (in northern hemi)
        // or north (in southern hemisphere).  Phi may not be zero.
        // If Phi is zero then relLong is also zero?
        if (Com.sameValue(relLong, 0.0) || Com.sameValue(bigPhi, 0.0)) {
            return (Com.PI);
        }

        a = MathUtil.asin(Math.sin(relLong) * Math.cos(satellite.getLatitude())
                / Math.sin(bigPhi));
        rel = findRelativePosition();
        // Here NE means Northern Hemishpere and Satellite East of Terminal
        switch (rel) {
            case NE:
                azimuth = Com.PI - a;
                break;
            case NW:
                azimuth = Com.PI + a;
                break;
            case SE:
                azimuth = a;
                break;
            case SW:
                azimuth = Com.PI * 2.0 - a;
                break;
        }
        return azimuth;
    }

    public relativePosition findRelativePosition() {
        // terminal in northern hemisphere
        relativePosition rel;
        rel = relativePosition.NE;
        // northern hemisphere for terminal
        if (terminal.getLatitude() >= 0.0) {
            // satellite is East of Terminal
            if (satellite.getLongitude() > terminal.getLongitude()) {
                rel = relativePosition.NE;
            } else {
                rel = relativePosition.NW;
            }
        } else {
            // terminal in southern hemisphere
            if (satellite.getLongitude() >= terminal.getLongitude()) {
                // satellite is East of terminal
                rel = relativePosition.SE;
            } else {
                rel = relativePosition.SW;
            }
        }

        return rel;
    }

}
