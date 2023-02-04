package com.aws_demo_app.user_service.Repository;

import com.aws_demo_app.user_service.Repository.DAO.FileMetadata;
import org.springframework.data.repository.CrudRepository;

public interface FileMetadataRepository extends CrudRepository<FileMetadata, Integer> {
    FileMetadata findByTitle(String title);
}
