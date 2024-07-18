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
	public Date regdate;		//작성일
	public Date updatedate;		//수정일
	@DateTimeFormat(pattern="yy-MM-dd")
	public String image;		//첨부이미지
	
}
