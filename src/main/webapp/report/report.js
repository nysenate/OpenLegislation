$(document).ready(function(){
	(function() {
		$('#shield').hide();
		$('#loading').hide();
		window.onresize = function() { util.center($('#loading')); };
	})();
	
	cache = {
		'reported':[], 
		'cur_bill_elem' : null
	};
	
	$('body').delegate('li.bill', 'click', function(evt) {
		var elem = $(this);
		
		if(cache.cur_bill_elem && cache.cur_bill_elem.html() === elem.html()) {
			return;
		}
		else {
			$('.bill_bottom_container').slideUp();
			var type = $(elem.parents().filter('.wrapper')).attr('id');
			var id = elem.find('.bill_id').html();

			if(type && id) {
				var bill = cache[type][id];
				var cont = $(elem.find('.bill_bottom_container'));
				
				cont.slideToggle();
			}
			
			cache.cur_bill_elem = elem;
		}
		
	});
		
	var write_bills = function() {
		var html="";
		html+= tmpl("tmpl_container", {
				'util' : util, 
				'reported' : cache.reported,
			});
		$('#container').html(html).find('.bill_bottom_container').hide();
	};
	
	var init = function(data) {
		if(data) {
			for(idx in data) {
				cache.reported[data[idx].oid] = data[idx];
			}
		}
		else {
			util.getJSON("/legislation/report.json", [init, write_bills]);
		}
	};
	
	init();
});

(function() {
	this.util = {
        'getJSON': function(url, callbacks, params) {
            $.getJSON(url, function(data) {
                    for(i in callbacks) {
                            callbacks[i](data, params);
                    }
            });
        },
		'center': function(elem) {
        	var left = ($('body').width()/2) - (elem.width()/2);
    		elem.css('left',left);
        },
        'formatDate': function(ms) {
        	var date = new Date(ms);
        	var month = date.getMonth();
        	month++;

        	return month + "/" + date.getDate() + "/" + date.getFullYear();
    	},
    	'getColor': function(heat) {
    		if(heat == 10) return "#ee2200";
    		if(heat > 8) return "#ff4433"
    		if(heat > 6) return "#ff8877";
    		if(heat > 4) return "#ffbbaa";
    		return "#ffeedd";
    	},
    	'getNonMatchingFields': (function(fields) {
			return function(nonMatching) {
				var lst = [];
				for(var x in fields) {
					if(nonMatching[fields[x]]) lst[lst.length] = nonMatching[fields[x]].field;
        		}
				return lst;
			};
    	})(['full text', 'memo', 'sponsor', 'cosponsors', 'summary', 'title', 'law section', 'actions'])
    }
	$.ajaxSetup({
		beforeSend: function() {
			$('#shield').show();
			$('#loading').fadeIn(100);
			util.center($('#loading'));
		},
		complete: function(){
		    $('#shield').hide();
		    $('#loading').fadeOut(100);
		},
		success: function() {}
	});
})();