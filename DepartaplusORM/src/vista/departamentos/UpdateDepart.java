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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import modelo.hibernate.Depart;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.exception.GenericJDBCException;
import vista.Vista;

/**
 * Clase que tiene tres funcionalidades:
 *
 * Alta de departamentos. Baja de departamentos. Modificacion de departamentos.
 *
 * Las tres funcionalidades tienen una misma vista, porque se ven igual.
 *
 * Los controles se comportan segun su funcion.
 *
 * @author Antonio López Marín
 */
public class UpdateDepart extends JInternalFrame implements Vista {

    //Paneles
    JPanel titulo, formulario, botones, window;
    //Botones
    JButton consultar, accionBtn;
    //Label's
    JLabel codigoLabel, nombreLabel, locLabel, accionLabel;
    //TextField's
    JTextField codigoField, nombreField, locField, messageLabel;
    //Contenedor
    Container contenedor;
    //Restricciones de el gridBag
    GridBagConstraints gridCons;
    //Controlador 
    ControladorDepart controller;
    //Para que se utiliza la ventana
    String accion;
    /**
     * Errores de la base de datos, que necesitara la vista.
     */
    final public int NUMBER_TOO_LONG = 1438;
    final public static int STRING_TOO_LONG = 12899;
    final public static int ERROR_INTEGRIDAD = 2292;
    /**
     * Funciones de departamento.
     *
     * Lo ideal seria que fueran identificadores numericos.
     */
    final static public String ALTA = "Alta";
    final static public String BAJA = "Baja";
    final static public String MODIFICAR = "Modificar";

    /**
     * Con el controlador.
     *
     * @param accion
     * @param controller
     */
    public UpdateDepart(String accion, ControladorDepart controller) throws ClassNotFoundException, SQLException {
        setTitle(accion + " departamento");
        this.accion = accion;
        contenedor = getContentPane();
        accionLabel = new JLabel("departamento", JLabel.LEFT);
        accionLabel.setText(accion + " departamento");
        accionBtn = new JButton(accion);

        this.controller = controller;
        loadWindows();
    }

    /**
     * Dialogo que sale para migrar los empleados.
     *
     * @param codigo
     * @return
     * @throws SQLException
     */
    public Depart migrarEmpleados(Depart depart) throws SQLException {
        //Todos los departamentos
        Depart[] departs = controller.departamentosAll();
        //Array auxiliar
        Depart[] listaDeps = new Depart[departs.length - 1];

        //Variable auxiliar para el recorrido del array auxiliar
        int x = 0;

        //Recoje todos los empleados menos al que se le va a dar de baja
        for (int i = 0; i < departs.length; i++) {
            Depart dep = departs[i];

            //Si no es igual al departamento que se quiere borrar..
            if (dep.getDeptNo()!= depart.getDeptNo()) {
                listaDeps[i - x] = dep;
            } else {
                x = 1;
            }
        }
        //Imagen para poner al dialogo, de un empleado
        ImageIcon img = new ImageIcon("images/empleado.png");
        Object salida = JOptionPane.showInputDialog(this,
                "Traspasar empleados al departamento:",
                "Transladar empleados",
                JOptionPane.INFORMATION_MESSAGE,
                img,
                listaDeps, listaDeps[0]);

        if (salida != null) {
            Depart dep = (Depart) salida;
            return dep;
        }

        return null;
    }

    public boolean isTraspaso(String depart) throws SQLException {
        int decision = JOptionPane.showConfirmDialog(this, "El departamento "
                + "" + depart + " tiene empleados asociados en este momento\n¿Desea "
                + "traspasarlos a otro departamento?", "¿Traspasar empleados?",
                JOptionPane.YES_NO_OPTION);

        return decision == JOptionPane.YES_OPTION;
    }

    /**
     * Muestra un dialogo de mensaje, segun el texto que se le pasa por
     * parametros.
     *
     * @param mensaje
     */
    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    /**
     * Formatea el mensaje segun lo largo que sea, para que se vea
     * correctamente.
     */
    @Override
    public void mostrarMensaje(String texto) {
        if (texto.length() > 58) {
            messageLabel.setFont(new Font("Arial", Font.BOLD, 11));
            messageLabel.setText(texto);
        } else {
            messageLabel.setFont(new Font("Arial", Font.BOLD, 15));
            messageLabel.setText(texto);
        }
    }

