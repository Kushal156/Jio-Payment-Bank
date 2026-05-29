package com.jpb.Service;

import org.springframework.http.ResponseEntity;

import com.jpb.DTO.AgentStatusResponseDTO;
import com.jpb.DTO.SaveAgentInputDTO;
import com.jpb.DTO.SaveAgentResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface AgentService {

	ResponseEntity<SaveAgentResponseDTO> saveAgentDetails(SaveAgentInputDTO request, HttpServletRequest httpRequest);

	ResponseEntity<?> agentEkyc(String applicationNo, String aadharNo, String biometricDataBase64, HttpServletRequest httpRequest, 
			 String externalRefNo, String vkid, String latitude, String longitude);

	ResponseEntity<AgentStatusResponseDTO> agentStatus(String applicationNo, HttpServletRequest httpRequest, String latitude, String longitude);

	ResponseEntity<?> panUpdate(String applicationNo, String panNumber, String panName, String dob,
			HttpServletRequest httpRequest, String externalRefNo, String vkid, String latitude, String longitude);

	ResponseEntity<?> recallApplication(String applicationNo, HttpServletRequest httpRequest, String latitude, String longitude);

	ResponseEntity<?> agentInfo();
}
