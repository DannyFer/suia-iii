package ec.gob.ambiente.rcoa.pagos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.UbicacionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.FacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.FacilitadorPPC;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.catalogos.facade.InstitucionFinancieraFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless 
public class FinalizarPagosRcoaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private InstitucionFinancieraFacade institucionFinancieraFacade;
	@EJB
	private TransaccionFinancieraFacade transaccionFinancieraFacade;
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private UbicacionDigitalizacionFacade ubicacionDigitalizacionFacade;
	@EJB
	private FacilitadorPPCFacade facilitadorPPCFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
	private Integer tipoPago;

	
	@SuppressWarnings("unchecked")
	public void obtenerPagosRealizados(){
		try {
			String sql="select reus_id, nut_project_code, sum(case nuts_id when 3 then nut_value else 0 end) totalPagado, sum(nut_value) total "
					+ "from payments.unique_transaction_number "
					+ " where nut_status  and (nut_used is false or nut_used is null) "
					+ " and bnf_date_pay is not null and (bnf_date_pay +  interval '15 minute') < now() "
					+ "group by reus_id, nut_project_code "
					+ "having sum(case nuts_id when 3 then nut_value else 0 end)  = sum(nut_value) ";				
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			if (resultList.size() > 0) {
				for (Object objNut : resultList) {
					Object[] row = (Object[]) objNut;
					Integer solicitudId = (Integer) row[0];
					String codigoProyecto = (String) row[1];
					// busco cual es la tarea de pago 
					obtenerTareasPagoRcoa(codigoProyecto, solicitudId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void obtenerTareasPagoRcoa(String codigoProyecto, Integer solicitudId){
		try {
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, t.actualowner_id, t.processinstanceid, v.value, t.activationtime, bt.taskname, t.processid, p.processname "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ " inner join bamtasksummary bt on t.id = bt.taskid "
					+ " inner join processinstancelog p on t.processinstanceid = p.processinstanceid "
					+ "where value = ''"+codigoProyecto+"'' and v.variableid = ''tramite'' "
					+ "and t.status in (''Reserved'') "
					+ "and t.actualowner_id is not null and "
					+ " bt.taskname is not null and (lower(bt.taskname) like ''%pago%'' or lower(bt.taskname) like ''%valor %'') and "
					+ "t.processid in( ''rcoa.CertificadoAmbiental'', ''rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales'', ''rcoa.RegistroAmbiental'', ''rcoa.RegistroAmbientalconPPC'', ''rcoa.ResolucionLicenciaAmbiental'', ''rcoa.ProcesoParticipacionCiudadanaBypass'', ''rcoa.DeclaracionSustanciasQuimicas'', ''rcoa.EstudioImpactoAmbientalNO'' ) ') "
					+ "as (id varchar, usuario varchar, processinstanceid varchar, tramite varchar, fecha varchar, nombretarea varchar, processid varchar, procesname varchar) "
					+ "order by 1";
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					TaskSummaryCustom tarea = new TaskSummaryCustom();
					tarea.setTaskId(Long.parseLong(row[0].toString()));
					Long processInstanceId = Long.parseLong(row[2].toString());
					tarea.setProcessInstanceId(processInstanceId);
					tarea.setTaskName((String) row[5]);
					tarea.setProcessId((String) row[6]);
					tarea.setProcessName((String) row[7]);
					Usuario usuario = usuarioFacade.buscarUsuario((String) row[1]);
					boolean correcto =false;
					List<NumeroUnicoTransaccional> listaNuts = numeroUnicoTransaccionalFacade.listBuscaNUTPorIdSolicitud(solicitudId);
					for (NumeroUnicoTransaccional objNut : listaNuts) {

						switch (tarea.getProcessId()) {
							case "rcoa.CertificadoAmbiental":
								tipoPago = 2;  // pago por coberturavegetal nativa
								break;
							case "rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales":
								tipoPago = 3;  // pago por RGD
								break;
							case "rcoa.DeclaracionSustanciasQuimicas":
								tipoPago = 4;  // pago por RSQ
								break;
							case "rcoa.RegistroAmbiental":
							case "rcoa.RegistroAmbientalconPPC":
								tipoPago = 1;  // pago por Registro ambiental
								ProyectoLicenciaCoa proyectoRCoa = proyectoLicenciaCoaFacade.buscarProyecto(codigoProyecto);
								// verifico si no es un enete acreditasdo para finalizar la tarea
								if (proyectoRCoa.getAreaResponsable().getTipoEnteAcreditado() != null) {
									if (proyectoRCoa.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
										if(objNut.getCuentas().getId().equals(4))
											tipoPago = 2;  // pago por coberturavegetal nativa
									}else{
										tipoPago = 2;  // pago por coberturavegetal nativa
									}
								}
								break;
							case "rcoa.ResolucionLicenciaAmbiental":
								tipoPago = 1; // pago de licencia ambiental
								ProyectoLicenciaCoa proyectoRCoa1 = proyectoLicenciaCoaFacade.buscarProyecto(codigoProyecto);
								if(objNut.getCuentas().getId().equals(4)){
									tipoPago = 2;  // pago por coberturavegetal nativa
								}else{
									if(proyectoRCoa1.getGeneraDesechos() && objNut.getNutValor().equals(180.0))
										tipoPago = 3;  // pago por RGD 
								}
							break;
							case "rcoa.ProcesoParticipacionCiudadanaBypass":
								tipoPago = 5;  // pago por servicios de facilitadores
								break;
						}
						correcto = registrarPago(objNut, tarea);
						if(!correcto){
							break;
						}
					}
					if(correcto){
						boolean pasoTarea = false;
						switch (tarea.getProcessId()) {
							case "rcoa.CertificadoAmbiental":
								pasoTarea = finalizar(tarea, usuario);
								break;
							case "rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales":
								pasoTarea = finalizarRGD(tarea, usuario);
								break;
							case "rcoa.DeclaracionSustanciasQuimicas":
								pasoTarea = finalizar(tarea, usuario);
								break;
							case "rcoa.RegistroAmbiental":
							case "rcoa.RegistroAmbientalconPPC":
								ProyectoLicenciaCoa proyectoRCoa = proyectoLicenciaCoaFacade.buscarProyecto(codigoProyecto);
								// verifico si no es un enete acreditasdo para finalizar la tarea
								if (proyectoRCoa.getAreaResponsable().getTipoEnteAcreditado() != null) {
									if (proyectoRCoa.getAreaResponsable().getTipoEnteAcreditado().equals("ZONAL")) {
										//verifico que no tenga pagos pendientes
										boolean faltaPago=false;
										List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoProyecto);
										if(listNUTXTramite!=null && listNUTXTramite.size()>0){
											for (NumeroUnicoTransaccional nut : listNUTXTramite) {
												//si esta caducado no lo tomo en cuenta
												if(nut.getEstadosNut().getId().equals(5))
													continue;
												//solo valido los nuts que corresponden a registro ambiental
												if(!nut.getNutDescripcion().toLowerCase().contains("registro ambiental") && !nut.getNutDescripcion().toLowerCase().contains("cobertura vegetal nativa"))
													continue;
												if(nut.getNutUsado() != null && nut.getNutUsado())
													faltaPago=true;
											}
										}
										if(!faltaPago)
											pasoTarea = finalizarRegistoAmbiental(tarea, usuario);
									}else{
										// si es ente solo registro el pago porcobertura vegetal
										pasoTarea=true;
									}
								}else{
									//verifico que no tenga pagos pendientes
									boolean faltaPago=false;
									List<NumeroUnicoTransaccional> listNUTXTramite=numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoProyecto);
									if(listNUTXTramite!=null && listNUTXTramite.size()>0){
										for (NumeroUnicoTransaccional nut : listNUTXTramite) {
											//si esta caducado no lo tomo en cuenta
											if(nut.getEstadosNut().getId().equals(5))
												continue;
											//solo valido los nuts que corresponden a registro ambiental
											if(!nut.getNutDescripcion().toLowerCase().contains("registro ambiental") && !nut.getNutDescripcion().toLowerCase().contains("cobertura vegetal nativa"))
												continue;	
											if(nut.getNutUsado() != null && nut.getNutUsado())
												faltaPago=true;
										}
									}
									if(!faltaPago)
										pasoTarea = finalizarRegistoAmbiental(tarea, usuario);
								}
								break;
							case "rcoa.ResolucionLicenciaAmbiental":
								// si es licencia solo registro el pago porcobertura vegetal							
								if (tarea.getTaskName().equals(Constantes.TAREA_PAGO_FLUJO1))
								{
									pasoTarea = finalizarResolucion(codigoProyecto, tarea, usuario);
								}
								else
								{
									pasoTarea=true;
								}
							break;
							case "rcoa.ProcesoParticipacionCiudadanaBypass":
								pasoTarea = finalizarParticipacion(tarea, usuario);
							break;
							default:
								break;
						}
						if(pasoTarea)
							finalizarNut(codigoProyecto, tarea.getProcessInstanceId());
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void finalizarNut(String codigoProyecto, Long processInstanceId){
		Query sqlUpdateTarea = crudServiceBean.getEntityManager().createNativeQuery(" update payments.unique_transaction_number "
				+ "set nut_used = true, nut_date_update = now() "
				+ "where nut_id in ( "
				+ " 		select a.nut_id from payments.unique_transaction_number a inner join payments.request_user b on a.reus_id = b.reus_id "
				+ "					inner join payments.documents c on b.reus_id = c.reus_id  "
				+ "		where a.nut_project_code = '"+codigoProyecto+"' and a.nut_status = true  and c.docu_process_instance_id = "+processInstanceId+" and b.reus_status = true "
				+ "		and a.nuts_id = 3 and a.nut_used is not true "
				+ " ) ");
		sqlUpdateTarea.executeUpdate();
	}
	
	public boolean registrarPago(NumeroUnicoTransaccional nutPrincipal, TaskSummaryCustom tarea ){
		List<TransaccionFinanciera> transaccionesFinancierasProyecto = new ArrayList<TransaccionFinanciera>();		
		ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(nutPrincipal.getNutCodigoProyecto());
		List<NumeroUnicoTransaccional> listaNuts = numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(nutPrincipal.getNutCodigoProyecto());
		for (NumeroUnicoTransaccional objNut : listaNuts) {
			//si esta caducado no lo tomo en cuenta
			if(objNut.getEstadosNut().getId().equals(5))
				continue;
			if(objNut.getEstadosNut().getId().equals(3) && (objNut.getNutUsado() == null || !objNut.getNutUsado())){
				// crea la transaccion financiera
				TransaccionFinanciera transaccionFinanciera = new TransaccionFinanciera();
				//busco la institucion financiera		
				List<InstitucionFinanciera> institucionesFinancieras = institucionFinancieraFacade.obtenerInstitucionFinancieraPorId(objNut.getSolicitudUsuario().getInstitucionFinanciera().getId());
				if(institucionesFinancieras != null && !institucionesFinancieras.isEmpty()){
					transaccionFinanciera.setInstitucionFinanciera(institucionesFinancieras.get(0));
				}
				transaccionFinanciera.setMontoTransaccion(objNut.getNutValor());
				transaccionFinanciera.setMontoPago(objNut.getNutValor());
				if(tarea.getProcessId().equals("rcoa.ResolucionLicenciaAmbiental"))
				{
					if(objNut.getNutDescripcion().contains("correspondiente a remoción de cobertura vegetal nativa"))
					{
						transaccionFinanciera.setTipoPago(2);
					} else if(objNut.getNutDescripcion().contains("correspondiente a RGDP"))
					{
						transaccionFinanciera.setTipoPago(3);
					}else if(objNut.getNutDescripcion().contains("costo del proyecto"))
					{
						transaccionFinanciera.setTipoPago(1);
					}
					else
					{
						transaccionFinanciera.setTipoPago(tipoPago);	
					}					
				}
				else
				{
					transaccionFinanciera.setTipoPago(tipoPago);	
				}				
				if(proyecto != null && proyecto.getId() != null){
					transaccionFinanciera.setProyectoRcoa(proyecto);
					if(proyecto.getCategorizacion() == 3 || proyecto.getCategorizacion() == 4)
						transaccionFinanciera.setMontoPago(objNut.getNutValor());
				}else{
					transaccionFinanciera.setIdentificadorMotivo(objNut.getNutCodigoProyecto());
				}
				transaccionFinanciera.setNumeroTransaccion(objNut.getBnfTramitNumber());
				transaccionesFinancierasProyecto.add(transaccionFinanciera);
			}
		}
	
		//guarado la transacion
		boolean pagoSatisfactorio=transaccionFinancieraFacade.realizarPago(nutPrincipal.getSolicitudUsuario().getValorTotal(), transaccionesFinancierasProyecto,nutPrincipal.getNutCodigoProyecto());
		if(pagoSatisfactorio){
			boolean faltaRegistrarTransaccion = true;
			//busco si ya existe transacciones guardadass
			List<TransaccionFinanciera> listaTransacciones = transaccionFinancieraFacade.cargarTransacciones(tarea.getProcessInstanceId(), tarea.getTaskId());
			if(listaTransacciones != null && listaTransacciones.size() > 0){
				if(transaccionesFinancierasProyecto.size() == 1){
					faltaRegistrarTransaccion = false;
				}else{
					List<TransaccionFinanciera> listaTransaccionesEliminar = new ArrayList<TransaccionFinanciera>();
					for (TransaccionFinanciera objTransaccionFinanciera2 : transaccionesFinancierasProyecto) {
						for (TransaccionFinanciera objTransaccionFinancieraAux : listaTransacciones) {
							if(objTransaccionFinanciera2.getNumeroTransaccion().equals(objTransaccionFinancieraAux.getNumeroTransaccion())){
								listaTransaccionesEliminar.add(objTransaccionFinanciera2);
								break;
							}
						}
					}
					//elimino las transacciones que ya fueron registradas para no volver a registrar
					if(listaTransaccionesEliminar.size() > 0){
						transaccionesFinancierasProyecto.removeAll(listaTransaccionesEliminar);
					}
				}
			}
			
			// actualizo ael nut a usado
			//nut.setNutUsado(true);
			//crudServiceBean.saveOrUpdate(nut);
			//guarado la transacion log si todavia no ha sido registrado
			if(faltaRegistrarTransaccion && transaccionesFinancierasProyecto.size() > 0)
            transaccionFinancieraFacade.guardarTransacciones(transaccionesFinancierasProyecto,
            		tarea.getTaskId(), tarea.getTaskName(),	tarea.getProcessInstanceId(), tarea.getProcessId(),
            		tarea.getProcessName());
		}
		return pagoSatisfactorio;
	}
	
	private boolean finalizarRGD(TaskSummaryCustom tarea, Usuario usuario){
		try {
			String rolDirector = "";
			Usuario usuarioAutoridad;
			String autoridad = "";
			Area areaTramite = new Area();
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(usuario, tarea.getProcessInstanceId());
			String tipoPermisoRGD =(String)variables.get(Constantes.VARIABLE_TIPO_RGD);
			String tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_REP)){
				usuarioAutoridad = areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental");
				if(usuarioAutoridad != null && usuarioAutoridad.getId() != null){
					autoridad = usuarioAutoridad.getNombre();
				}else{
					System.out.println("No existe usuario " + Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental") );
					return false;
				}
			}else{
				ProyectoLicenciaCoa proyectoRCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
				// si es proyecto rcoa
				if(proyectoRCoa != null && proyectoRCoa.getId() != null){
					if (proyectoRCoa.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
						UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyectoRCoa.getIdCantonOficina());
						areaTramite = ubicacion.getAreaCoordinacionZonal().getArea();
					} else{
						areaTramite = (proyectoRCoa.getAreaInventarioForestal().getArea() != null) ? proyectoRCoa.getAreaInventarioForestal().getArea(): proyectoRCoa.getAreaInventarioForestal();
					}
				}else{
					ProyectoLicenciamientoAmbiental proyectoSuia = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(tramite);
					if(proyectoSuia != null && proyectoSuia.getId() != null){
						// si es proyectos suia busco la direccion zonal en base a la ubicacion geografica
						if(proyectoSuia.getProyectoUbicacionesGeograficas() != null && proyectoSuia.getProyectoUbicacionesGeograficas().size() > 0){
							UbicacionesGeografica ubicacion = proyectoSuia.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
							if(proyectoSuia.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){

							}else{
								if(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
									areaTramite = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
								} else {
									areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
								}
							}
						}
					}
				}

				// si es proyecto digitalizado busco el area del proyecto digitalizado
				AutorizacionAdministrativaAmbiental autorizacionAdministrativa = new AutorizacionAdministrativaAmbiental();
				Integer proyectoId = Integer.valueOf(((String)variables.get("idProyectoDigitalizacion") == null)?"0":(String)variables.get("idProyectoDigitalizacion"));
				if(proyectoId > 0){
					autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(proyectoId);
					if(autorizacionAdministrativa != null && autorizacionAdministrativa.getId() != null){
						// listo las ubicaciones del proyecto original
						List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), 1, "WGS84", "17S");
						// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
						if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
							ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), 3, "WGS84", "17S");
						}
						// si no existen // listo las ubicaciones del proyecto ingresadas en digitalizacion
						if(ListaUbicacionTipo == null || ListaUbicacionTipo.size() == 0){
							ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyecto(autorizacionAdministrativa.getId(), 2);
						}
						// si  existen busco el arae 
						if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
							UbicacionesGeografica ubicacion = ListaUbicacionTipo.get(0).getUbicacionesGeografica();
							if(ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) {
								areaTramite = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
							} else {
								areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal().getArea();
							}
						}
						if(autorizacionAdministrativa.getAreaResponsableControl().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)){
							
						}
					}
				}
				
				if(areaTramite != null  && areaTramite.getId() != null){
					try {
						rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");
						List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, areaTramite);
						if(listaUsuarios != null && !listaUsuarios.isEmpty()){
							usuarioAutoridad = listaUsuarios.get(0);
							autoridad = usuarioAutoridad.getNombre();
						}else{
							System.out.println("No existe usuario " + rolDirector + " para el area " + areaTramite.getAreaName());						
							return false;
						}
					} catch (Exception e) {
						System.out.println("No existe usuario " + rolDirector + " para el area " + areaTramite.getAreaName());
						return false;
					}
				}else{
					System.out.println("No existe el area " + areaTramite);					
					return false;
				}
			}
			Map<String, Object> params=new HashMap<>();
			params.put("realizoPago", true);
			params.put("director", autoridad);
			procesoFacade.modificarVariablesProceso(usuario, tarea.getProcessInstanceId(), params);
			procesoFacade.aprobarTarea(usuario, tarea.getTaskId(), tarea.getProcessInstanceId(), null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean finalizar(TaskSummaryCustom tarea, Usuario usuario){
		try {
			Map<String, Object> params=new HashMap<>();
			procesoFacade.modificarVariablesProceso(usuario, tarea.getProcessInstanceId(), params);
			Map<String, Object> data = new ConcurrentHashMap<String, Object>();
			procesoFacade.aprobarTarea(usuario, tarea.getTaskId(), tarea.getProcessInstanceId(), data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean finalizarResolucion(String codigoProyecto, TaskSummaryCustom tarea, Usuario usuario)
	{		
		try {
			String autoridad;
			Boolean requiereFirmaRgd = true;
			Boolean continuar = false;
			Usuario usuarioAutoridad = areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental");
			ProyectoLicenciaCoa proyectoRCoa = proyectoLicenciaCoaFacade.buscarProyecto(codigoProyecto);
			if(usuarioAutoridad != null && usuarioAutoridad.getId() != null){
				autoridad = usuarioAutoridad.getNombre();
				Map<String, Object> params=new HashMap<>();			
				params.put("realizoPago", true);
				params.put("autoridadAmbiental", autoridad);
				params.put("requiereFirmaRgd", requiereFirmaRgd);
				params.put("requiereFirmaRsq", false);
				
				if(requiereFirmaRgd) {
					//buscar zonal correspondiente
					String rolDirector = "";
					Area areaAutoridad = null;
					if (proyectoRCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)) {
						UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyectoRCoa.getIdCantonOficina());
						areaAutoridad = ubicacion.getAreaCoordinacionZonal().getArea();
						rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");
					} else {
						areaAutoridad = proyectoRCoa.getAreaInventarioForestal().getArea();
						rolDirector = Constantes.getRoleAreaName("role.dp.director.provincial.rgd");

						if(proyectoRCoa.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
							areaAutoridad = proyectoRCoa.getAreaResponsable();
							rolDirector = Constantes.getRoleAreaName("role.resolucion.galapagos.autoridad");
						} 
					}					
					try {
						List<Usuario> listaUsuarios = usuarioFacade.buscarUsuariosPorRolYArea(rolDirector, areaAutoridad);
						if(listaUsuarios != null && !listaUsuarios.isEmpty()){
							params.put("autoridadAmbientalRgd", listaUsuarios.get(0).getNombre());
							continuar = true;
						}else{
												
						}
					} catch (Exception e) {

						System.out.println("Error al recuperar la zonal correspondiente.");					
					}
				}
				if(continuar){
					procesoFacade.modificarVariablesProceso(usuario, tarea.getProcessInstanceId(), params);
					procesoFacade.aprobarTarea(usuario, tarea.getTaskId(), tarea.getProcessInstanceId(), null);
					return true;
				}else{
					return false;
				}
			}else{
				System.out.println("No existe usuario " + Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental") );
				return false;
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	
	private boolean finalizarRegistoAmbiental(TaskSummaryCustom tarea, Usuario usuario){
		try {
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(usuario, tarea.getProcessInstanceId());
			String tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			ProyectoLicenciaCoa proyectoRCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			Map<String, Object> parametros = new HashMap<>();
			List<Usuario> listaUsuario = null;
			String rol="role.registro.ambiental.autoridad";
			Area area = proyectoRCoa.getAreaResponsable();
			String rolAutoridad = Constantes.getRoleAreaName(rol);
			
			if(area.getTipoArea().getSiglas().toUpperCase().equals("ZONALES")){
				UbicacionesGeografica provincia = proyectoRCoa.getAreaResponsable().getUbicacionesGeografica();	
				area = areaFacade.getAreaCoordinacionZonal(provincia);
			}
			
			if(area.getTipoArea().getSiglas().equals("PC")){
				listaUsuario = usuarioFacade.buscarUsuarioPorRol(Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental"));
			}else if(area.getTipoArea().getSiglas().equals("OT")){
				listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, area.getArea());
			}else{
				listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, area);
			}
			if(listaUsuario != null && listaUsuario.size() > 0){
				parametros.put("autoridadAmbiental", listaUsuario.get(0).getNombre());
			}else{
				return false;
			}
			procesoFacade.modificarVariablesProceso(usuario, tarea.getProcessInstanceId(), parametros);
			Map<String, Object> data = new ConcurrentHashMap<String, Object>();
			procesoFacade.aprobarTarea(usuario, tarea.getTaskId(), tarea.getProcessInstanceId(), data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean finalizarParticipacion(TaskSummaryCustom tarea, Usuario usuario){
		try {
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(usuario, tarea.getProcessInstanceId());
			String tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			Integer numeroFacilitadores = Integer.valueOf((String) variables.get("numeroFacilitadores"));
			ProyectoLicenciaCoa proyectoRCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			List<Usuario> listaUsuariosFacilitadores = facilitadorPPCFacade.usuariosFacilitadoresAsignadosPendientesAceptacion(proyectoRCoa);

			//validar la asignacion de los facilitadores
			String[] facilitadoresLista = new String[numeroFacilitadores];
			Integer cont = 0;
			if(listaUsuariosFacilitadores == null || listaUsuariosFacilitadores.size()==0) {
				System.out.println(tramite + " No existe la cantidad de facilitadores solicitados disponibles.");
	            return false;
			}
			for (Usuario objUsuario : listaUsuariosFacilitadores) {
				if(cont<numeroFacilitadores)
					facilitadoresLista[cont++] = objUsuario.getNombre();
			}
			Map<String, Object> params = new HashMap<String, Object>();
            params.put("listaFacilitadores", facilitadoresLista);
            List<FacilitadorPPC> facilitadores = facilitadorPPCFacade.facilitadoresAsignadosPendientesAceptacion(proyectoRCoa);
            for (FacilitadorPPC facilitadorPPC : facilitadores) {
            	facilitadorPPC.setFechaRegistroPago(new Date());
				facilitadorPPCFacade.guardar(facilitadorPPC);
			}
			
            procesoFacade.modificarVariablesProceso(usuario, tarea.getProcessInstanceId(), params);
            procesoFacade.aprobarTarea(usuario,tarea.getTaskId(),tarea.getProcessInstanceId(), null);
            
            for (Usuario objUsuario : listaUsuariosFacilitadores) {
            	notificaciones(objUsuario, proyectoRCoa,  usuario);
            }
            return true;
		} catch (Exception e) {					
			e.printStackTrace();
			return false;
		}
	}

	public void deshactivarNut(){
		String sqlUpdate=" update payments.unique_transaction_number "
				+ "	set nuts_id = 5 "
				+ "	where nut_status and nuts_id = 2 and nut_date_finalized_activation < now(); ";

			 Query queryUpdate = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdate);
	         queryUpdate.executeUpdate();
	}


	
	private void notificaciones(Usuario usuarioFacilitador, ProyectoLicenciaCoa proyectoRCoa, Usuario usuario ) {
		try {
			Object[] parametrosCorreoTecnicos = new Object[] {usuarioFacilitador.getPersona().getTipoTratos().getNombre(), 
						usuarioFacilitador.getPersona().getNombre(), proyectoRCoa.getNombreProyecto(), 
						proyectoRCoa.getCodigoUnicoAmbiental() };
				
				String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
								"bodyNotificacionDesignacionFacilitadorByPass",
								parametrosCorreoTecnicos);

    			NotificacionAutoridadesController email=new NotificacionAutoridadesController();
    			List<Contacto> contactos = usuarioFacilitador.getPersona().getContactos();
    			String emailDestino = "";
    			for (Contacto contacto : contactos) {
    				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL) && contacto.getEstado()){
    					emailDestino=contacto.getValor();
    					break;
    				}
    			}
    			email.sendEmailInformacion(emailDestino, notificacion, "Regularización Ambiental Nacional", proyectoRCoa.getCodigoUnicoAmbiental(), usuario, usuario);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
