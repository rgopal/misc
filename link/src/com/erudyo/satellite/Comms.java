/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import java.util.ArrayList;
import java.util.Hashtable;
import com.erudyo.satellite.Com.Modulation;
import com.codename1.util.MathUtil;

/**
 *
 * @author ubuntu
 */
public class Comms extends Entity {
    private double dataRate = 10.0;    // Mbps  
    private double rollOff = .30;
    private double bw = 5;      // MHz
    private Com.Code code;
    private Com.Modulation modulation;
    
    final public static double  DATA_RATE_LO = .1;
    final public static double DATA_RATE_HI = 100; 
    final public static double ROLL_OFF_LO = 0.05;
    final public static double ROLL_OFF_HI = 0.45;
 
      final public static double BW_LO = .05;
    final public static double BW_HI = 100.0;
   
    public int getMaryFactor(Modulation m) {
       
       switch (m) {
           case BPSK:
               return 1;
           case QPSK:
               return 2;
           case PSK8:
               return 3;
           case PSK16:
               return 4;
           default:
               return 1;
       }
    }   
    
    
    public double spectralEfficiency(Modulation m) {
        return getMaryFactor(m)/(1.0+getRollOff());
    }
    
    
       // lookup by String name with class level table
    // could be used to get an object by name
    final public static Hashtable<String, Com.Modulation> modulationHash
            = new Hashtable<String, Com.Modulation>();

    // lookup by index with class level vector to get
    // object by index (may be ID or some sort of sorting)
    final public static ArrayList<Com.Modulation> indexModulation
            = new ArrayList<Com.Modulation>();
    
    
      // lookup by String name with class level table
    // could be used to get an object by name
    final public static Hashtable<String, Com.Code> codeHash
            = new Hashtable<String, Com.Code>();

    // lookup by index with class level vector to get
    // object by index (may be ID or some sort of sorting)
    final public static ArrayList<Com.Code> indexCode
            = new ArrayList<Com.Code>();
    
    // create global hash and array of modulations and codes
    static {
        
        for (Com.Modulation m: Com.Modulation.values()) {
            modulationHash.put(m.toString(),m);
            indexModulation.add(m);
                    }
        
        for (Com.Code c : Com.Code.values()) {
            codeHash.put(c.toString(), c);
            indexCode.add(c);
        }
    
    }
    public Comms()
    {
        
    }
    public Comms (String s) {
        this.name = s;
    }
    public double getDataRate() {
        return dataRate;
    }
    public void setDataRate(double d) {
        this.dataRate = d;
    }

    // BER for each modulation
    public static double getBEP(Com.Modulation m, Double ebno) {
        double ber;
        switch(m) {
            case BPSK:
            case QPSK:
                // does not match satellite book (excel erfc.precise wrong too?)
                ber = (1-Com.erf(MathUtil.pow(ebno,0.5)))/2.0;
                break;
            
            default:
                ber = (1-Com.erf(MathUtil.pow(ebno,0.5)));
                break;
                
        }
        return ber;
    }
    /**
     * @return the modulation
     */
    public Com.Modulation getModulation() {
        return modulation;
    }

    /**
     * @param modulation the modulation to set
     */
    public void setModulation(Com.Modulation modulation) {
        this.modulation = modulation;
    }

    /**
     * @return the code
     */
    public Com.Code getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Com.Code code) {
        this.code = code;
    }

    /**
     * @return the rollOff
     */
    public double getRollOff() {
        return rollOff;
    }

    /**
     * @param rollOff the rollOff to set
     */
    public void setRollOff(double rollOff) {
        this.rollOff = rollOff;
    }

    /**
     * @return the bw
     */
    public double getBW() {
        return bw;
    }

    /**
     * @param bw the bw to set
     */
    public void setBW(double bw) {
        this.bw = bw;
    }
}
