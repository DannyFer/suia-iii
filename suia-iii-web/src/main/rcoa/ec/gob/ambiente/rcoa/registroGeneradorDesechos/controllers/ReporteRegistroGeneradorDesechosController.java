package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.dto.EntityRegistroGeneradorDesecho;
import ec.gob.ambiente.rcoa.dto.EntityRegistroGeneradorDesechoOficio;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.OficioPronunciamientoRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PermisoRegistroGeneradorDesechosFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.OficioPronunciamientoRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PermisoRegistroGeneradorDesechos;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.util.UtilGenerarInformeRgd;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.util.UtilGenerarInformeRgd;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class ReporteRegistroGeneradorDesechosController {
    
    @EJB
    private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private OrganizacionFacade organizacionFacade;
    @EJB
    private ContactoFacade contactoFacade;
    @EJB
    private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
    @EJB
    private PlantillaReporteFacade plantillaReporteFacade;
    @EJB
    private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
    @EJB
    private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
    @EJB
    private PuntoRecuperacionRgdRcoaFacade puntoRecuperacionRgdRcoaFacade;
    @EJB
    private SecuenciasFacade secuenciasFacade;
    @EJB
    private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
    @EJB
    private PermisoRegistroGeneradorDesechosFacade permisoRegistroGeneradorDesechosFacade;
    @EJB
    private OficioPronunciamientoRgdRcoaFacade oficioPronunciamientoRgdRcoaFacade;
    @EJB
    private AreaFacade areaFacade;
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
    
    @Getter
    @Setter
    private String informePath;
    
    @Getter
    @Setter
    private String nombreReporte;
    @Getter
    @Setter
    private String nombreFichero;
    
    @Getter
    @Setter
    private File informePdf, archivoFinal;
    
    @Getter
    @Setter
    private byte[] archivoInforme;
    
    @Getter
    @Setter
    private TipoDocumento tipoDocumento;
    
    private boolean licenciaNueva = false;
    private boolean licenciaEjecucion = false;  //expost  
    
    private String actividadPrincipal;
    private String codigoCiiu;
    private String rolDirector;
    
    @Getter
    @Setter
    private Usuario usuarioAutoridad;
    
    @Getter
    @Setter
    private boolean provicional;
        
    @Getter
    @Setter
    private String codigoPermisoRgd;
    
    @Getter
    @Setter
    private List<UbicacionesGeografica> listaUbicaciones = new ArrayList<UbicacionesGeografica>();
    
    @Getter
    @Setter
    private boolean actualizacion =false;     
    
    @Getter
    @Setter
    private String cargoAutoridad, areaResponsable;
    
    @Getter
    @Setter
    private Area direccionProvincial, oficinaTecnica;
    
    @Getter
    @Setter
    private boolean personaJuridica = false;
    
    
    public void generarRegistro(RegistroGeneradorDesechosRcoa registro, boolean provicional){
        try {
            //Ver como hacer para buscar el área responsable para varios proyectos cuando debamos hacer la parte de agrupación de proyectos
            this.provicional = provicional;
            
            PermisoRegistroGeneradorDesechos permisoRgd = new PermisoRegistroGeneradorDesechos();
            List<PermisoRegistroGeneradorDesechos> listaPermiso = permisoRegistroGeneradorDesechosFacade.findByRegistroGenerador(registro.getId());
            if(listaPermiso != null && listaPermiso.size() > 0){
                permisoRgd = listaPermiso.get(0);
            }            
            
            List<RegistroGeneradorDesechosProyectosRcoa> listaProyecto = new ArrayList<>();
            
            listaProyecto = registroGeneradorDesechosProyectosRcoaFacade.buscarPorRegistroGenerador(registro.getId());
            
            ProyectoLicenciaCoa proyectoActivo = listaProyecto.get(0).getProyectoLicenciaCoa();

            if(proyectoActivo.getCategorizacion() == 1){
                licenciaNueva = true;
            }
            
            if(proyectoActivo.getCategorizacion() == 3 || proyectoActivo.getCategorizacion() == 4){
                if(proyectoActivo.getTipoProyecto() == 1){
                    licenciaNueva = true;
                }else if(proyectoActivo.getTipoProyecto() == 2){
                    licenciaEjecucion = true;
                }
            }
            
            actividadPrincipal = "";
            List<ProyectoLicenciaCuaCiuu> listaProyectoActividadesPrincipal = registroGeneradorDesechosRcoaFacade.buscarActividadesCiuPrincipal(proyectoActivo);
            if(listaProyectoActividadesPrincipal != null && !listaProyectoActividadesPrincipal.isEmpty()){
                actividadPrincipal = listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getNombre();
                codigoCiiu = listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getCodigo();
            }

            String identificacionUsuario = proyectoActivo.getUsuario().getNombre();
            String cargo = "";
            String direccion = "";
            String correo = "";
            String telefono = "";
            String celular = "";
            String nombreEmpresa = "";
            String representante = "";
            personaJuridica = false;
            
            if(identificacionUsuario.length() == 10){
                cargo = proyectoActivo.getUsuario().getPersona().getTitulo();
                
                List<Contacto> contacto = contactoFacade.buscarPorPersona(proyectoActivo.getUsuario().getPersona());
                
                for (Contacto con : contacto) {
                    if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
                        direccion = con.getValor();
                    }
                    if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
                        correo = con.getValor();
                    }
                    if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
                        telefono = con.getValor();
                    }
                    if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
                        celular = con.getValor();
                    }
                }
                
                nombreEmpresa = proyectoActivo.getUsuario().getPersona().getNombre();
                representante = nombreEmpresa;
                
            }else{
                Organizacion organizacion = organizacionFacade.buscarPorRuc(identificacionUsuario);
                
                if(organizacion != null){
                    cargo = organizacion.getCargoRepresentante();
                    
                    List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);
                    
                    for (Contacto con : contacto) {
                        if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
                            direccion = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
                            correo = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
                            telefono = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
                            celular = con.getValor();
                        }
                    }
                    
                    nombreEmpresa = organizacion.getNombre();
                    representante = organizacion.getPersona().getNombre();
                    personaJuridica = true;
                }else{
                    
                    cargo = proyectoActivo.getUsuario().getPersona().getTitulo();
                    
                    List<Contacto> contacto = contactoFacade.buscarPorPersona(proyectoActivo.getUsuario().getPersona());
                    
                    for (Contacto con : contacto) {
                        if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
                            direccion = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
                            correo = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
                            telefono = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
                            celular = con.getValor();
                        }
                    }
                    
                    nombreEmpresa = proyectoActivo.getUsuario().getPersona().getNombre();
                    representante = nombreEmpresa;
                }
            }
            
            EntityRegistroGeneradorDesecho entity = new EntityRegistroGeneradorDesecho();
            
            rolDirector = "";
            cargoAutoridad = "";
            
            if (proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
                UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyectoActivo.getIdCantonOficina());
                Area areaResponsableRgd = null;
                try {
                    rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");
                    
                    if(ubicacion.getAreaCoordinacionZonal() == null 
                    		&& ubicacion.getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
                    	areaResponsableRgd = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
    				} else {
    					areaResponsableRgd = ubicacion.getAreaCoordinacionZonal().getArea();
    				}
                    
                    List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, areaResponsableRgd);
                    
                    if(listaUsuarios != null && !listaUsuarios.isEmpty()){
                        usuarioAutoridad = listaUsuarios.get(0);
                    }else{
                        JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
                        System.out.println("No existe usuario " + rolDirector + " para el area " + areaResponsableRgd.getAreaName());
                        return;
                    }
                } catch (Exception e) {
                    JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
                    System.out.println("No existe usuario " + rolDirector + "para el area " + ubicacion.getAreaCoordinacionZonal().getArea().getAreaName());
                    return;
                }
                
                areaResponsable = "DIRECCIÓN ZONAL";
                if(areaResponsableRgd.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
                	areaResponsable = areaResponsableRgd.getAreaName();
                	oficinaTecnica = areaResponsableRgd;
                }
                direccionProvincial = areaResponsableRgd;
                oficinaTecnica = (oficinaTecnica == null) ? ubicacion.getAreaCoordinacionZonal() : oficinaTecnica;
                usuarioAutoridad = loginBean.getUsuario();
                
                if(usuarioAutoridad.getPersona().getGenero().equals("FEMENINO")){
                    cargoAutoridad = "DIRECTORA ZONAL";
                }else if(usuarioAutoridad.getPersona().getGenero().equals("MASCULINO")){
                    cargoAutoridad = "DIRECTOR ZONAL";
                }else{
                    cargoAutoridad = "DIRECTOR/A ZONAL";
                }
                
            } else{
                try {
                    rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");
                    
//                    List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, proyectoActivo.getAreaInventarioForestal().getArea());
                    areaResponsable = "DIRECCIÓN ZONAL";//proyectoActivo.getAreaInventarioForestal().getArea().getAreaName();
                    if(proyectoActivo.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS))
                    	areaResponsable = proyectoActivo.getAreaResponsable().getAreaName();
                    if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("DP")){
                        direccionProvincial = proyectoActivo.getAreaInventarioForestal();
                    }else{
                        direccionProvincial = proyectoActivo.getAreaInventarioForestal().getArea();
                    }
                    
                    
                    oficinaTecnica = proyectoActivo.getAreaInventarioForestal();
                    
