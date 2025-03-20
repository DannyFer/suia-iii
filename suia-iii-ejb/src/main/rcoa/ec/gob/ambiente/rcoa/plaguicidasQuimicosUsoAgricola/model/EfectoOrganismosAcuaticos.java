package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="aquatic_organic_effects", schema = "chemical_pesticides")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "aqor_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "aqor_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "aqor_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "aqor_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "aqor_user_update")) })

@NamedQueries({
@NamedQuery(name="EfectoOrganismosAcuaticos.findAll", query="SELECT a FROM EfectoOrganismosAcuaticos a"),
@NamedQuery(name=EfectoOrganismosAcuaticos.GET_POR_PROYECTO, query="SELECT a FROM EfectoOrganismosAcuaticos a where a.idProyecto = :idProyecto and a.estado = true order by id desc"),
@NamedQuery(name=EfectoOrganismosAcuaticos.GET_POR_PROYECTO_ORDER_CATEGORIA, query="SELECT a FROM EfectoOrganismosAcuaticos a where a.idProyecto = :idProyecto and a.estado = true order by a.categoria.valor asc"),
@NamedQuery(name=EfectoOrganismosAcuaticos.GET_POR_PROYECTO_TIPO, query="SELECT a FROM EfectoOrganismosAcuaticos a where a.idProyecto = :idProyecto and a.idTipoOrganismo = :idTipoOrganismo and a.estado = true order by id desc")
})

public class EfectoOrganismosAcuaticos extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_POR_PROYECTO = PAQUETE + "EfectoOrganismosAcuaticos.getPorProyecto";
	public static final String GET_POR_PROYECTO_TIPO = PAQUETE + "EfectoOrganismosAcuaticos.getPorProyectoTipo";
	public static final String GET_POR_PROYECTO_ORDER_CATEGORIA = PAQUETE + "EfectoOrganismosAcuaticos.getPorProyectoOrderCategoria";

	public static final String UNIDAD_MEDIDA="CL 50(mg/l o ppm)";
	public static final String ADVERTENCIA="PELIGRO";
	public static final String SIN_ADVERTENCIA="SIN PALABRA DE ADVERTENCIA";
	public static final String INDICACIONES_1="MUY TÓXICO PARA ORGANISMOS ACUÁTICOS";
	public static final String INDICACIONES_2="TÓXICO PARA ORGANISMOS ACUÁTICOS";
	public static final String INDICACIONES_3="NOCIVO PARA ORGANISMOS ACUÁTICOS";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="aqor_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="aqor_ingredient_name")
	private String ingredienteActivo;

	@Getter
	@Setter
	@Column(name="aqor_ecotoxicological_value")
	private Double valor;

	@Getter
	@Setter
	@Column(name="aqor_unit_measurement")
	private String unidad;

	@Getter
	@Setter
	@Column(name="chpe_id")
	private Integer idProyecto;

	@Getter
	@Setter
	@Column(name="tyaq_id")
	private Integer idTipoOrganismo; //1 peces, 2 crustaceos, 3 algas

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id")	
	private CatalogoGeneralCoa categoria;

	@Getter
	@Setter
	@Column(name="aqor_pictogram")
	private Boolean pictograma;

	@Getter
	@Setter
	@Column(name="aqor_word_warning")
	private String palabraAdvertencia;

	@Getter
	@Setter
	@Column(name="aqor_danger_statements")
	private String indicacionesPeligro;

	@Getter
	@Setter
	@Transient
	private DocumentoPqua respaldo = new DocumentoPqua();

}