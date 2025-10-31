package Ex1;
import java.net.*;
import java.io.*;

public class servidorthread extends Thread{
    public Socket s1=null;
    public servidorthread(Socket s1){
        super();
        this.s1=s1;
    }

    public void run(){
        DataOutputStream s1out;
        DataInputStream sIn;
        try{
            s1out = new DataOutputStream(s1.getOutputStream());
            sIn = new DataInputStream(s1.getInputStream());

            while(true){

                int num1 = sIn.readInt();
                int num2 = sIn.readInt();
                int num3 = sIn.readInt();

                System.out.println("\nNúmeros recebidos do cliente: "+ num1 + num2 + num3);

                if(num1 >= num2 && num1 >= num3){
                    s1out.writeInt(num1);
                }else if(num2 >= num1 && num2 >= num3){
                    s1out.writeInt(num2);
                }else{
                    s1out.writeInt(num3);
                }

                if(num1 <= num2 && num1 <= num3){
                    s1out.writeInt(num1);
                }else if(num2 <= num1 && num2 <= num3){
                    s1out.writeInt(num2);
                }else{
                    s1out.writeInt(num3);
                }

                if(num1 == 0 && num2 == 0 && num3 == 0){
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
