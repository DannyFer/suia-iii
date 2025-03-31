package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 07/01/2015]
 *          </p>
 */
@Entity
@Table(name = "projects_blocks", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prbl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prbl_status = 'TRUE'")
public class ProyectoBloque extends EntidadBase {

	private static final long serialVersionUID = 9123720598631110298L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PROJECTS_BLOCKS_PRBL_ID_GENERATOR", sequenceName = "prb_prwp_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_BLOCKS_PRBL_ID_GENERATOR")
	@Column(name = "prbl_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_projects_blocksprbl_projectspren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "bloc_id")
	@ForeignKey(name = "fk_projects_blocksprbl_blockbloc_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "bloc_status = 'TRUE'")
	private Bloque bloque;

}
