/*
 * $Id: LayoutContext.java,v 1.12 2003/03/07 07:58:51 jeremias Exp $
 * ============================================================================
 *                    The Apache Software License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include the following acknowledgment: "This product includes software
 *    developed by the Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself, if
 *    and wherever such third-party acknowledgments normally appear.
 * 
 * 4. The names "FOP" and "Apache Software Foundation" must not be used to
 *    endorse or promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    apache@apache.org.
 * 
 * 5. Products derived from this software may not be called "Apache", nor may
 *    "Apache" appear in their name, without prior written permission of the
 *    Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ============================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation and was originally created by
 * James Tauber <jtauber@jtauber.com>. For more information on the Apache
 * Software Foundation, please see <http://www.apache.org/>.
 */ 
package org.apache.fop.layoutmgr;


/**
 * This class is used to pass information to the getNextBreakPoss()
 * method. It is set up by higher level LM and used by lower level LM.
 */
public class LayoutContext {
    /**
     * Values for flags.
     */
    public static final int LINEBREAK_AT_LF_ONLY = 0x01;
    /** Generated break possibility is first in a new area */
    public static final int NEW_AREA = 0x02;
    public static final int IPD_UNKNOWN = 0x04;
    /** Signal to a Line LM that a higher level LM may provoke a change
     *  in the reference area, thus ref area IPD. The LineLM should return
     *  without looking for a line break.
     */
    public static final int CHECK_REF_AREA = 0x08;

    /**
     * If this flag is set, it indicates that any leading fo:character
     * objects with suppress-at-line-break="suppress" should not generate
     * areas. This is the case at the beginning of each new LineArea
     * except the first.
     */
    public static final int SUPPRESS_LEADING_SPACE = 0x10;
    public static final int FIRST_AREA = 0x20;
    public static final int TRY_HYPHENATE = 0x40;
    public static final int LAST_AREA = 0x80;

    public static final int RESOLVE_LEADING_SPACE = 0x100;


    public int flags; // Contains some set of flags defined above
    /**
     * Total available stacking dimension for a "galley-level" layout
     * manager (Line or Flow). It is passed by the parent LM. For LineLM,
     * the block LM determines this based on indent properties.
     * These LM <b>may</b> wish to pass this information down to lower
     * level LM to allow them to optimize returned break possibilities.
     */
    MinOptMax stackLimit;


    /** True if current top-level reference area is spanning. */
    boolean bIsSpan;

    /** inline-progression-dimension of nearest ancestor reference area */
    int refIPD;

    /** Current pending space-after or space-end from preceding area */
    SpaceSpecifier trailingSpace;

    /** Current pending space-before or space-start from ancestor areas */
    SpaceSpecifier leadingSpace;

    /** Current hyphenation context. May be null. */
    private HyphContext hyphContext = null;

    /** Stretch or shrink value when making areas. */
    private double ipdAdjust = 0.0;

    /** Stretch or shrink value when adding spaces. */
    private double dSpaceAdjust = 0.0;

    private int iLineHeight;
    private int iBaseline;

    public LayoutContext(LayoutContext parentLC) {
        this.flags = parentLC.flags;
        this.refIPD = parentLC.refIPD;
        this.stackLimit = null; // Don't reference parent MinOptMax!
        this.leadingSpace = parentLC.leadingSpace; //???
        this.trailingSpace = parentLC.trailingSpace; //???
        this.hyphContext = parentLC.hyphContext;
        this.dSpaceAdjust = parentLC.dSpaceAdjust;
        this.iLineHeight = parentLC.iLineHeight;
        this.iBaseline = parentLC.iBaseline;
        // Copy other fields as necessary. Use clone???
    }

    public LayoutContext(int flags) {
        this.flags = flags;
        this.refIPD = 0;
        stackLimit = new MinOptMax(0);
        leadingSpace = null;
        trailingSpace = null;
    }

    public void setFlags(int flags) {
        setFlags(flags, true);
    }

    public void setFlags(int flags, boolean bSet) {
        if (bSet) {
            this.flags |= flags;
        } else {
            this.flags &= ~flags;
        }
    }

    public void unsetFlags(int flags) {
        setFlags(flags, false);
    }

    public boolean isStart() {
        return ((this.flags & NEW_AREA) != 0);
    }

    public boolean startsNewArea() {
        return ((this.flags & NEW_AREA) != 0 && leadingSpace != null);
    }

    public boolean isFirstArea() {
        return ((this.flags & FIRST_AREA) != 0);
    }

    public boolean isLastArea() {
        return ((this.flags & LAST_AREA) != 0);
    }

    public boolean suppressLeadingSpace() {
        return ((this.flags & SUPPRESS_LEADING_SPACE) != 0);
    }

    public void setLeadingSpace(SpaceSpecifier space) {
        leadingSpace = space;
    }

    public SpaceSpecifier getLeadingSpace() {
        return leadingSpace;
    }

    public boolean resolveLeadingSpace() {
        return ((this.flags & RESOLVE_LEADING_SPACE) != 0);
    }

    public void setTrailingSpace(SpaceSpecifier space) {
        trailingSpace = space;
    }

    public SpaceSpecifier getTrailingSpace() {
        return trailingSpace;
    }

    public void setStackLimit(MinOptMax limit) {
        stackLimit = limit;
    }

    public MinOptMax getStackLimit() {
        return stackLimit;
    }

    public void setRefIPD(int ipd) {
        refIPD = ipd;
    }

    public int getRefIPD() {
        return refIPD;
    }

    public void setHyphContext(HyphContext hyph) {
        hyphContext = hyph;
    }

    public HyphContext getHyphContext() {
        return hyphContext;
    }

    public boolean tryHyphenate() {
        return ((this.flags & TRY_HYPHENATE) != 0);
    }

    public void setSpaceAdjust(double adjust) {
        dSpaceAdjust = adjust;
    }

    public double getSpaceAdjust() {
        return dSpaceAdjust;
    }

    public void setIPDAdjust(double ipdA) {
        ipdAdjust = ipdA;
    }

    public double getIPDAdjust() {
        return ipdAdjust;
    }

    public void setLineHeight(int lh) {
        iLineHeight = lh;
    }

    public int getLineHeight() {
        return iLineHeight;
    }

    public void setBaseline(int bl) {
        iBaseline = bl;
    }

    public int getBaseline() {
        return iBaseline;
    }
}

