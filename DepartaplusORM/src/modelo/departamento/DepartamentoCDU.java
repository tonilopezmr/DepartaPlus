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

import java.util.Set;
import modelo.hibernate.Depart;
import modelo.hibernate.Emple;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Departamento Caso de Uso, es el que maneja y controla las transacciones.
 *
 * @author Antonio López Marín
 */
public class DepartamentoCDU extends DepartamentoDAO {

    /**
     *
     * @throws ClassNotFoundException
     * @throws HibernateException
     */
    public DepartamentoCDU(Session sesion) {
        super(sesion);
    }

    /**
     *
     * @param codigo
     * @return
     * @throws HibernateException
     */
    @Override
    public int bajaDepartamento(Depart codigo) throws HibernateException {
        if (exists(codigo.getDeptNo())) {
            return delete(codigo);
        } else {
            throw new HibernateException("El departamento a dar de baja no existe.");
        }
    }

    /**
     *
     * @param depart
     * @return
     * @throws HibernateException
     */
    @Override
    public boolean altaDepartamento(Depart depart) throws HibernateException {
        byte codigo = depart.getDeptNo();
        if (!exists(codigo)) {
            return create(depart) > 0;
        } else {
            throw new HibernateException("El departamento con codigo " + (int)codigo
                    + " ya existe.");
        }
    }

    /**
     *
     * @param codigo
     * @return
     * @throws HibernateException
     */
    @Override
    public Depart buscarDepartamento(byte codigo) throws HibernateException {
        return findById(codigo);
    }

    /**
     *
     * @param oldCodigo
     * @param newCodigo
     * @return
     * @throws HibernateException
     */
    @Override
    public int bajaDepartamento(Depart oldDepart, Depart newDepart) throws HibernateException {
        Transaction tx = sesion.beginTransaction();
        
        Set<Emple> emps = oldDepart.getEmples();
        for (Emple emp : emps) {
            emp.setDepart(newDepart);
        }
        //Paso los empleados del viejo departamento y se los añado al nuevo
        newDepart.getEmples().addAll(emps);  
        
        sesion.delete(oldDepart);
        
        tx.commit();
        return 1;
    }

    /**
     * Devuelve el numero de empleados de un departamento;
     *
     * @param codigo
     * @return
     * @throws HibernateException
     */
    @Override
    public boolean hasEmpleados(byte codigo) throws HibernateException {        
        Depart dep = (Depart)sesion.get(Depart.class, codigo);
        
        int numEmple = 0;
        if (dep!=null) {
            numEmple = dep.getEmples().size();
        }
        return numEmple > 0;
    }

    /**
     * Modifica el departamento que se le pasa por parametros.
     *
     * @param depart
     * @return
     * @throws HibernateException
     */
    @Override
    public int modificarDepartamento(Depart depart) throws HibernateException {
        return update(depart);
    }

    /**
     * Devuelve la clase abstracta de esta clase para operar con ella.
     *
     * @return
     * @throws ClassNotFoundException
     * @throws HibernateException
     */
    public static DepartamentoDAO getDAO(Session sesion) throws ClassNotFoundException,
            HibernateException {
        return new DepartamentoCDU(sesion);
    }
}
