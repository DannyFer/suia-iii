package ec.gob.ambiente.suia.reportes;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CertificadoInterseccion;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.InformeTecnicoEia;
import ec.gob.ambiente.suia.domain.InventarioForestal;
import ec.gob.ambiente.suia.domain.LicenciaAmbiental;
import ec.gob.ambiente.suia.domain.MecanismoParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.OficioAprobacionEia;
import ec.gob.ambiente.suia.domain.OficioObservacionEia;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityLicenciaAmbientalMineriaMunicipal;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.eia.inventarioforestal.facade.InventarioForestalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@Stateless
public class LicenciaAmbientalGeneralProvincial {

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
	String Numresolucion;
	List<String> direccionRepresentanteLegal = new ArrayList<String>();
	List<String> telefonoRepresentanteLegal = new ArrayList<String>();
	List<String> emailRepresentanteLegal = new ArrayList<String>();
	UbicacionesGeografica canton = new UbicacionesGeografica();
	UbicacionesGeografica provincia = new UbicacionesGeografica();
	String sector;
	String descripcionExnate;
	String numeroTramite;
	Date fechaTramite;
	Double valorTramite=0.00;
	Double valorTramiteProcesosAdministrativos=0.00;
	Double valorTramiteInventarioForestal=0.00;
	String numeroOficoInforme;
	Date fechaOficioInforme;

	String numeroOficioObservaciones;
	String fechaOficioObservaciones;

	String numeroOficioAprobacion;
	Date fechaOficioAprobacion;

	String codigoConsecionMinera;
	String nombreConsecionMinera;

	String valorTramiteGolbal="0";
	String ejecute="";
	String lugarAsamblea="";
	String obligaciones="";

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
	private TipoDocumento tipoDocumento;

	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private InformeProvincialGADFacade informeProvincialGADFacade;

	@Getter
	@Setter
	private File informePath;

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
	private CertificadoInterseccionFacade certificadoInterseccionFacade;

	@Getter
	@Setter
	private CertificadoInterseccion certificadoInterseccion;

	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	
	@EJB
	private LicenciaAmbientalFacade licenciaAmbientalFacade;
	
	@EJB
	private InventarioForestalFacade inventarioForestalFacade;
	
	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinanciera;

	@Getter
	@Setter
	private InformeTecnicoEia informeTecnicoEia;

	@Getter
	@Setter
	private OficioObservacionEia oficioObservacionEia;

	@Getter
	@Setter
	private ConcesionMinera concesionMinera;

	@PostConstruct
	public void init() {
		// visualizarOficio("MAE-RA-2015-200217", "resolucion", "oficio",
		// "licencia");
	}

