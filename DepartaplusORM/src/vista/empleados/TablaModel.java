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
package vista.empleados;

import javax.swing.table.DefaultTableModel;

/**
 * Modelo del JTable que muestra los empleados, se redefine un modelo
 * de la tabla para que se comporte como yo desee.
 * 
 * @author Antonio López Marín
 */
public class TablaModel extends DefaultTableModel{

    public TablaModel() {
    }

    public TablaModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }

    public TablaModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }
    
    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false; 
    }
}
