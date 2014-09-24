/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.satellite;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 * @author rgopal
 */
  public class BandParams {

        public double lowFrequency;
        public double highFrequency;
        public double centerFrequency;
        public Com.Band band;

        public BandParams(double low, double hi) {
            lowFrequency = low;
            highFrequency = hi;
            centerFrequency = (low + hi) / 2.0;
        }
    }