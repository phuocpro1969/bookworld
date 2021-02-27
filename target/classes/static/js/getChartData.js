var userScript = (function(){
    var chartData = [];
    var charts = [];
    
    /* Add default config to make charts responsive */
    var options = {
        responsive: true,
        maintainAspectRatio: true
    };
    
    var prepareChartContext = function (canvasNode,chartdata) {
         /* body... */
         var ctx = canvasNode[0].getContext("2d");
         ctx.canvas.width = "300";
         ctx.canvas.height = "200";
         var data = {
        labels: chartdata.lables,
        datasets: [
            {
                label: chartdata.datasets[0].name,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(255, 159, 64, 0.2)'
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1,
                data: chartdata.datasets[0].value
            }]
         }
         return {
             ctx : ctx,
             data : data
         }
    };
    
    
    var createCanvasNode = function () {
         /* body... */ 
         var $div = $("<div>", {class: "col-md-6"});
         var $span = $("<span>", {class: "appName"});
         $($div).append($span);
         var $canvas = $("<canvas>",{class: "chartClass"});
         return {
             canvasNodeHandler : $($div).append($canvas),
             canvasHandler : $canvas
         }
    };
    
    var createContainerNode = function () {
         /* body... */ 
         var $div = $("<div>", {class: "container"});
         return $div;
    };
    
    
    var getChartData = function () {
         /* body... */ 
         var endpoint="/viewCharts/";
         $.getJSON(endpoint)
         .done(function(data){
             var parentDiv = $("#parentDiv");
             /* little housekeeping */
             $(parentDiv).empty();
             chartData = [];
            charts = [];
             var containerNode;
             var canvasNode;
             for (var i =0; i<data.length; i++) {
                 if (i%3 == 0) {
                     containerNode = createContainerNode();
                     canvasNode = createCanvasNode();
                     chartData[i] = prepareChartContext(canvasNode.canvasHandler,data[i]);
                     $($(canvasNode.canvasNodeHandler).find('span')).append(document.createTextNode(data[i].appName));
                     $(containerNode).append(canvasNode.canvasNodeHandler);
                     $(parentDiv).append(containerNode);
                 } else{
                     canvasNode = createCanvasNode();
                     chartData[i] = prepareChartContext(canvasNode.canvasHandler,data[i]);
                     $($(canvasNode.canvasNodeHandler).find('span')).append(document.createTextNode(data[i].appName));	 	 
                     $(containerNode).append(canvasNode.canvasNodeHandler);
                 };
              }
              
              $.each(chartData,function (index,value) {
                   /* body... */ 
                   charts[index] = new Chart(value.ctx, {
                        type: 'bar',
                        data: value.data,
                        options: options
                   });
              });
    
         })
        .fail(function( jqxhr, textStatus, error ) {
            var err = textStatus + ", " + error;
            console.log( "Request Failed: " + err );
        });
    };
    
    $("#line").click(function () {
         /* body... */ 
        $.each(chartData,function (index,value) {
                   /* destroy previous chart */
                   if(charts[index] !== undefined || charts[index] !== null){
                       charts[index].destroy();
                   }	 	 	
                  charts[index] =  new Chart(value.ctx, {
                    type: 'line',
                    data: value.data,
                    options: options
               });
              });	 
    });
    
    $("#bar").click(function () {
         /* body... */ 
        $.each(chartData,function (index,value) {
                   /* destroy previous chart */ 
                  if(charts[index] !== undefined || charts[index] !== null){
                       charts[index].destroy();
                   }
                  charts[index] =  new Chart(value.ctx, {
                    type: 'bar',
                    data: value.data,
                    options: options
                    });
              });	 
    });
    
    $("#refreshCharts").click(getChartData);
    
    return {
        getChartData : getChartData
        }
    })(this);