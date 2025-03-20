package ec.gob.ambiente.control.retce.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang.SerializationUtils;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.retce.model.DesechoAutogestion;
import ec.gob.ambiente.retce.model.DesechoExportacion;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.DetalleManifiestoDesecho;
import ec.gob.ambiente.retce.model.DisposicionFueraInstalacion;
import ec.gob.ambiente.retce.model.EliminacionFueraInstalacion;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.IdentificacionDesecho;
import ec.gob.ambiente.retce.model.Manifiesto;
import ec.gob.ambiente.retce.services.DesechoAutogestionFacade;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.DisposicionFueraInstalacionFacade;
import ec.gob.ambiente.retce.services.EliminacionFueraInstalacionFacade;
import ec.gob.ambiente.retce.services.ExportacionDesechosFacade;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.ManifiestoFacade;
import ec.gob.ambiente.retce.services.TecnicoResponsableFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class DeclaracionGeneradorRetceBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@EJB
    private TecnicoResponsableFacade tecnicoResponsableFacade;
	
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private DesechoAutogestionFacade desechoAutogestionFacade;
	
	@EJB
	private ManifiestoFacade manifiestoFacade;
	
	@EJB
	private ExportacionDesechosFacade exportacionDesechosFacade;
	
	@EJB
	private EliminacionFueraInstalacionFacade eliminacionFueraInstalacionFacade;
	
	@EJB
	private DisposicionFueraInstalacionFacade disposicionFueraInstalacionFacade;
	
	@Getter
	@Setter
	private List<DesechoPeligroso> listaDesechosGenerador;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;
	
	@Getter
	@Setter
	private DetalleCatalogoGeneral tipoUnidadTonelada;
	
	@Getter
	@Setter
	private List<DesechoAutogestion> listaDesechosAutogestionEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<DetalleManifiestoDesecho> listaDesechoManifiestosEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<DesechoExportacion> listaDesechosExportacionEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<EliminacionFueraInstalacion> listaDesechosEliminacionEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<DisposicionFueraInstalacion> listaDesechosDisposicionEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<Documento> listaDocumentosExportacionEliminar = new ArrayList<>();
	
	@Getter
	@Setter
	private Double factorKgT;
	
	@Getter
	@Setter
	private Integer numeroObservacion;
	
	@Getter
	@Setter
	private Boolean editarGeneracion, editarAutogestion, editarTransporte, editarExportacion, editarEliminacion, editarDisposicion;

	@PostConstruct
	public void init() {
		try {
			
			tipoUnidadTonelada = detalleCatalogoGeneralFacade.findByCodigo("tipounidad.tonelada");
			factorKgT = Double.parseDouble(detalleCatalogoGeneralFacade.findByCodigo("factor.kg_toneladas").getParametro());
			numeroObservacion = 0;
			
//			listaDesechosAutogestionEliminar = null;
//			listaDesechosExportacionEliminar = null;
//			listaDesechosEliminacionEliminar = null;
//			listaDesechosDisposicionEliminar = null;
			
			listaDesechosAutogestionEliminar = new ArrayList<>();
			listaDesechoManifiestosEliminar = new ArrayList<>();
			listaDesechosExportacionEliminar = new ArrayList<>();
			listaDesechosEliminacionEliminar = new ArrayList<>();
			listaDesechosDisposicionEliminar = new ArrayList<>();
			
			editarGeneracion = false;
			editarAutogestion = false;
			editarTransporte = false;
			editarExportacion = false;
			editarEliminacion = false;
			editarDisposicion = false;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Boolean validarCantidadReporteDesecho(Double valorIngresado, Integer idDesecho, Integer idUnidad) {
		Double valorIngresadoToneladas = valorIngresado;
		if (!idUnidad.equals(tipoUnidadTonelada.getId())) 
			valorIngresadoToneladas = valorIngresado * factorKgT;		
		
		List<IdentificacionDesecho> listaIdentificacionDesechos = JsfUtil.getBean(IdentificacionDesechosBean.class).getListaIdentificacionDesechos();
		
		for (IdentificacionDesecho identificacionDesecho : listaIdentificacionDesechos) {
			if (identificacionDesecho.getDesechoPeligroso().getId().equals(idDesecho)) {
				if (valorIngresadoToneladas > identificacionDesecho.getCantidadTotalToneladas()) {
					JsfUtil.addMessageError("El valor de eliminación no debe superar la \"cantidad del año anterior (si lo tuviera)\" más la \"cantidad de generación anual\" menos la \"cantidad que no pudo ser gestionada\".");
					return false;
				}

				break;
			}
		}

		return true;
	}
	
	public Double getCantidadToneladas(Double valorIngresado, Integer idUnidad) {
		Double valorIngresadoToneladas = valorIngresado;
		if (!idUnidad.equals(tipoUnidadTonelada.getId()))
			valorIngresadoToneladas = valorIngresado * factorKgT;
		
		return valorIngresadoToneladas;
	}
	
	public void validarDatosRelacionados(Integer seccion, Integer idDesechoPeligroso) {
		if(seccion == 10) {
			//Validacion q elimina los desechos de la seccion exportacion cuando se registran en autogestion
			List<DesechoExportacion> desechosExportacion = JsfUtil.getBean(ExportacionDesechosBean.class).getListaExportacionDesechos();
			if(desechosExportacion == null || desechosExportacion.size() == 0)
				JsfUtil.getBean(ExportacionDesechosBean.class).cargarDatos(); //se carga los datos xq al iniciar el form la seccion no esta cargada 
			
			desechosExportacion = JsfUtil.getBean(ExportacionDesechosBean.class).getListaExportacionDesechos();
			if(desechosExportacion != null && desechosExportacion.size() > 0){
				for (DesechoExportacion desecho : desechosExportacion) {
					if(desecho.getDesechoPeligroso().getId().equals(idDesechoPeligroso)){
						desecho.setEstado(false);
						listaDesechosExportacionEliminar.add(desecho);
						
						List<Documento> documentosDesecho = new ArrayList<>();
						documentosDesecho.addAll(desecho.getDocumentosNotificacion());
						documentosDesecho.addAll(desecho.getDocumentosAutorizacion());
						documentosDesecho.addAll(desecho.getDocumentosMovimiento());
						documentosDesecho.addAll(desecho.getDocumentosDestruccion());
						
						for (Documento documentoExportacion : documentosDesecho) {
							if(documentoExportacion.getId() != null && 
									!listaDocumentosExportacionEliminar.contains(documentoExportacion)) {
								documentoExportacion.setEstado(false);
								listaDocumentosExportacionEliminar.add(documentoExportacion);
							}
						}
					}
				}
			}
			return;
		}
		
		//AUTOGESTION
		if(seccion == 1) {
			List<DesechoAutogestion> desechosAutogestion = JsfUtil.getBean(AutogestionDesechosBean.class).getListaDesechosAutogestion();
			if(desechosAutogestion == null || desechosAutogestion.size() == 0)
				JsfUtil.getBean(AutogestionDesechosBean.class).cargarDatos();
			
			desechosAutogestion = JsfUtil.getBean(AutogestionDesechosBean.class).getListaDesechosAutogestion();
			if(desechosAutogestion != null && desechosAutogestion.size() > 0){
				for (DesechoAutogestion desechoAutogestion : desechosAutogestion) {
					if(desechoAutogestion.getDesechoPeligroso().getId().equals(idDesechoPeligroso)){
						listaDesechosAutogestionEliminar.add(desechoAutogestion);
					}
				}
			}
		}
		
		//TRANSPORTE
		if(seccion == 1 || seccion == 2) {
			List<Manifiesto> manifiestos = new ArrayList<>();
			manifiestos.addAll(JsfUtil.getBean(TransporteDesechosBean.class).getListaManifiestos());
			manifiestos.addAll(JsfUtil.getBean(TransporteDesechosBean.class).getListaManifiestosGestores());
			
			if(manifiestos.size() == 0)
				JsfUtil.getBean(TransporteDesechosBean.class).cargarDatos();
			
			manifiestos.addAll(JsfUtil.getBean(TransporteDesechosBean.class).getListaManifiestos());
			manifiestos.addAll(JsfUtil.getBean(TransporteDesechosBean.class).getListaManifiestosGestores());
			
			if(manifiestos != null && manifiestos.size() > 0){
				for (Manifiesto manifiesto : manifiestos) {
					if(manifiesto.getListaManifiestoDesechos() != null && manifiesto.getListaManifiestoDesechos().size() > 0) {
						for (DetalleManifiestoDesecho desecho : manifiesto.getListaManifiestoDesechos()) {
							if(desecho.getDesechoPeligroso().getId().equals(idDesechoPeligroso)){
								desecho.setEstado(false);
								listaDesechoManifiestosEliminar.add(desecho);
							}
						}
					}
				}
			}
		}
		
		//EXPORTACION
		if(seccion == 1 || seccion == 2) {
			List<DesechoExportacion> desechosExportacion = JsfUtil.getBean(ExportacionDesechosBean.class).getListaExportacionDesechos();
			if(desechosExportacion == null || desechosExportacion.size() == 0)
				JsfUtil.getBean(ExportacionDesechosBean.class).cargarDatos();
			
			desechosExportacion = JsfUtil.getBean(ExportacionDesechosBean.class).getListaExportacionDesechos();
			if(desechosExportacion != null && desechosExportacion.size() > 0){
				for (DesechoExportacion desecho : desechosExportacion) {
					if(desecho.getDesechoPeligroso().getId().equals(idDesechoPeligroso)){
						desecho.setEstado(false);
						listaDesechosExportacionEliminar.add(desecho);
						
						List<Documento> documentosDesecho = new ArrayList<>();
						documentosDesecho.addAll(desecho.getDocumentosNotificacion());
						documentosDesecho.addAll(desecho.getDocumentosAutorizacion());
						documentosDesecho.addAll(desecho.getDocumentosMovimiento());
						documentosDesecho.addAll(desecho.getDocumentosDestruccion());
						
						for (Documento documentoExportacion : documentosDesecho) {
							if(documentoExportacion.getId() != null && 
									!listaDocumentosExportacionEliminar.contains(documentoExportacion)) {								
								documentoExportacion.setEstado(false);
								listaDocumentosExportacionEliminar.add(documentoExportacion);
							}
						}
					}
				}
			}
		}
		
		//ELIMINACION
		if(seccion == 1 || seccion == 2 || seccion == 4) {
			List<EliminacionFueraInstalacion> desechosEliminacion = JsfUtil.getBean(EliminacionDesechosRetceBean.class).getListaEliminacionFueraInstalacion();
			if(desechosEliminacion == null || desechosEliminacion.size() == 0)
				JsfUtil.getBean(EliminacionDesechosRetceBean.class).cargarDatos();
			
			desechosEliminacion = JsfUtil.getBean(EliminacionDesechosRetceBean.class).getListaEliminacionFueraInstalacion();
			if(desechosEliminacion != null && desechosEliminacion.size() > 0){
				for (EliminacionFueraInstalacion desecho : desechosEliminacion) {
					if(desecho.getDesechoPeligroso().getId().equals(idDesechoPeligroso)){
						desecho.setEstado(false);
						listaDesechosEliminacionEliminar.add(desecho);
					}
				}
			}
		}
		
		//DISPOSICION
		if(seccion == 1 || seccion == 2 || seccion == 4 || seccion == 5) {
			List<DisposicionFueraInstalacion> desechosDisposicion = JsfUtil.getBean(DisposicionDesechosRetceBean.class).getListaDisposicionFueraInstalacion();
			if(desechosDisposicion == null || desechosDisposicion.size() == 0)
				JsfUtil.getBean(DisposicionDesechosRetceBean.class).cargarDatos();
			
			desechosDisposicion = JsfUtil.getBean(DisposicionDesechosRetceBean.class).getListaDisposicionFueraInstalacion();
			if(desechosDisposicion != null && desechosDisposicion.size() > 0){
				for (DisposicionFueraInstalacion desecho : desechosDisposicion) {
					if(desecho.getDesechoPeligroso().getId().equals(idDesechoPeligroso)){
						desecho.setEstado(false);
						listaDesechosDisposicionEliminar.add(desecho);
					}
				}
			}
		}
	}
	
	public void eliminarDatosAsociados() {
		if(numeroObservacion > 0) {
			guardarHistorial();
		}
		
		//eliminar datos asociados cuando se realiza cambios en los desechos
		if(listaDesechosAutogestionEliminar != null)
			desechoAutogestionFacade.eliminarDesechosAutogestion(listaDesechosAutogestionEliminar);
		if(listaDesechoManifiestosEliminar != null)
			manifiestoFacade.eliminarDetallesManifiesto(listaDesechoManifiestosEliminar);
		if(listaDesechosExportacionEliminar != null)
			exportacionDesechosFacade.eliminarExportacionDesechos(listaDesechosExportacionEliminar);
		if(listaDocumentosExportacionEliminar != null) {
			for (Documento documentoExportacion : listaDocumentosExportacionEliminar) {
				documentosFacade.eliminarDocumento(documentoExportacion);
			}
		}
		if(listaDesechosEliminacionEliminar != null)
			eliminacionFueraInstalacionFacade.eliminarEliminacionFueraInstalacion(listaDesechosEliminacionEliminar);
		if(listaDesechosDisposicionEliminar != null)
			disposicionFueraInstalacionFacade.eliminarListaDisposicion(listaDesechosDisposicionEliminar);
		
		limpiarListasDatosAsociados();
	}
	
	public void limpiarListasDatosAsociados(){
		listaDesechosAutogestionEliminar = new ArrayList<>();
		listaDesechoManifiestosEliminar = new ArrayList<>();
		listaDesechosExportacionEliminar = new ArrayList<>();
		listaDesechosEliminacionEliminar = new ArrayList<>();
		listaDesechosDisposicionEliminar = new ArrayList<>();
	}
	
	public void guardarHistorial() {
		if(listaDesechosAutogestionEliminar.size() > 0) {
			for (DesechoAutogestion desechoGestion : listaDesechosAutogestionEliminar) {
				if (desechoGestion.getId() != null) {
					DesechoAutogestion desechoOriginal = (DesechoAutogestion) SerializationUtils.clone(desechoGestion);
					desechoOriginal.setEstado(true);
					JsfUtil.getBean(AutogestionDesechosBean.class).getHistorialDesechoEliminado(desechoOriginal);
				}
			}
			
			JsfUtil.getBean(AutogestionDesechosBean.class).guardarHistorial();
		}
		if(listaDesechoManifiestosEliminar.size() > 0) {
			for (DetalleManifiestoDesecho detalleManifiesto : listaDesechoManifiestosEliminar) {
				if (detalleManifiesto.getId() != null) {
					DetalleManifiestoDesecho detalleOriginal = (DetalleManifiestoDesecho) SerializationUtils.clone(detalleManifiesto);
					detalleOriginal.setEstado(true);
					JsfUtil.getBean(TransporteDesechosBean.class).buscarHistorialDetalleManifiesto(detalleOriginal, detalleManifiesto);
				}
			}
			JsfUtil.getBean(TransporteDesechosBean.class).guardarHistorial();
		}
		if(listaDesechosExportacionEliminar.size() > 0) {
			for (DesechoExportacion desechoExportacion : listaDesechosExportacionEliminar) {
				if (desechoExportacion.getId() != null) {
					DesechoExportacion desechoOriginal = (DesechoExportacion) SerializationUtils.clone(desechoExportacion);
					desechoOriginal.setEstado(true);
					JsfUtil.getBean(ExportacionDesechosBean.class).buscarHistorialDesechoExportacion(desechoOriginal, desechoExportacion, true);
				}
			}
			JsfUtil.getBean(ExportacionDesechosBean.class).guardarHistorial();
		}
		if(listaDesechosEliminacionEliminar.size() > 0) {
			for (EliminacionFueraInstalacion desecho : listaDesechosEliminacionEliminar) {
				if (desecho.getId() != null) {
					EliminacionFueraInstalacion desechoOriginal = (EliminacionFueraInstalacion) SerializationUtils.clone(desecho);
					desechoOriginal.setEstado(true);
					JsfUtil.getBean(EliminacionDesechosRetceBean.class).buscarHistorialEliminacionFueraInstalacion(desechoOriginal, desecho);
				}
			}
			JsfUtil.getBean(EliminacionDesechosRetceBean.class).guardarHistorial();
		}
		if(listaDesechosDisposicionEliminar.size() > 0) {
			for (DisposicionFueraInstalacion desecho : listaDesechosDisposicionEliminar) {
				if (desecho.getId() != null) {
					DisposicionFueraInstalacion desechoOriginal = (DisposicionFueraInstalacion) SerializationUtils.clone(desecho);
					desechoOriginal.setEstado(true);
					JsfUtil.getBean(DisposicionDesechosRetceBean.class).buscarHistorialDiposicionFueraInstalacion(desechoOriginal, desecho);
				}
			}
			JsfUtil.getBean(DisposicionDesechosRetceBean.class).guardarHistorial();
		}
	}
}
