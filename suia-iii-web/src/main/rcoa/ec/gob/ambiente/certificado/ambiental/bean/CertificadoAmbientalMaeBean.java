package ec.gob.ambiente.certificado.ambiental.bean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.prevencion.certificado.ambiental.controllers.GenerarQRCertificadoAmbiental;
import ec.gob.ambiente.rcoa.dto.EntityCertificadoAmbiental;
import ec.gob.ambiente.rcoa.facade.ProyectoCertificadoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ReporteInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ReporteInventarioForestal;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoGeneracionDesechoFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionDesecho;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.UbicacionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeRevisionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@Stateless
public class CertificadoAmbientalMaeBean {

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
    private ProyectoLicenciaCoa proyectoLicenciaCoa;
    @EJB
    private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    @EJB
    private PersonaFacade personaFacade;
    
    @EJB
    private AreaFacade areaFacade;
    
    @EJB
	private ProyectoCertificadoAmbientalFacade proyectoCertificadoAmbientalFacade;
    
    @EJB
    private OrganizacionFacade organizacionFacade;
    @EJB
    private ContactoFacade contactoFacade;
    @EJB
    private CategoriaIIFacade categoriaIIFacade;
	@EJB
	UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	
	@EJB
	private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	
	@EJB
	private PuntoGeneracionDesechoFacade puntoGeneracionDesechoFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private InformeRevisionForestalFacade informeInspeccionFacade;
	
	@EJB
	private InformeInspeccionBiodiversidadFacade informeInspeccionSnapFacade;
    @EJB
    private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
    @EJB
    private UbicacionSustanciaQuimicaFacade ubicacionSustanciaQuimicaFacade; 
	@EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
	@EJB
	private ReporteInventarioForestalFacade reporteInventarioForestalFacade;
	
	private ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = (ProyectoSedeZonalUbicacionController) BeanLocator.getInstance(ProyectoSedeZonalUbicacionController.class);
	
    public String visualizarOficio(String codigo, String resolucion, Usuario usuarioAutoridad) {
        String pathPdf = null;
        String html = null;
    	String proponente = "";
    	String representante = "";

        try {
            ProyectoLicenciaCoaFacade dr = new ProyectoLicenciaCoaFacade();
            this.proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(codigo);
            
            ProyectoLicenciaCoa proyectoActivo = proyectoLicenciaCoaFacade.buscarProyecto(codigo);
            List<UbicacionesGeografica> ubicaciones = proyectoLicenciaCoaUbicacionFacade.ubicacionesGeograficas(proyectoActivo);
            
            ProyectoLicenciaCoaUbicacion ubicacionesMayorArea = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoActivo);
            
            parroquia= ubicaciones.get(0);
            
            canton = ubicacionGeograficaFacade.buscarPorId(parroquia
                    .getUbicacionesGeografica().getId());
            provincias = ubicacionGeograficaFacade.buscarPorId(canton
                    .getUbicacionesGeografica().getId());
            Usuario usuario = proyectoLicenciaCoa.getUsuario();
    		usuario = usuarioFacade.buscarUsuarioPorIdFull(usuario.getId());
    		Persona titular = usuario.getPersona();
            org = organizacionFacade.buscarPorPersona(titular,
            		this.proyectoLicenciaCoa.getUsuario().getNombre());
            
            UbicacionesGeografica parroquiaPeople=ubicacionGeograficaFacade.buscarPorId(proyectoLicenciaCoa.getUsuario().getPersona().getIdUbicacionGeografica());
            
            if (org == null) {
            	proponente = titular.getNombre();
            	representante = proponente;
                contacto = contactoFacade
                        .buscarPorPersona(proyectoLicenciaCoa
                                .getUsuario().getPersona());
                representanteLegal="";
                nombreRepresentanteLegal=this.proyectoLicenciaCoa.getUsuario().getPersona().getNombre().toString();
                cedula=this.proyectoLicenciaCoa.getUsuario().getPersona().getPin();
            } else {
            	proponente = org.getNombre();
            	representante = org.getPersona().getNombre();
                contacto = contactoFacade.buscarPorOrganizacion(org);
                representanteLegal=org.getPersona().getNombre();
                nombreRepresentanteLegal=org.getPersona().getNombre();
                cedula=org.getPersona().getPin();
            }
            
            valordireccion = "";
            valorcorreo = "";
            valortelefono = "";

            cantonpro = ubicacionGeograficaFacade.buscarPorId(parroquiaPeople.getId());
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
            
            String cantonP="";
            String provinciaP = "";
            String ubicacionCompleta = "";
            String ubicacionP = "";
            				
			if (ubicaciones.size() == 1) {
				UbicacionesGeografica parroquiaU = ubicaciones.get(0);
				if (parroquiaU.getUbicacionesGeografica() != null) {
					UbicacionesGeografica cantonU = parroquiaU
							.getUbicacionesGeografica();
					cantonP = cantonU.getNombre();
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU
								.getUbicacionesGeografica();
						provinciaP = provinciaU.getNombre();
					}
				}
				ubicacionCompleta = provinciaP + ", " + cantonP + ", "
						+ parroquiaU.getNombre();
				ubicacionP = " EL CANTÓN " + cantonP + ", PROVINCIA  "
						+ provinciaP;

			} else {
				ubicacionP = ": <br/><table>";
				ubicacionCompleta = "<table>";

				ubicacionP += "<tr><td>CANTÓN</td><td>PROVINCIA</td></tr>";
				ubicacionCompleta += "<tr><td>PROVINCIA</td><td>CANTÓN</td><td>PARROQUIA</td></tr>";
				for (UbicacionesGeografica ubicacionActual : ubicaciones) {
					
					ubicacionCompleta += "<tr><td>" + ubicacionActual.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td><td>"
							+ ubicacionActual.getUbicacionesGeografica().getNombre() + "</td><td>"
							+ ubicacionActual.getNombre() + "</td></tr>";
					
					
					UbicacionesGeografica cantonU = ubicacionActual.getUbicacionesGeografica();
					cantonP = cantonU.getNombre();
					if (cantonU.getUbicacionesGeografica() != null) {
						UbicacionesGeografica provinciaU = cantonU.getUbicacionesGeografica();
						provinciaP = provinciaU.getNombre();
					}

					ubicacionP += "<tr><td>" + cantonP + "</td><td>" + provinciaP
							+ "</td></tr>";

					
				}

				ubicacionP += "</table>";

				ubicacionCompleta += "</table>";
			}
			
