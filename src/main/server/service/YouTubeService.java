package server.service;

import server.configuration.YoutubeProperties;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class YouTubeService {

    private static final Logger log = LoggerFactory.getLogger(YouTubeService.class);

    private final YoutubeProperties properties;

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private static final JsonFactory JSON_FACTORY = new GsonFactory();

    private static final YoutubeDownloader downloader = new YoutubeDownloader();

    public enum DownloadFileFormat {
        ANY, BEST_VIDEO, BEST_AUDIO_AND_VIDEO, BEST_AUDIO
    }

    public YouTubeService(YoutubeProperties properties) {
        this.properties = properties;
    }


    public YouTube auth() {
        return new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {
        }).setApplicationName("YoutubeVideoInfo").setYouTubeRequestInitializer(new YouTubeRequestInitializer("AIzaSyBAtw5TNQ82XE74of8bwRckZ-vTDGbDBG8")).build();
    }

    public List<SearchResult> searchVideos(String query, YouTube youTube) throws IOException {
        YouTube.Search.List search = youTube.search().list(Collections.singletonList("id,snippet"));


        search.setQ(query);
        search.setType(Collections.singletonList("video"));
        search.setMaxResults(10L);

        List<SearchResult> ans = search.execute().getItems();
        return ans;
    }

    public File downloadVideo(String id, String outputFileName, YoutubeProgressCallback<File> callback, DownloadFileFormat downloadFileFormat) {
        VideoInfo video = downloader.getVideoInfo(new RequestVideoInfo(id)).data();

        File outputDir = new File(properties.fileStoragePath);
        Format format = switch (downloadFileFormat) {
            case ANY:
                yield video.videoFormats().get(0);
            case BEST_VIDEO:
                yield video.bestVideoFormat();
            case BEST_AUDIO_AND_VIDEO:
                yield video.bestVideoWithAudioFormat();
            case BEST_AUDIO:
                yield video.bestAudioFormat();
        };

        RequestVideoFileDownload request = new RequestVideoFileDownload(format)
                .callback(callback)
                .saveTo(outputDir)
                .renameTo(outputFileName)
                .overwriteIfExists(true)
                .async();
        return downloader.downloadVideoFile(request).data();
    }
}
