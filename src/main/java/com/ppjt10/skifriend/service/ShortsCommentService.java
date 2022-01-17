//package com.ppjt10.skifriend.service;
//
//import com.ppjt10.skifriend.dto.commentdto.CommentRequestDto;
//import com.ppjt10.skifriend.dto.shortscommentdto.ShortsCommentRequestDto;
//import com.ppjt10.skifriend.entity.*;
//import com.ppjt10.skifriend.repository.ShortsCommentRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class ShortsCommentService {
//    private final ShortsCommentRepository shortsCommentRepository;
//    private final ShortsRepository shortsRepository;
//
//    // Shorts 댓글 작성
//    @Transactional
//    public void createShortsComment(Long shortsId, ShortsCommentRequestDto requestDto, User user) {
//        Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(
//                ()-> new IllegalArgumentException("해당 shorts가 존재하지 않습니다"));
//
//        ShortsComment shortsComment = new ShortsComment(user.getId(), shorts, requestDto.getContent());
//
//        shortsRepository.save(shortsComment);
//
//        shorts.setShortsCommentCnt(shorts.getShortsCommentCnt() + 1);
//    }
//
//    // Shorts 댓글 수정
//    @Transactional
//    public void updateShortsComment(Long shortsCommentId, CommentRequestDto requestDto, User user) {
//
//        ShortsComment comment = shortsRepository.findById(shortsCommentId).orElseThrow(
//                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다")
//        );
//
//        if(!user.getId().equals(comment.getUserId())) {
//            throw new IllegalArgumentException("댓글 작성자만 댓글을 수정할 수 있습니다");
//        }
//
//        comment.update(requestDto);
//    }
//
//    // 자유 게시판 게시글 댓글 삭제
//    @Transactional
//    public void deleteComment(Long commentId, User user) {
//
//        Comment comment = commentRepository.findById(commentId).orElseThrow(
//                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다")
//        );
//
//        if(!user.getId().equals(comment.getUserId())) {
//            throw new IllegalArgumentException("댓글 작성자만 댓글을 삭제할 수 있습니다");
//        }
//
//        commentRepository.deleteById(commentId);
//
//        FreePost commentFreePost = comment.getFreePost();
//
//        commentFreePost.setCommentCnt(commentFreePost.getCommentCnt() - 1);
//    }
//}
