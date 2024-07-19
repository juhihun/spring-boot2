package com.yedam.app.board.service;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class BoardVO {
	public Integer boardNo;		//번호
	public String boardTitle;	//제목
	public String boardContent;	//내용
	public String boardWriter;	//작성
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date regdate;		//작성일 java.util.date : yyyy/MM/dd->annotation 쓰기,화면 넘겨줄때 필요(등록, 수정 시)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	public Date updatedate;		//수정일
	public String image;		//첨부이미지
	
}
