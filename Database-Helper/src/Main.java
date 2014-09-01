import java.util.List;

import database.Connector;
import database.Metadata;
import facade.ObjectRelational;
import facade.loader.ObjectRelationalBuilder;
import model.Model;

public class Main {

	public static void main (String[] args) {
		Connector.setSchema("teste_bd");
		ObjectRelationalBuilder builder = new ObjectRelationalBuilder();
		try {
			builder.setClassName("User");
			builder.setPrefix("USR_");
			builder.setCaseMod(ObjectRelational.SNAKEUPPERCASE_TO_CAMELCASE);
			builder.setAttribute("int", "id");
			builder.setAttribute("String", "name");
			Class<ObjectRelational> clazz = builder.generate();
			Model<ObjectRelational> model = new Model<ObjectRelational>("USERS", "id", clazz);
			System.out.println(model.get("USR_ID", "2"));
			
			Metadata metadata = new Metadata(Connector.getConnection(), Connector.getSchema());
			System.out.println(metadata.getTableNames());
			List<String> columns = metadata.getColumnNames("users");
			System.out.println(columns);
			System.out.println(metadata.getColumnClassNames("users"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
