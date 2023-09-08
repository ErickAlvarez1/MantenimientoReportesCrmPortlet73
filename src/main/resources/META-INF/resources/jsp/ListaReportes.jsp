<%--
  Created by IntelliJ IDEA.
  User: erickalvarez
  Date: 16/11/21
  Time: 17:41
  To change this template use File | Settings | File Templates.

--%>
<%@ include file="../init.jsp" %>
<jsp:useBean id="current" class="java.util.Date" />
<portlet:actionURL var="muestraReporteUrl" name="/crm/action/mantenimientoReportes/muestraReporte" />
<portlet:actionURL var="editaReporteUrl" name="/crm/action/mantenimientoReportes/editaReporte" />
<portlet:resourceURL id="/crm/resource/mantenimientoReportes/eliminaReporte" var="eliminaReporteUrl" cacheability="FULL"/>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link href="<%=request.getContextPath()%>/css/main.css?v=${version}" media="all" rel="stylesheet" type="text/css" />
<section class="listaReportes">
    <div class="row">
        <div class="col-md-12">
            <p class="font-weight-bold h3-responsive center-block mt-4 mb-4 animated zoomInDown animation-delay-5">
                Lista de Reportes Creados
            </p>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <ul class="list-group">
                <c:forEach items="${reportes}" var="reporte">
                    <li class="list-group-item">
                        <a onclick="MuestraReporte(${reporte.id_configuracion_reporte})" class="text-primary">
                            ${reporte.nombre_reporte}
                        </a>
                        <a class="btn-blue btn-floating btn-sm" onclick="EditarReporte(${reporte.id_configuracion_reporte})">
                            <i class="fas fa-pencil-alt"></i>
                        </a>
                        <a class="btn-blue btn-floating btn-sm" onclick="EliminaReporte(${reporte.id_configuracion_reporte},this)">
                            <i class="far fa-trash-alt"></i>
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
    <div class="row">
        <form class="mb-4" id="formMuestraReporte" action="${muestraReporteUrl}" method="post">
            <label>
                <input type="hidden" id="id_reporte" name="id_reporte" value="">
            </label>
        </form>
    </div>
    <div class="row">
        <form class="mb-4" id="formEditaReporte" action="${editaReporteUrl}" method="post">
            <label>
                <input type="hidden" id="id_reporteEdicion" name="id_reporte" value="">
            </label>
        </form>
    </div>
    <div class="row mt-5">
        <div class="col-sm-12 text-center">
            <div class="btn btn-blue" id="btnRegresar" onclick="Regresar()">Regresar</div>
        </div>
    </div>
</section>
<script src="<%=request.getContextPath()%>/js/listaReportes.js?v=${version}"></script>
<script>
const eliminaReporteUrl = "${eliminaReporteUrl}";
</script>