package murilo.libs.facade.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import murilo.libs.database.Connector;
import murilo.libs.database.Metadata;
import murilo.libs.facade.ObjectRelational;
import murilo.libs.facade.loader.exceptions.CompilerNotFoundException;
import murilo.libs.facade.loader.exceptions.ObjectRelationalBuilderException;
import murilo.libs.model.Model;
import static murilo.libs.utils.Utils.firstLetterToUpperCase;
import static murilo.libs.utils.Utils.snakeToCamelCase;

public class DatabaseLoader {

	private Metadata metadata;
	private ObjectRelationalBuilder objectRelationalBuilder;
	private String pack;
	private Integer selecetedCase;
	private boolean forceUpdate;
	private String binaryPath;

	private List<String> tables;
	private List<Class<ObjectRelational>> classes;

	public DatabaseLoader() {
		metadata = new Metadata(Connector.getConnection(),
				Connector.getSchema());
		forceUpdate = false;
	}

	public void setBinaryPath(String binaryPath) {
		this.binaryPath = binaryPath;
	}

	public void setPackage(String pack) {
		this.pack = pack;
	}

	public void setCaseMod(int mod) {
		selecetedCase = mod;
	}

	public void forceUpdate() {
		forceUpdate = true;
	}

	public List<Class<ObjectRelational>> createVOs()
			throws ObjectRelationalBuilderException, ClassNotFoundException,
			IOException, CompilerNotFoundException {
		classes = new ArrayList<Class<ObjectRelational>>();
		tables = metadata.getTableNames();
		for (String table : tables) {
			classes.add(buildObjectRelational(table));
		}
		return classes;
	}

	@SuppressWarnings("unchecked")
	public List<Class<ObjectRelational>> getClassesList()
			throws ClassNotFoundException {
		classes = new ArrayList<Class<ObjectRelational>>();
		tables = metadata.getTableNames();
		for (String table : tables) {
			String className = firstLetterToUpperCase(snakeToCamelCase(table));
			if (pack != null)
				className = pack + "." + className;
			classes.add((Class<ObjectRelational>) Class.forName(className));
		}
		return classes;
	}

	@SuppressWarnings("unchecked")
	public List<Class<ObjectRelational>> createVOsIfNotExistAndGetClasses()
			throws ObjectRelationalBuilderException, ClassNotFoundException,
			IOException, CompilerNotFoundException {
		classes = new ArrayList<Class<ObjectRelational>>();
		tables = metadata.getTableNames();
		for (String table : tables) { 
			String className = firstLetterToUpperCase(snakeToCamelCase(table));
			if (pack != null)
				className = pack + "." + className;
			Class<ObjectRelational> clazz;
			try {
				clazz = (Class<ObjectRelational>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				clazz = buildObjectRelational(table);
			}
			classes.add(clazz);
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
	
	private Class<ObjectRelational> buildObjectRelational(String table) throws ObjectRelationalBuilderException, ClassNotFoundException, IOException, CompilerNotFoundException{
		objectRelationalBuilder = new ObjectRelationalBuilder();
		objectRelationalBuilder.setClassName(table);
		if (forceUpdate)
			objectRelationalBuilder.forceUpdate();
		if (pack != null)
			objectRelationalBuilder.setPackage(pack);
		if (selecetedCase != null)
			objectRelationalBuilder.setCaseMod(selecetedCase);
		if (binaryPath != null)
			objectRelationalBuilder.setBinaryPath(binaryPath);
		List<String> columns = metadata.getColumnNames(table);
		List<String> types = metadata.getColumnClassNames(table);
		for (int i = 0; i < columns.size(); i++) {
			objectRelationalBuilder.setAttribute(
					getLastPartClass(types.get(i)), columns.get(i));
		}
		return objectRelationalBuilder.generate();
	}

	private String getLastPartClass(String clazz) {
		String[] parts = clazz.split("\\.");
		if (parts.length > 0) {
			clazz = parts[parts.length - 1];
		}
		return clazz;
	}

}
