package com.jt.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping(value = "/page")
@Controller
public class PageController {
	//专门接受页面跳转的请求
	 @RequestMapping(value = "/{pageName}")
	    public String toPage(@PathVariable("pageName") String pageName) {
	        return pageName;
	    }

}
