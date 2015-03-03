/*
 * OVERVIEW 
 * Common class for most other classes to get name, description, and common
 * processing for automated updates when a child or sibling changes its
 * state
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
    // in each class definition
  
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
                     Log.p("Entity: Updating Object " + a + " because of " + this, Log.DEBUG);
                     a.update(this);             
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
        Log.p("Entity: update not implemented for " + this, Log.WARNING);
                

    }

    // if entity already exists then increment its reference count,
    // else create a new enty in the affected hashtable
    final public void addAffected(Entity e) {
        if (affected == null) {
            affected = new Hashtable<Entity, Integer>();
        }
        if (affected.get(e) != null) {
            Log.p("Entity: addAffected reference value will increase by 1, current value = "
                    + affected.get(e) + " in affected list of " + this, Log.DEBUG);
            affected.put(e, affected.get(e) + 1);
        } else {
            affected.put(e, 1);
            Log.p("Entity: Added Object " + e + " to affected list of "
                    + this, Log.DEBUG);
        }

    }
    
    // remove an entity from the list.  It actually decrements the reference
    // count and logs warnings if there is any unexpected behavior
    
    final public void removeAffected(Entity e) {
        if (affected == null) {
            Log.p("Entity: affected is NULL", Log.WARNING);
            return;
        }
        if (affected.get(e) == null) {
            Log.p("Entity: Can't find entity " + e + " in affected list of " +
                    this, Log.WARNING);
        } else {
            if (affected.get(e) < 1)
                 Log.p("Entity: Reference is already 0 for " + e +
                         " in affected list of " + this, Log.WARNING);
            else {
                // decrement reference count
                affected.put(e, affected.get(e) - 1);
                Log.p("Entity: Decremented reference by 1 for " + 
                        e + " in affected list of " + this , Log.DEBUG);
                
            }
        } 
    }

    public Entity() {

    }

    public Entity(String n) {
        name = n;

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

    public String toString() {
        return name;
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
