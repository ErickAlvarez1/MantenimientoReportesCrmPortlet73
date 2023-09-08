package com.tokio.crm.mantenimientoreportes73.service;

import com.google.gson.JsonArray;
import com.liferay.portal.kernel.model.User;
import com.tokio.crm.crmservices73.Bean.ClienteInfoResponse;
import com.tokio.crm.crmservices73.Bean.ProduccionPrima;
import com.tokio.crm.crmservices73.Bean.ReporteCliente;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Agente_Cartera;
import com.tokio.crm.servicebuilder73.model.Agente_Domicilio;
import com.tokio.crm.servicebuilder73.model.Agente_Legal;
import com.tokio.crm.servicebuilder73.model.Configuracion_Reporte;
import com.tokio.crm.servicebuilder73.model.Cotizacion;
import com.tokio.crm.servicebuilder73.model.Notificaciones_Manuales;
import com.tokio.crm.servicebuilder73.model.Notificaciones_auto;
import com.tokio.crm.servicebuilder73.model.Presupuesto;
import com.tokio.crm.servicebuilder73.model.Recordatorio_Tramite;
import com.tokio.crm.servicebuilder73.model.User_Crm;

import java.util.List;

public interface ReporteService {
    public JsonArray usuarios(List<User_Crm> users_crm, Configuracion_Reporte configuracion_reporte, User usuarioConsulta);

    public JsonArray agentes(List<Agente> agentes, List<Agente_Domicilio> agentes_domicilio, List<Agente_Legal> agentes_legal, List<Agente_Cartera> agentes_cartera, Configuracion_Reporte configuracion_reporte, User usuarioConsulta);

    public JsonArray notificacionesAutomaticas(List<Notificaciones_auto> notificaciones_autos, Configuracion_Reporte configuracion_reporte);

    public JsonArray notificacionesManuales(List<Notificaciones_Manuales> notificaciones_manuales, Configuracion_Reporte configuracion_reporte);

    public JsonArray registroClientes(List<ClienteInfoResponse> clienteInfoResponses, List<ReporteCliente> reporteClientes , Configuracion_Reporte configuracion_reporte, long tipoPersona);

    public JsonArray registroCotizaciones(List<Cotizacion> cotizaciones, List<ClienteInfoResponse> clienteInfoResponses, Configuracion_Reporte configuracion_reporte,User usuarioConsulta);

    public JsonArray recordatoriosSeguimiento(List<Recordatorio_Tramite> recordatorio_tramites, Configuracion_Reporte configuracion_reporte);

    public JsonArray produccionPrima(List<ProduccionPrima> produccionPrimas, Configuracion_Reporte configuracion_reporte);

    public JsonArray gastos(List<Presupuesto> presupuestos, Configuracion_Reporte configuracion_reporte,User usuarioConsulta);
}
