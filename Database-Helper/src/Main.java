import java.util.Map;

import model.Model;
import database.Connector;
import facade.ObjectRelational;
import facade.loader.DatabaseLoader;

public class Main {

	public static void main (String[] args) {
		Connector.setSchema("orlek_cabi");
		DatabaseLoader dbLoader = new DatabaseLoader();
		dbLoader.setPackage("vo");
		dbLoader.setCaseMod(ObjectRelational.SNAKELOWERCASE_TO_CAMELCASE);
		try {
			dbLoader.createVOs();
			Map<String, Model<ObjectRelational>> models = dbLoader.getModels();
			models.get("Produtos").list().get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
