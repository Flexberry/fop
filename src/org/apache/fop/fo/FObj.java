/*
 * $Id$
 * Copyright (C) 2001 The Apache Software Foundation. All rights reserved.
 * For details on use and redistribution please refer to the
 * LICENSE file included with these sources.
 */

package org.apache.fop.fo;

// FOP
import org.apache.fop.layout.Area;
import org.apache.fop.apps.FOPException;
import org.apache.fop.datatypes.IDReferences;

// Java
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * base class for representation of formatting objects and their processing
 */
public class FObj extends FONode {

    public static class Maker {
        public FObj make(FObj parent,
                         PropertyList propertyList) throws FOPException {
            return new FObj(parent, propertyList);
        }

    }

    public static Maker maker() {
        return new Maker();
    }

    // protected PropertyList properties;
    public PropertyList properties;
    protected PropertyManager propMgr;

    protected String name;

    protected FObj(FObj parent, PropertyList propertyList) {
        super(parent);
        this.properties = propertyList;    // TO BE REMOVED!!!
        propertyList.setFObj(this);
        this.propMgr = makePropertyManager(propertyList);
        this.name = "default FO";
        setWritingMode();
    }

    protected PropertyManager makePropertyManager(PropertyList propertyList) {
        return new PropertyManager(propertyList);
    }

    /**
     * adds characters (does nothing here)
     * @param data text
     * @param start start position
     * @param length length of the text
     */
    protected void addCharacters(char data[], int start, int length) {
        // ignore
    }

    /**
     * generates the area or areas for this formatting object
     * and adds these to the area. This method should always be
     * overridden by all sub classes
     *
     * @param area
     */
    public Status layout(Area area) throws FOPException {
        // should always be overridden
        return new Status(Status.OK);
    }

    /**
     * returns the name of the formatting object
     * @return the name of this formatting objects
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     */
    protected void start() {
        // do nothing by default
    }

    /**
     *
     */
    protected void end() {
        // do nothing by default
    }

    /**
     * lets outside sources access the property list
     * first used by PageNumberCitation to find the "id" property
     * @param name - the name of the desired property to obtain
     * @return the property
     */
    public Property getProperty(String name) {
        return (properties.get(name));
    }


    /**
     * Return the "content width" of the areas generated by this FO.
     * This is used by percent-based properties to get the dimension of
     * the containing block.
     * If an FO has a property with a percentage value, that value
     * is usually calculated on the basis of the corresponding dimension
     * of the area which contains areas generated by the FO.
     * NOTE: subclasses of FObj should implement this to return a reasonable
     * value!
     */
    public int getContentWidth() {
        return 0;
    }

    /**
     * removes property id
     * @param idReferences the id to remove
     */
    public void removeID(IDReferences idReferences) {
        if (((FObj)this).properties.get("id") == null
                || ((FObj)this).properties.get("id").getString() == null)
            return;
        idReferences.removeID(((FObj)this).properties.get("id").getString());
        int numChildren = this.children.size();
        for (int i = 0; i < numChildren; i++) {
            FONode child = (FONode)children.elementAt(i);
            if ((child instanceof FObj)) {
                ((FObj)child).removeID(idReferences);
            }
        }
    }

    public boolean generatesReferenceAreas() {
        return false;
    }

    /**
     * Set writing mode for this FO.
     * Find nearest ancestor, including self, which generates
     * reference areas and use the value of its writing-mode property.
     * If no such ancestor is found, use the value on the root FO.
     */
    protected void setWritingMode() {
        FObj p;
        FObj parent;
        for (p = this;
                !p.generatesReferenceAreas() && (parent = p.getParent()) != null;
                p = parent);
        this.properties.setWritingMode(p.getProperty("writing-mode").getEnum());
    }

}

