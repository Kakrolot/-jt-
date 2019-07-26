package com.jt.manage.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.EasyUIResult;
import com.jt.common.vo.SysResult;
import com.jt.manage.pojo.Item;
import com.jt.manage.service.ItemService;

@Controller
@RequestMapping("/item/")
public class ItemController {
	//成员变量：定义log日志的打印
	private static final Logger 
	LOGGER=LoggerFactory.getLogger(ItemController.class);
	
	//需要依赖service层
	@Autowired
	private ItemService itemService;
	
	//编写自定义方法
	@RequestMapping("query")
	@ResponseBody
	public EasyUIResult queryList(
			//url使用的get方式，而不是form表单的post方式
			//所以，获取值时 需要使用注解@RequestParam
			@RequestParam("page") Integer page,
			@RequestParam("rows") Integer rows) {
		return itemService.queryList(page, rows);
	}
	
	//编写新增方法
	//特别注意：返回值类型是SysResult对象
	//如果执行成功，返回结果是 SysResult.ok()
	//SysResult 是 common工程提供的工具类
	@RequestMapping("save")
	@ResponseBody
	public SysResult save(Item item) {
		//封装item对象中的成员变量：
		//封装页面中没有维护的数据:
		//新增日期、商品状态默认是正常1、id主键
		item.setCreated(new Date());
		item.setUpdated(new Date());
		item.setStatus(1);
		//先判断id是否为空，新增是id一定是空的
		if (item.getId() != null && 
				!"".equals(item.getId())) {
			//直接返回错误日志的信息
			LOGGER.warn("传入的商品数据中包含id数据！id={}",item.getId());
		}
		//为了安全起见，把id赋值为null
		item.setId(null);
		//调用service层的方法
		itemService.save(item);
		return SysResult.ok();
	}
	
	//更新方法：【编辑按钮】
	@RequestMapping("update")
	@ResponseBody
	public SysResult update(Item item) {
		//需要调用service层的update方法
		//特别注意：item对象中是基础信息
		item.setUpdated(new Date());
		itemService.updateSelective(item);
		return SysResult.ok();
	}
	
	//批量删除：【删除按钮】
	@RequestMapping("delete")
	@ResponseBody
	public SysResult deleteByIds(
			@RequestParam(value="ids",
			defaultValue="0000") String ids) {
		itemService.deleteByIds(ids.split(","));
		return SysResult.ok();
	}
	
	@RequestMapping("/instock")
	@ResponseBody
	public SysResult instock(Long[] ids) {
		/*for (Long id : ids) {
			System.out.println("===="+id);
		}*/
		System.out.println(ids);
		return SysResult.ok();
	}
	
}




