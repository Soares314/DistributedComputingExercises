package Ex2;

import Ex1.servidorthread;

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
            System.out.println( e );
        }

        System.out.println("\nServidor Iniciado!!!");

        while( true ) {
            try {

                s1 = s.accept();
                servidorthreadobject st;
                st = new servidorthreadobject(s1);
                st.start();

            } catch( IOException e ) {
                System.out.println( e );
            }
        }
    }
}
