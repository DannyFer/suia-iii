package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.certificado.interseccion.CertificadoInterseccionRcoaOficioHtml;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.dto.EntityOficioPronunciamiento;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.PronunciamientoArchivacionProyectoFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.SubActividadesFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.PronunciamientoArchivacionProyecto;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.registroAmbiental.controller.GenerarDocumentosResolucionAmbientalCoaController;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityResolucionAmbientalRCOA;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class GeneracionDocumentosController implements Serializable{

	  private static final long serialVersionUID = -875087443147320594L;
	
	@EJB
	private PronunciamientoArchivacionProyectoFacade pronunciamientoProhibicionActividadFacade;
	@EJB
	private DocumentosCoaFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;	
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
	@EJB
	private CapasCoaFacade capasCoaFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private CategoriaIIFacade categoriaIIFacade;
	@EJB
    private ContactoFacade contactoFacade;
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentosRegistroFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private SubActividadesFacade subActividadesFacade;
	
	@Getter
	@Setter
	private SubActividades Subactividad=new SubActividades();
	
	
	public DocumentosCOA generarOficioArchivacion(ProyectoLicenciaCoa proyecto, Boolean esZonificacionBiodiversidad) {
		try{
			
			String docuTableClass = "PronunciamientoProhibicionActividad";
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.RCOA_PRONUNCIAMIENTO_PROHIBICION_ACTIVIDAD;
			
			List<DocumentosCOA> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), tipoDocumento, docuTableClass);
			if (listaDocumentosInt.size() > 0) {
				DocumentosCOA documento = listaDocumentosInt.get(0);
				return documento;
			}
			
			PronunciamientoArchivacionProyecto oficio = pronunciamientoProhibicionActividadFacade.obtenerPorProyecto(proyecto.getId());
			if(oficio == null) {
				oficio = new PronunciamientoArchivacionProyecto();
				oficio.setIdProyecto(proyecto.getId());
				oficio = pronunciamientoProhibicionActividadFacade.guardar(oficio);
			}
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(tipoDocumento);
			
			String nombreOperador = usuarioFacade.recuperarNombreOperador(proyecto.getUsuario()).get(0);

			String actividades = "";
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
			CatalogoCIUU ciiuPrincipal = null, ciiuComplementaria1 = null, ciiuComplementaria2 = null;

			for (ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu) {
				if (actividad.getOrderJerarquia() == 1) {
					ciiuPrincipal = actividad.getCatalogoCIUU();
				} else if (actividad.getOrderJerarquia() == 2) {
					ciiuComplementaria1 = actividad.getCatalogoCIUU();
				} else if (actividad.getOrderJerarquia() == 3) {
					ciiuComplementaria2 = actividad.getCatalogoCIUU();
				}
			}

			actividades = ciiuPrincipal.getCodigo() + " - " + ciiuPrincipal.getNombre();
			if(ciiuComplementaria1 != null && ciiuComplementaria1.getId() != null)
				actividades += ", " + ciiuComplementaria1.getCodigo() + " - " + ciiuComplementaria1.getNombre();
			if(ciiuComplementaria2 != null && ciiuComplementaria2.getId() != null)
				actividades += ", " + ciiuComplementaria2.getCodigo() + " - " + ciiuComplementaria2.getNombre();
			
			DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
			
			Area areaTramite = proyecto.getAreaResponsable();
			if (areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT)) {
				areaTramite = areaTramite.getArea();
			}
			
			String cargoTemporal = usuarioFacade.getTipoUsuarioAutoridad(JsfUtil.getLoggedUser());
			
			EntityOficioPronunciamiento entityOficio = new EntityOficioPronunciamiento();
			entityOficio.setNumeroOficio(oficio.getCodigo());
			entityOficio.setUbicacion(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			entityOficio.setFechaEmision(formatoFecha.format(new Date()));
			entityOficio.setNombreOperador(nombreOperador);
			entityOficio.setNombreProyecto(proyecto.getNombreProyecto());
			entityOficio.setActividadesCiiu(actividades);
			entityOficio.setNombreAutoridad(JsfUtil.getLoggedUser().getPersona().getNombre());
			entityOficio.setAreaResponsable(areaTramite.getAreaName() + cargoTemporal);
			entityOficio.setDisplayExtractiva("none");
			entityOficio.setDisplayZonificacion("none");
			if(esZonificacionBiodiversidad)
				entityOficio.setDisplayZonificacion("inline");
			else
				entityOficio.setDisplayExtractiva("inline");			
			
			String nombreArchivo = "PronunciamientoArchivoProyecto_" + proyecto.getCodigoUnicoAmbiental() + ".pdf";
			
			File informePdf = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					nombreArchivo, true, entityOficio);
					
	
			Path path = Paths.get(informePdf.getAbsolutePath());
			String reporteHtmlfinal = nombreArchivo;
			byte[] archivoInforme = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();
			
			TipoDocumento tipoDoc = new TipoDocumento();
			tipoDoc.setId(tipoDocumento.getIdTipoDocumento());
			
			DocumentosCOA documento = new DocumentosCOA();
			documento.setNombreDocumento(nombreArchivo);
			documento.setExtencionDocumento(".pdf");		
			documento.setTipo("application/pdf");
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(docuTableClass);
			documento.setTipoDocumento(tipoDoc);
			documento.setIdTabla(proyecto.getId());
			documento.setProyectoLicenciaCoa(proyecto);
			
			documento = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 0L, documento, tipoDocumento);
			
			return documento;
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al generar el documento.");
		}
		
		return null;
	}
	
	public DocumentosCOA generarCertificadoInterseccion(ProyectoLicenciaCoa proyecto, Usuario usuarioAutoridad)
	{			
		FileOutputStream fileOutputStream;
		try {
			String nombreReporte= "Certificado_Intersección";
			
			CertificadoInterseccionOficioCoa oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
			
			List<DocumentosCOA> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDoc(oficioCI.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO,"CertificadoInterseccionOficioCoa");
			if (listaDocumentosInt.size() > 0) {
				DocumentosCOA documento = listaDocumentosInt.get(0);
				documento.setProyectoLicenciaCoa(proyecto);
				return documento;
			}
			
			Area areaInternaTramite = proyecto.getAreaInventarioForestal();
			
			String tipoRol = "role.esia.cz.autoridad";
			if(areaInternaTramite.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
				tipoRol = "role.esia.cz.autoridad";
				areaInternaTramite = areaInternaTramite.getArea();
			}else if(areaInternaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
				tipoRol = "role.esia.pc.autoridad";
			}else if(areaInternaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)){
				tipoRol = "role.esia.ga.autoridad";
			}
			
			if(usuarioAutoridad == null) {				
				String rolAutoridad = Constantes.getRoleAreaName(tipoRol);
				usuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaInternaTramite).get(0);
				
				Area areaAutoridad = new Area();
	        	if(usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1){
	        		areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
	        	}else{
	        		areaAutoridad = areaInternaTramite;
	        	}
				
				oficioCI.setFechaOficio(new Date());
	        	oficioCI.setAreaUsuarioFirma(areaAutoridad.getId());
	        	oficioCI.setUsuarioFirma(usuarioAutoridad.getNombre());
	        	oficioCI =  certificadoInterseccionCoaFacade.guardar(oficioCI);
			}
			
			Usuario uOperador = proyecto.getUsuario();
			Organizacion orga = organizacionFacade.buscarPorRuc(proyecto.getUsuarioCreacion());
			//busco la actividad  ciiu principal
			ProyectoLicenciaCuaCiuu actividadPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
			String codigoCiiu = actividadPrincipal.getCatalogoCIUU() != null ? actividadPrincipal.getCatalogoCIUU().getCodigo(): "";
			// obtengo la informacion geografica externa CONALI
			List<CatalogoGeneralCoa> listaCapas =catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.COA_CAPAS_EXTERNAS_CONALI);
			String strTableCapaCONALI = "";
			for (CatalogoGeneralCoa catalogoCapas : listaCapas) {		
				strTableCapaCONALI += catalogoCapas.getDescripcion() + "<br/>";
			}
			String nombreOperador = uOperador.getPersona().getNombre();
			String cedulaOperador = uOperador.getPersona().getPin();
			String razonSocial = orga == null ? " " : orga.getNombre();
			
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO);
			File file = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new CertificadoInterseccionRcoaOficioHtml(oficioCI,nombreOperador,cedulaOperador,razonSocial,codigoCiiu,strTableCapaCONALI,usuarioAutoridad, true),null)," ",BaseColor.GRAY);
	        Path path = Paths.get(file.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(file.getName()));
	        fileOutputStream = new FileOutputStream(fileArchivo);
	        fileOutputStream.write(byteArchivo);
	        fileOutputStream.close();
	        
	        Path pathQr = Paths.get(proyecto.getCodigoUnicoAmbiental().replace("/", "-") + "-qr-firma.png");
			Files.delete(pathQr);
	        
	        DocumentosCOA documento = new DocumentosCOA();	        	
        	documento.setContenidoDocumento(Files.readAllBytes(path));
        	documento.setExtencionDocumento(".pdf");		
        	documento.setTipo("application/pdf");
        	documento.setIdTabla(oficioCI.getId());	       		
        	documento.setNombreTabla(CertificadoInterseccionOficioCoa.class.getSimpleName());
        	documento.setNombreDocumento(nombreReporte+"_"+oficioCI.getCodigo()+".pdf");
        	
        	
        	documento = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "Certificado_Interseccion", 1L, documento, TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO);
        	
