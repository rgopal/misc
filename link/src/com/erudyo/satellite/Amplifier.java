/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;
import com.codename1.io.Log;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Amplifier extends Entity {

    private double power = 1;       // in watts for Tx
    private double gain = 10;    // in dB for Rx
    private double Te = 290;   // in degree K, effective temperature

    // all gains and loss will be in dB addition/subtraction 
    private double LFTX = 0.5;     // Tx loss between amplifier and antenna  in DB
    private double LFRX = 0.5;      // Rx loss between antenna and reciver in dB
    private double feederTemp = 290.0;  // feeder temp in K
    private double tempGround = 45.0;
    private double tempSky = 20.0;  // in K, depends on frequency
    private double noiseFigure = 1.0;   // in dB

    public final static double POWER_LO = 1.0;
    public final static double POWER_HI = 100.0;
    
    public final static double NOISE_FIG_LO = 1.0;
    public final static double NOISE_FIG_HI = 10.0;

    public void setPower(double power) {
        if (validatePower(power)== true) {
            this.power = power;
        
            updateAffected();
        } else
            Log.p("Amplifier: power out of range " + 
                    String.valueOf(power), Log.INFO);
    }

     public boolean validatePower(double p) {
        if (p < POWER_LO || p > POWER_HI) {
            return false;
        } else {
            return true;
        }
    }
    public double getPower() {
        return power;
    }

    /**
     * @return the LFTX
     */
    public double getLFTX() {
        return LFTX;
    }

    /**
     * @param LFTX the LFTX to set
     */
    public void setLFTX(double LFTX) {
        this.LFTX = LFTX;
        updateAffected();
    }

    /**
     * @return the gain
     */
    public double getGain() {
        return gain;
    }

    /**
     * @param gain the gain to set
     */
    public void setGain(double gain) {
        this.gain = gain;
        updateAffected();
    }

    /**
     * @return the Te
     */
    public double getTe() {
        return Te;
    }

    /**
     * @param Te the Te to set
     */
    public void setTe(double Te) {
        this.Te = Te;
        updateAffected();
    }

    /**
     * @return the LFRX
     */
    public double getLFRX() {
        return LFRX;
    }

    /**
     * @param LFRX the LFRX to set
     */
    public void setLFRX(double LFRX) {
        this.LFRX = LFRX;
        updateAffected();
    }

    /**
     * @return the feederTemp
     */
    public double getFeederTemp() {
        return feederTemp;
    }

    /**
     * @param feederTemp the feederTemp to set
     */
    public void setFeederTemp(double feederTemp) {
        this.feederTemp = feederTemp;
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
        double  temp=0;
        if (band == RfBand.Band.C) 
            temp = tempSky;
        else
            Log.p("Amplifier: no tempSky for band " + band, Log.WARNING);
        return temp;
    }

    /**
     * @param tempSky the tempSky to set
     */
    public void setTempSky(double tempSky) {
        this.tempSky = tempSky;
    }

    /**
     * @return the noiseFigure
     */
    public double getNoiseFigure() {
        return noiseFigure;
    }

    /**
     * @param noiseFigure the noiseFigure to set
     */
    public void setNoiseFigure(double noiseFigure) {
        this.noiseFigure = noiseFigure;
    }

}
