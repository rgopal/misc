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
    
    private final double DIAMETER_LO = 0.01;
    private final double DIAMETER_HI = 33;
    private final double EFFICIENCY_LO = 0.01;
    private final double EFFICIENCY_HI = 1.0;
    
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
    private double calcThreeDB(double d, double f) {
          return 70*Com.C/(f*d);
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
        this.threeDBangle = calcThreeDB(diameter, frequency);
    }

    public boolean validateDiameter(double d) {
        if (d < DIAMETER_LO || d > DIAMETER_HI)
            return false;
        else
            return true;
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
        this.threeDBangle = calcThreeDB(diameter, frequency);
        
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

    public boolean validateEfficiency(double e) {
        if (e < EFFICIENCY_LO || e > EFFICIENCY_HI)
            return false;
        else
            return true; 
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
    public void setBand(Com.Band band) {
        double f = 1E9;
        this.band = band;
        switch (band) {
            case X_DL:
                f = (Com.X_DL_HI + Com.X_DL_LO)/2.0;
                break;
            case X_UL:
                f = (Com.X_UL_HI + Com.X_UL_LO)/2.0;
                break;
            case C_DL:
                f = (Com.C_DL_HI + Com.C_DL_LO)/2.0;
                break;
            case C_UL:
                f = (Com.C_UL_HI + Com.C_UL_LO)/2.0;
                break;
            case KA_DL:
                f = (Com.KA_DL_HI + Com.KA_DL_LO)/2.0;
                break;
            case KA_UL:
                f = (Com.KA_UL_HI + Com.KA_UL_LO)/2.0;
                break;
             case KA2_DL:
                f = (Com.KA2_DL_HI + Com.KA2_DL_LO)/2.0;
                break;
            case KA2_UL:
                f = (Com.KA2_UL_HI + Com.KA2_UL_LO)/2.0;
                break;
            case KU_DL:
                f = (Com.KU_DL_HI + Com.KU_DL_LO)/2.0;
                break;
            case KU_UL:
                f = (Com.KU_UL_HI + Com.KU_UL_LO)/2.0;
                break;
            case KA:
                f = (Com.KA_HI + Com.KA_LO)/2.0;
                break;
            case KU:
                f = (Com.KU_HI + Com.KU_LO)/2.0;
                break;
            default:
                f = 0;
        }
        this.frequency = f;
        this.gain = calcGain (diameter, frequency, efficiency);
        this.threeDBangle = calcThreeDB(diameter, frequency);
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
        this.threeDBangle = calcThreeDB(diameter, frequency);
        
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
        this.frequency = f;
        this.gain = calcGain(diameter, frequency, efficiency);
        this.threeDBangle = 70*Com.C/(frequency*diameter);
        if (f >= Com.C_DL_LO && f <= Com.C_DL_HI)
            band = (Com.Band.C_DL);
        else if (f >= Com.C_UL_LO && f <= Com.C_UL_HI )
            band = (Com.Band.C_UL);
        else if (f >= Com.X_DL_LO && f <= Com.X_DL_HI)
            band = (Com.Band.X_DL);
        else if (f >= Com.X_UL_LO && f <= Com.X_UL_HI )
            band = (Com.Band.X_UL);
        else if (f >= Com.KU_DL_LO && f <= Com.KU_DL_HI)
            band = (Com.Band.KU_DL);
        else if (f >= Com.KU_UL_LO && f <= Com.KU_UL_HI )
            band = (Com.Band.KU_UL);
        else    if (f >= Com.KA_DL_LO && f <= Com.KA_DL_HI)
            band = (Com.Band.KA_DL);
        else if (f >= Com.KA_UL_LO && f <= Com.KA_UL_HI )
            band = (Com.Band.KA_UL);
        else if (f >= Com.KA_DL_LO && f <= Com.KA_DL_HI)
            band = (Com.Band.KA_DL);
        else if (f >= Com.KA2_UL_LO && f <= Com.KA2_UL_HI )
            band = (Com.Band.KA2_UL);
    }

    public boolean validateFrequency(double f) {
        if (f < Com.C_DL_LO || f > Com.KA2_UL_HI)
            return false;
        else
            return true;
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

   
    
    
}
