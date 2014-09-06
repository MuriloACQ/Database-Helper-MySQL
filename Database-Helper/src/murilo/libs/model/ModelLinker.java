package murilo.libs.model;

import static murilo.libs.utils.Utils.getLastPartClass;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import murilo.libs.facade.ObjectRelational;

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
			InstantiationException, SQLException {
		String clazz = getLastPartClass(obj.getClass().getName());
		return get(clazz, obj, foreignKeyAttributeName);
	}

	public List<ObjectRelational> get(ObjectRelational obj)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchMethodException, SecurityException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException, SQLException {
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

	private ObjectRelational get(String clazz, ObjectRelational obj,
			String foreignKeyAttributeName) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NoSuchFieldException,
			InstantiationException, SQLException {
		ObjectRelational result = null;
		Object foreignKeyValue = obj.getFieldValue(obj
				.getField(foreignKeyAttributeName));
		if (foreignKeyValue != null) {
			Model<ObjectRelational> model = models.get(links.get(clazz).get(
					foreignKeyAttributeName));
			result = model.get(foreignKeyValue);
		}
		return result;
	}

}
