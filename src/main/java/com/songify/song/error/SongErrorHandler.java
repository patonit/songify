package com.songify.song.error;

import com.songify.song.controller.SongRestController;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Log4j2
@ControllerAdvice(assignableTypes = SongRestController.class)
public class SongErrorHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)  //może,my usunać ResponseEntity z sygnatury metody bo dodaliśmy tę adnotację
    @ExceptionHandler(SongNotFoundException.class)
    public ErrorSongResponseDto handleException(SongNotFoundException exception) {
        log.warn("SongNotFoundException while accesing song");
        return new ErrorSongResponseDto(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
