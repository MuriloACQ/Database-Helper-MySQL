import database.Connector;
import facade.loader.DatabaseLoader;

public class Main {

	public static void main (String[] args) {
		Connector.setSchema("teste_bd");
		DatabaseLoader dbLoader = new DatabaseLoader();
		try {
			System.out.println(dbLoader.createVOs());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
