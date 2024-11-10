package com.blazing.vault.database.model.item;

import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.image.DImage;
import com.blazing.vault.database.model.item.rent.DRentingPrice;
import io.ebean.Model;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "item")
public class DItem extends Model {

    @Id
    private long id;
    @ManyToOne
    private DClient owner;
    @Column(nullable = false)
    private ItemStatus status;
    @Column(nullable = false)
    private String name;
    @Column
    private String description;

    @JoinColumn
    @OneToOne(optional = false)
    private DImage image;
    @OneToMany
    private List<DRentingPrice> prices = new ArrayList<>();

    public DItem(DClient owner, String name, String description, ItemStatus status, DRentingPrice price, DImage image) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.status = status;
        this.prices.add(price);
        this.image = image;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }
}
