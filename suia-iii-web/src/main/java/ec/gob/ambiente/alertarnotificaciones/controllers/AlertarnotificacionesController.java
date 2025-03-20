package ec.gob.ambiente.alertarnotificaciones.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.alertarnotificaciones.facade.DocumentosPresentadosCatFacade;
import ec.gob.ambiente.alertarnotificaciones.facade.DocumentosPresentadosFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.DocumentosPresentados;
import ec.gob.ambiente.suia.domain.DocumentosPresentadosCate;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.EntityDocumentosPresentados;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AlertarnotificacionesController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4914662062998679199L;

	@EJB
	protected DocumentosPresentadosFacade documentosPresentadosFacade;
	
	@EJB
	protected AreaFacade areaFacade;

	@EJB
	protected DocumentosPresentadosCatFacade documentosPresentadosCatFacade;

	// private List<DocumentosPresentados> proyectos;
	private List<EntityDocumentosPresentados> proyectos;

	@Getter
	@Setter
	private boolean deletionActive;

	@Getter
	@Setter
	private boolean updateSuiaActive;

	@Getter
	@Setter
	private EntityDocumentosPresentados documentosPresentadoss;

	@Getter
	@Setter
	private boolean presento;

	private List<DocumentosPresentados> filteredProyectos;

	public boolean presentado;

	@PostConstruct
	private void init() {
		try {
			if(JsfUtil.getLoggedUser().getListaAreaUsuario() != null && JsfUtil.getLoggedUser().getListaAreaUsuario().size() > 0){
				proyectos = new ArrayList<>();
				for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
            		Area areaU = areaUser.getArea();
            		List<EntityDocumentosPresentados> proyectosAux = new ArrayList<>();
            		Area areaId=areaFacade.getareaPafre(areaU.getId());
        			if (areaId.getId()==null){
        				proyectosAux = documentosPresentadosFacade
        						.listaDocumentosPresentadosEntity(areaU.getId());	
        			}else{

        				proyectosAux = documentosPresentadosFacade
        						.listaDocumentosPresentadosEntity(areaId.getId());
        			}
        			
        			proyectos.addAll(proyectosAux);
				}
			}
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		deletionActive = Usuario.isUserInRole(JsfUtil.getLoggedUser(),
				"TECNICO ALERTAS NOTIFICACION");
		updateSuiaActive = Usuario.isUserInRole(JsfUtil.getLoggedUser(),
				"TECNICO ALERTAS NOTIFICACION");
	}

	public List<EntityDocumentosPresentados> getProyectos() {
		return proyectos;
	}

	public void marcar(EntityDocumentosPresentados docuemDocumentosPresentados) {
		documentosPresentadoss = docuemDocumentosPresentados;
	}

	public void presentarDocumento() {
		DocumentosPresentados documentosPresentados = new DocumentosPresentados();
		documentosPresentados.setDocumentoPresentado(presentado);
	}

	public boolean isPresentado() {
		return presentado;
	}

	public void setPresentado(boolean presentado) {
		this.presentado = presentado;
	}

	public List<DocumentosPresentados> getFilteredProyectos() {
		return filteredProyectos;
	}

	public void setFilteredProyectos(
			List<DocumentosPresentados> filteredProyectos) {
		this.filteredProyectos = filteredProyectos;
	}

	public void aceptarDocumento() {
		try {
			System.out.println("ss");
			DocumentosPresentados documentosPresentados = new DocumentosPresentados();
			DocumentosPresentadosCate documentosPresentadosCate = new DocumentosPresentadosCate();
			documentosPresentados = documentosPresentadosFacade
					.datosDocumentosPresentados(Integer
							.parseInt(documentosPresentadoss.getId()));		
			documentosPresentadosCate = documentosPresentadosCatFacade
					.datosDocumentosPresentadosCate(Integer
							.parseInt(documentosPresentadoss.getId()));
			if (documentosPresentados.getId() != null) {
				documentosPresentados.setDocumentoPresentado(presentado);
				documentosPresentados.setUsuarioCreado(JsfUtil.getLoggedUser()
						.getId());
				documentosPresentados.setFechaUsuarioCreado(new Date());
				documentosPresentados.setUsuarioModificado(JsfUtil
						.getLoggedUser().getId());
				documentosPresentados.setFechaUsuarioModificado(new Date());
				documentosPresentadosFacade
						.guadarDocumentosPresentados(documentosPresentados);
				JsfUtil.addMessageInfo(JsfUtil.getStringAsHtmlUL(
						"Tarea ejecutada con exito", false));
			}
			if (documentosPresentadosCate.getId() != null) {
				
				documentosPresentadosCate.setDocumentoPresentadoCat(presentado);
				documentosPresentadosCate.setUsuarioCreadoCat(JsfUtil
						.getLoggedUser().getId());
				documentosPresentadosCate.setFechaUsuarioCreadoCat(new Date());
				documentosPresentadosCate.setUsuarioModificadoCat(JsfUtil
						.getLoggedUser().getId());
				documentosPresentadosCate
						.setFechaUsuarioModificadoCat(new Date());
				documentosPresentadosCatFacade
						.guadarDocumentosPresentadosCat(documentosPresentadosCate);
				JsfUtil.addMessageInfo(JsfUtil.getStringAsHtmlUL(
						"Tarea ejecutada con exito", false));				
			}
			JsfUtil.addCallbackParam("eliminarProyecto");
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error al guardar la información. Contacte con Mesa de Ayuda.");
			// TODO: handle exception
		}
		System.out.println("ss");

	}
}
