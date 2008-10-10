/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

package org.apache.fop.render.afp;

import java.io.IOException;

/**
 * Abstract image configurator
 */
public abstract class AFPDataObjectInfoFactory {
    private static final int X = 0;
    private static final int Y = 1;

    /** the AFP state */
    protected final AFPState state;

    /** foreign attribute reader */
    private final AFPForeignAttributeReader foreignAttributeReader
        = new AFPForeignAttributeReader();

    /**
     * Main constructor
     *
     * @param state the AFP state
     */
    public AFPDataObjectInfoFactory(AFPState state) {
        this.state = state;
    }

    /**
     * Configures the data object info
     *
     * @param afpImageInfo the afp image info
     * @return the data object info
     * @throws IOException thrown if an I/O exception of some sort has occurred.
     */
    public AFPDataObjectInfo create(AFPImageInfo afpImageInfo) throws IOException {
        AFPDataObjectInfo dataObjectInfo = createDataObjectInfo();

        // set resource information
        AFPResourceInfo resourceInfo
        = foreignAttributeReader.getResourceInfo(afpImageInfo.foreignAttributes);
        resourceInfo.setUri(afpImageInfo.uri);
        dataObjectInfo.setResourceInfo(resourceInfo);

        // set object area
        AFPObjectAreaInfo objectAreaInfo = new AFPObjectAreaInfo();

        float srcX = afpImageInfo.origin.x + (float)afpImageInfo.pos.getX();
        float srcY = afpImageInfo.origin.y + (float)afpImageInfo.pos.getY();
        AFPUnitConverter unitConv = state.getUnitConverter();
        int[] coords = unitConv.mpts2units(new float[] {srcX, srcY});
        objectAreaInfo.setX(coords[X]);
        objectAreaInfo.setY(coords[Y]);

        int width = Math.round(unitConv.mpt2units((float)afpImageInfo.pos.getWidth()));
        objectAreaInfo.setWidth(width);

        int height = Math.round(unitConv.mpt2units((float)afpImageInfo.pos.getHeight()));
        objectAreaInfo.setHeight(height);

        int resolution = state.getResolution();
        objectAreaInfo.setHeightRes(resolution);
        objectAreaInfo.setWidthRes(resolution);

        objectAreaInfo.setRotation(state.getRotation());

        dataObjectInfo.setObjectAreaInfo(objectAreaInfo);

        return dataObjectInfo;
    }

    /**
     * Creates the data object information object
     *
     * @return the data object information object
     */
    protected abstract AFPDataObjectInfo createDataObjectInfo();
}