/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.com;

/**
 *
 * @author rgopal
 */
public class Com {
    public enum Band {C, X, KU, KA, KA2, X_DL, X_UL, C_DL, C_UL, KU_DL, KU_UL, KA_DL, KA_UL, KA2_DL, KA2_UL};
    public enum Orbit {LEO, MEO, GEO};
    public enum Code {FEC_1_2, FEC_2_3, FEC_4_5, FEC_8_9};
    public enum Modulation {BPSK, QPSK, PSK8, PSK16};
    public final static double PI = 3.14159;
    public final static double C = 2.99792458E8;
    
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

    static Band findBand(double f) {
        Com.Band band = Band.C;
        if (f >= Com.C_DL_LO && f <= Com.C_DL_HI)
            band = (Com.Band.C_DL);
        else if (f >= Com.C_UL_LO && f <= Com.C_UL_HI )
            band = (Com.Band.C_UL);
        else if (f >= Com.X_DL_LO && f <= Com.X_DL_HI)
            band = (Com.Band.X_DL);
        else if (f >= Com.X_UL_LO && f <= Com.X_UL_HI )
            band = (Com.Band.X_UL);
        else if (f >= Com.KU_DL_LO && f <= Com.KU_DL_HI)
            band = (Com.Band.KU_DL);
        else if (f >= Com.KU_UL_LO && f <= Com.KU_UL_HI )
            band = (Com.Band.KU_UL);
        else    if (f >= Com.KA_DL_LO && f <= Com.KA_DL_HI)
            band = (Com.Band.KA_DL);
        else if (f >= Com.KA_UL_LO && f <= Com.KA_UL_HI )
            band = (Com.Band.KA_UL);
        else if (f >= Com.KA_DL_LO && f <= Com.KA_DL_HI)
            band = (Com.Band.KA_DL);
        else if (f >= Com.KA2_UL_LO && f <= Com.KA2_UL_HI )
            band = (Com.Band.KA2_UL);
        return band;
    }
    public static double centerFrequency(Band band) {
        double f;
        switch (band) {
            case X_DL:
                f = (Com.X_DL_HI + Com.X_DL_LO)/2.0;
                break;
            case X_UL:
                f = (Com.X_UL_HI + Com.X_UL_LO)/2.0;
                break;
            case C_DL:
                f = (Com.C_DL_HI + Com.C_DL_LO)/2.0;
                break;
            case C_UL:
                f = (Com.C_UL_HI + Com.C_UL_LO)/2.0;
                break;
            case KA_DL:
                f = (Com.KA_DL_HI + Com.KA_DL_LO)/2.0;
                break;
            case KA_UL:
                f = (Com.KA_UL_HI + Com.KA_UL_LO)/2.0;
                break;
             case KA2_DL:
                f = (Com.KA2_DL_HI + Com.KA2_DL_LO)/2.0;
                break;
            case KA2_UL:
                f = (Com.KA2_UL_HI + Com.KA2_UL_LO)/2.0;
                break;
            case KU_DL:
                f = (Com.KU_DL_HI + Com.KU_DL_LO)/2.0;
                break;
            case KU_UL:
                f = (Com.KU_UL_HI + Com.KU_UL_LO)/2.0;
                break;
            case KA:
                f = (Com.KA_HI + Com.KA_LO)/2.0;
                break;
            case KU:
                f = (Com.KU_HI + Com.KU_LO)/2.0;
                break;
            default:
                f = 0;
        }
        return f;
    }
}
