package model;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.ResultSet;

import database.Connector;
import database.Database;
import facade.ObjectRelacionalFactory;
import facade.ObjectRelational;

public class Model<T extends ObjectRelational> {

	private String table;
	ObjectRelacionalFactory<T> factory;
	private Database db;
	private String primaryKey;
	
	public Model(String table, String PrimeryKeyAttributeName, Class<T> reference) {
		this.table = table;
		primaryKey = PrimeryKeyAttributeName;
		db = new Database(Connector.getConnection());
		factory = new ObjectRelacionalFactory<T>(reference);
	}
	
	public T get(String primaryKey, String value) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchMethodException, SecurityException, InvocationTargetException, SQLException {
		db.where(primaryKey, value);
		ResultSet resultSet = db.get(table);
		List<T> list = factory.getList(resultSet);
		if(list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
	
	public List<T> list() throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchMethodException, SecurityException, InvocationTargetException, SQLException{
		ResultSet resultSet = db.get(table);
		return factory.getList(resultSet);
	}
	
	public void insert(T t) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException, NoSuchFieldException {
		Integer id = db.insert(removeNullValues(t.export()), table);
		setId(id, t);
	}
	
	public void insertKeepingNullValues(T t) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException, NoSuchFieldException{
		Integer id = db.insert(t.export(), table);
		setId(id, t);
	}
	
	public Integer insertReturningGeneratedId (T t) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException, NoSuchFieldException {
		Integer id = db.insert(removeNullValues(t.export()), table);
		setId(id, t);
		return id;
	}
	
	public void update (T t) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		Map<String, String> export = t.export();
		String primaryKey = t.getColumnName(this.primaryKey);
		db.where(primaryKey, export.get(primaryKey));
		db.update(export, table);
	}
	
	public void delete (T t) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		Map<String, String> export = t.export();
		String primaryKey = t.getColumnName(this.primaryKey);
		db.where(primaryKey, export.get(primaryKey));
		db.delete(table);
		t = null;
	}
	
	private void setId(Integer id, T t) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException, NoSuchFieldException {
		if(id != null) {
			t.setFieldValue(t.getField(primaryKey), id);
		}
	}
	
	private Map<String, String> removeNullValues(Map <String, String> map) {
		Map <String, String> newMap = new HashMap<String, String>();
		Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> mapEntry = iterator.next();
			if(mapEntry.getValue() != null && !mapEntry.getValue().equals("null")) {
				newMap.put(mapEntry.getKey(), mapEntry.getValue());
			}  
		}
		return newMap;
	}
	
}
