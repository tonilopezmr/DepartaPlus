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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import modelo.departamento.DepartamentoVo;
import modelo.empleado.Director;
import modelo.empleado.EmpleadoVo;
import vista.Vista;

/**
 * Vista que tiene dos funcionalidades:
 * 
 *      Modificar empleados.
 *      Consultar empleados.
 * 
 * Segun su funcionalidad va a tener una distribucion distinta.
 *
 * @author Antonio López Marín
 */
public class ModificarConsultaEmple extends JInternalFrame implements Observer, Vista {

    //Controlador
    private ControladorEmple controller;
    //paneles
    private JPanel panelBusqueda, panelModificar, panelConsultar;
    private JPanel window, contenedorDatos;
    //Componentes del panelBusqueda
    private JTextField busNameField;
    private JLabel busNameLabel, busDepartLabel;
    private JComboBox<DepartamentoVo> busDepartLista;
    //Tabla donde se ven los empleados
    private JTable tabla;
    //Modelo de la tabla, filas, columnas y restricciones
    TablaModel modeloTabla;
    //Restricciones
    private GridBagConstraints gridCons;
    //Componentes del panel modificar
    private JTextField modNombreField, modSalarioField, modComisionField;
    private JTextField messageLabel;
    private JLabel modNombreLabel, modDepartLabel, modOficioLabel, 
            modDirectorLabel,modSalarioLabel, modComisionLabel;
    private JComboBox<DepartamentoVo> departamentos;
    private JComboBox<Director> directores;
    private JComboBox<String> oficios;
    //Componentes del panel consultar
    private JTextField conNombreField, conSalarioField, conComisionField, 
            conCodigoField;
    private JTextField conDirectorField, conOficioField, conDepartField, 
            conFechaAltaField;
    private JLabel conNombreLabel, conDepartLabel, conOficioLabel, 
            conDirectorLabel,
            conSalarioLabel, conComisionLabel, conCodigoLabel, conFechaAltaLabel;
    //Acion que realiza la vista
    private String accion;
    //Guarda el codigo del empleado seleccionado
    private int codigo;
    //Boton que realiza la accion
    private JButton btonModificar;
    //Fuente de la letra
    private Font fuente;
    //Columnas de la tabla
    final private int COD = 0;
    final private int NOM = 1;
    final private int DEP = 2;
    //Numero de columnas de la tabla
    final private int NUMCOL = 3;
    
    /**
     * Funciones de empleado.
     *
     * Lo ideal seria que fueran identificadores numericos.
     */
    final public static String MODIFICAR = "Modificar";
    final public static String CONSULTAR = "Consultar";

    public ModificarConsultaEmple(String accion, ControladorEmple controller) {
        this.controller = controller;
        this.accion = accion;
        setTitle(accion + " empleado");
        loadWindow();
    }

    /**
     * Carga el internalFrame
     */
    private void loadWindow() {
        //Para cuando cierran la ventana, no sea visible
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        //Lo que puede realizar la ventana
        setResizable(false);
        setClosable(true);
        setIconifiable(true);


        //Instancio la fuente de los controles
        fuente = new Font("Arial", Font.PLAIN, 15);

        //El contenedor de datos
        contenedorDatos = new JPanel(new BorderLayout(13, 13));
        contenedorDatos.add(new JLabel(), BorderLayout.EAST);

        //Cargo los controles de la busqueda, siempre debe de cargarse     
        loadBusqueda();

        //Si la accion es modificar que cargue los controles de modificar
        //sino que cargue los controles de consulta
        if (accion.equals(MODIFICAR)) {
            loadModificar();
        } else {
            loadConsulta();
        }

        //Cargo el panel de la ventana, 
        window = new JPanel(new BorderLayout(20, 20));

        //Titulo que se muestra en el internal frame
        JLabel accionLabel = new JLabel(accion + " empleado");
        accionLabel.setFont(new Font("Arial", Font.BOLD, 27));
        accionLabel.setHorizontalTextPosition(JLabel.CENTER);

        JPanel titulo = new JPanel(new FlowLayout());
        titulo.add(accionLabel);
        titulo.setBackground(new Color(197, 217, 237));


        window.add(titulo, BorderLayout.NORTH);
        window.add(contenedorDatos, BorderLayout.CENTER);

        //Le pongo el gridBaglayout al internalFrame
        setLayout(new GridBagLayout());
        //Añado la ventana al contenedor con espacios
        gridCons = new GridBagConstraints();
        gridCons.insets = new Insets(0, 15, 10, 15);

        //Pongo el color de fondo del contenedor y le pongo la vetana en el centro 
        getContentPane().setBackground(new Color(197, 217, 237));
        getContentPane().add(window, gridCons);

        //Por ultimo actualizo las listas
        this.update(null, null);
    }

