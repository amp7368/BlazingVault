package com.blazing.vault.database.model.item.rent;

import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.item.DItem;
import io.ebean.Model;
import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "renting_action")
public class DRentingAction extends Model {

    @Id
    private UUID id;
    @ManyToOne
    private DClient renter;
    @ManyToOne
    private DItem rentedItem;
    @Column
    private Timestamp rentedAt;
    @Column
    private Timestamp returnedAt;

    public DRentingAction(DClient renter, DItem item) {
        this.renter = renter;
        this.rentedItem = item;
        this.rentedAt = new Timestamp(System.currentTimeMillis());
    }
}
