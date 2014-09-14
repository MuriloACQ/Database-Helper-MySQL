package murilo.libs.model;

import static murilo.libs.utils.Utils.getLastPartClass;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import murilo.libs.model.exception.ModelException;
import murilo.libs.relational.EncapsulatedObjectRelational;
import murilo.libs.relational.ObjectRelational;

public class ModelLinker {

	private Map<String, Model<ObjectRelational>> models;
	private Map<String, Map<String, String>> links;

	public ModelLinker(Map<String, Model<ObjectRelational>> models) {
		this.models = models;
		links = new HashMap<String, Map<String, String>>();
	}

	public Model<ObjectRelational> getModel(String clazz) {
		return models.get(clazz);
	}

	public Map<String, Model<ObjectRelational>> getAllModels() {
		return models;
	}

	public void setLink(String clazz, String foreignKeyAttributeName,
			String foreignKeyTable) {
		clazz = getLastPartClass(clazz);
		Map<String, String> link = links.get(clazz);
		if (link == null) {
			link = new HashMap<String, String>();
		}
		link.put(foreignKeyAttributeName, foreignKeyTable);
		links.put(clazz, link);
	}

	public ObjectRelational get(ObjectRelational obj,
			String foreignKeyAttributeName) throws IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException, SQLException, ModelException {
		String clazz = getLastPartClass(obj.getClass().getName());
		return get(clazz, obj, foreignKeyAttributeName);
	}

	public EncapsulatedObjectRelational<ObjectRelational> getEncapsulated(
			EncapsulatedObjectRelational<ObjectRelational> eobj,
			String foreignKeyAttributeName) throws IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException, SQLException, ModelException {
		return new EncapsulatedObjectRelational<ObjectRelational>(get(
				eobj.get(), foreignKeyAttributeName));
	}

	public EncapsulatedObjectRelational<ObjectRelational> getEncapsulated(
			ObjectRelational obj, String foreignKeyAttributeName)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException, SQLException, ModelException {
		return new EncapsulatedObjectRelational<ObjectRelational>(get(obj,
				foreignKeyAttributeName));
	}

	public List<ObjectRelational> getExported(ObjectRelational obj, String clazz)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException, ModelException {
		List<ObjectRelational> list = null;
		String klass = getLastPartClass(obj.getClass().getName());
		clazz = getLastPartClass(clazz);
		Model<ObjectRelational> model = models.get(klass);
		String pk = model.getPrimaryKeyAttributeName();
		Map<String, String> link = links.get(clazz);
		String foreignAttribute = null;
		Iterator<Map.Entry<String, String>> iterator = link.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> mapEntry = iterator.next();
			if (getLastPartClass(mapEntry.getValue()).equals(klass)) {
				foreignAttribute = mapEntry.getKey();
				break;
			}
		}
		if (foreignAttribute != null) {
			model = models.get(clazz);
			model.getDatabase().where(obj.getColumnName(foreignAttribute),
					obj.getFieldValueAsString(obj.getField(pk)));
			list = model.list();
		}
		return list;
	}

	public List<EncapsulatedObjectRelational<ObjectRelational>> getEncapsulatedExported(
			EncapsulatedObjectRelational<ObjectRelational> eobj, String clazz)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException, SQLException, ModelException {
		return EncapsulatedObjectRelational.encapsulate(getExported(eobj.get(),
				clazz));
	}

	public List<EncapsulatedObjectRelational<ObjectRelational>> getEncapsulatedExported(
			ObjectRelational obj, String clazz)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException, SQLException, ModelException {
		return EncapsulatedObjectRelational
				.encapsulate(getExported(obj, clazz));
	}

	public List<ObjectRelational> get(ObjectRelational obj)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException, SQLException, ModelException {
		List<ObjectRelational> list = new ArrayList<ObjectRelational>();
		String clazz = getLastPartClass(obj.getClass().getName());
		Map<String, String> link = links.get(clazz);
		for (String key : link.keySet()) {
			ObjectRelational foreignObj = get(clazz, obj, key);
			if (foreignObj != null)
				list.add(foreignObj);
		}
		return list;
	}

	public List<EncapsulatedObjectRelational<ObjectRelational>> getEncapsulated(
			EncapsulatedObjectRelational<ObjectRelational> eobj)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException, SQLException, ModelException {
		return EncapsulatedObjectRelational.encapsulate(get(eobj.get()));
	}

	public List<EncapsulatedObjectRelational<ObjectRelational>> getEncapsulated(
			ObjectRelational obj) throws IllegalArgumentException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException, SQLException, ModelException {
		return EncapsulatedObjectRelational.encapsulate(get(obj));
	}

	private ObjectRelational get(String clazz, ObjectRelational obj,
			String foreignKeyAttributeName) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NoSuchFieldException,
			ModelException {
		ObjectRelational result = null;
		Object foreignKeyValue = obj.getFieldValue(obj
				.getField(foreignKeyAttributeName));
		if (foreignKeyValue != null) {
			Model<ObjectRelational> model = models.get(getLastPartClass(links
					.get(clazz).get(foreignKeyAttributeName)));
			result = model.get(foreignKeyValue);
		}
		return result;
	}

}
