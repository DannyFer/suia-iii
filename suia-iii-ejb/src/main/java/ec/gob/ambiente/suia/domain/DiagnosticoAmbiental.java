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

/**
 *
 * <b> Entity que representa la tabla areas. </b>
 *
 * @author pganan
 * @version Revision: 1.0
 * <p>
 * [Autor: pganan, Fecha: 22/12/2014]
 * </p>
 */
@Entity
@Table(name = "environmental_diagnosis", schema = "suia_iii")

@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "endi_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "endi_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "endi_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "endi_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "endi_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "endi_status = 'TRUE'")
@NamedQueries({
	@NamedQuery(name = DiagnosticoAmbiental.BUSCAR_POR_ESTUDIO_ID, query = "SELECT c FROM DiagnosticoAmbiental c where c.eiaId = :p_eiaId ")
})
public class DiagnosticoAmbiental extends EntidadAuditable {

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String BUSCAR_POR_ESTUDIO_ID = PAQUETE
			+ "DiagnosticoAmbiental.buscarPorEstudioId";

	private static final long serialVersionUID = -1756612644167548934L;
	
    @Getter
    @Setter
    @Column(name = "endi_id")
    @Id
    @SequenceGenerator(name = "ENDI_GENERATOR", initialValue = 1, sequenceName = "seq_endi_id", schema = "suia_iii")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENDI_GENERATOR")
    private Integer id;

	@Getter
	@Setter
	@Column(name = "endi_physical_environment_problematic", nullable = false, length = 255)
	private String problematicaMedioFisico;
	@Getter
	@Setter
	@Column(name = "endi_biotic_environment_problematic", nullable = false, length = 255)
	private String problematicaMedioBiotico;
	@Getter
	@Setter
	@Column(name = "endi_cultural_problematic", nullable = false, length = 255)
	private String problematicaCultura;
	@Getter
	@Setter
	@Column(name = "endi_annex_diagnosis", nullable = false, length = 255)
	private String anexoDiagnosticoName;
	@Getter
	@Setter
	@Column(name = "endi_eia_id")
	private Integer eiaId;
	
	@Transient
	@Getter
	@Setter
	private byte[] anexoDiagnostico;
	@Transient
	@Getter
	@Setter
	private String anexoDiagnosticoContenType;
	@Transient
	@Getter
	@Setter
	private boolean archivoEditado = false;

}
