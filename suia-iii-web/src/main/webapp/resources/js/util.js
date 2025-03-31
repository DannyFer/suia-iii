function CambioURL(url) {

	document.getElementById('frame').src = url;
	// alert("ff"+url);
	document.getElementById('frame').height = "auto;";
	document.getElementById('frame').width = "800px";
	// alert("fdsf");
}

function getVarsUrl() {
	var url = location.search.replace("?", "");
	var arrUrl = url.split("&");
	var urlObj = {};
	for (var i = 0; i < arrUrl.length; i++) {
		var x = arrUrl[i].split("=");
		urlObj[x[0]] = x[1];
	}
	return urlObj;
}

function resizeIframe(obj) {
	obj.style.height = obj.contentWindow.document.body.scrollHeight + 15 + 'px';
}

PrimeFaces.locales["es"] = {
	closeText : "Cerrar",
	prevText : "Anterior",
	nextText : "Siguiente",
	monthNames : [ "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" ],
	monthNamesShort : [ "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" ],
	dayNames : [ "Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado" ],
	dayNamesShort : [ "Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab" ],
	dayNamesMin : [ "Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa" ],
	weekHeader : "Semana",
	firstDay : 0,
	isRTL : false,
	showMonthAfterYear : false,
	yearSuffix : "",
	timeOnlyTitle : "Solo hora",
	timeText : "Tiempo",
	hourText : "Hora",
	minuteText : "Minuto",
	secondText : "Segundo",
	currentText : "Fecha actual",
	ampm : false,
	month : "Mes",
	week : "Semana",
	day : "Día",
	allDayText : "Todo el día"
};

PrimeFacesExt.locales.Timeline['es'] = {
	'MONTHS' : [ "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" ],
	'MONTHS_SHORT' : [ "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" ],
	'DAYS' : [ "Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado" ],
	'DAYS_SHORT' : [ "Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb" ],
	'ZOOM_IN' : "Aumentar zoom",
	'ZOOM_OUT' : "Disminuir zoom",
	'MOVE_LEFT' : "Mover izquierda",
	'MOVE_RIGHT' : "Mover derecha",
	'NEW' : "Nuevo",
	'CREATE_NEW_EVENT' : "Crear nueva actividad"
};

function soloLetras(e) {
	// tecla=(document.all)?e.keyCode:e.which;
	// patron=/[A-Z a-záéíóú]/;
	// te=String.fromCharCode(tecla);
	// return patron.test(te);

	key = e.keyCode || e.which;
	tecla = String.fromCharCode(key).toLowerCase();
	letras = " abcdefghijklmnñopqrstuvwxyzáéíóúÁÉÍÓÚÑ";
	especiales = [ 8, 9, 37, 39, 46 ];
	tecla_especial = false
	for ( var i in especiales) {
		if (key == especiales[i]) {
			tecla_especial = true;
			break;
		}
	}

	if (letras.indexOf(tecla) == -1 && !tecla_especial) {
		return false;
	}
}

function validarLetras(e) {
	tecla = (document.all) ? e.keyCode : e.which;
	if (tecla == 8)
		return true;
	if (tecla == 32)
		return true;
	if (tecla == 9)
		return true;
	patron = /[A-Za-zñÑ]/;
	te = String.fromCharCode(tecla);
	return patron.test(te);
}

function soloLetrasNumeros(e) {
	key = e.keyCode || e.which;
	tecla = String.fromCharCode(key).toLowerCase();
	letras = " abcdefghijklmnñopqrstuvwxyz0123456789";
	especiales = [ 8, 9, 37, 39, 46 ];
	tecla_especial = false
	for ( var i in especiales) {
		if (key == especiales[i]) {
			tecla_especial = true;
			break;
		}
	}

	if (letras.indexOf(tecla) == -1 && !tecla_especial) {
		return false;
	}
}

