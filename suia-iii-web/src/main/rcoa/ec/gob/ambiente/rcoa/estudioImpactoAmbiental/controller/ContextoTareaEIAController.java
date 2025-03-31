package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.VerRegistroGeneradorDesechoBean;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeOficioArtFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.OficioAproReqTec;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class ContextoTareaEIAController {
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;	
	@EJB
	private InformeOficioArtFacade informeOficioFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
    private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;

    @Getter
    @Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
    private Map<String, Object> variables;
	
	@Getter
	private ProyectoLicenciaCoa proyecto;	
    
	@Getter
	private GeneradorDesechosPeligrosos rgd;
	
	@Getter
	private RegistroGeneradorDesechosProyectosRcoa rgdRcoa;
	
	@Getter
	private AprobacionRequisitosTecnicos art;
	
	@Getter
	private RegistroSustanciaQuimica rsq;
	
	private String tramite;
	
	@Getter
	@Setter
	private boolean requiereDescargaGuiasMineriaNoMetalico;
	
	@PostConstruct
	private void init(){
		try{
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			JsfUtil.cargarObjetoSession("rgdIdLectura", null);
			JsfUtil.cargarObjetoSession("rsqIdLectura", null);
			JsfUtil.cargarObjetoSession("artIdLectura", null);
			
			if(proyecto.getCodigoRgdAsociado()!=null) {
				rgd=registroGeneradorDesechosFacade.get(proyecto.getCodigoRgdAsociado());
			}
			if(rgd == null || rgd.getId() == null) {
				List<RegistroGeneradorDesechosProyectosRcoa> list=registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId());
				rgdRcoa=list!=null && !list.isEmpty()?list.get(0):null;
				if (rgdRcoa != null && rgdRcoa.getId() != null)
					proyecto.setCodigoRgdAsociado(rgdRcoa.getRegistroGeneradorDesechosRcoa().getCodigo());
			}
			
			rsq=registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyecto);
			if (rsq != null && rsq.getId() != null)
				proyecto.setCodigoRsqAsociado(rsq.getNumeroAplicacion());
			
			art=aprobacionRequisitosTecnicosFacade.getAprobacionRequisitosTecnicos(proyecto.getCodigoUnicoAmbiental());
			
			mostrarGuiaMineriaNoMetalicos();
			
		}catch(Exception e){
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public boolean getTieneRgd() {
		return (rgd == null || rgd.getId() == null) ? (rgdRcoa == null || rgdRcoa.getId() == null) ? false : true : true;
	} 
	
	public boolean getTieneRsq() {
		return (rsq == null || rsq.getId() == null) ? false : true;
	}
	
	public boolean getTieneArt() {
		return art!=null;
	}
	
	public String redireccionarRgd() {
		JsfUtil.cargarObjetoSession("rgdIdLectura",rgd!=null?rgd.getId():rgdRcoa.getId());
		JsfUtil.cargarObjetoSession("rgdRevisado"+proyecto.getCodigoUnicoAmbiental(), true);
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		
		if(rgd!=null) {			
			String urlRgd = JsfUtil.getBean(VerRegistroGeneradorDesechoBean.class).actionVerGeneradorDesechosPeligrosos(rgd.getId(),true, false, false);
			return JsfUtil.actionNavigateTo(externalContext.getRequestContextPath() + urlRgd);
		}else {
			return JsfUtil.actionNavigateTo(externalContext.getRequestContextPath() + "/pages/rcoa/generadorDesechos/informacionRegistroGeneradorVer.jsf");
		}
			
	}
	
	public String redireccionarRsq() {
		JsfUtil.cargarObjetoSession("rsqIdLectura", rsq.getId());
		JsfUtil.cargarObjetoSession("rsqRevisado"+proyecto.getCodigoUnicoAmbiental(), true);
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		return JsfUtil.actionNavigateTo(externalContext.getRequestContextPath() +  "/pages/rcoa/sustanciasQuimicas/ingresarInformacionRSQVer.xhtml");
	}
	
	public String redireccionarArt() {
		JsfUtil.cargarObjetoSession("artIdLectura", art.getId());
		JsfUtil.cargarObjetoSession("artRevisado"+proyecto.getCodigoUnicoAmbiental(), true);
		return "/control/aprobacionRequisitosTecnicos/defaultAnalisisRevision.xhtml";
	}
	
	public StreamedContent descargarOficoArt() throws IOException {
		DefaultStreamedContent content = null;
		try {
			OficioAproReqTec oficioArt = this.informeOficioFacade.obtenerOficioAprobacionPorArt(
					TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART, art.getId());
			
			Documento documentoEia = oficioArt.getDocumentoOficio();
			
			byte[] documentoContent = null;
				
			if (documentoEia != null && documentoEia.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoEia.getIdAlfresco());
			}
			
			if (documentoEia != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoEia.getNombre());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void mostrarGuiaMineriaNoMetalicos() {
		requiereDescargaGuiasMineriaNoMetalico = false;
		
		if (proyecto.getCategorizacion() == 3|| proyecto.getCategorizacion() == 4) {
			ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
			
			if (proyectoCiuuPrincipal != null) {
				SubActividades subActividadProyecto = proyectoCiuuPrincipal.getSubActividad();
				
				if (subActividadProyecto != null && subActividadProyecto.getId() != null
						&& subActividadProyecto.getRequiereRegimenMinero() != null
						&& subActividadProyecto.getRequiereRegimenMinero()) {
					if (proyectoCiuuPrincipal.getValorOpcion() != null
							&& proyectoCiuuPrincipal.getTipoRegimenMinero() != null
							&& !proyectoCiuuPrincipal.getValorOpcion()
							&& proyectoCiuuPrincipal.getTipoRegimenMinero().getValor().equals("1")) {
						requiereDescargaGuiasMineriaNoMetalico = true;
					}
				}
			}
			
		}
	}
	
}