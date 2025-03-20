package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


@Entity
@Table(name="vegetation_cover_affectation", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "veca_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "veca_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "veca_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "veca_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "veca_user_update")) })

@NamedQueries({
@NamedQuery(name="AfectacionCoberturaVegetal.findAll", query="SELECT a FROM AfectacionCoberturaVegetal a"),
@NamedQuery(name=AfectacionCoberturaVegetal.GET_LISTA_POR_INFORME_TIPO, query="SELECT a FROM AfectacionCoberturaVegetal a where a.idInforme = :idInforme and a.tipoRegistro = :tipo and a.estado = true order by id")
})
public class AfectacionCoberturaVegetal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_LISTA_POR_INFORME_TIPO = PAQUETE + "AfectacionCoberturaVegetal.getListaPorInformeTipo";	

	public static Integer coberturaVegetal = 1;
	public static Integer ecosistemas = 2;
	public static Integer convenios = 3;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="veca_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="inrf_id")
	private Integer idInforme;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="dein_id")
	private DetalleInterseccionProyectoAmbiental detalleInterseccion;

	@Getter
	@Setter
	@Column(name="veca_surface")
	private String superficieDescripcion;

	@Getter
	@Setter
	@Column(name="veca_affectation")
	private Boolean afectacion;

	@Getter
	@Setter
	@Column(name="veca_type")
	private Integer tipoRegistro; //1 = Tipo de covertura vegetal, 2 = Ecosistema del área del proyecto, 3 = Afectacion a áreas bajo convenios

}