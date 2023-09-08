package com.tokio.crm.mantenimientoreportes73.commands.resource;

import com.google.gson.Gson;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.crm.crmservices73.Bean.SimpleResponse;
import com.tokio.crm.mantenimientoreportes73.constants.MantenimientoReportesCrmPortlet73PortletKeys;
import com.tokio.crm.servicebuilder73.model.Configuracion_Reporte;
import com.tokio.crm.servicebuilder73.service.Configuracion_ReporteLocalService;

import java.io.PrintWriter;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        immediate = true,
        property = {
                "javax.portlet.name=" + MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,
                "mvc.command.name=/crm/resource/mantenimientoReportes/eliminaReporte"
        },
        service = MVCResourceCommand.class
)
public class EliminaReporteCommand extends BaseMVCResourceCommand {
    private static final Log _log = LogFactoryUtil.getLog(EliminaReporteCommand.class);
    
    @Reference
    Configuracion_ReporteLocalService _Configuracion_ReporteLocalService;

    User usuario;
    @Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws Exception {
        SimpleResponse respuesta = new SimpleResponse();
        int id_reporte = ParamUtil.getInteger(resourceRequest, "id_reporte");
        _log.debug(id_reporte);
        Gson gson = new Gson();
        try{
            usuario = (User) resourceRequest.getAttribute(WebKeys.USER);
            Configuracion_Reporte configuracion_reporte = _Configuracion_ReporteLocalService.deleteConfiguracion_Reporte(id_reporte);
            respuesta.setCode(0);
            respuesta.setMsg("Reporte eliminado con éxito.");
        }catch (Exception e){
            respuesta.setCode(1);
            respuesta.setMsg(e.getMessage());
            _log.error(e.getMessage());
        }
        String responseString = gson.toJson(respuesta);
        PrintWriter writer = resourceResponse.getWriter();
        writer.write(responseString);
    }
}
