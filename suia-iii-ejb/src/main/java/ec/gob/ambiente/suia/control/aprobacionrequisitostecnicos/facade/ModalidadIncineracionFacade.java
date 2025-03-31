/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.ActividadProtocoloPruebasService;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.ModalidadIncineracionService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ActividadProtocoloPrueba;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesechoProcesar;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionFormulacion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoIncineracion;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> <b> Clase facade para modalidad incineracion. </b> </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Stateless
public class ModalidadIncineracionFacade {

	/**
     *
     */
	private static final String NOMBRE_MODALIDAD = "INCINERACION";

	public static final String PLANTILLA_INCINERACION_PROCESOS = "Plantilla_incineracion_procesos.xlsx";
	public static final String PLANTILLA_INCINERACION = "Plantilla_incineracion.xlsx";

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ModalidadIncineracionService modalidadIncineracionService;

	@EJB
	private ActividadProtocoloPruebasService actividadProtocoloPruebasService;

	@EJB
	private ModalidadesFacade modalidadesFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

	public void guardar(ModalidadIncineracion modalidad, long idProceso, long idTarea,
			List<ModalidadIncineracionFormulacion> formulacionesEliminacion) throws ServiceException,
			CmisAlfrescoException {
		gurdarDocumentos(modalidad, idProceso, idTarea);
		List<ModalidadIncineracionDesecho> desechos = modalidad.getModalidadIncineracionDesechos();
		List<ModalidadIncineracionDesechoProcesar> procesados = modalidad.getModalidadIncineracionDesechoProcesados();
		List<ModalidadIncineracionFormulacion> formulaciones = modalidad.getModalidadIncineracionFormulaciones();
		modalidad = crudServiceBean.saveOrUpdate(modalidad);
		modalidad.getDocumentoPlano();
		modalidad.getDocumentoRequisitos();
		modalidad.getDocumentoCombustible();
		modalidad.getDocumentoProtocoloPruebas();
		modalidad.getDocumentoProcedimiento();
		modalidad.getDocumentoDesechosBiologicosIncineracion();
		modalidad.getDocumentoDesechosBiologicosProtocoloIncineracion();
		modalidad.getDocumentoPruebas();

		modalidad = crudServiceBean.saveOrUpdate(modalidad);
		for (ModalidadIncineracionDesecho desecho : desechos) {
			desecho.setModalidadIncineracion(modalidad);
			crudServiceBean.saveOrUpdate(desecho);
		}
		for (ModalidadIncineracionDesechoProcesar procesar : procesados) {
			procesar.setModalidadIncineracion(modalidad);
			crudServiceBean.saveOrUpdate(procesar);
		}
		for (ModalidadIncineracionFormulacion formulacion : formulaciones) {
			formulacion.setModalidadIncineracion(modalidad);
			crudServiceBean.saveOrUpdate(formulacion);
		}
		crudServiceBean.delete(formulacionesEliminacion);

	}

