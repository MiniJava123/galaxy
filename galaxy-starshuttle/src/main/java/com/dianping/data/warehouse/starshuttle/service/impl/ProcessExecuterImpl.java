package com.dianping.data.warehouse.starshuttle.service.impl;

import com.dianping.data.warehouse.starshuttle.common.Const;
import com.dianping.data.warehouse.domain.model.ExceptionAlertDO;
import com.dianping.data.warehouse.starshuttle.service.ProcessExecuter;
import com.dianping.data.warehouse.starshuttle.utils.ExceptionAnalyze;
import com.dianping.data.warehouse.starshuttle.utils.StreamPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * Created by hongdi.tang on 14-3-4.
 */
public class ProcessExecuterImpl implements ProcessExecuter {
    private static Logger logger = LoggerFactory.getLogger(ProcessExecuterImpl.class);

    @Override
    public Integer execute(List<String> commands) {
        ProcessBuilder builer = new ProcessBuilder(commands);
        ExceptionAnalyze exceptionAnalyze = new ExceptionAnalyze();
        builer.redirectErrorStream(true);
        Process process = null;
        try {
            process = builer.start();
            InputStream stdin = process.getInputStream();
            StreamPrinter printer = new StreamPrinter(stdin);
            printer.start();
            int rtn = process.waitFor();
            if (rtn != Const.RET_SUCCESS) {
                ExceptionAlertDO alert = exceptionAnalyze.analyze(stdin);
                if (alert != null)
                    return alert.getId();
                return 16;
            }
            return Const.RET_SUCCESS;
        } catch (Exception e) {
            logger.error("process execute exception", e);
            throw new RuntimeException("process execute occur exception", e);
        }
    }

}
