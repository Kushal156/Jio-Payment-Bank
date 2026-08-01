package com.jpb.Controller;

import com.jpb.DTO.AepsCommonResponseDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.jpb.DTO.AepsCommonRequestDto;
import com.jpb.DTO.JpbAepsResponseDto;
import com.jpb.Service.JpbAepsService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/jpb/AEPS")
@CrossOrigin("*")
public class JpbAepsController {


	@Autowired
	private JpbAepsService jpbAepsService;

	@PostMapping("/cashDeposit")
	public AepsCommonResponseDto CashDeposite(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest){



		AepsCommonResponseDto	response =jpbAepsService.CashDeposite(request,  httpRequest);

		return response;
	}

	@PostMapping("/authenticate")
	public JpbAepsResponseDto aepsAuthenticate(@RequestBody AepsCommonRequestDto request,
											   HttpSession session,
											   HttpServletRequest httpRequest) throws Exception{

		JpbAepsResponseDto	response =jpbAepsService.eAuthenticate(request,  session, httpRequest);

		return response;
	}


	@PostMapping("/aepscashwithdrawal")
	public AepsCommonResponseDto CashWithdrawal(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest){

		System.out.println("request>>>"+request);
		AepsCommonResponseDto	response =jpbAepsService.AepsCashWithdrawal(request,  httpRequest);

		return response;
	}

	@PostMapping("/aepsFundTransfer")
	public JpbAepsResponseDto fundTransfer(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest) {

		JpbAepsResponseDto response =	jpbAepsService.AepsFundTransfer(request,httpRequest);

		return response;
	}

	@PostMapping("/aepsMiniStatement")
	public AepsCommonResponseDto miniStatement(@RequestBody AepsCommonRequestDto request, HttpServletRequest httpRequest ) {

		AepsCommonResponseDto response =jpbAepsService.AepsMiniStement(request,httpRequest);

		return response;
	}

	@PostMapping("/balanceInquiry")
	public AepsCommonResponseDto BalanceInquiry(@RequestBody AepsCommonRequestDto request, HttpServletRequest httpRequest) {

		AepsCommonResponseDto response =	jpbAepsService.BalanceInquiry(request,httpRequest);

		return response;

	}

	@PostMapping("/agentHistory")
	public JpbAepsResponseDto agentHistory(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest) {

		JpbAepsResponseDto response = jpbAepsService.AgentHistory(request,httpRequest);

		return response;
	}

	@PostMapping("/agentInfo")
	public JpbAepsResponseDto agentInfo(@RequestBody AepsCommonRequestDto request,
										HttpSession session
			,HttpServletRequest httpRequest) {

		JpbAepsResponseDto response =jpbAepsService.AgentInfo(request, session,httpRequest);

		return response;
	}

	@PostMapping("/cashWithdraGenerateOtp")
	public JpbAepsResponseDto cashWithdrawalGenerateOtp(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest) {

		JpbAepsResponseDto response =jpbAepsService.CashdepositeGenerateOtp(request, httpRequest);

		return response;
	}

}
