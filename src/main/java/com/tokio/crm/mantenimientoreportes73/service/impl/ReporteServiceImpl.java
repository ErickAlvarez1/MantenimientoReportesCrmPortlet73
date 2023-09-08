package com.tokio.crm.mantenimientoreportes73.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.tokio.crm.crmservices73.Bean.CatalogoMoneda;
import com.tokio.crm.crmservices73.Bean.ClienteInfoResponse;
import com.tokio.crm.crmservices73.Bean.ListaCpData;
import com.tokio.crm.crmservices73.Bean.ListaRegistro;
import com.tokio.crm.crmservices73.Bean.Moneda;
import com.tokio.crm.crmservices73.Bean.ProduccionPrima;
import com.tokio.crm.crmservices73.Bean.Registro;
import com.tokio.crm.crmservices73.Bean.ReporteCliente;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.crmservices73.Constants.CrmServiceKey;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;
import com.tokio.crm.mantenimientoreportes73.constants.MantenimientoReportesCrmPortlet73PortletKeys;
import com.tokio.crm.mantenimientoreportes73.service.ReporteService;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Agente_Cartera;
import com.tokio.crm.servicebuilder73.model.Agente_Domicilio;
import com.tokio.crm.servicebuilder73.model.Agente_Legal;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.model.Configuracion_Reporte;
import com.tokio.crm.servicebuilder73.model.Cotizacion;
import com.tokio.crm.servicebuilder73.model.Notificaciones_Manuales;
import com.tokio.crm.servicebuilder73.model.Notificaciones_auto;
import com.tokio.crm.servicebuilder73.model.Perfil_Crm;
import com.tokio.crm.servicebuilder73.model.Personal_A_Cargo;
import com.tokio.crm.servicebuilder73.model.Presupuesto;
import com.tokio.crm.servicebuilder73.model.Recordatorio_Tramite;
import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;
import com.tokio.crm.servicebuilder73.service.Perfil_CrmLocalService;
import com.tokio.crm.servicebuilder73.service.Personal_A_CargoLocalService;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, properties = {}, service = ReporteService.class)
public class ReporteServiceImpl implements ReporteService {
    private static final Log _log = LogFactoryUtil.getLog(ReporteServiceImpl.class);
    JsonArray jsonArray;
    JsonObject jsonObject;
    String[] idCabeceras;
    List<Catalogo_Detalle> catalogo_detalles;
    Map<Long,String> mapaColumnas;
    Map<Long,String> mapaEjecutivos;
    Map<Long,String> mapaOficinas;
    Map<Long,String> mapaSociedad;
    Map<Long,String> mapaPerfilContacto;
    Map<Long,String> mapaTipoCedula;
    Map<Long,String> mapaPeriodo;
    Map<Long,String> mapaAgentes;
    Map<Integer,String> mapaControlSub;
    Map<String,String> mapaProducto;
    Map<String,String> mapaTipoNegocio;
    Map<Integer,String> mapaMoneda;
    ListaCpData listaCpData;
    @Reference
    CrmGenerico _CrmGenericoService;
    
    @Reference
	User_CrmLocalService _User_CrmLocalService;
    
    @Reference
	Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;
    
    @Reference
    AgenteLocalService _AgenteLocalService;
    
    @Reference
    Perfil_CrmLocalService _Perfil_CrmLocalService;
    
    @Reference
    Personal_A_CargoLocalService _Personal_A_CargoLocalService;

