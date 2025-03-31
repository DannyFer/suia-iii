package ec.gob.ambiente.suia.reportes;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityLicenciaAmbientalMineriaMunicipal;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@Stateless
public class LicenciaAmbientalHidrocarburos {
	
	String nombreProyecto;
	String nombreArea;
	String codigoArea;
	String nombreProponente;
	String cedulaPropononte;
	String nombreRepresentanteLegal;
	String email="";
	String direccion="";
	String telefono="";
	String codigoProyecto="";
	String parametroExante;
	String parametroExpost;
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
	
	@PostConstruct
	public void init(){
//		visualizarOficio("MAE-RA-2015-200217", "resolucion", "oficio", "licencia");
	}
	
	public String Html=null;
	
	public String visualizarOficio(String codigoProyecto, String resolucion, String oficio, String licencia) {
		
		CodPro=codigoProyecto;
		Numresolucion=resolucion;
		
		try {
			this.tipoDocumento = new TipoDocumento();
			proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
			proyectoLicenciamientoAmbiental = proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
			nombreArea=proyectoLicenciamientoAmbiental.getCatalogoCategoria().getDescripcion().toString();
			codigoArea=proyectoLicenciamientoAmbiental.getCatalogoCategoria().getId().toString();
			codigoProyecto=proyectoLicenciamientoAmbiental.getCodigo();
			proponente=organizacionFacade.buscarPorPersona(proyectoLicenciamientoAmbiental.getUsuario().getPersona());
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
				parametroExpost="Exante";
			}else
			if(parametroExante.equals("2") ){
				parametroExpost="Expost";
			}else{
			parametroExpost="";
			}
			
			
			verInforme=false;
		} catch (Exception e) {
		//	this.LOG.error("Error al inicializar: InformeTecnicoArtBean: ", e);
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}

		String pathPdf = null;
		//String Html=null;
		try {
//			String MineriaArtesanal="LAS LABORES DE MINERÍA ARTESANAL PARA LA EXPLOTACIÓN,";
//			String MineriaConstruccion="LAS LABORES DE MINERÍA ARTESANAL PARA LA EXPLOTACIÓN DE MATERIALES DE CONSTRUCCIÓN DE LA CONCESIÓN MINERA,";
			PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.TIPO_LICENCIA_HIDROCARBUROS.getIdTipoDocumento());

			EntityLicenciaAmbientalMineriaMunicipal entityInforme = new EntityLicenciaAmbientalMineriaMunicipal();					
			
			entityInforme.setNombreProyecto(proyectoLicenciamientoAmbiental.getNombre());
			
			entityInforme.setNumeroResolucion("Numero Resolución");
			entityInforme.setAreaMinera(nombreArea);
			entityInforme.setCodigoArea(codigoArea);
			entityInforme.setProvincia(provincia.toString());
			entityInforme.setNombreProponente(nombreProponente);
			