function NumCheckDouble(e, field) {
	key = e.keyCode ? e.keyCode : e.which
	// backspace
	if (key == 8)
		return true
		// tab
	if (key == 9)
		return true
		// point
	if (key == 46)
		return true
		// 0-9
	if (key > 47 && key < 58) {
		if (field.value == "")
			return true
		regexp = /^\d+(\.\d{2})$/
		return !(regexp.test(field.value))
	}
	// .
	// if (key == 46) {
	// if (field.value == "") return false
	// regexp = /^\d+(\.\d{2})$/
	// return !regexp.test(field.value)
	// }
	// other key
	return false
}

function NumCheckDos(e, field) {
	key = e.keyCode ? e.keyCode : e.which
	// backspace
	if (key == 8)
		return true
		// tab
	if (key == 9)
		return true
		// 0-9
	if (key > 47 && key < 58) {
		if (field.value == "")
			return true
		regexp = /.[0-9]{1}$/
		return !(regexp.test(field.value))
	}
	// other key
	return false
}

function numbersonly(myfield, e, dec) {
	var key;
	var keychar;
	if (window.event)
		key = window.event.keyCode;
	else if (e)
		key = e.which;
	else
		return true;
	keychar = String.fromCharCode(key);
	// control keys
	if ((key == null) || (key == 0) || (key == 8) || (key == 9) || (key == 13) || (key == 27))
		return true;
	// numbers
	else if ((("0123456789").indexOf(keychar) > -1))
		return true;
	// decimal point jump
	else if (dec && (keychar == ".")) {
		myfield.form.elements[dec].focus();
		return false;
	} else
		return false;
}

function disableEnterKey(e) {
	var key;
	if (window.event) {
		key = window.event.keyCode; // IE
	} else {
		key = e.which; // firefox
	}
	if (key == 13 || key == 17) {
		return false;
	} else {
		return true;
	}
}

function bodyOverflow(value) {
	var result = value ? 'auto' : 'hidden';
	$('body').css('overflow', result);
}

function nobackbutton() {
	window.location.hash = "no-back-button";
	window.location.hash = "Again-No-back-button" // chrome
	window.onhashchange = function() {
		window.location.hash = "no-back-button";
	}
}

/** ****************************************************** */
// ANIMATIONS
/** ****************************************************** */

var angle = 0;
var previuosScroll = 0;
$(window).scroll(function() {
	var currentScroll = $(window).scrollTop();
	if (currentScroll > previuosScroll)
		angle++;
	else
		angle--;
	previuosScroll = currentScroll;
	$('.header-left-part1').css({
		transform : 'rotate(' + (angle) + 'deg)'
	});
});

function arrow_indicator_start(arrowIndicator, time) {
	arrow_indicators_construct();
	arrow_indicator(arrowIndicator);
	setTimeout(function() {
		arrow_indicator_hide(arrowIndicator);
	}, time * 1000);
}

var arrows_onmouse_over_containers = [];
function arrow_indicator_start_onover(arrowIndicator, time, container_onover, cyclic) {
	arrow_indicators_construct();
	if (!cyclic) {
		$(container_onover).one('mouseover', function() {
			if (arrows_onmouse_over_containers.indexOf(container_onover) == -1) {
				arrows_onmouse_over_containers.push(container_onover);
				arrow_indicator(arrowIndicator);
				setTimeout(function() {
					arrow_indicator_hide(arrowIndicator, container_onover);
				}, time * 1000);
			}
		});
	} else {
		$(container_onover).mouseover(function() {
			if (arrows_onmouse_over_containers.indexOf(container_onover) == -1) {
				arrows_onmouse_over_containers.push(container_onover);
				arrow_indicator(arrowIndicator);
				setTimeout(function() {
					arrow_indicator_hide(arrowIndicator, container_onover);
				}, time * 1000);
			}
		});
	}
}

