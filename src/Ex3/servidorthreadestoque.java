package Ex3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class servidorthreadestoque extends Thread{
    public Socket s1=null;
    public servidorthreadestoque(Socket s1){
        super();
        this.s1=s1;
    }

    public void run(){
        DataOutputStream s1out;
        DataInputStream sIn;
        HashMap<String, Integer> StockProducts = new HashMap<>();

        try{
            s1out = new DataOutputStream(s1.getOutputStream());
            sIn = new DataInputStream(s1.getInputStream());

            while(true){

                String product = sIn.readUTF();
                int operationProduct = sIn.readInt();

                if(product.equals("terminar")){
                    break;
                }

                if( StockProducts.containsKey(product) ){
                    int stockAfterOperation = StockProducts.get(product) + operationProduct;

                    if( stockAfterOperation >= 0 ){
                        StockProducts.put(product, stockAfterOperation);
                        s1out.writeUTF("Estoque atualizado com quantidade" + stockAfterOperation);
                    }
                    else
                        s1out.writeUTF("Não é possível fazer a saída de estoque – quantidade menor que o valor desejado");

                }else{
                    if(operationProduct > 0){
                        StockProducts.put(product, operationProduct);
                        s1out.writeUTF("Produto adicionado com quantidade " +  operationProduct);
                    }
                    else
                        s1out.writeUTF("Produto inexistente");
                }

            }
            s1.close();
        }
        catch(Exception e){
            System.out.println("Error="+e.getMessage());
        }

    }
}