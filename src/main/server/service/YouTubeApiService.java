package server.service;


import server.api.YoutubeVideoSearchResult;
import server.aspect.Profile;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.google.api.services.youtube.model.SearchResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class YouTubeApiService {

    private final YouTubeService youTubeService;

    public YouTubeApiService(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    @Profile
    public List<YoutubeVideoSearchResult> searchVideos(String query) throws IOException {
        List<SearchResult> searchResult = youTubeService.searchVideos(query, youTubeService.auth());

        return searchResult.stream().map(it ->
                new YoutubeVideoSearchResult(
                    it.getId().getVideoId(),
                    it.getSnippet().getTitle()
                )
        ).collect(Collectors.toList());
    }

    @Profile
    public File downloadVideo(String id, String outputFileName, YoutubeProgressCallback<File> callback, YouTubeService.DownloadFileFormat format) {
        return youTubeService.downloadVideo(id, outputFileName, callback, format);
    }

}
