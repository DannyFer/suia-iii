package ec.gob.ambiente.prevencion.certificado.ambiental.controllers;

import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ec.gob.ambiente.suia.utils.BeanLocator;


@LocalBean
@Singleton
public class TareaProgramadaFinalizarCertificadosAmbientales {
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "30",  minute = "0,30", hour = "*", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Automatizacion de Certificado Ambiental",persistent = true)
	public void ejecutar(){
		CertificadoAmbientalTeminacionTareaFirmaController certificadoAmbientalTeminacionTareaFirmaController = (CertificadoAmbientalTeminacionTareaFirmaController) BeanLocator.getInstance(CertificadoAmbientalTeminacionTareaFirmaController.class);
		certificadoAmbientalTeminacionTareaFirmaController.obtenerTareasFirma();
	}

}
