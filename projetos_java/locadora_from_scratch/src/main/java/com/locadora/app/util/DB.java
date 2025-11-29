package com.locadora.app.util;

import java.nio.file.*;
import java.sql.*;

public class DB {

    private static final String DB_FILE = "data/locadora.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void init() throws Exception {
        Path p = Path.of(DB_FILE);
        if (!Files.exists(p.getParent())) Files.createDirectories(p.getParent());

        boolean exists = Files.exists(p);

        try (Connection c = getConnection(); Statement s = c.createStatement()) {

            if (!exists) {

                // ========================
                // CRIAÇÃO DE TABELAS
                // ========================
                s.execute("""
                    CREATE TABLE IF NOT EXISTS usuarios (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        email TEXT,
                        telefone TEXT
                    );
                """);

                s.execute("""
                    CREATE TABLE IF NOT EXISTS veiculos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        placa TEXT UNIQUE NOT NULL,
                        marca TEXT,
                        modelo TEXT,
                        ano INTEGER,
                        cor TEXT,
                        disponivel INTEGER,
                        valor_diaria REAL
                    );
                """);

                s.execute("""
                    CREATE TABLE IF NOT EXISTS alugueis (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        usuario_id INTEGER,
                        veiculo_id INTEGER,
                        data_inicio TEXT,
                        data_fim TEXT,
                        km_inicio INTEGER,
                        km_fim INTEGER,
                        status TEXT,
                        FOREIGN KEY(usuario_id) REFERENCES usuarios(id),
                        FOREIGN KEY(veiculo_id) REFERENCES veiculos(id)
                    );
                """);

                // ========================
                // SEED AUTOMÁTICO
                // ========================

                // Usuários
                s.execute("""
                    INSERT INTO usuarios(nome,email,telefone) VALUES
                    ('João da Silva','joao@example.com','(54) 99888-1111'),
                    ('Maria Souza','maria@example.com','(54) 99777-2222'),
                    ('Carlos Pereira','carlos@example.com','(54) 99666-3333');
                """);

                // Veículos
                s.execute("""
                    INSERT INTO veiculos(placa,marca,modelo,ano,cor,disponivel,valor_diaria) VALUES
                    ('ABC1D23','Volkswagen','Gol 1.0',2015,'Prata',1,120.0),
                    ('DEF4G56','Chevrolet','Onix LT',2018,'Branco',1,150.0),
                    ('XYZ9A88','Fiat','Argo',2020,'Vermelho',1,130.0);
                """);

                // Aluguéis
                s.execute("""
                    INSERT INTO alugueis(usuario_id, veiculo_id, data_inicio, km_inicio, status) VALUES
                    (1,1,'2025-11-25',10000,'ABERTO'),
                    (2,2,'2025-11-20',50000,'FECHADO');
                """);

                System.out.println("✔ Banco criado com dados iniciais (SEED).");
            }
        }
    }
}
