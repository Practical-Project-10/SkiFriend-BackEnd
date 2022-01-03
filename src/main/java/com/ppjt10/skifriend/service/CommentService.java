package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.dto.CommentDto;
import com.ppjt10.skifriend.entity.Comment;
import com.ppjt10.skifriend.entity.FreePost;
import com.ppjt10.skifriend.repository.CommentRepository;
import com.ppjt10.skifriend.repository.FreePostRepository;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final FreePostRepository freePostRepository;
    private final CommentRepository commentRepository;

    //region 자유 게시판 게시글 댓글 작성
    @Transactional
    public void writeComment(
            UserDetailsImpl userDetails,
            CommentDto.RequestDto requestDto,
            Long postId
    ) {
        if(userDetails == null) {
            throw new IllegalArgumentException("회원가입 후 이용해주세요.");
        }

        FreePost freePost = freePostRepository.findById(postId).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

        Comment comment = new Comment(userDetails.getUser(), freePost, requestDto.getContent());

        commentRepository.save(comment);

        freePost.setCommentCnt(freePost.getCommentCnt() + 1);
    }
    //endregion

    //region 자유 게시판 게시글 댓글 수정
    @Transactional
    public void editComment(UserDetailsImpl userDetails,
                            CommentDto.RequestDto requestDto,
                            Long commentId)
    {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다")
        );

        if(userDetails.getUser().getId() != comment.getUser().getId()) {
            throw new IllegalArgumentException("댓글 작성자만 댓글을 수정할 수 있습니다");
        }

        comment.update(requestDto);
    }

    // 자유 게시판 게시글 댓글 삭제
    @Transactional
    public void deleteComment(UserDetailsImpl userDetails, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다")
        );

        if(userDetails.getUser().getId() != comment.getUser().getId()) {
            throw new IllegalArgumentException("댓글 작성자만 댓글을 삭제할 수 있습니다");
        }

        commentRepository.deleteById(commentId);

        comment.getFreePost().setCommentCnt(comment.getFreePost().getCommentCnt() - 1);
    }
}
