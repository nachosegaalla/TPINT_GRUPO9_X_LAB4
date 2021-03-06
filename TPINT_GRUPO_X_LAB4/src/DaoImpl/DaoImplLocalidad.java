package DaoImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.DaoLocalidad;
import entidad.Localidad;

public class DaoImplLocalidad implements DaoLocalidad {
	
	private static final String readall = "SELECT * from localidades";
	private static final String readall_Localidad_x_Provincia = "SELECT IdLocalidad,Nombre FROM localidades where IdProvincia = ?";
	
	
	
	
	public List<Localidad> readAll() {
		PreparedStatement statement;
		ResultSet resultSet;
		List<Localidad> LLocal = new ArrayList<Localidad>();
		Conexion conexion = Conexion.getConexion();
		try {
			statement = conexion.getSQLConexion().prepareStatement(readall);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				LLocal.add(getLocalidad(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conexion.getConexion().cerrarConexion();
		}
		return LLocal;
	}

	private Localidad getLocalidad(ResultSet resultSet) throws SQLException {
		Localidad NLocal = new Localidad();
		NLocal.setIdLocalidad(resultSet.getInt("IdLocalidad"));
		NLocal.setIdProvincia(resultSet.getInt("IdProvincia"));
		NLocal.setNombre(resultSet.getString("Nombre"));

		return NLocal;
	}

	public List<Localidad> Find(int x) {
		PreparedStatement statement;
		ResultSet resultSet;
		List<Localidad> LLocal = new ArrayList<Localidad>();
		Conexion conexion = Conexion.getConexion();
		try {
			statement = conexion.getSQLConexion().prepareStatement(readall_Localidad_x_Provincia);
			statement.setInt(1, x);
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				LLocal.add(getLocalidad_X_Provincia(resultSet));	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Conexion.getConexion().cerrarConexion();
		}
		return LLocal;
	}

	private Localidad getLocalidad_X_Provincia(ResultSet resultSet) throws SQLException {
		Localidad NLocal = new Localidad();
		NLocal.setIdLocalidad(resultSet.getInt("IdLocalidad"));
		NLocal.setNombre(resultSet.getString("Nombre"));

		return NLocal;
	}

}