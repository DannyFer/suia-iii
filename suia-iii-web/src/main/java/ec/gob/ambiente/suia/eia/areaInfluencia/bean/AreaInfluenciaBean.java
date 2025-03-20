package ec.gob.ambiente.suia.eia.areaInfluencia.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.AreaInfluencia;
import ec.gob.ambiente.suia.domain.DistanciaElementoSensible;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ElementoSensible;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.InfraestructuraAfectada;
import ec.gob.ambiente.suia.domain.InventarioForestal;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.enums.MultiplosArea;
import ec.gob.ambiente.suia.domain.enums.MultiplosMetro;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.areaInfluencia.AreaInfluenciaFacade;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.resumenEjecutivo.bean.ResumenEjecutivoBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * Created by juangabriel on 15/07/15.
 */
@ManagedBean
@ViewScoped
public class AreaInfluenciaBean implements Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = -7966063693358192137L;

//	private static final Logger LOG = Logger.getLogger(AreaInfluencia.class);

    private static final Logger LOGGER = Logger.getLogger(AreaInfluenciaBean.class);

    @Getter
    private List<MultiplosMetro> multiplosMetro;
    
    @Getter
    private List<MultiplosArea> multiplosArea;

    @Getter
    EstudioImpactoAmbiental estudio;

    @Setter
    private AreaInfluencia areaInfluencia;

    @Setter
    @Getter
    private List<AreaInfluencia> listaAreaInfluenciaDirecta;

    @Setter
    @Getter
    private List<AreaInfluencia> listaAreaInfluenciaIndirecta;

    @Setter
    @Getter
    private List<AreaInfluencia> listaAreaInfluenciaPos;

    @Getter
    @Setter
    private List<AreaInfluencia> listaAreaInfluenciaBorrar;

