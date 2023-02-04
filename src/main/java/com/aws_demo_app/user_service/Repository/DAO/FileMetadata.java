package com.aws_demo_app.user_service.Repository.DAO;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// We will store File Metadata in MySQL and the file in the S3 bucket
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class FileMetadata {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private String imagePath;
    private String imageFileName;
}



