package ec.gob.ambiente.suia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@Table(name = "type_material_catalog", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = CatalogoTipoMaterial.LISTAR_POR_TIPO_MATERIAL, query = "SELECT f FROM CatalogoTipoMaterial f WHERE f.idTipoMaterial = :idTipoMaterial AND f.estado = true") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tymc_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tymc_status = 'TRUE'")
public class CatalogoTipoMaterial extends EntidadBase {

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_TIPO_MATERIAL = PAQUETE + "CatalogoTipoMaterial.listarPorTipoMaterial";
	/**
     *
     */
	private static final long serialVersionUID = 1154755965481020655L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "TYPE_MATERIAL_CATALOG_TYMC_ID_GENERATOR", sequenceName = "seq_tymc_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TYPE_MATERIAL_CATALOG_TYMC_ID_GENERATOR")
	@Column(name = "tymc_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tymc_nombre")
	private String nombre;

	@ManyToOne
	@JoinColumn(name = "maty_id")
	@ForeignKey(name = "fk_mining_enviromental_record_maty_id_material_types_maty_id")
	@Getter
	@Setter
	private TipoMaterial tipoMaterial;

	@Getter
	@Setter
	@Column(name = "maty_id", insertable = false, updatable = false)
	private Integer idTipoMaterial;

}
