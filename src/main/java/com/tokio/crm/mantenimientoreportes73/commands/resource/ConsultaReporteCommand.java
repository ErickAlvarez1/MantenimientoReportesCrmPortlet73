package com.tokio.crm.mantenimientoreportes73.commands.resource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.crm.crmservices73.Bean.ClienteInfoResponse;
import com.tokio.crm.crmservices73.Bean.ProduccionPrimaResponse;
import com.tokio.crm.crmservices73.Bean.ReporteClienteResponse;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.crmservices73.Exeption.CrmServicesException;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;
import com.tokio.crm.mantenimientoreportes73.bean.ReporteResponse;
import com.tokio.crm.mantenimientoreportes73.constants.MantenimientoReportesCrmPortlet73PortletKeys;
import com.tokio.crm.mantenimientoreportes73.service.ReporteService;
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
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_CarteraLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_DomicilioLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_LegalLocalService;
import com.tokio.crm.servicebuilder73.service.Configuracion_ReporteLocalService;
import com.tokio.crm.servicebuilder73.service.CotizacionLocalService;
import com.tokio.crm.servicebuilder73.service.Notificaciones_ManualesLocalService;
import com.tokio.crm.servicebuilder73.service.Notificaciones_autoLocalService;
import com.tokio.crm.servicebuilder73.service.PresupuestoLocalService;
import com.tokio.crm.servicebuilder73.service.Recordatorio_TramiteLocalService;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        immediate = true,
        property = {
                "javax.portlet.name=" + MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,
                "mvc.command.name=/crm/resource/mantenimientoReportes/consultaReporte"
        },
        service = MVCResourceCommand.class
)
public class ConsultaReporteCommand extends BaseMVCResourceCommand {
    private static final Log _log = LogFactoryUtil.getLog(ConsultaReporteCommand.class);
    @Reference
    ReporteService reporteService;

    @Reference
    CrmGenerico _CrmGenericoService;
    
    @Reference
	User_CrmLocalService _User_CrmLocalService;
    
    @Reference
    AgenteLocalService _AgenteLocalService;
    
    @Reference
    Configuracion_ReporteLocalService _Configuracion_ReporteLocalService;
    
    @Reference
    Agente_DomicilioLocalService _Agente_DomicilioLocalService;
    
    @Reference
    Agente_CarteraLocalService _Agente_CarteraLocalService;
    
    @Reference
    Notificaciones_ManualesLocalService _Notificaciones_ManualesLocalService;
    
    @Reference
    Notificaciones_autoLocalService _Notificaciones_autoLocalService;
    
    @Reference
    PresupuestoLocalService _PresupuestoLocalService;
    
    @Reference
    Recordatorio_TramiteLocalService _Recordatorio_TramiteLocalService;
    
    @Reference
    CotizacionLocalService _CotizacionLocalService;
    
    @Reference
    Agente_LegalLocalService _Agente_LegalLocalService;

    User usuario;