    @Override
    public JsonArray usuarios(List<User_Crm> users_crm, Configuracion_Reporte configuracion_reporte, User usuarioConsutla) {
        inicializar(configuracion_reporte);
        mapaOficinas(usuarioConsutla);
        Map<Integer,String> mapaAreas = new HashMap<>();
        Map<Integer,String> mapaPerfiles = new HashMap<>();
        try {
            ListaRegistro listaRegistro = _CrmGenericoService.getCatalogo(
                    CrmServiceKey.TMX_CTE_ROW_TODOS,
                    CrmServiceKey.TMX_CTE_TRANSACCION_GET,
                    CrmServiceKey.LIST_CAT_AREA,
                    CrmServiceKey.TMX_CTE_CAT_ACTIVOS,
                    usuarioConsutla.getScreenName(),
                    MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73
            );
            mapaAreas = listaRegistro.getLista().stream().collect(Collectors.toMap(Registro::getId,Registro::getValor));
            List<Perfil_Crm> listaPerfiles = _Perfil_CrmLocalService.getPerfil_Crms(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
            mapaPerfiles = listaPerfiles.stream().collect(Collectors.toMap(Perfil_Crm::getPerfilId,Perfil_Crm::getDescripcion));
        }catch (Exception e){
            _log.error(e.getMessage());
        }
        User usuario = null;
        String jefe;
        String dependientes;
        List<String> llaves = new ArrayList<>();
        for(User_Crm user_crm: users_crm){
            jsonObject = new JsonObject();
            try{
                usuario = UserLocalServiceUtil.getUserById(user_crm.getUserId());
            }catch (Exception e){
                _log.error(e.getMessage());
            }
            for(String columna: idCabeceras){
                if(!llaves.contains(mapaColumnas.get(Long.parseLong(columna)))){
                    llaves.add(mapaColumnas.get(Long.parseLong(columna)));
                }
                switch (mapaColumnas.get(Long.parseLong(columna))){
                    case "accesoCRM":
                        jsonObject.addProperty("accesoCRM",user_crm.isAccesoCRM()?"Activo":"Denegado");
                    break;
                    case "userScreen":
                        assert usuario != null;
                        jsonObject.addProperty("userScreen",usuario.getScreenName());
                        break;
                    case "nombre_completo":
                        assert usuario != null;
                        jsonObject.addProperty("nombre_completo",usuario.getFullName());
                        break;
                    case "email":
                        assert usuario != null;
                        jsonObject.addProperty("email",usuario.getEmailAddress());
                        break;
                    case "area":
                        jsonObject.addProperty("area",mapaAreas.get(user_crm.getArea()));
                        break;
                    case "pefilId":
                        jsonObject.addProperty("pefilId",mapaPerfiles.get(user_crm.getPerfilId()));
                        break;
                    case "jefe":
                        jefe = "";
                        if(user_crm.getJefe() != -1){
                            try {
                                jefe = UserLocalServiceUtil.getUserById(user_crm.getJefe()).getFullName();
                            }catch (Exception e){
                                _log.error(e.getMessage());
                            }
                        }
                        jsonObject.addProperty("jefe",jefe);
                        break;
                    case "dependientesId":
                        dependientes = "";
                        List<Personal_A_Cargo> personal_a_cargo;
                        personal_a_cargo = _Personal_A_CargoLocalService.getPersonal_A_CargoByUserId(user_crm.getUserId());
                        if(!personal_a_cargo.isEmpty()){
                            for(Personal_A_Cargo personal: personal_a_cargo){
                                try {
                                    if("".equals(dependientes)){
                                        dependientes = UserLocalServiceUtil.getUserById(personal.getUserId()).getFullName();
                                    }else{
                                        dependientes += "," + UserLocalServiceUtil.getUserById(personal.getUserId()).getFullName();
                                    }
                                } catch (PortalException e) {
                                    _log.error(e.getMessage());
                                }

                            }
                            jsonObject.addProperty("dependientesId",dependientes);
                        }else{
                            jsonObject.addProperty("dependientesId","");
                        }
                        break;
                    case "oficina":
                        jsonObject.addProperty("oficina",mapaOficinas.get((long)user_crm.getOficina()));
                        break;
                }
            }
            remplazaNull(llaves);
            jsonArray.add(jsonObject);
        }
        _log.debug(jsonArray);
        return jsonArray;
    }

    @Override
    public JsonArray agentes(List<Agente> agentes, List<Agente_Domicilio> agentes_domicilio, List<Agente_Legal> agentes_legal, List<Agente_Cartera> agentes_cartera,Configuracion_Reporte configuracion_reporte, User usuarioConsutla) {
        Optional <Agente_Domicilio> agente_domicilio;
        Optional <Agente_Legal> agente_legal;
        Optional <Agente_Cartera> agente_cartera;
        inicializar(configuracion_reporte);
        mapaEjecutivos();
        mapaOficinas(usuarioConsutla);
        mapaTipoSociedad();
        mapaPerfilContactos();
        mapaTipoCedula();
        User_Crm userCrm = null;
        agentes.removeAll(Collections.singleton(null));
        agentes_domicilio.removeAll(Collections.singleton(null));
        agentes_legal.removeAll(Collections.singleton(null));
        agentes_cartera.removeAll(Collections.singleton(null));
        String columnaS;
        //_log.debug(mapaPerfilContacto);
        List<String> llaves = new ArrayList<>();
        for(Agente agente: agentes){
            jsonObject = new JsonObject();
            agente_domicilio = agentes_domicilio.stream().parallel().filter(f-> f.getAgenteId()==agente.getAgenteId()).findFirst();
            agente_legal = agentes_legal.stream().parallel().filter(f-> f.getAgenteId()==agente.getAgenteId()).findFirst();
            agente_cartera = agentes_cartera.stream().parallel().filter(f-> f.getAgenteId()==agente.getAgenteId()).findFirst();
            try {
                userCrm = _User_CrmLocalService.getUser_Crm((int)usuarioConsutla.getUserId());
            }catch (Exception e){
                e.printStackTrace();
            }
            for(String columna: idCabeceras) {
                if(!llaves.contains(mapaColumnas.get(Long.parseLong(columna)))){
                    llaves.add(mapaColumnas.get(Long.parseLong(columna)));
                }
                columnaS = mapaColumnas.get(Long.parseLong(columna));
                switch (columnaS) {
                    case "datosRfc":
                        jsonObject.addProperty("datosRfc",agente.getDatosRfc());
                        break;
                    case "tipoNegocio":
                        jsonObject.addProperty("tipoNegocio",agente.getTipoNegocio()== CrmDatabaseKey.NEGOCIO_M?"MD":"J");
                        break;
                    case "ejecutivoId":
                        jsonObject.addProperty("ejecutivoId",mapaEjecutivos.get(agente.getEjecutivo()));
                        break;
                    case "oficina":
                        jsonObject.addProperty("oficina",mapaOficinas.get((int)agente.getOficinaId()));
                        break;
                    case "preclave":
                        jsonObject.addProperty("preclave",agente.getPreclave());
                        break;
                    case "clave":
                        jsonObject.addProperty("clave",agente.getClave());
                        break;
                    case "tipoPersona":
                        jsonObject.addProperty("tipoPersona",agente.getTipoPersona()==CrmDatabaseKey.PERSONA_FISICA?"FISICA":"MORAL");
                        break;
                    case "nombre_completo":
                        if(agente.getTipoPersona() == CrmDatabaseKey.PERSONA_FISICA){
                            jsonObject.addProperty("nombre_completo",agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM());
                        }else {
                            jsonObject.addProperty("nombre_completo",agente.getNombre() + " " + mapaSociedad.get((long)agente.getTipoSociedad()));
                        }
                        break;
                    case "codigo":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("codigo",agente_domicilio.get().getCodigo());
                        }else{
                            jsonObject.addProperty("codigo","");
                        }
                        break;
                    case "calle":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("calle",agente_domicilio.get().getCalle());
                        }else{
                            jsonObject.addProperty("calle","");
                        }
                        break;
                    case "noExt":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("noExt",agente_domicilio.get().getNoExt());
                        }else{
                            jsonObject.addProperty("noExt","");
                        }
                        break;
                    case "cpId":
                        if(agente_domicilio.isPresent()){
                            try{
                                listaCpData =_CrmGenericoService.getCatalogoCP(agente_domicilio.get().getCodigo(), usuarioConsutla.getScreenName(), MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73);
                                long idColonia = agente_domicilio.get().getCpId();
                                jsonObject.addProperty("cpId",listaCpData.getCpData().stream().filter(f->f.getId() == idColonia).collect(Collectors.toList()).get(0).getColonia());
                            }catch (Exception e){
                                jsonObject.addProperty("cpId","");
                            }
                        }else{
                            jsonObject.addProperty("cpId","");
                        }
                        break;
                    case "municipio":
                        if(agente_domicilio.isPresent()){
                            try{
                                listaCpData =_CrmGenericoService.getCatalogoCP(agente_domicilio.get().getCodigo(), usuarioConsutla.getScreenName(), MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73);
                                long idColonia = agente_domicilio.get().getCpId();
                                jsonObject.addProperty("municipio",listaCpData.getCpData().stream().filter(f->f.getId() == idColonia).collect(Collectors.toList()).get(0).getEstado());
                            }catch (Exception e){
                                jsonObject.addProperty("municipio","");
                            }
                        }else{
                            jsonObject.addProperty("municipio","");
                        }
                        break;
                    case "estado":
                        if(agente_domicilio.isPresent()){
                            try{
                                listaCpData =_CrmGenericoService.getCatalogoCP(agente_domicilio.get().getCodigo(), usuarioConsutla.getScreenName(), MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73);
                                long idColonia = agente_domicilio.get().getCpId();
                                jsonObject.addProperty("estado",listaCpData.getCpData().stream().filter(f->f.getId() == idColonia).collect(Collectors.toList()).get(0).getEstado());
                            }catch (Exception e){
                                jsonObject.addProperty("estado","");
                            }
                        }else{
                            jsonObject.addProperty("estado","");
                        }
                        break;
                    case "valorCartera":
                        if(agente_cartera.isPresent()){
                            jsonObject.addProperty("valorCartera",agente_cartera.get().getValorCartera());
                        }else{
                            jsonObject.addProperty("valorCartera","");
                        }
                        break;
                    case "danos":
                        if(agente_cartera.isPresent()){
                            jsonObject.addProperty("danos",agente_cartera.get().getDanos());
                        }else{
                            jsonObject.addProperty("danos","");
                        }
                        break;
                    case "vida":
                        if(agente_cartera.isPresent()){
                            jsonObject.addProperty("vida",agente_cartera.get().getVida());
                        }else{
                            jsonObject.addProperty("vida","");
                        }
                        break;
                    case "gmm":
                        if(agente_cartera.isPresent()){
                            jsonObject.addProperty("gmm",agente_cartera.get().getGmm());
                        }else{
                            jsonObject.addProperty("gmm","");
                        }
                        break;
                    case "autos":
                        if(agente_cartera.isPresent()){
                            jsonObject.addProperty("autos",agente_cartera.get().getVida());
                        }else{
                            jsonObject.addProperty("autos","");
                        }
                        break;
                    case "nombreContacto1":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("nombreContacto1",agente_domicilio.get().getNombreContacto1());
                        }else{
                            jsonObject.addProperty("nombreContacto1","");
                        }
                        break;
                    case "tel1Contacto1":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("tel1Contacto1",agente_domicilio.get().getTel1Contacto1());
                        }else{
                            jsonObject.addProperty("tel1Contacto1","");
                        }
                        break;
                    case "tel2Contacto1":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("tel2Contacto1",agente_domicilio.get().getTel2Contacto1());
                        }else{
                            jsonObject.addProperty("tel2Contacto1","");
                        }
                        break;
                    case "emailContacto1":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("emailContacto1",agente_domicilio.get().getEmailContacto1());
                        }else{
                            jsonObject.addProperty("emailContacto1","");
                        }
                        break;
                    case "perfil1":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("perfil1",mapaPerfilContacto.get(agente_domicilio.get().getPerfil1()));
                        }else{
                            jsonObject.addProperty("perfil1","");
                        }
                        break;
                    case "nombreContacto2":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("nombreContacto2",agente_domicilio.get().getNombreContacto2());
                        }else{
                            jsonObject.addProperty("nombreContacto2","");
                        }
                        break;
                    case "tel1Contacto2":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("tel1Contacto2",agente_domicilio.get().getTel1Contacto2());
                        }else{
                            jsonObject.addProperty("tel1Contacto2","");
                        }
                        break;
                    case "tel2Contacto2":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("tel2Contacto2",agente_domicilio.get().getTel2Contacto2());
                        }else{
                            jsonObject.addProperty("tel2Contacto2","");
                        }
                        break;
                    case "emailContacto2":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("emailContacto2",agente_domicilio.get().getEmailContacto2());
                        }else{
                            jsonObject.addProperty("emailContacto2","");
                        }
                        break;
                    case "perfil2":
                        if(agente_domicilio.isPresent()){
                            jsonObject.addProperty("perfil2", mapaPerfilContacto.get(agente_domicilio.get().getPerfil2()));
                        }else{
                            jsonObject.addProperty("perfil2","");
                        }
                        break;
                    case "agrupador":
                        jsonObject.addProperty("agrupador",agente.getAgrupador());
                        break;
                    case "fechaAlta":
                        jsonObject.addProperty("agrupador",agente.getFechaCreacion().toString());
                        break;
                    case "tipoCedula":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("tipoCedula",mapaTipoCedula.get(agente_legal.get().getTipoCedula()));
                            }else {
                                jsonObject.addProperty("tipoCedula","");
                            }
                        }else{
                            jsonObject.addProperty("tipoCedula","");
                        }
                        break;
                    case "vencimientoCedula":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("vencimientoCedula",agente_legal.get().getVencimientoCedula().toString());
                            }else {
                                jsonObject.addProperty("vencimientoCedula","");
                            }
                        }else{
                            jsonObject.addProperty("vencimientoCedula","");
                        }
                        break;
                    case "comprobanteDomicilio":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("comprobanteDomicilio",agente_legal.get().isComprobanteDomicilio()?"SI":"NO");
                            }else {
                                jsonObject.addProperty("comprobanteDomicilio","");
                            }
                        }else{
                            jsonObject.addProperty("comprobanteDomicilio","");
                        }
                        break;
                    case "vencimientoCompDomicilio":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("vencimientoCompDomicilio",agente_legal.get().getVencimientoCompDomicilio().toString());
                            }else {
                                jsonObject.addProperty("vencimientoCompDomicilio","");
                            }
                        }else{
                            jsonObject.addProperty("vencimientoCompDomicilio","");
                        }
                        break;
                    case "polizaRC":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("polizaRC",agente_legal.get().isPolizaRC()?"SI":"NO");
                            }else {
                                jsonObject.addProperty("polizaRC","");
                            }
                        }else{
                            jsonObject.addProperty("polizaRC","");
                        }
                        break;
                    case "vencimientoPoliza":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("vencimientoPoliza",agente_legal.get().getVencimientoPoliza().toString());
                            }else {
                                jsonObject.addProperty("vencimientoPoliza","");
                            }
                        }else{
                            jsonObject.addProperty("vencimientoPoliza","");
                        }
                        break;
                    case "idApoderado":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("idApoderado",agente_legal.get().isIdApoderado()?"SI":"NO");
                            }else {
                                jsonObject.addProperty("idApoderado","");
                            }
                        }else{
                            jsonObject.addProperty("idApoderado","");
                        }
                        break;
                    case "vencimientoIdApoderado":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("vencimientoIdApoderado",agente_legal.get().getVencimientoCedula().toString());
                            }else {
                                jsonObject.addProperty("vencimientoIdApoderado","");
                            }
                        }else{
                            jsonObject.addProperty("vencimientoIdApoderado","");
                        }
                        break;
                    case "contrato":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("vencimientoCedula",agente_legal.get().isIdApoderado()?"SI":"NO");
                            }else {
                                jsonObject.addProperty("vencimientoCedula","");
                            }
                        }else{
                            jsonObject.addProperty("vencimientoCedula","");
                        }
                        break;
                    case "vencimientoContrato":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("vencimientoContrato",agente_legal.get().getVencimientoContrato().toString());
                            }else {
                                jsonObject.addProperty("vencimientoContrato","");
                            }
                        }else{
                            jsonObject.addProperty("vencimientoContrato","");
                        }
                        break;
                    case "datosBancarios":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("datosBancarios",agente_legal.get().isDatosBancarios()?"SI":"NO");
                            }else {
                                jsonObject.addProperty("datosBancarios","");
                            }
                        }else{
                            jsonObject.addProperty("datosBancarios","");
                        }
                        break;
                    case "autoCNSF":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("autoCNSF",agente_legal.get().isAutoCNSF()?"SI":"NO");
                            }else {
                                jsonObject.addProperty("autoCNSF","");
                            }
                        }else{
                            jsonObject.addProperty("autoCNSF","");
                        }
                        break;
                    case "rfc":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("rfc",agente_legal.get().isRfc()?"SI":"NO");
                            }else {
                                jsonObject.addProperty("rfc","");
                            }
                        }else{
                            jsonObject.addProperty("rfc","");
                        }
                        break;
                    case "acta":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("acta",agente_legal.get().isActa()?"SI":"NO");
                            }else {
                                jsonObject.addProperty("acta","");
                            }
                        }else{
                            jsonObject.addProperty("acta","");
                        }
                        break;
                    case "poder":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("poder",agente_legal.get().isPoder()?"SI":"NO");
                            }else {
                                jsonObject.addProperty("poder","");
                            }
                        }else{
                            jsonObject.addProperty("poder","");
                        }
                        break;
                    case "cartaCompromiso":
                        if(userCrm.getArea() == CrmDatabaseKey.AREA_LEGAL){
                            if(agente_legal.isPresent()){
                                jsonObject.addProperty("cartaCompromiso",agente_legal.get().isCartaCompromiso()?"SI":"NO");
                            }else {
                                jsonObject.addProperty("cartaCompromiso","");
                            }
                        }else{
                            jsonObject.addProperty("cartaCompromiso","");
                        }
                        break;
                }
            }
            remplazaNull(llaves);
            _log.debug(jsonObject.toString());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JsonArray notificacionesAutomaticas(List<Notificaciones_auto> notificaciones_autos, Configuracion_Reporte configuracion_reporte) {
        inicializar(configuracion_reporte);
        List<String> llaves = new ArrayList<>();
        for(Notificaciones_auto notificaciones_auto: notificaciones_autos){
            jsonObject = new JsonObject();
            for(String columna: idCabeceras){
                if(!llaves.contains(mapaColumnas.get(Long.parseLong(columna)))){
                    llaves.add(mapaColumnas.get(Long.parseLong(columna)));
                }
                switch (mapaColumnas.get(Long.parseLong(columna))){
                    case "notificacion":
                        jsonObject.addProperty("notificacion",notificaciones_auto.getNotificacion());
                        break;
                    case "descripcion":
                        jsonObject.addProperty("notificacion",notificaciones_auto.getDescripcion());
                        break;
                }
            }
            remplazaNull(llaves);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JsonArray notificacionesManuales(List<Notificaciones_Manuales> notificaciones_manuales, Configuracion_Reporte configuracion_reporte) {
        mapaPeriodo();
        inicializar(configuracion_reporte);
        List<String> llaves = new ArrayList<>();
        for(Notificaciones_Manuales notificacionesManuales: notificaciones_manuales){
            jsonObject = new JsonObject();
            for(String columna: idCabeceras){
                if(!llaves.contains(mapaColumnas.get(Long.parseLong(columna)))){
                    llaves.add(mapaColumnas.get(Long.parseLong(columna)));
                }
                switch (mapaColumnas.get(Long.parseLong(columna))){
                    case "nombre":
                        jsonObject.addProperty("nombre",notificacionesManuales.getNombre());
                        break;
                    case "agenteCliente":
                        jsonObject.addProperty("agenteCliente",notificacionesManuales.getAgenteCliente());
                        break;
                    case "agenteClienteNombre":
                        jsonObject.addProperty("agenteClienteNombre",notificacionesManuales.getAgenteCliente());
                        break;
                    case "periodo":
                        jsonObject.addProperty("periodo",notificacionesManuales.isPeriodica()?"SI":"NO");
                        break;
                    case "fechaInicio":
                        jsonObject.addProperty("fechaInicio",notificacionesManuales.getFechaInicio().toString());
                        break;
                    case "numPeriodo":
                        try{
                            jsonObject.addProperty("numPeriodo",mapaPeriodo.get((long)notificacionesManuales.getOrdinalidad()));
                        }catch (NullPointerException exception){
                            jsonObject.addProperty("numPeriodo","");
                        }
                        break;
                    case "fechaFin":
                        jsonObject.addProperty("fechaFin",notificacionesManuales.getFechaFin().toString());
                        break;
                    case "correos":
                        jsonObject.addProperty("fechaFin",notificacionesManuales.getCorreos());
                        break;
                    case "texto":
                        jsonObject.addProperty("fechaFin",notificacionesManuales.getTexto());
                        break;
                }
            }
            remplazaNull(llaves);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JsonArray registroClientes(List<ClienteInfoResponse> clienteInfoResponses, List<ReporteCliente> reporteClientes, Configuracion_Reporte configuracion_reporte, long tipoPersona) {
        inicializar(configuracion_reporte);
        Optional<ClienteInfoResponse> clienteInfoResponse;
        clienteInfoResponses.removeAll(Collections.singleton(null));
        List<String> llaves = new ArrayList<>();
        for(ReporteCliente reporteCliente: reporteClientes){
            clienteInfoResponse = clienteInfoResponses.stream().filter(f-> Objects.equals(f.getCodigo(), reporteCliente.getCliente())).findFirst();
            jsonObject = new JsonObject();
            for(String columna: idCabeceras) {
                if(!llaves.contains(mapaColumnas.get(Long.parseLong(columna)))){
                    llaves.add(mapaColumnas.get(Long.parseLong(columna)));
                }
                switch (mapaColumnas.get(Long.parseLong(columna))) {
                    case "rfc":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("rfc",clienteInfoResponse.get().getRfc());
                        }else{
                            jsonObject.addProperty("rfc","");
                        }
                        break;
                    case "nombre_completo":
                        if(clienteInfoResponse.isPresent()){
                            if(CrmDatabaseKey.PERSONA_FISICA == tipoPersona){
                                jsonObject.addProperty("nombre_completo",clienteInfoResponse.get().getNombre() + " " + clienteInfoResponse.get().getPaterno() + " " + clienteInfoResponse.get().getMaterno());
                            }
                            else {
                                jsonObject.addProperty("nombre_completo",clienteInfoResponse.get().getNombre() + " " + clienteInfoResponse.get().getTipo_sociedad());
                            }
                        }else{
                            jsonObject.addProperty("nombre_completo","");
                        }
                        break;
                    case "fisica":
                        jsonObject.addProperty("fisica","FISICA");
                        break;
                    case "nombre":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("nombre",clienteInfoResponse.get().getNombre());
                        }else{
                            jsonObject.addProperty("nombre","");
                        }
                        break;
                    case "paterno":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("paterno",clienteInfoResponse.get().getPaterno());
                        }else{
                            jsonObject.addProperty("paterno","");
                        }
                        break;
                    case "materno":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("materno",clienteInfoResponse.get().getMaterno());
                        }else{
                            jsonObject.addProperty("materno","");
                        }
                        break;
                    case "cp":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("cp",clienteInfoResponse.get().getCp());
                        }else{
                            jsonObject.addProperty("cp","");
                        }
                        break;
                    case "calle":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("calle",clienteInfoResponse.get().getCalle());
                        }else{
                            jsonObject.addProperty("calle","");
                        }
                        break;
                    case "num_ext":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("num_ext",clienteInfoResponse.get().getNum_ext());
                        }else{
                            jsonObject.addProperty("num_ext","");
                        }
                        break;
                    case "colonia":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("colonia",clienteInfoResponse.get().getColonia());
                        }else{
                            jsonObject.addProperty("colonia","");
                        }
                        break;
                    case "municipio":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("municipio",clienteInfoResponse.get().getMunicipio());
                        }else{
                            jsonObject.addProperty("municipio","");
                        }
                        break;
                    case "estado":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("estado",clienteInfoResponse.get().getEstado());
                        }else{
                            jsonObject.addProperty("estado","");
                        }
                        break;
                    case "telefonos":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("telefonos",clienteInfoResponse.get().getTelefono() + "," + clienteInfoResponse.get().getTelefonosec());
                        }else{
                            jsonObject.addProperty("telefonos","");
                        }
                        break;
                    case "email":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("email",clienteInfoResponse.get().getEmail());
                        }else{
                            jsonObject.addProperty("email","");
                        }
                        break;
                    case "ejecutivo":
                        jsonObject.addProperty("ejecutivo",reporteCliente.getEjecutivo());
                        break;
                    case "oficina":
                        jsonObject.addProperty("oficina",reporteCliente.getOficina_agente());
                        break;
                    case "ocupacion":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("ocupacion",clienteInfoResponse.get().getOcupacion());
                        }else{
                            jsonObject.addProperty("ocupacion","");
                        }
                        break;
                    case "pais_nac":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("pais_nac",clienteInfoResponse.get().getPais_nac());
                        }else{
                            jsonObject.addProperty("pais_nac","");
                        }
                        break;
                    case "dom_mex":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("dom_mex",clienteInfoResponse.get().getDom_mex());
                        }else{
                            jsonObject.addProperty("dom_mex","");
                        }
                        break;
                    case "cuenta_prop":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("cuenta_prop",clienteInfoResponse.get().getCuenta_prop());
                        }else{
                            jsonObject.addProperty("cuenta_prop","");
                        }
                        break;
                    case "met_pago":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("met_pago",clienteInfoResponse.get().getMet_pago());
                        }else{
                            jsonObject.addProperty("met_pago","");
                        }
                        break;
                    case "nacionalidad":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("nacionalidad",clienteInfoResponse.get().getNacionalidad());
                        }else{
                            jsonObject.addProperty("nacionalidad","");
                        }
                        break;
                    case "moral":
                        jsonObject.addProperty("fisica","MORAL");
                        break;
                    case "rezon_social":
                        jsonObject.addProperty("rezon_social","");
                        break;
                    case "tipo_sociedad":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("tipo_sociedad",clienteInfoResponse.get().getTipo_sociedad());
                        }else{
                            jsonObject.addProperty("tipo_sociedad","");
                        }
                        break;
                    case "fecha_constitucion":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("fecha_constitucion",clienteInfoResponse.get().getFecha_constitucion());
                        }else{
                            jsonObject.addProperty("fecha_constitucion","");
                        }
                        break;
                    case "giro":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("giro",clienteInfoResponse.get().getGiro());
                        }else{
                            jsonObject.addProperty("giro","");
                        }
                        break;
                    case "folio_mercantil":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("folio_mercantil",clienteInfoResponse.get().getFolio_mercantil());
                        }else{
                            jsonObject.addProperty("folio_mercantil","");
                        }
                        break;
                    case "representante_legal":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("representante_legal",clienteInfoResponse.get().getRepresentante_legal());
                        }else{
                            jsonObject.addProperty("representante_legal","");
                        }
                        break;
                    case "fiel":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("fiel",clienteInfoResponse.get().getFiel());
                        }else{
                            jsonObject.addProperty("fiel","");
                        }
                        break;
                }
            }
            try{
                remplazaNull(llaves);
                _log.debug(jsonObject);
                jsonArray.add(jsonObject);
            }catch (Exception e){
                _log.error(e.getMessage());
            }

        }
        return jsonArray;
    }

    @Override
    public JsonArray registroCotizaciones(List<Cotizacion> cotizaciones, List<ClienteInfoResponse> clienteInfoResponses,Configuracion_Reporte configuracion_reporte, User usuarioConsutla) {
        inicializar(configuracion_reporte);
        mapaTipoSociedad();
        mapaAgentes();
        mapaControlSub();
        mapaProducto(usuarioConsutla);
        mapaTipoNegocio();
        Optional<ClienteInfoResponse> clienteInfoResponse;
        clienteInfoResponses.removeAll(Collections.singleton(null));
        List<String> llaves = new ArrayList<>();
        for(Cotizacion cotizacion: cotizaciones) {
            clienteInfoResponse = clienteInfoResponses.stream().filter(f-> Objects.equals(f.getCodigo(), cotizacion.getCodigo_cliente())).findFirst();
            jsonObject = new JsonObject();
            for(String columna: idCabeceras) {
                if(!llaves.contains(mapaColumnas.get(Long.parseLong(columna)))){
                    llaves.add(mapaColumnas.get(Long.parseLong(columna)));
                }
                switch (mapaColumnas.get(Long.parseLong(columna))) {
                    case "rfc":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("rfc",clienteInfoResponse.get().getRfc());
                        }else{
                            jsonObject.addProperty("rfc","");
                        }
                        break;
                    case "nombre":
                        if(clienteInfoResponse.isPresent()){
                            if("".equals(clienteInfoResponse.get().getTipo_sociedad())){
                                jsonObject.addProperty("nombre",clienteInfoResponse.get().getNombre() + " " + clienteInfoResponse.get().getPaterno() + " " + clienteInfoResponse.get().getMaterno());
                            }else{
                                jsonObject.addProperty("nombre",clienteInfoResponse.get().getNombre() + " " + clienteInfoResponse.get().getTipo_sociedad());
                            }
                        }else{
                            jsonObject.addProperty("nombre","");
                        }
                        break;
                    case "codigo":
                        jsonObject.addProperty("codigo",cotizacion.getCodigo_cliente());
                        break;
                    case "cp":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("cp",clienteInfoResponse.get().getCp());
                        }else{
                            jsonObject.addProperty("cp","");
                        }
                        break;
                    case "calle":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("calle",clienteInfoResponse.get().getCalle());
                        }else{
                            jsonObject.addProperty("calle","");
                        }
                        break;
                    case "num_ext":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("num_ext",clienteInfoResponse.get().getNum_ext());
                        }else{
                            jsonObject.addProperty("num_ext","");
                        }
                        break;
                    case "colonia":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("colonia",clienteInfoResponse.get().getColonia());
                        }else{
                            jsonObject.addProperty("colonia","");
                        }
                        break;
                    case "municipio":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("municipio",clienteInfoResponse.get().getColonia());
                        }else{
                            jsonObject.addProperty("municipio","");
                        }
                        break;
                    case "estado":
                        if(clienteInfoResponse.isPresent()){
                            jsonObject.addProperty("estado",clienteInfoResponse.get().getEstado());
                        }else{
                            jsonObject.addProperty("estado","");
                        }
                        break;
                    case "agente":
                        jsonObject.addProperty("agente",mapaAgentes.get(cotizacion.getId_agente()));
                        break;
                    case "producto":
                        jsonObject.addProperty("producto",mapaProducto.get(cotizacion.getProducto()));
                        break;
                    case "prima":
                        jsonObject.addProperty("prima",cotizacion.getPrima());
                        break;
                    case "tipo_movimiento":
                        jsonObject.addProperty("tipo_movimiento",cotizacion.getTipo_movimiento()==1?"NUEVO":"RENOVACION");
                        break;
                    case "tipo_negocio":
                        jsonObject.addProperty("tipo_negocio",mapaTipoNegocio.get(cotizacion.getTipo_negocio()));
                        break;
                    case "id_moneda":
                        jsonObject.addProperty("id_moneda",mapaMoneda.get((int)cotizacion.getId_moneda()));
                        break;
                    case "fecha_inicio":
                        jsonObject.addProperty("fecha_inicio",cotizacion.getFecha_inicio().toString());
                        break;
                    case "fecha_fin":
                        jsonObject.addProperty("fecha_fin",cotizacion.getFecha_fin().toString());
                        break;
                    case "fecha_solicitud":
                        jsonObject.addProperty("fecha_solicitud",cotizacion.getFecha_solicitud().toString());
                        break;
                    case "fecha_alta":
                        jsonObject.addProperty("fecha_alta",cotizacion.getFecha_alta().toString());
                        break;
                    case "control_subscripcion":
                        jsonObject.addProperty("control_subscripcion",mapaControlSub.get(cotizacion.getControl_subscripcion()));
                        break;
                    case "email":
                        jsonObject.addProperty("email",cotizacion.getEmail());
                        break;
                    case "gp_flag":
                        jsonObject.addProperty("gp_flag",cotizacion.getGp_flag()==1?"SI":"NO");
                        break;
                    case "canal":
                        jsonObject.addProperty("canal",cotizacion.getCanal());
                        break;
                    case "ejecutivo":
                        jsonObject.addProperty("ejecutivo",cotizacion.getEjecutivo());
                        break;
                }
            }
            remplazaNull(llaves);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JsonArray recordatoriosSeguimiento(List<Recordatorio_Tramite> recordatorio_tramites, Configuracion_Reporte configuracion_reporte) {
        List<String> llaves = new ArrayList<>();
        for(Recordatorio_Tramite recordatorio_tramite: recordatorio_tramites) {
            jsonObject = new JsonObject();
            for (String columna : idCabeceras) {
                if(!llaves.contains(mapaColumnas.get(Long.parseLong(columna)))){
                    llaves.add(mapaColumnas.get(Long.parseLong(columna)));
                }
                switch (mapaColumnas.get(Long.parseLong(columna))) {
                    case "tipo_accion":
                        jsonObject.addProperty("tipo_accion",recordatorio_tramite.getTipo_accion());
                        break;
                    case "folio":
                        jsonObject.addProperty("folio",recordatorio_tramite.getFolio());
                        break;
                    case "fecha_recordatorio":
                        jsonObject.addProperty("fecha_recordatorio",recordatorio_tramite.getFecha_recordatorio().toString());
                        break;
                }
            }
            remplazaNull(llaves);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JsonArray produccionPrima(List<ProduccionPrima> produccionPrimas, Configuracion_Reporte configuracion_reporte) {
        inicializar(configuracion_reporte);
        List<String> llaves = new ArrayList<>();
        for(ProduccionPrima produccion: produccionPrimas) {
            jsonObject = new JsonObject();
            for (String columna : idCabeceras) {
                if(!llaves.contains(mapaColumnas.get(Long.parseLong(columna)))){
                    llaves.add(mapaColumnas.get(Long.parseLong(columna)));
                }
                switch (mapaColumnas.get(Long.parseLong(columna))) {
                    case "agente":
                        jsonObject.addProperty("agente",produccion.getAgente());
                        break;
                    case "ejecutivo":
                        jsonObject.addProperty("ejecutivo",produccion.getEjecutivo());
                        break;
                    case "area":
                        jsonObject.addProperty("area",produccion.getArea());
                        break;
                    case "mes_anio":
                        jsonObject.addProperty("mes_anio",produccion.getMes() + "/" + produccion.getAnio());
                        break;
                    case "moneda":
                        jsonObject.addProperty("moneda",produccion.getMoneda());
                        break;
                    case "monto":
                        jsonObject.addProperty("monto",produccion.getMonto());
                        break;
                    case "tipo_cambio_bp":
                        jsonObject.addProperty("tipo_cambio_bp",produccion.getTipo_cambio_bp());
                        break;
                    case "conversion_bp":
                        jsonObject.addProperty("conversion_bp","");
                        break;
                    case "mes_real":
                        jsonObject.addProperty("mes_real",produccion.getReal());
                        break;
                    case "mes_budget":
                        jsonObject.addProperty("mes_budget",produccion.getBudget());
                        break;
                }
            }
            remplazaNull(llaves);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public JsonArray gastos(List<Presupuesto> presupuestos, Configuracion_Reporte configuracion_reporte, User usuarioConsulta) {
        inicializar(configuracion_reporte);
        mapaAgentes();
        mapaEjecutivos();
        mapaMoneda(usuarioConsulta);
        List<String> llaves = new ArrayList<>();
        for(Presupuesto gastos: presupuestos) {
            if(gastos.getTipo_presupuesto() != 0) {
                jsonObject = new JsonObject();
                for (String columna : idCabeceras) {
                    if(!llaves.contains(mapaColumnas.get(Long.parseLong(columna)))){
                        llaves.add(mapaColumnas.get(Long.parseLong(columna)));
                    }
                    switch (mapaColumnas.get(Long.parseLong(columna))) {
                        case "idAgente":
                            jsonObject.addProperty("idAgente",mapaAgentes.get(gastos.getId_agente()));
                            break;
                        case "idEjecutivo":
                            jsonObject.addProperty("idEjecutivo",mapaEjecutivos.get(gastos.getId_ejecutivo()));
                            break;
                        case "idArea":
                            jsonObject.addProperty("idArea",gastos.getId_area()== CrmDatabaseKey.NEGOCIO_M?"MD":"J");
                            break;
                        case "mes+anio":
                            jsonObject.addProperty("mes+anio",gastos.getMes() + "-" + gastos.getAnio());
                            break;
                        case "idMoneda":
                            jsonObject.addProperty("idMoneda",mapaMoneda.get((int)gastos.getId_moneda()));
                            break;
                        case "Monto":
                            jsonObject.addProperty("Monto",gastos.getMonto());
                            break;
                        case "acumulado_real":
                            jsonObject.addProperty("acumulado_real","");
                            break;
                        case "acumulado_budget":
                            jsonObject.addProperty("acumulado_budget","");
                            break;
                    }
                }
                remplazaNull(llaves);
                jsonArray.add(jsonObject);
            }

        }
        return jsonArray;
    }

    public void inicializar(Configuracion_Reporte configuracion_reporte){
        jsonArray = new JsonArray();
        idCabeceras = configuracion_reporte.getColumnas().split(",");
        catalogo_detalles = _Catalogo_DetalleLocalService.findByCodigoAndCatalogoDetallePadreId("CATCOLUMNAREPORTE",configuracion_reporte.getId_reporte() + "");
        mapaColumnas = catalogo_detalles.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_Detalle::getValorS));
    }

    public void mapaEjecutivos(){
        List<User_Crm> ejecutivos = _User_CrmLocalService.getUsers_CrmByPerfil(CrmDatabaseKey.ID_PERFIL_EJECUTIVO_VENTAS);
        List<User_Crm> analistas = _User_CrmLocalService.getUsers_CrmByPerfil(CrmDatabaseKey.ID_PERFIL_ANALISTA_VENTAS);
        mapaEjecutivos = new HashMap<>();
        Stream.concat(ejecutivos.stream(),analistas.stream()).forEach(user_crm -> {
            try {
                mapaEjecutivos.put((long)user_crm.getUserId(),UserLocalServiceUtil.getUserById(user_crm.getUserId()).getFullName().toUpperCase());
            } catch (PortalException e) {
                _log.error(e.getMessage());
            }
        });
    }

    public void mapaOficinas(User usuarioConsutla){
        mapaOficinas = new HashMap<>();
        try {
            /*ListaRegistro listaRegistro = _CrmGenericoService.getCatalogo(
                    CrmServiceKey.TMX_CTE_ROW_TODOS,
                    CrmServiceKey.TMX_CTE_TRANSACCION_GET,
                    CrmServiceKey.LIST_CAT_OFICINA,
                    CrmServiceKey.TMX_CTE_CAT_ACTIVOS,
                    usuarioConsutla.getScreenName(),
                    MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73
            );
            mapaOficinas = listaRegistro.getLista().stream().collect(Collectors.toMap(Registro::getId,Registro::getValor));*/
            List<Catalogo_Detalle> oficianas = _Catalogo_DetalleLocalService.findByCodigo("CATOFICINA");
            mapaOficinas = oficianas.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_Detalle::getDescripcion));
        }catch (Exception e){
            _log.error(e.getMessage());
        }
    }

    public void mapaTipoSociedad(){
        mapaSociedad = new HashMap<>();
        List<Catalogo_Detalle> tiposSociedad = _Catalogo_DetalleLocalService.findByCodigo("CATTIPSOC");
        mapaSociedad = tiposSociedad.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_Detalle::getDescripcion));
    }

    public void mapaPerfilContactos(){
        mapaPerfilContacto = new HashMap<>();
        List<Catalogo_Detalle> perfilesContacto = _Catalogo_DetalleLocalService.findByCodigo("CATPERCON");
        mapaPerfilContacto = perfilesContacto.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_Detalle::getDescripcion));
    }

    public void mapaTipoCedula(){
        mapaTipoCedula = new HashMap<>();
        List<Catalogo_Detalle> tipoCedula = _Catalogo_DetalleLocalService.findByCodigo("CATTIPCED");
        mapaTipoCedula = tipoCedula.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_Detalle::getDescripcion));
    }

    public void mapaPeriodo(){
        mapaPeriodo = new HashMap<>();
        List<Catalogo_Detalle> perfilesContacto = _Catalogo_DetalleLocalService.findByCodigo("CATPER");
        mapaPeriodo = perfilesContacto.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_Detalle::getDescripcion));
    }

    public void mapaAgentes(){
        mapaAgentes = new HashMap<>();
        List<Agente> agentes = _AgenteLocalService.getAgentes(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
        String nombre;
        _log.info(mapaSociedad);
        for(Agente agente: agentes){
            if(agente.getTipoPersona() == CrmDatabaseKey.PERSONA_FISICA){
                nombre = agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM();
            }else{
                nombre = agente.getNombre() + " " + mapaSociedad.get((long)agente.getTipoSociedad());
            }
            mapaAgentes.put(agente.getAgenteId(),nombre);
        }
    }

    public void mapaControlSub(){
        mapaControlSub = new HashMap<>();
        mapaControlSub.put(1,"Respuesta Cliente");
        mapaControlSub.put(2,"Cotizaci&oacute;n");
        mapaControlSub.put(3,"Requiere Autorizaci&oacute;n");
    }

    public void mapaProducto(User usuarioConsutla){
        mapaProducto = new HashMap<>();
        try {
            ListaRegistro registro = _CrmGenericoService.getCatalogo(0,"","CATPRODUCTO",0,usuarioConsutla.getScreenName(),MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73);
            registro.getLista().forEach(f ->{
                mapaProducto.put(f.getCodigo(),f.getDescripcion());
            });
            registro = _CrmGenericoService.getCatalogo(0,"","CATPRODUCTO",0,"0004",usuarioConsutla.getScreenName(),MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73);
            registro.getLista().forEach(f ->{
                if(!mapaProducto.containsKey(f.getCodigo())){
                    mapaProducto.put(f.getCodigo(),f.getDescripcion());
                }
            });
        }catch (Exception e){
            _log.error(e.getMessage());
        }
    }

    public void mapaTipoNegocio(){
        mapaTipoNegocio = new HashMap<>();
        mapaTipoNegocio.put("1","DIRECTO");
        mapaTipoNegocio.put("2","COASEGURO L&Iacute;DER (CEDIDO)");
        mapaTipoNegocio.put("3","COASEGURO SEGUIDOR (ACEPTADO)");
        mapaTipoNegocio.put("4","REASEGURO L&Iacute;DER (FACULTATIVO)");
        mapaTipoNegocio.put("5","REASEGURO SEGUIDOR (TOMADO)");
    }

    public void mapaMoneda(User usuarioConsutla){
        mapaMoneda = new HashMap<>();
        try {
            CatalogoMoneda catalogoMoneda = _CrmGenericoService.getCatalogoMoneda(usuarioConsutla.getScreenName(),MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73);
            mapaMoneda = catalogoMoneda.getLista().stream().collect(Collectors.toMap(Moneda::getId,Moneda::getValor));
        }catch (Exception e){
            _log.error(e.getMessage());
        }
    }

    public synchronized void remplazaNull(){
        Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for(Map.Entry<String,JsonElement> entry : entrySet){
            if(entry.getValue().isJsonNull()){
                jsonObject.remove(entry.getKey());
                jsonObject.addProperty(entry.getKey(),"");
            }
        }
    }

    public void remplazaNull(List<String> llaves){
        String valor;
        for(String llave: llaves){
            valor = jsonObject.get(llave).toString();
            if("null".equals(valor)){
                //jsonObject.add
                //jsonObject.remove(llave);
                jsonObject.addProperty(llave,"-");
            }
        }
    }
}
