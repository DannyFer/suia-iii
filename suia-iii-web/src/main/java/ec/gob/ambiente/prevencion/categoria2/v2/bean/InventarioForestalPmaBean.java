/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */

package ec.gob.ambiente.prevencion.categoria2.v2.bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.inventarioForestalPma.facade.InventarioForestalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class InventarioForestalPmaBean implements Serializable {

	/**
     *
     */
	private static final long serialVersionUID = 8306276082839328940L;

	private static final Logger LOG = Logger
			.getLogger(InventarioForestalPmaBean.class);

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private InventarioForestalPmaFacade inventarioForestalPmaFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

	@Getter
	@Setter
	private InventarioForestalPma inventarioForestalPma, inventarioForestalPmaOriginal;

	@Setter
	@Getter
	private String nombreFileInventarioForestal;

    @Getter
    @Setter
    private Boolean descargado = false;

	@Setter
	@Getter
	private UploadedFile file;
	
	@Getter
	@Setter
	private List<InventarioForestalPma> listaHistorialMaderaEnPie, listaHistorialPlantilla, listaHistorialInventario;
	
	@Setter
	@Getter
	private Documento documentoOriginal;
	
	@Setter
	@Getter
	private Integer opcionHistorial;
	
	@Setter
	@Getter
	private String nombreHistorial;

	@PostConstruct
	public void init() throws CloneNotSupportedException {
		try {
			cargarDatosIniciales();
			
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos inventario forestal.");
		}
	}

	private void cargarDatosIniciales() throws ServiceException {
		ProyectoLicenciamientoAmbiental proyectoActivo = proyectoLicenciamientoAmbientalFacade
				.buscarProyectosLicenciamientoAmbientalPorId(proyectosBean
						.getProyecto().getId());
		fichaAmbientalPma = fichaAmbientalPmaFacade
				.getFichaAmbientalPorCodigoProyecto(proyectoActivo.getCodigo());
		
		//MarielaG para manejo de historiales
		opcionHistorial = 0;
		listaHistorialInventario = new ArrayList<>();
		List<InventarioForestalPma> listaInventariosEnBdd = inventarioForestalPmaFacade
															.obtenerAllInventarioForestalPmaPorFicha(fichaAmbientalPma.getId());
		inventarioForestalPma = new InventarioForestalPma();
		if (listaInventariosEnBdd != null) {
			for(InventarioForestalPma inventario : listaInventariosEnBdd){
				if(inventario.getIdRegistroOriginal() == null){
					inventarioForestalPma = inventario;
					
					if(fichaAmbientalPma.getValidarInventarioForestal() != null && fichaAmbientalPma.getValidarInventarioForestal() == true){
						descargado = true;
                    }
					inventarioForestalPmaOriginal = (InventarioForestalPma) SerializationUtils.clone(inventarioForestalPma);
					documentoOriginal = inventarioForestalPma.getInventarioForestal();
				}
			}
			recuperarHistorialInventario(listaInventariosEnBdd);
			
            if (inventarioForestalPma.getRemocionVegetal()==true && inventarioForestalPma.getInventarioForestal() != null) {
                nombreFileInventarioForestal = inventarioForestalPma.getInventarioForestal().getNombre();
            }
		}
	}

	public void fileUploadListenerInventarioForestal(FileUploadEvent event) {
		file = event.getFile();
		inventarioForestalPma.setInventarioForestal(UtilDocumento
				.generateDocumentXLSFromUpload(file.getContents(),
						file.getFileName()));
	}

	public StreamedContent descargar() {
		try {
            byte[] inventarioForestalFile = inventarioForestalPmaFacade
					.descargarFile(inventarioForestalPma.getInventarioForestal());
            if (inventarioForestalFile != null) {
                InputStream is = new ByteArrayInputStream(inventarioForestalFile);
                return new DefaultStreamedContent(is, "application/xls", inventarioForestalPma.getInventarioForestal().getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}

	public StreamedContent descargarPlantillaInventarioForestal() {
        try {
            byte[] plantillaInventarioForestal = inventarioForestalPmaFacade
                    .getPlantillaInventarioForestal(Constantes.PLANTILLA_INVENTARIO_FORESTAL);
            if (plantillaInventarioForestal != null) {
                InputStream is = new ByteArrayInputStream(plantillaInventarioForestal);
                descargado = true;
                return new DefaultStreamedContent(is, "application/xls", "PlantillaInventarioForestal.xls");
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
        } catch (Exception e) {
            LOG.error("Error en optener la plantilla de inventario forestal.", e);
            JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
        }
	}

	public void guardar() {
		try {
            if(inventarioForestalPma.getRemocionVegetal()==true){
                if(getDescargado()) {
                    if (inventarioForestalPma.getInventarioForestal() != null) {
                        inventarioForestalPma.setFichaAmbientalPma(fichaAmbientalPma);
                        Boolean guardarArchivo = (file != null &&
                                !nombreFileInventarioForestal.equals(inventarioForestalPma.getInventarioForestal().getNombre()));
                        if(fichaAmbientalPma.getValidarInventarioForestal() != null && fichaAmbientalPma.getValidarInventarioForestal() == true){
                        	this.guardarHistorial();
                        }
						
                        //TODO Verificar el tipo de documento
	                    inventarioForestalPmaFacade.guardar(inventarioForestalPma, TipoDocumentoSistema.INVENTARIO_FORESTAL_GEN ,guardarArchivo, documentoOriginal);
                        nombreFileInventarioForestal = inventarioForestalPma.getInventarioForestal().getNombre();
                    } else {
                        JsfUtil.addMessageError("Debe subir el archivo de inventario forestal");
                        return;
                    }
                }
                else {
                    JsfUtil.addMessageError("Debe descargar la plantilla del inventario forestal.");
                    return;
                }
            }
            else{
            	if(fichaAmbientalPma.getValidarInventarioForestal() != null && fichaAmbientalPma.getValidarInventarioForestal() == true){
                	this.guardarHistorial();
                }
            	
                inventarioForestalPma.setFichaAmbientalPma(fichaAmbientalPma);
                inventarioForestalPmaFacade.guardarSinInventario(inventarioForestalPma);
            }

            fichaAmbientalPma.setValidarInventarioForestal(true);
            fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbientalPma);
            
            cargarDatosIniciales();
            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

		} catch (ServiceException | CmisAlfrescoException e) {
			LOG.error("error al guardar ", e);
			JsfUtil.addMessageError("Ocurrió un error al guardar Inventario Forestal");
		}
	}
	
	public void guardarHistorial() {
		if(inventarioForestalPmaOriginal != null &&
				!inventarioForestalPma.equalsObject(inventarioForestalPmaOriginal)){
    		//guardar historial
    		inventarioForestalPmaOriginal.setId(null);
    		inventarioForestalPmaOriginal.setIdRegistroOriginal(inventarioForestalPma.getId());
    		inventarioForestalPmaOriginal.setFechaHistorico(new Date());
    		inventarioForestalPmaFacade.guardarOriginal(inventarioForestalPmaOriginal);
    	}
	}
	
	public StreamedContent descargarArchivoHistorico(Documento documento) {
		try {
            byte[] inventarioForestalFile = inventarioForestalPmaFacade.descargarFile(documento);
            if (inventarioForestalFile != null) {
                InputStream is = new ByteArrayInputStream(inventarioForestalFile);
                return new DefaultStreamedContent(is, "application/xls", documento.getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}
	
	public void recuperarHistorialInventario(List<InventarioForestalPma> listaInventariosEnBdd){		
		listaHistorialMaderaEnPie = new ArrayList<>();
		listaHistorialPlantilla = new ArrayList<>();
		
		if (inventarioForestalPma.getRemocionVegetal()) {
			for(InventarioForestalPma inventario : listaInventariosEnBdd){
				if(inventario.getRemocionVegetal() == true && inventario.getIdRegistroOriginal() != null){
					if(inventario.getMaderaEnPie() != inventarioForestalPma.getMaderaEnPie()){
						Boolean agregarInventario = false;
						for(InventarioForestalPma inventarioMadera : listaHistorialMaderaEnPie){
							if(inventarioMadera.getMaderaEnPie() == inventario.getMaderaEnPie()){
								agregarInventario = true;
								break;
							}
						}
						if(!agregarInventario)
							listaHistorialMaderaEnPie.add(0, inventario);
					}
					
					if(inventario.getInventarioForestal() != null && 
							inventario.getInventarioForestal().getNombre() != inventarioForestalPma.getInventarioForestal().getNombre()){
						Boolean agregarPlantilla = false;
						for(InventarioForestalPma inventarioPlantilla : listaHistorialPlantilla){
							if(inventarioPlantilla.getInventarioForestal().getId().equals(inventario.getInventarioForestal().getId())){
								agregarPlantilla = true;
								break;
							}
						}
						if(!agregarPlantilla)
							listaHistorialPlantilla.add(0, inventario);						
					}
				}
			}
		}else{
			nombreHistorial = "Historial inventario";
			for(InventarioForestalPma inventario : listaInventariosEnBdd){
				if(inventario.getIdRegistroOriginal() != null && inventario.getRemocionVegetal()){
					listaHistorialInventario.add(0, inventario);
				}
			}
		}
	}
	
	public void fillHistorialInventario(Integer opcion) {
		listaHistorialInventario = new ArrayList<>();
		nombreHistorial = null;
		opcionHistorial = opcion;
		switch (opcion) {
		case 1:
			nombreHistorial = "Historial plantilla";
			listaHistorialInventario = listaHistorialPlantilla;
			break;
		case 2:
			nombreHistorial = "Historial cantidad madera";
			listaHistorialInventario = listaHistorialMaderaEnPie;
			break;
		default:
			break;
		}
	}
}