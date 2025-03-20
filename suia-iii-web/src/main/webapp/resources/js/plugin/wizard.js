var intervalWizardId;

$(document).ready(function() {
	reloadWizard();
});

function reloadWizard() {
	clearInterval(intervalWizardId);

	var updateMode = false;
	var wizard = $('ul.ui-wizard-step-titles');

	if (wizard.length && wizard.length > 0) {
		$(wizard).removeClass('ui-wizard-step-titles ui-helper-clearfix ui-helper-reset');
		$(wizard).addClass('wizard-steps');
	} else {
		wizard = $('ul.wizard-steps');
		updateMode = true;
	}
	
	var currentStep = PF('wizard').getStepIndex(PF('wizard').currentStep);

	$('.wizard-extra-step').each(function() {	
		var attr = $(this).attr('data-step-label');
		var contained = false;
		$(wizard).children('li').each(function() {
			if($(this).children('.title').html() == attr)
				contained = true;
		});
		if(!contained) {
			var html = '';
			if (typeof attr !== typeof undefined && attr !== false) {
				html = attr;
			}
			$(wizard).append('<li class="ui-wizard-step-title">' + html + '</li>');
		}
	});

	var pos = 0;
	$(wizard).children('li').each(function() {
		pos++;
		if ($(this).hasClass('ui-wizard-step-title')) {
			$(this).removeClass('ui-wizard-step-title ui-state-highlight ui-state-default ui-corner-all');
			if (pos - 1 == currentStep)
				$(this).addClass('ui-state-highlight active');
			var title = $(this).html();
			$(this).empty();
			$(this).append('<span class="step">' + (pos) + '</span>');
			$(this).append('<span class="title">' + title + '</span>');
		}
	});

	setInterval(function() {
		intervalWizardId = updateSteps();
	}, 500);
}

function addWizardStep(wizard, stepOrder, stepLabel) {
	var html = '<li><span class="step">' + stepOrder + '</span><span class="title">' + stepLabel + '</span></li>';
	$(wizard).append(html);
}

function updateSteps() {
	var wizard = $('ul.wizard-steps');
	$(wizard).children('li').each(function() {
		$(this).removeClass('active');
	});

	var addClass = true;

	var index = 0;
	$(wizard).children('li').each(function() {
		if (addClass) {
			index++;
			$(this).addClass('active');
		}
		if ($(this).hasClass('ui-state-highlight')) {
			addClass = false;
		}
	});
	
	var currentStepWizard = PF('wizard').getStepIndex(PF('wizard').currentStep);
	if(currentStepWizard == 0)
		$('.wizard-custom-btn-back').hide();
	else
		$('.wizard-custom-btn-back').show();
		
	$('.wizard-custom-btn-only-end').hide();
	$('.wizard-custom-btn-only-not-end').show();
	
	var stepsWizardCounter = $(wizard).children('li').length;
	if(currentStepWizard == stepsWizardCounter - 1) {
		$('.wizard-custom-btn-next').hide();
		$('.wizard-custom-btn-only-end').show();
		$('.wizard-custom-btn-only-not-end').hide();
	}
	else
		$('.wizard-custom-btn-next').show();

	executeInUpdateSteps(index);
}

function executeInUpdateSteps(index) {

}