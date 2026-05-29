package com.jpb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.jpb.Entity.InsertSPAgentDetailsEntity;

import jakarta.transaction.Transactional;

public interface InsertSPAgentDetails extends JpaRepository<InsertSPAgentDetailsEntity, Integer> {


	@Procedure(procedureName = "BankingJio.sp_insert_agent_with_log")
	InsertSPAgentDetailsEntity insertAgentWithLog(

	        @Param("application_number") String applicationNumber,

	        @Param("external_app_ref_number") String externalAppRefNumber,
	        @Param("api_version") String apiVersion,
	        @Param("initiating_entity_id") String initiatingEntityId,
	        @Param("organization_id") String organizationId,

	        @Param("external_id") String externalId,
	        @Param("pan_number") String panNumber,
	        @Param("name_as_per_pan") String nameAsPerPan,
	        @Param("dob") String dob,
	        @Param("aadhar_no") Long aadharNo,

	        @Param("street") String street,
	        @Param("city") String city,
	        @Param("state") String state,
	        @Param("state_code") String stateCode,
	        @Param("pincode") String pincode,
	        @Param("latitude") String string,
	        @Param("longitude") String string2,

	        @Param("contact_type") String contactType,
	        @Param("mobile_number") String mobileNumber,
	        @Param("email") String email,

	        @Param("instrument_id") String instrumentId,
	        @Param("enabled") Boolean enabled,

	        @Param("created_by") String createdBy,

	        @Param("api_name") String apiName,
	        @Param("request_payload") String requestPayload,
	        @Param("response_payload") String responsePayload,
	        @Param("http_status_code") Integer statusCode,
	        @Param("trace_id") String traceId,
	        @Param("status") String status,
	        @Param("error_message") String errorMessage,
	        @Param("next_action_type") String nextActionType,
	        @Param("next_action_subType") String nextActionSubType,
	        
	        @Param("application_type") String applicationType,
	        @Param("application_sub_type") String applicationSubType,
	        @Param("partner") String partner,
	        @Param("action_type") String actionType,
	        @Param("action_sub_type") String actionSubType
	);
}