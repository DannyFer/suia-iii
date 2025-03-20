package ec.gob.ambiente.prevencion.participacionsocial.bean;

import ec.gob.ambiente.control.denuncias.bean.DenunciaBean;
import ec.gob.ambiente.control.denuncias.controllers.DenunciaController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.InformeReunionInformacionFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.MecanismosParticipacionSocialFacade;
import ec.gob.ambiente.suia.prevencion.participacionsocial.facade.ParticipacionSocialFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ViewScoped
@ManagedBean
public class InformeReunionInformacionBean implements Serializable {


    private static final Logger LOGGER = Logger
            .getLogger(InformeReunionInformacionBean.class);

    private static final long serialVersionUID = -3296283306112193322L;
    @Getter
    @Setter
    List<MecanismoParticipacionSocialAmbiental> listaMecanismos;
    @EJB
    private ParticipacionSocialFacade participacionSocialFacade;
    @EJB
    private InformeReunionInformacionFacade informeReunionInformacionFacade;
    @EJB
    @Getter
    private MecanismosParticipacionSocialFacade mecanismosParticipacionSocialFacade;
    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{denunciaController}")
    private DenunciaController denunciaController;
    @Getter
    @Setter
    @ManagedProperty(value = "#{denunciaBean}")
    private DenunciaBean denunciaBean;
    @Setter
    @Getter
    private CatalogoMediosParticipacionSocial catalogoMedio;
    @Setter
    @Getter
    private List<CatalogoMediosParticipacionSocial> catalogoMediosParticipacionSociales;
    @Setter
    @Getter
    private RegistroMediosParticipacionSocial registroMediosParticipacionSocial;
    @Setter
    @Getter
    private List<RegistroMediosParticipacionSocial> registrosMediosParticipacionSocial;
    @Setter
    @Getter
    private ParticipacionSocialAmbiental participacionSocialAmbiental;
    @Getter
    @Setter
    private String documentoActivo = "";
    @Getter
    @Setter
    private Map<String, Documento> documentos;
    @Getter
    @Setter
    private List<String> listaClaves;
    @Getter
    @Setter
    private boolean revisar;
    @Getter
    @Setter
    private boolean isEditing;
    @Getter
    @Setter
    private Boolean informacionCompleta;
    @Getter
    @Setter
    private String tipo = "";
    @Getter
    @Setter
    private MecanismoParticipacionSocialAmbiental mecanismo;
    @Getter
    @Setter
    private List<MecanismoParticipacionSocialAmbiental> listaMecanismosEliminados;

    @PostConstruct
    public void init() {

        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
        if (params.containsKey("tipo")) {
            tipo = params.get("tipo");
            if (params.get("tipo").equals("revisar") || params.get("tipo").equals("revisarDatos")) {
                revisar = true;

            }
        }

        setCatalogoMediosParticipacionSociales(informeReunionInformacionFacade.getCatalogoMediosParticipacionSocial());
        if (proyectosBean.getProyecto() != null) {
            participacionSocialAmbiental = participacionSocialFacade.buscarCrearParticipacionSocialAmbiental(proyectosBean.getProyecto());
            loadDataTable();
        }
        registroMediosParticipacionSocial = new RegistroMediosParticipacionSocial();

        listaClaves = new ArrayList<>(1);
        listaClaves.add("respaldoInformeReunionPPS");
        documentos = new HashMap<>();

        try {
            documentos = participacionSocialFacade.recuperarDocumentosTipo(proyectosBean.getProyecto().getId(), RegistroMediosParticipacionSocial.class.getSimpleName(), listaClaves);
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }


        this.listaMecanismosEliminados = new ArrayList<MecanismoParticipacionSocialAmbiental>();
    }

    public void loadDataTable() {
        registrosMediosParticipacionSocial = informeReunionInformacionFacade.getRecordsByProjectId(this.participacionSocialAmbiental);

        listaMecanismos = new ArrayList<MecanismoParticipacionSocialAmbiental>();
        listaMecanismos = mecanismosParticipacionSocialFacade.consultar(participacionSocialAmbiental);

    }


    public void crearMecanismo() {
        this.isEditing = false;
        this.mecanismo = new MecanismoParticipacionSocialAmbiental();
        this.mecanismo.setCatalogoMedio(new CatalogoMediosParticipacionSocial());
        this.mecanismo.setCatalogoMedio(new CatalogoMediosParticipacionSocial());
        this.mecanismo.getCatalogoMedio().setId(19);
        this.mecanismo.setParticipacionSocialAmbiental(this.participacionSocialAmbiental);
        this.denunciaBean.setProvincia(null);
        this.denunciaBean.setCanton(null);
        this.denunciaBean.setParroquia(null);
    }

    public void editarMecanismo(MecanismoParticipacionSocialAmbiental mecanismo) {
        this.isEditing = true;
        this.mecanismo = mecanismo;

        this.denunciaBean.setParroquia(this.mecanismo.getUbicacionesGeografica());
        this.denunciaBean.setCanton(this.mecanismo.getUbicacionesGeografica().getUbicacionesGeografica());
        this.denunciaBean.setProvincia(this.mecanismo.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica());

        this.denunciaController.cargarCantones();
        this.denunciaController.cargarParroquias();
    }

    public void eliminarMecanismo(MecanismoParticipacionSocialAmbiental mecanismo) {
        this.isEditing = false;
        this.listaMecanismos.remove(mecanismo);
        this.listaMecanismosEliminados.add(mecanismo);
    }


    public void agregarMecanismo() {

        this.mecanismo.setUbicacionesGeografica(this.denunciaBean.getParroquia());
        if (!this.isEditing) {

            this.listaMecanismos.add(this.mecanismo);
        }

        RequestContext.getCurrentInstance().execute(
                "PF('dlg2').hide();");

    }
}