    /**
     * Muestra un mensaje de error, segun su error.
     *
     * @param codigoError
     */
    public void sucedioError(int codigoError) {
        switch (codigoError) {
            //Valor de numeros demasiado grandes
            case NUMBER_TOO_LONG:
                mostrarMensaje("** Numero de codigo demasiado grande **");
                break;
            //Valor de demasiados caracteres
            case STRING_TOO_LONG:
                mostrarMensaje("** Cadena demasiada larga, escriba una mas pequeña **");
                break;
            //Error de restriccion de integridad violada
            case ERROR_INTEGRIDAD:
                mostrarMensaje("** El departamento tiene empleados, traspaselos **");
                break;
            default:
                mostrarMensaje("** Expecifique un departamento **");
        }
    }

    /**
     * Metodo que consulta al usuario si quiere realizar el traspaso, y si es
     * asi a quien quiere traspasar.
     *
     * @param codigo
     * @param nombreDep
     * @return
     * @throws SQLException
     */
    private int traspaso(Depart depart, String nombreDep) throws SQLException {
        int delete = 0;
        if (isTraspaso(nombreDep)) {
            Depart newDep = migrarEmpleados(depart);
            //controller.traspaso(codigo, newCod);
            if (newDep != null) {
                delete = controller.bajaDepartamento(depart, newDep);
            } else {
                mostrarError("No se realizo la baja de "
                        + "departamento " + nombreDep);
            }
        } else {
            mostrarError("No se realizo la baja de "
                    + "departamento " + nombreDep);
        }

        return delete;
    }

    /**
     *
     * @param codigo
     * @throws SQLException
     */
    public void alta(int codigo, String nombre, String loc) throws SQLException {
        boolean isCreate = controller.altaDepartamento(codigo, nombre, loc);
        if (isCreate) {
            mostrarMensaje("** El departamento se dio "
                    + "de alta con exito**");
            controller.nuevaModificacion();
        } else {
            mostrarMensaje("** Hubo un problema al dar "
                    + "de alta, no se creo el nuevo departamento **");
        }
    }

    /**
     *
     * @param codigo
     * @throws SQLException
     */
    private void baja(int codigo) throws SQLException {
        Depart depart = controller.getDepartamento((byte)codigo);
        String nombreDep = depart.getDnombre();
        int delete;

        //Si el departamento tiene empleados, los traspasa
        if (controller.hasEmpleados(codigo)) {
            delete = traspaso(depart, nombreDep);
        } else {
            //Sino lo borra directamente.
            delete = controller.bajaDepartamento(depart);
        }
        //Si se borro
        if (delete > 0) {
            mostrarMensaje("** El departamento " + nombreDep
                    + " se dio de baja con exito **");
            controller.nuevaModificacion();
        } else {
            mostrarMensaje("** No se dio de baja al departamento "
                    + nombreDep + " **");
        }
    }

    /**
     *
     * @param codigo
     * @param nombre
     * @param loc
     * @throws SQLException
     */
    private void modificar(int codigo, String nombre, String loc) 
            throws SQLException, GenericJDBCException {        
        int update = controller.modificarDepartamento(codigo, nombre, loc);

        if (update != 0) {
            mostrarMensaje("** El departamento " + nombre
                    + " se modifico correctamente. **");
            controller.nuevaModificacion();

            //Desactivo los campos de modificiacion
            nombreField.setText("");
            nombreField.setEditable(false);
            locField.setText("");
            locField.setEditable(false);
            //Activo el campo para poder modificar mas departamentos
            codigoField.setEditable(true);
        } else {
            mostrarMensaje("** El departamento " + nombre
                    + " no se modifico correctamente. **");
        }
    }

    /**
     * Consulta si la vista reune las condiciones para realizar la accion.
     *
     * @param codigo
     * @throws SQLException
     */
    private void consultaAlta(int codigo) throws SQLException {
        if (verificarNombreField()) {
            if (controller.existeDepartamento(codigo)) {
                mostrarMensaje("** El departamento con codigo "
                        + "" + codigo + " ya existe **");
                codigoField.requestFocus();
                accionBtn.setEnabled(false);
            } else {
                if (nombreField.getText().length() != 0) {
                    //No pasa nada si es nula
                    if (esCadena(locField.getText()) 
                            || locField.getText().length() == 0) {
                        mostrarMensaje("** Se puede dar de alta el "
                                + "nuevo departamento **");
                        accionBtn.setEnabled(true);
                    } else {
                        mostrarMensaje("** Ponga una localidad correcta **");
                        accionBtn.setEnabled(false);
                    }
                } else {
                    mostrarMensaje("** Introduzca un nombre de "
                            + "departamento **");
                    accionBtn.setEnabled(false);
                }
            }
        }
    }

