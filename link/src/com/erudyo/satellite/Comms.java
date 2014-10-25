/*
 * OVERVIEW:
 * represents common communications stuff across terminals for transponded
 * satellites.  In future, this could evolve into modem which could be different
 * for each terminal and a regenerative satellite.
 * 
 * Manage primary items such as FEC, Modulation, Data Rate, BW.  If Code, Mod,
 * or Code rate changes, then BEP will change.  If someones sets a speciic BEP
 * then constraint based optimzation is done which would then change mod, cod
 * directly using this (and not set which triggers other calculations, including
 * BEP).
 * BEP depends on mod, code, codeRate, and eBno
 * EbNo depends on code rate and power (which is not present here).
 *
 * Comms itself does not depend on any child or siblint (so has no update()
 * processing.
 * TODO: 
 * 
 * Put it the end of the list of views and include data rate and bandwidth, BER
 * calculate total C/No and Eb/No and store in Comms.
 * 
 */
package com.erudyo.satellite;

import java.util.ArrayList;
import java.util.Hashtable;
import com.codename1.util.StringUtil;
import java.util.Vector;
import com.codename1.io.Log;

import com.codename1.util.MathUtil;

/**
 *
 * @author ubuntu
 */
public class Comms extends Entity {

    /**
     * @return the indexCode
     */
    public static ArrayList<Code> getIndexCode() {
        return indexCode;
    }

    /**
     * @return the BEP
     */
    public double getBEP() {
        return BEP;
    }

    /**
     * @param BEP the BEP to set
     */
    public void setBEP(double BEP) {
        this.BEP = BEP;
    }

    /**
     * @return the eBno
     */
    public double geteBno() {
        return eBno;
    }

    /**
     * @param eBno the eBno to set
     */
    public void seteBno(double eBno) {
        this.eBno = eBno;

        this.BEP = calcBEPmodCode(this.modulation, this.code,
                this.codeRate, this.geteBno());
        updateAffected();
    }

    /**
     * @return the codingGain
     */
    public double getCodingGain() {
        return codingGain;
    }

    /**
     * @param codingGain the codingGain to set
     */
    public void setCodingGain(double codingGain) {
        this.codingGain = codingGain;
        // do optimation to find a good collection
    }

    public enum CodeRate {

        FEC_1_9("1/9"),
        FEC_1_4("1/4"),
        FEC_1_3("1/3"),
        FEC_2_3("2/3"),
        FEC_4_5("4/5"),
        FEC_7_8("7/8"),
        FEC_8_9("8/9"),
        FEC_1_1("1/1");

        private final String value;

        private CodeRate(final String text) {
            this.value = text;
        }

        public String toString() {
            return value;
        }
        // this is not working

    };

    public enum Code {

        NONE,
        RS,
        BCH,
        LDPC,
        CONV, // convolutional
        BHLC        // concatenated in DVB-S2
    };

    public enum Modulation {

        BPSK, QPSK, PSK8, PSK16

    };
    private double dataRate = 10.0;    // Mbps  
    private double rollOff = .30;
    private double bw = 5;      // MHz
    private double BEP = 1E-6;
    private double eBno = 10;           // in dB
    private double codingGain = 0;      // in dB
    private CodeRate codeRate;
    private Code code;
    private Modulation modulation;

    final public static double DATA_RATE_LO = .1;
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
        return getMaryFactor(m) / (1.0 + getRollOff());
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
    final public static Hashtable<String, CodeRate> codeRateHash
            = new Hashtable<String, CodeRate>();

    // lookup by index wifth class level vector to get
    // object by index (may be ID or some sort of sorting)
    final public static ArrayList<CodeRate> indexCodeRate
            = new ArrayList<CodeRate>();

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

        for (Modulation m : Modulation.values()) {
            modulationHash.put(m.toString(), m);
            indexModulation.add(m);

        }

        for (CodeRate c : CodeRate.values()) {
            codeRateHash.put(c.toString(), c);
            indexCodeRate.add(c);
        }

