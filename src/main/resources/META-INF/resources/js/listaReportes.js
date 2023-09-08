function MuestraReporte (id_reporte){
    showLoader();
    $("#id_reporte").val(id_reporte);
    $("#formMuestraReporte").submit();
}

function Regresar(){
    showLoader();
    window.location="mantenimiento-de-reportes";
    hideLoader();
}

function EliminaReporte(id_reporte,elemento){
    showLoader();
    let reporte = $(elemento).parent();
    $.post(eliminaReporteUrl,{"id_reporte":id_reporte}).done(function (data){
       let datos = JSON.parse(data);
       if(datos.code == 0){
           showMessageSuccess('.navbar', datos.msg, 0);
           reporte.remove();
           hideLoader();
       }else{
           showMessageError(".navbar", datos.msg, 0);
           hideLoader();
       }
    });
}

function EditarReporte (id_reporte){
    showLoader();
    $("#id_reporteEdicion").val(id_reporte);
    $("#formEditaReporte").submit();
}