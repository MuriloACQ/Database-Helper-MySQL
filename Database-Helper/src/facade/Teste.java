package facade;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Timestamp;

import model.Model;

import com.mysql.jdbc.ResultSet;

import database.Connector;

public class Teste extends ObjectRelational {

	private Integer id;
	private String name;
	private String email;
	private Timestamp ini;
	
	public Teste(){
		super();
		setCaseMod(SNAKEUPPERCASE_TO_CAMELCASE);
		setPrefix("USR_");
	}
	
	public Teste(ResultSet resultSet) throws SQLException,
			IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		super(resultSet);
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Timestamp getIni() {
		return ini;
	}

	public void setIni(Timestamp ini) {
		this.ini = ini;
	}

	public static void main(String[] args) {
		
		Connector.setSchema("teste_bd");
		Model<Teste> model = new Model<Teste>("users", "id", Teste.class);
		
		Teste teste = new Teste();
		teste.name = "Teste de update";
		teste.email = "aeeee";
		
		try {
			teste = model.insertReturningUpdatedObject(teste);
			System.out.print(teste);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.print(teste);
			e.printStackTrace();
		}
		
//		try {
//			System.out.print(model.list());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
	}

}
