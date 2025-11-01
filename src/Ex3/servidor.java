package Ex3;

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
                servidorthreadestoque st;
                st = new servidorthreadestoque(s1);
                st.start();

            } catch( IOException e ) {
                System.out.println("Error="+e.getMessage());
            }
        }
    }
}
