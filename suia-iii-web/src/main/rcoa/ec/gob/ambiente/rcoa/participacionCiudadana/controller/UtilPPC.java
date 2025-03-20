package ec.gob.ambiente.rcoa.participacionCiudadana.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ProyectoFasesCoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ProyectoFasesEiaCoa;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.FacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.FacilitadorPPC;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@Stateful
public class UtilPPC {
	
	public static final Integer ID_TIPO_AREA_PLANTA_CENTRAL = 1;
	public static final Integer ID_TIPO_AREA_OFICINA = 6;
	public static final Integer ID_TIPO_AREA_GAD = 3;
	public static final Integer ID_TIPO_AREA_GALAPAGOS = 2;
	// coa_mae.general_catalogs_coa - caty_id=19
	public static final Integer GECA_ID_INFORME_TECNICO_EIA = 143;
	public static final Integer GECA_ID_OFICIO_PRONUNCIAMIENTO_EIA = 144;
	
	@EJB
	private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;

	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private FacilitadorFacade facilitadorFacade;	
	
	@EJB
	private FacilitadorPPCFacade facilitadorPPCFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@EJB
	private ProyectoFasesCoaFacade proyectoFasesCoaFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	
	public Usuario asignarRol(ProyectoLicenciaCoa proyectoLicenciaCoa, String role ) {
    	Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
    	String rolPrefijo = "";
		String rolTecnico="";
		String areaRes=areaTramite.getAreaName();
		ProyectoLicenciaCuaCiuu ciiuArearesponsable=proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
		switch (role) {
		case "subsecretario":
	    	if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
	    		areaRes=areaTramite.getArea().getAreaName();
	    		rolPrefijo = "role.ppc.pc.subsecretario";
			}
			break;
		case "autoridad":
	    	if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
	    		rolPrefijo = "role.ppc.pc.autoridad";
			}
	    	if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_OFICINA) {
	    		areaRes=areaTramite.getArea().getAreaName();
	    		rolPrefijo = "role.ppc.cz.autoridad";
			}
	    	if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_GAD) {
	    		rolPrefijo = "role.ppc.gad.autoridad";
			}
	    	if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_GALAPAGOS) {
	    		rolPrefijo = "role.ppc.galapagos.autoridad";
			}
			break;
		case "coodinador": 
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_ELECTRICO)
					rolPrefijo = "role.ppc.pc.coordinador.electrico";
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_HIDROCARBUROS)
					rolPrefijo = "role.ppc.pc.coordinador.hidrocarburos";
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_MINERIA)
					rolPrefijo = "role.ppc.pc.coordinador.mineria";
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_OTROS)
					rolPrefijo = "role.ppc.pc.coordinador.otros";
			}
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_OFICINA) {
				areaRes=areaTramite.getAreaName();//los coordinadores se buscan en las OT
				rolPrefijo = "role.ppc.cz.coordinador";
			}
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_GAD) {
				rolPrefijo = "role.ppc.gad.coordinador";
			}
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_GALAPAGOS) {
	    		rolPrefijo = "role.ppc.galapagos.coordinador";
			}
			break;
		case "tecnicoPPC":
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_ELECTRICO)
					rolPrefijo = "role.ppc.pc.tecnico.ppc.electrico";
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_HIDROCARBUROS)
					rolPrefijo = "role.ppc.pc.tecnico.ppc.hidrocarburos";
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_MINERIA)
					rolPrefijo = "role.ppc.pc.tecnico.ppc.mineria";
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_OTROS)
					rolPrefijo = "role.ppc.pc.tecnico.ppc.otros";
			}
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_OFICINA) {
				areaRes=areaTramite.getAreaName();//los tecnicos se buscan en las OT
				rolPrefijo = "role.ppc.cz.tecnico.ppc";
			}
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_GAD) {
				rolPrefijo = "role.ppc.gad.tecnico.ppc";
			}
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_GALAPAGOS) {
	    		rolPrefijo = "role.ppc.galapagos.tecnico.ppc";
			}
			break;
		case "tecnicoEIA":
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_ELECTRICO)
					rolPrefijo = "role.ppc.pc.tecnico.eia.electrico";
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_HIDROCARBUROS)
					rolPrefijo = "role.ppc.pc.tecnico.eia.hidrocarburos";
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_MINERIA)
					rolPrefijo = "role.ppc.pc.tecnico.eia.mineria";
				if(ciiuArearesponsable.getCatalogoCIUU().getTipoSector().getId()==TipoSector.TIPO_SECTOR_OTROS)
					rolPrefijo = "role.ppc.pc.tecnico.eia.otros";
			}
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_OFICINA) {
				areaRes=areaTramite.getAreaName();//los tecnicos se buscan en las OT
				rolPrefijo = "role.ppc.cz.tecnico.eia";
			}
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_GAD) {
				rolPrefijo = "role.ppc.gad.tecnico.eia";
			}
			if (areaTramite.getTipoArea().getId() == ID_TIPO_AREA_GALAPAGOS) {
	    		rolPrefijo = "role.ppc.galapagos.tecnico.eia";
			}
			break;		
		default:
			rolPrefijo = "";
			break;
		}
    	rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
    	List<Usuario> listaTecnicosResponsables=null;
    	if(rolTecnico!=null)
    		listaTecnicosResponsables = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, areaRes);			

		if (listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
			System.out.println("No se encontro usuario PPC::::" + rolTecnico + " en " + areaTramite.getAreaName());			
			return null;
		}
		Usuario tecnicoResponsable = listaTecnicosResponsables.get(0);
		return tecnicoResponsable;
    }
	
	public String initiciales(String nombres)
	{
		String initials = "";
		StringTokenizer st = new StringTokenizer(nombres);
		while (st.hasMoreTokens()) {
			initials = initials + st.nextToken().charAt(0);
		}
		
		return initials;
	}
	
	public String generarCodigoOficio(Area area, String nombre) {
		String anioActual=secuenciasFacade.getCurrentYear();		
		String nombreSecuencia="MAAE-SUIA-"+anioActual+"_"+area.getAreaAbbreviation();
		String codigo = "";
		try {
			if (area.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
				codigo += Constantes.SIGLAS_INSTITUCION + "-SUIA-"
						+ initiciales(nombre)
						+ "-"
						+ anioActual
						+ "-"
						+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
			} else {
				codigo += area.getAreaAbbreviation()
						+ "-"
						+ anioActual
						+ "-"
						+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
			}
			return codigo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String generarCodigoInforme(Area area, String nombre) {
		String anioActual=secuenciasFacade.getCurrentYear();		
		String nombreSecuencia="PPC-DNPCA-SCA-MAA";
		String codigo = "";
		try {
			if (area.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
				codigo = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4) 
						+ "-"
						+ anioActual
						+ "-"
						+ initiciales(nombre)
						+ "-PPC-DNPCA-SCA-MAA";
			}
			return codigo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String obtenerNombreEnte(Area area, Integer tipoReporte) {
		String ente = "";
		try {
			if (tipoReporte.intValue() == GECA_ID_INFORME_TECNICO_EIA || tipoReporte.intValue() == GECA_ID_OFICIO_PRONUNCIAMIENTO_EIA) {
				if (area.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
					ente = area.getArea().getAreaName();
				} else {
					ente = area.getAreaName();
				}
			} else {
				if (area.getTipoArea().getId() == ID_TIPO_AREA_OFICINA) {
					ente = area.getArea().getAreaName();
				} else {
					ente = area.getAreaName();
				}
			}
			return ente;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String obtenerNombreEnteOficio(Area area) {
		String ente = "";
		
		if (area.getTipoArea().getId() == ID_TIPO_AREA_OFICINA) {
			ente = Constantes.CARGO_AUTORIDAD_ZONAL;
		} else {
			ente = area.getAreaName();
		}
		
		return ente;
	}
	
	public List<String> facilitadores(Integer numeroFacilitadores, ProyectoLicenciaCoa proyecto)
	{
		List<Usuario> confirmado = new ArrayList<Usuario>();
        List<Usuario> rechazado = new ArrayList<Usuario>();
        List<String> lista = new ArrayList<String>();
		///selecciono los facilitadores
        confirmado=facilitadorPPCFacade.facilitadoresConfirmado(proyecto);
        rechazado=facilitadorPPCFacade.facilitadoresRezhazado(proyecto);
        List<Usuario> facilitadores = facilitadorFacade.seleccionarFacilitadores(numeroFacilitadores,confirmado,rechazado,proyecto.getAreaResponsable().getAreaName());
        if (facilitadores.size() == numeroFacilitadores) {            
            for (Usuario usuario : facilitadores) {
            	lista.add(usuario.getNombre());
            }
        }	
        
		return lista;
	}
	
	public List<Usuario> getUsuariosfacilitadores(Integer numeroFacilitadores, ProyectoLicenciaCoa proyecto)
	{
		List<Usuario> confirmado = new ArrayList<Usuario>();
        List<Usuario> rechazado = new ArrayList<Usuario>();
        List<Usuario> lista = new ArrayList<Usuario>();
		///selecciono los facilitadores
        confirmado=facilitadorPPCFacade.facilitadoresConfirmado(proyecto);
        rechazado=facilitadorPPCFacade.facilitadoresRezhazado(proyecto);
        List<Usuario> facilitadores = facilitadorFacade.seleccionarFacilitadores(numeroFacilitadores,confirmado,rechazado,proyecto.getAreaResponsable().getAreaName());
        if (facilitadores.size() == numeroFacilitadores) {            
            return facilitadores;
        }	
        
		return lista;
	}
	
	public String generarCodigoOficioAprobacionPPC(Area area, String nombre) {
		String anioActual=secuenciasFacade.getCurrentYear();		
		String nombreSecuencia="MAAE-SUIA-"+anioActual+"_"+area.getAreaAbbreviation();
		String codigo = "";
		try {
			if (area.getTipoArea().getId() == ID_TIPO_AREA_PLANTA_CENTRAL) {
				codigo += Constantes.SIGLAS_INSTITUCION + "-SUIA-"
						+ initiciales(nombre)
						+ "-"
						+ anioActual
						+ "-"
						+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
			} else if (area.getTipoArea().getId() == ID_TIPO_AREA_GAD) {
				codigo = area.getAreaAbbreviation()
						+ "-"
						+ anioActual
						+ "-"
						+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
			}else {
				codigo += area.getAreaAbbreviation()
						+ "-"
						+ anioActual
						+ "-"
						+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
			}
			return codigo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String htmlFacilitadores(ProyectoLicenciaCoa proyecto) {
		List<FacilitadorPPC> listaFacilitadores = new ArrayList<FacilitadorPPC>();
		listaFacilitadores = facilitadorPPCFacade.allFacilitadores(proyecto);
		String htmlFacilitadores = "";
		for (FacilitadorPPC facilitador : listaFacilitadores) {
			String nombre = facilitador.getUsuario().getPersona().getNombre();
			htmlFacilitadores += (htmlFacilitadores == "") ? nombre : ", " + nombre;
		}
		
		return htmlFacilitadores;
	}
	
	public String htmlFases(ProyectoLicenciaCoa proyecto) {
		InformacionProyectoEia informacionProyectoEia = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
		String fasesProyecto = "";
		List<ProyectoFasesEiaCoa> listaFasesProyecto = proyectoFasesCoaFacade.obtenerInformacionProyectoEIAPorProyecto(informacionProyectoEia);
		if(listaFasesProyecto.size() > 0) {
			for (ProyectoFasesEiaCoa objFasesPro : listaFasesProyecto) {
				fasesProyecto = (fasesProyecto == null) ? objFasesPro.getFaseSector().getNombre() : fasesProyecto + "<br>" + objFasesPro.getFaseSector().getNombre();
			}
		} else {
			fasesProyecto = "N/A";
		}
		
		return fasesProyecto;
	}
	
	public List<String> getFechasTarea(String nombreTarea, Long idProceso, Integer respuesta, Integer tipo) {
		List<Date> fecha = null;
		if(tipo == 1)
			fecha = bandejaFacade.getFechasTarea(nombreTarea, idProceso);
		else 
			fecha = bandejaFacade.getFechasTareaNombre(nombreTarea, idProceso);
		
		List<String> fechasTarea = new ArrayList<>(); 
		
		if(fecha.size() > 0) {
		
			switch (respuesta) {
			case 1:
				SimpleDateFormat fechaFormatS = new SimpleDateFormat("dd/MM/yyy");
				String fechaInicio = fechaFormatS.format(fecha.get(0));
				String fechaFin = (fecha.get(1) == null) ? null : fechaFormatS.format(fecha.get(1));
	
				fechasTarea.add(fechaInicio);
				fechasTarea.add(fechaFin);
				break;
			case 2:
				SimpleDateFormat fechaFormatM = new SimpleDateFormat("dd/MM/yyy HH:mm");
				String fechaInicioM = fechaFormatM.format(fecha.get(0));
				String fechaFinM = (fecha.get(1) == null) ? null : fechaFormatM.format(fecha.get(1));
	
				fechasTarea.add(fechaInicioM);
				fechasTarea.add(fechaFinM);
				break;
			case 3:
				DateFormat fechaFormat =  DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
				String fechaInicioL = fechaFormat.format(fecha.get(0));
				String fechaFinL = (fecha.get(1) == null) ? null :  fechaFormat.format(fecha.get(1));
	
				fechasTarea.add(fechaInicioL);
				fechasTarea.add(fechaFinL);
				break;
	
			default:
				break;
			}
		}
		
		return fechasTarea;
	}
	
}
