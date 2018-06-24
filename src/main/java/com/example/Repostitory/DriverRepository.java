package com.example.Repostitory;


import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.example.models.Driver;

public interface DriverRepository extends CrudRepository<Driver, Long> {
	public Driver findBySsn(String ssn);
	public ArrayList<Driver> findByDeleted(boolean deleted);
}
