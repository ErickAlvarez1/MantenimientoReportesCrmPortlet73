<%@ include file="/init.jsp" %>

<link href="<%=request.getContextPath()%>/css/main.css?v=${version}" media="all" rel="stylesheet" type="text/css" />
<%--<link href="<%=request.getContextPath()%>/css/rowReorder.bootstrap4.min.css" media="all" rel="stylesheet" type="text/css" />-->
<%--<link href="<%=request.getContextPath()%>/css/jquery.dataTables.min.css" media="all" rel="stylesheet" type="text/css" />--%>
<link href="<%=request.getContextPath()%>/css/buttons.dataTables.min.css" media="all" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/keyTable.dataTables.min.css" media="all" rel="stylesheet" type="text/css" />
<link href="<%=request.getContextPath()%>/css/select.dataTables.min.css" media="all" rel="stylesheet" type="text/css" />
<portlet:actionURL var="listaReportesUrl" name="/crm/action/mantenimientoReportes/listaReportes" />
<portlet:resourceURL id="/crm/resource/mantenimientoReportes/obtieneColumnas" var="obtieneColumnasURL" cacheability="FULL"/>
<portlet:resourceURL id="/crm/resource/mantenimientoReportes/guardaReporte" var="guardaReporteURL" cacheability="FULL"/>
<portlet:resourceURL id="/crm/resource/mantenimientoReportes/guardaEdicionReporte" var="guardaEdicionReporteURL" cacheability="FULL"/>

<section class="dynamicTables">
	<div class="row">
		<div class="col-md-12">
			<p class="font-weight-bold h3-responsive center-block mt-4 mb-4 animated zoomInDown animation-delay-5">
				Lista de Reportes Existentes
			</p>
		</div>
	</div>
	<div class="row">
		<div class="col-md-2">
			<input type="hidden" name="id_reporte" id="id_reporte" value="${reporte.id_configuracion_reporte}" class="form-control"/>
		</div>
		<div class="col-md-6">
			<div class="md-form form-group">
				<input type="text" name="nombre_reporte" id="nombre_reporte" value="${reporte.nombre_reporte}" class="form-control">
				<label for="nombre_reporte" >
					<liferay-ui:message key="MANTENIMIENTOREPORTESCRMPORTLET73.AltaReporte.NombreReporte"/>
				</label>
			</div>
		</div>
		<div class="col-md-2">
			<div class="md-form form-group">
				<form class="mb-4" id="formListaReportes" action="${listaReportesUrl}" method="post">
				</form>
				<div class="btn btn-blue" id="btnListaReportes" onclick="">Lista Reportes</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-2">
			<ul id="listReportes" class="list-group">
				<c:forEach items="${reportes}" var="reporte">
					<li class="list-group-item${reporteDefault==reporte.catalogoDetalleId?' active':''}" value="${reporte.catalogoDetalleId}"><a>${reporte.descripcion}</a></li>
				</c:forEach>
			</ul>
		</div>
		<div class="col-10">
			<div class="row">
				<div class="col-sm-6 col-md-4 col-md-offset-1">
					<div class="section-heading text-left">
						<h5 class="title">DISPONIBLES</h5>
					</div>
					<table class="table custom option" id="columnasReporte">
						<thead>
							<tr>
								<td></td>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${columnas}" var="columna">
								<tr>
									<td>${columna.descripcion}<input tabla="columnasReporte" type="hidden" value="${columna.catalogoDetalleId}"/></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="col-sm-6 col-md-2 control" style="text-align:  center; margin: auto 0px;">
					<div> <a class="btn btn-blue allLeft"> << </a> </div>
					<div> <a class="btn btn-blue left"> < </a> </div>
					<div> <a class="btn btn-blue right"> > </a> </div>
					<div> <a class="btn btn-blue allRight"> >>  </a> </div>
				</div>
				<div class="col-sm-6 col-md-4 ">
					<div class="section-heading text-left">
						<h5 class="title">SELECCIONADOS</h5>
					</div>
					<div class="row">
						<a class="btn btn-blue waves-effect waves-light" onclick="subeSeleccion();"><i class="fa fa-angle-up" aria-hidden="true"></i></a>
						<a class="btn btn-blue waves-effect waves-light" onclick="bajaSeleccion();"><i class="fa fa-angle-down" aria-hidden="true"></i></a>
					</div>
					<table class="table custom result" id="columnasReporteSeleccionas">
						<thead>
							<tr>
								<td></td>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${columnasReporte}" var="columna">
								<tr>
									<td>${columna.descripcion}<input tabla="columnasReporte" type="hidden" value="${columna.catalogoDetalleId}"/></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				
				</div>
			</div>
			<div class="row" id="tablaOculta" style="${fn:length(columnasLegal)> 0?'':'display:none;'}">
				<div class="col-sm-6 col-md-4 col-md-offset-1">
					<div class="section-heading text-left">
						<h5 class="title" id="tituloReporteAux">Legal</h5>
					</div>
					<table class="table custom option" id="columnasReporteAux">
						<thead>
						<tr>
							<td></td>
						</tr>
						</thead>
						<tbody>
							<c:forEach items="${columnasLegal}" var="columna">
								<tr>
									<td>${columna.descripcion}<input tabla="columnasReporteSeleccionas" type="hidden" value="${columna.catalogoDetalleId}"/></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<div class="row mt-5">
		<div class="col-sm-12 text-center">
			<div class="btn btn-pink" id="btnCancelar" onclick="">Cancelar</div>
			<div class="btn btn-blue" id="btnGuardar" onclick="">Guardar</div>
		</div>
	</div>
</section>

<%--<script src="<%=request.getContextPath()%>/js/jquery-ui.min.js"></script>--%>
<script src="<%=request.getContextPath()%>/js/main.js?v=${version}"></script>
<script src="<%=request.getContextPath()%>/js/jquery.dataTables.min.js"></script>
<script src="<%=request.getContextPath()%>/js/dataTables.select.min.js"></script>
<script src="<%=request.getContextPath()%>/js/dataTables.buttons.min.js"></script>
<script src="<%=request.getContextPath()%>/js/buttons.html5.min.js"></script>
<%--<script src="<%=request.getContextPath()%>/js/dataTables.keyTable.min.js"></script>--%>
<%--<script src="<%=request.getContextPath()%>/js/dataTables.rowReorder.min.js"></script>--%>
<script src="<%=request.getContextPath()%>/js/pdfmake.min.js"></script>
<script src="<%=request.getContextPath()%>/js/vfs_fonts.js"></script>
<script>
	const obtieneColumnasURL = "${obtieneColumnasURL}";
	const guardaReporteURL = "${guardaReporteURL}";
	const guardaEdicionReporteURL = "${guardaEdicionReporteURL}";
	const edicion = "${edicion}";
	const perfil = "${pefil}";
	const area = "${area}";
</script>