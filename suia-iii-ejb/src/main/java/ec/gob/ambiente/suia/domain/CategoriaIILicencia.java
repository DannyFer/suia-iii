package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Entidad para manejo de datos de Licencia Ambiental Categoría II. </b>
 * 
 * @author jgras
 * @version Revision: 1.0
 *          <p>
 *          [Autor: jgras, Fecha: 05/02/2015]
 *          </p>
 */

@NamedQueries({
	@NamedQuery(name = CategoriaIILicencia.FIND_ALL, query = "SELECT c FROM CategoriaIILicencia c"),
	@NamedQuery(name = CategoriaIILicencia.FIND_BY_PROJECT, query = "SELECT c FROM CategoriaIILicencia c WHERE c.projectId = :idProyecto") })
@Entity
@Table(name = "cat_ii_license", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "ciil_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ciil_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ciil_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ciil_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ciil_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ciil_status = 'TRUE'")
public class CategoriaIILicencia extends EntidadAuditable {

	private static final long serialVersionUID = 1L;
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.CategoriaIILicencia.findAll";
	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.CategoriaIILicencia.findByProject";
	public static final String SEQUENCE_CODE = "seq_cat_ii_code";
	
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "CATIILICENSE_CIILID_GENERATOR", sequenceName = "seq_ciil_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATIILICENSE_CIILID_GENERATOR")
	@Column(name = "ciil_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "ciil_report_observations")
	private String observacionesInforme;

	@Getter
	@Setter
	@Column(name = "project_id")
	private Integer projectId;
	
	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_cat_ii_license_pren_id_project_envirolment_licensing_pren_i")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyecto;

}
