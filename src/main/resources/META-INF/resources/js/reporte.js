$(document).ready(function(){
    window.scrollTo(0, 0);
    /*$('.datepicker').pickadate({
        format : 'yyyy-mm-dd',
        formatSubmit : 'yyyy-mm-dd',
        yearRange: "1970:+nn",
        yearDropdownItemNumber:100,
        scrollableYearDropdown:true
    });*/
    $('.datepicker').datepicker({
        format : 'yyyy-mm-dd',
        formatSubmit : 'yyyy-mm-dd',
        yearRange: "1970:+nn",
        yearDropdownItemNumber:100,
        scrollableYearDropdown:true
    });
    $('.data-table-test').DataTable({
        scrollX:true,
        responsive: false,
        destroy: true,
        dom: 'fBrltip',
        ordering:false,
        ordered:false,
        buttons: [{
            extend:    'excelHtml5',
            text:      '<a class="btn-floating btn-sm teal"><i class="far fa-file-excel"></i></a>',
            titleAttr: 'Exportar XLS',
            className:"btn-unstyled",
        }],
        /*columnDefs: [
            {targets: '_all', className: "py-2" }
        ],*/
        lengthChange: true,
        language: {
            "url": spanishJson,

        },
        lengthMenu: [[5,10,15], [5,10,15]],
        pageLength: 10
    });
});

function ConsultaReporte(id_reporte){
    showLoader();
    let objSend = {
        id_reporte:id_reporte,
        fecha_inicio:$("#fechaInicio").val(),
        fecha_fin:$("#fechaTermino").val()
    }
    $.getJSON(consultaReporteURL,objSend,function (data){
        if(data.code == 0){
            if(data.lista.length > 0){
                console.log(data.lista);
                var dt = $("#tablaReporte").DataTable();
                dt.clear().draw();
                $(data.lista).each(function (key,fila){
                   dt.row.add(Object.values(fila)).draw(false);
                });
            }else{
                showMessageError(".navbar", "La consulta no encontro ningún dato.", 0);
                hideLoader();
            }
        }else {
            showMessageError(".navbar", data.msg, 0);
            hideLoader();
        }
        hideLoader();
    });
}

$("#btnRegresar").on("click",function (e){
    showLoader();
    $("#formListaReportes").submit();
});

