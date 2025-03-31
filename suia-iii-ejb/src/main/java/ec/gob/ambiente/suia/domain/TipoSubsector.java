package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Feb 2, 2015]
 *          </p>
 */
@Table(name = "sectors_classification", schema = "public")
@NamedQueries({ @NamedQuery(name = TipoSubsector.OBTENER_POR_CODIGO, query = "SELECT p FROM TipoSubsector p WHERE p.codigo = :codigo AND p.estado = true") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "secl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "secl_status = 'TRUE'")
@Entity
public class TipoSubsector extends EntidadBase {
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_POR_CODIGO = PAQUETE + "TipoSubsector.obtenerPorCodigo";

	private static final long serialVersionUID = -1393294198751084742L;

	public static final String CODIGO_AGRICOLA = "0001";
	public static final String CODIGO_AGROINDUSTRIA = "0002";
	public static final String CODIGO_PECUARIO = "0003";
	public static final String CODIGO_MINERIA_EXPLORACION_INICIAL = "0004";
	public static final String CODIGO_MENERIA_LIBRE_APROVECHAMIENTOS = "0005";
	public static final String CODIGO_ELECTRICA_TELECOMUNICACIONES = "0006";
	public static final String CODIGO_SANEAMIENTO = "0007";
	public static final String CODIGO_OTROS_SECTORES = "0008";
	public static final String CODIGO_PESCA = "0009";
	public static final String CODIGO_PESCA_ACUACULTURA = "0010";
	public static final String CODIGO_PESCA_MARICULTURA = "0011";
	public static final String CODIGO_RESIDUOS_SOLIDOS = "0015";

	@Id
	@Getter
	@Setter
	@Column(name = "secl_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "secl_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "secl_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "secl_code")
	private String codigo;

	@OneToMany(mappedBy = "tipoSubsector")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "plse_status = 'TRUE'")
	@Getter
	@Setter
	private List<PlanSector> planSectorList;

	@Override
	public String toString() {
		return nombre;
	}
}
