/*
 * OVERVIEW:
 * represents common communications stuff across terminals for transponded
 * satellites.  In future, this could evolve into modem which could be different
 * for each terminal and a regenerative satellite.
 * TODO: Add full coding/modulation processing
 */
package com.erudyo.satellite;

import java.util.ArrayList;
import java.util.Hashtable;

import com.codename1.util.MathUtil;

/**
 *
 * @author ubuntu
 */
public class Comms extends Entity {
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
    private double dataRate = 10.0;    // Mbps  
    private double rollOff = .30;
    private double bw = 5;      // MHz
    private Code code;
    private Modulation modulation;
    
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
    final public static Hashtable<String, Modulation> modulationHash
            = new Hashtable<String, Modulation>();

    // lookup by index with class level vector to get
    // object by index (may be ID or some sort of sorting)
    final public static ArrayList<Modulation> indexModulation
            = new ArrayList<Modulation>();
    
    
      // lookup by String name with class level table
    // could be used to get an object by name
    final public static Hashtable<String, Code> codeHash
            = new Hashtable<String, Code>();

    // lookup by index with class level vector to get
    // object by index (may be ID or some sort of sorting)
    final public static ArrayList<Code> indexCode
            = new ArrayList<Code>();
    
    // create global hash and array of modulations and codes
    static {
        
        for (Modulation m: Modulation.values()) {
            modulationHash.put(m.toString(),m);
            indexModulation.add(m);
                    }
        
        for (Code c : Code.values()) {
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
    public static double getBEP(Modulation m, Double ebno) {
        double ber;
        switch(m) {
            case BPSK:
            case QPSK:
                //
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
    public Modulation getModulation() {
        return modulation;
    }

    /**
     * @param modulation the modulation to set
     */
    public void setModulation(Modulation modulation) {
        this.modulation = modulation;
    }

    /**
     * @return the code
     */
    public Code getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Code code) {
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
