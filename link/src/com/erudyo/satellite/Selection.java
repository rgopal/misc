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
public class Selection {
    private Terminal tXterminal;
    private Terminal rXterminal;
    private Satellite satellite;
    private Path uLpath;
    private Path dLpath;
    private Satellite[] satellites;
    private Terminal[] terminals;
  

    /**
     * @return the tXterminal
     */
    public Terminal gettXterminal() {
        return tXterminal;
    }

    /**
     * @param tXterminal the tXterminal to set
     */
    public void settXterminal(Terminal tXterminal) {
        this.tXterminal = tXterminal;
    }

    /**
     * @return the rXterminal
     */
    public Terminal getrXterminal() {
        return rXterminal;
    }

    /**
     * @param rXterminal the rXterminal to set
     */
    public void setrXterminal(Terminal rXterminal) {
        this.rXterminal = rXterminal;
    }

    /**
     * @return the satellite
     */
    public Satellite getSatellite() {
        return satellite;
    }

    /**
     * @param satellite the satellite to set
     */
    public void setSatellite(Satellite satellite) {
        this.satellite = satellite;
    }

    /**
     * @return the uLpath
     */
    public Path getuLpath() {
        return uLpath;
    }

    /**
     * @param uLpath the uLpath to set
     */
    public void setuLpath(Path uLpath) {
        this.uLpath = uLpath;
    }

    /**
     * @return the dLpath
     */
    public Path getdLpath() {
        return dLpath;
    }

    /**
     * @param dLpath the dLpath to set
     */
    public void setdLpath(Path dLpath) {
        this.dLpath = dLpath;
    }

    /**
     * @return the satellites
     */
    public Satellite[] getSatellites() {
        return satellites;
    }

    /**
     * @param satellites the satellites to set
     */
    public void setSatellites(String[][] satellites) {
        
    }

    /**
     * @return the terminals
     */
    public Terminal[] getTerminals() {
        return terminals;
    }

    /**
     * @param terminals the terminals to set
     */
    public void setTerminals(String[][] terminals) {
        
    }

}
