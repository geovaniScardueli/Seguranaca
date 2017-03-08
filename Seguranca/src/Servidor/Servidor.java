/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author isolda
 */
public class Servidor {

    public static void main(String[] args) {
        try { // Instancia o ServerSocket ouvindo a porta 12345 

            ServerSocket servidor = new ServerSocket(12345);
            while (true) {
                System.out.println("Servidor ouvindo a porta 12345");
                Socket cliente = servidor.accept();
                new Thread(new EnviarVideo(cliente)).start();
            }
        } catch (Exception e) {
           System.out.println("Erro servidor: " + e.getMessage());
        }
    }
}
