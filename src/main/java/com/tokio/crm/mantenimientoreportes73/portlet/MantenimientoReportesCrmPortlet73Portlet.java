package com.tokio.crm.mantenimientoreportes73.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;
import com.tokio.crm.mantenimientoreportes73.constants.MantenimientoReportesCrmPortlet73PortletKeys;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;

import java.io.IOException;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author urielfloresvaldovinos
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=MantenimientoReportesCrmPortlet73",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + MantenimientoReportesCrmPortlet73PortletKeys.MANTENIMIENTOREPORTESCRMPORTLET73,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.requires-namespaced-parameters=false",
		"com.liferay.portlet.private-request-attributes=false"
	},
	service = Portlet.class
)
public class MantenimientoReportesCrmPortlet73Portlet extends MVCPortlet {
	
	private static final Log _log = LogFactoryUtil.getLog(MantenimientoReportesCrmPortlet73Portlet.class);
	@Reference
	CrmGenerico _CrmGenericoService;
	
	@Reference
	User_CrmLocalService _User_CrmLocalService;
	
	@Reference
	Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;

	User usuario;

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		usuario = (User) renderRequest.getAttribute(WebKeys.USER);
		cargarInformacion(renderRequest);
		super.doView(renderRequest, renderResponse);
	}

	public void cargarInformacion(RenderRequest renderRequest){
		User_Crm user_crm;
		try {
			user_crm = _User_CrmLocalService.getUser_Crm(new Long(usuario.getUserId()).intValue());
			List<Catalogo_Detalle> reportes = _Catalogo_DetalleLocalService.findByCodigo("CATREPORTES");
			List<Catalogo_Detalle> columnas = _Catalogo_DetalleLocalService.findByCodigoAndCatalogoDetallePadreId("CATCOLUMNAREPORTE",reportes.get(0).getCatalogoDetalleId() + "");
			renderRequest.setAttribute("reportes",reportes);
			renderRequest.setAttribute("columnas",columnas);
			renderRequest.setAttribute("reporteDefault",reportes.get(0).getCatalogoDetalleId());
			renderRequest.setAttribute("perfil",user_crm.getPerfilId());
			renderRequest.setAttribute("area",user_crm.getArea());
		}catch (Exception e){
			_log.error(e.getMessage(), e);
		}
	}
}