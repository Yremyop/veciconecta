package com.example.veciconecta.models;

public class Aporte {
    private int id;
    private String concepto;
    private double monto;
    private String fecha_vencimiento;
    private String estado;  // "PENDIENTE", "PAGADO"

    public Aporte(int id, String concepto, double monto, String fecha_vencimiento, String estado) {
        this.id = id;
        this.concepto = concepto;
        this.monto = monto;
        this.fecha_vencimiento = fecha_vencimiento;
        this.estado = estado;
    }

    // Getters
    public int getId() { return id; }
    public String getConcepto() { return concepto; }
    public double getMonto() { return monto; }
    public String getFechaVencimiento() { return fecha_vencimiento; }
    public String getEstado() { return estado; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public void setMonto(double monto) { this.monto = monto; }
    public void setFechaVencimiento(String fecha_vencimiento) { this.fecha_vencimiento = fecha_vencimiento; }
    public void setEstado(String estado) { this.estado = estado; }
}