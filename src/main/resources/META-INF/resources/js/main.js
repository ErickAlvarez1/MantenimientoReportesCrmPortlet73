var tableOption  = null;
var tableResult  = null;

$(document).ready(function(){

	tableOption = $('.table.option').DataTable({
		scrollY:        '50vh',
		scrollCollapse: true,
		paging: false,
		"searching": false,
		info:false
	});

	tableResult = $('.table.result').DataTable({
		scrollY:        '50vh',
		scrollCollapse: true,
		paging: false,
		"searching": false,
		info:false,
		"ordering": false,
		ordered: false
		//rowReorder: true,
	});

});

$('.custom tbody').on( 'click', 'tr', function () {
	 $(this).toggleClass('active');
} );


$("a.allLeft").on("click", function(){

	$("table.result tbody tr").addClass("active");
	if( $("table.result tbody tr.active").length  >0 && tableResult.data().count()>0){
		let tablaOrigen;
		$("table.result tbody tr.active").each(function(key,data) {
			tablaOrigen = $("#" + $(data).find("input").attr("tabla")).DataTable();;
			pasa( tableResult,tablaOrigen ,this);
		});
		
	}
	$("table.option tbody tr").removeClass("active");

});

$("a.left").on("click", function(){
	if( $("table.result tbody tr.active").length  >0 && tableResult.data().count()>0 ){
		let tablaOrigen;
		$("table.result tbody tr.active").each(function(key,data) {
			tablaOrigen = $("#" + $(data).find("input").attr("tabla")).DataTable();;
			pasa(tableResult,tablaOrigen,this)
		});
	}
	$("table.option tbody tr").removeClass("active");
	
});

$("a.right").on("click", function(){
	if( $("table.option tbody tr.active").length > 0  && tableOption.data().count() > 0){
		let tablaOrigen;
		$("table.option tbody tr.active").each(function(key,data) {
			tablaOrigen = $("#" + $(data).parent().parent().attr("id")).DataTable();
			pasa(tablaOrigen,tableResult ,this);
		});
	}
	$("table.result tbody tr").removeClass("active");
	
});

$("a.allRight").on("click", function(){
	$("table.option tbody tr").addClass("active");
	let tablaOrigen;
	if( $("table.option tbody tr.active").length  >0  && tableOption.data().count()>0 ){
		$("table.option tbody tr.active").each(function(key,data) {
			tablaOrigen = $("#" + $(data).parent().parent().attr("id")).DataTable();
			pasa(tablaOrigen,tableResult ,this);
		});
	}
	$("table.result tbody tr").removeClass("active");
});

function pasa(tablaInicio,tablaFin,valor){
	var row = tablaInicio.row( valor );
    var rowNode = row.node();
    row.remove();
    tablaFin.row.add( rowNode ).draw();
    tablaInicio.draw();	

}

function noSelect(campos) {
	var errores = false;
	$.each(campos, function(index, value) {
		if (valIsInvalidSelect($(value).val())) {
			errores = true;
			$(value).siblings("input").addClass('invalid');
			$(value).parent().append("<div class=\"alert alert-danger\"> <span class=\"glyphicon glyphicon-ban-circle\"></span> El campo es requerido </div>");
		}
	});
	return errores;
}

function valIsInvalidSelect(value) {
	return (value === '0');
}

$( '#listReportes .list-group-item' ).click( function(e) {
	showLoader();
	$('#listReportes li').removeClass('active');
	$(this).addClass('active');
	let id_repote = $(this).val();
	$.post(obtieneColumnasURL,{id_reporte:id_repote}).done(function (data){
		let datos = JSON.parse(data);
		if(datos.code == 0){
			datos = JSON.parse(datos.otro);
			let dt = $("#columnasReporte").DataTable();
			let dta = $("#columnasReporteAux").DataTable();
			let dtr = $("#columnasReporteSeleccionas").DataTable();
			dt.clear().draw();
			dta.clear().draw();
			dtr.clear().draw();
			$(datos).each(function(key,data){
				if(data._codigo != 2){
					dt.row.add([
						data._descripcion + '<input type="hidden" tabla="columnasReporte" value="' + data._catalogoDetalleId + '"/>'
					]).draw(false);
				}else{
					if(area == 2 && id_repote == 132){
						dta.row.add([
							data._descripcion + '<input type="hidden" tabla="columnasReporteAux" value="' + data._catalogoDetalleId + '"/>'
						]).draw(false);
					}
				}
			});
			if(area == 2 && id_repote == 132){
				$("#tablaOculta").attr("style","");
			}else{
				$("#tablaOculta").attr("style","display:none");
			}
			hideLoader();
		}else{
			showMessageError(".navbar", datos.msg, 0);
			hideLoader();
		}
	})
});

