package facade;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

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
	
	public ObjectRelational() {}
	
	public ObjectRelational(ResultSet resultSet) throws SQLException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		initialize(resultSet);
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setCaseMod(int mod) {
		selectedCase = mod;
	}
	
	public void initialize(ResultSet resultSet) throws IllegalArgumentException, IllegalAccessException, SQLException, NoSuchMethodException, SecurityException, InvocationTargetException {
		selectedCase = NONE;
		fields = this.getClass().getDeclaredFields();
		for (Field field: fields){
			String columnName = setSelectedPrefix(field.getName());
			columnName = getStringInSelectedCase(columnName);
			setFieldValue(field, resultSet.getObject(columnName));
		}
	}
	
	private void setFieldValue(Field field, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		if (field.getModifiers() == 1) {
			field.set(this, value);
		} else {
			Method method = this.getClass().getMethod(convertToSetMethod(field), value.getClass());
			method.invoke(this, value);
		}
	}
	
	private String convertToSetMethod (Field field) {
		return "set" + firstLetterToUpperCase(field.getName());
	}
	
	private String camelToSnakeCase(String snakecase) {
		return snakecase.replaceAll("([A-Z][a-z]+)", "$1_");
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

}