//        	firmaAutomaticaDocumento(documento.getIdAlfresco());
        	
        	documento.setProyectoLicenciaCoa(proyecto);
        	documentosFacade.guardar(documento);
        	
        	oficioCI.setFechaOficio(new Date());
        	
        	Area areaAutoridad = new Area();
        	if(usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1){
        		areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
        	}else{
        		areaAutoridad = areaInternaTramite;
        	}
        	
        	oficioCI.setAreaUsuarioFirma(areaAutoridad.getId());
        	oficioCI.setUsuarioFirma(usuarioAutoridad.getNombre());
        	certificadoInterseccionCoaFacade.guardar(oficioCI);
        	
	        return documento;
	       
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al generar el documento.");
			System.out.println("Error al generar el documento del certificado de intersección.");
			e.printStackTrace();
		}
		return null;
	}

	public DocumentoRegistroAmbiental generarResolucionRegistroAmbiental(ProyectoLicenciaCoa proyecto) throws Exception{
		String[] parametros = new String[40]; 
		parametros = categoriaIIFacade.cargarDatosLicenciaAmbientalCompletoRcoa(
				proyecto, JsfUtil
						.getLoggedUser()
						.getNombre());
		
		String tipoEnteResponsable = null;
		Area areaResponsable = proyecto.getAreaResponsable();
		
		String nombreReporteRegistro = "Resolucion_AmbientalRcoa.pdf";
		
		switch (proyecto.getAreaResponsable().getTipoArea().getSiglas().toUpperCase()) {
		case "DP":
			tipoEnteResponsable = "RCOA RESOLUCION AMBIENTAL DP";
			areaResponsable = proyecto.getAreaResponsable();
			break;
		case "ZONALES":
			tipoEnteResponsable = "RCOA RESOLUCION AMBIENTAL ZONAL";
			areaResponsable = proyecto.getAreaResponsable();
			break;
		case "OT":
			areaResponsable = proyecto.getAreaResponsable().getArea();
			tipoEnteResponsable = "RCOA RESOLUCION AMBIENTAL ZONAL"; 
			
			Subactividad=subActividadesFacade.getSubActividadesPorProyecto(proyecto,Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION);
			if(Subactividad.getId()!=null)
				tipoEnteResponsable="Resolución Subestaciones Energia Eléctrica PMA";
			else if (Subactividad.getId()==null) {
				Subactividad=subActividadesFacade.getSubActividadesPorProyecto(proyecto,Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION_MAYOR);
				if(Subactividad.getId()!=null)
					tipoEnteResponsable=Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION_MAYOR;
			}
			//verifico si se trata de un proyecto de escombreras Menor o igual a 20000 m3 o superficie menor o igual a 2 ha
			if (Subactividad.getId()==null) {
				Subactividad=subActividadesFacade.getSubActividadesPorProyecto(proyecto,Constantes.ACTIVIDAD_ESCOMBRERAS_RA);
				if(Subactividad.getId()!=null)
					tipoEnteResponsable=Constantes.ACTIVIDAD_ESCOMBRERAS_RA;
			}
			
			if(Subactividad.getId() == null){
				Subactividad=subActividadesFacade.getSubActividadesPorProyectoMineria(proyecto, Constantes.ACTIVIDAD_TIPO_MINERIA_CONTRATO);
				if(Subactividad.getId()!=null)
					tipoEnteResponsable=Constantes.ACTIVIDAD_MINERIA_CONTRATO;
			}
			break;
		case "PC":
			tipoEnteResponsable = "RCOA RESOLUCION AMBIENTAL SCA";
			nombreReporteRegistro= "Resolucion_GADs_Municipales_gestión_ambiental_"+proyecto.getCodigoUnicoAmbiental()+".pdf";
			break;
		case "EA":
			if(areaResponsable.getAreaName().contains("PROVINCIA")){
				tipoEnteResponsable = "RCOA RESOLUCION AMBIENTAL GADP";
			}else{
				if(areaResponsable.getAreaName().contains("QUITO")
						|| areaResponsable.getAreaName().contains("GUAYAQUIL")
						|| areaResponsable.getAreaName().contains("CUENCA")){
					tipoEnteResponsable = "RCOA RESOLUCION AMBIENTAL GADM-PRINCIPAL";
					nombreReporteRegistro= "Resolucion_GADs_Municipales_gestión_ambiental_"+proyecto.getCodigoUnicoAmbiental()+".pdf";
				}else{
					tipoEnteResponsable = "RCOA RESOLUCION AMBIENTAL GADM";
				}
			}
			break;
		default:
			break;
		}
		
		PlantillaReporte plantillaReporte = plantillaReporteFacade.obtenerPlantillaReportePorCodigo(TipoDocumentoSistema.REGISTRO_AMBIENTAL_RCOA.getIdTipoDocumento(), tipoEnteResponsable);
		
		EntityResolucionAmbientalRCOA entityInforme = new EntityResolucionAmbientalRCOA();
		SimpleDateFormat fecha= new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es"));
		// nombre del arae
		switch (areaResponsable.getTipoArea().getSiglas().toUpperCase()) {
		case "DP":
			entityInforme.setNombreArea(areaResponsable.getAreaName());
			entityInforme.setAutoridadAmbiental("AUTORIDAD AMBIENTAL");
			break;
		case "ZONALES":
			entityInforme.setZonalNombre(areaResponsable.getAreaName());
			entityInforme.setZonal("DIRECCIÓN ZONAL");
			entityInforme.setNombreArea(areaResponsable.getAreaName());
			entityInforme.setAutoridadAmbiental("DIRECTOR ZONAL");
			break;
		case "EA":
			entityInforme.setNombreArea(areaResponsable.getAreaName());
			entityInforme.setAutoridadAmbiental("AUTORIDAD AMBIENTAL");
			break;
		case "PC":
			entityInforme.setNombreArea("SUBSECRETARIA DE CALIDAD AMBIENTAL");
			entityInforme.setAutoridadAmbiental("SUBSECRETARIO DE CALIDAD AMBIENTAL");
			
			break;
		default:
			break;
		}
		String ActividadesSecundarias[] = {"",""};
		String CodigosActividadesSecundarias[] = {"",""};
		// nombre de la actividad PRINCIPAL y Secundarias
		int i = 0;
		List<ProyectoLicenciaCuaCiuu>  listaactividades = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
		for (ProyectoLicenciaCuaCiuu objActividad : listaactividades) {
			
			if(objActividad.getPrimario()){
				
				entityInforme.setActividad(objActividad.getCatalogoCIUU().getNombre());
				entityInforme.setCodigoCiiu(objActividad.getCatalogoCIUU().getCodigo());
				entityInforme.setSector(objActividad.getCatalogoCIUU().getTipoSector().getNombre());
			}
			
			if (!objActividad.getPrimario()) {
				
				ActividadesSecundarias[i] = objActividad.getCatalogoCIUU().getNombre();
				CodigosActividadesSecundarias[i] = objActividad.getCatalogoCIUU().getCodigo();
				i=i+1;
			}
			
		}
		
		if (ActividadesSecundarias[0]!="") {
//			entityInforme.setActividadSec1("<p style=\"margin-top: -10px\"><br/>Actividad complementaria 1 CIIU: "+ActividadesSecundarias[0]+"</p>");
			entityInforme.setActividadSec1( "<br/><br/>Actividad complementaria 1 CIIU: "+ActividadesSecundarias[0]);
			entityInforme.setCodigoCiiuSec1("<br/>Actividad complementaria 1 CIIU: "+CodigosActividadesSecundarias[0]);
		}
		
		if (ActividadesSecundarias[1] !="") {
			entityInforme.setActividadSec2( "<br/><br/>Actividad complementaria 2 CIIU: "+ActividadesSecundarias[1]);
			entityInforme.setCodigoCiiuSec2("<br/>Actividad complementaria 2 CIIU: "+CodigosActividadesSecundarias[1]);
		}
		
		/// para la provincia
		String ubicacionCompleta = "";
		 List<UbicacionesGeografica> ubicacion = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyecto);
		 if(ubicacion != null && ubicacion.size() == 1){
			 entityInforme.setUbicacionCompleta(ubicacion.get(0).getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + 
					 ", "+ ubicacion.get(0).getUbicacionesGeografica().getNombre() + ", "
						+ ubicacion.get(0).getNombre() );
		 }else if(ubicacion != null && ubicacion.size() > 0){
				ubicacionCompleta = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
					+ "<tbody><tr BGCOLOR=\"#B2B2B2\">"	;
				ubicacionCompleta += "<td>PROVINCIA</td><td>CANTÓN</td><td>PARROQUIA</td></tr>";
				for (UbicacionesGeografica objUbicacion : ubicacion) {
					ubicacionCompleta += "<tr><td>" + objUbicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td><td>"
						+ objUbicacion.getUbicacionesGeografica().getNombre() + "</td><td>"
						+ objUbicacion.getNombre() + "</td></tr>";
				}
				ubicacionCompleta += "</tbody></table></center><br/>";
				entityInforme.setUbicacionCompleta(ubicacionCompleta);
		 }
		 String nombreCanton="";
		 
		 
		 if(areaResponsable.getTipoArea().getSiglas().toUpperCase().equals("ZONALES")){
			Map<String, String> mapProvincia = new HashMap<>();
			Map<String, String> mapCantones = new HashMap<>();
			Map<String, String> mapParroquias = new HashMap<>();
			
			String provincias = "";
			String cantones = "";
			String parroquias = "";

			for (UbicacionesGeografica objUbicacion : ubicacion) {

				mapProvincia.put(objUbicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre(), objUbicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
				mapCantones.put(objUbicacion.getUbicacionesGeografica().getNombre(), objUbicacion.getUbicacionesGeografica().getNombre());
				mapParroquias.put(objUbicacion.getNombre(),	objUbicacion.getNombre());				
			}
			
			for(Entry<String, String> p : mapProvincia.entrySet()){
				provincias += p.getValue() + " ";
			}			
						
			for(Entry<String, String> p : mapCantones.entrySet()){
				cantones += p.getValue() + " ";
			}
						
			for(Entry<String, String> p : mapParroquias.entrySet()){
				parroquias += p.getValue() + " ";
			}
			entityInforme.setProvincia(provincias);
			entityInforme.setCanton(cantones);
			entityInforme.setParroquia(parroquias);			
			
			for (UbicacionesGeografica objUbicacion : ubicacion) {
				nombreCanton = objUbicacion.getUbicacionesGeografica().getNombre();
				break;
			}
			
		 }else{
			for (UbicacionesGeografica objUbicacion : ubicacion) {

				entityInforme.setProvincia(objUbicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
				entityInforme.setCanton(objUbicacion.getUbicacionesGeografica().getNombre());
				entityInforme.setParroquia(objUbicacion.getNombre());
				nombreCanton = entityInforme.getCanton();
				entityInforme.setParroquia(entityInforme.getParroquia().replace("PROVINCIAL.", "PROVINCIAL"));
				break;
			}
		 }
		
		switch (areaResponsable.getTipoArea().getSiglas().toUpperCase()) {
		case "DP":
		case "EA":
			break;
		case "ZONALES":
		case "OT":
			/*UbicacionesGeografica ubicacionPrincipal=proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto).getUbicacionesGeografica();
			if (ubicacionPrincipal != null && ubicacionPrincipal.getUbicacionesGeografica() != null){
				nombreCanton = ubicacionPrincipal.getUbicacionesGeografica().getNombre();
			}*/
			// incluir informacion de la sede de la zonal en el documento
			ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
			nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(JsfUtil.getLoggedUser(), "PROYECTORCOA", proyecto, null, null);
			break;
		case "PC":
			nombreCanton= "QUITO";
			break;
		default:
			break;
		}
		// representante legal
		String usuariocreacion = proyecto.getUsuario().getNombre();
		String razonSocial = "", representanteLegal = "", cedulaRepresentanteLegal="";
		if(usuariocreacion.length() == 13){
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuariocreacion);				
			if(organizacion != null && organizacion.getNombre() != null){
				razonSocial = organizacion.getNombre();
				representanteLegal = organizacion.getPersona().getNombre();
				cedulaRepresentanteLegal = organizacion.getPersona().getPin();
			}else{
				razonSocial = proyecto.getUsuario().getPersona().getNombre();
				representanteLegal = proyecto.getUsuario().getPersona().getNombre();
				cedulaRepresentanteLegal =  proyecto.getUsuario().getNombre();
				}
		}else{
			Persona persona = proyecto.getUsuario().getPersona();
			if(persona != null && persona.getNombre() != null){
				razonSocial = persona.getNombre();
				representanteLegal = persona.getNombre();
				cedulaRepresentanteLegal =  persona.getPin();
			}
		}
		entityInforme.setNombreRepresentante(representanteLegal);
		entityInforme.setNumeroDocumentoRepresentante(cedulaRepresentanteLegal);
        entityInforme.setNombreOperador(razonSocial);
        
        List<Contacto> listaContactos = null;
        String correo ="", direccion ="", telefono="";
        listaContactos = contactoFacade.buscarUsuarioNativeQuery(proyecto.getUsuario().getNombre());
        
        for (Contacto c : listaContactos) {
            if (correo.isEmpty() && c.getFormasContacto().getId().intValue() == FormasContacto.EMAIL) {
                correo = c.getValor();
            } else if (direccion.isEmpty() && c.getFormasContacto().getId().intValue() == FormasContacto.DIRECCION) {
                direccion = c.getValor();
            } else if (telefono.isEmpty() && c.getFormasContacto().getId().intValue() == FormasContacto.TELEFONO) {
                telefono = c.getValor();
            }
        }
        entityInforme.setNumeroDocumento(proyecto.getUsuario().getNombre());
        entityInforme.setEmail(correo);
        entityInforme.setDireccion(direccion);
        entityInforme.setTelefono(telefono);
            
		// nombre proyecto
		entityInforme.setNombreProyecto(proyecto.getNombreProyecto());
		entityInforme.setCodigoProyecto(proyecto.getCodigoUnicoAmbiental());
		// autoridad ambiental
		entityInforme.setNombreAutoridadAmbiental(JsfUtil.getLoggedUser().getPersona().getNombre());
		entityInforme.setNumeroOficio(parametros[0]);
		entityInforme.setNumeroResolucion(parametros[8]);
		entityInforme.setFecha(parametros[16]);
		entityInforme.setResolucionMinisterial(areaResponsable.getResolucionMinisterial() == null ? "xxxx": areaResponsable.getResolucionMinisterial());
		entityInforme.setResolucionAcreditacion(areaResponsable.getResolucionMinisterial() == null ? "" : areaResponsable.getResolucionMinisterial());
		entityInforme.setFechaCompleta(nombreCanton+", el "+fecha.format(new java.util.Date()));
		entityInforme.setVerAnexoCoordenadas(detalleCoordenadasnProyecto(proyecto));
		
		byte[] imagenByte = null; 
        //byte[] imagenByte = certificadoInterseccionFacade.getFirmaAutoridadPrevencion();
        File resolucionTmpPdf = GenerarDocumentosResolucionAmbientalCoaController.generarResolucionRCoa1(plantillaReporte.getHtmlPlantilla(), nombreReporteRegistro, areaResponsable, Boolean.valueOf(true), entityInforme, "<span style='color:red'>INGRESAR</span>", imagenByte, false, false);
		//genero el archivo en reporteshtml para poder visualizar en vista previa
		Path path = Paths.get(resolucionTmpPdf.getAbsolutePath());
		byte[] archivoInforme = Files.readAllBytes(path);
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(nombreReporteRegistro));
		// para guardar el documento al alfresco
		DocumentoRegistroAmbiental documento= new DocumentoRegistroAmbiental();
		documento.setContenidoDocumento(archivoInforme);
		if(documento.getContenidoDocumento()!=null){
			documentosRegistroFacade.ingresarDocumento(documento, proyecto.getId(), proyecto.getCodigoUnicoAmbiental(), TipoDocumentoSistema.RESOLUCION_AMBIENTAL_RCOA, archivoFinal.getName(), ProyectoLicenciaCoa.class.getSimpleName(), 0L);
			documento = documentosRegistroFacade.documentoXTablaIdXIdDoc(proyecto.getId(), ProyectoLicenciaCoa.class.getSimpleName(), TipoDocumentoSistema.RESOLUCION_AMBIENTAL_RCOA);
        }
		
		return documento;
		
	}
	
	private String detalleCoordenadasnProyecto(ProyectoLicenciaCoa proyecto){
		try {
			 StringBuilder tablaStandar = new StringBuilder();
			 List<CoordenadasProyecto> coordenadas = coordenadasProyectoCoaFacade.buscarPorFormaPorProyecto(proyecto);
			 if(coordenadas.size() > 0){
				 tablaStandar.append("<div><br/><br/><br/>");
				 tablaStandar.append("<table style=\"width: 100%;\">");
				 tablaStandar.append("</tbody>");
				 tablaStandar.append("</tr>");
				 tablaStandar.append("<td style=\"text-align: center;padding-bottom: 10px;vertical-align: top\">");
				 tablaStandar.append("<span style=\"font-size:12px;\"><span style=\"font-family:arial,helvetica,sans-serif;\">Anexo</span></span></td>");
				 tablaStandar.append("</tr>");
				 tablaStandar.append("</tbody>");
				 tablaStandar.append("</table>");
				 tablaStandar.append("</div>");

				 String cabeza="";
				 //String[] columnas = {"Este (X)", "Norte (Y)", "Altitud"};
				 String[] columnas = {"Coordenadas X", "Coordenadas Y", "Forma"};
				 String titulo="Coordenadas geográficas";
				 tablaStandar.append(cabeceraTabla(cabeza, titulo, columnas));
				for (CoordenadasProyecto objCoordenadas : coordenadas) {
					tablaStandar.append("<tr>");
					tablaStandar.append("<td style=\" text-align: left\">"+objCoordenadas.getX()+"</td>");
					tablaStandar.append("<td style=\"width: 30%; text-align: left\">"+objCoordenadas.getY()+"</td>");
					tablaStandar.append("<td style=\"width: 40%; text-align: left\">"+objCoordenadas.getProyectoLicenciaAmbientalCoaShape().getTipoForma().getNombre()+"</td>");
					tablaStandar.append("</tr>");
				}
				tablaStandar.append("</tbody></table>");
			 }
			 return "";
			//return tablaStandar.toString();
		 } catch (Exception e) {
			 return "";
		 }
	}
	
	private String cabeceraTabla(String cabeza, String titulo, String[] columnas){
		 StringBuilder tablaStandar = new StringBuilder();
		 if (cabeza != null && !cabeza.isEmpty()) {
			tablaStandar.append("<table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%\">");
			tablaStandar.append("<tbody><tr>");
			tablaStandar.append("<td style=\"width: 100%; text-align: left\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
			tablaStandar.append(cabeza);
			tablaStandar.append("<br/></div></td>");
			tablaStandar.append("</tr>");
			tablaStandar.append("</tbody></table>");
		}
		 if(columnas != null && columnas.length > 0){
			tablaStandar.append("<table align=\"left\" border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:100%; font-size: small; \">");
			tablaStandar.append("<tbody>");
			if(!titulo.isEmpty())
				tablaStandar.append("<tr><td style=\"font-weight: bold; text-align: center\" colspan=\""+columnas.length+"\">"+titulo+"</td></tr> ");
			tablaStandar.append("<tr>");
			for (String s : columnas) {
				tablaStandar.append("<td style=\"font-weight: bold; text-align: center\"> <div style=\"font-size: small\">");
				tablaStandar.append(s);
				tablaStandar.append("</div></td>");
			}
			tablaStandar.append("</tr>");
		 }
   	return tablaStandar.toString();
   }
}
