package ec.gob.ambiente.prevencion.certificado.ambiental.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.certificado.ambiental.controllers.CertificadoAmbientalController;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class CertificadoAmbientalTeminacionTareaFirmaController {
	
	
	private static final Logger LOG = Logger.getLogger(CertificadoAmbientalTeminacionTareaFirmaController.class);
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ConexionBpms conexionBpms;
	
	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
	
	
	public void obtenerTareasFirma(){
		try {
			
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, v.value, t.actualowner_id, t.processinstanceid  "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%firmarElectronicamenteCertificado%'' and v.variableid = ''tramite'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.formname is not null and t.processid = ''rcoa.CertificadoAmbiental'' and t.actualowner_id is not null')  "
					+ "as (id varchar, tramite varchar, usuario varchar, processinstanceid varchar) "
					+ "order by 1 limit 50";	
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1], (String) row[2] ,(String) row[3]});
				}
			}
			
			for (String[] codigoProyecto : listaCodigosProyectos) {
				
				Usuario usuarioAutoridad = usuarioFacade.buscarUsuario(codigoProyecto[2]);	
				
				String idTask = codigoProyecto[0];
				String codigo = codigoProyecto[1];
				
				if(usuarioAutoridad != null && usuarioAutoridad.getId() != null){
					ProyectoLicenciaCoaFacade proyectoLicenciCoaFacade = (ProyectoLicenciaCoaFacade) BeanLocator.getInstance(ProyectoLicenciaCoaFacade.class);
					
					ProyectoLicenciaCoa proyecto = proyectoLicenciCoaFacade.buscarProyecto(codigo);
					
					try {
						
						if(!proyecto.getAreaResponsable().getAreaAbbreviation().equals("DRA") && !proyecto.getAreaResponsable().getAreaAbbreviation().equals("SCA")){
							CertificadoAmbientalController certificadoAmbientalController = (CertificadoAmbientalController) BeanLocator.getInstance(CertificadoAmbientalController.class);
						
						procesoFacade.aprobarTarea(usuarioAutoridad, Long.valueOf(idTask), Long.parseLong(codigoProyecto[3]), null);	
						
						certificadoAmbientalController.enviarFicha(proyecto.getId(), Long.parseLong(codigoProyecto[3]));		
						
						System.out.println("**************************************************************************************************");
						System.out.println("Codigo Proyecto " + codigo + " Operador" +  proyecto.getUsuario().getNombre() + " autoridad: " + usuarioAutoridad.getNombre());
						System.out.println("**************************************************************************************************");
						
						
						String sqlDescarga="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, v.value, t.actualowner_id  "
								+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
								+ "where t.formname like ''%descargarCertificadoAmbiental%'' and v.variableid = ''tramite'' and v.value = ''" + proyecto.getCodigoUnicoAmbiental() +"'' "
								+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
								+ "and t.formname is not null and t.processid = ''rcoa.CertificadoAmbiental'' ') "
								+ "as (id varchar, tramite varchar, usuario varchar) "
								+ "order by 1";	
						
						Query queryFirma = crudServiceBean.getEntityManager().createNativeQuery(sqlDescarga);
						List<Object>  resultListDescarga = new ArrayList<Object>();
						resultListDescarga=queryFirma.getResultList();
						List<String[]>listaCodigosProyectosDescarga= new ArrayList<String[]>();		
						if (resultListDescarga.size() > 0) {
							for (Object a : resultListDescarga) {
								Object[] row = (Object[]) a;
								listaCodigosProyectosDescarga.add(new String[] { (String) row[0],(String) row[1], (String) row[2]});
							}
						}
						
						for (String[] codigoProyectoDesc : listaCodigosProyectosDescarga) {
							
							Usuario usuarioOperador = usuarioFacade.buscarUsuario(codigoProyectoDesc[2]);	
							
							String idTaskDescarga = codigoProyectoDesc[0];
							procesoFacade.aprobarTarea(usuarioOperador, Long.valueOf(idTaskDescarga), Long.parseLong(codigoProyecto[3]), null);	
							break;
						}
						
					}				
						
					} catch (Exception e) {
						LOG.error("Error al pasar la tarea codigo:" + codigo, e);							
						try {										
							
							if(conexionBpms == null){
								conexionBpms = (ConexionBpms) BeanLocator.getInstance(ConexionBpms.class);
							}								
							procesoFacade.reasignarTarea(usuarioAutoridad, 
									Long.valueOf(idTask),
									usuarioAutoridad.getNombre(), 
									usuarioAutoridad.getNombre(),
									conexionBpms.deploymentId(Long.valueOf(idTask), "S"));
							conexionBpms.reasigment(usuarioAutoridad,Long.valueOf(idTask), usuarioAutoridad.getNombre(), usuarioAutoridad.getNombre(), Long.parseLong(codigoProyecto[3]));
							
						} catch (JbpmException ex) {
							System.out.println("Error en la tarea suia-iii:::"+Long.valueOf(idTask));
						}							
					}										
				}				
			}			
		} catch (Exception e) {
			LOG.error("Error al ejecutar la generación de certificado ambientales", e);
		}
	}

}
