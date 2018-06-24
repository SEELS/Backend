package com.example.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.example.Repostitory.DriverRepository;
import com.example.Repostitory.LocationRepository;


@RestController
public class TruckController {

	@Autowired
	DriverRepository driverRepository;

	@Autowired
	LocationRepository locationRepository;



}
