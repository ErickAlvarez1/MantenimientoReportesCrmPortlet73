package com.tokio.crm.mantenimientoreportes73.commands.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.mantenimientoreportes73.constants.MantenimientoReportesCrmPortlet73PortletKeys;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.model.Configuracion_Reporte;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;
import com.tokio.crm.servicebuilder73.service.Configuracion_ReporteLocalService;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(immediate = true, property = {"javax.portlet.init-param.copy-request-parameters=true",
        "javax.portlet.name=" + MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,
        "mvc.command.name=/crm/action/mantenimientoReportes/editaReporte"}, service = MVCActionCommand.class)

public class EditaReportesCommand extends BaseMVCActionCommand {
    private static final Log _log = LogFactoryUtil.getLog(EditaReportesCommand.class);
    
    @Reference
	Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;
    
    @Reference
    Configuracion_ReporteLocalService _Configuracion_ReporteLocalService;

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) {
        try{
            long id_reporte = Long.parseLong(actionRequest.getParameter("id_reporte"));
            _log.debug(id_reporte);
            Configuracion_Reporte configuracion_reporte = _Configuracion_ReporteLocalService.getConfiguracion_Reporte(id_reporte);
            String[] idCabeceras = configuracion_reporte.getColumnas().split(",");
            List<Catalogo_Detalle> reportes = _Catalogo_DetalleLocalService.findByCodigo("CATREPORTES");
            List<Catalogo_Detalle> catalogo_detalles = _Catalogo_DetalleLocalService.findByCodigoAndCatalogoDetallePadreId("CATCOLUMNAREPORTE",configuracion_reporte.getId_reporte() + "");
            long idColumna;
            List<Catalogo_Detalle> columnasReporte = new ArrayList<>();
            List<Catalogo_Detalle> columnasEdicion = new ArrayList<>();
            List<Catalogo_Detalle> columnasEdicionLegal = new ArrayList<>();
            Catalogo_Detalle catalogo_detalle;
            for(String id: idCabeceras){
                catalogo_detalle = _Catalogo_DetalleLocalService.getCatalogo_Detalle(Long.parseLong(id));
                columnasReporte.add(catalogo_detalle);
            }
            for(Catalogo_Detalle columna: catalogo_detalles) {
                _log.debug(columna.getCodigo());
                if (!"2".equals(columna.getCodigo())) {
                    if(!columnasReporte.contains(columna)) {
                        columnasEdicion.add(columna);
                    }
                } else {
                    _log.debug("Columnas de legal");
                    if (configuracion_reporte.getId_reporte() == CrmDatabaseKey.ALTA_DE_AGENTES) {
                        if(!columnasReporte.contains(columna)) {
                            columnasEdicionLegal.add(columna);
                        }
                    }
                }
            }
            actionRequest.setAttribute("reportes",reportes);
            _log.debug(columnasReporte);
            _log.debug(columnasEdicion);
            _log.debug(columnasEdicionLegal);
            actionRequest.setAttribute("reporte",configuracion_reporte);
            actionRequest.setAttribute("reporteDefault",configuracion_reporte.getId_reporte());
            actionRequest.setAttribute("columnas",columnasEdicion);
            actionRequest.setAttribute("columnasReporte",columnasReporte);
            actionRequest.setAttribute("columnasLegal",columnasEdicionLegal);
            actionRequest.setAttribute("edicion",1);
        }catch (Exception e){
            _log.error(e.getMessage());
        }
        actionResponse.setRenderParameter("jspPage", "/view.jsp");
    }
}
