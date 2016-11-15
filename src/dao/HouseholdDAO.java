package dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

import ibatis.MyAppSqlMapConfig;
import vo.HouseholdVO;

public class HouseholdDAO {
	public List<HouseholdVO> selectHouseholdList(String householdDate){
		
		ArrayList<HouseholdVO> resultList = null;
		
		HashMap<String, String> paramMap = new HashMap<String,String>();
		
		paramMap.put("householdDate",householdDate);
		
		SqlMapClient sqlMap = MyAppSqlMapConfig.getSqlMapInstance();
		try {
			resultList = (ArrayList<HouseholdVO>) sqlMap.queryForList("Household.selectHouseholdList",paramMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	public List<HouseholdVO> selectHousehold(String householdDate){
		
		ArrayList<HouseholdVO> resultList = null;
		
		HashMap<String, String> paramMap = new HashMap<String,String>();
		
		paramMap.put("householdDate",householdDate);
		
		SqlMapClient sqlMap = MyAppSqlMapConfig.getSqlMapInstance();
		try {
			resultList = (ArrayList<HouseholdVO>) sqlMap.queryForList("Household.selectHousehold",paramMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	public void insertHousehold(String householdDate, String householdHistory, String householdInamt, String householdOutamt){
		
		HashMap<String, String> paramMap = new HashMap<String,String>();
		
		paramMap.put("householdDate",householdDate);
		paramMap.put("householdHistory",householdHistory);
		paramMap.put("householdInamt",householdInamt+"");
		paramMap.put("householdOutamt",householdOutamt+"");
		
		SqlMapClient sqlMap = MyAppSqlMapConfig.getSqlMapInstance();
		try {
			sqlMap.insert("Household.insertHousehold",paramMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateHousehold(String householdId, String householdDate, String householdHistory, String householdInamt, String householdOutamt){
		
		HashMap<String, String> paramMap = new HashMap<String,String>();
		
		paramMap.put("householdId", householdId);
		paramMap.put("householdDate",householdDate);
		paramMap.put("householdHistory",householdHistory);
		paramMap.put("householdInamt",householdInamt+"");
		paramMap.put("householdOutamt",householdOutamt+"");
		
		SqlMapClient sqlMap = MyAppSqlMapConfig.getSqlMapInstance();
		try {
			sqlMap.update("Household.updateHousehold",paramMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteHousehold(String householdId){
		
		HashMap<String, String> paramMap = new HashMap<String,String>();
		
		paramMap.put("householdId",householdId);
		
		SqlMapClient sqlMap = MyAppSqlMapConfig.getSqlMapInstance();
		try {
			sqlMap.delete("Household.deleteHousehold",paramMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		HouseholdDAO dao = new HouseholdDAO();
		dao.deleteHousehold("7");
	}
}
