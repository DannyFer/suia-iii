package ec.gob.ambiente.rcoa.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name="layers", schema="coa_mae")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "laye_status")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "laye_date_update"))
})
//@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "laye_status = 'TRUE'")
public class CapasCoa  extends EntidadBase {
	
	private static final long serialVersionUID = 1L;

	public static final int ID_ZONIFICACION_SNAP=16;
	public static final int ID_LIMITE_INTERNO_20_KM=20;
	public static final int ID_INTANGIBLES_AMORTIGUAMIENTO=2;
	public static final int ID_AMORTIGUAMIENTO_YASUNI=7;
	public static final String NAME_CAPA_SNAP_ZONAS = "SNAP ADMINISTRACION";
	public static final int ID_COBERTURA_VEGETAL_2018 = 30;
	public static final int ID_ECOSISTEMAS = 13;
	public static final int ID_SOCIO_BOSQUE = 14;
	public static final String NAME_CAPA_RESTAURACION = "CONVENIO RESTAURACION";
	public static final String NAME_COBERTURA_VEGETAL_2022 = "Cobertura y Uso de la Tierra 2022";
	
	@Getter
	@Setter
	@Id
	@Column(name="laye_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="laye_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name="laye_viability")
	private Boolean viabilidad;
	
	@Getter
	@Setter
	@Column(name="laye_date_update")
	private Date fechaActualizacionCapa;	
	
	@Getter
	@Setter
	@Column(name="laye_abbreviation")
	private String abreviacion;
	
	@Getter
	@Setter
	@Column(name="laye_intersection_certificate")
	private Boolean certificadoInterseccion;

	@Getter
	@Setter
	@Column(name="laye_scheme_name_table")
	private String esquemaInterseccion;

	@Getter
	@Setter
	@Column(name="laye_name_table")
	private String tablaInterseccion;

	@Getter
	@Setter
	@Column(name="laye_fields_intersection")
	private String camposInterseccion;

	@Getter
	@Setter
	@Column(name="laye_soap_intersection")
	private Boolean soapInterseccion;

	@Getter
	@Setter
	@Column(name="laye_time_load")
	private Integer pesoTiempoCarga;
}