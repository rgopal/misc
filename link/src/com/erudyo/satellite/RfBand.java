/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class RfBand extends Entity {

    public enum Band {

        C, X, KU, KA, KA2, X_DL, X_UL, C_DL, C_UL, KU_DL, KU_UL, KA_DL, KA_UL, KA2_DL, KA2_UL
    };
    public double lowFrequency;
    public double highFrequency;
    public double centerFrequency;
    public Band band;

    // lookup by String name with class level table
    final public static Hashtable<String, RfBand> rFbandHash
            = new Hashtable<String, RfBand>();
    
    // lookup by index with class level vector
    final public static ArrayList<RfBand> indexRfBand = new ArrayList<RfBand>();

    public final static double C_LO = 3.7E9;
    public final static double C_HI = 6.425E9;
    public final static double C_DL_LO = 3.7E9;
    public final static double C_DL_HI = 4.2E9;
    public final static double C_UL_LO = 5.925E9;
    public final static double C_UL_HI = 6.425E9;

    public final static double X_LO = 7.25E9;
    public final static double X_HI = 8.4E9;
    public final static double X_DL_LO = 7.25E9;
    public final static double X_DL_HI = 7.75E9;
    public final static double X_UL_LO = 7.9E9;
    public final static double X_UL_HI = 8.4E9;

    public final static double KU_LO = 11.7E9;
    public final static double KU_HI = 14.5E9;
    public final static double KU_DL_LO = 11.7E9;
    public final static double KU_DL_HI = 12.2E9;
    public final static double KU_UL_LO = 14.0E9;
    public final static double KU_UL_HI = 14.5E9;

    public final static double KA_LO = 18.3E9;
    public final static double KA_HI = 28.0E9;
    public final static double KA_DL_LO = 18.3E9;
    public final static double KA_DL_HI = 18.8E9;
    public final static double KA_UL_LO = 27.5E9;
    public final static double KA_UL_HI = 28.0E9;

    public final static double KA2_LO = 19.7E9;
    public final static double KA2_HI = 30.0E9;
    public final static double KA2_DL_LO = 19.7E9;
    public final static double KA2_DL_HI = 20.2E9;
    public final static double KA2_UL_LO = 29.5E9;
    public final static double KA2_UL_HI = 30.0E9;

    // static block will execute once when the class is created
    static {

        RfBand r;

        r = new RfBand(Band.C, "C", C_LO, C_HI);
        r = new RfBand(Band.X, "X", X_LO, X_HI);
        r = new RfBand(Band.KU, "KU", KU_LO, KU_HI);
        r = new RfBand(Band.KA,"KA", KA_LO, KA_HI);
    }

    public RfBand(Band band, String name, double lo, double hi) {

        this.centerRfBand(lo, hi);

        this.band = band;
        // add the new Band object instance to Hashtable
        this.name = name;

        // add the new Satellite instance to the Hashtable at the class level
        RfBand.rFbandHash.put(getName(), this);

        // Add new object instance to the array list
        indexRfBand.add(this);
        
        // increment index
        this.setIndex(this.getIndex() + 1);

    }


    public String toString () {
        return name;
    }
    public static Band findBand(double f) {
        Band band = Band.C;
        if (f >= C_DL_LO && f <= C_DL_HI) {
            band = (Band.C_DL);
        } else if (f >= C_UL_LO && f <= C_UL_HI) {
            band = (Band.C_UL);
        } else if (f >= X_DL_LO && f <= X_DL_HI) {
            band = (Band.X_DL);
        } else if (f >= X_UL_LO && f <= X_UL_HI) {
            band = (Band.X_UL);
        } else if (f >= KU_DL_LO && f <= KU_DL_HI) {
            band = (Band.KU_DL);
        } else if (f >= KU_UL_LO && f <= KU_UL_HI) {
            band = (Band.KU_UL);
        } else if (f >= KA_DL_LO && f <= KA_DL_HI) {
            band = (Band.KA_DL);
        } else if (f >= KA_UL_LO && f <= KA_UL_HI) {
            band = (Band.KA_UL);
        } else if (f >= KA_DL_LO && f <= KA_DL_HI) {
            band = (Band.KA_DL);
        } else if (f >= KA2_UL_LO && f <= KA2_UL_HI) {
            band = (Band.KA2_UL);
        }
        return band;
    }

    public static double centerFrequency(Band band) {
        double f;
        switch (band) {
            case X_DL:
                f = (X_DL_HI + X_DL_LO) / 2.0;
                break;
            case X_UL:
                f = (X_UL_HI + X_UL_LO) / 2.0;
                break;
            case C_DL:
                f = (C_DL_HI + C_DL_LO) / 2.0;
                break;
            case C_UL:
                f = (C_UL_HI + C_UL_LO) / 2.0;
                break;
            case KA_DL:
                f = (KA_DL_HI + KA_DL_LO) / 2.0;
                break;
            case KA_UL:
                f = (KA_UL_HI + KA_UL_LO) / 2.0;
                break;
            case KA2_DL:
                f = (KA2_DL_HI + KA2_DL_LO) / 2.0;
                break;
            case KA2_UL:
                f = (KA2_UL_HI + KA2_UL_LO) / 2.0;
                break;
            case KU_DL:
                f = (KU_DL_HI + KU_DL_LO) / 2.0;
                break;
            case KU_UL:
                f = (KU_UL_HI + KU_UL_LO) / 2.0;
                break;
            case KA:
                f = (KA_HI + KA_LO) / 2.0;
                break;
            case KU:
                f = (KU_HI + KU_LO) / 2.0;
                break;
            default:
                f = 0;
        }
        return f;
    }

    public void centerRfBand(double low, double hi) {
        lowFrequency = low;
        highFrequency = hi;
        centerFrequency = (low + hi) / 2.0;
    }

    public RfBand() {

    }
    public Band getBand () {
        return band;
    }
}