	public String Html = null;
	SimpleDateFormat fecha = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy",new Locale("es"));

public String visualizarOficio(String codigoProyecto, String resolucion) {
		
		CodPro=codigoProyecto;
		Numresolucion=resolucion;
		
		try {
			this.tipoDocumento = new TipoDocumento();
			proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
			proyectoLicenciamientoAmbiental = proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
			nombreArea=proyectoLicenciamientoAmbiental.getCatalogoCategoria().getDescripcion().toString();
			codigoArea=proyectoLicenciamientoAmbiental.getCatalogoCategoria().getId().toString();
			codigoProyecto=proyectoLicenciamientoAmbiental.getCodigo();
			//proponente=organizacionFacade.buscarPorPersona(proyectoLicenciamientoAmbiental.getUsuario().getPersona());
			proponente=organizacionFacade.buscarPorPersona(proyectoLicenciamientoAmbiental.getUsuario().getPersona(), proyectoLicenciamientoAmbiental.getUsuario().getNombre());
			if(proponente!=null){
			nombreProponente=proponente.getNombre();
			cedulaPropononte=proponente.getPersona().getPin();
			listContacto=contactoFacade.buscarPorOrganizacion(proponente);
			}else{
			nombreProponente=proyectoLicenciamientoAmbiental.getUsuario().getPersona().getNombre().toString();
			listContacto= contactoFacade.buscarPorPersona(proyectoLicenciamientoAmbiental.getUsuario().getPersona());
			}
			sector=proyectoLicenciamientoAmbiental.getTipoSector().getNombre();
			nombreRepresentanteLegal=proyectoLicenciamientoAmbiental.getUsuario().getPersona().getNombre().toString();
			parroquia = proyectoLicenciamientoAmbiental.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
			canton= ubicacionGeograficaFacade.buscarPorId(parroquia.getUbicacionesGeografica().getId());
			provincia=ubicacionGeograficaFacade.buscarPorId(canton.getUbicacionesGeografica().getId());
			
			parametroExante=proyectoLicenciamientoAmbiental.getTipoEstudio().getId().toString();
			
			if(parametroExante.equals("1") ){
				parametroExpost="exante";
				ejecute="ejecute";
				descripcionExnate = "1. PROTOCOLIZACIÓN DE PRESUPUESTO ESTIMADO (PARA LOS PROYECTOS EXANTE PAGO DEL 1X1000 DEL COSTO DEL PROYECTO APLICA SOLO AL SECTOR PRIVADO)";
			} else if (parametroExante.equals("2")) {
				parametroExpost="expost";
				ejecute="continue";
				descripcionExnate = "1. Formulario 101 (PARA LOS PROYECTOS EXPOST PAGO DEL 1X1000 DEL COSTO DEL PROYECTO APLICA SOLO AL SECTOR PRIVADO).";
			}else{
			parametroExpost="";
			}

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("p_proyecto", proyectoLicenciamientoAmbiental);

			List<InformeTecnicoEia> lista = crudServiceBean.findByNamedQueryGeneric(InformeTecnicoEia.OBTENER_INFORME_TECNICO_EIA_POR_PROYECTO,parametros);

			if (lista.size() > 0) {
				for (InformeTecnicoEia informeTecnicoEia : lista) {
					numeroOficoInforme = informeTecnicoEia.getNumeroInforme();
					fechaOficioInforme= informeTecnicoEia.getFechaCreacion();
					obligaciones=informeTecnicoEia.getOtrasObligaciones();
				}
			}else {
				numeroOficoInforme="";
				fechaOficioInforme=null;
			}

			List<OficioAprobacionEia> listaOficioAprobacionEias = crudServiceBean
					.findByNamedQueryGeneric(OficioAprobacionEia.OBTENER_INFORME_TECNICO_EIA_APROBACION_POR_PROYECTO,parametros);
			if (listaOficioAprobacionEias.size() > 0) {
				for (OficioAprobacionEia oficioAprobacionEia : listaOficioAprobacionEias) {
					numeroOficioAprobacion = oficioAprobacionEia.getNumeroOficio();
					fechaOficioAprobacion= oficioAprobacionEia.getFechaCreacion();
					valorTramiteProcesosAdministrativos=oficioAprobacionEia.getValorTotal();
				}
			}else {
				numeroOficioAprobacion="";
				fechaOficioAprobacion=null;
			}
			
			LicenciaAmbiental licenciaAmbiental = licenciaAmbientalFacade.obtenerLicenciaAmbientallPorProyectoId(proyectoLicenciamientoAmbiental.getId());			
			valorTramite=(double)licenciaAmbiental.getCostoInversion();
			EstudioImpactoAmbiental estudioImpactoAmbiental= estudioImpactoAmbientalFacade.obtenerPorProyecto(proyectoLicenciamientoAmbiental);
			InventarioForestal inventarioForestal= inventarioForestalFacade.obtenetInventarioForestal(estudioImpactoAmbiental);
			
			if(inventarioForestal.getProductosForestalesMaderablesYNoMaderables()!=null)
			valorTramiteInventarioForestal=inventarioForestal.getProductosForestalesMaderablesYNoMaderables();
			certificadoInterseccion = certificadoInterseccionFacade.recuperarCertificadoInterseccion(proyectoLicenciamientoAmbiental);
			List<TransaccionFinanciera> transaccionFinancieras = transaccionFinancieraFacade.cargarTransacciones(proyectoLicenciamientoAmbiental.getId());
			for (TransaccionFinanciera transaccionFinanciera : transaccionFinancieras) {
				numeroTramite = transaccionFinanciera.getNumeroTransaccion();
				fechaTramite = transaccionFinanciera.getFechaCreacion();
			}
			if(valorTramite>=500000.00){
				valorTramite=(valorTramite/1000.00);
			}				
			valorTramite=(valorTramite+valorTramiteProcesosAdministrativos+valorTramiteInventarioForestal);
			
			List<MecanismoParticipacionSocialAmbiental> listaMecanismoAsamblea = crudServiceBean
					.findByNamedQueryGeneric(MecanismoParticipacionSocialAmbiental.OBTENER_MECANISMO_ASAMBLEA_PUBLICA,parametros);
			if (listaMecanismoAsamblea.size() > 0) {
				for (MecanismoParticipacionSocialAmbiental mecanismoParticipacionSocialAmbiental : listaMecanismoAsamblea) {
					lugarAsamblea = "se realizó la Asamblea Pública en el "+mecanismoParticipacionSocialAmbiental.getLugar()+" el "+mecanismoParticipacionSocialAmbiental.getFechaCreacion()
							+"; y se mantuvo el Centro de Información permanente en "+mecanismoParticipacionSocialAmbiental.getComunidad()
							+" desde el"+mecanismoParticipacionSocialAmbiental.getFechaInicio()+" hasta el "+ mecanismoParticipacionSocialAmbiental.getFechaFin();
				}
			}else {
				lugarAsamblea="";
			}

			List<OficioObservacionEia> listaOficioObservacionEias = crudServiceBean
					.findByNamedQueryGeneric(OficioObservacionEia.OBTENER_OFICIO_APROBACION_POR_PROYECTO,parametros);
			
			if (listaOficioAprobacionEias.size() > 0) {
				for (OficioObservacionEia oficioObservacionEia : listaOficioObservacionEias) {
					numeroOficioObservaciones="<tr> <td style=\"font-weight: bold; vertical-align: top;\">Que, </td>"
							+ "<td style=\"text-align: justify;padding-bottom: 10px;vertical-align: top\">mediante Oficio No. "+oficioObservacionEia.getNumeroOficio()+" de "+oficioObservacionEia.getFechaCreacion()+" sobre la base del Informe Técnico No. "+numeroOficoInforme+" el Gobierno Autónomo Descentralizado Provincial "+provincia+", realiza observaciones al "+nombreProponente+" ubicado en "+provincia+", "+canton+", "+parroquia+";</td>"
							+ "</tr>"
							+"<tr><td style=\"font-weight: bold; vertical-align: top;\">Que, </td>"
							+ "<td style=\"text-align: justify;padding-bottom: 10px;vertical-align: top;\">el "+fecha.format(certificadoInterseccion.getFechaCreacion())+", la "+nombreProponente+", ingresa al Sistema Único de Información Ambiental para análisis, revisión y pronunciamiento las respuestas a las observaciones realizadas al Estudio de Impacto Ambiental "+nombreProyecto+", ubicado en la provincia de "+provincia+";</td>"
							+ "</tr>";
				}
			}

			List<ConcesionMinera> parametrosConsecionMinera = crudServiceBean.findByNamedQueryGeneric(
					ConcesionMinera.OBTENER_CONSECION_MINERA, parametros);
			if (lista.size() > 0) {
				for (ConcesionMinera concesionMinera  : parametrosConsecionMinera) {
					codigoConsecionMinera = concesionMinera.getCodigo();
					nombreConsecionMinera=concesionMinera.getNombre();
				}
			}else {
				codigoConsecionMinera = "";
				nombreConsecionMinera="";
			}
			//
			verInforme=false;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}

		//String Html=null;
		try {
			PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_LICENCIA_PROVINCIAL_GEN.getIdTipoDocumento());

			EntityLicenciaAmbientalMineriaMunicipal entityInforme = new EntityLicenciaAmbientalMineriaMunicipal();
			
			DecimalFormat decimalValorTramite= new DecimalFormat(".##");
			valorTramiteGolbal=decimalValorTramite.format(valorTramite).replace(",",".");
			entityInforme.setNombreProyecto(proyectoLicenciamientoAmbiental.getNombre());
			entityInforme.setFechaRegistro(""+ fecha.format(proyectoLicenciamientoAmbiental.getFechaRegistro()));
			entityInforme.setNombreProponente(nombreProponente);
			entityInforme.setNombreProyecto(proyectoLicenciamientoAmbiental.getNombre());
			entityInforme.setCodigoProyecto(codigoProyecto);
			entityInforme.setNumeroDocumentoInterseccion(certificadoInterseccion.getCodigo());
			entityInforme.setFechaCreacionInterseccion(""+ fecha.format(certificadoInterseccion.getFechaCreacion()));
			entityInforme.setFechaTDRs(""+ fecha.format(certificadoInterseccion.getFechaCreacion()));
			entityInforme.setFechaIngresoEstudio(""+ fecha.format(certificadoInterseccion.getFechaCreacion()));
			entityInforme.setFecha(""+ fecha.format(certificadoInterseccion.getFechaCreacion()));
			entityInforme.setNumeroInformeTecnico(numeroOficoInforme);
			entityInforme.setFechaInformeTecnico(" "+fecha.format(fechaOficioInforme));
			entityInforme.setDescripcionExante(descripcionExnate);
			entityInforme.setNumeroResolucion(resolucion);
			entityInforme.setNumeroTramite(numeroTramite);
			entityInforme.setValorNumeroTramite(valorTramiteGolbal);
			entityInforme.setFechaNumeroTramite("" + fecha.format(fechaTramite));
			entityInforme.setObligacionesPuestas(obligaciones);
			if(numeroOficioAprobacion!=null && fechaOficioAprobacion!=null){
				entityInforme.setNumeroOficioAprobacion(numeroOficioAprobacion);
				entityInforme.setFechaOficioAprobacion(""+fecha.format(fechaOficioAprobacion));
			}else{
				entityInforme.setNumeroOficioAprobacion(" ");
				entityInforme.setFechaOficioAprobacion(" ");
			}
			if(numeroOficioObservaciones!=null){
				entityInforme.setNumeroOficioObservaciones(numeroOficioObservaciones);
			}else {
				entityInforme.setNumeroOficioObservaciones("");
			}

			entityInforme.setNombreArea(nombreArea);
			entityInforme.setAreaConsecion(",");
			if(proyectoLicenciamientoAmbiental.getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().getNombre().equals("Minería")){
			if(codigoConsecionMinera!=null && nombreConsecionMinera!=null){
				entityInforme.setAreaConsecion(" conformado por las áreas mineras "+nombreConsecionMinera+" "+codigoConsecionMinera+" fase minera de explotación de áridos y petreos");
				} else {
					entityInforme.setAreaConsecion(",");
				}
			}
			
			//conformado por las áreas mineras $F{areaConsecion} $F{codigoConsecion} fase minera de explotación de áridos y petreos
			entityInforme.setNombreProyecto(proyectoLicenciamientoAmbiental.getNombre());
			entityInforme.setProvincia(provincia.toString());
			entityInforme.setNombreProponente(nombreProponente);
			entityInforme.setUbicacion(provincia+", "+canton+", "+parroquia);
			entityInforme.setCoordenadas(categoriaIIFacade.generarTablaCoordenadas(proyectoLicenciamientoAmbiental));
			entityInforme.setFechaRegistro(""+fecha.format(proyectoLicenciamientoAmbiental.getFechaRegistro()));
			entityInforme.setFechaCompleta(""+fecha.format(new Date()));
			entityInforme.setNumeroMemorando("número memorando");
			entityInforme.setMetalicosNoMetalicos("Metalicos o No Metalicos");
			entityInforme.setFase("Fase de la minería");
			entityInforme.setFechaMemorando(""+fecha.format(new Date()));
			entityInforme.setExanteOexpost(parametroExpost);
			entityInforme.setExpost(parametroExpost);
			entityInforme.setFechaOficio(fecha.format(new Date()));
			entityInforme.setTrimestralOsemestral("Trimestral o Semestral");
			entityInforme.setCodigoProyecto(codigoProyecto);
			entityInforme.setExante(parametroExpost);
			entityInforme.setEjecute(ejecute);
			entityInforme.setCanton(canton.toString());
			entityInforme.setParroquia(parroquia.toString());
			entityInforme.setFechaResolucion(" "+fecha.format(new Date()));
			entityInforme.setLugarAsamblea(lugarAsamblea);
			
			nombreReporte = "InformeTecnico.pdf";
			Html=UtilGenerarInforme.generarFicheroHtml(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					Boolean.valueOf(true), entityInforme);
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al visualizar el informe técnico");
		}
		return Html;
	}

	public String sector(String codigoProyecto) {
		String sector = "";
		CodPro = codigoProyecto;
		proyectoLicenciamientoAmbiental = proyectoLicenciaAmbientalFacade
				.getProyectoPorCodigo(CodPro);
		sector = proyectoLicenciamientoAmbiental.getCatalogoCategoria()
				.getCatalogoCategoriaPublico().getTipoSector().toString();
		return sector;
	}
}