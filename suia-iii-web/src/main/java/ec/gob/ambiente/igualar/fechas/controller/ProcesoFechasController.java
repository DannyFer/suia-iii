package ec.gob.ambiente.igualar.fechas.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class ProcesoFechasController {
	
	@Getter
	@Setter
	private Integer idProcesoAnterior;
	
	@Getter
	@Setter
	private Integer idProcesoActual;
	
	@EJB
	private ProcesoFechasFacade procesoFechasFacade;
	
	@Getter
	@Setter
	private List<ProcesoFechas>listProcesoFechas= new ArrayList<ProcesoFechas>();
	
	@EJB
	private PersonaFacade personaFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@PostConstruct
	private void init(){
		
	}
	
	public void consultarFechas() {
		listProcesoFechas=procesoFechasFacade.listConsultarFechasXProceso(idProcesoAnterior,idProcesoActual);
		idProcesoAnterior=null;
		idProcesoActual=null;
	}
	
	public void eliminarRegistro(ProcesoFechas procesoFechas){
		listProcesoFechas.remove(procesoFechas);
	}
	
	public void actualizarRegistro(ProcesoFechas procesoFechas){
		procesoFechasFacade.actualizarProcesosUsauario(procesoFechas);
		idProcesoAnterior=procesoFechas.getIdProcesoAnterior();
		idProcesoActual=procesoFechas.getIdProcesoActual();
		listProcesoFechas= new ArrayList<ProcesoFechas>();
		listProcesoFechas=procesoFechasFacade.listConsultarFechasXProceso(idProcesoAnterior,idProcesoActual);
	}
	
	public void guardar() {
		
		for (ProcesoFechas procesoFechas : listProcesoFechas) {
			procesoFechasFacade.actualizarProcesosBamtaskSummary(procesoFechas);
			procesoFechasFacade.actualizarProcesosTask(procesoFechas);
			procesoFechasFacade.actualizarProcesosTaskevent(procesoFechas);
			if(!procesoFechas.getStatusAnterior().equals("Completed")){
				procesoFechasFacade.actualizarProcesosTaskAnterior(procesoFechas);
			}
		}
		Integer procesoActual=listProcesoFechas.get(0).getIdProcesoActual();
		Integer procesoAnterior=listProcesoFechas.get(0).getIdProcesoAnterior();
		
		procesoFechasFacade.actualizarProcesosActual(listProcesoFechas.get(0));
		procesoFechasFacade.actualizarProcesosAnterior(listProcesoFechas.get(0));
		actualizarDocumentos(procesoAnterior, procesoActual);
		listProcesoFechas= new ArrayList<ProcesoFechas>();
		JsfUtil.addMessageInfo("Datos actualizados correctamente.");
	}

	public String getPersona(String pin) throws ServiceException{
		Persona persona= new Persona();
		if(pin.length()>10){
			Organizacion organizacion= new Organizacion();
			organizacion=organizacionFacade.buscarPorRuc(pin);
			if(organizacion!=null)
			return organizacion.getNombre();
			
			persona=personaFacade.buscarPersonaPorPin(pin);
			return persona.getNombre();
		}
		persona=personaFacade.buscarPersonaPorPin(pin);
		return persona.getNombre();
	}
	
	private void actualizarDocumentos(Integer idProcesoAnterior,Integer idProcesoActual){
		List<Documento> listDocumentosProcesoAnterior= new ArrayList<Documento>();
		List<Documento> listDocumentosProcesoActual= new ArrayList<Documento>();
		
		listDocumentosProcesoActual=documentosFacade.recuperarDocumentosPorIdProceso((long)idProcesoActual);
		if(listDocumentosProcesoActual.size()>0){
			for (Documento documento : listDocumentosProcesoActual) {
				if(documento.getDocumentosTareasProcesos().size()>0){
					for (DocumentosTareasProceso documentosTareasProceso : documento.getDocumentosTareasProcesos()) {
						documentosTareasProceso.setEstado(false);
						crudServiceBean.saveOrUpdate(documentosTareasProceso);
					}
				}
			}
		}
		
		listDocumentosProcesoAnterior=documentosFacade.recuperarDocumentosPorIdProceso((long)idProcesoAnterior);
		if(listDocumentosProcesoAnterior.size()>0){
			for (Documento documento : listDocumentosProcesoAnterior) {
				if(documento.getDocumentosTareasProcesos().size()>0){
					for (DocumentosTareasProceso documentosTareasProceso : documento.getDocumentosTareasProcesos()) {
						documentosTareasProceso.setProcessInstanceId(idProcesoActual);
						crudServiceBean.saveOrUpdate(documentosTareasProceso);
					}
				}
			}
		}
	} 
	
}
