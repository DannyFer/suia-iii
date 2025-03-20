package ec.gob.ambiente.prevencion.actualizacionPma.bean;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.VerProyectoController;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.integracion.bean.ContenidoExterno;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ManagedBean
@SessionScoped
public class ActualizacionPMABean implements Serializable {

	private static final long serialVersionUID = 6121157311685351281L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ActualizacionPMABean.class);

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
	private String proponente;

	@Setter
	private List<UbicacionesGeografica> ubicacionProponente;

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

}
