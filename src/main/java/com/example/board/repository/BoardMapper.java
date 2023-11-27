package com.example.board.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.example.board.model.board.AttachedFile;
import com.example.board.model.board.Board;

@Mapper
public interface BoardMapper {
	void saveBoard(Board board);
	List<Board> findBoards(String searchText, RowBounds rowBounds);
  Board findBoard(Long board_id);
  void updateBoard(Board updateBoard);
  void removeBoard(Long board_id);
  void saveFile(AttachedFile attachedFile);
  AttachedFile findFileByBoardId(Long board_id);
	AttachedFile findFileByAttachedFileId(Long id);
	int getTotal(String searchText);
	void removeAttachedFile(Long attached_file_id);
}
