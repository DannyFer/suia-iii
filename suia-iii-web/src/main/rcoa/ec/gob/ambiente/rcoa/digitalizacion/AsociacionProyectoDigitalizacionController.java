package ec.gob.ambiente.rcoa.digitalizacion;

import index.ContienePoligono_entrada;
import index.ContienePoligono_resultado;
import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
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
import org.primefaces.model.LazyDataModel;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AutorizacionesAdministrativasFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.CoordenadaDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.LicenciaAmbientalFisicaFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProyectoAsociadoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ShapeDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.CoordenadaDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.ShapeDigitalizacion;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.FormaPuntoRecuperacion;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AsociacionProyectoDigitalizacionController {
	
	Logger LOG = Logger.getLogger(AsociacionProyectoDigitalizacionController.class);
	
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private AutorizacionesAdministrativasFacade autorizacionesAdministrativasFacade;
	@EJB
	private LicenciaAmbientalFisicaFacade licenciaAmbientalFisicaFacade;
	@EJB
	private ProyectoAsociadoDigitalizacionFacade proyectoAsociadoFacade;
	@EJB
    private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private ShapeDigitalizacionFacade shapeDigitalizacionFacade;
	@EJB
	private CoordenadaDigitalizacionFacade coordenadaDigitalizacionFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
	private DatosOperadorRgdBean datosOperadorRgdBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{autorizacionAdministrativaAmbientalBean}")
	private AutorizacionAdministrativaAmbientalBean autorizacionAdministrativaAmbientalBean;
	
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativa;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativaAmbiental> listaAutorizacionPrincipal;
	
	@Getter
	@Setter
	private Boolean tieneAsociacion;

	@Getter
	@Setter
	private LazyDataModel<AutorizacionAdministrativa> listaProyectosSuiaLazy;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativa> listaProyectosSuiaFilter;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativa> listaProyectosSeleccionados;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativa> listaOtrosProyectos;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativa> listaOtrosProyectosSuiaFilter;
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativa> listaOtrosProyectosSeleccionados;
	
	@Getter
	@Setter
	private boolean esTecnico;
	
	@Getter
	@Setter
	private String mensajeCI, codigoProyecto4Cat;
	
	private Integer totalProyectosAsociados; 		
	
	@PostConstruct
	public void init(){
		try {
			esTecnico = true;
			for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
				if (areaUser.getArea().getAreaName().contains("PROPONENTE")) {
					datosOperadorRgdBean.buscarDatosOperador(JsfUtil.getLoggedUser());
					esTecnico = false;
					break;
				}
			}
			autorizacionAdministrativa =(AutorizacionAdministrativaAmbiental)(JsfUtil.devolverObjetoSession("autorizacionAdministrativa"));

			listaAutorizacionPrincipal = new ArrayList<AutorizacionAdministrativaAmbiental>();
			
			listaAutorizacionPrincipal.add(autorizacionAdministrativa);

			autorizacionAdministrativa = listaAutorizacionPrincipal.get(0);

			List<ProyectoAsociadoDigitalizacion> listaDigitalizacion = proyectoAsociadoFacade.buscarProyectosAsociados(autorizacionAdministrativa.getId());
			if(listaDigitalizacion == null)
				totalProyectosAsociados = 0;
			else
				totalProyectosAsociados = listaDigitalizacion.size();
					
			listaProyectosSeleccionados = new ArrayList<AutorizacionAdministrativa>();
			listaOtrosProyectosSeleccionados = new ArrayList<AutorizacionAdministrativa>();
			
			listaOtrosProyectos = new ArrayList<AutorizacionAdministrativa>();
		} catch (Exception e) {
			LOG.error("Error al recuperar la información", e);
		}
	}
	
	public void mostrarTramites(){
		try {
			Integer idPrincipal=null;
			if(listaAutorizacionPrincipal != null && listaAutorizacionPrincipal.size() > 0)
				idPrincipal = listaAutorizacionPrincipal.get(0).getId();
			
			listaProyectosSuiaLazy = new LazyProyectosDigitalizacionDataModel(idPrincipal, esTecnico);
			if(listaAutorizacionPrincipal != null && listaAutorizacionPrincipal.size() > 0)
				idPrincipal = listaAutorizacionPrincipal.get(0).getId();
		} catch (Exception e) {
			LOG.error("Error al cargar los proyectos", e);
		}
	}
	
	public boolean seleccionarTramite(AutorizacionAdministrativa autorizacionSeleccionada){
		try {
			boolean estaVinculado = false;
			//valido que el proyecto no este ya asociado 
			List<ProyectoAsociadoDigitalizacion> listaDigitalizacion= new ArrayList<ProyectoAsociadoDigitalizacion>();
			if(autorizacionSeleccionada.getIdDigitalizacion() != null)
				listaDigitalizacion = proyectoAsociadoFacade.buscarProyectoAsociado(autorizacionSeleccionada.getIdDigitalizacion());
			else if(autorizacionSeleccionada.getId() != null)
				listaDigitalizacion = proyectoAsociadoFacade.buscarProyectoAsociado(autorizacionSeleccionada.getId());
			if(listaDigitalizacion != null && listaDigitalizacion.size() > 0){
				for (ProyectoAsociadoDigitalizacion proyectoAsociadoDigitalizacion : listaDigitalizacion) {
					estaVinculado = true;
					break;
				}
			}
			return estaVinculado;
		} catch (Exception e) {
			LOG.error("Ocurrio un error al añadir el proyecto", e);
			return false;
		}
	}
	
	public void quitarTramite(AutorizacionAdministrativa autorizacionSeleccionada){
		try {
			if(autorizacionSeleccionada.getFuente().equals("3") || autorizacionSeleccionada.getFuente().equals("4")){
				autorizacionSeleccionada.setSeleccionadoSecundario(false);
				listaOtrosProyectosSeleccionados.remove(autorizacionSeleccionada);
			}else{
				autorizacionSeleccionada.setSeleccionadoSecundario(false);
				listaProyectosSeleccionados.remove(autorizacionSeleccionada);
			}
		} catch (Exception e) {
			LOG.error("Ocurrio un error al eliminar el proyecto", e);
		}
	}
	
	public void buscarOtrasAutorizaciones() {
		listaOtrosProyectos = new ArrayList<AutorizacionAdministrativa>();
		List<AutorizacionAdministrativa> pryArt = autorizacionesAdministrativasFacade.getProyectosAprobacionRequisitosTecnicosDig(null, autorizacionAdministrativa.getIdentificacionUsuario());
		if(pryArt != null)
			listaOtrosProyectos.addAll(pryArt);
		
		List<AutorizacionAdministrativa> pryRgd = autorizacionesAdministrativasFacade.getProyectosGeneradorDesechosPeligrososDig(autorizacionAdministrativa.getIdentificacionUsuario());
		if(pryRgd != null)
			listaOtrosProyectos.addAll(pryRgd);
		/**
		 * Falta ver como se asociaría con RSQ
		 */
//		List<AutorizacionAdministrativa> pryRsq = autorizacionesAdministrativasFacade.getProyectosGeneradorDesechosPeligrosos(autorizacionAdministrativa.getIdentificacionUsuario());
//		if(pryRsq != null){
//			listaOtrosProyectos.addAll(pryRsq);
//		}
	}
	
	public void redirigirNuevo(){
		autorizacionAdministrativaAmbientalBean.setEsRegistroNuevo(true);
		autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaPrincipal(autorizacionAdministrativa);
		if(listaProyectosSeleccionados != null && listaProyectosSeleccionados.size() > 0){
			siguiente();
		}else
			JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacion.jsf");
	}	
	
	public void cargarTramites(){
		if(tieneAsociacion){
			mostrarTramites();
			buscarOtrasAutorizaciones();
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("PF('tableProyectosSuiaLazy').clearFilters()");
			RequestContext.getCurrentInstance().update("form:pnlProyectosSuiaLazy:tableProyectosSuiaLazy");
			requestContext.execute("PF('tableOtrosProyectos').clearFilters()");
			RequestContext.getCurrentInstance().update("form:tableOtrosProyectos");
		}
	}
	
	public void cancelar(){
		listaProyectosSeleccionados = new ArrayList<AutorizacionAdministrativa>();
		listaOtrosProyectosSeleccionados = new ArrayList<AutorizacionAdministrativa>();
		listaOtrosProyectos = new ArrayList<AutorizacionAdministrativa>();
		setTieneAsociacion(null);
		autorizacionAdministrativaAmbientalBean.setListaProyectosSeleccionados(new ArrayList<AutorizacionAdministrativa>());
	}
	
	public void siguiente(){
		boolean proyectosPorCompletar = false;
		boolean otrosProyectos = false;
		Integer contarProyecto=0; 
		if(totalProyectosAsociados >= 3){
			JsfUtil.addMessageError("Solo se permite hasta 3 vinculaciones.");
			return;
		}
		if(tieneAsociacion == null){
			JsfUtil.addMessageError("Debe indicar si tiene resoluciones derivadas o asociadas a esta Autorización Administrativa Ambiental.");
			return;
		}
		if(!tieneAsociacion){
			RequestContext context = RequestContext.getCurrentInstance();
        	context.execute("PF('mdlFinalizacion').show();");
		}else{
			if(listaProyectosSeleccionados != null && !listaProyectosSeleccionados.isEmpty()){
				proyectosPorCompletar = true;
				contarProyecto = listaProyectosSeleccionados.size(); 
			}
			
			if(listaOtrosProyectosSeleccionados != null && !listaOtrosProyectosSeleccionados.isEmpty()){
				otrosProyectos = true;
				contarProyecto += listaOtrosProyectosSeleccionados.size();
			}
			if((totalProyectosAsociados + contarProyecto) > 3){
				JsfUtil.addMessageError("Solo se permite hasta 3 vinculaciones, ya tiene vinculado "+contarProyecto.toString()+" proyectos.");
				return;
			}
			
			if(!proyectosPorCompletar && !otrosProyectos){
				JsfUtil.addMessageError("Debe seleccionar al menos un proyecto para asociar.");
				return;
			}
			List<AutorizacionAdministrativa> listaProyectosSeleccionadosCI = new ArrayList<AutorizacionAdministrativa>();
			for (AutorizacionAdministrativa aaaAux : listaProyectosSeleccionados) {
				if(!validarProyecto(aaaAux)){
					JsfUtil.addMessageError("El proyecto "+aaaAux.getCodigo()+" no se puede vincular porque se encuentra en proceso de digitalización con otro usuario.");
					return;
				}
				if(seleccionarTramite(aaaAux)){
					JsfUtil.addMessageError("El proyecto "+aaaAux.getCodigo()+" ya se encuentra vinculado.");
					return;
				}
				// verifico si es actualizacion de certificado de intrerseccion
				if(aaaAux.getEstado().toUpperCase().contains("CERTIFICADO DE INTERSEC")){
					listaProyectosSeleccionadosCI.add(aaaAux);
				}
				if(aaaAux.getFuente().equals("3") || aaaAux.getFuente().equals("4")){
					aaaAux.setSeleccionadoSecundario(true);
				}else{
					aaaAux.setSeleccionadoSecundario(true);
				}
			}
			for (AutorizacionAdministrativa aaaAux : listaOtrosProyectosSeleccionados) {
				if(seleccionarTramite(aaaAux)){
					JsfUtil.addMessageError("El proyecto "+aaaAux.getCodigo()+" ya se encuentra vinculado.");
					return;
				}
				// si es RGD valido que las coordenadas esten dentro de las coordenadas de digitalizacion
				if(aaaAux.getFuente().equals("4")){
					boolean coordenadaVvalidas = validarRGDCoordenadas(aaaAux.getId());
					// si las coordenadas no estan dentro no pasa
					if(!coordenadaVvalidas){
						JsfUtil.addMessageError("Las coordenadas del RGD "+ aaaAux.getCodigo() +" no están dentro de las coordenadas del proyecto digitalizado.");
						return;
					}
				}
				if(aaaAux.getFuente().equals("3") || aaaAux.getFuente().equals("4")){
					aaaAux.setSeleccionadoSecundario(true);
				}else{
					aaaAux.setSeleccionadoSecundario(true);
				}
			}
			
			if(otrosProyectos){
				guardarOtrosProyectos();
			}
			// elimino los certificado de interseccion de la lista de seleccionados
			if(listaProyectosSeleccionadosCI.size() > 0){
				listaProyectosSeleccionados.removeAll(listaProyectosSeleccionadosCI);
				guardarProyectosUpdateCI(listaProyectosSeleccionadosCI);
			}else{
				continuarDigitralizacionProyectosseleccionados();
			}
		}
	}
	
	private boolean validarRGDCoordenadas(Integer codigoRGD){
		String coodenadasPuntoVerificacion="";
		boolean esPoligono = true, correcto = false;
		Integer tipoFormaPoligono=3;//poligono area geografica
		try{
			GeneradorDesechosPeligrosos objGenerador = registroGeneradorDesechosFacade.cargarGeneradorParaDocumentoPorId(codigoRGD);
			// verifico si el punto de verificacion esta dentro del area geografica modificada
			if(objGenerador != null && objGenerador.getId() > 0){
	    		List<PuntoRecuperacion> listaPuntos = registroGeneradorDesechosFacade.cargarPuntosPorIdGenerador(objGenerador.getId());
	    		if(listaPuntos == null || listaPuntos.size() == 0){
	    			return correcto;
	    		}
				for (PuntoRecuperacion puntoRecuperacion : listaPuntos){
					correcto = false;
					for (FormaPuntoRecuperacion forma : puntoRecuperacion.getFormasPuntoRecuperacion()){
						coodenadasPuntoVerificacion="";
						esPoligono=forma.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO);
						for (Coordenada objCoordenada : forma.getCoordenadas()) {
							coodenadasPuntoVerificacion += (coodenadasPuntoVerificacion == "") ? BigDecimal.valueOf(objCoordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(objCoordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString() : ","+BigDecimal.valueOf(objCoordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(objCoordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString();
						}
						String coodenadasgeograficas="";
						
						//busco coordenadas cargadas automaticamente
						List<ShapeDigitalizacion> objShape = shapeDigitalizacionFacade.obtenerShapePorSistema(autorizacionAdministrativa.getId(), 1, true);
						// si no existe coordenadas cargadas automaticamente busco coordenadas ingresadas manualmente
						if(objShape == null || objShape.size() == 0){
							objShape = shapeDigitalizacionFacade.obtenerShapePorSistema(autorizacionAdministrativa.getId(), 2, true);
						}
						Integer tipoformaId=3;
						if(objShape != null && objShape.size() > 0){
							for (ShapeDigitalizacion shapeAux : objShape) {
								if(shapeAux.getTipoForma() != null)
									tipoformaId = shapeAux.getTipoForma().getId();
								List<CoordenadaDigitalizacion> coordenadas = coordenadaDigitalizacionFacade.obtenerCoordenadas(shapeAux.getId());
								coodenadasgeograficas="";
								if(coordenadas.size() == 1)
									tipoformaId = TipoForma.TIPO_FORMA_PUNTO;
								for (CoordenadaDigitalizacion coordenada : coordenadas) {
									coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString() : ","+coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString();
								}
								correcto = validarCoordenadas(tipoformaId, esPoligono, coodenadasgeograficas, coodenadasPuntoVerificacion);
								if(correcto)
									break;
							}
						}
					}
				}
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		return correcto;
	}

	/**
	 * valida si las coordenadas ingresads estan dentro del area de implantacion
	 * @param tipoFormaPoligono forma del poligono contenedor
	 * @param poligonoTipo forma del poligono contenido
	 * @param coordenadasArea  coordenadas del poligono contenedor (area geografica o implantacion)
	 * @param coordenadasingresadas
	 * @return
	 */
	public boolean validarCoordenadas(Integer tipoFormaPoligono, boolean poligonoTipo, String coordenadasArea, String coordenadasingresadas){
		try{
			boolean estaDentro = false;
	        SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
	        ws.setEndpoint(Constantes.getInterseccionesWS());
			if(tipoFormaPoligono.equals(TipoForma.TIPO_FORMA_POLIGONO)){
				if(poligonoTipo){
					ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada(); //verifica que el poligono este contenida dentro de la ubicación geográfica
	        		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
	        		verificarGeoImpla.setXy1(coordenadasArea);
	        		verificarGeoImpla.setXy2(coordenadasingresadas);
	        		ContieneZona_resultado[]intRestGeoImpl;
	        		intRestGeoImpl=ws.contieneZona(verificarGeoImpla);
	        		if (intRestGeoImpl[0].getInformacion().getError() != null) {
	        			
	           		}else if (intRestGeoImpl[0].getContieneCapa().getValor().equals("t")){
	           			estaDentro=true;
	           		}
				}else{
					ContienePoligono_entrada verificarGeoImpla = new ContienePoligono_entrada(); //verifica que el punto este contenida dentro de la ubicación geográfica o de implantacion
	        		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
	        		verificarGeoImpla.setTipo("pu");
	        		verificarGeoImpla.setXy1(coordenadasArea);
	        		verificarGeoImpla.setXy2(coordenadasingresadas);
	        		ContienePoligono_resultado[]intRestGeoImpl;
	        		intRestGeoImpl=ws.contienePoligono(verificarGeoImpla);
	        		if (intRestGeoImpl[0].getInformacion().getError() != null) {
	           		}else if (intRestGeoImpl[0].getContienePoligono().getValor().equals("t")){
	           			estaDentro=true;
	              	}
				}
			}else{
    			if (coordenadasArea.equals(coordenadasingresadas)){
    				estaDentro = true;
    			}else if (coordenadasArea.contains(coordenadasingresadas)){
    				estaDentro = true;
    			}
			}
			return estaDentro;
		}catch(RemoteException e){
			return false;
		}
	}
	
	public void continuarDigitralizacionProyectosseleccionados(){
		boolean proyectosPorCompletar = false;
		if(listaProyectosSeleccionados != null && listaProyectosSeleccionados.size() > 0){
			proyectosPorCompletar = true;
		}
		if(proyectosPorCompletar){
			autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaPrincipal(autorizacionAdministrativa);
			// verifico si ya se tiene algun registro guardado del proyecto
			for (AutorizacionAdministrativa objAAA : listaProyectosSeleccionados) {
				AutorizacionAdministrativaAmbiental aaa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorIdProyecto(objAAA.getId(), Integer.valueOf(objAAA.getFuente()));
				if(aaa != null && aaa.getId() != null){
					objAAA.setCodigo(aaa.getCodigoProyecto());
				}
			}
			autorizacionAdministrativaAmbientalBean.setListaProyectosSeleccionados(listaProyectosSeleccionados);
			autorizacionAdministrativaAmbientalBean.setAutorizacionFisicaSeleccionada(null);
			autorizacionAdministrativaAmbientalBean.setAutorizacionAdministrativaSeleccionada(null);
			if(autorizacionAdministrativaAmbientalBean.getEsRegistroNuevo() != null && autorizacionAdministrativaAmbientalBean.getEsRegistroNuevo()){
				JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacion.jsf");
			}else{
				autorizacionAdministrativaAmbientalBean.setEsRegistroNuevo(false);
				RequestContext context = RequestContext.getCurrentInstance();
		        context.execute("PF('siguienteDlgDigitalizacion').show();");
			}
		}else{
			RequestContext context = RequestContext.getCurrentInstance();
        	context.execute("PF('mdlFinalizacion').show();");
		}
	}
	
	public void continuar(){
		boolean proyectosPorCompletar = false;
		List<AutorizacionAdministrativa> listaEliminar = new ArrayList<AutorizacionAdministrativa>();
		for (AutorizacionAdministrativa objAAA : listaProyectosSeleccionados) {
			if(objAAA.getDigitalizado()){
				guardarProyectoAsociadoCompletado(objAAA);
				listaEliminar.add(objAAA);
			}
		}
		if(listaEliminar.size() > 0){
			listaProyectosSeleccionados.removeAll(listaEliminar);
		}
		if(listaProyectosSeleccionados != null && listaProyectosSeleccionados.size() > 0){
			proyectosPorCompletar = true;
		}
		if(proyectosPorCompletar){
			//JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoInformacionAAA.jsf");
			JsfUtil.redirectTo("/pages/rcoa/digitalizacion/ingresoFormDigitalizacion.jsf");
		}else{
			RequestContext context = RequestContext.getCurrentInstance();
        	context.execute("PF('mdlFinalizacion').show();");
		}
	}
	
	public void continuarSiguiente(){
		RequestContext context = RequestContext.getCurrentInstance();
		listaProyectosSeleccionados.remove(0);
		autorizacionAdministrativaAmbientalBean.setListaProyectosSeleccionados(listaProyectosSeleccionados);
		context.update("form:pnlPadre");
		context.update("pnlPadre");
		if(listaProyectosSeleccionados.size() > 0){
        	context.execute("PF('siguienteDlgDigitalizacion').show();");
		}else{
        	context.execute("PF('siguienteDlgDigitalizacion').hide();");
		}
		context.update("form:pnlPadre");
		context.update("form:pnlProyectosSuia");
		context.update("form:pnlPadre:pnlProyectosSuia");
		context.update("form:tableProyectosSuia");
		context.update("tableProyectosSuia");
	}
	
	private boolean validarProyecto(AutorizacionAdministrativa autorizacion){
		boolean valido = true;
		if(autorizacion.getEstado().equals("Por completar")){
			AutorizacionAdministrativaAmbiental autorizacionAux = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(autorizacion.getCodigo());
			if(autorizacionAux != null){
				if(!autorizacionAux.getUsuarioCreacion().equals(loginBean.getUsuario().getNombre())){
					return false;
				}
			}
		}
		return valido;
	}
	
	private void guardarOtrosProyectos(){
		try {
			for(AutorizacionAdministrativa otroProyecto : listaOtrosProyectosSeleccionados){
				//valido que el proyecto no este ya asociado 
				List<ProyectoAsociadoDigitalizacion> listaDigitalizacion= proyectoAsociadoFacade.buscarProyectoAsociado(otroProyecto.getId());
				if(listaDigitalizacion != null && listaDigitalizacion.size() > 0){
					JsfUtil.addMessageInfo("El proyecto "+otroProyecto.getCodigo()+" ya se encuentra asociado");
					continue;
				}
				ProyectoAsociadoDigitalizacion proyectoAsociado = new ProyectoAsociadoDigitalizacion();
				proyectoAsociado.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
				proyectoAsociado.setProyectoAsociadoId(otroProyecto.getId());
				proyectoAsociado.setCodigo(otroProyecto.getCodigo());
				String fuente = otroProyecto.getFuente();
				proyectoAsociado.setTipoProyecto(Integer.valueOf(fuente));
				switch (fuente) {
				case "4":
					proyectoAsociado.setNombreTabla("suia_iii.hazardous_wastes_generators");
					break;
				case "3":
					proyectoAsociado.setNombreTabla("suia_iii.approval_technical_requirements");
					break;
				case "5":
					proyectoAsociado.setNombreTabla("coa_digitalization_linkage.environmental_administrative_authorizations");	
					break;
				default:
					break;
				}
				proyectoAsociadoFacade.guardar(proyectoAsociado, loginBean.getUsuario());
			}
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			LOG.error("Error al guardar otros proyectos (RGD, RSQ, ART)", e);
		}
	}

	
	private void guardarProyectosUpdateCI(List<AutorizacionAdministrativa> listaProyectosSeleccionadosCI){
		try {
			for (AutorizacionAdministrativa aaaACI : listaProyectosSeleccionadosCI) {
				mensajeCI="";
				//valido que el proyecto no este ya asociado 
				List<ProyectoAsociadoDigitalizacion> listaDigitalizacion= proyectoAsociadoFacade.buscarProyectoAsociadoPorCodigo(aaaACI.getCodigo());
				if(listaDigitalizacion != null && listaDigitalizacion.size() > 0){
					JsfUtil.addMessageInfo("El proyecto "+aaaACI.getCodigo()+" ya se encuentra asociado");
					continue;
				}
				mensajeCI = "Favor indicar si este proyecto "+aaaACI.getCodigo()+" corresponde a una"
						+ " Actualización de Certificado de Intersección de la Autorización Administrativa Ambiental ";
				codigoProyecto4Cat = aaaACI.getCodigo();
				RequestContext context = RequestContext.getCurrentInstance();
	        	context.execute("PF('certificadoInterseccion').show();");
			}
		} catch (Exception e) {
			LOG.error("Error al guardar otros proyectos (RGD, RSQ, ART)", e);
		}
	}
	
	public void guardarAsociacion4cat(String codigo4Cat){    	
		ProyectoAsociadoDigitalizacion proyectoAsociado = new ProyectoAsociadoDigitalizacion();
		proyectoAsociado.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
		proyectoAsociado.setCodigo(codigo4Cat);
		proyectoAsociado.setSistemaOriginal(2);
		proyectoAsociado.setNombreTabla("4Categorias");
		proyectoAsociadoFacade.guardar(proyectoAsociado, loginBean.getUsuario());
		continuarDigitralizacionProyectosseleccionados();
	}
	
	private void guardarProyectoAsociadoCompletado(AutorizacionAdministrativa objAAA){
		ProyectoAsociadoDigitalizacion proyectoAsociado = new ProyectoAsociadoDigitalizacion();
		proyectoAsociado.setAutorizacionAdministrativaAmbiental(autorizacionAdministrativa);
		proyectoAsociado.setProyectoAsociadoId(objAAA.getId());
		proyectoAsociado.setSistemaOriginal(4);
		String sistema = autorizacionAdministrativa.getSistema().toString();
		proyectoAsociado.setSistemaOriginal(Integer.valueOf(sistema));//0 nuevo
		proyectoAsociado.setNombreTabla("coa_digitalization_linkage.environmental_administrative_authorizations");
		proyectoAsociadoFacade.guardar(proyectoAsociado, loginBean.getUsuario());
		AutorizacionAdministrativaAmbiental autorizacionAux = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(objAAA.getCodigo());
		if(autorizacionAux != null && autorizacionAux.getId() != null){
			autorizacionAux.setAutorizacionAdministrativa(autorizacionAdministrativa);
			autorizacionAdministrativaAmbientalFacade.save(autorizacionAdministrativa, loginBean.getUsuario());
		}
	}
	
	public void cancelarAsociacion(){
		return;
	}
	
	public void redireccionarProyectos(){
			JsfUtil.redirectTo("/pages/rcoa/digitalizacion/digitalizacionAAA.jsf");
	}
	
	public void redireccionarBandeja(){
			JsfUtil.redirectToBandeja();
	}
}
