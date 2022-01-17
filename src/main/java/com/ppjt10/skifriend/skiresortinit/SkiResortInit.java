package com.ppjt10.skifriend.skiresortinit;

import com.ppjt10.skifriend.dto.userdto.UserProfileUpdateDto;
import com.ppjt10.skifriend.entity.SkiResort;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.SkiResortRepository;
import com.ppjt10.skifriend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class SkiResortInit implements ApplicationRunner {
    // park
    private final SkiResortRepository skiResortRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args)  {

        Boolean isHighOne = skiResortRepository.existsByResortName("HighOne");
        if (!isHighOne) {
            SkiResort skiResort1 = new SkiResort("HighOne","https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/bf6e186f-556f-4cb6-90d6-ce7b9d0bb3bd%E1%84%92%E1%85%A1%E1%84%8B%E1%85%B5%E1%84%8B%E1%85%AF%E1%86%AB3.png" );
            skiResortRepository.save(skiResort1);

            SkiResort skiResort2 = new SkiResort("YongPyong", "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/861c26ee-a3d5-403d-af96-d7795fbf3a7c%E1%84%8B%E1%85%AD%E1%86%BC%E1%84%91%E1%85%A7%E1%86%BC2.png");
            skiResortRepository.save(skiResort2);

            SkiResort skiResort3 = new SkiResort("WellihilliPark", "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/4b50db2d-ebdd-4366-abe7-987789083682%E1%84%8B%E1%85%B0%E1%86%AF1.png");
            skiResortRepository.save(skiResort3);

            SkiResort skiResort4 = new SkiResort("Konjiam", "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/260e15e3-246c-4de8-8807-4b9add3b18c4%E1%84%80%E1%85%A9%E1%86%AB%E1%84%8C%E1%85%B5%E1%84%8B%E1%85%A1%E1%86%B71.png");
            skiResortRepository.save(skiResort4);

            SkiResort skiResort5 = new SkiResort("VivaldiPark","https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/859566cc-8598-4dbc-b0f2-6585454f9685%E1%84%87%E1%85%B5%E1%84%87%E1%85%A1%E1%86%AF%E1%84%83%E1%85%B51.png");
            skiResortRepository.save(skiResort5);

            SkiResort skiResort6 = new SkiResort("Phoenix", "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/65753316-159b-4a64-a6f1-15c6193c2b19%E1%84%92%E1%85%B1%E1%84%82%E1%85%B5%E1%86%A8%E1%84%89%E1%85%B32.png");
            skiResortRepository.save(skiResort6);
        }

        String enps = passwordEncoder.encode("asdf12");
        User user1 = new User("skifriend1", "skijoa", enps, "여", "20대");
        userRepository.save(user1);
        UserProfileUpdateDto dto = UserProfileUpdateDto.builder()
                .nickname("skijoa")
                .career("초보")
                .selfIntro("")
                .build();
        user1.update(dto);
        user1.setPhoneNum("01012341234");

        String enps2 = passwordEncoder.encode("asdf12");
        User user2 = new User("skifriend2", "skijoa2", enps2, "남", "10대");
        userRepository.save(user2);
        UserProfileUpdateDto dto2 = UserProfileUpdateDto.builder()
                .nickname("skijoa2")
                .career("초보")
                .selfIntro("")
                .build();
        user2.update(dto2);
        user2.setPhoneNum("01011112222");
    }
}
