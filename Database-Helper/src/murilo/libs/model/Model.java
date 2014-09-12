/**
 * This is a generic model class to link a subclass of ObjectRelacional with a database table
 * 
 * @author Murilo Augusto Castagnoli de Quadros
 * @since 2014
 * @email macquadros@gmail.com
 */

package murilo.libs.model;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import murilo.libs.database.Connector;
import murilo.libs.database.Database;
import murilo.libs.relational.EncapsulatedObjectRelational;
import murilo.libs.relational.ObjectRelacionalFactory;
import murilo.libs.relational.ObjectRelational;

public class Model<T extends ObjectRelational> {

	private String table;
	private ObjectRelacionalFactory<T> factory;
	private Database db;
	private String primaryKey;
	private Class<T> reference;

	public Model(String table, String PrimaryKeyAttributeName,
			Class<T> reference) {
		this.table = table;
		this.reference = reference;
		primaryKey = PrimaryKeyAttributeName;
		db = new Database(Connector.getConnection());
		factory = new ObjectRelacionalFactory<T>(reference);
	}

	/**
	 * Use the database instance to generate a complex query
	 * 
	 * @return database
	 */
	public Database getDatabase() {
		return db;
	}

	/**
	 * Get primary key
	 * 
	 * @return
	 */
	public String getPrimaryKeyAttributeName() {
		return primaryKey;
	}

	/**
	 * Get a new instance of T class
	 * 
	 * @return new T object
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public T newInstance() throws InstantiationException,
			IllegalAccessException {
		return (T) reference.newInstance();
	}

	/**
	 * Return T object by primary key value
	 * 
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 * @throws NoSuchFieldException
	 */
	public T get(Object value) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException,
			NoSuchMethodException, SecurityException,
			InvocationTargetException, SQLException, NoSuchFieldException {
		T t = newInstance();
		t.setFieldValue(t.getField(primaryKey), value);
		return get(t);
	}

	/**
	 * Get a updated instance of T
	 * 
	 * @param t
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 * @throws NoSuchFieldException
	 */
	public T get(T t) throws IllegalArgumentException, IllegalAccessException,
			InstantiationException, NoSuchMethodException, SecurityException,
			InvocationTargetException, SQLException, NoSuchFieldException {
		db.where(t.getColumnName(primaryKey),
				t.getFieldValueAsString(t.getField(primaryKey)));
		List<T> list = factory.getList(db.get(table));
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * Get a specific T object
	 * 
	 * @param uniqueIdentifier
	 *            (database - column format)
	 * @param value
	 * @return T object
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 */
	public T get(String uniqueIdentifier, String value)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException, NoSuchMethodException, SecurityException,
			InvocationTargetException, SQLException {
		db.where(uniqueIdentifier, value);
		List<T> list = factory.getList(db.get(table));
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * Retrieve a list of T objects
	 * 
	 * @return list of T objects
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 */
	public List<T> list() throws IllegalArgumentException,
			IllegalAccessException, InstantiationException,
			NoSuchMethodException, SecurityException,
			InvocationTargetException, SQLException {
		return factory.getList(db.get(table));
	}

	/**
	 * Insert a T object in the database table
	 * 
	 * @param t
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	public void insert(T t) throws IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException {
		Integer id = db.insert(removeNullValues(t.export()), table);
		setId(id, t);
	}

	/**
	 * Insert a T object in the database table keeping null values
	 * 
	 * @param t
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	public void insertKeepingNullValues(T t) throws IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException {
		Integer id = db.insert(t.export(), table);
		setId(id, t);
	}

	/**
	 * Insert a T object in the database table
	 * 
	 * @param t
	 * @return auto generated id (Integer)
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	public Integer insertReturningGeneratedId(T t)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException {
		Integer id = db.insert(removeNullValues(t.export()), table);
		setId(id, t);
		return id;
	}

	/**
	 * Insert a T object in the database table and retrieve new object from
	 * table
	 * 
	 * @param t
	 * @return new T object
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 * @throws InstantiationException
	 * @throws SQLException
	 */
	public T insertReturningUpdatedObject(T t) throws IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException, SQLException {
		insert(t);
		return get(t);
	}

	/**
	 * Update a T object in database table
	 * 
	 * @param t
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 */
	public void update(T t) throws IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			InvocationTargetException {
		Map<String, String> export = removeNullValues(t.export());
		String primaryKey = t.getColumnName(this.primaryKey);
		db.where(primaryKey, export.get(primaryKey));
		export.remove(primaryKey);
		db.update(export, table);
	}

	/**
	 * Update a T object in database table keeping null values
	 * 
	 * @param t
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 */
	public void updateKeepingNullValues(T t) throws IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			InvocationTargetException {
		Map<String, String> export = t.export();
		String primaryKey = t.getColumnName(this.primaryKey);
		db.where(primaryKey, export.get(primaryKey));
		export.remove(primaryKey);
		db.update(export, table);
	}
	
	@SuppressWarnings("unchecked")
	public void update(EncapsulatedObjectRelational<?> et) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException, NoSuchFieldException {
		Map<String, String> export = et.export();
		if(!export.isEmpty()) {
			T t = (T) et.get();
			String pk = t.getColumnName(primaryKey);
			db.where(pk, t.getFieldValueAsString(t.getField(primaryKey)));
			db.update(export, table);
		}
	}

	/**
	 * Delete a T object from database table
	 * 
	 * @param t
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 */
	public void delete(T t) throws IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			InvocationTargetException {
		Map<String, String> export = t.export();
		String primaryKey = t.getColumnName(this.primaryKey);
		db.where(primaryKey, export.get(primaryKey));
		db.delete(table);
		t = null;
	}

	/**
	 * Delete by primary key
	 * 
	 * @param value
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 * @throws NoSuchFieldException
	 */
	public void delete(Object value) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException,
			NoSuchMethodException, SecurityException,
			InvocationTargetException, SQLException, NoSuchFieldException {
		delete(get(value));
	}

	/**
	 * Set auto generated id (Integer) in a recent inserted object
	 * 
	 * @param id
	 * @param t
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	private void setId(Integer id, T t) throws IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException {
		if (id != null) {
			t.setFieldValue(t.getField(primaryKey), id);
		}
	}

	/**
	 * Remove null values from exported values
	 * 
	 * @param map
	 * @return exported values without null values
	 */
	private Map<String, String> removeNullValues(Map<String, String> map) {
		Map<String, String> newMap = new HashMap<String, String>();
		Iterator<Map.Entry<String, String>> iterator = map.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> mapEntry = iterator.next();
			if (mapEntry.getValue() != null) {
				newMap.put(mapEntry.getKey(), mapEntry.getValue());
			}
		}
		return newMap;
	}

}
