/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.com;

/**
 *
 * @author rgopal
 */
public class Antenna {
    private float diameter;
    private float size = 20;
    private float efficiency;
    private float temperature;
    
    private float depointingLoss;
    private float threeDBangle;
    private Common.Band band = Common.Band.KA;
    private float gain;
    private float frequency;
    private float maxGain;
    
    /**
     * @return the diameter
     */
    public float getDiameter() {
        return diameter;
    }

    /**
     * @param diameter the diameter to set
     */
    public void setDiameter(float diameter) {
        this.diameter = diameter;
    }

    /**
     * @return the size
     */
    public float getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(float size) {
        this.size = size;
    }

    /**
     * @return the efficiency
     */
    public float getEfficiency() {
        return efficiency;
    }

    /**
     * @param efficiency the efficiency to set
     */
    public void setEfficiency(float efficiency) {
        this.efficiency = efficiency;
    }

    /**
     * @return the temperature
     */
    public float getTemperature() {
        return temperature;
    }

    /**
     * @param temperature the temperature to set
     */
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    /**
     * @return the depointingLoss
     */
    public float getDepointingLoss() {
        return depointingLoss;
    }

    /**
     * @param depointingLoss the depointingLoss to set
     */
    public void setDepointingLoss(float depointingLoss) {
        this.depointingLoss = depointingLoss;
    }

    /**
     * @return the threeDBangle
     */
    public float getThreeDBangle() {
        return threeDBangle;
    }

    /**
     * @param threeDBangle the threeDBangle to set
     */
    public void setThreeDBangle(float threeDBangle) {
        this.threeDBangle = threeDBangle;
    }

    /**
     * @return the band
     */
    public Common.Band getBand() {
        return band;
    }

    /**
     * @param band the band to set
     */
    public void setBand(float band) {
        this.setBand(band);
    }

    /**
     * @return the gain
     */
    public float getGain() {
        return gain;
    }

    /**
     * @param gain the gain to set
     */
    public void setGain(float gain) {
        this.gain = gain;
    }

    /**
     * @return the frequency
     */
    public float getFrequency() {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the maxGain
     */
    public float getMaxGain() {
        return maxGain;
    }

    /**
     * @param maxGain the maxGain to set
     */
    public void setMaxGain(float maxGain) {
        this.maxGain = maxGain;
    }

    /**
     * @param band the band to set
     */
    public void setBand(Common.Band band) {
        this.band = band;
    }
    
    
}
