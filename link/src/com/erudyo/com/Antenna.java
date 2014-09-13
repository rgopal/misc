/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.com;

import com.codename1.util.MathUtil;


/**
 *
 * @author rgopal
 */
public class Antenna {
    private double diameter;
    private double area;
    private double efficiency;
    private double temperature;
    
    private double depointingLoss;
    private double threeDBangle;
    private Com.Band band = Com.Band.KA;
    private double gain;
    private double frequency;
    private double maxGain;
    private double minGain;
   
    // calcGain (returns gain in dBi) is called whenever diameter, frequency, or efficiency is changed.
    // calcArea changes when diameter is changed.
    // setArea changes diameter and then gain
    // setFrequency and setEfficiency changes gain
    
    // setGain changes diameter and area (in future other things)
    
    public Antenna () {
        diameter = 4;
        area = calcArea(diameter);
        efficiency=0.6;
        frequency = 1.4E+10;
        // all gain values calculated in dB
        gain = calcGain (diameter, frequency, efficiency);
        maxGain = 100;
        minGain = -10;  
    }
    private double calcArea (double diameter) {
        double area;
        area = Com.PI*MathUtil.pow(diameter/2.0,2.0);
        return area;
    }
    // calculate gain from diameter, frequency, and efficiency
    private double calcGain (double d, double f, double e) {
        double gain;
        gain = e*MathUtil.pow((Com.PI*d*f/Com.C),2.0);
        return (10*MathUtil.log10(gain));
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
    public void setDiameter(double diameter) {
        this.diameter = diameter;
        this.area = calcArea(diameter);
        this.gain = calcGain(diameter, frequency, efficiency);
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
        d = MathUtil.pow(4.0*area/Com.PI,0.5);
        this.diameter = d;
        this.gain = calcGain (diameter, frequency, efficiency);
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
    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
        gain = calcGain(diameter, frequency, efficiency);
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
    }

    /**
     * @return the depointingLoss
     */
    public double getDepointingLoss() {
        return depointingLoss;
    }

    /**
     * @param depointingLoss the depointingLoss to set
     */
    public void setDepointingLoss(double depointingLoss) {
        this.depointingLoss = depointingLoss;
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
        this.threeDBangle = threeDBangle;
    }

    /**
     * @return the band
     */
    public Com.Band getBand() {
        return band;
    }

    /**
     * @param band the band to set
     */
    public void setBand(double band) {
        this.setBand(band);
    }

    /**
     * @return the gain
     */
    public double getGain() {
        return gain;
    }

    /**
     * @param gain the gain to set.  Changes diameter to be consistent with gain
     */
    public void setGain(double g) {
        this.gain = g;
        this.diameter = MathUtil.pow(MathUtil.pow(10.0,(gain/10))/efficiency,0.5)*Com.C/(Com.PI*frequency);
        
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
    public void setFrequency(double frequency) {
        this.frequency = frequency;
        this.gain = calcGain(diameter, frequency, efficiency);
    }

    /**
     * @return the maxGain
     */
    public double getMaxGain() {
        return maxGain;
    }

    /**
     * @param maxGain the maxGain to set
     */
    public void setMaxGain(double maxGain) {
        this.maxGain = maxGain;
    }

    /**
     * @param band the band to set
     */
    public void setBand(Com.Band band) {
        this.band = band;
    }
    
    
}