//    @Getter
    private List<InfraestructuraAfectada> infraestructurasAfectadasAlmacenadas;

    @Setter
    private InfraestructuraAfectada infraestructuraAfectada;

    @Setter
    private List<InfraestructuraAfectada> infraestructurasAfectadas;


    @Setter
    private DistanciaElementoSensible distanciaElementoSensible;

    @Getter
    private List<ElementoSensible> catalogoElementosSensibles;

    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

    @Getter
    @Setter
    private Documento documentoGeneral, documentoGeneralOriginal;


    @Setter
    private boolean habilitarOtroElemento;
    
    @Getter
    private boolean esMineriaNoMetalicos;

    @EJB
    private DocumentosFacade documentosFacade;
    @Setter
    private boolean nuevo;

    @Getter
    @Setter
    private List<DistanciaElementoSensible> distanciaElementoSensiblesEliminados;
    
  //Cris F: aumento de variables para error en estudio ambiental
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	private Map<String, Object> processVariables;
    
    @Getter
    private boolean existeObservaciones;
    @Getter
    private Integer numeroNotificaciones;
    
    @Getter
    private String promotor;
    
    @EJB
	private ProcesoFacade procesoFacade;
    @EJB
    private AreaInfluenciaFacade areaInfluenciaFacade;
    
    @Getter
    private Documento documentoHistorico;
    
    @Getter
    @Setter
    private List<InfraestructuraAfectada> infraestructurasAfectadasOriginales, infraestructurasAfectadasHistorial, infraestructurasAfectadasEliminadasBdd;

    @Getter
    @Setter
    private List<DistanciaElementoSensible> distanciasOriginales, distanciasHistorial, distanciasEliminadasBdd;
    
    @Getter
    @Setter
    private List<Documento> listaDocumentosHistorico;
    
    @Getter
    @Setter
    private List<AreaInfluencia> listaAreaDirectaFisicoHistorico, listaAreaDirectaBioticoHistorico, listaAreaIndirectaFisicoHistorico, listaAreaIndirectaBioticoHistorico;
    
    @Getter
    @Setter
    private Integer totalInfraestructuraModificado;
    
    @Getter
    @Setter
    private boolean proyectoHidrocarburos = false;
    

    @PostConstruct
    public void init() throws JbpmException, CloneNotSupportedException {    	
    	/**
		 * Cristina Flores obtener variables
		 */
		processVariables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
		String numNotificaciones = (String) processVariables.get("cantidadNotificaciones");
		if(numNotificaciones != null){
			numeroNotificaciones = Integer.valueOf(numNotificaciones);
		}else{
			numeroNotificaciones = 0;
		}	
		promotor = (String) processVariables.get("u_Promotor");
		if(numeroNotificaciones > 0)
			existeObservaciones = true;
		//Fin CF
    	
    	
        this.distanciaElementoSensiblesEliminados = new ArrayList<>();
        listaAreaInfluenciaBorrar = new ArrayList<>();        
        multiplosMetro = new ArrayList<MultiplosMetro>();
        multiplosArea = new ArrayList<MultiplosArea>();
        catalogoElementosSensibles = areaInfluenciaFacade.getElementosSensibles();
        
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        estudio=(EstudioImpactoAmbiental) request.getSession().getAttribute("estudio");
        //estudio = estudioImpactoAmbientalFacade.obtenerEIAPorId(((EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT)).getId());
        
        //MarielaG
        //cambio para ejecutar un solo metodo de busqueda de infraestructuras
        infraestructurasAfectadas = new ArrayList<>();
		List<InfraestructuraAfectada> infraestructuraAfectadaEnBdd = areaInfluenciaFacade.getInfraestructuraEnBdd(estudio);
		List<InfraestructuraAfectada> infraestructuraDistanciaEnBdd = new ArrayList<>();
		if (infraestructuraAfectadaEnBdd != null && !infraestructuraAfectadaEnBdd.isEmpty()) {
			for (InfraestructuraAfectada infraestructura : infraestructuraAfectadaEnBdd) {
				InfraestructuraAfectada infraestructuraCopia = infraestructura.clone();
				infraestructuraCopia.setId(infraestructura.getId());
				infraestructuraDistanciaEnBdd.add(infraestructuraCopia);
				
				if (infraestructura.getIdHistorico() == null) {
					List<DistanciaElementoSensible> elementos = new ArrayList<DistanciaElementoSensible>();
		        	for(DistanciaElementoSensible distancia : infraestructura.getDistanciaElementoSensibles()){
		        		if(distancia.getIdHistorico() == null){
		        			elementos.add(distancia);
		        		}        		
		        	}		        	
		        	infraestructura.setDistanciaElementoSensibles(elementos);
		        	
					infraestructurasAfectadas.add(infraestructura);
				}
			}
		}

        //if (JsfUtil.getBean(ResumenEjecutivoBean.class).isProyectoHidrocarburos()) {
		if(estudio.getProyectoLicenciamientoAmbiental().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)){
			proyectoHidrocarburos = true;
            listaAreaInfluenciaDirecta = new ArrayList<AreaInfluencia>();
            listaAreaInfluenciaIndirecta = new ArrayList<AreaInfluencia>();
            listaAreaInfluenciaPos = new ArrayList<AreaInfluencia>();
            listaAreaInfluenciaPos = areaInfluenciaFacade.getListaAreaInfluencia(estudio);
            for (AreaInfluencia area : this.listaAreaInfluenciaPos) {
                if (area.getDirectaBioticoDistancia() == null || area.getDirectaBioticoDistancia().isNaN()) {
                    this.listaAreaInfluenciaIndirecta.add(area);
                } else {
                    this.listaAreaInfluenciaDirecta.add(area);
                }
            }
        } else {
            areaInfluencia = areaInfluenciaFacade.getAreaInfluencia(estudio);
        }

        documentoGeneralOriginal = new Documento();
        inicializarDocumento();
        ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(estudio.getIdProyectoLicenciamientoAmbiental());
        
        esMineriaNoMetalicos=proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudio.getResumenEjecutivo()==null?true:false;
        if(esMineriaNoMetalicos)
        {        	
            for (MultiplosArea multiplo : MultiplosArea.values()) {
            	multiplosArea.add(multiplo);
    		} 
        }else
        {
        	for (MultiplosMetro multiplo : MultiplosMetro.values()) {
            	multiplosMetro.add(multiplo);
    		}        	
        }
        
        //MarielaG para buscar informacion original
        if(existeObservaciones){			
//			if(!promotor.equals(loginBean.getNombreUsuario())){
				consultarAreaOriginal();
				cargarInfraestructurasOriginales(infraestructuraAfectadaEnBdd);
				cargarDistanciasOriginales(infraestructuraDistanciaEnBdd);
//			}
		}

    }


    public void inicializarDocumento() {
        documentoGeneral = new Documento();
        cargarAdjuntosEIA(TipoDocumentoSistema.DETERMINACION_AREA_INFLUENCIA_GEN);
    }


    /**
     * @param tipoDocumento
     * @throws CmisAlfrescoException
     */
    private void cargarAdjuntosEIA(TipoDocumentoSistema tipoDocumento) {
        List<Documento> documentosXEIA = documentosFacade
        		.documentosTodosXTablaIdXIdDoc(estudio.getId(), "EstudioImpactoAmbiental", tipoDocumento);
        if (documentosXEIA.size() > 0) {
            this.documentoGeneral = documentosXEIA.get(0);
            
            documentoHistorico = validarDocumentoHistorico(documentoGeneral, documentosXEIA);
            
          //MARIELAG para recuperar el archivo original
			if(existeObservaciones){				
//				if(!promotor.equals(loginBean.getNombreUsuario())){
					consultarDocumentosOriginales(documentosXEIA.get(0).getId(), documentosXEIA);
//				}
			}
        }
    }

    public AreaInfluencia getAreaInfluencia() {
        return areaInfluencia == null ? areaInfluencia = new AreaInfluencia() : areaInfluencia;
    }
    /*public List<AreaInfluencia> getAreaInfluenciaList() {
        return listaAreaInfluencia == null ? listaAreaInfluencia = new ArrayList<AreaInfluencia>() : listaAreaInfluencia;
    }*/

    public List<InfraestructuraAfectada> getInfraestructurasAfectadas() {
        return infraestructurasAfectadas == null ? infraestructurasAfectadas = new ArrayList<InfraestructuraAfectada>() : infraestructurasAfectadas;
    }

    public InfraestructuraAfectada getInfraestructuraAfectada() {
        return infraestructuraAfectada == null ? infraestructuraAfectada = new InfraestructuraAfectada() : infraestructuraAfectada;
    }

    public List<InfraestructuraAfectada> getInfraestructurasAfectadasAlmacenadas() {
        return infraestructurasAfectadasAlmacenadas == null ? infraestructurasAfectadasAlmacenadas = new ArrayList<InfraestructuraAfectada>() : infraestructurasAfectadasAlmacenadas;
    }

    public DistanciaElementoSensible getDistanciaElementoSensible() {
        return distanciaElementoSensible == null ? distanciaElementoSensible = new DistanciaElementoSensible() : distanciaElementoSensible;
    }

    public boolean isHabilitarOtroElemento() {
        if (distanciaElementoSensible == null || distanciaElementoSensible.getElementoSensible() == null || distanciaElementoSensible.getElementoSensible().getNombre() == null) {
            return false;
        } else {
            if (!distanciaElementoSensible.getElementoSensible().getNombre().equals("Otros")) {
                return false;
            } else {
                return true;
            }
        }
    }

    public void resetAreaInfluencia() {
        this.areaInfluencia = new AreaInfluencia();
        nuevo = true;
    }

    public void adicionarAreaInfluenciaIndirecta() {
        if(nuevo)
            listaAreaInfluenciaIndirecta.add(this.areaInfluencia);
        nuevo = false;
    }

    public void adicionarAreaInfluenciaDirecta() {
        if(nuevo)
            listaAreaInfluenciaDirecta.add(this.areaInfluencia);
        nuevo = false;
    }

    public StreamedContent getStreamContent(Documento documento) throws Exception {
        DefaultStreamedContent content = null;
        try {
            this.documentoGeneral = this.descargarAlfresco(documento);
            if (this.documentoGeneral != null && this.documentoGeneral.getNombre() != null && this.documentoGeneral.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(this.documentoGeneral.getContenidoDocumento()));
                content.setName(this.documentoGeneral.getNombre());
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

        } catch (Exception exception) {
            LOGGER.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }

    public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
            documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
        if (documentoContenido != null)
            documento.setContenidoDocumento(documentoContenido);
        return documento;
    }    

    private Documento validarDocumentoHistorico(Documento documentoIngresado, List<Documento> documentosXEIA){		
		try {
			List<Documento> documentosList = new ArrayList<>();
			for(Documento documento : documentosXEIA){
				if(documento.getIdHistorico() != null && 
						documento.getIdHistorico().equals(documentoIngresado.getId()) && 
						documento.getNumeroNotificacion().equals(numeroNotificaciones)){
					documentosList.add(documento);
				}
			}			
			
			if(documentosList != null && !documentosList.isEmpty()){		        
				return documentosList.get(0);
			}else{
				return documentoIngresado;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return documentoIngresado = new Documento();
		}		
	}
    
    /**
	 * MarielaG
	 * Consultar los documentos originales
	 */
	private void consultarDocumentosOriginales(Integer idDocumento, List<Documento> documentosList){		
		try {
			if (documentosList != null && !documentosList.isEmpty() && documentosList.size() > 1) {
				listaDocumentosHistorico = new ArrayList<Documento>();
				while (idDocumento > 0) {
					idDocumento = recuperarHistoricos(idDocumento, documentosList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Consultar los documentos historicos en bucle
	 */
	private int recuperarHistoricos(Integer idDocumento, List<Documento> documentosList) {
		try {
			int nextDocument = 0;
			for (Documento documento : documentosList) {
				if (documento.getIdHistorico() != null && documento.getIdHistorico().equals(idDocumento)) {
					nextDocument = documento.getId();
					listaDocumentosHistorico.add(0, documento);
				}
			}

			return nextDocument;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
    /**
	 * MarielaG
	 * Consultar el area ingresada antes de las correcciones
	 */
	private void consultarAreaOriginal() {
		try {
			listaAreaDirectaFisicoHistorico = new ArrayList<>();
			listaAreaDirectaBioticoHistorico = new ArrayList<>();
			listaAreaIndirectaFisicoHistorico = new ArrayList<>();
			listaAreaIndirectaBioticoHistorico = new ArrayList<>();
			
			List<AreaInfluencia> lista = areaInfluenciaFacade.getAreaInfluenciaHistorico(areaInfluencia.getId());
			if (lista != null && !lista.isEmpty()) {
				for(AreaInfluencia area : lista){					
					if(!areaInfluencia.getDirectaFisicoDescripcion().equals(area.getDirectaFisicoDescripcion()) ||
							!areaInfluencia.getDirectaFisicoDistancia().equals(area.getDirectaFisicoDistancia()) ||
							!areaInfluencia.getDirectaFisicoDistanciaUnidad().equals(area.getDirectaFisicoDistanciaUnidad())){
						listaAreaDirectaFisicoHistorico.add(area);
					}
					if(!areaInfluencia.getDirectaBioticoDescripcion().equals(area.getDirectaBioticoDescripcion()) ||
							!areaInfluencia.getDirectaBioticoDistancia().equals(area.getDirectaBioticoDistancia()) ||
							!areaInfluencia.getDirectaBioticoDistanciaUnidad().equals(area.getDirectaBioticoDistanciaUnidad())){
						listaAreaDirectaBioticoHistorico.add(area);
					}
					if (!areaInfluencia.getIndirectaFisicoDescripcion().equals(area.getIndirectaFisicoDescripcion()) ||
							!areaInfluencia.getIndirectaFisicoDistancia().equals(area.getIndirectaFisicoDistancia()) ||
							!areaInfluencia.getIndirectaFisicoDistanciaUnidad().equals(area.getIndirectaFisicoDistanciaUnidad())) {
						listaAreaIndirectaFisicoHistorico.add(area);
					}
					if(!areaInfluencia.getIndirectaBioticoDescripcion().equals(area.getIndirectaBioticoDescripcion()) ||
							!areaInfluencia.getIndirectaBioticoDistancia().equals(area.getIndirectaBioticoDistancia()) ||
							!areaInfluencia.getIndirectaBioticoDistanciaUnidad().equals(area.getIndirectaBioticoDistanciaUnidad())){
						listaAreaIndirectaBioticoHistorico.add(area);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Consultar la infraestructura original
	 */
	public void cargarInfraestructurasOriginales(List<InfraestructuraAfectada> infraestructurasEnBdd) {
		try {
			infraestructurasAfectadasOriginales = new ArrayList<InfraestructuraAfectada>();
			List<InfraestructuraAfectada> infraestructurasOriginales = new ArrayList<InfraestructuraAfectada>();
			List<InfraestructuraAfectada> infraestructurasEliminadas = new ArrayList<>();
			totalInfraestructuraModificado = 0;
			
			for(InfraestructuraAfectada infraestructuraBdd : infraestructurasEnBdd){
				//si es un registro que se ingreso originalmente
				//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
				if(infraestructuraBdd.getNumeroNotificacion() == null ||
						!infraestructuraBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
					boolean agregarItemLista = true;
	    			//buscar si tiene historial
					for (InfraestructuraAfectada infraestructuraHistorico : infraestructurasEnBdd) {
						if (infraestructuraHistorico.getIdHistorico() != null  
								&& infraestructuraHistorico.getIdHistorico().equals(infraestructuraBdd.getId())) {
							//si existe un registro historico, no se agrega a la lista en este paso
							agregarItemLista = false;
							infraestructuraBdd.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						infraestructurasOriginales.add(infraestructuraBdd);
					}
				} else {
					totalInfraestructuraModificado++;
	    			//es una modificacion
	    			if(infraestructuraBdd.getIdHistorico() == null && infraestructuraBdd.getNumeroNotificacion() == numeroNotificaciones){
	    				//es un registro nuevo
	    				//no ingresa en el lista de originales
	    				infraestructuraBdd.setNuevoEnModificacion(true);
	    			}else{
	    				infraestructuraBdd.setRegistroModificado(true);
	    				if(!infraestructurasOriginales.contains(infraestructuraBdd)){
	    					infraestructurasOriginales.add(infraestructuraBdd);
	    				}
	    			}
	    		}
				
				//para consultar eliminados
				if (infraestructuraBdd.getIdHistorico() != null
						&& infraestructuraBdd.getNumeroNotificacion() != null) {
					boolean existePunto = false;
					for (InfraestructuraAfectada itemActual : this.infraestructurasAfectadas) {
						if (itemActual.getId().equals(infraestructuraBdd.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto) {
						infraestructurasEliminadas.add(infraestructuraBdd);
					}
				}
			}
			
			if (totalInfraestructuraModificado > 0) {
				this.infraestructurasAfectadasOriginales = infraestructurasOriginales;
			}
			
			infraestructurasAfectadasEliminadasBdd = infraestructurasEliminadas;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Consultar los elementos sensibles originales
	 */
	public void cargarDistanciasOriginales(List<InfraestructuraAfectada> infraestructurasBdd) {
		try {
			distanciasOriginales = new ArrayList<>();
			distanciasEliminadasBdd = new ArrayList<>();
			
			//para consultar las infraestructuras eliminadas
			for (InfraestructuraAfectada infraestructuraBdd : infraestructurasBdd) {
				List<DistanciaElementoSensible> distanciasOriginales = new ArrayList<>();
				List<DistanciaElementoSensible> distanciasEliminadas = new ArrayList<>();
				int totalInfraModificadas = 0;
				
				List<DistanciaElementoSensible> distanciasEnBdd = infraestructuraBdd.getDistanciaElementoSensibles();
				List<DistanciaElementoSensible> distanciasActuales = new ArrayList<>(); 
				for (DistanciaElementoSensible distanciaBdd : distanciasEnBdd) {
					if(distanciaBdd.getIdHistorico() == null)
						distanciasActuales.add(distanciaBdd);
				}

				for (DistanciaElementoSensible distanciaBdd : distanciasEnBdd) {
					// si es un registro que se ingreso originalmente
					// o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
					if (distanciaBdd.getNumeroNotificacion() == null
							|| !distanciaBdd.getNumeroNotificacion().equals(numeroNotificaciones)) {
						boolean agregarItemLista = true;
						// buscar si tiene historial
						for (DistanciaElementoSensible distanciaHistorico : distanciasEnBdd) {
							if (distanciaHistorico.getIdHistorico() != null
									&& distanciaHistorico.getIdHistorico().equals(distanciaBdd.getId())) {
								// si existe un registro historico, no se agrega
								// a la lista en este paso
								agregarItemLista = false;
								distanciaBdd.setRegistroModificado(true);
								break;
							}
						}
						if (agregarItemLista) {
							distanciasOriginales.add(distanciaBdd);
						}
					} else {
						totalInfraModificadas++;
						// es una modificacion
						if (distanciaBdd.getIdHistorico() == null && distanciaBdd.getNumeroNotificacion() == numeroNotificaciones) {
							// es un registro nuevo
							// no ingresa en el lista de originales
							distanciaBdd.setNuevoEnModificacion(true);
						} else {
							distanciaBdd.setRegistroModificado(true);
							if (!distanciasOriginales.contains(distanciaBdd)) {
								distanciasOriginales.add(distanciaBdd);
							}
						}
					}

					// para consultar eliminados
					if (distanciaBdd.getIdHistorico() != null
							&& distanciaBdd.getNumeroNotificacion() != null
							&& distanciaBdd.getNumeroNotificacion().equals(numeroNotificaciones)) {
						boolean existePunto = false;
						for (DistanciaElementoSensible itemActual : distanciasActuales) {
							if (itemActual.getId().equals(distanciaBdd.getIdHistorico())) {
								existePunto = true;
								break;
							}
						}

						if (!existePunto) {
							distanciasEliminadas.add(distanciaBdd);
						}
					}
				}

				if (totalInfraModificadas > 0) {
					this.distanciasOriginales.addAll(distanciasOriginales);
				}

				this.distanciasEliminadasBdd.addAll(distanciasEliminadas);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Para mostrar el historial de cambios de infraestructura
	 */
	public void mostrarInfraestructuraOriginal(InfraestructuraAfectada hallazgo) {
		infraestructurasAfectadasHistorial = new ArrayList<>();
		
		for(InfraestructuraAfectada infraOriginal : this.infraestructurasAfectadasOriginales){
			if(infraOriginal.getIdHistorico() != null && hallazgo.getId().equals(infraOriginal.getIdHistorico())){
				this.infraestructurasAfectadasHistorial.add(infraOriginal);
			}
		}
	}
	
	/**
	 * MarielaG Para mostrar las infraestructuras eliminadas
	 */
	public void fillInfraestructurasEliminadas() {
		infraestructurasAfectadasHistorial = infraestructurasAfectadasEliminadasBdd;
	}
	
	/**
	 * MarielaG
	 * Para mostrar el historial de cambios de distancias
	 */
	public void mostrarDistanciasOriginales(DistanciaElementoSensible distancia) {
		distanciasHistorial = new ArrayList<>();
		
		for(DistanciaElementoSensible distanciaOriginal : this.distanciasOriginales){
			if(distanciaOriginal.getIdHistorico() != null && distancia.getId().equals(distanciaOriginal.getIdHistorico())){
				this.distanciasHistorial.add(distanciaOriginal);
			}
		}
	}
	
	/**
	 * MarielaG Para mostrar las distancias eliminadas
	 */
	public void fillDistanciasEliminadas() {
		distanciasHistorial = distanciasEliminadasBdd;
	}
}
