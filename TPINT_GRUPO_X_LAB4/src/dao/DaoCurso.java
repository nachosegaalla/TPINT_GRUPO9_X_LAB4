package dao;

import java.util.List;

import entidad.Alumno;
import entidad.Curso;

public interface DaoCurso {

	public List<Curso> readAll_Cursos();
	public List<Alumno> readAll_AlumnosxCurso();
	public List<Curso> readAll_Cursos_Nombres();
	public boolean insert(Curso NCurs);
	public boolean edit (Curso NCurs);
	public boolean logic_delete(int x);
	public boolean delete(Curso NCurso);
	public boolean find(Curso NCurso);
	
}
