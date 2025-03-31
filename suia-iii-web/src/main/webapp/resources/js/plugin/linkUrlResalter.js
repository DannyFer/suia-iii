$(document).ready(function() {

	var url = window.location.pathname;	
	var realPath = window.location.href;
	
	$('.links-container a').each(function() {		
		if (realPath.indexOf(this.href) != -1) {
			if(realPath.substring(realPath.indexOf(this.href)).length == this.href.length) 
				$(this).addClass('bold');
		}
	});
})