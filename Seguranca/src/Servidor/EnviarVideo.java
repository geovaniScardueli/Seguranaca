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
import java.io.File;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author isold
 */
public class EnviarVideo implements Runnable {

    int cont = 0;
    private final Socket cliente;
    VideoCapture webSource = null;
    Mat frame = null;
    Mat frameMovimento = null;
    MatOfByte mem = null;
    private static String clinteMensagem = "";
    private static int valor = 1;
    Mat outerBox = null;
    Mat diff_frame = null;
    Mat tempon_frame = null;
    BufferedImage imagem;
    int i;
    boolean ativacao = true;

    public EnviarVideo(Socket cliente) {
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
            new Thread(new Receber()).start();
            if (webSource.grab()) {
                while (true) {
                    webSource.retrieve(frame);
                    Highgui.imencode(".bmp", frame, mem);
                    Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));

                    BufferedImage buff = (BufferedImage) im;
                    if ("Gravar".equals(clinteMensagem)) {
                        File outputfile = new File("D:\\temp\\MessgeScreenList_" + valor + ".jpg");
                        ImageIO.write(buff, "jpg", outputfile);
                        valor++;
                    } else if ("Movimento".equals(clinteMensagem)) {
                        if (ativacao) {
                            i = 0;
                            verificarMovimento();
                            ativacao = false;
                            i = 1;
                        } else if (verificarMovimento()) {
                            File outputfile = new File("D:\\temp\\MessgeScreenList_" + valor + ".jpg");
                            ImageIO.write(buff, "jpg", outputfile);
                            valor++;
                        }
//                        new Thread(new Movimento()).start();
                    }
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

    public boolean verificarMovimento() {
        try {
            outerBox = new Mat();
            if (webSource.read(frame)) {
                outerBox = new Mat(frame.size(), CvType.CV_8UC1);

                if (i == 0) {
                    diff_frame = new Mat(outerBox.size(), CvType.CV_8UC1);
                    tempon_frame = new Mat(outerBox.size(), CvType.CV_8UC1);
                    diff_frame = outerBox.clone();
                }

                if (i == 1) {
                    Core.subtract(outerBox, tempon_frame, diff_frame);
                    Imgproc.adaptiveThreshold(diff_frame, diff_frame, 255,
                            Imgproc.ADAPTIVE_THRESH_MEAN_C,
                            Imgproc.THRESH_BINARY_INV, 5, 2);
                    Mat v = new Mat();
                    Mat vv = diff_frame.clone();
                    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                    Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                    double maxArea = 190;
                    int maxAreaIdx;
                    Rect r;
                    ArrayList<Rect> rect_array = new ArrayList<Rect>();
                    for (int idx = 0; idx < contours.size(); idx++) {
                        Mat contour = contours.get(idx);
                        double contourarea = Imgproc.contourArea(contour);
                        if (contourarea > maxArea) {
                            maxArea = contourarea;
                            maxAreaIdx = idx;
                            r = Imgproc.boundingRect(contours.get(maxAreaIdx));
                            rect_array.add(r);
                        }

                    }
                    System.out.println(rect_array.size());
                    tempon_frame = outerBox.clone();
                    if (rect_array.size() > 1) {
                        return true;
                    } else {
                        return false;
                    }
                }
                i = 1;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro: " + ex);
        }
        return false;
    }

    class Receber implements Runnable {

        @Override
        public void run() {
            try {
                ObjectInputStream veioDoCliente = new ObjectInputStream(cliente.getInputStream());
                while (true) {
                    clinteMensagem = veioDoCliente.readObject().toString();
                    if ("Gravar".equals(clinteMensagem) || "Movimento".equals(clinteMensagem)) {
                        valor = 1;
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Deu paude de " + ex);
            }
        }
    }

    class Movimento implements Runnable {

        @Override
        public void run() {
            try {
                int i = 0;
                outerBox = new Mat();
                while (true) {
                    if ("Mostrar".equals(clinteMensagem)) {
                        System.out.println("Parar movimento");
                        break;
                    }
                    if (webSource.read(frame)) {
                        outerBox = new Mat(frame.size(), CvType.CV_8UC1);

                        if (i == 0) {
                            diff_frame = new Mat(outerBox.size(), CvType.CV_8UC1);
                            tempon_frame = new Mat(outerBox.size(), CvType.CV_8UC1);
                            diff_frame = outerBox.clone();
                        }

                        if (i == 1) {
                            Core.subtract(outerBox, tempon_frame, diff_frame);
                            Imgproc.adaptiveThreshold(diff_frame, diff_frame, 255,
                                    Imgproc.ADAPTIVE_THRESH_MEAN_C,
                                    Imgproc.THRESH_BINARY_INV, 5, 2);
                            Mat v = new Mat();
                            Mat vv = diff_frame.clone();
                            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                            Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                            double maxArea = 100;
                            int maxAreaIdx;
                            Rect r;
                            ArrayList<Rect> rect_array = new ArrayList<Rect>();
                            for (int idx = 0; idx < contours.size(); idx++) {
                                Mat contour = contours.get(idx);
                                double contourarea = Imgproc.contourArea(contour);
                                if (contourarea > maxArea) {
                                    maxArea = contourarea;
                                    maxAreaIdx = idx;
                                    r = Imgproc.boundingRect(contours.get(maxAreaIdx));
                                    rect_array.add(r);
                                }
                            }
                            if (rect_array.size() > 0) {
                                Highgui.imencode(".bmp", frame, mem);
                                Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));

                                BufferedImage buff = (BufferedImage) im;
                                System.out.println("movimento");
                                File outputfile = new File("D:\\temp\\MessgeScreenList_" + valor + ".jpg");
                                if (buff != null) {
                                    ImageIO.write(buff, "jpg", outputfile);
                                    valor++;
                                }
                            }
                        }
                        i = 1;
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro Movimento: " + ex);
            }
        }
    }
}
