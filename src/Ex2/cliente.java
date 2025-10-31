package Ex2;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class cliente {
    public static void main( String args[] ) throws IOException {
        Socket socket;
        ObjectInputStream sIn;
        ObjectOutputStream s1out;

        try {

            Scanner scanner = new Scanner(System.in);

            System.out.println("\nDigite o IP:");
            String IP = scanner.nextLine();

            System.out.println("\nDigite a Porta:");
            int porta = scanner.nextInt();

            InetSocketAddress endereco = new InetSocketAddress(IP, porta);
            socket = new Socket();
            socket.connect(endereco, 1000);

            s1out = new ObjectOutputStream(socket.getOutputStream());
            sIn = new ObjectInputStream(socket.getInputStream());
            while (true) {

                System.out.println("\nDigite cada lado do quadrilátero");
                int lado1 = scanner.nextInt();
                int lado2 = scanner.nextInt();
                int lado3 = scanner.nextInt();
                int lado4 = scanner.nextInt();

                if (lado1 == 0 || lado2 == 0 || lado3 == 0 || lado4 == 0) {
                    break;
                }

                Quadrilatero quadrilat = new Quadrilatero(lado1, lado2, lado3, lado4);

                s1out.writeObject(quadrilat);
                s1out.flush();

                quadrilat = (Quadrilatero) sIn.readObject();

                quadrilat.mostradados();

            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Erro = " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
