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
package controlador;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import modelo.Conexion;
import modelo.departamento.DepartamentoCDU;
import modelo.departamento.DepartamentoDAO;
import modelo.hibernate.Depart;
import modelo.hibernate.Emple;
import org.hibernate.Session;
import org.hibernate.exception.GenericJDBCException;
import vista.Vista;

/**
 * Extiende de observable porque utiliza el patron de diseño observer.
 *
 * @author Antonio López Marín
 */
public class ControladorDepart extends Observable {

    //Vista
    Vista vista;
    //Modelo
    DepartamentoDAO modelo;
     
    public ControladorDepart(Vista vista) throws ClassNotFoundException, SQLException {
        this.vista = vista;
        modelo = DepartamentoCDU.getDAO(Conexion.getSession());
    }
     
    /**
     * Devuelve el nombre de un departamento.
     *
     * @param codigo pasandole el codigo del mismo.
     * @return
     * @throws SQLException
     */
    public String nombreDepart(byte codigo) throws SQLException {
        return modelo.buscarDepartamento(codigo).getDnombre();
    }

    /**
     * Da de alta un departamento con sus validaciones etc.
     *
     * @param codigo
     * @param nombre
     * @param loc
     * @return
     * @throws SQLException
     */
    public boolean altaDepartamento(int codigo, String nombre, String loc)
            throws SQLException {
        return modelo.altaDepartamento(new Depart((byte)codigo, nombre, loc, null));
    }

    /**
     * Da de baja un departamento.
     *
     * @param codigo
     * @return
     * @throws SQLException
     */
    public int bajaDepartamento(Depart codigo) throws SQLException {
        return modelo.bajaDepartamento(codigo);
    }

    /**
     * Da de baja un departamento y los empleados que tenga se cambian.
     *
     * @param oldCod
     * @param newCod
     * @return
     * @throws SQLException
     */
    public int bajaDepartamento(Depart oldCod, Depart newCod) throws SQLException {
        return modelo.bajaDepartamento(oldCod, newCod);
    }
    
    /**
     * Devuelve el departamento con ese codigo.
     * 
     * @param codigo
     * @return
     * @throws SQLException 
     */
    public Depart getDepartamento(byte codigo) throws SQLException{
        return modelo.buscarDepartamento(codigo);
    }
    
    /**
     * Devuelve todos los departamentos de la base de datos, en un array.
     *
     * @return Depart[] array de departamentos
     * @throws SQLException
     */
    public Depart[] departamentosAll() throws SQLException {
        modelo.setSesion(Conexion.getSession());
        List<Depart> lista = modelo.findAll();
        Depart[] array = new Depart[lista.size()];
        return lista.toArray(array);
    }

    /**
     *
     * @param cod
     * @return
     * @throws SQLException
     */
    public boolean existeDepartamento(int cod) throws SQLException {
        return modelo.exists((byte)cod);
    }

    /**
     * Devuelve el numero de empleados.
     *
     * @param codigo
     * @return
     * @throws SQLException
     */
    public boolean hasEmpleados(int codigo) throws SQLException {
        return modelo.hasEmpleados((byte)codigo);
    }
    
    /**
     * Modifica un departamento.
     * 
     * @param codigo
     * @param nombre
     * @param loc
     * @return
     * @throws SQLException 
     */
    public int modificarDepartamento(int codigo, String nombre, String loc) 
            throws SQLException, GenericJDBCException{  
        Session sesion = Conexion.getSession();
        modelo.setSesion(sesion);
        
        Depart dep = (Depart) sesion.get(Depart.class, (byte)codigo);
        dep.setDnombre(nombre);
        dep.setLoc(loc);
        modelo.modificarDepartamento(dep);
        
        sesion.close();
        return 1;
    }

    /**
     * Para que la vista sepa que se dio de alta correctamente.
     *
     */
    public void nuevaModificacion() {
        //Notifico que hay que actualizar la lista de Consultas
        this.setChanged();
        this.notifyObservers();
    }
}
