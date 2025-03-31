package ec.gob.ambiente.suia.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean.InformeProvincialGADBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.dto.EntityInformeProvincialGAD;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;


@Stateless
public class ReporteGADMunicipal {
    private final Logger LOG = Logger.getLogger(InformeProvincialGADBean.class);

    String nombreProyecto;
    String nombreArea;
    String codigoArea;
    String nombreRepresentanteLegal;
    String email = "";
    String direccion = "";
    String telefono = "";
    String codigoProyecto = "";
    String parametroExante;
    String parametroExpost;
    String CodPro = "";
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

    @EJB
    private CategoriaIIFacade categoriaIIFacade;

    @Getter
    @Setter
    private UbicacionesGeografica parroquia;

    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;

    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

    @EJB
    private OrganizacionFacade organizacionFacade;

    @EJB
    private InformeProvincialGADFacade informeProvincialGADFacade;

    @Getter
    @Setter
    private String informePath;

    @Getter
    @Setter
    private boolean verInforme;

    @Getter
    @Setter
    private byte[] archivoInforme;

    @Getter
    @Setter
    private String nombreReporte;

    @Getter
    @Setter
    private File informePdf;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;


    public String visualizarOficio(String codigoProyecto, String resolucion, String oficio, String licencia) {

        Organizacion org = new Organizacion();
        UbicacionesGeografica parroquiapro = new UbicacionesGeografica();
        UbicacionesGeografica provinciaspro = new UbicacionesGeografica();
        String proponente = "";
        String cedula = "";
        CodPro = codigoProyecto;
        List<Contacto> contacto = null;
        Numresolucion = resolucion;
        direccionRepresentanteLegal = new ArrayList<String>();
        telefonoRepresentanteLegal = new ArrayList<String>();
        emailRepresentanteLegal = new ArrayList<String>();
        email = "";
        direccion = "";
        telefono = "";

        try {
            proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
            proyectoLicenciamientoAmbiental = proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
            nombreArea = proyectoLicenciamientoAmbiental.getCatalogoCategoria().getDescripcion().toString();
            codigoArea = proyectoLicenciamientoAmbiental.getCatalogoCategoria().getId().toString();
            codigoProyecto = proyectoLicenciamientoAmbiental.getCodigo();

            parroquia = proyectoLicenciamientoAmbiental
                    .getProyectoUbicacionesGeograficas().get(0)
                    .getUbicacionesGeografica();
            canton = ubicacionGeograficaFacade.buscarPorId(parroquia
                    .getUbicacionesGeografica().getId());

            org = organizacionFacade.buscarPorPersona(proyectoLicenciamientoAmbiental.getUsuario().getPersona(),proyectoLicenciamientoAmbiental.getUsuario().getNombre().toString());

            if (org == null) {
                proponente = this.proyectoLicenciamientoAmbiental.getUsuario().getPersona().getNombre().toString();
                cedula = this.proyectoLicenciamientoAmbiental.getUsuario()
                        .getPin().toString();
                contacto = contactoFacade
                        .buscarPorPersona(proyectoLicenciamientoAmbiental
                                .getUsuario().getPersona());
            } else {
                proponente = org.getNombre();
                cedula = org.getRuc();
                contacto = contactoFacade.buscarPorOrganizacion(org);
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
            PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_MINIRIA_MUNICIPAL_GAD.getIdTipoDocumento());

            EntityInformeProvincialGAD entityInforme = new EntityInformeProvincialGAD();
            entityInforme.setAreaResponsable(proyectoLicenciamientoAmbiental.getAreaResponsable().getAreaAbbreviation());
            entityInforme.setParametroNombreProyecto(proyectoLicenciamientoAmbiental.getNombre());
            entityInforme.setParametroAutomatico(Numresolucion);
            entityInforme.setParametroArt1Normal(MineriaArtesanal + " EN EL ÁREA DENOMINADA " + nombreArea + " " + codigoArea + ", UBICADA EN LA PROVINCIA DE " + provincia);
            entityInforme.setParametroArt2Normal(MineriaArtesanal + " EN EL ÁREA DENOMINADA " + nombreArea + " " + codigoArea + "A FAVOR DEL " + proponente + ", UBICADA EN LA PROVINCIA DE " + provincia);
            entityInforme.setParametroArt3Normal(MineriaArtesanal + " EN EL ÁREA DENOMINADA " + nombreArea + " " + codigoArea + "A FAVOR DEL " + proponente + ", UBICADA EN LA PROVINCIA DE " + provincia);
            entityInforme.setParametroProvincial("de " + provincia);
            entityInforme.setParametroTitularMinero(proponente);
            entityInforme.setParametroArt4Normal(MineriaArtesanal + " EN EL ÁREA DENOMINADA " + nombreArea + " " + codigoArea + "A FAVOR DEL " + proponente + ", UBICADA EN LA PROVINCIA DE " + provincia);
            entityInforme.setParametroTitularMinero(proponente);
            entityInforme.setParametroCodigo("numero pasan ellos");
            entityInforme.setParametroDescripcionActividad(nombreArea);
            entityInforme.setParametroSector(sector);
            entityInforme.setParametroUbicacion(provincia + ", " + canton + ", " + parroquia);
            entityInforme.setParametroRepresentanteLegal(nombreRepresentanteLegal);
            entityInforme.setParametroCoordenadas(categoriaIIFacade.generarTablaCoordenadas(proyectoLicenciamientoAmbiental));
            SimpleDateFormat fecha = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es"));
//            entityInforme.setFechaactual(canton.getNombre().toString() + ", a "
//                    + fecha.format(new java.util.Date()));
            entityInforme.setFechaactual(canton.getNombre().toString() + ", a 11 de Junio de 2020");
            entityInforme.setParametroCanton(canton.toString());
            entityInforme.setRegistroambiental(licencia);
//            entityInforme.setParametroFecha(canton.getNombre().toString()+", a "+ fecha.format(new Date()));
            entityInforme.setParametroFecha(canton.getNombre().toString()+", a 11 de Junio de 2020");
            entityInforme.setParametroNumOficio("ponen ellos");

            for (Contacto cont : contacto) {
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
            entityInforme.setParametroArt5Normal(MineriaArtesanal + " EN EL ÁREA DENOMINADA " + nombreArea + " " + codigoArea + "A FAVOR DEL " + proponente + ", UBICADA EN LA PROVINCIA DE " + provincia);
            entityInforme.setParametroExpost(parametroExpost);
            entityInforme.setParametrocedulaProponente(cedula);
            entityInforme.setParametroCanton(canton.toString());

            nombreReporte = "InformeTecnico.pdf";
            File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
                    Boolean.valueOf(true), entityInforme);
//            File informePdf1 = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
//                    Boolean.valueOf(true), entityInforme);

            Html = UtilGenerarInforme.generarFicheroHtml(plantillaReporte.getHtmlPlantilla(), nombreReporte,
                    Boolean.valueOf(true), entityInforme);


            setInformePdf(informePdf);

        } catch (Exception e) {
            this.LOG.error("Error al visualizar el informe técnico", e);
            JsfUtil.addMessageError("Error al visualizar el informe técnico");
        }
        return Html;
    }

}
