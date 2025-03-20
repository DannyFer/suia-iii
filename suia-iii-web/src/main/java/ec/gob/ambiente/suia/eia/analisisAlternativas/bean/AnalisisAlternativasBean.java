package ec.gob.ambiente.suia.eia.analisisAlternativas.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.resumenEjecutivo.bean.ResumenEjecutivoBean;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.eia.analisisAlternativas.facade.AnalisisAlternativasFacade;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AnalisisAlternativasBean implements Serializable {

    private static final long serialVersionUID = 7566912687697700818L;

    @Setter
    private List<ActividadImplantacion> actividadesImplantacion;

    @Setter
    private List<Alternativa> alternativas;

    @Getter
    private List<TipoCriterioTecnico> tiposCriterioTecnico;

    @Getter
    private List<TipoSistemaSocioeconomico> tiposSistemaSocioeconomico;

    @Getter
    private List<TipoSistemaEcologico> tiposSistemaEcologico;

    @Setter
    private ActividadImplantacion actividadImplantacion;

    @Setter
    private Alternativa alternativa;

    @EJB
    private AnalisisAlternativasFacade analisisAlternativasFacade;

    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

    @Getter
    private EstudioImpactoAmbiental estudio;

    @Getter
    private List<ActividadImplantacion> saved;

    @Getter
    @Setter
    private Integer indiceModificacionActividad;


    //Adjuntar pdf Analisis de Alternativas Proyevto Hidrocarburos

    @EJB
    private DocumentosFacade documentosFacade;

    @Getter
    @Setter
    private Documento documentoGeneral, documentoGeneralHistorico, documentoGeneralOriginal;
//HASTA AKI
    
    @Getter
    private Boolean esMineriaNoMetalicos;
    
    private Map<String, Object> processVariables;
	@Getter
    private Integer numeroNotificaciones;
	
	@Getter
    private String promotor;
	
	@Getter
	private boolean existeObservaciones;
	@EJB
	private ProcesoFacade procesoFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
    private List<ActividadImplantacion> actividadesImplantacionOriginales, actividadesMejorOpcionOriginales, actividadesEliminadasBdd, actividadesHistorico;
	
	@Getter
	@Setter
    private List<Alternativa> alternativasOriginales, alternativasEliminadasBdd, alternativasHistorico;
	
	@Getter
    @Setter
    private List<Documento> listaDocumentoGeneralHistorico;

    @PostConstruct
    private void init() {
    	
    	try {
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
    		
    		if(numeroNotificaciones > 0){
				existeObservaciones = true;
    		}
    		//Fin CF		    	
        	
            tiposCriterioTecnico = analisisAlternativasFacade.getTiposCriterioTecnico();
            tiposSistemaSocioeconomico = analisisAlternativasFacade.getTiposSistemaSocioeconomico();
            tiposSistemaEcologico = analisisAlternativasFacade.getTiposSistemaEcologico();

    		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            estudio=(EstudioImpactoAmbiental) request.getSession().getAttribute("estudio"); 
//            estudio = estudioImpactoAmbientalFacade.obtenerEIAPorId(((EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT)).getId());
            if (JsfUtil.getBean(ResumenEjecutivoBean.class).isProyectoHidrocarburos()) {
                inicializarDocumento();
            }
            
            //MarielaG
            //Cambio metodo para recuperar todos los registros en la base datos
            List<ActividadImplantacion> actividadesEnBdd = analisisAlternativasFacade.getActividadesImplantacionEnBdd(estudio);
            if (actividadesEnBdd != null && !actividadesEnBdd.isEmpty()) {
            	for(ActividadImplantacion actividad : actividadesEnBdd){
            		if(actividad.getIdHistorico() == null){
            			getActividadesImplantacion().add(actividad);
            			
            			for(Alternativa alternativa: actividad.getAlternativas()){
            				if(alternativa.getIdHistorico() == null){
            					getAlternativas().add(alternativa);
            				}
            			}
            		}
            	}
            }

            esMineriaNoMetalicos=estudio.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudio.getResumenEjecutivo()==null?true:false;
            if (esMineriaNoMetalicos) {
                inicializarDocumento();
            }
            
            //MarielaG
			//consultar datos originales cuando el usuario es diferente al proponente
            //se realiza la consulta si NO es un proyecto minero
//            if(existeObservaciones && !promotor.equals(loginBean.getNombreUsuario()) && !esMineriaNoMetalicos){
            	consultarDatosOriginales(actividadesEnBdd);
//    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public List<ActividadImplantacion> getActividadesImplantacion() {
        return actividadesImplantacion == null ? actividadesImplantacion = new ArrayList<>() : actividadesImplantacion;
    }

    public ActividadImplantacion getActividadImplantacion() {
        return actividadImplantacion == null ? actividadImplantacion = new ActividadImplantacion()
                : actividadImplantacion;
    }

    public List<Alternativa> getAlternativas() {
        return alternativas == null ? alternativas = new ArrayList<>() : alternativas;
    }

    public Alternativa getAlternativa() {
        return alternativa == null ? alternativa = new Alternativa() : alternativa;
    }

    public List<Alternativa> getAlternativasPorActividad(ActividadImplantacion actividad) {
        List<Alternativa> result = new ArrayList<>();
        for (Alternativa alternativa : getAlternativas()) {
            if (alternativa.getActividadImplantacion() != null
                    && alternativa.getActividadImplantacion().equals(actividad))
                result.add(alternativa);
        }
        return result;
    }

    public void validateResultados(FacesContext context, UIComponent validate, Object value) {
        if (getActividadesImplantacion().isEmpty())
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Debe adicionar, al menos, una actividad o infraestructura de implantación.", null));
        for (ActividadImplantacion actividad : getActividadesImplantacion()) {
            if (getAlternativasPorActividad(actividad).isEmpty())
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Debe asociar al menos una alternativa para cada actividad o infraestructura de implantación.",
                        null));
        }
    }
