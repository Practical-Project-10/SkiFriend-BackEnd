package com.ppjt10.skifriend.skiresortinit;

import com.ppjt10.skifriend.entity.SkiResort;
import com.ppjt10.skifriend.repository.SkiResortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkiResortInit implements ApplicationRunner {
    private final SkiResortRepository skiResortRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        SkiResort skiResort1 = SkiResort.builder()
                .resortName("HighOne")
                .build();
        skiResortRepository.save(skiResort1);

        SkiResort skiResort2 = SkiResort.builder()
                .resortName("YongPyong")
                .build();
        skiResortRepository.save(skiResort2);

        SkiResort skiResort3 = SkiResort.builder()
                .resortName("WellihilliPark")
                .build();
        skiResortRepository.save(skiResort3);

        SkiResort skiResort4 = SkiResort.builder()
                .resortName("Konjiam")
                .build();
        skiResortRepository.save(skiResort4);

        SkiResort skiResort5 = SkiResort.builder()
                .resortName("VivaldiPark")
                .build();
        skiResortRepository.save(skiResort5);

        SkiResort skiResort6 = SkiResort.builder()
                .resortName("Phoenix")
                .build();
        skiResortRepository.save(skiResort6);
    }
}
