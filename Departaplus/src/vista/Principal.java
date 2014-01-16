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
package vista;

import controlador.Controlador;
import vista.departamentos.ConsultarDepart;
import vista.departamentos.UpdateDepart;
import controlador.ControladorDepart;
import controlador.ControladorEmple;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import vista.empleados.AltaEmpleado;
import vista.empleados.BajaEmpleado;
import vista.empleados.ModificarConsultaEmple;

/**
 * Vista principal, que contiene los internal frames que tiene una barra de menu
 * para interaccionar con el usuario y diferentes controles.
 *
 * @author Antonio López Marín
 */
public class Principal extends JFrame implements Vista {

    /**
     * Controladores
     */
    private ControladorDepart departController;
    private ControladorEmple empleController;
    private Controlador controlador;
    //Escritorio donde van los internalFrame
    private JDesktopPane desktop;
    /*
     * Departamentos
     */
    private ConsultarDepart consultaDepart;
    private UpdateDepart altaDepart;
    private UpdateDepart bajaDepart;
    private UpdateDepart modificarDepart;
    /*
     * Empleados
     */
    private AltaEmpleado altaEmple;
    private BajaEmpleado bajaEmple;
    private ModificarConsultaEmple modificarEmple;
    private ModificarConsultaEmple consultarEmple;
    /**
     * Menu items de Archivo
     */
    private AccionConectarse actionConnect;
    private JMenuItem iniciar;
    private JMenuItem cerrar;
    private JMenuItem conectar;
    private JMenuItem desconectar;
    /**
     * La barra de progreso, que aparece para indicar que esta cargando
     * cualquier cosa.
     */
    private JProgressBar progressBar;

    /**
     * HACER UN FOOTER, QUE PONGA MI NOMBRE Y DONDE APARECERA UN PROGRESS BAR
     * CUANDO CARGUE LA BASE DE DATOS.
     *
     */
    public Principal() {
        loadWindow();
    }

    /**
     * Hacer o no visible la barra de progreso.
     *
     * @param visible
     */
    private void visibleProgress(boolean visible) {
        progressBar.setVisible(visible);
    }