    /**
     * Carga los componentes de busqueda
     */
    private void loadBusqueda() {
        /*
         * Cargo los controles del panel de busqueda
         */
        //Etiquetas
        busDepartLabel = new JLabel("Departamento");
        busNameLabel = new JLabel("Nombre");

        //Campo
        busNameField = new JTextField(10);
        busNameField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                buscarEmpleados();
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        //Lista
        busDepartLista = new JComboBox<>();
        busDepartLista.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    buscarEmpleados();
                    limpiarDatos();
                }
            }
        });

        //Tabla donde apareceran los empleados
        String[] columnas = new String[NUMCOL];
        columnas[COD] = "Codigo";
        columnas[NOM] = "Nombre";
        columnas[DEP] = "Departamento";

        //Por defecto tiene cuatro lineas en blanco
        Object[][] noEmple = new Object[4][NUMCOL];

        //Instancio el modelo de la tabla con su configuracion
        modeloTabla = new TablaModel(noEmple, columnas);

        //Tabla swing para mostrar los datos
        Dimension dimenTabla = new Dimension(500, 88);
        tabla = new JTable(modeloTabla);
        //Para que no se puedan mover los campos de sistio
        tabla.getTableHeader().setReorderingAllowed(false);
        //Para que no se pueda seleccionar mas de una fila
        tabla.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tabla.rowAtPoint(e.getPoint());
                int column = tabla.columnAtPoint(e.getPoint());
                if (row >= 0 && column >= 0) {
                    Object codigo = modeloTabla.getValueAt(row, COD);
                    if (codigo != null) {
                        cargarDatosEmple((int) codigo);
                    }
                }
            }
        });

        //para hacer scroll si hay muchos empleados
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setSize(dimenTabla);
        scrollTabla.setPreferredSize(dimenTabla);

        //Instancio el panel de busqueda, y las restricciones del gridbag
        panelBusqueda = new JPanel(new GridBagLayout());
        gridCons = new GridBagConstraints();
        gridCons.insets = new Insets(10, 20, 10, 20);

        //Añado los componentes
        addGridBag(busNameLabel, 0, 0);
        addGridBag(busNameField, 1, 0);
        addGridBag(busDepartLabel, 2, 0);
        addGridBag(busDepartLista, 3, 0);

        gridCons.fill = GridBagConstraints.BOTH;
        gridCons.gridwidth = 4;
        gridCons.gridheight = 4;
        addGridBag(scrollTabla, 0, 1);

        //Añadir al contentPane
        contenedorDatos.add(panelBusqueda, BorderLayout.NORTH);
    }

    /**
     * Carga la parte de la vsita cuando es consultar
     */
    private void loadConsulta() {
        //Ajusto su tamaño
        Dimension dimen = new Dimension(600, 410);
        setSize(dimen);
        setPreferredSize(dimen);
        setMinimumSize(dimen);
        setMaximumSize(dimen);
        
        //Campos
        conCodigoField = new JTextField(10);
        conCodigoField.setEditable(false);
        conCodigoField.setFont(fuente);
        
        conFechaAltaField = new JTextField(10);
        conFechaAltaField.setEditable(false);
        conFechaAltaField.setFont(fuente);
        
        conDepartField = new JTextField(10);
        conDepartField.setEditable(false);
        conDepartField.setFont(fuente);

        conDirectorField = new JTextField(10);
        conDirectorField.setEditable(false);
        conDirectorField.setFont(fuente);

        conOficioField = new JTextField(10);
        conOficioField.setEditable(false);
        conOficioField.setFont(fuente);

        conComisionField = new JTextField(10);
        conComisionField.setEditable(false);
        conComisionField.setFont(fuente);

        conNombreField = new JTextField(10);
        conNombreField.setEditable(false);
        conNombreField.setFont(fuente);

        conSalarioField = new JTextField(10);
        conSalarioField.setEditable(false);
        conSalarioField.setFont(fuente);

        //Etiquetas modificar
        conCodigoLabel = new JLabel("        Codigo");
        conCodigoLabel.setFont(fuente);
        
        conFechaAltaLabel = new JLabel("    Fecha de Alta");
        conFechaAltaLabel.setFont(fuente);
        
        conNombreLabel = new JLabel("        Nombre");
        conNombreLabel.setFont(fuente);

        conDepartLabel = new JLabel("        Departamento");
        conDepartLabel.setFont(fuente);

        conOficioLabel = new JLabel("          Oficio");
        conOficioLabel.setFont(fuente);

        conDirectorLabel = new JLabel("        Director");
        conDirectorLabel.setFont(fuente);

        conSalarioLabel = new JLabel("          Salario");
        conSalarioLabel.setFont(fuente);

        conComisionLabel = new JLabel("          Comisión");
        conComisionLabel.setFont(fuente);

        //instancio el panel de la consulta
        panelConsultar = new JPanel(new BorderLayout(10, 10));

        //Creo un panel auxiliar para poner los controles de consulta
        //para poder añadir tambien el boton por separado
        JPanel panelConsultaAUX = new JPanel(new GridLayout(4, 4, 0, 5));

        //Añado los controles
        panelConsultaAUX.add(conCodigoLabel);
        panelConsultaAUX.add(conCodigoField);
        panelConsultaAUX.add(conFechaAltaLabel);
        panelConsultaAUX.add(conFechaAltaField);
        panelConsultaAUX.add(conNombreLabel);
        panelConsultaAUX.add(conNombreField);
        panelConsultaAUX.add(conOficioLabel);
        panelConsultaAUX.add(conOficioField);
        panelConsultaAUX.add(conDirectorLabel);
        panelConsultaAUX.add(conDirectorField);
        panelConsultaAUX.add(conSalarioLabel);
        panelConsultaAUX.add(conSalarioField);
        panelConsultaAUX.add(conDepartLabel);
        panelConsultaAUX.add(conDepartField);
        panelConsultaAUX.add(conComisionLabel);
        panelConsultaAUX.add(conComisionField);

        panelConsultar.add(new JLabel(), BorderLayout.EAST);
        panelConsultar.add(panelConsultaAUX, BorderLayout.CENTER);
        panelConsultar.add(new JLabel(), BorderLayout.SOUTH);

        //Añado al panel contenedor de datos
        contenedorDatos.add(panelConsultar, BorderLayout.SOUTH);
    }

    /**
     * Carga la parte de la vista cuando es para modificar
     */
    private void loadModificar() {
        //Ajusto su tamaño
        Dimension dimen = new Dimension(640, 470);
        setSize(dimen);
        setPreferredSize(dimen);
        setMinimumSize(dimen);
        setMaximumSize(dimen);
        
        //Listas
        departamentos = new JComboBox<DepartamentoVo>();
        directores = new JComboBox<Director>();
        oficios = new JComboBox<String>();

        //Etiquetas modificar
        modNombreLabel = new JLabel("        Nombre");
        modNombreLabel.setFont(fuente);

        modDepartLabel = new JLabel("        Departamento");
        modDepartLabel.setFont(fuente);

        modOficioLabel = new JLabel("          Oficio");
        modOficioLabel.setFont(fuente);

        modDirectorLabel = new JLabel("        Director");
        modDirectorLabel.setFont(fuente);

        modSalarioLabel = new JLabel("          Salario");
        modSalarioLabel.setFont(fuente);

        modComisionLabel = new JLabel("          Comisión");
        modComisionLabel.setFont(fuente);

        //Campos modificar
        modNombreField = new JTextField(10);
        modNombreField.setFont(fuente);
        modSalarioField = new JTextField(10);
        modSalarioField.setFont(fuente);
        modComisionField = new JTextField(10);
        modComisionField.setFont(fuente);

        //Creo el panel con el mensaje y el boton
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));

        messageLabel = new JTextField(30);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 15));
        messageLabel.setHorizontalAlignment(JTextField.CENTER);
        messageLabel.setPreferredSize(new Dimension(80, 50));
        messageLabel.setEditable(false);

        //Instancio el boton de modificar
        btonModificar = new JButton(accion);
        btonModificar.setSize(new Dimension(100, 50));
        btonModificar.setPreferredSize(new Dimension(100, 50));
        btonModificar.setEnabled(false);
        btonModificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validName();
                validSalario();

                try {
                    String nombre = modNombreField.getText();
                    String oficio = (String) oficios.getSelectedItem();
                    double salario = Double.parseDouble(modSalarioField.getText());
                    Director director = (Director) directores.getSelectedItem();
                    DepartamentoVo depart =
                            (DepartamentoVo) departamentos.getSelectedItem();
                    //Creo la fecha de hoy
                    Date date = new Date();

                    //Segun el salario y el codigo del departamento cobra una 
                    //comision u otra
                    double comision = controller.calcularComision(salario,
                            depart.getCodigo());
                    modComisionField.setText(comision + "");  //Informo de la comision

                    if (controller.modificarEmpleado(codigo, nombre, oficio,
                            director.getCodigoDir(), date, salario, comision,
                            depart.getCodigo())) {
                        mostrarMensaje("** "+ nombre +" se modifico correctamente **");
                        limpiarDatos();
                        controller.nuevosCambios();
                    } else {
                        mostrarMensaje("** "+ nombre +"  no se modifico "
                                + "correctamente **");
                    }
                } catch (NumberFormatException nfe) {
                    mostrarMensaje("** Introduzca un salario correcto **");
                } catch (SQLException ex) {
                    mostrarError(ex.getMessage());
                }
            }
        });

        //Añado el boton
        south.add(messageLabel);
        south.add(btonModificar);


        //Instancio el panel de modificar
        panelModificar = new JPanel(new GridLayout(3, 4, 0, 5));

        //Añado los controles
        panelModificar.add(modNombreLabel);
        panelModificar.add(modNombreField);
        panelModificar.add(modOficioLabel);
        panelModificar.add(oficios);
        panelModificar.add(modDirectorLabel);
        panelModificar.add(directores);
        panelModificar.add(modSalarioLabel);
        panelModificar.add(modSalarioField);
        panelModificar.add(modDepartLabel);
        panelModificar.add(departamentos);
        panelModificar.add(modComisionLabel);
        panelModificar.add(modComisionField);

        //Añadir al contentPane
        contenedorDatos.add(panelModificar, BorderLayout.CENTER);
        contenedorDatos.add(south, BorderLayout.SOUTH);

        //Por defecto pongo el boton de consultar
        getRootPane().setDefaultButton(btonModificar);
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
        panelBusqueda.add(component, gridCons);
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
        if (!esCadena(modNombreField.getText())) {
            mostrarMensaje("** Introduzca un nombre valido **");
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
        if (!isNumeric(modSalarioField.getText())) {
            mostrarMensaje("** Introduzca un salario correcto **");
            modComisionField.setText("");
        } else {
            double salario = Double.parseDouble(modSalarioField.getText());

            messageLabel.setText("");
            DepartamentoVo depart =
                    (DepartamentoVo) departamentos.getSelectedItem();
            //Muestro la comision que seria con ese salario segun el codigo
            modComisionField.setText(controller.calcularComision(salario,
                    depart.getCodigo()) + "");

        }
    }
    
    /**
     * Carga los datos de el panel de modificar.
     * 
     * @param emple 
     */
    public void cargarDatosModificar(EmpleadoVo emple) {
        modNombreField.setText(emple.getNombre());
        modSalarioField.setText(emple.getSalario() + "");
        modComisionField.setText(emple.getComision() + "");
        try {
            oficios.setSelectedItem(emple.getOficio());
        } catch (Exception e) {
        }
        try {
            DepartamentoVo dep = controller.getDepartamento(emple.getCodigoDepart());
            for (int i = 0; i < departamentos.getItemCount(); i++) {
                DepartamentoVo depart = departamentos.getItemAt(i);
                if (dep.getCodigo() == depart.getCodigo()) {
                    departamentos.setSelectedIndex(i);
                }
            }
        } catch (Exception e) {
        }
        try {
            for (int i = 0; i < directores.getItemCount(); i++) {
                Director direc = directores.getItemAt(i);
                if (emple.getDirector() == direc.getCodigoDir()) {
                    directores.setSelectedIndex(i);
                    break;
                }
                directores.setSelectedIndex(0);
            }
        } catch (Exception e) {
            System.out.println("asdf");
        }
        //Se puede modificar
        btonModificar.setEnabled(true);
    }
    
    /**
     * Carga los datos del panel de la consulta.
     * 
     * @param emple 
     */
    public void cargarDatosConsulta(EmpleadoVo emple) {
        try {
            conCodigoField.setText(emple.getCodigo()+"");
            conFechaAltaField.setText(emple.getFechaAlta()+"");
            conNombreField.setText(emple.getNombre());
            conSalarioField.setText(emple.getSalario() + "");
            conComisionField.setText(emple.getComision() + "");
            conOficioField.setText(emple.getOficio());
            conDepartField.setText(controller.getDepartamento(
                    emple.getCodigoDepart()).getNombre());
            Director dir = controller.getDirector(emple.getDirector());
            if (dir != null) {
                conDirectorField.setText(dir.getNombre());
            } else {
                conDirectorField.setText("NO DIRECTOR");
            }
        } catch (SQLException ex) {
            conDirectorField.setText("NONE");
        }
    }
    
    /**
     * Carga los datos de empleado.
     * 
     * @param codigo 
     */
    public void cargarDatosEmple(int codigo) {
        try {
            EmpleadoVo emple = controller.buscarEmpleado(codigo);
            this.codigo = codigo;
            if (accion.equals(MODIFICAR)) {
                cargarDatosModificar(emple);
            } else {
                cargarDatosConsulta(emple);
            }
        } catch (SQLException e) {
            mostrarMensaje(e.getMessage());
        }
    }
    
    /**
     * Limpia los datos segun la funcionalidad de la ventana.
     * 
     */
    public void limpiarDatos() {
        if (accion.equals(MODIFICAR)) {
            limpiarModificacion();
        } else {
            limpiarConsulta();
        }
        //Cuando sea distinto de null, se desactivara cada vez que se limpie
        if (btonModificar != null) {
            btonModificar.setEnabled(false);
        }
    }
        
    /**
     * Limpia los componentes del panel de modificar.
     * 
     */
    public void limpiarModificacion(){
        modNombreField.setText("");
        modSalarioField.setText("");
        modComisionField.setText("");
        messageLabel.setText("");
    }
    
    /**
     * Limpia los datos de los comopnentes del panel de consultar.
     * 
     */
    public void limpiarConsulta() {
        conCodigoField.setText("");
        conFechaAltaField.setText("");
        conNombreField.setText("");
        conSalarioField.setText("");
        conComisionField.setText("");
        conOficioField.setText("");
        conDepartField.setText("");
        conDirectorField.setText("");
    }
    
    /**
     * Busca los empleados
     * 
     */
    public void buscarEmpleados() {
        DepartamentoVo dep = (DepartamentoVo) busDepartLista.getSelectedItem();
        setRegistrosTabla(busNameField.getText(), dep.getCodigo());
    }
    
    /**
     * Añade los registros a la tabla.
     * 
     * @param empleados 
     */
    public void addRegistros(EmpleadoVo[] empleados) {
        modeloTabla.setRowCount(0);

        for (int i = 0; i < empleados.length; i++) {
            //Un empleado
            EmpleadoVo emp = empleados[i];
            //Un array con sus atributos
            Object[] empleado = new Object[NUMCOL];
            empleado[COD] = emp.getCodigo();
            empleado[NOM] = emp.getNombre();
            empleado[DEP] = emp.getCodigoDepart();
            modeloTabla.addRow(empleado);
        }

        //Cuatro filas vacias
        if (empleados.length == 0) {
            limpiarDatos();
            String[] empty = new String[NUMCOL];
            modeloTabla.addRow(empty);
            modeloTabla.addRow(empty);
            modeloTabla.addRow(empty);
            modeloTabla.addRow(empty);
        }
    }
    
    /**
     * Cambia los registros de la tabla segun el departamento y el nombre
     * de un empleado.
     * 
     * @param nombreEmple
     * @param codDepart 
     */
    public void setRegistrosTabla(String nombreEmple, int codDepart) {
        try {
            //Si es igual a -1 es el departamento definido por mi mismo
            if (codDepart != -1) {
                //Busca empleados segun su departamento y nombre
                EmpleadoVo[] empleados = controller.buscarEmpleadosDepart(nombreEmple, codDepart);

                addRegistros(empleados);
            } else {
                //Busca empleados segun su nombre
                EmpleadoVo[] empleados = controller.buscarEmpleados(nombreEmple);

                addRegistros(empleados);
            }
        } catch (SQLException e) {
            mostrarError("** Error de la base de datos **");
        }
    }
    
    /**
     * Cambia los departamentos.
     * 
     */
    public void setDepartamentosBuscar() {
        try {
            DepartamentoVo[] departamentos = controller.listarDepartamentos();
            this.busDepartLista.removeAllItems();
            //Siempre se crea un departamento NONE que es no seleccionado ningun depart
            busDepartLista.addItem(new DepartamentoVo(-1, "NONE", null));

            for (int i = 0; i < departamentos.length; i++) {
                DepartamentoVo dep = departamentos[i];
                this.busDepartLista.addItem(dep);
            }
        } catch (SQLException ex) {
            this.busDepartLista.addItem(new DepartamentoVo(0, "NONE", null));
        }
    }

    /**
     * Cambia la lista departamentos
     * 
     */
    public void setDepartamentos() {
        try {
            DepartamentoVo[] departamentos = controller.listarDepartamentos();
            this.departamentos.removeAllItems();

            for (int i = 0; i < departamentos.length; i++) {
                DepartamentoVo dep = departamentos[i];
                this.departamentos.addItem(dep);
            }
        } catch (SQLException ex) {
            this.departamentos.addItem(new DepartamentoVo(0, "NONE", null));
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
            this.directores.addItem(new Director(0, "NO DIRECTOR"));
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
     * Metodo que se redefine del patron Observer, que es llamado cuando
     * hay cambios en la base de datos, para que se actualicen.
     * 
     * @param o
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        //Se actualizan los datos de busquedas
        setDepartamentosBuscar();
        buscarEmpleados();
        //Y si la vista es de modificar se actualiza sus componentes
        if (accion.equals(MODIFICAR)) {
            setDepartamentos();
            setOficios();
            setDirectores();
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
