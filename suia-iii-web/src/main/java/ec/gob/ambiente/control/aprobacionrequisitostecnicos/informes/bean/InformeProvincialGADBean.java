package ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityInformeProvincialGAD;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class InformeProvincialGADBean {
    //	public static final String TEMP_CONST_NOMBRE_SECUENCIA_NUMERO = "seq_number_technical_report_eia";
//	public static final String TEMP_CONST_NOMBRE_ESQUEMA_SUIA = "suia_iii";
//	public static final int TEMP_CONST_TAMANIO_NUMERO_OFICIO = 6;
//	public static final String SEQ_NUMERO_REPORTE_ART = "seq_number_technical_report_art";
//	private static final long serialVersionUID = 165683211928358047L;
    private final Logger LOG = Logger.getLogger(InformeProvincialGADBean.class);
    String nombreProyecto;
    String nombreArea;
    String codigoArea;
    String nombreProponente;
    String cedulaPropononte;
    String nombreRepresentanteLegal;
    String email = "";
    String direccion = "";
    String telefono = "";
    String codigoProyecto = "";
    String parametroExante;
    String parametroExpost;
    String CodPro = "";

//	@Getter
//	@Setter
//	@ManagedProperty("#{proyectosBean}")
//	private ProyectosBean proyectosBean;
//
//	@Getter
//	@Setter
//	@ManagedProperty(value = "#{loginBean}")
//	private LoginBean loginBean;
    String Numresolucion;
    List<String> direccionRepresentanteLegal = new ArrayList<String>();
    List<String> telefonoRepresentanteLegal = new ArrayList<String>();
    List<String> emailRepresentanteLegal = new ArrayList<String>();
    UbicacionesGeografica canton = new UbicacionesGeografica();
    UbicacionesGeografica provincia = new UbicacionesGeografica();
    String sector;
    Organizacion proponente = new Organizacion();
    Contacto contacto = new Contacto();
    @EJB
    ContactoFacade contactoFacade;
    List<Contacto> listContacto = new ArrayList<Contacto>();
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
    private String nombreReporte = "jhadskjdhkjashd";
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;
    @Getter
    @Setter
    private Usuario usuario;
    @Getter
    @Setter
    private Organizacion organizacion;
    @Getter
    @Setter
    private UbicacionesGeografica parroquia;
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    @EJB
    private InformeProvincialGADFacade informeProvincialGADFacade;  //informeOficioFacade;
    @EJB
    private OrganizacionFacade organizacionFacade;
    @EJB
    private UsuarioFacade usuarioFace;
    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
    @Getter
    @Setter
    private boolean verInforme;
    @EJB
    private CategoriaIIFacade categoriaIIFacade;

    @PostConstruct
    public void init() {
        try {
            proyectoLicenciaAmbientalFacade.getProyectoPorCodigo("MAE-RA-2015-207296");
            proyectoLicenciamientoAmbiental = proyectoLicenciaAmbientalFacade.getProyectoPorCodigo("MAE-RA-2015-207296");
            nombreArea = proyectoLicenciamientoAmbiental.getCatalogoCategoria().getDescripcion().toString();
            codigoArea = proyectoLicenciamientoAmbiental.getCatalogoCategoria().getId().toString();
            codigoProyecto = proyectoLicenciamientoAmbiental.getCodigo();
            proponente = organizacionFacade.buscarPorPersona(proyectoLicenciamientoAmbiental.getUsuario().getPersona());
            if (proponente != null) {
                nombreProponente = proponente.getNombre();
                cedulaPropononte = proponente.getPersona().getPin();
                listContacto = contactoFacade.buscarPorOrganizacion(proponente);
            } else {
                nombreProponente = proyectoLicenciamientoAmbiental.getUsuario().getPersona().getNombre().toString();
                listContacto = contactoFacade.buscarPorPersona(proyectoLicenciamientoAmbiental.getUsuario().getPersona());
            }
            sector = proyectoLicenciamientoAmbiental.getTipoSector().getNombre();
            nombreRepresentanteLegal = proyectoLicenciamientoAmbiental.getUsuario().getPersona().getNombre().toString();
            parroquia = proyectoLicenciamientoAmbiental.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
            canton = ubicacionGeograficaFacade.buscarPorId(parroquia.getUbicacionesGeografica().getId());
            provincia = ubicacionGeograficaFacade.buscarPorId(canton.getUbicacionesGeografica().getId());

            parametroExante = proyectoLicenciamientoAmbiental.getTipoEstudio().getId().toString();

            if (parametroExante.equals("1")) {
                parametroExpost = "60 días";
            } else if (parametroExante.equals("2")) {
                parametroExpost = "30 días";
            } else {
                parametroExpost = "";
            }


            verInforme = false;
            visualizarOficio("MAE-RA-2015-207296", "123", "123", "asdasd");
        } catch (Exception e) {
            this.LOG.error("Error al inicializar: InformeTecnicoArtBean: ", e);
            JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
        }
    }

    public void verInformePdf(String codigoProyecto, String resolucion, String oficio, String licencia) {
        CodPro = codigoProyecto;
        Numresolucion = resolucion;
        init();
    }

    public String visualizarOficio(String codigoProyecto, String resolucion, String oficio, String licencia) {
        CodPro = codigoProyecto;
        Numresolucion = resolucion;

        try {
            proyectoLicenciaAmbientalFacade.getProyectoPorCodigo("MAE-RA-2015-207296");
            proyectoLicenciamientoAmbiental = proyectoLicenciaAmbientalFacade.getProyectoPorCodigo("MAE-RA-2015-207296");
            nombreArea = proyectoLicenciamientoAmbiental.getCatalogoCategoria().getDescripcion().toString();
            codigoArea = proyectoLicenciamientoAmbiental.getCatalogoCategoria().getId().toString();
            codigoProyecto = proyectoLicenciamientoAmbiental.getCodigo();
            proponente = organizacionFacade.buscarPorPersona(proyectoLicenciamientoAmbiental.getUsuario().getPersona());
            if (proponente != null) {
                nombreProponente = proponente.getNombre();
                cedulaPropononte = proponente.getPersona().getPin();
                listContacto = contactoFacade.buscarPorOrganizacion(proponente);
            } else {
                nombreProponente = proyectoLicenciamientoAmbiental.getUsuario().getPersona().getNombre().toString();
                listContacto = contactoFacade.buscarPorPersona(proyectoLicenciamientoAmbiental.getUsuario().getPersona());
            }
            sector = proyectoLicenciamientoAmbiental.getTipoSector().getNombre();
            nombreRepresentanteLegal = proyectoLicenciamientoAmbiental.getUsuario().getPersona().getNombre().toString();
            parroquia = proyectoLicenciamientoAmbiental.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
            canton = ubicacionGeograficaFacade.buscarPorId(parroquia.getUbicacionesGeografica().getId());
            provincia = ubicacionGeograficaFacade.buscarPorId(canton.getUbicacionesGeografica().getId());

            parametroExante = proyectoLicenciamientoAmbiental.getTipoEstudio().getId().toString();

            if (parametroExante.equals("1")) {
                parametroExpost = "60 días";
            } else if (parametroExante.equals("2")) {
                parametroExpost = "30 días";
            } else {
                parametroExpost = "";
            }


            verInforme = false;
        } catch (Exception e) {
            this.LOG.error("Error al inicializar: InformeTecnicoArtBean: ", e);
            JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
        }

        String pathPdf = null;
        String Html = null;
        try {
            String MineriaArtesanal = "LAS LABORES DE MINERÍA ARTESANAL PARA LA EXPLOTACIÓN,";
            String MineriaConstruccion = "LAS LABORES DE MINERÍA ARTESANAL PARA LA EXPLOTACIÓN DE MATERIALES DE CONSTRUCCIÓN DE LA CONCESIÓN MINERA,";
            PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_INFORME_PROVINCIAL_GAD.getIdTipoDocumento());

            EntityInformeProvincialGAD entityInforme = new EntityInformeProvincialGAD();

            entityInforme.setParametroNombreProyecto(proyectoLicenciamientoAmbiental.getNombre());
            entityInforme.setParametroAutomatico(Numresolucion);
            entityInforme.setParametroArt1Normal(MineriaArtesanal + " EN EL ÁREA DENOMINADA " + nombreArea + " " + codigoArea + ", UBICADA EN LA PROVINCIA DE " + provincia);
            entityInforme.setParametroArt2Normal(MineriaArtesanal + " EN EL ÁREA DENOMINADA " + nombreArea + " " + codigoArea + "A FAVOR DEL " + nombreProponente + ", UBICADA EN LA PROVINCIA DE " + provincia);
            entityInforme.setParametroArt3Normal(MineriaArtesanal + " EN EL ÁREA DENOMINADA " + nombreArea + " " + codigoArea + "A FAVOR DEL " + nombreProponente + ", UBICADA EN LA PROVINCIA DE " + provincia);
            entityInforme.setParametroProvincial("de " + provincia);
            entityInforme.setParametroTitularMinero(nombreProponente);
            entityInforme.setParametroArt4Normal(MineriaArtesanal + " EN EL ÁREA DENOMINADA " + nombreArea + " " + codigoArea + "A FAVOR DEL " + nombreProponente + ", UBICADA EN LA PROVINCIA DE " + provincia);
            entityInforme.setParametroTitularMinero(nombreProponente);
            entityInforme.setParametroCodigo("numero pasan ellos");
            entityInforme.setParametroDescripcionActividad(nombreArea);
            entityInforme.setParametroSector(sector);
            entityInforme.setParametroUbicacion(provincia + ", " + canton + ", " + parroquia);
            entityInforme.setParametroRepresentanteLegal(nombreRepresentanteLegal);
            entityInforme.setParametroCoordenadas(categoriaIIFacade.generarTablaCoordenadas(proyectoLicenciamientoAmbiental));
            SimpleDateFormat fecha = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
            entityInforme.setParametroFecha("" + fecha.format(proyectoLicenciamientoAmbiental.getFechaRegistro()));
            entityInforme.setParametroNumOficio("ponen ellos");

            for (Contacto cont : listContacto) {
                if (cont.getFormasContacto().getId() == 2 && cont.getEstado().equals(true))
                    direccionRepresentanteLegal.add(cont.getValor());
                if (cont.getFormasContacto().getId() == 6 && cont.getEstado().equals(true))
                    telefonoRepresentanteLegal.add(cont.getValor());
                if (cont.getFormasContacto().getId() == 5 && cont.getEstado().equals(true))
                    emailRepresentanteLegal.add(cont.getValor());
            }

            for (int i = 0; i < telefonoRepresentanteLegal.size(); i++) {
                if (i == 0)
                    telefono = telefonoRepresentanteLegal.get(i);
                else
                    telefono = telefono + ", " + telefonoRepresentanteLegal.get(i);
            }
            for (int i = 0; i < direccionRepresentanteLegal.size(); i++) {
                if (i == 0)
                    direccion = direccionRepresentanteLegal.get(i);
                else
                    direccion = direccion + ", " + direccionRepresentanteLegal.get(i);
            }
            entityInforme.setParametroDireccionRepresentanteLegal(direccion);
            entityInforme.setParametroTelefonoRepresentanteLegal(telefono);
            for (int i = 0; i < emailRepresentanteLegal.size(); i++) {
                if (i == 0)
                    email = emailRepresentanteLegal.get(i);
                else
                    email = email + ", " + emailRepresentanteLegal.get(i);
            }
            entityInforme.setParametroEmailRepresentanteLegal(email);
            entityInforme.setParametroCodigoProyecto(codigoProyecto);
            entityInforme.setParametroArt5Normal(MineriaArtesanal + " EN EL ÁREA DENOMINADA " + nombreArea + " " + codigoArea + "A FAVOR DEL " + nombreProponente + ", UBICADA EN LA PROVINCIA DE " + provincia);
            entityInforme.setParametroExpost(parametroExpost);
            entityInforme.setParametrocedulaProponente(cedulaPropononte);
            entityInforme.setParametroCanton(canton.toString());

            nombreReporte = "InformeTecnico.pdf";
            File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
                    Boolean.valueOf(true), entityInforme);
//            File informePdf1 = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
//                    Boolean.valueOf(true), entityInforme);

            Html = UtilGenerarInforme.generarFicheroHtml(plantillaReporte.getHtmlPlantilla(), nombreReporte,
                    Boolean.valueOf(true), entityInforme);


            setInformePdf(informePdf);

            Path path = Paths.get(getInformePdf().getAbsolutePath(), new String[0]);
            this.archivoInforme = Files.readAllBytes(path);
            File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(getInformePdf().getName()));
            FileOutputStream file = new FileOutputStream(archivoFinal);
            file.write(this.archivoInforme);
            file.close();

            setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + getInformePdf().getName()));
            //System.out.println("informePath: "+plantillaReporte.getHtmlPlantilla());
            pathPdf = informePdf.getParent();
        } catch (Exception e) {
            this.LOG.error("Error al visualizar el informe técnico", e);
            JsfUtil.addMessageError("Error al visualizar el informe técnico");
        }
        return Html;
    }
}
