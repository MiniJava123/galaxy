package com.dianping.data.warehouse.core.common;

import junit.framework.TestCase;

/**
 * Created by hongdi.tang on 14-3-17.
 */
public class GreenplumReaderParasTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();

    }

    public void testIfReadOnly() throws Exception {
        System.out.println(ReaderParameters.GreenplumReaderParas.partitionName.isReadOnly());
    }
}
