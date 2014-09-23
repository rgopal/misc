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

public class TxView extends View {

    Terminal terminal;
    
    public TxView() {

    }


    public TxView(Terminal terminal) {
        this.terminal = terminal;
        this.name = terminal.getName();
        this.value = "23";
        this.unit = "dB";
       
        
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getDisplayName() {
        return name;
    }

    public Form createView() {
        Form tx = new Form(this.terminal.getName());

    
        tx.setScrollable(
                true);
            // override pointerPressed to locate new positions 

        return tx;
    }
}
