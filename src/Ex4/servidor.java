package Ex4;

import Ex3.servidorthreadestoque;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class servidor {
    public static void main(String[] args) {
        ServerSocket s = (ServerSocket)null;
        Socket s1;

        try {
            s = new ServerSocket( 4321,300 );
        } catch( IOException e ) {
            System.out.println("Error="+e.getMessage());
        }

        System.out.println("\nServidor Iniciado!!!");

        while( true ) {
            try {

                assert s != null;
                s1 = s.accept();
                servidorthreadFTP st;
                st = new servidorthreadFTP(s1);
                st.start();

            } catch( IOException e ) {
                System.out.println("Error="+e.getMessage());
            }
        }
    }

    static void DownloadData(DataInputStream sIn, long fileSize, FileOutputStream fos) throws IOException {
        byte[] buffer = new byte[4096];
        long totalBytesRead = 0;
        int bytesRead;

        while(totalBytesRead < fileSize && (bytesRead = sIn.read(buffer, 0,
                (int)Math.min(buffer.length, fileSize - totalBytesRead))) != -1){
            fos.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
        }

        fos.close();
    }
}
