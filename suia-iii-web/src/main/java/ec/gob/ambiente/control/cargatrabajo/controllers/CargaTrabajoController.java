/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.control.cargatrabajo.controllers;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.CargaTrabajo;
import ec.gob.ambiente.suia.domain.CargaTrabajoRevision;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.cargaTrabajo.facade.CargaTrabajoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.primefaces.context.RequestContext;

/**
 * @author bburbano
 */
@ManagedBean
@ViewScoped
public class CargaTrabajoController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7332827533098349739L;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private ProcesoFacade procesoFacade;
    @EJB
    private CargaTrabajoFacade cargaTrabajoFacade;

	@Getter
	private List<SelectItem> listaPrioridad;

	@Getter
	private List<SelectItem> listaEstados;

	@Getter
	private List<SelectItem> listaPronunciamientos;

	@Getter
	private Date fechaActual;

	@Getter
	private Date fechaInicial;

	@Getter
	@Setter
	public List<CargaTrabajo> listaCargarTrabajo;

	@Getter
	@Setter
	public List<CargaTrabajo> listaCargarTrabajoObligacionesAtrazadas;

	@Getter
	@Setter
	public List<CargaTrabajoRevision> listaCargarTrabajoRevision;

	@Getter
	@Setter
	public List<CargaTrabajoRevision> listaCargarTrabajoRevisionAtrazadas;

	@Getter
	@Setter
	public boolean presentacinFisica;

	@Getter
	@Setter
	private boolean existeProvincia;

	@Getter
	@Setter
	private boolean existeResolucion;

	@Getter
	@Setter
	private boolean existeFechaResolucion;

	@Getter
	@Setter
	private boolean mostrarFormulario;

	@Getter
	@Setter
	private boolean editarFormulario;

	@Getter
	@Setter
	private boolean esHidrocarburos;

	@Getter
	@Setter
	private boolean tieneBloque;

	@Getter
	@Setter
	private boolean aNivelNacional;

	@Getter
	@Setter
	private boolean mostrarTodos;

	@Getter
	@Setter
	private boolean mostrarBtnAgragar;

	@Getter
	@Setter
	private boolean mostrarObligaciones;

	@Getter
	@Setter
	private boolean editarDatos;
	
	private boolean guardar;
	
	private String datosPendientes;
	
	private String provinciaUsuario;

	@Getter
	@Setter
	private String tipoTramiteId;

	@Getter
	@Setter
	private String tipoSectorId;

	@Getter
	@Setter
	private String tipoServicioId;

	@Getter
	@Setter
	private String operadoraId;

	@Getter
	@Setter
	private String bloqueId;

	@Getter
	@Setter
	private String metaId;

	@Getter
	@Setter
	private String provinciaId;

	@Getter
	@Setter
	private String unidadAdministrativa ="";
	
	private String areaAbreviacion ="";

	@Getter
	private List<CatalogoGeneral> listaTipoTramite;

	@Getter
	private List<CatalogoGeneral> listaTipoSector;

	@Getter
	private List<CatalogoGeneral> listaTipoServicio;

	@Getter
	private List<CatalogoGeneral> listaOperadoras;

	@Getter
	private List<CatalogoGeneral> listaMeses;
	
	@Getter
	private List<CatalogoGeneral> listaBloques;
	
	@Getter
	private List<String> listaAnios, listaAniosMeta, listaAniosMetaAux;

    @Getter
    @Setter
    private FichaAmbientalPma fichaAmbiental;

    @Getter
    @Setter
    private CargaTrabajo cargaTrabajo;

    @Getter
    @Setter
    private CargaTrabajoRevision cargaTrabajoRevision;
    
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    @Getter
    @Setter
    private CargaTrabajo cargaTrabajoSeleccionado;

    @Getter
    @Setter
    private CargaTrabajoRevision cargaTrabajoRevisionSeleccionado;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	private static final SelectItem SELECCIONE = new SelectItem(null,"Seleccione");
    private static final String PRIORIDAD_NORMAL = "N";
    private static final String PRIORIDAD_URGENTE = "U";
    private static final String ESTADO_PENDIENTE = "P";
    private static final String ESTADO_TRAMITADO = "T";
    private static final String PRONUNCIAMINETO_APROBADO = "A";
    private static final String PRONUNCIAMINETO_OBSERVADO = "O";
    private static final String PRONUNCIAMINETO_NOAPLICA = "N";
    private static String CODIGO_HIDROCARBUROS = "";
    private static String CODIGO_MINERIA = "";
    private static String CODIGOS_OBLIGACIONES = "";
    private static Integer idPorDefinir = 1946;
    public static Integer idNoAplica = 1957;

    @PostConstruct
    public void inicio() {
    	// inicializamos la fecha de inicio
    	SimpleDateFormat formatofecha = new SimpleDateFormat("yyyy-MM-dd");
    	editarDatos = true;
    	datosPendientes = "";
    	mostrarBtnAgragar = false;
    	mostrarObligaciones = false;
    	try{
    		fechaInicial = formatofecha.parse("2017-01-01");
    	}catch(ParseException ex){
    		fechaInicial = new Date();
    	}
    	if (loginBean.getUsuario().getPersona() != null){
    		if (loginBean.getUsuario().getPersona().getUbicacionesGeografica() != null){
    			provinciaUsuario = loginBean.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec();
    		}
    	}
    	fechaActual = new Date();
		cargaTrabajoSeleccionado = new CargaTrabajo();
    	presentacinFisica = true;
		existeProvincia = false;
		existeResolucion = false;
		existeFechaResolucion = false;
		mostrarFormulario = false;
		esHidrocarburos = false;
		editarFormulario = true;
		tieneBloque = false;
    	cargaTrabajo = new CargaTrabajo();
    	proyecto = new ProyectoLicenciamientoAmbiental();
    	cargaTrabajo.setFechaRegistro(new Date());
        cargarDatosUsuario();
        cargarListaPrioridades();
        cargarListaEstados();
        cargarListaAnios();
        cargarListaPronunciamiento();
        //listaBloques = proyectoLicenciamientoAmbientalFacade.getBloques("");
        listaBloques = llenarCatalogos(TipoCatalogo.BLOQUES_HIDROCARBUROS);
        listaOperadoras = llenarCatalogos(TipoCatalogo.OPERADORAS_HIDROCARBUROS);
        listaTipoSector =  llenarCatalogoTipoSector(TipoCatalogo.TIPO_SECTOR_CARGA_TRABAJO);
        if(listaTipoSector.size() > 0){
        	for(CatalogoGeneral catalogo : listaTipoSector){
        		if (catalogo.getDescripcion().equals("Hidrocarburos")){
                    CODIGO_HIDROCARBUROS = catalogo.getId().toString();
        		}else if (catalogo.getDescripcion().equals("Minería")){
        			CODIGO_MINERIA = catalogo.getId().toString();
        		}
        	}
        }
        listaTipoServicio = llenarCatalogoTipoSector(TipoCatalogo.TIPO_SERVICIOS);
        if(listaTipoServicio.size() > 0){
        	for(CatalogoGeneral catalogo : listaTipoServicio){
        		if (catalogo.getValor().equals("1")){
                    CODIGOS_OBLIGACIONES += ","+catalogo.getId().toString();
        		}
        	}
        }
        listaMeses = llenarCatalogosSinOrden(100);
        cargarListaTrabajos();
    	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		if (request.getParameter("nuevo") != null){
			//nuevo();
			cancelar();
		}else{
			//muestro alerta si hay tramites sin responder
			if( (listaCargarTrabajoObligacionesAtrazadas != null && listaCargarTrabajoObligacionesAtrazadas.size() > 0) || (listaCargarTrabajoRevisionAtrazadas != null && listaCargarTrabajoRevisionAtrazadas.size() > 0) ){
				RequestContext.getCurrentInstance().execute("PF('modalPendientes').show();");
			}
		}
      }

    private void cargarDatosUsuario() {
        try {
            cargaTrabajo.setUnidadAdministrativaId(loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getId());
            cargaTrabajo.setUsuario(loginBean.getUsuario());
            
            Boolean esUnidadDP = false;
            for (AreaUsuario areaUser : loginBean.getUsuario().getListaAreaUsuario()) {
        		Area areaU = areaUser.getArea();
        		if ("UNIDAD DE PRODUCTOS DESECHOS PELIGROSOS Y NO PELIGROSOS".equals(areaU.getAreaName())) {
        			esUnidadDP = true;
        			break;
        		}
			}
            
            if (esUnidadDP){
            	unidadAdministrativa  = "DIRECCIÓN NACIONAL DE CONTROL AMBIENTAL";	
            }
			if (loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().contains("DP")){
				areaAbreviacion = "NODP";
			}else{
				areaAbreviacion = "DP";
			}
        } catch (Exception e) {

        }
    }

	public void mostrarBusqueda() {
		limpiarCampos();
		if (cargaTrabajo.isExisteCodigoSuia()) {
			presentacinFisica = false;
		}else {
			presentacinFisica = true;
			existeProvincia = false;
			existeResolucion = false;
			existeFechaResolucion = false;
		}
		mostrarBloque();
	}

	public void mostrarBloque() {
		if (tipoSectorId.equals(CODIGO_HIDROCARBUROS)) {
			esHidrocarburos = true;
			if(cargaTrabajo.getProyectoId() != null ){
				if (cargaTrabajo.isExisteCodigoSuia() && cargaTrabajo.getProyectoId() > 0) {
					esHidrocarburos = false;
				}else if (cargaTrabajo.isExisteCodigoSuia() && cargaTrabajo.getProyectoId() == 0 && proyecto != null) {
					esHidrocarburos = tieneBloque;
				}
			}
		}else {
			esHidrocarburos = false;
			if(cargaTrabajo.getProyectoId() != null ){
				if (cargaTrabajo.isExisteCodigoSuia() && cargaTrabajo.getProyectoId() == 0 && proyecto != null) {
					esHidrocarburos = tieneBloque;
				}
			}
		}
		//actualizo el combo de tipo de tramite
		if(!tipoSectorId.isEmpty()){
			llenarSubCatalogo(tipoSectorId);
		}
		operadoraId="";
	}

	public boolean habilitarObligaciones(){
		mostrarObligaciones = CODIGOS_OBLIGACIONES.contains(tipoServicioId);
		return !CODIGOS_OBLIGACIONES.contains(tipoServicioId);
	}

	private void cargarListaTrabajos() {
		try{
			mostrarTodos = false;
			// si pertenece a la unidda Administrativa UNIDAD DE PRODUCTOS DESECHOS PELIGROSOS Y NO PELIGROSOS se muestra todos los registros
			Boolean esUnidadDP = false;
            for (AreaUsuario areaUser : loginBean.getUsuario().getListaAreaUsuario()) {
        		Area areaU = areaUser.getArea();
        		if ("UNIDAD DE PRODUCTOS DESECHOS PELIGROSOS Y NO PELIGROSOS".equals(areaU.getAreaName())) {
        			esUnidadDP = true;
        			break;
        		}
			}
            
            if (esUnidadDP){
				mostrarTodos = true;
				listaCargarTrabajo = cargaTrabajoFacade.listarCargarTrabajo(loginBean.getUsuario());
				//listaCargarTrabajo = cargaTrabajoFacade.listarCargarTrabajoTodos();
			}else{
				listaCargarTrabajo = cargaTrabajoFacade.listarCargarTrabajo(loginBean.getUsuario());
			}
			cargaTrabajoRevision = null;
			// cargo las revisiones que ya paso el tiempo de respuesta
			listaCargarTrabajoRevisionAtrazadas = cargaTrabajoFacade.listarCargarTrabajoRevisionesAtrazadas(loginBean.getUsuario());
			// cargo las obligaciones que ya paso el tiempo de respuesta
			listaCargarTrabajoObligacionesAtrazadas = cargaTrabajoFacade.listarCargarTrabajoObligacionesAtrazadas(loginBean.getUsuario());
		}catch(ServiceException ex){
				
		}
	}

	public void buscarProyecto() throws JbpmException, ServiceException {
		if(cargaTrabajo.getCodigo().isEmpty()){
            JsfUtil.addMessageError("El campo Código es requerido.");
		}else{
			// si no encontro el proyecto busco en 4  categorias 
			proyecto = cargaTrabajoFacade.obtenerProyecto4CategoriasPorCodigo(cargaTrabajo.getCodigo());
			if(proyecto.getCodigo() != null){
				cargarProyecto4Categorias();
			}else {
				proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(cargaTrabajo.getCodigo());
				if(proyecto != null){
					cargarProyectoSuia();
				}else{
		            JsfUtil.addMessageError("El Código del proyecto no existe.");
					limpiarCampos();
				}
			}
		}
	}
	
	private void cargarProyectoSuia(){
		tieneBloque = false;
		String codigoP = cargaTrabajo.getCodigo(); 
		String provinciaProyecto = proyecto.getAreaResponsable().getUbicacionesGeografica().getCodificacionInec();
		// valido que no se encuentre repetido el codigo
		Integer id = (cargaTrabajo.getId() == null )? 0:cargaTrabajo.getId();
		try{
			if (validarExisteCarga(id)){
				//mostrarTodos = true;
				limpiarCampos();
				if (!provinciaUsuario.equals(provinciaProyecto)  && !mostrarTodos){
		            JsfUtil.addMessageError("El Código del proyecto pertenece a otra provincia.");
				}else {
					/*List<ProcessInstanceLog> listaProces = procesoFacade.getProcessInstancesLogsVariableValue(loginBean.getUsuario(), "idProyecto", proyecto.getId().toString());
					if (listaProces.size() > 0){*/
						//ProcessInstanceLog  proceso = procesoFacade.getProcessInstanceLog(loginBean.getUsuario(), listaProces.get(0).getProcessInstanceId());
						//if (proceso.getStatus().equals(2) ){
							existeProvincia = false;
							existeResolucion = false;
							existeFechaResolucion = false;
							cargaTrabajo.setNombre(proyecto.getNombre());
							cargaTrabajo.setProyectoId(proyecto.getId());
							cargaDatosProyecto(proyecto);
					        fichaAmbiental = fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyecto(proyecto.getId());
					        // si existe resolucion seteo
					        if(fichaAmbiental != null){
					        	if(fichaAmbiental.getNumeroOficio() != null){
					        		cargaTrabajo.setNumeroResolucion(fichaAmbiental.getNumeroOficio());
									existeResolucion = true;
					        	}
					        	if(fichaAmbiental.getFechaModificacion() != null){
					        		cargaTrabajo.setFechaResolucion(fichaAmbiental.getFechaModificacion());
									existeFechaResolucion = true;
					        	}
					        }
							cargaTrabajo.setCodigo(codigoP); 
					/*	}else {
				            JsfUtil.addMessageError("El Código del proyecto no esta finalizado.");
							limpiarCampos();
						}*/
					/*}else {
			            JsfUtil.addMessageError("El Código del proyecto no esta finalizado.");
						limpiarCampos();
					}*/
				}
			}
		}catch(Exception e){
			JsfUtil.addMessageError("Ocurrió un error al realizar la búsqueda.");
		}
	}

	private void cargarProyecto4Categorias(){
		tieneBloque = false;
		String codigoP = cargaTrabajo.getCodigo(); 
		String provinciaProyecto = proyecto.getDireccionProyecto();
		// valido que no se encuentre repetido el codigo
		Integer id = (cargaTrabajo.getId() == null )? 0:cargaTrabajo.getId();
		try{
			if (!validarExisteCarga(id)){
				JsfUtil.addMessageError("El código del proyecto ya está registrado.");
			}else{
				//mostrarTodos = true;
				limpiarCampos();
				if (!provinciaUsuario.equals(provinciaProyecto)  && !mostrarTodos){
		            JsfUtil.addMessageError("El Código del proyecto pertenece a otra provincia.");
				}else {
					// verifico si es de hidrocarburos
					if(proyecto.getCodigoMinero() != null && "hidrocarburos".equals(proyecto.getCodigoMinero())){
						// consulto el proyecto esta finalizado > 0
						//if (proyectoLicenciamientoAmbientalFacade.consultaHidrocarburosFinalizado(proyecto.getCodigo()) > 0) {
							existeProvincia = false;
							cargaTrabajo.setNombre(proyecto.getNombre());
							cargaTrabajo.setProyectoId(0);
							cargaTrabajo.setNumeroResolucion(null);
							cargaDatosProyecto4Categorias(proyecto);
							cargaTrabajo.setCodigo(codigoP); 
						/*}else{
				            JsfUtil.addMessageError("El Código del proyecto no esta finalizado.");
							limpiarCampos();
						}*/
					}else{
						if (proyecto.getFechaFin() != null ){
							existeProvincia = false;
							cargaTrabajo.setNombre(proyecto.getNombre());
							cargaTrabajo.setProyectoId(0);
							cargaTrabajo.setNumeroResolucion(null);
							cargaDatosProyecto4Categorias(proyecto);
					        // cargaTrabajo.setFechaResolucion(proyecto.getFechaFin());
							cargaTrabajo.setCodigo(codigoP); 
						}else {
				            JsfUtil.addMessageError("El Código del proyecto no esta finalizado.");
							limpiarCampos();
						}
					}
				}
			}
		}catch(Exception e){
            JsfUtil.addMessageError("Ocurrió un error al realizar la búsqueda.");
		}
	}
	
	private boolean validarExisteCarga(Integer id) throws ServiceException{
		List<CargaTrabajo> lista = cargaTrabajoFacade.consultarExisteCargaTrabajo(cargaTrabajo.getCodigo() , id);
		if (lista.size() > 0){
			JsfUtil.addMessageError("El código del proyecto ya está registrado.");
            return false;
		}
		return true;
	}
	
	private void cargaDatosProyecto(ProyectoLicenciamientoAmbiental proyecto){
		existeFechaResolucion = false;
		existeResolucion = false;
		fichaAmbiental = fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyecto(proyecto.getId());
        // si existe resolucion seteo
        if(fichaAmbiental != null){
        	if(fichaAmbiental.getNumeroOficio() != null){
        		cargaTrabajo.setNumeroResolucion(fichaAmbiental.getNumeroOficio());
				existeResolucion = true;
        	}
        	if(fichaAmbiental.getFechaModificacion() != null){
        		cargaTrabajo.setFechaResolucion(fichaAmbiental.getFechaModificacion());
				existeFechaResolucion = true;
        	}
        }
        
		// si hay ubicacion seteo la provincia
		if (proyecto.getAreaResponsable().getUbicacionesGeografica().getId() > 0 ){
			existeProvincia = true;
			cargaTrabajo.setProvincia(proyecto.getAreaResponsable().getUbicacionesGeografica());
			provinciaId = proyecto.getAreaResponsable().getUbicacionesGeografica().getId().toString();
		}
		// verifico si es de hidrocarburos
		if(proyecto.getTipoSector() != null && proyecto.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)){
			esHidrocarburos = true;
		}
	}

	private void cargaDatosProyecto4Categorias(ProyectoLicenciamientoAmbiental proyecto){
		existeResolucion = false;
		existeFechaResolucion = false;
		// si hay ubicacion seteo la provincia
		if(proyecto.getDireccionProyecto() != null){
			UbicacionesGeografica objUbicacion = ubicacionGeograficaFacade.buscarUbicacionPorCodigoInec(proyecto.getDireccionProyecto());
			cargaTrabajo.setProvincia(objUbicacion);
			provinciaId = objUbicacion.getId().toString();
			existeProvincia = true;
		}
		// verifico si es de hidrocarburos
		if(proyecto.getCodigoMinero() != null && "hidrocarburos".equals(proyecto.getCodigoMinero())){
			// consulto el proyecto en suia para ver si tiene bloques
			ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(proyecto.getCodigo());
			if (proyectoLicenciamientoAmbiental != null && proyectoLicenciamientoAmbiental.getProyectoBloques() != null && proyectoLicenciamientoAmbiental.getProyectoBloques().size() > 0) {
				esHidrocarburos = true;
				tieneBloque = true;
			}
		}
	}
	
	private void cargarListaPrioridades() {
		listaPrioridad = new ArrayList<SelectItem>();
		listaPrioridad.add(SELECCIONE);
		listaPrioridad.add(new SelectItem(PRIORIDAD_NORMAL, "NORMAL"));
		listaPrioridad.add(new SelectItem(PRIORIDAD_URGENTE, "URGENTE"));
	}

	private void cargarListaEstados() {
		listaEstados = new ArrayList<SelectItem>();
		listaEstados.add(SELECCIONE);
		listaEstados.add(new SelectItem(ESTADO_PENDIENTE, "PENDIENTE"));
		listaEstados.add(new SelectItem(ESTADO_TRAMITADO, "TRAMITADO"));
	}

	private void cargarListaPronunciamiento() {
		listaPronunciamientos = new ArrayList<SelectItem>();
		listaPronunciamientos.add(SELECCIONE);
		listaPronunciamientos.add(new SelectItem(PRONUNCIAMINETO_APROBADO, "APROBADO"));
		listaPronunciamientos.add(new SelectItem(PRONUNCIAMINETO_OBSERVADO, "OBSERVADO"));
		listaPronunciamientos.add(new SelectItem(PRONUNCIAMINETO_NOAPLICA, "NO APLICA"));
	}

	private void cargarListaAnios() {
		Date nuevaFecha = new Date();
		Integer i= JsfUtil.getYearFromDate(nuevaFecha);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, 6);
		nuevaFecha = cal.getTime();
		listaAnios = new ArrayList<String>();
		listaAniosMetaAux = new ArrayList<String>();
		listaAniosMetaAux.add(i.toString());
		if (i < JsfUtil.getYearFromDate(nuevaFecha)){
			i= JsfUtil.getYearFromDate(nuevaFecha);
			listaAniosMetaAux.add(i.toString());
		}
		for ( i=2010; i<= JsfUtil.getYearFromDate(nuevaFecha); i++){
			listaAnios.add(i.toString());
		}
		listaAniosMeta = listaAniosMetaAux;
	}

	public List<CatalogoGeneral> llenarCatalogos(int tipo) {
		try {
			return catalogoGeneralFacade.obtenerCatalogoXTipoOrdenado(tipo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<CatalogoGeneral> llenarCatalogosSinOrden(int tipo) {
		try {
			List<CatalogoGeneral> listaM = catalogoGeneralFacade.obtenerCatalogoXTipo(tipo); 
			CatalogoGeneral objCatalogo = catalogoGeneralFacade.obtenerCatalogoXId(Integer.valueOf(idPorDefinir));
			listaM.add(objCatalogo);
			return listaM;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<CatalogoGeneral> llenarCatalogoTipoSector(int tipo) {
		try {
			List<CatalogoGeneral>  listaCatalogo = catalogoGeneralFacade.obtenerCatalogoXTipoOrdenado(tipo);
			return eliminarRegistrosLista(listaCatalogo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void llenarSubCatalogo(String catalogoId){
		try {
				listaTipoTramite = null;
				listaTipoTramite = catalogoGeneralFacade.obtenerCatalogoXPadreOrdenadoDescripcion(Integer.valueOf(catalogoId));
				//quito los elementos de la lista de acuerdo a la direccion del usuario si es o no una DP
				if (tipoSectorId.equals(CODIGO_HIDROCARBUROS) || tipoSectorId.equals(CODIGO_MINERIA) ) {
					listaTipoTramite = eliminarRegistrosLista(listaTipoTramite);
				}
		} catch (Exception e) {
			e.printStackTrace();
				listaTipoTramite = null;
		}
	}
	
	public  List<CatalogoGeneral>  eliminarRegistrosLista(List<CatalogoGeneral>  listaCatalogosAux){
		for(int i=(listaCatalogosAux.size() -1); i >= 0; i--    ){
			if (listaCatalogosAux.get(i).getCodigo() != null && listaCatalogosAux.get(i).getCodigo().equals(areaAbreviacion)){
				listaCatalogosAux.remove(i);
			}
		}
		return listaCatalogosAux;
	}

	
	private void limpiarCamposObligaciones(){
		cargaTrabajo.setAuditoria(false);
		cargaTrabajo.setInforme(false);
		cargaTrabajo.setMonitoreo(false);
		cargaTrabajo.setTdr(false);
		cargaTrabajo.setFechaAuditoria(null);
		cargaTrabajo.setFechaInforme(null);
		cargaTrabajo.setFechaMonitoreo(null);
		cargaTrabajo.setFechaTdr(null);
		
	}
	
	private void limpiarCampos(){
		cargaTrabajo.setProyectoId(null);
		cargaTrabajo.setNombre("");
		cargaTrabajo.setCodigo("");
		cargaTrabajo.setProvincia(null);	
		cargaTrabajo.setNumeroResolucion("");
		cargaTrabajo.setFechaResolucion(null);
		esHidrocarburos = false;
		tieneBloque = false;
		provinciaId = "";
		limpiarCamposObligaciones();
	}

	public void guardar() throws NumberFormatException, Exception {
		guardar = true;
		datosPendientes = "";
		if(loginBean.getUsuario().getId() == null){
			JsfUtil.addMessageError("Debe iniciar session para continuar con el proceso.");
		}else{
			if(CODIGOS_OBLIGACIONES.contains(tipoServicioId)){
				if(!(cargaTrabajo.isAuditoria() || cargaTrabajo.isInforme() || cargaTrabajo.isMonitoreo() || cargaTrabajo.isTdr())){
		            JsfUtil.addMessageError("El campo obligaciones es requerido.");
					return;
				}
			}
			if(cargaTrabajo.isExisteCodigoSuia() && cargaTrabajo.getProyectoId() == null ){
	            JsfUtil.addMessageError("El código del proyecto no existe.");
			}else{
				if (cargaTrabajo.isExisteCodigoSuia()){
					// valido que exista el codigo
					if (cargaTrabajo.getProyectoId() != null && cargaTrabajo.getProyectoId() > 0){
						proyecto = proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(cargaTrabajo.getProyectoId());
					}else{
						//valido que exista en 4 categorias
						proyecto = cargaTrabajoFacade.obtenerProyecto4CategoriasPorCodigo(cargaTrabajo.getCodigo());
					}
					if (cargaTrabajo.getProyectoId() != null && !cargaTrabajo.getCodigo().equals(proyecto.getCodigo()) ){
			            JsfUtil.addMessageError("El código del proyecto no existe.");
			            guardar = false;
					}
				}
				// valido que no se encuentre repetido el codigo
				Integer id = (cargaTrabajo.getId() == null )? 0:cargaTrabajo.getId(); 
				if(cargaTrabajo.getCodigo() != null && !cargaTrabajo.getCodigo().isEmpty()){
					guardar = validarExisteCarga(id);
				}
				// valido si la meta es mayor a la fecha
				if(cargaTrabajoRevision != null && cargaTrabajoRevision.getId() == null){
					String sDate1= cargaTrabajoRevision.getAnioMeta().toString();
				    switch (Integer.valueOf(metaId)) {
					case 323:
						sDate1 +="/01/01";
						break;
					case 324:
						sDate1 +="/02/01";
						break;
					case 325:
						sDate1 +="/03/01";
						break;
					case 326:
						sDate1 +="/04/01";
						break;
					case 327:
						sDate1 +="/05/01";
						break;
					case 328:
						sDate1 +="/06/01";
						break;
					case 329:
						sDate1 +="/07/01";
						break;
					case 330:
						sDate1 +="/08/01";
						break;
					case 331:
						sDate1 +="/09/01";
						break;
					case 332:
						sDate1 +="/10/01";
						break;
					case 333:
						sDate1 +="/11/01";
						break;
					case 334:
						sDate1 +="/12/01";
						break;

					default:
						sDate1 ="2040/01/01";
						break;
					}

				    Date date1=new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
					Calendar cal = Calendar.getInstance();
					cal.setTime(date1);
					cal.add(Calendar.MONTH, 1);
					date1 = cal.getTime();
					if(date1.before(new Date())){
			            JsfUtil.addMessageError("La meta no puede ser menor a la fecha actual.");
			            guardar = false;
					}
				}
				if(!CODIGOS_OBLIGACIONES.contains(tipoServicioId) ){
					limpiarCamposObligaciones();
				}
				if(guardar){
					// pasar informacion a mayusculas
					datosUpperCase();
					//seteo la provincia seleccionada
					if(!provinciaId.isEmpty()){
						UbicacionesGeografica objUbicacion = ubicacionGeograficaFacade.buscarPorId(Integer.valueOf(provinciaId));
						cargaTrabajo.setProvincia(objUbicacion);
					}
					// seteo el tipo de sector seleccionado 
					if(!tipoSectorId.isEmpty() ){
						CatalogoGeneral objCatalogoSector = catalogoGeneralFacade.obtenerCatalogoXId(Integer.valueOf(tipoSectorId));
						cargaTrabajo.setTipoSector(objCatalogoSector);
					}
					// seteo el tipo de servicio seleccionado 
					if(!tipoServicioId.isEmpty() ){
						CatalogoGeneral objCatalogoServicio = catalogoGeneralFacade.obtenerCatalogoXId(Integer.valueOf(tipoServicioId));
						cargaTrabajo.setTipoServicio(objCatalogoServicio);
					}
					// seteo la operadora
					if(!operadoraId.isEmpty() ){
						CatalogoGeneral objCatalogoOp = catalogoGeneralFacade.obtenerCatalogoXId(Integer.valueOf(operadoraId));
						cargaTrabajo.setOperadora(objCatalogoOp);
					}else{
						cargaTrabajo.setOperadora(null);
					}
					// seteo el bloque seleccionado 
					if(!bloqueId.isEmpty() ){
						CatalogoGeneral objCatalogoOp = catalogoGeneralFacade.obtenerCatalogoXId(Integer.valueOf(bloqueId));
						cargaTrabajo.setBloque(objCatalogoOp);  
					}
					// seteo a null las fechas de obligaciones si no estan seleccionadas las obligaciones
					if (!cargaTrabajo.isAuditoria()) cargaTrabajo.setFechaAuditoria(null);
					if (!cargaTrabajo.isInforme()) cargaTrabajo.setFechaInforme(null);
					if (!cargaTrabajo.isMonitoreo()) cargaTrabajo.setFechaMonitoreo(null);
					if (!cargaTrabajo.isTdr()) cargaTrabajo.setFechaTdr(null);
					// seteo a null la provincia si es a nivel nacional
					if(cargaTrabajo.isNivelNacional())cargaTrabajo.setProvincia(null);
					// guardo el historico antes de guardar
					if(cargaTrabajo.getId() != null){
						guardarHistorico();
					}
					cargaTrabajoFacade.guardar(cargaTrabajo);
					// si no tiene codigo de tramite genero uno y actualizo
					if(cargaTrabajo.getCodigoTramite() == null || cargaTrabajo.getCodigoTramite().isEmpty() || ( cargaTrabajoSeleccionado != null && !cargaTrabajo.getTipoServicio().equals(cargaTrabajoSeleccionado.getTipoServicio())) ){
						cargaTrabajoFacade.guardarCodigotramite(cargaTrabajo.getId());
					}
					// guardar revision
					if(cargaTrabajo.getId() > 0 && cargaTrabajoRevision != null){
						// seteo el tipo de tramite seleccionado 
						if(!tipoTramiteId.isEmpty() ){
							CatalogoGeneral objCatalogoTramite = catalogoGeneralFacade.obtenerCatalogoXId(Integer.valueOf(tipoTramiteId));
							cargaTrabajoRevision.setTipoTramite(objCatalogoTramite);
						}
						// seteo la meta seleccionada 
						if(!metaId.isEmpty() ){
							CatalogoGeneral objCatalogoMesta = catalogoGeneralFacade.obtenerCatalogoXId(Integer.valueOf(metaId));
							cargaTrabajoRevision.setMeta(objCatalogoMesta);
						}
						cargaTrabajoRevision.setCargaTrabajo(cargaTrabajo);
						// guardo historico de la revison antes ade actualizar los datos
						if(cargaTrabajoRevision.getId() != null){
							guardarHistoricoRevision();
						}
						cargaTrabajoFacade.guardarRevision(cargaTrabajoRevision);
						/// para actualizar el estado del tramite si la revision es aprobada
						if("A".equals(cargaTrabajoRevision.getPronunciamiento()) ){
							cargaTrabajo.setEstadoTramite(cargaTrabajoRevision.getPronunciamiento());
							cargaTrabajoFacade.guardar(cargaTrabajo);
						}
					}
					mostrarFormulario = false;
					cargarListaTrabajos();
				}
			}
		}
	}

	private void datosUpperCase(){
		if(cargaTrabajo.getNombre() != null && !cargaTrabajo.getNombre().isEmpty()) cargaTrabajo.setNombre(cargaTrabajo.getNombre().toUpperCase());
		if(cargaTrabajo.getNumeroResolucion() != null && !cargaTrabajo.getNumeroResolucion().isEmpty()) cargaTrabajo.setNumeroResolucion(cargaTrabajo.getNumeroResolucion().toUpperCase());
		if(cargaTrabajo.getRemitente() != null && !cargaTrabajo.getRemitente().isEmpty()) cargaTrabajo.setRemitente(cargaTrabajo.getRemitente().toUpperCase());
		if(cargaTrabajo.getAsunto() != null && !cargaTrabajo.getAsunto().isEmpty()) cargaTrabajo.setAsunto(cargaTrabajo.getAsunto().toUpperCase());
		if(cargaTrabajo.getCodigo() != null && !cargaTrabajo.getCodigo().isEmpty()) cargaTrabajo.setCodigo(cargaTrabajo.getCodigo().toUpperCase());
		if(cargaTrabajoRevision != null){
			if(cargaTrabajoRevision.getDocumentoEntrada() != null && !cargaTrabajoRevision.getDocumentoEntrada().isEmpty()) cargaTrabajoRevision.setDocumentoEntrada(cargaTrabajoRevision.getDocumentoEntrada().toUpperCase());
			if(cargaTrabajoRevision.getDocumentoSalida() != null && !cargaTrabajoRevision.getDocumentoSalida().isEmpty()) cargaTrabajoRevision.setDocumentoSalida(cargaTrabajoRevision.getDocumentoSalida().toUpperCase());
			if(cargaTrabajoRevision.getObservacion() != null && !cargaTrabajoRevision.getObservacion().isEmpty()) cargaTrabajoRevision.setObservacion(cargaTrabajoRevision.getObservacion().toUpperCase());
		}
	}
	
	public void guardarHistorico() throws ServiceException {
		boolean guardarHistorico = false;
		if(cargaTrabajoSeleccionado.isExisteCodigoSuia() != cargaTrabajo.isExisteCodigoSuia()
				|| !cargaTrabajoSeleccionado.getUnidadAdministrativaId().equals(cargaTrabajo.getUnidadAdministrativaId())
				|| !cargaTrabajoSeleccionado.getPrioridad().equals(cargaTrabajo.getPrioridad())
				|| !cargaTrabajoSeleccionado.getNombre().equals(cargaTrabajo.getNombre())
				|| !cargaTrabajoSeleccionado.getAsunto().equals(cargaTrabajo.getAsunto())
				|| !cargaTrabajoSeleccionado.getEstadoTramite().equals(cargaTrabajo.getEstadoTramite())
				|| !cargaTrabajoSeleccionado.getTipoSector().getId().equals(cargaTrabajo.getTipoSector().getId())
				|| cargaTrabajoSeleccionado.isNivelNacional() != cargaTrabajo.isNivelNacional()
				|| cargaTrabajoSeleccionado.isAuditoria() != cargaTrabajo.isAuditoria()
				|| cargaTrabajoSeleccionado.isInforme() != cargaTrabajo.isInforme()
				|| cargaTrabajoSeleccionado.isMonitoreo() != cargaTrabajo.isMonitoreo()
				|| cargaTrabajoSeleccionado.isTdr() != cargaTrabajo.isTdr()
				){
			guardarHistorico = true;
		}
		if(!guardarHistorico && cargaTrabajoSeleccionado.getRemitente() != null && cargaTrabajo.getRemitente() != null){
			if (!cargaTrabajoSeleccionado.getRemitente().equals(cargaTrabajo.getRemitente())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getRemitente() != null && !(cargaTrabajo.getRemitente() != null) ){
			guardarHistorico = true;
		}
		//getNumeroResolucion
		if(!guardarHistorico && cargaTrabajoSeleccionado.getNumeroResolucion() != null && cargaTrabajo.getNumeroResolucion() != null){
			if (!cargaTrabajoSeleccionado.getNumeroResolucion().equals(cargaTrabajo.getNumeroResolucion())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getNumeroResolucion() != null && !(cargaTrabajo.getNumeroResolucion() != null) ){
			guardarHistorico = true;
		}
		if(!guardarHistorico && cargaTrabajoSeleccionado.getOperadora() != null && cargaTrabajo.getOperadora() != null){
			if (!cargaTrabajoSeleccionado.getOperadora().equals(cargaTrabajo.getOperadora())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getOperadora() != null && !(cargaTrabajo.getOperadora() != null) ){
			guardarHistorico = true;
		}
		if(!guardarHistorico && cargaTrabajoSeleccionado.getProvincia() != null && cargaTrabajo.getProvincia() != null){
			if (!cargaTrabajoSeleccionado.getProvincia().equals(cargaTrabajo.getProvincia())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getProvincia() != null && !(cargaTrabajo.getProvincia() != null) ){
			guardarHistorico = true;
		}
		if(!guardarHistorico && cargaTrabajoSeleccionado.getBloque() != null && cargaTrabajo.getBloque() != null){
			if (!cargaTrabajoSeleccionado.getBloque().equals(cargaTrabajo.getBloque())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getBloque() != null && !(cargaTrabajo.getBloque() != null) ){
			guardarHistorico = true;
		}
		//getProyectoId
		if(!guardarHistorico && cargaTrabajoSeleccionado.getProyectoId() != null && cargaTrabajo.getProyectoId() != null){
			if (!cargaTrabajoSeleccionado.getProyectoId().equals(cargaTrabajo.getProyectoId())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getProyectoId() != null && !(cargaTrabajo.getProyectoId() != null) ){
			guardarHistorico = true;
		}
		//getFechaResolucion
		if(!guardarHistorico && cargaTrabajoSeleccionado.getFechaResolucion() != null && cargaTrabajo.getFechaResolucion() != null){
			if (!cargaTrabajoSeleccionado.getFechaResolucion().equals(cargaTrabajo.getFechaResolucion())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getFechaResolucion() != null && !(cargaTrabajo.getFechaResolucion() != null) ){
			guardarHistorico = true;
		}
		//fecha auditoria
		if(!guardarHistorico && cargaTrabajoSeleccionado.getFechaAuditoria() != null && cargaTrabajo.getFechaAuditoria() != null){
			if (!cargaTrabajoSeleccionado.getFechaAuditoria().equals(cargaTrabajo.getFechaAuditoria())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getFechaAuditoria() != null && !(cargaTrabajo.getFechaAuditoria() != null) ){
			guardarHistorico = true;
		}
		//fecha informe
		if(!guardarHistorico && cargaTrabajoSeleccionado.getFechaInforme() != null && cargaTrabajo.getFechaInforme() != null){
			if (!cargaTrabajoSeleccionado.getFechaInforme().equals(cargaTrabajo.getFechaInforme())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getFechaInforme() != null && !(cargaTrabajo.getFechaInforme() != null) ){
			guardarHistorico = true;
		}
		// fecha monitoreo
		if(!guardarHistorico && cargaTrabajoSeleccionado.getFechaMonitoreo() != null && cargaTrabajo.getFechaMonitoreo() != null){
			if (!cargaTrabajoSeleccionado.getFechaMonitoreo().equals(cargaTrabajo.getFechaMonitoreo())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getFechaMonitoreo() != null && !(cargaTrabajo.getFechaMonitoreo() != null) ){
			guardarHistorico = true;
		}
		// fecha tdr 
		if(!guardarHistorico && cargaTrabajoSeleccionado.getFechaTdr() != null && cargaTrabajo.getFechaTdr() != null){
			if (!cargaTrabajoSeleccionado.getFechaTdr().equals(cargaTrabajo.getFechaTdr())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getFechaTdr() != null && !(cargaTrabajo.getFechaTdr() != null) ){
			guardarHistorico = true;
		}
		//getCodigo
		if(!guardarHistorico && cargaTrabajoSeleccionado.getCodigo() != null && cargaTrabajo.getCodigo() != null){
			if (!cargaTrabajoSeleccionado.getCodigo().equals(cargaTrabajo.getCodigo())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoSeleccionado.getCodigo() != null && !(cargaTrabajo.getCodigo() != null) ){
			guardarHistorico = true;
		}
		
		if(guardarHistorico){
			cargaTrabajoFacade.guardarCargaTrabajohistorico(cargaTrabajo.getId());
		}
	}

	public void guardarHistoricoRevision() throws ServiceException {
		boolean guardarHistorico = false;
		if(cargaTrabajoRevisionSeleccionado.getAnioMeta() != null && !cargaTrabajoRevisionSeleccionado.getAnioMeta().equals(cargaTrabajoRevision.getAnioMeta())
				){
			guardarHistorico = true;
		}
		if(cargaTrabajoRevisionSeleccionado.getMeta() != null 
				&& !cargaTrabajoRevisionSeleccionado.getMeta().equals(cargaTrabajoRevision.getMeta())
				){
			guardarHistorico = true;
		}
		if(	cargaTrabajoRevisionSeleccionado.getEstadoRevision() != null 
				&& !cargaTrabajoRevisionSeleccionado.getEstadoRevision().equals(cargaTrabajoRevision.getEstadoRevision())
				){
			guardarHistorico = true;
		}
		if(	cargaTrabajoRevisionSeleccionado.getEstadoRevision() != null 
				&& !cargaTrabajoRevisionSeleccionado.getTipoTramite().equals(cargaTrabajoRevision.getTipoTramite())
				){
			guardarHistorico = true;
		}
		if(!guardarHistorico && cargaTrabajoRevisionSeleccionado.getDocumentoEntrada() != null && cargaTrabajoRevision.getDocumentoEntrada() != null){
			if (!cargaTrabajoRevisionSeleccionado.getDocumentoEntrada().equals(cargaTrabajoRevision.getDocumentoEntrada())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoRevisionSeleccionado.getDocumentoEntrada() != null && !(cargaTrabajoRevision.getDocumentoEntrada() != null) ){
			guardarHistorico = true;
		}
		// documento salida
		if(!guardarHistorico && cargaTrabajoRevisionSeleccionado.getDocumentoSalida() != null && cargaTrabajoRevision.getDocumentoSalida() != null){
			if (!cargaTrabajoRevisionSeleccionado.getDocumentoSalida().equals(cargaTrabajoRevision.getDocumentoSalida())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoRevisionSeleccionado.getDocumentoSalida() != null && !(cargaTrabajoRevision.getDocumentoSalida() != null) ){
			guardarHistorico = true;
		}
		//pronunciamineto
		if(!guardarHistorico && cargaTrabajoRevisionSeleccionado.getPronunciamiento() != null && cargaTrabajoRevision.getPronunciamiento() != null){
			if (!cargaTrabajoRevisionSeleccionado.getPronunciamiento().equals(cargaTrabajoRevision.getPronunciamiento())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoRevisionSeleccionado.getPronunciamiento() != null && !(cargaTrabajoRevision.getPronunciamiento() != null) ){
			guardarHistorico = true;
		}
		// plazo
		if(!guardarHistorico && cargaTrabajoRevisionSeleccionado.getPlazo() != null && cargaTrabajoRevision.getPlazo() != null){
			if (!cargaTrabajoRevisionSeleccionado.getPlazo().equals(cargaTrabajoRevision.getPlazo())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoRevisionSeleccionado.getPlazo() != null && !(cargaTrabajoRevision.getPlazo() != null) ){
			guardarHistorico = true;
		}
		// quejas
		if(!guardarHistorico && cargaTrabajoRevisionSeleccionado.getQuejas() != null && cargaTrabajoRevision.getQuejas() != null){
			if (!cargaTrabajoRevisionSeleccionado.getQuejas().equals(cargaTrabajoRevision.getQuejas())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoRevisionSeleccionado.getQuejas() != null && !(cargaTrabajoRevision.getQuejas() != null) ){
			guardarHistorico = true;
		}
		// capacitaciones
		if(!guardarHistorico && cargaTrabajoRevisionSeleccionado.getCapacitaciones() != null && cargaTrabajoRevision.getCapacitaciones() != null){
			if (!cargaTrabajoRevisionSeleccionado.getCapacitaciones().equals(cargaTrabajoRevision.getCapacitaciones())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoRevisionSeleccionado.getCapacitaciones() != null && !(cargaTrabajoRevision.getCapacitaciones() != null) ){
			guardarHistorico = true;
		}
		//getObservacion
		if(!guardarHistorico && cargaTrabajoRevisionSeleccionado.getObservacion() != null && cargaTrabajoRevision.getObservacion() != null){
			if (!cargaTrabajoRevisionSeleccionado.getObservacion().equals(cargaTrabajoRevision.getObservacion())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoRevisionSeleccionado.getObservacion() != null && !(cargaTrabajoRevision.getObservacion() != null) ){
			guardarHistorico = true;
		}
		//fecha entrada documento
		if(!guardarHistorico && cargaTrabajoRevisionSeleccionado.getFechaEntrada() != null && cargaTrabajoRevision.getFechaEntrada() != null){
			if (!cargaTrabajoRevisionSeleccionado.getFechaEntrada().equals(cargaTrabajoRevision.getFechaEntrada())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoRevisionSeleccionado.getFechaEntrada() != null && !(cargaTrabajoRevision.getFechaEntrada() != null) ){
			guardarHistorico = true;
		}
		//fecha salida documento
		if(!guardarHistorico && cargaTrabajoRevisionSeleccionado.getFechaSalida() != null && cargaTrabajoRevision.getFechaSalida() != null){
			if (!cargaTrabajoRevisionSeleccionado.getFechaSalida().equals(cargaTrabajoRevision.getFechaSalida())){
				guardarHistorico = true;
			}
		}
		if( !guardarHistorico &&  cargaTrabajoRevisionSeleccionado.getFechaSalida() != null && !(cargaTrabajoRevision.getFechaSalida() != null) ){
			guardarHistorico = true;
		}
		
		if(guardarHistorico){
			cargaTrabajoFacade.guardarCargaTrabajohistoricoRevision(cargaTrabajoRevision.getId());
		}
	}

	private void cargarDatosIniciales(CargaTrabajo objCargaTrabajo1){
		cargaTrabajoSeleccionado = new CargaTrabajo();
		cargaTrabajoSeleccionado.setId((objCargaTrabajo1.getId() != null)?objCargaTrabajo1.getId():null);
		cargaTrabajoSeleccionado.setUnidadAdministrativaId((objCargaTrabajo1.getUnidadAdministrativaId() != null)?objCargaTrabajo1.getUnidadAdministrativaId():null);
		cargaTrabajoSeleccionado.setProyectoId((objCargaTrabajo1.getProyectoId() != null)?objCargaTrabajo1.getProyectoId():null);
		cargaTrabajoSeleccionado.setPrioridad((objCargaTrabajo1.getPrioridad() != null)?objCargaTrabajo1.getPrioridad():null);
		cargaTrabajoSeleccionado.setCodigo((objCargaTrabajo1.getCodigo() != null)?objCargaTrabajo1.getCodigo():null);
		cargaTrabajoSeleccionado.setNombre((objCargaTrabajo1.getNombre() != null)?objCargaTrabajo1.getNombre():null);
		cargaTrabajoSeleccionado.setNumeroResolucion((objCargaTrabajo1.getNumeroResolucion() != null)?objCargaTrabajo1.getNumeroResolucion():null);
		cargaTrabajoSeleccionado.setFechaResolucion((objCargaTrabajo1.getFechaResolucion() != null)?objCargaTrabajo1.getFechaResolucion():null);
		cargaTrabajoSeleccionado.setBloque((objCargaTrabajo1.getBloque() != null)?objCargaTrabajo1.getBloque():null);
		cargaTrabajoSeleccionado.setRemitente((objCargaTrabajo1.getRemitente() != null)?objCargaTrabajo1.getRemitente():null);
		cargaTrabajoSeleccionado.setAsunto((objCargaTrabajo1.getAsunto() != null)?objCargaTrabajo1.getAsunto():null);
		cargaTrabajoSeleccionado.setEstadoTramite((objCargaTrabajo1.getEstadoTramite() != null)?objCargaTrabajo1.getEstadoTramite():null);
		cargaTrabajoSeleccionado.setExisteCodigoSuia(objCargaTrabajo1.isExisteCodigoSuia());
		cargaTrabajoSeleccionado.setUsuario((objCargaTrabajo1.getUsuario() != null)?objCargaTrabajo1.getUsuario():null);
		cargaTrabajoSeleccionado.setProvincia((objCargaTrabajo1.getProvincia() != null)?objCargaTrabajo1.getProvincia():null);
		cargaTrabajoSeleccionado.setOperadora((objCargaTrabajo1.getOperadora() != null)?objCargaTrabajo1.getOperadora():null);
		cargaTrabajoSeleccionado.setTipoSector((objCargaTrabajo1.getTipoSector() != null)?objCargaTrabajo1.getTipoSector():null);
		cargaTrabajoSeleccionado.setTipoServicio((objCargaTrabajo1.getTipoServicio() != null)?objCargaTrabajo1.getTipoServicio():null);
		cargaTrabajoSeleccionado.setNivelNacional(objCargaTrabajo1.isNivelNacional());
		
		cargaTrabajoSeleccionado.setAuditoria(objCargaTrabajo1.isAuditoria());
		cargaTrabajoSeleccionado.setInforme(objCargaTrabajo1.isInforme());
		cargaTrabajoSeleccionado.setMonitoreo(objCargaTrabajo1.isMonitoreo());
		cargaTrabajoSeleccionado.setTdr(objCargaTrabajo1.isTdr());
		cargaTrabajoSeleccionado.setFechaAuditoria((objCargaTrabajo1.getFechaAuditoria() != null)?objCargaTrabajo1.getFechaAuditoria():null);
		cargaTrabajoSeleccionado.setFechaInforme((objCargaTrabajo1.getFechaInforme() != null)?objCargaTrabajo1.getFechaInforme():null);
		cargaTrabajoSeleccionado.setFechaMonitoreo((objCargaTrabajo1.getFechaMonitoreo() != null)?objCargaTrabajo1.getFechaMonitoreo():null);
		cargaTrabajoSeleccionado.setFechaTdr((objCargaTrabajo1.getFechaTdr() != null)?objCargaTrabajo1.getFechaTdr():null);
	}

	private void cargarDatosInicialesRevision(CargaTrabajoRevision objCargaTrabajoRevision1){
		cargaTrabajoRevisionSeleccionado = new CargaTrabajoRevision();
		cargaTrabajoRevisionSeleccionado.setId((objCargaTrabajoRevision1.getId() != null)?objCargaTrabajoRevision1.getId():null);
		cargaTrabajoRevisionSeleccionado.setAnioMeta((objCargaTrabajoRevision1.getAnioMeta() != null)?objCargaTrabajoRevision1.getAnioMeta():null);
		cargaTrabajoRevisionSeleccionado.setMeta((objCargaTrabajoRevision1.getMeta() != null)?objCargaTrabajoRevision1.getMeta():null);
		cargaTrabajoRevisionSeleccionado.setEstadoRevision((objCargaTrabajoRevision1.getEstadoRevision() != null)?objCargaTrabajoRevision1.getEstadoRevision():null);
		cargaTrabajoRevisionSeleccionado.setDocumentoEntrada((objCargaTrabajoRevision1.getDocumentoEntrada() != null)?objCargaTrabajoRevision1.getDocumentoEntrada():null);
		cargaTrabajoRevisionSeleccionado.setDocumentoSalida((objCargaTrabajoRevision1.getDocumentoSalida() != null)?objCargaTrabajoRevision1.getDocumentoSalida():null);
		cargaTrabajoRevisionSeleccionado.setTipoTramite((objCargaTrabajoRevision1.getTipoTramite() != null)?objCargaTrabajoRevision1.getTipoTramite():null);
		cargaTrabajoRevisionSeleccionado.setPronunciamiento((objCargaTrabajoRevision1.getPronunciamiento() != null)?objCargaTrabajoRevision1.getPronunciamiento():null);
		cargaTrabajoRevisionSeleccionado.setPlazo((objCargaTrabajoRevision1.getPlazo() != null)?objCargaTrabajoRevision1.getPlazo():null);
		cargaTrabajoRevisionSeleccionado.setQuejas((objCargaTrabajoRevision1.getQuejas() != null)?objCargaTrabajoRevision1.getQuejas():null);
		cargaTrabajoRevisionSeleccionado.setCapacitaciones((objCargaTrabajoRevision1.getCapacitaciones() != null)?objCargaTrabajoRevision1.getCapacitaciones():null);
		cargaTrabajoRevisionSeleccionado.setObservacion((objCargaTrabajoRevision1.getObservacion() != null)?objCargaTrabajoRevision1.getObservacion():null);
		
		cargaTrabajoRevisionSeleccionado.setFechaEntrada((objCargaTrabajoRevision1.getFechaEntrada() != null)?objCargaTrabajoRevision1.getFechaEntrada():null);
		cargaTrabajoRevisionSeleccionado.setFechaSalida((objCargaTrabajoRevision1.getFechaSalida() != null)?objCargaTrabajoRevision1.getFechaSalida():null);
	}
	
	public void seleccionarCargaTrabajo(CargaTrabajo objCargaTrabajo, String tipo) throws ServiceException{
		cargaTrabajoRevision = null;
		mostrarBtnAgragar = false;
		editarDatos = true;
		datosPendientes="";
		metaId = "";
		tipoTramiteId = "";
		tipoSectorId = "";
		tipoServicioId = "";
		operadoraId = "";
		provinciaId = "";
		bloqueId = "";
		cargarDatosIniciales(cargaTrabajo);
		if("editar".equals(tipo)){
			mostrarFormulario = true;
			editarFormulario = true;
		}else{
			mostrarFormulario = false;
			editarFormulario = false;
		}
        esHidrocarburos = false;
        cargaTrabajo = null;
		cargaTrabajo = objCargaTrabajo;
		// cargo las revisiones
		listaCargarTrabajoRevision = cargaTrabajoFacade.listarCargarTrabajoRevisiones(cargaTrabajo.getId());
		if (listaCargarTrabajoRevision.size() > 0){
			// para habilitar el boton agregar
			if(listaCargarTrabajoRevision.get(listaCargarTrabajoRevision.size() -1 ).getEstadoRevision().equals("T")
					&& listaCargarTrabajoRevision.get(listaCargarTrabajoRevision.size() -1 ).getPronunciamiento() != null
					&& !listaCargarTrabajoRevision.get(listaCargarTrabajoRevision.size() -1 ).getPronunciamiento().isEmpty()
					&& listaCargarTrabajoRevision.get(listaCargarTrabajoRevision.size() -1 ).getPronunciamiento().equals("O") ){
				mostrarBtnAgragar = true;
			}
			// para deshabilitar la edision de los campos
			for (CargaTrabajoRevision lista : listaCargarTrabajoRevision) {
				if(!lista.getEstadoRevision().equals("P")){
					editarDatos = false;
				}
				break;
			}
		}

		if (!cargaTrabajo.isExisteCodigoSuia()){
			existeProvincia = false;
		}else{
			if(cargaTrabajo.isExisteCodigoSuia() && cargaTrabajo.getProyectoId() != null && cargaTrabajo.getProyectoId() > 0){
				presentacinFisica = false;
				ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorId(cargaTrabajo.getProyectoId());
				if(proyecto !=null){
					cargaDatosProyecto(proyecto);
				}
			}
			// si es de 4 categorias
			if(cargaTrabajo.isExisteCodigoSuia() && cargaTrabajo.getProyectoId() != null && cargaTrabajo.getProyectoId() == 0){
				presentacinFisica = false;
				ProyectoLicenciamientoAmbiental proyecto = cargaTrabajoFacade.obtenerProyecto4CategoriasPorCodigo(cargaTrabajo.getCodigo());
				if(proyecto !=null){
					cargaDatosProyecto4Categorias(proyecto);
				}
			}
		}
		if (objCargaTrabajo.getTipoSector() != null){
			tipoSectorId = objCargaTrabajo.getTipoSector().getId().toString();
			llenarSubCatalogo(tipoSectorId);
			mostrarBloque();
			if (objCargaTrabajo.getBloque() != null){
				bloqueId = objCargaTrabajo.getBloque().getId().toString();
			}
		}
		if (objCargaTrabajo.getTipoServicio() != null){
			tipoServicioId = objCargaTrabajo.getTipoServicio().getId().toString();
			mostrarObligaciones = CODIGOS_OBLIGACIONES.contains(tipoServicioId);
		}
		if (objCargaTrabajo.getOperadora() != null){
			operadoraId = objCargaTrabajo.getOperadora().getId().toString();
		}
		if (objCargaTrabajo.getProvincia() != null){
			provinciaId = objCargaTrabajo.getProvincia().getId().toString();
		} else if(cargaTrabajo.isNivelNacional()){
			existeProvincia = false;
		}
		if (objCargaTrabajo.getBloque() != null){
			bloqueId = objCargaTrabajo.getBloque().getId().toString();
			// verifico si es de hidrocarburos
	//		if(objCargaTrabajo.getBloque().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)){
				esHidrocarburos = true;
//			}
		}
	}

	public void nuevo(){
		cargaTrabajo = null;
		datosPendientes="";
		listaCargarTrabajoRevision = new ArrayList<CargaTrabajoRevision>();
		cargaTrabajo = new CargaTrabajo();
		cargaTrabajoRevision = new CargaTrabajoRevision();
		cargaTrabajo.setNivelNacional(false);
		cargaTrabajoSeleccionado = new CargaTrabajo();
    	proyecto = new ProyectoLicenciamientoAmbiental();
		cargaTrabajo.setNombre("");
		limpiarCampos();
		metaId ="";
		tipoTramiteId ="";
		tipoSectorId ="";
		tipoServicioId ="";
		provinciaId ="";
		operadoraId ="";
		bloqueId = "";
    	cargaTrabajo.setFechaRegistro(new Date());
    	cargaTrabajo.setEstadoTramite("P");
        cargarDatosUsuario();
        mostrarFormulario = true;
        esHidrocarburos = false;;
        existeProvincia = false;;
		existeResolucion = false;
		existeFechaResolucion = false;
        presentacinFisica = true;
        mostrarBtnAgragar = false;
        editarDatos = true;
	}

	public void cancelar(){
        mostrarFormulario = false;
        editarFormulario = true;
        cargaTrabajoRevision = null;
        mostrarBtnAgragar = false;
        editarDatos = true;
		//cargarListaTrabajos();
        ///refresh();
	}

	public void refresh() {
	    FacesContext context = FacesContext.getCurrentInstance();
	    Application application = context.getApplication();
	    ViewHandler viewHandler = application.getViewHandler();
	    UIViewRoot viewRoot = viewHandler.createView(context, context.getViewRoot().getViewId());
	    context.setViewRoot(viewRoot);
	    context.renderResponse(); 
	 }

	public void limpiar(){
		nuevo();
        mostrarFormulario = true;
	}

	public void regresar(){
        mostrarFormulario = false;
        editarFormulario = true;
	}

	public void limpiarForm(){
		if(datosPendientes.equals("Si")){
			JsfUtil.redirectTo("/cargaTrabajo/listadoTrabajos.jsf?nuevo=0");
		}
		nuevo();
		//cancelar();
		regresar();
	}

	public void editarRevision(CargaTrabajoRevision revision){
		cargaTrabajoRevision = revision;
		cargarDatosInicialesRevision(revision);
		if (cargaTrabajoRevision.getTipoTramite() != null){
			tipoTramiteId = cargaTrabajoRevision.getTipoTramite().getId().toString();
		}
		if (cargaTrabajoRevision.getMeta() != null){
			metaId = cargaTrabajoRevision.getMeta().getId().toString();
		}
	}

	public void cancelarRevision(){
		cargaTrabajoRevision = null;
	}

	public void nuevaRevision(){
		cargaTrabajoRevision = new CargaTrabajoRevision();
	}

	public void inicializarValores(){
		cargaTrabajoRevision.setPlazo(null);
	}
	
	public void prueba (){
		if(datosPendientes.isEmpty())
			datosPendientes="No";
		else if(datosPendientes.equals("No"))
			datosPendientes="Si";
	}
}
