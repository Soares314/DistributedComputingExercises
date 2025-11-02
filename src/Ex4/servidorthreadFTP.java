package Ex4;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class servidorthreadFTP extends Thread{
    public Socket s1=null;
    public servidorthreadFTP(Socket s1){
        super();
        this.s1=s1;
    }

    public void run(){
        DataOutputStream s1out;
        DataInputStream sIn;
        File TargetDirectory = null;

        try{
            s1out = new DataOutputStream(s1.getOutputStream());
            sIn = new DataInputStream(s1.getInputStream());

            while(true){

                String action = sIn.readUTF();
                System.out.println(action);

                if( action.contains("ENTER") ){
                    File directoryName = new File(action.substring(5));

                    if(directoryName.exists() && directoryName.isDirectory()){
                        TargetDirectory = directoryName;
                        System.out.println("No diretório: " + TargetDirectory.getAbsolutePath());
                        s1out.writeUTF("OK");
                        s1out.writeUTF("No diretório: " + TargetDirectory.getAbsolutePath());
                    }else{
                        s1out.writeUTF("ABORT");
                        s1out.writeUTF("Diretório inexistente");
                    }

                }else if(action.contains("GET")) {
                    File fileToSend = new File(TargetDirectory, action.substring(4));
                    System.out.println("Enviando arquivo: " + action.substring(4));

                    if(fileToSend.exists() && fileToSend.isFile()) {
                        s1out.writeUTF("OK");
                        s1out.writeLong(fileToSend.length());

                        FileInputStream fis = new FileInputStream(fileToSend);
                        byte[] buffer = new byte[4096];
                        int bytesRead;

                        while ((bytesRead = fis.read(buffer)) != -1) {
                            s1out.write(buffer, 0, bytesRead);
                        }
                        s1out.flush();
                        fis.close();

                        s1out.writeUTF("Arquivo enviado com sucesso!");
                    }else{
                        s1out.writeUTF("ABORT");
                        s1out.writeUTF("Arquivo inexistente");
                    }

                }else if(action.contains("PUT")) {
                    File fileToReceive = new File(TargetDirectory, action.substring(3));
                    FileOutputStream fos = new FileOutputStream(fileToReceive);
                    long fileSize = sIn.readLong();

                    servidor.DownloadData(sIn, fileSize, fos);
                    s1out.writeUTF("Arquivo recebido e salvo em: " + fileToReceive.getAbsolutePath());

                }else if(action.contains("EXIT")) {
                    TargetDirectory = null;
                    s1out.writeUTF("Saiu do diretório");
                }else if(action.contains("FINISH")){
                    s1out.writeUTF("Programa finalizado");
                    break;
                }

            }
            s1.close();
        }
        catch(Exception e){
            System.out.println("Error="+e.getMessage());
        }

    }
}