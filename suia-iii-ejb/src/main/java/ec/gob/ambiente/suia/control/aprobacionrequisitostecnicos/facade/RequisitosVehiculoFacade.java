/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ant.DatosMatricula;
import ec.gob.ambiente.client.SuiaServices_PortType;
import ec.gob.ambiente.client.SuiaServices_ServiceLocator;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.RequisitosVehiculoService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RequisitosVehiculo;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * 
 * <b> Clase facade de la pagina requisitos de vehiculo. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 12/06/2015 $]
 *          </p>
 */
@Stateless
public class RequisitosVehiculoFacade {

	/**
	* 
	*/
	private static final String TIPO_VEHICULO_VANCOUM = "VANCOUM";

	/**
	* 
	*/
	private static final String TIPO_VEHICULO_TANQUE = "TANQUERO";

	/**
	* 
	*/
	private static final String TIPO_VEHICULO_AUTOTANQUE = "AUTOTANQUE";

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private RequisitosVehiculoService requisitosVehiculoService;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;
	

	@SuppressWarnings("unchecked")
	public List<RequisitosVehiculo> guardar(List<RequisitosVehiculo> listaRequisitosVehiculo, long idProceso,
			long idTarea) throws ServiceException, CmisAlfrescoException {
		for (RequisitosVehiculo requisitosVehiculo : listaRequisitosVehiculo) {
			if (requisitosVehiculo.getDocumentoFoto().getExtesion() != null) {
				requisitosVehiculo.setDocumentoFoto(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
						requisitosVehiculo.getDocumentoFoto(), requisitosVehiculo.getAprobacionRequisitosTecnicos().getSolicitud(),
						idProceso, idTarea, RequisitosVehiculo.class.getSimpleName(),
						AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_REQUISITOS_VEHICULO,
						TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
			} else {
				requisitosVehiculo.setDocumentoFoto(null);
			}
			if (requisitosVehiculo.getDocumentoCertificadoInspeccionTecnicaVehicular().getExtesion() != null) {
				requisitosVehiculo.setDocumentoCertificadoInspeccionTecnicaVehicular(documentosFacade
						.subirFileAlfrescoSinProyectoAsociado(
								requisitosVehiculo.getDocumentoCertificadoInspeccionTecnicaVehicular(),
								requisitosVehiculo.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso, idTarea,
								RequisitosVehiculo.class.getSimpleName(),
								AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_REQUISITOS_VEHICULO,
								TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
			} else {
				requisitosVehiculo.setDocumentoCertificadoInspeccionTecnicaVehicular(null);
			}
			if (requisitosVehiculo.getDocumentoCertificadoCalibracionTanque().getExtesion() != null) {
				requisitosVehiculo.setDocumentoCertificadoCalibracionTanque(documentosFacade
						.subirFileAlfrescoSinProyectoAsociado(requisitosVehiculo
								.getDocumentoCertificadoCalibracionTanque(), requisitosVehiculo.getAprobacionRequisitosTecnicos().getSolicitud(),
								idProceso, idTarea, RequisitosVehiculo.class.getSimpleName(),
								AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_REQUISITOS_VEHICULO,
								TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
			} else {
				requisitosVehiculo.setDocumentoCertificadoCalibracionTanque(null);
			}
			if (requisitosVehiculo.getDocumentoMatricula().getExtesion() != null) {
				requisitosVehiculo.setDocumentoMatricula(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
						requisitosVehiculo.getDocumentoMatricula(), requisitosVehiculo.getAprobacionRequisitosTecnicos().getSolicitud(),
						idProceso, idTarea, RequisitosVehiculo.class.getSimpleName(),
						AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_REQUISITOS_VEHICULO,
						TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
			} else {
				requisitosVehiculo.setDocumentoMatricula(null);
			}
		}
		return (List<RequisitosVehiculo>) crudServiceBean.saveOrUpdate(listaRequisitosVehiculo);
	}

	public RequisitosVehiculo buscarPorId(Integer id) throws ServiceException {
		try {
			RequisitosVehiculo rv = crudServiceBean.find(RequisitosVehiculo.class, id);
			if (rv != null) {
				rv.getId();
			}
			return rv;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
	}

	public void eliminar(List<RequisitosVehiculo> requisitosVehiculo) {
		crudServiceBean.delete(requisitosVehiculo);
	}

	public List<RequisitosVehiculo> getListaRequisitosVehiculo(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		return requisitosVehiculoService.getListaRequisitosVehiculo(aprobacionRequisitosTecnicos);
	}

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

	public RequisitosVehiculo obtenerVehiculoPlaca(String numeroPlaca) throws ServiceException {
		return requisitosVehiculoService.obtenerVehiculoPlaca(numeroPlaca);
	}

	public DatosMatricula buscarVehiculo(String placaVehiculo) throws ServiceException {
		try {
			return invocarWSBuscarVehiculo(placaVehiculo);
		} catch (Exception e) {
			throw new ServiceException(e.getCause());
		}
	}

	private DatosMatricula invocarWSBuscarVehiculo(String placaVehiculo) throws Exception {
		SuiaServices_ServiceLocator wsBuscarVehiculo = new SuiaServices_ServiceLocator();
		SuiaServices_PortType servicioWebBuscarVehiculo = wsBuscarVehiculo.getSuiaServicesPort(getUrlVehicleSearch());
		/*DatosMatricula respuestaServicioWebBuscarVehiculo = servicioWebBuscarVehiculo.getMatricula(placaVehiculo,
				"string", "string");*/
		DatosMatricula respuestaServicioWebBuscarVehiculo = servicioWebBuscarVehiculo.getMatricula(placaVehiculo,
				"1", Constantes.USUARIO_WS_MAE_SRI_RC);
		return respuestaServicioWebBuscarVehiculo;
	}

	private URL getUrlVehicleSearch() throws MalformedURLException {
		URL url = null;
		URL baseUrl;
		baseUrl = ec.gob.ambiente.client.SuiaServices_Service.class.getResource(".");
		url = new URL(baseUrl, Constantes.getUrlVehicleSearch());
		return url;
	}

	public boolean isTipoVehiculoParaCertificadoCalibracion(RequisitosVehiculo requisitoVehiculo) {
		if (requisitoVehiculo.getTipo().equals(TIPO_VEHICULO_AUTOTANQUE)
				|| requisitoVehiculo.getTipo().equals(TIPO_VEHICULO_TANQUE)
				|| requisitoVehiculo.getTipo().equals(TIPO_VEHICULO_VANCOUM)) {
			return true;
		}

		return false;
	}

	public boolean isPageRequitosVehiculoRequerida(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		if (aprobacionRequisitosTecnicos.isIniciadoPorNecesidad()) {
			return false;
		} else {
			return aprobacionRequisitosTecnicos.isProyectoExPost();
		}

	}

	public void guardarPaginaComoCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "requisitosVehiculo",
				aprobacionRequisitosTecnicos.getId().toString());
	}

	public void guardarPaginaComoInCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "requisitosVehiculo",
				aprobacionRequisitosTecnicos.getId().toString(), false);

	}

	public Integer getNumeroVehiculosRegistrados(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		List<RequisitosVehiculo> vehiculos = getListaRequisitosVehiculo(aprobacionRequisitosTecnicos);
		return (vehiculos != null) ? vehiculos.size() : 0;
	}

	public boolean isCamposPageRequitosVehiculoRequeridos(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		if (aprobacionRequisitosTecnicos.isIniciadoPorNecesidad()) {
			return false;
		} else {
			return aprobacionRequisitosTecnicos.isProyectoExPost();
		}
	}
	
	public Integer getNumeroRequisitosVehiculo(Integer idAprobacionRequisitosTecnicos)
			throws ServiceException {
		return requisitosVehiculoService.getNumeroRequisitosVehiculo(idAprobacionRequisitosTecnicos);
	}
	
}
