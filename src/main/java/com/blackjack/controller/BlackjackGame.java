package com.blackjack.controller;

import com.blackjack.model.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class BlackjackGame extends Application {

    private Baralho baralho;
    private Mao maoJogador;
    private Mao maoDealer;

    private HBox mesaDealer;
    private HBox mesaJogador;
    private Label lblStatus;
    private Label lblPontosJogador;

    private Button btnPedir;
    private Button btnParar;
    private Button btnReiniciar;

    @Override
    public void start(Stage stage) {
        baralho = new Baralho();
        maoJogador = new Mao();
        maoDealer = new Mao();

        // --- Layout da Interface (Responsiva) ---
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2E8B57; -fx-padding: 30;"); // Verde Mesa de Jogo

        // Área do Dealer
        Label lblDealer = new Label("Dealer");
        lblDealer.getStyleClass().add("titulo");
        mesaDealer = new HBox(10);
        mesaDealer.setAlignment(Pos.CENTER);

        // Área do Jogador
        Label lblJogador = new Label("Você");
        lblJogador.getStyleClass().add("titulo");
        mesaJogador = new HBox(10);
        mesaJogador.setAlignment(Pos.CENTER);
        lblPontosJogador = new Label("Pontos: 0");
        lblPontosJogador.getStyleClass().add("texto-branco");

        // Botões
        HBox painelBotoes = new HBox(15);
        painelBotoes.setAlignment(Pos.CENTER);
        btnPedir = criarBotao("Pedir Carta", "#3498db");
        btnParar = criarBotao("Parar", "#e74c3c");
        btnReiniciar = criarBotao("Novo Jogo", "#f1c40f");
        btnReiniciar.setVisible(false); // Só aparece no fim

        painelBotoes.getChildren().addAll(btnPedir, btnParar, btnReiniciar);

        lblStatus = new Label("Bem-vindo ao Blackjack!");
        lblStatus.getStyleClass().add("status");

        root.getChildren().addAll(lblDealer, mesaDealer, lblStatus, mesaJogador, lblJogador, lblPontosJogador, painelBotoes);

        // --- Eventos (Interatividade) ---
        btnPedir.setOnAction(e -> comprarCartaJogador());
        btnParar.setOnAction(e -> turnoDealer());
        btnReiniciar.setOnAction(e -> iniciarJogo());

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Blackjack Java 21");
        stage.setScene(scene);
        stage.show();

        iniciarJogo();
    }

    private void iniciarJogo() {
        baralho.reiniciar();
        maoJogador.limpar();
        maoDealer.limpar();

        btnPedir.setDisable(false);
        btnParar.setDisable(false);
        btnReiniciar.setVisible(false);
        lblStatus.setText("Sua vez...");

        // Distribuição inicial
        maoJogador.adicionarCarta(baralho.comprar());
        maoDealer.adicionarCarta(baralho.comprar());
        maoJogador.adicionarCarta(baralho.comprar());
        maoDealer.adicionarCarta(baralho.comprar());

        atualizarMesa(false);
    }

    private void comprarCartaJogador() {
        maoJogador.adicionarCarta(baralho.comprar());
        if (maoJogador.calcularPontuacao() > 21) {
            atualizarMesa(false);
            fimDeJogo("Você estourou! Dealer venceu.");
        } else {
            atualizarMesa(false);
        }
    }

    private void turnoDealer() {
        // Dealer compra até ter 17 ou mais
        while (maoDealer.calcularPontuacao() < 17) {
            maoDealer.adicionarCarta(baralho.comprar());
        }
        verificarVencedor();
    }

    private void verificarVencedor() {
        int pJogador = maoJogador.calcularPontuacao();
        int pDealer = maoDealer.calcularPontuacao();

        atualizarMesa(true); // Revela cartas do dealer

        if (pDealer > 21) {
            fimDeJogo("Dealer estourou! Você venceu!");
        } else if (pJogador > pDealer) {
            fimDeJogo("Você venceu!");
        } else if (pJogador < pDealer) {
            fimDeJogo("Dealer venceu.");
        } else {
            fimDeJogo("Empate.");
        }
    }

    private void fimDeJogo(String mensagem) {
        lblStatus.setText(mensagem);
        btnPedir.setDisable(true);
        btnParar.setDisable(true);
        btnReiniciar.setVisible(true);
    }

    // Atualiza a "DOM" do JavaFX
    private void atualizarMesa(boolean mostrarTudoDealer) {
        mesaJogador.getChildren().clear();
        mesaDealer.getChildren().clear();

        // Renderiza Jogador
        for (Carta c : maoJogador.getCartas()) {
            mesaJogador.getChildren().add(criarVisualCarta(c));
        }
        lblPontosJogador.setText("Pontos: " + maoJogador.calcularPontuacao());

        // Renderiza Dealer
        List<Carta> cartasDealer = maoDealer.getCartas();
        for (int i = 0; i < cartasDealer.size(); i++) {
            if (i == 0 && !mostrarTudoDealer) {
                // Carta oculta
                mesaDealer.getChildren().add(criarVisualCartaOculta());
            } else {
                mesaDealer.getChildren().add(criarVisualCarta(cartasDealer.get(i)));
            }
        }
    }

    // Helpers de UI
    private StackPane criarVisualCarta(Carta c) {
        StackPane card = new StackPane();
        card.setPrefSize(80, 120);
        card.getStyleClass().add("carta");
        Label lbl = new Label(c.toString());
        lbl.setWrapText(true);
        card.getChildren().add(lbl);
        return card;
    }

    private StackPane criarVisualCartaOculta() {
        StackPane card = new StackPane();
        card.setPrefSize(80, 120);
        card.getStyleClass().add("carta-oculta");
        return card;
    }

    private Button criarBotao(String texto, String cor) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        btn.setPrefWidth(120);
        return btn;
    }

    public static void main(String[] args) {
        launch();
    }
}