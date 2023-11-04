package com.example.web_bookstore_be.service.UploadImage;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UploadImageImp implements UploadImageService {
    private final Cloudinary cloudinary;
    @Override
    public String uploadImage(MultipartFile multipartFile, String name) {
        String url = "";
        try{
             url = cloudinary.uploader()
                    .upload(multipartFile.getBytes(), Map.of("public_id", name))
                    .get("url")
                    .toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}