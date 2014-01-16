/*
 * Copyright (C) 2014 Antonio López Marín
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

import java.io.IOException;
import java.sql.SQLException;
import modelo.Conexion;

/**
 * Controlador que controla el comportamiento del programa, para encender, 
 * apagar, conectar o desconectar la base de datos.
 * 
 * @author Antonio López Marín
 */
public class Controlador {
    ControladorDepart depar; 
    ControladorEmple emple;

    public Controlador(ControladorDepart depar, ControladorEmple emple) {
        this.depar = depar;
        this.emple = emple;
    }
    

    public void encender() throws IOException, InterruptedException {
        Conexion.encenderDataBase();
    }

    public void apagar() throws IOException, InterruptedException {
        Conexion.apagarDataBase();
    }
    
}
