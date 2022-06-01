package io.polyrepo.analyser.config;

import io.polyrepo.analyser.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TrendSchedulerConfig {

    @Autowired
    private QueryService queryService;

    private final Logger logger = LoggerFactory.getLogger(TrendSchedulerConfig.class);

    @Scheduled(cron = "${cron.expression}")
    public void scheduledTrendCapture() {
        logger.debug("Executing Scheduled Method");
        queryService.scheduledTrendCapture();
        logger.debug("Execution Finished");
    }
}
