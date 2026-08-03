package com.jpb.Service;

import org.springframework.http.ResponseEntity;

import com.jpb.DTO.DmtCommonrequestDto;

import jakarta.servlet.http.HttpServletRequest;

public interface DMTService {

	ResponseEntity<?> checkmobileNo(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> generateDmtOtp(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> verifyDmtOtp(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> dmtEkyc(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> registerUser(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> tranx(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> tranxStatusCheck(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> customerLimit(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> tranxHistory(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> beneValidation(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> addBene(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> custDetails(DmtCommonrequestDto request, HttpServletRequest httpRequest);

	ResponseEntity<?> amtCalculation(DmtCommonrequestDto request, HttpServletRequest httpRequest);

}
