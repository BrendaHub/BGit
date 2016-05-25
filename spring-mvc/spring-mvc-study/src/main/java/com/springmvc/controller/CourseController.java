package com.springmvc.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.springmvc.model.Course;
import com.springmvc.service.CourseService;

@Controller
@RequestMapping("/course")
public class CourseController {

	private static Logger log = LoggerFactory.getLogger(CourseController.class);
	
	private CourseService courseService;
	
	@Autowired
	private CommonsMultipartResolver multipartResolver;

	@Autowired
	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}
	
	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String viewCourse(@RequestParam("courseId") Integer courseId, Model model){
		log.debug("requestParam course = " + courseId);
		Course course = (Course)courseService.getCoursebyId(courseId);
		model.addAttribute(course);
		return "course_overview";
	}
	
	@RequestMapping(value="/view2/{courseId}",method=RequestMethod.GET)
	public String viewCourse2(@PathVariable("courseId") Integer courseId, Map<String, Object> model){
		log.info("requestParam course = " + courseId);
		Course course = (Course)courseService.getCoursebyId(courseId);
		model.put("course", course);
		return "course_overview";
	}
	
	@RequestMapping(value="/view3", method=RequestMethod.GET)
	public String viewCourse3(HttpServletRequest request){
		
		Course course = (Course)courseService.getCoursebyId(Integer.parseInt(request.getParameter("courseId")));
		
		log.debug("requestParam course = " + course);
		
		request.setAttribute("course", course); 
		
		return "course_overview";
	}
	
	@RequestMapping(value="/admin" , method=RequestMethod.GET, params = "add")
	public String toEdit(){
		return "course_admin/edit";
	}
	
	/**
	 * 传统式的请求数据与对象的绑定，
	 * 注意： 页面上控件的属性名称一定要与对象的属性名称一致，spring mvc是以名称进行匹配对象
	 * @param course
	 * @return
	 */
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public String toSave(Course course){
		log.debug("获取到了新的course对象为 :");
		log.debug(ReflectionToStringBuilder.toString(course));
		course.setCourseId(123);
		return "redirect:view2/"+course.getCourseId();
	} 
	
	@RequestMapping(value="/save2", method=RequestMethod.POST)
	public String toSave2(@ModelAttribute Course course){
		course.setCourseId(234);
		log.debug("toSave ========================================== 2的参数值为： = ");
		log.debug(ReflectionToStringBuilder.toString(course));
		return "redirect:view3?courseId="+course.getCourseId();
	}    
	
	@RequestMapping(value="/upload", method=RequestMethod.GET, params = "upload")
	public String toUploadFile(){
		return "course_admin/file";
	}
	
	@RequestMapping(value="/doUpload", method = RequestMethod.POST)
	public String upload(@RequestParam("file") MultipartFile file) {
		log.debug("Process file: " , file.getOriginalFilename());
		log.debug("file name = " , file.getName());
		
		if(file.isEmpty()){
			return "emptyFail";
		}
		
		
		if(file.getSize() > 1048576){
			return "maxFail";
		}
		
		log.debug("=================== multipartResolver.getFileUpload().getFileSizeMax() ", multipartResolver.getFileUpload().getFileSizeMax());
		
		String exFileName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		try {
			FileUtils.copyInputStreamToFile(file.getInputStream(), new File("H:\\MIG\\"+System.currentTimeMillis()+exFileName));
		} catch (IOException e) {
			e.printStackTrace();
		} catch(MaxUploadSizeExceededException mse){
			mse.printStackTrace();
			return "maxFail"; 
		} catch(Exception ex){
			return "allFail";
		}
		return "redirect:upload?upload";
	}
	
	/**
	 * 处理直接返回JSON字符串的方法
	 */
	@RequestMapping(value="/{courseId}", method=RequestMethod.GET)
	public @ResponseBody Course JSON1(@PathVariable("courseId") Integer courseId){
		
		return courseService.getCoursebyId(courseId);
	}
	
	@RequestMapping(value="/jsontype/{courseId}" , method=RequestMethod.GET)
	public ResponseEntity<Course> JSON2(@PathVariable("courseId") Integer courseId){
		
		return new ResponseEntity<Course>((Course)courseService.getCoursebyId(courseId), HttpStatus.OK);
	}
	
}
