package facade.loader;

import static utils.Utils.firstLetterToUpperCase;
import static utils.Utils.snakeToCamelCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import facade.ObjectRelational;
import facade.loader.exceptions.CompilerNotFoundException;
import facade.loader.exceptions.ObjectRelationalBuilderException;

public class ObjectRelationalBuilder {

	private String className;
	private List<String> types;
	private List<String> names;
	private HashSet<String> imports;
	private String pack;
	private String binaryPath;
	private boolean forceUpdate;
	private Integer selectedCase;
	private String prefix;

	public ObjectRelationalBuilder() {
		types = new ArrayList<String>();
		names = new ArrayList<String>();
		imports = new HashSet<String>();
		forceUpdate = false;
		binaryPath = "bin";
	}
	
	public void forceUpdate() {
		forceUpdate = true;
	}

	public void setClassName(String className) throws ObjectRelationalBuilderException {
		if(!isValidAttributeOrClassName(className)){
			throw new ObjectRelationalBuilderException("Invalid class name");
		}
		this.className = firstLetterToUpperCase(snakeToCamelCase(className));
	}

	public void setAttribute(String type, String name) throws ObjectRelationalBuilderException {
		if (!isValidAttributeOrClassName(type)) {
			throw new ObjectRelationalBuilderException("Invalid type");
		}
		if (!isValidAttributeOrClassName(name)) {
			throw new ObjectRelationalBuilderException("Invalid attribute name");
		}
		type = convertPrimitiveToClass(type);
		importType(type);
		types.add(type);
		names.add(snakeToCamelCase(name));
	}
	
	public void setPackage(String pack) {
		this.pack = pack;
	}

	public void setBinayPath(String binayPath) {
		this.binaryPath = binayPath;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setCaseMod(int mod) {
		selectedCase = mod;
	}

	@SuppressWarnings("unchecked")
	public Class<ObjectRelational> generate() throws IOException, CompilerNotFoundException, ObjectRelationalBuilderException, ClassNotFoundException {
		if(className == null){
			throw new ObjectRelationalBuilderException("Cannot generate a subclass of ObjectRelational without class name");
		}
		if (types.size() == 0){
			throw new ObjectRelationalBuilderException("Cannot generate a subclass of ObjectRelational without attributes");
		}
		ClassLoader classLoader = new ClassLoader();
		if(forceUpdate) classLoader.forceUpdate();
		classLoader.setBinaryPath(binaryPath);
		classLoader.setPackage(pack);
		return (Class<ObjectRelational>) classLoader.newClass(className, assemble());
	}

	private String assemble() {
		String clazz = "";
		if(pack != null) clazz += "package "+ pack+";\n\n";
		clazz += "import facade.ObjectRelational;\n";
		for (String importEntry : imports) {
			clazz += "import "+ importEntry +";\n";
		}
		clazz += "\npublic class ";
		clazz += className + " extends ObjectRelational { \n\n";
		clazz += "\tpublic "+className+"() {\n";
		clazz += "\t\tsuper();\n";
		if(prefix != null) clazz += "\t\tsetPrefix("+'"'+prefix+'"'+");\n";
		if(selectedCase != null) clazz += "\t\tsetCaseMod("+selectedCase+");\n";
		clazz += "\t}\n\n";
		for (int i = 0; i < types.size(); i++) {
			clazz += "\tprivate " + types.get(i) + " " +names.get(i) + ";\n\n";
			clazz += "\tpublic " + types.get(i) + " get" +firstLetterToUpperCase(names.get(i)) + "() {\n";
			clazz += "\t\treturn "+names.get(i) + ";\n";
			clazz += "\t}\n\n";
			clazz += "\tpublic void set" +firstLetterToUpperCase(names.get(i)) + "("+types.get(i)+" "+names.get(i)+") {\n";
			clazz += "\t\tthis."+names.get(i) + " = " + names.get(i)+";\n";
			clazz += "\t}\n\n";
		}
		clazz += "}";
		return clazz;
	}

	private boolean isValidAttributeOrClassName(String attrName) {
		return attrName.matches("(^[a-zA-Z][a-zA-Z0-9_]*)|(^[_][a-zA-Z0-9_]+)");
	}

	private String convertPrimitiveToClass(String type) {
		if (type.equals("int")) {
			type = "Integer";
		} else if (type.equals("double")) {
			type = "Double";
		} else if (type.equals("boolean")) {
			type = "Boolean";
		} else if (type.equals("float")) {
			type = "Float";
		} else if (type.equals("char")) {
			type = "String";
		} else if (type.equals("short")) {
			type = "Short";
		} else if (type.equals("byte")) {
			type = "Byte";
		} else if (type.equals("long")) {
			type = "Long";
		}
		return type;
	}
	
	private void importType(String type) {
		if (type.equals("Timestamp")) {
			imports.add("java.sql.Timestamp");
		} else if (type.equals("Time")) {
			imports.add("java.sql.Time");
		} else if (type.equals("Date")) {
			imports.add("java.sql.Date");
		} else if (type.equals("BigInteger")) {
			imports.add("java.math.BigInteger");
		} else if (type.equals("BigDecimal")) {
			imports.add("java.math.BigDecimal");
		}
	}

}
