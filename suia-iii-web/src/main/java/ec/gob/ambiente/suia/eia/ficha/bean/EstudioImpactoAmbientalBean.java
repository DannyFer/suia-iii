package ec.gob.ambiente.suia.eia.ficha.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.EIAGeneralBean;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@Getter
@ManagedBean(name = "eia")
@ViewScoped
public class EstudioImpactoAmbientalBean implements Serializable {

    private static final long serialVersionUID = -2501697438997887700L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(EstudioImpactoAmbientalBean.class);
    public static Integer PROJECT_CODE = 123;
    public static Integer ID = 123;
    public static Integer PROCESS_ID = 112;
    public static String PROCESS_NAME = "EIA";
    @EJB
    ValidacionSeccionesFacade validacionSeccionesFacade;
    @Getter
    @Setter
    String nombreReporte = "";
    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoFacade;
    @EJB
    private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
    @EJB
    private EiaOpcionesFacade eiaOpcionesFacade;
    @Getter
    @Setter
    private String mensaje;
    @Getter
    @Setter
    private List<String> listaMensaje;
    @Getter
    @Setter
    private List<String> seccionesRequeridas;
    @Getter
    @Setter
    private List<String> secciones;
    @Getter
    @Setter
    private boolean revision;
    @Getter
    @Setter
    private boolean mostrarInforme;
    @Getter
    @Setter
    private String informePath = "";
    @Getter
    @Setter
    private String idAlfresco = "";
    @Getter
    @Setter
    private byte[] documento;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @ManagedProperty(value = "#{loginBean}")
    @Getter
    @Setter
    private LoginBean loginBean;

    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    private EstudioImpactoAmbiental estudio;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    @Getter
    @Setter
    private boolean completado;
    
    @Getter
    private Boolean esMineriaNoMetalicos;

    @PostConstruct
    public void cargarDatos() {
        setEstudio(new EstudioImpactoAmbiental());
        if (proyectosBean.getProyecto() != null&& proyectosBean.getProyecto().getId() != null) {
            cargarDatosBandeja();
            cargarDocumentoObservaciones();
        }
        
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.getSession(false).setAttribute("estudio", estudio); 
        
    }

    private void cargarDatosBandeja() {
        proyecto = proyectosBean.getProyecto();
        try {
            setEstudio(estudioImpactoAmbientalFacade
                    .obtenerPorProyecto(proyecto));
            
            if (getEstudio() == null) {
                if (proyectosBean.getProyecto().getId() != null) {
                    setEstudio(new EstudioImpactoAmbiental());
                    getEstudio().setProyectoLicenciamientoAmbiental(proyecto);
                    EstudioImpactoAmbiental estudioImpactoAmbiental = estudioImpactoAmbientalFacade
                            .guardar(estudio);
                    JsfUtil.cargarObjetoSession(Constantes.SESSION_EIA_OBJECT,
                            estudioImpactoAmbiental);
                } else {
                    setMensaje("Debe seleccionar un proyecto");
                }
            } else {
                getEstudio().setProyectoLicenciamientoAmbiental(proyecto);
                JsfUtil.cargarObjetoSession(Constantes.SESSION_EIA_OBJECT,
                        getEstudio());
            }
            esMineriaNoMetalicos=estudio.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudio.getResumenEjecutivo()==null?true:false;
            cargarDatosRequeridos();

            validarCompleado();

        } catch (Exception e) {
            LOG.error(e, e);
        }
    }


