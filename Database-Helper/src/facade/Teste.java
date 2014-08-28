package facade;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;

import database.Connector;
import database.Database;

public class Teste extends ObjectRelational {

	private int id;
	public String name;
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Teste(){
		super();
	}
	
	public Teste(ResultSet resultSet) throws SQLException,
			IllegalArgumentException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
		super(resultSet);
	}
	
	public static void main(String[] args) {
		
		Connector.setSchema("tcc");
		Connection conn = Connector.getConnection();
		Database db = new Database(conn);
		
		ResultSet resultSet = db.get("user");
		ObjectRelacionalFactory<Teste> factory = new ObjectRelacionalFactory<Teste>(Teste.class);
		try {
			List<Teste> lista = factory.getList(resultSet);
			System.out.println(lista.get(0).name);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
