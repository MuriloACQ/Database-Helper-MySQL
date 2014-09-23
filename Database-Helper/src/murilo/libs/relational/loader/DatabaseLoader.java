package murilo.libs.relational.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import murilo.libs.database.Connector;
import murilo.libs.database.Metadata;
import murilo.libs.model.Model;
import murilo.libs.model.ModelLinker;
import murilo.libs.model.exception.ModelException;
import murilo.libs.relational.ObjectRelational;
import murilo.libs.relational.loader.exception.ObjectRelationalBuilderException;
import static murilo.libs.utils.Utils.firstLetterToUpperCase;
import static murilo.libs.utils.Utils.snakeToCamelCase;
import static murilo.libs.utils.Utils.getLastPartClass;

public class DatabaseLoader {

	private Metadata metadata;
	private ObjectRelationalBuilder objectRelationalBuilder;
	private String pack;
	private Integer selecetedCase;
	private boolean forceUpdate;
	private String binaryPath;

	private List<String> tables;
	private List<Class<ObjectRelational>> classes;
	private Map<String, Model<ObjectRelational>> models;

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
			IOException, ModelException {
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
			classes.add((Class<ObjectRelational>) Class
					.forName(getClassName(table)));
		}
		return classes;
	}

	@SuppressWarnings("unchecked")
	public List<Class<ObjectRelational>> createVOsIfNotExistAndGetClasses()
			throws ObjectRelationalBuilderException, ClassNotFoundException,
			IOException, ModelException {
		classes = new ArrayList<Class<ObjectRelational>>();
		tables = metadata.getTableNames();
		for (String table : tables) {
			try {
				classes.add((Class<ObjectRelational>) Class
						.forName(getClassName(table)));
			} catch (ClassNotFoundException e) {
				classes.add(buildObjectRelational(table));
			}
		}
		return classes;
	}

	public Map<String, Model<ObjectRelational>> getModels()
			throws ClassNotFoundException, ObjectRelationalBuilderException,
			IOException, ModelException {
		if (classes == null)
			createVOsIfNotExistAndGetClasses();
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

	public ModelLinker getLinker() throws ClassNotFoundException,
			ObjectRelationalBuilderException, IOException,
			ModelException {
		ModelLinker linker = null;
		if (models == null)
			getModels();
		if (models != null) {
			linker = new ModelLinker(models);
			for (String table : tables) {
				Map<String, String> foreignKeys = metadata
						.getForeignKeys(table);
				Iterator<Map.Entry<String, String>> iterator = foreignKeys
						.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, String> mapEntry = iterator.next();
					linker.setLink(getClassName(mapEntry.getKey()),
							snakeToCamelCase(mapEntry.getValue()),
							getClassName(table));
				}
			}
		}
		return linker;
	}
	
	public List<Class<ObjectRelational>> getLoadedClasses() {
		return classes;
	}

	private Class<ObjectRelational> buildObjectRelational(String table)
			throws ObjectRelationalBuilderException, ClassNotFoundException,
			IOException, ModelException {
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

	private String getClassName(String table) {
		String className = firstLetterToUpperCase(snakeToCamelCase(table));
		if (pack != null)
			className = pack + "." + className;
		return className;
	}

}