//                    if(listaUsuarios != null && !listaUsuarios.isEmpty()){
//                        usuarioAutoridad = listaUsuarios.get(0);
                        usuarioAutoridad = loginBean.getUsuario();
                        
                        if(usuarioAutoridad.getPersona().getGenero().equals("FEMENINO")){
                            cargoAutoridad = "DIRECTORA ZONAL" ; // + areaResponsable;
                        }else if(usuarioAutoridad.getPersona().getGenero().equals("MASCULINO")){
                            cargoAutoridad = "DIRECTOR ZONAL"; // + areaResponsable;
                        }else{
                            cargoAutoridad = "DIRECTOR/A ZONAL"; // + areaResponsable;
                        }
//                    }else{
//                        JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
//                        return;
//                    }
                } catch (Exception e) {
                    JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
                    return;
                }
            }
            
            if(provicional){
                entity.setProvicional("PROVISIONAL");
            }else{
                entity.setProvicional(" ");
            }
            
            SimpleDateFormat fecha = new SimpleDateFormat("dd-MM-yy",new Locale("es"));
//           entityInforme.setFechaactual(fecha.format(new java.util.Date()));
            
            entity.setFechaEmisionRgd(fecha.format(new Date()));
            
            //ver variable para actualización 
            entity.setFechaActualizacionRgd(fechaActualizacion());
            
            entity.setNombreOperador(nombreEmpresa);
            entity.setResponsableEmpresa(representante);
            
            //solo persona juridica
            if(personaJuridica){
                entity.setCargoEmpresa(cargo);
            }
            
            String datos = "<table border=\"1\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\">";
            datos += "<tbody><tr><td colspan=\"2\"><strong>A. INFORMACI&Oacute;N GENERAL</strong></td></tr>";
            datos += "<tr><td style=\"width:50%\">Fecha de emisi&oacute;n de Registro Generador:</td><td style=\"width:50%\">" + entity.getFechaEmisionRgd()  + "</td></tr>";
            
            if(actualizacion){
             datos+="<tr><td>Fecha de actualizaci&oacute;n de Registro Generador:</td><td>"+ entity.getFechaActualizacionRgd()+"</td></tr>";
            }
            
            datos += "<tr><td>Nombre del operador:</td><td>"+ nombreEmpresa +"</td></tr>";
            datos += "<tr><td>Responsable o representante de la empresa:</td><td>"+representante+"</td></tr>";
            
            if(personaJuridica){
                datos +="<tr><td>Cargo o puesto en la empresa:</td><td>" + entity.getCargoEmpresa()+"</td></tr>";
            }
            
            datos +="<tr><td>C&oacute;digo del proyecto, obra o actividad regularizado:</td><td>"+proyectoActivo.getCodigoUnicoAmbiental()+"</td></tr>";
            datos +="<tr><td>Actividad CIIU del proyecto, obra o actividad:</td><td>"+actividadPrincipal+"</td></tr>";
            
            if(licenciaNueva || actualizacion){
                datos += "<tr><td>Autorizaci&oacute;n Administrativa Ambiental:</td><td>$F{autorizacionAdministrativaAmbiental}</td></tr>";
            }
            
            datos +="<tr><td>Ubicaci&oacute;n del operador:</td><td>"+direccion+"</td></tr>";
            datos +="</tbody></table>";
            
            entity.setTabla(datos);
            
            
            entity.setNombreProyecto(proyectoActivo.getNombreProyecto());
            
            if(permisoRgd != null && (permisoRgd.getNumeroDocumento() == null || permisoRgd.getNumeroDocumento().isEmpty())){               
                permisoRgd.setRegistroGeneradorDesechosRcoa(registro);
                permisoRgd.setNumeroDocumento(generarCodigoRGD(oficinaTecnica));
            }
            
            if(provicional){
                entity.setCodigoRegistroGenerador(permisoRgd.getNumeroDocumento()+"-PROVISIONAL");
            }else{
                entity.setCodigoRegistroGenerador(permisoRgd.getNumeroDocumento());
            }
            
            entity.setDireccionProyectoRgd(proyectoActivo.getDireccionProyecto());
            
            entity.setDesechos(getDesechosPeligrosos(registro));
            entity.setPuntosGeneracion(getPuntosMonitoreo(registro));
            entity.setCargoAutoridad(cargoAutoridad);
            entity.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
            entity.setEntidadAutoridad(areaResponsable);
            
            if(provicional){
                String mensaje = "k) Finalizar el proceso de regularización ambiental para obtener el documento definitivo del "
                        + "Registro Generador de Residuos y Desechos Peligrosos y/o Especiales. "
                        + "En caso de no culminar el proceso de regularización ambiental en los plazos establecidos en la normativa ambiental, "
                        + "se procederá a la cancelación del Registro de Generador Provisional, sin perjuicio de las acciones a las que haya lugar.";
                entity.setLiteralkProvicional(mensaje);
            }else{
                entity.setLiteralkProvicional(" ");
            }
            
            codigoPermisoRgd = entity.getCodigoRegistroGenerador();
            
            nombreFichero = "RegistroGeneradorDesechos" + registro.getCodigo()+".pdf";
            nombreReporte = "RegistroGeneradorDesechos.pdf";
            
            PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS);
            
            File informePdf = UtilGenerarInforme.generarFichero(
                    plantillaReporte.getHtmlPlantilla(),
                    nombreReporte, true, entity);

            Path path = Paths.get(informePdf.getAbsolutePath());
            String reporteHtmlfinal = nombreReporte;
            archivoInforme = Files.readAllBytes(path);
            File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
            FileOutputStream file = new FileOutputStream(archivoFinal);
            file.write(Files.readAllBytes(path));
            file.close();
            
            TipoDocumento tipoDoc = new TipoDocumento();
            
            tipoDoc.setId(TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS.getIdTipoDocumento());
            
            DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
            documento.setNombre(nombreReporte);
            documento.setExtesion(".pdf");
            documento.setTipoContenido("application/pdf");
            documento.setMime("application/pdf");
            documento.setContenidoDocumento(archivoInforme);
            documento.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
            documento.setTipoDocumento(tipoDoc);
            documento.setIdTable(registro.getId());
            documento.setRegistroGeneradorDesechosRcoa(registro);//Registro de Generador de Desechos Peligrosos y Especiales
            //int procesoId = (int)bandejaTareasBean.getProcessId();
            //documento.setIdProceso(procesoId);

            documento = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
                    registro.getCodigo(), "REGISTRO GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documento, TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS);
            
