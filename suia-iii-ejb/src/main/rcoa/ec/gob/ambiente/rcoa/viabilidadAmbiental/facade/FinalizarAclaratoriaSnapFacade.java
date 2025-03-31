package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Holiday;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless 
public class FinalizarAclaratoriaSnapFacade {
	
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;	
	@EJB
	private FeriadosFacade feriadosFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;


	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
	
	@SuppressWarnings("unchecked")
	public void finalizarTareaIngresoAclaratoria(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"',"
					+ "'select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime from task t "
					+ "inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%ingresarInformacionAclaratoria%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') and t.actualowner_id is not null and "
					+ "t.formname is not null and t.processid = ''rcoa.EmisionViabilidadAmbientalSnap'' ') "
					+ "as (id varchar, usuario varchar, processinstanceid varchar, tramite varchar, fecha varchar) "
					+ "order by 1";			
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1], (String) row[2], (String) row[3], (String) row[4] });
					
				}
			}
			
			for (String[] codigoProyecto : listaCodigosProyectos) {
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

				Date fecha = sdf.parse(codigoProyecto[4]); 
				
				CatalogoGeneralCoa catalogo = catalogoCoaFacade.obtenerCatalogoPorCodigo("dias.ingresar.aclaratoria.viabilidad.snap");
				Integer dias = Integer.valueOf(catalogo.getValor());;
				
				Date fechaFinalizarTarea = fechaFinal(fecha, dias);
				Date fechaActual = new Date();
				
				if(fechaFinalizarTarea.before(fechaActual) || fechaFinalizarTarea.equals(fechaActual)){
					try {
						Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
						
						Map<String, Object> params = new HashMap<String, Object>();
	
		                params.put("ingresaAclaratoria", false);
		                
		                Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]));
		                Boolean esSnapMae = Boolean.parseBoolean(variables.get("esAdministracionDirecta").toString());
		                
		                String tipoUsuario = (esSnapMae) ? "responsableAreaProtegida" : "responsableAreasPC";
						String jefePrincipalBpm = variables.get(tipoUsuario).toString();
						
						ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(codigoProyecto[3]);
						ViabilidadCoa viabilidadProyecto = viabilidadCoaFacade.getViabilidadSnapPorProyectoTipo(proyecto.getId(), esSnapMae);
						
						AreasSnapProvincia areaSnap = UtilViabilidadSnap.getInfoAreaSnap(viabilidadProyecto.getAreaSnap().getCodigoSnap(), viabilidadProyecto.getAreaSnap().getZona());
						Usuario tecnicoResponsable = (areaSnap != null) ? areaSnap.getTecnicoResponsable() : null;
						
						if(areaSnap != null && tecnicoResponsable != null) {
							if(!jefePrincipalBpm.equals(tecnicoResponsable.getNombre())) {
								params.put(tipoUsuario, tecnicoResponsable.getNombre());
							}
						}
		                
		                procesoFacade.modificarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]), params);

		                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), null);
		                
	                } catch (Exception e) {
						e.printStackTrace();
					}
	                
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Método para calcular días laborables, no toma en cuenta sábados y
	 * domingos
	 * 
	 * @param fechaInicial
	 * @param diasRequisitos
	 * @return
	 */
	private Date fechaFinal(Date fechaInicial, int diasRequisitos) {
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date fechaFinal = new Date();
			Calendar fechaPrueba = Calendar.getInstance();
			fechaPrueba.setTime(fechaInicial);

			int i = 0;
			while (i < diasRequisitos) {
				fechaPrueba.add(Calendar.DATE, 1);

				Integer anio = fechaPrueba.get(Calendar.YEAR);
				Integer mes = fechaPrueba.get(Calendar.MONTH) + 1;
				Integer dia = fechaPrueba.get(Calendar.DATE);

				String fechaFeriado = anio.toString() + "-" + mes.toString() + "-" + dia.toString();
				Date fechaComparar = format.parse(fechaFeriado);

				List<Holiday> listaFeriados = feriadosFacade.listarFeriadosNacionalesPorRangoFechas(fechaComparar, fechaComparar);

				if (fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
						&& fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
					if (listaFeriados == null)
						i++;
				}
			}

			fechaFinal = fechaPrueba.getTime();
			return fechaFinal;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
