/*
 * OVERVIEW:
 * represents common communications stuff across terminals for transponded
 * satellites.  In future, this could evolve into modem which could be different
 * for each terminal and a regenerative satellite.
 * 
 * Manage primary items such as FEC, Modulation, Data Rate, BW.  If Code, Mod,
 * or Code rate changes, then cBEP will change.  If someones sets a speciic cBEP
 * then constraint based optimzation is done which would then change mod, cod
 * directly using this (and not set which triggers other calculations, including
 * cBEP).
 * cBEP depends on mod, code, codeRate, and eBno
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
     * @return the indexBER
     */
    public static ArrayList<BER> getIndexBER() {
        return indexBER;
    }

    /**
     * @return the cBEP
     */
    public double getCbEP() {
        return cBEP;
    }

    /**
     * @param BEP the cBEP to set
     */
    public void setCbEP(double BEP) {
        this.cBEP = BEP;
    }

    /**
     * @return the eBno
     */
    public double geteBno() {
        return EbNo;
    }

    public double geteSno() {
        return EsNo;
    }

    /**
     * @param eBno the eBno to set
     */
    public double availableEsNo() {
        double value;
        // not so sure about spectral efficiency, so use Mary factor
        // value = eBno + 10 * MathUtil.log10(spectralEfficiency(this.modulation))
        value = this.EbNo + 10 * MathUtil.log10(getMaryFactor(this.modulation))
                + 10 * MathUtil.log10(calcCodeRate(codeRate));
        return value;
    }

    // TODO use it in optimization
    public void seteBno(double eBno) {
        this.EbNo = eBno;

        this.EsNo = availableEsNo();

        this.cBEP = calcBEPmod(this.modulation, this.EbcNo);
        this.setBEP(calcBEPmodCode(this.modulation, this.code,
                this.codeRate, this.cBEP));

        this.SEP = calcSEPmod(this.modulation, this.EsNo);
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

    /**
     * @return the CNo
     */
    public double getCNo() {
        return CNo;
    }

    /**
     * @param CNo the CNo to set
     */
    public void setCNo(double CNo) {
        this.CNo = CNo;
    }

    /**
     * @return the BER
     */
    public BER getbER() {
        return bER;
    }

    /**
     * @return the SEP
     */
    public double getSEP() {
        return SEP;
    }

    /**
     * @param SEP the SEP to set
     */
    public void setSEP(double SEP) {
        this.SEP = SEP;
    }

    /**
     * @return the EbcNo
     */
    public double getEbcNo() {
        return EbcNo;
    }

    /**
     * @param EbcNo the EbcNo to set
     */
    public void setEbcNo(double EbcNo) {
        this.EbcNo = EbcNo;
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
     * @param BER the BER to set
     */
    public enum BER {

        BER_7("1.0E-7"),
        BER_6("1.0E-6"),
        BER_5("1.0E-5"),
        BER_4("1.0E-4"),
        BER_3("1.0E-3"),
        BER_2("1.0E-2"),
        BER_1("1.0E-1"),
        BER_0("1.0E-0"),
        BER_N("N/A"),;

        private final String value;

        private BER(final String text) {
            this.value = text;
        }

        public String toString() {
            return value;
        }
        // this is not working

    };

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

    // can include DE-BPSK and DE-QPSK (closed form available) TODO
    public enum Modulation {

        BAM, BPSK, QPSK, QAM4, PSK8, QAM16, APSK16, APSK32, APSK64

    };
    private Path uLpath;
    private Path dLpath;
    private final static double EBNO_MIN = -5.0;    // in dB
    private final static double EBNO_MAX = 100.0;   // in dB

    final public int PACKET_BITS = 1500 * 8;   //12000 bits
    private double dataRate = 1E6;    // bps  
    private double rollOff = .30;
    private double bw = 1E6;      // Hz
    private double cBEP = 1E-6;
    private double BEP = 1E-6;      // information bit error
    private double SEP = 1E-6;      // coded bit error
    private BER bER = BER.BER_N;    // no explict BER set
    private double CNo;
    private double EbNo = Satellite.NEGLIGIBLE;           // information bit
    private double EsNo = Satellite.NEGLIGIBLE;          // symbol
    private double EbcNo = Satellite.NEGLIGIBLE;        // coded bit

    private double targetEbNo = Satellite.NEGLIGIBLE; // for BER set by user
    private double codingGain = 0;      // in dB
    private CodeRate codeRate = CodeRate.FEC_7_8;
    private Code code = Code.BCH;
    private Modulation modulation = Modulation.BPSK;

    final public static double DATA_RATE_LO = .1 * 1E6;  // in bps
    final public static double DATA_RATE_HI = 100.0 * 1E6; // in bps
    final public static double ROLL_OFF_LO = .05;
    final public static double ROLL_OFF_HI = 0.45;

    final public static double BW_LO = .1 * 1E6;
    final public static double BW_HI = 100.0 * 1E6;   // in Hz

    public double getTargetEbNo() {
        return targetEbNo;
    }

    public static int getMaryFactor(Modulation m) {

        switch (m) {
            case BAM:
            case BPSK:
                return 1;
            case QPSK:
            case QAM4:
                return 2;
            case PSK8:
                return 3;
            case APSK16:
            case QAM16:
                return 4;
            case APSK32:
                return 5;
            case APSK64:
                return 6;
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
    final public static Hashtable<String, BER> BERHash
            = new Hashtable<String, BER>();

    // lookup by index wifth class level vector to get
    // object by index (may be ID or some sort of sorting)
    final public static ArrayList<BER> indexBER
            = new ArrayList<BER>();

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

        for (BER m : BER.values()) {
            BERHash.put(m.toString(), m);
            indexBER.add(m);

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

    public void changeULname(String s) {
        this.name = s + "-" + this.dLpath.name;
    }

    public void changeDLname(String s) {
        this.name = this.uLpath.name + "-" + s;
    }

    // note that unlike satellite/terminal path UL/DL and Comms have the same
    // object for each selection
    public Comms(Path u, Path d) {
        this.uLpath = u;
        this.dLpath = d;
        this.name = "Comms:" + u.getName() + "-" + d.getName();

        // include this Path in the Affected list of satellite and terminal
        u.addAffected(this);
        d.addAffected(this);

        update(this);

    }

    public Comms(String s) {
        this.name = s;
    }

    public void update(Entity e) {
        // bw is updated here because of other factors)
        // data rate is set explicitly (using 
        this.bw = this.dataRate / (this.spectralEfficiency(this.modulation)
                * this.calcCodeRate(this.codeRate));
        this.CNo = availableCNo();
        this.EbNo = availableEbNo();      // information bit
        this.EsNo = availableEsNo();        // symbol
        this.EbcNo = availableEbcNo();   // coded bit

        this.cBEP = calcBEPmod(this.modulation, this.EbNo);

        this.BEP = calcBEPmodCode(this.modulation, this.code,
                this.codeRate, this.cBEP);
        this.SEP = calcSEPmod(this.modulation, this.EsNo);
        // derive EbNo if a BER is explicitly set
        if (this.bER != BER.BER_N) {
            this.targetEbNo = calcTargetEbNo(this.modulation, this.code,
                    this.codeRate, this.bER);
        }
        // highest so no updateAffected
    }

    // information bit rate (data rate)
    public double availableEbNo() {
        double value;
        // all in dB
        value = availableCNo() - 10.0 * MathUtil.log10(this.dataRate);
        return value;
    }

    // coded bit rate 
    public double availableEbcNo() {
        double value;
        value = availableCNo()
                - 10.0 * MathUtil.log10(calcCodedBitRate(
                                this.codeRate, this.dataRate));
        return value;
    }

    private double availableCNo() {
        double value;
        double first, second;

        first = MathUtil.pow(10.0, uLpath.getCNo() / 10.0);
        first = 1.0 / first;

        second = MathUtil.pow(10.0, dLpath.getCNo() / 10.0);
        second = 1.0 / second;

        value = 10.0 * MathUtil.log10(1.0 / (first + second));

        return value;
    }

    public double getDataRate() {
        return dataRate;
    }

    public CodeRate getCodeRate() {
        return this.codeRate;
    }

    public void setDataRate(double d) {
        this.dataRate = d;

        // update will also change bw
        // bandwidth will change because of data rate
        update(this);
    }

    // BEP for each modulation/code, ebno is in dB, and this is stateless
    // here cBEP is passed (instead of EbNo etc. in other such calcs)
    public static double calcBEPmodCode(Modulation m, Code code,
            CodeRate rate, double cBEP) {
        double value = 1.0;

        // use the EbcNo value to get cBEP and use that in the expression
        // for a specific coding scheme
        switch (code) {
            case BCH:
                // BCH is determined by N, K, T.  N = 2^m-1
                // Jordonava 16200 is close to 2^14 (m-14), m*T = N-K
                value = bepBCH(16200, 16008, 14, cBEP);
                break;
            case LDPC:
                break;
            default:
                break;
        }
        // TODO
        return .001;
    }

    // symbol error probably for M-ary Amplitude Modulation
    // Simon, Digital Communications, 8.3 full range
    public static double sepMAM(Modulation m, double EsNo) {
        double value;
        int M = calcM(m);
        // make sure it is 1.0 or else it will be integer arithmatic
        value = 2.0 * ((M - 1.0) / M);
        value = value
                * Com.Q(MathUtil.pow(6 * Com.reverseDB(EsNo) / (M * M - 1.0), 0.5));
        return value;

    }

    // Simon, eqn 8.10, general value
    public static double sepQAM(Modulation m, double EsNo) {
        double value;
        int M = calcM(m);

        double first = (MathUtil.pow(M, 0.5) - 1) / MathUtil.pow(M, 0.5);
        double second = Com.Q(MathUtil.pow(
                3 * Com.reverseDB(EsNo) / (M - 1), 0.5));

        value = 4 * first * second - 4 * first * first * second * second;

        return value;

    }

    public static double bepQAM(Modulation m, double EbNo) {
        double value;
        int M = calcM(m);

        double first = (MathUtil.pow(M, 0.5) - 1) / MathUtil.pow(M, 0.5);

        double sum = 0.0;

        int max;
        max = (int) MathUtil.pow(M, 0.5) / 2;

        for (int i = 1; i <= max; i++) {
            sum = sum + (2.0 * i - 1.0) * Com.Q(
                    MathUtil.pow(
                            3 * Com.reverseDB(EbNo) / (M - 1), 0.5));
        }

        value = 4.0 * first / (MathUtil.log(M) / MathUtil.log(2.0)) * sum;

        return value;

    }

    // Simon, Digital Comms, 8.14. better for for large EbNo
    public static double bepQAM4(Modulation m, double EbNo) {
        double value = 0.0;
        int M = calcM(m);
        value = 4 * (MathUtil.pow(M, 0.5) - 1.0) / MathUtil.pow(M, 0.5);
        value = value * (1.0 / (MathUtil.log(M) / MathUtil.log(2.0)));
        value = value * Com.Q(MathUtil.pow(
                3 * Com.reverseDB(EbNo)
                * (MathUtil.log(M) / MathUtil.log(2.0))
                / (M - 1.0), 0.5));
        return value;
    }

    // Simon, Digital Comms, eqn 8.25, approximation (upper bound)
    public static double sepPSK(Modulation m, double EsNo) {
        double value = 0.0;
        int M = calcM(m);

        // make sure it is 1.0 or else int arithmetic
        value = ((M - 1.0) / M) * MathUtil.exp(
                -Com.reverseDB(EsNo) * MathUtil.pow(Math.sin(Com.PI / M), 2.0));

        return value;
    }

    // Constellatoin and Mapping Optimization, 2014, Jordanova, Laskov
    // equation 8.2
    public static double sepAPSK(Modulation m, double EsNo) {
        double value = 0.0;
        int M = calcM(m);

        double out = 0.0;
     
        for (int i = 1; i <= M; i++) {
            // find the inner sum
            double inner = 0.0, weightedR = 0.0;
            for (int j = 1; j <= M; j++) {
                if (i == j) {
                    continue;
                }
                // note that to use EsNO
                if (m == Modulation.APSK16) {
                    // gamma1 is 2.6
                    weightedR = (4 + 12 * 2.6 * 2.6);
                } else if (m == Modulation.APSK32) {
                    // gamma1 is 2.54 and gamma2 is 4.33
                    weightedR = (4 + 12 * 2.54 * 2.54 + 16 * 4.33 * 4.33);
                }
                inner = inner
                        + Com.erfc(calcAPSKdij(i, j, m)
                                / 2.0
                                * MathUtil.pow(Com.reverseDB(EsNo)
                                        * (M / weightedR), 0.5)
                        )
                        / 2.0;
            }
            out = out + inner;
        }
        value = out / M;
        return value;

    }

    public static int calcM(Modulation m) {
        int mFactor = getMaryFactor(m);
        return (int) MathUtil.pow(2, mFactor);
    }

    // abstracted out r to get clean EsNo
    public static double calcAPSKdij(int i, int j, Modulation m) {

        double r = 1.0;  // to get clean EsNo
        int p = 0, q = 0;       // circles
        double rP = 0, rQ = 0;
        int nP = 0, nQ = 0;
        double phiP = 0, phiQ = 0;
        double phi1 = 45.0 * Com.PI / 180.0;
        double phi2 = 15.0 * Com.PI / 180.0;
        double phi3 = 0.0 * Com.PI / 180.0;
        int N = 2, n1 = 4, n2 = 12;

        double gamma1;

        double dIJ;

        if (m == Modulation.APSK16) {
            // for DVB-S2 system
            gamma1 = 2.6;

            if (i < 5) {
                p = 1;
                rP = r;
                nP = 4;
                phiP = phi1;
            } else {
                p = 2;
                rP = gamma1 * r;
                nP = 12;
                phiP = phi2;
            }
            if (j < 5) {
                q = 1;
                rQ = r;
                nQ = 4;
                phiQ = phi1;
            } else {
                q = 2;
                rQ = gamma1 * r;
                nQ = 12;
                phiQ = phi2;
            }

        } else if (m == Modulation.APSK32) {

            // additional parameters (gamma1 is different
            int n3 = 16;
            gamma1 = 2.54;
            double gamma2 = 4.33;

            if (i < 5) {
                p = 1;
                rP = r;
                nP = 4;
                phiP = phi1;
            } else if (i < 17) {
                p = 2;
                rP = gamma1 * r;
                nP = 12;
                phiP = phi2;
            } else {
                p = 3;
                rP = gamma2 * r;
                nP = 16;
                phiP = phi3;

            }

            if (j < 5) {
                q = 1;
                rQ = r;
                nQ = 4;
                phiQ = phi1;
            } else if (j < 17) {
                q = 2;
                rQ = gamma1 * r;
                nQ = 12;
                phiQ = phi2;
            } else {
                q = 3;
                rQ = gamma2 * r;
                nQ = 16;
                phiQ = phi3;
            }

        } else {
            Log.p("Comms: calcAPSKdij not implemented " + m, Log.WARNING);
        }

        dIJ = MathUtil.pow(rP * rP + rQ * rQ - 2.0 * rP * rQ
                * Math.cos(calcAPSKthetaIJ(i, j, nP, nQ, phiP, phiQ)), 0.5);
        return dIJ;
    }

    public static double calcAPSKthetaIJ(int i, int j, int nP, int nQ,
            double phiP, double phiQ) {

        double value = 0.0;
        value = Math.abs((phiP - phiQ)
                + 2.0
                * Com.PI
                * ((i - 1.0) / nP - (j - 1.0) / nQ));
        return value;
    }

    public static double bepBCH(int N, int K, int T, double p) {
        double value = 0.0;

        int i;

        for (i = T + 1; i <= N; i++) {

            value = value + i * Com.Combinatorial(N, i) * MathUtil.pow(p, i)
                    * MathUtil.pow(1 - p, N - i);
        }
        value = value / N;
        return value;
    }

    // Simon, Digital Comms, eqn 8.31, for large EbNo and M .4, approximation
    public static double bepPSK(Modulation m, double EbNo) {
        double value = 0.0;
        double sum = 0.0;
        int M;
        M = calcM(m);
        // should be 4.0 or else int arithmetic
        int max = (int) Math.max(M / 4.0, 1);
        int i;
        double first, second;
        for (i = 1; i <= max; i++) {
            first = 2.0 * Com.reverseDB(EbNo) * MathUtil.log(M) / MathUtil.log(2.0);
            first = MathUtil.pow(first, 0.5);
            second = Math.sin((2.0 * i - 1) * Com.PI / M);
            sum = sum + Com.Q(first * second);
        }

        value = (2.0 / Math.max(MathUtil.log(M) / MathUtil.log(2.0), 2.0)) * sum;
        return value;
    }

    public static double calcSEPmod(Modulation m, double EsNo) {
        double SEP = 0.0;
        switch (m) {
            case BAM:
                SEP = sepMAM(m, EsNo);
                break;
            case BPSK:
                SEP = sepPSK(m, EsNo);
                break;
            case QPSK:
                SEP = sepPSK(m, EsNo);
                break;
            case PSK8:
                SEP = sepPSK(m, EsNo);
                break;
            case QAM4:
                // Simon, Digital Comms eqn 8.11 for 4-QAM actually
                // M=4 for eqn 8.10 (general M-ary case)
                SEP = sepQAM(m, EsNo);
                break;

            case QAM16:
                SEP = sepQAM(m, EsNo);
                break;
            case APSK16:
                SEP = sepAPSK(m, EsNo);
                break;
            case APSK32:
                SEP = sepAPSK(m, EsNo);
            default:
                break;
        }
        return SEP;
    }

    // BER for each modulation, ebno is in dB
    public static double calcBEPmod(Modulation m, double EbNo) {
        double ber = 0.0;
        switch (m) {
            case BAM:
                ber = calcSEPmod(m, EbNo);  // log2M 
                break;
            case BPSK:
            // dont' use the general bepPSK (M=2,4)
            case QPSK:
                ber = (1 - Com.erf(MathUtil.pow(
                        MathUtil.pow(10.0, EbNo / 10.0), 0.5)))
                        / 2.0;
                break;
            case PSK8:
                ber = bepPSK(m, EbNo);
                break;
            case QAM4:
                ber = bepQAM(m, EbNo);
                break;
            case QAM16:
                ber = bepQAM(m, EbNo);
                break;
            case APSK16:
                // derive from corresponding EsNo
                ber = calcSEPmod(m, EbNo * getMaryFactor(m))
                        / getMaryFactor(m);
                break;
            case APSK32:
                ber = calcSEPmod(m, EbNo * getMaryFactor(m))
                        / getMaryFactor(m);
                break;
            default:

                break;

        }
        return ber;
    }

    public static double calcCodingGain(Modulation m, Code c, CodeRate r,
            Double BEP) {
        Double gain = 0.0;
        // right now hardcoded for cBEP 1E-6

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

        // table 4.7 for cBEP 1E-6 typical VITERBI CONV and perhaps BPSK (4.6)
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
        String text = c.toString();
        Vector<String> parts = (Vector) StringUtil.tokenize(text, "/");
        try {
            value = (Double.parseDouble(parts.get(0) + ".0")
                    / Double.parseDouble(parts.get(1) + ".0"));
        } catch (java.lang.NumberFormatException e) {
            Log.p("Comms: bad number " + c.toString(), Log.WARNING);

        }
        return value;
    }

    public static double calcCodedBitRate(CodeRate c, double dataRate) {
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
        update(this);

        this.setCodingGain(calcBEPmodCode(this.modulation, this.code,
                this.codeRate, this.cBEP));
        // change only bandwidth (and keep data rate fixed)
        this.bw = this.dataRate / (this.spectralEfficiency(this.modulation)
                * this.calcCodeRate(this.codeRate));
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
        update(this);

        // BER is generally not set and find the value
        // change only bandwidth (and keep data rate fixed)
        this.bw = this.dataRate / (this.spectralEfficiency(this.modulation)
                * this.calcCodeRate(this.codeRate));
        updateAffected();
    }

    /**
     * @param code the code to set
     */
    public void setCode(Code code) {
        this.code = code;
        update(this);

        this.setCodingGain(calcCodingGain(this.modulation, this.code,
                this.codeRate, this.cBEP));
        // change only bandwidth (and keep data rate fixed)

        updateAffected();
    }

    // determines targetEbNo if an explicit BER is set.  
    public void setbER(BER ber) {
        this.bER = ber;
        if (ber != BER.BER_N) {

            this.targetEbNo = calcTargetEbNo(this.modulation, this.code,
                    this.codeRate, this.bER);
        }

        this.setCodingGain(calcCodingGain(this.modulation, this.code,
                this.codeRate, this.cBEP));
        updateAffected();

    }

    // find an EbNo value in dB which can support the explicit BER
    public double calcTargetEbNo(Modulation m, Code c, CodeRate r, BER ber) {
        // all in dB
        double center = -100.0;
        double left = EBNO_MIN;
        double right = EBNO_MAX;
        Boolean notDone = true;

        int iterations = 0;
        double targetBEP = Double.parseDouble(ber.toString());

        // curves are monotonic
        do {

            iterations++;
            center = (left + right) / 2.0;
            // left, right, center should be in dB.  Uses cBEP of coded bit
            double bep = calcBEPmodCode(m, c, r, this.cBEP);

            if (bep < targetBEP) {
                right = center;
            } else {
                left = center;
            }

            if (Com.sameValue(bep, targetBEP) || iterations > 100) {
                notDone = false;
            }
            Log.p("Comms: calcTargetEbNo: (all in dBHz) left " + left + " center "
                    + center + " right " + right, Log.DEBUG);
        } while (notDone);

        return center;
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
        // bandwidth does not changes data rate (include code rate also)
        // this.dataRate = this.bw * spectralEfficiency(this.modulation)     * calcCodeRate(this.codeRate);
        // which changes EbNo, EsNo, cBEP, SEP
        update(this);

        // TODO coding gain check 
        // resulting in coding gain changes (circular because this changes ebno)
        this.setCodingGain(calcCodingGain(this.modulation, this.code,
                this.codeRate, this.cBEP));

    }
}
