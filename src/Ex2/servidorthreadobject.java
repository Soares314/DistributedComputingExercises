package Ex2;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class servidorthreadobject extends Thread{
    public Socket s1=null;
    public servidorthreadobject(Socket s1){
        super();
        this.s1=s1;
    }

    public void run(){
        ObjectOutputStream s1out;
        ObjectInputStream sIn;
        try{
            sIn = new ObjectInputStream(s1.getInputStream());
            s1out = new ObjectOutputStream(s1.getOutputStream());

            while(true){

                Quadrilatero quadrilat = (Quadrilatero) sIn.readObject();
                System.out.println("\nId do quadrilátero recebido: " + quadrilat);

                if (quadrilat.ledados(1) == 0 || quadrilat.ledados(2) == 0 || quadrilat.ledados(3) == 0 || quadrilat.ledados(4) == 0)
                    break;

                quadrilat.indicatipoquadrilatero();

                s1out.writeObject(quadrilat);
                s1out.flush();

            }
            s1.close();
        }
        catch(Exception e){
            System.out.println("Error="+e.getMessage());
        }

    }
}
