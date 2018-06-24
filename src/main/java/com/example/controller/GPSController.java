package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GPSController {
	
	@RequestMapping(value="/",method=RequestMethod.GET)
	public String HomePage()
	{
		return "PC";
	}
	
	@RequestMapping(value="/mobile",method=RequestMethod.GET)
	public String Home2()
	{
		return "mobile";
	}
}
