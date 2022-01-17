package com.ppjt10.skifriend.service;

import com.ppjt10.skifriend.entity.Shorts;
import com.ppjt10.skifriend.entity.ShortsLike;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.ShortsLikeRepository;
import com.ppjt10.skifriend.repository.ShortsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ShortsLikeService {
    private final ShortsRepository shortsRepository;
    private final ShortsLikeRepository shortsLikeRepository;

    @Transactional
    public String changeShortsLike(Long shortsId, User user) {
        Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 쇼츠가 없습니다")
        );

        Long userId = user.getId();
        Optional<ShortsLike> foundShortsLike = shortsLikeRepository.findByUserIdAndAndShortsId(userId, shortsId);
        // 기존에 이미 좋아요를 누른 상태라면
        if(foundShortsLike.isPresent()){
            shortsLikeRepository.deleteById(foundShortsLike.get().getId());
            shorts.setShortsLikeCnt(shorts.getShortsLikeCnt() - 1);
            return "false";
        } else{ // 기존에 좋아요를 하지 않은 상태
            shortsLikeRepository.save(new ShortsLike(userId, shorts));
            shorts.setShortsLikeCnt(shorts.getShortsLikeCnt() + 1);
            return "true";
        }
    }
}