function arrow_indicator_hide(arrowIndicator, container_onover) {
	$(arrowIndicator).hide();
	$(arrowIndicator).stop();
	$(arrowIndicator).css('display', 'none');
	if (container_onover) {
		delete arrows_onmouse_over_containers[arrows_onmouse_over_containers.indexOf(container_onover)];
	}
}

var ARROW_INDICATOR_WIDTH_OFFSET = 55;
var ARROW_INDICATOR_HEIGHT_OFFSET = -60;
var ARROW_INDICATOR_LEFT_ANIMATION = 40;
var ARROW_INDICATOR_RIGHT_ANIMATION = -20;
var ARROW_INDICATOR_SPEED_ANIMATION = 350;
function arrow_indicators_construct() {
	try {
		$('.arrow-indicator').each(function() {
			var arrowIndicator = $(this);
			if (arrowIndicator) {
				var target = $('.' + $(arrowIndicator).attr('data-forStyleClass'));
				if (target) {
					var position = $(target).position();
					var positionLeft = (position.left + $(target).width() / 2) + ARROW_INDICATOR_WIDTH_OFFSET;
					var positionTop = position.top + $(target).height() + ARROW_INDICATOR_HEIGHT_OFFSET;
					$(arrowIndicator).css('left', positionLeft + 'px');
					$(arrowIndicator).css('top', positionTop + 'px');
					$(arrowIndicator).stop();
				}
			}
		});
	} catch (e) {
	}
}

function arrow_indicator(component) {
	var target = $('.' + $(component).attr('data-forStyleClass'));
	if (!target)
		return;
	var position = $(target).position();
	if (!position)
		return;
	var positionLeft = (position.left + $(target).width() / 2) + ARROW_INDICATOR_WIDTH_OFFSET;
	var positionTop = position.top + $(target).height() + ARROW_INDICATOR_HEIGHT_OFFSET;
	$(component).animate({
		left : positionLeft + ARROW_INDICATOR_LEFT_ANIMATION,
		top : positionTop + ARROW_INDICATOR_RIGHT_ANIMATION
	}, ARROW_INDICATOR_SPEED_ANIMATION, function() {
		arrow_indicator_revert(component);
	});
}
function arrow_indicator_revert(component) {
	var target = $('.' + $(component).attr('data-forStyleClass'));
	if (!target)
		return;
	var position = $(target).position();
	if (!position)
		return;
	$(component).css('display', 'inline');
	var positionLeft = (position.left + $(target).width() / 2) + ARROW_INDICATOR_WIDTH_OFFSET;
	var positionTop = position.top + $(target).height() + ARROW_INDICATOR_HEIGHT_OFFSET;
	$(component).animate({
		left : positionLeft,
		top : positionTop
	}, ARROW_INDICATOR_SPEED_ANIMATION, function() {
		arrow_indicator(component);
	});
}

function resetPageScroll() {
	window.scrollTo(0, 0);
}

/**
 * Agrega la apariencia de error a un componente.
 * 
 * @param idComponent
 *            El identificador del componente que queremos resaltar
 */
function highlightComponent(idComponent) {

	var target = $("[id='" + idComponent + "']");
	$(target).addClass("ui-state-error");

}

/**
 * Elimina la apariencia de error de un componente.
 * 
 * @param idComponent
 *            El identificador del componente
 */
function removeHighLightComponent(idComponent) {

	var target = $("[id='" + idComponent + "']");
	$(target).removeClass("ui-state-error");
}

function numbersonlyf(myfield, e, dec) {
	var key;
	var keychar;
	if (window.event)
		key = window.event.keyCode;
	else if (e)
		key = e.which;
	else
		return true;
	keychar = String.fromCharCode(key);
	// control keys
	if ((key == null) || (key == 0) || (key == 8) || (key == 9) || (key == 13) || (key == 27))
		return true;
	// numbers
	else if ((("0123456789-").indexOf(keychar) > -1))
		return true;
	// decimal point jump
	else if (dec && (keychar == ".")) {
		myfield.form.elements[dec].focus();
		return false;
	} else
		return false;
}
