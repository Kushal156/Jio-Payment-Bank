package com.jpb.Config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.jpb.ServiceImpl.AgentServiceImpl;
import com.jpb.ServiceImpl.CustomerServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerConfig {

	 private final CustomerServiceImpl customerService;
	 private final AgentServiceImpl agentService;
	 
	  @EventListener(ApplicationReadyEvent.class)
	    public void runOnStartup() {

	        log.info("************ Starting all scheduler jobs after deployment ************");

//	        try {
//	            customerService.applicationStatus();
//	        } catch (Exception e) {
//	            log.error("Customer Scheduler Startup Failed", e);
//	        }
//
//	        try {
//	            agentService.agentInfo();
//	        } catch (Exception e) {
//	            log.error("Agent Scheduler Startup Failed", e);
//	        }
	        
	    }
}
