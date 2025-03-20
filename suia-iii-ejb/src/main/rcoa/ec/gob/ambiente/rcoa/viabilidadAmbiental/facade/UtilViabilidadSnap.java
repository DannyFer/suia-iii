package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.administracion.facade.RolFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

public final class UtilViabilidadSnap {
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	public static String getFileNameEscaped(String file) {
		String result = file.replaceAll("Ñ", "N");
		result = result.replaceAll("Á", "A");
		result = result.replaceAll("É", "E");
		result = result.replaceAll("Í", "I");
		result = result.replaceAll("Ó", "O");
		result = result.replaceAll("Ú", "U");
		return result;
	}
	
	public static AreasSnapProvincia getInfoAreaSnap(String codigoCapa, String zona) {
		InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade = BeanLocator.getInstance(InterseccionViabilidadCoaFacade.class);
		AsignarTareaFacade asignarTareaFacade = BeanLocator.getInstance(AsignarTareaFacade.class);
		AreaFacade areaFacade = BeanLocator.getInstance(AreaFacade.class);
		UsuarioFacade usuarioFacade = BeanLocator.getInstance(UsuarioFacade.class);
		RolFacade rolFacade = BeanLocator.getInstance(RolFacade.class);
		
		List<AreasSnapProvincia> areasSnapZonas = interseccionViabilidadCoaFacade.getAreaSnapPorCodigoZona(codigoCapa, zona);
		
		Usuario tecnicoResponsable = null;
		AreasSnapProvincia areaSnap = areasSnapZonas.get(0);
		
		if(areaSnap.getTipoSubsitema().equals(1)) {
			//recuperar los usuarios responsables del área
			List<String> usuariosArea = new ArrayList<>();
			for (AreasSnapProvincia area : areasSnapZonas) {
				if(area.getUsuario() != null && !usuariosArea.contains(area.getUsuario().getNombre()))
					usuariosArea.add(area.getUsuario().getNombre());
			}
			
			if(usuariosArea.size() > 1) {
				//si existen dos usuarios asignados a la misma área y provincia. Se asigna el usuario con menor carga de trabajo
				List<Usuario> listaTecnicosResponsables = asignarTareaFacade
						.getCargaLaboralPorNombreUsuariosProceso(usuariosArea,
								Constantes.RCOA_PROCESO_VIABILIDAD_SNAP);
				if(listaTecnicosResponsables != null && listaTecnicosResponsables.size() >0)
					tecnicoResponsable = listaTecnicosResponsables.get(0);
			} else {
				tecnicoResponsable = areaSnap.getUsuario();
			}
			
			//verifico que el usuario de la matriz tenga el rol requerido
			String tipoRol = "role.va.snap.admin.area";
			String rolTecnico = Constantes.getRoleAreaName(tipoRol);
			if (!rolFacade.isUserInRole(tecnicoResponsable, rolTecnico)) {
				tecnicoResponsable = null;
			}
		} else {
			Area areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS);
			String tipoRol = "role.va.pc.tecnico.snap.delegada";
			
			String rolTecnico = Constantes.getRoleAreaName(tipoRol);
			
			//buscar usuarios por rol y area
			List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaTramite);
			if (listaUsuario == null || listaUsuario.size() == 0)
				return areaSnap;
			
			List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosProceso(listaUsuario, Constantes.RCOA_PROCESO_VIABILIDAD_SNAP);
			tecnicoResponsable = listaTecnicosResponsables.get(0);
		}
		
		if(areaSnap != null)
			areaSnap.setTecnicoResponsable(tecnicoResponsable);
		
		return areaSnap;
	}

}
