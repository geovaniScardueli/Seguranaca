/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seguranca;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author isold
 */
public class ExibirVideo extends javax.swing.JFrame {

    /**
     * Creates new form ExibirVideo
     */
    private static int valor;
    
    public ExibirVideo() {
        initComponents();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        valor = 1;
    }

    class DaemonThread implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    BufferedImage image = ImageIO.read(new File("D:\\temp\\MessgeScreenList_"+valor+".jpg"));
                    Graphics g = jPanel1.getGraphics();
                    if (g.drawImage(image, 0, 0, getWidth(), getHeight() - 150, 0, 0, image.getWidth(), image.getHeight(), null)) {
                    }
                    valor++;
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, "fim");
            }
        }
    }

    public void ativar() {
        Thread t = new Thread(new DaemonThread());
        t.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setPreferredSize(new java.awt.Dimension(640, 480));

        jPanel1.setPreferredSize(new java.awt.Dimension(640, 480));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 703, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 502, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        jPanel1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
