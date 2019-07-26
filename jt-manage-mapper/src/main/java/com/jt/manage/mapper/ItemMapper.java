package com.jt.manage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

import com.jt.manage.mapper.base.mapper.SysMapper;
import com.jt.manage.pojo.Item;

public interface ItemMapper extends SysMapper<Item>{
	
	//添加排序的列表查询方法
	//特别注意:通用的mapper插件不支持order by查询
	@Select("select * from tb_item order by updated desc")
	public List<Item> queryListByOrder();
	//商品的商家与下架方法
	public void updateStatus(Map<String, Object> map);
	
}
