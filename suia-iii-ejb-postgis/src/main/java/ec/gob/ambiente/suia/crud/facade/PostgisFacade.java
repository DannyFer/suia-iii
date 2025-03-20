package ec.gob.ambiente.suia.crud.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServicePostgisBean;
import ec.gob.ambiente.suia.domain.TipoProyeccion;

@Stateless
public class PostgisFacade {

	@EJB
	private CrudServicePostgisBean crudServicePostgisBean;

	public List<TipoProyeccion> getTiposProyeccionesSoportadas() {
		return crudServicePostgisBean.findByNamedQueryGeneric(TipoProyeccion.FIND_ALL_WITHOUT_WGS84_ZONA_17S, null);
	}

	public List<String> transformarCoordenadas(List<String> coordenadas, TipoProyeccion tipoProyeccion) {
		int WGS84_ZONA_17S = ((TipoProyeccion) crudServicePostgisBean.find(TipoProyeccion.class,
				TipoProyeccion.TIPO_PROYECCION_WGS84_ZONA_17S)).getValor();
		List<String> coordenadasResult = new ArrayList<String>();
		for (String par : coordenadas) {
			String x = par.split(" ")[0];
			String y = par.split(" ")[1];
			Object result = crudServicePostgisBean
					.executeNativeQueryUniqueResult("SELECT * FROM ST_ASTEXT(ST_TRANSFORM(ST_GeomFromText('POINT(" + x
							+ " " + y + ")', " + tipoProyeccion.getValor() + ")," + WGS84_ZONA_17S + "))");

			String lower = result.toString().toLowerCase();
			lower = lower.replace("point(", "");
			lower = lower.replace(")", "");
			coordenadasResult.add(lower);
		}

		return coordenadasResult;
	}
}
