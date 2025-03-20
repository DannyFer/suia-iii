package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Table(name = "contributors_data", schema = "suia_iii")
@Entity
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "coda_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "coda_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "coda_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "coda_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "coda_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "coda_status = 'TRUE'")
public class ContributorData extends EntidadAuditable {

	private static final long serialVersionUID = 5254140748242663892L;

	@Id
	@Getter
	@Setter
	@Column(name = "coda_id")
	@SequenceGenerator(name = "CONTRUBUTORS_CODA_ID_GENERATOR", sequenceName = "seq_coda_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTRUBUTORS_CODA_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "coda_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "coda_ruc")
	private String ruc;

	@Override
	public String toString() {
		return nombre;
	}
}
