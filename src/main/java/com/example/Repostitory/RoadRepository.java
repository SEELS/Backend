package com.example.Repostitory;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.example.models.Road;

public interface RoadRepository extends CrudRepository<Road, Long> {
	
	ArrayList<Road> findByDeleted(boolean deleted);
}
