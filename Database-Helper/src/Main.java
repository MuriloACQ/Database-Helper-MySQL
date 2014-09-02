import java.util.Map;

import model.Model;
import database.Connector;
import facade.ObjectRelational;
import facade.loader.DatabaseLoader;

public class Main {

	public static void main (String[] args) {
		Connector.setSchema("orlek_cabi");
		Connector.getConnection();
		System.out.println(Connector.getConnetionStatus());
		if(Connector.isConnected()){
			DatabaseLoader dbLoader = new DatabaseLoader();
			dbLoader.setPackage("vo");
			dbLoader.setCaseMod(ObjectRelational.SNAKELOWERCASE_TO_CAMELCASE);
			dbLoader.forceUpdate();
			try {
				dbLoader.createVOs();
				Map<String, Model<ObjectRelational>> models = dbLoader.getModels();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
