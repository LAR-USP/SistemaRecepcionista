package br.usp.icmc.lar.lara.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * @brief Recebe comandos AJAX para executa a varredura
 * do Kinect.
 * @author tarcisio
 */
public class Detect extends HttpServlet {
    /**
     * @brief Recebe um envio GET do AJAX em search.js.
     * @param request Conteúdo da mensagem.
     * @param response Resposta a ser enviada de volta ao script.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        doPost(request, response);
    }

    /**
     * @brief Recebe um envio POST do AJAX em search.js.
     * @param request Conteúdo da mensagem.
     * @param response Resposta a ser enviada de volta ao script.
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        // Create a face detector from the cascade file in the resources
        // directory.
        CascadeClassifier faceDetector = new CascadeClassifier(getClass().getResource("/lbpcascade_frontalface.xml").getPath());
        VideoCapture video = new VideoCapture(0);
        Mat image = new Mat();
        video.read( image );
        video.release();

        // Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        RectVector faceDetections = new RectVector();
        faceDetector.detectMultiScale( image, faceDetections);

        // Draw a bounding box around each face.
        for ( long i = 0; i < faceDetections.size(); i++ ) {
            Rect rect = faceDetections.get( i );
            rectangle( image,
                    new Point( rect.x(), rect.y() ),
                    new Point( rect.x() + rect.width(), rect.y() + rect.height() ),
                    new Scalar( 0, 255, 0, 1 ) );
        }

        // PrintWriter out = response.getWriter();
        // out.println(String.format("%s faces detectadas", faceDetections.toArray().length));
    }
}
