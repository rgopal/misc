/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import java.text.Format;
import java.lang.Math;
import java.util.Hashtable;
import com.codename1.util.MathUtil;

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

    public enum Orbit {

        LEO, MEO, GEO
    };

    public enum Code {

        FEC_1_9 ("1/9"), 
        FEC_1_4 ("1/4"), 
        FEC_1_3 ("1/3"),
        FEC_1_2 ("1/3"),
        FEC_2_3 ("2/3"), 
        FEC_4_5 ("4/5"), 
        FEC_7_8 ("7/8"), 
        FEC_8_9 ("8/9");
        
        private final String value;


        public String getValue() {
            return value;
        }

        private Code(final String text) {
            this.value = text;
        }

        public String toString() {
            return value;
        }
    };

    public enum Modulation {

        BPSK, QPSK, PSK8, PSK16
        
    };

    public final static double PI = Math.PI;
    public final static double K = 1.379E-23;       // Boltzmann constant, W/HzK
    public final static double KdB = -228.6;        // dBW/HzK

    public final static double C = 2.99792458E8;
    public final static double RE = 6378.1E3;     // mean equatorial radius
    public final static String DEGREE = "\u00b0";

    // print 4 characters of a double number (format does not work)
    static String shortText(double num) {
        String s = String.valueOf(num);
        int len = s.length();
        if (len > 4) {
            len = 4;
        }
        return s.substring(0, len);
    }

    // convert degree, minute, second to radian.  Note it does not know about
    // North or South etc.   
    public static double toRadian(double d, double m, double s) {
        return Math.toRadians(d + m / 60.0 + s / 3600.0);
    }

    // from picmath site
    public static double erf(double x) {
        // constants
        final double a1 = 0.254829592;
        final double a2 = -0.284496736;
        final double a3 = 1.421413741;
        final double a4 = -1.453152027;
        final double a5 = 1.061405429;
        final double p = 0.3275911;

        // Save the sign of x
        double sign = 1;
        if (x < 0) {
            sign = -1;
        }
        x = Math.abs(x);

        // A&S formula 7.1.26
        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * MathUtil.exp(-x * x);

        return sign * y;
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
