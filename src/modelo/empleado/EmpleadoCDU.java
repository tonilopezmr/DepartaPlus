/*
 * Copyright (C) 2013 Antonio López Marín
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package modelo.empleado;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Clase empleado Caso de Uso que trabaja con los casos de uso 
 * de la base de datos.
 * 
 * @author Antonio López Marín
 */
public class EmpleadoCDU extends EmpleadoDAO {

    public EmpleadoCDU() throws ClassNotFoundException, SQLException {
        super();
    }
    
    /**
     * 
     * @param codigo
     * @return
     * @throws SQLException 
     */
    @Override
    public int bajaEmpleado(int codigo) throws SQLException {
        if (exists(codigo)) {
            return delete(codigo);
        } else {
            throw new SQLException("El empleado a dar de baja no existe.");
        }
    }
    
    /**
     * 
     * @param emple
     * @return
     * @throws SQLException 
     */
    @Override
    public boolean altaEmpleado(EmpleadoVo emple) throws SQLException {
        int codigo = emple.getCodigo();
        if (!exists(codigo)) {
            return create(emple) > 0;
        } else {
            throw new SQLException("El empleado con codigo " + codigo
                    + " ya existe.");
        }
    }
    
    /**
     * 
     * @param emple
     * @return
     * @throws SQLException 
     */
    @Override
    public int modificarEmpleado(EmpleadoVo emple) throws SQLException {
        return update(emple);
    }

    /**
     * 
     * @param codigo
     * @return
     * @throws SQLException 
     */
    @Override
    public EmpleadoVo buscarEmpleado(int codigo) throws SQLException {
        return findById(codigo);
    }

    /**
     * Lista de oficios.
     *
     * @return
     * @throws SQLException
     */
    @Override
    public String[] listarOficios() throws SQLException {
        String sql = "SELECT DISTINCT OFICIO "
                + "FROM EMPLE";

        //Creo el statement y ejecuto la consulta
        Statement stm = conexion.createStatement();
        ResultSet rst = stm.executeQuery(sql);

        //Creo un arraylist para meter los resultados
        ArrayList<String> lista = new ArrayList();

        //Mientras haya resultados
        if (rst.next()) {
            do {
                lista.add(rst.getString(1));
            } while (rst.next());
        } else {
            throw new SQLException("No hay oficios.");
        }


        rst.close();
        stm.close();

        //Devuelvo una array de cadenas con los oficios
        String[] array = new String[lista.size()];
        return lista.toArray(array);
    }

    /**
     * Lista de directores.
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Director[] listaDirectores() throws SQLException {
        String sql = "SELECT EMP_NO, APELLIDO "
                + "FROM EMPLE "
                + "WHERE OFICIO='DIRECTOR'";

        Statement stm = conexion.createStatement();
        ResultSet rst = stm.executeQuery(sql);

        //Creo un arraylist para meter los resultados
        ArrayList<Director> lista = new ArrayList();

        if (rst.next()) {
            do {
                lista.add(new Director(rst.getInt(1), rst.getString(2)));
            } while (rst.next());
        } else {
            throw new SQLException("No hay directores.");
        }

        rst.close();
        stm.close();

        //Devuelvo una array de los directores
        Director[] array = new Director[lista.size()];
        return lista.toArray(array);
    }

    /**
     * Lista de empleados segun su departamento, que contiene solo el codigo y
     * nombre de empleado.
     *
     * @param codDepart
     * @return
     * @throws SQLException
     */
    @Override
    public EmpleadoVo[] listaEmpleadosDepart(int codDepart) throws SQLException {

        //Lista de todos los empleados
        List<EmpleadoVo> lista = findAll();
        Iterator<EmpleadoVo> it = lista.iterator();

        //Arraylist donde estaran los empleados de ese departamento
        ArrayList<EmpleadoVo> listaEmp = new ArrayList<>();

        //Recorro todos los empleados
        while (it.hasNext()) {
            EmpleadoVo emp = it.next();

            if (emp.getCodigoDepart() == codDepart) {
                listaEmp.add(emp);
            }
        }

        if (listaEmp.isEmpty()) {
            throw new SQLException("No hay empleados con ese departamento.");
        }
        
        //Devuelvo una array de los directores
        EmpleadoVo[] array = new EmpleadoVo[listaEmp.size()];
        return listaEmp.toArray(array);
    }

