package com.jpb.Service;

import com.jpb.DTO.AepsCommonRequestDto;
import com.jpb.DTO.AepsCommonResponseDto;
import com.jpb.DTO.JpbAepsResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public interface JpbAepsService {


	public AepsCommonResponseDto CashDeposite(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public AepsCommonResponseDto AepsCashWithdrawal(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public JpbAepsResponseDto AepsFundTransfer(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public AepsCommonResponseDto AepsMiniStement(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public AepsCommonResponseDto BalanceInquiry(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public JpbAepsResponseDto AgentHistory(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public JpbAepsResponseDto AgentInfo(AepsCommonRequestDto request, HttpSession session, HttpServletRequest httpRequest);

	public JpbAepsResponseDto CashdepositeGenerateOtp(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public JpbAepsResponseDto eAuthenticate(AepsCommonRequestDto request, HttpSession session , HttpServletRequest httpRequest) throws Exception;


}
