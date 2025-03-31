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
import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.ModalidadReciclajeService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.TipoManejoDesechos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoModalidadReciclaje;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadReciclaje;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.TipoManejoDesechosModalidadReciclaje;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> <b> Clase facade para modalidad reciclaje. </b> </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Stateless
public class ModalidadReciclajeFacade {

	/**
	* 
	*/
	private static final String NOMBRE_MODALIDAD = "RECICLAJE";

	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ModalidadReciclajeService modalidadReciclajeService;

	@EJB
	private ModalidadesFacade modalidadesFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	public List<TipoManejoDesechos> getTiposTratamientoDesechoReciclaje() {
		return (List<TipoManejoDesechos>) modalidadReciclajeService.getManejoDesechos(NOMBRE_MODALIDAD);

	}

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

	public ModalidadReciclaje guardar(ModalidadReciclaje modalidad, List<TipoManejoDesechos> tiposManejoReciclaje,
			long idProceso, long idTarea) throws ServiceException, CmisAlfrescoException {
		modalidad.setDocumentoPlano(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoPlano(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso,
				idTarea, ModalidadReciclaje.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_RECICLAJE,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		modalidad.setDocumentoRequisitos(documentosFacade.subirFileAlfrescoSinProyectoAsociado(
				modalidad.getDocumentoRequisitos(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
				idProceso, idTarea, ModalidadReciclaje.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_RECICLAJE,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

		List<DesechoModalidadReciclaje> desechos = modalidad.getListaDesechos();
		modalidad = crudServiceBean.saveOrUpdate(modalidad);
		guardarTiposManejos(tiposManejoReciclaje, modalidad.getTiposManejoDesechosModalidadReciclaje(), modalidad);
		modalidad.setListaDesechos(guardarDesechosModalidad(desechos, modalidad));
		modalidad.getTiposManejoDesechosModalidadReciclaje();

		return modalidad;
	}

	@SuppressWarnings("unchecked")
	private List<DesechoModalidadReciclaje> guardarDesechosModalidad(List<DesechoModalidadReciclaje> desechosModalidad,
			ModalidadReciclaje modalidadReciclaje) {

		for (DesechoModalidadReciclaje desechoModalidadReciclaje : desechosModalidad) {
			desechoModalidadReciclaje.setModalidadReciclaje(modalidadReciclaje);
		}
		return (List<DesechoModalidadReciclaje>) crudServiceBean.saveOrUpdate(desechosModalidad);
	}

	private void guardarTiposManejos(List<TipoManejoDesechos> tiposManejoNuevo,
			List<TipoManejoDesechosModalidadReciclaje> tiposManejoReciclajeActual, ModalidadReciclaje modalidadReciclaje) {
		List<TipoManejoDesechosModalidadReciclaje> tiposManejoReciclajeExistentes = new ArrayList<TipoManejoDesechosModalidadReciclaje>();
		List<TipoManejoDesechos> tiposManejoExistentes = new ArrayList<TipoManejoDesechos>();
		for (TipoManejoDesechos tipoManejoNuevo : tiposManejoExistentes) {

			for (TipoManejoDesechosModalidadReciclaje tipoManejoDesechosModalidadReciclaje : tiposManejoReciclajeActual) {

				if (tipoManejoDesechosModalidadReciclaje.getTipoManejoDesecho().equals(tipoManejoNuevo)) {
					tiposManejoReciclajeExistentes.add(tipoManejoDesechosModalidadReciclaje);
					tiposManejoExistentes.add(tipoManejoNuevo);

				}
			}

		}
		List<TipoManejoDesechosModalidadReciclaje> listaNuevaTipos = new ArrayList<TipoManejoDesechosModalidadReciclaje>();
		for (TipoManejoDesechos tipo : tiposManejoNuevo) {
			TipoManejoDesechosModalidadReciclaje tipoModalidad = new TipoManejoDesechosModalidadReciclaje();
			tipoModalidad.setModalidadReciclaje(modalidadReciclaje);
			tipoModalidad.setTipoManejoDesecho(tipo);
			listaNuevaTipos.add(tipoModalidad);

		}
		tiposManejoReciclajeActual.removeAll(tiposManejoReciclajeExistentes);
		tiposManejoNuevo.removeAll(tiposManejoExistentes);

		crudServiceBean.saveOrUpdate(listaNuevaTipos);
		crudServiceBean.delete(tiposManejoReciclajeActual);
	}

	public ModalidadReciclaje getModalidadReciclaje(AprobacionRequisitosTecnicos aprobacion) throws ServiceException {
		ModalidadReciclaje modalidad = modalidadReciclajeService.getModalidadReciclaje(aprobacion);
		if (modalidad != null) {
			modalidad.getTiposManejoDesechosModalidadReciclaje();
			modalidad.getListaDesechos();
		}
		return modalidad;
	}

	public void eliminarModalidadReciclaje(ModalidadReciclaje modalidadReciclaje) {
		crudServiceBean.delete(modalidadReciclaje);
	}

	public void eliminarModalidadReciclajeExistente(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		ModalidadReciclaje modalidadReciclaje = null;
		try {
			modalidadReciclaje = getModalidadReciclaje(aprobacionRequisitosTecnicos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (modalidadReciclaje != null)
			eliminarModalidadReciclaje(modalidadReciclaje);
	}

	public List<DesechoModalidadReciclaje> getDesechosReciclaje(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {

		List<DesechoModalidadReciclaje> listaDesechosReciclaje = new ArrayList<DesechoModalidadReciclaje>();
		List<DesechoPeligroso> lista = (List<DesechoPeligroso>) modalidadesFacade.getDesechosPorTipoElimiancion(
				ModalidadGestionDesechos.ID_MODALIDAD_RECICLAJE, aprobacionRequisitosTecnicos);

		for (DesechoPeligroso desechoPeligroso : lista) {
			DesechoModalidadReciclaje dt = new DesechoModalidadReciclaje();
			dt.setDesecho(desechoPeligroso);
			listaDesechosReciclaje.add(dt);
		}
		return listaDesechosReciclaje;
	}

	public boolean isPageCompleta(AprobacionRequisitosTecnicos aprobacionRequisitos) throws ServiceException {
		ModalidadReciclaje modalidad = getModalidadReciclaje(aprobacionRequisitos);
		
		List<DesechoModalidadReciclaje> listaDesechosEliminados = getDesechosReciclaje(aprobacionRequisitos);
		
		if (modalidad != null) {
			for (DesechoModalidadReciclaje desecho : modalidad.getListaDesechos()) {
				if(listaDesechosEliminados != null) {
					if (!desecho.isRegistroCompleto() && listaDesechosEliminados.contains(desecho)) {
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
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadReciclaje",
				aprobacionRequisitosTecnicos.getId().toString());
	}

	public void guardarPaginaComoInCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadReciclaje",
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
		List<DesechoPeligroso> desechosAlmacenados = getModalidadReciclaje(aprobacionRequisitosTecnicos)
				.getDesechosAsociadosModalidad();
		desechosCalculados.removeAll(desechosAlmacenados);
		return desechosCalculados;
	}

	public void guardarNuevosDesechosParaModalidad(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		List<DesechoPeligroso> desechos = nuevosDesechosParaModalidad(aprobacionRequisitosTecnicos);
		ModalidadReciclaje modalidad = getModalidadReciclaje(aprobacionRequisitosTecnicos);
		List<DesechoModalidadReciclaje> desechosModalidad = new ArrayList<DesechoModalidadReciclaje>();
		for (DesechoPeligroso desechoPeligroso : desechos) {
			desechosModalidad.add(new DesechoModalidadReciclaje(desechoPeligroso, modalidad));
		}
		crudServiceBean.saveOrUpdate(desechosModalidad);
	}
}
