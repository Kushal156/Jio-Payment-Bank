package com.jpb.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpb.DTO.AuthTokenRequestDTO;
import com.jpb.DTO.AuthTokenResponseDTO;
import com.jpb.Service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	AuthService service;
	
	@GetMapping("/token")
	public ResponseEntity<AuthTokenResponseDTO> generateToken(HttpServletRequest httpRequest){
		return service.generateToken(httpRequest);
	}
	
}
