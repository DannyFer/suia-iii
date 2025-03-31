package ec.gob.ambiente.control.retce.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AgregarSustanciasQuimicasBean;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.ReporteSustanciasQuimicasPeligrosas;
import ec.gob.ambiente.retce.model.SustanciaQuimicaPeligrosaDetalle;
import ec.gob.ambiente.retce.model.SustanciaQuimicaPeligrosaRetce;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.ReporteSustanciasQuimicasPeligrosasFacade;
import ec.gob.ambiente.retce.services.SustanciaQuimicaPeligrosaDetalleFacade;
import ec.gob.ambiente.retce.services.SustanciaQuimicaPeligrosaRetceFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.SustanciaQuimicaPeligrosaFacade;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class SustanciasQuimicasPeligrosasRetceController {
	
	private final Logger LOG = Logger.getLogger(RevisionInformeOficioGeneradorController.class);
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoFacade;
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	@EJB
	private SustanciaQuimicaPeligrosaFacade sustanciaQuimicaPeligrosaFacade;
	@EJB
	private SustanciaQuimicaPeligrosaRetceFacade sustanciaQuimicaPeligrosaRetceFacade;
	@EJB
	private SustanciaQuimicaPeligrosaDetalleFacade sustanciaQuimicaPeligrosaDetalleFacade;
	@EJB
	private ReporteSustanciasQuimicasPeligrosasFacade reporteSustanciasPeligrosasFacade;
	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Setter
	@Getter
	@ManagedProperty(value = "#{agregarSustanciasQuimicasBean}")
	private AgregarSustanciasQuimicasBean agregarSustanciasQuimicasBean;
	
	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaCatalogoProcesosSustanciaQuimica;
	
	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaCatalogoTipoSustanciaQuimica;
	
	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaCatalogoEstadoFisico;
	
	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaCatalogoEnvaseAlmacenamiento;
	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> listaSustanciasQuimicasPeligrosas;
	
	@Getter
	@Setter
	private Integer idProcesoSustancia, idTipoSustancia, idSustanciaQuimica, idEnvase, idEstadoFisico;
	
	@Getter
	@Setter
	private boolean mostrarMezcla, mostrarOtroEnvase, sustanciaModificada, sustanciaQuimicaRetceModificada, 
					verFormulario, habilitarIngreso;
	
	@Getter
	@Setter
	private SustanciaQuimicaPeligrosaRetce sustanciaQuimicaRetce, sustanciaQuimicaRetceEditada;
	
	@Getter
	@Setter
	private String porcentajeSustanciaEnMezcla;
	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosaDetalle> listaDetallesSustancias, listaDetallesSustanciasEliminar, listaDetallesSustanciasAux;
	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosaRetce> listaSustanciasQuimicasRetce, listaSustanciaMezclaResumen, listaSustanciasQuimicasRetceEliminadas, listaSustanciasQuimicasRetceAux;
	
	@Getter
	@Setter
	private SustanciaQuimicaPeligrosaDetalle detalleSustanciaMezcla, detalleSustanciaMezclaAux;
	
	@Getter
	@Setter
	private ReporteSustanciasQuimicasPeligrosas reporteSustanciasQuimicas;
	
	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	private List<ReporteSustanciasQuimicasPeligrosas> listaReporteSustanciasQuimicas;
	
	@Getter
	@Setter
	private List<InformacionProyecto> informacionProyectoList;
	
	@Getter
	@Setter
	private Integer porcentajeTotal = 0, anteriorValorPorcentaje = 0;
	
	@Getter
	@Setter
	private String representanteLegal, cedulaRepresentanteLegal, mensajeResponsabilidad;
	
	@Getter
	@Setter
	private List<Integer> listaAnios;
		
	@PostConstruct
	public void init(){
		try {
			
			Integer idInformacionBasica =(Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName()));
			if(idInformacionBasica!=null)
			{
				informacionProyecto=informacionProyectoFacade.findById(idInformacionBasica);
				JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), null);
			}else{
				return;
			}
			
			listaCatalogoTipoSustanciaQuimica = new ArrayList<DetalleCatalogoGeneral>();
			
			sustanciaQuimicaRetce = new SustanciaQuimicaPeligrosaRetce();
			listaDetallesSustancias = new ArrayList<SustanciaQuimicaPeligrosaDetalle>();
			listaCatalogoEstadoFisico = new ArrayList<DetalleCatalogoGeneral>();
			listaCatalogoEnvaseAlmacenamiento = new ArrayList<DetalleCatalogoGeneral>();
			listaSustanciasQuimicasRetce = new ArrayList<SustanciaQuimicaPeligrosaRetce>();
			detalleSustanciaMezcla = new SustanciaQuimicaPeligrosaDetalle();
			listaDetallesSustanciasEliminar = new ArrayList<SustanciaQuimicaPeligrosaDetalle>();
			reporteSustanciasQuimicas = new ReporteSustanciasQuimicasPeligrosas();
			listaSustanciasQuimicasRetceEliminadas = new ArrayList<SustanciaQuimicaPeligrosaRetce>();	
			listaSustanciasQuimicasRetceAux = new ArrayList<SustanciaQuimicaPeligrosaRetce>();
			sustanciaQuimicaRetceEditada = new SustanciaQuimicaPeligrosaRetce();
			listaDetallesSustanciasAux = new ArrayList<SustanciaQuimicaPeligrosaDetalle>();
			
			cargarDatosCatalogos();
			cargarListaAnios();
			listaReporteSustanciasQuimicas = new ArrayList<ReporteSustanciasQuimicasPeligrosas>();
			listaReporteSustanciasQuimicas = reporteSustanciasPeligrosasFacade.findByInformacionProyecto(informacionProyecto);
			
			verFormulario = false;
			
			razonSocial();
			MensajeResponsabilidadRetceController mensajeResponsabilidadRetceController = JsfUtil.getBean(MensajeResponsabilidadRetceController.class);
			mensajeResponsabilidad = mensajeResponsabilidadRetceController.mensajeResponsabilidadRetce(informacionProyecto.getUsuarioCreacion());
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void crearRegistro(){
		reporteSustanciasQuimicas = new ReporteSustanciasQuimicasPeligrosas();
		listaSustanciaMezclaResumen = new ArrayList<SustanciaQuimicaPeligrosaRetce>();
		listaSustanciasQuimicasRetce = new ArrayList<SustanciaQuimicaPeligrosaRetce>();
		listaDetallesSustancias = new ArrayList<SustanciaQuimicaPeligrosaDetalle>();
		listaSustanciasQuimicasRetceEliminadas = new ArrayList<SustanciaQuimicaPeligrosaRetce>();
		
		idProcesoSustancia = null; 
		idTipoSustancia = null;
		idSustanciaQuimica = null; 
		idEnvase = null;
		verFormulario = true;
		habilitarIngreso = false;
		reporteSustanciasQuimicas.setInformacionProyecto(informacionProyecto);
	}
	
	public void seleccionarReporteSustancia(ReporteSustanciasQuimicasPeligrosas reporteSustancia){
		this.reporteSustanciasQuimicas = reporteSustancia;
			
		listaSustanciasQuimicasRetce = sustanciaQuimicaPeligrosaRetceFacade.findByReporte(reporteSustancia);
		
		if(listaSustanciasQuimicasRetce != null && !listaSustanciasQuimicasRetce.isEmpty()){
			
			for(SustanciaQuimicaPeligrosaRetce sustancia : listaSustanciasQuimicasRetce){
				
				if(sustancia.getCatalogoGeneralTipoSustancia().getDescripcion().equals("Mezclas")){
					sustancia.setEstadoFisico("N/A");
				}else{
					sustancia.setEstadoFisico(sustancia.getDetalleList().get(0).getCatalogoEstado().getDescripcion());
				}				
			}
						
			calculo(listaSustanciasQuimicasRetce);
		}		
		verFormulario = true;
		
		if(reporteSustancia.getEnviado() != null && reporteSustancia.getEnviado().equals(true)){
			habilitarIngreso = true;
		}else{
			habilitarIngreso = false;
		}
	}
	
	private void cargarDatosCatalogos(){
		try {
			listaCatalogoProcesosSustanciaQuimica = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("sustanciaQuimica.proceso");
			listaCatalogoTipoSustanciaQuimica = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("sustanciaQuimica.tipo_sustancia");
			listaCatalogoEstadoFisico = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("sustanciaQuimica.estado_fisico");
			listaCatalogoEnvaseAlmacenamiento = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("sustanciaQuimica.tipo_envase");
			
			mostrarMezcla = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public void seleccionarProyecto(InformacionProyecto informacionProyecto){
		this.reporteSustanciasQuimicas.setInformacionProyecto(informacionProyecto);		
	}
	
	public void tipoSustanciaListener(){
		try {
			DetalleCatalogoGeneral tipoSus = detalleCatalogoFacade.findById(idTipoSustancia);
									
			if(tipoSus.getDescripcion().equals("Mezclas"))
				mostrarMezcla = true;
			else
				mostrarMezcla = false;
			
			listaDetallesSustancias = new ArrayList<SustanciaQuimicaPeligrosaDetalle>();
			detalleSustanciaMezcla = new SustanciaQuimicaPeligrosaDetalle();
			idEnvase = null;
			
			sustanciaQuimicaRetce.setCatalogoGeneralTipoSustancia(tipoSus);
			
		} catch (Exception e) {
			e.printStackTrace();
		}				
	}
		
	public void estadoFisicoListener(){
		try {
			DetalleCatalogoGeneral estadoFisico = detalleCatalogoFacade.findById(idEstadoFisico);
			detalleSustanciaMezcla.setCatalogoEstado(estadoFisico);	
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
		
	public void agregarSustancia(){
		
		if(!sustanciaModificada){
			if(listaDetallesSustancias != null && !listaDetallesSustancias.isEmpty()){
				if(mostrarMezcla){
					for(SustanciaQuimicaPeligrosaDetalle sustanciaIng : listaDetallesSustancias){
						if(sustanciaIng.getSustanciaQuimica().equals(agregarSustanciasQuimicasBean.getSustanciaSeleccionada()) && 
								detalleSustanciaMezcla.getCatalogoEstado().equals(sustanciaIng.getCatalogoEstado())){
							JsfUtil.addMessageError("La sustancia ya se encuentra ingresada.");
							return;
						}
					}				
				}
			}		
		}else{
			for(SustanciaQuimicaPeligrosaDetalle sustanciaIng : listaDetallesSustanciasAux){
				if(sustanciaIng.getSustanciaQuimica().equals(agregarSustanciasQuimicasBean.getSustanciaSeleccionada()) && 
						detalleSustanciaMezcla.getCatalogoEstado().equals(sustanciaIng.getCatalogoEstado())){					
					
					getDetalleSustanciaMezcla().setId(getDetalleSustanciaMezclaAux().getId());
					getDetalleSustanciaMezcla().setCantidadAnual(getDetalleSustanciaMezclaAux().getCantidadAnual());
					getDetalleSustanciaMezcla().setCatalogoEstado(getDetalleSustanciaMezclaAux().getCatalogoEstado());
					getDetalleSustanciaMezcla().setEstado(getDetalleSustanciaMezclaAux().getEstado());
					getDetalleSustanciaMezcla().setFechaCreacion(getDetalleSustanciaMezclaAux().getFechaCreacion());
					getDetalleSustanciaMezcla().setFechaModificacion(getDetalleSustanciaMezclaAux().getFechaModificacion());
					getDetalleSustanciaMezcla().setPorcentajeSustanciaEnMezcla(getDetalleSustanciaMezclaAux().getPorcentajeSustanciaEnMezcla());;
					getDetalleSustanciaMezcla().setSustanciaQuimica(getDetalleSustanciaMezclaAux().getSustanciaQuimica());
					getDetalleSustanciaMezcla().setSustanciaQuimicaPeligrosa(getDetalleSustanciaMezclaAux().getSustanciaQuimicaPeligrosa());
					getDetalleSustanciaMezcla().setUsuarioCreacion(getDetalleSustanciaMezclaAux().getUsuarioCreacion());
					getDetalleSustanciaMezcla().setUsuarioModificacion(getDetalleSustanciaMezclaAux().getUsuarioModificacion());
						
					JsfUtil.addMessageError("La sustancia ya se encuentra ingresada.");
					
					return;
				}
			}	
		}
		
		if(mostrarMezcla){
			int sumaPorcentaje = 0;
			if(listaDetallesSustancias != null && !listaDetallesSustancias.isEmpty()){
				
				sumaPorcentaje = detalleSustanciaMezcla.getPorcentajeSustanciaEnMezcla();
				
				if(sustanciaModificada){
					porcentajeTotal -= anteriorValorPorcentaje;
					sumaPorcentaje += porcentajeTotal;
				}else{
					for(SustanciaQuimicaPeligrosaDetalle sustanciaIng : listaDetallesSustancias){
						sumaPorcentaje += sustanciaIng.getPorcentajeSustanciaEnMezcla();
					}
				}				
			}
			
			if(sumaPorcentaje >100){
				JsfUtil.addMessageError("El porcentaje añadido a la mezcla sobrepasa la suma del 100% del total de todas las sustancias");
				detalleSustanciaMezcla.setPorcentajeSustanciaEnMezcla(anteriorValorPorcentaje);
				return;
			}
		}
		
		
		if(agregarSustanciasQuimicasBean.getSustanciaSeleccionada() == null || 
				agregarSustanciasQuimicasBean.getSustanciaSeleccionada().getId() == null){
			JsfUtil.addMessageError("El campo Nombre de la sustancia química es requerido.");
			return;
		}
			
		if(!sustanciaModificada){
			detalleSustanciaMezcla.setSustanciaQuimica(agregarSustanciasQuimicasBean.getSustanciaSeleccionada());	
			
			listaDetallesSustancias.add(detalleSustanciaMezcla);
		}else{
			detalleSustanciaMezcla.setSustanciaQuimica(agregarSustanciasQuimicasBean.getSustanciaSeleccionada());			
		}	
		
		if(mostrarMezcla){
			detalleSustanciaMezcla = new SustanciaQuimicaPeligrosaDetalle();
		}
		idEstadoFisico = null;		
		
		sustanciaModificada = false;
		if(mostrarMezcla){
			porcentajeTotal = 0;
			for(SustanciaQuimicaPeligrosaDetalle sustanciaIng : listaDetallesSustancias){
				porcentajeTotal += sustanciaIng.getPorcentajeSustanciaEnMezcla();
			}		
		}
		
		RequestContext.getCurrentInstance().execute("PF('adicionarDatos').hide()");
	}
	
	public void editarSustancia(SustanciaQuimicaPeligrosaDetalle detalle){
		listaDetallesSustanciasAux = new ArrayList<SustanciaQuimicaPeligrosaDetalle>();
		if(mostrarMezcla){
			anteriorValorPorcentaje = detalle.getPorcentajeSustanciaEnMezcla();
		}
		setDetalleSustanciaMezcla(detalle);
		
		listaDetallesSustanciasAux.addAll(listaDetallesSustancias);
		listaDetallesSustanciasAux.remove(detalle);
		
		detalleSustanciaMezclaAux = (SustanciaQuimicaPeligrosaDetalle)SerializationUtils.clone(detalle);
				
		idEstadoFisico = detalle.getCatalogoEstado().getId();
		agregarSustanciasQuimicasBean.setSustanciaSeleccionada(detalle.getSustanciaQuimica());
		sustanciaModificada = true;		
	}
	
	public void eliminarSustanciaMezcla(SustanciaQuimicaPeligrosaDetalle sustancia){
		if(listaDetallesSustancias.contains(sustancia)){
			listaDetallesSustancias.remove(sustancia);
			listaDetallesSustanciasEliminar.add(sustancia);
			
			porcentajeTotal -= sustancia.getPorcentajeSustanciaEnMezcla();
			
		}		
	}
	
	public void agregar(){
		try {			
			if(listaDetallesSustancias == null || listaDetallesSustancias.isEmpty()){
				JsfUtil.addMessageError("Debe seleccionar sustancias químicas");
				return;
			}
									
			sustanciaQuimicaRetce.setDetalleList(listaDetallesSustancias);
									
			if(!sustanciaQuimicaRetceModificada){
				if(!validarSustanciaPeligrosaRetce(sustanciaQuimicaRetce, listaSustanciasQuimicasRetce)){
					JsfUtil.addMessageError("La sustancia química o mezcla ya se encuentra ingresada con los mismos parámetros");
					return;
				}		
				sustanciaQuimicaRetce.setReporteSustanciaQuimica(reporteSustanciasQuimicas);
				listaSustanciasQuimicasRetce.add(sustanciaQuimicaRetce);				
			}else{
				if(!validarSustanciaPeligrosaRetce(sustanciaQuimicaRetce, listaSustanciasQuimicasRetceAux)){
					JsfUtil.addMessageError("La sustancia química o mezcla ya se encuentra ingresada con los mismos parámetros");
					setearObjeto();
					return;
				}	
			}
			
			sustanciaQuimicaRetce.setEstadoFisico(estadoFisicoSustancia(listaDetallesSustancias));
			
			calculo(listaSustanciasQuimicasRetce);						
			limpiar();
			sustanciaQuimicaRetceModificada = false;
			
			mostrarOtroEnvase = false;
			limpiarSustanciaMezcla();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	//valida repetidos
	private boolean validarSustanciaPeligrosaRetce(SustanciaQuimicaPeligrosaRetce sustanciaQ, List<SustanciaQuimicaPeligrosaRetce> lista){
		try {
			
			if(lista != null  && lista.isEmpty()){
				return true;
			}
			
			boolean agregarSustanciaGeneral = false;
			
			for(SustanciaQuimicaPeligrosaRetce sustanciaA : lista){
								
				if(sustanciaA.getCatalogoGeneralProceso().equals(sustanciaQ.getCatalogoGeneralProceso()) && 
						sustanciaA.getCatalogoGeneralTipoSustancia().equals(sustanciaQ.getCatalogoGeneralTipoSustancia()) &&					
						sustanciaA.getCatalogoGeneralEnvase().equals(sustanciaQ.getCatalogoGeneralEnvase()) 
						){
					
					if(sustanciaA.getCatalogoGeneralEnvase().getDescripcion().equals("Otros")){
						if(sustanciaA.getOtroEnvase().equals(sustanciaQ.getOtroEnvase())){
							agregarSustanciaGeneral = false;
							break;
						}else{
							agregarSustanciaGeneral = true;
						}
					}
				
					if(sustanciaA.getCatalogoGeneralTipoSustancia().getDescripcion().equals("Mezclas")){
						if(sustanciaA.getNombreMezcla().equals(sustanciaQ.getNombreMezcla())){
							agregarSustanciaGeneral = false;	
							break;
						}
						else{
							agregarSustanciaGeneral = true;
						}
					}else{
						if (sustanciaA.getDetalleList().get(0).getSustanciaQuimica().getDescripcion()
								.equals(sustanciaQ.getDetalleList().get(0).getSustanciaQuimica().getDescripcion()) &&
								sustanciaA.getDetalleList().get(0).getCatalogoEstado()
								.equals(sustanciaQ.getDetalleList().get(0).getCatalogoEstado())) {
							agregarSustanciaGeneral = false;
							break;
						}else{
							agregarSustanciaGeneral = true;
						}
					}				
				}else{
					agregarSustanciaGeneral = true;
				}
			}
			
			return agregarSustanciaGeneral;
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return false;
	}
	
	private void setearObjeto(){
		getSustanciaQuimicaRetce().setId(sustanciaQuimicaRetceEditada.getId());
		getSustanciaQuimicaRetce().setCatalogoGeneralEnvase(sustanciaQuimicaRetceEditada.getCatalogoGeneralEnvase());
		getSustanciaQuimicaRetce().setCatalogoGeneralProceso(sustanciaQuimicaRetceEditada.getCatalogoGeneralProceso());
		getSustanciaQuimicaRetce().setCatalogoGeneralTipoSustancia(sustanciaQuimicaRetceEditada.getCatalogoGeneralTipoSustancia());
		getSustanciaQuimicaRetce().setDetalleList(sustanciaQuimicaRetceEditada.getDetalleList());
		getSustanciaQuimicaRetce().setEstado(sustanciaQuimicaRetceEditada.getEstado());
		getSustanciaQuimicaRetce().setFechaCreacion(sustanciaQuimicaRetceEditada.getFechaCreacion());
		getSustanciaQuimicaRetce().setFechaModificacion(sustanciaQuimicaRetceEditada.getFechaModificacion());
		getSustanciaQuimicaRetce().setNombreMezcla(sustanciaQuimicaRetceEditada.getNombreMezcla());
		getSustanciaQuimicaRetce().setOtroEnvase(sustanciaQuimicaRetceEditada.getOtroEnvase());
		getSustanciaQuimicaRetce().setReporteSustanciaQuimica(sustanciaQuimicaRetceEditada.getReporteSustanciaQuimica());
		getSustanciaQuimicaRetce().setUsuarioCreacion(sustanciaQuimicaRetceEditada.getUsuarioCreacion());
		getSustanciaQuimicaRetce().setUsuarioModificacion(sustanciaQuimicaRetceEditada.getUsuarioModificacion());		
	}
	
	public void guardar(){
		try {
			if(listaSustanciasQuimicasRetce != null && !listaSustanciasQuimicasRetce.isEmpty()){
				
				reporteSustanciasQuimicas.setInformacionProyecto(informacionProyecto);
				reporteSustanciasQuimicas.setEstado(true);
				
				ReporteSustanciasQuimicasPeligrosas reporte = reporteSustanciasPeligrosasFacade.save(reporteSustanciasQuimicas, loginBean.getUsuario());
				
				for(SustanciaQuimicaPeligrosaRetce sustanciaG : listaSustanciasQuimicasRetce){
					sustanciaG.setReporteSustanciaQuimica(reporte);										
					
					sustanciaQuimicaPeligrosaRetceFacade.save(sustanciaG, loginBean.getUsuario());										
				}
				
				if(!listaSustanciasQuimicasRetceEliminadas.isEmpty()){
					for(SustanciaQuimicaPeligrosaRetce eliminada : listaSustanciasQuimicasRetceEliminadas){
						if(eliminada.getId() != null){
							eliminada.setEstado(false);
							sustanciaQuimicaPeligrosaRetceFacade.save(eliminada, loginBean.getUsuario());	
						}
					}
				}
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}else{
				JsfUtil.addMessageError("Debe ingresar información");
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}		
	}
	
	private void limpiar(){
		listaDetallesSustancias = new ArrayList<SustanciaQuimicaPeligrosaDetalle>();
		sustanciaQuimicaRetce = new SustanciaQuimicaPeligrosaRetce();
		detalleSustanciaMezcla = new SustanciaQuimicaPeligrosaDetalle();
		detalleSustanciaMezclaAux = new SustanciaQuimicaPeligrosaDetalle();
		listaSustanciasQuimicasRetceAux = new ArrayList<SustanciaQuimicaPeligrosaRetce>();
		sustanciaQuimicaRetceEditada = new SustanciaQuimicaPeligrosaRetce();
		
		idProcesoSustancia = null; 
		idTipoSustancia = null;
		idSustanciaQuimica = null; 
		idEnvase = null;
		mostrarMezcla = false;
		porcentajeTotal = 0;
		sustanciaQuimicaRetceModificada = false;
	}
	
	public void cancelar(){
		limpiar();
	}
	
	public void cancelarSustancia(){
		sustanciaModificada = false;
	}
	
	public void cargarSustanciaPura(){
		if(sustanciaQuimicaRetce.getCatalogoGeneralTipoSustancia() == null || 
				sustanciaQuimicaRetce.getCatalogoGeneralTipoSustancia().getId() == null){
			JsfUtil.addMessageError("Seleccione el tipo de sustancia");
			return;
		}
		
		if(listaDetallesSustancias != null && !listaDetallesSustancias.isEmpty()){
			sustanciaModificada = true;
			agregarSustanciasQuimicasBean.setSustanciaSeleccionada(listaDetallesSustancias.get(0).getSustanciaQuimica());
			setDetalleSustanciaMezcla(listaDetallesSustancias.get(0));
			idEstadoFisico = listaDetallesSustancias.get(0).getCatalogoEstado().getId();
			RequestContext.getCurrentInstance().execute("PF('adicionarDatos').show()");
		}else{
			limpiarSustanciaMezcla();
			RequestContext.getCurrentInstance().execute("PF('adicionarDatos').show()");
		}		
	}
	
	public void agregarSustanciaPura(){
		if(sustanciaQuimicaRetce.getCatalogoGeneralTipoSustancia() == null || 
				sustanciaQuimicaRetce.getCatalogoGeneralTipoSustancia().getId() == null){
			JsfUtil.addMessageError("Seleccione el tipo de sustancia");
			return;
		}
		
		limpiarSustanciaMezcla();
		
		RequestContext.getCurrentInstance().execute("PF('adicionarDatos').show()");	
	}
	
	public void limpiarSustanciaMezcla(){
		
		RequestContext.getCurrentInstance().reset(":form:sustanciaMezcla");
		detalleSustanciaMezcla = new SustanciaQuimicaPeligrosaDetalle();
		detalleSustanciaMezclaAux = new SustanciaQuimicaPeligrosaDetalle();
		idEstadoFisico = null;
		agregarSustanciasQuimicasBean.setSustanciaSeleccionada(null);
		
		if(!mostrarMezcla){
			listaDetallesSustancias = new ArrayList<SustanciaQuimicaPeligrosaDetalle>();
		}
						
		RequestContext.getCurrentInstance().reset(":form:sustanciaMezcla");
		RequestContext.getCurrentInstance().reset(":form:codigoSustanciaHidden");	
			
	}
	
	private void calculo(List<SustanciaQuimicaPeligrosaRetce> lista){
		try {
			Map<String, Double> sus = new HashMap<String, Double>();	
			listaSustanciaMezclaResumen = new ArrayList<SustanciaQuimicaPeligrosaRetce>();
			
			for(SustanciaQuimicaPeligrosaRetce sustancia : lista){
				
				for(SustanciaQuimicaPeligrosaDetalle susPeligrosa : sustancia.getDetalleList()){
					if(sus.isEmpty()){
						sus.put(susPeligrosa.getSustanciaQuimica().getDescripcion(), susPeligrosa.getCantidadAnual());
					}else{
						if(sus.containsKey(susPeligrosa.getSustanciaQuimica().getDescripcion())){
							Double cantidad = sus.get(susPeligrosa.getSustanciaQuimica().getDescripcion());
							Double cantidadNueva = cantidad + susPeligrosa.getCantidadAnual();
							sus.put(susPeligrosa.getSustanciaQuimica().getDescripcion(), cantidadNueva);
						}else{
							sus.put(susPeligrosa.getSustanciaQuimica().getDescripcion(), susPeligrosa.getCantidadAnual());
						}
					}
				}				
			}
			
			for (Entry<String, Double> entry : sus.entrySet()) {
				
				SustanciaQuimicaPeligrosaRetce sustanciaResumen = new SustanciaQuimicaPeligrosaRetce();
				
				sustanciaResumen.setNombreMezcla(entry.getKey());
				sustanciaResumen.setCantidadAnual(entry.getValue());
				
				listaSustanciaMezclaResumen.add(sustanciaResumen);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public String nombreMezcla(SustanciaQuimicaPeligrosaRetce sustancia){
		
		String nombre = "";
		if(sustancia.getNombreMezcla() == null){
			nombre = sustancia.getDetalleList().get(0).getSustanciaQuimica().getDescripcion();
		}else{
			nombre = sustancia.getNombreMezcla();
		}
				
		return nombre;		
	}
	
	public void editarSustanciaGeneral(SustanciaQuimicaPeligrosaRetce sustancia){
		try {
			limpiar();
			listaSustanciasQuimicasRetceAux.addAll(listaSustanciasQuimicasRetce);
			listaSustanciasQuimicasRetceAux.remove(sustancia);
			setSustanciaQuimicaRetce(sustancia);
			
			sustanciaQuimicaRetceEditada = (SustanciaQuimicaPeligrosaRetce)SerializationUtils.clone(sustancia);
		
			if(sustancia.getCatalogoGeneralEnvase().getDescripcion().equals("Otros")){
				mostrarOtroEnvase = true;
			}else{
				mostrarOtroEnvase = false;
			}								
			
			idProcesoSustancia = sustancia.getCatalogoGeneralProceso().getId();
			idTipoSustancia = sustancia.getCatalogoGeneralTipoSustancia().getId();
			idEnvase = sustancia.getCatalogoGeneralEnvase().getId();	
			
			if(sustancia.getCatalogoGeneralTipoSustancia().getDescripcion().equals("Mezclas")){
				mostrarMezcla = true;
				listaDetallesSustancias = new ArrayList<SustanciaQuimicaPeligrosaDetalle>();
				listaDetallesSustancias.addAll(sustancia.getDetalleList());
			}else{
				mostrarMezcla = false;
				listaDetallesSustancias = new ArrayList<SustanciaQuimicaPeligrosaDetalle>();
				listaDetallesSustancias.addAll(sustancia.getDetalleList());
				if(listaDetallesSustancias != null && !listaDetallesSustancias.isEmpty()){
					setDetalleSustanciaMezcla(listaDetallesSustancias.get(0));
				}				
			}
			
			if(mostrarMezcla){
				porcentajeTotal = 0;
				for(SustanciaQuimicaPeligrosaDetalle sustanciaIng : listaDetallesSustancias){
					porcentajeTotal += sustanciaIng.getPorcentajeSustanciaEnMezcla();
				}		
			}
			
			sustanciaQuimicaRetceModificada = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void eliminarReporte(ReporteSustanciasQuimicasPeligrosas reporteSustancia){
		try {
			reporteSustancia.setEstado(false);
			reporteSustanciasPeligrosasFacade.save(reporteSustancia, JsfUtil.getLoggedUser());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void eliminarSustanciaQuimicaPeligrosa(SustanciaQuimicaPeligrosaRetce sustancia){
		try {
			listaSustanciasQuimicasRetce.remove(sustancia);
			listaSustanciasQuimicasRetceEliminadas.add(sustancia);
			calculo(listaSustanciasQuimicasRetce);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void enviar(){
		try {			
			guardar();
			reporteSustanciasQuimicas.setEnviado(true);
			reporteSustanciasPeligrosasFacade.save(reporteSustanciasQuimicas, loginBean.getUsuario());
			verFormulario = false;
			ocultarFormulario();
						
			notificacion();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void notificacion(){
		try {
			
			boolean notificacionSustancias = sustanciaQuimicaPeligrosaRetceFacade.encontrarSustancia(reporteSustanciasQuimicas);
			
			Usuario usuarioCoordinador = new Usuario();
			Usuario usuarioTecnico = new Usuario();
			
			if(informacionProyecto.getAreaSeguimiento().getTipoArea().getSiglas().equals("PC")){
				usuarioCoordinador=areaFacade.getUsuarioPorRolArea("role.retce.pc.coordinador.desechos", reporteSustanciasQuimicas.getInformacionProyecto().getAreaSeguimiento());
				usuarioTecnico = areaFacade.getUsuarioPorRolArea("role.retce.pc.tecnico.desechos", reporteSustanciasQuimicas.getInformacionProyecto().getAreaSeguimiento());				
			}else if(informacionProyecto.getAreaSeguimiento().getTipoArea().getSiglas().equals("DP")){
				usuarioCoordinador=areaFacade.getUsuarioPorRolArea("role.retce.dp.coordinador.desechos", reporteSustanciasQuimicas.getInformacionProyecto().getAreaSeguimiento());
				usuarioTecnico = areaFacade.getUsuarioPorRolArea("role.retce.dp.tecnico.desechos", reporteSustanciasQuimicas.getInformacionProyecto().getAreaSeguimiento());	
			}else if(informacionProyecto.getAreaSeguimiento().getTipoArea().getSiglas().equals("EA")){
				usuarioCoordinador=areaFacade.getUsuarioPorRolArea("role.retce.ea.coordinador.desechos", reporteSustanciasQuimicas.getInformacionProyecto().getAreaSeguimiento());
				usuarioTecnico = areaFacade.getUsuarioPorRolArea("role.retce.ea.tecnico.desechos", reporteSustanciasQuimicas.getInformacionProyecto().getAreaSeguimiento());	
			}		
			
			if(usuarioCoordinador == null || usuarioCoordinador.getId() == null){
				LOG.error("No se encontro usuario coordinador en " + informacionProyecto.getAreaSeguimiento().getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return;
			}
			
			if(usuarioTecnico == null || usuarioTecnico.getId() == null){
				LOG.error("No se encontro usuario técnico en " + informacionProyecto.getAreaSeguimiento().getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return;
			}
			
			String mensajeAdicional = "";
			if(notificacionSustancias){
				String adiciona = " que requieren un permiso adicional (cianuro de sodio, cianuro de potasio o mercurio)";
				mensajeAdicional = "Sustancias Químicas Peligrosas" + adiciona;
			}else{
				mensajeAdicional = "Sustancias Químicas Peligrosas";
			}
				
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionIngresoReporteRetce",
					new Object[] {usuarioCoordinador.getPersona().getNombre(),razonSocial(),							
					reporteSustanciasQuimicas.getCodigo(),mensajeAdicional});

			String mensajeTecnico = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionIngresoReporteRetce",
							new Object[] {usuarioTecnico.getPersona().getNombre(),razonSocial(),
							reporteSustanciasQuimicas.getCodigo(), mensajeAdicional});				
			
			Email.sendEmail(usuarioCoordinador,"Reporte RETCE Sustancias Químicas Peligrosas", mensaje, informacionProyecto.getCodigo(), loginBean.getUsuario());
			Email.sendEmail(usuarioTecnico,"Reporte RETCE Sustancias Químicas Peligrosas", mensajeTecnico, informacionProyecto.getCodigo(), loginBean.getUsuario());
			
		} catch (Exception e) {
			e.printStackTrace();			
		}		
	}
	
	private String razonSocial(){
		try {
			
			String razonSocial = "";
			if(informacionProyecto.getUsuarioCreacion().length() == 13){
				Organizacion organizacion = organizacionFacade.buscarPorRuc(informacionProyecto.getUsuarioCreacion());				
				if(organizacion != null && organizacion.getNombre() != null){
					razonSocial = organizacion.getNombre();
					representanteLegal = organizacion.getPersona().getNombre();
					cedulaRepresentanteLegal = organizacion.getPersona().getPin();
				}else{
					razonSocial = loginBean.getUsuario().getPersona().getNombre();
					representanteLegal = loginBean.getUsuario().getPersona().getNombre();
					cedulaRepresentanteLegal =  loginBean.getUsuario().getNombre();
					}
			}else{
				Persona persona = loginBean.getUsuario().getPersona();
				if(persona != null && persona.getNombre() != null){
					razonSocial = persona.getNombre();
					representanteLegal = persona.getNombre();
					cedulaRepresentanteLegal =  persona.getPin();
				}
			}	
			return razonSocial;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void ocultarFormulario(){
		try {
			verFormulario=false;		
			listaReporteSustanciasQuimicas = reporteSustanciasPeligrosasFacade.findByInformacionProyecto(informacionProyecto);
			limpiar();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void procesoListener(){
		try {
			DetalleCatalogoGeneral procesoSustancia = detalleCatalogoFacade.findById(idProcesoSustancia);
			sustanciaQuimicaRetce.setCatalogoGeneralProceso(procesoSustancia);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void tipoEnvaseListener(){
		try {
			DetalleCatalogoGeneral otro = detalleCatalogoFacade.findById(idEnvase);
			
			sustanciaQuimicaRetce.setCatalogoGeneralEnvase(otro);
			
			mostrarOtroEnvase = false;
			if(otro.getDescripcion().equals("Otros")){
				mostrarOtroEnvase =  true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public String estadoFisicoSustancia(List<SustanciaQuimicaPeligrosaDetalle> lista){
		
		if(sustanciaQuimicaRetce.getCatalogoGeneralTipoSustancia().getDescripcion().equals("Sustancias Puras")){
			String estado = lista.get(0).getCatalogoEstado().getDescripcion();
			return estado;
		}else{
			return "N/A";
		}
	}
	
	/*public String seleccionarAnioReporte() {
		validarExisteReporte();
		return null;
	}
	
	public Boolean validarExisteReporte() {
		ReporteSustanciasQuimicasPeligrosas reporteSustancias = reporteSustanciasPeligrosasFacade.getByInformacionProyectoAnio(informacionProyecto.getId(), reporteSustanciasQuimicas.getAnioDeclaracion());
		if(reporteSustancias != null) {
			if (reporteSustancias.getEnviado()) {
				JsfUtil.addMessageError("Usted ya generó el reporte de Sustancias Químicas para el año seleccionado.");
				JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), informacionProyecto.getId());
				JsfUtil.redirectTo("/control/retce/sustanciasQuimicasPeligrosas.jsf");
			}
			reporteSustanciasQuimicas = reporteSustancias;
			return true;
		}
		return false;
	}*/
	
	private void cargarListaAnios() {
		Date nuevaFecha = new Date();
		Integer i= JsfUtil.getYearFromDate(nuevaFecha);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -1);
		nuevaFecha = cal.getTime();
		listaAnios = new ArrayList<Integer>();

		for ( i=2019; i<= JsfUtil.getYearFromDate(nuevaFecha); i++){
			listaAnios.add(i);
		}
	}
}
