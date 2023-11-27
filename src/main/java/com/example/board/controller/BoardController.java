package com.example.board.controller;

import com.example.board.model.board.AttachedFile;
import com.example.board.model.board.Board;
import com.example.board.model.board.BoardUpdateForm;
import com.example.board.model.board.BoardWriteForm;
import com.example.board.model.member.Member;
import com.example.board.repository.BoardMapper;
import com.example.board.service.BoardService;
import com.example.board.util.FileService;
import com.example.board.util.PageNavigator;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RequestMapping("board")
@RequiredArgsConstructor
@Controller
public class BoardController {

    // 데이터베이스 접근을 위한 BoardMapper 필드 선언
    private final BoardMapper boardMapper;
    private final BoardService boardService;
    @Value("${file.upload.path}")
    private String uploadPath;
    
    // 페이징 처리를 위한 상수값
    private final int countPerPage = 10;
    private final int pagePerGroup = 5;

    // BoardMapper 객체 필드 주입(생성자 주입 방식)
//    public BoardController(BoardMapper boardMapper) {
//        this.boardMapper = boardMapper;
//    }

    // 글쓰기 페이지 이동
    @GetMapping("write")
    public String writeForm(Model model) {
        
        // writeForm.html의 필드 표시를 위해 빈 BoardWriteForm 객체를 생성하여 model 에 저장한다.
        model.addAttribute("writeForm", new BoardWriteForm());
        // board/writeForm.html 을 찾아 리턴한다.
        return "board/write";
    }

    // 게시글 쓰기
    @PostMapping("write")
    public String write(@SessionAttribute(value = "loginMember", required = false) Member loginMember,
                        @Validated @ModelAttribute("writeForm") BoardWriteForm boardWriteForm,
                        @RequestParam(required=false) MultipartFile file,
                        BindingResult result) {
       
//        log.info("board: {}", boardWriteForm);
//        log.info("file : {}", file);
        
        // validation 에러가 있으면 board/write.html 페이지를 다시 보여준다.
        if (result.hasErrors()) {
            return "board/write";
        }

        // 파라미터로 받은 BoardWriteForm 객체를 Board 타입으로 변환한다.
        Board board = BoardWriteForm.toBoard(boardWriteForm);
        
        // board 객체에 로그인한 사용자의 아이디를 추가한다.
        board.setMember_id(loginMember.getMember_id());
        
        boardService.saveBoard(board, file);
        
        // board/list 로 리다이렉트한다.
        return "redirect:/board/list";
    }

    // 게시글 전체 보기
    @GetMapping("list")
    public String list(@RequestParam(value="page", defaultValue="1") int page,
    									 @RequestParam(value="searchText", defaultValue="") String searchText,
                       Model model) {
    	log.info("검색어 : {}", searchText);
    	
    	int total = boardService.getTotal(searchText);
    	
      PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
//      log.info("페이지 정보 : {}", navi);
      
      // 데이터베이스에 저장된 모든 Board 객체를 리스트 형태로 받는다.
      List<Board> boards = boardService.findBoards(searchText, navi.getStartRecord(), navi.getCountPerPage());
      // Board 리스트를 model 에 저장한다.
      model.addAttribute("boards", boards);
      model.addAttribute("navi", navi);
      model.addAttribute("searchText", searchText);
      // board/list.html 를 찾아서 리턴한다.
      return "board/list";
    }
    	
    // 게시글 읽기
    @GetMapping("read")
    public String read(@RequestParam Long board_id,
                       Model model) {

        log.info("id: {}", board_id);
        
        // board_id 에 해당하는 게시글을 데이터베이스에서 찾는다.
        Board board = boardService.readBoard(board_id);
        // board_id에 해당하는 게시글이 없으면 리스트로 리다이렉트 시킨다.
        if (board == null || board_id == null) {
            log.info("게시글 없음");
            return "redirect:/board/list";
        }
        
        AttachedFile attachedFile = boardService.findFileByBoardId(board_id);
//        log.info("첨부파일 : {}", attachedFile);
        model.addAttribute("file", attachedFile);
        
        // 모델에 Board 객체를 저장한다.
        model.addAttribute("board", board);
        
        // board/read.html 를 찾아서 리턴한다.
        return "board/read";
    }

