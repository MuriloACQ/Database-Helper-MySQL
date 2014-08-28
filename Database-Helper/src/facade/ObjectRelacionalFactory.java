package facade;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.ResultSet;

public class ObjectRelacionalFactory<T extends ObjectRelational> {
	
	private Class<T> t;
	private ObjectRelational objectRelational;
	
	public ObjectRelacionalFactory(Class<T> t) {
		this.t = t;
	}

	@SuppressWarnings("unchecked")
	public List<T> getList(ResultSet resultSet) throws SQLException, IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchMethodException, SecurityException, InvocationTargetException {
		List<T> list = new ArrayList<T>();
		while(resultSet.next()) {
			objectRelational = t.newInstance();
			objectRelational.initialize(resultSet);
			list.add((T) objectRelational);
		}
		return list;
	}
	
}
