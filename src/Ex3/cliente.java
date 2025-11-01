package Ex3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class cliente {
    public static void main(String[] args) throws IOException {
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

                System.out.println("\nQual produto você busca?");
                String product = scanner.nextLine();

                if(product.equals("terminar")){
                    break;
                }

                System.out.printf("\nAdicione(+) ou retire(-) produto %s do estoque", product);
                int operationProduct = scanner.nextInt();

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
