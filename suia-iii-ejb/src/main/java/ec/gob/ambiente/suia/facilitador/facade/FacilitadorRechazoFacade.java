package ec.gob.ambiente.suia.facilitador.facade;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import net.sf.jasperreports.compilers.JavaScriptEvaluatorScope.JSField;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.autoridadambiental.service.AutoridadAmbientalFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FacilitadorProyectoLog;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Holiday;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * 
 * @author cristina.flores
 *
 */

@Stateless
public class FacilitadorRechazoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private AutoridadAmbientalFacade autoridadAmbientalFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private FacilitadorProyectoLogFacade facilitadorProyectoLogFacade;
	@EJB 
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private ContactoFacade contactoFacade;
	//Cambio para tomar en cuenta feriados
	@EJB
	private FeriadosFacade feriadosFacade;

	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
		
	//Método para identificar proyectos en tareas para que acepte el facilitador
	@SuppressWarnings("unchecked")
	public void obtenerTareasFacilitador(){
		try {
			Usuario usuarioEnvio = new Usuario();
			usuarioEnvio.setNombrePersona(Constantes.SIGLAS_SITEAA);
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%recibir_informacion%'' and v.variableid = ''tramite'' "
					+ "and EXTRACT(DAY FROM NOW() - t.activationtime)>=3 "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') and t.actualowner_id is not null and "
					+ "t.createdby_id is not null and t.formname is not null and t.processid = ''suia.participacion-social'' ') "
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
				
				//metodo para validar fechas hábiles, (solo incluyen fines de semana)
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

				Date fecha = sdf.parse(codigoProyecto[4]); 
				
				Date fechaRechazo = fechaFinal(fecha, 3);
				Date fechaActual = new Date();
				
				ProyectoLicenciamientoAmbiental proyecto= new ProyectoLicenciamientoAmbiental();    			
    			proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(codigoProyecto[3]);
    			
    			SimpleDateFormat sdfaux = new SimpleDateFormat("yyyy-MM-dd");
    			
    	    	Date fechabloqueo = sdfaux.parse(Constantes.getFechaBloqueoRegistroFisico());    	    	
    		    Date fechaproyecto=sdf.parse(proyecto.getFechaRegistro().toString());
    		    		
    		    if (fechaproyecto.before(fechabloqueo)){
    		    	if(fechaRechazo.before(fechaActual) || fechaRechazo.equals(fechaActual)){
    					Usuario usuario = usuarioFacade.buscarUsuario(codigoProyecto[1]);				
    					
    					Map<String, Object> params = new HashMap<String, Object>();

    	                params.put("aceptarInformacion", false);
    	                
    	                procesoFacade.modificarVariablesProceso(usuario, Long.parseLong(codigoProyecto[2]), params);

    	                Map<String, Object> data = new ConcurrentHashMap<String, Object>();
    	                procesoFacade.aprobarTarea(usuario, Long.parseLong(codigoProyecto[0]), Long.parseLong(codigoProyecto[2]), data);
    	                
    	                
    	    			
    	    			if(proyecto != null){
    	    				FacilitadorProyectoLog facilitadorProyectoLog = new FacilitadorProyectoLog();
    		                facilitadorProyectoLog.setUsuario(usuario);
    		                facilitadorProyectoLog.setFechaNegacion(new Date());
    		                facilitadorProyectoLog.setResetarTarea(1);
    		                facilitadorProyectoLog.setProyecto(proyecto);
    		                facilitadorProyectoLog.setObservacion("Tarea no atendida en 3 dias.");
    		                facilitadorProyectoLogFacade.guardar(facilitadorProyectoLog);
    						
    		                //mails
    		                Usuario usuarioDirector = autoridadAmbientalFacade.getUsuarioDirectorPrevencionContaminacionAmbiental();
//    		            	String nombreDirector = usuarioDirector.getPersona().getNombre();
    		            	
    		            	List<Contacto> listaContactosDirector = contactoFacade.buscarPorPersona(usuarioDirector.getPersona());
    		    			String emailDirector = "";
    		    			for(Contacto contacto : listaContactosDirector){
    		    				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
    		    					emailDirector = contacto.getValor();
    		    					break;
    		    				}            						
    		    			}                    
    		    			       			  
    		    			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionNoAceptacionFacilitadorPorTiempo", new Object[]{});
    		    			String mensajeTecnico = mensaje;
    						mensaje = mensaje.replace("nombre_usuario", usuarioDirector.getPersona().getNombre());
    						mensaje = mensaje.replace("nombre_facilitador", usuario.getPersona().getNombre());
    						mensaje = mensaje.replace("numero_tramite", codigoProyecto[3]);
    		                
    		                NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
    		    			mail_a.sendEmailInformacionProponente(emailDirector, "", mensaje, "Notificación Rechazo de Facilitadores", proyecto.getCodigo(), usuarioDirector, usuarioEnvio);  
    		    			
    		            	Persona tecnico = autoridadAmbientalFacade.getTecnicoNotificacionesPPS();
    		            	
    		            	List<Contacto> listaContactoTecnico = contactoFacade.buscarPorPersona(tecnico);
    		            	String emailTecnico = "";
    		            	for(Contacto contacto : listaContactoTecnico){
    		            		if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
    		            			emailTecnico = contacto.getValor();
    		            			break;
    		            		}
    		            	}
    		            	//busco el usuario del tecnico
    		            	Usuario usuarioTecnico = usuarioFacade.buscarUsuario(tecnico.getPin());
    		            	
    		            	mensajeTecnico = mensajeTecnico.replace("nombre_usuario", tecnico.getNombre());
    		            	mensajeTecnico = mensajeTecnico.replace("nombre_facilitador", usuario.getPersona().getNombre());
    		            	mensajeTecnico = mensajeTecnico.replace("numero_tramite", codigoProyecto[3]);
    						
    		            	mail_a.sendEmailInformacionProponente(emailTecnico, "", mensajeTecnico, "Notificación Rechazo de Facilitadores", proyecto.getCodigo(), usuarioTecnico, usuarioEnvio);
    	    			}	                
    				} 
    			}				            
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Método para calcular días laborables, no toma en cuenta sábados y domingos
	 * @param fechaInicial
	 * @param diasRequisitos
	 * @return
	 */
	private Date fechaFinal(Date fechaInicial, int diasRequisitos){		
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date fechaFinal = new Date();		
			Calendar fechaPrueba = Calendar.getInstance();
			fechaPrueba.setTime(fechaInicial);	
		
			int i = 0;
			while(i < diasRequisitos){
				fechaPrueba.add(Calendar.DATE, 1);
				
				Integer anio = fechaPrueba.get(Calendar.YEAR);
				Integer mes = fechaPrueba.get(Calendar.MONTH) + 1;
				Integer dia = fechaPrueba.get(Calendar.DATE);
			
				String fechaFeriado = anio.toString() + "-" + mes.toString() + "-"+dia.toString();
				Date fechaComparar = format.parse(fechaFeriado);
			
				List<Holiday> listaFeriados = feriadosFacade.listarFeriadosNacionalesPorRangoFechas(fechaComparar, fechaComparar);
				
				if(fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaPrueba.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){
					if(listaFeriados == null)
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
