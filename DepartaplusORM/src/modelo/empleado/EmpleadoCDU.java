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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import modelo.hibernate.Depart;
import modelo.hibernate.Emple;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Clase empleado Caso de Uso que trabaja con los casos de uso de la base de
 * datos.
 *
 * @author Antonio López Marín
 */
public class EmpleadoCDU extends EmpleadoDAO {

    public EmpleadoCDU(Session sesion) throws ClassNotFoundException,
            HibernateException {
        super(sesion);
    }

    /**
     *
     * @param codigo
     * @return
     * @throws HibernateException
     */
    @Override
    public int bajaEmpleado(Emple emple) throws HibernateException {
        if (exists(emple.getEmpNo())) {
            return delete(emple);
        } else {
            throw new HibernateException("El empleado a dar de baja no existe.");
        }
    }

    /**
     *
     * @param emple
     * @return
     * @throws HibernateException
     */
    @Override
    public boolean altaEmpleado(Emple emple) throws HibernateException {
        short codigo = emple.getEmpNo();
        if (!exists(codigo)) {
            return create(emple) > 0;
        } else {
            throw new HibernateException("El empleado con codigo " + codigo
                    + " ya existe.");
        }
    }

    /**
     *
     * @param emple
     * @return
     * @throws HibernateException
     */
    @Override
    public int modificarEmpleado(Emple emple) throws HibernateException {
        return update(emple);
    }

    /**
     *
     * @param codigo
     * @return
     * @throws HibernateException
     */
    @Override
    public Emple buscarEmpleado(short codigo) throws HibernateException {
        return findById(codigo);
    }

    /**
     * Lista de oficios.
     *
     * @return
     * @throws HibernateException
     */
    @Override
    public String[] listarOficios() throws HibernateException {
        String sql = "select distinct oficio from Emple";

        Query consulta = sesion.createQuery(sql);

        List<String> listOficios = consulta.list();

        String[] oficios = new String[listOficios.size()];
        return listOficios.toArray(oficios);
    }

    /**
     * Lista de directores.
     *
     * @return
     * @throws HibernateException
     */
    @Override
    public Director[] listaDirectores() throws HibernateException {
        String sql = "select empNo, apellido "
                + "from Emple "
                + "where oficio='DIRECTOR'";

        Query consulta = sesion.createQuery(sql);

        List<String> listaDir = consulta.list();

        //Creo un arraylist para meter los resultados
        ArrayList<Director> lista = new ArrayList();

        for (Object s : listaDir) {
            Object[] o = (Object[]) s;
            if (o.length != 0) {
                lista.add(new Director((short)o[0], (String) o[1]));
            }
        }

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
     * @throws HibernateException
     */
    @Override
    public Emple[] listaEmpleadosDepart(byte codDepart) throws HibernateException {
        //Preparamos la lista de empleados
        Set<Emple> empleados = new TreeSet<>();

        Depart dep = (Depart) sesion.get(Depart.class, codDepart);

        if (dep != null) {
            empleados = dep.getEmples();
        }

        //Devuelvo una array de los empleados
        Emple[] array = new Emple[empleados.size()];
        return empleados.toArray(array);
    }

    /**
     * Busca un empleado, que empiece por las siguientes letras que se le pasan
     * por parametros.
     *
     * @param nombre
     * @return
     * @throws HibernateException
     */
    @Override
    public Emple[] buscarEmpleados(String nombre) throws HibernateException {
        String sql = "from Emple "
                + "where upper(apellido) like upper('" + nombre.toUpperCase() + "%') "
                + "order by apellido";

        Query consulta = sesion.createQuery(sql);

        List<Emple> empleados = consulta.list();

        //Devuelvo una array de los empleados
        Emple[] array = new Emple[empleados.size()];
        return empleados.toArray(array);
    }

    /**
     * BUSCA LOS EMPLEADOS QUE EMPIECEN CON ESE NOMBRE Y DEL DEPARTAMENTO QUE SE
     * LE PASA POR PARAMETROS.
     *
     * @param nombre
     * @param codDepart
     * @return
     * @throws HibernateException
     */
    @Override
    public Emple[] buscarEmpleadosDepart(String nombre, byte codDepart)
            throws HibernateException {
        String sql = "from Emple "
                + "where upper(apellido) like upper('" + nombre.toUpperCase() + "%') "
                + "and dept_no = " + (int) codDepart
                + "order by apellido";


        Query consulta = sesion.createQuery(sql);

        List<Emple> empleados = consulta.list();

        //Devuelvo una array de los directores
        Emple[] array = new Emple[empleados.size()];
        return empleados.toArray(array);
    }

    /**
     * Funcion que devuelve un nuevo codigo de empleado.
     *
     * @return
     * @throws HibernateException
     */
    @Override
    public int nuevoCodigo() throws HibernateException {
        String sql = "select max(empNo) from Emple ";

        Query consulta = sesion.createQuery(sql);

        List<Short> codigos = consulta.list();

        int codigo = 0;
        if (!codigos.isEmpty()) {
            codigo = (short)codigos.get(0);
        }

        return codigo + 10;
    }

    /**
     * Devuelve la clase abstracta de esta clase para operar con ella.
     *
     * @return
     * @throws ClassNotFoundException
     * @throws HibernateException
     */
    public static EmpleadoDAO getDAO(Session sesion) throws ClassNotFoundException,
            HibernateException {
        return new EmpleadoCDU(sesion);
    }
}