/*
 * OVERVIEW
 * Manages an instance representing path between satellite and terminal, 
 * including the distance, azimuth, and elevation angles (all stored in Radians).
 * Affected by satellite and terminal.  Includes attenuation (atmospheric, and 
 * rain).
 * Includes sky and ground temperatures for calculating G/T values.
 * Antenna noise temperature can
 * be calculated only here (and not in terminal) since the terminal needs to
 * be involved in a path.  Unlike EIRP which was in terminal.
 * TODO
 * Expand attenuation as a function of frequency (now use _DL and _UL)
 * THis class may be superfluous!   See RxView and TxView for duplication
 */
package com.erudyo.satellite;

import com.codename1.io.Log;
import com.codename1.util.MathUtil;
import java.lang.Math;

/**
 * Copyright (c) 2014 distance. R. Gopal. All Rights Reserved.
 *
 * @author rgopal ISO Standard is Long and then LAT, North and East are
 * positive. Fine to use all decimal after degree.
 */
public class Path extends Entity {

    private Satellite satellite;
    // NOT USED private RfBand.Band band;
    private Terminal terminal;
    private double pathLoss = 200.0;        // in dB
    private double attenuation = 0.3;         // in dB (varies on frequency)
    private double azimuth;
    private double elevation;
    private double CNo = Satellite.NEGLIGIBLE;
    private double powerReceived = Satellite.NEGLIGIBLE;
    private double spectralDensity = Satellite.NEGLIGIBLE;

    /**
     * @return the CNo
     */
    public double getCNo() {
        return CNo;
    }

    /**
     * @param CNo the CNo to set. TODO in constraint based optimization
     */
    public void setCNo(double CNo) {
        this.CNo = CNo;
    }

    /**
     * @return the spectralDensity
     */
    public double getSpectralDensity() {
        return spectralDensity;
    }

    /**
     * @param spectralDensity the spectralDensity to set
     */
    public void setSpectralDensity(double spectralDensity) {
        this.spectralDensity = spectralDensity;
    }

    /**
     * @return the pathType
     */
    public PATH_TYPE getPathType() {
        return pathType;
    }

    /**
     * @param pathType the pathType to set
     */
    public void setPathType(PATH_TYPE pathType) {
        this.pathType = pathType;
    }

    /**
     * @return the powerReceived
     */
    public double getPowerReceived() {
        return powerReceived;
    }

    /**
     * @param powerReceived the powerReceived to set
     */
    public void setPowerReceived(double powerReceived) {
        this.powerReceived = powerReceived;
    }

    public enum PATH_TYPE {

        UPLINK, DOWNLINK
    };
    private PATH_TYPE pathType = PATH_TYPE.UPLINK;
    private double distance;     //distance of satellite from terminal

    private double rainAttenuation = 7.0;  // TODO

    public Path() {

    }

    public Path(Satellite s, Terminal t, PATH_TYPE pathType) {
        this.satellite = s;
        this.terminal = t;
        if (pathType == PATH_TYPE.DOWNLINK) {
            this.name = pathType + ":" + s.getName() + "-" + t.getName();
        } else {
            this.name = pathType + ":" + t.getName() + "-" + s.getName();
        }
        // include this Path in the Affected list of satellite and terminal
        this.pathType = pathType;
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
            if (pathType == PATH_TYPE.DOWNLINK) {
                this.name = pathType + ":" + s.getName() + "-" + terminal.getName();
            } else {
                this.name = pathType + ":" + terminal.getName() + "-"
                        + s.getName();
            }
            s.addAffected(this);
            this.satellite = s;

            Log.p("Path: path " + this
                    + " in satellite " + s + "'s  Affected list",
                    Log.DEBUG);
        } else {
            Log.p("Path: path " + this
                    + " already in affected list of satellite " + s,
                    Log.INFO);
        }

