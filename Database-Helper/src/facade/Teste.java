package facade;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import model.Model;

import com.mysql.jdbc.ResultSet;

import database.Connector;

public class Teste extends ObjectRelational {

	public int id;
	public String name;
	public String username;
	public String password;
	
	public Teste(){
		setCaseMod(SNAKEUPPERCASE_TO_CAMELCASE);
		setPrefix("");
	}
	
	public Teste(ResultSet resultSet) throws SQLException,
			IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		super(resultSet);
	}
	
	public static void main(String[] args) {
		
		Connector.setSchema("tcc");
		Model<Teste> model = new Model<Teste>("user", "id", Teste.class);
		
		Teste teste = new Teste();
		teste.id = 2;
		teste.name = "Teste de update";
		teste.username = "aeeee";
		teste.password = "3232fsfsef";
		
		try {
			model.delete(teste);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.print(model.list());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
		
	}

}
