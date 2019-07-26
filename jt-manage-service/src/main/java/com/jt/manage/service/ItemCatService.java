package com.jt.manage.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.jt.common.service.RedisService;
import com.jt.common.util.RedisCluster;
import com.jt.common.vo.ItemCatData;
import com.jt.common.vo.ItemCatResult;
import com.jt.manage.mapper.ItemCatMapper;
import com.jt.manage.pojo.Item;
import com.jt.manage.pojo.ItemCat;

import redis.clients.jedis.JedisCluster;

@Service
public class ItemCatService extends BaseService<ItemCat>{
	@Autowired
	private ItemCatMapper itemCatMapper;
	
	//引入java对象和json串转换对象ObjectMapper；全局唯一
	private static final ObjectMapper MAPPER = new ObjectMapper();
	

	public List<ItemCat> queryListById(Long id) throws IOException{
		
		 /** 商品分类要使用缓存步骤：
		 * 1）先判断缓存中是否有数据，如果有数据就读取，直接返回
		 * 2）如果缓存中没有数据，要继续执行业务，不能抛出异常
		 * 3）执行完业务，要多一步动作，要把结果放入缓存中string，先把java对象转换成json串，kv写入缓存中。
		 */
		
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String ITEM_CAT_KEY = "ITEM_CAT_" + id;	//唯一性
		System.out.println("生成的唯一ID,ITEM_CAT_KEY："+ITEM_CAT_KEY);
		//从redis中获取数据
		String jsonItemCat = (String) redisTemplate.boundValueOps(ITEM_CAT_KEY).get();
		System.out.println("从redis获取的缓存数据jsonItemCat:"+jsonItemCat);
		if(StringUtils.isNotEmpty(jsonItemCat)){
			JsonNode jsonNode = MAPPER.readTree(jsonItemCat);	//把json串转换JsonNode
			//利用jackson提供方法，将json串转成java对象，List<Object>
			List<ItemCat> itemCatList = MAPPER.readValue(jsonNode.traverse(),
                    MAPPER.getTypeFactory().constructCollectionType(List.class, ItemCat.class));
			for (ItemCat ic  : itemCatList) {
				System.out.println("当缓存不为空时："+ic);
			}
			return itemCatList;
		}else{
			//继续执行业务，一般都是去数据库访问
			ItemCat ic = new ItemCat();
			ic.setParentId(id);
			List<ItemCat> itemCatList = itemCatMapper.select(ic);
			for (ItemCat icc  : itemCatList) {
				System.out.println("当缓存==空时："+icc);
			}
			//写redis，kv(string,string)
			jsonItemCat = MAPPER.writeValueAsString(itemCatList);	//将java对象转成json串
			redisTemplate.boundValueOps(ITEM_CAT_KEY).set(jsonItemCat);		
			System.out.println("存入redis缓存的数据jsonItemCat"+jsonItemCat);
			return itemCatList;
		}
	}
	//专门为前台系统构建json格式的对象
	//返回值类型是:ItemCatResult
	//核心代码
	public ItemCatResult queryItemCatWebAll() {
		/*
		 * 1.定义json存储的对象:itemcatresult
		 * 2.查询所有的目录结构 共三级分层的菜单
		 * 3.后去第二部的菜单下的子菜单
		 * 4.构建三级json目录的格式 
		 */
		ItemCatResult itemCatResult = new ItemCatResult();
		//查询所有的目录list集合
		List<ItemCat> list = super.queryAll();
		System.out.println("list集合"+list);
		//定义一个map集合
		Map<Long,List<ItemCat>> map = new HashMap<Long,List<ItemCat>>();
		for (ItemCat itemCat : list) {
			if (!map.containsKey(itemCat.getParentId())) {
				map.put(itemCat.getParentId(),new ArrayList<ItemCat>());
			}
			map.get(itemCat.getParentId()).add(itemCat);
		}
		System.out.println("封装后的map集合："+map);

		
		//使用for循环，继续遍历map集合
		//把数据封装到ITtemCatResult对象中
		//一级目录:定义list集合，存放的itemcatData
		List<ItemCatData> list_1 = new ArrayList<ItemCatData>();
		for (ItemCat itemCat_1:map.get(0L)) {
			//把ItemCat对象:封装成ItemCatData对象
			//ItemCatData有三个属性:u/n/i
			ItemCatData data_1 = new ItemCatData();
			data_1.setUrl("/products"+itemCat_1.getId()+".html");
			data_1.setName("<a href='products/"+itemCat_1.getId()+".html'></a>"+itemCat_1.getName());
			//二级目录需要循环:data_1.setItems(items)
			List<ItemCatData> list_2 = new ArrayList<ItemCatData>();
			//item_cat_1的id为cat_2的parent_id
			//itemcat_1为父节点，cat_2为子节点
			for(ItemCat itemCat_2 : map.get(itemCat_1.getId())){
				ItemCatData data_2 = new ItemCatData();
				data_2.setUrl("/products"+itemCat_2.getId()+".html");
				data_2.setName(itemCat_2.getName());
				//三级目录List集合:data_2.setItems(items)
				//itemcat_2的id为itemcat_3的parentId
				//itemcat_2为二级父节点,itemcat_3为三级子节点
				List<String> list_3 = new ArrayList<String>();
				for(ItemCat itemCat_3 : map.get(itemCat_2.getId()) ){
					list_3.add("/products/"+itemCat_3.getId()+".html|"+itemCat_3.getName());
				}
				data_2.setItems(list_3);
				list_2.add(data_2);
				System.out.println(list_3);
			}
			data_1.setItems(list_2);
			list_1.add(data_1);
			System.out.println(list_2);
			
		}
		itemCatResult.setItemCats(list_1);
		System.out.println(list_1);
		return itemCatResult;
	

	}
}

