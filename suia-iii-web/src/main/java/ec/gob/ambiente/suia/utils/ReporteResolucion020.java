package ec.gob.ambiente.suia.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean.InformeProvincialGADBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.ConcesionesBean;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalConcesionesMinerasFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalConcesionesMineras;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.DetalleInterseccionProyecto;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityResolucion020;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;


@Stateless
public class ReporteResolucion020 {
	private final Logger LOG = Logger.getLogger(InformeProvincialGADBean.class);

	String nombreProyecto;
	String nombreArea;
	String codigoArea;
	String nombreRepresentanteLegal;
	String email="";
	String direccion="";
	String telefono="";
	String codigoProyecto="";
	
	String nombreConcesion="";
	String provincias="";
	String intersecaTexto="";
	
	String CodPro="";
	String Numresolucion;
	List<String> direccionRepresentanteLegal = new ArrayList<String>();
	List<String> telefonoRepresentanteLegal= new ArrayList<String>();
	List<String> emailRepresentanteLegal= new ArrayList<String>();
	UbicacionesGeografica canton= new UbicacionesGeografica();
	UbicacionesGeografica provincia= new UbicacionesGeografica();
	String sector;

	Organizacion proponente= new Organizacion();
	Contacto contacto= new Contacto();
	@EJB
	ContactoFacade contactoFacade;
	List<Contacto> listContacto = new ArrayList<Contacto>();

	@EJB
	private CategoriaIIFacade categoriaIIFacade;
	
	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionService;

	@Getter
	@Setter
	private UbicacionesGeografica parroquia;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;


	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;

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

	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private ProyectoLicenciaAmbientalConcesionesMinerasFacade proyectoConcesionesMinerasFacade;
	
	@EJB
	private  DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;

	public String visualizarOficio(String codigoProyecto, String resolucion, String oficio, String nombreAutoridad) {

		CodPro=codigoProyecto;
		Numresolucion=resolucion;
		Organizacion org = new Organizacion();		
		String proponente = "";
		String cedula = "";
		boolean actualiza=false;

		try {
			proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
			proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
			PerforacionExplorativa perforacionExplorativa=fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyecto);
			actualiza=perforacionExplorativa.getCodeUpdate()==null||perforacionExplorativa.getCodeUpdate().isEmpty()?false:true;
			nombreArea=proyecto.getCatalogoCategoria().getDescripcion().toString();
			codigoArea=proyecto.getCatalogoCategoria().getId().toString();
			codigoProyecto=proyecto.getCodigo();

			org = organizacionFacade.buscarPorPersona(proyecto.getUsuario().getPersona(),proyecto.getUsuario().getNombre().toString());

			if (org == null) {
				proponente = this.proyecto.getUsuario().getPersona().getNombre().toString();
				cedula = this.proyecto.getUsuario().getPin().toString();				
			} else {
				proponente = org.getNombre();
				cedula = org.getRuc();				
			}

			sector=proyecto.getTipoSector().getNombre();
			nombreRepresentanteLegal=proyecto.getUsuario().getPersona().getNombre().toString();
			parroquia = proyecto.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
			canton= ubicacionGeograficaFacade.buscarPorId(parroquia.getUbicacionesGeografica().getId());
			provincia=ubicacionGeograficaFacade.buscarPorId(canton.getUbicacionesGeografica().getId());
			
			List<ConcesionMinera> concesionmineraList=proyecto.getConcesionesMineras();
			for (ConcesionMinera concesionMinera : concesionmineraList) {				
				nombreConcesion+=concesionMinera.getNombre()+"(CÓDIGO "+concesionMinera.getCodigo()+"),";
			}
						
			List<ProyectoUbicacionGeografica> ubicaciones = proyecto.getProyectoUbicacionesGeograficas();
			for (ProyectoUbicacionGeografica ubicacion : ubicaciones) {
				String provTemp=ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
				if(!provincias.contains(provTemp))
					provincias+=provTemp+", ";
			}
			
			intersecaTexto+=provincias;
			
			
			Boolean interseca = certificadoInterseccionService.getValueInterseccionProyectoIntersecaCapas(proyecto);
			
			if (interseca) {
				intersecaTexto+="concluyendo que el proyecto INTERSECTA con ";
				
				List<InterseccionProyecto> interseccionProyecto=certificadoInterseccionService.getListaInterseccionProyectoIntersecaCapas(proyecto.getCodigo());
				for (InterseccionProyecto interseccion : interseccionProyecto) {
					List<DetalleInterseccionProyecto>interseccionProyectoDetalle =certificadoInterseccionService.getDetallesInterseccion(interseccion.getId());
					for (DetalleInterseccionProyecto detalle : interseccionProyectoDetalle) {
						intersecaTexto+= detalle.getNombre()+", ";
					}
				}			
			}else{
				intersecaTexto+="concluyendo que el proyecto NO INTERSECTA con el Sistema Nacional de Áreas Protegidas (SNAP), Bosques Protectores y Vegetación Protectora y Patrimonio Forestal del Estado";
			}
				
			
			verInforme=false;
		} catch (Exception e) {
			this.LOG.error("Error al inicializar: InformeTecnicoArtBean: ", e);
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}

