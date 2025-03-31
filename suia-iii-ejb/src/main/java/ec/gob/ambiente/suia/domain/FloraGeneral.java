package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.List;

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
 * The persistent class for the categories_catalog database table.
 * 
 */
@Entity
@Table(name = "general_flora", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "gefl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gefl_status = 'TRUE'")
@NamedQueries({
		@NamedQuery(name = FloraGeneral.BUSCAR_POR_ESTUDIO_ID, query = "SELECT c FROM FloraGeneral c where c.eiaId = :p_eiaId ")
 })
public class FloraGeneral extends EntidadAuditable implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5236062399325918257L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String BUSCAR_POR_ESTUDIO_ID = PAQUETE
			+ "FloraGeneral.buscarPorEstudioId";

	
	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "GENERAL_FLORA_GEFL_GENERATOR", initialValue = 1, sequenceName = "seq_gefl_id", schema = "suia_iii", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GENERAL_FLORA_GEFL_GENERATOR")
	@Column(name = "gefl_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "geca_sampling_type", nullable = false)
	private Integer tipoMuestreo;

	@Getter
	@Setter
	@Column(name = "geca_methodology", nullable = false)
	private Integer metodologia;

	@Getter
	@Setter
	@Column(name = "gefl_effort_sampling", nullable = true, length = 255)
	private String esfuerzoMuestreo;

	@Getter
	@Setter
	@Column(name = "gefl_methodology_annex", length = 400)
	private String anexoMetodologia;

	@Getter
	@Setter
	@Column(name = "gefl_eia_id", length = 400)
	private Integer eiaId;

    @Getter
    @Setter
    @Transient
    private List<PuntosMuestreoFlora> listaPuntosMuestreo;

    @Transient
    @Getter
    @Setter
    private byte[] fileAnexo;

    @Transient
    @Getter
    @Setter
    private String contentType;

    @Transient
    @Getter
    @Setter
    private boolean archivoEditado;

}