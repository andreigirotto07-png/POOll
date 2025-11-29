package com.locadora.app.model;

import java.time.LocalDate;

public class Aluguel {
    private Integer id;
    private Integer usuarioId;
    private Integer veiculoId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer kmInicio;
    private Integer kmFim;
    private String status;

    public Aluguel() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Integer veiculoId) { this.veiculoId = veiculoId; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public Integer getKmInicio() { return kmInicio; }
    public void setKmInicio(Integer kmInicio) { this.kmInicio = kmInicio; }
    public Integer getKmFim() { return kmFim; }
    public void setKmFim(Integer kmFim) { this.kmFim = kmFim; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
