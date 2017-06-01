package usp.lar.lara.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.opencv.core.Core;

/**
 *
 * @author tarcisio
 */
public class LibraryLoader implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent context) {
        final String LIBRARY_FILE = "lib"+Core.NATIVE_LIBRARY_NAME+".so";
        System.load( getClass().getResource("/"+LIBRARY_FILE).getPath() );
    }

    @Override
    public void contextDestroyed(ServletContextEvent context) { 
        
    }

}
