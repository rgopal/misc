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
    private String shortName;
    protected String summary;
    protected String value;
    protected String subValue;
    
    private Label widget = new Label();
    private Label label = new Label();
    private Label subLabel = new Label();
    private Label subWidget = new Label();
    
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
        return widget;
    }
    
    public void setName(String name) {
        this.widget.setText(name);
        this.name = name;
    }
    
     public void setSummary(String name) {
        this.subWidget.setText(name);
        this.summary = name;
    }
     
      public void setValue(String name) {
        this.label.setText(name);
        this.value = name;
    }
      
       public void setSubValue(String name) {
        this.subLabel.setText(name);
        this.subValue = name;
    }
    // some could have no second widget so just summary
    public Component getSubWidget(Selection selection) {
        return subWidget;
    }
    
    public  Component getLabel(Selection selection) {
        return label;
    }
    
    // some could have no second widget so just summary (probably not used)
    public Component getSubLabel(Selection selection) {
        return subLabel;
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
     * @return the unit
     */
    public String getSubValue() {
        return subValue;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.subValue = unit;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

 
    
}
