package ec.gob.ambiente.suia.domain;


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "soil_chemicals", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "soch_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "soch_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "soch_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "soch_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "soch_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "soch_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = QuimicaSuelo.LISTAR_POR_ID_EIA, query = "SELECT q FROM QuimicaSuelo q WHERE q.estado=true AND q.eiaId = :eiaId") })
public class QuimicaSuelo extends EntidadAuditable {

	private static final long serialVersionUID = 1218304605831535129L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ID_EIA = PAQUETE
			+ "QuimicaSuelo.listarPorIdEia";
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "SOIL_CHEMICALS_GENERATOR", initialValue = 1, schema = "suia_iii", sequenceName = "seq_soch_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOIL_CHEMICALS_GENERATOR")
	@Column(name = "soch_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "soch_regulation")
	private Integer idNormativa;
	
	@Getter
	@Setter
	@Column(name = "soch_parameter")
	private Integer idParametro;
	@Getter
	@Setter
	@Column(name = "soch_unit")
	private String unidad;
	@Getter
	@Setter
	@Column(name = "soch_limit")
	private Double limite;
	@Getter
	@Setter
	@Column(name = "soch_use")
	private Integer idUso;
	@Getter
	@Setter
	@Column(name = "soch_laboratory")
	private Integer idLaboratorio;
	@Getter
	@Setter
	@Column(name = "soch_code")
	private String codigo;
	@Getter
	@Setter
	@Column(name = "soch_place")
	private String lugar;
	@Getter
	@Setter
	@Column(name = "soch_result")
	private Double resultado;
	
	@Getter
	@Setter
	@Column(name="eia_id")
	private Integer eiaId;
	@Transient
	@Getter
	@Setter
	private boolean editar;
	
	@Transient
	@Getter
	@Setter
	private CoordenadaGeneral coordenadaGeneral;
	
	@Transient
	@Getter
	@Setter
	private CatalogoGeneral normativa;
	@Transient
	@Getter
	@Setter
	private CatalogoGeneral parametro;
	@Transient
	@Getter
	@Setter
	private CatalogoGeneral laboratorio;
	@Transient
	@Getter
	@Setter
	private CatalogoGeneral usoSuelo;
	
	@Transient
	@Getter
	@Setter
	private String color;

}
