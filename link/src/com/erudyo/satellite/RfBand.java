/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Random;
import com.codename1.io.Log;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class RfBand extends Entity {

    public enum Band {

        UK, C, X, KU, KA, KA2, X_DL, X_UL, C_DL, C_UL, KU_DL, KU_UL,
        KA_DL, KA_UL, KA2_DL, KA2_UL
    };
    public double lowFrequency;
    public double highFrequency;
    public double centerFrequency;
    public Band band;

    // index stores the location of instance in array indexRfBand
    private int index;

    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public static Band findDl(Band band) {
        Band u;
        switch (band) {
            case KA:
                u = Band.KA_DL;
                break;
            case KU:
                u = Band.KU_DL;
                break;
            case C:
                u = Band.C_DL;
                break;
            case X:
                u = Band.X_DL;
                break;
            case KA2:
                u = Band.KA2_DL;
                break;
            default:
                u = Band.UK;
        }
        return u;
    }

    public static Band findUl(Band band) {
        Band u;
        switch (band) {
            case KA:
                u = Band.KA_UL;
                break;
            case KU:
                u = Band.KU_UL;
                break;
            case C:
                u = Band.C_UL;
                break;
            case X:
                u = Band.X_UL;
                break;
            case KA2:
                u = Band.KA2_UL;
                break;
            default:
                u = Band.UK;
        }
        return u;
    }
    // lookup by String name with class level table
    // could be used to get an object by name
    final public static Hashtable<String, RfBand> rFbandHash
            = new Hashtable<String, RfBand>();

    // lookup by index with class level vector to get
    // object by index (may be ID or some sort of sorting)
    final public static ArrayList<RfBand> indexRfBand
            = new ArrayList<RfBand>();

    public final static double LO = 3.7E9;
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

    public final static double HI = 30.0E9;

    // static block will execute once when the class is created
    static {

        RfBand r;

        r = new RfBand(Band.C, "C", C_LO, C_HI);
        r = new RfBand(Band.X, "X", X_LO, X_HI);
        r = new RfBand(Band.KU, "KU", KU_LO, KU_HI);
        r = new RfBand(Band.KA, "KA", KA_LO, KA_HI);
        r = new RfBand(Band.KA2, "KA2", KA2_LO, KA2_HI);
        r = new RfBand(Band.UK, "UK", LO, HI);
    }

    public RfBand(Band band, String name, double lo, double hi) {

        this.centerRfBand(lo, hi);

        this.band = band;
        // add the new Band object instance to Hashtable
        this.name = name;

        // add the new Satellite instance to the Hashtable at the class level
        RfBand.rFbandHash.put(getName(), this);

        // Add new object instance to the current slot in array list of bands
        indexRfBand.add(this);

        this.index = indexRfBand.size() - 1;

    }

    public String toString() {
        return name;
    }

    public static Band findBand(double f) {
        // does not handle Unknown band
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
            case C:
                f = (C_HI + C_LO)/ 2.0;
                break;
            case UK:
                f = (HI + LO) / 2.0;
                break;
            default:
                f = 0;
                Log.p("RfBand: frequency not in the list for " + band, Log.DEBUG);
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

    public Band getBand() {
        return band;
    }
}
