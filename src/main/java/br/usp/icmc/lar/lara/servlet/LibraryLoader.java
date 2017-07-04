package br.usp.icmc.lar.lara.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.bytedeco.javacpp.Loader;


/**
 *
 * @author tarcisio
 */
public class LibraryLoader implements ServletContextListener{

    static { Loader.load(); }

    @Override
    public void contextInitialized(ServletContextEvent context) {
        //final String LIBRARY_FILE = "lib"+Core.NATIVE_LIBRARY_NAME+".so";
        //System.load( getClass().getResource("/"+LIBRARY_FILE).getPath() );
    }

    @Override
    public void contextDestroyed(ServletContextEvent context) { 
        
    }

}
