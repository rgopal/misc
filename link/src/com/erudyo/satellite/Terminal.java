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
public class Terminal extends Entity {
    private double psi;     //longitude
    private double l;       //latitude
            
    public Terminal () {
        
    }
 
    public Terminal (String n) {
        super(n);
    }
    public Terminal (String n, String d, String s) {
        super(n,d,s);
    }

    /**
     * @return the psi
     */
    public double getPsi() {
        return psi;
    }

    /**
     * @param psi the psi to set
     */
    public void setPsi(double psi) {
        this.psi = psi;
    }

    /**
     * @return the l
     */
    public double getL() {
        return l;
    }

    /**
     * @param l the l to set
     */
    public void setL(double l) {
        this.l = l;
    }
}
