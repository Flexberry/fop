/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package org.apache.fop.fo.flow;

// XML
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

// FOP
import org.apache.fop.fo.FONode;
import org.apache.fop.fo.FObj;
import org.apache.fop.fo.PropertyList;
import org.apache.fop.fo.properties.CommonAccessibility;
import org.apache.fop.fo.properties.KeepProperty;

/**
 * Class modelling the fo:list-item-label object.
 * @todo implement validateChildNode()
 */
public class ListItemLabel extends FObj {
    // The value of properties relevant for fo:list-item-label.
    private CommonAccessibility commonAccessibility;
    private String id;
    private KeepProperty keepTogether;
    // End of property values

    /**
     * @param parent FONode that is the parent of this object
     */
    public ListItemLabel(FONode parent) {
        super(parent);
    }

    /**
     * @see org.apache.fop.fo.FObj#bind(PropertyList)
     */
    public void bind(PropertyList pList) {
        commonAccessibility = pList.getAccessibilityProps();
        id = pList.get(PR_ID).getString();
        keepTogether = pList.get(PR_KEEP_TOGETHER).getKeep();
    }

    /**
     * @see org.apache.fop.fo.FONode#startOfNode
     */
    protected void startOfNode() throws SAXParseException {
        checkId(id);
        getFOEventHandler().startListLabel();
    }

    /**
     * @see org.apache.fop.fo.FONode#endOfNode
     */
    protected void endOfNode() throws SAXParseException {
        getFOEventHandler().endListLabel();
    }

    /**
     * @see org.apache.fop.fo.FObj#addProperties
     */
    protected void addProperties(Attributes attlist) throws SAXParseException {
        super.addProperties(attlist);
        getFOEventHandler().startListLabel();
        /*
         * For calculating the lineage - The fo:list-item-label formatting object
         * does not generate any areas. The fo:list-item-label formatting object
         * returns the sequence of areas created by concatenating the sequences
         * of areas returned by each of the child nodes of the fo:list-item-label.
         */
    }

    /**
     * Return the "id" property.
     */
    public String getId() {
        return id;
    }

    /**
     * @see org.apache.fop.fo.FObj#getName()
     */
    public String getName() {
        return "fo:list-item-label";
    }
    
    /**
     * @see org.apache.fop.fo.FObj#getNameId()
     */
    public int getNameId() {
        return FO_LIST_ITEM_LABEL;
    }
}

