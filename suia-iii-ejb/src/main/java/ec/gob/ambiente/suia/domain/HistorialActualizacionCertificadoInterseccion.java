package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the response_report_project_viability_coa_biodiversity database table.
 * 
 */
@Entity
@Table(name="certificate_update_history", schema = "suia_iii")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "ceuh_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ceuh_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ceuh_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ceuh_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ceuh_user_update")) })


public class HistorialActualizacionCertificadoInterseccion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ceuh_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="ceuh_code")
	private String codigoPryecto;

	@Getter
	@Setter
	@Column(name="ceuh_status_update")
	private Integer estadoActualizacion;

	@Getter
	@Setter
	@Column(name="ceuh_observation")
	private String observacion;

}