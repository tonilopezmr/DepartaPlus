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

import java.io.Serializable;

/**
 * Clase de departamento segun el patron de diseño VO.
 * 
 * Value Object o Transfer Object.
 * 
 * @author Antonio López Marín
 */
public class DepartamentoVo implements Serializable{
    
    private int codigo;
    private String nombre;
    private String localidad;

    public DepartamentoVo(int codigo, String nombre, String localidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.localidad = localidad;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    
    @Override
    public String toString(){
        return nombre;
    }
}
