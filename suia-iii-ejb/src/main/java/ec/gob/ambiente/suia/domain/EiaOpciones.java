/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "environmental_impact_studies_items", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "eisi_status")) })
@NamedQueries({ @NamedQuery(name = EiaOpciones.LISTAR_POR_TIPO_SECTOR, query = "SELECT e FROM EiaOpciones e WHERE e.idTipoSector = :idTipoSector AND e.estado = true ") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eisi_status = 'TRUE'")
public class EiaOpciones extends EntidadBase {

	private static final long serialVersionUID = 8612143050550644937L;
	
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_TIPO_SECTOR = PAQUETE + "EiaOpciones.listarPorTipoSector";
	public static final String GLOSARIO_TERMINOS_HIDRO = "11";
	public static final String REFERENCIAS_BIBLIOGRAFIA_HIDRO = "12";
	public static final String FIRMA_RESPONSABILIDAD_HIDRO = "13";
	public static final String ANEXOS_HIDRO = "14";
	public static final String SIGLAS_ABREVIATURAS_HIDRO = "3";
	public static final String RESUMEN_EJECUTIVO_HIDRO = "1";
	public static final String FICHA_TECNICA_HIDRO = "2";
	public static final String AREA_ESTUDIO_HIDRO = "5";
	public static final String ANALISIS_RIESGO_HIDRO = "7";
	public static final String DETERMINACION_AREA_INFLUENCIA_HIDRO = "8";
	public static final String CARACTERISTICAS_HIDROMETRICAS_HIDRO = "6.1.1";
	public static final String CARACTERISTICAS_FISICO_MECANICAS_HIDRO = "6.1.2";
	public static final String CARACTERISTICAS_QUIMICAS_HIDRO = "6.1.3";
	public static final String MEDIO_SOCIO_ECONOMICO_CULTURAL_HIDRO = "6.7";
	public static final String CALIDAD_AGUA_HIDRO = "6.1.4";
	public static final String CALIDAD_AIRE_HIDRO = "6.1.5";
	public static final String NIVEL_PRESION_SONORA_HIDRO = "6.1.6";
	public static final String RADIACION_NO_IONIZANTES_HIDRO = "6.1.7";

	public static final String MEDIO_BIOTICO_HIDRO = "6.2";
	public static final String FAUNA_HIDRO = "6.2.2";
	public static final String INVENTARIO_FORESTAL_HIDRO = "6.8";
	public static final String IDENTIFICACION_SITIOS_FUENTES_CONTAMINADOS_HIDRO = "6.9";

	public static final String INVENTARIO_FORESTAL = "8";
	public static final String PLAN_DE_ACCION = "9";
	public static final String DETERMINACION_AREA_INFLUENCIA = "10";
	public static final String IDENTIFICACION_HALLAZGOS = "11";
	public static final String MEDIO_FISICO = "12";
	public static final String MEDIO_BIOTICO = "13";
	public static final String LINEA_BASE = "14";

	@Getter
	@Setter
	@Id
	@Column(name = "eisi_id")
	@SequenceGenerator(name = "ENVIROMENTAL_IMPACT_STUDIES_ITEMS_EISI_ID_GENERATOR", sequenceName = "seq_eisi_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIROMENTAL_IMPACT_STUDIES_ITEMS_EISI_ID_GENERATOR")
	private Integer id;
	@Getter
	@Setter
	@Column(name = "eiai_name", length = 500)
	private String nombre;
	@Getter
	@Setter
	@Column(name = "eiai_number", length = 50)
	private String numeroIdentificacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "sety_id", referencedColumnName = "sety_id")
	@ForeignKey(name = "fk_eia_items_sety_id_sector_types_sety_id")
	private TipoSector tipoSector;

	@Getter
	@Setter
	@Column(name = "sety_id", insertable = false, updatable = false)
	private Integer idTipoSector;

}
