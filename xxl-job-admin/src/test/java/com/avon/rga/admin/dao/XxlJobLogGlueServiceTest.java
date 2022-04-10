package com.avon.rga.admin.dao;

import com.avon.rga.admin.core.model.XxlJobLogGlue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobLogGlueServiceTest {

    @Resource
    private XxlJobLogGlueService xxlJobLogGlueService;

    @Test
    public void test(){
        XxlJobLogGlue logGlue = new XxlJobLogGlue();
        logGlue.setJobId(1);
        logGlue.setGlueType("1");
        logGlue.setGlueSource("1");
        logGlue.setGlueRemark("1");

        logGlue.setAddTime(new Date());
        logGlue.setUpdateTime(new Date());
        int ret = xxlJobLogGlueService.save(logGlue);

        List<XxlJobLogGlue> list = xxlJobLogGlueService.findByJobId(1);

        long ret2 = xxlJobLogGlueService.removeOld("1", 1);

        int ret3 = xxlJobLogGlueService.deleteByJobId(1);
    }

}
