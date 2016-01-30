package com.dianping.data.warehouse.starshuttle.service;

import com.dianping.data.warehouse.starshuttle.service.impl.ProcessExecuterImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hongdi.tang on 14-4-9.
 */
public class ProcessExecuterTest {
    @Test
    public void testExecute() throws Exception {
        ProcessExecuter executer = new ProcessExecuterImpl();
        List<String> cmds = new ArrayList<String>();
        cmds.add("D:/data/55.bat");
        executer.execute(cmds);
        Assert.assertTrue(true);
    }
}
