/*
 * Overview
 * Antenna could be instantiated for transmit or for receive (in Terminal).
 * STATUS:  20141001 All code in including functions in both directions
 * TODO:    Testing
 */
package com.erudyo.satellite;

import com.codename1.io.Log;

import com.codename1.util.MathUtil;

/**
 *
 * @author rgopal
 */
public class Antenna extends Entity {

    private double diameter = 1;
    private double efficiency = 0.6;
    private RfBand.Band band = RfBand.Band.KA;

    private double frequency = RfBand.centerFrequency(RfBand.Band.KA);

    private double depointingLoss = 0.5;
    private double depointingError = 0.1 * Com.PI / 180.0;      // in Radian
    private double temperature = 290;

    // the following are calculated, but can be set individually
    private double threeDBangle;
    private double gain;
    private double area;

    private static double GAIN_LO = 100;
    private static double GAIN_HI = - 10;
    public static final double DIAMETER_LO = .10;  // in m
    public static final double DIAMETER_HI = 12;
    private static final double EFFICIENCY_LO = 0.01;
    private static final double EFFICIENCY_HI = 1.0;

    // calcGain (returns gain in dBi) is called whenever diameter, frequency, or efficiency is changed.
    // calcArea changes when diameter is changed.
    // setArea changes diameter and then gain
    // setFrequency and setEfficiency changes gain
    // setGain changes diameter and area (in future other things)
    public Antenna() {

    }

    public Antenna(String n) {
        super(n);
        init();     // all derived values from basic dia, freq, eff
    }

    private void init() {

        area = calcArea(diameter);

        threeDBangle = calcThreeDB(diameter, frequency);

        gain = calcGain(diameter, frequency, efficiency);
    }

    private double calcArea(double diameter) {
        double area;
        area = Com.PI * MathUtil.pow(diameter / 2.0, 2.0);
        return area;
    }

    private double calcThreeDB(double d, double f) {
        return Math.toRadians(70 * Com.C / (f * d));    // Radians
    }

    // calculate gain from diameter, frequency, and efficiency
    private double calcGain(double d, double f, double e) {
        double gain;
        gain = e * MathUtil.pow((Com.PI * d * f / Com.C), 2.0);
        return (10 * MathUtil.log10(gain));     // in dB
    }

    /**
     * @return the diameter
     */
    public double getDiameter() {
        return diameter;
    }

    /**
     * @param diameter the diameter to set
     */
    public void setDiameter(double d) {
        if (validateDiameter(d)) {
            // only if valid diameter

            this.diameter = d;

            // calculate everything that depends on diameter
            this.area = calcArea(this.diameter);
            this.gain = calcGain(this.diameter, this.frequency, this.efficiency);
            this.threeDBangle = calcThreeDB(this.diameter, this.frequency);
            this.depointingLoss = calcDepointingLoss();
            updateAffected();
        } else {
            Log.p("Antenna: setDiameter: out of range diameter "
                    + String.valueOf(diameter), Log.DEBUG);
        }

    }

    public void setEfficiency(double e) {
        this.efficiency = e;
        this.gain = calcGain(this.diameter, this.frequency, e);
        updateAffected();
    }

    public boolean validateDiameter(double d) {
        if (d < DIAMETER_LO || d > DIAMETER_HI) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return the area
     */
    public double getArea() {
        return area;
    }

    /**
     * @param area the area to set
     */
    public void setArea(double area) {
        double d;
        this.area = area;
        d = MathUtil.pow(4.0 * area / Com.PI, 0.5);
        this.diameter = d;
        this.gain = calcGain(diameter, frequency, efficiency);
        this.threeDBangle = calcThreeDB(diameter, frequency);
        this.depointingLoss = calcDepointingLoss();
        updateAffected();

    }

    /**
     * @return the efficiency
     */
    public double getEfficiency() {
        return efficiency;
    }

    /**
     * @param efficiency the efficiency to set
     */
    public boolean validateEfficiency(double e) {
        if (e < EFFICIENCY_LO || e > EFFICIENCY_HI) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return the temperature
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * @param temperature the temperature to set
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
        updateAffected();
    }

    /**
     * @return the depointingLoss
     */
    public double getDepointingLoss() {
        return depointingLoss;

    }

    /**
     * @param depointingLoss depends on depointing error 12(theta/3dBangl)^2
     * Terminal uses it in Gain/T calc
     */
    public double calcDepointingLoss() {
        double l;
        // This formula is already in dB
        l = 12.0 * MathUtil.pow(depointingError / threeDBangle, 2.0);
        return l;
    }

    public void setDepointingLoss(double depointingLoss) {
        this.depointingLoss = calcDepointingLoss();
        updateAffected();
    }

    /**
     * @return the threeDBangle
     */
    public double getThreeDBangle() {
        return threeDBangle;
    }

    /**
     * @param threeDBangle the threeDBangle to set
     */
    public void setThreeDBangle(double threeDBangle) {
        // TODO change diameter
        this.threeDBangle = threeDBangle;
        updateAffected();
    }

    /**
     * @return the band
     */
    public RfBand.Band getBand() {
        return band;
    }

    /**
     * @param band the band to set
     */
    public void setBand(RfBand.Band band) {

        this.band = band;
        double f = RfBand.centerFrequency(band);

        this.frequency = f;
        this.gain = calcGain(diameter, f, efficiency);
        this.threeDBangle = calcThreeDB(diameter, f);
        updateAffected();
    }

    /**
     * @return the gain
     */
    public double getGain() {
        return gain;
    }

    /**
     * @param gain the gain to set. Changes diameter to be consistent with gain
     */
    public void setGain(double g) {
        this.gain = g;
        this.diameter = MathUtil.pow(MathUtil.pow(10.0, (gain / 10))
                / efficiency, 0.5) * Com.C / (Com.PI * frequency);
        this.threeDBangle = calcThreeDB(diameter, frequency);
        updateAffected();
    }

    /**
     * @return the frequency
     */
    public double getFrequency() {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(double f) {
        if (validateFrequency(f)) {
            this.frequency = f;
            this.gain = calcGain(diameter, frequency, efficiency);
            this.threeDBangle = calcThreeDB(diameter, f);
            this.band = RfBand.findBand(f);
            updateAffected();
        } else {
            Log.p("Antenna: setFrequency out of range "
                    + String.valueOf(f), Log.DEBUG);
        }
    }

    public boolean validateFrequency(double f) {
        if (f < RfBand.C_DL_LO || f > RfBand.KA2_UL_HI) {
            return false;
        } else {
            return true;
        }
    }

}
