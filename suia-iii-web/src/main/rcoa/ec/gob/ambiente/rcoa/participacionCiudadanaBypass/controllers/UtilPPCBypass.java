package ec.gob.ambiente.rcoa.participacionCiudadanaBypass.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ProyectoFasesCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.FacilitadorPPCFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.facilitador.facade.FacilitadorFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@Stateful
public class UtilPPCBypass {
	
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
		
	public String generarCodigoOficio(Area area) {
		String anioActual=secuenciasFacade.getCurrentYear();		
		String nombreSecuencia="MAAE-SUIA-"+anioActual+"_"+area.getAreaAbbreviation();
		String codigo = Constantes.SIGLAS_INSTITUCION + "-SUIA-";
		try {
			if (area.getTipoArea().getId() == ID_TIPO_AREA_OFICINA)
				area = area.getArea();
			
			codigo += area.getAreaAbbreviation()
					+ "-"
					+ anioActual
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
			
			return codigo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public List<String> facilitadoresBypass(Integer numeroFacilitadores, ProyectoLicenciaCoa proyecto)
	{
		List<Usuario> confirmado = new ArrayList<Usuario>();
        List<Usuario> rechazado = new ArrayList<Usuario>();
        List<String> lista = new ArrayList<String>();
		///selecciono los facilitadores
        confirmado=facilitadorPPCFacade.facilitadoresConfirmado(proyecto);
        rechazado=facilitadorPPCFacade.facilitadoresRezhazado(proyecto);
        
        //se excluyen tambien los q estan asignados y no confirman
        List<Usuario> facilitadoresAsignados = facilitadorPPCFacade.usuariosFacilitadoresAsignadosPendientesAceptacion(proyecto);
        confirmado.addAll(facilitadoresAsignados);
        
        List<Usuario> facilitadores = facilitadorFacade.seleccionarFacilitadoresBypass(numeroFacilitadores,confirmado,rechazado,proyecto.getAreaResponsable().getAreaName());
        if (facilitadores.size() == numeroFacilitadores) {            
            for (Usuario usuario : facilitadores) {
            	lista.add(usuario.getNombre());
            }
        }	
        
		return lista;
	}
	
	public List<Usuario> getUsuariosfacilitadoresBypass(Integer numeroFacilitadores, ProyectoLicenciaCoa proyecto)
	{
		List<Usuario> confirmado = new ArrayList<Usuario>();
        List<Usuario> rechazado = new ArrayList<Usuario>();
        List<Usuario> lista = new ArrayList<Usuario>();
		///selecciono los facilitadores
        confirmado=facilitadorPPCFacade.facilitadoresConfirmado(proyecto);
        rechazado=facilitadorPPCFacade.facilitadoresRezhazado(proyecto);
        List<Usuario> facilitadores = facilitadorFacade.seleccionarFacilitadoresBypass(numeroFacilitadores,confirmado,rechazado,proyecto.getAreaResponsable().getAreaName());
        if (facilitadores.size() == numeroFacilitadores) {            
            return facilitadores;
        }	
        
		return lista;
	}
	
}
