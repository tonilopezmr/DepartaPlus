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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Departamento Caso de Uso, es el que maneja y controla las transacciones.
 *
 * @author Antonio López Marín
 */
public class DepartamentoCDU extends DepartamentoDAO {

    /**
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public DepartamentoCDU() throws ClassNotFoundException, SQLException {
        super();
    }

    /**
     *
     * @param codigo
     * @return
     * @throws SQLException
     */
    @Override
    public int bajaDepartamento(int codigo) throws SQLException {
        if (exists(codigo)) {
            return delete(codigo);
        } else {
            throw new SQLException("El departamento a dar de baja no existe.");
        }
    }

    /**
     *
     * @param depart
     * @return
     * @throws SQLException
     */
    @Override
    public boolean altaDepartamento(DepartamentoVo depart) throws SQLException {
        int codigo = depart.getCodigo();
        if (!exists(codigo)) {
            return create(depart) > 0;
        } else {
            throw new SQLException("El departamento con codigo " + codigo
                    + " ya existe.");
        }
    }

    /**
     *
     * @param codigo
     * @return
     * @throws SQLException
     */
    @Override
    public DepartamentoVo buscarDepartamento(int codigo) throws SQLException {
        return findById(codigo);
    }

    /**
     *
     * @param nombre
     * @return
     * @throws SQLException
     */
    @Override
    public DepartamentoVo buscarDepartNombre(String nombre) throws SQLException {
        List<DepartamentoVo> lista = findAll();
        for (DepartamentoVo dep : lista) {
            if (dep.getNombre().equals(nombre)) {
                return dep;
            }
        }
        return null;
    }

    /**
     *
     * @return @throws SQLException
     */
    @Override
    public String[] nombreDepartamentos() throws SQLException {
        List<DepartamentoVo> deps = findAll();
        String[] nombreDeps = new String[deps.size()];
        for (int i = 0; i < nombreDeps.length; i++) {
            nombreDeps[i] = deps.get(i).getNombre();
        }
        return nombreDeps;
    }

    /**
     *
     * @param oldCodigo
     * @param newCodigo
     * @return
     * @throws SQLException
     */
    @Override
    public int bajaDepartamento(int oldCodigo, int newCodigo) throws SQLException {
        CallableStatement funcion = null;
        try {
            String sql = "{? = call borra_dep(?, ?)}";
            funcion = conexion.prepareCall(sql);
            funcion.registerOutParameter(1, Types.NUMERIC);
            funcion.setInt(2, oldCodigo);
            funcion.setInt(3, newCodigo);
            funcion.executeUpdate();

            int delete = funcion.getInt(1);

            return delete;
        } finally {
            if (funcion != null) {
                funcion.close();
            }
        }
    }

    /**
     * Devuelve el numero de empleados de un departamento;
     *
     * @param codigo
     * @return
     * @throws SQLException
     */
    @Override
    public boolean hasEmpleados(int codigo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM EMPLE WHERE DEPT_NO = ?";
        PreparedStatement pStm = conexion.prepareStatement(sql);
        pStm.setInt(1, codigo);

        ResultSet rst = pStm.executeQuery();
        rst.next();

        int numEmple = rst.getInt(1);

        rst.close();
        pStm.close();
        return numEmple > 0;
    }

    /**
     * Modifica el departamento que se le pasa por parametros.
     *
     * @param depart
     * @return
     * @throws SQLException
     */
    @Override
    public int modificarDepartamento(DepartamentoVo depart) throws SQLException {
        return update(depart);
    }

    /**
     * Devuelve la clase abstracta de esta clase para operar con ella.
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static DepartamentoDAO getDAO() throws ClassNotFoundException,
            SQLException {
        return new DepartamentoCDU();
    }
}
