package com.jt.manage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.jt.manage.mapper.base.mapper.SysMapper;

//抽象类：抽象出所有的公用方法
//抽离出所有的业务逻辑层的增删改查的方法
//举例：泛型<T> 对应着 不同的业务模块
//        <ItemCat>  对应着   Item模块
//        <User>   对应着 User模块
public abstract class BaseService<T> {
	//根据泛型，自动的去匹配不同的mapper接口
	@Autowired
	private SysMapper<T> sysMapper;
	@Autowired
	public RedisTemplate redisTemplate;
	//以下方法是抽离出的公用方法
	//命名规则：是一种固定的前缀形式
	//这个前缀，主要是用于 spring的AOP切面进行事务控制
	 /**
     * 根据主键查询数据
     * 
     * @param id
     * @return
     */
    public T queryById(Object id) {
        return this.sysMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据条件查询，多条件之间是 and 关系
     * 
     * @param t
     * @return
     */
    public List<T> queryListByWhere(T t) {
        return this.sysMapper.select(t);
    }

    /**
     * 根据条件查询单条数据
     * 
     * @param t
     * @return
     */
    public T queryByWhere(T t) {
        List<T> list = queryListByWhere(t);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询所有数据
     * 
     * @return
     */
    public List<T> queryAll() {
        return this.sysMapper.select(null);
    }

    /**
     * 新增数据，使用全部字段
     * 
     * @param t
     */
    public void save(T t) {
        this.sysMapper.insert(t);
    }

    /**
     * 新增数据，使用不为null的字段
     * 
     * @param t
     */
    public void saveSelective(T t) {
        this.sysMapper.insertSelective(t);
    }

    /**
     * 根据id删除
     * 
     * @param id
     * @return
     */
    public Integer deleteById(Object id) {
        return this.sysMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据ids删除
     * 
     * @param ids
     * @return
     */
    public Integer deleteByIds(Object[] ids) {
        return this.sysMapper.deleteByIDS(ids);
    }

    /**
     * 根据条件删除
     * 
     * @param t
     */
    public Integer deleteByWhere(T t) {
        return this.sysMapper.delete(t);
    }

    /**
     * 根据主键id更新数据
     * 
     * @param t
     */
    public Integer update(T t) {
        return this.sysMapper.updateByPrimaryKey(t);
    }
    
    /**
     * 根据主键id更新数据
     * 
     * @param t
     */
    public Integer updateSelective(T t) {
        return this.sysMapper.updateByPrimaryKeySelective(t);
    }

}



