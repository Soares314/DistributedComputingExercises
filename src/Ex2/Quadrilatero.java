package Ex2;

import java.io.Serializable;

public class Quadrilatero implements Serializable {
    private int lado1;
    private int lado2;
    private int lado3;
    private int lado4;
    private String tipo;

    public Quadrilatero(int lado1, int lado2, int lado3, int lado4){
        this.lado1 = lado1;
        this.lado2 = lado2;
        this.lado3 = lado3;
        this.lado4 = lado4;
        this.tipo = null;
    }

    public int ledados(int quallado) {
        if (quallado == 1) {
            return lado1;
        } else if (quallado == 2) {
            return lado2;
        } else if (quallado == 3) {
            return lado3;
        } else if (quallado == 4) {
            return lado4;
        } else {
            return -1;
        }
    }

    public void indicatipoquadrilatero(){
        if(lado1 == lado2 && lado1 == lado3 && lado1 == lado4){
            tipo = "Quadrado";
        }else if((lado1 == lado2 && lado3 == lado4) || (lado2 == lado3 && lado1 == lado4) || (lado1 == lado3 && lado2 == lado4)){
            tipo = "Retângulo";

        } else{
            tipo = "Quadrilatero";
        }

        System.out.println("Tipo do quadrilatero: " + tipo);

    }

    public void mostradados(){
        System.out.println("Lado1: " + lado1);
        System.out.println("Lado2: " + lado2);
        System.out.println("Lado3: " + lado3);
        System.out.println("Lado4: " + lado4);
        if(tipo != null)
            System.out.println("Tipo do Quadrilatero: " + tipo);
        else
            System.out.println("Tipo do Quadrilatero não definido");
    }

}
