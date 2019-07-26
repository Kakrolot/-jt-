package com.jt.manage.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.manage.pojo.ItemCat;
import com.jt.manage.service.ItemCatService;


@Controller
@RequestMapping("/item/cat/")
public class ItemCatController {
	//需要依赖service层
	@Autowired
	private ItemCatService itemCatService;
	
	//编写自定义方法
	@RequestMapping("list")
	@ResponseBody
	public List<ItemCat> queryListById(
			@RequestParam(value="id",defaultValue="0") Long id) throws Exception {
		return itemCatService.queryListById(id);
	}
	
}



