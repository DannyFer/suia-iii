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
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.ModalidadReusoService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.TipoManejoDesechos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoModalidadReuso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadReuso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.TipoManejoDesechosModalidadReuso;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> <b> Clase facade para modalidad reuso. </b> </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Stateless
public class ModalidadReusoFacade {

	/**
	* 
	*/
	private static final String NOMBRE_MODALIDAD = "REUSO";

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ModalidadReusoService modalidadReusoService;

	@EJB
	private ModalidadesFacade modalidadesFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	public List<TipoManejoDesechos> getTiposTratamientoDesechoReuso() {
		return (List<TipoManejoDesechos>) modalidadReusoService.getManejoDesechos(NOMBRE_MODALIDAD);
	}

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

	public ModalidadReuso guardar(ModalidadReuso modalidad, List<TipoManejoDesechos> tiposManejoReuso, long idProceso,
			long idTarea) throws ServiceException, CmisAlfrescoException {
		modalidad.setDocumentoPlano(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoPlano(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso,
				idTarea, ModalidadReuso.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_REUSO,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		modalidad.setDocumentoRequisitos(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoRequisitos(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
				idProceso, idTarea, ModalidadReuso.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_REUSO,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		List<DesechoModalidadReuso> desechos = modalidad.getListaDesechos();
		modalidad = crudServiceBean.saveOrUpdate(modalidad);
		guardarTiposManejos(tiposManejoReuso, modalidad.getTiposManejoDesechosModalidadReuso(), modalidad);
		modalidad.setListaDesechos(guardarDesechosModalidad(desechos, modalidad));
		modalidad.getTiposManejoDesechosModalidadReuso();
		return modalidad;

	}

	@SuppressWarnings("unchecked")
	private List<DesechoModalidadReuso> guardarDesechosModalidad(List<DesechoModalidadReuso> desechosModalidad,
			ModalidadReuso modalidadReuso) {

		for (DesechoModalidadReuso desechoModalidadReuso : desechosModalidad) {
			desechoModalidadReuso.setModalidadReuso(modalidadReuso);
		}
		return (List<DesechoModalidadReuso>) crudServiceBean.saveOrUpdate(desechosModalidad);
	}

	private void guardarTiposManejos(List<TipoManejoDesechos> tiposManejoNuevo,
			List<TipoManejoDesechosModalidadReuso> tiposManejoReciclajeActual, ModalidadReuso modalidadReuso) {
		List<TipoManejoDesechosModalidadReuso> tiposManejoReciclajeExistentes = new ArrayList<TipoManejoDesechosModalidadReuso>();
		List<TipoManejoDesechos> tiposManejoExistentes = new ArrayList<TipoManejoDesechos>();
		for (TipoManejoDesechos tipoManejoNuevo : tiposManejoExistentes) {

			for (TipoManejoDesechosModalidadReuso tipoManejoDesechosModalidadReuso : tiposManejoReciclajeActual) {

				if (tipoManejoDesechosModalidadReuso.getTipoManejoDesecho().equals(tipoManejoNuevo)) {
					tiposManejoReciclajeExistentes.add(tipoManejoDesechosModalidadReuso);
					tiposManejoExistentes.add(tipoManejoNuevo);

				}
			}

		}
		List<TipoManejoDesechosModalidadReuso> listaNuevaTipos = new ArrayList<TipoManejoDesechosModalidadReuso>();
		for (TipoManejoDesechos tipo : tiposManejoNuevo) {
			TipoManejoDesechosModalidadReuso tipoModalidad = new TipoManejoDesechosModalidadReuso();
			tipoModalidad.setModalidadReuso(modalidadReuso);
			tipoModalidad.setTipoManejoDesecho(tipo);
			listaNuevaTipos.add(tipoModalidad);

		}
		tiposManejoReciclajeActual.removeAll(tiposManejoReciclajeExistentes);
		tiposManejoNuevo.removeAll(tiposManejoExistentes);

		crudServiceBean.saveOrUpdate(listaNuevaTipos);
		crudServiceBean.delete(tiposManejoReciclajeActual);
	}

	public ModalidadReuso getModalidadReuso(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		ModalidadReuso modalidad = modalidadReusoService.getModalidadReuso(aprobacionRequisitosTecnicos);
		if (modalidad != null) {
			modalidad.getTiposManejoDesechosModalidadReuso();
			modalidad.getListaDesechos();
		}
		return modalidad;
	}

	public void eliminarModalidadReusoExistente(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		ModalidadReuso modalidadReuso = null;
		try {
			modalidadReuso = getModalidadReuso(aprobacionRequisitosTecnicos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (modalidadReuso != null)
			crudServiceBean.delete(modalidadReuso);
	}

	public List<DesechoModalidadReuso> getDesechosReuso(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {

		List<DesechoModalidadReuso> listaDesechosReuso = new ArrayList<DesechoModalidadReuso>();
		List<DesechoPeligroso> lista = (List<DesechoPeligroso>) modalidadesFacade.getDesechosPorTipoElimiancion(
				ModalidadGestionDesechos.ID_MODALIDAD_REUSO, aprobacionRequisitosTecnicos);

		for (DesechoPeligroso desechoPeligroso : lista) {
			DesechoModalidadReuso dt = new DesechoModalidadReuso();
			dt.setDesecho(desechoPeligroso);
			listaDesechosReuso.add(dt);
		}
		return listaDesechosReuso;
	}

	public boolean isPageCompleta(AprobacionRequisitosTecnicos aprobacionRequisitos) throws ServiceException {
		ModalidadReuso modalidad = getModalidadReuso(aprobacionRequisitos);
		
		List<DesechoModalidadReuso> listaDesechosReuso = getDesechosReuso(aprobacionRequisitos);
		
		if (modalidad != null) {
			for (DesechoModalidadReuso desecho : modalidad.getListaDesechos()) {
				if(listaDesechosReuso != null) {
					if (!desecho.isRegistroCompleto() && listaDesechosReuso.contains(desecho)) {
						return false;
					}
				} else {
					if (!desecho.isRegistroCompleto()) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	public void guardarPaginaComoCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadReuso",
				aprobacionRequisitosTecnicos.getId().toString());

	}

	public void guardarPaginaComoInCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadReuso",
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
		List<DesechoPeligroso> desechosAlmacenados = getModalidadReuso(aprobacionRequisitosTecnicos)
				.getDesechosAsociadosModalidad();
		desechosCalculados.removeAll(desechosAlmacenados);
		return desechosCalculados;
	}

	public void guardarNuevosDesechosParaModalidad(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		List<DesechoPeligroso> desechos = nuevosDesechosParaModalidad(aprobacionRequisitosTecnicos);
		ModalidadReuso modalidad = getModalidadReuso(aprobacionRequisitosTecnicos);
		List<DesechoModalidadReuso> desechosModalidad = new ArrayList<DesechoModalidadReuso>();
		for (DesechoPeligroso desechoPeligroso : desechos) {
			desechosModalidad.add(new DesechoModalidadReuso(desechoPeligroso, modalidad));
		}
		crudServiceBean.saveOrUpdate(desechosModalidad);
	}
}
