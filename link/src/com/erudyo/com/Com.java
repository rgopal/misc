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

    
}
