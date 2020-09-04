package com.cc.test;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.cc.domain.User;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class TxTest {
	ApplicationContext app=new ClassPathXmlApplicationContext("applicationContext.xml");
	JdbcTemplate jdbcTemplate=(JdbcTemplate) app.getBean("jdbcTemplate");
	NamedParameterJdbcTemplate namedJdbcTemplate=(NamedParameterJdbcTemplate) app.getBean("namedParameterJdbcTemplate");
	@Test
	public void test() throws SQLException {
		
		DataSource dataSource = app.getBean(DataSource.class);
		Connection connection = dataSource.getConnection();
		System.out.println(connection);
		connection.close();
	}
	
	//修改
	@Test
	public void test02() {
		String sql="update t_user set username=? where id=?";
		int update = jdbcTemplate.update(sql, "王八蛋",15);
		System.out.println(update);
	}
	
	
	
	//批量插入
	@Test
	public void test03() {
		String sql="insert into t_user (username,password) values(?,?)";
		//List的长度就是sql语句要执行的次数
		List<Object[]> batchArgs=new ArrayList<Object[]>();
		//Object[] 保存的是每次要执行的参数
		batchArgs.add(new Object[] {"张三","123456"});
		batchArgs.add(new Object[] {"李四","123456"});
		batchArgs.add(new Object[] {"王五","123456"});
		batchArgs.add(new Object[] {"赵六","123456"});
		int[] is=jdbcTemplate.batchUpdate(sql, batchArgs); //每次影响的行数
		for (int i : is) {
			System.out.println(i);
		}
	}
	
	//查询单个对象
	@Test
	public void test04() {
		String sql="select * from t_user where id=?";
		//RowMapper 每一行数据和javabean如何映射
		//queryForObject 查询没结果就报错
		User user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class),15);
		System.out.println(user);
		
	}
	
	//查询集合
	@Test
	public void test05() {
	  String sql="select * from t_user where id>=?";
	  List<User> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), 15);
	  for (User user : list) {
		System.out.println(user);
	}
	  
	}
	//查询单个数据， 返回的是单个数据的类型
		@Test
		public void test06() {
			String sql="select MAX(id) from t_user ";
			//RowMapper 每一行数据和javabean如何映射
			//queryForObject 查询没结果就报错
			Integer object = jdbcTemplate.queryForObject(sql, Integer.class);
			System.out.println(object);
			
		}
	
	/*
	 * 使用带有具名参数的sql语句插入一条记录，并以map形式传入参数值
	 * 
	 * 具名参数: 具有名字的参数，即参数不是占位符了，而是一个具体的变量名
	 *   语法格式:    :参数名
	 *   spring有一个支持具名参数的jdbctemplate
	 *   namedJdbcTemplate
	 *     可以支持map作为具名参数值的来源，也支持bean属性作为来源
	 *   
	 * 
	 * 占位符参数: ?的顺序不能乱，书写不方便
	 */
		
		@Test
		public void test07() {
			String sql="insert into t_user(username,password,age,sex,birthday) values(:username,:password,:age,:sex,:birthday)";
			Map<String, Object> paramMap=new HashMap<String, Object>();
			//将所有具名参数的值放入map中
			 paramMap.put("username", "洛无极");
			 paramMap.put("password", "123456");
			 paramMap.put("age", 22);
			 paramMap.put("sex", "男");
			 paramMap.put("birthday", new Date());
			int update = namedJdbcTemplate.update(sql, paramMap);
			System.out.println(update);
		}
		
		//以bean属性作为具名参数来源，即以SqlParamterSource形式传入值
		@Test
		public void test08() {
			String sql="insert into t_user(username,password,age,sex,birthday) values(:username,:password,:age,:sex,:birthday)";
		      User user=new User();
		      user.setUsername("小魔君");
		      user.setPassword("123456");
		      user.setAge(21);
		      user.setSex("男");
		      user.setBirthday(new Date());
			int update = namedJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(user));
			System.out.println(update);
		}
}
