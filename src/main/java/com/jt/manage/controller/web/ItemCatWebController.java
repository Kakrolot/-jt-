package com.jt.manage.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.ItemCatResult;
import com.jt.manage.service.ItemCatService;

/**
 * 该类专门用于json跨域访问的接口类
 * jt-web工程与jt-manage-web工程的对接
 * 特别注意:对应着功能；商品主页的分类
 * @author 40272
 *
 */
@Controller
@RequestMapping("web/itemcat")
public class ItemCatWebController {
	@Autowired
	private ItemCatService itemCatService;
	
	//前台系统发送的请求是:
	//http://manage.jt.com/web/item/all?callback=category.getDataService
	@RequestMapping("all")
	@ResponseBody
	public ItemCatResult queryAll(@RequestParam(value="callback",required=false)String callback) {
		//需要调用service层的方法
		//
		
		
		return itemCatService.queryItemCatWebAll();
	}

}
