/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import com.codename1.io.Log;
import java.util.Hashtable;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Entity {

    protected String name;
    protected String description;
    protected String summary;
    private Hashtable<Entity, Integer> affected;
    // Map of objects (and reference count affected by it

    // static variables are shared by all sub-classes so have individual copies
    // protected static int index = 0;
  
    // Parent can set properties of a child which can trigger an update for
    // the parent itself.  When parent creates a child then it also places itself
    // in the affects list of the child.   The child calls updateAffected to go
    // through all objects affected by it, and calls their respective update 
    // method.
    
    // It is possible that same object appears twice (for example same tx,rx
    // terminal) for a parent entity.  Then update will be called once because
    // of reference counts.  Also, if one instance changes, that will affect
    // in two places.
    final public void updateAffected() {
        // go through the list 
        if (affected != null) {
            // find each entity with reference count > 0
            for (Entity a : affected.keySet()) {
                //  call update function of each object affected by it
                if (affected.get(a) > 0) {
                    a.update(this);
                    Log.p("Entity: Updating Object " + a + " because of " + this, Log.DEBUG);
                } else
                    Log.p("Entity: Reference count for " + a +
                            " is 0 so don't update because of " 
                            + this, Log.DEBUG);
            }
        }

    }

    // called by child or sibling of an entity (see updateAffected) and customized
    public void update(Entity e) {
        // keep all local udpates (and possibly call your own updateAffected

    }

    // if entity already exists then increment its reference count,
    // else create a new enty in the affected hashtable
    final public void addAffected(Entity e) {
        if (affected == null) {
            affected = new Hashtable<Entity, Integer>();
        }
        if (affected.get(e) != null) {
            Log.p("Entity: addAffected reference value will by 1"
                    + affected.get(e));
            affected.put(e, affected.get(e) + 1);
        } else {
            affected.put(e, 1);
            Log.p("Entity: Added Object " + e + " to Affected list of "
                    + this, Log.DEBUG);
        }

    }
    
     // remove an entity from the list.  It actually decrements the reference
    // count
    
    final public void removeAffected(Entity e) {
        if (affected == null) {
            Log.p("Entity: affected is NULL", Log.ERROR);
            return;
        }
        if (affected.get(e) == null) {
            Log.p("Entity: Can't find entity " + e, Log.WARNING);
        } else {
            if (affected.get(e) < 1)
                 Log.p("Entity: Reference is already 0 for " + e, Log.WARNING);
            else {
                // decrement reference count
                affected.put(e, affected.get(e) - 1);
            }
        } 
    }

    public Entity() {

    }

    public Entity(String n) {
        name = n;

    }

    public Entity(String n, String d, String s) {
        name = n;
        description = d;
        summary = s;
    }

    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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

    /**
     * @return the index
     */
}
