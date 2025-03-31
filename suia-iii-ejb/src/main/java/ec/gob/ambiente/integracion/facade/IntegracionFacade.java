package ec.gob.ambiente.integracion.facade;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.xml.ws.BindingProvider;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ec.fugu.ambiente.consultoring.projects.*;
import ec.fugu.ambiente.consultoring.retasking.ProyectoLicenciaVo;
import ec.fugu.ambiente.consultoring.retasking.ReasignacionTareaMasivaService;
import ec.fugu.ambiente.consultoring.retasking.ReasignacionTareaMasivaService_Service;
import ec.fugu.ambiente.consultoring.retasking.UsuarioCarga;
import ec.gob.ambiente.hyd.service.HidrocarburosWs;
import ec.gob.ambiente.hyd.service.HidrocarburosWsService;
import ec.gob.ambiente.hyd.service.TaskSummaryDto;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.procedimientosadministrativosmaelogin.controller.ConsultaPA;
import ec.gob.ambiente.suia.procedimientosadministrativosmaelogin.controller.ConsultaPAWS;
import ec.gob.ambiente.suia.procedimientosadministrativosmaelogin.controller.ProcedimientoAdministrativoVO;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class IntegracionFacade {

	public List<TaskSummaryDto> getTaskByUserFromHidrocarburos(String actorId, String user, String pass)
			throws Exception {
		try {
			HidrocarburosWsService hidrocarburosWsService = new HidrocarburosWsService();
			HidrocarburosWs service = hidrocarburosWsService.getHidrocarburosWsPort();

			setWebServiceTimeOut((BindingProvider) service);

			return service.getTaskList(user, pass);
		} catch (Exception ex) {
			throw new Exception("Error en web service de hidrocarburos obteniendo tareas", ex);
		}
	}
	
	public List<Task> getTaskByUserFromSuiaPaginado(Integer desde,Integer limite, String tramite, String flujo, String actividad, String actorId, String user, String pass) throws Exception {
		try {
			ProyectoSuia2FacadeService_Service proyectoSuia2FacadeService = new ProyectoSuia2FacadeService_Service();
			ProyectoSuia2FacadeService service = proyectoSuia2FacadeService.getProyectoSuia2FacadeServicePort();

			setWebServiceTimeOut((BindingProvider) service);

			return service.getTasks(user, pass, actorId, tramite, flujo, actividad, limite, desde);
		} catch (Exception ex) {
			throw new Exception("Error en web service de suia obteniendo tareas", ex);
		}
	}
	
	public Long getCountTaskByUserIVCategorias(String tramite, String flujo, String actividad, String actorId, String user, String pass) throws Exception {
		try {
			ProyectoSuia2FacadeService_Service proyectoSuia2FacadeService = new ProyectoSuia2FacadeService_Service();
			ProyectoSuia2FacadeService service = proyectoSuia2FacadeService.getProyectoSuia2FacadeServicePort();

			setWebServiceTimeOut((BindingProvider) service);

			return service.getCountTasks(user, pass, actorId, tramite, flujo, actividad);
		} catch (Exception ex) {
			throw new Exception("Error en web service de suia obteniendo tareas", ex);
		}
	}

	public List<ProyectoLicenciaVo> getTaskByUserFromSuiaForRetasking(String actorId) throws Exception {
		try {
			ReasignacionTareaMasivaService_Service masivaService_Service = new ReasignacionTareaMasivaService_Service();
			ReasignacionTareaMasivaService service = masivaService_Service.getReasignacionTareaMasivaServicePort();

			setWebServiceTimeOut((BindingProvider) service);

			return service.listaTareas(actorId);
		} catch (Exception ex) {
			throw new Exception("Error en web service de suia obteniendo tareas", ex);
		}
	}

	public List<UsuarioCarga> getCandidateUsersToRetasking(String actorId) throws Exception {
		try {
			ReasignacionTareaMasivaService_Service masivaService_Service = new ReasignacionTareaMasivaService_Service();
			ReasignacionTareaMasivaService service = masivaService_Service.getReasignacionTareaMasivaServicePort();

			setWebServiceTimeOut((BindingProvider) service);

			return service.traerCargaTecnicos(actorId, (byte) 0);
		} catch (Exception ex) {
			throw new Exception("Error en web service de suia obteniendo tareas", ex);
		}
	}

	public String executeRetasking(List<ProyectoLicenciaVo> proyectoSelecionados, String usuarioAnterior,
			String usuarioReasignar) throws Exception {
		try {
			ReasignacionTareaMasivaService_Service masivaService_Service = new ReasignacionTareaMasivaService_Service();
			ReasignacionTareaMasivaService service = masivaService_Service.getReasignacionTareaMasivaServicePort();

			setWebServiceTimeOut((BindingProvider) service);

			return service.reasignarProyestos(proyectoSelecionados, usuarioAnterior, usuarioReasignar);
		} catch (Exception ex) {
			throw new Exception("Error en web service de suia obteniendo tareas", ex);
		}
	}

	public List<ProyectoLicIntegacionVo> getProjectsByUserFromSuia(String user, String pass) throws Exception {
		try {
			ProyectoSuia2FacadeService_Service proyectoSuia2FacadeService = new ProyectoSuia2FacadeService_Service();
			ProyectoSuia2FacadeService service = proyectoSuia2FacadeService.getProyectoSuia2FacadeServicePort();

			setWebServiceTimeOut((BindingProvider) service);
			List<ProyectoLicIntegacionVo> proyectolista= new ArrayList<ProyectoLicIntegacionVo>();
			
			proyectolista= service.getProjects(user, pass, user);
			if (proyectolista!=null && !proyectolista.isEmpty()){
				return proyectolista ;
			}
			
		} catch (Exception ex) {
			return null;
		}
		return null;
	}

	public boolean isProjectHydrocarbons(String code, String user, String pass) throws Exception {
		try {
			ProyectoSuia2FacadeService_Service proyectoSuia2FacadeService = new ProyectoSuia2FacadeService_Service();
			ProyectoSuia2FacadeService service = proyectoSuia2FacadeService.getProyectoSuia2FacadeServicePort();

			setWebServiceTimeOut((BindingProvider) service);

			return service.isProjectHydrocarbons(user, pass, code);
		} catch (Exception ex) {
			throw new Exception("Error en web service de suia verificando sector del proyecto: " + code, ex);
		}
	}

	public void logout(Usuario usuario) throws Exception {
		try {
			// new HidrocarburosWsService().getHidrocarburosWsPort().logout();
			// NO HAY LOGOFF EN EL SERVICIO DE SUIA II
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void sendExternalCallHidrocarburos(String user, String pass, IntegrationActions action,
			String... parameters) throws Exception {
		if (Constantes.getAppIntegrationHydrocarbonsEnabled()) {
			HttpPost httpPost = new HttpPost(Constantes.getAppIntegrationServletHydrocarbons());
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", user));
			nvps.add(new BasicNameValuePair("password", pass));
			nvps.add(new BasicNameValuePair("accion", action.toString()));

			if (parameters != null && parameters.length % 2 == 0) {
				for (int i = 0; i < parameters.length; i += 2) {
					nvps.add(new BasicNameValuePair(parameters[i], parameters[i + 1]));
				}
			}

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
		}
	}

	public static void sendExternalCallSuia(String user, String pass, IntegrationActions action, String... parameters)
			throws Exception {
		if (Constantes.getAppIntegrationSuiaEnabled()) {
			HttpPost httpPost = new HttpPost(Constantes.getAppIntegrationServletSuia());
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", user));
			nvps.add(new BasicNameValuePair("password", pass));
			nvps.add(new BasicNameValuePair("accion", action.toString()));

			if (parameters != null && parameters.length % 2 == 0) {
				for (int i = 0; i < parameters.length; i += 2) {
					nvps.add(new BasicNameValuePair(parameters[i], parameters[i + 1]));
				}
			}

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
		}
	}

	public String executeExternalCommand(String executableScript, String... arguments) throws Exception {
		StringBuffer output = new StringBuffer();

		final File batchFile = new File(executableScript);
		batchFile.setExecutable(true);
		final File outputFile = new File(String.format("output_%tY%<tm%<td_%<tH%<tM%<tS.txt",
				System.currentTimeMillis()));

		List<String> parametersToRun = new ArrayList<String>();
		parametersToRun.add(batchFile.getAbsolutePath());
		if (arguments != null) {
			for (String arg : arguments)
				parametersToRun.add(arg);
		}

		final ProcessBuilder processBuilder = new ProcessBuilder(parametersToRun);

		processBuilder.redirectErrorStream(true);
		processBuilder.redirectOutput(outputFile);

		final Process process = processBuilder.start();
		final int exitStatus = process.waitFor();

		if (exitStatus >= 0) {
			BufferedReader br = new BufferedReader(new FileReader(outputFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				output.append(line + "\n");
			}
			br.close();
			outputFile.delete();

			String plainOutput = output.toString().replace("\n", "");
			if (plainOutput.toLowerCase().contains("error")) {
				new File(System.getProperty("jboss.home.dir") + "/standalone/log/externalCommands/").mkdirs();
				File log = new File(System.getProperty("jboss.home.dir") + "/standalone/log/externalCommands/"
						+ String.format("command_error_%tY%<tm%<td_%<tH%<tM%<tS.log", System.currentTimeMillis()));
				BufferedWriter bw = new BufferedWriter(new FileWriter(log.getAbsoluteFile()));
				bw.write(output.toString());
				bw.close();
				throw new Exception("Error in script execution, see the full trace in '" + log.getAbsolutePath() + "'");
			}

			return output.toString();
		}

		return null;
	}

	private void setWebServiceTimeOut(BindingProvider service) {
		service.getRequestContext().put("javax.xml.ws.client.connectionTimeout",
				Constantes.getAppIntegrationServicesTimeOut() * 1000);
		service.getRequestContext().put("javax.xml.ws.client.receiveTimeout",
				Constantes.getAppIntegrationServicesTimeOut() * 1000);
	}

	public List<ProyectoLicIntegacionVo> getListProjectByAdmin(String user, String pass, String userName, int limite,
			int desde, String nombreProyecto, String codigoProyecto, String sector, String permiso) throws Exception {
		try {
			Suia2ConsultasExternas_Service proyectoSuia2CunsultasService = new Suia2ConsultasExternas_Service();
			Suia2ConsultasExternas service = proyectoSuia2CunsultasService.getSuia2ConsultasExternasPort();

			setWebServiceTimeOut((BindingProvider) service);

			return service.getProjectsExternal(user, pass, userName, limite, desde, codigoProyecto, nombreProyecto, sector, permiso);
		} catch (Exception ex) {
			throw new Exception("Error en web service de suia obteniendo el listado de proyectos. ", ex);
		}
	}

	public Integer contarListProjectByAdmin(String userName, String nombreProyecto, String codigoProyecto, String sector, String permiso)
			throws Exception {
		try {
			Suia2ConsultasExternas_Service proyectoSuia2CunsultasService = new Suia2ConsultasExternas_Service();
			Suia2ConsultasExternas service = proyectoSuia2CunsultasService.getSuia2ConsultasExternasPort();

			setWebServiceTimeOut((BindingProvider) service);

			return Integer.parseInt(service.getProjectsCantidadExternal(userName, codigoProyecto, nombreProyecto,sector,permiso)
					.toString());
		} catch (Exception ex) {
			throw new Exception("Error en web service de suia obteniendo el listado de proyectos. ", ex);
		}
	}

	public int getAdministrativeProcessByUser(String document) throws Exception {
		try {
			ConsultaPA consultaPAService = new ConsultaPA();
			ConsultaPAWS service = consultaPAService.getConsultaPAWSPort();

			setWebServiceTimeOut((BindingProvider) service);

			List<ProcedimientoAdministrativoVO> result = service.obtenerRUC(document);

			return result != null ? result.size() : 0;
		} catch (Exception ex) {
			throw new Exception("Error en web service de procesos administrativos", ex);
		}
	}

	public enum IntegrationActions {
		registrar_proyecto, iniciar_tarea, mostrar_dashboard, logout, eliminar_proyecto, mostrar_reporte_provincial, reasignacion, eliminar_proyecto_II, reasignacion_masiva
	}
}
