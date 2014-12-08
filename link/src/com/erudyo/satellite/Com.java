/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import com.codename1.io.Log;
import com.codename1.processing.Result;
import com.codename1.ui.Image;
import com.codename1.ui.Slider;
import com.codename1.util.MathUtil;
import com.codename1.util.StringUtil;
import java.lang.Math;
import java.text.Format;
import java.util.ArrayList;
import java.util.Hashtable;

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

    public static Image blue_pin;
    public static Image red_pin;
    public final static double PI = Math.PI;
    public final static double e = 2.7182818;
    public final static double K = 1.379E-23;       // Boltzmann constant, W/HzK
    public final static double KdB = -228.6;        // dBW/HzK

    public final static double C = 2.99792458E8;
    public final static double RE = 6378.1E3;     // mean equatorial radius
    public final static String DEGREE = "\u00b0";
    public final static double VISIBLE_ANGLE = 80.0 * PI / 180.0;

    public final static double T0 = 290.0;

    // print 4 characters of a double number (format does not work)
    static String shortText(double num) {
        String s = String.valueOf(num);
        int len = s.length();
        if (len > 4) {
            len = 4;
        }
        return s.substring(0, len);
    }

    public static long CombinatorialLong(int n, int k) {
        long b;

        b = 1;
        if (k > n - k) {
            k = n - k;
        }

        for (int i = 1, m = n; i <= k; m--) {
            b = b * m / i;
        }
        return b;
    }

    public static double Combinatorial(int n, int k) {
        double b;

            // sterlings approximation ln(n!) = n*ln(n) - n
        // n!/k!*(n-k)! = n*ln(n) -n - k*ln(k) - (n-k)*ln(n-k) +k +n -k
        double logVal = n * MathUtil.log(n) - k * MathUtil.log(k)
                - (n - k) * MathUtil.log(n - k);
        b = (long) MathUtil.pow(Math.E, logVal);

        return b;

    }

    static void formatSlider(Slider sldr) {

        sldr.setThumbImage(Com.red_pin);
        sldr.getUnselectedStyle().setBgColor(0x0000FF);
        sldr.getUnselectedStyle().setFgColor(0x0000FF);  // blue
        sldr.getUnselectedStyle().setBgTransparency(30);
        sldr.getSelectedStyle().setBgColor(0x0000FF);
        sldr.getSelectedStyle().setFgColor(0x0000FF); // blue
        sldr.getSelectedStyle().setBgTransparency(30);
        sldr.setPreferredH(40);
    }

    static String text(double num) {
        String s = String.valueOf(num);
        int len = s.length();
        if (len > 8) {
            len = 8;
        }
        return s.substring(0, len);
    }

    static String textN(double num, int length) {
        String s = String.valueOf(num);
        int len = s.length();
        if (len > length) {
            len = length;
        }
        return s.substring(0, len);
    }

    // convert degree, minute, second to radian.  Note it does not know about
    // North or South etc.   
    public static double toRadian(double d, double m, double s) {
        return Math.toRadians(d + m / 60.0 + s / 3600.0);
    }

    public static String removeNonNum(String s) {

        return StringUtil.replaceAll(StringUtil.replaceAll(StringUtil.
                replaceAll(s, "'", ""), "\n", ""), " ", "");
    }

    public static String SwapBlueRed(String s) {
        // KML uses BGR vs Java which has RGB (KML has two more opacity Hex upfront
        if (s.length() > 6) {
            Log.p("Com: SwapBlueRed found larger than 6 color " + s, Log.WARNING);
        }
        return (s.substring(4, 6) + s.substring(2, 4) + s.substring(0, 2));
    }

    public static String removeQuoteEol(String s) {

        return (StringUtil.replaceAll(StringUtil.
                replaceAll(s, "'", ""), "\n", ""));
    }
    // from picmath site

    public static double erfc(double x) {
        return (1 - erf(x));
    }

    public static double Q(double x) {
        double value = (erfc(x / MathUtil.pow(2, 0.5)) / 2.0);
        return value;
    }

    // undo DB 
    public static double reverseDB(double db) {
        return MathUtil.pow(10.0, db / 10.0);
    }

    public static String textD(double d) {

        String s = String.valueOf(d);
        int index = s.indexOf("E");

        if (index > 0) {
            // if in exponenet form

            String mantissa = s.substring(0, index);
            String exp = s.substring(index);
            // reduce mantissa to 6 characters
            int min = Math.min(6, mantissa.length());
            mantissa = mantissa.substring(0, min);
            s = mantissa + exp;

        } else {
            int min = Math.min(8, s.length());

            s = s.substring(0, min);
        }
        return s;
    }

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
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1)
                * t * MathUtil.exp(-x * x);

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
        // difference is within .001 percent of first
        if (Math.abs(one - two) < .00001 * Math.abs(one)) {
            return true;
        } else {
            return false;
        }
    }

}
