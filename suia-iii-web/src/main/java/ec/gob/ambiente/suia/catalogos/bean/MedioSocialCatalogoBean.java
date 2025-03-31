package ec.gob.ambiente.suia.catalogos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.ProyectoGeneralCatalogo;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.proyectos.service.ProyectoGeneralCatalogoServiceBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class MedioSocialCatalogoBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(MedioSocialCatalogoBean.class);
	
	@Getter
    @Setter
	private List<CatalogoGeneral> mediosSocial;
	
	@Setter
	@Getter
	private List<ProyectoGeneralCatalogo> mediosSocialesSeleccionados;
		
	@Setter
	@Getter
	private TdrEiaLicencia tdrEia;
	
	@Setter
	@Getter
	private ProyectoLicenciamientoAmbiental proyectoActivo;
	
	@Getter
    @Setter
	private ProyectoGeneralCatalogo proyectoGeneralCatalogo;

	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;
	
	@EJB
	private TdrFacade tdrEiaFacade;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoFacade;
	
	@EJB
	private ProyectoGeneralCatalogoServiceBean proyectoGeneralCatalogoServiceBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@Getter
    @Setter
	private boolean habilita;
	
	@PostConstruct
	public void init(){
		proyectoActivo = proyectoLicenciamientoFacade.buscarProyectosLicenciamientoAmbientalPorId(proyectosBean.getProyecto().getId());
		tdrEia = tdrEiaFacade.getTdrEiaLicenciaPorIdProyecto(proyectoActivo.getId());
		proyectoGeneralCatalogo = new ProyectoGeneralCatalogo();
		//habilita=false;
		
		try {        	
        	mediosSocial = catalogoGeneralFacade
        			.obtenerCatalogoXTipoSeleccionado(TipoCatalogo.MEDIO_SOCIAL, getProyectoActivo().getId());
        	
        	mediosSocialesSeleccionados = proyectoGeneralCatalogoServiceBean
					.listarProyectoGeneralCatalogoPorProyectoCategoria(proyectoActivo.getId(), TipoCatalogo.MEDIO_SOCIAL);
        } catch (Exception e) {
        	mediosSocial = new ArrayList<CatalogoGeneral>();
        	mediosSocialesSeleccionados = new ArrayList<ProyectoGeneralCatalogo>();
        }
	}
	
	/**
	 * Metodo para guardar medio
	 */
	public void adicionarMedioSocial(){
		try {
			proyectoGeneralCatalogo.setTdrEiaLicencia(getTdrEia());
			proyectoGeneralCatalogo.setProyectoLicenciamientoAmbiental(getProyectoActivo());
			
			if(habilita){
				proyectoGeneralCatalogo.setAplicable("Si");
			}else {
				proyectoGeneralCatalogo.setAplicable("No");
				proyectoGeneralCatalogo.setTipoInformacion("Ninguna");
			}
			
			proyectoGeneralCatalogoServiceBean.guardarProyectoGeneralCatalogo(proyectoGeneralCatalogo);
			RequestContext context = RequestContext.getCurrentInstance();
			JsfUtil.addMessageInfo("La información ha sido guardada correctamente.");
			context.addCallbackParam("sucess", true);
			this.proyectoGeneralCatalogo = new ProyectoGeneralCatalogo();
		} catch (Exception e) {
			log.info("Error al guardar medio", e);
			JsfUtil.addMessageInfo("La información NO ha sido guardada correctamente.");
		}
	}
	
	/**
	 * 
	 */
	public void limpiarMedioSocial() {
		log.info("LIMPIA");
		habilita=false;
		this.proyectoGeneralCatalogo = new ProyectoGeneralCatalogo();
		this.proyectoGeneralCatalogo.setCatalogoGeneral(new CatalogoGeneral());
		log.info("Proy: " + proyectoGeneralCatalogo.getAplicable());
	}
	
	/**
	 * 
	 * @param proyecto
	 */
	public void seleccionarMedioSocial(ProyectoGeneralCatalogo proyecto) {
		try {
			log.info("INGRESA A SELECCIONAR MEDIO");
			this.proyectoGeneralCatalogo = proyecto;
			this.proyectoGeneralCatalogo.setCatalogoGeneral(proyecto.getCatalogoGeneral());
			this.proyectoGeneralCatalogo.setTdrEiaLicencia(proyecto.getTdrEiaLicencia());
			this.proyectoGeneralCatalogo.setProyectoLicenciamientoAmbiental(proyecto.getProyectoLicenciamientoAmbiental());
			
			if(this.proyectoGeneralCatalogo.getAplicable().equals("Si")){
				habilita=true;
			}else {
				habilita=false;
			}
			log.info("Descripcion Selec" + getProyectoGeneralCatalogo().getCatalogoGeneral().getDescripcion());
			log.info("Es aplicable Selec" + getProyectoGeneralCatalogo().getAplicable());
		} catch (Exception e) {
			log.error("Error al seleccionar", e);
		}
	}
	
	/**
	 * 
	 */
	public void editarMedioSocial(){
		try {
			proyectoGeneralCatalogo.setTdrEiaLicencia(getProyectoGeneralCatalogo().getTdrEiaLicencia());
			proyectoGeneralCatalogo.setProyectoLicenciamientoAmbiental(getProyectoGeneralCatalogo().getProyectoLicenciamientoAmbiental());
			
			if(habilita){
				proyectoGeneralCatalogo.setAplicable("Si");
			}else {
				proyectoGeneralCatalogo.setAplicable("No");
				proyectoGeneralCatalogo.setTipoInformacion("Ninguna");
			}
			
			proyectoGeneralCatalogoServiceBean.guardarProyectoGeneralCatalogo(this.proyectoGeneralCatalogo);
			RequestContext context = RequestContext.getCurrentInstance();
			JsfUtil.addMessageInfo("La información ha sido guardada correctamente.");
			context.addCallbackParam("sucess", true);
			this.proyectoGeneralCatalogo = new ProyectoGeneralCatalogo();
		} catch (Exception e) {
			log.error("no ha sido actualizada correctamente", e);
			JsfUtil.addMessageInfo("La información no ha sido actualizada correctamente.");
		}
	}
	
	/**
	 * 
	 * @param proyectoGeneralCatalogo
	 */
	public void eliminarMedioSocial(ProyectoGeneralCatalogo proyectoGeneralCatalogo) {		
		try {
			if (proyectoGeneralCatalogo.getCatalogoGeneral().getTipoCatalogo().getId() == TipoCatalogo.MEDIO_FISICO) {
				this.mediosSocial.remove(proyectoGeneralCatalogo);
				proyectoGeneralCatalogoServiceBean.eliminarProyectoGeneralCatalogo(proyectoGeneralCatalogo);
			}else {
				log.info("No es medio fisico");
			}
		} catch (Exception e) {
			JsfUtil.addMessageInfo("La información no ha sido eliminada correctamente.");
			log.error("Error al eliminar", e);
		}
	}
	
	
}