    /**
     * Consulta si la vista reune las condiciones para realizar la accion.
     *
     * @param codigo
     * @throws SQLException
     */
    private void consultaBaja(int codigo) throws SQLException {
        if (controller.existeDepartamento(codigo)) {
            mostrarMensaje("** Se puede dar de baja el "
                    + "departamento " + codigo + " **");
            accionBtn.setEnabled(true);
        } else {
            mostrarMensaje("** El departamento no existe **");
            accionBtn.setEnabled(false);
        }
    }

    /**
     * Consulta si la vista reune las condiciones para realizar la accion.
     *
     * @param codigo
     * @throws SQLException
     */
    private void consultaModificar(int codigo) throws SQLException {
        if (controller.existeDepartamento(codigo)) {
            mostrarMensaje("** Se puede modificar el "
                    + "departamento " + codigo + " **");
            Depart dep = controller.getDepartamento((byte)codigo);

            nombreField.setText(dep.getDnombre());
            locField.setText(dep.getLoc());

            //No se puede modificar el codigo
            codigoField.setEditable(false);
            //Para que pueda modificar los campos
            nombreField.setEditable(true);
            locField.setEditable(true);
            //Se pueda modificar en la base de datos
            accionBtn.setEnabled(true);
        } else {
            mostrarMensaje("** El departamento no existe **");
            accionBtn.setEnabled(false);
        }
    }

    /**
     *
     * @param codigo
     * @throws SQLException
     */
    private void consultar(int codigo) throws SQLException {
        //Como la accion se pone en el titulo
        switch (accion) {
            case ALTA:
                consultaAlta(codigo);
                break;
            case BAJA:
                consultaBaja(codigo);
                break;
            case MODIFICAR:
                consultaModificar(codigo);
                break;
        }
    }

    /**
     * Verifica el codigoField.
     *
     * @return
     */
    private boolean verificarCodigoField() {
        if (isNumeric(codigoField.getText())) {
            Integer.parseInt(codigoField.getText());
            messageLabel.setText("");
            return true;
        } else {
            mostrarMensaje("Introduzca un numero como codigo de "
                    + "departamento.");
            accionBtn.setEnabled(false);
            return false;
        }
    }

    /**
     * Verifica el nombreField.
     *
     * @return
     */
    private boolean verificarNombreField() {
        if (esCadena(nombreField.getText())) {
            messageLabel.setText("");
            return true;
        } else {
            mostrarMensaje("** Introduzca un nombre de departamento **");
            accionBtn.setEnabled(false);
            return false;
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
            return Integer.parseInt(cadena) >= 0;
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
     * Cargo la ventana con sus controles.
     *
     */
    private void loadWindows() {
        Dimension dimen = new Dimension(630, 380);
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

        //Añado el listener del foco
        addFocusListener(new FocoListener());

        //Head
        accionLabel.setFont(new Font("Arial", Font.BOLD, 27));

        //Etiquetas
        codigoLabel = new JLabel("Codigo");
        codigoLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        nombreLabel = new JLabel("Nombre");
        nombreLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        locLabel = new JLabel("Localidad");
        locLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        /* Campos */

        //Codigo
        codigoField = new JTextField(5);
        codigoField.setFont(new Font("Arial", Font.PLAIN, 15));
        //No te deja poner el foco en otro sitio hasta que no sea valido
        codigoField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verificarCodigoField();
            }
        });

