package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ActividadSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DeclaracionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.PermisoDeclaracionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DeclaracionSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.PermisoDeclaracionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.bandeja.controllers.BandejaTareasController;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.SustanciaQuimicaPeligrosaFacade;
import ec.gob.ambiente.suia.domain.Holiday;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DeclaracionSeleccionarRSQController {
	
	private static final Logger LOG = Logger.getLogger(DeclaracionSeleccionarRSQController.class);
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;
	    
    /*EJBs*/    
    @EJB
    private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;    
    @EJB
    private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;    
    @EJB
    private DeclaracionSustanciaQuimicaFacade declaracionSustanciaQuimicaFacade;    
    @EJB
    private CatalogoCoaFacade catalogoCoaFacade;
    @EJB
    private ActividadSustanciaQuimicaFacade actividadSustanciaQuimicaFacade;
    @EJB
    private FeriadosFacade feriadoFacade;
    @EJB
    private SustanciaQuimicaPeligrosaFacade sustanciaQuimicaFacade;
    @EJB
    private PermisoDeclaracionRSQFacade permisoDeclaracionRSQFacade;
    
    /*List*/
    @Getter
	@Setter
	private List<RegistroSustanciaQuimica> registroSustanciaQuimicaLista;
	
	@Getter
	@Setter
	private List<DeclaracionSustanciaQuimica> listaDeclaraciones;
			
	@Getter
	@Setter
    private List<CatalogoGeneralCoa> listaMeses;
	
	/*Object*/	
	@Getter
	@Setter
	private RegistroSustanciaQuimica rsqSeleccionado;
	
	@Getter
	@Setter
	private DeclaracionSustanciaQuimica declaracionSustanciaQuimica;
	
	@Getter
	@Setter
	private List<Integer> listaAnios;
//	
//	@Getter
//	@Setter
//	private Boolean mostrarDeclaraciones, agregarNuevo;
	
	@Getter
	@Setter
	private int mes, anio, mesesAtrasados;
	
	@Getter
	@Setter
	private List<Integer> listaMesesADeclarar;
	
	@Getter
	@Setter
	private boolean mostrarListadoRSQ = true;
	
	@Getter
	@Setter
	private boolean tienePermisos = false;
	
	@Getter
	@Setter
	private String mensaje = "Estimado Operador, se le habilitará esta opción los 10 primeros días hábiles del siguiente mes de reporte.";
	
	@Getter
	@Setter
	private String mensajePermiso = "Estimado Operador, no tiene permisos para realizar declaraciones de sustancias químicas.";
	
	@Getter
	@Setter
	private List<PermisoDeclaracionRSQ> listaSustanciasQuimicas;
	
	@Getter
	@Setter
	private boolean permisosActuales = true;
	
	
    @PostConstruct
	public void init(){		
		try {
			JsfUtil.cargarObjetoSession("idRSQ",null);
			
			listaSustanciasQuimicas = new ArrayList<PermisoDeclaracionRSQ>();
			
			obtenerRSQFinalizados();
						
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}
    
    private void obtenerRSQFinalizados() {
    	
    	Calendar fechaActual= Calendar.getInstance();
    	int mes = fechaActual.get(Calendar.MONTH);
    	int anio = fechaActual.get(Calendar.YEAR);
    	/**
    	 * Si el mes es cero significa que es enero y debe declarar de diciembre del año anterior.
    	 */
    	if(mes == 0){
    		mes = 12;
    		anio = anio - 1;
    	}
              
        /**
         * Verificar permisos para realizar declaraciones
         */
        List<RegistroSustanciaQuimica> registrosSustanciasPermisos = new ArrayList<>();
        registrosSustanciasPermisos = registroSustanciaQuimicaFacade.obtenerRegistrosPorUsuarioPermisoAnio(anio, JsfUtil.getLoggedUser()); 
        
        /**
         * Si no se encuentran registros de sustancias químicas con permisos en el año en el cual se busca entonces se realiza una búsqueda con el año 
         * anterior para saber si tiene registros con permisos.
         */
        if(registrosSustanciasPermisos == null || registrosSustanciasPermisos.isEmpty()){
        	permisosActuales = false;
        	anio = anio -1;
        	mes = 12;
        	registrosSustanciasPermisos = registroSustanciaQuimicaFacade.obtenerRegistrosPorUsuarioPermisoAnio(anio, JsfUtil.getLoggedUser()); 
        }        
        
        /**
         * si no existe ningún permiso entonces no puede realizar declaraciones
         */
         if(registrosSustanciasPermisos != null && !registrosSustanciasPermisos.isEmpty()){
        	 tienePermisos = true;        	 
         }else
        	 tienePermisos = false;
         
         registroSustanciaQuimicaLista=new ArrayList<RegistroSustanciaQuimica>();
         
         if(tienePermisos){
         	/**
         	 * la lista muestra los registro de sustancias químicas que pueden realizar la declaración desde el mes actual 
         	 * En está consulta  también se considera si el operador tiene autorización para realizar declaraciones.
         	 */
     		List<RegistroSustanciaQuimica> registroSustanciaQuimicaListTemp = registroSustanciaQuimicaFacade
     				.obtenerRegistrosPorUsuarioPermisoAnioMes(anio, mes, JsfUtil.getLoggedUser());
     		
     		/**
     		 * Recoremos la lista de los RSQ obtenidos para conocer si ya tiene la declaracion de las sustancias por el mes y anio actual
     		 */
     		List<DeclaracionSustanciaQuimica> listaDeclaracionesPendiente = new ArrayList<>();
     		for (RegistroSustanciaQuimica item : registroSustanciaQuimicaListTemp) {
     						
     			/**
     			 * Se obtiene el id de las sustancias químicas que tiene el rsq
     			 */     	
     			listaDeclaracionesPendiente = new ArrayList<>();
     			List<DeclaracionSustanciaQuimica> declaracionesAnteriores = new ArrayList<>();
     			List<Integer> lista = permisoDeclaracionRSQFacade.obtenerIdSustanciasPorRegistro(item);
     			List<Integer> listaAux = new ArrayList<Integer>();
     			listaAux.addAll(lista);
     			
     			if(!lista.isEmpty()){
     				/**
     				 * Se consulta el número de declaraciones que tiene por anio mes y sustancias químicas
     				 */     				
     				List<DeclaracionSustanciaQuimica> declaraciones = declaracionSustanciaQuimicaFacade.buscarPorSustancia(item,lista, anio, mes);				
     				     				
     				/**
     				 * Si son iguales entonces se realizó todas las declaraciones de todas las sustancias del mes.
     				 * Sino son iguales entonces no se realizó todas las declaraciones y debe seguir mostrando.
     				 */
     				     				
     				if(declaraciones.size() < lista.size()){
     					if(!registroSustanciaQuimicaLista.contains(item)){
     						
     						for(DeclaracionSustanciaQuimica dec : declaraciones){
     							if(lista.contains(dec.getSustanciaQuimica().getId())){
     								listaAux.remove(dec.getSustanciaQuimica().getId());
     							}
     						}
     						item.setListaSustancia(new ArrayList<SustanciaQuimicaPeligrosa>());
     						for(Integer id : listaAux){
     							SustanciaQuimicaPeligrosa sus = sustanciaQuimicaFacade.buscarSustanciaQuimicaPorId(id);
     							if(sus != null){
     								item.getListaSustancia().add(sus);
     							}
     							
     						}
     						
     						registroSustanciaQuimicaLista.add(item);
     					}     					
     				}    				    				
     			}				
     		}	
         }
    			
		/**
		 * Si no existe registros del anio, mes y sustancia entonces se debe ir al formulario de empresas activas para ingresar RSQ
		 */
		if(registroSustanciaQuimicaLista != null && !registroSustanciaQuimicaLista.isEmpty()){
			mostrarListadoRSQ = true;
		}else{
			mostrarListadoRSQ = false;
			if(!permisosActuales)
				tienePermisos = false;
		}
    }
    
	public void mostrarSustanciasQuimicas(RegistroSustanciaQuimica registro) {
		try {
			listaSustanciasQuimicas = new ArrayList<PermisoDeclaracionRSQ>();
			Calendar fechaActual= Calendar.getInstance();
	    	int mes = fechaActual.get(Calendar.MONTH);
	    	
	    	if(mes == 0){
	    		mes = 12;
	    	}
									
			for(SustanciaQuimicaPeligrosa sus : registro.getListaSustancia()){
				List<PermisoDeclaracionRSQ> permisos = permisoDeclaracionRSQFacade.obtenerSustanciasPorRSQSustancia(registro, sus);
				if(permisos != null && !permisos.isEmpty()){
					if(permisos.get(0).getMes() <= mes){
						listaSustanciasQuimicas.add(permisos.get(0));
					}
				}
			}
			
			if(listaSustanciasQuimicas.size() > 1){
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('sustanciasDialog').show();");
			}else{
				seleccionarRegistro(registro, listaSustanciasQuimicas.get(0));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean validarInicioDeclaracion(RegistroSustanciaQuimica item, PermisoDeclaracionRSQ permiso, int anio, int mes){
		try {
			
 			List<Integer> lista = new ArrayList<>();
 			lista.add(permiso.getSustanciaQuimica().getId());
 			
 			List<DeclaracionSustanciaQuimica> declaraciones = new ArrayList<DeclaracionSustanciaQuimica>();
 			
 			if(!lista.isEmpty()){
 				/**
 				 * Se consulta el número de declaraciones que tiene por anio mes y sustancias químicas
 				 */     				
 				declaraciones = declaracionSustanciaQuimicaFacade.buscarPorSustancia(item,lista, anio, mes);
 			}
			
     		if(declaraciones.isEmpty()){
     			return false;
     		}else{
     			return true;
     		}			
			
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return false;
	}
	
	public boolean validarDeclaracionPendiente(RegistroSustanciaQuimica registro, SustanciaQuimicaPeligrosa sustancia, Integer anio){
		try {			
			
			List<DeclaracionSustanciaQuimica> declaracionesAnteriores = declaracionSustanciaQuimicaFacade.buscarDeclaracionesFaltantesPorRegistroSustanciaAnio(registro,sustancia, anio);	
			
			if(!declaracionesAnteriores.isEmpty()){
				return true;
			}else
				return false;				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
    
	public void seleccionarRegistro(RegistroSustanciaQuimica obj, PermisoDeclaracionRSQ permiso) {
		try {			
			
			if(validarDeclaracionPendiente(obj, permiso.getSustanciaQuimica(), permiso.getAnio())){
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('mensajeDialog').show();");
				return;
			}			

			rsqSeleccionado = registroSustanciaQuimicaFacade.obtenerRegistroPorId(obj.getId());
			// mostrarDeclaraciones = true;
			// agregarNuevo = false;

//			 Calendar fechaActual = Calendar.getInstance();
//			 fechaActual.set(Calendar.DATE, 1);
//			 fechaActual.set(Calendar.MONTH, 0);
//			 fechaActual.set(Calendar.YEAR, 2023);

			/**
			 * Obtengo la fecha actual del sistema  para setear el día con el primer día de mes
			 */
			Calendar fechaActual = Calendar.getInstance();
			int mesActual = fechaActual.get(Calendar.MONTH);
			int anioActual = fechaActual.get(Calendar.YEAR);

			Calendar inicioMes = Calendar.getInstance();
			inicioMes.set(Calendar.DATE, 1);
			inicioMes.set(Calendar.MONTH, mesActual);
			inicioMes.set(Calendar.YEAR, anioActual);

			/**
			 * Se obtiene la fecha límite para que el operador pueda realizar la declaración sin la necesidad de un pago
			 * se cuenta solo 10 días laborables
			 */
			CatalogoGeneralCoa valorDias = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.DSQ_DIAS_DECLARACION, 1);
    		
    		int dias = Integer.valueOf(valorDias.getValor());
			
			Date fechaLimite = fechaFinal(inicioMes.getTime(), dias);

			/**
			 * Obtengo las declaraciones del RSQ por la sustancia seleccionada
			 */
			listaDeclaraciones = declaracionSustanciaQuimicaFacade.obtenerPorRSQSustancia(obj, permiso.getSustanciaQuimica());
			
			
			if (listaDeclaraciones.isEmpty()) {
				/**
				 * DECLARACIÓN INICIAL DEL ANIO
				 * Para
				 * igualar con el último mes de año como en enero el valor
				 * es 0 colocaremos como mes actual 12 para que si debía
				 * declarar en diciembre y estamos en enero no realice la
				 * tarea de pago. El MESACTUAL tendrá el valor del calendar
				 * para los meses
				 * */
				if (mesActual == 0) {
					mesActual = 12;
					anioActual = anioActual - 1;
				}
												
				/** Valida fecha límite del mes y dentro del método se valida si además tiene meses atrasados */
				if (fechaActual.getTime().after(fechaLimite)) {
					calcularPago(rsqSeleccionado, null, mesActual, true, permiso, anioActual);
				} else {
					/**
					 * Esta parte es cuando se tiene meses atrasados pero no la fecha, dentro del método realiza el cálculo
					 */
					if (permiso.getMes() < mesActual) {
						calcularPago(rsqSeleccionado, null, mesActual, false, permiso, anioActual);
					}
				}
			} else {
				
				int indexUltimaD = listaDeclaraciones.size() - 1;

				if (mesActual == 0) {
					mesActual = 12;
					anioActual = anioActual - 1;
				}
												
				/**
				 * Si el mes actual es igual al ultimo mes de la declaracion + 1 entonces quiere decir que solo realiza pago si paso la fecha limite.
				 */
				if (mesActual == listaDeclaraciones.get(indexUltimaD).getMesDeclaracion() + 1) {
					if (fechaActual.getTime().after(fechaLimite)) {
						calcularPago(rsqSeleccionado, listaDeclaraciones.get(indexUltimaD), mesActual, true, permiso, anioActual);
					}
				} else {
					/**
					 * Si el mes actual no es igual entonces realiza pago de los meses atrasados incluido el mes por sobrepasar la fecha límite
					 * 
					 */
					if (fechaActual.getTime().after(fechaLimite)) {
						calcularPago(rsqSeleccionado,listaDeclaraciones.get(indexUltimaD),mesActual, true, permiso, anioActual);
					} else {
						/**
						 * Sola paga los meses porque todavía se encuentra dentro de la fecha límite
						 */
						calcularPago(rsqSeleccionado,listaDeclaraciones.get(indexUltimaD),mesActual, false, permiso, anioActual);
					}
				}
			}
			
			if (anioActual > permiso.getAnio()) {
				mesActual = 12;
				anioActual = anioActual - 1;
			}
			
			if(validarInicioDeclaracion(obj, permiso, anioActual, mesActual)){
				JsfUtil.addMessageWarning("La declaración del mes, año y sustancia ya ha sido registrada");
				return ;
			}
			
			agregarDeclaracion(permiso);
			iniciarDeclaracion(mesActual, anioActual, permiso);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public String styleBtnRSQ(RegistroSustanciaQuimica item) {    	
    	return rsqSeleccionado!=null && rsqSeleccionado.equals(item)?"primary":"secondary";
    }
    
    public void agregarDeclaracion(PermisoDeclaracionRSQ permiso) {	
		
		declaracionSustanciaQuimica = new DeclaracionSustanciaQuimica(rsqSeleccionado);
		declaracionSustanciaQuimica.setPagoPendiente(false);
		declaracionSustanciaQuimica.setSustanciaQuimica(permiso.getSustanciaQuimica());
	}
    
    public void calcularPago(RegistroSustanciaQuimica registro, DeclaracionSustanciaQuimica declaracion, Integer mesActual, boolean fechaLimite, PermisoDeclaracionRSQ permiso, Integer anioActual){
    	try {
    		listaMesesADeclarar = new ArrayList<>();
    		boolean declaracionPendiente = false;
    		mesesAtrasados = 0;
    		/**
    		 * Si la declaración es null significa que es la primera declaración del año.
    		 */
    		if(declaracion == null){
    			/**
    			 * Si todavía tiene pemisos del año anterior se debe hacer pagar los meses que no declaro el año anterior
    			 */
    			if(anioActual > permiso.getAnio()){
    				mesActual = 12;  
    				declaracionPendiente = true;
    				fechaLimite = false; //se coloca en false ya que como tiene declaraciones pendientes del anterior año no debemos tomar en cuenta la fecha.
    			}
    			
    			/**
    			 * se realiza esta resta porque es la primera declaración que se hace del año por lo que el mes del permiso
    			 * es la referencia que se debe tomar al no tener declaraciones anteriores.
    			 */
    			mesesAtrasados = mesActual - permiso.getMes(); 
    			int mes = permiso.getMes();
				while (mes < (mesActual)) {
					listaMesesADeclarar.add(mes);
					mes++;
				}
    			
    			
    			/**Debido a que la resta cuando se pase la fecha de declaración da cero */
				/** Se iguala a uno los meses atrasados ya el operador no cumplio con la fecha limite para la declaracion */   				
				
				if(fechaLimite){
					if(mesesAtrasados == 0){
    					mesesAtrasados = 1;
    				}else{
    					mesesAtrasados = mesesAtrasados + 1;
    				}
				}		
				
				if(declaracionPendiente){
					mesesAtrasados = mesesAtrasados + 1;
				}
				
    		}else{
    			
    			if(declaracion.getMesDeclaracion() != null){    				
    				/**
        			 * Si todavía tiene pemisos del año anterior se debe hacer pagar los meses que no declaro el año anterior
        			 */
        			if(anioActual > permiso.getAnio()){
	        				
        				/**
        				 * Si el permiso tiene una validez mayor al año actual entonces se debe validar si el anio de declaracion
        				 * es actual o no, ya que si no es actual entonces se debe considerar para el pago el mes 12 y si es diferente
        				 * se debe continuar con el mes actual para poder realizar las declaraciones esto se debe a que se puede tomar la declaración 
        				 * del anterior año para declarar el año actual (pero es una excepción) 
        				 */
        				if(declaracion.getAnioDeclaracion() < anioActual &&  declaracion.getMesDeclaracion() < 12){
        					mesActual = 12;
            				declaracionPendiente = true;
            				fechaLimite = false;
        				}else{
        					/**Está parte está así porque se está considerando que se va a utilizar el mismo rsq del año anterior 
        					 * para seguir declarando el año actual.
        					 * En teoría esto no pasaría ya que se debe activar los permisos cada año, esto es una excepción
        				 	*/
        					if(declaracion.getMesDeclaracion().equals(12)){
        						mesActual = 1; //es uno porque la primera declaración que se realizaría sería en febrero
        					}
        				}
        			}
    				/**
    				 * Si el mes actual es 1 entonces no paga ninguna multa de meses anteriores 
    				 * ya que primero se hace las declaraciones hasta el mes de diciembre del año anterior y una vez realizadas esas declaraciones 
    				 * se pueda hacer la declaración de enero y como es el primer mes no debería tener meses atrasados y ni multa.
    				 */    				
    				if(mesActual > 1){
    					/**se suma uno al mes de la declaración porque si ya se tiene la declaración de ese mes
        				 * se debe tomar el siguiente mes para hacer la declaración es decir el mes que no se hizo la declaración
        				 * NOTA: la variable mes Actual en realidad es el mes a declarar según Calendar ya que se declara mes vencido, 
        				 * el cual es un mes antes del mes actual*/
    					mesesAtrasados = mesActual - (declaracion.getMesDeclaracion() + 1);
    					int mes = declaracion.getMesDeclaracion() + 1;
        				while(mes < (mesActual)){
        					listaMesesADeclarar.add(mes);
        					mes++;
        				} 
    				}
    			}    	
    			
    			/**Debido a que la resta cuando se pase la fecha de declaración da cero */
				/** Se iguala a uno los meses atrasados ya que el operador no cumplio con la fecha limite para la declaracion */
    			if(fechaLimite){
					if(mesesAtrasados == 0){
						mesesAtrasados = 1;
    				}else{
    					mesesAtrasados = mesesAtrasados + 1;
    				}
				}
    			
    			if(declaracionPendiente){
					mesesAtrasados = mesesAtrasados + 1;
				}
    		}	
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
   
    public String getMesNombre(int mesDeclaracion) {
    	return JsfUtil.devuelveMes(mesDeclaracion-1);
	}
    
    private void redireccionar() {
    	JsfUtil.cargarObjetoSession("idDeclaracionSQ",declaracionSustanciaQuimica.getId());
    	JsfUtil.redirectTo("/pages/rcoa/sustanciasQuimicas/declaracion/ingresarInformacion.jsf");
    }
    
    public void iniciarDeclaracion(int mesDeclaracion, int anioDeclaracion, PermisoDeclaracionRSQ permiso) {    	
    	try {    	 	
    		
    		List<DeclaracionSustanciaQuimica> declaracionesAGuardar = new ArrayList<>();
    		
    		CatalogoGeneralCoa valorTasa = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.DSQ_VALOR_TASA, 1);
    		
    		double valorMulta = Double.valueOf(valorTasa.getValor());
    		
    		double multa = 0;
    		if(mesesAtrasados > 0){
    			multa = valorMulta * mesesAtrasados;
    		}
        	
        	CatalogoGeneralCoa estadoRegistro = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.DSQ_ESTADOS_DECLARACION, 1);
        
        	if(listaMesesADeclarar != null){
        		for(Integer meses : listaMesesADeclarar){
            		if(meses != mesDeclaracion){
            			DeclaracionSustanciaQuimica declaracion = new DeclaracionSustanciaQuimica(rsqSeleccionado);
            			declaracion.setSustanciaQuimica(declaracionSustanciaQuimica.getSustanciaQuimica());
            			declaracion.setEstadoDeclaracion(estadoRegistro);        		
            			declaracion.setAnioDeclaracion(anioDeclaracion);
            			declaracion.setMesDeclaracion(meses);
            			declaracion.setPagoPendiente(true);
            			declaracion.setValorMulta(multa);
            			declaracionesAGuardar.add(declaracion);
            		}
            	} 
        	}  
        	
        	declaracionSustanciaQuimica.setEstadoDeclaracion(estadoRegistro);
        	declaracionSustanciaQuimica.setAnioDeclaracion(anioDeclaracion);
        	declaracionSustanciaQuimica.setMesDeclaracion(mesDeclaracion);  
        	if(listaMesesADeclarar != null && (mesesAtrasados > listaMesesADeclarar.size())){
        		declaracionSustanciaQuimica.setPagoPendiente(true);
        		declaracionSustanciaQuimica.setValorMulta(multa);
        	}
        	declaracionesAGuardar.add(declaracionSustanciaQuimica);
        	
        	int i = 0;
        	for(DeclaracionSustanciaQuimica declaracion : declaracionesAGuardar){
        		if(i == 0){
        			if(listaDeclaraciones.isEmpty()){
        				/**
        				 * Si el stock actual tiene algún valor del permiso anterior ese es la cantidad de inicio que se toma en cuenta.
        				 */
            			if(permiso.getStockActual() != null){
            				/**
            				 * Esta validación permite verificar si existió un cambio en el stock inicial del año
            				 * pero solo se valida el primer mes de declaración con el primer mes que se va a declarar 
            				 * si son iguales pero stock que no es igual al stock actual entonces se toma el valor del
            				 * stock que se ingreso en empresas activas.
            				 */
            				if(permiso.getMes().equals(declaracion.getMesDeclaracion()) && permiso.getStock() != permiso.getStockActual()){
            					declaracion.setCantidadInicio(permiso.getStock());
            				}else{
            					declaracion.setCantidadInicio(permiso.getStockActual());
            				}            				
            			}else{
            				declaracion.setCantidadInicio(permiso.getStock());
            			}			
            		}else{
            			declaracion.setCantidadInicio(listaDeclaraciones.get(listaDeclaraciones.size()-1).getCantidadFin());
            		}
        		}
        		i++;
        		
        		declaracionSustanciaQuimicaFacade.guardar(declaracion, JsfUtil.getLoggedUser());
        	}
        	
        	TaskSummaryCustom tarea = declaracionSustanciaQuimicaFacade.iniciarProceso(declaracionesAGuardar.get(0), JsfUtil.getLoggedUser());
        	
        	if(tarea != null){
        		JsfUtil.getBean(BandejaTareasController.class).startTask(tarea);
        	}       	    	
        	
		} catch (Exception e) {
			e.printStackTrace();
		}    	
    	
    }
    
    public void editarDeclaracion(DeclaracionSustanciaQuimica declaracionSustanciaQuimica) {
    	this.declaracionSustanciaQuimica=declaracionSustanciaQuimica;
    	redireccionar();
    }
    
   
    /**
     * Se obtiene la fecha en la que se termina la declaración de rsq sin contar fines de semana y feriados
     * @param fechaInicial
     * @param diasRequisitos
     * @return
     * @throws ServiceException
     */
    private Date fechaFinal(Date fechaInicial, int diasRequisitos) throws ServiceException{
		Date fechaFinal = new Date();		
		Calendar fechaPrueba = Calendar.getInstance();
		fechaPrueba.setTime(fechaInicial);	
		
		int i = 0;
		while(i < diasRequisitos){
			fechaPrueba.add(Calendar.DATE, 1);
			
			Date fechaFeriado = fechaPrueba.getTime();
			
			List<Holiday> listaFeriados = feriadoFacade.listarFeriadosNacionalesPorRangoFechas(fechaFeriado, fechaFeriado);
			
			if(fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){
				if(listaFeriados == null || listaFeriados.isEmpty()){
					i++;
				}					
			}					
		}
		
		fechaFinal = fechaPrueba.getTime();			
		return fechaFinal;	
	}
    

}