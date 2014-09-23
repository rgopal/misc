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
public class Amplifier {
    private double power = 1;   // in watts
    public void setPower (double power) {
        this.power = power;
    }
    public double getPower () {
        return power;
    }

}
