package com.ppjt10.skifriend.config;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.modelmapper.internal.util.Assert;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.io.IOException;
//
@Slf4j
@Component
public class VideoFileUtils {
//    @Value("${yangs.ffmpeg.path}") // application.yml 파일에서 프로퍼티로 설정한다.
//    private String ffmpegPath = "/opt/homebrew/Cellar/ffmpeg/4.4.1_5/bin/ffmpeg";
    private String ffmpegPath = "/usr/bin/ffmpeg";
//    @Value("${yangs.ffprobe.path}")
//    private String ffprobePath = "/opt/homebrew/Cellar/ffmpeg/4.4.1_5/bin/ffprobe";
    private String ffprobePath = "/usr/bin/ffprobe";

    private FFmpeg ffmpeg;
    private FFprobe ffprobe;

    @PostConstruct
    public void init(){
        try {
            ffmpeg = new FFmpeg(ffmpegPath);
            Assert.isTrue(ffmpeg.isFFmpeg());

            ffprobe = new FFprobe(ffprobePath);
            Assert.isTrue(ffprobe.isFFprobe());

            log.debug("VideoFileUtils init complete.");
        } catch (Exception e) {
            log.error("VideoFileUtils init fail.", e);
        }
    }

//    public void getMediaInfo(String filePath) throws IOException {
//        FFmpegProbeResult probeResult = ffprobe.probe(filePath);
//
//        if(log.isDebugEnabled()){
//            log.debug("========== VideoFileUtils.getMediaInfo() ==========");
//            log.debug("filename : {}", probeResult.getFormat().filename);
//            log.debug("format_name : {}", probeResult.getFormat().format_name);
//            log.debug("format_long_name : {}", probeResult.getFormat().format_long_name);
//            log.debug("tags : {}", probeResult.getFormat().tags.toString());
//            log.debug("duration : {} second", probeResult.getFormat().duration);
//            log.debug("size : {} byte", probeResult.getFormat().size);
//
//            log.debug("width : {} px", probeResult.getStreams().get(0).width);
//            log.debug("height : {} px", probeResult.getStreams().get(0).height);
//            log.debug("===================================================");
//        }
//    }

    public void createThumbnail(String filePath, String thumbnailPath){
        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true) // 오버라이드 여부
                .setInput(filePath) // 썸네일 생성대상 파일
                .addExtraArgs("-ss", "00:00:01") // 썸네일 추출 시작점
                .addOutput(thumbnailPath) // 썸네일 파일의 Path
                .setFrames(1) // 프레임 수
                .done();
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run();
    }

    public void videoEncode(String filePath, String videoPath) throws IllegalArgumentException, IOException {
        FFmpegProbeResult probeResult = ffprobe.probe(filePath);
        int width = probeResult.getStreams().get(0).width;
        int height = probeResult.getStreams().get(0).height;

        int enWidth;
        int enHeight;
        if(width>height){
            enWidth = 416;
            enHeight = 234;
        } else{
            enWidth = 234;
            enHeight = 416;
        }

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(filePath) // 파일경로
                .overrideOutputFiles(true) // 오버라이드
                .addOutput(videoPath) // 저장 경로 ( mov to mp4 )
                .setFormat("mp4") // 포맷 ( 확장자 )
                .setVideoCodec("libx264") // 비디오 코덱
                .disableSubtitle() // 서브타이틀 제거
                .setAudioChannels(2) // 오디오 채널 ( 1 : 모노 , 2 : 스테레오 )
                .setVideoResolution(enWidth, enHeight) // 동영상 해상도
                .setVideoBitRate(1464800) // 비디오 비트레이트
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // ffmpeg 빌더 실행 허용
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run();
    }


}