    @Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws Exception {
        ReporteResponse respuesta = new ReporteResponse();
        long id_reporte = ParamUtil.getLong(resourceRequest, "id_reporte");
        String fecha_inicio = ParamUtil.getString(resourceRequest, "fecha_inicio");
        String fecha_fin = ParamUtil.getString(resourceRequest, "fecha_fin");
        _log.debug(id_reporte);
        _log.debug(fecha_fin);
        _log.debug(fecha_inicio);
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();
        Date fechaFinal;
        Date fechaInicial;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        usuario = (User) resourceRequest.getAttribute(WebKeys.USER);
        try{
            if("".equals(fecha_fin)){
                fechaFinal = new Date();
            }else{
                fechaFinal = new SimpleDateFormat("yyyy-MM-dd").parse(fecha_fin);
            }
            if("".equals(fecha_inicio)){
                Calendar c = Calendar.getInstance();
                c.setTime(fechaFinal);
                c.add(Calendar.DAY_OF_MONTH,-30);
                fechaInicial = c.getTime();
            }else{
                fechaInicial = new SimpleDateFormat("yyyy-MM-dd").parse(fecha_inicio);
            }
            Configuracion_Reporte configuracion_reporte = _Configuracion_ReporteLocalService.getConfiguracion_Reporte(id_reporte);
            switch ((int)configuracion_reporte.getId_reporte()){
                case CrmDatabaseKey.USUARIOS:
                    List<User_Crm> user_crms = _User_CrmLocalService.getUser_Crms(0,_User_CrmLocalService.getUser_CrmsCount());
                    _log.debug(user_crms);
                    jsonArray = reporteService.usuarios(user_crms, configuracion_reporte, usuario);
                    break;
                case CrmDatabaseKey.ALTA_DE_AGENTES:
                    List<Agente> agentes = _AgenteLocalService.findByFechasBetween(simpleDateFormat.format(fechaInicial),simpleDateFormat.format(fechaFinal));
                    List<Agente_Legal> agente_legals = agentes.stream()
                        .map(agente -> {
                            try {
                                return _Agente_LegalLocalService.getAgente_Legal(agente.getAgenteId());
                            } catch (PortalException e) {
                                return null;
                            }
                        }).collect(Collectors.toList());
                    List<Agente_Domicilio> agente_domicilios = agentes.stream()
                        .map(agente -> {
                            try {
                                return _Agente_DomicilioLocalService.getAgente_Domicilio(agente.getAgenteId());
                            } catch (PortalException e) {
                                return null;
                            }
                        }).collect(Collectors.toList());
                    List<Agente_Cartera> agentes_cartera = agentes.stream()
                            .map(agente -> {
                                try {
                                    return _Agente_CarteraLocalService.getAgente_Cartera(agente.getAgenteId());
                                } catch (PortalException e) {
                                    return null;
                                }
                            }).collect(Collectors.toList());
                    jsonArray = reporteService.agentes(agentes,agente_domicilios,agente_legals,agentes_cartera,configuracion_reporte,usuario);
                    break;
                case CrmDatabaseKey.NOTIFICACIONES_AUTOMATICAS:
                    List<Notificaciones_auto> notificaciones_autos = _Notificaciones_autoLocalService.getNotificaciones_autos(0,_Notificaciones_autoLocalService.getNotificaciones_autosCount());
                    jsonArray = reporteService.notificacionesAutomaticas(notificaciones_autos,configuracion_reporte);
                    break;
                case CrmDatabaseKey.NOTIFICACIONES_MANUALES:
                    List<Notificaciones_Manuales> notificaciones_manuales = _Notificaciones_ManualesLocalService.findByFechas(simpleDateFormat.format(fechaInicial),simpleDateFormat.format(fechaFinal));
                    jsonArray = reporteService.notificacionesManuales(notificaciones_manuales, configuracion_reporte);
                    break;
                case CrmDatabaseKey.REGISTRO_CLIENTES_FISICAS:
                case CrmDatabaseKey.REGISTRO_CLIENTES_MORALES:
                    //ReporteClienteResponse reporteClienteResponse = _CrmGenericoService.getReporteClientes(usuario.getScreenName(),MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,simpleDateFormat.format(fechaInicial),simpleDateFormat.format(fechaFinal),"T","");
                    ReporteClienteResponse reporteClienteResponse = _CrmGenericoService.getReporteClientes(usuario.getScreenName(),MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,fecha_inicio,fecha_fin,"T","");
                    if (reporteClienteResponse.getCode()==0){
                        _log.info(reporteClienteResponse.getLista().size());
                        List<ClienteInfoResponse> clienteInfoResponses = reporteClienteResponse.getLista().stream().map(cliente ->{
                            try{
                                if(Objects.nonNull(cliente.getCliente())){
                                    return _CrmGenericoService.getClienteInfo(usuario.getScreenName(),MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,cliente.getCliente());
                                }else{
                                    return null;
                                }
                            }catch (CrmServicesException e){
                                return null;
                            }
                        }).collect(Collectors.toList());
                        jsonArray = reporteService.registroClientes(clienteInfoResponses,reporteClienteResponse.getLista(),configuracion_reporte,configuracion_reporte.getId_configuracion_reporte());
                    }else{
                        respuesta.setCode(reporteClienteResponse.getCode());
                        respuesta.setMsg(reporteClienteResponse.getMsg());
                    }
                    break;
                case CrmDatabaseKey.REGISTRO_COTIZACIONES:
                    List<Cotizacion> cotizaciones = _CotizacionLocalService.findByFechas(simpleDateFormat.format(fechaInicial),simpleDateFormat.format(fechaFinal));
                    List<ClienteInfoResponse> clienteInfoResponseCotizaciones = cotizaciones.stream().map(cotizacion ->{
                        try{
                            return _CrmGenericoService.getClienteInfo(usuario.getScreenName(),MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,cotizacion.getCodigo_cliente());
                        }catch (CrmServicesException e){
                            return null;
                        }
                    }).collect(Collectors.toList());
                    jsonArray = reporteService.registroCotizaciones(cotizaciones,clienteInfoResponseCotizaciones,configuracion_reporte,usuario);
                    break;
                case CrmDatabaseKey.RECORDATORIO_SEGUIMIENTO:
                    List<Recordatorio_Tramite> recordatorio_tramites = _Recordatorio_TramiteLocalService.findByFechas(simpleDateFormat.format(fechaInicial),simpleDateFormat.format(fechaFinal));
                    jsonArray = reporteService.recordatoriosSeguimiento(recordatorio_tramites,configuracion_reporte);

                    break;
                case CrmDatabaseKey.PRODUCCION_PRIMA:
                    ProduccionPrimaResponse produccionPrimaResponse = _CrmGenericoService.getReporteProduccionPrima(usuario.getScreenName(),MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,simpleDateFormat.format(fechaInicial),simpleDateFormat.format(fechaFinal));
                    jsonArray = reporteService.produccionPrima(produccionPrimaResponse.getLista(),configuracion_reporte);
                    break;
                case CrmDatabaseKey.GASTOS:
                    List<Presupuesto> presupuestos = _PresupuestoLocalService.findByFechas(simpleDateFormat.format(fechaInicial),simpleDateFormat.format(fechaFinal));
                    jsonArray = reporteService.gastos(presupuestos,configuracion_reporte,usuario);
                    break;
            }
            respuesta.setLista(jsonArray);
        }catch (Exception e){
            respuesta.setCode(1);
            respuesta.setMsg("Ocurrio un Error. " + e.getMessage());
            e.printStackTrace();
            _log.error(e.getMessage());
        }
        String responseString = gson.toJson(respuesta);
        PrintWriter writer = resourceResponse.getWriter();
        writer.write(responseString);
    }
}
