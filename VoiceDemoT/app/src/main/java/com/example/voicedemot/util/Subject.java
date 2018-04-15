package com.example.voicedemot.util;


/**
 * Created by chenyiAlone on 2018/4/10.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class Subject {
	
	Connection conn = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    
    /*
     * 获取数据库连接
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
    	
    	
    	 Connection conn = null;
    	 Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
		conn = DriverManager.getConnection("jdbc:mysql://Ip:端口号/Xxxxxx?"
				+ "user=Xxxxxxx&password=Xxxxxxxxx&useUnicode=true&characterEncoding=UTF8");
		return conn;
	}
    
    /*
     * 释放连接
     */
    public static void release(Connection conn,Statement st,ResultSet rs){

        if(rs!=null){
            try{
                //关闭存储查询结果的ResultSet对象
                rs.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(st!=null){
            try{
                //关闭负责执行SQL命令的Statement对象
                st.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if(conn!=null){
            try{
                //将Connection连接对象还给数据库连接池
                conn.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    /*
     * 抽题
     */
    public Map<String, String> chouti() {

    	     Map<String, String> map = new HashMap<String, String>();
    	     int count = 0;
   	
		   	 try{
		            //获取数据库连接
		            conn = getConnection();

		            //查询题库的题目总条数
		            String sql = "select count(*) as sum from exercises";
		            st = conn.prepareStatement(sql);
		            rs = st.executeQuery();
		            while (rs.next()) { 
		            	count=Integer.valueOf(rs.getString("sum"));
		            }

		            //抽取题号（10道题）
		            Set<Integer> set = new TreeSet<>();
		            while (set.size() <= 10) {
						int i = (int)(Math.random()*count)+1;
		            	if (i!=0) {
		            		set.add(i);
						}
					}
		            
		            //根据抽取的题号查询题目
		            for (Integer o : set) {
		            	 String string = "select * from exercises where id="+o;
		            	 st = conn.prepareStatement(string);
				         rs = st.executeQuery();
				         while (rs.next()) { 
				            map.put(rs.getString("problem"), rs.getString("answer"));
				            System.out.println(rs.getString("problem")+"********"+ rs.getString("answer"));
				         }
					}
		        }catch (Exception e) {
		            e.printStackTrace();
		        }finally{
		            //释放资源
		        	release(conn, st, rs);
		        }
		   	
		   	return map;
			}
}
