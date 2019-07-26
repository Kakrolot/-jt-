package com.jt.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	@RequestMapping("index")
	public String index(){
		
		//springmvc的试图解析器:负责页面跳转
		return "index";//index.jsp
	}
}
