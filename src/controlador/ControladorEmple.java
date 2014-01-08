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

import java.sql.SQLException;
import java.util.Date;
import java.util.Observable;
import modelo.departamento.DepartamentoVo;
import modelo.empleado.Director;
import modelo.empleado.EmpleadoCDU;
import modelo.empleado.EmpleadoDAO;
import modelo.empleado.EmpleadoVo;
import vista.Vista;

/**
 * Controlador de empleados que tiene un metodo por cada control de la base de
 * datos, para comunicarse con ella.
 * 
 * @author Antonio López Marín
 */
public class ControladorEmple extends Observable{
    
    private Vista vista;
    private EmpleadoDAO modelo;
    private ControladorDepart departController;
    
    /**
     * 
     * @param vista
     * @param departController
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public ControladorEmple(Vista vista, ControladorDepart departController) 
            throws ClassNotFoundException, SQLException {
        this.vista = vista;
        this.departController = departController;
        this.modelo = EmpleadoCDU.getDAO();
    } 
    
    /**
     * 
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public void conectar() throws ClassNotFoundException, SQLException{
        modelo.conectar();
    }
    
    /**
     * 
     * @param codigo
     * @return
     * @throws SQLException 
     */
    public DepartamentoVo getDepartamento(int codigo) throws SQLException{
        return departController.getDepartamento(codigo);
    }
    
    /**
     * 
     * @param codigo
     * @return
     * @throws SQLException 
     */
    public Director getDirector(int codigo) throws SQLException{
        Director dir = null;
        Director[] directores = modelo.listaDirectores();
        
        for (int i = 0; i < directores.length; i++) {
            Director director = directores[i];
            
            if (director.getCodigoDir() == codigo) {
                dir = director;
            }
        }
        
        return dir;
    }
    
    /**
     * 
     * @return
     * @throws SQLException 
     */
    public int nuevoCodigo() throws SQLException{
        return modelo.nuevoCodigo();
    }
    
    /**
     * 
     * @param salario
     * @param codigo
     * @return 
     */
    public double calcularComision(double salario, int codigo){
        return (salario*0.010) + (codigo*5);
    }
    
    /**
     * 
     * @param codigo
     * @return
     * @throws SQLException 
     */
    public boolean bajaEmpleado(int codigo) throws SQLException{
        return modelo.bajaEmpleado(codigo) > 0;
    }
    
    /**
     * 
     * @param codigo
     * @param nombre
     * @param oficio
     * @param director
     * @param fechaAlta
     * @param salario
     * @param comision
     * @param codDepart
     * @return
     * @throws SQLException 
     */
    public boolean altaEmpleado(int codigo, String nombre, String oficio, 
            int director, Date fechaAlta, double salario, double comision, 
            int codDepart) throws SQLException{

         EmpleadoVo emp = new EmpleadoVo(codigo,nombre,oficio, director, 
                 fechaAlta,salario,comision,codDepart);

        return modelo.altaEmpleado(emp);
    }
    
    /**
     * 
     * @param codigo
     * @param nombre
     * @param oficio
     * @param director
     * @param fechaAlta
     * @param salario
     * @param comision
     * @param codDepart
     * @return
     * @throws SQLException 
     */
    public boolean modificarEmpleado(int codigo, String nombre, String oficio, 
            int director, Date fechaAlta, double salario, double comision, 
            int codDepart) throws SQLException{
        
         EmpleadoVo emp = new EmpleadoVo(codigo,nombre,oficio, director, 
                 fechaAlta,salario,comision,codDepart);
         
        return modelo.update(emp) > 0;
    }
    
    /**
     * 
     * @param codigo
     * @return
     * @throws SQLException 
     */
    public EmpleadoVo buscarEmpleado(int codigo) throws SQLException{
        return modelo.findById(codigo);
    }
    
    /**
     * 
     * @return
     * @throws SQLException 
     */
    public DepartamentoVo[] listarDepartamentos() throws SQLException{
        return departController.departamentosAll();
    }
        
    /**
     * 
     * @return
     * @throws SQLException 
     */
    public String[] listarOficios() throws SQLException{
        return modelo.listarOficios();
    }
    
    /**
     * 
     * @return
     * @throws SQLException 
     */
    public Director[] listarDirectores() throws SQLException{
        return modelo.listaDirectores();
    }
    
    /**
     * 
     * @param codDepart
     * @return
     * @throws SQLException 
     */
    public EmpleadoVo[] listarEmpleadosPorDepart(int codDepart) throws SQLException{
        return modelo.listaEmpleadosDepart(codDepart);
    }
    
    /**
     * 
     * @param nombre
     * @return
     * @throws SQLException 
     */
    public EmpleadoVo[] buscarEmpleados(String nombre) throws SQLException{
        return modelo.buscarEmpleados(nombre);
    }
    
    /**
     * 
     * @param nombre
     * @param codDepart
     * @return
     * @throws SQLException 
     */
    public EmpleadoVo[] buscarEmpleadosDepart(String nombre, int codDepart) 
            throws SQLException{
        return modelo.buscarEmpleadosDepart(nombre, codDepart);
    }
    
    /**
     * Para que la vista sepa que se dio de alta correctamente.
     *
     */
    public void nuevosCambios(){
        this.setChanged();
        this.notifyObservers();
    }
}
