package murilo.libs.facade.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import murilo.libs.facade.loader.exceptions.CompilerNotFoundException;

public class ClassLoader {

	private String binaryPath;
	private String pack;
	private boolean forceUpdate;

	public ClassLoader() {
		forceUpdate = false;
	}

	public void setBinaryPath(String binaryPath) {
		this.binaryPath = binaryPath;
	}

	public void setPackage(String pack) {
		this.pack = pack;
	}

	public void forceUpdate() {
		forceUpdate = true;
	}

	public Class<?> newClass(String className, String declaration)
			throws IOException, CompilerNotFoundException,
			ClassNotFoundException {
		String filePath = "src";
		String classPath = "";
		if (pack != null) {
			classPath += pack + ".";
			filePath += "/" + pack.replaceAll("\\.", "/");
		}
		filePath += "/" + className + ".java";
		File file = new File(filePath);
		if (!file.exists() || forceUpdate) {
			file.createNewFile();
			FileOutputStream fos;
			fos = new FileOutputStream(file);
			fos.write(declaration.getBytes());
			fos.flush();
			fos.close();
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			if (compiler == null) {
				throw new CompilerNotFoundException(
						"Maybe you should use jdk to running application instead of jre");
			}
			StandardJavaFileManager standardJavaFileManager = compiler
					.getStandardFileManager(null, null, null);
			standardJavaFileManager.setLocation(StandardLocation.CLASS_OUTPUT,
					Arrays.asList(new File(binaryPath)));
			CompilationTask compilationTask = compiler.getTask(null,
					standardJavaFileManager, null, null, null,
					standardJavaFileManager.getJavaFileObjectsFromFiles(Arrays
							.asList(file)));
			compilationTask.call();
		}
		return Class.forName(classPath + className);
	}

}
