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
package modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para la conexion de la base de datos Oracle mediante JDBC.
 *
 * Si se quiere cambair de base de datos con otra configuracion, hay que
 * modificar el fichero de configuracion.
 * 
 * 
 * @author Antonio López Marín
 */
public class Conexion{

    public static Connection conexion;
    
    /**
     * Constructor privado, patron singleton.
     */
    private Conexion() {
    }

    /**
     * Conecta a la base de datos.
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConexion()
            throws ClassNotFoundException, SQLException {
        Config config = new Config();
        if (conexion == null) {
            if (!config.file.exists()) {
                throw new SQLException("No encontrado el fichero base-datos.cfg "
                        + "de la base de datos.");
            }
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conexion = DriverManager.getConnection("jdbc:oracle:thin:@"
                    + config.getServer() + ":1521:"
                    + config.getName(), config.getUser(), config.getPassword());
        }
        return conexion;
    }
    
    /**
     * Enciende la base de datos.
     */
    public static void encenderDataBase() throws IOException, InterruptedException {
        String comando = "CMD /C net start OracleServiceXE";

        Process proceso = Runtime.getRuntime().exec(comando);
        
        proceso.waitFor();
    }
    
    /**
     * Apaga la base de datos.
     */
    public static void apagarDataBase() throws IOException, InterruptedException {
        String comando = "CMD /C net stop OracleServiceXE";

        Process proceso = Runtime.getRuntime().exec(comando);
        
        proceso.waitFor();
    }
    
    /**
     * Desconecta la base de datos.
     * @throws SQLException 
     */
    public static void desconectar() throws SQLException{
        conexion.close();
        conexion = null;
    }
    
    /**
     * Clase que comprueba la configuracion de la base de datos a la que se
     * quiere conectar..
     *
     */
    public static class Config {

        File file;
        Map<String, String> properties;
        private String server;
        private String name;
        private String user;
        private String password;
        final private String SERVER = "server";
        final private String NAME = "name";
        final private String USER = "user";
        final private String PASSWORD = "password";

        public Config() {
            properties = new HashMap();
            this.file = new File("base-datos.cfg");
            read();
        }

        /**
         * Lee el fichero de configuracion.
         *
         */
        private void read() {
            BufferedReader bf = null;
            try {
                bf = new BufferedReader(new FileReader(file));
                String linea;
                while ((linea = bf.readLine()) != null) {
                    propiedades(linea);
                }

                rellenar();
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            } finally {
                try {
                    if (bf != null) {
                        bf.close();
                    }
                } catch (Exception e) {
                }
            }
        }

        /**
         * Recoje los comandos de propiedades del fichero.
         *
         * @param linea
         * @throws IOException
         */
        private void propiedades(String linea) throws IOException {
            String[] propertyValue = linea.split(" +");

            if (propertyValue.length == 2) {
                properties.put(propertyValue[0], propertyValue[1].replaceAll("\"", ""));
            } else {
                throw new IOException("Configuracion corrompida.");
            }
        }

        /**
         * Recojo dichas propiedades
         */
        private void rellenar() {
            this.server = properties.get(SERVER);
            this.name = properties.get(NAME);
            this.user = properties.get(USER);
            this.password = properties.get(PASSWORD);
        }

        public String getServer() {
            return server;
        }

        public String getName() {
            return name;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
