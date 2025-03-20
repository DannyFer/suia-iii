package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

@NamedQueries({ @NamedQuery(name = PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, query = "select p from PlantillaReporte p where p.tipoDocumentoId = :p_tipoDocumentoId"),
	            @NamedQuery(name = PlantillaReporte.OBTENER_PLANTILLA_POR_TIPO_POR_CODIGO, query = "select p from PlantillaReporte p where p.tipoDocumentoId = :p_tipoDocumentoId and p.codigoProceso = :codProceso")})
@Entity
@Table(name = "report_template", schema = "public")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "rete_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rete_status = 'TRUE'")
public class PlantillaReporte extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1525971341648586158L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_PLANTILLA_POR_INFORME = PAQUETE + "PlantillaReporte.obtenerPlantillaPorInforme";
	public static final String OBTENER_PLANTILLA_POR_TIPO_POR_CODIGO = PAQUETE + "PlantillaReporte.obtenerPlantillaPorTipoPorCodigo";

	@Id
	@Column(name = "rete_id")
	@SequenceGenerator(name = "report_template_generator", sequenceName = "report_template_id_seq", schema = "public", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_template_generator")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "rete_html_template", length = 5000)
	private String htmlPlantilla;

	@Getter
	@Setter
	@Column(name = "rete_process_name")
	private String nombreProceso;

	@Getter
	@Setter
	@Column(name = "rete_process_code")
	private String codigoProceso;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_report_tamplate_id_type_documentstydo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@Getter
	@Setter
	@Column(name = "doty_id", updatable = false, insertable = false)
	private Integer tipoDocumentoId;

	@Override
	public String toString() {
		return nombreProceso;
	}
}
