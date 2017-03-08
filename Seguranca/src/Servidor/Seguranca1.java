/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

/**
 *
 * @author isold
 */
public class Seguranca1 implements Runnable {

    int cont = 0;
    private final Socket cliente;
    VideoCapture webSource = null;
    Mat frame = null;
    MatOfByte mem = null;

    public Seguranca1(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            DataOutputStream outputStream = new DataOutputStream(cliente.getOutputStream());
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            webSource = new VideoCapture(0);
            frame = new Mat();
            mem = new MatOfByte();
            if (webSource.grab()) {
                while (true) {
                    webSource.retrieve(frame);
                    Highgui.imencode(".bmp", frame, mem);
                    Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));

                    BufferedImage buff = (BufferedImage) im;
                    //limpar memoria para evitar estouro de memória
                    frame.release();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(buff, "jpg", byteArrayOutputStream);

                    byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                    
                    outputStream.write(size);
                    outputStream.write(byteArrayOutputStream.toByteArray());
                    outputStream.flush();
                }
            }
            webSource.release();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Deu paude de " + ex);
        }
    }
}