		String pathPdf = null;
		String Html=null;
		try {			
			PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RESOLUCION_REGISTRO_AMBIENTAL_020.getIdTipoDocumento());

			EntityResolucion020 entityInforme = new EntityResolucion020();
			SimpleDateFormat fecha= new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es"));
			
			entityInforme.setNumeroResolucion(Numresolucion);			
			entityInforme.setFecha(""+fecha.format(new java.util.Date()));
			entityInforme.setFecha_certificado_interseccion(""+fecha.format(new java.util.Date()));
			entityInforme.setNumero_certificado_interseccion(oficio);
			entityInforme.setInterseca(intersecaTexto);
			entityInforme.setCoordenadasMineria(categoriaIIFacade.generarTablaCoordenadas(proyecto));			
			entityInforme.setNombre_concesion(nombreConcesion);			
			entityInforme.setCodigo_proyecto(codigoProyecto);
			entityInforme.setProvincias(provincias);
			entityInforme.setTitular_minero(proponente);
			entityInforme.setCantones(canton.toString());
			entityInforme.setCanton(canton.toString());
			entityInforme.setUbicacionFirma("Quito");
			entityInforme.setFechaCompleta(""+fecha.format(new java.util.Date()));
			entityInforme.setSubsecretario(nombreAutoridad);
			entityInforme.setCedula_titular_minero(cedula);	
			entityInforme.setTextoRegistroActualizacion(actualiza);
			

