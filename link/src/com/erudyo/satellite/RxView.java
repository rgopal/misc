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

    private Terminal terminal;

 
       
 

    public String getDisplayName() {
        return name;
    }

    public RxView(Terminal terminal) {
        this.terminal = terminal;
        this.name = terminal.getName();
         this.value = "ter";
        this.unit = "dB";
    }

    public Form createView() {
        Form rx = new Form(this.terminal.getName());

        rx.setScrollable(
                false);

        return rx;
    }
}
