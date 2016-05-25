package com.springmvc.service;

import org.springframework.stereotype.Service;

import com.springmvc.model.Course;

@Service
public interface CourseService {
	
	
	Course getCoursebyId(Integer courseId);
	
}
