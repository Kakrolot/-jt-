package com.jt.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	@RequestMapping("index")
	public String index(){
		
		//springmvc����ͼ������:����ҳ����ת
		return "index";//index.jsp
	}
}