        //Nombre
        nombreField = new JTextField(10);
        nombreField.setFont(new Font("Arial", Font.PLAIN, 15));
        nombreField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verificarNombreField();
            }
        });

        //Si el titulo de la ventana es de Alta que se pueda editar
        nombreField.setEditable(accion.equals(ALTA));

        //Localidad
        locField = new JTextField(10);
        locField.setFont(new Font("Arial", Font.PLAIN, 15));
        locField.setEditable(accion.equals(ALTA));

        //Mensajes 
        messageLabel = new JTextField(30);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 15));
        messageLabel.setHorizontalAlignment(JTextField.CENTER);
        messageLabel.setPreferredSize(new Dimension(100, 50));
        messageLabel.setEditable(false);

        //Botones
        consultar = new JButton("Consultar");
        consultar.setFont(new Font("Arial", Font.BOLD, 12));
        consultar.setPreferredSize(new Dimension(90, 50));
        consultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (verificarCodigoField()) {
                        consultar(Integer.parseInt(codigoField.getText()));
                    } else {
                        codigoField.requestFocus();
                    }
                } catch (SQLException sqle) {
                    mostrarMensaje("** Error en la base de datos **");
                }
            }
        });

        if (accion.equals(MODIFICAR)) {
            accionBtn.setFont(new Font("Arial", Font.BOLD, 12));
        } else {
            accionBtn.setFont(new Font("Arial", Font.BOLD, 20));
        }
        accionBtn.setPreferredSize(new Dimension(90, 50));
        accionBtn.setEnabled(false);
        accionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    switch (accion) {
                        case ALTA:
                            alta(Integer.parseInt(codigoField.getText()),
                                    nombreField.getText(), locField.getText());
                            break;
                        case BAJA:
                            baja(Integer.parseInt(codigoField.getText()));
                            break;
                        case MODIFICAR:
                            if (esCadena(nombreField.getText())) {
                                //No pasa nada si la localidad es nula
                                if (esCadena(locField.getText()) 
                                        || locField.getText().length() == 0) {
                                        modificar(Integer.parseInt(codigoField.getText()),
                                            nombreField.getText(), locField.getText());
                                    codigoField.setEnabled(true);
                                } else {
                                    mostrarMensaje("** Ponga una localidad correcta **");
                                    accionBtn.setEnabled(false);
                                }
                            } else {
                                mostrarMensaje("** Introduzca un nombre de "
                                        + "departamento **");
                                accionBtn.setEnabled(false);
                            }
                            break;
                    }
                    accionBtn.setEnabled(false);
                }catch(GenericJDBCException bue){
                    sucedioError(bue.getErrorCode());
                }catch (SQLException sqle) {
                    sucedioError(sqle.getErrorCode());
                }
            }
        });

        //Paneles
        titulo = new JPanel(new FlowLayout());
        titulo.add(accionLabel);
        titulo.setBackground(new Color(197, 217, 237));

        //Añadir botones
        botones = new JPanel(new BorderLayout());
        botones.add(consultar, BorderLayout.NORTH);
        botones.add(accionBtn, BorderLayout.SOUTH);

        formulario = new JPanel(new GridBagLayout());
        gridCons = new GridBagConstraints();
        //Espacioes entre componentes
        gridCons.insets = new Insets(20, 20, 20, 20);
        //Todos pegados a la izquierda
        gridCons.anchor = GridBagConstraints.LINE_START;

        //Row1
        addGridBag(codigoLabel, 0, 0);
        addGridBag(codigoField, 1, 0);
        gridCons.gridheight = 5;
        gridCons.fill = GridBagConstraints.BOTH;
        addGridBag(botones, 2, 0);
        gridCons.gridheight = 1;
        gridCons.fill = GridBagConstraints.NONE;
        //Row2
        addGridBag(nombreLabel, 0, 1);
        addGridBag(nombreField, 1, 1);
        //Row3
        addGridBag(locLabel, 0, 2);
        addGridBag(locField, 1, 2);
        //Row4
        gridCons.anchor = GridBagConstraints.CENTER;
        gridCons.fill = GridBagConstraints.BOTH;
        gridCons.ipadx = 40;
        gridCons.gridwidth = 2;
        gridCons.gridheight = 2;
        addGridBag(messageLabel, 0, 3);

        //Lo añado a la ventana que contiene todos los compotentes
        window = new JPanel(new BorderLayout());

        window.add(titulo, BorderLayout.NORTH);
        window.add(formulario, BorderLayout.CENTER);

        //Añado la ventana al contenedor con espacios
        gridCons = new GridBagConstraints();
        gridCons.insets = new Insets(0, 20, 10, 20);

        contenedor.setLayout(new GridBagLayout());
        contenedor.setBackground(new Color(197, 217, 237));
        contenedor.add(window, gridCons);

        //Por defecto pongo el boton de consultar
        getRootPane().setDefaultButton(consultar);
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

    /**
     * Controla el foco del codigoField.
     *
     */
    class FocoListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            codigoField.requestFocus();
        }

        @Override
        public void focusLost(FocusEvent e) {
        }
    }
}