$("#btnGuardar").on("click",function () {
	showLoader();
	if(tableResult.data().count()>0){
		if($("#nombre_reporte").val() != ""){
			let columnas = "";
			let div;
			tableResult.data().each(function (key,data){
				div = document.createElement('div');
				div.innerHTML = tableResult.data()[data];
				if(columnas == ""){
					columnas = $(div).find('input').val();
				}else{
					columnas += "," + $(div).find('input').val();
				}
			});
			let objSend = {
				id_reporte_edicion:$("#id_reporte").val(),
				id_reporte:$($(".list-group-item.active")[0]).val(),
				nombre: $("#nombre_reporte").val(),
				columnas: columnas
			};
			if(edicion == 1){
				$.post(guardaEdicionReporteURL,objSend).done(function (data){
					let datos = JSON.parse(data);
					if(datos.code == 0){
						showLoader();
						showMessageSuccess('.navbar', datos.msg, 0)
						$("#formListaReportes").submit();
						hideLoader();
					}else{
						showMessageError(".navbar", datos.msg, 0);
					}
					hideLoader();
				});
			}else{
				$.post(guardaReporteURL,objSend).done(function (data){
					let datos = JSON.parse(data);
					if(datos.code == 0){
						$("#nombre_reporte").val("");
						$($(".list-group-item")[0]).trigger("click");
						$("#columnasReporteSeleccionas").DataTable().clear().draw();
						showMessageSuccess('.navbar', datos.msg, 0)
						hideLoader();
					}else{
						showMessageError(".navbar", datos.msg, 0);
					}
					hideLoader();
				});
			}
		}else{
			showMessageError(".navbar", "El reporte debe de tener un nombre", 0);
			hideLoader();
		}
	}else{
		showMessageError(".navbar", "No hay columnas para configurar al reporte", 0);
		hideLoader();
	}
});

$("#btnCancelar").on("click",function (){
	showLoader();
	window.location="landing";
	hideLoader();
});

$("#btnListaReportes").on("click",function (e){
	showLoader();
	$("#formListaReportes").submit();
});

function subeSeleccion(){
	let option = $("table.result tbody tr.active")[0];
	let optionSelect = tableResult.row(option);
	let optionData = optionSelect.data();
	let optionIndex = optionSelect.index();
	if (optionSelect.index() < 1) {
		return;
	}
	let optionPrev;
	tableResult.rows().every( function ( rowIdx, tableLoop, rowLoop ) {
		var data = this.index();
		if (data == optionSelect.index() - 1) {
			optionPrev = this;
		}
	});
	let optionPrevData = optionPrev.data();
	let optionPrevIndex = optionPrev.index();
	tableResult.row(optionPrevIndex).data(optionData).draw();
	tableResult.row(optionIndex).data(optionPrevData).draw();
	$(option).removeClass('active');
}

function bajaSeleccion(){
	let option = $("table.result tbody tr.active")[0];
	let optionSelect = tableResult.row(option);
	let optionData = optionSelect.data();
	let optionIndex = optionSelect.index();
	if (optionSelect.index() == tableResult.rows().count() - 1) {
		return;
	}
	let optionNext;
	tableResult.rows().every( function ( rowIdx, tableLoop, rowLoop ) {
		var data = this.index();
		if (data == optionSelect.index() + 1) {
			optionNext = this;
		}
	});
	let optionNextData = optionNext.data();
	let optionNextIndex = optionNext.index();
	tableResult.row(optionNextIndex).data(optionData).draw();
	tableResult.row(optionIndex).data(optionNextData).draw();
	$(option).removeClass('active');
}

