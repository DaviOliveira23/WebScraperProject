package model;

import java.math.BigDecimal;

public class Produto {
    private String nomeP;
    private String valores;
    private String parcelaP;
    private String imageP;



    public Produto(String nomeP, String valores, String parcelaP,String imageP) {
        super();
        this.nomeP = nomeP;
        this.valores = valores;
        this.imageP = imageP;
        this.parcelaP = parcelaP;


    }

    public String getParcelaP() {
        return parcelaP;
    }

    public void setParcelaP(String parcelaP) {
        this.parcelaP = parcelaP;
    }

    public String getNomeP() {
        return nomeP;
    }

    public void setNomeP(String nomeP) {
        this.nomeP = nomeP;
    }

    public String getValores() {
        return valores;
    }

    public void setValores(String valores) {
        this.valores = valores;
    }

    public String getImageP() {
        return imageP;
    }

    public void setImageP(String imageP) {
        this.imageP = imageP;
    }

    @Override
    public String toString() {
        return "\nProduto{" +
                "nomeP='" + nomeP + '\'' +
                ", valores='" + valores + '\'' +
                ", parcelaP='" + parcelaP + '\'' +
                ", imageP='" + imageP + '\'' +
                '}' + "\n";
    }
}
