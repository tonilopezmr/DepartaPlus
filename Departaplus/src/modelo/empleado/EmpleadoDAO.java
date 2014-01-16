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
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import modelo.Conexion;
import modelo.DataAccesObject;

/**
 * Clase abstracta que implementa la interfaz del patron de diseño DAO, y 
 * crea unos cuantos metodos abstractos que deberan ser implementados.
 * 
 * @author Antonio López Marín
 */
public abstract class EmpleadoDAO implements DataAccesObject<EmpleadoVo, Integer> {

    public Connection conexion;

    public EmpleadoDAO() throws ClassNotFoundException, SQLException {
        conexion = Conexion.getConexion();
    }
    
    public void conectar() throws ClassNotFoundException, SQLException{
        conexion = Conexion.getConexion();
    }
    
    @Override
    public int create(EmpleadoVo emp) throws SQLException {
        String sql = "INSERT INTO EMPLE (EMP_NO, APELLIDO, OFICIO, DIR, "
                + "FECHA_ALT, SALARIO, COMISION, DEPT_NO) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        //Parseo la fecha de java.util.Date, a la de SQL.
        Date sqlDate = new Date(emp.getFechaAlta().getTime());
        
        //Preparo una sentencia
        PreparedStatement pStm = conexion.prepareStatement(sql);
        //Introduzco los nuevos datos de del empleado
        pStm.setInt(1, emp.getCodigo());
        pStm.setString(2, emp.getNombre());
        pStm.setString(3, emp.getOficio());
        pStm.setInt(4, emp.getDirector());
        pStm.setDate(5, sqlDate);
        pStm.setDouble(6, emp.getSalario());
        pStm.setDouble(7, emp.getComision());
        pStm.setInt(8, emp.getCodigoDepart());

        return pStm.executeUpdate();
    }

    @Override
    public int update(EmpleadoVo emp) throws SQLException {
        String sql = "UPDATE EMPLE "
                + "SET APELLIDO = ?, "
                + "OFICIO = ?, "
                + "DIR = ?, "
                + "FECHA_ALT = ?, "
                + "SALARIO = ?, "
                + "COMISION = ?, "
                + "DEPT_NO = ? "
                + "WHERE EMP_NO = ?";
        Date sqlDate = new Date(emp.getFechaAlta().getTime());
        
        PreparedStatement pStm = conexion.prepareStatement(sql);
        //Introduzco todos los datos de empleado
        pStm.setString(1, emp.getNombre());
        pStm.setString(2, emp.getOficio());
        pStm.setInt(3, emp.getDirector());
        pStm.setDate(4, sqlDate);
        pStm.setDouble(5, emp.getSalario());
        pStm.setDouble(6, emp.getComision());
        pStm.setInt(7, emp.getCodigoDepart());
        
        //WHERE el codigo del empleado sea este
        pStm.setInt(8, emp.getCodigo());
        
        
        return pStm.executeUpdate();
    }

    @Override
    public int update(EmpleadoVo instance, Integer indentificador) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int delete(Integer itentificador) throws SQLException {
        String sql = "DELETE FROM EMPLE WHERE EMP_NO = ?";
        
        PreparedStatement pStm = conexion.prepareStatement(sql);
        pStm.setInt(1, itentificador);
        
        return pStm.executeUpdate();
    }

    @Override
    public int deleteAll() throws SQLException {
        Statement stm = conexion.createStatement();
        String sql = "DELETE FROM EMPLE";

        return stm.executeUpdate(sql);
    }

    @Override
    public EmpleadoVo findById(Integer itentificador) throws SQLException {
       String sql = "SELECT * FROM EMPLE WHERE EMP_NO = ?";
        PreparedStatement pStm = conexion.prepareStatement(sql);
        pStm.setInt(1, itentificador);

        ResultSet rst = pStm.executeQuery();

        if (rst.next()) {
            //Creo un empleado
            EmpleadoVo emp = new EmpleadoVo(rst.getInt("EMP_NO"), 
                    rst.getString("APELLIDO"), 
                    rst.getString("OFICIO"), 
                    rst.getInt("DIR"),
                    (Date) rst.getDate("FECHA_ALT"), 
                    rst.getDouble("SALARIO"),
                    rst.getDouble("COMISION"), 
                    rst.getInt("DEPT_NO"));
            return emp;
        } else {
            throw new SQLException("El empleado con el codigo "
                    + itentificador + " no existe.");
        }
    }

    @Override
    public List<EmpleadoVo> findAll() throws SQLException {
        String sql = "SELECT * FROM EMPLE";
        Statement stm = conexion.createStatement();
        ResultSet rst = stm.executeQuery(sql);

        ArrayList<EmpleadoVo> lista = new ArrayList<>();
        while (rst.next()) {
            //Creo un empleado
            EmpleadoVo emp = new EmpleadoVo(rst.getInt("EMP_NO"), 
                    rst.getString("APELLIDO"), 
                    rst.getString("OFICIO"), 
                    rst.getInt("DIR"),
                    (Date) rst.getDate("FECHA_ALT"), 
                    rst.getDouble("SALARIO"),
                    rst.getDouble("COMISION"), 
                    rst.getInt("DEPT_NO"));
            lista.add(emp);
        }

        rst.close();
        stm.close();
        if (!lista.isEmpty()) {
            return lista;
        } else {
            throw new SQLException("No hay empleados en la base de datos.");
        }
    }

    @Override
    public boolean exists(Integer identificador) throws SQLException {
        String sql = "{? = call existEmple(?)}";
        CallableStatement funcion = conexion.prepareCall(sql);
        funcion.registerOutParameter(1, Types.NUMERIC);
        funcion.setInt(2, identificador);

        funcion.executeUpdate();

        return funcion.getInt(1) > 0;
    }
    
    /*** Metodos Abstractos ***/
    
    public abstract int bajaEmpleado(int codigo) throws SQLException;
    
    public abstract boolean altaEmpleado(EmpleadoVo emple) throws SQLException;
    
    public abstract int modificarEmpleado(EmpleadoVo emple) throws SQLException;
    
    public abstract EmpleadoVo buscarEmpleado(int codigo) throws SQLException;
    
    public abstract String[] listarOficios() throws SQLException;
    
    public abstract Director[] listaDirectores() throws SQLException;
    
    public abstract EmpleadoVo[] listaEmpleadosDepart(int codDepart) throws SQLException;
    
    public abstract EmpleadoVo[] buscarEmpleados(String nombre) throws SQLException;
    
    public abstract EmpleadoVo[] buscarEmpleadosDepart(String nombre, int codDepart) 
            throws SQLException;
    
    public abstract int nuevoCodigo() throws SQLException;
}