    /**
     * Metodo que carga la ventana, poniendo todos sus controles.
     *
     */
    private void loadWindow() {
        setTitle("DepartaPlus+");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.height -= 50;
        Dimension dimen = new Dimension(630, 380);
        setSize(screenSize);
        setPreferredSize(screenSize);
        setMinimumSize(new Dimension(700, 650));
        setResizable(true);

        //Deja cargar la ventana 3 segundos
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
                visibleProgress(false);
            }
        });
        th.start();

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel(), BorderLayout.NORTH);
        panel.add(new JLabel(), BorderLayout.SOUTH);
        panel.add(new JLabel("        Antonio López Marín"), BorderLayout.WEST);
        panel.add(progressBar, BorderLayout.EAST);

        //Creo el escritorio 
        desktop = new JDesktopPane();
        //Pongo un border layout al contentpane
        setLayout(new BorderLayout());
        //Y le añado los componentes
        getContentPane().add(panel, BorderLayout.SOUTH);
        getContentPane().add(desktop, BorderLayout.CENTER);

        //Le pongo de fondo un gris sexy
        desktop.setBackground(Color.LIGHT_GRAY);

        // Make dragging faster by setting drag mode to Outline
        desktop.putClientProperty("JDesktopPane.dragMode", "outline");

        //Pongo la barra de menu
        menuBar();

        //Inicio los internal frames o ventanas secundarias.
        startInternalFrames();
        
        //Por ultimo instancio el controlador que abarca los otros dos
        //controladores, para conectar desconectar etc..
        controlador = new Controlador(departController, empleController);
    }
        
    /**
     * Inicia los internalframes
     */
    private void startInternalFrames(){
        //Cargo los internal frames, y si ocurre un error, no dejo que los abran
        try {
             loadInternalFrames();
        } catch (ClassNotFoundException | SQLException ex) {
            mostrarError("¡Error de conexion de base de datos!");
            System.out.println(ex.getMessage());
            //Cierro los siguientes controles..
            actionConnect.setEnabled(false);
            iniciar.setEnabled(true);
            cerrar.setEnabled(false);
            conectar.setEnabled(false);
            desconectar.setEnabled(false);
        }
    }
        
    /**
     * Carga los internal frames.
     * 
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    private void loadInternalFrames() throws ClassNotFoundException, SQLException {
        
        //Instancio los dos controladores lo primero
        departController = new ControladorDepart(this);
        empleController = new ControladorEmple(this, departController);
        
        //Si todo funciona bien, carga las interfaces.

        //////////////////////
        //////Departamentos
        //////////////////////

        //Dos ventanas para alta y baja
        altaDepart = new UpdateDepart(UpdateDepart.ALTA, departController);
        altaDepart.setLocation(10, 10);
        bajaDepart = new UpdateDepart(UpdateDepart.BAJA, departController);
        bajaDepart.setLocation(30, 30);

        //Modificar 
        modificarDepart = new UpdateDepart(UpdateDepart.MODIFICAR, departController);
        modificarDepart.setLocation(50, 50);

        //Consultar departamento
        consultaDepart = new ConsultarDepart(departController);
        consultaDepart.setLocation(70, 70);

        //Añado los observables al observer
        departController.addObserver(consultaDepart);

        //Añado al escritorio las ventanas de departamentos
        desktop.add(consultaDepart);
        desktop.add(altaDepart);
        desktop.add(bajaDepart);
        desktop.add(modificarDepart);

        //////////////////////
        //////Empleados
        //////////////////////

        //Alta y baja
        altaEmple = new AltaEmpleado(empleController);
        altaEmple.setLocation(200, 10);
        bajaEmple = new BajaEmpleado(empleController);
        bajaEmple.setLocation(230, 30);

        //Modificar
        modificarEmple = new ModificarConsultaEmple(
                ModificarConsultaEmple.MODIFICAR, empleController);
        modificarEmple.setLocation(260, 50);

        //Consultar
        consultarEmple = new ModificarConsultaEmple(
                ModificarConsultaEmple.CONSULTAR, empleController);
        consultarEmple.setLocation(300, 70);

        //Añado los observables al controlador empleado
        empleController.addObserver(altaEmple);
        empleController.addObserver(bajaEmple);
        empleController.addObserver(modificarEmple);
        empleController.addObserver(consultarEmple);

        //Añado al controlador de departamentos, los de empleado para
        //Cuando cambie un departamento se cambie en todas las demas vistas
        departController.addObserver(altaEmple);
        departController.addObserver(bajaEmple);
        departController.addObserver(modificarEmple);
        departController.addObserver(consultarEmple);

        //Añado al escritorio las nuevas ventanas de empleado
        desktop.add(altaEmple);
        desktop.add(bajaEmple);
        desktop.add(modificarEmple);
        desktop.add(consultarEmple);
        desktop.add(new JLabel());
    }

    /**
     * Barra de menu.
     *
     */
    private void menuBar() {
        //La barra de menu
        JMenuBar menuBar = new JMenuBar();
        actionConnect = new AccionConectarse();

        /**
         * EN ARCHIVO PODER, CONECTAR Y DESCONECTAR LA BASE DE DATOS.
         *
         */
        //Menu Archivo
        JMenu archivo = new JMenu("Archivo");

        /**
         * Hacer un progres bar de esos.
         */
        iniciar = new JMenuItem("Iniciar DB Oracle");
        iniciar.setEnabled(false);  //Por defecto la base de datos esta encendida
        iniciar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
                ActionEvent.CTRL_MASK));
        iniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            visibleProgress(true);
                            controlador.encender();
                            startInternalFrames();
                           actionConnect.setEnabled(true);
                            visibleProgress(false);
                        } catch (InterruptedException ew) {
                        } catch (IOException ex) {
                            mostrarError("Error al iniciar la base de datos");
                        }
                    }
                });
                thread.start();
                iniciar.setEnabled(false);
                cerrar.setEnabled(true);
                desconectar.setEnabled(true);
            }
        });

        cerrar = new JMenuItem("Apagar DB Oracle");
        cerrar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
                ActionEvent.CTRL_MASK));
        cerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            visibleProgress(true);
                            controlador.apagar();
                            visibleProgress(false);
                        } catch (InterruptedException ew) {
                        } catch (IOException ex) {
                            mostrarError("Error al iniciar la base de datos");
                        }
                    }
                });
                thread.start();
                actionConnect.setEnabled(false);
                cerrar.setEnabled(false);
                conectar.setEnabled(false);
                desconectar.setEnabled(false);
            }
        });

        /**
         * Conecatr con dialogo.
         */
        conectar = new JMenuItem("Conectar DB Oracle");
        conectar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,
                ActionEvent.CTRL_MASK));
        conectar.setEnabled(false);  //Por defecto esta conectada
        conectar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controlador.conectarse();
                    actionConnect.setEnabled(true);
                    conectar.setEnabled(false);
                    desconectar.setEnabled(true);
                } catch (ClassNotFoundException | SQLException ex) {
                }
            }
        });

        desconectar = new JMenuItem("Desconectar Oracle");
        desconectar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4,
                ActionEvent.CTRL_MASK));
        desconectar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controlador.desconectarse();
                    actionConnect.setEnabled(false);
                    desconectar.setEnabled(false);
                    conectar.setEnabled(true);
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        JMenuItem salir = new JMenuItem("Salir");
        salir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.CTRL_MASK));
        salir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //añado los items al menu archivo
        archivo.add(iniciar);
        archivo.add(cerrar);
        archivo.addSeparator();
        archivo.add(conectar);
        archivo.add(desconectar);
        archivo.addSeparator();
        archivo.add(salir);

        //Menu departamentos
        JMenu departamentos = new JMenu("Departamentos");

        //Items
        JMenuItem altaDep = new JMenuItem(actionConnect);
        altaDep.setText("Crear Nuevo...");
        altaDep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                altaDepart.setVisible(true);
                try {
                    altaDepart.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
            }
        });

        JMenuItem bajaDep = new JMenuItem(actionConnect);
        bajaDep.setText("Dar de Baja");
        bajaDep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bajaDepart.setVisible(true);
                try {
                    bajaDepart.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
            }
        });

        JMenuItem modificarDep = new JMenuItem(actionConnect);
        modificarDep.setText("Modificar");
        modificarDep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarDepart.setVisible(true);
                try {
                    modificarDepart.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
            }
        });
        JMenuItem consultarDep = new JMenuItem(actionConnect);
        consultarDep.setText("Consultar");
        consultarDep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consultaDepart.setVisible(true);
                try {
                    consultaDepart.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
            }
        });

        //Añado al menu departamentos
        departamentos.add(altaDep);
        departamentos.add(bajaDep);
        departamentos.add(modificarDep);
        departamentos.add(consultarDep);

        //Menu empleados
        JMenu empleados = new JMenu("Empleados");

        //items
        JMenuItem itemAltaEmple = new JMenuItem(actionConnect);
        itemAltaEmple.setText("Crear Nuevo...");
        itemAltaEmple.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                altaEmple.setVisible(true);
                try {
                    altaEmple.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
            }
        });

        JMenuItem itemBajaEmple = new JMenuItem(actionConnect);
        itemBajaEmple.setText("Dar de Baja");
        itemBajaEmple.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bajaEmple.setVisible(true);
                try {
                    bajaEmple.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
            }
        });

        JMenuItem itemModificarEmple = new JMenuItem(actionConnect);
        itemModificarEmple.setText("Modificar");
        itemModificarEmple.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarEmple.setVisible(true);
                try {
                    modificarEmple.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
            }
        });
        JMenuItem itemConsultarEmple = new JMenuItem(actionConnect);
        itemConsultarEmple.setText("Consultar");
        itemConsultarEmple.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consultarEmple.setVisible(true);
                try {
                    consultarEmple.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
            }
        });
        //Añado al menu empleados
        empleados.add(itemAltaEmple);
        empleados.add(itemBajaEmple);
        empleados.add(itemModificarEmple);
        empleados.add(itemConsultarEmple);

        //Menu Ayuda
        JMenu ayuda = new JMenu("Ayuda");

        //Puede mostrar la info con el metodo mostrarMensaje
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(desktop,
                        "Departaplus+ 1.0.1\n\n Creado por \tAntonio López Marín",
                        "Departaplus+ 1.0.1", JOptionPane.OK_OPTION);
            }
        });

        //Añado los items de ayuda
        ayuda.add(about);

        //Añado a la barra de menu los menus
        menuBar.add(archivo);
        menuBar.add(departamentos);
        menuBar.add(empleados);
        menuBar.add(ayuda);

        //Añado el menu al frame
        setJMenuBar(menuBar);
    }

    /**
     * Implementacion de vista, lo dejo vacio ya que es una ventana, principal
     * sin controles.
     *
     * @param mensaje
     */
    @Override
    public void mostrarMensaje(String mensaje) {
    }

    @Override
    public void mostrarError(String error) {
        //DIALOG 
        JOptionPane.showMessageDialog(null, error, "¡ERROR!",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Inner class que extiende de AbstractAction para hacer una accion, y
     * añadir las operaciones de alta y baja de empleados y departamentos para
     * controlar el menuBar de una vez.
     *
     */
    class AccionConectarse extends AbstractAction {

        public AccionConectarse() {
            super();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }
}
