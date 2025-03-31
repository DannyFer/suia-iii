package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.ByteArrayInputStream;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformativoFacade;

@ManagedBean
@ViewScoped
public class InformativoDescargaController {

	private static final Logger LOGGER = Logger.getLogger(InformativoDescargaController.class);

	@EJB
	private InformativoFacade informativoFacade;

	private byte[] file;

	private StreamedContent imageGuiaRemision;

	private StreamedContent imageBitacoraHorasViaje;

	private StreamedContent imageRegistroAccidente;

	private StreamedContent imageModeloTarjetaEmergencia;

	private StreamedContent imageModeloHojaSeguridad;

	private StreamedContent imageInformativoTratamiento;

	private StreamedContent imageInformativoIncineracion;

	private StreamedContent imageInformativoCoprocesamiento;

	public StreamedContent getImageGuiaRemision() {
		imageGuiaRemision = getDefaultStreamedContentImagen(InformativoFacade.GUIA_REMISION);
		return imageGuiaRemision;

	}

	public StreamedContent getImageRegistroAccidente() {
		imageRegistroAccidente = getDefaultStreamedContentImagen(InformativoFacade.REGISTRO_ACCIDENTES);
		return imageRegistroAccidente;
	}

	public StreamedContent getImageBitacoraHorasViaje() {
		imageBitacoraHorasViaje = getDefaultStreamedContentImagen(InformativoFacade.BITACORA_HORAS_VIAJE);
		return imageBitacoraHorasViaje;

	}

	public StreamedContent getImageModeloTarjetaEmergencia() {
		imageModeloTarjetaEmergencia = getDefaultStreamedContentImagen(InformativoFacade.MODELO_TARJETA_EMERGENCIA);
		return imageModeloTarjetaEmergencia;

	}

	public StreamedContent getImageModeloHojaSeguridad() {
		imageModeloHojaSeguridad = getDefaultStreamedContentImagen(InformativoFacade.MODELO_HOJA_SEGURIDAD);
		return imageModeloHojaSeguridad;

	}

	private DefaultStreamedContent getDefaultStreamedContentImagen(String nombre) {
		try {
			file = informativoFacade.getDocumentoInformativo(nombre);
			return new DefaultStreamedContent(new ByteArrayInputStream(file), "image/png", nombre);
		} catch (Exception e) {
			LOGGER.error(e, e);
			return null;
		}

	}

}
