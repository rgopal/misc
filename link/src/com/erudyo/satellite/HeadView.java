/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erudyo.satellite;

import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.util.Resources;

public class HeadView extends View {

    Terminal terminal;
    
  

    public HeadView() {
        
        this.name = "";
        this.summary = "";
        
        this.value = "";
        this.unit = "Unit";
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getDisplayName() {
        return name;
    }

    public Form createView(Selection selection) {
        Form tx = new Form(this.name);

    
        tx.setScrollable(
                true);
            // override pointerPressed to locate new positions 

        return tx;
    }
}
