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
package modelo.departamento;

import java.sql.CallableStatement;
import java.sql.Connection;
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
 * Clase abstracta de departamento segun el patron de diseño DAO.
 *
 * Implementa la interfaz DataAccesObject, para DepartamentoVo y su
 * identificador es un Integer(int).
 *
 * @author Antonio López Marín
 */
public abstract class DepartamentoDAO
        implements DataAccesObject<DepartamentoVo, Integer> {

    //Conexion de la base de datos
    public Connection conexion;

    public DepartamentoDAO() throws ClassNotFoundException, SQLException {
        conexion = Conexion.getConexion();
    }
    
    /**
     * Para poder volver a conectar.
     * 
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public void conectar() throws ClassNotFoundException, SQLException{
        conexion = Conexion.getConexion();
    }
    
    @Override
    public int create(DepartamentoVo instance) throws SQLException {
        String sql = "INSERT INTO DEPART (DEPT_NO, DNOMBRE, LOC)"
                + " VALUES (?, ?, ?)";
        PreparedStatement pStm = conexion.prepareStatement(sql);
        pStm.setInt(1, instance.getCodigo());
        pStm.setString(2, instance.getNombre());
        pStm.setString(3, instance.getLocalidad());

        return pStm.executeUpdate();
    }

    /**
     * Cambiar de nombre de departamento, localidad o ambas.
     *
     * @param instance
     * @return
     * @throws SQLException
     */
    @Override
    public int update(DepartamentoVo instance) throws SQLException {
        String sql = "UPDATE DEPART"
                + " SET DNOMBRE = ?, LOC = ?"
                + " WHERE DEPT_NO = ?";
        PreparedStatement pStm = conexion.prepareStatement(sql);
        pStm.setString(1, instance.getNombre());
        pStm.setString(2, instance.getLocalidad());
        pStm.setInt(3, instance.getCodigo());

        return pStm.executeUpdate();
    }

    /**
     * Cambiar completamente un departamento, hasta su identificador.
     *
     * @param instance
     * @param indentificador Integer Nuevo identificador de el departamento.
     * @return Integer numero de filas actualizadas
     * @throws SQLException
     */
    @Override
    public int update(DepartamentoVo instance, Integer indentificador)
            throws SQLException {
        String sql = "UPDATE DEPART"
                + " SET DEPT_NO = ?,"
                + " DNOMBRE = ?, LOC = ?"
                + " WHERE DEPT_NO = ?";
        PreparedStatement pStm = conexion.prepareStatement(sql);
        pStm.setInt(1, indentificador);
        pStm.setString(2, instance.getNombre());
        pStm.setString(3, instance.getLocalidad());
        pStm.setInt(4, instance.getCodigo());

        return pStm.executeUpdate();
    }

    @Override
    public int delete(Integer itentificador) throws SQLException {
        String sql = "DELETE FROM DEPART WHERE DEPT_NO = ?";
        PreparedStatement pStm = conexion.prepareStatement(sql);
        pStm.setInt(1, itentificador);

        return pStm.executeUpdate();
    }

    @Override
    public int deleteAll() throws SQLException {
        Statement stm = conexion.createStatement();
        String sql = "DELETE FROM DEPART";

        return stm.executeUpdate(sql);
    }

    @Override
    public List<DepartamentoVo> findAll() throws SQLException {
        String sql = "SELECT * FROM DEPART";
        Statement stm = conexion.createStatement();
        ResultSet rst = stm.executeQuery(sql);

        ArrayList<DepartamentoVo> lista = new ArrayList<>();
        while (rst.next()) {
            DepartamentoVo dep = new DepartamentoVo(rst.getInt("DEPT_NO"),
                    rst.getString("DNOMBRE"), rst.getString("LOC"));
            lista.add(dep);
        }

        rst.close();
        stm.close();
        if (!lista.isEmpty()) {
            return lista;
        } else {
            throw new SQLException("No hay departamentos en la base de datos.");
        }
    }

    @Override
    public DepartamentoVo findById(Integer identificador) throws SQLException {
        String sql = "SELECT * FROM DEPART WHERE DEPT_NO = ?";
        PreparedStatement pStm = conexion.prepareStatement(sql);
        pStm.setInt(1, identificador);

        ResultSet rst = pStm.executeQuery();

        if (rst.next()) {
            DepartamentoVo dep = new DepartamentoVo(rst.getInt("DEPT_NO"),
                    rst.getString("DNOMBRE"), rst.getString("LOC"));
            return dep;
        } else {
            throw new SQLException("El departamento con el codigo "
                    + identificador + " no existe.");
        }

    }

    @Override
    public boolean exists(Integer identificador) throws SQLException {
        String sql = "{? = call existDepart(?)}";
        CallableStatement funcion = conexion.prepareCall(sql);
        funcion.registerOutParameter(1, Types.NUMERIC);
        funcion.setInt(2, identificador);

        funcion.executeUpdate();

        boolean exits = funcion.getInt(1) > 0;

        funcion.close();
        return exits;
    }

    /**
     * * Metodos Abstractos **
     */
    public abstract int bajaDepartamento(int codigo) throws SQLException;

    public abstract int bajaDepartamento(int oldCodigo, int newCodigo) throws SQLException;

    public abstract boolean altaDepartamento(DepartamentoVo depart) throws SQLException;

    public abstract int modificarDepartamento(DepartamentoVo depart) throws SQLException;

    public abstract DepartamentoVo buscarDepartamento(int codigo) throws SQLException;

    public abstract DepartamentoVo buscarDepartNombre(String nombre) throws SQLException;

    public abstract String[] nombreDepartamentos() throws SQLException;

    public abstract boolean hasEmpleados(int codigo) throws SQLException;
}