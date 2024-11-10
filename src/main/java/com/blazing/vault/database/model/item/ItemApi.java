package com.blazing.vault.database.model.item;

import com.blazing.vault.database.model.entity.client.DClient;
import com.blazing.vault.database.model.image.DImage;
import com.blazing.vault.database.model.item.rent.DRentingAction;
import com.blazing.vault.database.model.item.rent.DRentingPrice;
import com.blazing.vault.util.emerald.Emeralds;
import io.ebean.DB;
import io.ebean.Transaction;
import java.io.File;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface ItemApi {

    static DItem createItem(DClient owner, String name, String extension, @Nullable String description, Emeralds price,
        File imageFile) {
        DRentingPrice rentingPrice = new DRentingPrice(price);
        DImage image = new DImage(imageFile, name, extension);
        DItem item = new DItem(owner, name, description, rentingPrice, image);

        try (Transaction transaction = DB.beginTransaction()) {
            rentingPrice.save(transaction);
            image.save(transaction);
            item.save(transaction);
            transaction.commit();
            return item;
        }
    }

    static void rent(DClient client, List<DItem> items) {
        try (Transaction transaction = DB.beginTransaction()) {
            for (DItem item : items) {
                item.setStatus(ItemStatus.RENTED);
                item.save(transaction);
                DRentingAction action = new DRentingAction(client, item);
                action.save(transaction);
            }
            transaction.commit();
        }

    }

}
