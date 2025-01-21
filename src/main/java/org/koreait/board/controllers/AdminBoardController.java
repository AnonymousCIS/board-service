package org.koreait.board.controllers;

import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import org.koreait.global.rests.JSONData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminBoardController {


    @PostMapping("/config/save")
    public JSONData save() {

        return new JSONData();
    }

    @GetMapping("/list")
    public JSONData list() {

        return new JSONData();
    }

    @PatchMapping("/config/update")
    public JSONData update() {

        return new JSONData();
    }

//    public JSON
}
