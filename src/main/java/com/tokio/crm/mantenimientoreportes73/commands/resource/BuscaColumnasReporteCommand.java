package com.tokio.crm.mantenimientoreportes73.commands.resource;

import com.google.gson.Gson;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.tokio.crm.crmservices73.Bean.SimpleResponse;
import com.tokio.crm.mantenimientoreportes73.constants.MantenimientoReportesCrmPortlet73PortletKeys;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;

import java.io.PrintWriter;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
        immediate = true,
        property = {
                "javax.portlet.name=" + MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,
                "mvc.command.name=/crm/resource/mantenimientoReportes/obtieneColumnas"
        },
        service = MVCResourceCommand.class
)
public class BuscaColumnasReporteCommand extends BaseMVCResourceCommand {
    private static final Log _log = LogFactoryUtil.getLog(BuscaColumnasReporteCommand.class);
    
    @Reference
	Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;

    @Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws Exception {
        SimpleResponse respuesta = new SimpleResponse();
        String id_reporte = ParamUtil.getString(resourceRequest, "id_reporte");
        _log.debug(id_reporte);
        Gson gson = new Gson();
        try{
            List<Catalogo_Detalle> lista = _Catalogo_DetalleLocalService.findByCodigoAndCatalogoDetallePadreId("CATCOLUMNAREPORTE",id_reporte);
            _log.debug(lista);
            respuesta.setCode(0);
            respuesta.setMsg("OK");
            respuesta.setOtro(gson.toJson(lista));
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
