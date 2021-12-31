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

        Boolean isHighOne = skiResortRepository.existsByResortName("HighOne");
        if (!isHighOne) {
            SkiResort skiResort1 = new SkiResort("HighOne");
            skiResortRepository.save(skiResort1);

            SkiResort skiResort2 = new SkiResort("YongPyong");
            skiResortRepository.save(skiResort2);

            SkiResort skiResort3 = new SkiResort("WellihilliPark");
            skiResortRepository.save(skiResort3);

            SkiResort skiResort4 = new SkiResort("Konjiam");
            skiResortRepository.save(skiResort4);

            SkiResort skiResort5 = new SkiResort("VivaldiPark");
            skiResortRepository.save(skiResort5);

            SkiResort skiResort6 = new SkiResort("Phoenix");
            skiResortRepository.save(skiResort6);
        }
    }
}
