package Ex3;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class cliente {
    public static void main(String[] args) throws IOException {
        Socket socket;
        DataInputStream sIn;
        DataOutputStream s1out;

        try {

            Scanner scanner = new Scanner(System.in);

            System.out.println("\nDigite o IP:");
            String IP = scanner.nextLine();

            System.out.println("\nDigite a Porta:");
            int porta = scanner.nextInt();
            scanner.nextLine();

            InetSocketAddress endereco = new InetSocketAddress(IP, porta);
            socket = new Socket();
            socket.connect(endereco, 5000);

            s1out = new DataOutputStream(socket.getOutputStream());
            sIn = new DataInputStream(socket.getInputStream());
            while (true) {

                System.out.println("\nQual produto você busca?");
                String product = scanner.nextLine();

                if(product.equals("terminar")){
                    break;
                }

                System.out.printf("\nAdicione(+) ou retire(-) produto %s do estoque\n", product);
                int operationProduct = scanner.nextInt();
                scanner.nextLine();

                s1out.writeUTF(product);
                s1out.writeInt(operationProduct);
                s1out.flush();

                String resultOperation = sIn.readUTF();
                System.out.println(resultOperation);

            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Erro = " + e.getMessage());
        }
    }
}