//            PermisoRegistroGeneradorDesechos permisoRgd = new PermisoRegistroGeneradorDesechos();
//            permisoRgd.setRegistroGeneradorDesechosRcoa(registro);
//            permisoRgd.setEstado(true);
//            permisoRgd.setFechaCreacion(new Date());
//            permisoRgd.setFechaCreacionDocumento(new Date());
//            permisoRgd.setNumeroDocumento(entity.getCodigoRegistroGenerador());
//            
//            permisoRegistroGeneradorDesechosFacade.save(permisoRgd, loginBean.getUsuario());
            
            guardarRegistroRGD(permisoRgd);
            generarOficio(registro, proyectoActivo);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getDesechosPeligrosos(RegistroGeneradorDesechosRcoa registro)
    {
        List<DesechosRegistroGeneradorRcoa> listaDesechos = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGeneradorGenerados(registro);
        String desechos = "<table style=\"width: 300px; font-size: 11px !important;\" border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\">"
                + "<tbody><tr>"
                + "<td><strong>Código del residuo o desecho</strong></td>"
                + "<td><strong>Nombre del residuo o desecho peligroso y/o especial</strong></td>"
                + "</tr>";
        for (DesechosRegistroGeneradorRcoa desecho : listaDesechos) {
            desechos += "<tr>";
            desechos += "<td>" + desecho.getDesechoPeligroso().getClave() + "<t/td>";
            desechos += "<td>" + desecho.getDesechoPeligroso().getDescripcion() + "<t/td>";
            desechos += "</tr>";
        }
        desechos += "</tbody></table>";
        return desechos;
    }
    
    private String fechaActualizacion(){
        String fecha ="<tr>";
        
        fecha += "<td>Fecha de actualizaci&oacute;n de Registro Generador:</td>";
        fecha += "<td>" + JsfUtil.devuelveFechaEnLetrasSinHora(new Date()) + "<t/td>";
        fecha += "</tr>";
        return fecha;
    }
    
    public String getPuntosMonitoreo(RegistroGeneradorDesechosRcoa registro)
    {
        List<PuntoRecuperacionRgdRcoa> puntosRecuperacion = puntoRecuperacionRgdRcoaFacade.buscarPorRgd(registro.getId());
        String puntos = "";
        for(PuntoRecuperacionRgdRcoa punto : puntosRecuperacion){
            puntos += "<table style=\"width: 300px; font-size: 11px !important;\" border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\">";
            puntos += "<tbody><tr>"
                    + "<td><strong>NOMBRE</strong></td>"
                    + "<td colspan=\"2\">"+ punto.getNombre()+"</td>"
                    + "</tr>"
                    +"<tr>"
                    + "<td><strong>DIRECCIÓN</strong></td>"
                    + "<td colspan=\"2\">"+ punto.getDireccion()+"</td>"
                    + "</tr>";
            puntos += "<tr><td>N°</td><td>X</td><td>Y</td></tr>";
            
            if(punto.getFormasPuntoRecuperacionRgdRcoa() != null && !punto.getFormasPuntoRecuperacionRgdRcoa().isEmpty()){
                for(CoordenadaRgdCoa coordenada : punto.getFormasPuntoRecuperacionRgdRcoa().get(0).getCoordenadas()){
                    
                    String x = new BigDecimal(coordenada.getX()).toPlainString();
                    String y = new BigDecimal(coordenada.getY()).toPlainString();
                    
                    puntos += "<tr><td>"+coordenada.getOrden()+"</td><td>"+x+"</td><td>"+y+"</td></tr>";
                }
            }
            puntos += "</tbody></table><br /><br />";
        }
        return puntos;
    }
    
    public String getUbicacionPuntosMonitoreo(List<PuntoRecuperacionRgdRcoa> puntosRecuperacion )
    {
        String puntos = "";
        if(puntosRecuperacion == null || puntosRecuperacion.size() == 0)
            return puntos;
        for(PuntoRecuperacionRgdRcoa punto : puntosRecuperacion){
            if(puntosRecuperacion != null && puntosRecuperacion.size() > 10){
                puntos += "<br/><br/><table border=\"0\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\">";
                puntos += "<tbody><tr><td colspan=\"2\"><strong>F. ANEXOS</strong></td></tr>";
                puntos += "<tr><td  colspan=\"2\">Ubicación de puntos</td></tr>";
                puntos +="</tbody></table>";
                puntos +="<br/><br/>";
            }
            puntos += "<table style=\"width: 300px; font-size: 11px !important;\" border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\">";
            
            puntos += "<tbody><tr>"
                    + "<td><strong>NOMBRE</strong></td>"
                    + "<td colspan=\"2\">"+ punto.getNombre()+"</td>"
                    + "</tr>"
                    +"<tr>"
                    + "<td><strong>DIRECCIÓN</strong></td>"
                    + "<td colspan=\"2\">"+ punto.getDireccion()+"</td>"
                    + "</tr>"
                    +"<tr>"
                    + "<td><strong>PROVINCIA</strong></td>"
                    + "<td colspan=\"2\">"+ (punto.getUbicacionesGeografica() != null ? punto.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre() : "")+"</td>"
                    + "</tr>"
                    +"<tr>"
                    + "<td><strong>CANTÓN</strong></td>"
                    + "<td colspan=\"2\">"+ (punto.getUbicacionesGeografica() != null ? punto.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() : "")+"</td>"
                    + "</tr>"
                    +"<tr>"
                    + "<td><strong>PARROQUIA</strong></td>"
                    + "<td colspan=\"2\">"+ (punto.getUbicacionesGeografica() != null ? punto.getUbicacionesGeografica().getNombre(): "")+"</td>"
                    + "</tr>";
            
            puntos += "</tbody></table><br /><br />";
        }
        return puntos;
    }

    private void generarOficio(RegistroGeneradorDesechosRcoa registro, ProyectoLicenciaCoa proyectoActivo){
        try {
        	
        	OficioPronunciamientoRgdRcoa oficio = new OficioPronunciamientoRgdRcoa();
			List<OficioPronunciamientoRgdRcoa> listaoficio = oficioPronunciamientoRgdRcoaFacade.findByGeneradorDesechos(registro.getId());
			if(listaoficio != null && listaoficio.size() > 0){
				oficio = listaoficio.get(0);
			}
        	
            String identificacionUsuario = proyectoActivo.getUsuario().getNombre();
            String cargo = "";
            String nombreEmpresa = "";
            String nombreOperador = "";
            String ruc = "";
            boolean juridico = false;
            
            listaUbicaciones = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoActivo);
            ProyectoLicenciaCoaUbicacion ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoActivo);
            
            if(identificacionUsuario.length() == 10){
                cargo = proyectoActivo.getUsuario().getPersona().getTitulo();
                nombreOperador = proyectoActivo.getUsuario().getPersona().getNombre();
                nombreEmpresa = " ";
                ruc = " ";
            }else{
                Organizacion organizacion = organizacionFacade.buscarPorRuc(identificacionUsuario);
                if(organizacion != null){
                    cargo = organizacion.getCargoRepresentante();
                    nombreOperador = organizacion.getPersona().getNombre();
                    nombreEmpresa = organizacion.getNombre();
                    ruc = organizacion.getRuc();
                    juridico = true;
                }else{
                    cargo = proyectoActivo.getUsuario().getPersona().getTitulo();
                    nombreOperador = proyectoActivo.getUsuario().getPersona().getNombre();
                    nombreEmpresa = " ";
                    ruc = " ";
                }
            }
            
            EntityRegistroGeneradorDesechoOficio entity = new EntityRegistroGeneradorDesechoOficio();
            
            if(oficio != null && (oficio.getNumeroDocumento() == null || oficio.getNumeroDocumento().isEmpty())){
				oficio.setRegistroGeneradorDesechosRcoa(registro);
				oficio.setNumeroDocumento(generarCodigoOficio(direccionProvincial));
			}
            
            entity.setCodigo(oficio.getNumeroDocumento());
            
            entity.setActividadProyecto(actividadPrincipal);
            if(provicional){
                entity.setAdicionalmente("Adicionalmente, considerando que se trata de un Registro de Generador PROVISIONAL, se recuerda al operador su obligación de finalizar el proceso de regularización ambiental para obtener el documento definitivo del Registro Generador de Residuos y Desechos Peligrosos y/o Especiales, caso contrario, se procederá a la anulación del Registro de Generador Provisional, sin perjuicio de las acciones a las que haya lugar.");
                entity.setProvicional("PROVISIONAL");
            }else{
                entity.setAdicionalmente(" ");
                entity.setProvicional(" ");
            }
            
            entity.setCargo(cargo);
            entity.setCargoAutoridad(areaResponsable);
            entity.setCedula(usuarioAutoridad.getNombre());
            entity.setCodigoCIIU(codigoCiiu);
            entity.setCodigoPermisoRgd(codigoPermisoRgd);
            entity.setCodigoRgd(registro.getCodigo());
		// incluir informacion de la sede de la zonal en el documento
		ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
		String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyectoActivo, null, null);
		entity.setFecha(nombreCanton+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
            //entity.setFecha(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
            entity.setFechaLetras(JsfUtil.devuelveFechaEnLetrasSinHora(registro.getFechaCreacion()));
            if (licenciaEjecucion) {
                entity.setLicenciaEjecucion("en proceso de regularización ambiental");
            }else{
                entity.setLicenciaEjecucion(" ");
            }
            entity.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
            
            if(!juridico){
                entity.setNombreEmpresa(" ");
                entity.setOperadorEmpresa(" ");
                entity.setRuc(ruc);
            }else{
                entity.setNombreEmpresa(nombreEmpresa);
                entity.setOperadorEmpresa("representante de la empresa " + nombreEmpresa + ",");
                entity.setRuc(ruc);
            }
            
            entity.setNombreProyecto(proyectoActivo.getNombreProyecto());
            entity.setOperador(nombreOperador);
            
            if(ubicacionPrincipal != null && ubicacionPrincipal.getId() != null){
//                entity.setProvincia(listaUbicaciones.get(0).getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
//                entity.setCanton(listaUbicaciones.get(0).getUbicacionesGeografica().getNombre());
                entity.setProvincia(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
                entity.setCanton(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
            }else {
                entity.setProvincia(" ");
                entity.setCanton("  ");
            }
            
            nombreFichero = "OficioPronunciamiento" + registro.getCodigo()+".pdf";
            nombreReporte = "OficioPronunciamiento.pdf";
            
            PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO);
            
            File informePdf = UtilGenerarInforme.generarFichero(
                    plantillaReporte.getHtmlPlantilla(),
                    nombreReporte, true, entity);

            Path path = Paths.get(informePdf.getAbsolutePath());
            String reporteHtmlfinal = nombreReporte;
            archivoInforme = Files.readAllBytes(path);
            File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
            FileOutputStream file = new FileOutputStream(archivoFinal);
            file.write(Files.readAllBytes(path));
            file.close();
            
            TipoDocumento tipoDoc = new TipoDocumento();
            
            tipoDoc.setId(TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO.getIdTipoDocumento());
            
            DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
            documento.setNombre(nombreReporte);
            documento.setExtesion(".pdf");
            documento.setTipoContenido("application/pdf");
            documento.setMime("application/pdf");
            documento.setContenidoDocumento(archivoInforme);
            documento.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
            documento.setTipoDocumento(tipoDoc);
            documento.setIdTable(registro.getId());
            //int procesoId = (int)bandejaTareasBean.getProcessId();
            //documento.setIdProceso(procesoId);
            documento.setRegistroGeneradorDesechosRcoa(registro);//Registro de Generador de Desechos Peligrosos y Especiales

            documento = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
                    registro.getCodigo(), "OFICIO PRONUNCIAMIENTO GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documento, TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO);
            
//            OficioPronunciamientoRgdRcoa oficio = new OficioPronunciamientoRgdRcoa();
//            oficio.setEstado(true);
//            oficio.setRegistroGeneradorDesechosRcoa(registro);
//            oficio.setFechaCreacion(new Date());
//            oficio.setFechaCreacionDocumento(new Date());
//            oficio.setNumeroDocumento(entity.getCodigo());
//            
//            oficioPronunciamientoRgdRcoaFacade.save(oficio, loginBean.getUsuario());
            
            guardarOficio(oficio);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    //SUIA-MM-AAAA-MAAE-SIGLAS (OFICINA TECNICA)-RGD-NUMERO CONSECUTIVO-PROVISIONAL
    public String generarCodigoRGD(Area area) {
        String anioActual=secuenciasFacade.getCurrentYear();
        String nombreSecuencia="REGISTRO_GENERADOR_DESECHOS_"+anioActual+"_"+area.getAreaAbbreviation();
        
        try {
            return "SUIA-" 
                    + secuenciasFacade.getCurrentMonth()
                    + "-"
                    + anioActual
                    + "-"
                    + Constantes.SIGLAS_INSTITUCION
                    + "-"
                    + area.getAreaAbbreviation()
                    + "-"
                    + "RGD"
                    + "-"
                    + secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private String generarCodigoRGDREP(Area area) {
        String anioActual=secuenciasFacade.getCurrentYear();
        String nombreSecuencia="REGISTRO_GENERADOR_DESECHOS_REP_"+anioActual+"_"+area.getAreaAbbreviation();
        
        try {
            return "SUIA-" 
                    + secuenciasFacade.getCurrentMonth()
                    + "-"
                    + anioActual
                    + "-"
                    + Constantes.SIGLAS_INSTITUCION
                    + "-"
                    + area.getAreaAbbreviation()
                    + "-"
                    + "RGD-REP"
                    + "-"
                    + secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    //MAAE-2020-SIGLAS (OFICINA TECNICA)-000779
    private String generarCodigoOficioREP(Area area) {
        String anioActual=secuenciasFacade.getCurrentYear();
        String nombreSecuencia="OFICIO_PRONUNCIAMIENTO_RGD_REP_"+anioActual+"_"+area.getAreaAbbreviation();
        
        try {
            return Constantes.SIGLAS_INSTITUCION + "-SUIA-SCA-"
                    + anioActual
                    + "-"
                    + secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    //MAAE-2020-SIGLAS (OFICINA TECNICA)-000779
    public String generarCodigoOficio(Area area) {
        String anioActual=secuenciasFacade.getCurrentYear();
        String nombreSecuencia="OFICIO_PRONUNCIAMIENTO_RGD_"+anioActual+"_"+area.getAreaAbbreviation();
        
        try {
            return Constantes.SIGLAS_INSTITUCION +"-"
                    + anioActual
                    + "-"
                    + area.getAreaAbbreviation()
                    + "-"
                    + secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public String valorDouble(Double numero){
        return BigDecimal.valueOf(numero).toString();
    }

    public void generarRegistroREP(RegistroGeneradorDesechosRcoa registro){
        try {
            //Ver como hacer para buscar el área responsable para varios proyectos cuando debamos hacer la parte de agrupación de proyectos 
            PermisoRegistroGeneradorDesechos permisoRgd = new PermisoRegistroGeneradorDesechos();
            List<PermisoRegistroGeneradorDesechos> listaPermiso = permisoRegistroGeneradorDesechosFacade.findByRegistroGenerador(registro.getId());
            if(listaPermiso != null && listaPermiso.size() > 0){
                permisoRgd = listaPermiso.get(0);
            }
            
            String identificacionUsuario = registro.getUsuario().getNombre();
            String cargo = "";
            String direccion = "";
            String correo = "";
            String telefono = "";
            String celular = "";
            String nombreEmpresa = "";
            String representante = "";
            personaJuridica = false;
            
            if(identificacionUsuario.length() == 10){
                cargo = registro.getUsuario().getPersona().getTitulo();
                
                List<Contacto> contacto = contactoFacade.buscarPorPersona(registro.getUsuario().getPersona());
                
                for (Contacto con : contacto) {
                    if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
                        direccion = con.getValor();
                    }
                    if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
                        correo = con.getValor();
                    }
                    if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
                        telefono = con.getValor();
                    }
                    if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
                        celular = con.getValor();
                    }
                }
                
                nombreEmpresa = registro.getUsuario().getPersona().getNombre();
                representante = nombreEmpresa;
                
            }else{
                Organizacion organizacion = organizacionFacade.buscarPorRuc(identificacionUsuario);
                
                if(organizacion != null){
                    cargo = organizacion.getCargoRepresentante();
                    
                    List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);
                    
                    for (Contacto con : contacto) {
                        if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
                            direccion = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
                            correo = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
                            telefono = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
                            celular = con.getValor();
                        }
                    }
                    
                    nombreEmpresa = organizacion.getNombre();
                    representante = organizacion.getPersona().getNombre();
                    personaJuridica = true;
                }else{
                    
                    cargo = registro.getUsuario().getPersona().getTitulo();
                    
                    List<Contacto> contacto = contactoFacade.buscarPorPersona(registro.getUsuario().getPersona());
                    
                    for (Contacto con : contacto) {
                        if (con.getFormasContacto().getId() == 2 && con.getEstado().equals(true)) {
                            direccion = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
                            correo = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 6 && con.getEstado().equals(true)) {
                            telefono = con.getValor();
                        }
                        if (con.getFormasContacto().getId() == 4 && con.getEstado().equals(true)) {
                            celular = con.getValor();
                        }
                    }
                    
                    nombreEmpresa = registro.getUsuario().getPersona().getNombre();
                    representante = nombreEmpresa;
                }    
                
            }        
            
            EntityRegistroGeneradorDesecho entity = new EntityRegistroGeneradorDesecho();
            
            rolDirector = "";
            cargoAutoridad = "SUBSECRETARIO DE CALIDAD AMBIENTAL";
            areaResponsable = "Subsecretario(a) de Calidad Ambiental";
            usuarioAutoridad = loginBean.getUsuario();
            
            SimpleDateFormat fecha = new SimpleDateFormat("dd-MM-yy",new Locale("es"));
            entity.setFechaEmisionRgd(fecha.format(new Date()));
            //ver variable para actualización 
            entity.setFechaActualizacionRgd(fechaActualizacion());
            entity.setNombreOperador(nombreEmpresa);
            entity.setResponsableEmpresa(representante);
            if(permisoRgd != null && (permisoRgd.getNumeroDocumento() == null || permisoRgd.getNumeroDocumento().isEmpty())){
                direccionProvincial = areaFacade.getArea(253);
                permisoRgd.setRegistroGeneradorDesechosRcoa(registro);
                permisoRgd.setNumeroDocumento(generarCodigoRGDREP(direccionProvincial));
            }
            entity.setCodigoRegistroGenerador(permisoRgd.getNumeroDocumento());
            //solo persona juridica
            if(personaJuridica){
                entity.setCargoEmpresa(cargo);
            }
            
            // para la seccion A 
            String datos = "<table border=\"1\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1; text-align: justify;\">";
            datos += "<tbody><tr><td colspan=\"2\"><strong>A. INFORMACI&Oacute;N GENERAL DEL REGISTRO DE GENERADOR</strong></td></tr>";
            datos += "<tr><td style=\"width:50%\">Instructivo de aplicación de Responsabilidad Extendida:</td><td style=\"width:50%;\">"; 
            if(registro.getPoliticaDesechoRgdRcoa() != null){
                entity.setPoliticaNeumaticos("");
                entity.setTipoDeclaracion("anual");
                switch (registro.getPoliticaDesechoRgdRcoa().getId().toString()) {
                case "1":
                    datos += "Acuerdo Ministerial No. 131, Responsabilidad Extendida del Productor aplicada a la gestión de neumáticos fuera de uso.";
                    entity.setAcuerdoMinisterial("131 publicado en el R. O. 219 del 29 de diciembre de 2022");
                    entity.setPoliticaNeumaticos("Es compromiso del Productor que los neumáticos fuera de uso que no puedan ser reencauchados, deben ser entregados a un gestor autorizado para que sean gestionados de manera ambientalmente adecuada.");
                    
                    datos += "</td></tr>";
                    datos+="<tr><td>C&oacute;digo del tr&aacute;mite:</td><td>"+ registro.getCodigo()+"</td></tr>";
                    datos+="<tr><td>Fecha de emisión:</td><td>"+ (entity.getFechaEmisionRgd() != null ? entity.getFechaEmisionRgd() : "")+"</td></tr>";
                    entity.setMostrarTipoNeumatico("display: inline");
                    entity.setMostrarPoliticas("display: none");
                    break;
                case "2":
                    datos += "Acuerdo Ministerial No. 022, Responsabilidad Extendida del Productor aplicada a la gestión de pilas usadas.";
                    entity.setAcuerdoMinisterial("022 publicado en el R. O. 943 del 29 de abril de 2013");
                    
                    datos += "</td></tr>";
                    datos+="<tr><td>C&oacute;digo del tr&aacute;mite:</td><td>"+ registro.getCodigo()+"</td></tr>";
                    datos+="<tr><td>Fecha de emisión:</td><td>"+ (entity.getFechaEmisionRgd() != null ? entity.getFechaEmisionRgd() : "")+"</td></tr>";
                    entity.setMostrarTipoNeumatico("display: none");
                    entity.setMostrarPoliticas("display: inline");
                    break;
                case "3":
                    datos += "Acuerdo Ministerial No. 021, Responsabilidad Extendida del Productor aplicada a la gestión de desechos plásticos de uso agrícola.";
                    entity.setAcuerdoMinisterial("021 publicado en el R. O. 943 del 29 de abril de 2013");
                    
                    datos += "</td></tr>";
                    datos+="<tr><td>Fecha de emisión:</td><td>"+ (entity.getFechaEmisionRgd() != null ? entity.getFechaEmisionRgd() : "")+"</td></tr>";  
                    datos+="<tr><td>C&oacute;digo del tr&aacute;mite:</td><td>"+ registro.getCodigo()+"</td></tr>";                                      
                    entity.setMostrarTipoNeumatico("display: none");
                    entity.setMostrarPoliticas("display: inline");
                    break;
                case "4":
                    datos += "Acuerdo Ministerial No. 191, Responsabilidad Extendida del Productor aplicada a la gestión de equipos celulares en desuso.";
                    entity.setAcuerdoMinisterial("191 publicado en el R. O. 881 del 29 de enero de 2013");
                    entity.setTipoDeclaracion("mensual");
                    
                    datos += "</td></tr>";
                    datos+="<tr><td>C&oacute;digo del tr&aacute;mite:</td><td>"+ registro.getCodigo()+"</td></tr>";
                    datos+="<tr><td>Fecha de emisión:</td><td>"+ (entity.getFechaEmisionRgd() != null ? entity.getFechaEmisionRgd() : "")+"</td></tr>";
                    entity.setMostrarTipoNeumatico("display: none");
                    entity.setMostrarPoliticas("display: inline");
                    break;
                case "5":
                    datos += "Acuerdo Ministerial No. 042, Responsabilidad Extendida del Productor aplicada a la gestión de aceites lubricantes usados y sus envases vacíos.";
                    entity.setAcuerdoMinisterial("042 publicado en el R. O. 498 del 30 de mayo de 2019");
                    
                    datos += "</td></tr>";
                    datos+="<tr><td>Fecha de emisión:</td><td>"+ (entity.getFechaEmisionRgd() != null ? entity.getFechaEmisionRgd() : "")+"</td></tr>";
                    datos+="<tr><td>C&oacute;digo del tr&aacute;mite:</td><td>"+ registro.getCodigo()+"</td></tr>";
                    entity.setMostrarTipoNeumatico("display: none");
                    entity.setMostrarPoliticas("display: inline");
                    break;
                case "6":
                    datos += "Acuerdo Ministerial No. MAATE-2022-067, Responsabilidad Extendida  del Productor aplicada a la gestión de residuos de aparatos eléctricos y electrónicos (RAEE) de origen doméstico.";
                    entity.setAcuerdoMinisterial("MAATE-2022-067 publicado en el R.O. 117 del 1 de agosto del 2022");
                    entity.setTipoDeclaracion("anual");
                    
                    datos += "</td></tr>";
                    datos+="<tr><td>C&oacute;digo del tr&aacute;mite:</td><td>"+ registro.getCodigo()+"</td></tr>";
                    datos+="<tr><td>Fecha de emisión:</td><td>"+ (entity.getFechaEmisionRgd() != null ? entity.getFechaEmisionRgd() : "")+"</td></tr>";
                    entity.setMostrarTipoNeumatico("display: none");
                    entity.setMostrarPoliticas("display: inline");
                    break;
                case "7":
                    datos += "Acuerdo Ministerial No. MAATE-2021-034, Responsabilidad Extendida del Productor aplicada a la gestión de baterías ácido plomo usadas BAPU";
                    entity.setAcuerdoMinisterial("MAATE-2021-034 publicado en el R. O. 554 del 07 de octubre de 2021");
                    entity.setTipoDeclaracion("anual");
                    
                    datos += "</td></tr>";
                    datos+="<tr><td>C&oacute;digo del tr&aacute;mite:</td><td>"+ registro.getCodigo()+"</td></tr>";
                    datos+="<tr><td>Fecha de emisión:</td><td>"+ (entity.getFechaEmisionRgd() != null ? entity.getFechaEmisionRgd() : "")+"</td></tr>";
                    entity.setMostrarTipoNeumatico("display: none");
                    entity.setMostrarPoliticas("display: inline");
                    break;        
                case "8":
                    datos += "Acuerdo Ministerial No. MAATE-2022-097, Responsabilidad Extendida del Productor aplicada a la gestión integral de lámparas de descarga y/o lámparas led en desuso";
                    entity.setAcuerdoMinisterial("MAATE-2022-097 publicado en el R.O. No. 193, de 21 de noviembre de 2022");
                    entity.setTipoDeclaracion("anual");
                    
                    datos += "</td></tr>";
                    datos+="<tr><td>C&oacute;digo del tr&aacute;mite:</td><td>"+ registro.getCodigo()+"</td></tr>";
                    datos+="<tr><td>Fecha de emisión:</td><td>"+ (entity.getFechaEmisionRgd() != null ? entity.getFechaEmisionRgd() : "")+"</td></tr>";
                    entity.setMostrarTipoNeumatico("display: none");
                    entity.setMostrarPoliticas("display: inline");
                    break;   
                    
                case "9":
                    datos += "Acuerdo Ministerial No. 134 del 22 de noviembre de 2023., Medicamentos o Productos Caducados o Fuera de Especificaciones";
                    entity.setAcuerdoMinisterial("publicado en el R.O. No. 490, de 1 de febrero de 2024");
                    entity.setTipoDeclaracion("anual");
                    
                    datos += "</td></tr>";
                    datos+="<tr><td>C&oacute;digo del tr&aacute;mite:</td><td>"+ registro.getCodigo()+"</td></tr>";
                    datos+="<tr><td>Fecha de emisión:</td><td>"+ (entity.getFechaEmisionRgd() != null ? entity.getFechaEmisionRgd() : "")+"</td></tr>";
                    entity.setMostrarTipoNeumatico("display: none");
                    entity.setMostrarPoliticas("display: inline");
                    break; 
                default:
                    break;
                }
                entity.setGestionPolitica(registro.getPoliticaDesechoRgdRcoa().getNombre());
            }
//            datos += "</td></tr>";
//            datos+="<tr><td>C&oacute;digo del tr&aacute;mite:</td><td>"+ registro.getCodigo()+"</td></tr>";
//            datos+="<tr><td>Fecha de emisión:</td><td>"+ (entity.getFechaEmisionRgd() != null ? entity.getFechaEmisionRgd() : "")+"</td></tr>";
                        
            if(actualizacion && entity.getFechaActualizacionRgd() != null ){
                datos+="<tr><td>Número de actualización:</td><td>"+ entity.getFechaActualizacionRgd()+"</td></tr>";
            }
            if(actualizacion){
                 datos+="<tr><td>Fecha de última actualización:</td><td>"+ entity.getFechaActualizacionRgd()+"</td></tr>";
            }
            datos +="</tbody></table><br/><br/>";
            
            datos += "<table border=\"1\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\">";
            datos += "<tbody><tr><td colspan=\"2\"><strong>B. INFORMACION DEL PRODUCTOR</strong></td></tr>";
            datos += "<tr><td style=\"width:50%\">Nombre del Productor:</td><td style=\"width:50%\">" + entity.getNombreOperador() + "</td></tr>";
            datos+="<tr><td>Responsable o representante legal:</td><td>"+ representante+"</td></tr>";
            if(personaJuridica){
                datos += "<tr><td>Cargo o puesto:</td><td>"+ entity.getCargoEmpresa() +"</td></tr>";
            }
                        
            if(registro.getPoliticaDesechoRgdRcoa() != null){
                switch (registro.getPoliticaDesechoRgdRcoa().getId().toString()) {
                case "1":
                	datos +="<tr><td>Ubicaci&oacute;n del operador:</td><td>"+direccion+"</td></tr>";
                    break;
                case "2":
                	datos +="<tr><td>Ubicaci&oacute;n del operador:</td><td>"+direccion+"</td></tr>";                    
                    break;
                case "3":
                	datos +="<tr><td>Direcci&oacute;n del operador:</td><td>"+direccion+"</td></tr>";                    
                    break;
                case "4":
                	datos +="<tr><td>Ubicaci&oacute;n del operador:</td><td>"+direccion+"</td></tr>";                    
                    break;
                case "5":
                	datos +="<tr><td>Direcci&oacute;n del operador:</td><td>"+direccion+"</td></tr>";                  
                    break;
                case "6":
                	datos +="<tr><td>Ubicaci&oacute;n del operador:</td><td>"+direccion+"</td></tr>";                    
                    break;
                case "7":
                	datos +="<tr><td>Ubicaci&oacute;n del operador:</td><td>"+direccion+"</td></tr>";                    
                    break;        
                case "8":
                	datos +="<tr><td>Ubicaci&oacute;n del operador:</td><td>"+direccion+"</td></tr>";                    
                    break; 
                case "9":
                	datos +="<tr><td>Ubicaci&oacute;n del operador:</td><td>"+direccion+"</td></tr>";                    
                    break;
                default:
                    break;
                }
            }
            
//            datos +="<tr><td>Ubicaci&oacute;n del operador:</td><td>"+direccion+"</td></tr>";
            
            datos +="</tbody></table>";
            
            entity.setTabla(datos);
            
            
            entity.setDesechos(getDesechosPeligrosos(registro));
            List<PuntoRecuperacionRgdRcoa> puntosRecuperacion = puntoRecuperacionRgdRcoaFacade.buscarPorRgd(registro.getId());
            if(puntosRecuperacion != null && puntosRecuperacion.size() > 10)
                entity.setPuntosGeneracion("Ver anexo");
            else
                entity.setPuntosGeneracion(getUbicacionPuntosMonitoreo(puntosRecuperacion));
            entity.setCargoAutoridad(cargoAutoridad);
            entity.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
            entity.setEntidadAutoridad(areaResponsable);
            
            codigoPermisoRgd = entity.getCodigoRegistroGenerador();
            
            nombreFichero = "RegistroGeneradorDesechosREP" + registro.getCodigo()+".pdf";
            nombreReporte = "RegistroGeneradorDesechos.pdf";
            
            PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS_REP);            
            
            File informePdf = UtilGenerarInforme.generarFichero(
                    plantillaReporte.getHtmlPlantilla(),
                    nombreReporte, true, entity);

            if(puntosRecuperacion != null && puntosRecuperacion.size() > 10){
                File anexoPuntos = UtilGenerarInforme.generarFichero(
                        getUbicacionPuntosMonitoreo(puntosRecuperacion),
                        "anexos", true, entity);
                List<File> listaFiles = new ArrayList<File>();
                listaFiles.add(informePdf);
                listaFiles.add(anexoPuntos);
                informePdf = UtilFichaMineria.unirPdf(listaFiles, informePdf.getName()); 
            }

            Path path = Paths.get(informePdf.getAbsolutePath());
            String reporteHtmlfinal = nombreReporte;
            archivoInforme = Files.readAllBytes(path);
            File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
            FileOutputStream file = new FileOutputStream(archivoFinal);
            file.write(Files.readAllBytes(path));
            file.close();
            TipoDocumento tipoDoc = new TipoDocumento();
            
            tipoDoc.setId(TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS_REP.getIdTipoDocumento());
            
            DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
            documento.setNombre(nombreReporte);
            documento.setExtesion(".pdf");
            documento.setTipoContenido("application/pdf");
            documento.setMime("application/pdf");
            documento.setContenidoDocumento(archivoInforme);
            documento.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
            documento.setTipoDocumento(tipoDoc);
            documento.setIdTable(registro.getId());
            documento.setRegistroGeneradorDesechosRcoa(registro);//Registro de Generador de Desechos Peligrosos y Especiales
            //int procesoId = (int)bandejaTareasBean.getProcessId();
            //documento.setIdProceso(procesoId);

            documento = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
                    registro.getCodigo(), "REGISTRO GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documento, TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS_REP);
            guardarRegistroRGD(permisoRgd);
            generarOficioREP(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generarOficioREP(RegistroGeneradorDesechosRcoa registro){
        try {
            OficioPronunciamientoRgdRcoa oficio = new OficioPronunciamientoRgdRcoa();
            List<OficioPronunciamientoRgdRcoa> listaoficio = oficioPronunciamientoRgdRcoaFacade.findByGeneradorDesechos(registro.getId());
            if(listaoficio != null && listaoficio.size() > 0){
                oficio = listaoficio.get(0);
            }
            String identificacionUsuario = registro.getUsuario().getNombre();
            String cargo = "";
            String nombreEmpresa = "";
            String nombreOperador = "";
            String ruc = "";
            boolean juridico = false;
            if(identificacionUsuario.length() == 10){
                //cargo = registro.getUsuario().getPersona().getTitulo();
                nombreOperador = registro.getUsuario().getPersona().getNombre();
                nombreEmpresa = " ";
                ruc = " ";
                cargo = " ";
            }else{
                Organizacion organizacion = organizacionFacade.buscarPorRuc(identificacionUsuario);
                if(organizacion != null){
                    cargo = organizacion.getCargoRepresentante();
                    nombreOperador = organizacion.getPersona().getNombre();
                    nombreEmpresa = organizacion.getNombre();
                    ruc = organizacion.getRuc();
                    juridico = true;
                }else{
                    cargo = registro.getUsuario().getPersona().getTitulo();    
                    nombreOperador = registro.getUsuario().getPersona().getNombre();
                    nombreEmpresa = " ";
                    ruc = " ";
                }
            }
            
            EntityRegistroGeneradorDesechoOficio entity = new EntityRegistroGeneradorDesechoOficio();
            if(oficio != null && (oficio.getNumeroDocumento() == null || oficio.getNumeroDocumento().isEmpty())){
                direccionProvincial = areaFacade.getArea(253);
                oficio.setRegistroGeneradorDesechosRcoa(registro);
                oficio.setNumeroDocumento(generarCodigoOficioREP(direccionProvincial));
            }
            entity.setCodigo(oficio.getNumeroDocumento());
            entity.setAdicionalmente(" ");
            entity.setProvicional(" ");
            
            entity.setCargo(cargo);
            entity.setCargoAutoridad(areaResponsable);
            entity.setCedula(usuarioAutoridad.getNombre());
            entity.setCodigoPermisoRgd(codigoPermisoRgd);
            entity.setCodigoRgd(registro.getCodigo());
		// incluir informacion de la sede de la zonal en el documento
		ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
		String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "SP", null, null, null);
		entity.setFecha(nombreCanton+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
            //entity.setFecha(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
            entity.setFechaLetras(JsfUtil.devuelveFechaEnLetrasSinHora(registro.getFechaCreacion()));
            entity.setLicenciaEjecucion(" ");
            entity.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
            
            if(!juridico){
                entity.setNombreEmpresa(" ");
                entity.setOperadorEmpresa(" ");
                entity.setRuc(ruc);
                entity.setNombreProyecto(nombreOperador);
            }else{
                entity.setNombreEmpresa(nombreEmpresa);
                entity.setOperadorEmpresa("representante de la empresa " + nombreEmpresa + ",");
                entity.setRuc(ruc);
                entity.setNombreProyecto(nombreEmpresa);
            }
            
            entity.setOperador(nombreOperador);
            entity.setProvincia(" ");
            entity.setCanton("  ");
            if(registro.getPoliticaDesechoRgdRcoa() != null){
                switch (registro.getPoliticaDesechoRgdRcoa().getId().toString()) {
                case "1":
                    entity.setAcuerdoMinisterial("131 publicado en el R. O. 219 del 29 de diciembre de 2022");
                    break;
                case "2":
                    entity.setAcuerdoMinisterial("022 publicado en el R. O. 943 del 29 de abril de 2013");
                    break;
                case "3":
                    entity.setAcuerdoMinisterial("021 publicado en el R. O. 943 del 29 de abril de 2013");
                    break;
                case "4":
                    entity.setAcuerdoMinisterial("191 publicado en el R. O. 881 del 29 de enero de 2013");
                    break;
                case "5":
                    entity.setAcuerdoMinisterial("042 publicado en el R. O. 498 del 30 de mayo de 2019");
                    break;
                case "6":
                    entity.setAcuerdoMinisterial("MAATE-2022-067 publicado en el R. O. 117 del 01 de agosto de 2022");
                    break;                    
                case "7":
                    entity.setAcuerdoMinisterial("MAATE-2021-034 publicado en el R.O. 554 del 07 de octubre del 2021");
                    break; 
                case "8":
                    entity.setAcuerdoMinisterial("MAATE-2022-097 publicado en el Registro Oficial Suplemento No. 193, de 21 de noviembre de 2022");
                    break; 
                case "9":
                    entity.setAcuerdoMinisterial("134  publicado en el R. O. 490 del 1 de febrero de 2024");   
                default:
                    break;
                }
                entity.setGestionPolitica(registro.getPoliticaDesechoRgdRcoa().getNombre());
            }
            
            nombreFichero = "OficioPronunciamiento" + registro.getCodigo()+".pdf";
            nombreReporte = "OficioPronunciamiento.pdf";
            PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO_REP);            
            
            File informePdf = UtilGenerarInforme.generarFichero(
                    plantillaReporte.getHtmlPlantilla(),
                    nombreReporte, true, entity);
                    

            Path path = Paths.get(informePdf.getAbsolutePath());
            String reporteHtmlfinal = nombreReporte;
            archivoInforme = Files.readAllBytes(path);
            File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
            FileOutputStream file = new FileOutputStream(archivoFinal);
            file.write(Files.readAllBytes(path));
            file.close();
            
            TipoDocumento tipoDoc = new TipoDocumento();
            
            tipoDoc.setId(TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO_REP.getIdTipoDocumento());
            
            DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
            documento.setNombre(nombreReporte);
            documento.setExtesion(".pdf");
            documento.setTipoContenido("application/pdf");
            documento.setMime("application/pdf");
            documento.setContenidoDocumento(archivoInforme);
            documento.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
            documento.setTipoDocumento(tipoDoc);
            documento.setIdTable(registro.getId());
            //int procesoId = (int)bandejaTareasBean.getProcessId();
            //documento.setIdProceso(procesoId);
            documento.setRegistroGeneradorDesechosRcoa(registro);//Registro de Generador de Desechos Peligrosos y Especiales
            

            documento = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
                    registro.getCodigo(), "OFICIO PRONUNCIAMIENTO GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documento, TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO_REP);
            guardarOficio(oficio);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void guardarOficio(OficioPronunciamientoRgdRcoa oficio ){
        if(oficio.getId() == null){
            oficio.setEstado(true);
            oficio.setFechaCreacion(new Date());
        }
        oficio.setFechaCreacionDocumento(new Date());
        oficioPronunciamientoRgdRcoaFacade.save(oficio, loginBean.getUsuario());
    }
    
    private void guardarRegistroRGD(PermisoRegistroGeneradorDesechos permisoRgd ){
        if(permisoRgd.getId() == null){
            permisoRgd.setEstado(true);
            permisoRgd.setFechaCreacion(new Date());
        }
        permisoRgd.setFechaCreacionDocumento(new Date());
        permisoRegistroGeneradorDesechosFacade.save(permisoRgd, loginBean.getUsuario());
    }
}
