package facade.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Model;
import database.Connector;
import database.Metadata;
import facade.ObjectRelational;
import facade.loader.exceptions.CompilerNotFoundException;
import facade.loader.exceptions.ObjectRelationalBuilderException;

import static utils.Utils.snakeToCamelCase;

public class DatabaseLoader {

	private Metadata metadata;
	private ObjectRelationalBuilder objectRelationalBuilder;
	private String pack;
	private Integer selecetedCase;

	private List<String> tables;
	private List<Class<ObjectRelational>> classes;

	public DatabaseLoader() {
		metadata = new Metadata(Connector.getConnection(),
				Connector.getSchema());
	}

	public void setPackage(String pack) {
		this.pack = pack;
	}

	public void setCaseMod(int mod) {
		selecetedCase = mod;
	}

	public List<Class<ObjectRelational>> createVOs()
			throws ObjectRelationalBuilderException, ClassNotFoundException,
			IOException, CompilerNotFoundException {
		classes = new ArrayList<>();
		tables = metadata.getTableNames();
		for (String table : tables) {
			objectRelationalBuilder = new ObjectRelationalBuilder();
			objectRelationalBuilder.setClassName(table);
			if (pack != null)
				objectRelationalBuilder.setPackage(pack);
			if (selecetedCase != null)
				objectRelationalBuilder.setCaseMod(selecetedCase);
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

	public Map<String, Model<ObjectRelational>> getModels() {
		Map<String, Model<ObjectRelational>> models = null;
		if (classes != null) {
			models = new HashMap<String, Model<ObjectRelational>>();
			for (int i = 0; i < classes.size(); i++) {
				models.put(
						getLastPartClass(classes.get(i).getName()),
						new Model<ObjectRelational>(tables.get(i),
								snakeToCamelCase(metadata.getPrimaryKey(tables
										.get(i))), classes.get(i)));
			}
		}
		return models;
	}

	private String getLastPartClass(String clazz) {
		String[] parts = clazz.split("\\.");
		if (parts.length > 0) {
			clazz = parts[parts.length - 1];
		}
		return clazz;
	}

}
