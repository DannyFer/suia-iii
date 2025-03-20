package ec.gob.ambiente.proyectos.bean;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.VerProyectoController;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.proyecto.controller.VerProyectoRcoaBean;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.integracion.bean.ContenidoExterno;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.LazyDataModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ManagedBean
@SessionScoped
public class ProyectosBean implements Serializable {

	private static final long serialVersionUID = 6121157311685351281L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ProyectosBean.class);

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	@Setter
	private ProyectoCustom proyectoCustom;

	@Setter
	private List<ProyectoCustom> proyectos;

	@Setter
	private List<ProyectoCustom> proyectosNoFinalizados;

	@Getter
	private List<String> categoriasItems;

	@Getter
	private List<String> sectoresItems;
	
	@Getter
    @Setter
    private LazyDataModel<ProyectoCustom> proyectosArchivadosLazy;

	@Getter
	@Setter
	private String proponente;

	@Setter
	private List<UbicacionesGeografica> ubicacionProponente;
	
	//Cris F: Cargar correo y teléfono variables
	@Getter
	@Setter
	private String correo;
	
	@Getter
	@Setter
	private String numeroTelefonico;
	
	@Getter
	@Setter
	private String celular;
	
	@Getter
	@Setter
	private String representanteLegal;
	
	//RCOA
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoRcoa;
	
	@Getter
	@Setter
	private Boolean modificarProyectoRcoa = false;
	
	@Getter
	@Setter
	private Integer tipoProyectoRcoa;
	
	@Getter
	@Setter
	private Boolean esArchivado = false;
	
	@Getter
	@Setter
	private boolean ambienteProduccion;
	
	@Getter
	@Setter
	private String codRGDs;
	
	@Getter
	@Setter
	private String numAAA;
	
	@Getter
	@Setter
	private Integer flujoRLA;
	
	@Getter
    @Setter
    public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	//Fin
	
	public void setProyectoToShow(ProyectoLicenciamientoAmbiental proyecto) throws CmisAlfrescoException {
		this.proyecto = proyecto;
		JsfUtil.getBean(VerProyectoController.class).verProyecto(proyecto.getId());
	}

	public void setProyectoToEdit(ProyectoLicenciamientoAmbiental proyecto) throws CmisAlfrescoException {
		this.proyecto = proyecto;
		JsfUtil.getBean(VerProyectoController.class).verProyecto(proyecto.getId(), true);
	}

	@PostConstruct
	public void init() {
		proyecto = new ProyectoLicenciamientoAmbiental();
		proyectos = new ArrayList<ProyectoCustom>();
		sectoresItems = new ArrayList<String>();
		categoriasItems = new ArrayList<String>();

		Iterator<Categoria> iteratorCategorias = proyectoLicenciamientoAmbientalFacade.getCategorias().iterator();
		Iterator<TipoSector> iteratorSectores = proyectoLicenciamientoAmbientalFacade.getTiposSectores().iterator();

		while (iteratorSectores.hasNext()) {
			TipoSector tipoSector = iteratorSectores.next();
			sectoresItems.add(tipoSector.toString());
		}

		while (iteratorCategorias.hasNext()) {
			Categoria categoria = iteratorCategorias.next();
			if (!categoriasItems.contains(categoria.getNombrePublico()))
				categoriasItems.add(categoria.getNombrePublico());
		}
		
		ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
	}

	public List<ProyectoCustom> getProyectos() {
		return proyectos == null ? proyectos = new ArrayList<ProyectoCustom>() : proyectos;
	}

	public List<ProyectoCustom> getProyectosNoFinalizados() {
		return proyectosNoFinalizados == null ? proyectosNoFinalizados = new ArrayList<ProyectoCustom>()
				: proyectosNoFinalizados;
	}

	public List<UbicacionesGeografica> getUbicacionProponente() {
		return ubicacionProponente == null ? ubicacionProponente = new ArrayList<UbicacionesGeografica>()
				: ubicacionProponente;
	}
    public String seleccionar(ProyectoCustom proyectoCustom) {
        switch (proyectoCustom.getSourceType()) {
            case ProyectoCustom.SOURCE_TYPE_INTERNAL:

                try {
                    ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
                            .buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyectoCustom.getId()));
                    setProyectoToShow(proyecto);
                    return JsfUtil.actionNavigateTo("/proyectos/resumenProyecto.jsf");
                } catch (Exception e) {
                    LOG.error(e.getMessage(),e);
                    JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
                    return null;
                }

            case ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA:

                JsfUtil.getBean(ContenidoExterno.class).executeAction(proyectoCustom,
                        IntegracionFacade.IntegrationActions.mostrar_dashboard);

                break;
        }

        return null;
    }


	public String seleccionar(ProyectoLicenciamientoAmbiental proyectoCustom) {

		try {
			ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
					.buscarProyectosLicenciamientoAmbientalPorId( proyectoCustom.getId());
			setProyectoToShow(proyecto);
			return JsfUtil.actionNavigateTo("/proyectos/resumenProyecto.jsf");
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return "";
		}


	}


	public String seleccionarPublico(ProyectoLicenciamientoAmbiental proyectoCustom) {

		try {
			if(proyectoCustom != null) {
				ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(proyectoCustom.getId());
				setProyectoToShow(proyecto);
				return  "/resumenProyecto.jsf";
			}else{
				return "";
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return "";
		}


	}
	
	//Scout Drilling
		public void setProyectoSD(ProyectoLicenciamientoAmbiental proyecto) throws CmisAlfrescoException {
			this.proyecto = proyecto;
//			JsfUtil.getBean(RevisionFichaMineriaSDController.class).verProyecto(proyecto);
		}
		
		public String seleccionarVistaSD(ProyectoLicenciamientoAmbiental proyectoSD) {

			try {
				ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(proyectoSD.getId());
				setProyectoToShow(proyecto);
				return JsfUtil.actionNavigateTo("/prevencion/categoria2/v2/fichaMineria020/fichaMineria020Ver.jsf");
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return "";
			}


		}

		public String seleccionar(ProyectoLicenciaCoa proyectoLicenciaCoa, Boolean viewMenu) {
			try{
				proyectoRcoa = proyectoLicenciaCoa;
				if(viewMenu)
					return JsfUtil.actionNavigateTo("/proyectos/resumenProyectoRcoa.jsf");
				else
					return JsfUtil.actionNavigateTo("/proyectos/proyectoRcoaDatos.jsf"); //vista sin menu
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return "";
			}
		}
		
		public String ingresarObservacionesDiagnostico(ProyectoLicenciaCoa proyectoLicenciaCoa) {
			try{
				proyectoRcoa = proyectoLicenciaCoa;
				JsfUtil.getBean(VerProyectoRcoaBean.class).cargarDatos();
				return JsfUtil.actionNavigateTo("/pages/rcoa/revisionPreliminar/observacionesInformacionPreliminar.jsf"); //vista sin menu
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return "";
			}
		}
		
		public String ingresarObservacionesRevisionEIA(ProyectoLicenciaCoa proyectoLicenciaCoa) {
			try{
				proyectoRcoa = proyectoLicenciaCoa;
				return JsfUtil.actionNavigateTo("/pages/rcoa/estudioImpactoAmbiental/default.jsf"); 
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return "";
			}
		}
		
		public String seleccionarPlaguicidas(Boolean viewMenu) {
			try{
				if(viewMenu) {
					return JsfUtil.actionNavigateTo("/pages/rcoa/plaguicidasQuimicosUsoAgricola/proyectoVisualizacion/verResumenProyecto.jsf");
				} else {
					return JsfUtil.actionNavigateTo("/pages/rcoa/plaguicidasQuimicosUsoAgricola/proyectoVisualizacion/verProyectoPlaguicidas.jsf");
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return "";
			}
		}
}
