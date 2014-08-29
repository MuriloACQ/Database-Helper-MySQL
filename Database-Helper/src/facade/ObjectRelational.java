/**
 * This is a standard class to use Object Relational Mapping
 * Use generic constructor in subclasses to configure prefix and caseMod
 * Avoid primitive types in attributes of the subclasses
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

	/**
	 * Set a prefix in each column of table
	 * @param prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * From Database format to Java Object format
	 * @param mod values {SNAKELOWERCASE_TO_CAMELCASE, SNAKEUPPERCASE_TO_CAMELCASE, UPPERCASE_TO_LOWERCASE, NONE}
	 */
	public void setCaseMod(int mod) {
		selectedCase = mod;
	}
	
	/**
	 * This method is automatically called when you use a factory or the constructor <ObjectRelational(ResultSet resultSet)>
	 * @param resultSet
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 */
	public void initialize(ResultSet resultSet) throws IllegalArgumentException, IllegalAccessException, SQLException, NoSuchMethodException, SecurityException, InvocationTargetException {
		fields = this.getClass().getDeclaredFields();
		for (Field field : fields){
			String columnName = getColumnName(field.getName());
			setFieldValue(field, resultSet.getObject(columnName));
		}
	}
	
	/**
	 * Export to format <String key, String value> compatible with table columns 
	 * @return Map<String, String>
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 */
	public Map<String, String> export() throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		Map<String, String> map = new HashMap<String, String>();
		for(Field field : fields) {
			String columnName = getColumnName(field.getName());
			map.put(columnName, getFieldValueAsString(field));
		}
		return map;
	}
	
	/**
	 * Get the column name compatible with table
	 * @param fieldName
	 * @return column name
	 */
	public String getColumnName(String fieldName) {
		String columnName = setSelectedPrefix(fieldName);
		columnName = getStringInSelectedCase(columnName);
		return columnName;
	}
	
	/**
	 * Get a field declared in the class by name
	 * @param fieldName
	 * @return field
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public Field getField (String fieldName) throws NoSuchFieldException, SecurityException {
		return this.getClass().getDeclaredField(fieldName);
	}
	
	/**
	 * Set a value in a field
	 * @param field
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 */
	public void setFieldValue(Field field, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		if (field.getModifiers() == 1) {
			field.set(this, value);
		} else {
			Method method = this.getClass().getMethod(convertToSetMethod(field), value.getClass());
			method.invoke(this, value);
		}
	}
	
	/**
	 * Get a field value. Null value will return a string "null"
	 * @param field
	 * @return String
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 */
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
	
	/**
	 * Convert a field name in a set method name using Java standard code definition
	 * @param field
	 * @return set method name
	 */
	private String convertToSetMethod (Field field) {
		return "set" + firstLetterToUpperCase(field.getName());
	}
	
	/**
	 * Convert a field name in a get method name using Java standard code definition
	 * @param field
	 * @return get method name
	 */
	private String convertToGetMethod (Field field) {
		return "get" + firstLetterToUpperCase(field.getName());
	}
	
	/**
	 * Convert a camelcase string in a snakecase string
	 * @param camelcase
	 * @return snakecase
	 */
	private String camelToSnakeCase(String camelcase) {
		return camelcase.replaceAll("([A-Z][a-z])", "_$1");
	}
	
	/**
	 * Convert a string to be compatible with database columns
	 * @param string
	 * @return string in the selected format
	 */
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
	
	/**
	 * Apply prefix in a column name
	 * @param columnName
	 * @return column name with prefix
	 */
	private String setSelectedPrefix(String columnName) {
		if(prefix != null && !prefix.isEmpty()){
			columnName = prefix.concat(columnName);
		}
		return columnName;
	}
	
	/**
	 * Make the first letter uppercase
	 * @param string
	 * @return string with first letter uppercase 
	 */
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