			nombreReporte = "Resolucion020.pdf";
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					Boolean.valueOf(true), entityInforme);


			Html=UtilGenerarInforme.generarFicheroHtml(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					Boolean.valueOf(true), entityInforme);
			setInformePdf(informePdf);

		} catch (Exception e) {
			this.LOG.error("Error al visualizar el informe técnico", e);
			JsfUtil.addMessageError("Error al visualizar el informe técnico");
		}
		return Html;
	}

	public String sector(String codigoProyecto){
		String sector="";
		CodPro=codigoProyecto;
		proyecto = proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
		sector=proyecto.getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().toString();
		return sector;
	}
	
	public String sectorRcoa(String codigoProyecto){
		String sector="";
		CodPro=codigoProyecto;
		ProyectoLicenciaCuaCiuu actividadPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(codigoProyecto);
		if(actividadPrincipal!=null)
			sector=actividadPrincipal.getCatalogoCIUU().getTipoSector().toString();
		
		return sector;
	}
	
	public String visualizarOficioRcoa(String codigoProyecto, String resolucion, String oficio, String nombreAutoridad) {

		CodPro=codigoProyecto;
		Numresolucion=resolucion;
		Organizacion org = new Organizacion();		
		String proponente = "";
		String cedula = "";
		boolean actualiza=false;
		
		ProyectoLicenciaCoa proyectoCoa=null;

		try {			
			ProyectoLicenciaCuaCiuu actividadPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(codigoProyecto);
			proyectoCoa = actividadPrincipal.getProyectoLicenciaCoa();
//			PerforacionExplorativa perforacionExplorativa=fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyecto.getId());
//			actualiza=perforacionExplorativa.getCodeUpdate()==null||perforacionExplorativa.getCodeUpdate().isEmpty()?false:true;
			
			proyectoCoa.setNumeroOficio(oficio);
			proyectoCoa.setNumeroResolucion(resolucion);
			proyectoLicenciaCoaFacade.guardar(proyectoCoa);
			
			org = organizacionFacade.buscarPorPersona(proyectoCoa.getUsuario().getPersona(),proyectoCoa.getUsuario().getNombre().toString());

			if (org == null) {
				proponente = proyectoCoa.getUsuario().getPersona().getNombre().toString();
				cedula = proyectoCoa.getUsuario().getPin().toString();				
			} else {
				proponente = org.getNombre();
				cedula = org.getRuc();				
			}

			sector=actividadPrincipal.getCatalogoCIUU().getTipoSector().getNombre();
			nombreRepresentanteLegal=proyectoCoa.getUsuario().getPersona().getNombre().toString();
			
			UbicacionesGeografica ubicacionPrincipal=proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoCoa).getUbicacionesGeografica();
			parroquia=ubicacionPrincipal;
			canton=ubicacionPrincipal.getUbicacionesGeografica();
			provincia=ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica();
			
			List<ProyectoLicenciaAmbientalConcesionesMineras> concesionmineraList = proyectoConcesionesMinerasFacade.cargarConcesiones(proyectoCoa);
			for (ProyectoLicenciaAmbientalConcesionesMineras concesionMinera : concesionmineraList) {				
				nombreConcesion+=concesionMinera.getNombre()+"(CÓDIGO "+concesionMinera.getCodigo()+"),";
			}
						
			List<UbicacionesGeografica> ubicaciones = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoCoa);
			for (UbicacionesGeografica ubicacion : ubicaciones) {
				String provTemp=ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
				if(!provincias.contains(provTemp))
					provincias+=provTemp+", ";
			}
			
			intersecaTexto+=provincias;
			
			
			Boolean interseca = detalleInterseccionProyectoAmbientalFacade.isProyectoIntersecaCapas(proyectoCoa.getCodigoUnicoAmbiental());
			
			if (interseca) {
				intersecaTexto+="concluyendo que el proyecto INTERSECTA con ";				
				List<DetalleInterseccionProyectoAmbiental> detalleInter= detalleInterseccionProyectoAmbientalFacade.buscarPorProyecto(proyectoCoa);
				for (DetalleInterseccionProyectoAmbiental detalleInterseccionProyecto : detalleInter) {
					intersecaTexto+= detalleInterseccionProyecto.getInterseccionProyectoLicenciaAmbiental().getDescripcionCapa() +":"+ detalleInterseccionProyecto.getNombreGeometria() +", ";
				}		
			}else{
				intersecaTexto+="concluyendo que el proyecto NO INTERSECTA con el Sistema Nacional de Áreas Protegidas (SNAP), Bosques Protectores y Vegetación Protectora y Patrimonio Forestal del Estado";
			}
				
			
			verInforme=false;
		} catch (Exception e) {
			this.LOG.error("Error al inicializar: InformeTecnicoArtBean: ", e);
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}

		String pathPdf = null;
		String Html=null;
		try {			
			PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RESOLUCION_REGISTRO_AMBIENTAL_020.getIdTipoDocumento());

			EntityResolucion020 entityInforme = new EntityResolucion020();
			SimpleDateFormat fecha= new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es"));
			
			entityInforme.setNumeroResolucion(Numresolucion);			
			entityInforme.setFecha(""+fecha.format(new java.util.Date()));
			entityInforme.setFecha_certificado_interseccion(""+fecha.format(new java.util.Date()));
			entityInforme.setNumero_certificado_interseccion(oficio);
			entityInforme.setInterseca(intersecaTexto);
			entityInforme.setCoordenadasMineria(categoriaIIFacade.generarTablaCoordenadas(proyectoCoa));			
			entityInforme.setNombre_concesion(nombreConcesion);			
			entityInforme.setCodigo_proyecto(codigoProyecto);
			entityInforme.setProvincias(provincias);
			entityInforme.setTitular_minero(proponente);
			entityInforme.setCantones(canton.toString());
			entityInforme.setCanton(canton.toString());
			entityInforme.setUbicacionFirma("Quito");
			entityInforme.setFechaCompleta(""+fecha.format(new java.util.Date()));
			entityInforme.setSubsecretario(nombreAutoridad);
			entityInforme.setCedula_titular_minero(cedula);	
			entityInforme.setTextoRegistroActualizacion(actualiza);
			

			nombreReporte = "Resolucion020.pdf";
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					Boolean.valueOf(true), entityInforme);


			Html=UtilGenerarInforme.generarFicheroHtml(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					Boolean.valueOf(true), entityInforme);
			setInformePdf(informePdf);

		} catch (Exception e) {
			this.LOG.error("Error al visualizar el informe técnico", e);
			JsfUtil.addMessageError("Error al visualizar el informe técnico");
		}
		return Html;
	}

}
