package Ex1;

import java.net.*;
import java.io.*;

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

                assert s != null;
                s1 = s.accept();
                servidorthread st;
                st=new servidorthread(s1);
                st.start();

            } catch( IOException e ) {
                System.out.println("Error="+e.getMessage());
            }
        }
    }
}
