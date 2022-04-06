package com.avon.rga.admin.dao;

import com.avon.rga.admin.core.model.XxlJobRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlJobRegistryServiceTest {

    @Resource
    private XxlJobRegistryService xxlJobRegistryService;

    @Test
    public void test(){
        int ret = xxlJobRegistryService.registryUpdate("g1", "k1", "v1", new Date());
        if (ret < 1) {
            ret = xxlJobRegistryService.registrySave("g1", "k1", "v1", new Date());
        }

        List<XxlJobRegistry> list = xxlJobRegistryService.findAll(1, new Date());

        int ret2 = xxlJobRegistryService.removeDead(Arrays.asList(1));
    }

}
