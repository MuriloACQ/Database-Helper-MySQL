/**
 * This is a generic model class to link a subclass of ObjectRelacional with a database table
 * 
 * @author Murilo Augusto Castagnoli de Quadros
 * @since 2014
 * @email macquadros@gmail.com
 */

package murilo.libs.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import murilo.libs.database.Connector;
import murilo.libs.database.Database;
import murilo.libs.model.exception.ModelException;
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
	 * @throws ModelException
	 */
	public T newInstance() throws ModelException {
		try {
			return (T) reference.newInstance();
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	/**
	 * Return T object by primary key value
	 * 
	 * @param value
	 * @return
	 * @throws ModelException
	 */
	public T get(Object value) throws ModelException {
		T t = newInstance();
		try {
			t.setFieldValue(t.getField(primaryKey), value);
			return get(t);
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	/**
	 * Return T encapsulated object by primary key value
	 * 
	 * @param value
	 * @return
	 * @throws ModelException
	 */
	public EncapsulatedObjectRelational<T> getEncapsulated(Object value)
			throws ModelException {
		return new EncapsulatedObjectRelational<T>(get(value));
	}

	/**
	 * Get a updated instance of T
	 * 
	 * @param t
	 * @return
	 * @throws ModelException
	 */
	public T get(T t) throws ModelException {
		try {
			db.where(t.getColumnName(primaryKey),
					t.getFieldValueAsString(t.getField(primaryKey)));

			List<T> list = factory.getList(db.get(table));
			if (list.size() == 1) {
				return list.get(0);
			}
		} catch (Exception e) {
			throw new ModelException(e);
		}
		return null;
	}

	/**
	 * Get a updated instance of encapsulated object
	 * 
	 * @param et
	 * @return
	 * @throws ModelException
	 */
	public EncapsulatedObjectRelational<T> getEncapsulated(
			EncapsulatedObjectRelational<T> et) throws ModelException {
		return new EncapsulatedObjectRelational<T>(get(et.get()));
	}

	/**
	 * Get a specific T object
	 * 
	 * @param uniqueIdentifier
	 *            (database - column format)
	 * @param value
	 * @return T object
	 * @throws ModelException
	 */
	public T get(String uniqueIdentifier, String value) throws ModelException {
		try {
			db.where(uniqueIdentifier, value);
			List<T> list = factory.getList(db.get(table));
			if (list.size() == 1) {
				return list.get(0);
			}
		} catch (Exception e) {
			throw new ModelException(e);
		}
		return null;
	}

	/**
	 * Get a specific T encapsulated object
	 * 
	 * @param uniqueIdentifier
	 * @param value
	 * @return
	 * @throws ModelException
	 */
	public EncapsulatedObjectRelational<T> getEncapsulated(
			String uniqueIdentifier, String value) throws ModelException {
		return new EncapsulatedObjectRelational<T>(get(uniqueIdentifier, value));
	}

	/**
	 * Retrieve a list of T objects
	 * 
	 * @return list of T objects
	 * @throws ModelException
	 */
	public List<T> list() throws ModelException {
		try {
			return factory.getList(db.get(table));
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	/**
	 * Retrieve a list of T encapsulated objects
	 * 
	 * @return
	 * @throws ModelException
	 */
	public List<EncapsulatedObjectRelational<T>> listEncapsulated()
			throws ModelException {
		List<EncapsulatedObjectRelational<T>> list = new ArrayList<EncapsulatedObjectRelational<T>>();
		for (T t : list()) {
			list.add(new EncapsulatedObjectRelational<T>(t));
		}
		return list;
	}

	/**
	 * Insert a T object in the database table
	 * 
	 * @param t
	 * @throws ModelException
	 */
	public void insert(T t) throws ModelException {
		try {
			Integer id = db.insert(removeNullValues(t.export()), table);
			setId(id, t);
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	/**
	 * Insert a T object in the database table
	 * 
	 * @param et
	 * @throws ModelException
	 */
	public void insert(EncapsulatedObjectRelational<T> et)
			throws ModelException {
		insert(et.get());
	}

	/**
	 * Insert a T object in the database table keeping null values
	 * 
	 * @param t
	 * @throws ModelException
	 */
	public void insertKeepingNullValues(T t) throws ModelException {
		try {
			Integer id = db.insert(t.export(), table);
			setId(id, t);
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	/**
	 * Insert a T object in the database table
	 * 
	 * @param t
	 * @return auto generated id (Integer)
	 * @throws ModelException
	 */
	public Integer insertReturningGeneratedId(T t) throws ModelException {
		try {
			Integer id = db.insert(removeNullValues(t.export()), table);
			setId(id, t);
			return id;
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	/**
	 * Insert a T object in the database table
	 * 
	 * @param et
	 * @return
	 * @throws ModelException
	 */
	public Integer insertReturningGeneratedId(EncapsulatedObjectRelational<T> et)
			throws ModelException {
		return insertReturningGeneratedId(et.get());
	}

	/**
	 * Insert a T object in the database table and retrieve new object from
	 * table
	 * 
	 * @param t
	 * @return new T object
	 * @throws ModelException
	 */
	public T insertReturningUpdatedObject(T t) throws ModelException {
		insert(t);
		return get(t);
	}

	/**
	 * Insert a T object in the database table and retrieve new object from
	 * table
	 * 
	 * @param et
	 * @return
	 * @throws ModelException
	 */
	public EncapsulatedObjectRelational<T> insertReturningUpdatedObject(
			EncapsulatedObjectRelational<T> et) throws ModelException {
		return new EncapsulatedObjectRelational<T>(
				insertReturningUpdatedObject(et.get()));
	}

	/**
	 * Update a T object in database table
	 * 
	 * @param t
	 * @throws ModelException
	 */
	public void update(T t) throws ModelException {
		try {
			Map<String, String> export = removeNullValues(t.export());
			String primaryKey = t.getColumnName(this.primaryKey);
			db.where(primaryKey, export.get(primaryKey));
			export.remove(primaryKey);
			db.update(export, table);
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	/**
	 * Update a T object in database table keeping null values
	 * 
	 * @param t
	 * @throws ModelException
	 */
	public void updateKeepingNullValues(T t) throws ModelException {
		try {
			Map<String, String> export = t.export();
			String primaryKey = t.getColumnName(this.primaryKey);
			db.where(primaryKey, export.get(primaryKey));
			export.remove(primaryKey);
			db.update(export, table);
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	/**
	 * Update a T object in database table keeping null values
	 * 
	 * @param et
	 * @throws ModelException
	 */
	@SuppressWarnings("unchecked")
	public void update(EncapsulatedObjectRelational<?> et)
			throws ModelException {
		try {
			Map<String, String> export = et.export();
			if (!export.isEmpty()) {
				T t = (T) et.get();
				String pk = t.getColumnName(primaryKey);
				db.where(pk, t.getFieldValueAsString(t.getField(primaryKey)));
				db.update(export, table);
			}
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	/**
	 * Delete a T object from database table
	 * 
	 * @param t
	 * @throws ModelException
	 */
	public void delete(T t) throws ModelException {
		try {
			Map<String, String> export = t.export();
			String primaryKey = t.getColumnName(this.primaryKey);
			db.where(primaryKey, export.get(primaryKey));
			db.delete(table);
			t = null;
		} catch (Exception e) {
			throw new ModelException(e);
		}
	}

	/**
	 * Delete a T object from database table
	 * 
	 * @param et
	 * @throws ModelException
	 */
	@SuppressWarnings("unchecked")
	public void delete(EncapsulatedObjectRelational<?> et)
			throws ModelException {
		delete((T) et.get());
	}

	/**
	 * Delete by primary key
	 * 
	 * @param value
	 * @throws ModelException
	 */
	public void delete(Object value) throws ModelException {
		T t = newInstance();
		try {
			t.setFieldValue(t.getField(primaryKey), value);
		} catch (Exception e) {
			throw new ModelException(e);
		}
		delete(t);
	}

	/**
	 * Set auto generated id (Integer) in a recent inserted object
	 * 
	 * @param id
	 * @param t
	 * @throws ModelException
	 */
	private void setId(Integer id, T t) throws ModelException {
		if (id != null) {
			try {
				t.setFieldValue(t.getField(primaryKey), id);
			} catch (Exception e) {
				throw new ModelException(e);
			}
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
