package Ex4;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class cliente {
    public static void main(String[] args) throws IOException {
        Socket socket;
        DataInputStream sIn;
        DataOutputStream s1out;
        String TargetDirectory = null;

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

                if(TargetDirectory == null) {
                    System.out.println("\nQual o caminho absoluto do diretório alvo? (FINISH para encerrar o programa)");
                    TargetDirectory = scanner.nextLine();

                    s1out.writeUTF("ENTER" + TargetDirectory);
                    s1out.flush();

                    if(TargetDirectory.equals("FINISH"))
                        break;

                    if(sIn.readUTF().equals("ABORT"))
                        TargetDirectory = null;

                }else{
                    System.out.println("\nGET {Nome do arquivo} - Receber arquivo\nPUT {Nome do arquivo} - Enviar arquivo\nEXIT - Sair do diretório");
                    String action = scanner.nextLine();
                    s1out.writeUTF(action);
                    s1out.flush();

                    if(action.contains("GET")) {
                        if(sIn.readUTF().equals("OK")) {
                            long fileSize = sIn.readLong();

                            File localFile = new File(action.substring(4));
                            FileOutputStream fos = new FileOutputStream(localFile);

                            servidor.DownloadData(sIn, fileSize, fos);
                        }
                    }if(action.contains("PUT")){
                        File fileToReceive = new File(action.substring(4));
                        s1out.writeLong(fileToReceive.length());

                        if(fileToReceive.exists() && fileToReceive.isFile()){
                            FileInputStream fis = new FileInputStream(fileToReceive.getAbsolutePath());
                            byte[] buffer = new byte[4096];
                            int bytesRead;

                            while((bytesRead = fis.read(buffer)) != -1){
                                s1out.write(buffer, 0, bytesRead);
                            }
                            s1out.flush();
                            fis.close();
                        }
                    } else if(action.equals("EXIT")){
                        TargetDirectory = null;
                    }
                }

                System.out.println(sIn.readUTF());

            }

            socket.close();
        } catch (IOException e) {
            System.out.println("Erro = " + e.getMessage());
        }
    }
}