    public void cargarDatosRequeridos() {

        seccionesRequeridas = new ArrayList<>();
        seccionesRequeridas.add("resumenEjecutivo");
        seccionesRequeridas.add("fichaTecnicaEIA");

        if(!esMineriaNoMetalicos){
        	seccionesRequeridas.add("adjuntos");
        }
        	
        seccionesRequeridas.add("introduccion");
        seccionesRequeridas.add("marcoLegalEia");

        if(!esMineriaNoMetalicos)
        {
        	seccionesRequeridas.add("definicionAreaEstudio");
        }        

        /*
        seccionesRequeridas.add("clima");
        seccionesRequeridas.add("hidrologia");
        seccionesRequeridas.add("fisicoMecanicasSuelos");


        seccionesRequeridas.add("calidadAgua");
        seccionesRequeridas.add("calidadAire");
        seccionesRequeridas.add("calidadSuelo");
        */

        //medio biotico
        /*
        seccionesRequeridas.add("flora");
        seccionesRequeridas.add("mastofauna");
        seccionesRequeridas.add("ornitofauna");
        seccionesRequeridas.add("herpetofauna");
        seccionesRequeridas.add("entomofauna");
        seccionesRequeridas.add("ictiofauna");
        seccionesRequeridas.add("macroinvertebrados");
        seccionesRequeridas.add("identificacionSitiosContaminadosFuentesContaminacion");
        */

       /*  * */


        seccionesRequeridas.add("adjuntos12");
        seccionesRequeridas.add("adjuntos14");
        seccionesRequeridas.add("adjuntos13");


        seccionesRequeridas.add("descripcionProyectoObraActividad");

        if(!(esMineriaNoMetalicos && estudio.getProyectoLicenciamientoAmbiental().getTipoEstudio().getId()==2))
        {
        	seccionesRequeridas.add("analisisAlternativas");
        }

        seccionesRequeridas.add("areaInfluencia");

        seccionesRequeridas.add("inventarioForestal");

        seccionesRequeridas.add("identificacionEvaluacionImpactos");

        if (proyecto.getTipoEstudio().getId() == 2) {
            seccionesRequeridas.add("identificacionHallazgos");
            seccionesRequeridas.add("planHallazgos");
        }

        seccionesRequeridas.add("analisisRiesgo");

        seccionesRequeridas.add("planManejoAmbiental");
        seccionesRequeridas.add("planMonitoreo");
        seccionesRequeridas.add("cronogramaPma");

        seccionesRequeridas.add("anexos5");

    }


    public Boolean seccionCompletada(String seccion) {
    		return secciones.contains(seccion);
    }

    public void validarCompleado() {
        secciones = validacionSeccionesFacade.listaSeccionesPorClase("EIA", estudio.getId().toString());        
//        validarAnexos();
        completado = true;
        int i = seccionesRequeridas.size();
        while (completado && i > 0) {
            if (!secciones.contains(seccionesRequeridas.get(--i))) {
                completado = false;
            }
        }

    }


    public String verEIA() {
        return JsfUtil.getBean(EIAGeneralBean.class).actionVerEIA(estudio, false, true, false);

    }
    
    public String verEIA2(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) {
    	proyecto=proyectoLicenciamientoAmbiental;
    	proyectosBean.setProyecto(proyecto);
    	cargarDatosBandeja();
    	cargarDatosRequeridos();
    	validarCompleado();
        return "/prevencion/licenciamiento-ambiental/eia/resumenEjecutivo/resumenEjecutivoVer.jsf";

    }


    public void cargarDocumentoObservaciones() {

        revision = false;
        List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), LicenciaAmbientalFacade.class.getSimpleName(), TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_EIA);

        if (documentos != null && !documentos.isEmpty()) {
            revision = true;
            idAlfresco = documentos.get(0).getIdAlfresco();
        }

    }

    public void mostrarDocumentoObeservaciones() {
        String nombreFichero = "InformeObservacion" + proyecto.getId() + ".pdf";
        nombreReporte = "InformeObservacion.pdf";

        mostrarInforme = true;
        try {
            if (documento == null) {
                documento = documentosFacade.descargar(idAlfresco);

                File archivoFinal = new File(
                        JsfUtil.devolverPathReportesHtml(nombreFichero));

                FileOutputStream fos = new FileOutputStream(archivoFinal.getAbsolutePath());
                fos.write(documento);
                fos.close();

                setInformePath(JsfUtil.devolverContexto("/reportesHtml/"
                        + nombreFichero));

            }
        } catch (CmisAlfrescoException e) {
            LOG.error(e);
        } catch (FileNotFoundException e) {
            LOG.error(e);
        } catch (IOException e) {
            LOG.error(e);
        }


    }

    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documento != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documento), "application/pdf");
            content.setName(nombreReporte);

        }
        return content;

    }
    
	private void validarAnexos() {
    	List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(estudio.getId(), "EstudioImpactoAmbiental",
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS);
		int cont = 0;
		if (documentos.size() > 0) {

			for (Documento doc : documentos) {
				if (doc.getDescripcion().equals("Certificado de depósitos de especímenes (obligatorio)")
						|| doc.getDescripcion().equals("Glosario de Términos (obligatorio)") 
						|| doc.getDescripcion().equals("Estudio de Impacto Ambiental (obligatorio)")) {
					cont += 1;
				}
			}
			if (cont < 3) {
				secciones.remove("anexos5");
			} else {
				secciones.add("anexos5");
			}
		} else {
			secciones.remove("anexos5");
		}
	}
}