			Area area = new Area();
			
			if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("OT")){
				area = proyectoActivo.getAreaResponsable().getArea();
			}else{
				area = proyectoActivo.getAreaResponsable();
			}
			
			String arearesponsableusuario="";
			
			String roleKey="role.certificado.autoridad";			
			
			if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("OT") ||
					proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("ZONALES")){
				arearesponsableusuario = "DIRECCIÓN ZONAL";
				roleKey="role.certificado.autoridad";
				
			}else if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("PC")){

				arearesponsableusuario = area.getArea().getAreaName();
			}else{
				arearesponsableusuario = area.getAreaName();
			}						
			
			ProyectoLicenciaCuaCiuu objProyectoActividad = proyectoLicenciaCuaCiuuFacade.actividadproyecto(proyectoActivo);
			String sectorActividad = objProyectoActividad.getCatalogoCIUU().getNombre();
						
			String sectorActividadSec1 = "";
			String sectorActividadSec2 = "";
			ProyectoLicenciaCuaCiuu objProyectoActividadSec1 = proyectoLicenciaCuaCiuuFacade.actividadproyectoSec1(proyectoActivo);
			ProyectoLicenciaCuaCiuu objProyectoActividadSec2 = proyectoLicenciaCuaCiuuFacade.actividadproyectoSec2(proyectoActivo);
			try {
				sectorActividadSec1 = objProyectoActividadSec1.getCatalogoCIUU().getNombre();
			} catch (Exception e) {
		
			}
			try {
				sectorActividadSec2 = objProyectoActividadSec2.getCatalogoCIUU().getNombre();
			} catch (Exception e) {
		
			}
			
            PlantillaReporte plantillaReporte = new PlantillaReporte();
			if (proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("DP") || 
					proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("PC") || 
					proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("ZONALES") ||
					proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("OT")) {
				plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_MAE.getIdTipoDocumento());			
				
			}else{
				plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_CERTIFICADO_AMBIENTAL_GAD.getIdTipoDocumento());				
			}           
            
            EntityCertificadoAmbiental entityInforme = new EntityCertificadoAmbiental();
            entityInforme.setUbicacionCompleta(ubicacionCompleta);
            entityInforme.setSectorActividad(sectorActividad);
            try {
            	entityInforme.setSectorActividadSec1("<br/><br/>Actividad complementaria 1 CIIU: "+sectorActividadSec1);
            	entityInforme.setCodigoActividadCiuuSec1("<br/>Actividad complementaria 1 CIIU: "+objProyectoActividadSec1.getCatalogoCIUU().getCodigo());
			} catch (Exception e) {
				entityInforme.setSectorActividadSec1("");
				entityInforme.setCodigoActividadCiuuSec1("");
			}
            try {
            	entityInforme.setSectorActividadSec2("<br/><br/>Actividad complementaria 2 CIIU: "+sectorActividadSec2);
            	entityInforme.setCodigoActividadCiuuSec2("<br/>Actividad complementaria 2 CIIU: "+objProyectoActividadSec2.getCatalogoCIUU().getCodigo());
			} catch (Exception e) {
				entityInforme.setSectorActividadSec2("");
				entityInforme.setCodigoActividadCiuuSec2("");
			}
            entityInforme.setCodigoActividadCiuu(objProyectoActividad.getCatalogoCIUU().getCodigo());
            entityInforme.setNombreproyecto(this.proyectoLicenciaCoa.getNombreProyecto().toString());
            entityInforme.setCodigoproy(this.proyectoLicenciaCoa.getCodigoUnicoAmbiental().toString());
            entityInforme.setCantonproy(canton.getNombre().toString());
            entityInforme.setProvinciaproy(provincias.getNombre().toString());
            entityInforme.setParroquiaproy(parroquia.getNombre().toString());
            entityInforme.setCedulausu(cedula);
            entityInforme.setPromotor(proponente);
            entityInforme.setRepresentanteLegal(representanteLegal);
            entityInforme.setCantonpromotor(cantonpro.getNombre().toString());
            entityInforme.setProvinciapromotor(provincias.getNombre().toString());
            entityInforme.setSector(objProyectoActividad.getCatalogoCIUU().getTipoSector().getNombre());
            
            entityInforme.setDireccionpromotor(valordireccion.toString());
            entityInforme.setTelefonopromotro(valortelefono.toString());
            entityInforme.setEmailpromotor(valorcorreo.toString());
            entityInforme.setNombreDireccionProvincial(arearesponsableusuario);
            
            SimpleDateFormat fecha = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy",new Locale("es"));
            entityInforme.setFechaactual(fecha.format(new java.util.Date()));
            nombreReporte = "InformeTecnico.pdf";
            entityInforme.setNumeroResolucion(resolucion);
            
            if (proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("DP")){
            	 entityInforme.setProvincia(ubicacionesMayorArea.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());            	
			}else if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("ZONALES")){
				//entityInforme.setProvincia(ubicacionesMayorArea.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
				// incluir informacion de la sede de la zonal en el documento
				String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyectoLicenciaCoa, null, null);
				entityInforme.setProvincia(sedeZonal);
			}else if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("OT")){
				//entityInforme.setProvincia(ubicacionesMayorArea.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
				// incluir informacion de la sede de la zonal en el documento
				String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyectoLicenciaCoa, null, null);
				entityInforme.setProvincia(sedeZonal);
			}else if(proyectoActivo.getAreaResponsable().getTipoArea().getSiglas().equals("PC")){
				 entityInforme.setProvincia("QUITO");
			}else{				
				 entityInforme.setProvincia(ubicacionesMayorArea.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			}
            entityInforme.setNombreRepresentanteLegal(nombreRepresentanteLegal);
            entityInforme.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());            
            
            //DETALLE DE RESIDUOS Y DESECHOS PELIGROSOS Y/O ESPECIALES
            String infoDesecho = geInfoRegistroGenerador(proyectoActivo);
			if(infoDesecho != null) {
				entityInforme.setDisplayDesechos("inline");
				entityInforme.setInfoDesechos("<br/><br/>"+infoDesecho);
			}else{
				entityInforme.setInfoDesechos("");
			}
						
			//VIABILIDAD AMBIENTAL
			String infoViabilidad = geInfoViabilidad(proyectoActivo);
			if(infoViabilidad != null) {
				entityInforme.setDisplayViabilidad("inline");
				entityInforme.setInfoViabilidad("<br/><br/>"+infoViabilidad);
			}else{
				entityInforme.setInfoViabilidad("");
			}
			
			// informacion DE inventario forestal
			entityInforme.setInfoInventarioForestal("<br/><br/>"+geInfoInventarioForestal(proyectoActivo));
  
						
			// informacion DE sustancias quimicas
            if(proyectoActivo.getSustanciasQuimicas()){
            	entityInforme.setInfoSustanciasQuimicas("<br/><br/>"+geInfoSustanciasQuimicas(proyectoActivo));
            }else{
				entityInforme.setInfoSustanciasQuimicas("");
            }
            
            List<String> lista = getCodigoQrUrl(); 
            entityInforme.setCodigoQrFirma(lista.get(1));
			
            html = UtilGenerarInforme.generarFicheroHtml(
                    plantillaReporte.getHtmlPlantilla(), nombreReporte,
                    Boolean.valueOf(true), entityInforme);

            setInformePdf(informePdf);
            valordireccion="";
            valortelefono="";
            valorcorreo="";	
            
                        
            guardar(proyectoActivo,resolucion, lista.get(0), usuarioAutoridad);
            
        } catch (Exception e) {
            this.LOG.error("Error al visualizar el informe técnico", e);
            JsfUtil.addMessageError("Error al visualizar el informe técnico");
        }
        return html;
    }
	
    public String guardar (ProyectoLicenciaCoa proyectoactivo,String secuencial, String url, Usuario usuarioAutoridad) {
    	ProyectoCertificadoAmbiental proyectoCertificadoAmbiental =proyectoCertificadoAmbientalFacade.getProyectoCertificadoAmbientalPorCodigoProyecto(proyectoactivo);
		proyectoCertificadoAmbiental.setProyectoLicenciaCoa(proyectoactivo);
		proyectoCertificadoAmbiental.setCodigoCertificado(secuencial);
			proyectoCertificadoAmbiental.setNombreCertificado("Certificado Ambiental");
			proyectoCertificadoAmbiental.setDescripcion("Resolucion Certificado Ambiental");
			proyectoCertificadoAmbiental.setUrl(url);
			proyectoCertificadoAmbiental.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
			proyectoCertificadoAmbiental.setIdArea(proyectoactivo.getAreaResponsable().getId());
			proyectoCertificadoAmbiental.setIdUsuario(usuarioAutoridad.getId());
			proyectoCertificadoAmbientalFacade.guardarCertificadoAmbiental(proyectoCertificadoAmbiental);
		return "ok";
	}

    public String geInfoRegistroGenerador(ProyectoLicenciaCoa proyecto) {
    	RegistroGeneradorDesechosRcoa registroGeneradorDesechos = registroGeneradorDesechosRcoaFacade.buscarRGDPorProyectoRcoa(proyecto.getId());
		if(registroGeneradorDesechos != null) {
			

			List<DesechosRegistroGeneradorRcoa> listaDesechos = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGeneradorGenerados(registroGeneradorDesechos);		
			
			String desechos = "<table style=\"width: 100%; font-size: 11px !important;\" border=\"1\" cellpadding=\"3\" cellspacing=\"0\"  class=\"w600Table\" >"
					+ "<tbody><tr>"
					+ "<td><strong>CÓDIGO DEL RESIDUO O DESECHO PELIGROSO Y/O ESPECIAL</strong></td>"
					+ "<td><strong>NOMBRE DEL RESIDUO O DESECHO PELIGROSO Y/O ESPECIAL</strong></td>"
					+ "<td><strong>CRTIB</strong></td>"
					+ "<td><strong>ORIGEN DE LA GENERACIÓN</strong></td>"
					+ "</tr>";
			for (DesechosRegistroGeneradorRcoa desecho : listaDesechos) {
				List<PuntoGeneracionDesecho> listaGeneracion = puntoGeneracionDesechoFacade.buscarPorDesechoRcoa(desecho.getId());
				
				String origen = "";
				for(PuntoGeneracionDesecho punto : listaGeneracion){
					origen += punto.getPuntoGeneracionRgdRcoa().getNombre() + "<br />";
				}
				
				String critb = desecho.getDesechoPeligroso().getCritb() == null ? " " : desecho.getDesechoPeligroso().getCritb();
				
				desechos += "<tr>";
				desechos += "<td width=\"20%\">" + desecho.getDesechoPeligroso().getClave() + "</td>";
				desechos += "<td>" + desecho.getDesechoPeligroso().getDescripcion() + "</td>";
				desechos += "<td width=\"10%\">" +  critb + "</td>";
				desechos += "<td width=\"25%\">" + origen + "</td>";
				desechos += "</tr>";
			}
			desechos += "</tbody></table>";
			
			return desechos;
		}
		
		return null;
    }
    
    public String geInfoViabilidad(ProyectoLicenciaCoa proyecto) {
    	String viabilidad = viabilidadCoaFacade.getDetalleInfoViabilidad(proyecto);
    	return viabilidad;
    } 
	private String geInfoSustanciasQuimicas(ProyectoLicenciaCoa proyecto){
		try {
			 StringBuilder tablaStandar = new StringBuilder();
			 String desechos="";
				RegistroSustanciaQuimica registroSustanciaQuimica =registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyecto);
				List<UbicacionSustancia> ubicacionSustanciaProyectoLista = new ArrayList<UbicacionSustancia>();
				if(registroSustanciaQuimica!=null) {		
					ubicacionSustanciaProyectoLista=ubicacionSustanciaQuimicaFacade.obtenerUbicacionesPorRSQ(registroSustanciaQuimica);				
				}		
			 if(ubicacionSustanciaProyectoLista != null && ubicacionSustanciaProyectoLista.size() > 0){
				desechos =  "<div style=\"text-align: left;\"><p style=\"color: #FFF;\">..<p/>";
				desechos += "<span style=\"font-weight: bold;\">LISTADO DE SUSTANCIAS QUIMÍCAS PELIGROSAS<br/></span>";
				desechos += "<table style=\"width: 100%; font-size: 11px !important;\" border=\"1\" cellpadding=\"3\" cellspacing=\"0\"  class=\"w100Table\" >"
						+ "<tbody><tr>"
						+ "<td><strong>NOMBRE DE LA SUSTANCIA</strong></td>"
						+ "<td><strong>NÚMERO CAS O ONU</strong></td>"
//						+ "<td><strong>ACTIVIDADES DE CADA SUSTANCIA</strong></td>"
//						+ "<td><strong>NÚMERO DE REGISTRO</strong></td>"
						+ "</tr>";
				 for (UbicacionSustancia ubicacionSustancia : ubicacionSustanciaProyectoLista) {
					 
						desechos += "<tr>";
						desechos += "<td>" + ubicacionSustancia.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion() + "</td>";
						desechos += "<td>" + ubicacionSustancia.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getCodigoOnu()+"</td>";
						desechos += "</tr>";
				}

					desechos += "</table>";
			 }
				tablaStandar.append(desechos);
			return tablaStandar.toString();
		 } catch (Exception e) {
			 return "";
		 }
	}

	private String geInfoInventarioForestal(ProyectoLicenciaCoa proyecto){
		try {
			 StringBuilder tablaStandar = new StringBuilder();
			 InventarioForestalAmbiental inventario = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(proyecto.getId());
			 if(inventario != null && inventario.getId() != null){
				ReporteInventarioForestal reporte = reporteInventarioForestalFacade.getByIdCertificadoByIdTipoDocumento(inventario.getId(), 2);
				if(reporte != null && reporte.getRecomendaciones() != null) {
					tablaStandar.append("<div style=\"text-align: left;\">");
					tablaStandar.append("<span style=\"font-weight: bold;font-size: small;\">INVENTARIO FORESTAL<br/>");
					tablaStandar.append("</span>");
					tablaStandar.append("</div>");
					
					tablaStandar.append("<span style=\"font-size:11px\">");
					tablaStandar.append("<p style=\" text-align: justify\">"+(reporte.getRecomendaciones() == null?"":reporte.getRecomendaciones())+"</p>");
					tablaStandar.append("</span>");
				}
			 }
			return tablaStandar.toString();
		 } catch (Exception e) {
			 return "";
		 }
	}
	
	public List<String> getCodigoQrUrl() {
		
		List<String> resultado = GenerarQRCertificadoAmbiental.getCodigoQrUrl(true, 
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(), GenerarQRCertificadoAmbiental.tipo_suia_rcoa);
		return resultado;
	}
	
}
