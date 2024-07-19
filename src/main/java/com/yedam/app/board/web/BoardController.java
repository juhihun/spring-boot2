package com.yedam.app.board.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yedam.app.board.service.BoardService;
import com.yedam.app.board.service.BoardVO;

//@AllArgsConstructor 생성자 주입방식이 기억이 안난다면 이거적기
@Controller
public class BoardController {
	
	private BoardService boardService;
	
	//DI 생성자 주입 방식=> 생성자 선언하기
	@Autowired
	public BoardController(BoardService boardService){
		this.boardService = boardService;
	}
	@Value("${file.upload.path}")
	private String uploadPath;
	//메소드선언
	// 전체조회 : URI - boardList / RETURN - board/boardList
	@GetMapping("boardlist")
	public String boardList(Model model) { //Model = Request + Responsebody
		List<BoardVO> list = boardService.boardList(); //목록을 담음
		model.addAttribute("boards", list); //model의 boards 변수에 리스트를 담음(html에서 사용할 수 있게)
		return "board/boardList";
		//classpath:/templates/		board/boardList		.html 의 경로대로 html파일(view) 만들기
	}
	
	// 단건조회 : URI - boardInfo / PARAMETER - BoardVO(QueryString)
	//          RETURN - board/boardInfo
	@GetMapping("boardInfo")
	public String boardInfo(BoardVO baordVO, Model model) { //=>한건을 조회하기 위해 커맨드객체(기본)
		BoardVO findVO = boardService.boardInfo(baordVO);
		model.addAttribute("board", findVO);
		return "board/boardInfo";
	}

	// 등록 - 페이지 : URI - boardInsert / RETURN - board/boardInsert
	@GetMapping("boardInsert")
	public String boardInsert() {
		return "board/boardInsert";
	}
	
	// 등록 - 처리 : URI - boardInsert / PARAMETER - BoardVO(QueryString),form tag(submit)를 쓰려면 QueryString만 사용 가능
	//             RETURN - 단건조회 다시 호출
	@PostMapping("boardInsert")
	public String boardInsertProcess(BoardVO boardVO, @RequestPart MultipartFile[] images) {
      //log.info(images[0].getOriginalFilename()); //파일 이름만 가져온거
	      for(MultipartFile image : images) {
	         //1)원래 파일이름
	         String fileName = image.getOriginalFilename();
	         //empVO.setimage(sqvepath);
	         //empservice.insetEmpInfo(empVO);
	         
	         //고유한 식별자로 이미지 저장해서 클라이언트가 업로드했을때 파일이름이 겹치지 않도록 하는거
	         UUID uuid = UUID.randomUUID();
	         String uniqueFileName = uuid + "_" + fileName;
	         
	         //2)실제로 저장할 경로를 생성 : 서버의 업로드 경로 + 파일이름
	         String saveName = uploadPath + File.separator + uniqueFileName; //""가 /와 같아
	         
	         Path savePath = Paths.get(saveName); //여기에 경로 담았음
	         
	         boardVO.setImage(uniqueFileName); //파일의 정보를 가져와서 boardVO에 파일의 이름을 넣어줌
	         //3)*파일 작성(파일 업로드)
	         try {
	            image.transferTo(savePath); //*실제 경로 지정 /Path는 경로/transferTo=햇살
	         }catch(IOException e) {
	            e.printStackTrace();
	         }
	      }
	      int bno = boardService.insertBoard(boardVO);
	      return "redirect:boardInfo?boardNo=" + bno;
	   }
	
	// 수정 - 페이지 : URI - boardUpdate / PARAMETER - BoardVO(QueryString)
	//               RETURN - board/boardUpdate
	// => 단건조회 ,페이지 넘겨주는 거 -> model필요(addAttribute)
	@GetMapping("boardUpdate")
	public String boardUpdateForm(BoardVO boardVO, Model model) {
		BoardVO findVO = boardService.boardInfo(boardVO);
		model.addAttribute("board", findVO);
		return "board/boardUpdate";
	}
	
	// 수정 - 처리 : URI - boardUpdate / PARAMETER - BoardVO(JSON)
	//             RETURN - 수정결과 데이터(Map) -> AJAX => @ResponseBody => @RequestBody
	// 등록 (내부에서 수행하는 쿼리문 - UPDATE문)
	@PostMapping("boardUpdate")
	@ResponseBody
	public Map<String, Object> boardUpdateProcess(@RequestBody BoardVO boardVO){
		return boardService.updateBoard(boardVO);
	}
	
	// 삭제 - 처리 : URI - boardDelete / PARAMETER - Integer
	//             RETURN - 전체조회 다시 호출
	@GetMapping("boardDelete") //Integer앞에 RequestParam 적으면 받는건에 대해 필수값임
	public String boardDelete(Integer boardNo) { //service에 선언된 매개변수가 int라
		boardService.deleteBoard(boardNo);
		return "redirect:boardlist";
	}
	
}
