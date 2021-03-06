package DaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import dao.DaoAlumnos;
import entidad.Alumno;
import entidad.Instancia;

public class DaoImplAlumnos implements DaoAlumnos {

	private static final String insert = "INSERT INTO alumnos(Legajo, Dni, Nombre,Apellido,FechaNac,Direccion,Email,Telefono,IdLocalidad) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String edit = "UPDATE alumnos SET Legajo= ?,Dni= ?, Nombre= ?,Apellido= ?,FechaNac= ?,Direccion= ?,Email= ?,Telefono=?,IdLocalidad= ? WHERE IdAlumno = ?";
	private static final String logic_delete = "UPDATE alumnos SET Estado = 0 WHERE IdAlumno = ?";
	private static final String readall =  "select IdAlumno,Legajo,concat(Nombre,' ',Apellido) as Nombre,dni from Alumnos where estado = 1";
	private static final String find_Alumno = "SELECT IdAlumno,Legajo,Dni,a.Nombre as Nombre,Apellido,FechaNac,Direccion,Email,Telefono,l.IdLocalidad as IdLocalidad,l.IdProvincia as IdProvincia FROM alumnos as a\r\n" + 
			"INNER JOIN localidades as l on l.IdLocalidad = a.IdLocalidad\r\n" + 
			"WHERE IdAlumno = ?";
	
	private static final String readAll_Notas = "select alumnos.IdAlumno, alumnos.Legajo, alumnos.Nombre, alumnos.Apellido, alumnosxcurso.IdCurso,alumnos.Dni, alumnosxcurso.EstadoAlumno,"+
			"alumnosxcurso.Nota1,alumnosxcurso.Nota2,alumnosxcurso.Nota3,alumnosxcurso.Nota4 from alumnos "+
			"inner join alumnosxcurso on alumnosxcurso.IdAlumno = alumnos.IdAlumno "+
			"where IdCurso = ?;";
	
