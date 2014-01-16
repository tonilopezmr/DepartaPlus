/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modelo.departamento;

import java.util.List;
import modelo.DataAccesObject;
import modelo.hibernate.Depart;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.GenericJDBCException;

/**
 *
 * @author alumno
 */
public abstract class DepartamentoDAO implements DataAccesObject<Depart, Byte>{
    
    protected Session sesion;
    
    public DepartamentoDAO(Session sesion){
        this.sesion = sesion;
    }

    public Session getSesion() {
        return sesion;
    }

    public void setSesion(Session sesion) {
        this.sesion = sesion;
    }
    
    @Override
    public int create(Depart depart) throws HibernateException {
        Transaction trans = sesion.beginTransaction();
        
        sesion.save(depart);
        trans.commit();
        return 1;
    }

    @Override
    public int update(Depart depart) throws HibernateException , GenericJDBCException{
        Transaction trans = sesion.beginTransaction();
        
        sesion.update(depart);
        trans.commit();
        return 1;
    }

    @Override
    public int delete(Depart depart) throws HibernateException {
        Transaction trans = sesion.beginTransaction();
        
        sesion.delete(depart);
        trans.commit();
        return 1;
    }

    @Override
    public Depart findById(Byte identificador) throws HibernateException {
        Depart dep = (Depart)sesion.get(Depart.class, identificador);
        return dep;
    }

    @Override
    public List<Depart> findAll() throws HibernateException {
        Query consulta = sesion.createQuery("from Depart");
        List<Depart> lista = consulta.list();  
        return lista;
    }

    @Override
    public boolean exists(Byte identificador) throws HibernateException {
        Depart dep = (Depart)sesion.get(Depart.class, identificador);
        return dep!=null;
    }
    
     /**
     * * Metodos Abstractos **
     */
    public abstract int bajaDepartamento(Depart codigo) throws HibernateException;

    public abstract int bajaDepartamento(Depart oldDepart, Depart newDepart) throws HibernateException;

    public abstract boolean altaDepartamento(Depart depart) throws HibernateException;

    public abstract int modificarDepartamento(Depart depart) throws HibernateException;

    public abstract Depart buscarDepartamento(byte codigo) throws HibernateException;

    public abstract boolean hasEmpleados(byte codigo) throws HibernateException;
}
