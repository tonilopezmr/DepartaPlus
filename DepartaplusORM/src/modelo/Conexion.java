/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.IOException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 *
 * @author alumno
 */
public class Conexion {

    private static final SessionFactory sessionFactory;
    
    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static Session getSession(){
        return sessionFactory.openSession();
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
}
