package com.jt.manage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jt.common.vo.EasyUIResult;
import com.jt.manage.mapper.ItemMapper;
import com.jt.manage.pojo.Item;

@Service
public class ItemService extends BaseService<Item> {
	//需要依赖注入mapper层
	@Autowired
	private ItemMapper itemMapper;
	//编写自定义方法：分页查询
	public EasyUIResult queryList(Integer pageNum,Integer pageSize) {
		//1.使用分页的插件PageHelper，设置分页的信息
		PageHelper.startPage(pageNum, pageSize);
		//2.查询数据库，获得所有的数据
		/*String ITEM_KEY = "ITEM_"+pageNum+"_"+pageSize;
		List<Item> list = redisTemplate.boundListOps(ITEM_KEY).range(0, pageSize);
		System.out.println("!!!!!"+list);
		if (list == null || list.size() == 0) {
			System.out.println("查询数据库");
			list = itemMapper.queryListByOrder();
			for (Item it : list) {
				redisTemplate.boundListOps(ITEM_KEY).rightPush(it);
			}
		} 
		for (Item it : list) {
			System.out.println(it);
		}*/
		//2.1先从redis缓存中获取数据:根据key去获取
		//举例:第一页有20条数据:ITEM_KEY_1_20
		//2.2如果缓存没有，再从mysql中获取数据
		String ITEM_KEY = "ITEM_KEY_"+pageNum+"_"+pageSize;
		List<Item> list = redisTemplate.boundListOps(ITEM_KEY).range(0, pageSize);
		System.out.println("###nnn");
		if (list == null || list.size() == 0) {
			list = itemMapper.queryListByOrder();
			//把list集合存放到redis缓存中
			//下一次查询就从redis中查
			for(Item it:list)
			{
				redisTemplate.boundListOps(ITEM_KEY).rightPush(it);		
			}
		}
		for(Item it : list){
			System.out.println(it);
		}
		
		
		//3.1先从redis缓存中获取数据:根据key
		String ITEM_PAGE_KEY = "ITEM_PAGE_KEY_"+pageNum+"_"+pageSize;
		PageInfo<Item> pageInfo = (PageInfo<Item>) redisTemplate.boundValueOps(ITEM_PAGE_KEY).get();
		System.out.println("###"+pageInfo);
		//3.2如果redis换从中没有，再从pageInfo获取
		if (pageInfo == null || pageInfo.getSize() == 0) {
			pageInfo = new PageInfo<Item>(list);
			redisTemplate.boundValueOps(ITEM_PAGE_KEY).set(pageInfo);;
		}
		//PageInfo<Item> pageInfo = new PageInfo<Item>(list);
		System.out.println(pageInfo);
		//4.创建返回值对象
		//参数一：数据的总数量
		//参数二：数据的内容list集合
		EasyUIResult res = new EasyUIResult(pageInfo.getTotal(),pageInfo.getList());
		return res;
	}
	
}



