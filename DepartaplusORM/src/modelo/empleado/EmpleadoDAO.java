/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo.empleado;

import java.util.List;
import modelo.DataAccesObject;
import modelo.hibernate.Emple;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author alumno
 */
public abstract class EmpleadoDAO implements DataAccesObject<Emple, Short> {
    
    Session sesion;
    
    public EmpleadoDAO(Session sesion) {
        this.sesion = sesion;
    }    

    public Session getSesion() {
        return sesion;
    }

    public void setSesion(Session sesion) {
        this.sesion = sesion;
    }
    
    @Override
    public int create(Emple emple) throws HibernateException {
        Transaction trans = sesion.beginTransaction();
        sesion.save(emple);
        trans.commit();
        return 1;
    }

    @Override
    public int update(Emple emple) throws HibernateException {
        Transaction trans = sesion.beginTransaction();
        sesion.update(emple);
        trans.commit();
        return 1;
    }

    @Override
    public int delete(Emple emple) throws HibernateException {
        Transaction trans = sesion.beginTransaction();   
        //Importante limpiar antes de eliminar ya que puede ser que el objeto
        //que queremos borrar este ya asociado a alguna ventana o un array
        //por ello tenemos que limpiar la sesion para que no salte ninguna
        //excepcion de tipo org.hibernate.NonUniqueObjectException
        sesion.clear();     
        sesion.delete(emple);
        trans.commit();      
        return 1;
    }

    @Override
    public Emple findById(Short identificador) throws HibernateException {
        Emple emp = (Emple)sesion.get(Emple.class, identificador);        
        return emp;
    }

    @Override
    public List<Emple> findAll() throws HibernateException {        
        Query consulta = sesion.createQuery("from Emple");        
        return consulta.list();
    }

    @Override
    public boolean exists(Short identificador) throws HibernateException {                
        Emple emp = (Emple) sesion.get(Emple.class, identificador);        
        return emp!=null;
    }

    /**
     * * Metodos Abstractos **
     */
    public abstract int bajaEmpleado(Emple codigo) throws HibernateException;

    public abstract boolean altaEmpleado(Emple emple) throws HibernateException;

    public abstract int modificarEmpleado(Emple emple) throws HibernateException;

    public abstract Emple buscarEmpleado(short codigo) throws HibernateException;

    public abstract String[] listarOficios() throws HibernateException;

    public abstract Director[] listaDirectores() throws HibernateException;

    public abstract Emple[] listaEmpleadosDepart(byte codDepart) throws HibernateException;

    public abstract Emple[] buscarEmpleados(String nombre) throws HibernateException;

    public abstract Emple[] buscarEmpleadosDepart(String nombre, byte codDepart)
            throws HibernateException;

    public abstract int nuevoCodigo() throws HibernateException;
}