import database.Connector;
import facade.ObjectRelational;
import loader.ObjectRelationalBuilder;
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
}
