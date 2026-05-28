package com.darren.photo_challeng.service;

import com.darren.photo_challeng.entity.Progress;
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
public class PhotoExifService {

  // 建議路徑
  private final String uploadDir = "/Users/darren_li/uploads/";

  /**
   * 儲存實體檔案到 Mac 磁碟
   */
  public String saveImage(MultipartFile file) throws Exception {
    File folder = new File(uploadDir);
    if (!folder.exists())
      folder.mkdirs();

    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
    Path path = Paths.get(uploadDir + fileName);
    Files.write(path, file.getBytes());
    return fileName;
  }

  /**
   * 解析 EXIF 並直接填入 Progress 實體物件
   */
  public void fillPhotoMetadata(MultipartFile file, Progress progress) {
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());

      // 1. 抓取拍攝參數 (光圈、快門、ISO)
      ExifSubIFDDirectory subIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
      if (subIFD != null) {
        progress.setAperture("f/" + subIFD.getString(ExifSubIFDDirectory.TAG_FNUMBER));
        progress.setShutterSpeed(subIFD.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME) + "s");
        progress.setIso(subIFD.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
      }

    } catch (Exception e) {
      System.err.println("EXIF 解析失敗: " + e.getMessage());
      // 解析失敗時，欄位會保持為 null 或資料庫預設值
    }
  }
}