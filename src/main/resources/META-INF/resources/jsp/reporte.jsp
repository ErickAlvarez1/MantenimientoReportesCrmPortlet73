
<%--
  Created by IntelliJ IDEA.
  User: erickalvarez
  Date: 18/11/21
  Time: 20:03
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="../init.jsp" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/jquery-ui.css">
<portlet:actionURL var="listaReportesUrl" name="/crm/action/mantenimientoReportes/listaReportes" />
<portlet:resourceURL id="/crm/resource/mantenimientoReportes/consultaReporte" var="consultaReporteURL" cacheability="FULL"/>

<section class="upper-case-all">
    <div class="section-heading">
        <div class="container-fluid">
            <h1 class="title text-left">
                <liferay-ui:message key="ManteniminetoReportesCrm.titulo" />
            </h1>
        </div>
    </div>
    <div class="section-nav-wrapper">
        <ul class="nav nav-tabs nav-justified light-blue darken-4" role="tablist">
            <li class="nav-item active">
                <a class="nav-link " data-toggle="tab" href="#reporte" role="tab">
                    ${reporte.nombre_reporte}
                </a>
            </li>
        </ul>
    </div>
    <div class="tab-content">
        <div class="tab-pane active" id="reporte" role="tabpanel">
            <div class="form-group row">
                <div class="col-3">
                </div>
                <div class="col-3">
                    <%--<div class="md-form form-group">
                        <input placeholder="" type="date" id="fechaInicio" name="fechaInicio" class="form-control datepicker">
                        <label for="fechaInicio">
                            <liferay-ui:message key="ManteniminetoReportesCrm.dic_fechaInicio" />
                        </label>
                    </div>--%>
                        <div id="picker-fechaInicio" class="md-form md-outline input-with-post-icon datepicker">
                            <input placeholder="Seleccionar Fecha" type="text" id="fechaInicio" name="fechaInicio" class="form-control datepicker">
                            <label for="fechaInicio">
                                <liferay-ui:message key="ManteniminetoReportesCrm.dic_fechaInicio" />
                            </label>
                            <i class="fas fa-calendar input-prefix" tabindex=0></i>
                        </div>
                </div>
                <div class="col-3">
                    <%--<div class="md-form form-group">
                        <input placeholder="" type="date" id="fechaTermino" name="fechaTermino" class="form-control datepicker">
                        <label for="fechaTermino">
                            <liferay-ui:message key="ManteniminetoReportesCrm.dic_fechaFin" />
                        </label>
                    </div>--%>
                        <div id="picker-fechaTermino" class="md-form md-outline input-with-post-icon datepicker">
                            <input placeholder="Seleccionar Fecha" type="text" id="fechaTermino" name="fechaTermino" class="form-control datepicker">
                            <label for="fechaTermino">
                                <liferay-ui:message key="ManteniminetoReportesCrm.dic_fechaFin" />
                            </label>
                            <i class="fas fa-calendar input-prefix" tabindex=0></i>
                        </div>
                </div>
                <div class="col"></div>
                <div class="col-2">
                    <form class="mb-4" id="formListaReportes" action="${listaReportesUrl}" method="post">
                    </form>
                    <div class="btn btn-blue waves-effect waves-light" id="btnRegresar">Regresar</div>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12 text-center">
                    <button class="btn btn-rounded btn-blue" id="consultaReporte" onclick="ConsultaReporte(${reporte.id_configuracion_reporte})">
                        <liferay-ui:message key="ManteniminetoReportesCrm.button.generarReporte" />
                    </button>
                </div>
            </div>
            <div class="table-wrapper">
                <table class="table data-table-test table-bordered" style="width: 100%;" id="tablaReporte">
                    <thead class="btn-blue" style="color: #FFFFFF; background-color: #43aee9">
                    <tr>
                        <c:forEach items="${columnas}" var="columna">
                            <th>${columna}</th>
                        </c:forEach>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</section>
<script src="<%=request.getContextPath()%>/js/jquery-ui.min.js"></script>
<script src="<%=request.getContextPath()%>/js/reporte.js?v=${version}"></script>
<script src="<%=request.getContextPath()%>/js/jquery.dataTables.min.js"></script>
<script src="<%=request.getContextPath()%>/js/dataTables.buttons.min.js"></script>
<script src="<%=request.getContextPath()%>/js/pdfmake.min.js"></script>
<script src="<%=request.getContextPath()%>/js/vfs_fonts.js"></script>
<script src="<%=request.getContextPath()%>/js/buttons.html5.min.js"></script>
<script type="text/javascript">
    const spanishJson = "<%=request.getContextPath()%>" + "/js/dataTables.spanish.json";
    const consultaReporteURL = "${consultaReporteURL}";
</script>
