package com.songify.song.controller;

import com.songify.song.dto.request.PartiallyUpdateSongRequestDto;
import com.songify.song.dto.request.CreateSongRequestDto;
import com.songify.song.dto.request.UpdateSongRequestDto;
import com.songify.song.dto.response.*;
import com.songify.song.error.SongNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/songs")
public class SongRestController {

    private Map<Integer, Song> database = new HashMap<>(Map.of(
            1, new Song("Shawn Mendes", "Song1"),
            2, new Song("Ariana Grande", "Song2"),
            3, new Song("Michael Jackson", "Song3"),
            4, new Song("Britney Spears", "Song4")
    ));

    @GetMapping
    public ResponseEntity<SongResponseDto> getAllSongs(@RequestParam(required = false) Integer limit) {
        if (limit != null) {
            Map<Integer, Song> limitedMap = database.entrySet()
                    .stream()
                    .limit(limit)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            SongResponseDto response = new SongResponseDto(limitedMap);
            return ResponseEntity.ok(response);
        }
        SongResponseDto response = new SongResponseDto(database);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<GetSongResponseDto> getSongById(@PathVariable Integer id, @RequestHeader(required = false) String requestId) {
        log.info(requestId);
        if (!database.containsKey(id)) {
            throw new SongNotFoundException("Song with id: " + id + " not found");
        }
        Song song = database.get(id);
        GetSongResponseDto response = new GetSongResponseDto(song);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateSongResponseDto> postSong(@RequestBody @Valid CreateSongRequestDto request) {
        // wyciągniecie nazwy przez getter tego rekordu
        Song song = new Song(request.songName(), request.artist());
        log.info("adding new song: " + song);
        database.put(database.size() + 1, song);
        return ResponseEntity.ok(new CreateSongResponseDto(song));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSongByIdUsingPathVariable(@PathVariable Integer id) {
        database.remove(id);
        return ResponseEntity.ok("You delete song with id: " + id);
    }

    @DeleteMapping
    public ResponseEntity<DeleteSongResponseDto> deleteSongByIdUsingQueryParam(@RequestParam Integer limit) {
        if (!database.containsKey(limit)) {
            throw new SongNotFoundException("Song with id: " + limit + " not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ErrorDeleteSongResponseDto("Song with id: " + id + " not found", HttpStatus.NOT_FOUND));
        }

        database.remove(limit);
        return ResponseEntity.ok(new DeleteSongResponseDto("You delete song with id: " + limit + " by using a request param 'id'", HttpStatus.OK));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateSongResponseDto> update(@PathVariable Integer id, @RequestBody @Valid UpdateSongRequestDto request) {
        if (!database.containsKey(id)) {
            throw new SongNotFoundException("Song with id: " + id + " not found");
        }
        String newSongName = request.songName();
        String newArtistName = request.artist();
        Song oldSong = database.put(id, new Song(newSongName, newArtistName));
        String oldSongName = oldSong.songName();
        String oldArtist = oldSong.artist();
        log.info("Updated song with id: " + id + " with songName: " + oldSongName + " and artistName" + oldArtist
                + "to newSongName: " + newSongName + "and newArtistName: " + newArtistName);
        return ResponseEntity.ok(new UpdateSongResponseDto(new Song(newSongName, newArtistName)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PartiallyUpdateSongResponseDto> partiallyUpdateSong(
            @PathVariable Integer id,
            @RequestBody PartiallyUpdateSongRequestDto request) {

        if (!database.containsKey(id)) {
            throw new SongNotFoundException("Song with id: " + id + " not found");
        }
        Song songFromDatabase = database.get(id);
        Song.SongBuilder builder = Song.builder();
        if (request.songName() != null) {
            builder.songName(request.songName());
            log.info("Partially updated song name");
        } else {
            builder.songName(songFromDatabase.songName());
        }
        if (request.artist() != null) {
            builder.artist(request.artist());
            log.info("Partially updated artist");
        } else {
            builder.artist(songFromDatabase.artist());
        }
        Song updatedSong = builder.build();
        database.put(id, updatedSong);
/*        log.info("Partially updated song with id: " + id + " with songName: " + oldSongName + " and artistName" + oldArtist
                + "to newSongName: " + newSongName + "and newArtistName: " + newArtistName);*/
        return ResponseEntity.ok(new PartiallyUpdateSongResponseDto("success"));
    }
}