	private static final String Update_Notas = "UPDATE alumnosxcurso SET EstadoAlumno=?,Nota1=?,Nota2=?,Nota3=?,Nota4=? " + 
			"WHERE alumnosxcurso.IdCurso = ? and alumnosxcurso.IdAlumno = ?";
	
	
private static final String Alumnos_x_Materia = "select alumnos.Nombre,alumnos.Apellido,alumnos.dni,alumnos.Legajo,alumnosxcurso.IdCurso ,alumnos.IdAlumno, alumnos.Estado from alumnos " + 
			"inner join alumnosxcurso on alumnosxcurso.IdAlumno = alumnos.IdAlumno " + 
			"inner join cursos on alumnosxcurso.IdCurso = cursos.IdCurso " + 
			"where cursos.IdCurso=? and alumnos.Estado=1 and alumnosxcurso.Estado=1";
private static final String listar="select * from alumnos";
private static final String Traer_Uno="select * from alumnos where IdAlumno = ?";
private static final String Baja_logica_x_Curso_Materia="update alumnosxcurso set Estado = 0 where IdAlumno=? and IdCurso=?";
private static final String CheckId_Materia_Curso=" select count(*) from alumnosxcurso where IdCurso= ? and IdAlumno = ? and Estado =0";
private static final String Update_Materia_Curso="update alumnosxcurso set Estado =1 where IdAlumno = ? and IdCurso=?";
	
	
	
	
	public DaoImplAlumnos()
	{
	}
	public boolean Update_Estado_Materia_Curso(Alumno alumno) {


		PreparedStatement statement;
		Connection conexion = Conexion.getConexion().getSQLConexion();
		boolean isdeleteExitoso = false;
		try 
		{
			statement = conexion.prepareStatement(Update_Materia_Curso);
			statement.setInt(1, alumno.getIdAlumno());
			statement.setInt(2, alumno.getLInst().get(0).getIdCurso());
			if(statement.executeUpdate() > 0)
			{
				conexion.commit();
				isdeleteExitoso = true;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return isdeleteExitoso;
	}
	public boolean BajaLogica_Curso_x_Materia(Alumno alumno) {

		PreparedStatement statement;
		Connection conexion = Conexion.getConexion().getSQLConexion();
		boolean isdeleteExitoso = false;
		try 
		{
			statement = conexion.prepareStatement(Baja_logica_x_Curso_Materia);
			statement.setInt(1, alumno.getIdAlumno());
			statement.setInt(2, alumno.getLInst().get(0).getIdCurso());
			if(statement.executeUpdate() > 0)
			{
				conexion.commit();
				isdeleteExitoso = true;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return isdeleteExitoso;
	}

	@Override
	public boolean CheckId_Materia_Curso(Alumno a) {
		PreparedStatement statement;
		ResultSet resultSet;
		int x= -1;
		boolean Existe = false;
		Conexion conexion = Conexion.getConexion();
		try 
		{
		statement = conexion.getSQLConexion().prepareStatement(CheckId_Materia_Curso);
		statement.setInt(1, a.getLInst().get(0).getIdCurso());
		statement.setInt(2, a.getIdAlumno());
		resultSet = statement.executeQuery();
		while (resultSet.next()) {

			x= resultSet.getInt("count(*)");
		}
	} 
	catch (SQLException e) 
	{
		e.printStackTrace();

	}
		conexion.cerrarConexion();
		if(x>= 1)
		{	Existe=true;}
	return Existe;
	}
	public Alumno BuscarPorIdALumno(int idALumno) {

		PreparedStatement statement;
		ResultSet resultSet;

		Alumno a = new Alumno();
		Conexion conexion = Conexion.getConexion();
		try 
		{
		statement = conexion.getSQLConexion().prepareStatement(Traer_Uno);
		statement.setInt(1,  idALumno);
		resultSet = statement.executeQuery();
		if(resultSet.next())
		{

			a= getPersonaLista(resultSet);
		}
	} 
	catch (SQLException e) 
	{
		e.printStackTrace();

	}
		conexion.cerrarConexion();

	return a;
	}
	private Alumno getPersonaLista(ResultSet resultSet) throws SQLException
	{
		Alumno NAlum = new Alumno(); 
		NAlum.setIdAlumno(resultSet.getInt("IdAlumno"));
		NAlum.setLegajo(resultSet.getString("Legajo"));
		NAlum.setDni(resultSet.getString("Dni"));
		NAlum.setNombre(resultSet.getString("Nombre"));
		NAlum.setApellido(resultSet.getString("Apellido"));
		NAlum.setFechaNac(resultSet.getString("FechaNac"));
		NAlum.setDireccion(resultSet.getString("Direccion"));
		NAlum.setEmail(resultSet.getString("Email"));
		NAlum.setTelefono(resultSet.getString("Telefono"));
		NAlum.setEstado_Alumno(resultSet.getString("Estado"));

		return NAlum;
	}
	public ArrayList<Alumno> Listas_Alumnos_X_Materia(int IdCurso) {

		PreparedStatement statement;
		ResultSet resultSet;
		ArrayList<Alumno> personas = new ArrayList<Alumno>();
		Conexion conexion = Conexion.getConexion();
		try 
		{
			statement = conexion.getSQLConexion().prepareStatement(Alumnos_x_Materia);
			statement.setInt(1,IdCurso);

			resultSet = statement.executeQuery();

			while(resultSet.next())
			{
				personas.add(getPersona_x_Materias(resultSet));
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		conexion.cerrarConexion();

		return personas;
	}
	private Alumno getPersona_x_Materias(ResultSet rs) throws SQLException
	{
		Alumno NAlum = new Alumno();
		List<Instancia> LInst = new ArrayList<Instancia>();

		NAlum.setNombre(rs.getString("Nombre"));
		NAlum.setApellido(rs.getString("Apellido"));
		NAlum.setDni(rs.getString("Dni"));
		NAlum.setLegajo(rs.getString("Legajo"));
		NAlum.setIdAlumno(rs.getInt("IdAlumno"));
		NAlum.setEstado_Alumno(rs.getString("Estado"));

		NAlum.setLInst(LInst);

		return NAlum;


	}

	public List<Alumno> Listar()
	{
		PreparedStatement statement;
		ResultSet resultSet;
		ArrayList<Alumno> personas = new ArrayList<Alumno>();
		Conexion conexion = Conexion.getConexion();
		try 
		{
			statement = conexion.getSQLConexion().prepareStatement(listar);
			resultSet = statement.executeQuery();
			while(resultSet.next())
			{
				personas.add(getPersonaLista(resultSet));
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return personas;
	} 	
	
	@Override
	public boolean Update_Notas(Alumno NAlum) {
		PreparedStatement statement;
		Connection conexion = Conexion.getConexion().getSQLConexion();
		boolean isEditExistoso = false;
		try
		{
			statement = conexion.prepareStatement(Update_Notas);

			statement.setInt(1, Integer.parseInt( NAlum.getEstado_Alumno()));
			statement.setInt(2, NAlum.getLInst().get(0).getNota());
			statement.setInt(3, NAlum.getLInst().get(1).getNota());
			statement.setInt(4, NAlum.getLInst().get(2).getNota());
			statement.setInt(5, NAlum.getLInst().get(3).getNota());
			statement.setInt(6, NAlum.getLInst().get(0).getIdCurso());
			statement.setInt(7, NAlum.getIdAlumno());


			if(statement.executeUpdate() > 0)
			{
				conexion.commit();
				isEditExistoso = true;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			try {
				conexion.rollback();
				conexion.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return isEditExistoso;
	}

	@Override
	public ArrayList<Alumno> Listar_Notas(int x) {

		PreparedStatement statement;
		ResultSet resultSet;
		ArrayList<Alumno> personas = new ArrayList<Alumno>();
		Conexion conexion = Conexion.getConexion();
		try 
		{
			statement = conexion.getSQLConexion().prepareStatement(readAll_Notas);
			statement.setInt(1, x);
			resultSet = statement.executeQuery();
			while(resultSet.next())
			{
				personas.add(getPersona_Notas(resultSet));
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		conexion.cerrarConexion();
		return personas;
	}
	public boolean insert(Alumno NAlum)
	{
		PreparedStatement statement;
		Connection conexion = Conexion.getConexion().getSQLConexion();
		boolean isInsertExitoso = false;
		try
		{
			statement = conexion.prepareStatement(insert);
			statement.setString(1, NAlum.getLegajo());
			statement.setString(2, NAlum.getDni());
			statement.setString(3, NAlum.getNombre());
			statement.setString(4, NAlum.getApellido());
			statement.setString(5, NAlum.getFechNac());
			statement.setString(6, NAlum.getDireccion());
			statement.setString(7, NAlum.getEmail());
			statement.setString(8, NAlum.getTelefono());
			statement.setInt(9, NAlum.getLocalidad().getIdLocalidad());
			if(statement.executeUpdate() > 0)
			{
				conexion.commit();
				isInsertExitoso = true;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			try {
				conexion.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		finally
		{
		 Conexion.getConexion().cerrarConexion();	
		}
		
		return isInsertExitoso;
	}
	
	private Alumno getPersona_Notas(ResultSet rs) throws SQLException
	{
		Alumno NAlum = new Alumno();
		List<Instancia> LInst = new ArrayList<Instancia>();

		NAlum.setIdAlumno(Integer.parseInt(rs.getString("IdAlumno")));
		NAlum.setLegajo(rs.getString("Legajo"));
		NAlum.setNombre(rs.getString("Nombre"));
		NAlum.setApellido(rs.getString("Apellido"));
		NAlum.setDni(rs.getString("Dni"));
		NAlum.setEstado_Alumno(rs.getString("EstadoAlumno"));
		for(int i=1;i<5;i++)
		{		
			LInst.add(get_Nota(rs,"Nota"+String.valueOf(i), i));
		}

		NAlum.setLInst(LInst);

		return NAlum;

	}
	
	private Instancia get_Nota(ResultSet rs, String Nota, int NumNota) throws SQLException
	{
		Instancia Ins = new Instancia();
		Ins.setIdIntancia(NumNota);
		Ins.setIdCurso(rs.getInt("IdCurso"));
		Ins.setNota(rs.getInt(Nota));

		return Ins;
	}
	
	
	public boolean edit (Alumno NAlum)
	{
		PreparedStatement statement;
		Connection conexion = Conexion.getConexion().getSQLConexion();
		boolean isEditExistoso = false;
		try
		{
			statement = conexion.prepareStatement(edit);
			
			statement.setString(1, NAlum.getLegajo());
			statement.setString(2, NAlum.getDni());
			statement.setString(3, NAlum.getNombre());
			statement.setString(4, NAlum.getApellido());
			statement.setString(5, NAlum.getFechNac());
			statement.setString(6, NAlum.getDireccion());
			statement.setString(7, NAlum.getEmail());
			statement.setString(8, NAlum.getTelefono());
			statement.setInt(9, NAlum.getLocalidad().getIdLocalidad());
			statement.setInt(10, NAlum.getIdAlumno());
			
			if(statement.executeUpdate() > 0)
			{
				conexion.commit();
				isEditExistoso = true;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			try {
				conexion.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		finally
		{
		 Conexion.getConexion().cerrarConexion();	
		}
		return isEditExistoso;
	}
	public boolean logic_delete(int x) {
		
		PreparedStatement statement;
		Connection conexion = Conexion.getConexion().getSQLConexion();
		boolean isdeleteExitoso = false;
		try 
		{
			statement = conexion.prepareStatement(logic_delete);
			statement.setInt(1, x);
			if(statement.executeUpdate() > 0)
			{
				conexion.commit();
				isdeleteExitoso = true;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
		 Conexion.getConexion().cerrarConexion();	
		}
		return isdeleteExitoso;
	}
	
	public Alumno find(int x)
	{
		PreparedStatement statement;
		ResultSet resultSet;
		Connection conexion = Conexion.getConexion().getSQLConexion();
		
		try 
		{
			statement = conexion.prepareStatement(find_Alumno);
			statement.setInt(1, x);
			resultSet = statement.executeQuery();
			if(resultSet.next())
			{
				
				if(resultSet.getInt("IdAlumno") == x)
				{
					Alumno NAlum = new Alumno();
					
					NAlum.setIdAlumno(resultSet.getInt("IdAlumno"));
					NAlum.setLegajo(resultSet.getString("Legajo"));
					NAlum.setDni(resultSet.getString("Dni"));
					NAlum.setNombre(resultSet.getString("Nombre"));
					NAlum.setApellido(resultSet.getString("Apellido"));
					NAlum.setFechaNac(resultSet.getString("FechaNac"));
					NAlum.setDireccion(resultSet.getString("Direccion"));
					NAlum.setEmail(resultSet.getString("Email"));
					NAlum.setTelefono(resultSet.getString("Telefono"));
					NAlum.getLocalidad().setIdLocalidad(resultSet.getInt("IdLocalidad"));
					NAlum.getProvincia().setIdProvincia(resultSet.getInt("IdProvincia"));
					
					
					return NAlum;
					
				}
			}
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
		 Conexion.getConexion().cerrarConexion();	
		}
		return null;
		
	}
	public List<Alumno> readAll()
	{
		PreparedStatement statement;
		ResultSet resultSet;
		List<Alumno> personas = new ArrayList<Alumno>();
		Conexion conexion = Conexion.getConexion();
		try 
		{
			statement = conexion.getSQLConexion().prepareStatement(readall);
			resultSet = statement.executeQuery();
			while(resultSet.next())
			{
				personas.add(getPersona(resultSet));
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
		 Conexion.getConexion().cerrarConexion();	
		}
		return personas;
	}
	
	private Alumno getPersona(ResultSet resultSet) throws SQLException
	{
		Alumno NAlum = new Alumno();
		NAlum.setIdAlumno(resultSet.getInt("IdAlumno"));
		NAlum.setLegajo(resultSet.getString("Legajo"));
		NAlum.setNombre(resultSet.getString("Nombre"));
		NAlum.setDni(resultSet.getString("Dni"));
		
		
		return NAlum;
	}

	


	
	
}