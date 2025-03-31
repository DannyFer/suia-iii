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
import ec.gob.ambiente.suia.domain.InformeTecnicoEia;
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
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@Stateless
public class LicenciaAmbientalProvincialMineria {

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
	Double valorTramite;
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

	public String visualizarOficio(String codigoProyecto, String resolucion) {

		CodPro = codigoProyecto;
		Numresolucion = resolucion;

		try {
			this.tipoDocumento = new TipoDocumento();
			proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
			proyectoLicenciamientoAmbiental = proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
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
				parametroExpost = "Exante";
				ejecute="ejecute";
				descripcionExnate = "1. PROTOCOLIZACIÓN DE PRESUPUESTO ESTIMADO (PARA LOS PROYECTOS EXANTE PAGO DEL 1X1000 DEL COSTO DEL PROYECTO APLICA SOLO AL SECTOR PRIVADO)";
			} else if (parametroExante.equals("2")) {
				parametroExpost = "Expost";
				ejecute="continue";
				descripcionExnate = "1. Formulario 101 (PARA LOS PROYECTOS EXPOST PAGO DEL 1X1000 DEL COSTO DEL PROYECTO APLICA SOLO AL SECTOR PRIVADO).";
			} else {
				parametroExpost = "";
			}

			certificadoInterseccion = certificadoInterseccionFacade.recuperarCertificadoInterseccion(proyectoLicenciamientoAmbiental);
			List<TransaccionFinanciera> transaccionFinancieras = transaccionFinancieraFacade.cargarTransacciones(proyectoLicenciamientoAmbiental.getId());
			valorTramite = 0.0;
			for (TransaccionFinanciera transaccionFinanciera : transaccionFinancieras) {
				numeroTramite = transaccionFinanciera.getNumeroTransaccion();
				fechaTramite = transaccionFinanciera.getFechaCreacion();
				valorTramite += transaccionFinanciera.getMontoTransaccion();
			}

			DecimalFormat decimalValorTramite= new DecimalFormat(".##");
			valorTramiteGolbal=decimalValorTramite.format(valorTramite).replace(",",".");

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("p_proyecto", proyectoLicenciamientoAmbiental);

			List<InformeTecnicoEia> lista = crudServiceBean.findByNamedQueryGeneric(InformeTecnicoEia.OBTENER_INFORME_TECNICO_EIA_POR_PROYECTO,parametros);

			if (lista.size() > 0) {
				for (InformeTecnicoEia informeTecnicoEia : lista) {
					numeroOficoInforme = informeTecnicoEia.getNumeroInforme();
					fechaOficioInforme= informeTecnicoEia.getFechaCreacion();
				}
			}else {
				numeroOficoInforme="";
				fechaOficioInforme=null;
			}

//			Map<String, Object> parametros1 = new HashMap<String, Object>();
//			parametros1.put("p_proyecto", proyectoLicenciamientoAmbiental);

			List<OficioAprobacionEia> listaOficioAprobacionEias = crudServiceBean
					.findByNamedQueryGeneric(OficioAprobacionEia.OBTENER_INFORME_TECNICO_EIA_APROBACION_POR_PROYECTO,parametros);
			if (listaOficioAprobacionEias.size() > 0) {
				for (OficioAprobacionEia oficioAprobacionEia : listaOficioAprobacionEias) {
					numeroOficioAprobacion = oficioAprobacionEia.getNumeroOficio();
					fechaOficioAprobacion= oficioAprobacionEia.getFechaCreacion();
				}
			}else {
				numeroOficioAprobacion="";
				fechaOficioAprobacion=null;
			}

//			Map<String, Object> parametros2 = new HashMap<String, Object>();
//			parametros2.put("p_proyecto", proyectoLicenciamientoAmbiental);

			List<OficioObservacionEia> listaOficioObservacionEias = crudServiceBean
					.findByNamedQueryGeneric(OficioObservacionEia.OBTENER_OFICIO_APROBACION_POR_PROYECTO,parametros);
			if (listaOficioAprobacionEias.size() > 0) {
				for (OficioObservacionEia oficioObservacionEia : listaOficioObservacionEias) {

					numeroOficioObservaciones = oficioObservacionEia.getFechaOficioObservaciones();
					fechaOficioObservaciones= oficioObservacionEia.getFechaOficioObservaciones();
				}
			}else {
				numeroOficioObservaciones="";
				fechaOficioObservaciones="";
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
			verInforme = false;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}

		// String Html=null;
		try {
			PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_LICENCIA_PROVINCIAL_MIN.getIdTipoDocumento());

			EntityLicenciaAmbientalMineriaMunicipal entityInforme = new EntityLicenciaAmbientalMineriaMunicipal();
			entityInforme.setNombreProyecto(proyectoLicenciamientoAmbiental.getNombre());

			SimpleDateFormat fecha = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy",new Locale("es"));
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
			if(numeroOficioAprobacion!=null && fechaOficioAprobacion!=null){
				entityInforme.setNumeroOficioAprobacion(numeroOficioAprobacion);
				entityInforme.setFechaOficioAprobacion(""+fecha.format(fechaOficioAprobacion));
			}else{
				entityInforme.setNumeroOficioAprobacion("NO TIENE OFICIO APROBACION");
				entityInforme.setFechaOficioAprobacion("NO TIENE FECHA OFICIO APROBACION");
			}
			if(numeroOficioObservaciones!=null && fechaOficioObservaciones!=null){
				entityInforme.setNumeroOficioObservaciones(numeroOficioObservaciones);
				entityInforme.setFechaOficioObservaciones(fechaOficioObservaciones);

			}else {
				entityInforme.setNumeroOficioObservaciones("NO TIENE OFICIO OBSERVACIONES");
				entityInforme.setFechaOficioObservaciones("NO TIENE FECHA OFICIO OBSERVACIONES");
			}
			entityInforme.setNombreArea(nombreArea);
			if(codigoConsecionMinera!=null && nombreConsecionMinera!=null){
				entityInforme.setCodigoConsecion(codigoConsecionMinera);
				entityInforme.setAreaConsecion(nombreConsecionMinera);
			}else {
				entityInforme.setCodigoConsecion(" ");
				entityInforme.setAreaConsecion(" ");
			}


			entityInforme.setNombreProyecto(proyectoLicenciamientoAmbiental.getNombre());
			entityInforme.setProvincia(provincia.toString());
			entityInforme.setNombreProponente(nombreProponente);
			entityInforme.setUbicacion(provincia + ", " + canton + ", "+ parroquia);
			entityInforme.setCoordenadas(categoriaIIFacade.generarTablaCoordenadas(proyectoLicenciamientoAmbiental));
			entityInforme.setFechaRegistro(""+ fecha.format(proyectoLicenciamientoAmbiental.getFechaRegistro()));
			entityInforme.setFechaCompleta("" + fecha.format(new Date()));
			entityInforme.setNumeroMemorando("número memorando");
			entityInforme.setMetalicosNoMetalicos("Metalicos o No Metalicos");
			entityInforme.setFase("Fase de la minería");
			entityInforme.setFechaMemorando("" + fecha.format(new Date()));
			entityInforme.setExanteOexpost(parametroExpost);
			entityInforme.setExpost(parametroExpost);
			entityInforme.setFechaOficio(fecha.format(new Date()));
			entityInforme.setTrimestralOsemestral("Trimestral o Semestral");
			entityInforme.setCodigoProyecto(codigoProyecto);
			entityInforme.setExante(parametroExpost);
			entityInforme.setEjecute(ejecute);
			entityInforme.setCanton(canton.toString());
			entityInforme.setParroquia(parroquia.toString());
			entityInforme.setFechaResolucion(" " + fecha.format(new Date()));

			nombreReporte = "InformeTecnico.pdf";
			Html = UtilGenerarInforme.generarFicheroHtml(
					plantillaReporte.getHtmlPlantilla(), nombreReporte,
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