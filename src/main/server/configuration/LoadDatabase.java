package server.configuration;

import server.service.YouTubeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    final YouTubeService youTubeService;

    public LoadDatabase(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            log.info("технологии невероятно надоели");
            youTubeService.searchVideos("пудж", youTubeService.auth());
        };
    }

}