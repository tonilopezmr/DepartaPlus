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

import controlador.ControladorEmple;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import modelo.empleado.Director;
import modelo.hibernate.Depart;
import vista.Vista;

/**
 * Vista para dar de alta a un empleado.
 * 
 * @author Antonio López Marín
 */
public class AltaEmpleado extends JInternalFrame implements Observer, Vista {

    //Panel del formulario
    private JPanel formulario;
    //Panel de la ventana entera
    private JPanel window;
    //Campos
    private JTextField nombreField, salarioField, comisionField, messageLabel;
    //Listas Desplegables
    private JComboBox<Depart> departamentos;
    private JComboBox<Director> directores;
    private JComboBox<String> oficios;
    //Restricciones
    private GridBagConstraints gridCons;
    //Controlador de empleados
    private ControladorEmple controller;
    //Boton que da de alta
    private JButton boton;
    
    //booleano que utilizo para saber cuando se puede modificar
    private boolean hasEnabled;
    
    public AltaEmpleado(ControladorEmple controller) {
        this.controller = controller;
        loadWindow();
    }
    
    /**
     * Metodo que carga la ventana interna.
     * 
     */
    private void loadWindow() {
        setTitle("Alta empleado");
        Dimension dimen = new Dimension(630, 420);
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

        //Instancio las constraint de el GridBagLayout y creo el panel
        gridCons = new GridBagConstraints();
        gridCons.insets = new Insets(10, 10, 10, 10);
        formulario = new JPanel(new GridBagLayout());

        //Fuente
        Font fuente = new Font("Arial", Font.PLAIN, 15);

        //Etiquetas del formulario
        JLabel nombre = new JLabel("Nombre");
        nombre.setFont(fuente);
        JLabel oficio = new JLabel("Oficio");
        oficio.setFont(fuente);
        JLabel director = new JLabel("Director");
        director.setFont(fuente);
        JLabel salario = new JLabel("Salario");
        salario.setFont(fuente);
        JLabel depart = new JLabel("Departamento");
        depart.setFont(fuente);
        JLabel comision = new JLabel("Comisión");
        comision.setFont(fuente);
                       
        //JTextField
        nombreField = new JTextField(10);
        nombreField.setFont(fuente);
        nombreField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (nombreField.getText().length() != 0) {
                    if (hasEnabled) {
                        boton.setEnabled(true);
                    }
                    hasEnabled = true;
                }else{
                    hasEnabled = false;
                }
            }
                        
        });
        nombreField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validName();
            }
        });
        
        //Salario
        salarioField = new JTextField(10);
        salarioField.setFont(fuente);
        salarioField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (hasEnabled) {
                    hasEnabled = true;
                    boton.setEnabled(true);
                }else{
                    hasEnabled = false;
                }                
            }
            
        });
        salarioField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validSalario();
            }
        });
        
        //Comsion
        comisionField = new JTextField(10);
        comisionField.setFont(fuente);
        comisionField.setEditable(false);

        //Listas
        departamentos = new JComboBox<>();
        directores = new JComboBox<>();
        oficios = new JComboBox<>();

        //Etiqueta de mensaje
        messageLabel = new JTextField(30);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 15));
        messageLabel.setHorizontalAlignment(JTextField.CENTER);
        messageLabel.setPreferredSize(new Dimension(80, 50));
        messageLabel.setEditable(false);

        //Boton de alta
        boton = new JButton("Alta");
        boton.setEnabled(false);
        boton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validName();
                validSalario();

                try {
                    String nombre = nombreField.getText();
                    String oficio = (String) oficios.getSelectedItem();
                    double salario = Double.parseDouble(salarioField.getText());
                    Director director = (Director) directores.getSelectedItem();
                    Depart depart =
                            (Depart) departamentos.getSelectedItem();
                    //Creo la fecha de hoy
                    Date date = new Date();

                    int codigo = controller.nuevoCodigo();

                    //Segun el salario y el codigo del departamento cobra una 
                    //comision u otra
                    double comision = controller.calcularComision(salario,
                            depart.getDeptNo());
                    comisionField.setText(comision + "");  //Informo de la comision

                    if (esCadena(nombre)) {
                        if (controller.altaEmpleado(codigo, nombre, oficio,
                            director.getCodigoDir(), date, salario, comision,
                            depart)) {
                        mostrarMensaje("** Se dio de alta correctamente a "
                                + nombre +" **");
                        nombreField.setText("");
                        salarioField.setText("");
                        comisionField.setText("");
                        controller.nuevosCambios();
                    } else {
                        mostrarMensaje("** No se dio de alta a "+ nombre +" **");
                    }
                    }else{
                        mostrarMensaje("** Introduzca un nombre corercto **");
                        boton.setEnabled(false);
                        hasEnabled = false;
                    }
                    
                } catch (NumberFormatException nfe) {
                    mostrarMensaje("** Introduzca un salario correcto **");
                    boton.setEnabled(false);
                    hasEnabled = false;
                } catch (SQLException ex) {
                    mostrarError(ex.getMessage());
                }
            }
        });

        boton.setFont(new Font("Arial", Font.BOLD, 20));

        //Las 6 primeras filas
        gridCons.anchor = GridBagConstraints.LINE_END;
        addGridBag(nombre, 0, 0);
        addGridBag(oficio, 0, 1);
        addGridBag(director, 0, 2);
        addGridBag(salario, 0, 3);
        addGridBag(depart, 0, 4);
        addGridBag(comision, 0, 5);
        gridCons.anchor = GridBagConstraints.CENTER;
        addGridBag(nombreField, 1, 0);
        addGridBag(oficios, 1, 1);
        addGridBag(directores, 1, 2);
        addGridBag(salarioField, 1, 3);
        addGridBag(departamentos, 1, 4);
        addGridBag(comisionField, 1, 5);

        //Row 7
        gridCons.anchor = GridBagConstraints.CENTER;
        gridCons.fill = GridBagConstraints.BOTH;
        gridCons.gridwidth = 2;
        gridCons.gridheight = 2;
        addGridBag(messageLabel, 0, 6);
        gridCons.weightx = 0;
        gridCons.ipadx = 40;
        addGridBag(boton, 3, 6);

        //La etiqueta
        JLabel accionLabel = new JLabel("Alta Empleado");
        accionLabel.setFont(new Font("Arial", Font.BOLD, 27));
        accionLabel.setHorizontalTextPosition(JLabel.CENTER);
        //Creo el panel que contendra el titulo
        JPanel titulo = new JPanel(new FlowLayout());
        titulo.add(accionLabel);
        titulo.setBackground(new Color(197, 217, 237));

        //Creo el panel que contiene toda la ventana y meto sus componentes
        window = new JPanel(new BorderLayout());
        window.add(titulo, BorderLayout.NORTH);
        window.add(formulario, BorderLayout.CENTER);

        //Le pongo el gridBaglayout al internalFrame
        setLayout(new GridBagLayout());
        //Añado la ventana al contenedor con espacios
        gridCons = new GridBagConstraints();
        gridCons.insets = new Insets(0, 20, 10, 20);

        //Pongo el color de fondo del contenedor y le pongo la vetana en el centro 
        getContentPane().setBackground(new Color(197, 217, 237));
        getContentPane().add(window, gridCons);

        //Por defecto pongo el boton de consultar
        getRootPane().setDefaultButton(boton);

        //Por ultimo actualizo las listas
        this.update(null, null);
    }

    /**
     * Añade al GridBagContstraints.
     *
     * @param component
     * @param x
     * @param y
     */
    public void addGridBag(Component component, int x, int y) {
        gridCons.gridx = x;
        gridCons.gridy = y;
        formulario.add(component, gridCons);
    }

    //Cambia la lista departamentos
    public void setDepartamentos() {
        Depart[] departamentos;
        try {
            departamentos = controller.listarDepartamentos();
            this.departamentos.removeAllItems();

            for (int i = 0; i < departamentos.length; i++) {
                Depart dep = departamentos[i];
                this.departamentos.addItem(dep);
            }
        } catch (SQLException ex) {
            this.departamentos.addItem(new Depart((byte)-1, "NONE"));
        }
    }

    //Cambia la lista oficios
    public void setOficios() {
        try {
            String[] oficios = controller.listarOficios();
            this.oficios.removeAllItems();
            for (int i = 0; i < oficios.length; i++) {
                this.oficios.addItem(oficios[i]);
            }
        } catch (SQLException ex) {
            this.oficios.addItem("NONE");
        }
    }

    //Cambia la lista directores
    public void setDirectores() {
        try {
            Director[] directores = controller.listarDirectores();

            this.directores.removeAllItems();
            //Si no hay directores
            if (directores.length == 0) {
                this.directores.addItem(new Director(0, "NONE"));
            } else {
                for (int i = 0; i < directores.length; i++) {
                    this.directores.addItem(directores[i]);
                }
            }
        } catch (Exception e) {
            this.directores.addItem(new Director(0, "NONE"));
        }
    }

    /**
     * Determino que es un numero correcto.
     *
     * @param cadena
     * @return
     */
    private static boolean isNumeric(String cadena) {
        try {
            return Double.parseDouble(cadena) >= 0;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Determino si la cadena reune las condiciones necesarias.
     *
     * @param cadena
     * @return
     */
    private static boolean esCadena(String cadena) {
        try {
            int verify = 0;
            if ("".equals(cadena) || cadena.length() < 3) {
                return false;
            }
            for (int i = 0; i < cadena.length(); i++) {
                try {
                    String test = cadena.substring(i, i + 1);
                    if (" ".equals(test)) {
                        verify++;
                    }
                    Integer.parseInt(test); //Compruebo que no ponga numeros
                    return false;
                } catch (NumberFormatException e) {
                }
            }

            if (verify * 100 / cadena.length() > 20) {
                return false;
            }
            return true;
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

    /**
     * Determina si es correcto el nombre
     *
     * @return
     */
    public void validName() {
        if (!esCadena(nombreField.getText())) {
            mostrarMensaje("** Introduzca un nombre valido **");
            boton.setEnabled(false);
                        hasEnabled = false;
        } else {
            messageLabel.setText("");
        }
    }

    /**
     * Determina que es correcto el salario
     *
     * @return
     */
    public void validSalario() {
        if (!isNumeric(salarioField.getText())) {
            mostrarMensaje("** Introduzca un salario correcto **");
            comisionField.setText("");
            boton.setEnabled(false);
            hasEnabled = false;
        } else {
            double salario = Double.parseDouble(salarioField.getText());

            messageLabel.setText("");
            Depart depart =
                    (Depart) departamentos.getSelectedItem();
            //Muestro la comision que seria con ese salario segun el codigo
            comisionField.setText(controller.calcularComision(salario,
                    depart.getDeptNo()) + "");

        }
    }

    /**
     * Implementacion del patron Observer, cuando hay un cambio en la base de
     * datos se cambian los datos en la vista.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        setDepartamentos();
        setOficios();
        setDirectores();
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        if (mensaje.length() > 58) {
            messageLabel.setFont(new Font("Arial", Font.BOLD, 11));
            messageLabel.setText(mensaje);
        } else {
            messageLabel.setFont(new Font("Arial", Font.BOLD, 15));
            messageLabel.setText(mensaje);
        }
    }

    @Override
    public void mostrarError(String error) {
        JOptionPane.showMessageDialog(this, error);
    }
}