package autenticationserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



public class AutenticationServer {

    private static final int SERVER_PORT = 5000;
    private static final Map<String, String> users = new HashMap<>();

    public static void main(String[] args) {
        // Inicializar usuarios (usuario -> contraseña)
        users.put("usuario1", "contraseña1");
        users.put("usuario2", "contraseña2");
        users.put("usuario3", "contraseña3");

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Servidor de autenticación iniciado en el puerto " + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo usuario conectado desde " + clientSocket.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                
                var intentos = 2;
                var sinIntentos = false;
                
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                output.println("Bienvenido al servidor de autenticación");
                
                while(true){
                    
                    output.println("Introduzca su usuario:");
                    String username = input.readLine();
                    System.out.println("El cliente ha introducido el usuario: " + username);
                    
                    output.println("Introduzca su contraseña:");
                    String password = input.readLine();
                    System.out.println("El cliente ha introducido la contraseña: " + password);
                    
                    if(intentos <= 0){
                        System.out.println("El usuario se ha quedado sin intentos. Volverá a intentarlo de nuevo en 30 segundos");
                        output.println("Se ha quedado sin intentos. Vuelva a intentarlo de nuevo en 30 segundos");
                        Thread.sleep(3000);
                        sinIntentos = true;
                    }

                    if(intentos > 0){
                        if (authenticate(username, password)) {
                            output.println("Autenticación exitosa. Bienvenido, " + username + "!");
                            System.out.println("El usuario se ha conectado exitosamente");
                            System.out.println(username + " desconectado");
                            try {
                            socket.close();
                            break;
                            } catch (IOException e) {
                                System.err.println("Error al cerrar el socket: " + e.getMessage());
                            }
                        } else {
                            output.println("Autenticación fallida. Usuario o contraseña incorrectos. le quedan " + (intentos) + " intentos");
                            System.out.println("(" + username + ", " + password + ") " + "--> Acceso denegado");
                            System.out.println("Al usuario le quedan " + intentos + " intentos");
                            intentos --;
                        }
                    }
                    
                    if(sinIntentos == true){
                        intentos = 3;
                        sinIntentos = false;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error en la comunicación con el cliente: " + e.getMessage());
            } catch (InterruptedException ex) {
                Logger.getLogger(AutenticationServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private boolean authenticate(String username, String password) {
            return users.containsKey(username) && users.get(username).equals(password);
        }
    }
}