        // satellite has been set, band is set so change it for terminal
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
            if (pathType == PATH_TYPE.DOWNLINK) {
                this.name = pathType + ":" + satellite.getName() + "-" + t.getName();
            } else {
                this.name = pathType + ":" + t.getName() + "-" + satellite.getName();
            }
            t.addAffected(this);
            this.terminal = t;
            Log.p("Path: path " + this
                    + " added terminal " + t + " in affected list",
                    Log.DEBUG);

        } else {
            Log.p("Path: path " + this
                    + " already in affected list of terminal " + t,
                    Log.INFO);

        }
        // not needed this.terminal = t;
        setAll();
    }

    // nice to get G/T and EIRP for the specific terminal
    private double calcCNo() {
        double result;

        // CNo depends on Tx and receive of satellite, here EIRP, loss
        // and gain are all in dBHz
        if (getPathType() == PATH_TYPE.UPLINK) {
            // TODO: check for band similar to calcPowerReceived?
            result
                    = terminal.getEIRP()
                    - getPathLoss()
                    - getAttenuation()
                    + satellite.getGainTempForTerminal(terminal) // location based
                    - Com.KdB;
        } else {
            result = satellite.getEIRPforTerminal(terminal)
                    - getPathLoss()
                    - getAttenuation()
                    + terminal.getGainTemp()
                    - Com.KdB;
        }
        return result;
    }

    // just the power received
    private double calcPowerReceived() {
        double result = Satellite.NEGLIGIBLE;

        // CNo depends on Tx and receive of satellite, here EIRP, loss
        // and gain are all in dBHz
        if (getPathType() == PATH_TYPE.UPLINK) {
            if (satellite.bandSpecificItems.get(terminal.getBand()) != null) {
                result
                        = terminal.getEIRP()
                        - getPathLoss()
                        - getAttenuation()
                        + satellite.bandSpecificItems.get(
                                terminal.getBand()).rXantenna.getGain();
            }

        } else {
            result = satellite.getEIRPforTerminal(terminal)
                    - getPathLoss()
                    - getAttenuation()
                    + terminal.getrXantenna().getGain();

        }
        return result;
    }

    private double calcSpecDens() {
        double result;
        if (getPathType() == PATH_TYPE.UPLINK) {
            result
                    = terminal.getEIRP() // satellite Gain is location based
                    - getAttenuation()
                    - 10 * MathUtil.log10(4.0 * Com.PI
                            * (getDistance()
                            * getDistance())
                    );

        } else {
            result = satellite.getEIRPforTerminal(terminal)
                    - getAttenuation()
                    - 10 * MathUtil.log10(4.0 * Com.PI
                            * getDistance()
                            * getDistance());
        }
        return result;
        // Spec density depends on EIRP/4piR2 but EIRP is already in dB
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

    public static Boolean visible(Satellite satellite, Terminal terminal) {

        // check if satellite is visible from terminal
        double radian = Math.abs(satellite.getLongitude()
                - terminal.getLongitude());
        double degree = radian * 180.0 / Com.PI;
        if (radian > Com.VISIBLE_ANGLE) {
            Log.p("Path: terminal and satellite are far apart with " + degree
                    + " satellite at " + Com.toDMS(satellite.getLongitude())
                    + " terminal at " + Com.toDMS(terminal.getLongitude()), Log.DEBUG);
            return false;
        } else {
            return true;
        }
    }

    public void setAll() {

        this.distance = calcDistance(this.satellite, this.terminal);
        this.azimuth = calcAzimuth(this.satellite, this.terminal);
        this.elevation = calcElevation(this.satellite, this.terminal);

        // not using the true/false 
        //visible(satellite, terminal);

        // get center frequency of band used by terminal.  Note the _UL
        // and _DL at this time
        double frequency;
        if (pathType == PATH_TYPE.UPLINK) {
            frequency = terminal.gettXantenna().getFrequency();
        } else {
            frequency = terminal.getrXantenna().getFrequency();
        }

        this.pathLoss = calcPathLoss(this.distance, frequency);
        this.CNo = calcCNo();
        this.powerReceived = calcPowerReceived();
        this.spectralDensity = calcSpecDens();

        updateAffected();   // update Comms
    }

    public double calcPathLoss(double distance, Double frequency) {

        double p;
        if (Com.sameValue(frequency, 0.0)) {
            Log.p("Path: frequency is zero in calcPathLoss. Using 200.0", Log.WARNING);
            return (200.0);
        } else {
            p = 10.0 * MathUtil.log10(
                    MathUtil.pow(4.0 * Com.PI * distance
                            / (Com.C / frequency), 2.0)
            );
        }
        return p;
    }
    // called if there is any change in the child or sibling

    public void update(Entity e) {

        // EIRP depends on antenna and amplifier, but both need to exist 
        if (this.getSatellite() != null && this.getTerminal() != null) {
            setAll();       // calculate everything
        } else {
            Log.p("Path: satellite "
                    + satellite + " or terminal " + terminal + " is defined", 
                    Log.WARNING);
                    
        }

        // avoid using set since that should be used to send updates down
    }

    public Path(String n) {
        super(n);
    }

    public static double calcRelativeLongitude(double satellite,
            double terminal) {

        double relativeLong, term, sat;
        // make both angles 0 to 360.
        if (terminal < 0.0) {
            term = terminal + 2 * Com.PI;
        } else {
            term = terminal;
        }

        if (satellite < 0.0) {
            sat = satellite + 2 * Com.PI;
        } else {
            sat = satellite;
        }

        relativeLong = Math.abs(term - sat);

        if (relativeLong > Com.PI) {
            relativeLong = 2.0 * Com.PI - relativeLong;
        }
        // return absolute value of difference.  Above is not needed

        if (relativeLong > Com.PI) {
            Log.p("relativeLong: realative long is more than 180 degree "
                    + relativeLong + " satellite " + satellite
                    + " terminal " + terminal, Log.WARNING);
        }

        return Math.abs(relativeLong);
    }

    public static double calcBigPhi(Satellite satellite, Terminal terminal) {
        if (satellite == null || terminal == null) {
            Log.p("Path: satellite  " + satellite + " or terminal " + terminal
                    + " is not defined",
                    Log.WARNING);
        }
        double Phi;
        double relativeLong;
        relativeLong = calcRelativeLongitude(satellite.getLongitude(),
                terminal.getLongitude());
        Phi = MathUtil.acos(Math.cos(relativeLong)
                * Math.cos(terminal.getLatitude())
                * Math.cos(satellite.getLatitude())
                + Math.sin(satellite.getLatitude())
                * Math.sin(terminal.getLatitude())
        );
        if (Phi < 0 || Phi > Com.PI / 2.0) {
            /* TODO this gets called three times and clutters 94 deg upwards
                 Log.p("Path: Phi value out of range " + Com.toDMS(Phi) + 
                    " satellite " + satellite + " terminal " + terminal, 
                    Log.WARNING);
                    */
            
        }
        return Phi;
    }

    public static double calcDistance(Satellite satellite, Terminal terminal) {
        double d;
        double altitude = satellite.getAltitude();
        d = MathUtil.pow((1.0
                + 0.42 * (1.0 - Math.cos(calcBigPhi(satellite, terminal)))), 0.5)
                * altitude;
        return d;
    }

    public static double calcElevation(Satellite satellite, Terminal terminal) {

        double elev;
        double bigPhi;

        bigPhi = calcBigPhi(satellite, terminal);
        // if bigPhi is zero then you have to look straight up
        if (Com.sameValue(bigPhi, 0.0)) {
            return (Com.PI / 2.0);
        }
        elev = MathUtil.atan((Math.cos(bigPhi) - (Com.RE
                / (Com.RE + satellite.getAltitude())))
                / MathUtil.pow((1.0 - Math.cos(bigPhi) * Math.cos(bigPhi)), 0.5));
        return elev;

    }

    public static double calcAzimuth(Satellite satellite, Terminal terminal) {
        double azimuth = 0.0;
        double a;
        relativePosition rel;
        double relLong, bigPhi;
        relLong = calcRelativeLongitude(satellite.getLongitude(),
                terminal.getLongitude());
        bigPhi = calcBigPhi(satellite, terminal);

        // if terminal and satellite are on the same longitude
        // then terminal is either looking south (in northern hemi)
        // or north (in southern hemisphere).  Phi may not be zero.
        // If Phi is zero then relLong is also zero?
        if (Com.sameValue(relLong, 0.0) || Com.sameValue(bigPhi, 0.0)) {
            return (Com.PI);
        }

        a = MathUtil.asin(Math.sin(relLong) * Math.cos(satellite.getLatitude())
                / Math.sin(bigPhi));
        rel = findRelativePosition(satellite, terminal);
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

    public static relativePosition findRelativePosition(Satellite satellite,
            Terminal terminal) {
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
