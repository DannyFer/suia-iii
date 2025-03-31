package ec.gob.ambiente.suia.cargaArchivos.facade;

import java.io.InputStream;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.PlanManejoAmbiental;
import ec.gob.ambiente.suia.domain.TipoPlanManejoAmbiental;

@Stateless
public class CargarArchivosFacade {

	Logger LOG = Logger.getLogger(CargarArchivosFacade.class);

	@EJB
	private CatalogoGeneralFacade catalogoGeneralFacade;
	
	@SuppressWarnings("resource")
	public List<PlanManejoAmbiental> cargarArchivoPMA(InputStream file,
			TipoPlanManejoAmbiental tipoPlan, Integer eiaId) throws Exception {
		List<PlanManejoAmbiental> listaPMA = null;
		/*Scanner s = null;
		try {
			s = new Scanner(file).useDelimiter("\n");
			listaPMA = new ArrayList<PlanManejoAmbiental>();

			PlanManejoAmbiental pma = null;
			int i=1;
			while (s.hasNext()) {
				String[] linea = s.next()
						.split(Constantes.SPLIT_CARGA_ARCHIVOS);

				Integer id = null;
				String aspectoAmbiental = linea[0];
				if (aspectoAmbiental == null
						|| aspectoAmbiental.trim().equals("")) {
					aspectoAmbiental = null;
				}
				String impactoIdentificado = linea[1];
				if (impactoIdentificado == null
						|| impactoIdentificado.trim().equals("")) {
					impactoIdentificado = null;
				}
				String medidasPropuestas = linea[2];
				String indicadores = linea[3];
				String mediosVerificacion = linea[4];
				String responsable = linea[5];
				Integer plazo = new Integer(linea[6]);
				Integer periodo = new Integer(linea[7]);

				CatalogoGeneral catalogoPeriodo = catalogoGeneralFacade.obtenerXTipoXCodigo(TipoCatalogo.PERIODO, periodo.toString());
				if(catalogoPeriodo == null){
					throw new Exception("ERROR:::::: el periodo "+periodo+" no corresponde al catalogo asignado");
				}
				
				pma = new PlanManejoAmbiental(id, aspectoAmbiental,
						impactoIdentificado, medidasPropuestas, indicadores,
						mediosVerificacion, responsable, plazo, catalogoPeriodo.getDescripcion(),
						tipoPlan);
				pma.setOrden(i);
				pma.setEiaId(eiaId);
				listaPMA.add(pma);
				i++;
			}
		} catch (Exception e) {
			LOG.error("Error al cargar Archivo PMA", e);
			throw new Exception(e);
		} finally {
			if (s != null) {
				s.close();
			}
		}
*/
		return listaPMA;
	}
}