//PROYECTO HIDROCARBURO

    private void cargarAdjuntosEIA(TipoDocumentoSistema tipoDocumento) {
        List<Documento> documentosXEIA = documentosFacade
        		.documentosTodosXTablaIdXIdDoc(estudio.getId(), "EstudioImpactoAmbiental", tipoDocumento);
        if (documentosXEIA.size() > 0) {
            this.documentoGeneral = documentosXEIA.get(0);
            
			if(existeObservaciones){
				//MARIELAG para recuperar el archivo original
//				if(!promotor.equals(loginBean.getNombreUsuario())){
					consultarDocumentosOriginales(documentosXEIA.get(0).getId(), documentosXEIA);
//				}

				documentoGeneralHistorico = new Documento();
				documentoGeneralHistorico = validarDocumentoHistorico(documentoGeneral, documentosXEIA);
			}
        }
    }

    private void inicializarDocumento() {
        documentoGeneral = new Documento();
        cargarAdjuntosEIA(TipoDocumentoSistema.ANALISIS_ALTERNATIVAS);
    }
    
    /**
	 * MarielaG
	 * Consultar los documentos originales Proyecto Minero
	 */
	private void consultarDocumentosOriginales(Integer idDocumento, List<Documento> documentosList){		
		try {
			listaDocumentoGeneralHistorico = new ArrayList<>();
			if (documentosList != null && !documentosList.isEmpty() && documentosList.size() > 1) {
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

						listaDocumentoGeneralHistorico.add(0, documento);
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
	 * Consultar actividades y alternativas originales para visualizacion
	 */
    public void consultarDatosOriginales(
			List<ActividadImplantacion> actividadesEstudioEnBdd) {
		try {
			List<ActividadImplantacion> actividadesOriginales = new ArrayList<ActividadImplantacion>();
			List<ActividadImplantacion> actividadesEliminadas = new ArrayList<>();

			actividadesImplantacionOriginales = new ArrayList<>();
			alternativasOriginales = new ArrayList<>();
			actividadesMejorOpcionOriginales = new ArrayList<>();

			int totalActividadesModificadas = 0;

			for (ActividadImplantacion actividadBdd : actividadesEstudioEnBdd) {
				// para ver originales ingresados en la modificacion anterior
				if (actividadBdd.getNumeroNotificacion() == null
						|| !actividadBdd.getNumeroNotificacion().equals(
								numeroNotificaciones)) {
					boolean agregarItemLista = true;
					// buscar si tiene historial
					for (ActividadImplantacion actividadHistorico : actividadesEstudioEnBdd) {
						if (actividadHistorico.getIdHistorico() != null
								&& actividadHistorico.getIdHistorico().equals(
										actividadBdd.getId())) {
							// si existe un registro historico, no se agrega a
							// la lista en este paso
							agregarItemLista = false;
							actividadBdd.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						actividadesOriginales.add(actividadBdd);
					}
				} else {
					totalActividadesModificadas++;
					// es una modificacion
					if (actividadBdd.getIdHistorico() == null
							&& actividadBdd.getNumeroNotificacion().equals(
									numeroNotificaciones)) {
						// es un registro nuevo, no ingresa en el lista de
						// originales
						actividadBdd.setNuevoEnModificacion(true);
					} else {
						actividadBdd.setRegistroModificado(true);
						if (!actividadesOriginales.contains(actividadBdd)) {
							actividadesOriginales.add(actividadBdd);
						}
					}
				}

				// para consultar eliminados
				if (actividadBdd.getIdHistorico() != null
						&& actividadBdd.getNumeroNotificacion() != null) {
					boolean existePunto = false;
					for (ActividadImplantacion itemActual : this.actividadesImplantacion) {
						if (itemActual.getId().equals(
								actividadBdd.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto)
						actividadesEliminadas.add(actividadBdd);
				}
			}

			if (totalActividadesModificadas > 0) {
				this.actividadesImplantacionOriginales = actividadesOriginales;
			}

			this.actividadesEliminadasBdd = actividadesEliminadas;

			cargarAlternativasOriginales(totalActividadesModificadas);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cargarAlternativasOriginales(int totalActividadesModificadas)
			throws CloneNotSupportedException {
		int totalAlternativasModificadas = 0;

		List<Alternativa> alternativasOrig = new ArrayList<>();
		List<Alternativa> alternativasEliminadas = new ArrayList<>();

		alternativasOriginales = new ArrayList<>();

		List<Alternativa> alternativasEnBdd = analisisAlternativasFacade
				.getAlternativasEstudio(estudio.getId());
		
		if(alternativasEnBdd != null){
			for (Alternativa alternativa : alternativasEnBdd) {
				// o si fue modificado en la modificacion anterior para ver
				// originales ingresados en la modificacion anterior
				if (alternativa.getNumeroNotificacion() == null
						|| !alternativa.getNumeroNotificacion().equals(
								numeroNotificaciones)) {
					boolean agregarItemLista = true;

					for (Alternativa alternativaHistorico : alternativasEnBdd) {
						if (alternativaHistorico.getNumeroNotificacion() != null
								&& alternativaHistorico.getIdHistorico() != null
								&& alternativa.getId().equals(
										alternativaHistorico.getIdHistorico())) {
							agregarItemLista = false;
							// validar si se modifico solo la mejor opcion o las
							// otra propiedades de las alternativas
							if (!alternativa
									.getActividadImplantacion()
									.getId()
									.equals(alternativaHistorico
											.getActividadImplantacion().getId())
									|| !alternativa.getCaracteristica().equals(
											alternativaHistorico
													.getCaracteristica())
									| !alternativa.getNombre().equals(
											alternativaHistorico.getNombre())
									|| !alternativa.getTipoCriterioTecnico()
											.equals(alternativaHistorico
													.getTipoCriterioTecnico())
									|| !alternativa.getTipoSistemaEcologico()
											.equals(alternativaHistorico
													.getTipoSistemaEcologico())
									|| !alternativa
											.getTipoSistemaSocioeconomico()
											.equals(alternativaHistorico
													.getTipoSistemaSocioeconomico())) {
								alternativa.setRegistroModificado(true);
							}
							break;
						}
					}

					if (agregarItemLista) {
						alternativasOrig.add(alternativa);
					}
				} else {
					totalAlternativasModificadas++; // porque tienen numero de
													// notificacion

					if (alternativa.getIdHistorico() != null) { // si es historico
																// de otro registro
						alternativa.setRegistroModificado(true);

						if (!alternativasOrig.contains(alternativa)) {
							alternativasOrig.add(alternativa);
						}
					} else {
						if (alternativa.getNumeroNotificacion().equals(
								numeroNotificaciones)) {
							alternativa.setNuevoEnModificacion(true);
						}
					}
				}

				// para consultar eliminados
				if (alternativa.getIdHistorico() != null
						&& alternativa.getNumeroNotificacion() != null) {
					boolean existePunto = false;
					for (Alternativa itemActual : this.alternativas) {
						if (itemActual.getId().equals(alternativa.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto)
						alternativasEliminadas.add(alternativa);
				}

				// para enviar la informacion de cambios a la lista original
				for (Alternativa itemActual : this.alternativas) {
					if (itemActual.getId().equals(alternativa.getId())) {
						itemActual.setRegistroModificado(alternativa
								.getRegistroModificado());
						itemActual.setNuevoEnModificacion(alternativa
								.getNuevoEnModificacion());
						break;
					}
				}
			}
		}		

		if (totalAlternativasModificadas > 0) {
			this.alternativasOriginales = alternativasOrig;
		}

		alternativasEliminadasBdd = alternativasEliminadas;

		cargarMejorOpcionOriginales(alternativasEnBdd);
	}

	private void cargarMejorOpcionOriginales(List<Alternativa> alternativasEnBdd) {
		try {
			actividadesMejorOpcionOriginales = new ArrayList<>();
			
			if(actividadesImplantacion != null){
				for (ActividadImplantacion actividad : actividadesImplantacion) {
					List<Alternativa> alternativasActividad = new ArrayList<>();
					for (Alternativa alternativa : alternativasEnBdd) {
						if (alternativa.getActividadImplantacion().getId()
								.equals(actividad.getId())) {
							alternativasActividad.add(alternativa);
						}
					}

					for (Alternativa alternativa : alternativasActividad) {
						if (alternativa.getNumeroNotificacion() == null
								|| !alternativa.getNumeroNotificacion().equals(numeroNotificaciones)) {
							if (alternativasActividad.size() > 1) {
								for (Alternativa alternativaHistorico : alternativasActividad) {
									if (alternativaHistorico.getNumeroNotificacion() != null
											&& alternativaHistorico.getIdHistorico() != null
											&& alternativa.getId().equals(alternativaHistorico.getIdHistorico())) {
										if (alternativa.isMejorOpcion() != alternativaHistorico.isMejorOpcion()
												&& !actividad.getMejorOpcionModificado()) {
											actividad.setMejorOpcionModificado(true);
											actividadesMejorOpcionOriginales.add(actividad);
										}
										break;
									}
								}
							}
						} else {
							if (alternativa.getNumeroNotificacion() == numeroNotificaciones
									&& alternativa.getIdHistorico() == null) {
								if (alternativa.isMejorOpcion()
										&& !actividad.getMejorOpcionModificado()) {
									actividad.setMejorOpcionModificado(true);
									actividadesMejorOpcionOriginales.add(actividad);
								}
							}
						}
					}
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cris F: validación de documento para guardar cuando un proyecto es
	 * mineria no metalico
	 */

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
	 * MarielaG Para mostrar el historial de observaciones original de
	 * ACTIVIDADES
	 */
	public void mostrarActividadOriginal(
			ActividadImplantacion actividadSeleccionada) {
		actividadesHistorico = new ArrayList<>();

		for (ActividadImplantacion actividadOriginal : this.actividadesImplantacionOriginales) {
			if (actividadOriginal.getIdHistorico() != null
					&& actividadSeleccionada.getId().equals(
							actividadOriginal.getIdHistorico())) {
				this.actividadesHistorico.add(actividadOriginal);
			}
		}
	}

	/**
	 * MarielaG Para mostrar las ACTIVIDADES eliminadas
	 */
	public void fillActividadesEliminados() {
		actividadesHistorico = actividadesEliminadasBdd;
	}

	/**
	 * MarielaG Para mostrar el historial de observaciones original de
	 * ALTERNATIVAS
	 */
	public void mostrarAlternativaOriginal(Alternativa alternativaSeleccionada) {
		alternativasHistorico = new ArrayList<>();

		for (Alternativa alternativaOriginal : this.alternativasOriginales) {
			if (alternativaOriginal.getIdHistorico() != null
					&& alternativaSeleccionada.getId().equals(
							alternativaOriginal.getIdHistorico())) {
				this.alternativasHistorico.add(alternativaOriginal);
			}
		}
	}

	/**
	 * MarielaG Para mostrar el historial de observaciones original
	 */
	public void fillAlternativasEliminadas() {
		alternativasHistorico = alternativasEliminadasBdd;
	}

	/**
	 * MarielaG Para mostrar el historial de observaciones original de
	 * ACTIVIDADES MEJOR OPCION
	 */
	public void mostrarMejorOpcionOriginal(ActividadImplantacion actividadSeleccionada) {
		alternativasHistorico = new ArrayList<>();

		for (Alternativa alternativaOriginal : this.alternativasOriginales) {
			if(alternativaOriginal.getActividadImplantacion().getId().equals(actividadSeleccionada.getId())){
				if (alternativaOriginal.getIdHistorico() != null) {
					this.alternativasHistorico.add(alternativaOriginal);
				}
			}
		}
	}
}
