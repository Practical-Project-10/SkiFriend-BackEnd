package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.entity.FreePost;
import com.ppjt10.skifriend.entity.Likes;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.FreePostRepository;
import com.ppjt10.skifriend.repository.LikesRepository;
import com.ppjt10.skifriend.repository.UserRepository;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LikesService {
    private final LikesRepository likesRepository;
    private final FreePostRepository freePostRepository;

    @Transactional
    public void changeLike(
            UserDetailsImpl userDetails,
            String skiResort,
            Long postId
    )
    {
        if(userDetails == null) {
           throw new IllegalArgumentException("회원가입 후 이용하실 수 있습니다");
        }
        FreePost freePost = freePostRepository.findByIdAndSkiResort(postId, skiResort).orElseThrow(
                ()->new IllegalArgumentException("해당하는 게시물이 없습니다")
        );
        Long userId = userDetails.getUser().getId();
        Optional<Likes> foundLikes = likesRepository.findByUserIdAndFreePostId(userId, postId);
        if(foundLikes.isPresent()) {
            System.out.println(foundLikes.get().getId());
            likesRepository.deleteById(foundLikes.get().getId());
            System.out.println("좋아요가 삭제되었습니다");
        }
        else {
            likesRepository.save(new Likes(userDetails.getUser(), freePost));
            System.out.println("좋아요가 클릭되었습니다");

        }
    }



}
