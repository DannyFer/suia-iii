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
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.ModalidadTratamientoService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ActividadProtocoloPrueba;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoModalidadTratamiento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadTratamiento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoTratamiento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> <b> Clase facade para modalidad tratamiento. </b> </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Stateless
public class ModalidadTratamientoFacade {

	public static final String PLANTILLA_TRATAMIENTO_PROCESOS = "Plantilla_tratamiento_procesos.xlsx";
	public static final String PLANTILLA_TRATAMIENTO = "Plantilla_tratamiento.xlsx";

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ModalidadTratamientoService modalidadTratamientoService;

	@EJB
	private ActividadProtocoloPruebasService actividadProtocoloPruebasService;

	@EJB
	private ModalidadesFacade modalidadesFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

	public ModalidadTratamiento guardar(ModalidadTratamiento modalidad, long idProceso, long idTarea)
			throws ServiceException, CmisAlfrescoException {
		gurdarDocumentos(modalidad, idProceso, idTarea);
		List<DesechoModalidadTratamiento> desechos = modalidad.getListaDesechos();
		modalidad = crudServiceBean.saveOrUpdate(modalidad);
		modalidad.setListaDesechos(guardarDesechosModalidad(desechos, modalidad));
		modalidad.getDocumentoPlano();
		modalidad.getDocumentoRequisitos();
		modalidad.getDocumentoRequisitosProductoProceso();
		modalidad.getDocumentoDesechosBiologicos();
		modalidad.getDocumentoPruebas();
		return modalidad;
	}

	public void eliminarModalidadTratamiento(ModalidadTratamiento modalidadTratamiento) {
		crudServiceBean.delete(modalidadTratamiento);
	}

