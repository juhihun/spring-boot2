package com.yedam.app.board.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yedam.app.board.mapper.BoardMapper;
import com.yedam.app.board.service.BoardService;
import com.yedam.app.board.service.BoardVO;

@Service //=> @Transactional aop적용됨
public class BoardServiceImpl implements BoardService{
	//생성자 주입방식 이 방식으로 진행하기
	private BoardMapper boardMapper;
	@Autowired
	BoardServiceImpl(BoardMapper boardMapper){
		this.boardMapper = boardMapper;
	}
	
	@Override
	public List<BoardVO> boardList() {
		return boardMapper.selectBoardAll();
	}

	@Override
	public BoardVO boardInfo(BoardVO boardVO) {
		return boardMapper.selectBoardInfo(boardVO);
	}

	@Override
	public int insertBoard(BoardVO boardVO) {
		int result = boardMapper.insertBoardInfo(boardVO);
		return result == 1 ? boardVO.getBoardNo() : -1;
	}

	@Override
	public Map<String, Object> updateBoard(BoardVO boardVO) { //성공여부, 결과값을 넘겨야함
		Map<String, Object> map = new HashMap<>();
		boolean isSuccessed = false;
		int result = boardMapper.updateBoardInfo(boardVO);
		if(result == 1) {
			isSuccessed = true;
		}
		
		map.put("result", isSuccessed); //map에 결과를 넣어줌(put)
		map.put("target", boardVO);
		
		return map;
	}

	@Override
	public int deleteBoard(int BoardNo) { //몇건을 삭제했는지 넘겨짐
		return boardMapper.deleteBoardInfo(BoardNo);
	}
	
}
