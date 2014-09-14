/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.satellite;

import com.codename1.util.MathUtil;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 * @author rgopal
 */
public class Satellite extends Entity {
   
    protected double phi;     // latitude
    protected double lambda;  // longitude
    protected double r;       // distance from center of Earth
    protected double h;       // altitude of satellite from sub-satellite point
    protected double a;       // semi major axis
    protected double v;       // velocity
    protected double R0;      // altitude
    protected Com.Orbit orbit;
  
  
    public Satellite () {
        
    }
    public Satellite (String n) {
        super(n);
    }
    public Satellite (String n, String d, String s) {
        super(n,d,s);
    }
   
    /**
     * @return the lambda
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * @param lambda the lambda to set
     */
    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    /**
     * @return the r
     */
    public double getR() {
        return r;
    }

    /**
     * @param r the r to set
     */
    public void setR(double r) {
        this.r = r;
    }

    /**
     * @return the phi
     */
    public double getPhi() {
        return phi;
    }

    /**
     * @param phi the phi to set
     */
    public void setPhi(double phi) {
        this.phi = phi;
    }

    /**
     * @return the h
     */
    public double getH() {
        return h;
    }

    /**
     * @param h the h to set
     */
    public void setH(double h) {
        this.h = h;
    }

    /**
     * @return the a
     */
    public double getA() {
        return a;
    }

    /**
     * @param a the a to set
     */
    public void setA(double a) {
        this.a = a;
    }

    /**
     * @return the v
     */
    public double getV() {
        return v;
    }

    /**
     * @param v the v to set
     */
    public void setV(double v) {
        this.v = v;
    }

    /**
     * @return the R0
     */
    public double getR0() {
        return R0;
    }

    /**
     * @param R0 the R0 to set
     */
    public void setR0(double R0) {
        this.R0 = R0;
    }
   
    
   


}
