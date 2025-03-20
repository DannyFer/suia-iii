package ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityInformeregistroprovincial;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@Stateless
public class InformeregistroprovincialBean {
    private final Logger LOG = Logger
            .getLogger(PlantillaReporte.class);
    UbicacionesGeografica canton = new UbicacionesGeografica();
    UbicacionesGeografica provincias = new UbicacionesGeografica();
    UbicacionesGeografica parroquia = new UbicacionesGeografica();
    UbicacionesGeografica cantonpro = new UbicacionesGeografica();
    UbicacionesGeografica provinciaspro = new UbicacionesGeografica();
    UbicacionesGeografica parroquiapro = new UbicacionesGeografica();
    Organizacion org = new Organizacion();
    String cedula = "";
    String direccion = "";
    List<Contacto> contacto;
    String valordireccion = "";
    String valorcorreo = "";
    String valortelefono = "";
    String representanteLegal="";
    String nombreRepresentanteLegal="";    
    Contacto con = new Contacto();
    @Getter
    @Setter
    private File informePdf;
    @Getter
    @Setter
    private byte[] archivoInforme;
    @Getter
    @Setter
    private String informePath;
    @Getter
    @Setter
    private String nombreReporte;
    @Getter
    @Setter
    private boolean verInforme;
    @EJB
    private InformeProvincialGADFacade informeProvincialGADFacade;
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;
    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    @EJB
    private PersonaFacade personaFacade;
    @EJB
    private OrganizacionFacade organizacionFacade;
    @EJB
    private ContactoFacade contactoFacade;
    @EJB
    private CategoriaIIFacade categoriaIIFacade;
	@EJB
	UsuarioFacade usuarioFacade;
    
    public String visualizarOficio(String codigo, String resolucion,
                                   String oficio, String licencia) {
        String pathPdf = null;
        String html = null;
    	String proponente = "";
    	String representante = "";

        try {
            ProyectoLicenciaAmbientalFacade dr = new ProyectoLicenciaAmbientalFacade();
            this.proyectoLicenciamientoAmbiental = proyectoLicenciaAmbientalFacade

                    .getProyectoPorCodigo(codigo.toString());

            parroquia = proyectoLicenciamientoAmbiental
                    .getProyectoUbicacionesGeograficas().get(0)
                    .getUbicacionesGeografica();
            canton = ubicacionGeograficaFacade.buscarPorId(parroquia
                    .getUbicacionesGeografica().getId());
            provincias = ubicacionGeograficaFacade.buscarPorId(canton
                    .getUbicacionesGeografica().getId());
            Usuario usuario = proyectoLicenciamientoAmbiental.getUsuario();
    		usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario.getId());
    		Persona titular = usuario.getPersona();
            org = organizacionFacade.buscarPorPersona(titular,
            		this.proyectoLicenciamientoAmbiental.getUsuario().getNombre());
            
            UbicacionesGeografica parroquiaPeople=ubicacionGeograficaFacade.buscarPorId(proyectoLicenciamientoAmbiental.getUsuario().getPersona().getIdUbicacionGeografica());
            
            if (org == null) {
            	proponente = titular.getNombre();
            	representante = proponente;
                contacto = contactoFacade
                        .buscarPorPersona(proyectoLicenciamientoAmbiental
                                .getUsuario().getPersona());
                representanteLegal="";
                nombreRepresentanteLegal=this.proyectoLicenciamientoAmbiental.getUsuario().getPersona().getNombre().toString();
            } else {
            	proponente = org.getNombre();
            	representante = org.getPersona().getNombre();
                contacto = contactoFacade.buscarPorOrganizacion(org);
                representanteLegal=org.getPersona().getNombre();
                nombreRepresentanteLegal=org.getPersona().getNombre();
            }

            cantonpro = ubicacionGeograficaFacade.buscarPorId(parroquiaPeople
                    .getId());
            for (Contacto con : contacto) {
                if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
                    valordireccion = valordireccion + con.getValor();
                }
                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
                    valorcorreo = valorcorreo + con.getValor() + " - ";
                }
                if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
                    valortelefono = valortelefono + con.getValor() + " - ";
                }
                if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
                    valortelefono = valortelefono + con.getValor() + " - ";
                }

            }
            verInforme = false;

            PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_INFORME_PROVINCIAL_GAD_SINMI.getIdTipoDocumento());
            EntityInformeregistroprovincial entityInforme = new EntityInformeregistroprovincial();
            entityInforme.setNumeroResolucion("verificarnumeroresolucion");
            entityInforme.setProvincia("provincia");
            entityInforme
                    .setNombreproyecto(this.proyectoLicenciamientoAmbiental
                            .getNombre().toString());
            entityInforme.setActividad(this.proyectoLicenciamientoAmbiental
                    .getTipoSector().toString());
            entityInforme.setCodigoproy(this.proyectoLicenciamientoAmbiental
                    .getCodigo().toString());
            entityInforme.setCantonproy(canton.getNombre().toString());
            entityInforme.setProvinciaproy(provincias.getNombre().toString());
            entityInforme.setParroquiaproy(parroquia.getNombre().toString());
            entityInforme.setCedulausu(cedula);
            entityInforme.setPromotor(proponente);
            entityInforme.setRepresentanteLegal(representanteLegal);
            entityInforme.setCantonpromotor(cantonpro.getNombre().toString());
            entityInforme.setProvinciapromotor(provincias.getNombre()
                    .toString());
            entityInforme.setSector(this.proyectoLicenciamientoAmbiental
                    .getCatalogoCategoria().getDescripcion().toString());
            entityInforme.setDireccionpromotor(valordireccion.toString());
            entityInforme.setTelefonopromotro(valortelefono.toString());
            entityInforme.setEmailpromotor(valorcorreo.toString());
            entityInforme.setRegistroambiental(licencia);
            entityInforme.setCoordenadas(categoriaIIFacade
                    .generarTablaCoordenadas(proyectoLicenciamientoAmbiental));
            SimpleDateFormat fecha = new SimpleDateFormat(
                    "dd 'de' MMMM 'de' yyyy",new Locale("es"));
//            entityInforme.setFechaactual(canton.getNombre().toString() + ", a "
//                    + fecha.format(new java.util.Date()));
            entityInforme.setFechaactual(canton.getNombre().toString() + ", a 11 de Junio de 2020");
            nombreReporte = "InformeTecnico.pdf";
            entityInforme.setNumeroResolucion(resolucion);
            entityInforme.setProvincia(provincias.getNombre().toString());
            entityInforme.setNombreRepresentanteLegal(nombreRepresentanteLegal);
            html = UtilGenerarInforme.generarFicheroHtml(
                    plantillaReporte.getHtmlPlantilla(), nombreReporte,
                    Boolean.valueOf(true), entityInforme);

            setInformePdf(informePdf);
            valordireccion="";
            valortelefono="";
            valorcorreo="";
        } catch (Exception e) {
            this.LOG.error("Error al visualizar el informe técnico", e);
            JsfUtil.addMessageError("Error al visualizar el informe técnico");
        }
        return html;
    }

}

