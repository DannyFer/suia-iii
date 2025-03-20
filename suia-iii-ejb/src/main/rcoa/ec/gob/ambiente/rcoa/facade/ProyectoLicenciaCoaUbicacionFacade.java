package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;

@Stateless
public class ProyectoLicenciaCoaUbicacionFacade {

	@EJB
    private CrudServiceBean crudServiceBean;
	
	public void guardar(ProyectoLicenciaCoaUbicacion obj) {        
        	crudServiceBean.saveOrUpdate(obj);        
    }
	
	
	public List<UbicacionesGeografica> ubicacionesGeograficas(ProyectoLicenciaCoa proyecto) throws Exception {
		List<ProyectoLicenciaCoaUbicacion> ubicaciones = new ArrayList<ProyectoLicenciaCoaUbicacion>();
		
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From ProyectoLicenciaCoaUbicacion s "
				+ " where  proyectoLicenciaCoa=:proyecto and s.estado=true and s.nroActualizacion = 0");
		sql.setParameter("proyecto", proyecto);
		ubicaciones=sql.getResultList();
		List<UbicacionesGeografica> ubicacionesGeograficas = new ArrayList<UbicacionesGeografica>();
		for (ProyectoLicenciaCoaUbicacion proyectoUbicacionGeografica : ubicaciones) {
			ubicacionesGeograficas.add(proyectoUbicacionGeografica.getUbicacionesGeografica());
		}		
		return ubicacionesGeograficas;
	}	
	
	@SuppressWarnings("unchecked")
	public List<UbicacionesGeografica> buscarPorProyecto(ProyectoLicenciaCoa proyecto){
		
		List<UbicacionesGeografica> lista = new ArrayList<UbicacionesGeografica>();
		try {
			
			Query sql = crudServiceBean.getEntityManager().createQuery("Select p.ubicacionesGeografica from ProyectoLicenciaCoaUbicacion p where p.proyectoLicenciaCoa = :proyecto and p.estado = true and p.nroActualizacion = 0 order by p.id");
			sql.setParameter("proyecto", proyecto);
			
			if(sql.getResultList().size() > 0){
				lista = sql.getResultList();
			}
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return lista;		
	}
	
	public ProyectoLicenciaCoaUbicacion ubicacionPrincipal(ProyectoLicenciaCoa proyecto){	
		Query sql = crudServiceBean.getEntityManager().createQuery("Select p from ProyectoLicenciaCoaUbicacion p where p.proyectoLicenciaCoa = :proyecto and p.primario=true and p.estado = true and p.nroActualizacion = 0");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size() > 0){
			return (ProyectoLicenciaCoaUbicacion) sql.getResultList().get(0);
		}
		
		return null;
	}
	
	public void eliminar(ProyectoLicenciaCoa proyecto, Integer nroActualizacion){		
		Query sqlUpdate = crudServiceBean.getEntityManager().createQuery("update ProyectoLicenciaCoaUbicacion p set p.estado=false where p.proyectoLicenciaCoa.id=:id and p.nroActualizacion =:nroActualizacion");
		sqlUpdate.setParameter("id", proyecto.getId());
		sqlUpdate.setParameter("nroActualizacion", nroActualizacion);
		sqlUpdate.executeUpdate();		
	}
	
	public Integer buscarNroUltimaActualizacion(ProyectoLicenciaCoa proyecto){	
		Query sql = crudServiceBean.getEntityManager().createQuery("Select max(nroActualizacion) from ProyectoLicenciaCoaUbicacion p "
				+ "where p.proyectoLicenciaCoa = :proyecto and p.estado = true");
		sql.setParameter("proyecto", proyecto);
		if(sql.getResultList().size() > 0){
			return (Integer) sql.getResultList().get(0);
		}
		
		return null;
	}
	
	public List<String> getInfoUbicacionProyecto(ProyectoLicenciaCoa proyecto, Integer tipoRetorno) {
		List<UbicacionesGeografica> ubicaciones = buscarPorProyecto(proyecto);
		List<String> resultado = new ArrayList<>();

		switch (tipoRetorno) {
		case 1: //tabla
			String ubicacionCompleta = "";
		
	        ubicacionCompleta = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">";
			ubicacionCompleta += "<tr><th>Provincia</th><th>Cantón</th><th>Parroquia</th></tr>";
			for (UbicacionesGeografica ubicacionActual : ubicaciones) {
				ubicacionCompleta += "<tr>"
						+ "<td>" + ubicacionActual.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td>"
						+ "<td>" + ubicacionActual.getUbicacionesGeografica().getNombre() + "</td>"
						+ "<td>" + ubicacionActual.getNombre() + "</td></tr>";
			}

			ubicacionCompleta += "</table>";
			resultado.add(ubicacionCompleta);
			break;
		case 2:
			String provincias = null;
	        String canton = null;
	        String parroquia = null;

	        if(ubicaciones.size()== 1) {
	        	provincias = ubicaciones.get(0).getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
		        canton = ubicaciones.get(0).getUbicacionesGeografica().getNombre();
		        parroquia = ubicaciones.get(0).getNombre();
	        } else {
	        	Set<UbicacionesGeografica> ubicacionesProvincia = new LinkedHashSet<UbicacionesGeografica>();
				Set<UbicacionesGeografica> ubicacionesCanton = new LinkedHashSet<UbicacionesGeografica>();
				Set<UbicacionesGeografica> ubicacionesParroquia = new LinkedHashSet<UbicacionesGeografica>();

		        for (UbicacionesGeografica ubicacionesGeografica : ubicaciones) {
		            ubicacionesProvincia.add(ubicacionesGeografica.getUbicacionesGeografica().getUbicacionesGeografica());
		            ubicacionesCanton.add(ubicacionesGeografica.getUbicacionesGeografica());
		            ubicacionesParroquia.add(ubicacionesGeografica);
		        }
		        provincias = ubicacionesProvincia.toString().replace("[", "").replace("]", "");
		        canton = ubicacionesCanton.toString().replace("[", "").replace("]", "");
		        parroquia = ubicacionesParroquia.toString().replace("[", "").replace("]", "");
	        }

			resultado.add(provincias);
			resultado.add(canton);
			resultado.add(parroquia);

			break;
		default:
			break;
		}
		
		return resultado;
	}

}
