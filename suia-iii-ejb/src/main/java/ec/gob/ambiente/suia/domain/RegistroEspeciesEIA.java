/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author oscar campana
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Oscar Campana, Fecha: 13/08/2015]
 *          </p>
 */
@Entity
@Table(name = "especies_log_eia", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = RegistroEspeciesEIA.LISTAR, query = "SELECT a FROM RegistroEspeciesEIA a WHERE a.estado = true and a.eia.id = :idEia and a.tipoMedioBiotico = :tipo")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "eslo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eslo_status = 'TRUE'")
public class RegistroEspeciesEIA extends EntidadBase {

	private static final long serialVersionUID = 273477765787987760L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR = PAQUETE + "RegistroEspeciesEIA.listar";

	@Id
	@SequenceGenerator(name = "ESLO_GENERATOR", schema = "suia_iii", sequenceName = "seq_eslo_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ESLO_GENERATOR")
	@Column(name = "eslo_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "especies_log_eia_eist_id_fkey")
	@ManyToOne(fetch = FetchType.LAZY)
	private EstudioImpactoAmbiental eia;

	@Getter
	@Setter
	@JoinColumn(name = "sapo_id", referencedColumnName = "sapo_id")
	@ForeignKey(name = "fk_eslo_sapo_id_sam_po_sapo_id")
	@ManyToOne(fetch = FetchType.LAZY,  cascade = CascadeType.MERGE)
	private PuntosMuestreoEIA puntosMuestreo;

	@Getter
	@Setter
	@Column(name = "eslo_biotic_type")
	private String tipoMedioBiotico;

	@Getter
	@Setter
	@Column(name = "eslo_family")
	private String familia;

	@Getter
	@Setter
	@Column(name = "eslo_gender_order")
	private String genero_orden;

	@Getter
	@Setter
	@Column(name = "eslo_gender")
	private String genero;

	@Getter
	@Setter
	@Column(name = "eslo_scientific_name")
	private String nombreCientifico;
	
	@Getter
	@Setter
	@Column(name = "eslo_identification_level")
	private String nivelIdentificacion;
	
	@Getter
	@Setter
	@Column(name = "eslo_local_name")
	private String nombreLocal;

	@Getter
	@Setter
	@Column(name = "eslo_register_type")
	private String tipoRegistro;//Tipo de registro de especie nomenclado.

	@Setter
	@Getter
	@Column(name = "eslo_register_type_other")
	private Boolean otro;//Para saber si es un valor no nomenclado.


	@Getter
	@Setter
	@Column(name = "eslo_direct_register")
	private Boolean registroDirecto;
	
	@Getter
	@Setter
	@Column(name = "eslo_individuals")
	private Integer individuos;

	@Getter
	@Setter
	@Column(name = "eslo_AB")
	private Double ab;

    @Getter
    @Setter
    @Column(name = "eslo_relative_abundance")
    private String abundanciaRelativa;

    @Getter
    @Setter
    @Column(name = "eslo_uicn")
    private String uicn;

    @Getter
    @Setter
    @Column(name = "eslo_red_book_ecuador")
    private String libroRojoEcuador;

	@Getter
	@Setter
	@Column(name = "eslo_class")
	private String clase;

	@Getter
	@Setter
	@Column(name = "eslo_dap")
	private Double dap;

	@Getter
    @Setter
    @Column(name = "eslo_cites")
    private String cites;

    @Transient
	@Getter
	@Setter
	//@Column(name = "eslo_dnr")
	private Double dnr;

    @Transient
	@Getter
	@Setter
	//@Column(name = "eslo_dmr")
	private Double dmr;

    @Transient
	@Getter
	@Setter
	//@Column(name = "eslo_ivi")
	private Double ivi;

	@Transient
	@Getter
	@Setter
	private Double pi;

    @Transient
    @Getter
    @Setter
    private Double pi2;

    @Transient
    @Getter
    @Setter
    private Double lnPi;

    @Transient
	@Getter
	@Setter
	private Double shannon;

    @Transient
	@Getter
	@Setter
	private Double simpson;

	
	public RegistroEspeciesEIA() {
		// TODO Auto-generated constructor stub
	}
    public RegistroEspeciesEIA(Integer id) {
        this.id = id;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.id + "";
	}
}
