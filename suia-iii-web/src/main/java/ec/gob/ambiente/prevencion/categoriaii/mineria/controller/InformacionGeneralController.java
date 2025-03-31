package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.categoriaii.mineria.bean.InformacionGeneralBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 *
 * @author lili
 *
 */
@ManagedBean
@ViewScoped
public class InformacionGeneralController implements Serializable {

    private static final long serialVersionUID = -4665016657881501146L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InformacionGeneralController.class);

    @Getter
    @Setter
    private InformacionGeneralBean informacionGeneralBean;

    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    @EJB
    private CrudServiceBean crudServiceBean;

    @EJB
    private ContactoFacade contactoFacade;

    @EJB
    private UbicacionGeograficaFacade ubiGeograficaFacade;

    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;
    @EJB
    private OrganizacionFacade organizacionFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{fichaMineriaController}")
    private FichaMineriaController fichaMineriaController;

    private static final int TAMANIO_ARCHIVO = 1024;
    
    //Cris F: nuevas variables
    @Getter
    @Setter
    private List<FichaAmbientalMineria> listaFichaAmbientalMineraFechaPermiso, listaFichaAmbientalMineraFechaInscripcionContrato, 
    listaFichaAmbientalMineraDuracionPermiso, listaFichaAmbientalMineraDuracionContrato, listaFichaAmbientalMineriaObservaciones;
    
    @Getter
    private boolean verFechaPermiso, verFechaContrato, verDuracionPermiso, verDuracionContrato, verObservaciones;
    
    //Fin nuevas variables

    @PostConstruct
    public void init() throws CmisAlfrescoException {
        try {
            setInformacionGeneralBean(new InformacionGeneralBean());
            getInformacionGeneralBean().iniciar();
            getInformacionGeneralBean().setFichaAmbientalMineria(fichaAmbientalMineriaFacade.obtenerPorId(((FichaAmbientalMineria) JsfUtil.devolverObjetoSession(Constantes.SESSION_FICHA_AMBIENTAL_MINERA_OBJECT)).getId()));
            getInformacionGeneralBean().setProyecto(getInformacionGeneralBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental());
            getInformacionGeneralBean().setListaUbicacionProyecto(ubicacionGeograficaFacade.listarPorProyecto(getInformacionGeneralBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental()));
            Persona persona = getInformacionGeneralBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getUsuario().getPersona();
            getInformacionGeneralBean().setUbicacionContacto(obtenerUbicacionContacto(persona.getIdUbicacionGeografica()));
            obtenerContacto();
            obtenerUbicacionGeografica();
            cargarAdjunto();
            
            //Cris F: para cargar datos de historial
            recuperarInformacionHistorial();
        } catch (RuntimeException e) {
            LOG.error(e, e);
        }
    }

    public void validarTareaBpm() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/fichaMineria/default.jsf");
    }

    public void validarTareaBpmV2() {
        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), "/prevencion/categoria2/v2/fichaMineria/default.jsf");
    }

    private void cargarAdjunto() throws CmisAlfrescoException {
        try {
            getInformacionGeneralBean().setEntityAdjunto(fichaAmbientalMineriaFacade.obternerPorFicha(getInformacionGeneralBean().getFichaAmbientalMineria()));
            if (getInformacionGeneralBean().getEntityAdjunto() == null) {
                getInformacionGeneralBean().setEntityAdjunto(new EntityAdjunto());
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    public void obtenerUbicacionGeografica() {
        try {
            List<UbicacionesGeografica> ubicaciones = ubiGeograficaFacade.listarPorProyecto(getInformacionGeneralBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental());
        } catch (Exception e) {
            LOG.info("Error al consultar ubicacion geografica", e);
        }
    }

    public UbicacionesGeografica obtenerUbicacionContacto(Integer idUbicacion) {
        try {
            return ubicacionGeograficaFacade.buscarPorId(idUbicacion);
        } catch (ServiceException e) {
            LOG.info("Error al consultar ubicacion geografica del contacto", e);
            return null;
        }
    }

    public void obtenerContacto() {
        try {
            List<Contacto> contactos = null;
            Organizacion org = organizacionFacade.buscarPorPersona(getInformacionGeneralBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getUsuario().getPersona());
            if (org == null) {
                contactos = contactoFacade.buscarPorPersona(getInformacionGeneralBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getUsuario().getPersona());
            } else {
                contactos = contactoFacade.buscarPorOrganizacion(org);
            }

            for (Contacto contacto : contactos) {
                if (contacto.getFormasContacto().getId().equals(FormasContacto.DIRECCION)) {
                    informacionGeneralBean.setDireccion(contacto.getValor());
                } else if (contacto.getFormasContacto().getId().equals(FormasContacto.TELEFONO)) {
                    informacionGeneralBean.setTelefono(contacto.getValor());
                } else if (contacto.getFormasContacto().getId().equals(FormasContacto.FAX)) {
                    informacionGeneralBean.setFax(contacto.getValor());
                } else if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)) {
                    informacionGeneralBean.setEmail(contacto.getValor());
                }
            }
        } catch (ServiceException e) {
            LOG.error("Error al obtener contacto: ", e);
        }
    }

    public void guardarFichaInformacionGeneral() throws CmisAlfrescoException {
        try {
            if (validarFormulario1() & validarFormulario2()) {
                informacionGeneralBean.getFichaAmbientalMineria().getDuracionPermiso();
                getInformacionGeneralBean().setFichaAmbientalMineria(getInformacionGeneralBean().getFichaAmbientalMineria());
                getInformacionGeneralBean().getFichaAmbientalMineria().setValidarInformacionGeneral(true);
               // fichaAmbientalMineriaFacade.guardarFichaAdjunto(getInformacionGeneralBean().getFichaAmbientalMineria(), getInformacionGeneralBean().getEntityAdjunto());
                 
                //Cris F: historial para informacion general
                FichaAmbientalMineria fichaAmbientalMineriaBdd = fichaAmbientalMineriaFacade.obtenerPorId(((FichaAmbientalMineria) JsfUtil.devolverObjetoSession(Constantes.SESSION_FICHA_AMBIENTAL_MINERA_OBJECT)).getId());
                
                if(fichaAmbientalMineriaBdd.getFechaEmisionPermiso() != null){
                	
                	if(!compararFichaAmbientalMinera(getInformacionGeneralBean().getFichaAmbientalMineria(), fichaAmbientalMineriaBdd)){
                		FichaAmbientalMineria fichaAmbientalMineriaHistorico = (FichaAmbientalMineria)SerializationUtils.clone(fichaAmbientalMineriaBdd);
                    	fichaAmbientalMineriaHistorico.setId(null);
                    	fichaAmbientalMineriaHistorico.setFechaHistorico(new Date());
                    	fichaAmbientalMineriaHistorico.setIdRegistroOriginal(fichaAmbientalMineriaBdd.getId());  
                    	
                    	crudServiceBean.saveOrUpdate(fichaAmbientalMineriaHistorico);
                	}                	
                	crudServiceBean.saveOrUpdate(getInformacionGeneralBean().getFichaAmbientalMineria());                	
                }else{
                	 crudServiceBean.saveOrUpdate(getInformacionGeneralBean().getFichaAmbientalMineria());
                }  
                //fin codigo historial
                
                //codigo anterior
               //crudServiceBean.saveOrUpdate(getInformacionGeneralBean().getFichaAmbientalMineria());
                setInformacionGeneralBean(null);
                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
                init();
            }
        }/* catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
            LOG.info("Error al guardar muestreo inicial linea base: ", e);
        }*/ catch (RuntimeException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_GUARDAR_REGISTRO);
            LOG.info("Error al guardar muestreo inicial linea base: ", e);
        }
    }

    public void cancelar() {

    }

    public void handleFileUpload(FileUploadEvent event) {
        if (event.getFile().getContents().length > TAMANIO_ARCHIVO) {
            getInformacionGeneralBean().getEntityAdjunto().setArchivo(event.getFile().getContents());
            getInformacionGeneralBean().getEntityAdjunto().setExtension(JsfUtil.devuelveExtension(event.getFile().getFileName()));
            getInformacionGeneralBean().getEntityAdjunto().setNombre(event.getFile().getFileName());
            getInformacionGeneralBean().getEntityAdjunto().setMimeType(event.getFile().getContentType());
            LOG.info(event.getFile().getFileName() + " is uploaded.");
        } else {
            JsfUtil.addMessageError("Debe adjuntar un archivo superior a 0 bytes.");
        }
    }

    public boolean validarFormulario1() {
        List<String> listaMensajes = new ArrayList<String>();
        if (getInformacionGeneralBean().getFichaAmbientalMineria().getFechaEmisionPermiso() == null) {
            listaMensajes.add("El campo 'Fecha de otorgamiento del permiso' es requerido.");
        }
        if (getInformacionGeneralBean().getFichaAmbientalMineria().getFechaEmisionPermiso() != null
                && getInformacionGeneralBean().getFichaAmbientalMineria().getFechaEmisionPermiso().after(new Date())) {
            listaMensajes.add("El campo 'Fecha de otorgamiento del permiso' no puede ser mayor a la fecha actual.");
        }
//        if (getInformacionGeneralBean().getFichaAmbientalMineria().getFecharegistroContrato() == null) {
//            listaMensajes.add("El campo 'Fecha de inscripción del contrato de operación' es requerido.");
//        }
        if (getInformacionGeneralBean().getFichaAmbientalMineria().getFecharegistroContrato() != null
                && getInformacionGeneralBean().getFichaAmbientalMineria().getFecharegistroContrato().after(new Date())) {
            listaMensajes.add("El campo 'Fecha de inscripción del contrato de operación' no puede ser mayor a la fecha actual.");
        }
        if (listaMensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(listaMensajes);
            return false;
        }
    }

    public boolean validarFormulario2() {
        List<String> listaMensajes = new ArrayList<String>();
        if (getInformacionGeneralBean().getFichaAmbientalMineria().getDuracionPermiso().equals(0) && getInformacionGeneralBean().getFichaAmbientalMineria().getDuracionPermiso() == 0) {
            listaMensajes.add("El campo 'Plazo de duración del permiso (días)' es requerido.");
        }
//        if (getInformacionGeneralBean().getFichaAmbientalMineria().getDuracionContrato().equals(0) && getInformacionGeneralBean().getFichaAmbientalMineria().getDuracionContrato() == 0) {
//            listaMensajes.add("El campo 'Plazo de duración del contrato de operación (días)' es requerido.");
//        }
//        if (getInformacionGeneralBean().getEntityAdjunto().getArchivo() == null) {
//            listaMensajes.add("El campo 'Adjuntar Documento (Formato PDF)' es requerido.");
//        }
        if (listaMensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(listaMensajes);
            return false;
        }
    }

    public void descargar() throws IOException {
        JsfUtil.descargarPdf(getInformacionGeneralBean().getEntityAdjunto().getArchivo(), "reporte");
    }
    
    //Cris F: metodo para historial
    private boolean compararFichaAmbientalMinera(FichaAmbientalMineria fichaAmbientalMineria, FichaAmbientalMineria fichaAmbientalMineriaBdd){
    	try{
			if (((fichaAmbientalMineria.getFechaEmisionPermiso() == null && fichaAmbientalMineriaBdd.getFechaEmisionPermiso() == null) || 
					(fichaAmbientalMineria.getFechaEmisionPermiso() != null && fichaAmbientalMineriaBdd.getFechaEmisionPermiso() != null && 
					fichaAmbientalMineria.getFechaEmisionPermiso().equals(fichaAmbientalMineriaBdd.getFechaEmisionPermiso()))) && 
					((fichaAmbientalMineria.getFecharegistroContrato() == null && fichaAmbientalMineriaBdd.getFecharegistroContrato() == null) || 
					(fichaAmbientalMineria.getFecharegistroContrato() != null && fichaAmbientalMineriaBdd.getFecharegistroContrato() != null && 
					fichaAmbientalMineria.getFecharegistroContrato().equals(fichaAmbientalMineriaBdd.getFecharegistroContrato()))) && 
					((fichaAmbientalMineria.getDuracionPermiso() == null && fichaAmbientalMineriaBdd.getDuracionPermiso() == null) || 
					(fichaAmbientalMineria.getDuracionPermiso() != null && fichaAmbientalMineriaBdd.getDuracionPermiso() != null && 
					fichaAmbientalMineria.getDuracionPermiso().equals(fichaAmbientalMineriaBdd.getDuracionPermiso()))) && 
					((fichaAmbientalMineria.getDuracionContrato() == null && fichaAmbientalMineriaBdd.getDuracionContrato() == null) || 
					fichaAmbientalMineria.getDuracionContrato() != null && fichaAmbientalMineriaBdd.getDuracionContrato() != null && 
					fichaAmbientalMineria.getDuracionContrato().equals(fichaAmbientalMineriaBdd.getDuracionContrato()))
			) {
				return true;
			} else {
				return false;
			}
    	}catch(Exception e){
    		e.printStackTrace();
    		return true; //son iguales y no se hace nada
    	}    	
    }
    
    private void recuperarInformacionHistorial(){
    	
    	try {
			List<FichaAmbientalMineria> listaFichaAmbientalMineraHistorico = 
					fichaAmbientalMineriaFacade.buscarFichaAmbientalMineraHistorial(informacionGeneralBean.getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getId());
			
			if(listaFichaAmbientalMineraHistorico != null && !listaFichaAmbientalMineraHistorico.isEmpty()){
				
				listaFichaAmbientalMineraDuracionContrato = new ArrayList<FichaAmbientalMineria>();
				listaFichaAmbientalMineraDuracionPermiso = new ArrayList<FichaAmbientalMineria>();
				listaFichaAmbientalMineraFechaInscripcionContrato = new ArrayList<FichaAmbientalMineria>();
				listaFichaAmbientalMineraFechaPermiso = new ArrayList<FichaAmbientalMineria>();	
				listaFichaAmbientalMineriaObservaciones = new ArrayList<FichaAmbientalMineria>();				
				
				FichaAmbientalMineria fichaInicial = null;
				for(FichaAmbientalMineria fichaBdd : listaFichaAmbientalMineraHistorico){
					
					if(fichaInicial == null)
						fichaInicial =informacionGeneralBean.getFichaAmbientalMineria();
					
					//fecha Permiso
					if(!fichaBdd.getFechaEmisionPermiso().equals(fichaInicial.getFechaEmisionPermiso())){
						listaFichaAmbientalMineraFechaPermiso.add(0, fichaBdd);
					}
					
					//fecha registro del contrato
					if(!fichaBdd.getFecharegistroContrato().equals(fichaInicial.getFecharegistroContrato())){
						listaFichaAmbientalMineraFechaInscripcionContrato.add(0, fichaBdd);
					}
					
					//duracion de permiso
					if(!fichaBdd.getDuracionPermiso().equals(fichaInicial.getDuracionPermiso())){
						listaFichaAmbientalMineraDuracionPermiso.add(0, fichaBdd);
					}
					
					//Duracion Contrato
					if(!fichaBdd.getDuracionContrato().equals(fichaInicial.getDuracionContrato())){
						listaFichaAmbientalMineraDuracionContrato.add(0, fichaBdd);
					}
					
					//Observaciones
					if(!fichaBdd.getObservaciones().equals(fichaInicial.getObservaciones())){
						listaFichaAmbientalMineriaObservaciones.add(0, fichaBdd);
					}
							
					fichaInicial = fichaBdd;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
