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
package vista.departamentos;

import controlador.ControladorDepart;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import modelo.departamento.DepartamentoVo;
import vista.Vista;

/**
 * Vista para consultar departamentos.
 * 
 * @author Antonio López Marín
 */
public class ConsultarDepart extends JInternalFrame implements Observer, Vista {

    //Lista desplegable
    JComboBox<DepartamentoVo> lista;
    //Campo
    JTextField codigoField, nombreField, locField;
    //Array de nombre de departamentos.
    DepartamentoVo[] departamentos;
    //Controlador
    ControladorDepart controller;

    /**
     * Al constructor se le tiene que pasar un array
     *
     * Implementar un patron de diseño Observer, para cuando cambie la base de
     * datos, por ejemplo esta ventana este abierta y a la vez se añada un
     * departamento, pues entonces se tiene que actualizar la lista y tener
     * todos los departamentos.
     *
     * @param departamentos
     */
    public ConsultarDepart(ControladorDepart controller) throws ClassNotFoundException, SQLException {
        this.controller = controller;
        loadWindows();
    }

    /**
     * Añade los items de departamento a la lista
     *
     * @param items
     * @deprecated
     */
    private void addItems(DepartamentoVo[] items) {
        for (int i = 0; i < items.length; i++) {
            lista.addItem(items[i]);
        }
    }

    /**
     * Devuelve la lista
     *
     * @return
     */
    public DepartamentoVo[] getDepartamentos() {
        return departamentos;
    }

    //Cambia la lista
    public void setDepartamentos(DepartamentoVo[] departamentos) {
        lista.removeAllItems();
        lista.addItem(new DepartamentoVo(-1, "NONE", null));
        for (int i = 0; i < departamentos.length; i++) {
            lista.addItem(departamentos[i]);
        }
    }

    private void loadWindows() {
        setTitle("Consulta de departamentos");
        Dimension dimen = new Dimension(300, 250);
        setSize(dimen);
        setPreferredSize(dimen);
        setMinimumSize(dimen);
        setMaximumSize(dimen);
        //Para cuando cierran la ventana, no sea visible
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        //Lo que puede realizar la ventana
        setResizable(false);
        setClosable(true);
        setIconifiable(true);

        //Añado la consulta al observer
        controller.addObserver(this);

        lista = new JComboBox<>();
        lista.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    DepartamentoVo dep = (DepartamentoVo) e.getItem();
                    
                    //Si no es nulo y es distinto del departamento por defecto
                    if (dep != null && dep.getCodigo() != -1) {
                        codigoField.setText(dep.getCodigo() + "");
                        nombreField.setText(dep.getNombre());
                        locField.setText(dep.getLocalidad());
                    }else{
                        codigoField.setText("");
                        nombreField.setText("");
                        locField.setText("");
                    }
                }
            }
        });

        //Fuente
        Font fuente = new Font("Arial", Font.BOLD, 25);

        //Codigo
        codigoField = new JTextField(4);
        codigoField.setFont(fuente);
        codigoField.setHorizontalAlignment(JTextField.CENTER);
        codigoField.setEditable(false);

        //Nombre
        nombreField = new JTextField(4);
        nombreField.setFont(fuente);
        nombreField.setHorizontalAlignment(JTextField.CENTER);
        nombreField.setEditable(false);

        //Localidad
        locField = new JTextField(4);
        locField.setFont(fuente);
        locField.setHorizontalAlignment(JTextField.CENTER);
        locField.setEditable(false);

        Box box = Box.createVerticalBox();
        //Crea un hueco entre boton y boton
        box.add(Box.createVerticalStrut(20));
        box.add(lista);
        box.add(Box.createVerticalStrut(20));
        box.add(codigoField);
        box.add(Box.createVerticalStrut(20));
        box.add(nombreField);
        box.add(Box.createVerticalStrut(20));
        box.add(locField);
        box.add(Box.createVerticalStrut(100));

        Box box2 = Box.createHorizontalBox();
        box2.add(Box.createHorizontalStrut(40));
        box2.add(box);
        box2.add(Box.createHorizontalStrut(40));

        getContentPane().add(box2);
        
        //Cargo los datos de los departamentos
        this.update(null, null);
    }

    /**
     * Cuando cambien los departamentos, se llamara a este metodo, mediante el
     * patron Observer.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        try {
            setDepartamentos(controller.departamentosAll());
        } catch (SQLException ex) {
            mostrarError("Error al cargar los departamentos");
        }
    }

    @Override
    public void mostrarMensaje(String mensaje) {
    }

    @Override
    public void mostrarError(String error) {
        JOptionPane.showMessageDialog(null, error, "¡ERROR!",
                JOptionPane.WARNING_MESSAGE);
    }
}
