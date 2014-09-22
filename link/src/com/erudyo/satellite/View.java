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
    
}
