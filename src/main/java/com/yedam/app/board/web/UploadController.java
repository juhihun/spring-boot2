package com.yedam.app.board.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
public class UploadController {
	//1. 고정값 아님, 개발 후 운영서버 환경 확인 후 수정->재빌드
	//private String uploadPath = "D:/upload";
	
	//2. 환경변수 값으로 경로 지정 (시스템환경변수/개발하는 시점이 아닌, 실제 실행하는 시점에서 고정값을 넘겨주는 의미), spring annotation 
	//=> application.properties path 지정(오픈가능한 값, 오픈불가능은 입력 금지) 
	@Value("${file.upload.path}")
	private String uploadPath;
	
	@GetMapping("getPath")
	@ResponseBody
	public String getPath() {
		return uploadPath;
	}
	
	@GetMapping("formUpload")//경로가 페이지
	public void formUploadPage() {}//void return type 없다는 선언
	//classpath: /templates/formUpload.html
	
	 @PostMapping("uploadForm")
	 public String formUploadFile(@RequestPart MultipartFile[] images) {
		// log.info(images[0].getOriginalFilename()); 개별처리
		 for(MultipartFile image : images) {
			 //파일정보를 알기위한 코드
			 log.warn(image.getContentType()); //개별 파일의 종류
			 log.warn(image.getOriginalFilename()); //사용자가 넘겨준 실제 파일 이름
			 log.warn(String.valueOf(image.getSize())); //파일 크기
			 
			 //1) 사용자가 보낸 원래 파일이름
			 String fileName = image.getOriginalFilename();
			 
			 //2) 실제 저장할 경로를 생성 : 서버의 업로드 경로 + 파일이름 합치기
			 String saveName = uploadPath+ File.separator + fileName;
			 log.debug("saveName : " + saveName);
			 
			 Path savePath = Paths.get(saveName); //경로를 자바가 인식하기 위한 클래스 -> Paths
			 
			 //3) 파일 작성(파일 업로드) 경로를 지정 ex) D:/upload -> 위에 경로 선언하기
			 try {
				image.transferTo(savePath); //transferTo 메소드(출력스트림)
			 } catch (IOException e) {
				e.printStackTrace();
			 }	

		 }
		 return "formUpload";
	 }
	 
	 
		@GetMapping("upload")
		public void uploadPage() {}
		
		@PostMapping("/uploadsAjax")
		@ResponseBody
		public List<String> uploadFile(@RequestPart MultipartFile[] uploadFiles) {
		    
			List<String> imageList = new ArrayList<>();
			
		    for(MultipartFile uploadFile : uploadFiles){
		    	if(uploadFile.getContentType().startsWith("image") == false){//image 로 시작하지 않는 경우
		    		System.err.println("this file is not image type");
		    		return null; //강제종료
		        }
		  
		        String originalName = uploadFile.getOriginalFilename();
		        String fileName = originalName.substring(originalName.lastIndexOf("//")+1);
		        
		        System.out.println("fileName : " + fileName);
		    
		        //날짜 폴더 생성
		        String folderPath = makeFolder();
		        //UUID(식별자 역할을 함, 랜덤한 값 -> 형식을 만들어줌)
		        String uuid = UUID.randomUUID().toString();
		        //저장할 파일 이름 중간에 "_"를 이용하여 구분
		        
		        String uploadFileName = folderPath +File.separator + uuid + "_" + fileName;
		        
		        String saveName = uploadPath + File.separator + uploadFileName;
		        
		        Path savePath = Paths.get(saveName);
		        //Paths.get() 메서드는 특정 경로의 파일 정보를 가져옵니다.(경로 정의하기)
		        System.out.println("path : " + saveName);
		        try{
		        	uploadFile.transferTo(savePath);
		            //uploadFile에 파일을 업로드 하는 메서드 transferTo(file)
		        } catch (IOException e) {
		             e.printStackTrace();	             
		        }
		        // DB에 해당 경로 저장
		        // 1) 사용자가 업로드할 때 사용한 파일명
		        // 2) 실제 서버에 업로드할 때 사용한 경로
		        imageList.add(setImagePath(uploadFileName));
		     }
		    
		    return imageList;
		}
		//오늘날짜 기준으로 자동 생성 메소드
		private String makeFolder() {
			String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")); //오늘날짜기준 문자열로반환되어야함
			// LocalDate를 문자열로 포멧
			String folderPath = str.replace("/", File.separator); //경로로 인지할수잇게 대체
			File uploadPathFoler = new File(uploadPath, folderPath); //File 클래스 사용
			// File newFile= new File(dir,"파일명");
			if (uploadPathFoler.exists() == false) { //존재하면 True
				uploadPathFoler.mkdirs(); //사용자 파일 업로드 하는 시점에 폴더 자동 생성
				// 만약 uploadPathFolder가 존재하지않는다면 makeDirectory하라는 의미입니다.
				// mkdir(): 디렉토리에 상위 디렉토리가 존재하지 않을경우에는 생성이 불가능한 함수
				// mkdirs(): 디렉토리의 상위 디렉토리가 존재하지 않을 경우에는 상위 디렉토리까지 모두 생성하는 함수
			}
			return folderPath; //실제경로 컨트롤러에 반환
		}
		
		private String setImagePath(String uploadFileName) {
			return uploadFileName.replace(File.separator, "/");
		}
}
