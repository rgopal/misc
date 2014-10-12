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
import com.codename1.ui.Component;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.Image;
import com.codename1.ui.util.Resources;
import java.io.IOException;
import java.util.ArrayList;

public abstract class View {

    private Resources res;
    protected Image icon;
    protected String name;
    protected String summary;
    protected String value;
    protected String unit;
    
    // store print ready items of a view
    protected ArrayList<ArrayList<String>> text;
    
    public ArrayList<ArrayList<String>> getText(Selection selection) {
        return text;
    }
    public String getName () {
        return name;
    }
    // some could have no widget so just the name
    public  Component getWidget(Selection selection) {
        return (new Label (getName()));
    }
    
    // some could have no second widget so just summary
    public Component getSubWidget(Selection selection) {
        return (new Label (getSummary()));
    }
    
     public  Component getLabel(Selection selection) {
        return (new Label (getValue()));
    }
    
    // some could have no second widget so just summary (probably not used)
    public Component getSubLabel(Selection selection) {
        return (new Label (getUnit()));
    }
     
    public View(String name) {
        this.name = name;
    }
    public void init(Resources res) {
        this.res = res;
    }

    public View () {
        
    }
    
    // this will mostly get selection and then respective views will select
    // the appropriate components (satellite, rx, or tx etc.
    
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

    public abstract Form createView(Selection selection);

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

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
}
