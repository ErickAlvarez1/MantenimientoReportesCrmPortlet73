package com.tokio.crm.mantenimientoreportes73.bean;

import com.google.gson.JsonArray;

public class ReporteResponse {
    String msg;
    int code;
    JsonArray lista;

    public ReporteResponse() {
    }

    public ReporteResponse(String msg, int code, JsonArray lista) {
        this.msg = msg;
        this.code = code;
        this.lista = lista;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public JsonArray getLista() {
        return lista;
    }

    public void setLista(JsonArray lista) {
        this.lista = lista;
    }

    @Override
    public String toString() {
        return "ReporteResponse{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", lista=" + lista +
                '}';
    }
}
