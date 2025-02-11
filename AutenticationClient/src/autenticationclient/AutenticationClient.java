package autenticationclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AutenticationClient {

    public static void main(String[] args) {
        
        // Comprobación de argumentos
        if (args.length < 2) {
            System.out.println("Error, debes indicarle el host y el puerto");
            System.exit(1);
        }

        // Dirección y puerto del servidor
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        
        try {
            // Conexión al servidor
            Socket conexion = new Socket(host, port);
            System.out.println("Conectado al servidor " + host + " en el puerto " + port + ".");

            // Streams para enviar y recibir mensajes
            BufferedReader input = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            PrintWriter output = new PrintWriter(conexion.getOutputStream(), true);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.println(input.readLine());// Mensaje de bienvenida
            while(true){
                // Leer mensaje de bienvenida y solicitud de usuario y contraseña
                System.out.println(input.readLine());
                String username = stdInput.readLine();
                output.println(username);

                System.out.println(input.readLine());
                String password = stdInput.readLine();
                output.println(password);
                
                // Recibir respuesta del servidor
                String response = input.readLine();
                System.out.println(response);
                
                if(response.contains("Autenticación exitosa")){
                    conexion.close();
                }
            }
        } catch (IOException ex) {
            System.out.println("Se ha desconectado del servidor");
        }
    }
    
}