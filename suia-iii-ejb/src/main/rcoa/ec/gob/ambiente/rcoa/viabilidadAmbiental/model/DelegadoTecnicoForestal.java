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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the technical_delegates_forest database table.
 * 
 */
@Entity
@Table(name="technical_delegates_forest", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "tedf_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tedf_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tedf_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tedf_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tedf_user_update")) })

@NamedQueries({
@NamedQuery(name="DelegadoTecnicoForestal.findAll", query="SELECT d FROM DelegadoTecnicoForestal d"),
@NamedQuery(name=DelegadoTecnicoForestal.GET_POR_INFORME, query="SELECT d FROM DelegadoTecnicoForestal d where d.idInformeInspeccion = :idInforme and d.estado = true") })
public class DelegadoTecnicoForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";

	public static final String GET_POR_INFORME = PAQUETE + "DelegadoTecnicoForestal.getPorInforme";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="tedf_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="inrf_id")
	private Integer idInformeInspeccion;
	
	@Getter
	@Setter
	@Column(name="tedf_position")
	private String cargo;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_user_id")
	@Getter
	@Setter
	private Usuario usuario;
	
	@Getter
	@Setter
	@Transient
	private String cedula;

}