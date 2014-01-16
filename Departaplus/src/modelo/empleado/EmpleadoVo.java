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

import java.io.Serializable;
import java.util.Date;

/**
 * Clase de empleado segun el patron de diseño VO.
 * 
 * Value Object o Transfer Object.
 * 
 * @author Antonio López Marín
 */
public class EmpleadoVo implements Serializable{
       
    private int codigo;
    private int codigoDepart;
    private String nombre;
    private String oficio;
    private int director;
    private Date fechaAlta;
    private double salario;
    private double comision;

    public EmpleadoVo() {
    }
    
    public EmpleadoVo(int codigo, String nombre, 
            String oficio, int director, Date fechaAlta, double salario, 
            double comision, int codigoDepart) {
        this.codigo = codigo;
        this.codigoDepart = codigoDepart;
        this.nombre = nombre;
        this.oficio = oficio;
        this.director = director;
        this.fechaAlta = fechaAlta;
        this.salario = salario;
        this.comision = comision;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigoDepart() {
        return codigoDepart;
    }

    public void setCodigoDepart(int codigoDepart) {
        this.codigoDepart = codigoDepart;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getOficio() {
        return oficio;
    }

    public void setOficio(String oficio) {
        this.oficio = oficio;
    }

    public int getDirector() {
        return director;
    }

    public void setDirector(int director) {
        this.director = director;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public double getComision() {
        return comision;
    }

    public void setComision(double comision) {
        this.comision = comision;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
