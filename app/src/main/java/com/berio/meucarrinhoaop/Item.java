package com.berio.meucarrinhoaop;

public class Item {
    private String nome;
    private double valorUnitario;
    private int quantidade;

    public Item(String nome, double valorUnitario, int quantidade) {
        this.nome = nome;
        this.valorUnitario = valorUnitario;
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getTotal() {
        return valorUnitario * quantidade;
    }

    @Override
    public String toString() {
        return nome + " | R$" + valorUnitario + " x " + quantidade + " = R$" + String.format("%.2f", getTotal());
    }
}