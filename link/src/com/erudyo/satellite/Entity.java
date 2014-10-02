/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erudyo.satellite;

import java.util.ArrayList;

/**
 * Copyright (c) 2014 R. Gopal. All Rights Reserved.
 *
 * @author rgopal
 */
public class Entity {

    protected String name;
    protected String description;
    protected String summary;
    private ArrayList<Entity> affected;        // list of objects affected by it

    // static variables are shared by all sub-classes so have individual copies
    // protected static int index = 0;
    /**
     * @return the name
     */
    // Parent can set properties of a child which can trigger an update for
    // the parent itself.  When parent creates a child then it also places itself
    // in the affects list of the child.   The child calls updateAffected to go
    // through all objects affected by it, and calls their respective update 
    // method.
    final public void updateAffected() {
        // go through the list 
        if (affected != null) {
            for (Entity a : affected) {
                //  call update function of each object affected by it
                a.update(this);
            }
        }

    }

    // called by child or sibling of an entity (see updateAffected) and customized
    public void update(Entity e) {
        // keep all local udpates (and possibly call your own updateAffected

    }

    final public void addAffected(Entity e) {
        if (affected == null) {
            affected = new ArrayList<Entity>();
        }
        affected.add(e);

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
