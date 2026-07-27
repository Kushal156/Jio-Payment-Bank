package com.jpb.Service;

import com.jpb.DTO.AepsCommonRequestDto;
import com.jpb.DTO.JpbAepsResponseDto;

import jakarta.servlet.http.HttpServletRequest;

public interface JpbAepsService {

	public JpbAepsResponseDto CashDeposite(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public JpbAepsResponseDto AepsCashWithdrawal(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public JpbAepsResponseDto AepsFundTransfer(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public JpbAepsResponseDto AepsMiniStement(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public JpbAepsResponseDto BalanceInquiry(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public JpbAepsResponseDto AgentHistory(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public JpbAepsResponseDto AgentInfo(AepsCommonRequestDto request, HttpServletRequest httpRequest);

	public JpbAepsResponseDto CashdepositeGenerateOtp(AepsCommonRequestDto request, HttpServletRequest httpRequest);
}
