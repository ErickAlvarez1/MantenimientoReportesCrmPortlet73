package com.tokio.crm.mantenimientoreportes73.commands.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.tokio.crm.mantenimientoreportes73.constants.MantenimientoReportesCrmPortlet73PortletKeys;
import com.tokio.crm.servicebuilder73.model.Configuracion_Reporte;
import com.tokio.crm.servicebuilder73.service.Configuracion_ReporteLocalService;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(immediate = true, property = {"javax.portlet.init-param.copy-request-parameters=true",
        "javax.portlet.name=" + MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,
        "mvc.command.name=/crm/action/mantenimientoReportes/listaReportes"}, service = MVCActionCommand.class)

public class ListaReportesCommand extends BaseMVCActionCommand {
    private static final Log _log = LogFactoryUtil.getLog(ListaReportesCommand.class);
    
    @Reference
    Configuracion_ReporteLocalService _Configuracion_ReporteLocalService;

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) {
        try{
            List<Configuracion_Reporte> configuracion_reportes = _Configuracion_ReporteLocalService.getConfiguracion_Reportes(0,_Configuracion_ReporteLocalService.getConfiguracion_ReportesCount());
            _log.debug(configuracion_reportes);
            actionRequest.setAttribute("reportes",configuracion_reportes);
        }catch (Exception e){
            _log.error(e.getMessage());
        }
        actionResponse.setRenderParameter("jspPage", "/jsp/ListaReportes.jsp");
    }
}
