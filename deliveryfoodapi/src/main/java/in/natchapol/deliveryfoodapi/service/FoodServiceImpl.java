package in.natchapol.deliveryfoodapi.service;

import in.natchapol.deliveryfoodapi.entity.FoodEntity;
import in.natchapol.deliveryfoodapi.io.FoodRequest;
import in.natchapol.deliveryfoodapi.io.FoodResponse;
import in.natchapol.deliveryfoodapi.repository.FoodRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService {

    //ประกาศตัวแปรแบบไม่ให้เปลี่ยนค่าได้(final)
    @Autowired
    private S3Client s3Client;
    @Autowired
    private FoodRepository foodRepository;

    //inject ค่าจากapplication.properties
    @Value("${aws.s3.bucketname}")
    private String bucketName;

    //สร้างMethod
    @Override
    public String uploadFile(MultipartFile file) {
        //ประกาศตัวแปรเป็นสตริงจากfile.getOriginalFilename substringหาเครื่องหมายจุดแล้วเอาชื่อไฟล์จากตัวสุดท้าย
        String filenameExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        //สร้างkey uuidแบบไม่ซ้ำแล้วต่อสตริงเป็นชื่อไฟล์
        String key = UUID.randomUUID().toString() + "." + filenameExtension;
        try {
            //สร้างคำสั่งUploadfile
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    //ให้ทุกคนสามารถเข้าถึงไฟล์ได้ผ่านลิงก์
                    .acl("public-read")
                    //บอกชนิดของไฟล์
                    .contentType(file.getContentType())
                    .build();
            //uploadfileไปยังawsS3 เก็บใส่ตัวแปรresponseที่มีชนิดเป็นPutObjectResponse
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            if (response.sdkHttpResponse().isSuccessful()) {
                return "https://" + bucketName + ".s3.amazonaws.com/" + key;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");
            }
            //IOException (ย่อมาจาก Input/Output Exception) ในภาษา Java คือข้อผิดพลาด (Exception) ที่เกิดขึ้นเมื่อโปรแกรมมีปัญหาเกี่ยวกับ การอ่าน/เขียนข้อมูล
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occured while loading the file");
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest request, MultipartFile file) {
        FoodEntity newFoodEntity = convertToEntity(request);
        String imageUrl = uploadFile(file);
        newFoodEntity.setImageUrl(imageUrl);
        newFoodEntity = foodRepository.save(newFoodEntity);
        return convertToResponse(newFoodEntity);
    }

    @Override
    public List<FoodResponse> readFoods() {
      List<FoodEntity> databaseEntries = foodRepository.findAll();
     return databaseEntries.stream().map(object -> convertToResponse(object)).collect(Collectors.toList());
    }

    @Override
    public FoodResponse readFood(String id) {
       FoodEntity existingFood = foodRepository.findById(id).orElseThrow(()->new RuntimeException("Food not found for the id:"+id));
       return convertToResponse(existingFood);
    }

    @Override
    public boolean deleteFile(String filename) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        return true;
    }

    @Override
    public void deleteFood(String id) {
        FoodResponse response = readFood(id);
        String imageUrl = response.getImageUrl();
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/")+1);
        boolean isFileDelete = deleteFile(fileName);
        if(isFileDelete){
            foodRepository.deleteById(response.getId());
        }
    }


    private FoodEntity convertToEntity(FoodRequest request) {
        return FoodEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
    }

    private FoodResponse convertToResponse(FoodEntity entity) {
        return FoodResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
