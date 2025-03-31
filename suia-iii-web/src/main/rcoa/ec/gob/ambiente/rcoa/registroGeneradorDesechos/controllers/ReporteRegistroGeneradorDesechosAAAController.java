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
import ec.gob.ambiente.rcoa.digitalizacion.facade.UbicacionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.dto.EntityRegistroGeneradorDesecho;
import ec.gob.ambiente.rcoa.dto.EntityRegistroGeneradorDesechoOficio;
import ec.gob.ambiente.rcoa.facade.ProyectoCertificadoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.OficioPronunciamientoRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PermisoRegistroGeneradorDesechosFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.OficioPronunciamientoRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PermisoRegistroGeneradorDesechos;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class ReporteRegistroGeneradorDesechosAAAController {
	
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
	@EJB
	private ProyectoCertificadoAmbientalFacade proyectoCertificadoAmbientalFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalFacade;
	@EJB
	private InformeOficioFacade informeOficioFacade;
	@EJB
	private UbicacionDigitalizacionFacade ubicacionDigitalizacionFacade;
	/***************************************************************************************************/
	

	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoSuia;
	/****************************************************************************************************/
	
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
    private RegistroGeneradorDesechosProyectosRcoa registroGeneradorDesechosProyectosRcoa;
    
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
	private AutorizacionAdministrativaAmbiental proyectoAAA;
	
	public void generarRegistro(RegistroGeneradorDesechosRcoa registro, boolean provicional){
		try {
			//Ver como hacer para buscar el área responsable para varios proyectos cuando debamos hacer la parte de agrupación de proyectos 
			proyectoSuia = new ProyectoLicenciamientoAmbiental();
			PermisoRegistroGeneradorDesechos permisoRgd = new PermisoRegistroGeneradorDesechos();
			List<PermisoRegistroGeneradorDesechos> listaPermiso = permisoRegistroGeneradorDesechosFacade.findByRegistroGenerador(registro.getId());
			if(listaPermiso != null && listaPermiso.size() > 0){
				permisoRgd = listaPermiso.get(0);
			}		
			this.provicional = provicional;
			
			List<RegistroGeneradorDesechosProyectosRcoa> listaProyecto = new ArrayList<>();
			
			listaProyecto = registroGeneradorDesechosProyectosRcoaFacade.buscarPorRegistroGenerador(registro.getId());
			
			registroGeneradorDesechosProyectosRcoa = listaProyecto.get(0);
			ProyectoLicenciaCoa proyectoActivo = registroGeneradorDesechosProyectosRcoa.getProyectoLicenciaCoa();
			proyectoAAA = new AutorizacionAdministrativaAmbiental();
			if(registroGeneradorDesechosProyectosRcoa.getProyectoDigitalizado() != null)
				proyectoAAA = registroGeneradorDesechosProyectosRcoa.getProyectoDigitalizado();

			actividadPrincipal = "";
			codigoCiiu="";
			String identificacionUsuario = "";
			String cargo = "";
			String direccion = "";
			String correo = "";
			String telefono = "";
			String celular = "";
			String nombreEmpresa = "";
			String representante = "";
			String codigoProyecto = "";
			String nombreProyecto = "";
			String direccionProyecto = "";
			
			Usuario usuarioProponente = new Usuario();
			personaJuridica = false;
			UbicacionesGeografica ubicacion = new UbicacionesGeografica();
			if(proyectoActivo != null && proyectoActivo.getId() != null){
				if(proyectoActivo.getCategorizacion() == 1 || proyectoActivo.getCategorizacion() == 2){
					licenciaNueva = true;
				}
				
				if(proyectoActivo.getCategorizacion() == 3 || proyectoActivo.getCategorizacion() == 4){
					if(proyectoActivo.getTipoProyecto() == 1){
						licenciaNueva = true;
					}else if(proyectoActivo.getTipoProyecto() == 2){
						licenciaEjecucion = true;					
					}
				}			
				
				List<ProyectoLicenciaCuaCiuu> listaProyectoActividadesPrincipal = registroGeneradorDesechosRcoaFacade.buscarActividadesCiuPrincipal(proyectoActivo);
				if(listaProyectoActividadesPrincipal != null && !listaProyectoActividadesPrincipal.isEmpty()){
					actividadPrincipal = listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getNombre();
					codigoCiiu = listaProyectoActividadesPrincipal.get(0).getCatalogoCIUU().getCodigo();
				}
				ProyectoLicenciaCoaUbicacion ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoActivo);
				if(ubicacionPrincipal != null && ubicacionPrincipal.getId() != null){
					ubicacion = ubicacionPrincipal.getUbicacionesGeografica();
				}
				usuarioProponente = proyectoActivo.getUsuario();
				codigoProyecto = proyectoActivo.getCodigoUnicoAmbiental();
				nombreProyecto = proyectoActivo.getNombreProyecto();
				direccionProyecto = proyectoActivo.getDireccionProyecto();
			}else if(listaProyecto.get(0).getProyectoId() != null){
				proyectoSuia = proyectoLicenciamientoAmbientalFacade.buscarProyectosLicenciamientoAmbientalPorId(listaProyecto.get(0).getProyectoId());
				ubicacion = proyectoSuia.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
				usuarioProponente = proyectoSuia.getUsuario();
				codigoProyecto = proyectoSuia.getCodigo();
				nombreProyecto = proyectoSuia.getNombre();
				direccionProyecto = proyectoSuia.getDireccionProyecto();
			}else if(proyectoAAA != null && proyectoAAA.getId() != null){
				actividadPrincipal = proyectoAAA.getCatalogoCIUU().getNombre();
				codigoCiiu = proyectoAAA.getCatalogoCIUU().getCodigo();
				// listo las ubicaciones del proyecto original
				List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(proyectoAAA.getId(), 1, "WGS84", "17S");
				if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
					ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(proyectoAAA.getId(), 3, "WGS84", "17S");
				}
				// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
				if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
					ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(proyectoAAA.getId(), 2);
				}
				// si  existen busco el area 
				if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
					ubicacion = ListaUbicacionTipo.get(0).getUbicacionesGeografica();
				}
				usuarioProponente = proyectoAAA.getUsuario();
				codigoProyecto = proyectoAAA.getCodigoProyecto();
				nombreProyecto = proyectoAAA.getNombreProyecto();
				direccionProyecto = "";
			}
			
			identificacionUsuario = usuarioProponente.getNombre();
			if(identificacionUsuario.length() == 10){
				cargo = usuarioProponente.getPersona().getTitulo();				
				
				List<Contacto> contacto = contactoFacade.buscarPorPersona(usuarioProponente.getPersona());
				
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
				
				nombreEmpresa = usuarioProponente.getPersona().getNombre();
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
					
					cargo = usuarioProponente.getPersona().getTitulo();
					
					List<Contacto> contacto = contactoFacade.buscarPorPersona(usuarioProponente.getPersona());
					
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
					
					nombreEmpresa = usuarioProponente.getPersona().getNombre();
					representante = nombreEmpresa;					
				}	
				
			}		

			EntityRegistroGeneradorDesecho entity = new EntityRegistroGeneradorDesecho();
			
			rolDirector = "";
			cargoAutoridad = "";

			datosOficio(proyectoActivo);
			
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
			if(permisoRgd != null && (permisoRgd.getNumeroDocumento() == null || permisoRgd.getNumeroDocumento().isEmpty())){
				direccionProvincial = areaFacade.getArea(253);
				permisoRgd.setRegistroGeneradorDesechosRcoa(registro);
				if(provicional){
					permisoRgd.setNumeroDocumento(generarCodigoRGD(oficinaTecnica)+"-PROVISIONAL");
				}else{
					permisoRgd.setNumeroDocumento(generarCodigoRGD(oficinaTecnica));
				}
			}
			entity.setCodigoRegistroGenerador(permisoRgd.getNumeroDocumento());
			//solo persona juridica
			if(personaJuridica){
				entity.setCargoEmpresa(cargo);
			}
			
			String datos = "<table border=\"1\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\">";
			datos += "<tbody><tr><td colspan=\"2\"><strong>A. INFORMACI&Oacute;N GENERAL DEL REGISTRO DE GENERADOR</strong></td></tr>";
		
			datos += "<tr><td style=\"width:50%\">C&oacute;digo del Tr&aacute;mite:</td><td style=\"width:50%\">" + registro.getCodigo()  + "</td></tr>";
			datos += "<tr><td style=\"width:50%\">Fecha de primera emisi&oacute;n del Registro de Generador:</td><td style=\"width:50%\">" + entity.getFechaEmisionRgd()  + "</td></tr>";
			
			if(actualizacion){
				 datos+="<tr><td>Número de actualizaci&oacute;n:</td><td>"+ entity.getNumeroActualizacion()+"</td></tr>";
				 datos+="<tr><td>Fecha de última actualizaci&oacute;n:</td><td>"+ entity.getFechaActualizacionRgd()+"</td></tr>";
			}
			datos +="</tbody></table>";
			datos +="<br/><br/>";
			
			datos += "<table border=\"1\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\">";
			datos += "<tbody><tr><td colspan=\"2\"><strong>B. INFORMACI&Oacute;N DEL GENERADOR</strong></td></tr>";
			datos += "<tr><td>RUC/Cédula de Identidad:</td><td>"+ usuarioProponente.getNombre() +"</td></tr>";
			datos += "<tr><td>Nombre del operador:</td><td>"+ nombreEmpresa +"</td></tr>";
			datos += "<tr><td>Responsable o representante legal de la empresa:</td><td>"+representante+"</td></tr>";
			if(personaJuridica){
				datos +="<tr><td>Cargo o puesto en la empresa:</td><td>" + entity.getCargoEmpresa()+"</td></tr>";
			}
			datos +="<tr><td>C&oacute;digo del proyecto, obra o actividad regularizado  al cual se vincula el registro de generador::</td><td>"+codigoProyecto+"</td></tr>";
			datos +="<tr><td>Número y descripci&oacute;n del c&oacute;digo CIIU del proyecto, obra o actividad regularizado:</td><td>"+codigoCiiu+"<br/>"+actividadPrincipal+"</td></tr>";
			
			datos += "<tr><td>Autorización Administrativa Ambiental del proyecto, obra o actividad regularizado:</td><td>"+obtenerResolucion()+"</td></tr>";
			
			datos +="</tbody></table>";
			
			entity.setTabla(datos);
			
			
			entity.setNombreProyecto(nombreProyecto);
			
			
			entity.setDireccionProyectoRgd(direccionProyecto);
			
			entity.setDesechos(getDesechosPeligrosos(registro));
			List<PuntoRecuperacionRgdRcoa> puntosRecuperacion = puntoRecuperacionRgdRcoaFacade.buscarPorRgd(registro.getId());
			entity.setDisplayUbicacion("inline");
			if(puntosRecuperacion != null && puntosRecuperacion.size() > 10)
				entity.setDisplayUbicacion("none");
			//entity.setUbicacionPuntosGeneracion(getUbicacionPuntosMonitoreo(puntosRecuperacion));
			String informacionUbicacion = "";
			informacionUbicacion += "<table style=\"width: 300px; font-size: 11px !important;\" border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\">";			
			informacionUbicacion += "<tbody><tr>"
					+ "<td><strong>DIRECCIÓN</strong></td>"
					+ "<td colspan=\"2\">"+ direccionProyecto +"</td>"
					+ "</tr>"
					+"<tr>"
					+ "<td><strong>PROVINCIA</strong></td>"
					+ "<td colspan=\"2\">"+ (ubicacion != null ? ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() : "")+"</td>"
					+ "</tr>"
					+"<tr>"
					+ "<td><strong>CANTÓN</strong></td>"
					+ "<td colspan=\"2\">"+ (ubicacion != null ? ubicacion.getUbicacionesGeografica().getNombre() : "")+"</td>"
					+ "</tr>"
					+"<tr>"
					+ "<td><strong>PARROQUIA</strong></td>"
					+ "<td colspan=\"2\">"+ (ubicacion != null ? ubicacion.getNombre(): "")+"</td>"
					+ "</tr>";
			informacionUbicacion += "</tbody></table><br /><br />";
			entity.setUbicacionPuntosGeneracion(informacionUbicacion);
				
			entity.setPuntosGeneracion(getPuntosMonitoreo(puntosRecuperacion));
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
			
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS_AAA);
			
			File informePdf = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					nombreReporte, true, entity);
					
			if(puntosRecuperacion != null && puntosRecuperacion.size() > 10){
				File anexoPuntos = UtilGenerarInforme.generarFichero(
						entity.getPuntosGeneracion(),
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
			
			tipoDoc.setId(TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS_AAA.getIdTipoDocumento());
			
			DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
			documento.setNombre(nombreReporte);
			documento.setExtesion(".pdf");
			documento.setTipoContenido("application/pdf");
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
			documento.setTipoDocumento(tipoDoc);
			documento.setIdTable(registro.getId());
			documento.setRegistroGeneradorDesechosRcoa(registro);//Registro de Generador de Desechos Peligrosos y Especiales
			//int procesoId = (int)bandejaTareasBean.getProcessId();
			//documento.setIdProceso(procesoId);

			documento = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
					registro.getCodigo(), "REGISTRO GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documento, TipoDocumentoSistema.RGD_REGISTRO_GENERADOR_DESECHOS_AAA);
			guardarRegistroRGD(permisoRgd);
			generarOficio(registro, proyectoActivo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String obtenerResolucion(){
		String sistema = (registroGeneradorDesechosProyectosRcoa.getDescripcionSistema() != null && !registroGeneradorDesechosProyectosRcoa.getDescripcionSistema().isEmpty())?registroGeneradorDesechosProyectosRcoa.getDescripcionSistema():"";
		String resolucion = "";
		try {
			switch (sistema) {
			case "rcoa":
				ProyectoLicenciaCoa proyecto = registroGeneradorDesechosProyectosRcoa.getProyectoLicenciaCoa();
				if(proyecto.getCategoria().getNombrePublico().equals("Certificado Ambiental")){
					ProyectoCertificadoAmbiental certificado = proyectoCertificadoAmbientalFacade.getProyectoCertificadoAmbientalPorCodigoProyecto(proyecto);
					resolucion = certificado.getCodigoCertificado();
				}else if(proyecto.getCategoria().getNombrePublico().equals("Registro Ambiental")){
					RegistroAmbientalRcoa registro = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyecto);
					resolucion = registro.getNumeroResolucion();
				}
				break;
			case "suia_iii":
				if(proyectoSuia.getCatalogoCategoria().getCategoria().getNombrePublico().equals("Registro Ambiental")){
					FichaAmbientalPma fichaAmbiental = fichaAmbientalFacade.getFichaAmbientalPorIdProyecto(Integer.valueOf(proyectoSuia.getId()));
					if(fichaAmbiental != null && fichaAmbiental.getNumeroLicencia() != null){
						resolucion = fichaAmbiental.getNumeroLicencia();
					}
				}else if(proyectoSuia.getCatalogoCategoria().getCategoria().getNombrePublico().equals("Licencia Ambiental")){
					resolucion = informeOficioFacade.obtenerResolucion(proyectoSuia.getCodigo());
				}else if(proyectoSuia.getCatalogoCategoria().getCategoria().getNombrePublico().equals("Certificado Ambiental")){
					resolucion = " ";
				}
				break;
			case "digitalizacion":
				if(proyectoAAA != null && proyectoAAA.getId() != null){
					resolucion = proyectoAAA.getResolucion();
				}
				break;

			default:
				break;
			}
			if(resolucion == null || resolucion.isEmpty()){
				resolucion = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resolucion;
	}
	
	private String getDesechosPeligrosos(RegistroGeneradorDesechosRcoa registro)
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
	
	private String getPuntosMonitoreo(List<PuntoRecuperacionRgdRcoa> puntosRecuperacion)
	{
		String puntos = "";
		if(puntosRecuperacion != null && puntosRecuperacion.size() > 10){
			puntos += "<br/><br/><table border=\"0\" cellpadding=\"7\" cellspacing=\"0\" style=\"width: 700px;font-size: 11px !important;border:1;\">";
			puntos += "<tbody><tr><td colspan=\"2\"><strong>F. ANEXOS</strong></td></tr>";
			puntos += "<tr><td  colspan=\"2\">Ubicación de puntos de generación dentro de las instalaciones del proyecto, obra o actividad regularizada:</td></tr>";
			puntos +="</tbody></table>";
			puntos +="<br/><br/>";
		}
		for(PuntoRecuperacionRgdRcoa punto : puntosRecuperacion){
			puntos += "<table style=\"width: 300px; font-size: 11px !important;\" border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\">";
			puntos += "<tbody><tr>"
					+ "<td><strong>Nombre del punto de generación</strong></td>"
					+ "<td colspan=\"2\">"+ punto.getNombre()+"</td>"
					+ "</tr>"
					+"<tr>"
					+ "<td><strong>Origen de la generación</strong></td>"
					+ "<td colspan=\"2\">"+ punto.getPuntoGeneracion().getNombre()+(punto.getPuntoGeneracion().getClave().equals("OT")?": "+punto.getGeneracionOtros():"")+"</td>"
					+ "</tr>";
			puntos += "</tbody></table><br />";
		}
		return puntos;
	}
	
	private String getUbicacionPuntosMonitoreo(List<PuntoRecuperacionRgdRcoa> puntosRecuperacion)
	{
		String puntos = "";
		for(PuntoRecuperacionRgdRcoa punto : puntosRecuperacion){
			puntos += "<table style=\"width: 300px; font-size: 11px !important;\" border=\"1\" cellpadding=\"10\" cellspacing=\"0\" class=\"w600Table\">";
			puntos += "<tbody><tr>"
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
			String identificacionUsuario = "";
			String cargo = "";
			String nombreEmpresa = "";
			String nombreOperador = "";
			String ruc = "";
			String codigoProyecto = "";
			String nombreproyecto = "";
			String direccionProyecto = "";
			boolean juridico = false;
			Usuario usuarioProponente = new Usuario();
			EntityRegistroGeneradorDesechoOficio entity = new EntityRegistroGeneradorDesechoOficio();
			if(oficio != null && (oficio.getNumeroDocumento() == null || oficio.getNumeroDocumento().isEmpty())){
				oficio.setRegistroGeneradorDesechosRcoa(registro);
				oficio.setNumeroDocumento(generarCodigoOficio(oficinaTecnica));
			}
			entity.setCodigo(oficio.getNumeroDocumento());
			entity.setProvincia(" ");
			entity.setCanton("  ");
			if(proyectoActivo != null && proyectoActivo.getId() != null){
				usuarioProponente =  proyectoActivo.getUsuario();
				listaUbicaciones = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoActivo);
				ProyectoLicenciaCoaUbicacion ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoActivo);
				if(ubicacionPrincipal != null && ubicacionPrincipal.getId() != null){
//					entity.setProvincia(listaUbicaciones.get(0).getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
//					entity.setCanton(listaUbicaciones.get(0).getUbicacionesGeografica().getNombre());
					entity.setProvincia(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
					entity.setCanton(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
				}
				codigoProyecto = proyectoActivo.getCodigoUnicoAmbiental();
				nombreproyecto = proyectoActivo.getNombreProyecto();
				direccionProyecto = proyectoActivo.getDireccionProyecto();
				// incluir informacion de la sede de la zonal en el documento
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
				String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyectoActivo, null, null);
				entity.setFecha(nombreCanton+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
			}else{
				if(proyectoSuia != null && proyectoSuia.getId() != null && proyectoSuia.getProyectoUbicacionesGeograficas() != null && proyectoSuia.getProyectoUbicacionesGeograficas().size() > 0){
					usuarioProponente =  proyectoSuia.getUsuario();
					UbicacionesGeografica ubicacion = proyectoSuia.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
					if(ubicacion != null && ubicacion.getId() != null){
						entity.setProvincia(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
						entity.setCanton(ubicacion.getUbicacionesGeografica().getNombre());
					}
					codigoProyecto = proyectoSuia.getCodigo();
					nombreproyecto = proyectoSuia.getNombre();
					direccionProyecto = proyectoSuia.getDireccionProyecto();
					// incluir informacion de la sede de la zonal en el documento
					ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
					String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTOSUIAIII", null, proyectoSuia, null);
					entity.setFecha(nombreCanton+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
				}else if(proyectoAAA != null && proyectoAAA.getId() != null){
					usuarioProponente = proyectoAAA.getUsuario();
					// listo las ubicaciones del proyecto original
					List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(proyectoAAA.getId(), 1, "WGS84", "17S");
					if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
						ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(proyectoAAA.getId(), 3, "WGS84", "17S");
					}
					// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
					if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
						ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(proyectoAAA.getId(), 2);
					}
					// si  existen busco el arae 
					UbicacionesGeografica ubicacion = new UbicacionesGeografica();
					if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
						ubicacion = ListaUbicacionTipo.get(0).getUbicacionesGeografica();
					}
					if(ubicacion != null && ubicacion.getId() != null){
						entity.setProvincia(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
						entity.setCanton(ubicacion.getUbicacionesGeografica().getNombre());
					}
					codigoProyecto = proyectoAAA.getCodigoProyecto();
					nombreproyecto = proyectoAAA.getNombreProyecto();
					direccionProyecto = "";
					// incluir informacion de la sede de la zonal en el documento
					ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
					String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTODIGITALIZADO", null, null, proyectoAAA);
					entity.setFecha(nombreCanton+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
				}
			}
			
			identificacionUsuario = usuarioProponente.getNombre();
			if(identificacionUsuario.length() == 10){
				cargo = usuarioProponente.getPersona().getTitulo();	
				nombreOperador = usuarioProponente.getPersona().getNombre();	
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
					cargo = usuarioProponente.getPersona().getTitulo();	
					nombreOperador = usuarioProponente.getPersona().getNombre();	
					nombreEmpresa = " ";
					ruc = " ";
				}				
			}			
			
			
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
			if(codigoCiiu.isEmpty())
				entity.setCodigoCIIU("");
			else
				entity.setCodigoCIIU("con c&oacute;digo CIIU "+codigoCiiu+",");
			entity.setCodigoPermisoRgd(codigoPermisoRgd);
			entity.setCodigoRgd(registro.getCodigo());
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
			
			entity.setNombreProyecto(nombreproyecto);
			entity.setOperador(nombreOperador);
										
			
			nombreFichero = "OficioPronunciamiento" + registro.getCodigo()+".pdf";
			nombreReporte = "OficioPronunciamiento.pdf";
			
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO_AAA);			
			
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
			
			tipoDoc.setId(TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO_AAA.getIdTipoDocumento());
			
			DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
			documento.setNombre(nombreReporte);
			documento.setExtesion(".pdf");
			documento.setTipoContenido("application/pdf");			
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
			documento.setTipoDocumento(tipoDoc);
			documento.setIdTable(registro.getId());
			//int procesoId = (int)bandejaTareasBean.getProcessId();
			//documento.setIdProceso(procesoId);
			documento.setRegistroGeneradorDesechosRcoa(registro);//Registro de Generador de Desechos Peligrosos y Especiales
			

			documento = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
					registro.getCodigo(), "OFICIO PRONUNCIAMIENTO GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documento, TipoDocumentoSistema.RGD_OFICIO_PRONUNCIAMIENTO_AAA);
			guardarOficio(oficio);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	
	public void generarOficioArchivo(RegistroGeneradorDesechosRcoa registro, ProyectoLicenciaCoa proyectoActivo, boolean provicional){
		try {
			String identificacionUsuario = "";
			String cargo = "";
			String nombreEmpresa = "";
			String nombreOperador = "";
			String ruc = "";
			String codigoProyecto = "";
			String nombreProyecto = "";
			String direccionProyecto = "";
			boolean juridico = false;
			Usuario usuarioProponente = new Usuario();
			EntityRegistroGeneradorDesechoOficio entity = new EntityRegistroGeneradorDesechoOficio();
			entity.setProvincia(" ");
			entity.setCanton("  ");
			if(proyectoActivo != null && proyectoActivo.getId() != null){
				usuarioProponente =  proyectoActivo.getUsuario();
				listaUbicaciones = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoActivo);
				ProyectoLicenciaCoaUbicacion ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoActivo);
				if(ubicacionPrincipal != null && ubicacionPrincipal.getId() != null){
//					entity.setProvincia(listaUbicaciones.get(0).getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
//					entity.setCanton(listaUbicaciones.get(0).getUbicacionesGeografica().getNombre());
					entity.setProvincia(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
					entity.setCanton(ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
				}
				codigoProyecto = proyectoActivo.getCodigoUnicoAmbiental();
				nombreProyecto = proyectoActivo.getNombreProyecto();
				direccionProyecto = proyectoActivo.getDireccionProyecto();
				// incluir informacion de la sede de la zonal en el documento
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
				String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyectoActivo, null, null);
				entity.setFecha(nombreCanton+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
			}else{
				if(proyectoSuia != null && proyectoSuia.getId() != null && proyectoSuia.getProyectoUbicacionesGeograficas() != null && proyectoSuia.getProyectoUbicacionesGeograficas().size() > 0){
					usuarioProponente =  proyectoSuia.getUsuario();
					UbicacionesGeografica ubicacion = proyectoSuia.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
					if(ubicacion != null && ubicacion.getId() != null){
						entity.setProvincia(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
						entity.setCanton(ubicacion.getUbicacionesGeografica().getNombre());
					}
					codigoProyecto = proyectoSuia.getCodigo();
					nombreProyecto = proyectoSuia.getNombre();
					direccionProyecto = proyectoSuia.getDireccionProyecto();
					// incluir informacion de la sede de la zonal en el documento
					ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
					String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTOSUIAIII", null, proyectoSuia, null);
					entity.setFecha(nombreCanton+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
				}else if(proyectoAAA != null && proyectoAAA.getId() != null){
					usuarioProponente = proyectoAAA.getUsuario();
					// listo las ubicaciones del proyecto original
					List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(proyectoAAA.getId(), 1, "WGS84", "17S");
					if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
						ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(proyectoAAA.getId(), 3, "WGS84", "17S");
					}
					// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
					if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
						ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(proyectoAAA.getId(), 2);
					}
					// si  existen busco el arae 
					UbicacionesGeografica ubicacion = new UbicacionesGeografica();
					if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
						ubicacion = ListaUbicacionTipo.get(0).getUbicacionesGeografica();
					}
					if(ubicacion != null && ubicacion.getId() != null){
						entity.setProvincia(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
						entity.setCanton(ubicacion.getUbicacionesGeografica().getNombre());
					}
					codigoProyecto = proyectoAAA.getCodigoProyecto();
					nombreProyecto = proyectoAAA.getNombreProyecto();
					direccionProyecto = "";
					// incluir informacion de la sede de la zonal en el documento
					ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
					String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTODIGITALIZADO", null, null, proyectoAAA);
					entity.setFecha(nombreCanton+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
				}
			}

			identificacionUsuario = usuarioProponente.getNombre();
			if(identificacionUsuario.length() == 10){
				cargo = usuarioProponente.getPersona().getTitulo();	
				nombreOperador = usuarioProponente.getPersona().getNombre();	
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
					cargo = usuarioProponente.getPersona().getTitulo();	
					nombreOperador = usuarioProponente.getPersona().getNombre();	
					nombreEmpresa = " ";
					ruc = " ";
				}				
			}
			datosOficio(proyectoActivo);
			entity.setCodigo(generarCodigoOficio(direccionProvincial));
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
			entity.setCodigoPermisoRgd(codigoProyecto);
			entity.setCodigoRgd(registro.getCodigo());
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
			
			nombreFichero = "OficioArchivacion" + registro.getCodigo()+".pdf";
			nombreReporte = "OficioArchivacion_" + registro.getCodigo()+".pdf";
			
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RGD_OFICIO_ARCHIVACION);			
			
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
			
			tipoDoc.setId(TipoDocumentoSistema.RGD_OFICIO_ARCHIVACION.getIdTipoDocumento());
			
			DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
			documento.setNombre(nombreReporte);
			documento.setExtesion(".pdf");
			documento.setTipoContenido("application/pdf");			
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
			documento.setTipoDocumento(tipoDoc);
			documento.setIdTable(registro.getId());
			int procesoId = (int)bandejaTareasBean.getProcessId();
			documento.setIdProceso(procesoId);
			documento.setRegistroGeneradorDesechosRcoa(registro);//Registro de Generador de Desechos Peligrosos y Especiales
			

			documento = documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(
					registro.getCodigo(), "OFICIO ARCHIVACION GENERADOR DE DESECHOS PELIGROSOS Y ESPECIALES", 0L, documento, TipoDocumentoSistema.RGD_OFICIO_ARCHIVACION);	
			
			OficioPronunciamientoRgdRcoa oficio = new OficioPronunciamientoRgdRcoa();
			oficio.setEstado(true);
			oficio.setRegistroGeneradorDesechosRcoa(registro);
			oficio.setFechaCreacion(new Date());
			oficio.setFechaCreacionDocumento(new Date());
			oficio.setNumeroDocumento(entity.getCodigo());
			
			oficioPronunciamientoRgdRcoaFacade.save(oficio, loginBean.getUsuario());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void datosOficio (ProyectoLicenciaCoa proyectoActivo) throws ServiceException{
		if(proyectoActivo != null && proyectoActivo.getId() != null){
			if (proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyectoActivo.getIdCantonOficina());
				try {
					rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");					
					
					List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, ubicacion.getAreaCoordinacionZonal().getArea());
					
					if(listaUsuarios != null && !listaUsuarios.isEmpty()){
						usuarioAutoridad = listaUsuarios.get(0);						
					}else{
						JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
						System.out.println("No existe usuario " + rolDirector + " para el area " + ubicacion.getAreaCoordinacionZonal().getArea().getAreaName());						
						return;
					}					
				} catch (Exception e) {
					JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
					System.out.println("No existe usuario " + rolDirector + "para el area " + ubicacion.getAreaCoordinacionZonal().getArea().getAreaName());					
					return;
				}								
				
				areaResponsable = "DIRECCIÓN ZONAL";
				direccionProvincial = ubicacion.getAreaCoordinacionZonal().getArea();	
				oficinaTecnica = ubicacion.getAreaCoordinacionZonal();
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
					
//					List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, proyectoActivo.getAreaInventarioForestal().getArea());
					areaResponsable = "DIRECCIÓN ZONAL";//proyectoActivo.getAreaInventarioForestal().getArea().getAreaName();
					if(proyectoActivo.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS))
						areaResponsable = proyectoActivo.getAreaResponsable().getAreaName();
					if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("DP")){
						direccionProvincial = proyectoActivo.getAreaInventarioForestal();
					}else{
						direccionProvincial = proyectoActivo.getAreaInventarioForestal().getArea();
					}
					
					
					oficinaTecnica = proyectoActivo.getAreaInventarioForestal();
					
//					if(listaUsuarios != null && !listaUsuarios.isEmpty()){
//						usuarioAutoridad = listaUsuarios.get(0);
						usuarioAutoridad = loginBean.getUsuario();
						
						if(usuarioAutoridad.getPersona().getGenero().equals("FEMENINO")){
							cargoAutoridad = "DIRECTORA ZONAL" ; // + areaResponsable;								
						}else if(usuarioAutoridad.getPersona().getGenero().equals("MASCULINO")){
							cargoAutoridad = "DIRECTOR ZONAL"; // + areaResponsable;	
						}else{
							cargoAutoridad = "DIRECTOR/A ZONAL"; // + areaResponsable;	
						}											
//					}else{
//						JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
//						return;
//					}					
				} catch (Exception e) {
					JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
					return;
				}	
			}			
		}else{
			areaResponsable = "DIRECCIÓN ZONAL";
			rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");

			// si es proyectos suia busco la direccion zonal en base a la ubicacion geografica
			if(proyectoSuia != null && proyectoSuia.getId() != null){
				if(proyectoSuia.getProyectoUbicacionesGeograficas() != null && proyectoSuia.getProyectoUbicacionesGeograficas().size() > 0){
					UbicacionesGeografica ubicacion = proyectoSuia.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
					if(proyectoSuia.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
						direccionProvincial = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
						oficinaTecnica = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal();
					}else{
						if(proyectoSuia.getAreaResponsable().getTipoArea().getSiglas().equals("DP")){
							if(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
								direccionProvincial = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
								areaResponsable = direccionProvincial.getAreaName();
							}else{
								direccionProvincial = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal();
							}
						}else{
							direccionProvincial = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
						}
						if(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
							oficinaTecnica = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
						} else {
							oficinaTecnica = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal();
						}
					}
				}
			}else if(proyectoAAA != null && proyectoAAA.getId() != null){
				// listo las ubicaciones del proyecto original
				List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(proyectoAAA.getId(), 1, "WGS84", "17S");
				if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
					ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(proyectoAAA.getId(), 3, "WGS84", "17S");
				}
				// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
				if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
					ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(proyectoAAA.getId(), 2);
				}
				// si  existen busco el arae 
				UbicacionesGeografica ubicacion = new UbicacionesGeografica();
				if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
					ubicacion = ListaUbicacionTipo.get(0).getUbicacionesGeografica();
				}
				if(proyectoAAA.getAreaResponsableControl().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
					direccionProvincial = proyectoAAA.getAreaResponsableControl();
					oficinaTecnica = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal();
				}else{
					if(proyectoAAA.getAreaResponsableControl().getTipoArea().getSiglas().equals("DP")){
						if(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
							direccionProvincial = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
							areaResponsable = direccionProvincial.getAreaName();
						} else {
							direccionProvincial = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal();
						}
					}else{
						direccionProvincial = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
					}
					
					if(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
						oficinaTecnica = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
					} else {
						oficinaTecnica = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal();
					}
				}
			}
			usuarioAutoridad = loginBean.getUsuario();
			
			if(usuarioAutoridad.getPersona().getGenero().equals("FEMENINO")){
				cargoAutoridad = "DIRECTORA ZONAL" ; // + areaResponsable;
			}else if(usuarioAutoridad.getPersona().getGenero().equals("MASCULINO")){
				cargoAutoridad = "DIRECTOR ZONAL"; // + areaResponsable;
			}else{
				cargoAutoridad = "DIRECTOR/A ZONAL"; // + areaResponsable;
			}
		}
	}
	
	//SUIA-MM-AAAA-MAAE-SIGLAS (OFICINA TECNICA)-RGD-NUMERO CONSECUTIVO-PROVISIONAL
	private String generarCodigoRGD(Area area) {
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
	
	//MAAE-2020-SIGLAS (OFICINA TECNICA)-000779
	private String generarCodigoOficio(Area area) {
		String anioActual=secuenciasFacade.getCurrentYear();
		String nombreSecuencia="OFICIO_PRONUNCIAMIENTO_RGD_"+anioActual+"_"+area.getAreaAbbreviation();
		
		try {
			return Constantes.SIGLAS_INSTITUCION + "-"
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
