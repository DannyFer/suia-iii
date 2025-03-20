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
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.ModalidadCoprocesamientoService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ActividadProtocoloPrueba;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamiento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoDesechoProcesar;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoFormulacion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoCoprocesamiento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> <b> Clase facade para modalidad coprocesamiento. </b> </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 22/06/2015 $]
 *          </p>
 */
@Stateless
public class ModalidadCoprocesamientoFacade {

	private static final String TIPO_ACTIVIDAD_PRUEBA_COPROCESAMIENTO = "COPROCESAMIENTO";
	public static final String PLANTILLA_COOPROCESAMIENTO_PROCESOS = "Plantilla_cooprocesamiento_procesos.xlsx";
	public static final String PLANTILLA_COOPROCESAMIENTO = "Plantilla_cooprocesamiento.xlsx";

	@EJB
	DocumentosFacade documentosFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ModalidadCoprocesamientoService modalidadCoprocesamientoService;

	@EJB
	private ActividadProtocoloPruebasService actividadProtocoloPruebasService;

	@EJB
	private ModalidadesFacade modalidadesFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

	public byte[] getDocumentoInformativo(String nombre) throws ServiceException, CmisAlfrescoException {

		try {
			return documentosFacade.descargarDocumentoPorNombre(nombre);
		} catch (RuntimeException e) {
			throw new ServiceException("Error al recuperar el docuemento informativo", e);
		}
	}

