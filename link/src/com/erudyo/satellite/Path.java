/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.satellite;

import com.codename1.util.MathUtil;
import java.lang.Math;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 * @author rgopal
 * ISO Standard is Long and then LAT,  North and East are positive.  Fine to use
 * all decimal after degree.
 */
public class Path extends Entity {
    private double R;     //distance of satellite from terminal
    
    public Path () {
        
    }
    public Path (String n) {
        super(n);
    }
    public Path (String n, String d, String s) {
        super(n,d,s);
    }

    public double calcPhi(Satellite s, Terminal t) {
        if (s == null || t == null)
            return 0;
        double Phi;
        double relativeLong;
        // get relative longitdue between satellite and terminal
        if (s.lambda > t.getPsi())
            relativeLong = s.getLambda() - t.getPsi();
        else
            relativeLong = t.getPsi() - s.getLambda() ;
        
        Phi = MathUtil.acos(Math.cos(relativeLong)*Math.cos(t.getL()));
        return Phi;
    }
    
    public double calcDistance(Satellite s, Terminal t) {
        double d;
        d = MathUtil.pow((1.0 + 0.42*(1.0 - Math.cos(calcPhi(s,t)))),0.5)*s.getR0();
        return d;
    }
    public double calcElevation(Satellite s, Terminal t) {
       
        double num;
        double denom;
        num = MathUtil.atan(Math.cos(calcPhi(s,t)));
        denom = MathUtil.pow(1.0 - Math.cos(calcPhi(s,t)), 0.5);
        return num/denom;
        
    }
    public double calcAzimuth(Satellite s, Terminal t) {
        double azimuth = 0.0;
        
        return azimuth;
    }
}
