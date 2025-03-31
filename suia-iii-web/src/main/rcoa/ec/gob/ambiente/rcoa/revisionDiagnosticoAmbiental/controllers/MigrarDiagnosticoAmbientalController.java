package ec.gob.ambiente.rcoa.revisionDiagnosticoAmbiental.controllers;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.facade.MigrarDiagnosticoAmbientalFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class MigrarDiagnosticoAmbientalController implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private MigrarDiagnosticoAmbientalFacade migrarDiagnosticoAmbientalFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@Getter
	@Setter
	private Integer bloque;
	
	@SuppressWarnings("unchecked")
	public void migrarProyectosDiagnostico() {
		
		String procesosIntancias = null;
		
		if(bloque != null) {
			switch (bloque) {
			case 1:
				//en revisión 101
				procesosIntancias = "166762, 186007, 186792, 140765, 140864, 141091, 141793, 141816, 142735, 142854, 142880, 143185, 143278, 143420, 143540, 145234, 145542, 147194, 147860, 148299, 148468, 148469, 148723, 149362, 155476, 155845, 156373, 156664, 157052, 157386, 157399, 157404, 157973, 158107, 158503, 159987, 160104, 160746, 160760, 161815, 161976, 163457, 166501, 166514, 168287, 170039, 170055, 171126, 171489, 173121, 173738, 174242, 174534, 174766, 174846, 175008, 175471, 175592, 176289, 176305, 176307, 176312, 176322, 176325, 176445, 176449, 176453, 176462, 176467, 176477, 176482, 176484, 176485, 176487, 176488, 176489, 176649, 176771, 177672, 177706, 177851, 177905, 177912, 178319, 178587, 179497, 179512, 179565, 179579, 181241, 183127, 183323, 183414, 183598, 184174, 184546, 184751, 187072, 197726, 221179, 227435";
				break;
			case 2:
				//en diagnostico 38
				procesosIntancias = "187306, 187357, 188129, 188340, 188464, 189330, 189830, 189866, 189938, 190234, 190457, 190475, 190820, 191118, 191213, 191263, 191320, 191496, 191722, 191978, 192105, 192173, 192343, 192575, 192614, 192747, 192815, 192818, 192825, 192835, 192931, 192934, 192936, 193476, 193524, 193598, 193632, 193678";
				break;
			case 3:
				//en diagnostico 100
				procesosIntancias = "193714, 193715, 193928, 194025, 194037, 194065, 194278, 194282, 194485, 194505, 194717, 194732, 194834, 194845, 194955, 194958, 194961, 194969, 194970, 195448, 195526, 195947, 195970, 196185, 196233, 196251, 196553, 196778, 196844, 196958, 196973, 197120, 197151, 197173, 197766, 198084, 198402, 198666, 198681, 199183, 199627, 199640, 199875, 200030, 200299, 200340, 200891, 201174, 201784, 202271, 202330, 202462, 202751, 202906, 203055, 203308, 203944, 203981, 204107, 204236, 205559, 205655, 205743, 205746, 205788, 205999, 206079, 206224, 207601, 207991, 208155, 208915, 209232, 209387, 209553, 209682, 209887, 209910, 210099, 210144, 210425, 210456, 210595, 210688, 210800, 211219, 211386, 211463, 211845, 211972, 212035, 212142, 212280, 212344, 212497, 212630, 212751, 213126, 213500, 213636";
				break;
			case 4:
				//en diagnostico 100
				procesosIntancias = "213928, 214261, 214294, 214483, 214508, 214610, 214995, 215295, 215335, 215461, 215551, 216148, 216316, 216372, 216386, 216495, 216528, 216701, 216939, 216979, 217007, 217258, 217699, 217711, 217891, 218134, 218332, 218568, 218606, 218820, 218926, 219176, 219220, 219256, 219376, 219466, 219532, 219601, 219656, 219802, 219874, 219959, 219973, 220540, 220594, 220600, 220735, 220780, 220821, 220891, 220966, 221129, 221147, 221180, 221194, 221250, 221267, 221303, 221344, 221740, 221932, 222035, 222088, 222099, 222170, 222196, 222240, 222257, 222268, 222439, 222484, 222700, 222793, 223296, 223319, 223349, 223509, 223550, 223561, 223703, 223769, 223865, 223866, 223942, 224018, 224073, 224094, 224147, 224454, 224461, 224475, 224514, 224563, 224594, 224673, 224735, 224856, 224910, 224920, 224939";
				break;
			case 5:
				//en diagnostico 100
				procesosIntancias = "225976, 226021, 226047, 226233, 226256, 226347, 226505, 226733, 226783, 226800, 226984, 227043, 227064, 227081, 227191, 227250, 227269, 227297, 227351, 227416, 227420, 227467, 227626, 227824, 227825, 227939, 228005, 228011, 228012, 228115, 228195, 228206, 228234, 228375, 228406, 228600, 228643, 228693, 228779, 228882, 228909, 228965, 229098, 229149, 229173, 229339, 229404, 229458, 229570, 229614, 229679, 229690, 229691, 229844, 229870, 229903, 229967, 230090, 230164, 230203, 230278, 230283, 230314, 230338, 230456, 230485, 230712, 230790, 230851, 230865, 230893, 230908, 230986, 231008, 231027, 231106, 231110, 231115, 231122, 231172, 231174, 231198, 231205, 231226, 231268, 231294, 231345, 231574, 231646, 231739, 231793, 231803, 231906, 231907, 231995, 232021, 232057, 232175, 232176, 232236";
				break;

			default:
				break;
			}
		}
		
		if(procesosIntancias != null && !procesosIntancias.isEmpty()) {
			String sqlProcesos="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select distinct processinstanceid, actualowner_id "
	            	+ "from task " + "where processinstanceid in (" + procesosIntancias + ") "
	            	+ "and status in (''Reserved'', ''InProgress'') "
	            	+ "') as (processinstanceid text, actualowner_id text)"
	            	+ "order by 1"; 
			
	    	Query q_tareas = crudServiceBean.getEntityManager().createNativeQuery(sqlProcesos);
	    	
	    	List<Object[]> listProcesosActivos = (List<Object[]>) q_tareas.getResultList();
			if (listProcesosActivos.size() > 0) {
				for (int i = 0; i < listProcesosActivos.size(); i++) {
					Object[] dataProject = (Object[]) listProcesosActivos.get(i);
					
					migrarDiagnosticoAmbientalFacade.avanzarProceso(dataProject);
				}
			} else {
				JsfUtil.addMessageError("Procesos no encontrados: " + sqlProcesos);
			}
		} else {
			JsfUtil.addMessageError("Ingrese el numero de bloque");
		}
		
	}
	
}