			entityInforme.setUbicacion(provincia+", "+canton+", "+parroquia);
			//entityInforme.setParametroRepresentanteLegal(nombreRepresentanteLegal);
			entityInforme.setCoordenadas(categoriaIIFacade.generarTablaCoordenadas(proyectoLicenciamientoAmbiental));
			SimpleDateFormat fecha= new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
			entityInforme.setFechaRegistro(""+fecha.format(proyectoLicenciamientoAmbiental.getFechaRegistro()));
			entityInforme.setFechaCompleta(""+fecha.format(new Date()));
			entityInforme.setNumeroOficio(oficio);
			entityInforme.setDireccion1("dirección 1");
			entityInforme.setDireccion2("dirección 2");
			entityInforme.setNumeroMemorando("número memorando");
			entityInforme.setMetalicosNoMetalicos("Metalicos o No Metalicos");
			entityInforme.setFase("Fase de la minería");
			entityInforme.setFechaMemorando(""+fecha.format(new Date()));
			entityInforme.setNumeroInformeTecnico("No. Inforeme Técnico");
			entityInforme.setExanteOexpost(parametroExpost);
			entityInforme.setExanteContinue("Exante Continue");
			entityInforme.setInciso("Num Inciso");
			entityInforme.setValorFacPagoControlSeguimiento("Valor Pago Control Seguimiento");
			entityInforme.setFechaFacPagoControlSeguimiento("fechaFacPagoControlSeguimiento");
			entityInforme.setProvinciaFacPagoControlSeguimiento("provinciaFacPagoControlSeguimiento");
			entityInforme.setNumFacPagoControlSeguimiento("numFacPagoControlSeguimiento");
			entityInforme.setProvinciaFacPagoInventarioForestal("provinciaFacPagoInventarioForestal");
			entityInforme.setFechaFacPagoInventarioForestal("fechaFacPagoInventarioForestal");
			entityInforme.setValorFacPagoInventarioForestal("valorFacPagoInventarioForestal");
			entityInforme.setProvinciaFacPagoUnoMil("provinciaFacPagoUnoMil");
			entityInforme.setNumFacPagoUnoMil("numFacPagoUnoMil");
			entityInforme.setExpost(parametroExpost);
			entityInforme.setFechaInformeTecnico("fechaInformeTecnico");
			entityInforme.setFechaOficio(fecha.format(new Date()));
			entityInforme.setLugarAsamblea("lugarAsamblea");
			entityInforme.setFechaAsamblea("fechaAsamblea");
			entityInforme.setLugarCentroInformacion("lugarCentroInformacion");
			entityInforme.setFechaInicioCentroInformacion("fechaInicioCentroInformacion");
			entityInforme.setFechaIngresoEstudio("fechaIngresoEstudio");
			entityInforme.setContaminacionAmbiental("contaminacionAmbiental");
			entityInforme.setFechaFacPagoUnoMil("fechaFacPagoUnoMil");
			entityInforme.setValorFacPagoUnoMil("valorFacPagoUnoMil");
			entityInforme.setNumFacPagoInventarioForestal("numFacPagoInventarioForestal");
			entityInforme.setFechaFacPagoInventarioForestal("fechaFacPagoInventarioForestal");
			entityInforme.setNumAcuerdoMinCootad("numAcuerdoMinCootad");
			entityInforme.setTrimestralOsemestral("Trimestral o Semestral");
			entityInforme.setNombreProyecto(nombreProyecto);
//			for (Contacto cont : listContacto) {
//				if(cont.getFormasContacto().getId()==2 && cont.getEstado().equals(true))
//				direccionRepresentanteLegal.add(cont.getValor());
//				if(cont.getFormasContacto().getId()==6 && cont.getEstado().equals(true))
//					telefonoRepresentanteLegal.add(cont.getValor());
//				if(cont.getFormasContacto().getId()==5 && cont.getEstado().equals(true))
//					emailRepresentanteLegal.add(cont.getValor());
//			}
//			
//			for (int i=0; i<telefonoRepresentanteLegal.size();i++) {
//				if(i==0)	
//					telefono=telefonoRepresentanteLegal.get(i);
//				else
//					telefono=telefono+", "+telefonoRepresentanteLegal.get(i);
//			}
//			for (int i=0; i<direccionRepresentanteLegal.size();i++) {
//				if(i==0)	
//					direccion=direccionRepresentanteLegal.get(i);
//				else
//					direccion=direccion+", "+direccionRepresentanteLegal.get(i);
//			}
			/*entityInforme.setParametroDireccionRepresentanteLegal(direccion);
			entityInforme.setParametroTelefonoRepresentanteLegal(telefono);*/
//			for (int i=0; i<emailRepresentanteLegal.size();i++) {
//				if(i==0)	
//				email=emailRepresentanteLegal.get(i);
//				else
//					email=email+", "+emailRepresentanteLegal.get(i);
//			}
			//entityInforme.setParametroEmailRepresentanteLegal(email);
			entityInforme.setCodigoProyecto(codigoProyecto);
			
			entityInforme.setExante(parametroExpost);
			//entityInforme.setParametrocedulaProponente(cedulaPropononte);
			entityInforme.setCanton(canton.toString());
			entityInforme.setParroquia(parroquia.toString());
			entityInforme.setFechaResolucion(" "+fecha.format(new Date()));
			
			
			
			nombreReporte = "InformeTecnico.pdf";
			File informePdf = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
			Boolean.valueOf(true), entityInforme);
			/*File informePdf1 = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					Boolean.valueOf(true), entityInforme);*/
			
			Html=UtilGenerarInforme.generarFicheroHtml(plantillaReporte.getHtmlPlantilla(), nombreReporte,
					Boolean.valueOf(true), entityInforme);

			setInformePath(informePdf);
			
			pathPdf = informePdf.getParent();
			
		} catch (Exception e) {
			//this.LOG.error("Error al visualizar el informe técnico", e);
			JsfUtil.addMessageError("Error al visualizar el informe técnico");
		}
		System.out.println("plantilla: "+Html);
		return Html;
	}
	
	public String sector(String codigoProyecto){
		String sector="";
		CodPro=codigoProyecto;
		proyectoLicenciamientoAmbiental = proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(CodPro);
		sector=proyectoLicenciamientoAmbiental.getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().toString();
		return sector;
	}
}