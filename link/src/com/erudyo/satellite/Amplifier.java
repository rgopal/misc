/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.satellite;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 * @author rgopal
 */
public class Amplifier extends Entity {
    private double power = 1;       // in watts for Tx
    private double gain = 10;    // in dB for Rx
    private double Te = 290;   // in degree K, effective temperature
    
    // all gains and loss will be in dB addition/subtraction 
    private double LFTX =  0.5;     // loss between amplifier and antenna  in DB
    
    
    public void setPower (double power) {
        this.power = power;
        updateAffected();
    }
    public double getPower () {
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

}
