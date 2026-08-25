package com.blackjack.model;

import java.util.ArrayList;
import java.util.List;

public class Mao {
    private final List<Carta> cartas = new ArrayList<>();

    public void adicionarCarta(Carta carta) {
        cartas.add(carta);
    }

    public void limpar() {
        cartas.clear();
    }

    public List<Carta> getCartas() {
        return cartas;
    }

    public int calcularPontuacao() {
        int pontos = 0;
        int ases = 0;

        for (Carta c : cartas) {
            pontos += c.rank().valorBase;
            if (c.rank() == Rank.AS) ases++;
        }

        while (pontos > 21 && ases > 0) {
            pontos -= 10; // Transforma Ás de 11 para 1
            ases--;
        }
        return pontos;
    }
}