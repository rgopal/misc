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

public class UlPathView extends View {
static Path  path;
    
    public UlPathView () {
        
    }
    public UlPathView(Selection selection) {
      this.path = selection.getuLpath();
      this.name = path.getName();
      this.value = "-200";
      this.unit = "dB";
      
    }

    public String getDisplayName() {
        return name;
    }

    public Form createView() {
       
        Form path = new Form(this.path.getName());

        path.setScrollable(
                false);
            // override pointerPressed to locate new positions 

        return path;
    }
}
