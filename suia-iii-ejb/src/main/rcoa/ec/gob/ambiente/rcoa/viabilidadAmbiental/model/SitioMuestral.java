package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import java.math.BigDecimal;

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the sample_site database table.
 * 
 */
@Entity
@Table(name="sample_site", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "sasi_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "sasi_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "sasi_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "sasi_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "sasi_user_update")) })

@NamedQueries({
@NamedQuery(name="SitioMuestral.findAll", query="SELECT s FROM SitioMuestral s"),
@NamedQuery(name=SitioMuestral.GET_LISTA_SITIOS_POR_INFORME_TIPO, query="SELECT s FROM SitioMuestral s where s.idInformeFactibilidad = :idInforme and s.tipoSitio = :tipo and s.estado = true order by id")
})
public class SitioMuestral extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_LISTA_SITIOS_POR_INFORME_TIPO = PAQUETE + "SitioMuestral.getListaSitiosPorInformeTipo";

	public static Integer registroCualitativo = 1;
	public static Integer registroCuantitativoCenso = 2;
	public static Integer registroCuantitativoMuestreo = 3;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="sasi_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="fere_id")
	private Integer idInformeFactibilidad;

	@ManyToOne
	@JoinColumn(name = "shty_id")
	@ForeignKey(name = "fk_shty_id")
	@Getter
	@Setter
	private TipoForma tipoForma;

	@Getter
	@Setter
	@Column(name="sasi_name")
	private String nombreSitio;

	@Getter
	@Setter
	@Column(name="sasi_elevation")
	private Double elevacion;

	@Getter
	@Setter
	@Column(name="sasi_long")
	private Double largo;

	@Getter
	@Setter
	@Column(name="sasi_broad")
	private Double ancho;

	@Getter
	@Setter
	@Column(name="sasi_surface")
	private Double areaSitio;

	@Getter
	@Setter
	@Column(name="sasi_type")
	private Integer tipoSitio; //1 = Registro cualitativo de especies, 2 = Registro cuantitativo censo, 3 = Registro cuantitativo muestreo

	@Getter
	@Setter
	@Column(name="sasi_basal_area_summation")
	private Double sumatoriaAreaBasal;

	@Getter
	@Setter
	@Column(name="sasi_total_volume_summation")
	private Double sumatoriaVolumenTotal;

	@Getter
	@Setter
	@Column(name="sasi_sum_commercial_volume")
	private Double sumatoriaVolumenComercial;

	@Getter
	@Setter
	@Column(name="sasi_total_dnr")
	private BigDecimal totalDnr;

	@Getter
	@Setter
	@Column(name="sasi_total_dmr")
	private Double totalDmr;

	@Getter
	@Setter
	@Column(name="sasi_total_ivi")
	private Double totalIvi;

	@Getter
	@Setter
	@Column(name="sasi_shannon")
	private Double indiceShanon;

	@Getter
	@Setter
	@Column(name="sasi_simpson")
	private Double indiceSimpson;

	@Getter
	@Setter
	@Column(name="sasi_exp_shannon")
	private Double exponencialShannon;

	@Getter
	@Setter
	@Column(name="sasi_inv_simpson")
	private Double inversoSimpson;

	@Getter
	@Setter
	@Column(name="sasi_interpretation_ivi")
	private String interpretacionValorImportancia;

	@Getter
	@Setter
	@Column(name="sasi_interpretation_sha_simp")
	private String interpretacionShanonSimpson;
	
	@Getter
	@Setter
	@Transient
	private Integer nroIndividuos;

	@Getter
	@Setter
	@Transient
	private List<AnalisisResultadoForestal> listaEspecieValorImportancia;
	
	@Getter
	@Setter
	@Transient
	private List<CoordenadaSitioMuestral> listaCoordenadas;
	
	@Getter
	@Setter
	@Transient
	private CoordenadaSitioMuestral coordenadaSitioMuestral;
	
	@Getter
	@Setter
	@Transient
	private String provincia;
	
	@Getter
	@Setter
	@Transient
	private String canton;
	
	@Getter
	@Setter
	@Transient
	private String parroquia;

	@Getter
	@Setter
	@Transient
	private String coordenadasX;
	
	@Getter
	@Setter
	@Transient
	private String coordenadasY;

	@Getter
	@Setter
	@Transient
	private List<UbicacionSitioMuestral> ubicacionesGeograficas;

}