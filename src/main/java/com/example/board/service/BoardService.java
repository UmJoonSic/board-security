package com.example.board.service;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.model.board.AttachedFile;
import com.example.board.model.board.Board;
import com.example.board.repository.BoardMapper;
import com.example.board.util.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class BoardService {
	
	//데이터베이스 접근을 위한 BoardMapper 필드 선언
  private final BoardMapper boardMapper;
  private final FileService fileService;
  @Value("${file.upload.path}")
  private String uploadPath;
  
  @Transactional
	public void saveBoard(Board board, MultipartFile file) {
		
		// 데이터베이스에 저장한다.
    boardMapper.saveBoard(board);
    
    if(file != null && file.getSize() > 0 || !file.isEmpty()) {
    	// 첨부파일 저장
    	AttachedFile saveFile = fileService.saveFile(file);
    	// 첨부파일 내용을 데이터베이스에 저장
    	saveFile.setBoard_id(board.getBoard_id());
    	boardMapper.saveFile(saveFile);
    }
    
	}

	public AttachedFile findFileByAttachedFileId(Long id) {
		AttachedFile attachedFile = boardMapper.findFileByAttachedFileId(id);
		return attachedFile;
	}

	public int getTotal(String searchText) {
		return boardMapper.getTotal(searchText);
	}

	public List<Board> findBoards(String searchText, int startRecord, int countPerPage) {
		RowBounds rowBounds = new RowBounds(startRecord, countPerPage);
		return boardMapper.findBoards(searchText, rowBounds);
	}

	public Board readBoard(Long board_id) {
		Board board = findBoard(board_id);
		board.addHit();
		AttachedFile attachedFile = findFileByBoardId(board_id);
		
		boolean isFileRemoved = true;
		if(attachedFile == null) {
			isFileRemoved = false;
		}
//		updateBoard(board, isFileRemoved, attachedFile);
		return board;
	}


	public Board findBoard(Long board_id) {
		return boardMapper.findBoard(board_id);
	}
	
	@Transactional
	public void updateBoard(Board updateBoard, boolean isFileRemoved, MultipartFile file) {
		Board board = boardMapper.findBoard(updateBoard.getBoard_id());
		
		if(board != null) {
			boardMapper.updateBoard(updateBoard);
			// 첨부파일 정보를 가져옴
			AttachedFile attachedFile = boardMapper.findFileByBoardId(updateBoard.getBoard_id());
			if(attachedFile != null && (isFileRemoved || (file != null && file.getSize() > 0))) {
				// 파일 삭제를 요청했거나 새로운 파일이 업로드 됐다면 기존 파일을 삭제한다.
				removeAttachedFile(attachedFile.getAttached_file_id());
			}
		}
		
		// 새로 저장할 파일이 있다면 저장한다.
		if(file != null && file.getSize() > 0) {
			// 첨부파일을 서버에 저장한다.
			AttachedFile savedFile = fileService.saveFile(file);
			// 데이터베이스에 저장될 board_id를 세팅
			savedFile.setBoard_id(updateBoard.getBoard_id());
			// 첨부파일 내용을 데이터베이스에 저장
			boardMapper.saveFile(savedFile);
		}
		
	}
	
	@Transactional
	public void removeAttachedFile(Long attached_file_id) {
		AttachedFile attachedFile = boardMapper.findFileByAttachedFileId(attached_file_id);
		if(attachedFile != null) {
			String fullPath = uploadPath + "/" + attachedFile.getSaved_filename();
			fileService.deleteFile(fullPath);
			// 데이터베이스에서도 삭제
			boardMapper.removeAttachedFile(attached_file_id);		
		}
	}

	public AttachedFile findFileByBoardId(Long board_id) {
		return boardMapper.findFileByBoardId(board_id);
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
