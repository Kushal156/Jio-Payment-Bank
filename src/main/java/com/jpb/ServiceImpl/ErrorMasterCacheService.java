package com.jpb.ServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jpb.Entity.ErrorCodeEntity;
import com.jpb.Repository.CustomerErrorMasterRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ErrorMasterCacheService {

	@Autowired
	private CustomerErrorMasterRepository errorRepo;

	private volatile Map<String, ErrorCodeEntity> errorCache = new ConcurrentHashMap<>();
	private volatile Map<String, ErrorCodeEntity> actionCache = new ConcurrentHashMap<>();

	// Load at startup
	@PostConstruct
	public void init() {
		loadCache();
	}

	// Scheduler 1 hour
	@Scheduled(fixedRate = 60 * 60 * 1000)
	public void refreshCache() {
		log.info("Refreshing Error Master Cache...");
		loadCache();
	}

	// Core method to load cache
	private void loadCache() {

		List<ErrorCodeEntity> list = errorRepo.findAll();

		errorCache = list.stream()
				.collect(Collectors.toConcurrentMap(e -> e.getErrorCode().toUpperCase(), Function.identity()));

		actionCache = list.stream()
				.collect(Collectors.toConcurrentMap(
						e -> (e.getNextActionType() + "|" + e.getNextActionSubType()).toUpperCase(),
						Function.identity(), (a, b) -> a));
	}

	// Public method to fetch from cache
	public ErrorCodeEntity getErrorConfig(String errorCode) {
		if (errorCode == null) {
			return null;
		}
		return errorCache.get(errorCode.toUpperCase());
	}

	public boolean isExcluded(String nextActionType, String nextActionSubType) {

		ErrorCodeEntity config = actionCache.get((nextActionType + "|" + nextActionSubType).toUpperCase());
		return config != null && Boolean.TRUE.equals(config.getIsDedupeExcluded());
	}

}
