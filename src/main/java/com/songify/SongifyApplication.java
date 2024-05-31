package com.songify;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
get /songs
get /songs/100
get /songs?id=100
get /songs header “requestId: 100001“
*/

@Log4j2
@SpringBootApplication
public class SongifyApplication {


    public static void main(String[] args) {
        SpringApplication.run(SongifyApplication.class, args);
        log.info("Hello World!");
    }

}