	public void eliminarModalidadTratamientoExistente(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		ModalidadTratamiento modalidadTratamiento = null;
		try {
			modalidadTratamiento = getModalidadTratamiento(aprobacionRequisitosTecnicos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (modalidadTratamiento != null)
			eliminarModalidadTratamiento(modalidadTratamiento);
	}

	@SuppressWarnings("unchecked")
	private List<DesechoModalidadTratamiento> guardarDesechosModalidad(
			List<DesechoModalidadTratamiento> desechosModalidad, ModalidadTratamiento modalidadTratamiento) {

		for (DesechoModalidadTratamiento desechoModalidadTratamiento : desechosModalidad) {
			desechoModalidadTratamiento.getId();
			desechoModalidadTratamiento.setModalidadTratamiento(modalidadTratamiento);
		}
		return (List<DesechoModalidadTratamiento>) crudServiceBean.saveOrUpdate(desechosModalidad);
	}

	private void gurdarDocumentos(ModalidadTratamiento modalidad, long idProceso, long idTarea)
			throws ServiceException, CmisAlfrescoException {
		modalidad.setDocumentoPlano(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoPlano(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso,
				idTarea, ModalidadTratamiento.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_TRATAMIENTO,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		modalidad.setDocumentoRequisitos(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoRequisitos(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
				idProceso, idTarea, ModalidadTratamiento.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_TRATAMIENTO,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		modalidad.setDocumentoRequisitosProductoProceso(documentosFacade.subirFileAlfrescoSinProyectoAsociado(modalidad
				.getDocumentoRequisitosProductoProceso(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
				idProceso, idTarea, ModalidadTratamiento.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_TRATAMIENTO,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		if (modalidad.getTrataDesechosBiologicosInfecciosos()) {
			modalidad.setDocumentoDesechosBiologicos(documentosFacade.subirFileAlfrescoSinProyectoAsociado(modalidad
					.getDocumentoDesechosBiologicos(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
					idProceso, idTarea, ModalidadTratamiento.class.getSimpleName(),
					AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_TRATAMIENTO,
					TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		} else {
			modalidad.setDocumentoDesechosBiologicos(null);
		}
		modalidad.setDocumentoPruebas(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoPruebas(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso,
				idTarea, ModalidadTratamiento.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_TRATAMIENTO,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
	}

	public ModalidadTratamiento getModalidadTratamiento(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		ModalidadTratamiento modalidad = modalidadTratamientoService
				.getModalidadTratamiento(aprobacionRequisitosTecnicos);
		if (modalidad != null) {
			modalidad.getDocumentoPlano();
			modalidad.getDocumentoRequisitos();
			modalidad.getDocumentoRequisitosProductoProceso();
			modalidad.getDocumentoDesechosBiologicos();
			modalidad.getDocumentoPruebas();
			modalidad.getAprobacionRequisitosTecnicos();
			modalidad.getListaDesechos();
		}
		return modalidad;
	}

	public List<ActividadProtocoloPrueba> getActividadesProtocoloPrueba() throws ServiceException {
		return actividadProtocoloPruebasService.getActividadPrueba("TRATAMIENTO");
	}

	public ProgramaCalendarizadoTratamiento guardar(ProgramaCalendarizadoTratamiento calendario)
			throws ServiceException {
		return crudServiceBean.saveOrUpdate(calendario);
	}

	public void guardar(List<ProgramaCalendarizadoTratamiento> listaCalendario, Integer idModalidad)
			throws ServiceException {
		List<ProgramaCalendarizadoTratamiento> actividadesExistentes = new ArrayList();

		for (ProgramaCalendarizadoTratamiento actividadCalendario : listaCalendario) {
			if (actividadCalendario.getId() != null) {
				actividadesExistentes.add(actividadCalendario);
			}
		}

		if (!actividadesExistentes.isEmpty()) {
			crudServiceBean
					.getEntityManager()
					.createQuery(
							"Update ProgramaCalendarizadoTratamiento p SET p.estado='false' "
									+ "where p.modalidadTratamiento.id=:idModalidad "
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

	public List<ProgramaCalendarizadoTratamiento> getCalendarioActividades(ModalidadTratamiento modalidadTratamiento)
			throws ServiceException {
		List<ProgramaCalendarizadoTratamiento> actividades = modalidadTratamientoService
				.getCalendarioActividades(modalidadTratamiento);
		if (actividades == null) {
			actividades = new ArrayList<ProgramaCalendarizadoTratamiento>();
		}
		return modalidadTratamientoService.getCalendarioActividades(modalidadTratamiento);
	}

	public List<DesechoModalidadTratamiento> getDesechosTratamiento(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {

		List<DesechoModalidadTratamiento> listaDesechosTratamiento = new ArrayList<DesechoModalidadTratamiento>();
		List<DesechoPeligroso> lista = (List<DesechoPeligroso>) modalidadesFacade.getDesechosPorTipoElimiancion(
				ModalidadGestionDesechos.ID_MODALIDAD_TRATAMIENTO, aprobacionRequisitosTecnicos);

		for (DesechoPeligroso desechoPeligroso : lista) {
			DesechoModalidadTratamiento dt = new DesechoModalidadTratamiento();
			dt.setDesecho(desechoPeligroso);
			listaDesechosTratamiento.add(dt);
		}
		return listaDesechosTratamiento;
	}

	public boolean isPageCompleta(AprobacionRequisitosTecnicos aprobacionRequisitos) throws ServiceException {
		ModalidadTratamiento modalidad = getModalidadTratamiento(aprobacionRequisitos);
		if (modalidad != null) {
			for (DesechoModalidadTratamiento desecho : modalidad.getListaDesechos()) {
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
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadTratamiento",
				aprobacionRequisitosTecnicos.getId().toString());
	}

	public void guardarPaginaComoInCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadTratamiento",
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
				.getDesechosPorTipoElimiancion(ModalidadGestionDesechos.ID_MODALIDAD_TRATAMIENTO,
						aprobacionRequisitosTecnicos);
		List<DesechoPeligroso> desechosAlmacenados = getModalidadTratamiento(aprobacionRequisitosTecnicos)
				.getDesechosAsociadosModalidad();
		desechosCalculados.removeAll(desechosAlmacenados);
		return desechosCalculados;
	}

	public void guardarNuevosDesechosParaModalidad(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		List<DesechoPeligroso> desechos = nuevosDesechosParaModalidad(aprobacionRequisitosTecnicos);
		ModalidadTratamiento modalidad = getModalidadTratamiento(aprobacionRequisitosTecnicos);
		List<DesechoModalidadTratamiento> desechosModalidad = new ArrayList<DesechoModalidadTratamiento>();
		for (DesechoPeligroso desechoPeligroso : desechos) {
			desechosModalidad.add(new DesechoModalidadTratamiento(desechoPeligroso, modalidad));
		}
		crudServiceBean.saveOrUpdate(desechosModalidad);
	}

}
