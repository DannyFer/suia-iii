package ec.gob.ambiente.prevencion.actualizacionCertInterseccion;

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
import ec.gob.ambiente.prevencion.registro.proyecto.bean.VerProyectoBean;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.proyecto.controller.VerProyectoRcoaBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoActualizacionCertificadoInterseccionFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoSuiaVerdeFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ProyectosPendientesActualizacionCIController implements Serializable {

	private static final long serialVersionUID = 2198593440217678246L;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{verProyectoBean}")
    private VerProyectoBean verProyectoBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{proyectoSuiaVerdeBean}")
    private ProyectoSuiaVerdeBean proyectoSuiaVerdeBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{verProyectoRcoaBean}")
    private VerProyectoRcoaBean verProyectoRcoaBean;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private ProyectoActualizacionCertificadoInterseccionFacade proyectoActualizacionCIFacade;
	
	@EJB
	private ProyectoSuiaVerdeFacade proyectoSuiaVerdeFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@Getter
	@Setter
	private List<ProyectoCustom> proyectos;
	
	@Getter
	private List<String> sectoresItems;
	
	@PostConstruct
    public void init() {
		
		sectoresItems = new ArrayList<String>();
		proyectos = new ArrayList<>();
		
		List<TipoSector> sectores = proyectoLicenciamientoAmbientalFacade.getTiposSectores();
		for (TipoSector tipoSector : sectores) {
			sectoresItems.add(tipoSector.toString());
		}
		
		proyectos =  proyectoActualizacionCIFacade.listarProyectosPendientesActualizacionCI(JsfUtil.getLoggedUser());
		
		List<ProyectoCustom> proyectos4Cat = proyectoActualizacionCIFacade.listarProyectos4CategoriasEnTramiteActualizacionCI(JsfUtil.getLoggedUser().getNombre());
		if(proyectos4Cat.size() > 0) {
			List<String> proyectosSuia = new ArrayList<>();
			for(ProyectoCustom proy : proyectos) {
				proyectosSuia.add(proy.getCodigo());
			}
			for(ProyectoCustom proy : proyectos4Cat) {
				if(!proyectosSuia.contains(proy.getCodigo()))
					proyectos.add(proy);
			}
		}
		
		List<ProyectoCustom> proyectosRcoa = proyectoLicenciaCoaFacade.listarLicenciasPendientesActualizacion(JsfUtil.getLoggedUser().getNombre());
		proyectos.addAll(proyectosRcoa);
	}
	
	public void abrirProyecto(ProyectoCustom proyecto) {
		try {
			String tipoProyecto = proyecto.getSourceType();
			switch (tipoProyecto) {
			case "source_type_external_suia":
				Object[] proyectoSuiaVerde = proyectoSuiaVerdeFacade.getProyectoSuiaVerde(proyecto.getCodigo(), JsfUtil.getLoggedUser().getNombre());
				
				inicializarProyectoSuiaVerde(proyectoSuiaVerde, true);;
				
				proyectoSuiaVerdeBean.setEsProyectoFinalizado(false);
				proyectoSuiaVerdeBean.setEsSoloActualizacionCI(false);
				
				proyectoSuiaVerdeBean.setEsCertificadoActualizado(false);
				
				verProyectoBean.setProyecto(null);
				
				JsfUtil.redirectTo("/prevencion/actualizacionCertInterseccion/actualizarCoordenadasSuiaVerde.jsf");
				
				break;
			case "source_type_internal":
				ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(proyecto.getCodigo());
				
				verProyectoBean.setProyecto(proyectoLicenciamientoAmbiental);
				verProyectoBean.setShowModalAceptarResponsabilidad(false);
				verProyectoBean.setShowMensajeErrorGeneracionDocumentos(false);
				verProyectoBean.setShowModalErrorProcesoInterseccion(false);
				verProyectoBean.setShowModalCertificadoIntercepcion(false);
				verProyectoBean.setAlertaProyectoIntesecaZonasProtegidas("");
				verProyectoBean.setTransformarAlerta(false);
				
				proyectoSuiaVerdeBean.setProyecto(null);
				
				JsfUtil.redirectTo("/prevencion/actualizacionCertInterseccion/actualizarCoordenadas.jsf");
				
				break;
			case "source_type_rcoa":
				ProyectoLicenciaCoa proyectoRcoa = proyectoLicenciaCoaFacade.buscarProyecto(proyecto.getCodigo());
				verProyectoRcoaBean.getProyectosBean().setProyectoRcoa(proyectoRcoa);
				verProyectoRcoaBean.cargarDatos();
		        
				
				JsfUtil.redirectTo("/pages/rcoa/actualizacionCertInterseccion/actualizarCoordenadas.jsf");
				
				break;

			default:
				break;
			}
			
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}
	
	public void inicializarProyectoSuiaVerde(Object[] proyectoSuiaVerde, Boolean requiereActualizacion){
		
		proyectoSuiaVerdeBean.setResumen(proyectoSuiaVerdeFacade.getResumenProyectoSuiaVerde((String) proyectoSuiaVerde[0]).toString());
		
		proyectoSuiaVerdeBean.setCodigo((String) proyectoSuiaVerde[0]);
		proyectoSuiaVerdeBean.setNombre((String) proyectoSuiaVerde[1]);
		proyectoSuiaVerdeBean.setFechaRegistro((Date) proyectoSuiaVerde[2]);
		proyectoSuiaVerdeBean.setActividad((String) proyectoSuiaVerde[4]);
		proyectoSuiaVerdeBean.setProponente((proyectoSuiaVerde[7] == null) ? (String) proyectoSuiaVerde[8] : (String) proyectoSuiaVerde[7]);
		proyectoSuiaVerdeBean.setProyectoRequiereActualizacionCertInterseccion(requiereActualizacion);
		proyectoSuiaVerdeBean.setProyecto(proyectoSuiaVerde);
		
		
		proyectoSuiaVerdeBean.setEsActualizacionCertInterseccion(true);
		proyectoSuiaVerdeBean.setShowModalCertificadoInterseccion(false);
		proyectoSuiaVerdeBean.setShowMensajeErrorGeneracionDocumentos(false);
		proyectoSuiaVerdeBean.setShowModalErrorProcesoInterseccion(false);
	}

}
