package com.tokio.crm.mantenimientoreportes73.commands.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.tokio.crm.mantenimientoreportes73.constants.MantenimientoReportesCrmPortlet73PortletKeys;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.model.Configuracion_Reporte;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;
import com.tokio.crm.servicebuilder73.service.Configuracion_ReporteLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(immediate = true, property = {"javax.portlet.init-param.copy-request-parameters=true",
        "javax.portlet.name=" + MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,
        "mvc.command.name=/crm/action/mantenimientoReportes/muestraReporte"}, service = MVCActionCommand.class)

public class MuestraReportesCommand extends BaseMVCActionCommand {
    private static final Log _log = LogFactoryUtil.getLog(MuestraReportesCommand.class);
    
    @Reference
    Configuracion_ReporteLocalService _Configuracion_ReporteLocalService;
    
    @Reference
	Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) {
        try{
            long id_reporte = Long.parseLong(actionRequest.getParameter("id_reporte"));
            _log.debug(id_reporte);
            Configuracion_Reporte configuracion_reporte = _Configuracion_ReporteLocalService.getConfiguracion_Reporte(id_reporte);
            String[] idCabeceras = configuracion_reporte.getColumnas().split(",");
            List<Catalogo_Detalle> catalogo_detalles = _Catalogo_DetalleLocalService.findByCodigoAndCatalogoDetallePadreId("CATCOLUMNAREPORTE",configuracion_reporte.getId_reporte() + "");
            Map<Long,String> mapaColumnas = catalogo_detalles.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_Detalle::getDescripcion));
            long idColumna;
            List<String> columnas = new ArrayList<>();
            for(String id : idCabeceras){
                idColumna = Long.parseLong(id);
                columnas.add(mapaColumnas.get(idColumna));
            }
            actionRequest.setAttribute("reporte",configuracion_reporte);
            actionRequest.setAttribute("columnas",columnas);
        }catch (Exception e){
            _log.error(e.getMessage());
        }
        actionResponse.setRenderParameter("jspPage", "/jsp/reporte.jsp");
    }
}