	public void guardar(ModalidadCoprocesamiento modalidad, long idProceso, long idTarea,
			List<ModalidadCoprocesamientoFormulacion> formulacionesEliminacion) throws ServiceException,
			CmisAlfrescoException {

		modalidad.setPlanoPlanta(documentosFacade.subirFileAlfrescoSinProyectoAsociado(modalidad.getPlanoPlanta(),
				modalidad.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso, idTarea,
				ModalidadCoprocesamiento.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_COPROCESAMIENTO,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

		if (modalidad.getProcedimientoProtocoloPrueba() != null
				&& !modalidad.getProcedimientoProtocoloPrueba().getNombre().isEmpty()) {
			modalidad.setProcedimientoProtocoloPrueba(documentosFacade.subirFileAlfrescoSinProyectoAsociado(modalidad
					.getProcedimientoProtocoloPrueba(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
					idProceso, idTarea, ModalidadCoprocesamiento.class.getSimpleName(),
					AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_COPROCESAMIENTO,
					TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		} else {
			modalidad.setProcedimientoProtocoloPrueba(null);
		}

		modalidad.setRequisitosProtocoloPrueba(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getRequisitosProtocoloPrueba(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
				idProceso, idTarea, ModalidadCoprocesamiento.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_COPROCESAMIENTO,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

		modalidad.setResumenEjecutivoProtocoloPruebas(documentosFacade.subirFileAlfrescoSinProyectoAsociado(modalidad
				.getResumenEjecutivoProtocoloPruebas(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
				idProceso, idTarea, ModalidadCoprocesamiento.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_COPROCESAMIENTO,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

		modalidad.setSistemaAlimentacionDesechosYOperacionesActividad(documentosFacade
				.subirFileAlfrescoSinProyectoAsociado(modalidad.getSistemaAlimentacionDesechosYOperacionesActividad(),
						modalidad.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso, idTarea,
						ModalidadCoprocesamiento.class.getSimpleName(),
						AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_COPROCESAMIENTO,
						TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

		modalidad.setRequisitosCoprocesamientoDesechoPeligroso(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getRequisitosCoprocesamientoDesechoPeligroso(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
				idProceso, idTarea, ModalidadCoprocesamiento.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_COPROCESAMIENTO,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		List<ModalidadCoprocesamientoDesecho> desechos = modalidad.getModalidadCoprocesamientoDesechos();
		List<ModalidadCoprocesamientoDesechoProcesar> procesados = modalidad.getModalidadDesechoProcesados();
		List<ModalidadCoprocesamientoFormulacion> formulaciones = modalidad.getModalidadCoprocesamientoFormulaciones();

		modalidad = crudServiceBean.saveOrUpdate(modalidad);
		for (ModalidadCoprocesamientoDesecho desecho : desechos) {
			desecho.setModalidadCoprocesamiento(modalidad);
			crudServiceBean.saveOrUpdate(desecho);

		}
		for (ModalidadCoprocesamientoDesechoProcesar procesar : procesados) {
			procesar.setModalidadCoprocesamiento(modalidad);
			crudServiceBean.saveOrUpdate(procesar);
		}
		for (ModalidadCoprocesamientoFormulacion formulacion : formulaciones) {
			formulacion.setModalidadCoprocesamiento(modalidad);
			crudServiceBean.saveOrUpdate(formulacion);

		}
		crudServiceBean.delete(formulacionesEliminacion);

	}

	public void eliminarModalidadCoprocesamientoExistente(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		ModalidadCoprocesamiento modalidadCoprocesamiento = null;
		try {
			modalidadCoprocesamiento = getModalidadCoprocesamiento(aprobacionRequisitosTecnicos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (modalidadCoprocesamiento != null)
			eliminarModalidadCoprocesamiento(modalidadCoprocesamiento);
	}

	public void eliminarModalidadCoprocesamiento(ModalidadCoprocesamiento modalidadCoprocesamiento) {
		crudServiceBean.delete(modalidadCoprocesamiento);
	}

	public ModalidadCoprocesamiento getModalidadCoprocesamiento(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		ModalidadCoprocesamiento modalidad = modalidadCoprocesamientoService
				.getModalidadCoprocesamiento(aprobacionRequisitosTecnicos);
		return modalidad;
	}

	public List<ProgramaCalendarizadoCoprocesamiento> getCalendarioActividades(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		ModalidadCoprocesamiento modalidad = getModalidadCoprocesamiento(aprobacionRequisitosTecnicos);
		List<ProgramaCalendarizadoCoprocesamiento> calendarioActividades;
		try {
			calendarioActividades = modalidadCoprocesamientoService.getCalendarioActividadesPorModalidad(modalidad);
		} catch (Exception e) {
			calendarioActividades = new ArrayList<ProgramaCalendarizadoCoprocesamiento>();
		}
		return calendarioActividades;
	}

	public List<ActividadProtocoloPrueba> getActividadesProtocoloPrueba() throws ServiceException {
		return actividadProtocoloPruebasService.getActividadPrueba(TIPO_ACTIVIDAD_PRUEBA_COPROCESAMIENTO);
	}

	public ProgramaCalendarizadoCoprocesamiento guardar(ProgramaCalendarizadoCoprocesamiento calendario)
			throws ServiceException {
		return crudServiceBean.saveOrUpdate(calendario);
	}

	public void guardar(List<ProgramaCalendarizadoCoprocesamiento> listaCalendario, Integer idModalidad)
			throws ServiceException {
		List<ProgramaCalendarizadoCoprocesamiento> actividadesExistentes = new ArrayList();

		for (ProgramaCalendarizadoCoprocesamiento actividadCalendario : listaCalendario) {
			if (actividadCalendario.getId() != null) {
				actividadesExistentes.add(actividadCalendario);
			}
		}

		if (!actividadesExistentes.isEmpty()) {
			crudServiceBean
					.getEntityManager()
					.createQuery(
							"Update ProgramaCalendarizadoCoprocesamiento p SET p.estado='false' "
									+ "where p.modalidadCoprocesamiento.id=:idModalidad "
									+ "and p.estado=true and p not in(:actividadesExistentes)")
					.setParameter("idModalidad", idModalidad)
					.setParameter("actividadesExistentes", actividadesExistentes).executeUpdate();
		}

		crudServiceBean.saveOrUpdate(listaCalendario);
	}

	public List<DesechoPeligroso> getDesechosCoprocesamiento(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		return (List<DesechoPeligroso>) modalidadesFacade.getDesechosPorTipoElimiancion(
				ModalidadGestionDesechos.ID_MODALIDAD_COPROCESAMIENTO, aprobacionRequisitosTecnicos);

	}

	public boolean isPageCompleta(AprobacionRequisitosTecnicos aprobacionRequisitos) throws ServiceException {
		ModalidadCoprocesamiento modalidad = getModalidadCoprocesamiento(aprobacionRequisitos);

		if (modalidad != null) {
			List<ModalidadCoprocesamientoDesecho> desechosCoprocesamiento = modalidad
					.getModalidadCoprocesamientoDesechos();
			List<ModalidadCoprocesamientoDesechoProcesar> desechosProcesar = modalidad.getModalidadDesechoProcesados();
			for (ModalidadCoprocesamientoDesecho desecho : desechosCoprocesamiento) {
				if (!desecho.isRegistroCompleto()) {
					return false;
				}
			}
			for (ModalidadCoprocesamientoDesechoProcesar desecho : desechosProcesar) {
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
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadCoprocesamiento",
				aprobacionRequisitosTecnicos.getId().toString());
	}

	public void guardarPaginaComoInCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadCoprocesamiento",
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
				.getDesechosPorTipoElimiancion(ModalidadGestionDesechos.ID_MODALIDAD_COPROCESAMIENTO,
						aprobacionRequisitosTecnicos);

		List<DesechoPeligroso> desechosAlmacenados = getModalidadCoprocesamiento(aprobacionRequisitosTecnicos)
				.getDesechosAsociadosModalidad();

		desechosCalculados.removeAll(desechosAlmacenados);
		return desechosCalculados;
	}

	public void guardarNuevosDesechosParaModalidad(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {

		List<DesechoPeligroso> desechos = nuevosDesechosParaModalidad(aprobacionRequisitosTecnicos);
		ModalidadCoprocesamiento modalidad = getModalidadCoprocesamiento(aprobacionRequisitosTecnicos);
		List<ModalidadCoprocesamientoDesecho> desechosModalidad = new ArrayList<ModalidadCoprocesamientoDesecho>();
		List<ModalidadCoprocesamientoDesechoProcesar> desechosModalidadProcesar = new ArrayList<ModalidadCoprocesamientoDesechoProcesar>();
		for (DesechoPeligroso desechoPeligroso : desechos) {
			desechosModalidad.add(new ModalidadCoprocesamientoDesecho(desechoPeligroso, modalidad));
			desechosModalidadProcesar.add(new ModalidadCoprocesamientoDesechoProcesar(desechoPeligroso, modalidad));
		}
		crudServiceBean.saveOrUpdate(desechosModalidad);
		crudServiceBean.saveOrUpdate(desechosModalidadProcesar);
	}
}
