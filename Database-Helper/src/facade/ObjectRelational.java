/**
 * This is a standard class to use Object Relational Mapping
 * 
 * @author Murilo Augusto Castagnoli de Quadros
 * @since 2014
 * @email macquadros@gmail.com
 */

package facade;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mysql.jdbc.ResultSet;

public class ObjectRelational {
	
	public static final int 
		SNAKELOWERCASE_TO_CAMELCASE = 1, 
		SNAKEUPPERCASE_TO_CAMELCASE = 2, 
		UPPERCASE_TO_LOWERCASE = 3, 
		NONE = 0;
	
	private Field[] fields;
	private String prefix;
	private int selectedCase;
	
	public ObjectRelational() {
		fields = this.getClass().getDeclaredFields();
	}
	
	public ObjectRelational(ResultSet resultSet) throws SQLException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		this();
		initialize(resultSet);
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * Database format to Java Object format
	 * @param mod values {SNAKELOWERCASE_TO_CAMELCASE, SNAKEUPPERCASE_TO_CAMELCASE, UPPERCASE_TO_LOWERCASE, NONE}
	 */
	public void setCaseMod(int mod) {
		selectedCase = mod;
	}
	
	public void initialize(ResultSet resultSet) throws IllegalArgumentException, IllegalAccessException, SQLException, NoSuchMethodException, SecurityException, InvocationTargetException {
		for (Field field : fields){
			String columnName = getColumnName(field.getName());
			setFieldValue(field, resultSet.getObject(columnName));
		}
	}
	
	public Map<String, String> export() throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		Map<String, String> map = new HashMap<String, String>();
		for(Field field : fields) {
			String columnName = getColumnName(field.getName());
			map.put(columnName, getFieldValueAsString(field));
		}
		return map;
	}
	
	public String getColumnName(String fieldName) {
		String columnName = setSelectedPrefix(fieldName);
		columnName = getStringInSelectedCase(columnName);
		return columnName;
	}
	
	public Field getField (String fieldName) throws NoSuchFieldException, SecurityException {
		return this.getClass().getDeclaredField(fieldName);
	}
	
	public void setFieldValue(Field field, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		if (field.getModifiers() == 1) {
			field.set(this, value);
		} else {
			Method method = this.getClass().getMethod(convertToSetMethod(field), value.getClass());
			method.invoke(this, value);
		}
	}
	
	public String getFieldValueAsString(Field field) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		String result;
		if (field.getModifiers() == 1) {
			result = ""+field.get(this);
		} else {
			Method method = this.getClass().getMethod(convertToGetMethod(field));
			result = ""+method.invoke(this);
		}
		return result;
	}
	
	private String convertToSetMethod (Field field) {
		return "set" + firstLetterToUpperCase(field.getName());
	}
	
	private String convertToGetMethod (Field field) {
		return "get" + firstLetterToUpperCase(field.getName());
	}
	
	private String camelToSnakeCase(String camelcase) {
		return camelcase.replaceAll("([A-Z][a-z])", "_$1");
	}
	
	private String getStringInSelectedCase(String string) {
		String result;
		switch(selectedCase){
			case SNAKELOWERCASE_TO_CAMELCASE:
				result = camelToSnakeCase(string).toLowerCase();
				break;
			case SNAKEUPPERCASE_TO_CAMELCASE:
				result = camelToSnakeCase(string).toUpperCase();
				break;
			case UPPERCASE_TO_LOWERCASE:
				result = string.toUpperCase();
				break;
			default:
				result = string;
		}
		return result;
	}
	
	private String setSelectedPrefix(String columnName) {
		if(prefix != null && !prefix.isEmpty()){
			columnName = prefix.concat(columnName);
		}
		return columnName;
	}
	
	private String firstLetterToUpperCase (String string) {
		return string.substring(0,1).toUpperCase() + string.substring(1); 
	}
		
	@Override
	public String toString() {
		String string = getClass().toString();
		string = string.concat(" {");
		for(Field field : fields) {
			string = string.concat(field.getName());
			string = string.concat("=");
			try {
				string = string.concat(getFieldValueAsString(field));
			} catch (IllegalArgumentException | IllegalAccessException
					| NoSuchMethodException | SecurityException
					| InvocationTargetException e) {
				string = string.concat("[error: the value is not accessible]");
				e.printStackTrace();
			}
			string = string.concat(", ");
		}
		string = string.concat("}");
		return string.replace(", }", "}");
	}

}
