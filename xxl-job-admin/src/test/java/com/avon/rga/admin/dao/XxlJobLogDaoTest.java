package com.avon.rga.admin.dao;

import com.avon.rga.admin.core.model.XxlJobLog;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobLogDaoTest {

    @Resource
    private XxlJobLogService xxlJobLogService;

    @Test
    public void test(){
        List<XxlJobLog> list = xxlJobLogService.pageList(0, 10, 1, 1, null, null, 1);
        int list_count = xxlJobLogService.pageListCount(0, 10, 1, 1, null, null, 1);

        XxlJobLog log = new XxlJobLog();
        log.setJobGroup(1);
        log.setJobId(1);

        long ret1 = xxlJobLogService.save(log);
        XxlJobLog dto = xxlJobLogService.load(log.getId());

        log.setTriggerTime(new Date());
        log.setTriggerCode(1);
        log.setTriggerMsg("1");
        log.setExecutorAddress("1");
        log.setExecutorHandler("1");
        log.setExecutorParam("1");
        ret1 = xxlJobLogService.updateTriggerInfo(log);
        dto = xxlJobLogService.load(log.getId());


        log.setHandleTime(new Date());
        log.setHandleCode(2);
        log.setHandleMsg("2");
        ret1 = xxlJobLogService.updateHandleInfo(log);
        dto = xxlJobLogService.load(log.getId());


        List<Long> ret4 = xxlJobLogService.findClearLogIds(1, 1, new Date(), 100, 100);

        long ret2 = xxlJobLogService.delete(log.getJobId());

    }

}
