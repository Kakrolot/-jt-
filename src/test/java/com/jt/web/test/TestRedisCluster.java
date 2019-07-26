package com.jt.web.test;
//主要用于测试redis集群的增删改查
//

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

public class TestRedisCluster {
	//需要定义成员变量:RedisTemplate
	private RedisTemplate redisTemplate;
	//需要读取spring的配置文件
	//通过bean标签获取redisTemplate对象
	@Before
	public void before() {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-rediscluster.xml");
		redisTemplate = (RedisTemplate) context.getBean("redisTemplate");
	
	}
	
	@Test
	public void test01(){
		redisTemplate.boundValueOps("name").set("redis is good");
	}
	
	@Test
	public void test02(){
		//从redis集群中,获取key为name的值
		String name = (String) redisTemplate.boundValueOps("name").get();
		System.out.println(name);
	}
	@Test
	public void test03(){
		redisTemplate.delete("name");
	}
	@Test
	public void test04(){
		//插入list集合的数据
		//key是List的名称，唯一的
		//value 是list的每一个元素,多个元素
		//right右压栈 ， 顺序是从左到右
		//举例:写字时，从左向右写
		redisTemplate.boundListOps("name_list").rightPush("tony");
		redisTemplate.boundListOps("name_list").rightPush("huge");
		redisTemplate.boundListOps("name_list").rightPush("lining");
	}
	@Test
	public void test05(){
		//从redis集群中,获取list集合
		List<String> list =  redisTemplate.boundListOps("name_list").range(0, 3);
		for (String name : list) {
			System.out.println(name);
		}
	}
}
