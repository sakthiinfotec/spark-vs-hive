$(document).ready(function() {

	var source   = $("#search-result-template").html();
	var template = Handlebars.compile(source);

    var positionCounter = 1;
    Handlebars.registerHelper('position', function() {
        return positionCounter++;
    });

  $('input[name=patientId').focus();
    var $btnSearch = $("#search")

	$btnSearch.click(function(e){

		$btnSearch.button('loading');
		positionCounter = 1;
		e.preventDefault();
    //alert(JSON.stringify($('form').serialize()));
    $.ajax({
	    url: "/search",
	    type: "POST",
	    data: $('form').serialize(),
	    success: function(data){
				var html = template(data);
				$('#search-result').html(html);
				$btnSearch.button('reset');
	    }
		});
		
	});

	var $btnCompare = $('#compare');
	$btnCompare.click(function(e){

		var data = [
	      { y: '08:00', a: 135,  b: 16},
	      { y: '08:23', a: 93,  b: 10},
	      { y: '08:46', a: 105,  b: 15},
	      { y: '09:02', a: 88,  b: 08},
	      { y: '09:25', a: 98,  b: 07},
	      { y: '09:51', a: 120,  b: 13},
	      { y: '10:13', a: 125,  b: 06},
	      { y: '10:40', a: 87,  b: 12},
	      { y: '11:05', a: 128,  b: 09},
	      { y: '11:30', a: 130,  b: 15},
	      { y: '12:05', a: 111,  b: 08}
	    ],
	    config = {
	      data: data,
	      xkey: 'y',
	      ykeys: ['a', 'b'],
	      labels: ['Batch', 'Real-time'],
	      fillOpacity: 0.6,
	      hideHover: 'auto',
	      behaveLikeLine: true,
	      resize: true,
	      pointFillColors:['#ffffff'],
	      pointStrokeColors: ['black'],
	      lineColors:['gray','red']
	  };
		config.element = 'area-chart';
		Morris.Bar(config);
	});

});

$(function () {
	$('#startdatepicker,#enddatepicker').datetimepicker();
});