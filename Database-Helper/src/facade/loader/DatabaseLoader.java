package facade.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import database.Connector;
import database.Metadata;
import facade.ObjectRelational;
import facade.loader.exceptions.CompilerNotFoundException;
import facade.loader.exceptions.ObjectRelationalBuilderException;

public class DatabaseLoader {

	private Metadata metadata;
	private ObjectRelationalBuilder objectRelationalBuilder;

	public DatabaseLoader() {
		metadata = new Metadata(Connector.getConnection(),
				Connector.getSchema());
	}

	public List<Class<ObjectRelational>> createVOs()
			throws ObjectRelationalBuilderException, ClassNotFoundException,
			IOException, CompilerNotFoundException {
		List<Class<ObjectRelational>> classes = new ArrayList<>();
		List<String> tables = metadata.getTableNames();
		for (String table : tables) {
			objectRelationalBuilder = new ObjectRelationalBuilder();
			objectRelationalBuilder.setClassName(table);
			List<String> columns = metadata.getColumnNames(table);
			List<String> types = metadata.getColumnClassNames(table);
			for (int i = 0; i < columns.size(); i++) {
				objectRelationalBuilder.setAttribute(
						getLastPartClass(types.get(i)), columns.get(i));
			}
			classes.add(objectRelationalBuilder.generate());
		}
		return classes;
	}

	private String getLastPartClass(String clazz) {
		String[] parts = clazz.split("\\.");
		if (parts.length > 0) {
			clazz = parts[parts.length - 1];
		}
		return clazz;
	}
}