    /**
     * Busca un empleado, que empiece por las siguientes letras que se le pasan
     * por parametros.
     * 
     * @param nombre
     * @return
     * @throws SQLException 
     */
    @Override
    public EmpleadoVo[] buscarEmpleados(String nombre) throws SQLException {
        String sql = "SELECT * "
                + "FROM EMPLE "
                + "WHERE UPPER(APELLIDO) LIKE '" + nombre.toUpperCase() + "%' "
                + "ORDER BY APELLIDO";

        Statement stm = conexion.createStatement();
        ResultSet rst = stm.executeQuery(sql);

        //Creo un arraylist para meter los resultados
        ArrayList<EmpleadoVo> lista = new ArrayList();


        while (rst.next()) {
            lista.add(new EmpleadoVo(rst.getInt("EMP_NO"),
                    rst.getString("APELLIDO"),
                    rst.getString("OFICIO"),
                    rst.getInt("DIR"),
                    (Date) rst.getDate("FECHA_ALT"),
                    rst.getDouble("SALARIO"),
                    rst.getDouble("COMISION"),
                    rst.getInt("DEPT_NO")));
        }

        rst.close();
        stm.close();

        //Devuelvo una array de los directores
        EmpleadoVo[] array = new EmpleadoVo[lista.size()];
        return lista.toArray(array);
    }

    /**
     * BUSCA LOS EMPLEADOS QUE EMPIECEN CON ESE NOMBRE Y DEL DEPARTAMENTO QUE SE
     * LE PASA POR PARAMETROS.
     *
     * @param nombre
     * @param codDepart
     * @return
     * @throws SQLException
     */
    @Override
    public EmpleadoVo[] buscarEmpleadosDepart(String nombre, int codDepart)
            throws SQLException {
        String sql = "SELECT * "
                + "FROM EMPLE "
                + "WHERE UPPER(APELLIDO) LIKE '" + nombre.toUpperCase() + "%' "
                + "AND DEPT_NO = " + codDepart
                + " ORDER BY APELLIDO";

        Statement stm = conexion.createStatement();
        ResultSet rst = stm.executeQuery(sql);

        //Creo un arraylist para meter los resultados
        ArrayList<EmpleadoVo> lista = new ArrayList();


        while (rst.next()) {
            lista.add(new EmpleadoVo(rst.getInt("EMP_NO"),
                    rst.getString("APELLIDO"),
                    rst.getString("OFICIO"),
                    rst.getInt("DIR"),
                    (Date) rst.getDate("FECHA_ALT"),
                    rst.getDouble("SALARIO"),
                    rst.getDouble("COMISION"),
                    rst.getInt("DEPT_NO")));
        }

        rst.close();
        stm.close();

        //Devuelvo una array de los directores
        EmpleadoVo[] array = new EmpleadoVo[lista.size()];
        return lista.toArray(array);
    }

    /**
     * Funcion que devuelve un nuevo codigo de empleado.
     *
     * @return
     * @throws SQLException
     */
    @Override
    public int nuevoCodigo() throws SQLException {
        String sql = "{? = call nuevoCodigo}";

        CallableStatement funcion = conexion.prepareCall(sql);
        funcion.registerOutParameter(1, Types.NUMERIC);
        funcion.executeUpdate();

        return funcion.getInt(1);
    }

    /**
     * Devuelve la clase abstracta de esta clase para operar con ella.
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static EmpleadoDAO getDAO() throws ClassNotFoundException, SQLException {
        return new EmpleadoCDU();
    }
}