	private void gurdarDocumentos(ModalidadIncineracion modalidad, long idProceso, long idTarea)
			throws ServiceException, CmisAlfrescoException {
		modalidad.setDocumentoPlano(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoPlano(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso,
				idTarea, ModalidadIncineracion.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_INCINERACION,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		modalidad.setDocumentoRequisitos(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoRequisitos(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
				idProceso, idTarea, ModalidadIncineracion.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_INCINERACION,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		modalidad.setDocumentoCombustible(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoCombustible(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
				idProceso, idTarea, ModalidadIncineracion.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_INCINERACION,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		modalidad.setDocumentoProtocoloPruebas(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoProtocoloPruebas(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
				idProceso, idTarea, ModalidadIncineracion.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_INCINERACION,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		if (!modalidad.getDocumentoProcedimiento().getNombre().isEmpty()) {
			modalidad.setDocumentoProcedimiento(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
					modalidad.getDocumentoProcedimiento(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
					idProceso, idTarea, ModalidadIncineracion.class.getSimpleName(),
					AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_INCINERACION,
					TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		} else {
			modalidad.setDocumentoProcedimiento(null);
		}
		if (modalidad.getTrataDesechosBiologicosInfecciosos()) {
			modalidad.setDocumentoDesechosBiologicosIncineracion(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
					modalidad.getDocumentoDesechosBiologicosIncineracion(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
					idProceso, idTarea, ModalidadIncineracion.class.getSimpleName(),
					AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_INCINERACION,
					TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
			modalidad.setDocumentoDesechosBiologicosProtocoloIncineracion(documentosFacade
					.subirFileAlfrescoSinProyectoAsociado(modalidad
							.getDocumentoDesechosBiologicosProtocoloIncineracion(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
							idProceso, idTarea, ModalidadIncineracion.class.getSimpleName(),
							AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_INCINERACION,
							TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		} else {
			modalidad.setDocumentoDesechosBiologicosIncineracion(null);
			modalidad.setDocumentoDesechosBiologicosProtocoloIncineracion(null);
		}
		modalidad.setDocumentoPruebas(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoPruebas(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso,
				idTarea, ModalidadIncineracion.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_INCINERACION,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
	}

	public ModalidadIncineracion getModalidadIncineracion(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		ModalidadIncineracion modalidad = modalidadIncineracionService
				.getModalidadTratamiento(aprobacionRequisitosTecnicos);
		if (modalidad != null) {
			modalidad.getDocumentoPlano();
			modalidad.getDocumentoRequisitos();
			modalidad.getDocumentoCombustible();
			modalidad.getDocumentoProtocoloPruebas();
			modalidad.getDocumentoProcedimiento();
			modalidad.getDocumentoDesechosBiologicosIncineracion();
			modalidad.getDocumentoDesechosBiologicosProtocoloIncineracion();
			modalidad.getDocumentoPruebas();
		}
		return modalidad;
	}

	public void eliminarModalidadIncineracionExistente(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		ModalidadIncineracion modalidadIncineracion = null;
		try {
			modalidadIncineracion = getModalidadIncineracion(aprobacionRequisitosTecnicos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (modalidadIncineracion != null)
			crudServiceBean.delete(modalidadIncineracion);
	}

	public List<ActividadProtocoloPrueba> getActividadesProtocoloPrueba() throws ServiceException {
		return actividadProtocoloPruebasService.getActividadPrueba(NOMBRE_MODALIDAD);
	}

	public ProgramaCalendarizadoIncineracion guardar(ProgramaCalendarizadoIncineracion calendario)
			throws ServiceException {
		return crudServiceBean.saveOrUpdate(calendario);
	}

	public void guardar(List<ProgramaCalendarizadoIncineracion> listaCalendario) throws ServiceException {
		crudServiceBean.saveOrUpdate(listaCalendario);
	}

	public void guardar(List<ProgramaCalendarizadoIncineracion> listaCalendario, Integer idModalidad)
			throws ServiceException {
		List<ProgramaCalendarizadoIncineracion> actividadesExistentes = new ArrayList();

		for (ProgramaCalendarizadoIncineracion actividadCalendario : listaCalendario) {
			if (actividadCalendario.getId() != null) {
				actividadesExistentes.add(actividadCalendario);
			}
		}

		if (!actividadesExistentes.isEmpty()) {
			crudServiceBean
					.getEntityManager()
					.createQuery(
							"Update ProgramaCalendarizadoIncineracion p SET p.estado='false' "
									+ "where p.modalidadIncineracion.id=:idModalidad "
									+ "and p.estado=true and p not in(:actividadesExistentes)")
					.setParameter("idModalidad", idModalidad)
					.setParameter("actividadesExistentes", actividadesExistentes).executeUpdate();
		}

		crudServiceBean.saveOrUpdate(listaCalendario);
	}

	public byte[] getDocumentoInformativo(String nombre) throws ServiceException, CmisAlfrescoException {

		try {
			return documentosFacade.descargarDocumentoPorNombre(nombre);
		} catch (RuntimeException e) {
			throw new ServiceException("Error al recuperar el docuemento informativo", e);
		}
	}

	public List<ProgramaCalendarizadoIncineracion> getCalendarioActividades(ModalidadIncineracion modalidad)
			throws ServiceException {
		List<ProgramaCalendarizadoIncineracion> calendarioActividades = modalidadIncineracionService
				.getCalendarioActividades(modalidad);
		if (calendarioActividades == null) {
			calendarioActividades = new ArrayList<ProgramaCalendarizadoIncineracion>();
		}
		return calendarioActividades;
	}

	public List<DesechoPeligroso> getDesechosIncineracion(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		return (List<DesechoPeligroso>) modalidadesFacade.getDesechosPorTipoElimiancion(
				ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION, aprobacionRequisitosTecnicos);

	}

	public boolean isPageCompleta(AprobacionRequisitosTecnicos aprobacionRequisitos) throws ServiceException {
		ModalidadIncineracion modalidad = getModalidadIncineracion(aprobacionRequisitos);

		if (modalidad != null) {
			List<ModalidadIncineracionDesecho> desechosIncineracion = modalidad.getModalidadIncineracionDesechos();
			List<ModalidadIncineracionDesechoProcesar> desechosProcesar = modalidad
					.getModalidadIncineracionDesechoProcesados();
			for (ModalidadIncineracionDesecho desecho : desechosIncineracion) {
				if (!desecho.isRegistroCompleto()) {
					return false;
				}
			}
			for (ModalidadIncineracionDesechoProcesar desecho : desechosProcesar) {
				if (!desecho.isRegistroCompleto()) {
					return false;
				}
			}

			return !existeNuevosDesechosParaModalidad(aprobacionRequisitos);
		}
		return false;
	}

	public void guardarPaginaComoCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadIncineracion",
				aprobacionRequisitosTecnicos.getId().toString());
	}

	public void guardarPaginaComoInCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadIncineracion",
				aprobacionRequisitosTecnicos.getId().toString(), false);

	}

	public boolean existeNuevosDesechosParaModalidad(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		List<DesechoPeligroso> desechos = nuevosDesechosParaModalidad(aprobacionRequisitosTecnicos);
		if (desechos.size() > 0) {
			return true;
		}
		return false;
	}

	public List<DesechoPeligroso> nuevosDesechosParaModalidad(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		List<DesechoPeligroso> desechosCalculados = (List<DesechoPeligroso>) modalidadesFacade
				.getDesechosPorTipoElimiancion(ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION,
						aprobacionRequisitosTecnicos);

		List<DesechoPeligroso> desechosAlmacenados = getModalidadIncineracion(aprobacionRequisitosTecnicos)
				.getDesechosAsociadosModalidad();
		desechosCalculados.removeAll(desechosAlmacenados);
		return desechosCalculados;
	}

	public void guardarNuevosDesechosParaModalidad(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {

		List<DesechoPeligroso> desechos = nuevosDesechosParaModalidad(aprobacionRequisitosTecnicos);
		ModalidadIncineracion modalidad = getModalidadIncineracion(aprobacionRequisitosTecnicos);
		List<ModalidadIncineracionDesecho> desechosModalidad = new ArrayList<ModalidadIncineracionDesecho>();
		List<ModalidadIncineracionDesechoProcesar> desechosModalidadProcesar = new ArrayList<ModalidadIncineracionDesechoProcesar>();
		for (DesechoPeligroso desechoPeligroso : desechos) {
			desechosModalidad.add(new ModalidadIncineracionDesecho(desechoPeligroso, modalidad));
			desechosModalidadProcesar.add(new ModalidadIncineracionDesechoProcesar(desechoPeligroso, modalidad));
		}
		crudServiceBean.saveOrUpdate(desechosModalidad);
		crudServiceBean.saveOrUpdate(desechosModalidadProcesar);
	}
}
