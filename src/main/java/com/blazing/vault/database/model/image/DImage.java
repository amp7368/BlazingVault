package com.blazing.vault.database.model.image;

import io.ebean.Model;
import java.io.File;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import net.dv8tion.jda.api.utils.FileUpload;

@Entity
@Table(name = "image")
public class DImage extends Model {

    @Id
    private UUID id;
    @Lob
    private File image;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String extension;

    public DImage(File image, String name, String extension) {
        this.image = image;
        this.name = name;
        this.extension = extension;
    }

    public FileUpload getDiscordImage() {
        return FileUpload.fromData(image, name + "." + extension);
    }
}
