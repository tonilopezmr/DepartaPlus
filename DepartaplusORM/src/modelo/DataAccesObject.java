/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modelo;


import java.io.Serializable;
import java.util.List;
import org.hibernate.HibernateException;

/**
 * Interfaz para el patron de desarollo DAO, Data Acces Object.
 * 
 * T se refiere a la clase VO, donde se va a realizar los cambios y E al tipo
 * ID de esa clase.
 * 
 * @author Antonio López Marín
 * @param <T>
 * @param <E>
 */
public interface DataAccesObject<T extends Serializable,E> {
    
    /**
     * Crear
     * 
     * @param instance
     * @return
     * @throws HibernateException 
     */
    public int create(T instance) throws HibernateException;
    
    /**
     * Modificar
     * @param instance
     * @return
     * @throws HibernateException 
     */
    public int update(T instance) throws HibernateException;
    
    /**
     * Borrar 
     * 
     * @param instance
     * @return
     * @throws HibernateException 
     */
    public int delete(T instance) throws HibernateException;

    
    /**
     * 
     * @param indentificador
     * @return
     * @throws HibernateException 
     */
    public T findById(E identificador) throws HibernateException;
    
    /**
     * 
     * @return
     * @throws HibernateException 
     */
    public List<T> findAll() throws HibernateException;
    
    /**
     * 
     * @param identificador
     * @return
     * @throws HibernateException 
     */
    public boolean exists(E identificador) throws HibernateException;
}
