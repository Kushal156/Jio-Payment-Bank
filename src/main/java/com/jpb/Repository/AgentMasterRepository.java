package com.jpb.Repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jpb.Entity.AgentMasterEntity;

public interface AgentMasterRepository extends JpaRepository<AgentMasterEntity, Long> {

	Optional<AgentMasterEntity> findByApplicationNumber(String applicationNumber);

	Optional<AgentMasterEntity> findByExternalAppRefNumber(String agentId);
	
	Optional<AgentMasterEntity> findByVkidAndJioAgentIdIsNotNull(String vkid);
	
	@Query(value = """
            SELECT * FROM BankingJio.agent_master
            WHERE next_action_type IN ('AGENT ONBOARDED', 'APPLICATION')
              AND next_action_sub_type IN ('COMPLETE', 'PROCESSED')
              AND jio_agent_id IS NULL
            ORDER BY 1 DESC
            """, nativeQuery = true)
    List<AgentMasterEntity> findLatestPendingJioAgent();
	
}
