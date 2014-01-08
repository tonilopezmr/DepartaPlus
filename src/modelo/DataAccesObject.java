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
package modelo;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz para el patron de desarollo DAO, Data Acces Object.
 * 
 * T se refiere a la clase VO, donde se va a realizar los cambios y E al tipo
 * ID de esa clase.
 * 
 * @author Antonio López Marín
 */
public interface DataAccesObject<T extends Serializable,E> {
    
    /**
     * Crear
     * 
     * @param instance
     * @return
     * @throws SQLException 
     */
    public int create(T instance) throws SQLException;
    
    /**
     * Modificar
     * @param instance
     * @return
     * @throws SQLException 
     */
    public int update(T instance) throws SQLException;
    
    /**
     * Modificar, y tambien el identificador.
     * 
     * @param instance
     * @param indentificador
     * @return
     * @throws SQLException 
     */
    public int update(T instance, E indentificador) throws SQLException;
    
    /**
     * Borrar 
     * 
     * @param itentificador
     * @return
     * @throws SQLException 
     */
    public int delete(E itentificador) throws SQLException;
    
    /**
     * 
     * @return
     * @throws SQLException 
     */
    public int deleteAll() throws SQLException;
    
    /**
     * 
     * @param indentificador
     * @return
     * @throws SQLException 
     */
    public T findById(E indentificador) throws SQLException;
    
    /**
     * 
     * @return
     * @throws SQLException 
     */
    public List<T> findAll() throws SQLException;
    
    /**
     * 
     * @param identificador
     * @return
     * @throws SQLException 
     */
    public boolean exists(E identificador) throws SQLException;
}
