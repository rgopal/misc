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

public abstract class View {

    private Resources res;

    public void init(Resources res) {
        this.res = res;
    }

    protected Resources getResources() {
        return res;
    }

    public abstract void addBackCommand(Link.BackCommand bc);

    public abstract String getDisplayName();

    public abstract Image getViewIcon();

    public abstract Form createView();
    
}
