package com.avon.rga.admin.core.conf;

import com.avon.rga.admin.core.alarm.JobAlarmer;
import com.avon.rga.admin.core.scheduler.XxlJobScheduler;
import com.avon.rga.admin.dao.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;

/**
 * xxl-job config
 *
 * @author xuxueli 2017-04-28
 */

@Component
public class XxlJobAdminConfig implements InitializingBean, DisposableBean {

    private static XxlJobAdminConfig adminConfig = null;
    public static XxlJobAdminConfig getAdminConfig() {
        return adminConfig;
    }


    // ---------------------- XxlJobScheduler ----------------------

    private XxlJobScheduler xxlJobScheduler;

    @Override
    public void afterPropertiesSet() throws Exception {
        adminConfig = this;

        xxlJobScheduler = new XxlJobScheduler();
        xxlJobScheduler.init();
    }

    @Override
    public void destroy() throws Exception {
        xxlJobScheduler.destroy();
    }


    // ---------------------- XxlJobScheduler ----------------------

    // conf
    @Value("${xxl.job.i18n}")
    private String i18n;

    @Value("${xxl.job.accessToken}")
    private String accessToken;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Value("${xxl.job.triggerpool.fast.max}")
    private int triggerPoolFastMax;

    @Value("${xxl.job.triggerpool.slow.max}")
    private int triggerPoolSlowMax;

    @Value("${xxl.job.logretentiondays}")
    private int logretentiondays;

    // dao, service

    @Resource
    private XxlJobLogService xxlJobLogDao;
    @Resource
    private XxlJobInfoService xxlJobInfoDao;
    @Resource
    private XxlJobRegistryService xxlJobRegistryDao;
    @Resource
    private XxlJobGroupService xxlJobGroupDao;
    @Resource
    private XxlJobLogReportService xxlJobLogReportDao;
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private DataSource dataSource;
    @Resource
    private JobAlarmer jobAlarmer;


    public String getI18n() {
        if (!Arrays.asList("zh_CN", "zh_TC", "en").contains(i18n)) {
            return "zh_CN";
        }
        return i18n;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public int getTriggerPoolFastMax() {
        if (triggerPoolFastMax < 200) {
            return 200;
        }
        return triggerPoolFastMax;
    }

    public int getTriggerPoolSlowMax() {
        if (triggerPoolSlowMax < 100) {
            return 100;
        }
        return triggerPoolSlowMax;
    }

    public int getLogretentiondays() {
        if (logretentiondays < 7) {
            return -1;  // Limit greater than or equal to 7, otherwise close
        }
        return logretentiondays;
    }

    public XxlJobLogService getXxlJobLogDao() {
        return xxlJobLogDao;
    }

    public XxlJobInfoService getXxlJobInfoDao() {
        return xxlJobInfoDao;
    }

    public XxlJobRegistryService getXxlJobRegistryDao() {
        return xxlJobRegistryDao;
    }

    public XxlJobGroupService getXxlJobGroupDao() {
        return xxlJobGroupDao;
    }

    public XxlJobLogReportService getXxlJobLogReportDao() {
        return xxlJobLogReportDao;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public JobAlarmer getJobAlarmer() {
        return jobAlarmer;
    }

}
