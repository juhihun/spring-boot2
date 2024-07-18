package com.yedam.app.board.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	public String boardInsertProcess(BoardVO boardVO) {
		int bno = boardService.insertBoard(boardVO);
		return "redirect:boardInfo?boardNo="+ bno;
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
		return "redirect:boardList";
	}
	
}
