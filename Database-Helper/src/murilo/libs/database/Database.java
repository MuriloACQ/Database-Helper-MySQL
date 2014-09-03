/**
 * Simple database helper
 * 
 * @author Murilo Augusto Castagnoli de Quadros
 * @since 2014
 * @email macquadros@gmail.com
 */

package murilo.libs.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

public class Database {

	private Connection connection;

	private String queryWhere;
	private String querySelect;
	private String querySet;
	private String queryGroupBy;
	private String queryOrderBy;
	private String queryLimit;

	private boolean where;
	private boolean select;
	private boolean set;
	private boolean groupBy;
	private boolean orderBy;
	private boolean limit;
	private boolean whereNot;

	public Database(Connection conn) {
		connection = conn;
		clear();
	}

	public Integer insert(Map<String, String> data, String table) {
		String query = "INSERT INTO " + table + " ";
		set(data);
		query += getQuery();
		Statement stm = null;
		Integer id = null;
		ResultSet resultSet = null;
		try {
			stm = (Statement) connection.createStatement();
			id = stm.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			resultSet = (ResultSet) stm.getGeneratedKeys();
			while (resultSet.next()) {
				id = resultSet.getInt(1);
			}
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		clear();
		return id;
	}

	public void update(Map<String, String> data, String table) {
		String query = "UPDATE " + table + " ";
		set(data);
		query += getQuery();
		Statement stm = null;
		try {
			stm = (Statement) connection.createStatement();
			stm.execute(query);
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		clear();
	}

	public ResultSet get(String table) {
		if (!select) {
			querySelect += "* ";
			select = true;
		}
		querySelect += " FROM " + table;
		Statement stm = null;
		ResultSet resultSet = null;
		try {
			stm = (Statement) connection.createStatement();
			resultSet = (ResultSet) stm.executeQuery(getQuery());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		clear();
		return resultSet;
	}

	public void delete(String table) {
		String query = "DELETE FROM " + table + " ";
		query += getQuery();
		Statement stm = null;
		try {
			stm = (Statement) connection.createStatement();
			stm.execute(query);
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		clear();
	}

	public ResultSet join(String tableA, String tableB, String columnA,
			String columnB, String type) {
		if (!select) {
			querySelect += "* ";
			select = true;
		}
		querySelect += " FROM " + tableA + " " + type + " JOIN " + tableB
				+ " ON " + tableA + "." + columnA + " = " + tableB + "."
				+ columnB + " ";
		Statement stm = null;
		ResultSet resultSet = null;
		try {
			stm = (Statement) connection.createStatement();
			resultSet = (ResultSet) stm.executeQuery(getQuery());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		clear();
		return resultSet;
	}

	public ResultSet query(String query) {
		Statement stm = null;
		ResultSet resultSet = null;
		try {
			stm = (Statement) connection.createStatement();
			resultSet = (ResultSet) stm.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}

	public ResultSet innerJoin(String tableA, String tableB, String columnA,
			String columnB) {
		return join(tableA, tableB, columnA, columnB, "INNER");
	}

	public ResultSet innerJoin(String tableA, String tableB, String column) {
		return innerJoin(tableA, tableB, column, column);
	}

	public ResultSet leftJoin(String tableA, String tableB, String columnA,
			String columnB) {
		return join(tableA, tableB, columnA, columnB, "LEFT");
	}

	public ResultSet leftJoin(String tableA, String tableB, String column) {
		return leftJoin(tableA, tableB, column, column);
	}

	public ResultSet rightJoin(String tableA, String tableB, String columnA,
			String columnB) {
		return join(tableA, tableB, columnA, columnB, "RIGHT");
	}

	public ResultSet rightJoin(String tableA, String tableB, String column) {
		return rightJoin(tableA, tableB, column, column);
	}

	public void select(String data) {
		List<String> list = new ArrayList<String>();
		list.add(data);
		select(list);
	}

	public void select(List<String> data) {
		for (String dt : data) {
			if (!select) {
				querySelect += dt;
				select = true;
			} else {
				querySelect += ", " + dt;
			}
		}
	}

	public void where(Map<String, String> data) {
		where(data, "AND", "=");
	}

	public void whereNot(Map<String, String> data) {
		whereNot = true;
		where(data, "AND", "!=");
		whereNot = false;
	}

	public void whereOr(Map<String, String> data) {
		where(data, "OR", "=");
	}

	public void like(Map<String, String> data) {
		where(data, "AND", "LIKE");
	}

	public void likeOr(Map<String, String> data) {
		where(data, "OR", "LIKE");
	}

	public void whereNot(String key, String value) {
		Map<String, String> data = new HashMap<String, String>();
		data.put(key, value);
		whereNot(data);
	}

	public void where(String key, String value) {
		Map<String, String> data = new HashMap<String, String>();
		data.put(key, value);
		where(data);
	}

	public void whereOr(String key, String value) {
		Map<String, String> data = new HashMap<String, String>();
		data.put(key, value);
		whereOr(data);
	}

	public void like(String key, String value) {
		Map<String, String> data = new HashMap<String, String>();
		data.put(key, value);
		like(data);
	}

	public void likeOr(String key, String value) {
		Map<String, String> data = new HashMap<String, String>();
		data.put(key, value);
		likeOr(data);
	}

	public void whereInitiateGroup() {
		queryWhere += " ( ";
	}

	public void whereFinishGroup() {
		queryWhere += " ) ";
	}

	public void groupBy(List<String> data) {
		group_by(data);
	}

	public void groupBy(String data) {
		List<String> list = new ArrayList<String>();
		list.add(data);
		groupBy(list);
	}

	public void orderBy(List<String> data) {
		orderBy(data, "ASC");
	}

	public void orderBy(String data) {
		List<String> list = new ArrayList<String>();
		list.add(data);
		orderBy(list);
	}

	public void orderByDesc(List<String> data) {
		orderBy(data, "DESC");
	}

	public void orderByDesc(String data) {
		List<String> list = new ArrayList<String>();
		list.add(data);
		orderByDesc(list);
	}

	public void limit(int offset, int quantity) {
		queryLimit = "LIMIT " + offset + ", " + quantity;
		limit = true;
	}

	public void limit(int quantity) {
		limit(0, quantity);
	}

	public String getQuery() {
		String query = "";
		if (set) {
			query += querySet + " ";
		}
		if (select) {
			query += querySelect + " ";
		}
		if (where) {
			query += queryWhere + " ";
		}
		if (groupBy) {
			query += queryGroupBy + " ";
		}
		if (orderBy) {
			query += queryOrderBy + " ";
		}
		if (limit) {
			query += queryLimit + " ";
		}
		return query.trim();
	}

	private void where(Map<String, String> data, String type, String operator) {
		Iterator<Map.Entry<String, String>> iterator = data.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> mapEntry = iterator.next();
			if (!where) {
				if (mapEntry.getValue() == null) {
					queryWhere += mapEntry.getKey().replaceAll("'", "\\\\'");
					if (whereNot) {
						queryWhere += " IS NOT NULL";
					} else {
						queryWhere += " IS NULL";
					}
				} else {
					queryWhere += mapEntry.getKey().replaceAll("'", "\\\\'")
							+ " " + operator + " '"
							+ mapEntry.getValue().replaceAll("'", "\\\\'")
							+ "'";
				}
				where = true;
			} else if (mapEntry.getValue() == null) {
				queryWhere += " " + type + " "
						+ mapEntry.getKey().replaceAll("'", "\\\\'");
				if (whereNot) {
					queryWhere += " IS NOT NULL";
				} else {
					queryWhere += " IS NULL";
				}
			} else {
				queryWhere += " " + type + " "
						+ mapEntry.getKey().replaceAll("'", "\\\\'") + " "
						+ operator + " '"
						+ mapEntry.getValue().replaceAll("'", "\\\\'") + "'";
			}

		}
	}

	private void set(Map<String, String> data) {
		Iterator<Map.Entry<String, String>> iterator = data.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> mapEntry = iterator.next();
			if (!set) {
				if (mapEntry.getValue() == null) {
					querySet += mapEntry.getKey().replaceAll("'", "\\\\'")
							+ " = NULL";
				} else {
					querySet += mapEntry.getKey().replaceAll("'", "\\\\'")
							+ " = '"
							+ mapEntry.getValue().replaceAll("'", "\\\\'")
							+ "'";
				}
				set = true;
			} else if (mapEntry.getValue() == null) {
				querySet += ", " + mapEntry.getKey().replaceAll("'", "\\\\'")
						+ " = NULL";
			} else {
				querySet += ", " + mapEntry.getKey().replaceAll("'", "\\\\'")
						+ " = '" + mapEntry.getValue().replaceAll("'", "\\\\'")
						+ "'";
			}

		}
	}

	private void orderBy(List<String> data, String type) {
		for (String dt : data) {
			if (!orderBy) {
				queryOrderBy += dt;
				orderBy = true;
			} else {
				queryOrderBy += ", " + dt;
			}
		}
		queryOrderBy += " " + type;
	}

	private void group_by(List<String> data) {
		for (String dt : data) {
			if (!groupBy) {
				queryGroupBy += dt;
				groupBy = true;
			} else {
				queryGroupBy += ", " + dt;
			}
		}
	}

	private void clear() {
		queryWhere = "WHERE ";
		querySelect = "SELECT ";
		querySet = "SET ";
		queryGroupBy = "GROUP BY ";
		queryOrderBy = "ORDER BY ";
		queryLimit = "LIMIT ";

		where = false;
		select = false;
		set = false;
		groupBy = false;
		orderBy = false;
		limit = false;
		whereNot = false;
	}

}
