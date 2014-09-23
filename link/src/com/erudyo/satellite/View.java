/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.util.Resources;
import java.io.IOException;

public abstract class View {

    private Resources res;
    protected Image icon;
    protected String name;
    protected String value;
    protected String unit;

    public String getName () {
        return name;
    }
    public View(String name) {
        this.name = name;
    }
    public void init(Resources res) {
        this.res = res;
    }

    public View () {
        
    }
    public View (Object obj) {
        
    }
    protected Resources getResources() {
        return res;
    }

    public abstract String getDisplayName();

 
     public Image getViewIcon() {
         Image icon = null;
        if (icon == null) {
            // change it to better icon in future
            try {
                icon = Image.createImage(this.name + ".png");
                return icon;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            return icon;
        }
        return icon;
    }

    public abstract Form createView();

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
}
