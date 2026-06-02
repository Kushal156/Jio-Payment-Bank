package com.jpb.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpb.DTO.OtherCommonRequest;
import com.jpb.Service.OtherService;

@RestController
@RequestMapping("/other")
@CrossOrigin("*")
public class OtherController {

	@Autowired
	OtherService service;
	
	@PostMapping("/pan-card-verification")
	public ResponseEntity<?> panVerify(@RequestBody Map<String, Object> request){
		return service.panVerify(request);
	}
}
