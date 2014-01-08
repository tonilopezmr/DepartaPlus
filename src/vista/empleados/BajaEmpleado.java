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
package vista.empleados;

import controlador.ControladorDepart;
import controlador.ControladorEmple;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import modelo.departamento.DepartamentoVo;
import modelo.empleado.EmpleadoVo;
import vista.Vista;

/**
 * Vista que da de baja a los empleados.
 * 
 * @author Antonio López Marín
 */
public class BajaEmpleado extends JInternalFrame implements Observer, Vista {

    private JComboBox<DepartamentoVo> departamentos;
    private JComboBox<EmpleadoVo> empleados;
    private ControladorEmple controller;
    private JButton boton;
    private JTextField messageLabel;

    public BajaEmpleado(ControladorEmple controller) {
        this.controller = controller;
        loadWindow();
    }

    private void loadWindow() {
        setTitle("Baja empleado");
        Dimension dimen = new Dimension(330, 320);
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

        //Listas
        departamentos = new JComboBox<>();
        departamentos.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    DepartamentoVo dep = (DepartamentoVo) e.getItem();
                    try {
                        setEmpleados(dep.getCodigo());
                        messageLabel.setText("");
                        boton.setEnabled(true);
                    } catch (SQLException ex) {
                        empleados.removeAllItems();
                        boton.setEnabled(false);
                        mostrarMensaje(ex.getMessage());
                    }
                }
            }
        });
        empleados = new JComboBox<>();

        //Insancio el boton
        boton = new JButton("Baja");
        boton.setEnabled(false);
        boton.setPreferredSize(new Dimension(60, 35));
        boton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    EmpleadoVo emp = (EmpleadoVo) empleados.getSelectedItem();

                    if (emp != null) {
                        if (controller.bajaEmpleado(emp.getCodigo())) {
                            mostrarMensaje("** "+ emp.getNombre()
                                    +" se dio de baja correctamente **");
                            controller.nuevosCambios();
                        } else {
                            mostrarMensaje("** "+ emp.getNombre()
                                    +" no se dio de baja correctamente **");
                        }
                    } else {
                        mostrarMensaje("** No hay empleado a dar de baja **");
                    }
                } catch (SQLException ex) {
                    mostrarMensaje(ex.getMessage());
                }
            }
        });

        //Un JtextField para que muestre los mensajes
        messageLabel = new JTextField(30);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 11));
        messageLabel.setHorizontalAlignment(JTextField.CENTER);
        messageLabel.setPreferredSize(new Dimension(80, 50));
        messageLabel.setEditable(false);

        JPanel panelAccion = new JPanel(new BorderLayout(30, 30));

        panelAccion.add(messageLabel, BorderLayout.NORTH);
        panelAccion.add(boton, BorderLayout.SOUTH);

        Box box = Box.createVerticalBox();
        //Crea un hueco entre boton y boton

        box.add(Box.createVerticalStrut(30));
        box.add(departamentos);
        box.add(Box.createVerticalStrut(40));
        box.add(empleados);
        box.add(Box.createVerticalStrut(30));
        box.add(panelAccion);
        box.add(Box.createVerticalStrut(100));

        Box box2 = Box.createHorizontalBox();
        box2.add(Box.createHorizontalStrut(40));
        box2.add(box);
        box2.add(Box.createHorizontalStrut(40));

        //Añado al contentpane los componentes
        getContentPane().add(box2);

        //Cargo los datos
        this.update(null, null);
    }

    /**
     * Cambia los departamentos, segun haya en la base de datos.
     * 
     * @param departamentos 
     */
    public void setDepartamentos(DepartamentoVo[] departamentos) {
        this.departamentos.removeAllItems();
        for (int i = 0; i < departamentos.length; i++) {
            this.departamentos.addItem(departamentos[i]);
        }
    }
    
    /**
     * Cambia la lista empleados segun el departamento.
     * 
     * @param codigo
     * @throws SQLException 
     */
    public void setEmpleados(int codigo) throws SQLException {
        this.empleados.removeAllItems(); //Primero se borran todos los registros
        //Y recorre los empleados que haya para ese departamento
        EmpleadoVo[] empleados = controller.listarEmpleadosPorDepart(codigo);
        for (int i = 0; i < empleados.length; i++) {
            this.empleados.addItem(empleados[i]);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            //Si observable es nulo que quiere decir que lo llamamos a mano
            //o es instancia del controlador departamento se actualiza
            if (o == null || o instanceof ControladorDepart) {
                setDepartamentos(controller.listarDepartamentos());
            }
            
            //Se actualizan los empleados del departamento seleccionado
            DepartamentoVo dep = (DepartamentoVo) departamentos.getSelectedItem();
            setEmpleados(dep.getCodigo());
            boton.setEnabled(true);
        } catch (SQLException ex) {
            boton.setEnabled(false);
        }
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        messageLabel.setText(mensaje);
    }

    @Override
    public void mostrarError(String error) {
        JOptionPane.showMessageDialog(this, error);
    }
}