    // 게시글 수정 페이지 이동
    @GetMapping("update")
    public String updateForm(@SessionAttribute(value = "loginMember", required = false) Member loginMember,
                             @RequestParam Long board_id,
                             Model model) {
        

//        log.info("id: {}", board_id);

        // board_id에 해당하는 게시글이 없거나 게시글의 작성자가 로그인한 사용자의 아이디와 다르면 수정하지 않고 리스트로 리다이렉트 시킨다.
        Board board = boardService.findBoard(board_id);
        if (board_id == null || !board.getMember_id().equals(loginMember.getMember_id())) {
            log.info("수정 권한 없음");
            return "redirect:/board/list";
        }
        
        // model 에 board 객체를 저장한다.
        model.addAttribute("board", Board.toBoardUpdateForm(board));
        
        AttachedFile attachedFile = boardService.findFileByBoardId(board_id);
//        log.info("첨부파일 : {}", attachedFile);
        
        model.addAttribute("file", attachedFile);
        
        // board/update.html 를 찾아서 리턴한다.
        return "board/update";
    }

    // 게시글 수정
    @PostMapping("update")
    public String update(@SessionAttribute(value = "loginMember", required = false) Member loginMember,
                         @RequestParam Long board_id,
                         @Validated @ModelAttribute("board") BoardUpdateForm updateBoard,
                         @RequestParam(required=false) MultipartFile file,
                         
                         BindingResult result) {
        

//        log.info("board: {}", updateBoard);
        // validation 에 에러가 있으면 board/update.html 페이지로 돌아간다.
        if (result.hasErrors()) {
            return "board/update";
        }

        // board_id 에 해당하는 Board 정보를 데이터베이스에서 가져온다.
        Board board = boardService.findBoard(board_id);
        // Board 객체가 없거나 작성자가 로그인한 사용자의 아이디와 다르면 수정하지 않고 리스트로 리다이렉트 시킨다.
        if (board == null || !board.getMember_id().equals(loginMember.getMember_id())) {
            log.info("수정 권한 없음");
            return "redirect:/board/list";
        }
        // 제목을 수정한다.
        board.setTitle(updateBoard.getTitle());
        // 내용을 수정한다.
        board.setContents(updateBoard.getContents());
        // 수정한 Board 를 데이터베이스에 update 한다.
        boardService.updateBoard(board , updateBoard.isFileRemoved(), file);
        // 수정이 완료되면 리스트로 리다이렉트 시킨다.
        return "redirect:/board/list";
    }

    // 게시글 삭제
    @GetMapping("delete")
    public String remove(@SessionAttribute(value = "loginMember", required = false) Member loginMember,
                         @RequestParam Long board_id) {
        
        // board_id 에 해당하는 게시글을 가져온다.
        Board board = boardService.findBoard(board_id);
        // 게시글이 존재하지 않거나 작성자와 로그인 사용자의 아이디가 다르면 리스트로 리다이렉트 한다.
        if (board == null || !board.getMember_id().equals(loginMember.getMember_id())) {
            log.info("삭제 권한 없음");
            return "redirect:/board/list";
        }
        
        // 파일 존재 여부 체크
        AttachedFile attachedFile = boardService.findFileByBoardId(board_id);
        
        if(attachedFile != null) {
        	boardService.removeAttachedFile(attachedFile.getAttached_file_id());
        }
        
        // 게시글을 삭제한다.
        boardMapper.removeBoard(board_id);
        // board/list 로 리다이렉트 한다.
        return "redirect:/board/list";
    }
    
    @GetMapping("download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws MalformedURLException {
    	// 첨부파일 아이디로 첨부파일 정보를 가져온다
    	AttachedFile attachedFile = boardService.findFileByAttachedFileId(id);
    	
    	// 다운로드 하려는 파일의 절대경로 값을 만든다
    	String fullPath = uploadPath + "/" + attachedFile.getSaved_filename();
    	
    	UrlResource resource = new UrlResource("file:" + fullPath);
    	
    	// 한글 파일명이 깨지지 않도록 UTF_8로 파일명을 인코딩한다
    	String encodingFileName = UriUtils.encode(attachedFile.getOriginal_filename(), 
    																						StandardCharsets.UTF_8);
    	
    	// 응답헤더에 담을 Content Disposition 값을 생성한다
    	String contentDisposition = "attachment; filename=\""+ encodingFileName + "\"";
    	
    	return ResponseEntity.ok()
    			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
    			.body(resource);
    }
    
    
    
    
    
    
    
    

}
