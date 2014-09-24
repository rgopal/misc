/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import java.text.Format;
import java.lang.Math;

/**
 *
 * @author rgopal
 *
 * All measurements are in Hz, Meter, Radian, Second, Watt. Friendly print
 * functions are provided for user interface.
 *
 * Longitude are positive for Eastern hemisphere. Latitude is positive in
 * Northern hemisphere.
 *
 * Includes functions for degree processing which can go to its own class in
 * future. Does not know about North South etc. Simple radian to degree
 * conversions
 */
public class Com {

    public enum Band {

        C, X, KU, KA, KA2, X_DL, X_UL, C_DL, C_UL, KU_DL, KU_UL, KA_DL, KA_UL, KA2_DL, KA2_UL
    };

    public enum Orbit {

        LEO, MEO, GEO
    };

    public enum Code {

        FEC_1_2, FEC_2_3, FEC_4_5, FEC_8_9
    };

    public enum Modulation {

        BPSK, QPSK, PSK8, PSK16
    };

    final public static Band[] bands;
    final public static BandParams[] bandParams;

    public final static double PI = Math.PI;
    public final static double C = 2.99792458E8;
    public final static double RE = 6378.1E3;     // mean equatorial radius
    public final static String DEGREE = "\u00b0";

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
        bandParams = new BandParams[3];
        // use the same sequence in both bands and bandParams
        bands = new Band[]{Band.C, Band.KA, Band.KU};

        // bandParams had to be an external class otherwise "this" cannot
        // be used from a static class
        bandParams[0] = new BandParams(C_LO, C_HI);
        bandParams[1] = new BandParams(KA_LO, KA_HI);
        bandParams[2] = new BandParams(KU_LO, KU_HI);

    }

    public static Band findBand(double f) {
        Com.Band band = Band.C;
        if (f >= Com.C_DL_LO && f <= Com.C_DL_HI) {
            band = (Com.Band.C_DL);
        } else if (f >= Com.C_UL_LO && f <= Com.C_UL_HI) {
            band = (Com.Band.C_UL);
        } else if (f >= Com.X_DL_LO && f <= Com.X_DL_HI) {
            band = (Com.Band.X_DL);
        } else if (f >= Com.X_UL_LO && f <= Com.X_UL_HI) {
            band = (Com.Band.X_UL);
        } else if (f >= Com.KU_DL_LO && f <= Com.KU_DL_HI) {
            band = (Com.Band.KU_DL);
        } else if (f >= Com.KU_UL_LO && f <= Com.KU_UL_HI) {
            band = (Com.Band.KU_UL);
        } else if (f >= Com.KA_DL_LO && f <= Com.KA_DL_HI) {
            band = (Com.Band.KA_DL);
        } else if (f >= Com.KA_UL_LO && f <= Com.KA_UL_HI) {
            band = (Com.Band.KA_UL);
        } else if (f >= Com.KA_DL_LO && f <= Com.KA_DL_HI) {
            band = (Com.Band.KA_DL);
        } else if (f >= Com.KA2_UL_LO && f <= Com.KA2_UL_HI) {
            band = (Com.Band.KA2_UL);
        }
        return band;
    }

    public static double centerFrequency(Band band) {
        double f;
        switch (band) {
            case X_DL:
                f = (Com.X_DL_HI + Com.X_DL_LO) / 2.0;
                break;
            case X_UL:
                f = (Com.X_UL_HI + Com.X_UL_LO) / 2.0;
                break;
            case C_DL:
                f = (Com.C_DL_HI + Com.C_DL_LO) / 2.0;
                break;
            case C_UL:
                f = (Com.C_UL_HI + Com.C_UL_LO) / 2.0;
                break;
            case KA_DL:
                f = (Com.KA_DL_HI + Com.KA_DL_LO) / 2.0;
                break;
            case KA_UL:
                f = (Com.KA_UL_HI + Com.KA_UL_LO) / 2.0;
                break;
            case KA2_DL:
                f = (Com.KA2_DL_HI + Com.KA2_DL_LO) / 2.0;
                break;
            case KA2_UL:
                f = (Com.KA2_UL_HI + Com.KA2_UL_LO) / 2.0;
                break;
            case KU_DL:
                f = (Com.KU_DL_HI + Com.KU_DL_LO) / 2.0;
                break;
            case KU_UL:
                f = (Com.KU_UL_HI + Com.KU_UL_LO) / 2.0;
                break;
            case KA:
                f = (Com.KA_HI + Com.KA_LO) / 2.0;
                break;
            case KU:
                f = (Com.KU_HI + Com.KU_LO) / 2.0;
                break;
            default:
                f = 0;
        }
        return f;
    }

    // convert degree, minute, second to radian.  Note it does not know about
    // North or South etc.   
    public static double toRadian(double d, double m, double s) {
        return Math.toRadians(d + m / 60.0 + s / 3600.0);
    }

    public static String toDegree(double r) {
        String dms;

        double degree = Math.toDegrees(r);
        dms = String.valueOf(degree);
        return dms;
    }

    public static String toDMS(double radian) {

        double dfFrac;			// fraction after decimal
        double dfSec;			// fraction converted to seconds
        double dfDecimal, dfDegree, dfMinute, dfSecond;
        String dms;

        dfDecimal = Math.toDegrees(radian);
        // Get degrees by chopping off at the decimal
        dfDegree = Math.floor(dfDecimal);
        // correction required since floor() is not the same as int()
        if (dfDegree < 0) {
            dfDegree = dfDegree + 1;
        }

        // Get fraction after the decimal
        dfFrac = Math.abs(dfDecimal - dfDegree);

        // Convert this fraction to seconds (without minutes)
        dfSec = dfFrac * 3600;

        // Determine number of whole minutes in the fraction
        dfMinute = Math.floor(dfSec / 60);

        // Put the remainder in seconds
        dfSecond = dfSec - dfMinute * 60;

        // Fix rounoff errors
        if (Math.round(dfSecond) == 60) {
            dfMinute = dfMinute + 1;
            dfSecond = 0;
        }

        if (Math.round(dfMinute) == 60) {
            if (dfDegree < 0) {
                dfDegree = dfDegree - 1;
            } else // ( dfDegree => 0 )
            {
                dfDegree = dfDegree + 1;
            }

            dfMinute = 0;
        }

        dms = String.valueOf((int) dfDegree) + DEGREE
                + String.valueOf((int) dfMinute) + "'" + String.valueOf((int) dfSecond) + "\"";
        return dms;
    }

    public static boolean sameValue(double one, double two) {
        if (Math.abs(one - two) < .000001) {
            return true;
        } else {
            return false;
        }
    }
}
