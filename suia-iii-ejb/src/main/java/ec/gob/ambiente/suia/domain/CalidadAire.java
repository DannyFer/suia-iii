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
@Table(name = "air_qualities", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "aiqu_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "aiqu_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "aiqu_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "aiqu_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "aiqu_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "aiqu_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = CalidadAire.LISTAR_POR_ID_EIA, query = "SELECT c FROM CalidadAire c WHERE c.estado=true AND c.eiaId = :eiaId") })
public class CalidadAire extends EntidadAuditable {

	private static final long serialVersionUID = 1218304605831535129L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ID_EIA = PAQUETE
			+ "CalidadAire.listarPorIdEia";
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "AIR_QUALITIES_GENERATOR", initialValue = 1, schema = "suia_iii", sequenceName = "seq_aiqu_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AIR_QUALITIES_GENERATOR")
	@Column(name = "aiqu_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "aiqu_laboratory")
	private Integer idLaboratorio;
	
	@Getter
	@Setter
	@Column(name = "aiqu_parameter")
	private Integer idParametro;
	
	@Getter
	@Setter
	@Column(name = "aiqu_unit")
	private String unidad;
	@Getter
	@Setter
	@Column(name = "aiqu_limit")
	private Double limite;
	
	@Getter
	@Setter
	@Column(name = "aiqu_code")
	private String codigo;
	@Getter
	@Setter
	@Column(name = "aiqu_description")
	private String descripcion;
	@Getter
	@Setter
	@Column(name = "aiqu_result")
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
	private CatalogoGeneral laboratorio;
	@Transient
	@Getter
	@Setter
	private CatalogoGeneral parametro;
	
	@Transient
	@Getter
	@Setter
	private String color;
	

}
