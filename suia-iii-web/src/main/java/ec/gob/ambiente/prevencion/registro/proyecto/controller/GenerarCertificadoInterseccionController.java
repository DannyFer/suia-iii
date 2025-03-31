package ec.gob.ambiente.prevencion.registro.proyecto.controller;

import java.io.File;
import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.prevencion.registro.proyecto.controller.GenerarNotificacionesAplicativoController;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.VerProyectoBean;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.reportes.ReporteCertificadoInterseccion;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
public class GenerarCertificadoInterseccionController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6858698058887236887L;

	private static final Logger LOGGER = Logger.getLogger(GenerarCertificadoInterseccionController.class);

	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionFacade;

	@Setter
	@Getter
	@ManagedProperty(value = "#{verProyectoBean}")
	private VerProyectoBean verProyectoBean;

	public void validarGeneracionDocumentosCertificadoInterseccion() {
		if (certificadoInterseccionFacade.isGeneradoPDFMapaCertifiacdoInterseccionServidor(verProyectoBean
				.getProyecto().getCodigo())) {
			try {
				guardarCertificadoInterseccionMapaCreado();
				// verifico que exista la autoridad y la firma
				if(certificadoInterseccionFacade.getPersonaAutoridadAmbiental() != null && certificadoInterseccionFacade.getFirmaAutoridadPrevencion() != null ){
					generarOficioCertificadoInterseccion();
					verProyectoBean.setShowModalCertificadoIntercepcion(false);
					verProyectoBean.setGeneradoCorrectamenteDocumentosCertificadoInterseccion(true);
				}
				verProyectoBean.setShowModalErrorProcesoInterseccion(false);
				RequestContext.getCurrentInstance().execute("PF('certificadoIntercepcion').hide();");
				RequestContext.getCurrentInstance().execute("PF('notaResponsabilidadDialog').show();");
			} catch (Exception e) {
				LOGGER.error("Error al generar el CI", e);
				verProyectoBean.setShowModalErrorProcesoInterseccion(true);
				verProyectoBean.setGeneradoCorrectamenteDocumentosCertificadoInterseccion(false);
				try {
					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
					// envionotificacion de no existir el usuario con rol autoridad ambiental 2 mae
					if (e.getMessage().contains("No existe el usuario con el rol AUTORIDAD AMBIENTAL2 MAE")){
						enviarNotificaciones.enviarNotificacionError(verProyectoBean.getProyecto(), e.getMessage());						
					}else if (e.getMessage().contains("Error al recuperar la firma para el certificado")){
						enviarNotificaciones.enviarNotificacionError(verProyectoBean.getProyecto(), e.getMessage());						
					}
				} catch (ServiceException | InterruptedException e1) {
					LOGGER.error("Error al enviar la notificacion", e);
				}
				JsfUtil.addMessageError("Ocurrió un error al guardar el mapa del certificado de intersección o al generar el oficio.");
			}
		} else {
			verProyectoBean.setShowMensajeErrorGeneracionDocumentos(true);
			verProyectoBean.setGeneradoCorrectamenteDocumentosCertificadoInterseccion(false);
		}

	}

	private void guardarCertificadoInterseccionMapaCreado() throws Exception {
		certificadoInterseccionFacade.subirCIMapaAlfresco(verProyectoBean.getProyecto());
	}

	public void generarOficioCertificadoInterseccion() {
		String[] parametros;
		String nota="";
		String notaResponsable="";
		Integer marcaAgua=0;
		try {
			
			if (verProyectoBean.getProyecto().getEstadoMapa()==null || verProyectoBean.getProyecto().getEstadoMapa()==false){
				marcaAgua=1;
			}
			int valorcapa = certificadoInterseccionFacade
					.getDetalleInterseccionlista(verProyectoBean.getProyecto()
							.getCodigo());

//			if (verProyectoBean.getProyecto().getAreaResponsable().getId().equals(556) ){
			if (valorcapa == 1 || valorcapa==2) {
				// if
				// (!certificadoInterseccionFacade.getDetalleInterseccionlista(verProyectoBean.getProyecto().getCodigo())){
				notaResponsable= DocumentoPDFPlantillaHtml
						.getValorResourcePlantillaInformes("nota_ente_acreditado_certificado_interseccion");
//				nota = DocumentoPDFPlantillaHtml
//						.getValorResourcePlantillaInformes("nota_ente_mdq");
			}else{
				notaResponsable= DocumentoPDFPlantillaHtml
						.getValorResourcePlantillaInformes("nota_ente_acreditado_certificado_interseccion");
			}			
//			}else{
//				notaResponsable= DocumentoPDFPlantillaHtml
//						.getValorResourcePlantillaInformes("nota_ente_acreditado_certificado_interseccion");
//			}

			parametros = certificadoInterseccionFacade.getParametrosOficioCertificadoInterseccion(verProyectoBean
					.getProyecto().getId(), DocumentoPDFPlantillaHtml
					.getValorResourcePlantillaInformes("etiqueta_representante_legal"), DocumentoPDFPlantillaHtml
					.getValorResourcePlantillaInformes("titulo_tratamento_persona_juridica"), notaResponsable,
					DocumentoPDFPlantillaHtml.getValorResourcePlantillaInformes("comentario_proyecto_no_intersecta"),
					DocumentoPDFPlantillaHtml.getValorResourcePlantillaInformes("comentario_proyecto_si_intersecta"), null,nota,marcaAgua);

			File file = ReporteCertificadoInterseccion.crearCertificadoInterseccionPDF(parametros,
					"certificado_interseccion", certificadoInterseccionFacade.getPersonaAutoridadAmbiental(),
					"nota_responsabilidad_certificado_interseccion",
					certificadoInterseccionFacade.getPersonaRepresentanteProyecto(verProyectoBean.getProyecto()),
					certificadoInterseccionFacade.getFirmaAutoridadPrevencion(), marcaAgua);
			certificadoInterseccionFacade.subirReportesCIAlfresco(file, verProyectoBean.getProyecto());
		} catch (Exception e) {
			LOGGER.error("Error al generar el oficio del CI", e);
			JsfUtil.addMessageError("Ocurrió un error al generar el oficio del certificado de intersección");
		}
	}

	private boolean isHabilitadoDescargarMapaCertificadoInterseccion() {
		if (verProyectoBean.getProyecto().getProyectoInterseccionExitosa() != null
				&& verProyectoBean.getProyecto().getProyectoInterseccionExitosa()) {

			if (verProyectoBean.getProyecto().isFinalizado()) {
				return true;
			} else if (certificadoInterseccionFacade.isGeneradoPDFMapaCertifiacdoInterseccionServidor(verProyectoBean
					.getProyecto().getCodigo())) {
				return true;
			}
		}
		return false;
	}

	private boolean isHabilitadoDescargarOficioCertificadoInterseccion() {
		if (verProyectoBean.getProyecto().getProyectoInterseccionExitosa() != null
				&& verProyectoBean.getProyecto().getProyectoInterseccionExitosa()) {
			if (verProyectoBean.getProyecto().isFinalizado()) {
				return true;
			} else if (verProyectoBean.isGeneradoCorrectamenteDocumentosCertificadoInterseccion()) {
				return true;
			}
		}
		return false;
	}

	public void descargarMapa() {

		try {
			if (isHabilitadoDescargarMapaCertificadoInterseccion()) {
				UtilDocumento.descargarPDF(certificadoInterseccionFacade
						.getAlfrescoMapaCertificadoInterseccion(verProyectoBean.getProyecto()),
						"Mapa del certificado intersección");
			} else {
				JsfUtil.addMessageError("El documento aún no ha sido generado, por lo que no se puede descargar.");
			}

		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}

	}

	public void descargarOficio() {
		if (isHabilitadoDescargarOficioCertificadoInterseccion()) {
			try {
				UtilDocumento.descargarPDF(certificadoInterseccionFacade
						.getAlfrescoOficioCertificadoInterseccion(verProyectoBean.getProyecto()),
						"Oficio del certificado intersección");

			} catch (Exception e) {
				LOGGER.error("error al descargar ", e);
				JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
			}
		} else {
			JsfUtil.addMessageError("El documento aún no ha sido generado, por lo que no se puede descargar.");
		}

	}

	public void descargarCoordenadas() {
		try {
			UtilDocumento.descargarExcel(certificadoInterseccionFacade
					.descargarCoordenadasCertificadoInterseccion(verProyectoBean.getProyecto()),
					"Coordenadas del proyecto");
		} catch (Exception e) {
			LOGGER.error("error al descargar ", e);
		}

	}

	public boolean validarDocumentosCertificadosInterseccionProyectoParaFinalizar() {
		return certificadoInterseccionFacade.isGeneradoCorrectamenteDocumentosCertificadoInterseccion(verProyectoBean
				.getProyecto().getId());

	}

	public void iniciarVariablesGeneracionCI() {
		verProyectoBean.setShowModalAceptarResponsabilidad(false);
		verProyectoBean.setShowMensajeErrorGeneracionDocumentos(false);
		verProyectoBean.setTransformarAlerta(true);
		verProyectoBean.setShowModalErrorProcesoInterseccion((verProyectoBean.getProyecto()
				.getProyectoInterseccionExitosa() != null) ? !verProyectoBean.getProyecto()
				.getProyectoInterseccionExitosa() : true);
		verProyectoBean
				.setAlertaProyectoIntesecaZonasProtegidas(certificadoInterseccionFacade
						.isProyectoIntersecaCapas(verProyectoBean.getProyecto().getCodigo()) ? "Su proyecto obra o actividad interseca con: "
						+ certificadoInterseccionFacade.getDetalleInterseccion(verProyectoBean.getProyecto()
								.getCodigo())
						: "");
	}

	public boolean isProyectoIntersecaZonasProtegidas() {
		return certificadoInterseccionFacade.getValueInterseccionProyectoIntersecaCapas(verProyectoBean.getProyecto());
	}

	public void eliminarPDFMapaCertificadoInterseccion() throws Exception {
		certificadoInterseccionFacade.eliminarPDFMapaCertificadoInterseccion(verProyectoBean.getProyecto().getCodigo());
	}

	public boolean isRequeridoGenerarCertificadoInterseccion() {
//		if (!verProyectoBean.isCategoriaI())
			return true;
//		else
//			return false;
	}

	public void validarProcesoGeneracionCertificadoInterseccion(
			boolean mostrarAcciones) {
		LOGGER.info("Es requeridoGenerarCertificadoInterseccion"+isRequeridoGenerarCertificadoInterseccion());
		if (isRequeridoGenerarCertificadoInterseccion()) {
			LOGGER.info("proyectoInterseccionExitosa"+verProyectoBean.getProyecto().getProyectoInterseccionExitosa());
			if (verProyectoBean.getProyecto().getProyectoInterseccionExitosa() != null
					&& verProyectoBean.getProyecto().getProyectoInterseccionExitosa())
				verProyectoBean.setShowModalCertificadoIntercepcion(mostrarAcciones);
			else {
				verProyectoBean.setShowModalCertificadoIntercepcion(false);
				verProyectoBean.setShowModalErrorProcesoInterseccion(true);
			}
		}
	}

	public void interrumpirGeneracionMapa() {
		verProyectoBean.setShowModalCertificadoIntercepcion(false);
		verProyectoBean.setTransformarAlerta(true);
	}
	public void solititarTransformacion() {
		verProyectoBean.setTransformarAlerta(false);
	}
}
