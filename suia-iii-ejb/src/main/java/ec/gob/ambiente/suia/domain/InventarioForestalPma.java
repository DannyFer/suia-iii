/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author Jonathan Guerrero
 */
@Entity
@Table(name = "forest_inventory_pma", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "foip_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "foip_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "foip_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "foip_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "foip_user_update")) })
@NamedQueries({ @NamedQuery(name = InventarioForestalPma.OBTENER_POR_FICHA, query = "SELECT i FROM InventarioForestalPma i WHERE i.fichaAmbientalPma = :fichaAmbientalPma") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "foip_status = 'TRUE'")
public class InventarioForestalPma extends EntidadAuditable {

	/**
	*
	*/
	private static final long serialVersionUID = -9139180938777353834L;

	public static final String OBTENER_POR_FICHA = "ec.gob.ambiente.suia.domain.InventarioForestalPma.obtenerPorFicha";

	@Id
	@Basic(optional = false)
	@Getter
	@Setter
	@SequenceGenerator(name = "INVENTARIO_FORESTAL_PMA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_foip_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INVENTARIO_FORESTAL_PMA_ID_GENERATOR")
	@Column(name = "foip_id", nullable = false)
	private Integer id;

	// bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name = "cafa_id")
	@ForeignKey(name = "catii_fapma_forestal_inventory_pma_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

	// bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name = "mien_id")
	@ForeignKey(name = "catii_fapma_forestal_inventory_pma_mien_fk")
	@Getter
	@Setter
	private FichaAmbientalMineria fichaAmbientalMineria;

	@Getter
	@Setter
	@Column(name = "foip_vegetal_removal")
	private Boolean remocionVegetal;

	@Getter
	@Setter
	@Column(name = "foip_stand_up_wood")
	private float maderaEnPie;
	
	@Getter
	@Setter
	@Column(name = "foip_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "foip_historical_date")
	private Date fechaHistorico;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "foip_forestal_inventory")
	@ForeignKey(name = "fk_forestal_inventory_pma_foip_forestal_inventory_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento inventarioForestal;
	
	@ManyToOne
	@JoinColumn(name = "pren_id")	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	//para validar si se han realizado cambios en el objeto para guardar historial
	public boolean equalsObject(Object obj) {
		if (obj == null)
			return false;
		InventarioForestalPma base = (InventarioForestalPma) obj;
		if (this.getId() == null && base.getId() == null)
			return super.equals(obj);
		if (this.getId() == null || base.getId() == null)
			return false;
		
		if(this.getId().equals(base.getId()) && 
				this.getMaderaEnPie() == base.getMaderaEnPie() && 
				this.getRemocionVegetal().equals(base.getRemocionVegetal())){
			if (this.getInventarioForestal() == null && base.getInventarioForestal() == null)
				return true;
			else if (this.getInventarioForestal() != null && base.getInventarioForestal() != null) {
				if (this.getInventarioForestal().getNombre().equals(base.getInventarioForestal().getNombre()))
					return true;
			}
			return false;
		}
		else
			return false;
	}
}
