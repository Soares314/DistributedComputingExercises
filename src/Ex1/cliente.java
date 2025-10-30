package Ex1;

import java.net.*;
import java.io.*;
import java.util.*;

public class cliente {
    public static void main( String args[] ) throws IOException {
        String msg;
        Socket socket;
        DataInputStream sIn;

        try {

            Scanner scanner = new Scanner(System.in);

            System.out.println("\nDigite o IP:");
            String IP = scanner.nextLine();

            System.out.println("\nDigite a Porta:");
            int porta = scanner.nextInt();
            msg = scanner.nextLine();

            InetSocketAddress endereco = new InetSocketAddress(IP, porta);
            socket = new Socket();
            socket.connect(endereco, 1000);

            while (true) {
                DataOutputStream s1out;
                s1out = new DataOutputStream(socket.getOutputStream());

                System.out.println("\nDigite três números para serem comparadas (Digite 0 em todas para finalizar)");
                int num1 = scanner.nextInt();
                int num2 = scanner.nextInt();
                int num3 = scanner.nextInt();

                if (num1 == 0 && num2 == 0 && num3 == 0) {
                    break;
                }

                s1out.writeInt(num1);
                s1out.writeInt(num2);
                s1out.writeInt(num3);

                sIn = new DataInputStream(socket.getInputStream());

                int largestNumber = sIn.readInt();
                int smallestNumber = sIn.readInt();

                System.out.println("Maior número: " + largestNumber + "\n" + "Menor número: " + smallestNumber);

            }

            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
