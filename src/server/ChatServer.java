package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORTA = 5556;
    private static Set<PrintWriter> clientes = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket servidorSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor iniciado na porta " + PORTA);
            while (true) {
                Socket clienteSocket = servidorSocket.accept();
                System.out.println("Novo cliente conectado: " + clienteSocket);
                new Thread(new ManipuladorCliente(clienteSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    private static class ManipuladorCliente implements Runnable {
        private Socket socketCliente;
        private PrintWriter out;

        public ManipuladorCliente(Socket socket) {
            this.socketCliente = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socketCliente.getOutputStream(), true);
                clientes.add(out);
                BufferedReader in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));

                String mensagem;
                while ((mensagem = in.readLine()) != null) {
                    System.out.println("Recebido de " + socketCliente + ": " + mensagem);
                    transmitir(mensagem);
                }
            } catch (IOException e) {
                System.err.println("Erro na comunicaçao" + socketCliente + ": " + e.getMessage());
            } finally {
                if (out != null) {
                    clientes.remove(out);
                }
                try {
                    socketCliente.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar o socket " + socketCliente + ": " + e.getMessage());
                }
            }
        }
    }

    private static void transmitir(String mensagem) {
        for (PrintWriter cliente : clientes) {
            cliente.println(mensagem);
        }
    }
}
