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

/**
 * Clase director, que contiene un director de los empleados.
 * 
 * @author Antonio López Marín
 */
public class Director {
    
    private int codigoDir;
    private String nombre;

    public Director(int codigoDir, String nombre) {
        this.codigoDir = codigoDir;
        this.nombre = nombre;
    }

    public int getCodigoDir() {
        return codigoDir;
    }

    public void setCodigoDir(int codigoDir) {
        this.codigoDir = codigoDir;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
