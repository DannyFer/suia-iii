package ec.gob.ambiente.rcoa.facade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.utils.DiasHabilesUtil;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless 
public class ArchivarDiagnosticoAmbientalFacade {
	
	
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
	private DiasHabilesUtil diasHabilesUtil;
	

	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
	
	@SuppressWarnings("unchecked")
	public void archivarPorNoInicioRegularizacion(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%recibirOficioAprobacionDiagnostico%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.actualowner_id is not null and "
					+ "t.formname is not null and t.processid = ''rcoa.RegistroPreliminar'' ') "
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

				String tramite = codigoProyecto[3];
				
				ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
				
				Date fechaActual = new Date();
				
				if ( proyecto.getFechaInicioRegularizacionAmbiental() != null &&
						(proyecto.getFechaInicioRegularizacionAmbiental().before(fechaActual) || 
						proyecto.getFechaInicioRegularizacionAmbiental().equals(fechaActual)) ){
					Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
					
					Map<String, Object> params = new HashMap<String, Object>();

	                params.put("iniciaProceso", false);
	                
	                procesoFacade.modificarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]), params);

	                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), null);						
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void finalizarCorreccionDiagnostico(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "left join (select processinstanceid from variableinstancelog where variableid = ''notificacionDiagnostico'') as t3 on t3.processinstanceid = t.processinstanceid  "
					+ "where t.formname like ''%cargarPlanAccion%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') and t.actualowner_id is not null and "
					+ "t.formname is not null and t.processid = ''rcoa.RegistroPreliminar'' "
					+ "and t3.processinstanceid is null ') "
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
				
				CatalogoGeneralCoa catalogo = catalogoCoaFacade.obtenerCatalogoPorCodigo("dias.subsanacion.diagnostico.ambiental");
				
				Integer dias = Integer.valueOf(catalogo.getValor());
				
				Date fechaFinalCorreccion = diasHabilesUtil.recuperarFechaFinal(fecha, dias);
				Date fechaActual = new Date();
				
				if(fechaFinalCorreccion.before(fechaActual) || fechaFinalCorreccion.equals(fechaActual)){
					
					Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
					
					Map<String, Object> params = new HashMap<String, Object>();

	                params.put("cumpleTiempoSubsanacion", false);
	                
	                procesoFacade.modificarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]), params);

	                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), null);
				}	                
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("unchecked")
	public void finalizarRevisionProyectocNuevos() {
		try {
			//proyectos nuevos que ingresaron a revision de IIP y no tienen observaciones del técnico
			String sql = "select t.* from coa_mae.project_licencing_coa p "
					+ "inner join (SELECT * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
					+ "'select t.processinstanceid, t.id, actualowner_id, v.value, formname from task t "
							+ "inner join processinstancelog p on p.processinstanceid = t.processinstanceid "
							+ "inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
							+ "where formname in (''revisarInformacionRegistroPreliminar'') "
							+ "and t.status != ''Completed'' "
							+ "and p.status != 4 "
							+ "and v.variableid = ''tramite'' and p.processversion = ''1.0'' ' "
							+ ") AS t1 (idProceso CHARACTER VARYING (255), idTarea CHARACTER VARYING (255), "
							+ "usuario CHARACTER VARYING (255), tramite CHARACTER VARYING (255), tarea CHARACTER VARYING (255) ) "
							+ ") as t on t.tramite = p.prco_cua "
							+ "left join coa_mae.form_comments_coa f on f.foco_id_class = p.prco_id "
							+ "where prco_project_type = 1 and prco_categorizacion != 1 and prco_status = true and foco_id is null "
							+ "order by idProceso ";
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object[]> listProcesosActivos = (List<Object[]>) query.getResultList();
			if (listProcesosActivos.size() > 0) {
				for (int i = 0; i < listProcesosActivos.size(); i++) {
					Object[] dataProject = (Object[]) listProcesosActivos.get(i);
					
					Long idProceso = Long.parseLong(dataProject[0].toString());
					
					Usuario usuarioTarea = usuarioFacade.buscarUsuario(dataProject[2].toString());
					
					String fechaAvance = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS").format(new Date());
					
					System.out.println("======================================");
					System.out.println("Proceso: " + idProceso);
					
					List<TaskSummary> listaTareas=procesoFacade.getTaskReservedInProgress(usuarioTarea, idProceso);
					while(listaTareas != null && listaTareas.size() > 0) {				
						for (TaskSummary tarea : listaTareas) {
							usuarioTarea = usuarioFacade.buscarUsuario(tarea.getActualOwner().getId());
							
							
							if(tarea.getName().equals("Revisar informacion")) {
								Map<String, Object> parametros = new HashMap<>();
								parametros.put("observacionesRevisionDiagnostico", false);
								parametros.put("avanceAutomaticoTarea", fechaAvance);
								
								procesoFacade.modificarVariablesProceso(usuarioTarea, idProceso, parametros);
							} 
							
							System.out.println("Tarea: " + tarea.getName());
							try {
								procesoFacade.aprobarTarea(usuarioTarea, tarea.getId(), idProceso, null);
								
								System.out.println("Tarea: " + tarea.getId() + " APROBADA");
								
								listaTareas = procesoFacade.getTaskReservedInProgress(usuarioTarea, idProceso);
								
							} catch (Exception e) {
								e.printStackTrace();
								listaTareas = new ArrayList<TaskSummary>() ;
							}
						}
					}
					System.out.println("======================================");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public String deploymentId(long taskid, String busqueda){
    	String result="";
    	try {
    		String dblink="";
    		if(busqueda.equals("S"))//S = suia-iii
    			dblink=dblinkBpmsSuiaiii;
    		
    		Query sqlDeploymentId = crudServiceBean.getEntityManager().createNativeQuery(
    				"select * from  dblink('"+dblink+"','select deploymentid from task where id="+taskid+"') as (deploymentid varchar)"); 
    		List<Object>  resultListV = new ArrayList<Object>();
    		resultListV=sqlDeploymentId.getResultList();    		
    		if (resultListV!=null) {		    	 
    			for(Object a: resultListV)
    			{
    				result= a.toString();
    			}
    		}
    		return result;    		
    	}
    	catch (Exception e) {
    		return result;
    	}  
	}
}