        for (Code c : Code.values()) {
            codeHash.put(c.toString(), c);
            indexCode.add(c);
        }

    }

    public Comms() {

    }

    public Comms(String s) {
        this.name = s;
    }

    public double getDataRate() {
        return dataRate;
    }

    public CodeRate getCodeRate() {
        return this.codeRate;
    }

    public void setDataRate(double d) {
        this.dataRate = d;
    }

    // BER for each modulation, ebno is in dB, and this is stateless
    public static double calcBEPmodCode(Modulation m, Code code,
            CodeRate rate, Double ebno) {
        double ber;
        switch (m) {
            case BPSK:
            case QPSK:
                //
                ber = (1 - Com.erf(MathUtil.pow(
                        MathUtil.pow(10.0, ebno / 10.0), 0.5)))
                        / 2.0;
                break;

            default:
                ber = (1 - Com.erf(MathUtil.pow(
                        MathUtil.pow(10.0, ebno / 10.0), 0.5)
                ));

                break;

        }
        return ber;
    }

    // BER for each modulation, ebno is in dB
    public static double calcBEPmod(Modulation m, Double ebno) {
        double ber;
        switch (m) {
            case BPSK:
            case QPSK:
                //
                ber = (1 - Com.erf(MathUtil.pow(
                        MathUtil.pow(10.0, ebno / 10.0), 0.5)))
                        / 2.0;
                break;

            default:
                ber = (1 - Com.erf(MathUtil.pow(
                        MathUtil.pow(10.0, ebno / 10.0), 0.5)
                ));

                break;

        }
        return ber;
    }

    public static double calcCodingGain(Modulation m, Code c, CodeRate r,
            Double BEP) {
        Double gain = 0.0;
        // right now hardcoded for BEP 1E-6

        Double rate = calcCodeRate(r);

        if (m != Modulation.BPSK) {
            Log.p("Comms: calcDecodingGain modulation not "
                    + m, Log.DEBUG);
        }
        if (!Com.sameValue(BEP, 1E-6)) {
            Log.p("Comms: calcDecodingGain BEP not "
                    + String.valueOf(BEP), Log.DEBUG);
        }

        if (c != Code.CONV) {
            Log.p("Comms: calcDecodingGain code not "
                    + c, Log.DEBUG);
        }

        // table 4.7 for BEP 1E-6 typical VITERBI CONV and perhaps BPSK (4.6)
        if (rate < 1 / 2) {
            gain = 6.0;
        } else if (rate > 1 / 2 && rate < 2 / 3) {
            gain = 5.0;
        } else if (rate > 2 / 3 && rate < 3 / 4) {
            gain = 4.6;
        } else if (rate > 3 / 4 && rate < 7 / 8) {
            gain = 3.6;
        } else if (rate < 1) {
            gain = 0.0;
        }
        return gain;

    }

    /**
     * @return the modulation
     */
    public static double calcCodeRate(CodeRate c) {
        double value = 0;
        // get the numerator and denominator from text string n/(n+r)
        String text = c.name().toString();
        Vector<String> parts = (Vector) StringUtil.tokenize(text, "/");
        try {
            value = (Double.parseDouble(parts.get(0))
                    / Double.parseDouble(parts.get(1)));
        } catch (java.lang.NumberFormatException e) {
            Log.p("Comms: bad number " + c.toString(), Log.WARNING);

        }
        return value;
    }

    public double calcCodedBitRate(CodeRate c, double dataRate) {
        return dataRate / calcCodeRate(c);
    }

    // power reduction is same as decoding gain for variable bandwidth
    // power reduction is extra -10 log (codeRate) for fixed bandwidth
    // since the bit rate goes lower
    public Modulation getModulation() {
        return modulation;
    }

    /**
     * @param modulation the modulation to set
     */
    public void setModulation(Modulation modulation) {
        this.modulation = modulation;

        this.BEP = calcBEPmodCode(this.modulation, this.code,
                this.codeRate, this.geteBno());
        this.setCodingGain(calcBEPmodCode(this.modulation, this.code,
                this.codeRate, this.BEP));
        updateAffected();
    }

    /**
     * @return the code
     */
    public Code getCode() {
        return code;
    }

    public void setCodeRate(CodeRate c) {
        this.codeRate = c;

        this.BEP = calcBEPmodCode(this.modulation, this.code,
                this.codeRate, this.geteBno());
        this.setCodingGain(calcBEPmodCode(this.modulation, this.code,
                this.codeRate, this.BEP));
        updateAffected();
    }

    /**
     * @param code the code to set
     */
    public void setCode(Code code) {
        this.code = code;

        this.BEP = calcBEPmodCode(this.modulation, this.code,
                this.codeRate, this.eBno);

        this.setCodingGain(calcBEPmodCode(this.modulation, this.code,
                this.codeRate, this.BEP));
        updateAffected();
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
