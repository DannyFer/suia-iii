package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the scdr_environmental_record_physical_component_incline database table.
 * 
 */
@Entity
@Table(name="scdr_environmental_record_physical_component_incline", schema="suia_iii")
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "rpci_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "rpci_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "rpci_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "rpci_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "rpci_user_update"))})

public class ComponenteFisicoPendienteSD extends EntidadBase {
	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="rpci_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "erpc_id")
	@ForeignKey(name = "fk_environmental_record_physical_component")
	private ComponenteFisicoSD componenteFisicoSD;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gcph_id")
	@ForeignKey(name = "fk_environmental_record_physical_component")
	private CatalogoGeneralFisico catalogoFisico;


	public ComponenteFisicoPendienteSD() {
		
	}

}