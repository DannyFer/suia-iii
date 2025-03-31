package ec.gob.ambiente.retce.model;


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
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
/**
 * The persistent class for the retce.table_parameter database table.
 * 
 */
@Entity
@Table(name="table_parameter", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "tapa_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "tapa_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "tapa_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tapa_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tapa_user_update")) })
public class ParametrosTablas extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="tapa_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="mpct_id")
	private CaracteristicasPuntoMonitoreoTabla tabla;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="pald_id")
	private ParametrosDescargasLiquidas parametro;
	
	@Getter
	@Setter
	@Column(name="tapa_minimum_permissible_limit")
	private Double limiteMinimoPermisible;
	
	
	@Getter
	@Setter
	@Column(name="tapa_maximum_permissible_limit")
	private Double limiteMaximoPermisible;
	
	
	@Getter
	@Setter
	@Column(name="tapa_application")
	private String aplicacion;
	
	@Getter
	@Setter
	@Column(name="tapa_download_destination")
	private String destinoDescarga;
	
	@Getter
	@Setter
	@Transient
	private String nombreParametro;
}

