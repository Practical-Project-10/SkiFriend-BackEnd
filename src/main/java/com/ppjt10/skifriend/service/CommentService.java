package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.dto.commentdto.CommentRequestDto;
import com.ppjt10.skifriend.entity.Comment;
import com.ppjt10.skifriend.entity.FreePost;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.CommentRepository;
import com.ppjt10.skifriend.repository.FreePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final FreePostRepository freePostRepository;
    private final CommentRepository commentRepository;

    // 자유 게시판 게시글 댓글 작성
    @Transactional
    public void createComment(Long postId, CommentRequestDto requestDto, User user) {
        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

        Comment comment = new Comment(user.getId(), freePost, requestDto.getContent());

        commentRepository.save(comment);

        freePost.setCommentCnt(freePost.getCommentCnt() + 1);
    }

    // 자유 게시판 게시글 댓글 수정
    @Transactional
    public void updateComment(Long commentId, CommentRequestDto requestDto, User user) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다")
        );

        if(!user.getId().equals(comment.getUserId())) {
            throw new IllegalArgumentException("댓글 작성자만 댓글을 수정할 수 있습니다");
        }

        comment.update(requestDto);
    }

    // 자유 게시판 게시글 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다")
        );

        if(!user.getId().equals(comment.getUserId())) {
            throw new IllegalArgumentException("댓글 작성자만 댓글을 삭제할 수 있습니다");
        }

        commentRepository.deleteById(commentId);

        FreePost commentFreePost = comment.getFreePost();

        commentFreePost.setCommentCnt(commentFreePost.getCommentCnt() - 1);
    }
}
