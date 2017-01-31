jQuery(document).ready(
    function() {
      Highcharts
          .setOptions({
            lang : {
              months : [ 'Januar', 'Februar', 'März', 'April', 'Mai', 'Juni',
                  'Juli', 'August', 'September', 'Oktober', 'November',
                  'Dezember' ],
              resetZoom : 'Zoom zurücksetzen',
              printChart : 'Diagramm drucken',
              downloadPNG : 'Als PNG herunterladen',
              downloadJPEG : 'Als JPG herunterladen',
              downloadPDF : 'Als PDF herunterladen',
              downloadSVG : 'Als SVG herunterladen'
            },
            exporting : {
              width : jQuery(window).width() - 100
            }
          });

      jQuery("div.highchart").each(
          function(index, value) {
            var chartDiv = value;
            var issn = jQuery(chartDiv).data("issn");
            jQuery.getJSON('usage?issn=' + issn, function(data) {
              chart = new Highcharts.Chart({
                chart : {
                  renderTo : chartDiv,
                  type : 'area',
                  zoomType : 'xy'
                },
                title : {
                  text : '' + data.name
                },
                subtitle : {
                  text : '' + data.issn
                },
                xAxis : {
                	categories : data.categories
                },
                yAxis : {
                  title : {
                    text : 'Anzahl'
                  },
                  min : 0,
                  allowDecimals : false
                },
                plotOptions: {
                    area: {
                        stacking: 'normal'
                    }
                },
                colors : ['#AA4643', '#4572A7', '#89A54E', '#80699B',
                    '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92' ],
                series : data.series
              });
            });
          });

    });
