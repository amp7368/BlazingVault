package com.blazing.vault.database.model.item.rent;

import com.blazing.vault.util.emerald.Emeralds;
import io.ebean.Model;
import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rent_price")
public class DRentingPrice extends Model {

    @Id
    private UUID id;
    @Column(nullable = false)
    private Timestamp fromDate;
    @Column
    private Timestamp untilDate;
    @Column
    private long pricePerWeek;

    public DRentingPrice(Emeralds price) {
        this.fromDate = new Timestamp(System.currentTimeMillis());
        this.pricePerWeek = price.amount();
    }
}
