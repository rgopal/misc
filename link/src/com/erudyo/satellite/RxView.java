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

public class RxView extends View {

    public RxView(String name) {
        super(name);
    }

    public String getDisplayName() {
        return name;
    }

    public Form createView() {
        Form rx = new Form(getName());

        rx.setScrollable(
                false);
            // override pointerPressed to locate new positions 

      
        return rx;
    }
}
