package com.darren.photo_challeng.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PhotoService {

  // 實務建議：路徑可以寫在 application.yml，這裡先寫死方便你測試
  private final String uploadDir = "/Users/darren_li/uploads/";

  public String saveImage(MultipartFile file) throws Exception {
    File folder = new File(uploadDir);
    if (!folder.exists())
      folder.mkdirs();

    // 避免檔名重複，使用 UUID
    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
    Path path = Paths.get(uploadDir + fileName);
    Files.write(path, file.getBytes());
    return fileName; // 回傳檔名供資料庫儲存
  }

  public String extractExif(MultipartFile file) {
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
      ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

      // 抓取 Sony A7R3 常見參數：光圈、快門、ISO
      String fNumber = directory.getString(ExifSubIFDDirectory.TAG_FNUMBER);
      String exposure = directory.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
      String iso = directory.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);

      return String.format("{\"Aperture\":\"f/%s\", \"Shutter\":\"%s s\", \"ISO\":\"%s\"}", fNumber, exposure, iso);
    } catch (Exception e) {
      return "{}"; // 解析失敗回傳空 JSON
    }
  }
}